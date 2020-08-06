/*
 * Copyright (c) 2002-2020, City of Paris
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

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormResponseStepHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.business.Transition;
import fr.paris.lutece.plugins.forms.business.TransitionHome;
import fr.paris.lutece.plugins.forms.service.FormsResourceIdService;
import fr.paris.lutece.plugins.forms.service.StepService;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.html.AbstractPaginator;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class provides the user interface to manage Form features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageSteps.jsp", controllerPath = "jsp/admin/plugins/forms/", right = "FORMS_MANAGEMENT" )
public class FormStepJspBean extends AbstractJspBean
{

    private static final long serialVersionUID = 7515975782241863390L;

    private static final String EMPTY_STRING = "";

    // Templates
    private static final String TEMPLATE_MANAGE_STEPS = "/admin/plugins/forms/manage_steps.html";
    private static final String TEMPLATE_CREATE_STEP = "/admin/plugins/forms/create_step.html";
    private static final String TEMPLATE_MODIFY_STEP = "/admin/plugins/forms/modify_step.html";

    // Parameters
    private static final String PARAMETER_PAGE_INDEX = "page_index";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MODIFY_STEP = "forms.modify_step.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_STEP = "forms.create_step.pageTitle";

    // Markers
    private static final String MARK_STEP_LIST = "step_list";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_TRANSITIONS = "transition_list";

    // Properties
    private static final String PROPERTY_ITEM_PER_PAGE = "forms.itemsPerPage";
    private static final String PROPERTY_COPY_FORM_TITLE = "forms.copyStep.title";

    // Messages
    private static final String MESSAGE_CONFIRM_REMOVE_STEP = "forms.message.confirmRemoveStep";
    private static final String MESSAGE_CONFIRM_REMOVE_STEP_ACTIVE_FORM = "forms.message.confirmRemoveStepActiveForm";

    // Validations
    private static final String STEP_VALIDATION_ATTRIBUTES_PREFIX = "forms.model.entity.step.attribute.";

    // Views
    private static final String VIEW_MANAGE_STEPS = "manageSteps";
    private static final String VIEW_CREATE_STEP = "createStep";
    private static final String VIEW_MODIFY_STEP = "modifyStep";
    private static final String VIEW_CONFIRM_REMOVE_STEP = "confirmRemoveStep";

    // Actions
    private static final String ACTION_CREATE_STEP = "createStep";
    private static final String ACTION_MODIFY_STEP = "modifyStep";
    private static final String ACTION_REMOVE_STEP = "removeStep";
    private static final String ACTION_DUPLICATE_STEP = "duplicateStep";

    // Infos
    private static final String INFO_STEP_CREATED = "forms.info.step.created";
    private static final String INFO_STEP_UPDATED = "forms.info.step.updated";
    private static final String INFO_STEP_REMOVED = "forms.info.step.removed";
    private static final String INFO_STEP_COPIED = "forms.info.step.copied";

    // Errors
    private static final String ERROR_STEP_NOT_UPDATED = "forms.error.form.notUpdated";

    // Others
    private static final StepService _stepService = SpringContextService.getBean( StepService.BEAN_NAME );

    // Session variable to store working values
    private Form _form;
    private Step _step;

    private final int _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_ITEM_PER_PAGE, 50 );
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     * @throws fr.paris.lutece.portal.service.admin.AccessDeniedException
     *             Access denied is user isnt authorized by RBAC
     */
    @View( value = VIEW_MANAGE_STEPS, defaultView = true )
    public String getManageSteps( HttpServletRequest request ) throws AccessDeniedException
    {
        int nIdForm = -1;
        try
        {
            nIdForm = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ) );
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( ne );
            return redirectToViewManageForm( request );

        }

        checkUserPermission( Form.RESOURCE_TYPE, String.valueOf( nIdForm ), FormsResourceIdService.PERMISSION_MODIFY, request );

        Form formParent = FormHome.findByPrimaryKey( nIdForm );
        _step = new Step( );

        Map<String, Object> model = getModel( );
        model.put( FormsConstants.MARK_FORM, formParent );

        _strCurrentPageIndex = AbstractPaginator.getPageIndex( request, AbstractPaginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nItemsPerPage = AbstractPaginator.getItemsPerPage( request, AbstractPaginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage, _nDefaultItemsPerPage );

        List<Step> listSteps = StepHome.getStepsListByForm( nIdForm );
        List<Transition> listTransitions = TransitionHome.getTransitionsListFromForm( nIdForm );

        listSteps = StepService.sortStepsWithTransitions( listSteps, listTransitions );

        LocalizedPaginator<Step> paginator = new LocalizedPaginator<>( listSteps, _nItemsPerPage, getJspManageSteps( request ), PARAMETER_PAGE_INDEX,
                _strCurrentPageIndex, getLocale( ) );

        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_TRANSITIONS, listTransitions );
        model.put( MARK_NB_ITEMS_PER_PAGE, EMPTY_STRING + _nItemsPerPage );
        model.put( FormsConstants.MARK_FORM, formParent );
        model.put( MARK_STEP_LIST, paginator.getPageItems( ) );
        model.put( MARK_LOCALE, request.getLocale( ) );

        setPageTitleProperty( EMPTY_STRING );

        Locale locale = getLocale( );
        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_STEPS, locale, model );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Returns the form to create a step
     *
     * @param request
     *            The Http request
     * @return the html code of the step form
     */
    @View( VIEW_CREATE_STEP )
    public String getCreateStep( HttpServletRequest request )
    {
        int nIdForm = -1;
        try
        {
            nIdForm = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ) );
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( ne );

            return redirectToViewManageForm( request );
        }

        _step = ( _step != null ) ? _step : new Step( );
        _step.setIdForm( nIdForm );
        if ( StepHome.getInitialStep( nIdForm ) == null )
        {
            _step.setInitial( true );
        }

        Map<String, Object> model = getModel( );
        model.put( FormsConstants.MARK_STEP, _step );
        model.put( MARK_LOCALE, request.getLocale( ).getLanguage( ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_STEP, TEMPLATE_CREATE_STEP, model );
    }

    /**
     * Process the data capture form of a new step
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_STEP )
    public String doCreateStep( HttpServletRequest request )
    {
        populate( _step, request, request.getLocale( ) );

        if ( !validateStep( _step ) )
        {
            return redirectView( request, VIEW_CREATE_STEP );
        }

        StepHome.create( _step );
        addInfo( INFO_STEP_CREATED, getLocale( ) );

        return redirect( request, VIEW_MANAGE_STEPS, FormsConstants.MARK_ID_FORM, _step.getIdForm( ) );
    }

    /**
     * Manages the removal form of a step whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @View( VIEW_CONFIRM_REMOVE_STEP )
    public String getConfirmRemoveStep( HttpServletRequest request )
    {
        int nId = -1;

        try
        {
            nId = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( ne );

            return redirectToViewManageForm( request );
        }

        _step = StepHome.findByPrimaryKey( nId );

        _form = FormHome.findByPrimaryKey( _step.getIdForm( ) );

        String strConfirmRemoveMessage = MESSAGE_CONFIRM_REMOVE_STEP;

        if ( _form != null && _form.isActive( ) )
        {
            strConfirmRemoveMessage = MESSAGE_CONFIRM_REMOVE_STEP_ACTIVE_FORM;
        }

        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_STEP ) );
        url.addParameter( FormsConstants.PARAMETER_ID_STEP, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, strConfirmRemoveMessage, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );

    }

    /**
     * Manages the copy of a step whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_DUPLICATE_STEP )
    public String doDuplicateStep( HttpServletRequest request )
    {
        int nIdStep = -1;

        try
        {
            nIdStep = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( ne );

            return redirectToViewManageForm( request );
        }

        int nIdForm = -1;

        if ( ( nIdStep != -1 ) )
        {
            _step = StepHome.findByPrimaryKey( nIdStep );

            if ( _step != null )
            {

                nIdForm = _step.getIdForm( );
                Object [ ] tabSTepTitleCopy = {
                        _step.getTitle( ),
                };
                String strTitleCopyStep = I18nService.getLocalizedString( PROPERTY_COPY_FORM_TITLE, tabSTepTitleCopy, getLocale( ) );

                if ( strTitleCopyStep != null )
                {
                    _step.setTitle( strTitleCopyStep );
                }

                StepHome.create( _step );
                addInfo( INFO_STEP_COPIED, getLocale( ) );
            }
        }

        return redirect( request, VIEW_MANAGE_STEPS, FormsConstants.PARAMETER_ID_FORM, nIdForm );
    }

    /**
     * Handles the removal of a step
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage forms
     */
    @Action( ACTION_REMOVE_STEP )
    public String doRemoveStep( HttpServletRequest request )
    {
        int nIdStep = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), FormsConstants.DEFAULT_ID_VALUE );

        if ( nIdStep == -1 )
        {
            return redirectToViewManageForm( request );
        }

        if ( _step == null || _step.getId( ) != nIdStep )
        {
            _step = StepHome.findByPrimaryKey( nIdStep );
        }

        int nIdForm = -1;

        if ( _step != null )
        {
            nIdForm = _step.getIdForm( );
        }
        _stepService.removeStep( nIdStep );
        FormResponseStepHome.removeByStep( nIdStep );

        addInfo( INFO_STEP_REMOVED, getLocale( ) );

        return redirect( request, VIEW_MANAGE_STEPS, FormsConstants.PARAMETER_ID_FORM, nIdForm );
    }

    /**
     * Returns the form to update a step
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_STEP )
    public String getModifyStep( HttpServletRequest request )
    {
        int nIdStep = -1;
        try
        {
            nIdStep = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( ne );

            return redirectView( request, VIEW_MANAGE_STEPS );
        }

        _step = StepHome.findByPrimaryKey( nIdStep );

        if ( _step != null )
        {
            int nIdForm = _step.getIdForm( );

            if ( ( _form == null ) || ( _form.getId( ) != nIdForm ) )
            {
                _form = FormHome.findByPrimaryKey( nIdForm );
            }
            Map<String, Object> model = getModel( );
            model.put( FormsConstants.MARK_STEP, _step );
            model.put( FormsConstants.MARK_FORM, _form );
            model.put( MARK_LOCALE, request.getLocale( ) );

            return getPage( PROPERTY_PAGE_TITLE_MODIFY_STEP, TEMPLATE_MODIFY_STEP, model );
        }

        return redirectView( request, VIEW_MANAGE_STEPS );

    }

    /**
     * Process the change of a step
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_STEP )
    public String doModifyStep( HttpServletRequest request )
    {
        int nIdStep = -1;
        int nIdForm = -1;

        try
        {
            nIdForm = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ) );
            nIdStep = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( ne );
            addError( ERROR_STEP_NOT_UPDATED, getLocale( ) );
            return redirect( request, VIEW_MANAGE_STEPS, FormsConstants.PARAMETER_ID_FORM, nIdForm );
        }

        _step = StepHome.findByPrimaryKey( nIdStep );

        if ( _step == null )
        {
            addError( ERROR_STEP_NOT_UPDATED, getLocale( ) );
            return redirect( request, VIEW_MANAGE_STEPS, FormsConstants.PARAMETER_ID_FORM, nIdForm );
        }
        populate( _step, request, request.getLocale( ) );

        if ( !validateStep( _step ) )
        {
            return redirect( request, VIEW_MODIFY_STEP, FormsConstants.PARAMETER_ID_STEP, _step.getId( ) );
        }

        StepHome.update( _step );
        addInfo( INFO_STEP_UPDATED, getLocale( ) );

        return redirect( request, VIEW_MANAGE_STEPS, FormsConstants.PARAMETER_ID_FORM, nIdForm );
    }

    /**
     * Validate the Step field values
     * 
     * @param step
     *            the Step to validate
     * 
     * @return True if the step is valid
     */
    private boolean validateStep( Step step )
    {
        return validateBean( step, STEP_VALIDATION_ATTRIBUTES_PREFIX );

    }

}
