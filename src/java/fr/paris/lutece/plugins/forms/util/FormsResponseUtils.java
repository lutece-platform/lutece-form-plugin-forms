package fr.paris.lutece.plugins.forms.util;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.web.FormResponseData;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.state.StateService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

public class FormsResponseUtils 
{

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
        boolean userOwnsReponse = user != null && formResponse.getGuid( ) != null && user.getName( ).equals( formResponse.getGuid( ) );
        Form form= FormHome.findByPrimaryKey( formResponse.getFormId( ));

        if( user != null && form != null && form.isAccessToResponsesByRole( ) && formResponse.getRole() != null ) 
        {
            
            	return isUserHasRole( user, formResponse.getRole( ) ) ;
        }
        
        return userOwnsReponse && ( form != null && !form.isAccessToResponsesByRole( )); 
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
        List<FormResponse> formResponseListByGuid = FormResponseHome.getFormResponseUncompleteByGuid( user.getName( ) );
        String[] userRoles=  SecurityService.getInstance().getRolesByUser(user);
        userRoles= (userRoles!= null && userRoles.length != 0) ? userRoles:user.getRoles( ); 
		List<FormResponse> formResponseList= (userRoles==null)? new ArrayList<>( ):FormResponseHome.getFormResponseUncompleteByRole(Arrays.asList( userRoles ));
		
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
}
