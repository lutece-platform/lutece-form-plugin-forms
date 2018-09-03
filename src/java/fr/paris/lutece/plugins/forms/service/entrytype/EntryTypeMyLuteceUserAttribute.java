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
package fr.paris.lutece.plugins.forms.service.entrytype;

import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.MandatoryError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeMyLuteceUser;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

/**
 * class EntryTypeText
 */
public class EntryTypeMyLuteceUserAttribute extends AbstractEntryTypeMyLuteceUser implements IResponseComparator
{
    /**
     * Name of the bean of this service
     */
    public static final String BEAN_NAME = "forms.entryTypeMyLuteceUser";

    public static final String CONSTANT_COMMA = ",";

    private static final String TEMPLATE_CREATE = "admin/plugins/forms/entries/create_entry_type_mylutece_user_attribute.html";
    private static final String TEMPLATE_MODIFY = "admin/plugins/forms/entries/modify_entry_type_mylutece_user_attribute.html";
    private static final String TEMPLATE_READONLY_BACKOFFICE = "admin/plugins/forms/entries/readonly_entry_type_mylutece_user_attribute.html";
    private static final String TEMPLATE_EDITION_BACKOFFICE = "admin/plugins/forms/entries/fill_entry_type_mylutece_user_attribute.html";
    private static final String TEMPLATE_EDITION_FRONTOFFICE = "skin/plugins/forms/entries/fill_entry_type_mylutece_user_attribute.html";
    private static final String TEMPLATE_READONLY_FRONTOFFICE = "skin/plugins/forms/entries/readonly_entry_type_mylutece_user_attribute.html";;
    private static final String PROPERTY_ENTRY_TITLE = "forms.entryTypeMyLuteceUserAttribute.title";
    private static final String PARAMETER_ONLY_DISPLAY_IN_BACK = "only_display_in_back";
    private static final String PARAMETER_MYLUTECE_ATTRIBUTE_NAME = "mylutece_attribute_name";

    private ReferenceList _refListUserAttributes;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateHtmlForm( Entry entry, boolean bDisplayFront )
    {
        if ( bDisplayFront )
        {
            return TEMPLATE_EDITION_FRONTOFFICE;
        }

        return TEMPLATE_EDITION_BACKOFFICE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateCreate( Entry entry, boolean bDisplayFront )
    {
        return TEMPLATE_CREATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateModify( Entry entry, boolean bDisplayFront )
    {
        return TEMPLATE_MODIFY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequestData( Entry entry, HttpServletRequest request, Locale locale )
    {
        entry.setTitle( I18nService.getLocalizedString( PROPERTY_ENTRY_TITLE, locale ) );

        entry.setComment( StringUtils.EMPTY );
        entry.setOnlyDisplayInBack( Boolean.parseBoolean( request.getParameter( PARAMETER_ONLY_DISPLAY_IN_BACK ) ) );
        entry.setMandatory( entry.isConfirmField( ) && Boolean.parseBoolean( request.getParameter( PARAMETER_MANDATORY ) ) );
        entry.setCSSClass( request.getParameter( PARAMETER_CSS_CLASS ) );
        entry.setTitle( request.getParameter( PARAMETER_TITLE ) );
        entry.setHelpMessage( request.getParameter( PARAMETER_HELP_MESSAGE ) );

        if ( entry.getFields( ) == null )
        {
            ArrayList<Field> listFields = new ArrayList<Field>( );
            Field field = new Field( );
            listFields.add( field );
            entry.setFields( listFields );
        }

        entry.getFields( ).get( 0 ).setValue( request.getParameter( PARAMETER_MYLUTECE_ATTRIBUTE_NAME ) );
        entry.getFields( ).get( 0 ).setWidth( 50 );
        entry.getFields( ).get( 0 ).setMaxSizeEnter( 0 );

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericAttributeError getResponseData( Entry entry, HttpServletRequest request, List<Response> listResponse, Locale locale )
    {
        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );

        if ( ( user == null ) && SecurityService.isAuthenticationEnable( ) && SecurityService.getInstance( ).isExternalAuthentication( ) )
        {
            try
            {
                user = SecurityService.getInstance( ).getRemoteUser( request );
            }
            catch( UserNotSignedException e )
            {
                AppLogService.error( e.getMessage( ), e );
            }
        }

        String strAttribute = entry.getFields( ).get( 0 ).getValue( );

        if ( ( user == null ) || ( StringUtils.isNotEmpty( strAttribute ) && StringUtils.isEmpty( user.getUserInfo( strAttribute ) ) ) )
        {
            if ( entry.isMandatory( ) )
            {
                return new MandatoryError( entry, locale );
            }

            return null;
        }

        Response response = new Response( );
        response.setEntry( entry );
        response.setResponseValue( user.getUserInfo( strAttribute ) );
        response.setIterationNumber( getResponseIterationValue( request ) );

        listResponse.add( response );

        return null;
    }

    /**
     * Get a reference list with every lutece user attributes
     * 
     * @param strLangage
     *            the langage of admin user
     * @return The reference list with every user attributes
     */
    public ReferenceList getLuteceUserAttributesRefList( String strLangage )
    {
        if ( _refListUserAttributes != null )
        {
            return _refListUserAttributes;
        }

        ReferenceList referenceList = new ReferenceList( );

        String strAttributesList = AppPropertiesService.getProperty( FormsConstants.PROPERTY_MY_LUTECE_ATTRIBUTES_LIST, StringUtils.EMPTY );
        String [ ] tabAttributes = StringUtils.split( strAttributesList, CONSTANT_COMMA );
        for ( String strAttribute : tabAttributes )
        {
            ReferenceItem item = new ReferenceItem( );

            // First set the code
            item.setCode( strAttribute );

            // Then construct the item
            StringBuilder myLuteceAttribute18nKey = new StringBuilder( );
            myLuteceAttribute18nKey.append( FormsConstants.CONSTANT_MYLUTECE_ATTRIBUTE_I18N_PREFIX );
            myLuteceAttribute18nKey.append( strAttribute );
            item.setName( I18nService.getLocalizedString( myLuteceAttribute18nKey.toString( ), Locale.forLanguageTag( strLangage ) ) );

            referenceList.add( item );
        }

        // We save the reference list to avoid its generation each time this method is called
        _refListUserAttributes = referenceList;

        return referenceList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponseValueForExport( Entry entry, HttpServletRequest request, Response response, Locale locale )
    {
        return response.getResponseValue( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponseValueForRecap( Entry entry, HttpServletRequest request, Response response, Locale locale )
    {
        return response.getResponseValue( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateEntryReadOnly( boolean bDisplayFront )
    {
        if ( bDisplayFront )
        {
            return TEMPLATE_READONLY_FRONTOFFICE;
        }
        return TEMPLATE_READONLY_BACKOFFICE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isResponseChanged( List<Response> listResponseReference, List<Response> listResponseNew )
    {
        return false;
    }
}
