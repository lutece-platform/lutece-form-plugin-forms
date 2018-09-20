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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormMessage;
import fr.paris.lutece.plugins.forms.business.FormMessageHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.business.Transition;
import fr.paris.lutece.plugins.forms.business.TransitionHome;
import fr.paris.lutece.plugins.forms.exception.FormNotFoundException;
import fr.paris.lutece.plugins.forms.exception.QuestionValidationException;
import fr.paris.lutece.plugins.forms.service.EntryServiceManager;
import fr.paris.lutece.plugins.forms.service.FormService;
import fr.paris.lutece.plugins.forms.service.upload.FormsAsynchronousUploadHandler;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.validation.IValidator;
import fr.paris.lutece.plugins.forms.web.breadcrumb.IBreadcrumb;
import fr.paris.lutece.plugins.forms.web.entrytype.DisplayType;
import fr.paris.lutece.plugins.forms.web.entrytype.IEntryDataService;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.util.url.UrlItem;

/**
 * 
 * Controller for form display
 *
 */
@Controller( xpageName = FormXPage.XPAGE_NAME, pageTitleI18nKey = FormXPage.MESSAGE_PAGE_TITLE, pagePathI18nKey = FormXPage.MESSAGE_PATH )
public class FormXPage extends MVCApplication
{
    protected static final String XPAGE_NAME = "forms";

    // Messages
    protected static final String MESSAGE_PAGE_TITLE = "forms.xpage.form.view.pageTitle";
    protected static final String MESSAGE_PATH = "forms.xpage.form.view.pagePathLabel";
    protected static final String MESSAGE_ERROR_NO_STEP = "forms.xpage.form.error.noStep";
    protected static final String MESSAGE_ERROR_INACTIVE_FORM = "forms.xpage.form.error.inactive";
    protected static final String MESSAGE_LOAD_BACKUP = "forms.xpage.form.view.loadBackUp";

    /**
     * Generated serial id
     */
    private static final long serialVersionUID = -8380962697376893817L;

    // Views
    private static final String VIEW_STEP = "stepView";
    private static final String VIEW_GET_STEP = "getStep";

    // Actions
    private static final String ACTION_SAVE_FORM_RESPONSE = "doSaveResponse";
    private static final String ACTION_SAVE_FOR_BACKUP = "doSaveForBackup";
    private static final String ACTION_SAVE_STEP = "doSaveStep";
    private static final String ACTION_PREVIOUS_STEP = "doReturnStep";
    private static final String ACTION_ADD_ITERATION = "addIteration";
    private static final String ACTION_REMOVE_ITERATION = "removeIteration";
    private static final String ACTION_FORM_RESPONSE_SUMMARY = "formResponseSummary";

    // Templates
    private static final String TEMPLATE_VIEW_STEP = "/skin/plugins/forms/step_view.html";
    private static final String TEMPLATE_FORM_SUBMITTED = "/skin/plugins/forms/form_submitted_view.html";
    private static final String TEMPLATE_VIEW_FORM_RESPONSE_SUMMARY = "/skin/plugins/forms/form_response_summary.html";

    // Constants
    private static final int INCORRECT_ID = -1;

    // Markers
    private static final String STEP_HTML_MARKER = "stepContent";
    private static final String MARK_LIST_SUMMARY_STEP_DISPLAY = "list_summary_step_display";

    // Other
    private static FormService _formService = SpringContextService.getBean( FormService.BEAN_NAME );
    // Attributes
    private FormResponseManager _formResponseManager;
    private Step _currentStep;
    private StepDisplayTree _stepDisplayTree;
    private IBreadcrumb _breadcrumb;

    /**
     * 
     * @param form
     *            the given Form
     * @param request
     *            The Http request
     * @throws UserNotSignedException
     *             Exception
     */
    private void checkAuthentication( Form form, HttpServletRequest request ) throws UserNotSignedException
    {
        try
        {
            checkMyLuteceAuthentification( form, request );
        }
        catch( UserNotSignedException e )
        {
            _currentStep = StepHome.getInitialStep( form.getId( ) );
            _formResponseManager = null;
            _stepDisplayTree = null;

            throw new UserNotSignedException( );
        }
    }

    /**
     * check if authentification
     * 
     * @param form
     *            Form
     * @param request
     *            HttpServletRequest
     * @throws UserNotSignedException
     *             exception if the form requires an authentification and the user is not logged
     */
    public void checkMyLuteceAuthentification( Form form, HttpServletRequest request ) throws UserNotSignedException
    {
        // Try to register the user in case of external authentication
        if ( SecurityService.isAuthenticationEnable( ) )
        {
            if ( SecurityService.getInstance( ).isExternalAuthentication( ) )
            {
                // The authentication is external
                // Should register the user if it's not already done
                if ( SecurityService.getInstance( ).getRegisteredUser( request ) == null )
                {
                    if ( ( SecurityService.getInstance( ).getRemoteUser( request ) == null ) && ( form.getAuthentificationNeeded( ) ) )
                    {
                        // Authentication is required to access to the portal
                        throw new UserNotSignedException( );
                    }
                }
            }
            else
            {
                // If portal authentication is enabled and user is null and the requested URL
                // is not the login URL, user cannot access to Portal
                if ( ( form.getAuthentificationNeeded( ) ) && ( SecurityService.getInstance( ).getRegisteredUser( request ) == null )
                        && !SecurityService.getInstance( ).isLoginUrl( request ) )
                {
                    // Authentication is required to access to the portal
                    throw new UserNotSignedException( );
                }
            }
        }
    }

    /**
     * 
     * @param request
     *            The Http request
     * @return the XPage
     * 
     * @throws SiteMessageException
     *             Exception
     * @throws UserNotSignedException
     *             Exception
     */
    @View( value = VIEW_STEP, defaultView = true )
    public XPage getStepView( HttpServletRequest request ) throws SiteMessageException, UserNotSignedException
    {
        Map<String, Object> model = getModel( );
        String strTitleForm = StringUtils.EMPTY;
        int nIdForm = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ), FormsConstants.DEFAULT_ID_VALUE );

        if ( nIdForm != FormsConstants.DEFAULT_ID_VALUE && ( _currentStep == null || nIdForm != _currentStep.getIdForm( ) ) )
        {
            _currentStep = StepHome.getInitialStep( nIdForm );
            _formResponseManager = null;
        }

        if ( _currentStep == null )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR_NO_STEP, SiteMessage.TYPE_ERROR );
        }
        else
        {
            Form form = FormHome.findByPrimaryKey( _currentStep.getIdForm( ) );
            strTitleForm = form.getTitle( );
            if ( form.isActive( ) )
            {
                if ( _breadcrumb == null )
                {
                    _breadcrumb = SpringContextService.getBean( form.getBreadcrumbName( ) );
                }

                getFormStepModel( form, request, model );
            }
            else
            {
                SiteMessageService.setMessage( request, MESSAGE_ERROR_INACTIVE_FORM, SiteMessage.TYPE_ERROR );
            }
        }

        XPage xPage = getXPage( TEMPLATE_VIEW_STEP, request.getLocale( ), model );
        xPage.setTitle( strTitleForm );
        xPage.setPathLabel( strTitleForm );

        return xPage;
    }

    /**
     * @param form
     *            The form to display
     * @param request
     *            The Http request
     * @param model
     *            The model for XPage
     * @throws UserNotSignedException
     *             Exception
     */
    private void getFormStepModel( Form form, HttpServletRequest request, Map<String, Object> model ) throws UserNotSignedException
    {
        checkAuthentication( form, request );

        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );

        if ( _formResponseManager == null )
        {
            if ( user != null )
            {
                _formResponseManager = _formService.createFormResponseManagerFromBackUp( form, user.getName( ) );

                if ( _formResponseManager.getFormResponse( ).getFromSave( ) )
                {
                    _currentStep = _formResponseManager.getCurrentStep( );
                    _stepDisplayTree = new StepDisplayTree( _currentStep.getId( ), _formResponseManager.getFormResponse( ) );

                    Object [ ] args = {
                        _formResponseManager.getFormResponse( ).getUpdate( ),
                    };

                    model.put( FormsConstants.MARK_INFO, I18nService.getLocalizedString( MESSAGE_LOAD_BACKUP, args, request.getLocale( ) ) );
                }
            }
            else
            {
                _formResponseManager = new FormResponseManager( form );
            }
        }

        if ( _stepDisplayTree == null || _currentStep.getId( ) != _stepDisplayTree.getStep( ).getId( ) )
        {
            _stepDisplayTree = new StepDisplayTree( _currentStep.getId( ), _formResponseManager.getFormResponse( ) );
            _formResponseManager.add( _currentStep );
        }

        model.put( STEP_HTML_MARKER, _stepDisplayTree.getCompositeHtml( _formResponseManager.findResponsesFor( _currentStep ), request.getLocale( ),
                DisplayType.EDITION_FRONTOFFICE, user ) );
        model.put( FormsConstants.MARK_FORM_TOP_BREADCRUMB, _breadcrumb.getTopHtml( _formResponseManager ) );
        model.put( FormsConstants.MARK_FORM_BOTTOM_BREADCRUMB, _breadcrumb.getBottomHtml( _formResponseManager ) );
    }

    /**
     * 
     * @param request
     *            The Http request
     * @return the XPage
     * 
     * @throws SiteMessageException
     *             Exception
     */
    @Action( value = ACTION_PREVIOUS_STEP )
    public XPage doReturnStep( HttpServletRequest request ) throws SiteMessageException
    {
        _formResponseManager.popStep( );

        _currentStep = _formResponseManager.getCurrentStep( );

        _stepDisplayTree = new StepDisplayTree( _currentStep.getId( ), _formResponseManager.getFormResponse( ) );

        return redirectView( request, VIEW_STEP );
    }

    /**
     * 
     * @param request
     *            The Http request
     * @return the XPage
     * 
     * @throws SiteMessageException
     *             Exception
     */
    @View( value = VIEW_GET_STEP )
    public XPage getPreviousStep( HttpServletRequest request ) throws SiteMessageException
    {
        int nIndexStep = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_INDEX_STEP ), INCORRECT_ID );

        _currentStep = _formResponseManager.goTo( nIndexStep );

        _stepDisplayTree = new StepDisplayTree( _currentStep.getId( ), _formResponseManager.getFormResponse( ) );

        return redirectView( request, VIEW_STEP );
    }

    /**
     * Gives the summary page
     * 
     * @param request
     *            The request
     * @return the summary page
     * @throws SiteMessageException
     *             if there is an error during the page generation
     */
    @Action( value = ACTION_FORM_RESPONSE_SUMMARY )
    public XPage doFormResponseSummary( HttpServletRequest request ) throws SiteMessageException
    {
        Form form = null;

        try
        {
            findFormFrom( request );
            fillResponseManagerWithResponses( request );
        }
        catch( FormNotFoundException | QuestionValidationException exception )
        {
            return redirectView( request, VIEW_STEP );
        }

        Map<String, Object> model = buildModelForSummary( request, form );

        return getXPage( TEMPLATE_VIEW_FORM_RESPONSE_SUMMARY, request.getLocale( ), model );
    }

    /**
     * Builds the model for the summary page
     * 
     * @param request
     *            the request
     * @param form
     *            The form
     * @return the model
     */
    private Map<String, Object> buildModelForSummary( HttpServletRequest request, Form form )
    {
        Map<String, Object> mapFormResponseSummaryModel = new HashMap<>( );

        List<Step> listValidatedStep = _formResponseManager.getValidatedSteps( );

        List<String> listStepHtml = buildStepsHtml( request, listValidatedStep );
        mapFormResponseSummaryModel.put( MARK_LIST_SUMMARY_STEP_DISPLAY, listStepHtml );

        return mapFormResponseSummaryModel;
    }

    /**
     * Builds the HTML for the specified list of steps
     * 
     * @param request
     *            The request
     * @param listStep
     *            The list of steps
     * @return the list of HTML
     */
    private List<String> buildStepsHtml( HttpServletRequest request, List<Step> listStep )
    {
        List<String> listFormDisplayTrees = new ArrayList<>( );

        for ( Step step : listStep )
        {
            StepDisplayTree stepDisplayTree = new StepDisplayTree( step.getId( ), _formResponseManager.getFormResponse( ) );

            listFormDisplayTrees.add( stepDisplayTree.getCompositeHtml( _formResponseManager.findResponsesFor( step ), request.getLocale( ),
                    DisplayType.READONLY_FRONTOFFICE, null ) );
        }

        return listFormDisplayTrees;
    }

    /**
     * 
     * @param request
     *            The Http request
     * @return the XPage
     * 
     * @throws SiteMessageException
     *             Exception
     * @throws UserNotSignedException
     *             Exception
     */
    @Action( value = ACTION_SAVE_FORM_RESPONSE )
    public XPage doSaveFormResponse( HttpServletRequest request ) throws SiteMessageException, UserNotSignedException
    {
        Form form = null;

        try
        {
            form = findFormFrom( request );

            if ( !form.isDisplaySummary( ) )
            {
                fillResponseManagerWithResponses( request );
            }
        }
        catch( FormNotFoundException | QuestionValidationException exception )
        {
            return redirectView( request, VIEW_STEP );
        }

        FormResponse formResponse = _formResponseManager.getFormResponse( );

        _formService.saveForm( form, formResponse );

        FormsAsynchronousUploadHandler.getHandler( ).removeSessionFiles( request.getSession( ).getId( ) );
        Map<String, Object> model = getModel( );

        model.put( FormsConstants.PARAMETER_ID_FORM, form.getId( ) );

        _formResponseManager = null;
        _currentStep = null;
        _stepDisplayTree = null;
        _breadcrumb = null;

        FormMessage formMessage = FormMessageHome.findByForm( form.getId( ) );
        boolean bIsEndMessageDisplayed = formMessage.getEndMessageDisplay( );
        String strBackUrl = getBackUrl( form, bIsEndMessageDisplayed );

        if ( formMessage != null && bIsEndMessageDisplayed )
        {
            model.put( FormsConstants.MARK_INFO, formMessage.getEndMessage( ) );
        }
        else
        {
            return redirect( request, strBackUrl );
        }

        model.put( FormsConstants.PARAMETER_BACK_URL, strBackUrl );

        return getXPage( TEMPLATE_FORM_SUBMITTED, request.getLocale( ), model );
    }

    /**
     * Finds the form from the specified request
     * 
     * @param request
     *            the request
     * @return the found form, or {@code null} if not found
     * @throws FormNotFoundException
     *             if the form is not found
     * @throws SiteMessageException
     *             if the form is not accessible
     */
    private Form findFormFrom( HttpServletRequest request ) throws FormNotFoundException, SiteMessageException
    {
        Form form = null;

        if ( _currentStep == null )
        {
            int nIdForm = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ), INCORRECT_ID );

            if ( nIdForm != FormsConstants.DEFAULT_ID_VALUE )
            {
                form = FormHome.findByPrimaryKey( nIdForm );

                if ( form == null )
                {
                    throw new FormNotFoundException( );
                }

                _currentStep = StepHome.getInitialStep( nIdForm );
                _formResponseManager = null;
            }
            else
            {
                throw new FormNotFoundException( );
            }
        }
        else
        {
            form = FormHome.findByPrimaryKey( _currentStep.getIdForm( ) );
        }

        if ( !form.isActive( ) )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR_INACTIVE_FORM, SiteMessage.TYPE_ERROR );
        }

        return form;
    }

    /**
     * 
     * @param form
     *            The Form
     * @param bIsEndMessageDisplayed
     *            {@code true} if the end message is displayed, {@code false} otherwise
     * @return the back URL
     */
    private String getBackUrl( Form form, boolean bIsEndMessageDisplayed )
    {
        if ( StringUtils.isNotEmpty( form.getReturnUrl( ) ) )
        {
            return form.getReturnUrl( );
        }
        else
        {
            UrlItem url = null;

            if ( bIsEndMessageDisplayed )
            {
                url = new UrlItem( getViewFullUrl( VIEW_STEP ) );
            }
            else
            {
                url = new UrlItem( getViewUrl( VIEW_STEP ) );
            }

            url.addParameter( FormsConstants.PARAMETER_ID_FORM, form.getId( ) );

            return url.getUrl( );
        }
    }

    /**
     * @param request
     *            The Http request
     * 
     * @throws QuestionValidationException
     *             if there is at least one question not valid
     */
    private void fillResponseManagerWithResponses( HttpServletRequest request ) throws QuestionValidationException
    {
        List<Question> listQuestionStep = _stepDisplayTree.getQuestions( );

        boolean bValidStep = true;
        List<FormQuestionResponse> listResponsesTemp = new ArrayList<FormQuestionResponse>( );

        String [ ] listConditionalQuestionsValues = request.getParameterValues( FormsConstants.PARAMETER_DISPLAYED_QUESTIONS );

        for ( Question question : listQuestionStep )
        {
            for ( int i = 0; i < listConditionalQuestionsValues.length; i++ )
            {
                String [ ] listQuestionId = listConditionalQuestionsValues [i].split( FormsConstants.SEPARATOR_UNDERSCORE );
                if ( !StringUtils.isEmpty( listQuestionId [0] ) && Integer.parseInt( listQuestionId [0] ) == question.getId( )
                        && Integer.parseInt( listQuestionId [1] ) == question.getIterationNumber( ) )
                {
                    question.setIsVisible( true );
                    break;
                }
                else
                {
                    question.setIsVisible( false );
                }
            }
            if ( !question.getEntry( ).isOnlyDisplayInBack( ) )
            {
                IEntryDataService entryDataService = EntryServiceManager.getInstance( ).getEntryDataService( question.getEntry( ).getEntryType( ) );
                if ( entryDataService != null )
                {
                    FormQuestionResponse formQuestionResponse = entryDataService.createResponseFromRequest( question, request );

                    if ( formQuestionResponse.hasError( ) )
                    {
                        bValidStep = false;
                    }

                    listResponsesTemp.add( formQuestionResponse );
                }
            }
        }

        _formResponseManager.addResponses( listResponsesTemp );

        if ( !bValidStep )
        {
            throw new QuestionValidationException( );
        }
    }

    /**
     * 
     * @param request
     *            The Http request
     * @return the XPage
     * 
     * @throws SiteMessageException
     *             Exception
     * @throws UserNotSignedException
     *             Exception
     */
    @Action( value = ACTION_SAVE_STEP )
    public XPage doSaveStep( HttpServletRequest request ) throws SiteMessageException, UserNotSignedException
    {
        try
        {
            findFormFrom( request );
            fillResponseManagerWithResponses( request );
        }
        catch( FormNotFoundException | QuestionValidationException exception )
        {
            return redirectView( request, VIEW_STEP );
        }

        _currentStep = getNextStep( request );

        return redirectView( request, VIEW_STEP );
    }

    /**
     * 
     * @param request
     *            The Http request
     * @return The next Step
     */
    private Step getNextStep( HttpServletRequest request )
    {
        List<Transition> listTransition = TransitionHome.getTransitionsListFromStep( _currentStep.getId( ) );

        for ( Transition transition : listTransition )
        {
            if ( transition.getIdControl( ) <= 0 )
            {
                return StepHome.findByPrimaryKey( transition.getNextStep( ) );
            }
            else
            {
                Control transitionControl = ControlHome.findByPrimaryKey( transition.getIdControl( ) );

                Question targetQuestion = QuestionHome.findByPrimaryKey( transitionControl.getIdQuestion( ) );
                Step stepTarget = StepHome.findByPrimaryKey( targetQuestion.getIdStep( ) );

                for ( FormQuestionResponse questionResponse : _formResponseManager.findResponsesFor( stepTarget ) )
                {
                    if ( transitionControl.getIdQuestion( ) == questionResponse.getQuestion( ).getId( ) )
                    {
                        IValidator validator = EntryServiceManager.getInstance( ).getValidator( transitionControl.getValidatorName( ) );

                        if ( validator != null && validator.validate( questionResponse, transitionControl ) )
                        {
                            return StepHome.findByPrimaryKey( transition.getNextStep( ) );
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * 
     * @param request
     *            The Http request
     * @return the XPage
     * 
     * @throws SiteMessageException
     *             Exception
     * @throws UserNotSignedException
     *             Exception
     */
    @Action( value = ACTION_SAVE_FOR_BACKUP )
    public XPage doSaveForBackup( HttpServletRequest request ) throws SiteMessageException, UserNotSignedException
    {
        Form form = null;

        try
        {
            form = findFormFrom( request );
        }
        catch( FormNotFoundException exception )
        {
            return redirectView( request, VIEW_STEP );
        }

        checkAuthentication( form, request );

        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );

        List<Question> listQuestionStep = _stepDisplayTree.getQuestions( );

        Map<Integer, List<Response>> mapStepResponses = new HashMap<Integer, List<Response>>( );
        List<FormQuestionResponse> listResponsesTemp = new ArrayList<FormQuestionResponse>( );

        for ( Question question : listQuestionStep )
        {
            if ( !question.getEntry( ).isOnlyDisplayInBack( ) )
            {
                IEntryDataService entryDataService = EntryServiceManager.getInstance( ).getEntryDataService( question.getEntry( ).getEntryType( ) );
                if ( entryDataService != null )
                {
                    FormQuestionResponse formQuestionResponse = entryDataService.createResponseFromRequest( question, request );

                    if ( !formQuestionResponse.hasError( ) )
                    {
                        listResponsesTemp.add( formQuestionResponse );
                    }

                    mapStepResponses.put( question.getId( ), formQuestionResponse.getEntryResponse( ) );
                }
            }
        }

        _formResponseManager.addResponses( listResponsesTemp );

        FormResponse formResponse = _formResponseManager.getFormResponse( );
        formResponse.setGuid( user.getName( ) );

        _formService.saveFormForBackup( formResponse );

        return redirectView( request, VIEW_STEP );
    }

    /**
     * Adds an iteration
     * 
     * @param request
     *            the request
     * @return the XPage
     * @throws SiteMessageException
     *             if there is an error during the iteration
     * @throws UserNotSignedException
     *             if the user is not signed in
     */
    @Action( value = ACTION_ADD_ITERATION )
    public XPage doAddIteration( HttpServletRequest request ) throws SiteMessageException, UserNotSignedException
    {
        try
        {
            findFormFrom( request );
            fillResponseManagerWithResponses( request );
        }
        catch( FormNotFoundException | QuestionValidationException exception )
        {
            return redirectView( request, VIEW_STEP );
        }

        int nIdGroup = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ACTION_PREFIX + ACTION_ADD_ITERATION ),
                FormsConstants.DEFAULT_ID_VALUE );

        if ( nIdGroup != FormsConstants.DEFAULT_ID_VALUE )
        {
            _stepDisplayTree.iterate( nIdGroup );
        }

        return redirectView( request, VIEW_STEP );
    }

    /**
     * Remove an iteration
     * 
     * @param request
     *            the request
     * @return the XPage
     * @throws SiteMessageException
     *             if there is an error during the iteration
     * @throws UserNotSignedException
     *             if the user is not signed in
     */
    @Action( value = ACTION_REMOVE_ITERATION )
    public XPage doRemoveIteration( HttpServletRequest request ) throws SiteMessageException, UserNotSignedException
    {
        try
        {
            findFormFrom( request );
            fillResponseManagerWithResponses( request );
        }
        catch( FormNotFoundException | QuestionValidationException exception )
        {
            return redirectView( request, VIEW_STEP );
        }

        String strIterationInfo = request.getParameter( FormsConstants.PARAMETER_ACTION_PREFIX + ACTION_REMOVE_ITERATION );

        String [ ] arrayIterationInfo = strIterationInfo.split( FormsConstants.SEPARATOR_UNDERSCORE );

        int nIdGroupParent = Integer.valueOf( arrayIterationInfo [0] );
        int nIndexIteration = Integer.valueOf( arrayIterationInfo [1] );

        _stepDisplayTree.removeIteration( nIdGroupParent, nIndexIteration );

        return redirectView( request, VIEW_STEP );
    }
}
