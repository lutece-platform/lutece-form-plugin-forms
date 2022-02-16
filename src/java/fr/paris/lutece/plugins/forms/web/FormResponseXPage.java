package fr.paris.lutece.plugins.forms.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.service.upload.FormsAsynchronousUploadHandler;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;

/**
 * 
 * Controller for formResponse display
 *
 */
@Controller( xpageName = FormResponseXPage.XPAGE_NAME, pageTitleI18nKey = FormResponseXPage.MESSAGE_PAGE_TITLE, pagePathI18nKey = FormResponseXPage.MESSAGE_PATH )
public class FormResponseXPage extends MVCApplication
{
	protected static final String XPAGE_NAME = "formsResponse";

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
    private static final String MESSAGE_ERROR_TOKEN = "Invalid security token";

    // Views
    private static final String VIEW_FORM_RESPONSE = "formResponseView";
    
    // Actions
    private static final String ACTION_PROCESS_ACTION = "doProcessAction";
    private static final String ACTION_SAVE_TASK_FORM = "doSaveTaskForm";
    
	// Templates
    private static final String TEMPLATE_VIEW_FORM_RESPONSE = "/skin/plugins/forms/view_form_response.html";
    private static final String TEMPLATE_TASK_FORM_RESPONSE = "/skin/plugins/forms/task_form_workflow.html";
    
    // Marks 
    private static final String MARK_WORKFLOW_ACTION_LIST = "workflow_action_list";
    private static final String MARK_ID_FORM_RESPONSE = "id_form_response";
    private static final String MARK_ID_ACTION = "id_action";
    private static final String MARK_TASK_FORM = "tasks_form";
    
    // Parameters
    private static final String PARAMETER_ID_ACTION = "id_action";
    
    @View( value = VIEW_FORM_RESPONSE, defaultView=true )
    public XPage getFormResponseView( HttpServletRequest request ) throws SiteMessageException
    {
    	Locale locale = getLocale( request );
    	FormResponse formResponse = findFormResponseFrom(request);
    	
    	Collection<Action> actionsList = getActionsForUser( request, formResponse );
    	
    	Map<String, Object> model = getModel( );
		model.put( FormsConstants.MARK_FORM_RESPONSE, formResponse );
		model.put( MARK_WORKFLOW_ACTION_LIST, actionsList );
		model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_PROCESS_ACTION ) );
		
    	XPage xPage = getXPage( TEMPLATE_VIEW_FORM_RESPONSE, getLocale( request ), model );
        xPage.setTitle( I18nService.getLocalizedString( MESSAGE_FORM_RESPONSE_PAGETITLE, locale ) );
        xPage.setPathLabel( I18nService.getLocalizedString( MESSAGE_FORM_RESPONSE_PATHLABEL, locale ) );

        return xPage;
    }
    
    @fr.paris.lutece.portal.util.mvc.commons.annotations.Action( value = ACTION_PROCESS_ACTION )
    public XPage doProcessAction( HttpServletRequest request ) throws AccessDeniedException
    {
        // CSRF Token control
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_PROCESS_ACTION ) )
        {
            throw new AccessDeniedException( MESSAGE_ERROR_TOKEN );
        }
        // Get parameters from request
        int nIdFormResponse = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_RESPONSE ), NumberUtils.INTEGER_MINUS_ONE );
        int nIdAction = NumberUtils.toInt( request.getParameter( PARAMETER_ID_ACTION ), NumberUtils.INTEGER_MINUS_ONE );

        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );
        FormResponse formResponse = FormResponseHome.findByPrimaryKey( nIdFormResponse );
        
        if ( user == null || formResponse == null || formResponse.getGuid( ) == null || !formResponse.getGuid( ).equals( user.getName( ) ) )
        {
            return redirect( request, VIEW_FORM_RESPONSE, FormsConstants.PARAMETER_ID_RESPONSE, nIdFormResponse );
        }
        
        Locale locale = getLocale( request );
        WorkflowService workflowService = WorkflowService.getInstance( );
        if ( workflowService.isDisplayTasksForm( nIdAction, locale ) )
        {
            FormsAsynchronousUploadHandler.getHandler( ).removeSessionFiles( request.getSession( ) );

            String strHtmlTasksForm = WorkflowService.getInstance( ).getDisplayTasksForm( nIdFormResponse, FormResponse.RESOURCE_TYPE, nIdAction, request,
                    locale, null );
            
            Map<String, Object> model = new LinkedHashMap<>( );
            model.put( MARK_ID_FORM_RESPONSE, String.valueOf( nIdFormResponse ) );
            model.put( MARK_ID_ACTION, String.valueOf( nIdAction ) );
            model.put( MARK_TASK_FORM, strHtmlTasksForm );
            model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_SAVE_TASK_FORM ) );
            
            XPage xPage = getXPage( TEMPLATE_TASK_FORM_RESPONSE, locale, model );
            xPage.setTitle( I18nService.getLocalizedString( MESSAGE_FORM_RESPONSE_PAGETITLE, locale ) );
            xPage.setPathLabel( I18nService.getLocalizedString( MESSAGE_FORM_RESPONSE_PATHLABEL, locale ) );
            
            return xPage;
        }

        try
        {
            workflowService.doProcessAction( nIdFormResponse, FormResponse.RESOURCE_TYPE, nIdAction, formResponse.getFormId( ), request, locale,
                    false, user );
        }
        catch( AppException e )
        {
            AppLogService.error( "Error processing action for id response '" + nIdFormResponse + "' - cause : " + e.getMessage( ), e );
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
    @fr.paris.lutece.portal.util.mvc.commons.annotations.Action( value = ACTION_SAVE_TASK_FORM )
    public XPage doSaveTaskForm( HttpServletRequest request ) throws AccessDeniedException
    {
        int nIdFormResponse = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_RESPONSE ), NumberUtils.INTEGER_MINUS_ONE );
        int nIdAction = NumberUtils.toInt( request.getParameter( PARAMETER_ID_ACTION ), NumberUtils.INTEGER_MINUS_ONE );

        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );
        FormResponse formResponse = FormResponseHome.findByPrimaryKey( nIdFormResponse );
        
        if ( user == null || formResponse == null || formResponse.getGuid( ) == null || !formResponse.getGuid( ).equals( user.getName( ) ) )
        {
            return redirect( request, VIEW_FORM_RESPONSE, FormsConstants.PARAMETER_ID_RESPONSE, nIdFormResponse );
        }

        // CSRF Token control
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_SAVE_TASK_FORM ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }
        
        int nIdForm = formResponse.getFormId( );
        Locale locale = getLocale( request );
        WorkflowService workflowService = WorkflowService.getInstance( );
       
        if ( workflowService.canProcessAction( nIdFormResponse, FormResponse.RESOURCE_TYPE, nIdAction, nIdForm, request, false, user ) )
        {
            try
            {
                String strError = workflowService.doSaveTasksForm( nIdFormResponse, FormResponse.RESOURCE_TYPE, nIdAction, nIdForm, request, locale,
                        user );
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
            addError( MESSAGE_ACTION_ERROR, locale );
        }
        return redirect( request, VIEW_FORM_RESPONSE, FormsConstants.PARAMETER_ID_RESPONSE, nIdFormResponse );
    }
    	
	private Collection<Action> getActionsForUser( HttpServletRequest request, FormResponse formResponse )
	{
	    LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );
        if ( user != null && formResponse != null && formResponse.getGuid( ) != null && formResponse.getGuid( ).equals( user.getName( ) ) )
        {
            Form form = FormHome.findByPrimaryKey( formResponse.getFormId( ) );
            WorkflowService workflowService = WorkflowService.getInstance( );
            boolean workflowEnabled = workflowService.isAvailable( ) && ( form.getIdWorkflow( ) != FormsConstants.DEFAULT_ID_VALUE );

            if ( workflowEnabled )
            {
                return workflowService.getActions( formResponse.getId( ), FormResponse.RESOURCE_TYPE, form.getIdWorkflow( ), (User) user );
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
        else if ( !formResponse.isPublished() )
        {
            LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );
            boolean userOwnsReponse = user != null && user.getName( ).equals( formResponse.getGuid( ) );
            if ( !userOwnsReponse )
            {
                SiteMessageService.setMessage( request, MESSAGE_ERROR_NOT_PUBLISHED_FORM_RESPONSE, SiteMessage.TYPE_ERROR );
            }
        }
        
        return formResponse;
    }
}
