package fr.paris.lutece.plugins.forms.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.ControlMapping;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormMessageHome;
import fr.paris.lutece.plugins.forms.business.GroupHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.business.TransitionHome;
import fr.paris.lutece.plugins.forms.util.FormJsonData;

/**
 *  Json service to handle import/export
 */
public class FormJsonService
{
    public static final FormJsonService INSTANCE = new FormJsonService( );
    
    private FormJsonService( )
    {
    }
    
    public static FormJsonService getInstance( )
    {
        return INSTANCE;
    }
    
    /**
     * Export the form as an Json Object.
     * @return
     */
    public JSONObject jsonExportForm( int idForm )
    {
        FormJsonData jsonData = new FormJsonData( );
        jsonData.setForm( FormHome.findByPrimaryKey( idForm ) );
        
        List<Step> stepList = StepHome.getStepsListByForm( idForm );
        jsonData.setStepList( stepList );
        jsonData.setGroupList( GroupHome.getGroupsListByIdStepList( stepList.stream( ).map( Step::getId ).collect( Collectors.toList( ) ) ) );
        
        List<Control> controlList = new ArrayList<>( );
        List<Question> questionList = QuestionHome.getListQuestionByIdForm( idForm );
        for ( Question question : questionList )
        {
            controlList.addAll( ControlHome.getControlByQuestion( question.getId( ) ) );
        }
        List<ControlMapping> controlMappingList = new ArrayList<>( );
        for ( Control control : controlList )
        {
            controlMappingList.addAll( ControlHome.getControlMappingListByIdControl( control.getId( ) ) );
        }
        jsonData.setQuestionList( questionList );
        jsonData.setControlList( controlList );
        jsonData.setControlMappingList( controlMappingList );
        
        jsonData.setTransitionList( TransitionHome.getTransitionsListFromForm( idForm ) );
        jsonData.setFormMessage( FormMessageHome.findByForm( idForm ) );
        jsonData.setFormDisplayList( FormDisplayHome.getFormDisplayByForm( idForm ) );
        
        return new JSONObject( jsonData );
    }
}
