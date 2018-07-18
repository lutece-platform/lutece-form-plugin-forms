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

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.business.Transition;
import fr.paris.lutece.plugins.forms.business.TransitionHome;
import fr.paris.lutece.plugins.forms.service.EntryServiceManager;
import fr.paris.lutece.plugins.forms.service.FormService;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.validation.IValidator;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
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
    protected static final String MESSAGE_ERROR_NO_STEP = "forms.xpage.form.error.noStep";
    protected static final String MESSAGE_ERROR_INACTIVE_FORM = "forms.xpage.form.error.inactive";

    /**
     * Generated serial id
     */
    private static final long serialVersionUID = -8380962697376893817L;

    // Views
    private static final String VIEW_STEP = "stepView";

    // Actions
    private static final String ACTION_SAVE_FORM_RESPONSE = "doSaveResponse";
    private static final String ACTION_SAVE_STEP = "doSaveStep";
    private static final String ACTION_PREVIOUS_STEP = "doReturnStep";

    // Templates
    private static final String TEMPLATE_VIEW_STEP = "/skin/plugins/forms/step_view.html";

    // Parameters
    private static final String ID_FORM = "formId";
    private static final String ID_STEP = "stepId";

    // Constants
    private static final int INCORRECT_ID = -1;

    // Markers
    private static final String STEP_HTML_MARKER = "stepContent";

    // Other
    private static FormService _formService = SpringContextService.getBean( FormService.BEAN_NAME );
    // Attributes
    private FormResponseManager _formResponseManager;
    private Step _currentStep;
    private StepDisplayTree _stepDisplayTree;

    /**
     * 
     * @param request
     *            The Http request
     * @return the XPage
     * 
     * @throws SiteMessageException
     *             Exception
     */
    @View( value = VIEW_STEP, defaultView = true )
    public XPage getStepView( HttpServletRequest request ) throws SiteMessageException
    {
        Map<String, Object> model = getModel( );

        int nIdForm = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ), INCORRECT_ID );

        if ( nIdForm != FormsConstants.DEFAULT_ID_VALUE && ( _currentStep == null || nIdForm != _currentStep.getIdForm( ) ) )
        {
            _currentStep = StepHome.getInitialStep( nIdForm );
            _formResponseManager = null;
        }

        if ( _currentStep == null )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR_NO_STEP, SiteMessage.TYPE_ERROR );
        }

        else
        {
            Form form = FormHome.findByPrimaryKey( _currentStep.getIdForm( ) );

            if ( form.isActive( ) )
            {
                if ( _formResponseManager == null )
                {
                    _formResponseManager = new FormResponseManager( );
                }

                if ( _stepDisplayTree == null || _currentStep.getId( ) != _stepDisplayTree.getStep( ).getId( ) )
                {
                    _stepDisplayTree = new StepDisplayTree( _currentStep.getId( ) );
                    _formResponseManager.getListValidatedStep( ).add( _currentStep );
                }

                populateStepResponses( );

                boolean bIsForEdition = Boolean.TRUE;

				// Add all the display controls for visualisation
				List<Control> listDisplayControls = _stepDisplayTree.getListDisplayControls();
				model.put( FormsConstants.MARK_LIST_CONTROLS, listDisplayControls );

				// Add all the validators to generate their Javascript
				List<IValidator> listDisplayValidators = new ArrayList<IValidator>();
				for ( Control control : listDisplayControls )
				{
					IValidator validator = EntryServiceManager.getInstance().getValidator( control.getValidatorName() );
					if ( validator != null )
					{
						listDisplayValidators.add( validator );
					}
				}
				model.put( FormsConstants.MARK_LIST_VALIDATORS, listDisplayValidators );
				model.put( FormsConstants.MARK_LIST_CONTROLS, listDisplayControls );
				model.put( FormsConstants.MARKER_JS_PARAMETER_CONTROL_VALUE,
						FormsConstants.JS_PARAMETER_CONTROL_VALUE );
				model.put( FormsConstants.MARKER_JS_PARAMETER_INPUT_VALUE, FormsConstants.JS_PARAMETER_INPUT_VALUE );
                model.put( STEP_HTML_MARKER, _stepDisplayTree.getCompositeHtml( request.getLocale( ), bIsForEdition ) );
                model.put( FormsConstants.MARK_FORM_BREADCRUMB, _formResponseManager.getListValidatedStep( ) );
                model.put( FormsConstants.MARK_STEP, _currentStep );
            }
            else
            {
                SiteMessageService.setMessage( request, MESSAGE_ERROR_INACTIVE_FORM, SiteMessage.TYPE_ERROR );
            }
        }

        return getXPage( TEMPLATE_VIEW_STEP, request.getLocale( ), model );
    }

    /**
     * 
     * @param request
     *            The Http request
     * @return the XPage
     * 
     * @throws SiteMessageException
     *             Exception
     */
    @Action( value = ACTION_PREVIOUS_STEP )
    public XPage doReturnStep( HttpServletRequest request ) throws SiteMessageException
    {
        int nIndexLastStep = _formResponseManager.getListValidatedStep( ).size( ) - 1;

        _formResponseManager.getListValidatedStep( ).remove( nIndexLastStep );

        _currentStep = _formResponseManager.getListValidatedStep( ).get( nIndexLastStep - 1 );

        _stepDisplayTree = new StepDisplayTree( _currentStep.getId( ) );

        populateStepResponses( );

        return redirectView( request, VIEW_STEP );
    }

    /**
     * Populate the step responses
     */
    private void populateStepResponses( )
    {
        Map<Integer, List<Response>> mapStepResponses = new HashMap<Integer, List<Response>>( );

        List<FormQuestionResponse> listStepResponses = _formResponseManager.getMapStepFormResponses( ).get( _currentStep.getId( ) );

        if ( listStepResponses != null )
        {
            for ( FormQuestionResponse formQuestionResponse : listStepResponses )
            {
                Question question = QuestionHome.findByPrimaryKey( formQuestionResponse.getIdQuestion( ) );
                mapStepResponses.put( question.getId( ), formQuestionResponse.getEntryResponse( ) );
            }
        }

        _stepDisplayTree.setResponses( mapStepResponses );
    }

    /**
     * 
     * @param request
     *            The Http request
     * @return the XPage
     * 
     * @throws SiteMessageException
     *             Exception
     */
    @Action( value = ACTION_SAVE_STEP )
    public XPage doSaveStep( HttpServletRequest request ) throws SiteMessageException
    {
        Form form = FormHome.findByPrimaryKey( _currentStep.getIdForm( ) );

        if ( !form.isActive( ) )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR_INACTIVE_FORM, SiteMessage.TYPE_ERROR );
        }

        List<Question> stepQuestions = QuestionHome.getQuestionsListByStep( _currentStep.getId( ) );

        int nIdForm = _currentStep.getIdForm( );

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

        _formResponseManager.getMapStepFormResponses( ).put( _currentStep.getId( ), listResponsesTemp );

        if ( !bValidStep )
        {
            return redirectView( request, VIEW_STEP );
        }

        if ( _currentStep.isFinal( ) )
        {
            FormResponse formResponse = new FormResponse( );
            formResponse.setFormId( _currentStep.getIdForm( ) );

            for ( Step step : _formResponseManager.getListValidatedStep( ) )
            {
                formResponse.getListResponses( ).addAll( _formResponseManager.getMapStepFormResponses( ).get( step.getId( ) ) );
            }

            _formService.saveForm( formResponse );

            Map<String, String> model = new HashMap<String, String>( );

            model.put( FormsConstants.PARAMETER_ID_FORM, Integer.toString( _currentStep.getIdForm( ) ) );

            if ( WorkflowService.getInstance( ).isAvailable( ) && ( form.getIdWorkflow( ) > 0 ) )
            {
                WorkflowService.getInstance( ).getState( formResponse.getId( ), FormResponse.RESOURCE_TYPE, form.getIdWorkflow( ), form.getId( ) );
            }

            _formResponseManager = null;
            _currentStep = null;
            _stepDisplayTree = null;

            return redirect( request, VIEW_STEP, model );
        }
        else
        {
            List<Transition> listTransition = TransitionHome.getTransitionsListFromStep( _currentStep.getId( ) );

            for ( Transition transition : listTransition )
            {
                if ( transition.getIdControl( ) <= 0 )
                {
                    _currentStep = StepHome.findByPrimaryKey( transition.getNextStep( ) );
                    return redirectView( request, VIEW_STEP );
                }
                else
                {
                    Control transitionControl = ControlHome.findByPrimaryKey( transition.getIdControl( ) );

                    Question targetQuestion = QuestionHome.findByPrimaryKey( transitionControl.getIdQuestion( ) );

                    for ( FormQuestionResponse questionResponse : _formResponseManager.getMapStepFormResponses( ).get( targetQuestion.getIdStep( ) ) )
                    {
                        if ( transitionControl.getIdQuestion( ) == questionResponse.getIdQuestion( ) )
                        {
                            IValidator validator = EntryServiceManager.getInstance( ).getValidator( transitionControl.getValidatorName( ) );

                            if ( validator != null && validator.validate( questionResponse, transitionControl ) )
                            {
                                _currentStep = StepHome.findByPrimaryKey( transition.getNextStep( ) );
                                return redirectView( request, VIEW_STEP );
                            }
                        }
                    }
                }
            }

            SiteMessageService.setMessage( request, MESSAGE_ERROR_NO_STEP, SiteMessage.TYPE_ERROR );

            return redirectView( request, VIEW_STEP );
        }
    }

}
