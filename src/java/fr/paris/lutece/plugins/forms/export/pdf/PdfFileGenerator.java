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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities.EscapeMode;

import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.form.FormItemSortConfig;
import fr.paris.lutece.plugins.forms.business.form.FormResponseItem;
import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.filter.FormFilter;
import fr.paris.lutece.plugins.forms.business.form.panel.FormPanel;
import fr.paris.lutece.plugins.forms.export.AbstractFileGenerator;
import fr.paris.lutece.plugins.forms.service.MultiviewFormService;
import fr.paris.lutece.plugins.html2pdf.service.PdfConverterService;
import fr.paris.lutece.plugins.html2pdf.service.PdfConverterServiceException;
import fr.paris.lutece.plugins.workflow.modules.formspdf.service.template.IFormResponseTemplateService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.file.FileUtil;
import fr.paris.lutece.util.html.HtmlTemplate;

public class PdfFileGenerator extends AbstractFileGenerator
{
    private static final boolean ZIP_EXPORT = Boolean.parseBoolean( AppPropertiesService.getProperty( "forms.export.pdf.zip", "false" ) );
    private static final String CONSTANT_MIME_TYPE_PDF = "application/pdf";
    private static final String EXTENSION_PDF = ".pdf";

    private boolean _hasMultipleFiles = false;
    
    private static IFormResponseTemplateService _formResponseTemplateService = SpringContextService.getBean( "workflow-formspdf.formResponseTemplateService" );
    
    protected PdfFileGenerator( String formName, FormPanel formPanel, List<IFormColumn> listFormColumn, List<FormFilter> listFormFilter,
            FormItemSortConfig sortConfig, String fileDescription )
    {
        super( FileUtil.normalizeFileName( formName ), formPanel, listFormColumn, listFormFilter, sortConfig, fileDescription );
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
        for ( FormResponseItem responseItem : listFormResponseItems )
        {
        	Map<String, Object> model = new HashMap<>( );
        	
            FormResponse formResponse = FormResponseHome.findByPrimaryKey( responseItem.getIdFormResponse( ) );
            HtmlTemplate htmltemplate = _formResponseTemplateService.generateHtmlFromDefaultTemplate(model, formResponse);
            
            try ( OutputStream outputStream = Files.newOutputStream( directoryFile.resolve( generateFileName( formResponse ) + ".pdf" ) ) )
            {
            	Document doc = Jsoup.parse( htmltemplate.getHtml( ), UTF_8 );
	            doc.outputSettings( ).syntax( Document.OutputSettings.Syntax.xml );
	            doc.outputSettings( ).escapeMode( EscapeMode.base.xhtml );
	            doc.outputSettings( ).charset( UTF_8 );
	
	            PdfConverterService.getInstance( ).getPdfBuilder( ).reset( ).withHtmlContent( doc.html( ) ).notEditable( ).render( outputStream );
	        }
	        catch(PdfConverterServiceException | IOException e)
	        {
	            AppLogService.error( "Error generating pdf for response " + formResponse.getId( ), e );
	        }
	        
        }
    }
}
