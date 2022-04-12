/*
 * Copyright (c) 2002-2021, City of Paris
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

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.service.FormResponseService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;

/**
 * 
 * Controller for form reponses display
 *
 */
@Controller( xpageName = MyFormResponsesXPage.XPAGE_NAME, pageTitleI18nKey = MyFormResponsesXPage.MESSAGE_PAGE_TITLE, pagePathI18nKey = MyFormResponsesXPage.MESSAGE_PATH )

public class MyFormResponsesXPage extends MVCApplication
{
    private static final long serialVersionUID = 3045035844703500479L;

    protected static final String XPAGE_NAME = "myformresponses";

    // Messages
    protected static final String MESSAGE_PAGE_TITLE = "forms.xpage.myformresponses.view.pageTitle";
    protected static final String MESSAGE_PATH = "forms.xpage.myformresponses.view.pagePathLabel";
    private static final String MESSAGE_LIST_FORMRESPONSES_PAGETITLE = "forms.xpage.myformresponses.pagetitle";
    private static final String MESSAGE_LIST_FORMRESPONSES_PATHLABEL = "forms.xpage.myformresponses.pathlabel";

    // Views
    private static final String VIEW_LIST_FORM_RESPONSES = "listFormResponses";
    
    // Marks
    private static final String MARK_RESPONSE_LIST = "response_list";

    // Templates
    private static final String TEMPLATE_VIEW_FORMRESPONSES_LIST = "/skin/plugins/forms/list_formresponses.html";

    /**
     * Return the default XPage with the list of all available Form
     * 
     * @param request
     *            The HttpServletRequest
     * @return the list of all available forms
     * @throws UserNotSignedException
     */
    @View( value = VIEW_LIST_FORM_RESPONSES, defaultView = true )
    public XPage getListFormResponsesView( HttpServletRequest request ) throws UserNotSignedException
    {
        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );
        if ( user == null )
        {
            throw new UserNotSignedException( );
        }

        Map<String, Object> model = getModel( );
        Locale locale = getLocale( request );
        model.put( MARK_RESPONSE_LIST, FormResponseService.getInstance().getFormResponseListForUser( user ) );

        XPage xPage = getXPage( TEMPLATE_VIEW_FORMRESPONSES_LIST, locale, model );
        xPage.setTitle( I18nService.getLocalizedString( MESSAGE_LIST_FORMRESPONSES_PAGETITLE, locale ) );
        xPage.setPathLabel( I18nService.getLocalizedString( MESSAGE_LIST_FORMRESPONSES_PATHLABEL, locale ) );

        return xPage;
    }
}
