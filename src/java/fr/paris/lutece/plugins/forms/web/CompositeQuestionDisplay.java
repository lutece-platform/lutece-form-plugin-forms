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
package fr.paris.lutece.plugins.forms.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlGroup;
import fr.paris.lutece.plugins.forms.business.ControlGroupHome;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.ControlType;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseStep;
import fr.paris.lutece.plugins.forms.business.LogicalOperator;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.service.EntryServiceManager;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.validation.IValidator;
import fr.paris.lutece.plugins.forms.web.entrytype.DisplayType;
import fr.paris.lutece.plugins.forms.web.entrytype.IEntryDataService;
import fr.paris.lutece.plugins.forms.web.entrytype.IEntryDisplayService;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeComment;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.service.image.ImageResourceManager;
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
    private static final String TEMPLATE_QUESTION_READONLY_BACKOFFICE = "/admin/plugins/forms/composite/view_question_read_only.html";
    private static final String TEMPLATE_QUESTION_RESUBMIT_BACKOFFICE = "/admin/plugins/forms/composite/select_question.html";
    private static final String TEMPLATE_QUESTION_EDITION_BACKOFFICE = "/admin/plugins/forms/composite/view_question.html";
    private static final String TEMPLATE_QUESTION_READONLY_FRONTOFFICE = "/skin/plugins/forms/composite_template/view_question_read_only.html";
    private static final String TEMPLATE_QUESTION_RESUBMIT_FRONTOFFICE = "/skin/plugins/forms/composite_template/view_question_resubmit.html";

    // Marks
    private static final String MARK_QUESTION_ENTRY = "questionEntry";
    private static final String MARK_COMPLETENESS_FO = "is_completeness_bo";
    private static final String MARK_ENTRY_ITERATION_NUMBER = "entry_iteration_number";
    private static final String MARK_FIELDS_LIST_BY_ID_ENTRIES = "fields_list_by_id_entries";

    // Constants
    private static final String PUBLIC_IMAGE_RESOURCE = "public_image_resource";
    private static final String ILLUSTRATION_IMAGE = "illustration_image";
    
    private Question _question;
    private final FormDisplay _formDisplay;
    private String _strIconName;
    private final Map<String, Object> _model;

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
        _model = new HashMap<>( );

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
        if ( formResponse == null || formResponse.getSteps( ).isEmpty( ) )
        {
            return null;
        }

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

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCompositeHtml( HttpServletRequest request, List<FormQuestionResponse> listFormQuestionResponse, Locale locale, DisplayType displayType )
    {
        String strQuestionTemplate = StringUtils.EMPTY;
        Map<Integer, String> fieldsList = new HashMap<>( );
        Entry entry = _question.getEntry( );

        if ( entry == null )
        {
            return strQuestionTemplate;
        }

        IEntryDisplayService displayService = EntryServiceManager.getInstance( ).getEntryDisplayService( entry.getEntryType( ) );

        if ( displayService != null && isQuestionEnabled( entry, listFormQuestionResponse, displayType ) )
        {
            List<Response> listResponse = findResponses( listFormQuestionResponse );
            setQuestionVisibility( listResponse, displayType );

            _model.put( MARK_ENTRY_ITERATION_NUMBER, _question.getIterationNumber( ) );
            _model.put( FormsConstants.MARK_QUESTION_LIST_RESPONSES, listResponse );
            _model.put( MARK_QUESTION_ENTRY, _question.getEntry( ) );
            _model.put( MARK_COMPLETENESS_FO, displayType == DisplayType.COMPLETE_FRONTOFFICE );
            _model.put( FormsConstants.MARK_REGEX_URL, FormsConstants.DEFAULT_REGEX_URL );

            List<Field> listField = _question.getEntry().getFields();
            if( listField == null )
            {
                listField = FieldHome.getFieldListByIdEntry( _question.getEntry().getIdEntry( ) );
            }

            for( Field field : listField )
            {
            	if( field.getCode( ).equals( ILLUSTRATION_IMAGE ) && field.getValue( ) != null )
            		fieldsList.put( field.getIdField( ), ImageResourceManager.getImageUrl( PUBLIC_IMAGE_RESOURCE, Integer.parseInt( field.getValue( ) ) )  );
            }
            
            _model.put( MARK_FIELDS_LIST_BY_ID_ENTRIES, fieldsList );
            
            strQuestionTemplate = displayService.getEntryTemplateDisplay( request, _question.getEntry( ), locale, _model, displayType );

            _model.put( FormsConstants.MARK_QUESTION_CONTENT, strQuestionTemplate );
            _model.put( FormsConstants.MARK_QUESTION, _question );
            if ( _formDisplay.getDisplayControl( ) != null )
            {
                List<Control> listControl = ControlHome.getControlByControlTargetAndType( _formDisplay.getId( ), ControlType.CONDITIONAL );
                List<Control> listOtherStepControl = new ArrayList<>();
                List<IValidator> listValidator = new ArrayList<>();
                Boolean bOtherStepValidation = null;
                int nIdControlGroup = 0;
                int nValidControlsCount = 0;
                int nNotValidControlsCount = 0;
                for (Control control : listControl) {
                	if (nIdControlGroup == 0) {
            			nIdControlGroup = control.getIdControlGroup();
            		}
                	IValidator validator = EntryServiceManager.getInstance( ).getValidator( control.getValidatorName( ) );
                	listValidator.add(validator);
                	control.setValue(validator.getJavascriptControlValue( control ));
                	if ( CollectionUtils.isNotEmpty( control.getListIdQuestion( ) ) && CollectionUtils.isNotEmpty( listFormQuestionResponse ) )
                    {
                        int questionControlStep = QuestionHome.findByPrimaryKey( control.getListIdQuestion( ).iterator( ).next( ) ).getIdStep( );
                        if ( questionControlStep != _question.getIdStep( ) )
                        {
                            List<FormQuestionResponse> listFormQuestionReponseToCheck = listFormQuestionResponse.stream( )
                                    .filter( questionReponse -> control.getListIdQuestion( ).contains( questionReponse.getQuestion( ).getId( ) ) )
                                    .collect( Collectors.toList( ) );
                            if (validator.validate( listFormQuestionReponseToCheck, control )) {
                            	nValidControlsCount++;
                            } else {
                            	nNotValidControlsCount++;
                            }
                            listOtherStepControl.add(control);
                        }
                    }
                }
                
                // remove controls from other steps
                listControl.removeAll(listOtherStepControl);
                
                _model.put(FormsConstants.MARK_LIST_CONTROL, listControl);
                _model.put( FormsConstants.MARK_LIST_VALIDATOR, listValidator );
                
                ControlGroup controlGroup = ControlGroupHome.findByPrimaryKey(nIdControlGroup).orElse(null);
                _model.put( FormsConstants.MARK_LOGICAL_OPERATOR_LABEL, (controlGroup != null ? controlGroup.getLogicalOperator().getLabel() : LogicalOperator.AND.getLabel()) );
                if (controlGroup != null && LogicalOperator.OR.getLabel().equals(controlGroup.getLogicalOperator().getLabel())) {
                	bOtherStepValidation = nValidControlsCount > 0;
                } else {
                	bOtherStepValidation = nNotValidControlsCount == 0;
                }
                _model.put( FormsConstants.MARK_OTHER_STEP_VALIDATION, bOtherStepValidation);
            }

            HtmlTemplate htmlTemplateQuestion = AppTemplateService.getTemplate( findTemplateFor( displayType ), locale, _model );

            strQuestionTemplate = htmlTemplateQuestion != null ? htmlTemplateQuestion.getHtml( ) : StringUtils.EMPTY;
        }

        return strQuestionTemplate;
    }
    
    /**
     * Check if the question is enabled so that it can be displayed in the form
     * 
     * @param entry
     *            the entry
     * @param listFormQuestionResponse
     *            the list of form question responses
     * @param displayType
     *            the display type
     * @return true if the question is enabled, false otherwise
     */
    private boolean isQuestionEnabled( Entry entry, List<FormQuestionResponse> listFormQuestionResponse, DisplayType displayType )
    {
    	boolean isQuestionEnabled = true;
        Field disabledField = entry.getFieldByCode( IEntryTypeService.FIELD_DISABLED );
        
        if ( disabledField != null && Boolean.parseBoolean( disabledField.getValue( ) ) )
        {
        	switch( displayType.getMode( ) )
            {
                case EDITION:
                	isQuestionEnabled = false;
                    break;
                case READONLY:
                	isQuestionEnabled = existsResponseFilled( listFormQuestionResponse );
                    break;
                default: // Nothing to do
            }
        }

        return isQuestionEnabled;
    }
    
    /**
     * Check if a response value filled exists to a question 
     * 
     * @param listFormQuestionResponse
     *            the list of form question responses
     * @return true if a response value filled exists, false otherwise
     */
    private boolean existsResponseFilled( List<FormQuestionResponse> listFormQuestionResponse )
    {
    	boolean existsResponseFilled = false;
    	
        if ( CollectionUtils.isNotEmpty( listFormQuestionResponse ) )
        {
        	List<Response> listResponse = findResponses( listFormQuestionResponse );
        	
        	if ( CollectionUtils.isNotEmpty( listResponse ) 
        			&& listResponse.stream( ).anyMatch( response -> StringUtils.isNotBlank( response.getResponseValue( ) ) ) )
        	{
        		existsResponseFilled = true;
        	}
        }
        
        return existsResponseFilled;
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

        if ( displayType == DisplayType.EDITION_BACKOFFICE || displayType == DisplayType.SUBMIT_BACKOFFICE )
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
        if ( displayType == DisplayType.RESUBMIT_BACKOFFICE )
        {
            strTemplate = TEMPLATE_QUESTION_RESUBMIT_BACKOFFICE;
        }
        if ( displayType == DisplayType.RESUBMIT_FRONTOFFICE )
        {
            strTemplate = TEMPLATE_QUESTION_RESUBMIT_FRONTOFFICE;
        }
        if ( displayType == DisplayType.COMPLETE_BACKOFFICE )
        {
            strTemplate = TEMPLATE_QUESTION_RESUBMIT_BACKOFFICE;
        }
        if ( displayType == DisplayType.COMPLETE_FRONTOFFICE )
        {
            strTemplate = TEMPLATE_QUESTION_RESUBMIT_FRONTOFFICE;
        }
        return strTemplate;
    }

    private void setQuestionVisibilityReadOnlyBO( List<Response> listResponse )
    {
        List<Control> listConditionalControl = ControlHome.getControlByControlTargetAndType( _formDisplay.getId( ), ControlType.CONDITIONAL );
        Control controlConditionnalDisplay = null;

        if ( !listConditionalControl.isEmpty( ) )
        {
            controlConditionnalDisplay = listConditionalControl.get( 0 );
        }

        // No Conditional Display
        if ( controlConditionnalDisplay == null )
        {
            _question.setIsVisible( true );
        }
        else
        {
            IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( _question.getEntry( ) );
            // If there is a conditional display, shows question if it has responses or if it is a comment
            if ( CollectionUtils.isNotEmpty( listResponse ) || entryTypeService instanceof AbstractEntryTypeComment )
            {
                _question.setIsVisible( true );
            }
        }
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
            setQuestionVisibilityReadOnlyBO( listResponse );
        }
        if ( _question.getEntry( ) == null )
        {
            return;
        }

        if ( displayType == DisplayType.EDITION_FRONTOFFICE )
        {

            _question.setIsVisible( true );
        }

        if ( displayType == DisplayType.EDITION_BACKOFFICE || displayType == DisplayType.SUBMIT_BACKOFFICE )
        {
            _question.setIsVisible( true );

        }
        if ( displayType == DisplayType.RESUBMIT_BACKOFFICE )
        {
            _question.setIsVisible( CollectionUtils.isNotEmpty( listResponse ) );

        }

        if ( displayType == DisplayType.RESUBMIT_FRONTOFFICE )
        {
            _question.setIsVisible( true );

        }
        if ( displayType == DisplayType.COMPLETE_BACKOFFICE )
        {
            _question.setIsVisible( true );

        }

        if ( displayType == DisplayType.COMPLETE_FRONTOFFICE )
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
        // Nothing to do
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
        List<ICompositeDisplay> listICompositeDisplay = new ArrayList<>( );
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
        List<Control> listDisplayControls = new ArrayList<>( );

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

    /**
     * {@inheritDoc}
     */
    @Override
    public void addModel( Map<String, Object> model )
    {
        _model.putAll( model );
    }

    @Override
    public boolean isVisible( )
    {
        if ( _question == null )
        {
            return false;
        }
        return _question.isVisible( );
    }

    @Override
    public ICompositeDisplay filter( List<Integer> listQuestionIds )
    {
        if ( listQuestionIds.contains( _question.getId( ) ) )
        {
            return this;
        }
        return null;
    }
}
