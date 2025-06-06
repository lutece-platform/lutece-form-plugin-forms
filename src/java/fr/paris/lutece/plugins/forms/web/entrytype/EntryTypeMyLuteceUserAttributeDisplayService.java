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
package fr.paris.lutece.plugins.forms.web.entrytype;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.column.impl.FormColumnEntry;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.template.AppTemplateService;

/**
 * The display service for entry type date
 */
public class EntryTypeMyLuteceUserAttributeDisplayService implements IEntryDisplayService
{
    private static final String MARK_USER = "user";

    private String _strEntryServiceName = StringUtils.EMPTY;

    /**
     * Constructor of the EntryTypeDateDisplayService
     * 
     * @param strEntryServiceName
     *            The entry service name
     */
    public EntryTypeMyLuteceUserAttributeDisplayService( String strEntryServiceName )
    {
        _strEntryServiceName = strEntryServiceName;
    }

    /**
     * Return the completed model
     * 
     * @param entry
     *            The given entry
     * @param request
     *            the request
     * @param displayType
     *            The display type
     * @param model
     *            The given model
     * @return the completed model
     */
    private Map<String, Object> setModel( Entry entry, HttpServletRequest request, DisplayType displayType, Map<String, Object> model )
    {
        model.put( FormsConstants.QUESTION_ENTRY_MARKER, entry );

        if ( displayType.isFront( ) )
        {
            LuteceUser user = findLuteceUserFrom( request );
            model.put( MARK_USER, user );
        }

        return model;
    }

    /**
     * Finds the LuteceUser associated to the request
     * 
     * @param request
     *            The request to retrieve the LuteceUser from
     * @return the LuteceUser of the request
     */
    private static LuteceUser findLuteceUserFrom( HttpServletRequest request )
    {
        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );

        if ( user == null && SecurityService.isAuthenticationEnable( ) && SecurityService.getInstance( ).isExternalAuthentication( ) )
        {
            try
            {
                user = SecurityService.getInstance( ).getRemoteUser( request );
            }
            catch( UserNotSignedException e )
            {
                // Nothing to do : lutece user is not mandatory
            }
        }

        return user;
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
        IEntryTypeService service = EntryTypeServiceManager.getEntryTypeService( entry );

        switch( displayType.getMode( ) )
        {
            case EDITION:
                strEntryHtml = AppTemplateService
                        .getTemplate( service.getTemplateHtmlForm( entry, displayType.isFront( ) ), locale, setModel( entry, request, displayType, model ) )
                        .getHtml( );
                break;
            case READONLY:
                strEntryHtml = AppTemplateService
                        .getTemplate( service.getTemplateEntryReadOnly( displayType.isFront( ) ), locale, setModel( entry, request, displayType, model ) )
                        .getHtml( );
                break;
            default: // Nothing to do
        }

        return strEntryHtml;
    }

    /**
     * Get the form column
     * 
     * @param nFormColumnPosition
     * @param strColumnTitle
     * @return The form column
     */
    @Override
    public IFormColumn getFormColumn( int nFormColumnPosition, String strColumnTitle )
    {
        return new FormColumnEntry( nFormColumnPosition, strColumnTitle );
    }
}
