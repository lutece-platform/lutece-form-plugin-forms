/*
 * Copyright (c) 2002-2025, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.forms.dashboard;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponseStep;
import fr.paris.lutece.portal.service.dashboard.IPublicDashboardComponent;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PublicDashboardForms implements IPublicDashboardComponent {

	public static final String DASHBOARD_PROPERTIES_TITLE = "forms.publicdashboard.bean.title";
	public static final String DASHBOARD_PROPERTIES_FORMRESPONSE_PUBLISHED = "forms.publicdashboard.formresponse.isPublished";
	public static final String DASHBOARD_PROPERTIES_QUESTION_PUBLISHED = "forms.publicdashboard.question.isPublished";
	private String strIdComponent = "forms.dashboardForms";
	private static final String TEMPLATE_DASHBOARD_FORMS = "/skin/plugins/forms/publicdashboard_form.html";
	private static final String MARK_LIST_FORMRESPONSE = "list_formsresponse";
	
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
		model.put( MARK_LIST_FORMRESPONSE, searchDashboardFormReponsesFromUser( user_id, AppPropertiesService.getPropertyBoolean(DASHBOARD_PROPERTIES_FORMRESPONSE_PUBLISHED, true), AppPropertiesService.getPropertyBoolean(DASHBOARD_PROPERTIES_QUESTION_PUBLISHED, true) ) );
		return model;
	}
	
	/**
	 * Search all the formResponse published from a user.
	 * @param userId the userid
	 * @param published_formresponse_only request only the formResponse published
	 * @param published_questions_only request only the question published
	 * @return a list of form response from the user id and filtered by published formresponse
	 */
	private static List<FormResponse> searchDashboardFormReponsesFromUser( String userId, boolean published_formresponse_only, boolean published_questions_only )
    {

        List<FormResponse> formResponseList = FormResponseHome.getFormResponseByGuid( userId );
        //If the propertie published_formresponse_only is true, remove unpublished formReponse  
        formResponseList.removeIf( f -> !f.isPublished( ) && published_formresponse_only );
        if (published_questions_only)
        {
        	formResponseFilteredByQuestionsPublished(formResponseList);
        }
        return formResponseList;
		
    }
    
    /**
     * remove the questionReponse not published.
     * @param formRep The formResponse from a user
     */
    private static void formResponseFilteredByQuestionsPublished( List<FormResponse> lstFormRep )
    {
    	for ( FormResponse formRep : lstFormRep )
        {
    		List<FormResponseStep> lstStep = formRep.getSteps( );
            for ( FormResponseStep step : lstStep )
            {
                List<FormQuestionResponse> lstQr = step.getQuestions( );
                lstQr.removeIf( f -> !f.getQuestion( ).isPublished( ) );
                step.setQuestions(lstQr);
            };
        }
    	
    }
    

}
