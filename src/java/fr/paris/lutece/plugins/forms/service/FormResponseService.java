package fr.paris.lutece.plugins.forms.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

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

/**
 * FormResponseService
 *
 */
public class FormResponseService implements IFormResponseService
{
    public static final String BEAN_NAME = "forms.formResponseService";
    
    @Autowired( required = false )
    private StateService _stateService;

    @Override
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
    public static void saveFormResponse( FormResponse formResponse )
    {
    	FormResponseHome.update( formResponse );
    }
    
}
