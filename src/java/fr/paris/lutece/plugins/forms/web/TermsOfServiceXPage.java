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
package fr.paris.lutece.plugins.forms.web;

import java.util.Locale;
import java.util.Map;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeTermsOfService;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * This class is an XPage for the terms of service
 *
 */
@RequestScoped
@Named( "forms.xpage.formsTermsOfService" )
@Controller( xpageName = TermsOfServiceXPage.XPAGE_NAME, pageTitleI18nKey = TermsOfServiceXPage.MESSAGE_PAGE_TITLE, pagePathI18nKey = TermsOfServiceXPage.MESSAGE_PATH )
public class TermsOfServiceXPage extends MVCApplication
{
    protected static final String XPAGE_NAME = "formsTermsOfService";

    // Messages
    protected static final String MESSAGE_PAGE_TITLE = "forms.xpage.termsOfService.view.pageTitle";
    protected static final String MESSAGE_PATH = "forms.xpage.form.termsOfService.pagePathLabel";
    protected static final String MESSAGE_ERROR_TERMS_OF_SERVICE_NOT_FOUND = "forms.error.termsOfService.not.found";

    private static final long serialVersionUID = 4770204579727837581L;

    // Views
    private static final String VIEW_TERMS_OF_SERVICE = "termsOfService";
    private static final String VIEW_ACCEPTED_TERMS_OF_SERVICE = "acceptedTermsOfService";

    // Marks
    private static final String MARK_TERMS_OF_SERVICE = "tos";

    // Template
    private static final String TEMPLATE_XPAGE_REQUIREMENT_FORM = "skin/plugins/forms/terms_of_service.html";

    // Parameters
    private static final String PARAMETER_ENTRY_ID = "id_entry";
    private static final String PARAMETER_RESPONSE_ID = "id_response";

    /**
     * Returns the view for the terms of service
     * 
     * @param request
     *            The HttpServletRequest
     * @return the view associated to the terms of service
     * @throws SiteMessageException
     *             if there is an error during the treatment
     */
    @View( value = VIEW_TERMS_OF_SERVICE, defaultView = true )
    public XPage getTermsOfServiceView( HttpServletRequest request ) throws SiteMessageException
    {
        String strTermsOfService = findTermsOfServiceFromEntry( request );
        HtmlTemplate htmlTemplate = generateTermsOfService( strTermsOfService, request.getLocale( ) );

        return createXPage( htmlTemplate, request.getLocale( ) );
    }

    /**
     * Finds the terms of service from the entry
     * 
     * @param request
     *            the request containing the entry
     * @return the terms of service
     * @throws SiteMessageException
     *             if there is an error during the treatment
     */
    private String findTermsOfServiceFromEntry( HttpServletRequest request ) throws SiteMessageException
    {
        int nIdEntry = NumberUtils.toInt( request.getParameter( PARAMETER_ENTRY_ID ), FormsConstants.DEFAULT_ID_VALUE );

        if ( nIdEntry == FormsConstants.DEFAULT_ID_VALUE )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR_TERMS_OF_SERVICE_NOT_FOUND, SiteMessage.TYPE_STOP );
        }

        Entry entry = EntryHome.findByPrimaryKey( nIdEntry );

        if ( entry == null )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR_TERMS_OF_SERVICE_NOT_FOUND, SiteMessage.TYPE_STOP );
        }

        Field fieldTermsOfService = entry.getFieldByCode( EntryTypeTermsOfService.FIELD_TOS_CODE );

        if ( fieldTermsOfService == null )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR_TERMS_OF_SERVICE_NOT_FOUND, SiteMessage.TYPE_STOP );
        }

        return fieldTermsOfService.getValue( );
    }

    /**
     * Generated the HTML template for the terms of service
     * 
     * @param strTermsOfService
     *            The terms of service
     * @param locale
     *            the locale
     * @return the HTML template
     */
    private HtmlTemplate generateTermsOfService( String strTermsOfService, Locale locale )
    {
        Map<String, Object> model = getModel( );

        model.put( MARK_TERMS_OF_SERVICE, strTermsOfService );

        return AppTemplateService.getTemplate( TEMPLATE_XPAGE_REQUIREMENT_FORM, locale, model );
    }

    /**
     * Creates an XPage
     * 
     * @param htmlTemplate
     *            the HTML template
     * @param locale
     *            the locale
     * @return the created XPage
     */
    private XPage createXPage( HtmlTemplate htmlTemplate, Locale locale )
    {
        XPage page = new XPage( );
        page.setTitle( I18nService.getLocalizedString( MESSAGE_PAGE_TITLE, locale ) );
        page.setPathLabel( I18nService.getLocalizedString( MESSAGE_PATH, locale ) );
        page.setContent( htmlTemplate.getHtml( ) );

        return page;
    }

    /**
     * Returns the view for the accepted terms of service
     * 
     * @param request
     *            The request
     * @return the view associated to the accepted terms of service
     * @throws SiteMessageException
     *             if there is an error during the generation
     */
    @View( value = VIEW_ACCEPTED_TERMS_OF_SERVICE )
    public XPage getAcceptedTermsOfServiceView( HttpServletRequest request ) throws SiteMessageException
    {
        String strTermsOfService = findTermsOfServiceFromResponse( request );
        HtmlTemplate htmlTemplate = generateTermsOfService( strTermsOfService, request.getLocale( ) );

        return createXPage( htmlTemplate, request.getLocale( ) );
    }

    /**
     * Finds the terms of service from the response
     * 
     * @param request
     *            the request containing the response
     * @return the terms of service
     * @throws SiteMessageException
     *             if there is an error during the treatment
     */
    private String findTermsOfServiceFromResponse( HttpServletRequest request ) throws SiteMessageException
    {
        int nIdResponse = NumberUtils.toInt( request.getParameter( PARAMETER_RESPONSE_ID ), FormsConstants.DEFAULT_ID_VALUE );

        if ( nIdResponse == FormsConstants.DEFAULT_ID_VALUE )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR_TERMS_OF_SERVICE_NOT_FOUND, SiteMessage.TYPE_STOP );
        }

        Response response = ResponseHome.findByPrimaryKey( nIdResponse );

        if ( response == null || response.getField( ) == null )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR_TERMS_OF_SERVICE_NOT_FOUND, SiteMessage.TYPE_STOP );
        }

        Field fieldTermsOfService = FieldHome.findByPrimaryKey( response.getField( ).getIdField( ) );

        if ( fieldTermsOfService == null || !EntryTypeTermsOfService.FIELD_TOS_CODE.equals( fieldTermsOfService.getCode( ) ) )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR_TERMS_OF_SERVICE_NOT_FOUND, SiteMessage.TYPE_STOP );
        }

        return response.getToStringValueResponse( );
    }
}
