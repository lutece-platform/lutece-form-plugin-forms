package fr.paris.lutece.plugins.forms.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.ControlType;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.business.Transition;
import fr.paris.lutece.plugins.forms.business.TransitionHome;
import fr.paris.lutece.plugins.forms.exception.QuestionValidationException;
import fr.paris.lutece.plugins.forms.service.EntryServiceManager;
import fr.paris.lutece.plugins.forms.service.FormService;
import fr.paris.lutece.plugins.forms.validation.IValidator;
import fr.paris.lutece.plugins.forms.web.FormResponseManager;
import fr.paris.lutece.plugins.forms.web.StepDisplayTree;
import fr.paris.lutece.plugins.forms.web.breadcrumb.IBreadcrumb;
import fr.paris.lutece.plugins.forms.web.entrytype.DisplayType;
import fr.paris.lutece.plugins.forms.web.entrytype.IEntryDataService;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFileHome;
import fr.paris.lutece.portal.service.accesscontrol.AccessControlService;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.web.l10n.LocaleService;

public abstract class FormResponseUtils
{
	// Views
	public static final String VIEW_STEP = "stepView";
	public static final String VIEW_FORM_RESPONSE = "formResponseView";
	
	// Actions
	public static final String ACTION_SAVE_FORM_RESPONSE = "doSaveResponse";
	public static final String ACTION_SAVE_STEP = "doSaveStep";
	public static final String ACTION_RESET_BACKUP = "doResetBackup";
	public static final String ACTION_SAVE_FOR_BACKUP = "doSaveForBackup";
	public static final String ACTION_PREVIOUS_STEP = "doReturnStep";
	public static final String ACTION_GO_TO_STEP = "doGoToStep";
	public static final String ACTION_FORM_RESPONSE_SUMMARY = "formResponseSummary";
	public static final String ACTION_SAVE_FORM_RESPONSE_SUMMARY = "doSaveResponseSummary";
	
	// Steps
    public static final String STEP_HTML_MARKER = "stepContent";

	// Parameter
    public static final String PARAMETER_INIT = "true";
 
    // Constants
    public static final int INCORRECT_ID = -1;
    
    // Messages
    public static final String MESSAGE_STEP_TITLE = "forms.step.title";
    public static final String MESSAGE_ERROR_STEP_NOT_FINAL = "forms.error.step.isnot.final";
    public static final String MESSAGE_ERROR_NUMBER_MAX_RESPONSE_FORM = "forms.xpage.form.error.MaxResponse";
    public static final String MESSAGE_ERROR_NOT_RESPONSE_AGAIN_FORM = "forms.xpage.form.error.limitNumberResponse";
    public static final String MESSAGE_ERROR_INACTIVE_FORM = "forms.xpage.form.error.inactive";
    public static final String MESSAGE_LOAD_BACKUP = "forms.message.view.loadBackUp";
    public static final String MESSAGE_SUMMARY_TITLE = "forms.summary.title";
    
	// Other
    private static ConcurrentMap<Integer, Object> _lockFormId = new ConcurrentHashMap<>( );
    private static Map<Integer, Integer> _responsePerFormMap = new HashMap<>( );
    private static FormService _formService = SpringContextService.getBean( FormService.BEAN_NAME );
    
    private FormResponseUtils( )
    {
    }
    
    /**
     * @return The next Step
     */
    public static Step getNextStep( Step currentStep, List<String> errorList, FormResponseManager formResponseManager )
    {
        List<Transition> listTransition = TransitionHome.getTransitionsListFromStep( currentStep.getId( ) );

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
                List<FormQuestionResponse> listQuestionResponse = formResponseManager.findResponsesFor( stepTarget ).stream( )
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
    
    public static FormResponseManager initFormResponseManager( HttpServletRequest request, Form form, FormResponseManager formResponseManager, boolean frontOffice )
    {
    	if ( formResponseManager == null )
        {
    		if ( frontOffice )
        	{
        		LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );

                if ( user != null )
                {
                	formResponseManager = _formService.createFormResponseManagerFromBackUp( form, user.getName( ) );
                }
                else
                {
                	formResponseManager = new FormResponseManager( form );
                }
        	}
        	else
        	{
        		User admin = AdminUserService.getAdminUser( request );
        		
        		formResponseManager = _formService.createFormResponseManagerFromBackUp( form, admin.getEmail( ) );
        	}
        }
    	
    	return formResponseManager;
    }
    
    /**
     * @param form
     *            The form to display
     * @param request
     *            The Http request
     * @param model
     *            The model for view
     */
    public static Map<String, Object> getFormStepModel( Form form, HttpServletRequest request, Map<String, Object> model, IBreadcrumb breadcrumb, FormResponseManager formResponseManager, StepDisplayTree stepDisplayTree, boolean frontOffice )
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
        model.put( FormsConstants.MARK_FORM, form );
        model.put( FormsConstants.MARK_FORM_TOP_BREADCRUMB, breadcrumb.getTopHtml( request, formResponseManager ) );
        model.put( FormsConstants.MARK_FORM_BOTTOM_BREADCRUMB, breadcrumb.getBottomHtml( request, formResponseManager ) );
        model.put( STEP_HTML_MARKER,
                stepDisplayTree.getCompositeHtml( request, formResponseManager.findAllResponses( ), FormResponseUtils.getLocale( request ), frontOffice ? DisplayType.EDITION_FRONTOFFICE : DisplayType.EDITION_BACKOFFICE ) );
        
        return model;
    }

    /**
     * @param request
     *            The Http request
     * @param bValidateQuestionStep
     *            valid question ton next step
     * @return 
     * 
     * @throws QuestionValidationException
     *             if there is at least one question not valid
     */
    public static FormResponseManager fillResponseManagerWithResponses( HttpServletRequest request, boolean bValidateQuestionStep, FormResponseManager formResponseManager, List<Question> listQuestionStep ) throws QuestionValidationException
    {
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

        formResponseManager.addResponses( listResponsesTemp );

        if ( !bValidStep )
        {
            throw new QuestionValidationException( );
        }
        
        return formResponseManager;
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
    public static void saveFormResponseBO( Form form, HttpServletRequest request, FormResponse formResponse )
    {
		User adminUser = AdminUserService.getAdminUser( request );
    	formResponse.setAdmin( adminUser.getEmail( ) );

        if ( ( form.getMaxNumberResponse( ) != 0 ) )
        {
            Object lock = getLockOnForm( form );
            synchronized( lock )
            {
                int nNumberReponseForm = _responsePerFormMap.computeIfAbsent( form.getId( ), FormHome::getNumberOfResponseForms );
                if ( nNumberReponseForm >= form.getMaxNumberResponse( ) )
                {
                    return;
                }
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
    
    public static synchronized Object getLockOnForm( Form form )
    {
        _lockFormId.putIfAbsent( form.getId( ), new Object( ) );
        return _lockFormId.get( form.getId( ) );
    }
    
    /**
     * Increase the number of response of the Form
     * 
     * @param form
     */
    public static void increaseNumberResponse( Form form )
    {
        if ( form.getMaxNumberResponse( ) != 0 )
        {
            int nNumberReponseForm = _responsePerFormMap.get( form.getId( ) );
            _responsePerFormMap.put( form.getId( ), nNumberReponseForm + 1 );
        }
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
    public static void checkNumberMaxResponseForm( Form form, HttpServletRequest request ) throws SiteMessageException
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
     * check if user can answer the form again
     * 
     * @param form
     *            the form
     * @param request
     *            the request
     * @throws SiteMessageException
     */
    public static void checkIfUserResponseForm( Form form, HttpServletRequest request ) throws SiteMessageException
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
     * Builds the HTML for the specified list of steps
     * 
     * @param request
     *            The request
     * @param listStep
     *            The list of steps
     * @return the list of HTML
     */
    public static List<String> buildStepsHtml( HttpServletRequest request, List<Step> listStep, FormResponseManager formResponseManager)
    {
        List<String> listFormDisplayTrees = new ArrayList<>( );

        for ( Step step : listStep )
        {
            StepDisplayTree stepDisplayTree = new StepDisplayTree( step.getId( ), formResponseManager.getFormResponse( ) );

            listFormDisplayTrees.add( stepDisplayTree.getCompositeHtml( request, formResponseManager.findResponsesFor( step ), getLocale( request ),
                    DisplayType.READONLY_FRONTOFFICE ) );
        }

        return listFormDisplayTrees;
    }
    
    public static Locale getLocale( HttpServletRequest request )
    {
        return LocaleService.getContextUserLocale( request );
    }
}
