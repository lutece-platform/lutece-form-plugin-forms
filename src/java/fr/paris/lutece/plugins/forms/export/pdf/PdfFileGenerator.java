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

import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.MultiviewConfig;
import fr.paris.lutece.plugins.forms.business.form.FormItemSortConfig;
import fr.paris.lutece.plugins.forms.business.form.FormResponseItem;
import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.filter.FormFilter;
import fr.paris.lutece.plugins.forms.business.form.panel.FormPanel;
import fr.paris.lutece.plugins.forms.service.MultiviewFormService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.file.FileUtil;
import fr.paris.lutece.util.html.HtmlTemplate;

public class PdfFileGenerator extends AbstractPdfFileGenerator
{
    private static final boolean ZIP_EXPORT = Boolean.parseBoolean( AppPropertiesService.getProperty( "forms.export.pdf.zip", "false" ) );
    
    private boolean _hasMultipleFiles = false;
    
    protected PdfFileGenerator( String formName, FormPanel formPanel, List<IFormColumn> listFormColumn, List<FormFilter> listFormFilter,
            FormItemSortConfig sortConfig, String fileDescription )
    {
        super( FileUtil.normalizeFileName( formName ), formName, formPanel, listFormColumn, listFormFilter, sortConfig, fileDescription );
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
    
    @SuppressWarnings("unused")
	@Deprecated
    private void writeExportFileDeprecated( Path directoryFile ) throws IOException
    {
        FormResponsePdfExport export = new FormResponsePdfExport( );

        List<FormResponseItem> listFormResponseItems = MultiviewFormService.getInstance( ).searchAllListFormResponseItem( _formPanel, _listFormColumn,
                _listFormFilter, _sortConfig );

        _hasMultipleFiles = listFormResponseItems.size( ) > 1;
        for ( FormResponseItem responseItem : listFormResponseItems )
        {
            FormResponse formResponse = FormResponseHome.findByPrimaryKey( responseItem.getIdFormResponse( ) );
            try ( OutputStream outputStream = Files.newOutputStream( directoryFile.resolve( generateFileName( formResponse ) + ".pdf" ) ) )
            {
                export.buildPdfExport( formResponse, outputStream );
            }
        }
    }

    private void writeExportFile( Path directoryFile ) throws IOException
    {
        List<FormResponseItem> listFormResponseItems = MultiviewFormService.getInstance( ).searchAllListFormResponseItem( _formPanel, _listFormColumn,
                _listFormFilter, _sortConfig );
        _hasMultipleFiles = listFormResponseItems.size( ) > 1;
        
        int intNumberOfFormResponsesPerPdf = MultiviewConfig.getInstance().getNumberOfFormResponsesPerPdf();
        if (intNumberOfFormResponsesPerPdf > 1)
        {
        	generateMultipleFormResponsesPerFile(directoryFile, listFormResponseItems, intNumberOfFormResponsesPerPdf);
        }
        else
        {
        	generateOneFormResponsePerFile(directoryFile, listFormResponseItems);
        }
    }
    
    private void generateMultipleFormResponsesPerFile(Path directoryFile, List<FormResponseItem> listFormResponseItems, int intNumberOfFormResponsesPerPdf) throws IOException
    {
    	for (int intStartIndex = 0; intStartIndex < listFormResponseItems.size(); intStartIndex += intNumberOfFormResponsesPerPdf)
    	{
    		try {
				int intEndIndexExcluded = Math.min(intStartIndex + intNumberOfFormResponsesPerPdf, listFormResponseItems.size());
				List<FormResponseItem> subListFormResponseItems = listFormResponseItems.subList(intStartIndex, intEndIndexExcluded);
				List<FormResponse> subListFormResponse = new ArrayList<>();
				
				for(FormResponseItem responseItem : subListFormResponseItems)
				{
					FormResponse formResponse = FormResponseHome.findByPrimaryKey( responseItem.getIdFormResponse( ) );
					subListFormResponse.add(formResponse);
				}
				
				Map<String, Object> model = new HashMap<>( );
				HtmlTemplate htmltemplate = generateHtmlMultipleFormResponsesFromTemplate(model, subListFormResponse);
				
				String generatedName = generateMultiFormResponsesFileName(subListFormResponse, intStartIndex + 1, intEndIndexExcluded);
				generatePdfFile(directoryFile, htmltemplate, generatedName);
			} catch (IOException e) {
				AppLogService.error( LOG_ERROR_PDF_EXPORT_GENERATION, e );
				throw e;
			} catch (Exception e) {
				AppLogService.error( LOG_ERROR_PDF_EXPORT_GENERATION, e );
			}
    	}
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
				generatePdfFile(directoryFile, htmltemplate, generatedName);
			} catch (IOException e) {
				AppLogService.error( LOG_ERROR_PDF_EXPORT_GENERATION, e );
				throw e;
			} catch (Exception e) {
				AppLogService.error( LOG_ERROR_PDF_EXPORT_GENERATION, e );
			}
        }
    }

}
