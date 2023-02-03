package fr.paris.lutece.plugins.forms.util;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.ControlType;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.business.Transition;
import fr.paris.lutece.plugins.forms.business.TransitionHome;
import fr.paris.lutece.plugins.forms.exception.QuestionValidationException;
import fr.paris.lutece.plugins.forms.service.EntryServiceManager;
import fr.paris.lutece.plugins.forms.validation.IValidator;
import fr.paris.lutece.plugins.forms.web.FormResponseData;
import fr.paris.lutece.plugins.forms.web.FormResponseManager;
import fr.paris.lutece.plugins.forms.web.FormResponseXPage;
import fr.paris.lutece.plugins.forms.web.StepDisplayTree;
import fr.paris.lutece.plugins.forms.web.entrytype.DisplayType;
import fr.paris.lutece.plugins.forms.web.entrytype.IEntryDataService;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.state.StateService;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFileHome;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.web.l10n.LocaleService;
import fr.paris.lutece.util.url.UrlItem;

public class FormsResponseUtils 
{
    private static ConcurrentMap<Integer, Object> _lockFormId = new ConcurrentHashMap<>( );
    private static Map<Integer, Integer> _responsePerFormMap = new HashMap<>( );

	private FormsResponseUtils( )
	{
		
	}
	
	 /**
     * Check that a given user is allowed to access for manage the formResponse
     * 
     * @param formResponse
     *           the formsResponse
     * @param user
     *            the user trying to access the resource
     * @return true if the user can access for manage the given resource, false otherwise
     */
    public static boolean isAuthorized( FormResponse formResponse, LuteceUser user )
    {
        Form form= FormHome.findByPrimaryKey( formResponse.getFormId( ));
        return isAuthorized(  formResponse,  user, form );
    }
    /**
     * Check that a given user is allowed to access for manage the formResponse
     * 
     * @param formResponse
     *           the formsResponse
     * @param user
     *            the user trying to access the resource
     * @param form
     * 				the form
     * @return true if the user can access for manage the given resource, false otherwise
     */
    public static boolean isAuthorized( FormResponse formResponse, LuteceUser user, Form form )
    {
        boolean userOwnsReponse = user != null && formResponse.getGuid( ) != null && user.getName( ).equals( formResponse.getGuid( ) );

        if( user != null && form != null && form.isAccessToResponsesByRole( ) && formResponse.getRole() != null  ) 
        {           
            	return isUserHasRole( user, formResponse.getRole( ) ) && formResponse.getFormId() == form.getId( );
        }
        
        return userOwnsReponse && ( form != null && !form.isAccessToResponsesByRole( )) && formResponse.getFormId() == form.getId( ); 
    }
    
   /**
    * Filter formResponse by access rights for LuteceUser
    * @param formResponseList the list of fromResponse objects
    * @param user the luteceUser
    * @return list of FormResponse objects filtered
    */
    public static List<FormResponse> filterFormResponseForLuteceUser( List<FormResponse> formResponseList, LuteceUser user )
    {

        List<FormResponse> formResponseResult= new ArrayList<>( );
        if( CollectionUtils.isNotEmpty( formResponseList) ) 
        {
        	List<Form> listForm=FormHome.getFormByPrimaryKeyList( formResponseList.stream()
        			.map( FormResponse::getFormId )
        			.collect(ArrayList<Integer>::new, ArrayList::add, ArrayList::addAll));
        	
        	List<Integer>  listIdFormByRole=listForm.stream()
        					 .filter(Form::isAccessToResponsesByRole )
        					 .map(Form::getId )
        					 .collect(ArrayList<Integer>::new, ArrayList::add, ArrayList::addAll);
        	
        	formResponseList.forEach(( FormResponse frmRsp ) -> {
        		if( isAuthorized( listIdFormByRole,  frmRsp,  user ))
        		{
        			formResponseResult.add( frmRsp );
        		}
        	});
        }
        return formResponseResult;
    }
    /**
     * Filter and build formResponseData by access rights for LuteceUser
     * @param formResponseList the list of fromResponse objects
     * @param user the luteceUser
     * @return list of FormResponseData objects builded
     */
    public static List<FormResponseData> filterFormResponseListForLuteceUser( List<FormResponse> formResponseList, LuteceUser user )
    {
        List<FormResponseData> formResponseResult= new ArrayList<>( );
        if( CollectionUtils.isNotEmpty( formResponseList) ) 
        {
        	List<Form> listForm=FormHome.getFormByPrimaryKeyList( formResponseList.stream()
        			.map( FormResponse::getFormId )
        			.collect(ArrayList<Integer>::new, ArrayList::add, ArrayList::addAll));
        	
        	List<Integer>  listIdFormByRole=listForm.stream()
        					 .filter(Form::isAccessToResponsesByRole )
        					 .map(Form::getId )
        					 .collect(ArrayList<Integer>::new, ArrayList::add, ArrayList::addAll);
        	
        	formResponseList.forEach(( FormResponse frmRsp ) -> {
        		if( isAuthorized( listIdFormByRole,  frmRsp,  user ))
        		{
        			formResponseResult.add( buildFormResponseData(  frmRsp, listForm.stream().filter(frm->frm.getId()==frmRsp.getFormId()).findAny( )) );
        		}
        	});
        }
        return formResponseResult;
    }
    /**
     * Get FormResponse list By Role And Guid
     * @param user the LuteceUser
     * @return FormResponse objects list
     */
    public static List<FormResponse> getFormResponseListByRoleAndGuid( LuteceUser user )
    {
        List<FormResponse> formResponseListByGuid = FormResponseHome.getFormResponseByGuid( user.getName( ) );
        String[] userRoles=  SecurityService.getInstance().getRolesByUser(user);
        userRoles= (userRoles!= null && userRoles.length != 0) ? userRoles:user.getRoles( ); 
		List<FormResponse> formResponseList= (userRoles==null)? new ArrayList<>( ):FormResponseHome.getFormResponseByRole(Arrays.asList( userRoles ));
		
        formResponseListByGuid.forEach( (FormResponse formResponse) ->{
			if(formResponseList.stream().noneMatch( response -> response.getId() == formResponse.getId() )) 
			{
				formResponseList.add( formResponse );
			}
		});	
		return formResponseList ;
    }
    
    /**
     * Build FormResponseData
     * @param formResponse the from response
     * @param form the form
     * @return the FormResponseData builded
     */
    public static FormResponseData buildFormResponseData( FormResponse formResponse, Optional<Form> form)
    {
    	    IStateService stateService = SpringContextService.getBean( StateService.BEAN_SERVICE );

	        FormResponseData data = new FormResponseData( );
	        data.setIdFormResponse( formResponse.getId( ) );
	        data.setDateUpdate( new Date( formResponse.getUpdate( ).getTime( ) ) );
	        if ( form.isPresent())
	        {
	        	data.setFormTitle( form.get( ).getTitle( ) );
		        
		        if ( stateService != null )
		        {
		            State formResponseState = stateService.findByResource( formResponse.getId( ), FormResponse.RESOURCE_TYPE, form.get().getIdWorkflow( ) );
		            if ( formResponseState != null )
		            {
		                data.setWorkflowState( formResponseState.getName( ) );
		            }
		        }
	        }
	    return data;
    }
    
  /**
   * Build next Step object
   * @param nIdCurrentStep the id current step
   * @param errorMessageList the error message list
   * @param formResponseManager the formResponseManager
   * @return Step object
   */
    public static Step getNextStep( int nIdCurrentStep, List<String> errorMessageList, FormResponseManager formResponseManager )
    {
        List<Transition> listTransition = TransitionHome.getTransitionsListFromStep( nIdCurrentStep );

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
                    errorMessageList.add( transitionControl.getErrorMessage( ) );
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
     * Populate form with Physical File logo and number of response
     * @param form the form object
     */
    public static void populateFormWithLogoAndNumberResponse( Form form )
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
    public static void fillResponseManagerWithResponses( HttpServletRequest request, boolean bValidateQuestionStep, FormResponseManager formResponseManager, List<Question> listQuestionStep, boolean displayInBO ) throws QuestionValidationException
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
            if ( question.getEntry( ).isOnlyDisplayInBack( ) )
            {
            	if ( displayInBO )
            	{
            		question.setIsVisible( true );
            	}
            	else
            	{
            		continue;
            	}
            }
            
            if ( entryDataService == null )
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
        	synchronized( FormsResponseUtils.getLockOnForm( form ) )
             {
        		int nNumberReponseForm = _responsePerFormMap.get( form.getId( ) );
        		_responsePerFormMap.put( form.getId( ), nNumberReponseForm + 1 );
              }
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
    public static boolean checkNumberMaxResponseForm( Form form )
    {
    	
	    if ( form.getMaxNumberResponse( ) != 0 )
	    {
	      	synchronized( FormsResponseUtils.getLockOnForm( form ) )
	        {
	      		return ( _responsePerFormMap.computeIfAbsent( form.getId( ), FormHome::getNumberOfResponseForms ) < form.getMaxNumberResponse( ) );
	        }
	    }
	    return true;
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
    public static boolean checkIfUserResponseForm( Form form, String strGuid )
    {
        if ( strGuid != null && form.isAuthentificationNeeded( ) && form.isOneResponseByUser( ) )
        {
        		return ( FormHome.getNumberOfResponseFormByUser( form.getId( ), strGuid ) < NumberUtils.INTEGER_ONE );
        }
        return true;
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
    public static List<String> buildStepsHtml( HttpServletRequest request, List<Step> listStep, FormResponseManager formResponseManager, boolean frontOffice )
    {
        List<String> listFormDisplayTrees = new ArrayList<>( );

        for ( Step step : listStep )
        {
            StepDisplayTree stepDisplayTree = new StepDisplayTree( step.getId( ), formResponseManager.getFormResponse( ) );

            listFormDisplayTrees.add( stepDisplayTree.getCompositeHtml( request, formResponseManager.findResponsesFor( step ), frontOffice ? getLocaleFO( request ) : AdminUserService.getLocale( request ),
            		frontOffice ? DisplayType.READONLY_FRONTOFFICE: DisplayType.READONLY_BACKOFFICE ) );
        }

        return listFormDisplayTrees;
    }
    
    public static Locale getLocaleFO( HttpServletRequest request )
    {
        return LocaleService.getContextUserLocale( request );
    }
  
    /**
     * Checks that the current user is associated to a given role
     * 
     * @param user
     *            The user
     * @param strRole
     *            The role name
     * @return Returns true if the user is associated to the role, otherwise false
     */
    private static boolean isUserHasRole( LuteceUser user, String strRole  )
    {
    	return user.getLuteceAuthenticationService( ).isUserInRole( user, null, strRole);
    }
    
    private static boolean isAuthorized( List<Integer> listIdFormByRole, FormResponse formResponse, LuteceUser user )
    {
    	return (listIdFormByRole.contains( formResponse.getFormId( )) && formResponse.getRole() != null && isUserHasRole(user, formResponse.getRole( ) )) 
				|| (!listIdFormByRole.contains( formResponse.getFormId( )) && user.getName().equals(formResponse.getGuid( )));
    }
    /**
     * Build FormsResponse file url. This url is only used for FO
     * @param nIdFormQuestionResponse the forms question response
     * @param nIdFile the file id
     * @return the url builded 
     */
    public static String buildFileUrl(int nIdFormQuestionResponse, int nIdFile) 
    {
    	UrlItem url = new UrlItem( getRootUrl( ) + FormsConstants.FORMS_FILE_URL_FO );
        url.addParameter( FormsConstants.PARAMETER_ID_FORM_QUESTION_REPONSE, nIdFormQuestionResponse );
        url.addParameter( FormsConstants.PARAMETER_ID_FILE, nIdFile );

        return url.getUrl( );
    }
    /**
     * Build FormsResponse FO url.
     * @param nIdFormQuestionResponse the forms question response
     * @param nIdFile the file id
     * @return the url builded 
     */
    public static String buildFormsResponseFOUrl(int nIdFormResponse) 
    {
    	UrlItem url = new UrlItem( getRootUrl( ) + AppPathService.getPortalUrl( ) );
        url.addParameter( MVCUtils.PARAMETER_PAGE, FormResponseXPage.XPAGE_NAME );
        url.addParameter( MVCUtils.PARAMETER_VIEW, FormResponseXPage.VIEW_FORM_RESPONSE );
        url.addParameter( FormsConstants.PARAMETER_ID_RESPONSE, nIdFormResponse );

        return url.getUrl( );
    }
    /**
     * Return the url of the Root of the webapp
     * 
     * @return strBase the webapp url
     */
    public static String getRootUrl( )
    {
        String strBaseUrl = AppPropertiesService.getProperty( FormsConstants.PROPERTY_BASE_URL );

        if ( StringUtils.isBlank( strBaseUrl ) )
        {
            strBaseUrl = AppPropertiesService.getProperty( FormsConstants.PROPERTY_PROD_URL );
        }

        if ( !strBaseUrl.endsWith( "/" ) )
        {
            strBaseUrl = strBaseUrl + "/";
        }
        strBaseUrl = StringUtils.isBlank( strBaseUrl ) ? "" : strBaseUrl;
        return strBaseUrl;
    }
    
}
