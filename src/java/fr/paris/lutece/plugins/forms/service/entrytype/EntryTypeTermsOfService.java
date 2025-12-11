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
package fr.paris.lutece.plugins.forms.service.entrytype;

import java.util.List;
import java.util.Locale;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeService;
import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.portal.service.html.HtmlCleanerService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;

/**
 * This class is a service for the entry type Terms of service
 *
 */
@ApplicationScoped
@Named( "forms.entryTypeTermsOfService" )
public class EntryTypeTermsOfService extends EntryTypeService implements IResponseComparator
{
    // Fields
    public static final String FIELD_TOS_CODE = "tos";

    // Messages
    private static final String MESSAGE_ENTRY_LINK_TITLE = "forms.create_entry.termsOfService.labelLink";
    private static final String MESSAGE_ENTRY_TOS_TITLE = "forms.create_entry.termsOfService.labelTermsOfService";
    private static final String MESSAGE_ENTRY_MANDATORY = "forms.message.mandatory.termsOfService";

    // Templates
    private static final String TEMPLATE_CREATE = "admin/plugins/forms/entries/create_entry_type_terms_of_service.html";
    private static final String TEMPLATE_MODIFY = "admin/plugins/forms/entries/modify_entry_type_terms_of_service.html";
    private static final String TEMPLATE_READONLY_BACKOFFICE = "admin/plugins/forms/entries/readonly_entry_type_terms_of_service.html";
    private static final String TEMPLATE_EDITION_BACKOFFICE = "admin/plugins/forms/entries/fill_entry_type_terms_of_service.html";
    private static final String TEMPLATE_EDITION_FRONTOFFICE = "skin/plugins/forms/entries/fill_entry_type_terms_of_service.html";
    private static final String TEMPLATE_READONLY_FRONTOFFICE = "skin/plugins/forms/entries/readonly_entry_type_terms_of_service.html";

    // Field codes
    private static final String FIELD_LINK_CODE = "link";
    public static final String FIELD_AGREEMENT_CODE = "agreement";

    // Parameters
    private static final String PARAMETER_LINK = FIELD_LINK_CODE;
    private static final String PARAMETER_TOS = FIELD_TOS_CODE;

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
    public String getRequestData( Entry entry, HttpServletRequest request, Locale locale )
    {
        initCommonRequestData( entry, request );
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strLinkText = request.getParameter( PARAMETER_LINK );
        String strTermsOfService = request.getParameter( PARAMETER_TOS );

        String strFieldInError = validateConfig( strTitle, strLinkText, strTermsOfService );

        if ( StringUtils.isNotEmpty( strFieldInError ) )
        {
            return buildErrorUrl( strFieldInError, request, locale );
        }

        String strCSSClass = request.getParameter( PARAMETER_CSS_CLASS );

        entry.setIndexed( request.getParameter( PARAMETER_INDEXED ) != null );
        entry.setTitle( strTitle );
        entry.setCSSClass( strCSSClass );
        GenericAttributesUtils.createOrUpdateField( entry, FIELD_LINK_CODE, null, strLinkText );
        GenericAttributesUtils.createOrUpdateField( entry, FIELD_TOS_CODE, null, strTermsOfService );
        GenericAttributesUtils.createOrUpdateField( entry, FIELD_AGREEMENT_CODE, null, Boolean.FALSE.toString( ) );
        return null;
    }

    /**
     * Validates the configuration of the entry type
     * 
     * @param strTitle
     *            the title to validate
     * @param strLinkText
     *            the link text to validate
     * @param strTermsOfService
     *            the terms of service to validate
     * @return the field in error, or empty if all the fields are valid
     */
    private String validateConfig( String strTitle, String strLinkText, String strTermsOfService )
    {
        if ( StringUtils.isBlank( strTitle ) )
        {
            return ERROR_FIELD_TITLE;
        }

        if ( StringUtils.isBlank( strLinkText ) )
        {
            return MESSAGE_ENTRY_LINK_TITLE;
        }

        if ( StringUtils.isBlank( strTermsOfService ) )
        {
            return MESSAGE_ENTRY_TOS_TITLE;
        }

        return StringUtils.EMPTY;
    }

    /**
     * Builds the error URL
     * 
     * @param strFieldInError
     *            the field in error
     * @param request
     *            the request
     * @param locale
     *            the locale
     * @return the error URL
     */
    private String buildErrorUrl( String strFieldInError, HttpServletRequest request, Locale locale )
    {
        Object [ ] tabRequiredFields = {
                I18nService.getLocalizedString( strFieldInError, locale ),
        };

        return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericAttributeError getResponseData( Entry entry, HttpServletRequest request, List<Response> listResponse, Locale locale )
    {
        int nIdFieldAgreement = NumberUtils.toInt( request.getParameter( PREFIX_ATTRIBUTE + entry.getIdEntry( ) ), FormsConstants.DEFAULT_ID_VALUE );
        Response responseAgreement = createResponse( entry, request );
        listResponse.add( responseAgreement );

        GenericAttributeError error = validateUserResponse( entry, nIdFieldAgreement, locale );

        if ( error != null )
        {
            return error;
        }

        Field fieldAgreement = GenericAttributesUtils.findFieldByIdInTheList( nIdFieldAgreement, entry.getFields( ) );

        // this field is not null because its presence has been validated
        fieldAgreement.setValue( Boolean.TRUE.toString( ) );

        responseAgreement.setResponseValue( fieldAgreement.getValue( ) );
        responseAgreement.setField( fieldAgreement );

        listResponse.add( createResponseForAcceptedTermsOfService( entry, request ) );

        return null;
    }

    /**
     * Creates a response
     * 
     * @param entry
     *            the entry linked to the response
     * @param request
     *            the request
     * @return the created response
     */
    private Response createResponse( Entry entry, HttpServletRequest request )
    {
        Response response = new Response( );
        response.setEntry( entry );
        response.setIterationNumber( getResponseIterationValue( request ) );

        return response;
    }

    /**
     * Validates the user response
     * 
     * @param entry
     *            the entry to validate
     * @param nIdFieldAgreement
     *            the field id to validate
     * @param locale
     *            the locale
     * @return a {@code GenericAttributeError} if there is a validation error, {@code null} otherwise
     */
    private GenericAttributeError validateUserResponse( Entry entry, int nIdFieldAgreement, Locale locale )
    {
        GenericAttributeError error = null;

        if ( nIdFieldAgreement == FormsConstants.DEFAULT_ID_VALUE )
        {
            error = new GenericAttributeError( );
            error.setMandatoryError( true );
            error.setErrorMessage( I18nService.getLocalizedString( MESSAGE_ENTRY_MANDATORY, locale ) );
        }

        Field fieldAgreement = GenericAttributesUtils.findFieldByIdInTheList( nIdFieldAgreement, entry.getFields( ) );

        if ( fieldAgreement == null || !FIELD_AGREEMENT_CODE.equals( fieldAgreement.getCode( ) ) )
        {
            error = new GenericAttributeError( );
            error.setMandatoryError( true );
            error.setErrorMessage( I18nService.getLocalizedString( MESSAGE_ENTRY_MANDATORY, locale ) );
        }

        return error;
    }

    /**
     * Creates a response for the terms of service accepted by the user
     * 
     * @param entry
     *            the entry
     * @param request
     *            the request
     * @return the created response
     */
    private Response createResponseForAcceptedTermsOfService( Entry entry, HttpServletRequest request )
    {
        Field fieldAcceptedTermsOfService = entry.getFieldByCode( FIELD_TOS_CODE );

        Response response = createResponse( entry, request );
        response.setResponseValue( fieldAcceptedTermsOfService.getValue( ) );
        response.setField( fieldAcceptedTermsOfService );

        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isResponseChanged( List<Response> listResponseReference, List<Response> listResponseNew )
    {
        return false;
    }

    @Override
    public String getResponseValueForExport( Entry entry, HttpServletRequest request, Response response, Locale locale )
    {
        if ( StringUtils.isEmpty( response.getResponseValue( ) ) )
        {
            return StringUtils.EMPTY;
        }
        return HtmlCleanerService.text( response.getResponseValue( ) );
    }
}
