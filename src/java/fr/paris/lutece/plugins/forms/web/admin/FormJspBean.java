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

package fr.paris.lutece.plugins.forms.web.admin;

import fr.paris.lutece.plugins.forms.service.FormService;
import fr.paris.lutece.plugins.forms.service.FormsResourceIdService;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.business.FormAction;
import fr.paris.lutece.plugins.forms.business.FormActionHome;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * This class provides the user interface to manage Form features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageForms.jsp", controllerPath = "jsp/admin/plugins/forms/", right = "FORMS_MANAGEMENT" )
public class FormJspBean extends AbstractJspBean
{
    // Rights
    public static final String RIGHT_FORMS_MANAGEMENT = "FORMS_MANAGEMENT";

    private static final long serialVersionUID = 7515975782241863390L;

    private static final String EMPTY_STRING = "";

    // Templates
    private static final String TEMPLATE_MANAGE_FORMS = "/admin/plugins/forms/manage_forms.html";
    private static final String TEMPLATE_CREATE_FORM = "/admin/plugins/forms/create_form.html";
    private static final String TEMPLATE_MODIFY_FORM = "/admin/plugins/forms/modify_form.html";

    private static final String PARAMETER_PAGE_INDEX = "page_index";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MODIFY_FORM = "forms.modify_form.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_FORM = "forms.create_form.pageTitle";

    // Markers
    private static final String MARK_FORM_LIST = "form_list";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_FORM = "form";
    private static final String MARK_PERMISSION_CREATE_FORMS = "permission_create_forms";
    private static final String MARK_WORKFLOW_REF_LIST = "workflow_list";

    // Properties
    private static final String PROPERTY_ITEM_PER_PAGE = "forms.itemsPerPage";
    private static final String PROPERTY_COPY_FORM_TITLE = "forms.copyForm.title";

    // Messages
    private static final String MESSAGE_CONFIRM_REMOVE_FORM = "forms.message.confirmRemoveForm";
    private static final String MESSAGE_CONFIRM_REMOVE_ACTIVE_FORM = "forms.message.confirmRemoveActiveForm";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "forms.model.entity.form.attribute.";

    // Views
    private static final String VIEW_MANAGE_FORMS = "manageForms";
    private static final String VIEW_CREATE_FORM = "createForm";
    private static final String VIEW_MODIFY_FORM = "modifyForm";
    private static final String VIEW_CONFIRM_REMOVE_FORM = "confirmRemoveForm";

    // Actions
    private static final String ACTION_CREATE_FORM = "createForm";
    private static final String ACTION_MODIFY_FORM = "modifyForm";
    private static final String ACTION_REMOVE_FORM = "removeForm";
    private static final String ACTION_DUPLICATE_FORM = "duplicateForm";

    // Infos
    private static final String INFO_FORM_CREATED = "forms.info.form.created";
    private static final String INFO_FORM_UPDATED = "forms.info.form.updated";
    private static final String INFO_FORM_REMOVED = "forms.info.form.removed";
    private static final String INFO_FORM_COPIED = "forms.info.form.copied";

    // Errors
    private static final String ERROR_FORM_NOT_UPDATED = "forms.error.form.notUpdated";
    private static final String ERROR_FORM_DATE_START_AFTER_END = "forms.error.form.date.startAfterEnd";

    // Session variable to store working values
    private Form _form;

    private final int _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_ITEM_PER_PAGE, 50 );
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;

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
        AdminUser adminUser = getUser( );
        Plugin plugin = getPlugin( );
        Locale locale = getLocale( );
        List<FormAction> listFormActions;
        List<FormAction> listAuthorizedFormActions;
        _form = new Form( );

        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage, _nDefaultItemsPerPage );

        List<Form> listForms = FormHome.getFormList( );
        listForms = (List<Form>) AdminWorkgroupService.getAuthorizedCollection( listForms, adminUser );

        Map<String, Object> model = getModel( );
        LocalizedPaginator<Form> paginator = new LocalizedPaginator<Form>( listForms, _nItemsPerPage, getJspManageForm( request ), PARAMETER_PAGE_INDEX,
                _strCurrentPageIndex, getLocale( ) );

        listFormActions = FormActionHome.selectAllFormActions( plugin, locale );

        for ( Form form : paginator.getPageItems( ) )
        {
            listAuthorizedFormActions = (List<FormAction>) RBACService.getAuthorizedActionsCollection( listFormActions, form, getUser( ) );
            form.setActions( listAuthorizedFormActions );

        }

        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, EMPTY_STRING + _nItemsPerPage );

        model.put( MARK_FORM_LIST, paginator.getPageItems( ) );
        model.put( MARK_LOCALE, request.getLocale( ) );

        if ( RBACService.isAuthorized( Form.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, FormsResourceIdService.PERMISSION_CREATE, adminUser ) )
        {
            model.put( MARK_PERMISSION_CREATE_FORMS, true );
        }
        else
        {
            model.put( MARK_PERMISSION_CREATE_FORMS, false );
        }

        setPageTitleProperty( EMPTY_STRING );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_FORMS, locale, model );

        return getAdminPage( templateList.getHtml( ) );
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

        if ( WorkflowService.getInstance( ).isAvailable( ) )
        {
            AdminUser adminUser = getUser( );
            ReferenceList referenceList = WorkflowService.getInstance( ).getWorkflowsEnabled( adminUser, getLocale( ) );
            model.put( MARK_WORKFLOW_REF_LIST, referenceList );
        }

        model.put( MARK_FORM, _form );
        model.put( MARK_LOCALE, request.getLocale( ).getLanguage( ) );

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

        if ( !validateForm( _form ) )
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
    @View( VIEW_CONFIRM_REMOVE_FORM )
    public String getConfirmRemoveForm( HttpServletRequest request )
    {
        int nId = -1;
        try
        {
            nId = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ) );
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( ne );

            return redirectView( request, VIEW_MANAGE_FORMS );
        }

        Form formToBeDeleted = FormHome.findByPrimaryKey( nId );
        String strConfirmRemoveMessage = formToBeDeleted.isActive( ) ? MESSAGE_CONFIRM_REMOVE_ACTIVE_FORM : MESSAGE_CONFIRM_REMOVE_FORM;

        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_FORM ) );
        url.addParameter( FormsConstants.PARAMETER_ID_FORM, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, strConfirmRemoveMessage, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );

    }

    /**
     * Manages the copy of a form whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_DUPLICATE_FORM )
    public String doDuplicateForm( HttpServletRequest request )
    {
        int nId = -1;
        String strIdForm;

        try
        {
            strIdForm = request.getParameter( FormsConstants.PARAMETER_ID_FORM );
            nId = Integer.parseInt( strIdForm );
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( ne );

            return redirectView( request, VIEW_MANAGE_FORMS );
        }

        if ( ( nId != -1 ) && RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm, FormsResourceIdService.PERMISSION_COPY, getUser( ) ) )
        {
            Form formToBeCopied = FormHome.findByPrimaryKey( nId );

            if ( formToBeCopied != null )
            {

                Object [ ] tabFormTitleCopy = {
                    formToBeCopied.getTitle( ),
                };
                String strTitleCopyForm = I18nService.getLocalizedString( PROPERTY_COPY_FORM_TITLE, tabFormTitleCopy, getLocale( ) );

                if ( strTitleCopyForm != null )
                {
                    formToBeCopied.setTitle( strTitleCopyForm );
                }

                FormHome.create( formToBeCopied );
                addInfo( INFO_FORM_COPIED, getLocale( ) );
            }
        }

        return redirectView( request, VIEW_MANAGE_FORMS );
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
        int nId = -1;
        try
        {
            nId = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ) );
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( ne );

            return redirectView( request, VIEW_MANAGE_FORMS );
        }

        FormService formService = SpringContextService.getBean( FormService.BEAN_NAME );
        formService.removeForm( nId );

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
        int nId = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ), FormsConstants.DEFAULT_ID_VALUE );

        if ( nId == FormsConstants.DEFAULT_ID_VALUE )
        {
            return redirectView( request, VIEW_MANAGE_FORMS );
        }

        Form formToBeModified = FormHome.findByPrimaryKey( nId );

        if ( formToBeModified != null )
        {
            AdminUser adminUser = getUser( );

            Map<String, Object> model = getModel( );
            model.put( MARK_FORM, formToBeModified );
            model.put( MARK_LOCALE, request.getLocale( ).getLanguage( ) );

            if ( WorkflowService.getInstance( ).isAvailable( ) )
            {
                ReferenceList referenceList = WorkflowService.getInstance( ).getWorkflowsEnabled( adminUser, getLocale( ) );
                model.put( MARK_WORKFLOW_REF_LIST, referenceList );
            }

            return getPage( PROPERTY_PAGE_TITLE_MODIFY_FORM, TEMPLATE_MODIFY_FORM, model );
        }

        return redirectView( request, VIEW_MANAGE_FORMS );

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
        int nId = -1;
        try
        {
            nId = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ) );
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( ne );
            addError( ERROR_FORM_NOT_UPDATED, getLocale( ) );
            return redirectView( request, VIEW_MANAGE_FORMS );
        }

        _form = FormHome.findByPrimaryKey( nId );

        if ( _form == null )
        {
            addError( ERROR_FORM_NOT_UPDATED, getLocale( ) );
            return redirectView( request, VIEW_MANAGE_FORMS );
        }
        populate( _form, request, request.getLocale( ) );

        if ( !validateForm( _form ) )
        {
            return redirect( request, VIEW_MODIFY_FORM, FormsConstants.PARAMETER_ID_FORM, _form.getId( ) );
        }

        FormHome.update( _form );
        addInfo( INFO_FORM_UPDATED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_FORMS );
    }

    /**
     * Validate the form field values
     * 
     * @param form
     *            the Form to validate
     * 
     * @return True if the form is valid
     */
    private boolean validateForm( Form form )
    {

        boolean bIsFormValid = validateBean( form, VALIDATION_ATTRIBUTES_PREFIX );

        if ( form.getAvailabilityStartDate( ) != null && form.getAvailabilityEndDate( ) != null
                && form.getAvailabilityStartDate( ).after( form.getAvailabilityEndDate( ) ) )
        {
            addError( ERROR_FORM_DATE_START_AFTER_END, getLocale( ) );
            bIsFormValid = false;

        }
        return bIsFormValid;

    }

}
