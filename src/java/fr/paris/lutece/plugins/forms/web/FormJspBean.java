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

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.url.UrlItem;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * This class provides the user interface to manage Form features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageForms.jsp", controllerPath = "jsp/admin/plugins/forms/", right = "FORMS_MANAGEMENT" )
public class FormJspBean extends AbstractManageFormsJspBean
{
    // Templates
    private static final String TEMPLATE_MANAGE_FORMS = "/admin/plugins/forms/manage_forms.html";
    private static final String TEMPLATE_CREATE_FORM = "/admin/plugins/forms/create_form.html";
    private static final String TEMPLATE_MODIFY_FORM = "/admin/plugins/forms/modify_form.html";

    // Parameters
    private static final String PARAMETER_ID_FORM = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_FORMS = "forms.manage_forms.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_FORM = "forms.modify_form.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_FORM = "forms.create_form.pageTitle";

    // Markers
    private static final String MARK_FORM_LIST = "form_list";
    private static final String MARK_FORM = "form";

    private static final String JSP_MANAGE_FORMS = "jsp/admin/plugins/forms/ManageForms.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_FORM = "forms.message.confirmRemoveForm";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "forms.model.entity.form.attribute.";

    // Views
    private static final String VIEW_MANAGE_FORMS = "manageForms";
    private static final String VIEW_CREATE_FORM = "createForm";
    private static final String VIEW_MODIFY_FORM = "modifyForm";

    // Actions
    private static final String ACTION_CREATE_FORM = "createForm";
    private static final String ACTION_MODIFY_FORM = "modifyForm";
    private static final String ACTION_REMOVE_FORM = "removeForm";
    private static final String ACTION_CONFIRM_REMOVE_FORM = "confirmRemoveForm";

    // Infos
    private static final String INFO_FORM_CREATED = "forms.info.form.created";
    private static final String INFO_FORM_UPDATED = "forms.info.form.updated";
    private static final String INFO_FORM_REMOVED = "forms.info.form.removed";

    // Session variable to store working values
    private Form _form;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_FORMS, defaultView = true )
    public String getManageForms( HttpServletRequest request )
    {
        _form = null;
        List<Form> listForms = FormHome.getFormsList( );
        Map<String, Object> model = getPaginatedListModel( request, MARK_FORM_LIST, listForms, JSP_MANAGE_FORMS );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_FORMS, TEMPLATE_MANAGE_FORMS, model );
    }

    /**
     * Returns the form to create a form
     *
     * @param request
     *            The Http request
     * @return the html code of the form form
     */
    @View( VIEW_CREATE_FORM )
    public String getCreateForm( HttpServletRequest request )
    {
        _form = ( _form != null ) ? _form : new Form( );

        Map<String, Object> model = getModel( );
        model.put( MARK_FORM, _form );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_FORM, TEMPLATE_CREATE_FORM, model );
    }

    /**
     * Process the data capture form of a new form
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_FORM )
    public String doCreateForm( HttpServletRequest request )
    {
        populate( _form, request, request.getLocale( ) );

        // Check constraints
        if ( !validateBean( _form, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_FORM );
        }

        FormHome.create( _form );
        addInfo( INFO_FORM_CREATED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_FORMS );
    }

    /**
     * Manages the removal form of a form whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_FORM )
    public String getConfirmRemoveForm( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_FORM ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_FORM ) );
        url.addParameter( PARAMETER_ID_FORM, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_FORM, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a form
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage forms
     */
    @Action( ACTION_REMOVE_FORM )
    public String doRemoveForm( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_FORM ) );
        FormHome.remove( nId );
        addInfo( INFO_FORM_REMOVED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_FORMS );
    }

    /**
     * Returns the form to update info about a form
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_FORM )
    public String getModifyForm( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_FORM ) );

        if ( _form == null || ( _form.getId( ) != nId ) )
        {
            _form = FormHome.findByPrimaryKey( nId );
        }

        Map<String, Object> model = getModel( );
        model.put( MARK_FORM, _form );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_FORM, TEMPLATE_MODIFY_FORM, model );
    }

    /**
     * Process the change form of a form
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_FORM )
    public String doModifyForm( HttpServletRequest request )
    {
        populate( _form, request, request.getLocale( ) );

        // Check constraints
        if ( !validateBean( _form, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_FORM, PARAMETER_ID_FORM, _form.getId( ) );
        }

        FormHome.update( _form );
        addInfo( INFO_FORM_UPDATED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_FORMS );
    }
}
