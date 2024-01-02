/*
 * Copyright (c) 2002-2022, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.forms.export.pdffull;

import fr.paris.lutece.plugins.forms.business.*;
import fr.paris.lutece.plugins.forms.business.form.FormItemSortConfig;
import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.filter.FormFilter;
import fr.paris.lutece.plugins.forms.business.form.panel.FormPanel;
import fr.paris.lutece.plugins.forms.export.pdf.AbstractPdfFileGenerator;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.html2pdf.service.PdfConverterServiceException;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFileHome;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.file.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PdfFullFileGenerator extends AbstractPdfFileGenerator {
    private boolean _hasMultipleFiles = false;

    protected PdfFullFileGenerator(String formName, FormPanel formPanel, List<IFormColumn> listFormColumn, List<FormFilter> listFormFilter,
                                   FormItemSortConfig sortConfig, String fileDescription, String baseUrl, Form form) {
        super(FileUtil.normalizeFileName(formName), formName, formPanel, listFormColumn, listFormFilter, sortConfig, fileDescription, baseUrl, form);
    }

    @Override
    public boolean hasMultipleFiles() {
        return _hasMultipleFiles;
    }

    @Override
    public String getDescription() {
        return _fileDescription;
    }

    @Override
    public String getFileName() {
        return _fileName + FileUtil.EXTENSION_ZIP;
    }

    @Override
    public boolean isZippable() {
        return hasMultipleFiles();
    }

    @Override
    public String getMimeType() {
        return FileUtil.CONSTANT_MIME_TYPE_ZIP;
    }

    @Override
    public Path generateFile() throws IOException {
        Path directoryFile = Paths.get(TMP_DIR, _fileName);
        if (!directoryFile.toFile().exists()) {
            directoryFile.toFile().mkdir();
        }
        writeExportFile(directoryFile);
        if (hasMultipleFiles()) {
            return directoryFile;
        }
        File[] files = directoryFile.toFile().listFiles((File f) -> f.getName().endsWith(FileUtil.EXTENSION_ZIP));
        return files[0].toPath();
    }

    private void writeExportFile(Path directoryFile) throws IOException
    {
        List<Question> listQuestions = QuestionHome.getListQuestionByIdForm(_form.getId());
        listQuestions.sort(Comparator.comparingInt(Question::getExportDisplayOrder));
        List<FormResponse> listResponse = FormResponseHome.selectAllFormResponsesUncompleteByIdForm(_form.getId());
        listResponse.removeIf(FormResponse::isFromSave);
        listResponse.sort(Comparator.comparing(FormResponse::getCreation));
        List<List<FormQuestionResponse>> formQuestionResponseList = new ArrayList<>();
        _hasMultipleFiles = true;

        for (int i = 0; i < listResponse.size(); i++)
        {
            if (!listResponse.get(i).isFromSave())
            {
                formQuestionResponseList.add(FormQuestionResponseHome.getFormQuestionResponseListByFormResponse(listResponse.get(i).getId()));
            }
        }

        String customTemplate = getTemplateExportPDF();
        String strTemplateResponses;
        String strTemplateCover = null;
        boolean isCustomTemplate = false;
        if (customTemplate != null) {
            strTemplateResponses = customTemplate;
            isCustomTemplate = true;
        } else {
            strTemplateResponses = generateTemplateForPdfExportResponses(_form, listQuestions);
            strTemplateCover = generateTemplateForPdfExportCoverPage(_form, listResponse);
        }
        int numberOfLoop = formQuestionResponseList.size();
        for (int i = 0; i < numberOfLoop; i++)
        {
            String fileName = _form.getTitle() + "_" + i;
            generatedPdfForRangeOfFormResponses(directoryFile, strTemplateResponses, listQuestions, listResponse, formQuestionResponseList.subList(i, i+1), i, fileName, isCustomTemplate);
            Path pdfPath = directoryFile.resolve(fileName + ".pdf");
            if (listQuestions.stream().anyMatch(question -> question.getEntry().getEntryType().getBeanName().equals("forms.entryTypeGalleryImage") || question.getEntry().getEntryType().getBeanName().equals("forms.entryTypeCamera") || question.getEntry().getEntryType().getBeanName().equals("forms.entryTypeCamera") || question.getEntry().getEntryType().getBeanName().equals("forms.entryTypeFile")))
            {
                    try {
                        generateAttachments(directoryFile, formQuestionResponseList.get(i), pdfPath, i);
                    } catch (Exception e) {
                        AppLogService.error("Error generating attachment files", e);

                }
            }
        }
        if (strTemplateCover != null)
        {
            try {
                String fileName = _form.getTitle() + "_cover";
                generatePdfFile(directoryFile, strTemplateCover, fileName, isCustomTemplate);
            } catch (PdfConverterServiceException e)
            {
                AppLogService.error("Error generating pdf for cover", e);
            }
        }
    }


    private void generateAttachments(Path directoryFile, List<FormQuestionResponse> formQuestionResponseList, Path pdfFile, int numberResponse) throws Exception
    {
        List<Path> listAttachments = new ArrayList<>();
        for (int i = 0; i < formQuestionResponseList.size(); i++)
        {
            List<Path> attachment = writeAndGetAttachments(directoryFile, formQuestionResponseList.get(i), pdfFile, numberResponse);
            listAttachments.addAll(attachment);
        }
        Path[] filesToZip = listAttachments.toArray(new Path[listAttachments.size() + 1]);
        filesToZip[listAttachments.size()] = pdfFile;

        Path zipfile = directoryFile.resolve( pdfFile.getFileName() + FileUtil.EXTENSION_ZIP);
        FileUtil.zipFiles(zipfile, filesToZip);

        for (Path file : filesToZip) {
            FileUtil.deleteFile(file.toFile());
        }
        FileUtil.deleteFile(pdfFile.toFile());
    }

    /**
     * Get all attachements in the {@link FormResponse} and write them to disk
     *
     * @param directoryFile
     * @param formQuestionResponse
     * @param pdfFile
     * @param numberResponse
     * @return
     * @throws Exception
     */
    private List<Path> writeAndGetAttachments(Path directoryFile, FormQuestionResponse formQuestionResponse, Path pdfFile, int numberResponse) throws Exception
    {
        List<Path> listAttachments = new ArrayList<>();
        int i = 0;
        for (Response response : formQuestionResponse.getEntryResponse()) {
            fr.paris.lutece.portal.business.file.File file = FileHome.findByPrimaryKey(response.getFile().getIdFile());
            PhysicalFile physicalFile = PhysicalFileHome.findByPrimaryKey(file.getPhysicalFile().getIdPhysicalFile());
            if (physicalFile != null) {
                String fileName = "#_"+numberResponse +"_"+ file.getTitle() + "_"+ pdfFile.getFileName()+"."+i;
                Path path = directoryFile.resolve(fileName);
                Files.write(path, physicalFile.getValue());
                listAttachments.add(path);
            }
            i++;
        }
        return listAttachments;
    }
}
