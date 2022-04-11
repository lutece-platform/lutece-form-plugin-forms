package fr.paris.lutece.plugins.forms.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponseStepHome;
import fr.paris.lutece.plugins.forms.web.FormResponseData;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.service.state.StateService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.util.sql.TransactionManager;

/**
 * FormResponseService
 *
 */
public class FormResponseService
{
    private FormService _formService;
    
    private StateService _stateService;
    
    private static FormResponseService _formResponseService;
    
    private FormResponseService()
    {
    	_stateService = SpringContextService.getBean(StateService.BEAN_SERVICE );
    	_formService = SpringContextService.getBean( FormService.BEAN_NAME );
    }
    
    public static FormResponseService getInstance()
    {
    	if ( _formResponseService == null)
    	{
    		_formResponseService = new FormResponseService();
    	}
    	
    	return _formResponseService;
    }

    public List<FormResponseData> getFormResponseListForUser( LuteceUser user )
    {
        List<FormResponse> formResponseList = FormResponseHome.getFormResponseByGuid( user.getName( ) );
        List<FormResponseData> dataList = new ArrayList<>( formResponseList.size( ) );
        for ( FormResponse formResponse : formResponseList )
        {
            FormResponseData data = new FormResponseData( );
            data.setIdFormResponse( formResponse.getId( ) );
            data.setDateUpdate( new Date( formResponse.getUpdate( ).getTime( ) ) );
            Form form = FormHome.findByPrimaryKey( formResponse.getFormId( ) );
            if ( form == null )
            {
                continue;
            }
            
            data.setFormTitle( form.getTitle( ) );
            if ( _stateService != null )
            {
                State formResponseState = _stateService.findByResource( formResponse.getId( ), FormResponse.RESOURCE_TYPE, form.getIdWorkflow( ) );
                if ( formResponseState != null )
                {
                    data.setWorkflowState( formResponseState.getName( ) );
                }
            }
            dataList.add( data );
        }
        return dataList;
    }
    
    /**
     * Saves the form response
     * 
     * @param formResponse
     *            the form response to save
     */
    public void saveFormResponse( FormResponse formResponse )
    {
    	FormResponseHome.update( formResponse );
    }
    
    public void deleteFormResponse(FormResponse formResponse)
    {
    	TransactionManager.beginTransaction( FormsPlugin.getPlugin( ) );
    	try
        {
    		for ( FormQuestionResponse formQuestionResponse : FormQuestionResponseHome.getFormQuestionResponseListByFormResponse( formResponse.getId() ) )
            {
                FormQuestionResponseHome.remove( formQuestionResponse );
            }

            FormResponseStepHome.removeByFormResponse( formResponse.getId() );

            FormResponseHome.remove( formResponse.getId() );
            
            List<Integer> listIdResource = new ArrayList<Integer>();
            listIdResource.add( formResponse.getId() );
            
            WorkflowService.getInstance().doRemoveWorkFlowResource( formResponse.getId(), FormResponse.RESOURCE_TYPE );
            
            TransactionManager.commitTransaction( FormsPlugin.getPlugin( ) );
        }
    	catch( Exception e )
        {
            TransactionManager.rollBack( FormsPlugin.getPlugin( ) );
            throw new AppException( e.getMessage( ), e );
        }

        _formService.fireFormResponseEventDelete( formResponse );
    }
    
}
