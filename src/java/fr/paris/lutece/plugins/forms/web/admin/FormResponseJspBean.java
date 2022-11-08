package fr.paris.lutece.plugins.forms.web.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormMessage;
import fr.paris.lutece.plugins.forms.business.FormMessageHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.exception.FormNotFoundException;
import fr.paris.lutece.plugins.forms.exception.QuestionValidationException;
import fr.paris.lutece.plugins.forms.service.FormService;
import fr.paris.lutece.plugins.forms.service.upload.FormsAsynchronousUploadHandler;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.util.FormResponseUtils;
import fr.paris.lutece.plugins.forms.web.FormResponseManager;
import fr.paris.lutece.plugins.forms.web.StepDisplayTree;
import fr.paris.lutece.plugins.forms.web.breadcrumb.IBreadcrumb;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.url.UrlItem;

@Controller( controllerJsp = "ManageFormResponse.jsp", controllerPath = "jsp/admin/plugins/forms/", right = "FORM_RESPONSE_MANAGEMENT" )
public class FormResponseJspBean extends AbstractJspBean
{
	private static final long serialVersionUID = 1L;
	
	// Templates
	private static final String TEMPLATE_VIEW_STEP = "/admin/plugins/forms/step_view.html";
	private static final String TEMPLATE_FORM_SUBMITTED = "/admin/plugins/forms/form_submitted_view.html";
	private static final String TEMPLATE_VIEW_FORM_RESPONSE_SUMMARY = "/admin/plugins/forms/form_response_summary.html";

	// Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_CREATE_FORM_RESPONSE = "forms.create_form_response.pageTitle";
    
    // Markers
    private static final String MARK_FORM_TITLE = "formTitle";
    private static final String MARK_LIST_SUMMARY_STEP_DISPLAY = "list_summary_step_display";
    
    // Other
    private Controller _controller = getClass( ).getAnnotation( Controller.class );
    private FormService _formService = SpringContextService.getBean( FormService.BEAN_NAME );
    private FormResponseManager _formResponseManager;
    private Step _currentStep;
    private StepDisplayTree _stepDisplayTree;
    private IBreadcrumb _breadcrumb;
    private final Boolean _frontOffice = false;
	
    /**
     * 
     * @param request
     *            The Http request
     * @return the String
     * 
     */
    @View( value = FormResponseUtils.VIEW_STEP )
    public String getStepView( HttpServletRequest request )
    {
    	String paramInit = request.getParameter( FormsConstants.PARAMETER_INIT );
        if ( FormResponseUtils.PARAMETER_INIT.equals( paramInit ) )
        {
            init( request );
        }

        int nIdForm = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ), FormsConstants.DEFAULT_ID_VALUE );

        if ( nIdForm != FormsConstants.DEFAULT_ID_VALUE && ( _currentStep == null || nIdForm != _currentStep.getIdForm( ) ) )
        {
            init( nIdForm );
        }

        Form form = FormHome.findByPrimaryKey( _currentStep.getIdForm( ) );

        if (  form.getMaxNumberResponse( ) != 0 )
        {
            Object lock = FormResponseUtils.getLockOnForm( form );
            synchronized( lock )
            {
            	if ( form.getMaxNumberResponse( ) != 0 )
                {
            		Map<Integer, Integer> responsePerFormMap = new HashMap<>( );
                    int nNumberReponseForm = responsePerFormMap.computeIfAbsent( form.getId( ), FormHome::getNumberOfResponseForms );
                    if ( nNumberReponseForm >= form.getMaxNumberResponse( ) )
                    {
                    	return I18nService.getLocalizedString( FormResponseUtils.MESSAGE_ERROR_NUMBER_MAX_RESPONSE_FORM, getLocale( ) );
                    }
                }
            }
        }
        
        Map<String, Object> model = getModel( );
        
        if ( form.isActive( ) )
        {
            if ( _breadcrumb == null )
            {
                _breadcrumb = SpringContextService.getBean( form.getBreadcrumbName( ) );
            }

            _formResponseManager = FormResponseUtils.initFormResponseManager( request, form, _formResponseManager, _frontOffice );
            if ( _formResponseManager.getFormResponse( ).isFromSave( ) )
            {
                _currentStep = _formResponseManager.getCurrentStep( );
                _stepDisplayTree = new StepDisplayTree( _currentStep.getId( ), _formResponseManager.getFormResponse( ) );

                Object [ ] args = {
                        _formResponseManager.getFormResponse( ).getUpdate( ),
                };

                model.put( FormsConstants.MARK_INFO, I18nService.getLocalizedString( FormResponseUtils.MESSAGE_LOAD_BACKUP, args, FormResponseUtils.getLocale( request ) ) );
            }

            if ( _stepDisplayTree == null || _currentStep.getId( ) != _stepDisplayTree.getStep( ).getId( ) )
            {
                _stepDisplayTree = new StepDisplayTree( _currentStep.getId( ), _formResponseManager.getFormResponse( ) );
                _formResponseManager.add( _currentStep );
            }
            
            Map<String, Object> modelForStep = _breadcrumb.getModelForCurrentStep( request, _formResponseManager );
            modelForStep.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, FormResponseUtils.ACTION_SAVE_FORM_RESPONSE ) );
            _stepDisplayTree.addModel( modelForStep );

            model = FormResponseUtils.getFormStepModel( form, request, model, _breadcrumb, _formResponseManager, _stepDisplayTree, _frontOffice );
            fillCommons( model );
            
            String strTitleForm = I18nService.getLocalizedString( FormResponseUtils.MESSAGE_STEP_TITLE, new String [ ] {
                    form.getTitle( ), _currentStep.getTitle( )
            }, getLocale( ) );
            
            model.put( MARK_FORM_TITLE, strTitleForm );
        }
        else
        {
        	return I18nService.getLocalizedString( FormResponseUtils.MESSAGE_ERROR_INACTIVE_FORM, getLocale( ) );
        }
        
        return getPage( PROPERTY_PAGE_TITLE_CREATE_FORM_RESPONSE, TEMPLATE_VIEW_STEP, model );
    }
    
    /**
     * 
     * @param request
     *            The Http request
     * @return the view
     */
    @Action( value = FormResponseUtils.ACTION_SAVE_STEP )
    public String doSaveStep( HttpServletRequest request )
    {
    	try
        {	
    		findFormFrom( request );
            _formResponseManager = FormResponseUtils.fillResponseManagerWithResponses( request, true, _formResponseManager, _stepDisplayTree.getQuestions( ) );
        }
        catch( FormNotFoundException | QuestionValidationException e )
        {
        	AppLogService.error( e.getMessage( ), e );
        	return getStepView(  request );
        }

        List<String> errorList = new ArrayList<>( );

        Step currentStep = FormResponseUtils.getNextStep( _currentStep, errorList, _formResponseManager );
        _currentStep = currentStep != null ? currentStep : _currentStep;

        return getStepView( request );
    }
    
    /**
     * 
     * @param request
     *            The Http request
     * @return the view
     */
    @Action( value = FormResponseUtils.ACTION_SAVE_FORM_RESPONSE )
    public String doSaveFormResponse( HttpServletRequest request )
    {
        Form form = null;
        try
        {
        	form = findFormFrom( request );
            _formResponseManager = FormResponseUtils.fillResponseManagerWithResponses( request, true, _formResponseManager, _stepDisplayTree.getQuestions( ) );
        }
        catch( FormNotFoundException | QuestionValidationException exception )
        {
        	return getStepView(  request );
        }

        return doSaveResponse( request, form );
    }
    
    /**
     * 
     * @param request
     *            The Http request
     * @return the View
     */
    @Action( value = FormResponseUtils.ACTION_PREVIOUS_STEP )
    public String doReturnStep( HttpServletRequest request )
    {
        try
        {
        	findFormFrom( request );
            FormResponseUtils.fillResponseManagerWithResponses( request, false, _formResponseManager, _stepDisplayTree.getQuestions( ) );
        }
        catch( FormNotFoundException | QuestionValidationException exception )
        {
            return getStepView(  request );
        }
        _formResponseManager.popStep( );

        _currentStep = _formResponseManager.getCurrentStep( );

        _stepDisplayTree = new StepDisplayTree( _currentStep.getId( ), _formResponseManager.getFormResponse( ) );

        return  getStepView( request );
    }
    
    /**
     * 
     * @param request
     *            The Http request
     * @return the View
     */
    @Action( value = FormResponseUtils.ACTION_GO_TO_STEP )
    public String doGoToStep( HttpServletRequest request )
    {
        try
        {
        	findFormFrom( request );
        	FormResponseUtils.fillResponseManagerWithResponses( request, false, _formResponseManager, _stepDisplayTree.getQuestions( ) );
        }
        catch( FormNotFoundException | QuestionValidationException e )
        {
            return getStepView( request );
        }

        int nIndexStep = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ACTION_GO_TO_STEP ), FormResponseUtils.INCORRECT_ID );

        _currentStep = _formResponseManager.goTo( nIndexStep );

        _stepDisplayTree = new StepDisplayTree( _currentStep.getId( ), _formResponseManager.getFormResponse( ) );

        return getStepView(  request );
    }
    
    /**
     * 
     * @param request
     *            The Http request
     * @return the View
     */
    @Action( value = FormResponseUtils.ACTION_SAVE_FOR_BACKUP )
    public String doSaveForBackup( HttpServletRequest request )
    {
        try
        {
        	findFormFrom( request );
            _formResponseManager = FormResponseUtils.fillResponseManagerWithResponses( request, true, _formResponseManager, _stepDisplayTree.getQuestions( ) );
        }
        catch( FormNotFoundException | QuestionValidationException exception )
        {
        	return getStepView(  request );
        }

        User admin = AdminUserService.getAdminUser( request );

        FormResponse formResponse = _formResponseManager.getFormResponse( );
        formResponse.setGuid( admin.getEmail( ) );

        _formService.saveFormForBackup( formResponse );

        return getStepView(  request );
    }
    
    /**
     * Removes Backup
     * 
     * @param request
     *            The Http request
     * @return the View
     */
    @Action( value = FormResponseUtils.ACTION_RESET_BACKUP )
    public String doResetBackup( HttpServletRequest request )
    {
        Form form = null;
        
        try
        {
			form = findFormFrom( request );
		}
        catch (FormNotFoundException e)
        {
        	return getStepView(  request );
		}

        FormResponse formResponse = _formResponseManager.getFormResponse( );

        _formService.removeFormBackup( formResponse );

        if ( form != null )
        {
        	init( form.getId( ) );
        }
        
        return getStepView(  request );
    }
    
    /**
     * Gives the summary page
     * 
     * @param request
     *            The request
     * @return the summary page
     */
    @Action( value = FormResponseUtils.ACTION_FORM_RESPONSE_SUMMARY )
    public String doFormResponseSummary( HttpServletRequest request )
    {
        Form form = null;
        
        try 
        {
			form = findFormFrom( request );
			FormResponseUtils.fillResponseManagerWithResponses( request, true, _formResponseManager, _stepDisplayTree.getQuestions( ) );
		} 
        catch ( FormNotFoundException | QuestionValidationException e ) 
        {
			return getStepView( request );
		}

        if ( form.isCountResponses( ) )
        {
            form.setCurrentNumberResponse( FormHome.getNumberOfResponseForms( form.getId( ) ) );
        }

        Map<String, Object> model = buildModelForSummary( request );
        model.put( FormsConstants.MARK_ID_FORM, form.getId( ) );
        model.put( FormsConstants.MARK_FORM, form );

        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, FormResponseUtils.ACTION_SAVE_FORM_RESPONSE ) );
        
        return getPage( form.getTitle( ), TEMPLATE_VIEW_FORM_RESPONSE_SUMMARY, model );
    }
    
    /**
     * 
     * @param request
     *            The Http request
     * @return the view
     */
    @Action( value = FormResponseUtils.ACTION_SAVE_FORM_RESPONSE_SUMMARY )
    public synchronized String doSaveFormResponseSummary( HttpServletRequest request )
    {
        Form form = null;
        
        try 
        {
			form = findFormFrom( request );
		} 
        catch (FormNotFoundException e1)
        {
			return getStepView( request );
		}
        
    	_currentStep = _formResponseManager.getCurrentStep( );

        if ( !_formResponseManager.validateFormResponses( ) )
        {
            _stepDisplayTree = new StepDisplayTree( _currentStep.getId( ), _formResponseManager.getFormResponse( ) );
            return getStepView(  request );
        }
        if ( !_currentStep.isFinal( ) )
        {
            _stepDisplayTree = new StepDisplayTree( _currentStep.getId( ), _formResponseManager.getFormResponse( ) );
            addError( FormResponseUtils.MESSAGE_ERROR_STEP_NOT_FINAL, FormResponseUtils.getLocale( request ) );
            return getStepView(  request );
        }
        
        FormResponseUtils.saveFormResponseBO( form, request, _formResponseManager.getFormResponse( ) );

        Map<String, Object> model = getModel( );

        model.put( FormsConstants.PARAMETER_ID_FORM, form.getId( ) );

        FormMessage formMessage = FormMessageHome.findByForm( form.getId( ) );
        boolean bIsEndMessageDisplayed = formMessage.getEndMessageDisplay( );
        String strBackUrl = getBackUrl( form, bIsEndMessageDisplayed );
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
        
        return getPage( form.getTitle( ), TEMPLATE_FORM_SUBMITTED, model );
    }
    
    /**
     * initialize the object.
     */
    private void init( HttpServletRequest request)
    {
        _formResponseManager = null;
        _currentStep = null;
        _stepDisplayTree = null;
        _breadcrumb = null;
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
    
    private String doSaveResponse( HttpServletRequest request, Form form )
    {
        _currentStep = _formResponseManager.getCurrentStep( );
        if ( !_formResponseManager.validateFormResponses( ) )
        {
            _stepDisplayTree = new StepDisplayTree( _currentStep.getId( ), _formResponseManager.getFormResponse( ) );
            return getStepView(  request );
        }
        if ( !_currentStep.isFinal( ) )
        {
            _stepDisplayTree = new StepDisplayTree( _currentStep.getId( ), _formResponseManager.getFormResponse( ) );
            addError( FormResponseUtils.MESSAGE_ERROR_STEP_NOT_FINAL, getLocale( ) );
            return getStepView(  request );
        }
        
        FormResponseUtils.saveFormResponseBO( form, request, _formResponseManager.getFormResponse( ) );
        
        Map<String, Object> model = getModel( );

        model.put( FormsConstants.PARAMETER_ID_FORM, form.getId( ) );

        FormMessage formMessage = FormMessageHome.findByForm( form.getId( ) );
        boolean bIsEndMessageDisplayed = formMessage.getEndMessageDisplay( );
        String strBackUrl = getBackUrl( form, bIsEndMessageDisplayed );
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

        return getPage( form.getTitle( ), TEMPLATE_FORM_SUBMITTED, model );
    }
    
    /**
     * Builds the model for the summary page
     * 
     * @param request
     *            the request
     * @return the model
     */
    private Map<String, Object> buildModelForSummary( HttpServletRequest request )
    {
        Map<String, Object> mapFormResponseSummaryModel = getModel( );

        List<Step> listValidatedStep = _formResponseManager.getValidatedSteps( );

        List<String> listStepHtml = FormResponseUtils.buildStepsHtml( request, listValidatedStep, _formResponseManager );
        mapFormResponseSummaryModel.put( MARK_LIST_SUMMARY_STEP_DISPLAY, listStepHtml );
        fillCommons( mapFormResponseSummaryModel );
        return mapFormResponseSummaryModel;
    }
    
    /**
     * Finds the form from the specified request
     * 
     * @param request
     *            the request
     * @return the found form, or {@code null} if not found
     * @throws FormNotFoundException
     *             if the form is not found
     */
    private Form findFormFrom( HttpServletRequest request ) throws FormNotFoundException
    {
        Form form = null;

        if ( _currentStep == null )
        {
            int nIdForm = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ), FormResponseUtils.INCORRECT_ID );

            if ( nIdForm != FormResponseUtils.INCORRECT_ID )
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
                url = new UrlItem( _controller.controllerPath( ) + getViewUrl( FormResponseUtils.VIEW_STEP ) );
            }
            else
            {
                url = new UrlItem( getViewUrl( FormResponseUtils.VIEW_STEP ) );
            }
            
            url.addParameter( FormsConstants.PARAMETER_ID_FORM, form.getId( ) );

            return url.getUrl( );
        }
    }
}
