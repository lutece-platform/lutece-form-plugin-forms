/*
 * Copyright (c) 2002-2018, Mairie de Paris
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
package fr.paris.lutece.plugins.forms.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.service.EntryServiceManager;
import fr.paris.lutece.plugins.forms.service.FormService;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;

/**
 * 
 * Controller for form display
 *
 */
@Controller( xpageName = FormXPage.XPAGE_NAME, pageTitleI18nKey = FormXPage.MESSAGE_PAGE_TITLE, pagePathI18nKey = FormXPage.MESSAGE_PATH )
public class FormXPage extends MVCApplication
{
    protected static final String XPAGE_NAME = "forms";

    // Messages
    protected static final String MESSAGE_PAGE_TITLE = "forms.xpage.form.view.pageTitle";
    protected static final String MESSAGE_PATH = "forms.xpage.form.view.pagePathLabel";

    /**
     * Generated serial id
     */
    private static final long serialVersionUID = -8380962697376893817L;

    // Views
    private static final String VIEW_STEP = "stepView";

    // Actions
    private static final String ACTION_SAVE_FORM_RESPONSE = "doSaveResponse";
    private static final String ACTION_SAVE_STEP = "doSaveStep";

    // Templates
    private static final String TEMPLATE_VIEW_STEP = "/skin/plugins/forms/step_view.html";

    // Parameters
    private static final String ID_FORM = "formId";
    private static final String ID_STEP = "stepId";

    // Constants
    private static final int INCORRECT_ID = -1;

    // Markers
    private static final String STEP_HTML_MARKER = "stepContent";

    // Attributes
    private FormResponse _formResponse;
    private Step _currentStep;
    private StepDisplayTree _stepDisplayTree;

    /**
     * 
     * @param request
     *            The Http request
     * @return the XPage
     */
    @View( value = VIEW_STEP, defaultView = true )
    public XPage getStepView( HttpServletRequest request )
    {

        Map<String, Object> model = getModel( );

        int nIdForm = NumberUtils.toInt( request.getParameter( ID_FORM ), INCORRECT_ID );
        int nIdStep = NumberUtils.toInt( request.getParameter( ID_STEP ), INCORRECT_ID );

        if ( _currentStep == null || _currentStep.getId( ) != nIdStep )
        {
            _currentStep = StepHome.findByPrimaryKey( nIdStep );
            _stepDisplayTree = new StepDisplayTree( nIdStep );
        }

        if ( _currentStep != null )
        {
            Form form = FormHome.findByPrimaryKey( nIdForm );

            if ( form.isActive( ) )
            {
                model.put( STEP_HTML_MARKER, _stepDisplayTree.getCompositeHtml( request.getLocale( ) ) );
                model.put( FormsConstants.MARK_STEP, _currentStep );
            }
            // TODO
            /*
             * else { }
             */
        }

        return getXPage( TEMPLATE_VIEW_STEP, request.getLocale( ), model );
    }

    /**
     * 
     * @param request
     *            The Http request
     * @return the XPage
     */
    @Action( value = ACTION_SAVE_STEP )
    public XPage doSaveStep( HttpServletRequest request )
    {
        int nIdForm = NumberUtils.toInt( request.getParameter( ID_FORM ), INCORRECT_ID );
        int nIdStep = NumberUtils.toInt( request.getParameter( ID_STEP ), INCORRECT_ID );

        if ( nIdForm >= 0 && nIdStep >= 0 )
        {
            if ( _formResponse == null )
            {
                _formResponse = new FormResponse( );
                _formResponse.setFormId( nIdForm );
            }

            List<Question> stepQuestions = QuestionHome.getQuestionsListByStep( nIdStep );
            boolean bValidStep = true;
            Map<Integer, List<Response>> mapStepResponses = new HashMap<Integer, List<Response>>( );
            List<FormQuestionResponse> listResponsesTemp = new ArrayList<FormQuestionResponse>( );

            for ( Question question : stepQuestions )
            {
                IEntryDataService entryDataService = EntryServiceManager.getInstance( ).getEntryDataService( question.getEntry( ).getEntryType( ) );
                if ( entryDataService != null )
                {
                    FormQuestionResponse questionResponseInstance = new FormQuestionResponse( );
                    boolean bHasError = entryDataService.getResponseFromRequest( question, request, questionResponseInstance );

                    if ( bHasError )
                    {
                        bValidStep = false;
                    }
                    else
                    {
                        listResponsesTemp.add( questionResponseInstance );
                    }

                    mapStepResponses.put( question.getId( ), questionResponseInstance.getEntryResponse( ) );
                }
            }

            if ( !bValidStep )
            {
                _stepDisplayTree.setResponses( mapStepResponses );
                return getStepView( request );
            }

            _formResponse.getListResponses( ).addAll( listResponsesTemp );

        }

        return getStepView( request );
    }

    /**
     * 
     * @param request
     *            The Http request
     * @return the XPage
     */
    @Action( value = ACTION_SAVE_FORM_RESPONSE )
    public XPage doSaveFormResponse( HttpServletRequest request )
    {
        int nIdForm = NumberUtils.toInt( request.getParameter( ID_FORM ), INCORRECT_ID );
        int nIdStep = NumberUtils.toInt( request.getParameter( ID_STEP ), INCORRECT_ID );

        if ( nIdForm >= 0 && nIdStep >= 0 )
        {
            Form form = FormHome.findByPrimaryKey( nIdForm );

            if ( form.isActive( ) )
            {
                FormService.saveForm( _formResponse );
            }
        }

        return getStepView( request );
    }
}
