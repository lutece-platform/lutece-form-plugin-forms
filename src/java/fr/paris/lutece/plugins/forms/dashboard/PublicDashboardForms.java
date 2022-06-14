package fr.paris.lutece.plugins.forms.dashboard;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponseStep;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.service.dashboard.IPublicDashboardComponent;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

public class PublicDashboardForms implements IPublicDashboardComponent {

	public static final String DASHBOARD_PROPERTIES_TITLE = "forms.publicdashboard.bean.title";
	public static final String DASHBOARD_PROPERTIES_FORMRESPONSE_PUBLISHED = "forms.publicdashboard.formresponse.isPublished";
	public static final String DASHBOARD_PROPERTIES_QUESTION_PUBLISHED = "forms.publicdashboard.question.isPublished";
	private String strIdComponent = "forms.dashboardForms";
	private static final String TEMPLATE_DASHBOARD_FORMS = "/skin/plugins/forms/publicdashboard_form.html";
	private static final String MARK_DASHBOARD_FORMS = "forms_publicdashboard";
	
	@Override
	public String getComponentDescription( Locale local ) {
		return I18nService.getLocalizedString( DASHBOARD_PROPERTIES_TITLE, local );
	}

	@Override
	public String getComponentId( ) {
		return strIdComponent;
	}
	
	@Override
	public String getDashboardTemplate()
	{
		return TEMPLATE_DASHBOARD_FORMS;
	}
	
	@Override
	public Map<String, Object> getDashboardModel( String user_id, Map<String,String[]> additionalParameters )
	{
		Map<String, Object> model = new HashMap<String, Object>( );
		model.put( MARK_DASHBOARD_FORMS, searchDashboardFormsQuestionReponse( user_id, AppPropertiesService.getPropertyBoolean(DASHBOARD_PROPERTIES_FORMRESPONSE_PUBLISHED, true), AppPropertiesService.getPropertyBoolean(DASHBOARD_PROPERTIES_QUESTION_PUBLISHED, true) ) );
		return model;
	}
	
	/**
	 * Search all the responses from a user.
	 * @param userId the userid
	 * @param published_formresponse_only request only the formResponse published
	 * @param published_questions_only request only the question published
	 * @return
	 */
    private static Map<String, Map<String, String>> searchDashboardFormsQuestionReponse( String userId, boolean published_formresponse_only, boolean published_questions_only )
    {

    	
        Map<String, Map<String, String>> mapResponsesValuesByFormResponse = new HashMap<String, Map<String, String>>( );
        List<FormResponse> formResponseList = FormResponseHome.getFormResponseByGuid( userId );

        for ( FormResponse formRep : formResponseList )
        {
        	
        	getFormResponse(mapResponsesValuesByFormResponse, formRep, published_formresponse_only, published_questions_only);
        }
        
        return mapResponsesValuesByFormResponse;
		
    }
    
    /**
     * search all the formResponse from a user.
     * @param mapResponsesValuesByFormResponse the map filled with all the responses from a user
     * @param formRep The formResponses from a user
     * @param published_formresponse_only request only the formResponse published
     * @param published_questions_only request only the question published
     */
    private static void getFormResponse( Map<String, Map<String, String>> mapResponsesValuesByFormResponse, FormResponse formRep, boolean published_formresponse_only, boolean published_questions_only )
    {
    	//check if formResponse is published or if there is not control of the status of the formResponse
    	if ( formRep.isPublished( ) || !published_formresponse_only )
        {
        	List<FormResponseStep> lstStep = formRep.getSteps( );
            Map<String, String> mapResponsesValues = new HashMap<String, String>( );
            for ( FormResponseStep step : lstStep )
            {
                List<FormQuestionResponse> lstQr = step.getQuestions( );
                for ( FormQuestionResponse fqr : lstQr )
                {
                	getResponse(mapResponsesValues, fqr, published_questions_only);
                }
            }
        
            if ( !mapResponsesValues.isEmpty( ) )
            {
            	mapResponsesValuesByFormResponse.put( String.valueOf( formRep.getId() ), mapResponsesValues );
            }
        }
    }
    
    /**
     * Search all the question response from a user.
     * @param mapResponsesValues the map filled with all the responses from a user
     * @param formQuestionResponse the QuestionResponse from the user
     * @param published_questions_only request only the question published
     */
    private static void getResponse( Map<String, String> mapResponsesValues, FormQuestionResponse formQuestionResponse, boolean published_questions_only )
    {
    	//check if question is published or if there is not control of the status of the question
    	if ( formQuestionResponse.getQuestion( ).isPublished( ) || !published_questions_only )
        {
            List<Response> lstresp = formQuestionResponse.getEntryResponse( );
            for ( Response resp : lstresp )
            {
                mapResponsesValues.put( formQuestionResponse.getQuestion( ).getTitle( ), resp.getResponseValue( ) );
            }
        }
    }

}
