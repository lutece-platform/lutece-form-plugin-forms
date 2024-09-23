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
package fr.paris.lutece.plugins.forms.web.entrytype;

import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.GenAttFileItem;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeUpload;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFileHome;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.FileServiceException;
import fr.paris.lutece.portal.service.file.IFileStoreServiceProvider;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;

/**
 * The display service for entry type file
 */
public class EntryTypeFileDisplayService implements IEntryDisplayService
{
    protected static final String MARK_UPLOAD_HANDLER = "uploadHandler";
    private String _strEntryServiceName = StringUtils.EMPTY;

    /**
     * Constructor of the EntryTypeFileDisplayService
     * 
     * @param strEntryServiceName
     *            The entry service name
     */
    public EntryTypeFileDisplayService( String strEntryServiceName )
    {
        _strEntryServiceName = strEntryServiceName;
    }

    /**
     * Return the completed model
     * 
     * @param entry
     *            The given entry
     * @param service
     *            The upload service
     * @param model
     *            The upload model
     * @return the completed model
     */
    public Map<String, Object> setModel( Entry entry, IEntryTypeService service, Map<String, Object> model )
    {
        model.put( FormsConstants.QUESTION_ENTRY_MARKER, entry );
        model.put( MARK_UPLOAD_HANDLER, ( (AbstractEntryTypeUpload) service ).getAsynchronousUploadHandler( ) );

        return model;
    }

    @Override
    public String getDisplayServiceName( )
    {
        return _strEntryServiceName;
    }

    @Override
    public String getEntryTemplateDisplay( HttpServletRequest request, Entry entry, Locale locale, Map<String, Object> model, DisplayType displayType )
    {
        String strEntryHtml = StringUtils.EMPTY;

        switch( displayType.getMode( ) )
        {
            case EDITION:
                strEntryHtml = getTemplateEdition( request, entry, displayType, locale, model );
                break;
            case READONLY:
                strEntryHtml = getEntryResponseValueTemplateDisplay( entry, displayType, locale, model );
                break;
            default: // Nothing to do
        }

        return strEntryHtml;
    }

    /**
     * Return the template with response file(s) in user session.
     * 
     * @param entry
     *            The Entry to edit
     * @param displayType
     *            the DisplayType
     * @param locale
     *            The Locale to use to build the model
     * @param model
     *            The model to populate
     * @return the template of the given Entry with its Response file(s) in user session.
     */
    private String getTemplateEdition( HttpServletRequest request, Entry entry, DisplayType displayType, Locale locale, Map<String, Object> model )
    {
        IEntryTypeService service = EntryTypeServiceManager.getEntryTypeService( entry );
        List<Response> listResponse = retrieveResponseListFromModel( model );
        List<FileItem> listFiles = new ArrayList<>( );


        for ( Response response : listResponse )
        {
            if ( response.getFile( ) != null )
            {
                try {
                    if ( StringUtils.isNotEmpty( response.getFile( ).getFileKey( ) ) )
                    {
                        IFileStoreServiceProvider fss = FileService.getInstance( ).getFileStoreServiceProvider( response.getFile( ).getOrigin( ) );
                        File file = fss.getFile( response.getFile( ).getFileKey( ) );

                        file.setUrl(displayType.isFront( ) ? fss.getFileDownloadUrlFO( file.getFileKey()) : fss.getFileDownloadUrlBO(file.getFileKey()));

                        PhysicalFile physicalFile = file.getPhysicalFile();
                        FileItem fileItem = new GenAttFileItem( physicalFile.getValue( ), file.getTitle( ) );
                        ( (AbstractEntryTypeUpload) service ).getAsynchronousUploadHandler( ).addFileItemToUploadedFilesList( fileItem, "nIt"
                                + response.getIterationNumber( ) + "_" + IEntryTypeService.PREFIX_ATTRIBUTE + Integer.toString( response.getEntry( ).getIdEntry( ) ),
                                request );
                        listFiles.add( fileItem );
                    }
                    else if ( response.getFile( ).getPhysicalFile( ) != null )
                    {
                        FileItem fileItem = new GenAttFileItem( response.getFile( ).getPhysicalFile( ).getValue( ), response.getFile( ).getTitle( ) );
                        ( (AbstractEntryTypeUpload) service ).getAsynchronousUploadHandler( ).addFileItemToUploadedFilesList( fileItem, "nIt"
                                + response.getIterationNumber( ) + "_" + IEntryTypeService.PREFIX_ATTRIBUTE + Integer.toString( response.getEntry( ).getIdEntry( ) ),
                                request );
                        listFiles.add( fileItem );
                    }
                }
                catch (FileServiceException e) 
                {
                    AppLogService.error("Error getting file from file store service provider", e);
                }
            }
        }

        model.put( "listFiles", listFiles );
        return AppTemplateService.getTemplate( service.getTemplateHtmlForm( entry, displayType.isFront( ) ), locale, setModel( entry, service, model ) )
                .getHtml( );
    }

    /**
     * Return the template of an Entry with its Response
     * 
     * @param entry
     *            The Entry to display
     * @param displayType
     *            the DisplayType
     * @param locale
     *            The Locale to use to build the model
     * @param model
     *            The model to populate
     * @return the template of the given Entry with its Response value
     * @throws FileServiceException 
     */
    private String getEntryResponseValueTemplateDisplay( Entry entry, DisplayType displayType, Locale locale, Map<String, Object> model )
    {
        IEntryTypeService service = EntryTypeServiceManager.getEntryTypeService( entry );

        List<Response> listResponse = retrieveResponseListFromModel( model );
        if ( !CollectionUtils.isEmpty( listResponse ) )
        {
            for ( Response response : listResponse )
            {
                File file = response.getFile( );
                if ( file != null && file.getPhysicalFile( ) == null )
                {
                    try {
                        IFileStoreServiceProvider fss = FileService.getInstance( ).getFileStoreServiceProvider( response.getFile().getOrigin() );
                        file = fss.getFile(file.getFileKey() );

                        file.setUrl(displayType.isFront( ) ? fss.getFileDownloadUrlFO( file.getFileKey()) : fss.getFileDownloadUrlBO(file.getFileKey()));

                        response.setFile( file );
                    }
                    catch (FileServiceException e) {
                        AppLogService.error("Error getting file from file store service provider", e);
                    }
                }
            }
        }

        return AppTemplateService.getTemplate( service.getTemplateEntryReadOnly( displayType.isFront( ) ), locale, setModel( entry, service, model ) )
                .getHtml( );
    }

    @Override
    public IFormColumn getFormColumn( int nFormColumnPosition, String strColumnTitle )
    {
        return null;
    }
}