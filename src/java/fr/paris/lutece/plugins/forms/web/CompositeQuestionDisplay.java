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
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseStep;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.service.EntryServiceManager;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.validation.IValidator;
import fr.paris.lutece.plugins.forms.web.entrytype.DisplayType;
import fr.paris.lutece.plugins.forms.web.entrytype.IEntryDataService;
import fr.paris.lutece.plugins.forms.web.entrytype.IEntryDisplayService;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * 
 * Implementation of ICompositeDisplay for Question
 *
 */
public class CompositeQuestionDisplay implements ICompositeDisplay
{
    // Templates
    private static final String TEMPLATE_QUESTION_EDITION_FRONTOFFICE = "/skin/plugins/forms/composite_template/view_question.html";
    private static final String TEMPLATE_QUESTION_READONLY_BACKOFFICE = "/admin/plugins/forms/composite/view_question.html";
    private static final String TEMPLATE_QUESTION_EDITION_BACKOFFICE = TEMPLATE_QUESTION_READONLY_BACKOFFICE;
    private static final String TEMPLATE_QUESTION_READONLY_FRONTOFFICE = "/skin/plugins/forms/composite_template/view_question_read_only.html";

    // Marks
    private static final String MARK_QUESTION_ENTRY = "questionEntry";
    private static final String MARK_ENTRY_ITERATION_NUMBER = "entry_iteration_number";

    private Question _question;
    private final FormDisplay _formDisplay;
    private String _strIconName;

    /**
     * Constructor
     * 
     * @param formDisplay
     *            the form display
     * @param formResponse
     *            the form response
     * @param nIterationNumber
     *            the iteration number
     */
    public CompositeQuestionDisplay( FormDisplay formDisplay, FormResponse formResponse, int nIterationNumber )
    {
        _formDisplay = formDisplay;

        initComposite( formResponse, nIterationNumber );
    }

    /**
     * Initializes the composite
     * 
     * @param formResponse
     *            the form response
     * @param nIterationNumber
     *            the iteration number
     */
    private void initComposite( FormResponse formResponse, int nIterationNumber )
    {
        _question = QuestionHome.findByPrimaryKey( _formDisplay.getCompositeId( ) );

        if ( _question.getEntry( ) != null && _question.getEntry( ).getEntryType( ) != null )
        {
            _strIconName = _question.getEntry( ).getEntryType( ).getIconName( );
        }

        _question.setIterationNumber( nIterationNumber );

        Question question = getQuestionFromFormResponse( formResponse );

        if ( question != null )
        {
            _question.setIsVisible( question.isVisible( ) );
        }
    }

    /**
     * 
     * @param formResponse
     *            the formResponse
     * @return the current question from the given formResponse
     */
    private Question getQuestionFromFormResponse( FormResponse formResponse )
    {
        if ( formResponse != null && !formResponse.getSteps( ).isEmpty( ) )
        {
            List<FormResponseStep> listSteps = formResponse.getSteps( );

            for ( FormResponseStep formResponseStep : listSteps )
            {
                if ( formResponseStep.getStep( ).getId( ) == _question.getIdStep( ) )
                {
                    List<FormQuestionResponse> questionsResponse = formResponseStep.getQuestions( );

                    for ( FormQuestionResponse questionResponse : questionsResponse )
                    {
                        if ( questionResponse.getQuestion( ).getId( ) == _question.getId( )
                                && questionResponse.getQuestion( ).getIterationNumber( ) == _question.getIterationNumber( ) )
                        {
                            return questionResponse.getQuestion( );
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCompositeHtml( HttpServletRequest request, List<FormQuestionResponse> listFormQuestionResponse, Locale locale, DisplayType displayType )
    {
        String strQuestionTemplate = StringUtils.EMPTY;

        if ( _question.getEntry( ) != null )
        {
            IEntryDisplayService displayService = EntryServiceManager.getInstance( ).getEntryDisplayService( _question.getEntry( ).getEntryType( ) );

            if ( displayService != null )
            {
                List<Response> listResponse = findResponses( listFormQuestionResponse );
                setQuestionVisibility( listResponse, displayType );

                Map<String, Object> model = new HashMap<String, Object>( );
                model.put( MARK_ENTRY_ITERATION_NUMBER, _question.getIterationNumber( ) );
                model.put( FormsConstants.MARK_QUESTION_LIST_RESPONSES, listResponse );
                model.put( MARK_QUESTION_ENTRY, _question.getEntry( ) );

                strQuestionTemplate = displayService.getEntryTemplateDisplay( request, _question.getEntry( ), locale, model, displayType );

                model.put( FormsConstants.MARK_QUESTION_CONTENT, strQuestionTemplate );
                model.put( FormsConstants.MARK_QUESTION, _question );
                if ( _formDisplay.getDisplayControl( ) != null )
                {
                    Control control = _formDisplay.getDisplayControl( );
                    IValidator validator = EntryServiceManager.getInstance( ).getValidator( control.getValidatorName( ) );

                    model.put( FormsConstants.MARK_VALIDATOR, validator );
                    Control controlNew = control.clone( );
                    controlNew.setValue( validator.getJavascriptControlValue( control ) );
                    model.put( FormsConstants.MARK_CONTROL, controlNew );

                    model.put( FormsConstants.MARK_ID_DISPLAY, control.getIdTargetFormDisplay( ) );
                }

                HtmlTemplate htmlTemplateQuestion = AppTemplateService.getTemplate( findTemplateFor( displayType ), locale, model );

                strQuestionTemplate = htmlTemplateQuestion != null ? htmlTemplateQuestion.getHtml( ) : StringUtils.EMPTY;
            }
        }

        return strQuestionTemplate;
    }

    /**
     * Finds the responses associated to this instance among the specified list of form question responses
     * 
     * @param listFormQuestionResponse
     *            the list of form question responses
     * @return the responses
     */
    private List<Response> findResponses( List<FormQuestionResponse> listFormQuestionResponse )
    {
        List<Response> listResponse = new ArrayList<>( );

        if ( listFormQuestionResponse != null )
        {
            for ( FormQuestionResponse formQuestionResponse : listFormQuestionResponse )
            {
                Question question = formQuestionResponse.getQuestion( );

                if ( _question.getId( ) == question.getId( ) && _question.getIterationNumber( ) == question.getIterationNumber( ) )
                {
                    listResponse = formQuestionResponse.getEntryResponse( );
                    break;
                }
            }
        }

        return listResponse;
    }

    /**
     * Finds the template to use for the specified display type
     * 
     * @param displayType
     *            the display type
     * @return the template
     */
    private String findTemplateFor( DisplayType displayType )
    {
        String strTemplate = StringUtils.EMPTY;

        if ( displayType == DisplayType.EDITION_FRONTOFFICE )
        {
            strTemplate = TEMPLATE_QUESTION_EDITION_FRONTOFFICE;
        }

        if ( displayType == DisplayType.EDITION_BACKOFFICE )
        {
            strTemplate = TEMPLATE_QUESTION_EDITION_BACKOFFICE;
        }

        if ( displayType == DisplayType.READONLY_BACKOFFICE )
        {
            strTemplate = TEMPLATE_QUESTION_READONLY_BACKOFFICE;
        }

        if ( displayType == DisplayType.READONLY_FRONTOFFICE )
        {
            strTemplate = TEMPLATE_QUESTION_READONLY_FRONTOFFICE;
        }

        return strTemplate;
    }

    /**
     * 
     * @param listResponse
     *            The current question responses
     * @param displayType
     *            The current displayType
     */
    private void setQuestionVisibility( List<Response> listResponse, DisplayType displayType )
    {
        if ( displayType == DisplayType.READONLY_BACKOFFICE && _formDisplay != null )
        {
            Control controlConditionnalDisplay = ControlHome.getConditionalDisplayControlByDisplay( _formDisplay.getId( ) );

            if ( controlConditionnalDisplay != null )
            {
                if ( CollectionUtils.isNotEmpty( listResponse ) )
                {
                    _question.setIsVisible( true );
                }
            }
            else
            {
                _question.setIsVisible( true );
            }
        }

        if ( displayType == DisplayType.EDITION_BACKOFFICE )
        {
            _question.setIsVisible( true );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void iterate( int nIdFormDisplay )
    {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeIteration( HttpServletRequest request, int nIdGroupParent, int nIndexIterationToRemove, FormResponse formResponse )
    {
        FormDisplay formDisplayParent = FormDisplayHome.findByPrimaryKey( _formDisplay.getParentId( ) );

        if ( formDisplayParent != null && FormsConstants.COMPOSITE_GROUP_TYPE.equals( formDisplayParent.getCompositeType( ) )
                && formDisplayParent.getCompositeId( ) == nIdGroupParent )
        {
            IEntryDataService entryDataService = EntryServiceManager.getInstance( ).getEntryDataService( _question.getEntry( ).getEntryType( ) );

            if ( entryDataService != null )
            {
                if ( _question.getIterationNumber( ) == nIndexIterationToRemove )
                {
                    entryDataService.questionRemoved( request, _question );
                }

                if ( _question.getIterationNumber( ) > nIndexIterationToRemove )
                {
                    entryDataService.questionMoved( request, _question, _question.getIterationNumber( ) - 1 );
                }
            }
        }
    }

    @Override
    public List<ICompositeDisplay> getCompositeList( )
    {
        List<ICompositeDisplay> listICompositeDisplay = new ArrayList<ICompositeDisplay>( );
        listICompositeDisplay.add( this );
        return listICompositeDisplay;
    }

    @Override
    public String getTitle( )
    {
        String strTitle = "";
        if ( _question != null && StringUtils.isNotEmpty( _question.getTitle( ) ) )
        {
            strTitle = _question.getTitle( );
        }
        return strTitle;
    }

    @Override
    public String getType( )
    {
        return _question != null ? CompositeDisplayType.QUESTION.getLabel( ) : StringUtils.EMPTY;
    }

    @Override
    public FormDisplay getFormDisplay( )
    {
        return _formDisplay;
    }

    @Override
    public String getIcon( )
    {
        return _strIconName;
    }

    @Override
    public List<Control> getAllDisplayControls( )
    {
        List<Control> listDisplayControls = new ArrayList<Control>( );

        if ( _formDisplay.getDisplayControl( ) != null )
        {
            listDisplayControls.add( _formDisplay.getDisplayControl( ) );
        }

        return listDisplayControls;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addQuestions( List<Question> listQuestion )
    {
        listQuestion.add( _question );
    }

}
