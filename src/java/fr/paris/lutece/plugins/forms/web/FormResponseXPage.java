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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseStep;
import fr.paris.lutece.plugins.forms.service.EntryServiceManager;
import fr.paris.lutece.plugins.forms.web.entrytype.DisplayType;
import fr.paris.lutece.plugins.forms.web.entrytype.EntryTypeFileDisplayService;
import fr.paris.lutece.plugins.forms.web.entrytype.IEntryDisplayService;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.business.file.File;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.service.upload.FormsAsynchronousUploadHandler;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.util.FormsResponseUtils;
import fr.paris.lutece.plugins.forms.util.FormsUtils;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.exception.ResourceWorkflowLockedException;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.util.url.UrlItem;
import fr.paris.lutece.portal.service.template.HtmlMarkup;

/**
 *
 * Controller for formResponse display
 *
 */
@RequestScoped
@Named( "forms.xpage.formsResponse" )
@Controller( xpageName = FormResponseXPage.XPAGE_NAME, pageTitleI18nKey = FormResponseXPage.MESSAGE_PAGE_TITLE, pagePathI18nKey = FormResponseXPage.MESSAGE_PATH )
public class FormResponseXPage extends MVCApplication
{
    public static final String XPAGE_NAME = "formsResponse";

    /**
     * Generated serial id
     */
    private static final long serialVersionUID = 8146530527615651620L;

    // Messages
    protected static final String MESSAGE_PAGE_TITLE = "forms.response.xpage.form.view.pageTitle";
    protected static final String MESSAGE_PATH = "forms.response.xpage.form.view.pagePathLabel";
    protected static final String MESSAGE_ERROR_NOT_PUBLISHED_FORM_RESPONSE = "forms.xpage.response.error.inactive";
    protected static final String MESSAGE_ERROR_NOT_FOUND_FORM_RESPONSE = "forms.xpage.response.error.notfound";
    protected static final String MESSAGE_FORM_RESPONSE_PAGETITLE = "forms.xpage.response.pagetitle";
    protected static final String MESSAGE_FORM_RESPONSE_PATHLABEL = "forms.xpage.response.pathlabel";
    private static final String MESSAGE_ACTION_ERROR = "forms.xpage.response.action.error";
    private static final String MESSAGE_ACTION_LOCKED = "forms.xpage.response.action.locked";
    private static final String MESSAGE_ERROR_TOKEN = "Invalid security token";
    private static final String MESSAGE_ACTION_SUCCESS = "forms.xpage.response.action.success";


    // Views
    public static final String VIEW_FORM_RESPONSE = "formResponseView";
    public static final String VIEW_FORM_FILE = "formFileView";

    // Actions
    private static final String ACTION_PROCESS_ACTION = "doProcessAction";
    private static final String ACTION_SAVE_TASK_FORM = "doSaveTaskForm";

    // Templates
    private static final String TEMPLATE_VIEW_FORM_RESPONSE = "/skin/plugins/forms/view_form_response.html";
    private static final String TEMPLATE_VIEW_FORM_FILE = "/skin/plugins/forms/view_form_file.html";
    private static final String TEMPLATE_TASK_FORM_RESPONSE = "/skin/plugins/forms/task_form_workflow.html";

    // Marks
    private static final String MARK_WORKFLOW_ACTION_LIST = "workflow_action_list";
    private static final String MARK_ID_FORM_RESPONSE = "id_form_response";
    private static final String MARK_ID_ACTION = "id_action";
    private static final String MARK_TASK_FORM = "tasks_form";
    private static final String MARK_LIST_FILE = "listFiles";
    private static final String MARK_LIST_STEP_DISPLAY = "list_step_display";
    private static final String MARK_ACTION_SUCCESS = "is_action_success";
    private static final String MARK_BACK_URL = "back_url";
    private static final String MARK_TASK_FORM_ERROR = "task_form_error";

    // Parameters
    private static final String PARAMETER_ID_ACTION = "id_action";

    @Inject
    private Models _models;
    @Inject
    private FormsAsynchronousUploadHandler _formsAsynchronousUploadHandler;
    @Inject
    private WorkflowService _workflowService;
    @Inject
    private SecurityTokenService _securityTokenService;
    @Inject
    private EntryServiceManager _entryServiceManager;

    @View( value = VIEW_FORM_RESPONSE, defaultView = true )
    public XPage getFormResponseView( HttpServletRequest request ) throws SiteMessageException
    {
        Locale locale = getLocale( request );
        FormResponse formResponse = findFormResponseFrom( request );

        Collection<Action> actionsList = getActionsForUser( request, formResponse );
        Models model = _models;
        model.put( FormsConstants.MARK_FORM_RESPONSE, formResponse );
        model.put( MARK_LIST_STEP_DISPLAY, buildStepsHtml( request, formResponse ) );
        boolean bActionSuccess = "true".equals( request.getParameter( FormsConstants.PARAMETER_ACTION_SUCCESS ) );
        model.put( MARK_ACTION_SUCCESS, bActionSuccess );
        model.put( MARK_BACK_URL, getBackUrl( request, formResponse, bActionSuccess ) );
        model.put( MARK_WORKFLOW_ACTION_LIST, actionsList );
        model.put( SecurityTokenService.MARK_TOKEN, _securityTokenService.getToken( request, ACTION_PROCESS_ACTION ) );

        XPage xPage = getXPage( TEMPLATE_VIEW_FORM_RESPONSE, getLocale( request ), FormsUtils.withHtmlMarkup( model.asMap( ), MARK_LIST_STEP_DISPLAY ) );
        xPage.setTitle( I18nService.getLocalizedString( MESSAGE_FORM_RESPONSE_PAGETITLE, locale ) );
        xPage.setPathLabel( I18nService.getLocalizedString( MESSAGE_FORM_RESPONSE_PATHLABEL, locale ) );

        return xPage;
    }

    @View( value = VIEW_FORM_FILE, defaultView = false )
    public XPage getFormFileView( HttpServletRequest request ) throws SiteMessageException
    {
        Locale locale = getLocale( request );
        FormResponse formResponse = findFormResponseFrom( request );
        List<Response> listResponse = new ArrayList<Response>();
        if("true".equals(request.getParameter(FormsConstants.PARAMETER_ACTION_SUCCESS)))
        {
            addInfo( MESSAGE_ACTION_SUCCESS,getLocale(request) );
        }
        Models model = _models;
        model.put( FormsConstants.MARK_FORM_RESPONSE, formResponse );
        model.put( SecurityTokenService.MARK_TOKEN, _securityTokenService.getToken( request, ACTION_PROCESS_ACTION ) );
        for (FormResponseStep step: formResponse.getSteps() ){
            listResponse.addAll(findResponses(step.getQuestions()));
        }
        model.put( FormsConstants.MARK_QUESTION_LIST_RESPONSES, listResponse );

        // The display services work on a map: they get a copy of the model, their entries are put back in it
        Map<String, Object> mapDisplayModel = new HashMap<>( model.asMap( ) );
        formResponse.getSteps().stream()
                .flatMap(step -> step.getQuestions().stream())
                .forEach(fqr -> {
                    IEntryDisplayService displayService = _entryServiceManager
                            .getEntryDisplayService(fqr.getQuestion().getEntry().getEntryType());
                    if (displayService instanceof EntryTypeFileDisplayService) {
                        displayService.getEntryTemplateDisplay(request, fqr.getQuestion().getEntry(), locale, mapDisplayModel, DisplayType.READONLY_FRONTOFFICE);
                    }
                });
        mapDisplayModel.forEach( model::put );

        List<File> listFiles = formResponse.getSteps().stream()
                .flatMap(step -> step.getQuestions().stream())
                .filter(fqr -> fqr.getQuestion().getEntry().isOnlyDisplayInBack())
                .flatMap(fqr -> fqr.getEntryResponse().stream())
                .map(Response::getFile)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        model.put( MARK_LIST_FILE, listFiles );
        XPage xPage = getXPage( TEMPLATE_VIEW_FORM_FILE, getLocale( request ) );
        xPage.setTitle( I18nService.getLocalizedString( MESSAGE_FORM_RESPONSE_PAGETITLE, locale ) );
        xPage.setPathLabel( I18nService.getLocalizedString( MESSAGE_FORM_RESPONSE_PATHLABEL, locale ) );

        return xPage;
    }

    @fr.paris.lutece.portal.util.mvc.commons.annotations.Action( value = ACTION_PROCESS_ACTION )
    public XPage doProcessAction( HttpServletRequest request ) throws AccessDeniedException
    {
    	// CSRF Token control
        if ( !_securityTokenService.validate( request, ACTION_PROCESS_ACTION ) )
        {
            throw new AccessDeniedException( MESSAGE_ERROR_TOKEN );
        }
    	// Get parameters from request
        int nIdFormResponse = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_RESPONSE ), NumberUtils.INTEGER_MINUS_ONE );
        int nIdAction = NumberUtils.toInt( request.getParameter( PARAMETER_ID_ACTION ), NumberUtils.INTEGER_MINUS_ONE );

        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );
        FormResponse formResponse = FormResponseHome.findByPrimaryKey( nIdFormResponse );

        if (  formResponse == null || !FormsResponseUtils.isAuthorized(formResponse, user) )
        {
            return redirect( request, VIEW_FORM_RESPONSE, FormsConstants.PARAMETER_ID_RESPONSE, nIdFormResponse );
        }

        Locale locale = getLocale( request );

        if ( !_workflowService.canProcessAction( nIdFormResponse, FormResponse.RESOURCE_TYPE, nIdAction, formResponse.getFormId( ), request, false, user ) )
        {
            // The action is not available (any more) in the current state of the response, or not to this user
            addError( MESSAGE_ACTION_ERROR, locale );
            return redirect( request, VIEW_FORM_RESPONSE, FormsConstants.PARAMETER_ID_RESPONSE, nIdFormResponse );
        }

        if ( _workflowService.isDisplayTasksForm( nIdAction, locale ) )
        {
        	_formsAsynchronousUploadHandler.removeSessionFiles( request.getSession( ) );

            return getTaskFormXPage( request, nIdFormResponse, nIdAction, locale, null );
        }

        try
        {
        	_workflowService.doProcessAction( nIdFormResponse, FormResponse.RESOURCE_TYPE, nIdAction, formResponse.getFormId( ), request, locale, false, user );
        }
        catch( AppException e )
        {
            if ( e.getCause( ) instanceof ResourceWorkflowLockedException )
            {
                // Another action is in progress on this response (double click, other instance) or its state has changed
                addError( MESSAGE_ACTION_LOCKED, locale );
            }
            else
            {
                AppLogService.error( "Error processing action for id response '{}' - cause : {}", nIdFormResponse, e.getMessage( ), e );
                addError( MESSAGE_ACTION_ERROR, locale );
            }
        }
        // Redirect to the correct view
        return redirect( request, VIEW_FORM_RESPONSE, FormsConstants.PARAMETER_ID_RESPONSE, nIdFormResponse );
    }

    /**
     * Process workflow action
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @fr.paris.lutece.portal.util.mvc.commons.annotations.Action( value = ACTION_SAVE_TASK_FORM, securityTokenDisabled = true )
    public XPage doSaveTaskForm( HttpServletRequest request ) throws AccessDeniedException
    {
        int nIdFormResponse = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_RESPONSE ), NumberUtils.INTEGER_MINUS_ONE );
        int nIdAction = NumberUtils.toInt( request.getParameter( PARAMETER_ID_ACTION ), NumberUtils.INTEGER_MINUS_ONE );

        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );
        FormResponse formResponse = FormResponseHome.findByPrimaryKey( nIdFormResponse );

        if ( formResponse == null || !FormsResponseUtils.isAuthorized(formResponse, user) )
        {
            return redirect( request, VIEW_FORM_RESPONSE, FormsConstants.PARAMETER_ID_RESPONSE, nIdFormResponse );
        }

        // CSRF Token control
        if ( !_securityTokenService.validate( request, ACTION_SAVE_TASK_FORM ) )
        {
            throw new AccessDeniedException( MESSAGE_ERROR_TOKEN );
        }

        int nIdForm = formResponse.getFormId( );
        Locale locale = getLocale( request );

        if ( _workflowService.canProcessAction( nIdFormResponse, FormResponse.RESOURCE_TYPE, nIdAction, nIdForm, request, false, user ) )
        {
            try
            {
                String strError = _workflowService.doSaveTasksForm( nIdFormResponse, FormResponse.RESOURCE_TYPE, nIdAction, nIdForm, request, locale, user );
                if ( strError != null )
                {
                    // strError is the URL of the back-office message page: the task form is displayed again in the front office, with the message
                    return getTaskFormXPage( request, nIdFormResponse, nIdAction, locale, getTaskFormError( request, strError, locale ) );
                }
            }
            catch( AppException e )
            {
                if ( e.getCause( ) instanceof ResourceWorkflowLockedException )
                {
                    addError( MESSAGE_ACTION_LOCKED, locale );
                }
                else
                {
                    AppLogService.error( "Error processing action for record {}", nIdFormResponse, e );
                    addError( MESSAGE_ACTION_ERROR, locale );
                }
            }
        }
        else
        {
            addError( MESSAGE_ACTION_ERROR, locale );
        }
        return redirect( request, VIEW_FORM_RESPONSE, FormsConstants.PARAMETER_ID_RESPONSE, nIdFormResponse );
    }

    /**
     * Builds the page of the task form of a workflow action
     *
     * @param request
     *            The Http request
     * @param nIdFormResponse
     *            The id of the form response
     * @param nIdAction
     *            The id of the action
     * @param locale
     *            The locale
     * @param taskFormError
     *            The error of the previous validation of the task form, or {@code null}
     * @return the page
     */
    private XPage getTaskFormXPage( HttpServletRequest request, int nIdFormResponse, int nIdAction, Locale locale, Object taskFormError )
    {
        String strHtmlTasksForm = _workflowService.getDisplayTasksForm( nIdFormResponse, FormResponse.RESOURCE_TYPE, nIdAction, request, locale, null );

        Map<String, Object> model = new LinkedHashMap<>( );
        model.put( MARK_ID_FORM_RESPONSE, String.valueOf( nIdFormResponse ) );
        model.put( MARK_ID_ACTION, String.valueOf( nIdAction ) );
        model.put( MARK_TASK_FORM, HtmlMarkup.of( strHtmlTasksForm ) );
        model.put( SecurityTokenService.MARK_TOKEN, _securityTokenService.getToken( request, ACTION_SAVE_TASK_FORM ) );
        if ( taskFormError != null )
        {
            model.put( MARK_TASK_FORM_ERROR, taskFormError );
        }

        XPage xPage = getXPage( TEMPLATE_TASK_FORM_RESPONSE, locale, model );
        xPage.setTitle( I18nService.getLocalizedString( MESSAGE_FORM_RESPONSE_PAGETITLE, locale ) );
        xPage.setPathLabel( I18nService.getLocalizedString( MESSAGE_FORM_RESPONSE_PATHLABEL, locale ) );

        return xPage;
    }

    /**
     * Returns the error of the validation of a task form. The task components report it the back-office way: they store an AdminMessage in the session
     * and answer the URL of the back-office message page, which a front-office user must not be sent to.
     *
     * @param request
     *            The Http request
     * @param strError
     *            The error answered by the validation of the task form
     * @param locale
     *            The locale
     * @return the message of the task as markup (its arguments are escaped, its text sanitized), or the generic error message of the action
     */
    static Object getTaskFormError( HttpServletRequest request, String strError, Locale locale )
    {
        AdminMessage message = strError.contains( AdminMessageService.getMessageRelativeUrl( ) ) ? AdminMessageService.getMessage( request ) : null;

        if ( message != null )
        {
            return message.getMarkup( locale );
        }

        return I18nService.getLocalizedString( MESSAGE_ACTION_ERROR, locale );
    }

    private Collection<Action> getActionsForUser( HttpServletRequest request, FormResponse formResponse )
    {
        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );
        if (formResponse != null)
        {
            Form form = FormHome.findByPrimaryKey( formResponse.getFormId( ) );
            if (FormsResponseUtils.isAuthorized(formResponse, SecurityService.getInstance( ).getRegisteredUser( request ), form ))
            {
                boolean workflowEnabled = _workflowService.isAvailable( ) && ( form.getIdWorkflow( ) != FormsConstants.DEFAULT_ID_VALUE );

                if ( workflowEnabled )
                {
                    return _workflowService.getActions( formResponse.getId( ), FormResponse.RESOURCE_TYPE, form.getIdWorkflow( ), (User) user );
                }
            }
        }
        return new ArrayList<>( );
    }

    /**
     * Finds the formResponse from the specified request
     *
     * @param request
     *            the request
     * @return the found formResponse, or {@code null} if not found
     * @throws FormResponseNotFoundException
     *             if the form is not found
     * @throws SiteMessageException
     *             if the formResponse is not accessible
     */
    private FormResponse findFormResponseFrom( HttpServletRequest request ) throws SiteMessageException
    {
        FormResponse formResponse = null;
        int nIdFormResponse = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_RESPONSE ), FormsConstants.DEFAULT_ID_VALUE );

        if ( nIdFormResponse != FormsConstants.DEFAULT_ID_VALUE )
        {
            formResponse = FormResponseHome.findByPrimaryKey( nIdFormResponse );
        }
        else
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR_NOT_FOUND_FORM_RESPONSE, SiteMessage.TYPE_ERROR );
        }

        if ( formResponse == null )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR_NOT_FOUND_FORM_RESPONSE, SiteMessage.TYPE_ERROR );
        }
        else if ( !formResponse.isPublished( ) && !FormsResponseUtils.isAuthorized(formResponse, SecurityService.getInstance( ).getRegisteredUser( request ) ) )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR_NOT_PUBLISHED_FORM_RESPONSE, SiteMessage.TYPE_ERROR );
        }

        return formResponse;
    }

    /**
     * Returns the URL of the back button
     *
     * @param request
     *            The Http request
     * @param formResponse
     *            The form response
     * @param bActionSuccess
     *            {@code true} if the form response has just been created, {@code false} otherwise
     * @return the back URL, or null if there is none
     */
    private String getBackUrl( HttpServletRequest request, FormResponse formResponse, boolean bActionSuccess )
    {
        UrlItem url = new UrlItem( AppPathService.getPortalUrl( ) );

        if ( bActionSuccess )
        {
            url.addParameter( MVCUtils.PARAMETER_PAGE, FormXPage.XPAGE_NAME );
            url.addParameter( MVCUtils.PARAMETER_VIEW, FormXPage.VIEW_STEP );
            url.addParameter( FormsConstants.PARAMETER_ID_FORM, formResponse.getFormId( ) );
        }
        else if ( SecurityService.getInstance( ).getRegisteredUser( request ) != null )
        {
            url.addParameter( MVCUtils.PARAMETER_PAGE, MyFormResponsesXPage.XPAGE_NAME );
        }
        else
        {
            return null;
        }

        return url.getUrl( );
    }

    /**
     * Builds the read-only HTML of each step of the form response
     *
     * @param request
     *            The Http request
     * @param formResponse
     *            The form response
     * @return the list of HTML
     */
    private List<String> buildStepsHtml( HttpServletRequest request, FormResponse formResponse )
    {
        for ( FormResponseStep formResponseStep : formResponse.getSteps( ) )
        {
            for ( FormQuestionResponse formQuestionResponse : formResponseStep.getQuestions( ) )
            {
                if ( CollectionUtils.isNotEmpty( formQuestionResponse.getEntryResponse( ) ) )
                {
                    formQuestionResponse.getQuestion( ).setIsVisible( true );
                }
            }
        }

        List<String> listStepHtml = new ArrayList<>( );
        Locale locale = FormsResponseUtils.getLocaleFO( request );

        for ( FormResponseStep formResponseStep : formResponse.getSteps( ) )
        {
            StepDisplayTree stepDisplayTree = new StepDisplayTree( formResponseStep.getStep( ).getId( ), formResponse );
            listStepHtml.add( stepDisplayTree.getCompositeHtml( request, formResponseStep.getQuestions( ), locale, DisplayType.READONLY_FRONTOFFICE ) );
        }

        return listStepHtml;
    }

    private List<Response> findResponses(List<FormQuestionResponse> listFormQuestionResponse )
    {
        List<Response> listResponse = new ArrayList<>( );

        if ( listFormQuestionResponse != null )
        {
            for ( FormQuestionResponse formQuestionResponse : listFormQuestionResponse )
            {
                listResponse.addAll(formQuestionResponse.getEntryResponse( ));
            }
        }

        return listResponse;
    }
}
