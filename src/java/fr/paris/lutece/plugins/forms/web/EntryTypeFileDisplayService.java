/*
 * Copyright (c) 2002-2018, Mairie de Paris
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
package fr.paris.lutece.plugins.forms.web;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeUpload;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.service.template.AppTemplateService;

/**
 * The display service for entry type file
 */
public class EntryTypeFileDisplayService implements IEntryDisplayService
{
    private static final String MARK_UPLOAD_HANDLER = "uploadHandler";

    private static final String LIST_RESPONSES = "list_responses";

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
    private Map<String, Object> setModel( Entry entry, IEntryTypeService service, Map<String, Object> model )
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
    public String getEntryTemplateDisplay( Entry entry, Locale locale, Map<String, Object> model )
    {
        IEntryTypeService service = EntryTypeServiceManager.getEntryTypeService( entry );

        String strEntryHtml = AppTemplateService.getTemplate( service.getTemplateHtmlForm( entry, true ), locale, setModel( entry, service, model ) ).getHtml( );

        return strEntryHtml;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEntryResponseValueTemplateDisplay( Entry entry, Locale locale, Map<String, Object> model )
    {
        IEntryTypeService service = EntryTypeServiceManager.getEntryTypeService( entry );

        List<Response> listResponse = (List<Response>) model.get( LIST_RESPONSES );
        if ( !CollectionUtils.isEmpty( listResponse ) )
        {
            for ( Response response : listResponse )
            {
                File file = response.getFile( );
                if ( response.getFile( ) != null )
                {
                    response.setFile( FileHome.findByPrimaryKey( file.getIdFile( ) ) );
                }
            }
        }

        return AppTemplateService.getTemplate( service.getTemplateEntryReadOnly( ), locale, setModel( entry, service, model ) ).getHtml( );
    }
}
