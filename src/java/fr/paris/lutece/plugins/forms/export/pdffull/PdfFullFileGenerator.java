/*
 * Copyright (c) 2002-2025, City of Paris
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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;

import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.form.FormItemSortConfig;
import fr.paris.lutece.plugins.forms.business.form.FormResponseItem;
import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.filter.FormFilter;
import fr.paris.lutece.plugins.forms.business.form.panel.FormPanel;
import fr.paris.lutece.plugins.forms.export.pdf.AbstractPdfFileGenerator;
import fr.paris.lutece.plugins.forms.service.MultiviewFormService;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFileHome;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.file.FileUtil;
import fr.paris.lutece.util.html.HtmlTemplate;

public class PdfFullFileGenerator extends AbstractPdfFileGenerator
{
    private boolean _hasMultipleFiles = false;
    
    protected PdfFullFileGenerator( String formName, FormPanel formPanel, List<IFormColumn> listFormColumn, List<FormFilter> listFormFilter,
            FormItemSortConfig sortConfig, String fileDescription )
    {
        super( FileUtil.normalizeFileName( formName ), formName, formPanel, listFormColumn, listFormFilter, sortConfig, fileDescription );
    }

    @Override
    public boolean hasMultipleFiles( )
    {
        return _hasMultipleFiles;
    }

    @Override
    public String getDescription( )
    {
        return _fileDescription;
    }

    @Override
    public String getFileName( )
    {
        return _fileName + FileUtil.EXTENSION_ZIP;
    }

    @Override
    public boolean isZippable( )
    {
        return hasMultipleFiles( );
    }

    @Override
    public String getMimeType( )
    {
        return FileUtil.CONSTANT_MIME_TYPE_ZIP;
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
        File [ ] files = directoryFile.toFile( ).listFiles( ( File f ) -> f.getName( ).endsWith( FileUtil.EXTENSION_ZIP ) );
        return files [0].toPath( );
    }

    private void writeExportFile( Path directoryFile ) throws IOException
    {
        List<FormResponseItem> listFormResponseItems = MultiviewFormService.getInstance( ).searchAllListFormResponseItem( _formPanel, _listFormColumn,
                _listFormFilter, _sortConfig );
        _hasMultipleFiles = listFormResponseItems.size( ) > 1;
        generateOneFormResponsePerFile(directoryFile, listFormResponseItems);
    }

    private void generateOneFormResponsePerFile(Path directoryFile, List<FormResponseItem> listFormResponseItems) throws IOException
    {
    	for ( FormResponseItem responseItem : listFormResponseItems )
        {
        	try {
				Map<String, Object> model = new HashMap<>( );
				
				FormResponse formResponse = FormResponseHome.findByPrimaryKey( responseItem.getIdFormResponse( ) );
				HtmlTemplate htmltemplate = generateHtmlSingleFormResponsesFromTemplate(model, formResponse);
				
				String generatedName = generateFileName(formResponse);
				Path pdfFile = directoryFile.resolve( generatedName + ".pdf" );
				generatePdfFile(directoryFile, htmltemplate, generatedName);
				generateAttachments(directoryFile, formResponse, pdfFile, generatedName);
			}
        	catch (IOException e)
        	{
				AppLogService.error( LOG_ERROR_PDF_EXPORT_GENERATION, e );
				throw e;
			}
        	catch (Exception e)
        	{
				AppLogService.error( LOG_ERROR_PDF_EXPORT_GENERATION, e );
				throw new IOException(e);
			}
        }
    }
    
    private void generateAttachments(Path directoryFile, FormResponse formResponse, Path pdfFile, String generatedName) throws IOException, Exception
    {
    	List<Path> listAttachments = writeAndGetAttachments( directoryFile, formResponse );

        Path [ ] filesToZip = listAttachments.toArray( new Path [ listAttachments.size( ) + 1] );
        filesToZip [listAttachments.size( )] = pdfFile;

        Path zipfile = directoryFile.resolve( generatedName + FileUtil.EXTENSION_ZIP );
        FileUtil.zipFiles( zipfile, filesToZip );

        for ( Path file : filesToZip )
        {
            FileUtil.deleteFile( file.toFile( ) );
        }
    }

    /**
     * Get all attachements in the {@link FormResponse} and write them to disk
     * 
     * @param directoryFile
     * @param formResponse
     * @return
     * @throws IOException, Exception
     */
    private List<Path> writeAndGetAttachments( Path directoryFile, FormResponse formResponse ) throws IOException, Exception
    {
        List<Response> listResponse = formResponse.getSteps( ).stream( ).flatMap( frs -> frs.getQuestions( ).stream( ) )
                .flatMap( fqr -> fqr.getEntryResponse( ).stream( ) ).collect( Collectors.toList( ) );
        List<String> fileNames = new ArrayList<>( );
        List<Path> listAttachments = new ArrayList<>( );
        for ( Response response : listResponse )
        {
            if ( response.getFile( ) != null )
            {
                fr.paris.lutece.portal.business.file.File coreFile = FileHome.findByPrimaryKey( response.getFile( ).getIdFile( ) );
                if (coreFile == null)
                {
                	continue;
                }

                String baseFilename = FilenameUtils.removeExtension( coreFile.getTitle( ) );
                String extension = FilenameUtils.getExtension( coreFile.getTitle( ) );
                String filename = coreFile.getTitle( );

                int index = 1;
                while (fileNames.contains(filename))
                {
                    filename = baseFilename + "_" + index + "." + extension;
                    index++;
                }

                fileNames.add(filename);

                Path attachment = directoryFile.resolve( filename );

                PhysicalFile physicalFile = PhysicalFileHome.findByPrimaryKey( coreFile.getPhysicalFile( ).getIdPhysicalFile( ) );
                if (physicalFile != null)
                {
                	try ( OutputStream outputStream = Files.newOutputStream( attachment ) )
                    {
                        outputStream.write( physicalFile.getValue( ) );
                    }
                    listAttachments.add( attachment );
                }
            }
        }
    	return listAttachments;
    }
}
