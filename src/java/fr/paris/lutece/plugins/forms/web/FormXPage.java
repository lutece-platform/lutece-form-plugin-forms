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
package fr.paris.lutece.plugins.forms.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.ControlType;
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
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeAutomaticFileReading;
import fr.paris.lutece.plugins.forms.service.upload.FormsAsynchronousUploadHandler;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.util.FormsUtils;
import fr.paris.lutece.plugins.forms.validation.IValidator;
import fr.paris.lutece.plugins.forms.web.breadcrumb.IBreadcrumb;
import fr.paris.lutece.plugins.forms.web.entrytype.DisplayType;
import fr.paris.lutece.plugins.forms.web.entrytype.IEntryDataService;
import fr.paris.lutece.plugins.forms.web.http.SynchronousHttpServletRequestWrapper;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFileHome;
import fr.paris.lutece.portal.service.accesscontrol.AccessControlService;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.captcha.CaptchaSecurityService;
import fr.paris.lutece.portal.service.captcha.ICaptchaSecurityService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
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
    private static final long serialVersionUID = -8380962697376893817L;

    // Messages
    protected static final String MESSAGE_PAGE_TITLE = "forms.xpage.form.view.pageTitle";
    protected static final String MESSAGE_PATH = "forms.xpage.form.view.pagePathLabel";
    protected static final String MESSAGE_ERROR_NO_STEP = "forms.xpage.form.error.noStep";
    protected static final String MESSAGE_ERROR_CONTROL = "forms.xpage.form.error.control";
    protected static final String MESSAGE_ERROR_INACTIVE_FORM = "forms.xpage.form.error.inactive";
    protected static final String MESSAGE_ERROR_NUMBER_MAX_RESPONSE_FORM = "forms.xpage.form.error.MaxResponse";
    protected static final String MESSAGE_ERROR_NOT_RESPONSE_AGAIN_FORM = "forms.xpage.form.error.limitNumberResponse";
    protected static final String MESSAGE_LOAD_BACKUP = "forms.xpage.form.view.loadBackUp";
    protected static final String MESSAGE_LIST_FORMS_PAGETITLE = "forms.xpage.listForms.pagetitle";
    protected static final String MESSAGE_LIST_FORMS_PATHLABEL = "forms.xpage.listForms.pathlabel";
    private static final String MESSAGE_WARNING_LOST_SESSION = "forms.warning.lost.session";
    private static final String MESSAGE_WARNING_CAPTCHA = "portal.admin.message.wrongCaptcha";
    private static final String MESSAGE_ERROR_STEP_NOT_FINAL = "forms.error.step.isnot.final";
    private static final String MESSAGE_STEP_TITLE = "forms.step.title";
    private static final String MESSAGE_SUMMARY_TITLE = "forms.summary.title";
    private static final String MESSAGE_WARNING_INACTIVE_STATE_BYPASSED = "forms.warning.inactive.state.bypassed";
    private static final String MESSAGE_ERROR_TOKEN = "Invalid security token";

    // Views
    private static final String VIEW_STEP = "stepView";
    private static final String VIEW_LIST_FORM = "listForm";

    // Actions
    private static final String ACTION_SAVE_FORM_RESPONSE = "doSaveResponse";
    private static final String ACTION_SAVE_FORM_RESPONSE_SUMMARY = "doSaveResponseSummary";
    private static final String ACTION_SAVE_FOR_BACKUP = "doSaveForBackup";
    private static final String ACTION_SAVE_STEP = "doSaveStep";
    private static final String ACTION_UPLOAD = "doSynchronousUploadDocument";
    private static final String ACTION_RESET_BACKUP = "doResetBackup";
    private static final String ACTION_PREVIOUS_STEP = "doReturnStep";
    private static final String ACTION_GO_TO_STEP = "doGoToStep";
    private static final String ACTION_ADD_ITERATION = "addIteration";
    private static final String ACTION_REMOVE_ITERATION = "removeIteration";
    private static final String ACTION_FORM_RESPONSE_SUMMARY = "formResponseSummary";

    // Templates
    private static final String TEMPLATE_VIEW_STEP = "/skin/plugins/forms/step_view.html";
    private static final String TEMPLATE_FORM_SUBMITTED = "/skin/plugins/forms/form_submitted_view.html";
    private static final String TEMPLATE_VIEW_FORM_RESPONSE_SUMMARY = "/skin/plugins/forms/form_response_summary.html";
    private static final String TEMPLATE_LIST_FORMS = "skin/plugins/forms/list_forms.html";

    // Constants
    private static final int INCORRECT_ID = -1;
    private static final String PARAMETER_INIT = "true";

    // Markers
    private static final String STEP_HTML_MARKER = "stepContent";
    private static final String MARK_LIST_SUMMARY_STEP_DISPLAY = "list_summary_step_display";
    private static final String MARK_FORM_LIST = "form_list";
    private static final String MARK_DISPLAY_CAPTCHA = "display_captcha";
    private static final String MARK_CAPTCHA = "captcha";

    // Other
    private static FormService _formService = SpringContextService.getBean( FormService.BEAN_NAME );
    private ICaptchaSecurityService _captchaSecurityService = new CaptchaSecurityService( );
    private static Map<Integer, Integer> _responsePerFormMap = new HashMap<>( );
    private static ConcurrentMap<Integer, Object> _lockFormId = new ConcurrentHashMap<>( );

    // Attributes
    private FormResponseManager _formResponseManager;
    private Step _currentStep;
    private StepDisplayTree _stepDisplayTree;
    private IBreadcrumb _breadcrumb;
    private boolean _bInactiveStateBypassed;

    /**
     * Return the default XPage with the list of all available Form
     * 
     * @param request
     *            The HttpServletRequest
     * @return the list of all available forms
     */
    @View( value = VIEW_LIST_FORM, defaultView = true )
    public XPage getListFormView( HttpServletRequest request )
    {
        Locale locale = getLocale( request );
        List<Form> listFormsAll = FormHome.getFormList( );
        for ( Form form : listFormsAll )
        {
            if ( form.isCountResponses( ) )
            {
                form.setCurrentNumberResponse( FormHome.getNumberOfResponseForms( form.getId( ) ) );
            }
            if ( form.getLogo( ) != null )
            {
                form.setLogo( FileHome.findByPrimaryKey( form.getLogo( ).getIdFile( ) ) );
                form.getLogo( ).setPhysicalFile( PhysicalFileHome.findByPrimaryKey( form.getLogo( ).getPhysicalFile( ).getIdPhysicalFile( ) ) );
            }
        }

        Map<String, Object> model = getModel( );
        model.put( MARK_FORM_LIST, listFormsAll );
        XPage xPage = getXPage( TEMPLATE_LIST_FORMS, locale, model );
        xPage.setTitle( I18nService.getLocalizedString( MESSAGE_LIST_FORMS_PAGETITLE, locale ) );
        xPage.setPathLabel( I18nService.getLocalizedString( MESSAGE_LIST_FORMS_PATHLABEL, locale ) );
        return xPage;
    }

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
            init( form.getId( ) );

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
        if ( !SecurityService.isAuthenticationEnable( ) )
        {
            return;
        }
        if ( SecurityService.getInstance( ).isExternalAuthentication( ) )
        {
            // The authentication is external
            // Should register the user if it's not already done
            if ( SecurityService.getInstance( ).getRegisteredUser( request ) == null && SecurityService.getInstance( ).getRemoteUser( request ) == null
                    && form.isAuthentificationNeeded( ) )
            {
                // Authentication is required to access to the portal
                throw new UserNotSignedException( );
            }
        }
        else
        {
            // If portal authentication is enabled and user is null and the requested URL
            // is not the login URL, user cannot access to Portal
            if ( ( form.isAuthentificationNeeded( ) ) && ( SecurityService.getInstance( ).getRegisteredUser( request ) == null )
                    && !SecurityService.getInstance( ).isLoginUrl( request ) )
            {
                // Authentication is required to access to the portal
                throw new UserNotSignedException( );
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
    @View( value = VIEW_STEP )
    public XPage getStepView( HttpServletRequest request ) throws SiteMessageException, UserNotSignedException
    {
        String paramInit = request.getParameter( FormsConstants.PARAMETER_INIT );
        if ( PARAMETER_INIT.equals( paramInit ) )
        {
            init( request );
        }

        int nIdForm = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ), FormsConstants.DEFAULT_ID_VALUE );

        if ( nIdForm != FormsConstants.DEFAULT_ID_VALUE && ( _currentStep == null || nIdForm != _currentStep.getIdForm( ) ) )
        {
            init( nIdForm );
        }

        if ( _currentStep == null )
        {
            // Throws Exception
            SiteMessageService.setMessage( request, MESSAGE_ERROR_NO_STEP, SiteMessage.TYPE_ERROR );
            return null;
        }

        Form form = FormHome.findByPrimaryKey( _currentStep.getIdForm( ) );
        checkAuthentication( form, request );

        if ( form.isOneResponseByUser( ) || form.getMaxNumberResponse( ) != 0 )
        {
            Object lock = getLockOnForm( form );
            synchronized( lock )
            {
                checkIfUserResponseForm( form, request );
                checkNumberMaxResponseForm( form, request );
            }
        }

        String strTitleForm = I18nService.getLocalizedString( MESSAGE_STEP_TITLE, new String [ ] {
                form.getTitle( ), _currentStep.getTitle( )
        }, getLocale( request ) );
        String strPathForm = form.getTitle( );

        Map<String, Object> model = getModel( );
        if ( form.isActive( ) || bypassInactiveState( form, request ) )
        {
            if ( _breadcrumb == null )
            {
                _breadcrumb = SpringContextService.getBean( form.getBreadcrumbName( ) );
            }

            initFormResponseManager( request, form );
            if ( _formResponseManager.getFormResponse( ).isFromSave( ) )
            {
                _currentStep = _formResponseManager.getCurrentStep( );
                _stepDisplayTree = new StepDisplayTree( _currentStep.getId( ), _formResponseManager.getFormResponse( ) );

                Object [ ] args = {
                        _formResponseManager.getFormResponse( ).getUpdate( ),
                };

                model.put( FormsConstants.MARK_INFO, I18nService.getLocalizedString( MESSAGE_LOAD_BACKUP, args, getLocale( request ) ) );
            }

            if ( _stepDisplayTree == null || _currentStep.getId( ) != _stepDisplayTree.getStep( ).getId( ) )
            {
                _stepDisplayTree = new StepDisplayTree( _currentStep.getId( ), _formResponseManager.getFormResponse( ) );
                _formResponseManager.add( _currentStep );
            }

            if ( !_formResponseManager.getFormResponse( ).isFromSave( ) && !bypassInactiveState( form, request ) )
            {
                XPage accessControlPage = AccessControlService.getInstance( ).doExecuteAccessControl( request, form.getId( ), Form.RESOURCE_TYPE,
                        _formResponseManager );
                if ( accessControlPage != null )
                {
                    return accessControlPage;
                }
            }

            getFormStepModel( form, request, model );
        }
        else
        {
            if ( StringUtils.isNotEmpty( form.getUnavailableMessage( ) ) )
            {
                SiteMessageService.setCustomMessage( request, form.getUnavailableMessage( ), SiteMessage.TYPE_ERROR );
            }
            else
            {
                SiteMessageService.setMessage( request, MESSAGE_ERROR_INACTIVE_FORM, SiteMessage.TYPE_ERROR );
            }
        }

        XPage xPage = getXPage( TEMPLATE_VIEW_STEP, getLocale( request ), model );
        xPage.setTitle( strTitleForm );
        xPage.setPathLabel( strPathForm );

        return xPage;
    }

    private void initFormResponseManager( HttpServletRequest request, Form form )
    {
        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );

        if ( _formResponseManager == null )
        {
            if ( user != null )
            {
                _formResponseManager = _formService.createFormResponseManagerFromBackUp( form, user.getName( ) );
            }
            else
            {
                _formResponseManager = new FormResponseManager( form );
            }
        }
    }

    /**
     * Does the request contain parameters to bypass the inactive state of the form
     * 
     * @param form
     *            the forme
     * @param request
     *            thre request
     * @return <code>true</code> if the request contains valid bypass parameters, <code>false</code> otherwise
     */
    private boolean bypassInactiveState( Form form, HttpServletRequest request )
    {
        if ( _bInactiveStateBypassed )
        {
            return true;
        }
        String strTimestamp = request.getParameter( FormsConstants.PARAMETER_TIMESTAMP );
        String strToken = request.getParameter( FormsConstants.PARAMETER_TOKEN_BYPASS );
        if ( StringUtils.isBlank( strToken ) || !StringUtils.isNumeric( strTimestamp ) )
        {
            return false;
        }
        String refToken = FormsUtils.getInactiveBypassToken( form, strTimestamp );
        if ( !refToken.equals( strToken ) )
        {
            return false;
        }
        long now = new Date( ).getTime( );
        long timestampAge = now - Long.parseLong( strTimestamp );
        if ( timestampAge < 0 )
        {
            return false;
        }
        long lBypassDuration = AppPropertiesService.getPropertyLong( FormsConstants.PROPERTY_INACTIVE_BYPASS_DURATION_MILLISECONDS, 1000L * 60 * 30 ); // Half
                                                                                                                                                       // hour
                                                                                                                                                       // in
                                                                                                                                                       // milliseconds
        if ( timestampAge <= lBypassDuration )
        {
            _bInactiveStateBypassed = true;
            return true;
        }
        return false;
    }

    /**
     * @param form
     *            The form to display
     * @param request
     *            The Http request
     * @param model
     *            The model for XPage
     */
    private void getFormStepModel( Form form, HttpServletRequest request, Map<String, Object> model )
    {
        Map<String, Object> modelForStep = _breadcrumb.getModelForCurrentStep( request, _formResponseManager );
        modelForStep.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_SAVE_FORM_RESPONSE ) );
        _stepDisplayTree.addModel( modelForStep );

        if ( form.isCountResponses( ) )
        {
            form.setCurrentNumberResponse( FormHome.getNumberOfResponseForms( form.getId( ) ) );
        }
        if ( form.getLogo( ) != null )
        {
            form.setLogo( FileHome.findByPrimaryKey( form.getLogo( ).getIdFile( ) ) );
            form.getLogo( ).setPhysicalFile( PhysicalFileHome.findByPrimaryKey( form.getLogo( ).getPhysicalFile( ).getIdPhysicalFile( ) ) );
        }
        model.put( FormsConstants.MARK_FORM, form );
        model.put( STEP_HTML_MARKER,
                _stepDisplayTree.getCompositeHtml( request, _formResponseManager.findAllResponses( ), getLocale( request ), DisplayType.EDITION_FRONTOFFICE ) );
        model.put( FormsConstants.MARK_FORM_TOP_BREADCRUMB, _breadcrumb.getTopHtml( request, _formResponseManager ) );
        model.put( FormsConstants.MARK_FORM_BOTTOM_BREADCRUMB, _breadcrumb.getBottomHtml( request, _formResponseManager ) );
        if ( bypassInactiveState( form, request ) )
        {
            addWarning( MESSAGE_WARNING_INACTIVE_STATE_BYPASSED, getLocale( request ) );
        }
        fillCommons( model );
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
        boolean bSessionLost = isSessionLost( );
        try
        {
            findFormFrom( request );
        }
        catch( FormNotFoundException e )
        {
            return redirectView( request, VIEW_STEP );
        }
        if ( bSessionLost )
        {
            addWarning( MESSAGE_WARNING_LOST_SESSION, getLocale( request ) );
            return redirectView( request, VIEW_STEP );
        }
        try
        {
            fillResponseManagerWithResponses( request, false );
        }
        catch( QuestionValidationException exception )
        {
            return redirectView( request, VIEW_STEP );
        }
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
    @Action( value = ACTION_GO_TO_STEP )
    public XPage doGoToStep( HttpServletRequest request ) throws SiteMessageException
    {
        boolean bSessionLost = isSessionLost( );

        try
        {
            findFormFrom( request );
        }
        catch( FormNotFoundException e )
        {
            return redirectView( request, VIEW_STEP );
        }

        if ( bSessionLost )
        {
            addWarning( MESSAGE_WARNING_LOST_SESSION, getLocale( request ) );
            return redirectView( request, VIEW_STEP );
        }

        try
        {
            fillResponseManagerWithResponses( request, false );
        }
        catch( QuestionValidationException e )
        {
            // this cannot happen because the validation is not performed
        }

        int nIndexStep = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ACTION_GO_TO_STEP ), INCORRECT_ID );

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
            boolean bSessionLost = isSessionLost( );
            form = findFormFrom( request );
            if ( bSessionLost )
            {
                addWarning( MESSAGE_WARNING_LOST_SESSION, getLocale( request ) );
                return redirectView( request, VIEW_STEP );
            }
            fillResponseManagerWithResponses( request, true );
            boolean needValidation = form.isCaptchaStepFinal( );
            if ( isCaptchaKO( request, needValidation ) )
            {
                addWarning( MESSAGE_WARNING_CAPTCHA, getLocale( request ) );
                return redirectView( request, VIEW_STEP );
            }
        }
        catch( FormNotFoundException | QuestionValidationException exception )
        {
            return redirectView( request, VIEW_STEP );
        }

        return getFormResponseSummaryPage( request, form );
    }

    private XPage getFormResponseSummaryPage( HttpServletRequest request, Form form )
    {
        if ( form.isCountResponses( ) )
        {
            form.setCurrentNumberResponse( FormHome.getNumberOfResponseForms( form.getId( ) ) );
        }

        Map<String, Object> model = buildModelForSummary( form, request );
        model.put( FormsConstants.MARK_ID_FORM, form.getId( ) );
        model.put( FormsConstants.MARK_FORM, form );

        boolean displayCaptcha = _captchaSecurityService.isAvailable( ) && form.isCaptchaRecap( );
        model.put( MARK_DISPLAY_CAPTCHA, displayCaptcha );

        if ( displayCaptcha )
        {
            model.put( MARK_CAPTCHA, _captchaSecurityService.getHtmlCode( ) );
        }
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_SAVE_FORM_RESPONSE ) );
        String strTitleForm = I18nService.getLocalizedString( MESSAGE_SUMMARY_TITLE, new String [ ] {
                form.getTitle( )
        }, getLocale( request ) );

        XPage xPage = getXPage( TEMPLATE_VIEW_FORM_RESPONSE_SUMMARY, getLocale( request ), model );
        xPage.setTitle( strTitleForm );
        xPage.setPathLabel( form.getTitle( ) );

        return xPage;
    }

    /**
     * Builds the model for the summary page
     * 
     * @param request
     *            the request
     * @return the model
     */
    private Map<String, Object> buildModelForSummary( Form form, HttpServletRequest request )
    {
        Map<String, Object> mapFormResponseSummaryModel = getModel( );

        List<Step> listValidatedStep = _formResponseManager.getValidatedSteps( );

        List<String> listStepHtml = buildStepsHtml( request, listValidatedStep );
        mapFormResponseSummaryModel.put( MARK_LIST_SUMMARY_STEP_DISPLAY, listStepHtml );
        if ( bypassInactiveState( form, request ) )
        {
            addWarning( MESSAGE_WARNING_INACTIVE_STATE_BYPASSED, getLocale( request ) );
        }
        fillCommons( mapFormResponseSummaryModel );
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

            listFormDisplayTrees.add( stepDisplayTree.getCompositeHtml( request, _formResponseManager.findResponsesFor( step ), getLocale( request ),
                    DisplayType.READONLY_FRONTOFFICE ) );
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
     * @throws AccessDeniedException
     */
    @Action( value = ACTION_SAVE_FORM_RESPONSE )
    public synchronized XPage doSaveFormResponse( HttpServletRequest request ) throws SiteMessageException, UserNotSignedException, AccessDeniedException
    {
        // CSRF Token control
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_SAVE_FORM_RESPONSE ) )
        {
            throw new AccessDeniedException( MESSAGE_ERROR_TOKEN );
        }
        Form form = null;
        try
        {
            boolean bSessionLost = isSessionLost( );
            form = findFormFrom( request );
            if ( bSessionLost )
            {
                addWarning( MESSAGE_WARNING_LOST_SESSION, getLocale( request ) );
                return redirectView( request, VIEW_STEP );
            }
            fillResponseManagerWithResponses( request, true );
            boolean needValidation = form.isCaptchaStepFinal( );
            if ( isCaptchaKO( request, needValidation ) )
            {
                addWarning( MESSAGE_WARNING_CAPTCHA, getLocale( request ) );
                return redirectView( request, VIEW_STEP );
            }
        }
        catch( FormNotFoundException | QuestionValidationException exception )
        {
            return redirectView( request, VIEW_STEP );
        }

        return doSaveResponse( request, form );
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
     * @throws AccessDeniedException
     */
    @Action( value = ACTION_SAVE_FORM_RESPONSE_SUMMARY )
    public synchronized XPage doSaveFormResponseSummary( HttpServletRequest request ) throws SiteMessageException, UserNotSignedException, AccessDeniedException
    {
        // CSRF Token control
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_SAVE_FORM_RESPONSE ) )
        {
            throw new AccessDeniedException( MESSAGE_ERROR_TOKEN );
        }

        Form form = null;
        try
        {
            boolean bSessionLost = isSessionLost( );
            form = findFormFrom( request );
            if ( bSessionLost )
            {
                addWarning( MESSAGE_WARNING_LOST_SESSION, getLocale( request ) );
                return redirectView( request, VIEW_STEP );
            }
            boolean needValidation = form.isCaptchaRecap( );
            if ( isCaptchaKO( request, needValidation ) )
            {
                addWarning( MESSAGE_WARNING_CAPTCHA, getLocale( request ) );
                return getFormResponseSummaryPage( request, form );
            }
        }
        catch( FormNotFoundException exception )
        {
            return redirectView( request, VIEW_STEP );
        }

        return doSaveResponse( request, form );
    }

    private XPage doSaveResponse( HttpServletRequest request, Form form ) throws SiteMessageException
    {
        _currentStep = _formResponseManager.getCurrentStep( );
        if ( !_formResponseManager.validateFormResponses( ) )
        {
            _stepDisplayTree = new StepDisplayTree( _currentStep.getId( ), _formResponseManager.getFormResponse( ) );
            return redirectView( request, VIEW_STEP );
        }
        if ( !_currentStep.isFinal( ) )
        {

            _stepDisplayTree = new StepDisplayTree( _currentStep.getId( ), _formResponseManager.getFormResponse( ) );
            addError( MESSAGE_ERROR_STEP_NOT_FINAL, getLocale( request ) );
            return redirectView( request, VIEW_STEP );
        }

        saveFormResponse( form, request );
        Map<String, Object> model = getModel( );

        model.put( FormsConstants.PARAMETER_ID_FORM, form.getId( ) );
        if ( bypassInactiveState( form, request ) )
        {
            addWarning( MESSAGE_WARNING_INACTIVE_STATE_BYPASSED, getLocale( request ) );
        }

        FormMessage formMessage = FormMessageHome.findByForm( form.getId( ) );
        boolean bIsEndMessageDisplayed = formMessage.getEndMessageDisplay( );
        String strBackUrl = getBackUrl( form, bIsEndMessageDisplayed, _formResponseManager.getFormResponse( ).getId( ) );
        init( request );

        if ( formMessage.getEndMessageDisplay( ) )
        {
            model.put( FormsConstants.MARK_INFO, formMessage.getEndMessage( ) );
        }
        else
        {
            return redirect( request, strBackUrl );
        }

        model.put( FormsConstants.PARAMETER_BACK_URL, strBackUrl );
        fillCommons( model );

        XPage xPage = getXPage( TEMPLATE_FORM_SUBMITTED, getLocale( request ), model );
        xPage.setTitle( form.getTitle( ) );
        xPage.setPathLabel( form.getTitle( ) );
        return xPage;
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

            if ( nIdForm != INCORRECT_ID )
            {
                form = FormHome.findByPrimaryKey( nIdForm );

                if ( form == null )
                {
                    throw new FormNotFoundException( );
                }

                init( nIdForm );
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

        if ( !form.isActive( ) && !bypassInactiveState( form, request ) )
        {
            if ( StringUtils.isNotEmpty( form.getUnavailableMessage( ) ) )
            {
                SiteMessageService.setCustomMessage( request, form.getUnavailableMessage( ), SiteMessage.TYPE_ERROR );
            }
            else
            {
                SiteMessageService.setMessage( request, MESSAGE_ERROR_INACTIVE_FORM, SiteMessage.TYPE_ERROR );
            }
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
    private String getBackUrl( Form form, boolean bIsEndMessageDisplayed, int nIdFormResponse )
    {
        if ( StringUtils.isNotEmpty( form.getReturnUrl( ) ) )
        {
            return form.getReturnUrl( );
        }
        else
        {
            UrlItem url = null;

            if ( form.isAuthentificationNeeded( ) )
            {
            	url= bIsEndMessageDisplayed? new UrlItem( AppPathService.getPortalUrl( ) ):new UrlItem( "" );
                url.addParameter( MVCUtils.PARAMETER_PAGE, FormResponseXPage.XPAGE_NAME );
                url.addParameter( MVCUtils.PARAMETER_VIEW, FormResponseXPage.VIEW_FORM_RESPONSE );
                url.addParameter( FormsConstants.PARAMETER_ID_RESPONSE, nIdFormResponse );
                url.addParameter( FormsConstants.PARAMETER_ACTION_SUCCESS, "true" );     

            }
            else
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
     * @param bValidateQuestionStep
     *            valid question ton next step
     * 
     * @throws QuestionValidationException
     *             if there is at least one question not valid
     */
    private void fillResponseManagerWithResponses( HttpServletRequest request, boolean bValidateQuestionStep ) throws QuestionValidationException
    {
        List<Question> listQuestionStep = _stepDisplayTree.getQuestions( );

        boolean bValidStep = true;
        List<FormQuestionResponse> listResponsesTemp = new ArrayList<>( );

        String [ ] listConditionalQuestionsValues = request.getParameterValues( FormsConstants.PARAMETER_DISPLAYED_QUESTIONS );

        for ( Question question : listQuestionStep )
        {
            for ( int i = 0; i < listConditionalQuestionsValues.length; i++ )
            {
                String [ ] listQuestionId = listConditionalQuestionsValues [i].split( FormsConstants.SEPARATOR_UNDERSCORE );
                if ( StringUtils.isNotEmpty( listQuestionId [0] ) && Integer.parseInt( listQuestionId [0] ) == question.getId( )
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
            IEntryDataService entryDataService = EntryServiceManager.getInstance( ).getEntryDataService( question.getEntry( ).getEntryType( ) );
            if ( question.getEntry( ).isOnlyDisplayInBack( ) || entryDataService == null )
            {
                continue;
            }

            FormQuestionResponse formQuestionResponse = entryDataService.createResponseFromRequest( question, request,
                    question.isVisible( ) && bValidateQuestionStep );

            if ( formQuestionResponse.hasError( ) )
            {
                bValidStep = false;
            }

            listResponsesTemp.add( formQuestionResponse );
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
     * @throws AccessDeniedException
     */
    @Action( value = ACTION_SAVE_STEP )
    public XPage doSaveStep( HttpServletRequest request ) throws SiteMessageException, UserNotSignedException, AccessDeniedException
    {
        // CSRF Token control
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_SAVE_FORM_RESPONSE ) )
        {
            throw new AccessDeniedException( MESSAGE_ERROR_TOKEN );
        }

        try
        {
            boolean bSessionLost = isSessionLost( );
            Form form = findFormFrom( request );
            if ( bSessionLost )
            {
                addWarning( MESSAGE_WARNING_LOST_SESSION, getLocale( request ) );
                return redirectView( request, VIEW_STEP );
            }
            fillResponseManagerWithResponses( request, true );

            boolean needValidation = _currentStep.isInitial( ) && form.isCaptchaStepInitial( );
            if ( isCaptchaKO( request, needValidation ) )
            {
                addWarning( MESSAGE_WARNING_CAPTCHA, getLocale( request ) );
                return redirectView( request, VIEW_STEP );
            }
        }
        catch( FormNotFoundException | QuestionValidationException exception )
        {
            return redirectView( request, VIEW_STEP );
        }

        List<String> errorList = new ArrayList<>( );

        Step currentStep = getNextStep( errorList );
        _currentStep = currentStep != null ? currentStep : _currentStep;

        if ( currentStep == null )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR_CONTROL, new Object [ ] {
                    errorList.stream( ).collect( Collectors.joining( ) )
            }, null, null, null, SiteMessage.TYPE_ERROR, null, getViewFullUrl( VIEW_STEP ) );
        }
        return redirectView( request, VIEW_STEP );
    }

    private boolean isCaptchaKO( HttpServletRequest request, boolean needValidation )
    {
        if ( !needValidation )
        {
            return false;
        }
        return !_captchaSecurityService.validate( request );
    }

    /**
     * @return The next Step
     */
    private Step getNextStep( List<String> errorList )
    {
        List<Transition> listTransition = TransitionHome.getTransitionsListFromStep( _currentStep.getId( ) );

        for ( Transition transition : listTransition )
        {
            List<Control> listTransitionControl = ControlHome.getControlByControlTargetAndType( transition.getId( ), ControlType.TRANSITION );
            boolean controlsValidated = true;

            if ( listTransitionControl.isEmpty( ) )
            {
                return StepHome.findByPrimaryKey( transition.getNextStep( ) );
            }

            for ( Control transitionControl : listTransitionControl )
            {
                Question targetQuestion = QuestionHome.findByPrimaryKey( transitionControl.getListIdQuestion( ).iterator( ).next( ) );
                Step stepTarget = StepHome.findByPrimaryKey( targetQuestion.getIdStep( ) );
                List<FormQuestionResponse> listQuestionResponse = _formResponseManager.findResponsesFor( stepTarget ).stream( )
                        .filter( q -> transitionControl.getListIdQuestion( ).stream( ).anyMatch( t -> t.equals( q.getQuestion( ).getId( ) ) ) )
                        .collect( Collectors.toList( ) );

                IValidator validator = EntryServiceManager.getInstance( ).getValidator( transitionControl.getValidatorName( ) );
                if ( validator != null && !validator.validate( listQuestionResponse, transitionControl ) )
                {
                    controlsValidated = false;
                    errorList.add( transitionControl.getErrorMessage( ) );
                    break;
                }
            }

            if ( controlsValidated )
            {
                return StepHome.findByPrimaryKey( transition.getNextStep( ) );
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
     * @throws AccessDeniedException
     */
    @Action( value = ACTION_SAVE_FOR_BACKUP )
    public XPage doSaveForBackup( HttpServletRequest request ) throws SiteMessageException, UserNotSignedException, AccessDeniedException
    {
        // CSRF Token control
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_SAVE_FORM_RESPONSE ) )
        {
            throw new AccessDeniedException( MESSAGE_ERROR_TOKEN );
        }

        Form form = null;
        try
        {

            boolean bSessionLost = isSessionLost( );
            form = findFormFrom( request );
            if ( bSessionLost )
            {
                addWarning( MESSAGE_WARNING_LOST_SESSION, getLocale( request ) );
                return redirectView( request, VIEW_STEP );
            }
        }
        catch( FormNotFoundException exception )
        {
            return redirectView( request, VIEW_STEP );
        }

        checkAuthentication( form, request );

        try
        {
            fillResponseManagerWithResponses( request, false );
        }
        catch( QuestionValidationException e )
        {
            return redirectView( request, VIEW_STEP );
        }

        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );

        FormResponse formResponse = _formResponseManager.getFormResponse( );
        formResponse.setGuid( user.getName( ) );

        _formService.saveFormForBackup( formResponse );

        return redirectView( request, VIEW_STEP );
    }

    /**
     * Removes Backup
     * 
     * @param request
     *            The Http request
     * @return the XPage
     * 
     * @throws SiteMessageException
     *             Exception
     * @throws UserNotSignedException
     *             Exception
     * @throws AccessDeniedException
     */
    @Action( value = ACTION_RESET_BACKUP )
    public XPage doResetBackup( HttpServletRequest request ) throws SiteMessageException, UserNotSignedException, AccessDeniedException
    {
        // CSRF Token control
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_SAVE_FORM_RESPONSE ) )
        {
            throw new AccessDeniedException( MESSAGE_ERROR_TOKEN );
        }
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

        FormResponse formResponse = _formResponseManager.getFormResponse( );

        _formService.removeFormBackup( formResponse );

        init( form.getId( ) );

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
            boolean bSessionLost = isSessionLost( );
            findFormFrom( request );
            if ( bSessionLost )
            {
                addWarning( MESSAGE_WARNING_LOST_SESSION, getLocale( request ) );
                return redirectView( request, VIEW_STEP );
            }

            fillResponseManagerWithResponses( request, false );
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
            boolean bSessionLost = isSessionLost( );
            findFormFrom( request );
            if ( bSessionLost )
            {
                addWarning( MESSAGE_WARNING_LOST_SESSION, getLocale( request ) );
                return redirectView( request, VIEW_STEP );
            }
            fillResponseManagerWithResponses( request, false );
        }
        catch( FormNotFoundException | QuestionValidationException exception )
        {
            return redirectView( request, VIEW_STEP );
        }

        String strIterationInfo = request.getParameter( FormsConstants.PARAMETER_ACTION_PREFIX + ACTION_REMOVE_ITERATION );

        String [ ] arrayIterationInfo = strIterationInfo.split( FormsConstants.SEPARATOR_UNDERSCORE );

        int nIdGroupParent = Integer.parseInt( arrayIterationInfo [0] );
        int nIndexIteration = Integer.parseInt( arrayIterationInfo [1] );

        _stepDisplayTree.removeIteration( request, nIdGroupParent, nIndexIteration );

        return redirectView( request, VIEW_STEP );
    }

    /**
     * Synchronous upload process to fill the form.
     *
     * @param request
     *            the request
     * @return the XPage
     * @throws SiteMessageException
     *             if there is an error during the iteration
     * @throws UserNotSignedException
     *             if the user is not signed in
     */
    @Action( value = ACTION_UPLOAD )
    public XPage doSynchronousUploadDocument( HttpServletRequest request ) throws SiteMessageException, UserNotSignedException
    {

        boolean bSessionLost = isSessionLost( );
        if ( bSessionLost )
        {
            addWarning( MESSAGE_WARNING_LOST_SESSION, getLocale( request ) );
            return redirectView( request, VIEW_STEP );
        }

        String error = null;
        String strFlagAsynchrounous = request.getParameter( "action_" + ACTION_UPLOAD );
        boolean isUpload = strFlagAsynchrounous.startsWith( FormsAsynchronousUploadHandler.getUploadFormsSubmitPrefix( ) );
        Map<String, String [ ]> extraParams = new TreeMap<>( );
        extraParams.put( strFlagAsynchrounous, new String [ ] {
                strFlagAsynchrounous
        } );
        HttpServletRequest wrappedRequest = new SynchronousHttpServletRequestWrapper( (MultipartHttpServletRequest) request, extraParams );

        FormsAsynchronousUploadHandler handler = FormsAsynchronousUploadHandler.getHandler( );
        String strAttributeName = handler.getUploadAction( wrappedRequest ).substring( handler.getUploadSubmitPrefix( ).length( ) );

        if ( isUpload )
        {

            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            List<FileItem> fileUploaded = multipartRequest.getFileList( strAttributeName );
            error = handler.canUploadFiles( wrappedRequest, strAttributeName, fileUploaded, getLocale( request ) );
        }

        try
        {

            fillResponseManagerWithResponses( wrappedRequest, false );

        }
        catch( QuestionValidationException exception )
        {
            return redirectView( request, VIEW_STEP );
        }

        if ( isUpload )
        {

            List<Question> listQuestionStep = _stepDisplayTree.getQuestions( );
            List<FormQuestionResponse> listFormQuestionResponse = _formResponseManager.findResponsesFor( _currentStep );
            String strIdEntry = strAttributeName.split( IEntryTypeService.PREFIX_ATTRIBUTE ) [1].trim( );

            if ( error != null )
            {

                for ( FormQuestionResponse formResponse : listFormQuestionResponse )
                {
                    if ( formResponse.getQuestion( ).getIdEntry( ) == Integer.parseInt( strIdEntry ) )
                    {

                        List<Response> listResponse = formResponse.getEntryResponse( );
                        Response response = new Response( );
                        response.setEntry( formResponse.getQuestion( ).getEntry( ) );
                        GenericAttributeError genAttError = new GenericAttributeError( );
                        genAttError.setErrorMessage( error );
                        listResponse.add( response );
                        formResponse.setError( genAttError );

                        formResponse.setEntryResponse( listResponse );
                        break;
                    }

                }
                _formResponseManager.addResponses( listFormQuestionResponse );
            }

            // if the entry type is Automatic file Reading, we fill the form responses question with ocr values readed
            else
                if ( EntryTypeAutomaticFileReading.fill( listQuestionStep, listFormQuestionResponse, wrappedRequest ) )
                {

                    _formResponseManager.addResponses( listFormQuestionResponse );
                }
        }

        return redirectView( request, VIEW_STEP );
    }

    /**
     * save the response of form
     * 
     * @param form
     *            the form
     * @param request
     *            The Http request request
     * @throws SiteMessageException
     *             the exception
     */
    private void saveFormResponse( Form form, HttpServletRequest request ) throws SiteMessageException
    {
        if ( _bInactiveStateBypassed )
        {
            return; // form was in testing mode; do not save response
        }
        FormResponse formResponse = _formResponseManager.getFormResponse( );
        if ( form.isAuthentificationNeeded( ) )
        {
            LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );
            formResponse.setGuid( user.getName( ) );
        }

        if ( form.isOneResponseByUser( ) || form.getMaxNumberResponse( ) != 0 )
        {
            Object lock = getLockOnForm( form );
            synchronized( lock )
            {
                checkIfUserResponseForm( form, request );
                checkNumberMaxResponseForm( form, request );
                _formService.saveForm( form, formResponse );
                increaseNumberResponse( form );
            }
        }
        else
        {
            _formService.saveForm( form, formResponse );
        }
        AccessControlService.getInstance( ).cleanSessionData( request, form.getId( ), Form.RESOURCE_TYPE );

        _formService.processFormAction( form, formResponse );
    }

    private static synchronized Object getLockOnForm( Form form )
    {
        _lockFormId.putIfAbsent( form.getId( ), new Object( ) );
        return _lockFormId.get( form.getId( ) );
    }

    /**
     * check if form is reached the number max of response
     * 
     * @param form
     *            the form
     * @param request
     *            the request
     * @throws SiteMessageException
     *             the exception
     */
    private static void checkNumberMaxResponseForm( Form form, HttpServletRequest request ) throws SiteMessageException
    {
        if ( form.getMaxNumberResponse( ) != 0 )
        {
            int nNumberReponseForm = _responsePerFormMap.computeIfAbsent( form.getId( ), FormHome::getNumberOfResponseForms );
            if ( nNumberReponseForm >= form.getMaxNumberResponse( ) )
            {
                SiteMessageService.setMessage( request, MESSAGE_ERROR_NUMBER_MAX_RESPONSE_FORM, SiteMessage.TYPE_ERROR );
            }
        }
    }

    /**
     * Increase the number of response of the Form
     * 
     * @param form
     */
    private static void increaseNumberResponse( Form form )
    {
        if ( form.getMaxNumberResponse( ) != 0 )
        {
            int nNumberReponseForm = _responsePerFormMap.get( form.getId( ) );
            _responsePerFormMap.put( form.getId( ), nNumberReponseForm + 1 );
        }
    }

    /**
     * check if user can answer the form again
     * 
     * @param form
     *            the form
     * @param request
     *            the request
     * @throws SiteMessageException
     */
    private void checkIfUserResponseForm( Form form, HttpServletRequest request ) throws SiteMessageException
    {
        if ( form.isAuthentificationNeeded( ) && form.isOneResponseByUser( ) )
        {
            LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );
            int nLimitNumberResponse = FormHome.getNumberOfResponseFormByUser( form.getId( ), user.getName( ) );
            if ( nLimitNumberResponse >= NumberUtils.INTEGER_ONE )
            {
                SiteMessageService.setMessage( request, MESSAGE_ERROR_NOT_RESPONSE_AGAIN_FORM, SiteMessage.TYPE_ERROR );
            }
        }
    }

    /**
     * initialize the object.
     */
    private void init( HttpServletRequest request )
    {
        _formResponseManager = null;
        _currentStep = null;
        _stepDisplayTree = null;
        _breadcrumb = null;
        _bInactiveStateBypassed = false;
        FormsAsynchronousUploadHandler.getHandler( ).removeSessionFiles( request.getSession( ) );
    }

    /**
     * initialize the object
     * 
     * @param nIdForm
     *            id form
     */
    private void init( int nIdForm )
    {
        _currentStep = StepHome.getInitialStep( nIdForm );
        _formResponseManager = null;
        _stepDisplayTree = null;
        _breadcrumb = null;
    }

    /**
     * ckeck if the session has expired
     * 
     * 
     */
    private boolean isSessionLost( )
    {
        return ( _currentStep == null && _formResponseManager == null && _stepDisplayTree == null && _breadcrumb == null );
    }

}
