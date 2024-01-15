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
package fr.paris.lutece.plugins.forms.export.pdf;

import fr.paris.lutece.plugins.forms.business.*;
import fr.paris.lutece.plugins.forms.business.form.FormItemSortConfig;
import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.filter.FormFilter;
import fr.paris.lutece.plugins.forms.business.form.panel.FormPanel;
import fr.paris.lutece.plugins.html2pdf.service.PdfConverterServiceException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.file.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PdfFileGenerator extends AbstractPdfFileGenerator
{
    private static final boolean ZIP_EXPORT = Boolean.parseBoolean( AppPropertiesService.getProperty( "forms.export.pdf.zip", "false" ) );
    
    private boolean _hasMultipleFiles = false;

    public PdfFileGenerator(String formName, FormPanel formPanel, List<IFormColumn> listFormColumn, List<FormFilter> listFormFilter, FormItemSortConfig sortConfig, String fileDescription)
    {
        super( FileUtil.normalizeFileName( formName ), formName, formPanel, listFormColumn, listFormFilter, sortConfig, fileDescription);
    }

    @Override
    public Path generateFile( ) throws IOException
    {
        Path directoryFile = Paths.get( TMP_DIR, _fileName );
        if ( !directoryFile.toFile( ).exists( ) )
        {
            directoryFile.toFile( ).mkdir( );
        }
        writeExportFile( directoryFile );
        if ( hasMultipleFiles( ) )
        {
            return directoryFile;
        }
        File [ ] files = directoryFile.toFile( ).listFiles( ( File f ) -> f.getName( ).endsWith( EXTENSION_PDF ) );
        return files [0].toPath( );
    }

    @Override
    public String getDescription( )
    {
        return _fileDescription;
    }

    @Override
    public String getFileName( )
    {
        return _fileName + ( hasMultipleFiles( ) || isZippable( ) ? FileUtil.EXTENSION_ZIP : EXTENSION_PDF );
    }

    @Override
    public String getMimeType( )
    {
        return hasMultipleFiles( ) || isZippable( ) ? FileUtil.CONSTANT_MIME_TYPE_ZIP : CONSTANT_MIME_TYPE_PDF;
    }

    @Override
    public boolean isZippable( )
    {
        return ZIP_EXPORT;
    }

    @Override
    public boolean hasMultipleFiles( )
    {
        return _hasMultipleFiles;
    }

    private void writeExportFile( Path directoryFile ) throws IOException
    {
        List<Question> listQuestions = QuestionHome.getListQuestionByIdForm(_form.getId());
        listQuestions.sort(Comparator.comparingInt(Question::getExportDisplayOrder));
        List<FormResponse> listResponse = FormResponseHome.selectAllFormResponsesUncompleteByIdForm(_form.getId());
        listResponse.removeIf(FormResponse::isFromSave);
        listResponse.sort(Comparator.comparing(FormResponse::getCreation));
        List<List<FormQuestionResponse>>  formQuestionResponseList = new ArrayList<>();
        _hasMultipleFiles = true;

        for (int i = 0; i < listResponse.size(); i++)
        {
            if(!listResponse.get(i).isFromSave())
            {
              formQuestionResponseList.add(FormQuestionResponseHome.getFormQuestionResponseListByFormResponse(listResponse.get(i).getId()));
            }
        }
        int intNumberOfFormResponsesPerPdf = MultiviewConfig.getInstance().getNumberOfFormResponsesPerPdf();
        String customTemplate = getTemplateExportPDF();
        String strTemplateResponses ;
        String strTemplateCover = null;
        boolean isCustomTemplate = false;
        if(customTemplate != null)
        {
            strTemplateResponses = customTemplate;
            isCustomTemplate = true;
        }
        else
        {
                strTemplateResponses = generateTemplateForPdfExportResponses(_form, listQuestions);
                strTemplateCover = generateTemplateForPdfExportCoverPage(_form, listResponse);
        }
        if ( intNumberOfFormResponsesPerPdf == 0)
        {
            intNumberOfFormResponsesPerPdf = formQuestionResponseList.size();
        }
        if(intNumberOfFormResponsesPerPdf > formQuestionResponseList.size())
        {
            intNumberOfFormResponsesPerPdf = formQuestionResponseList.size();
        }
        int numberOfLoop = formQuestionResponseList.size() / intNumberOfFormResponsesPerPdf;
        if(formQuestionResponseList.size() % intNumberOfFormResponsesPerPdf != 0){
            numberOfLoop++;
        }
        int startRange = 0;
        int endRange = intNumberOfFormResponsesPerPdf;
        for (int i = 0; i < numberOfLoop; i++)
        {
            if(i == numberOfLoop - 1)
            {
                endRange = formQuestionResponseList.size();
            }
            String fileName = _form.getTitle() + "_" + startRange + "_" + endRange;
            generatedPdfForRangeOfFormResponses(directoryFile, strTemplateResponses, listQuestions, listResponse, formQuestionResponseList.subList(startRange, endRange), startRange, fileName,isCustomTemplate);
            startRange = endRange;
            endRange += intNumberOfFormResponsesPerPdf;
        }
        if(strTemplateCover != null )
        {
            try {
                String fileName = _form.getTitle() + "_cover";
                generatePdfFile(directoryFile,strTemplateCover, fileName, isCustomTemplate);
            } catch (PdfConverterServiceException e) {
                AppLogService.error( "Error generating pdf for cover", e );
            }
        }
    }
}
