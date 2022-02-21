/*
 * Copyright (c) 2002-2022, City of Paris
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponseStep;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.business.TransitionHome;
import fr.paris.lutece.plugins.forms.service.FormsMultiviewAuthorizationService;
import fr.paris.lutece.plugins.forms.service.FormsResourceIdService;
import fr.paris.lutece.plugins.forms.service.IFormsMultiviewAuthorizationService;
import fr.paris.lutece.plugins.forms.service.upload.FormsAsynchronousUploadHandler;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.web.StepDisplayTree;
import fr.paris.lutece.plugins.forms.web.entrytype.DisplayType;
import fr.paris.lutece.plugins.forms.web.form.response.view.FormResponseViewModelProcessorFactory;
import fr.paris.lutece.plugins.forms.web.form.response.view.IFormResponseViewModelProcessor;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.url.UrlItem;

/**
 * Jsp Bean associated to the page which display the details of a form response
 */
@Controller( controllerJsp = "ManageDirectoryFormResponseDetails.jsp", controllerPath = "jsp/admin/plugins/forms/", right = "FORMS_MULTIVIEW" )
public class MultiviewFormResponseDetailsJspBean extends AbstractJspBean
{
    // Rights
    public static final String RIGHT_FORMS_MULTIVIEW = "FORMS_MULTIVIEW";

    // JSP path
    public static final String CONTROLLER_JSP_NAME_WITH_PATH = "jsp/admin/plugins/forms/ManageDirectoryFormResponseDetails.jsp";

    // Generated serial UID
    private static final long serialVersionUID = 3673744119212180461L;

    // Templates
    private static final String TEMPLATE_VIEW_FORM_RESPONSE = "admin/plugins/forms/multiview/view_form_response.html";
    private static final String TEMPLATE_FORM_RESPONSE_HISTORY = "admin/plugins/forms/multiview/form_response_history.html";
    private static final String TEMPLATE_TASK_FORM = "admin/plugins/forms/multiview/task_form_workflow.html";

    // Views
    private static final String VIEW_FORM_RESPONSE_DETAILS = "view_form_response_details";
    private static final String VIEW_TASKS_FORM = "view_tasksForm";

    // Actions
    private static final String ACTION_PROCESS_ACTION = "doProcessAction";
    private static final String ACTION_SAVE_TASK_FORM = "doSaveTaskForm";
    private static final String ACTION_CANCEL_TASK_FORM = "doCancelTaskForm";

    // Parameters
    private static final String PARAMETER_ID_FORM_RESPONSE = "id_form_response";
    private static final String PARAMETER_BACK_FROM_ACTION = "back_form_action";
    private static final String PARAMETER_ID_ACTION = "id_action";

    // Marks
    private static final String MARK_LIST_FILTER_VALUES = "list_filter_values";
    private static final String MARK_FORM = "form";
    private static final String MARK_FORM_RESPONSE = "form_response";
    private static final String MARK_ID_FORM_RESPONSE = "id_form_response";
    private static final String MARK_ID_ACTION = "id_action";
    private static final String MARK_TASK_FORM = "tasks_form";
    private static final String MARK_LIST_MULTIVIEW_STEP_DISPLAY = "list_multiview_step_display";
    private static final String MARK_MAP_MULTIVIEW_STEP_REF_LIST = "map_multiview_step_ref_list";
    private static final String MARK_RESOURCE_ACTIONS = "resource_actions";
    private static final String MARK_RESOURCE_HISTORY = "resource_history";
    private static final String MARK_HISTORY_WORKFLOW_ENABLED = "history_workflow";
    private static final String MARK_WORKFLOW_STATE = "workflow_state";
    private static final String MARK_WORKFLOW_ACTION_LIST = "workflow_action_list";

    // Messages
    private static final String MESSAGE_ACCESS_DENIED = "Acces denied";
    private static final String MESSAGE_MULTIVIEW_FORM_RESPONSE_TITLE = "forms.multiviewForms.pageTitle";
    private static final String MESSAGE_ACTION_ERROR = "forms.multiviewForms.action.error";

    // Variables
    private Map<String, String> _mapFilterValues = new LinkedHashMap<>( );
    private final transient IFormsMultiviewAuthorizationService _formsMultiviewAuthorizationService = SpringContextService
            .getBean( FormsMultiviewAuthorizationService.BEAN_NAME );

    /**
     * Return the page with the details of a form response
     * 
     * @param request
     *            The request used the retrieve the values of the selected parameters
     * @return the page with the details of the form response
     * @throws AccessDeniedException
     *             if the user is not authorize to access the details of the form response
     */
    @View( value = VIEW_FORM_RESPONSE_DETAILS, defaultView = true )
    public String getResponseDetails( HttpServletRequest request ) throws AccessDeniedException
    {
    	String strIdFormResponse = request.getParameter( PARAMETER_ID_FORM_RESPONSE );
        int nIdFormResponse = NumberUtils.toInt( strIdFormResponse, NumberUtils.INTEGER_MINUS_ONE );
        FormResponse formResponse = FormResponseHome.findByPrimaryKey( nIdFormResponse );
        if ( formResponse == null )
        {
            throw new AccessDeniedException( MESSAGE_ACCESS_DENIED );
        }

        boolean bRBACAuthorization = RBACService.isAuthorized( Form.RESOURCE_TYPE, Integer.toString( formResponse.getFormId( ) ),
                FormsResourceIdService.PERMISSION_VIEW_FORM_RESPONSE, (User) getUser( ) );
        boolean bAuthorizedRecord = _formsMultiviewAuthorizationService.isUserAuthorizedOnFormResponse( request, nIdFormResponse );

        if ( !bRBACAuthorization || !bAuthorizedRecord )
        {
            throw new AccessDeniedException( MESSAGE_ACCESS_DENIED );
        }

        // Build the base model for the page of the details of a FormResponse
        Map<String, Object> model = buildFormResponseDetailsModel( request, formResponse );

        // Build the model of all ModelProcessors
        FormResponseViewModelProcessorFactory formResponseViewModelProcessorFactory = new FormResponseViewModelProcessorFactory( );
        List<IFormResponseViewModelProcessor> listFormResponseViewModelProcesor = formResponseViewModelProcessorFactory
                .buildFormResponseViewModelProcessorList( );
        if ( !CollectionUtils.isEmpty( listFormResponseViewModelProcesor ) )
        {
            Locale locale = getLocale( );
            for ( IFormResponseViewModelProcessor formResponseViewModelProcessor : listFormResponseViewModelProcesor )
            {
                formResponseViewModelProcessor.populateModel( request, model, nIdFormResponse, locale );
            }
        }

        // Fill the map which store the values of all filters and search previously selected if we are not coming from an action
        if ( request.getParameter( PARAMETER_BACK_FROM_ACTION ) == null )
        {
            _mapFilterValues = fillFilterMapValues( request );
        }
        populateModelWithFilterValues( _mapFilterValues, model );
        
        return getPage( MESSAGE_MULTIVIEW_FORM_RESPONSE_TITLE, TEMPLATE_VIEW_FORM_RESPONSE, model );
    }

    /**
     * Build the model of the page which display the details of a FormResponse
     * 
     * @param request
     *            the HttpServletRequest
     * @param formResponse
     *            The FormResponse on which the model must be built
     * @return the model associate for the details of the given FormResponse
     */
    private Map<String, Object> buildFormResponseDetailsModel( HttpServletRequest request, FormResponse formResponse )
    {
        Form form = FormHome.findByPrimaryKey( formResponse.getFormId( ) );

        Map<String, Object> mapFormResponseDetailsModel = getModel( );
        mapFormResponseDetailsModel.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_PROCESS_ACTION ) );
        mapFormResponseDetailsModel.put( MARK_FORM_RESPONSE, formResponse );
        mapFormResponseDetailsModel.put( MARK_FORM, form );

        Map<Integer, Step> mapSteps = new HashMap<>( );
        List<Integer> listStepsOfForm = StepHome.getIdStepsListByForm( form.getId( ) );
        List<FormResponseStep> listFormResponseStep = formResponse.getSteps( );
        List<Integer> listStepsOrdered = new ArrayList<>( );

        for ( FormResponseStep formResponseStep : listFormResponseStep )
        {
            listStepsOrdered.add( formResponseStep.getStep( ).getId( ) );
        }

        // Add the steps that are editable but not in the actuel form response flow
        for ( Integer idStepForm : listStepsOfForm )
        {
            if ( !listStepsOrdered.contains( idStepForm ) && TransitionHome.getTransitionsListFromStep( idStepForm ).isEmpty( ) )
            {
                listStepsOrdered.add( idStepForm );
            }
        }

        List<Step> listSteps = listStepsOrdered.stream( ).map( StepHome::findByPrimaryKey ).collect( Collectors.toList( ) );
        List<String> listStepDisplayTree = buildFormStepDisplayTreeList( request, listSteps, formResponse );
        listSteps.stream( ).forEach( step -> mapSteps.put( step.getId( ), step ) );

        mapFormResponseDetailsModel.put( MARK_LIST_MULTIVIEW_STEP_DISPLAY, listStepDisplayTree );
        mapFormResponseDetailsModel.put( MARK_MAP_MULTIVIEW_STEP_REF_LIST, mapSteps );

        int nIdWorkflow = form.getIdWorkflow( );
        WorkflowService workflowService = WorkflowService.getInstance( );
        boolean bHistoryEnabled = workflowService.isAvailable( ) && ( nIdWorkflow != FormsConstants.DEFAULT_ID_VALUE );

        if ( bHistoryEnabled )
        {
            Map<String, Object> resourceActions = new HashMap<>( );

            Collection<fr.paris.lutece.plugins.workflowcore.business.action.Action> lListActions = workflowService.getActions( formResponse.getId( ),
                    FormResponse.RESOURCE_TYPE, form.getIdWorkflow( ), (User) getUser( ) );
            State state = workflowService.getState( formResponse.getId( ), FormResponse.RESOURCE_TYPE, form.getIdWorkflow( ), formResponse.getFormId( ) );
            resourceActions.put( MARK_WORKFLOW_STATE, state );
            resourceActions.put( MARK_WORKFLOW_ACTION_LIST, lListActions );

            mapFormResponseDetailsModel.put( MARK_RESOURCE_ACTIONS, resourceActions );
            mapFormResponseDetailsModel.put( MARK_HISTORY_WORKFLOW_ENABLED, bHistoryEnabled );
            mapFormResponseDetailsModel.put( MARK_RESOURCE_HISTORY,
                    workflowService.getDisplayDocumentHistory( formResponse.getId( ), FormResponse.RESOURCE_TYPE, form.getIdWorkflow( ), request, getLocale( ),
                            mapFormResponseDetailsModel, TEMPLATE_FORM_RESPONSE_HISTORY, null ) );
        }

        return mapFormResponseDetailsModel;
    }

    /**
     * Return the list of all DisplayTree for the given list of Step
     * 
     * @param request
     *            the request
     * @param listStep
     *            The list of all Step on which the DisplayTree must be build
     * @param formResponse
     *            The form response on which to retrieve the Response objects
     * @return the list of all DisplayTree for the given list of Step
     */
    private List<String> buildFormStepDisplayTreeList( HttpServletRequest request, List<Step> listStep, FormResponse formResponse )
    {
        List<String> listFormDisplayTrees = new ArrayList<>( );

        if ( !CollectionUtils.isEmpty( listStep ) )
        {
            for ( Step step : listStep )
            {
                int nIdStep = step.getId( );

                StepDisplayTree stepDisplayTree = new StepDisplayTree( nIdStep, formResponse );
                listFormDisplayTrees.add(
                        stepDisplayTree.getCompositeHtml( request, FormQuestionResponseHome.getFormQuestionResponseListByFormResponse( formResponse.getId( ) ),
                                getLocale( ), DisplayType.READONLY_BACKOFFICE ) );
            }
        }

        return listFormDisplayTrees;
    }

    /**
     * Fill the map which contains the values of all filters with the data of the request
     * 
     * @param request
     *            The request used to retrieve the values of the parameters of the filters
     * @return the map which associate for each filter parameter its value
     */
    private Map<String, String> fillFilterMapValues( HttpServletRequest request )
    {
        Map<String, String> mapFilterValues = new LinkedHashMap<>( );

        Set<String> setFilterParameterName = new LinkedHashSet<>( );
        Enumeration<String> enumerationParameterName = request.getParameterNames( );

        if ( enumerationParameterName != null )
        {
            List<String> listFilterParameterName = Collections.list( enumerationParameterName );
            setFilterParameterName = listFilterParameterName.stream( )
                    .filter( strParameterName -> strParameterName.startsWith( FormsConstants.PARAMETER_URL_FILTER_PREFIX ) ).collect( Collectors.toSet( ) );
        }

        if ( !CollectionUtils.isEmpty( setFilterParameterName ) )
        {
            for ( String strFilterParameterName : setFilterParameterName )
            {
                mapFilterValues.put( strFilterParameterName.split( FormsConstants.PARAMETER_URL_FILTER_PREFIX ) [1],
                        request.getParameter( strFilterParameterName ) );
            }
        }

        String strSelectedTechnicalCode = request.getParameter( FormsConstants.PARAMETER_SELECTED_PANEL );
        if ( !StringUtils.isBlank( strSelectedTechnicalCode ) )
        {
            mapFilterValues.put( FormsConstants.PARAMETER_CURRENT_SELECTED_PANEL, strSelectedTechnicalCode );
        }

        if ( request.getParameter( FormsConstants.PARAMETER_SORT_COLUMN_POSITION ) != null )
        {
            addSortConfigParameterValues( request );
        }

        return mapFilterValues;
    }

    /**
     * Fill the map which contains the values of all filters with informations of the sort to use
     * 
     * @param request
     *            The request to use to retrieve the value of the sort
     */
    private void addSortConfigParameterValues( HttpServletRequest request )
    {
        String strPositionToSort = request.getParameter( FormsConstants.PARAMETER_SORT_COLUMN_POSITION );
        String strAttributeName = request.getParameter( FormsConstants.PARAMETER_SORT_ATTRIBUTE_NAME );
        String strAscSort = request.getParameter( FormsConstants.PARAMETER_SORT_ASC_VALUE );

        _mapFilterValues.put( FormsConstants.PARAMETER_SORT_COLUMN_POSITION, strPositionToSort );
        _mapFilterValues.put( FormsConstants.PARAMETER_SORT_ATTRIBUTE_NAME, strAttributeName );
        _mapFilterValues.put( FormsConstants.PARAMETER_SORT_ASC_VALUE, strAscSort );
    }

    /**
     * Populate the given model with the data associated to the filters from the request
     * 
     * @param mapFilterNameValues
     *            The map which contains the name of all parameters used to filter and their values
     * @param model
     *            The given model to populate
     */
    private void populateModelWithFilterValues( Map<String, String> mapFilterNameValues, Map<String, Object> model )
    {
        if ( !MapUtils.isEmpty( mapFilterNameValues ) )
        {
            ReferenceList referenceListFilterValues = new ReferenceList( );

            for ( Map.Entry<String, String> entryFilterNameValue : mapFilterNameValues.entrySet( ) )
            {
                ReferenceItem referenceItem = new ReferenceItem( );
                referenceItem.setCode( entryFilterNameValue.getKey( ) );
                referenceItem.setName( entryFilterNameValue.getValue( ) );

                referenceListFilterValues.add( referenceItem );
            }

            model.put( MARK_LIST_FILTER_VALUES, referenceListFilterValues );
        }
    }

    /**
     * Returns the task form associate to the workflow action
     *
     * @param request
     *            The Http request
     * @return The HTML form the task form associate to the workflow action
     */
    @View( value = VIEW_TASKS_FORM )
    public String getTaskForm( HttpServletRequest request )
    {
        int nIdFormResponse = NumberUtils.toInt( request.getParameter( PARAMETER_ID_FORM_RESPONSE ), NumberUtils.INTEGER_MINUS_ONE );
        int nIdAction = NumberUtils.toInt( request.getParameter( PARAMETER_ID_ACTION ), NumberUtils.INTEGER_MINUS_ONE );

        if ( nIdAction == NumberUtils.INTEGER_MINUS_ONE || nIdFormResponse == NumberUtils.INTEGER_MINUS_ONE )
        {
            return redirectView( request, VIEW_FORM_RESPONSE_DETAILS );
        }

        FormsAsynchronousUploadHandler.getHandler( ).removeSessionFiles( request.getSession( ) );

        String strHtmlTasksForm = WorkflowService.getInstance( ).getDisplayTasksForm( nIdFormResponse, FormResponse.RESOURCE_TYPE, nIdAction, request,
                getLocale( ), null );

        Map<String, Object> model = getModel( );
        model.put( MARK_ID_FORM_RESPONSE, nIdFormResponse );
        model.put( MARK_ID_ACTION, nIdAction );
        model.put( MARK_TASK_FORM, strHtmlTasksForm );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_SAVE_TASK_FORM ) );

        return getPage( MESSAGE_MULTIVIEW_FORM_RESPONSE_TITLE, TEMPLATE_TASK_FORM, model );
    }

    /**
     * Process workflow action on a form Response
     * 
     * @param request
     *            the HttpServletRequest
     * @return the task form if exists, or the FormResponse detail view otherwise.
     * @throws AccessDeniedException 
     */
    @Action( value = ACTION_PROCESS_ACTION )
    public String doProcessWorkflowAction( HttpServletRequest request ) throws AccessDeniedException
    {
        // CSRF Token control
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_PROCESS_ACTION ) )
        {
            throw new AccessDeniedException( MESSAGE_ERROR_TOKEN );
        }
        // Get parameters from request
        int nIdFormResponse = NumberUtils.toInt( request.getParameter( PARAMETER_ID_FORM_RESPONSE ), NumberUtils.INTEGER_MINUS_ONE );
        int nIdAction = NumberUtils.toInt( request.getParameter( PARAMETER_ID_ACTION ), NumberUtils.INTEGER_MINUS_ONE );

        Locale locale = getLocale( );
        WorkflowService workflowService = WorkflowService.getInstance( );
        if ( workflowService.isDisplayTasksForm( nIdAction, locale ) )
        {
            Map<String, String> model = new LinkedHashMap<>( );
            model.put( PARAMETER_ID_FORM_RESPONSE, String.valueOf( nIdFormResponse ) );
            model.put( PARAMETER_ID_ACTION, String.valueOf( nIdAction ) );

            return redirect( request, VIEW_TASKS_FORM, model );
        }

        FormResponse formResponse = FormResponseHome.findByPrimaryKey( nIdFormResponse );

        try
        {
            if ( formResponse != null )
            {
                boolean bIsAutomaticAction = Boolean.FALSE;

                workflowService.doProcessAction( nIdFormResponse, FormResponse.RESOURCE_TYPE, nIdAction, formResponse.getFormId( ), request, locale,
                        bIsAutomaticAction, getUser( ) );
            }
            else
            {
                AppLogService.error( "Error processing action for form response '" + nIdFormResponse + "' - cause : the response doesn't exist " );
            }
        }
        catch( AppException e )
        {
            AppLogService.error( "Error processing action for id response '" + nIdFormResponse + "' - cause : " + e.getMessage( ), e );
        }

        // Redirect to the correct view
        return manageRedirection( request );
    }

    /**
     * Process workflow action
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException 
     */
    @Action( value = ACTION_SAVE_TASK_FORM )
    public String doSaveTaskForm( HttpServletRequest request ) throws AccessDeniedException
    {
        // CSRF Token control
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_SAVE_TASK_FORM ) )
        {
            throw new AccessDeniedException( ERROR_INVALID_TOKEN );
        }
        int nIdFormResponse = NumberUtils.toInt( request.getParameter( PARAMETER_ID_FORM_RESPONSE ), NumberUtils.INTEGER_MINUS_ONE );
        int nIdAction = NumberUtils.toInt( request.getParameter( PARAMETER_ID_ACTION ), NumberUtils.INTEGER_MINUS_ONE );

        FormResponse formResponse = FormResponseHome.findByPrimaryKey( nIdFormResponse );
        int nIdForm = ( formResponse != null ) ? formResponse.getFormId( ) : NumberUtils.INTEGER_MINUS_ONE;

        WorkflowService workflowService = WorkflowService.getInstance( );
        if ( workflowService.canProcessAction( nIdFormResponse, FormResponse.RESOURCE_TYPE, nIdAction, nIdForm, request, false, null ) )
        {
            try
            {
                String strError = workflowService.doSaveTasksForm( nIdFormResponse, FormResponse.RESOURCE_TYPE, nIdAction, nIdForm, request, getLocale( ),
                        getUser( ) );
                if ( strError != null )
                {
                    return redirect( request, strError );
                }
            }
            catch( AppException e )
            {
                AppLogService.error( "Error processing action for record " + nIdFormResponse, e );
            }
        }
        else
        {
            addError( MESSAGE_ACTION_ERROR, request.getLocale( ) );
            return redirect( request, VIEW_FORM_RESPONSE_DETAILS, PARAMETER_ID_FORM_RESPONSE,
                    Integer.parseInt( request.getParameter( PARAMETER_ID_FORM_RESPONSE ) ), PARAMETER_BACK_FROM_ACTION, 1 );
        }
        return manageRedirection( request );
    }

    /**
     * Cancel an action of the workflow
     * 
     * @param request
     *            The HttpServletRequest
     * @return the Jsp URL to return
     */
    @Action( value = ACTION_CANCEL_TASK_FORM )
    public String doCancelTaskForm( HttpServletRequest request )
    {
        Map<String, String> mapParameters = new LinkedHashMap<>( );
        mapParameters.put( PARAMETER_ID_FORM_RESPONSE, request.getParameter( PARAMETER_ID_FORM_RESPONSE ) );
        mapParameters.put( PARAMETER_BACK_FROM_ACTION, Boolean.TRUE.toString( ) );

        return redirect( request, VIEW_FORM_RESPONSE_DETAILS, mapParameters );
    }

    /**
     * Return the default view base url for the MultiviewFormResponseDetailsJspBean
     * 
     * @return the default view base url for the MultiviewFormResponseDetailsJspBean
     */
    protected static String getMultiviewRecordDetailsBaseUrl( )
    {
        UrlItem urlRecordDetailsBase = new UrlItem( CONTROLLER_JSP_NAME_WITH_PATH );
        urlRecordDetailsBase.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_FORM_RESPONSE_DETAILS );

        return urlRecordDetailsBase.getUrl( );
    }

    /**
     * Redirect to the appropriate view
     * 
     * @param request
     *            The HttpServletRequest to retrieve data from
     * @return redirect to the appropriate view
     */
    private String manageRedirection( HttpServletRequest request )
    {
        String strWorkflowActionRedirection = request.getParameter( FormsConstants.PARAMETER_WORKFLOW_ACTION_REDIRECTION );
        if ( StringUtils.isNotBlank( strWorkflowActionRedirection ) )
        {
            MultiviewFormWorkflowRedirectionEnum workflowActionRedirectionEnum = MultiviewFormWorkflowRedirectionEnum
                    .getEnumNameByValue( strWorkflowActionRedirection );
            switch( workflowActionRedirectionEnum )
            {
                case LIST:
                    return redirectToResponseList( request );
                case DETAILS:
                    return redirectToResponseDetailView( request );
                default:
                    return defaultRedirection( request );
            }
        }
        else
        {
            return defaultRedirection( request );
        }
    }

    /**
     * Redirect to the page of the list of all responses
     * 
     * @param request
     *            The HttpServletRequest to retrieve data from
     * @return redirect to the page with the list of all records
     */
    private String redirectToResponseList( HttpServletRequest request )
    {
        return redirect( request, buildRedirecUrlWithFilterValues( ) );
    }

    /**
     * Redirect to the page of a record
     * 
     * @param request
     *            The HttpServletRequest to retrieve data from
     * @return redirect to the page of the record
     */
    private String redirectToResponseDetailView( HttpServletRequest request )
    {
        return defaultRedirection( request );
    }

    /**
     * Return to the default view which is the response detail page
     * 
     * @param request
     *            The HttpServletRequest to retrieve data from
     * @return redirect to the default view which is the page of the record
     */
    private String defaultRedirection( HttpServletRequest request )
    {

        int nIdFormResponse = NumberUtils.toInt( request.getParameter( PARAMETER_ID_FORM_RESPONSE ), NumberUtils.INTEGER_MINUS_ONE );

        if ( nIdFormResponse != NumberUtils.INTEGER_MINUS_ONE )
        {

            Map<String, String> mapParameters = new LinkedHashMap<>( );
            mapParameters.put( PARAMETER_ID_FORM_RESPONSE, String.valueOf( nIdFormResponse ) );
            mapParameters.put( PARAMETER_BACK_FROM_ACTION, Boolean.TRUE.toString( ) );

            return redirect( request, VIEW_FORM_RESPONSE_DETAILS, mapParameters );
        }
        else
        {
            AppLogService.error( "The given id form response is not valid !" );

            return redirect( request, buildRedirecUrlWithFilterValues( ) );
        }
    }

    /**
     * Build the url with the values of the filter selected on the list view
     * 
     * @return the url with the values of the filter selected on the list view
     */
    private String buildRedirecUrlWithFilterValues( )
    {
        UrlItem urlRedirectWithFilterValues = new UrlItem( MultiviewFormsJspBean.getMultiviewBaseViewUrl( ) );

        if ( !MapUtils.isEmpty( _mapFilterValues ) )
        {
            for ( Entry<String, String> entryFilterNameValue : _mapFilterValues.entrySet( ) )
            {
                String strFilterName = entryFilterNameValue.getKey( );
                String strFilterValue = entryFilterNameValue.getValue( );
                try
                {
                    strFilterValue = URLEncoder.encode( strFilterValue, StandardCharsets.UTF_8.name( ) );
                }
                catch( UnsupportedEncodingException exception )
                {
                    AppLogService.debug( "Failed to encode url parameter value !" );
                }

                urlRedirectWithFilterValues.addParameter( strFilterName, strFilterValue );
            }
        }

        return urlRedirectWithFilterValues.getUrl( );
    }

}
