package fr.paris.lutece.plugins.forms.dashboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponseStep;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.service.dashboard.IPublicDashboardComponent;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

public class PublicDashboardForms implements IPublicDashboardComponent {

	public static final String DASHBOARD_PROPERTIES_TITLE = "forms.publicdashboard.bean.title";
	private String strIdComponent = "forms.dashboardForms";
	private static final String TEMPLATE_MANAGE_FORMS = "/skin/plugins/publicdashboard/view_forms.html";
	private static final String MARK_DASHBOARD_FORMS = "formsdashboard";
	
	@Override
	public String getDashboardData( String user_id, Map<String,String> additionalParameters ) {
		Map<String, Object> model = new HashMap<String, Object>( );
		model.put( MARK_DASHBOARD_FORMS, searchDashboardFormsQuestionReponse( user_id ) );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_FORMS, I18nService.getDefaultLocale( ), model );

        return template.getHtml( );
	}

	@Override
	public String getComponentDescription( ) {
		return I18nService.getLocalizedString( DASHBOARD_PROPERTIES_TITLE, I18nService.getDefaultLocale( ) );
	}

	@Override
	public String getComponentId( ) {
		return strIdComponent;
	}
	
	/**
     * Search Question response published from a user.
     *
     * @param guid the guid
     * @return the list
     */
    private static Map<String, Map<String, String>> searchDashboardFormsQuestionReponse( String userId )
    {

    	
        Map<String, Map<String, String>> mapResponsesValuesByFormResponse = new HashMap<String, Map<String, String>>( );
        List<FormResponse> formResponseList = FormResponseHome.getFormResponseByGuid( userId );

        for ( FormResponse formRep : formResponseList )
        {
            List<FormResponseStep> lstStep = formRep.getSteps( );
            Map<String, String> mapResponsesValues = new HashMap<String, String>( );
            for ( FormResponseStep step : lstStep )
            {
                List<FormQuestionResponse> lstQr = step.getQuestions( );
                for ( FormQuestionResponse fqr : lstQr )
                {
                    if ( fqr.getQuestion( ).isPublished( ) )
                    {
                        List<Response> lstresp = fqr.getEntryResponse( );
                        for ( Response resp : lstresp )
                        {
                            mapResponsesValues.put( fqr.getQuestion( ).getTitle( ), resp.getResponseValue( ) );
                        }
                    }
                }
            }
            if ( !mapResponsesValues.isEmpty( ) )
            {
            	mapResponsesValuesByFormResponse.put( String.valueOf( formRep.getId() ), mapResponsesValues );
            }
        }
        
        return mapResponsesValuesByFormResponse;
		
    }

}
