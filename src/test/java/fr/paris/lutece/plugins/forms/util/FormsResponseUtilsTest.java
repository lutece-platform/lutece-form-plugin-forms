/*
 * Copyright (c) 2002-2026, City of Paris
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
package fr.paris.lutece.plugins.forms.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlGroup;
import fr.paris.lutece.plugins.forms.business.ControlGroupHome;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.ControlType;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.LogicalOperator;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.test.LuteceTestCase;


public class FormsResponseUtilsTest extends LuteceTestCase
{
    private static final String VALIDATOR_NUMBER_SUPERIOR = "forms.numberSuperiorValidator";
    private static final String VALIDATOR_UNKNOWN = "forms.doesNotExist";

    private final List<Integer> _listCreatedFormDisplayIds = new ArrayList<>( );
    private final List<Integer> _listCreatedControlIds = new ArrayList<>( );
    private final List<Integer> _listCreatedControlGroupIds = new ArrayList<>( );

    @AfterEach
    public void cleanup( )
    {
        for ( Integer nIdControl : _listCreatedControlIds )
        {
            try
            {
                ControlHome.remove( nIdControl );
            }
            catch ( Exception e )
            {
                // best effort cleanup
            }
        }
        for ( Integer nIdControlGroup : _listCreatedControlGroupIds )
        {
            try
            {
                ControlGroupHome.remove( nIdControlGroup );
            }
            catch ( Exception e )
            {
                // best effort cleanup
            }
        }
        for ( Integer nIdFormDisplay : _listCreatedFormDisplayIds )
        {
            try
            {
                FormDisplayHome.remove( nIdFormDisplay );
            }
            catch ( Exception e )
            {
                // best effort cleanup
            }
        }
        _listCreatedControlIds.clear( );
        _listCreatedControlGroupIds.clear( );
        _listCreatedFormDisplayIds.clear( );
    }

    @Test
    public void testNoFormDisplay_QuestionIsVisible( )
    {
        // No FormDisplay at all matches (nIdForm, idStep, idQuestion) : formDisplay == null branch
        Question question = createQuestion( IdGenerator.generateId( ), IdGenerator.generateId( ) );

        boolean bIsVisible = FormsResponseUtils.isQuestionConditionallyVisible( question, Collections.emptyList( ), IdGenerator.generateId( ) );

        assertTrue( bIsVisible );
    }

    @Test
    public void testFormDisplayWithoutConditionalControl_QuestionIsVisible( )
    {
        int nIdForm = IdGenerator.generateId( );
        Question question = createQuestion( IdGenerator.generateId( ), IdGenerator.generateId( ) );
        createFormDisplay( nIdForm, question );

        boolean bIsVisible = FormsResponseUtils.isQuestionConditionallyVisible( question, Collections.emptyList( ), nIdForm );

        assertTrue( bIsVisible );
    }

    @Test
    public void testConditionMet_QuestionIsVisible( )
    {
        int nIdForm = IdGenerator.generateId( );
        Question targetQuestion = createQuestion( IdGenerator.generateId( ), IdGenerator.generateId( ) );
        FormDisplay formDisplay = createFormDisplay( nIdForm, targetQuestion );

        Question controllingQuestion = createQuestion( IdGenerator.generateId( ), targetQuestion.getIdStep( ) );
        createConditionalControl( formDisplay.getId( ), controllingQuestion.getId( ), VALIDATOR_NUMBER_SUPERIOR, "10", 0 );

        List<FormQuestionResponse> listResponses = Collections
                .singletonList( createResponse( controllingQuestion, "20" ) );

        boolean bIsVisible = FormsResponseUtils.isQuestionConditionallyVisible( targetQuestion, listResponses, nIdForm );

        assertTrue( bIsVisible );
    }

    @Test
    public void testConditionNotMet_QuestionIsHidden( )
    {
        int nIdForm = IdGenerator.generateId( );
        Question targetQuestion = createQuestion( IdGenerator.generateId( ), IdGenerator.generateId( ) );
        FormDisplay formDisplay = createFormDisplay( nIdForm, targetQuestion );

        Question controllingQuestion = createQuestion( IdGenerator.generateId( ), targetQuestion.getIdStep( ) );
        createConditionalControl( formDisplay.getId( ), controllingQuestion.getId( ), VALIDATOR_NUMBER_SUPERIOR, "10", 0 );

        List<FormQuestionResponse> listResponses = Collections
                .singletonList( createResponse( controllingQuestion, "5" ) );

        boolean bIsVisible = FormsResponseUtils.isQuestionConditionallyVisible( targetQuestion, listResponses, nIdForm );

        assertFalse( bIsVisible );
    }

    /**
     * Documents an important corner case : if the controlling question's response is not (yet)
     * present in {@code listResponsesTemp} - e.g. an ordering issue in the step's question list -
     * the filtered response list passed to the validator is empty. {@code AbstractValidator}
     * (the base class of every validator shipped with plugin-forms) returns {@code true} for an
     * empty list ("vacuous truth"), so in this situation the question defaults to *visible*
     * rather than hidden.
     */
    @Test
    public void testControllingResponseMissing_QuestionDefaultsVisible( )
    {
        int nIdForm = IdGenerator.generateId( );
        Question targetQuestion = createQuestion( IdGenerator.generateId( ), IdGenerator.generateId( ) );
        FormDisplay formDisplay = createFormDisplay( nIdForm, targetQuestion );

        Question controllingQuestion = createQuestion( IdGenerator.generateId( ), targetQuestion.getIdStep( ) );
        createConditionalControl( formDisplay.getId( ), controllingQuestion.getId( ), VALIDATOR_NUMBER_SUPERIOR, "10", 0 );

        boolean bIsVisible = FormsResponseUtils.isQuestionConditionallyVisible( targetQuestion, Collections.emptyList( ), nIdForm );

        assertTrue( bIsVisible );
    }

    @Test
    public void testUnknownValidatorName_ControlIsIgnored( )
    {
        int nIdForm = IdGenerator.generateId( );
        Question targetQuestion = createQuestion( IdGenerator.generateId( ), IdGenerator.generateId( ) );
        FormDisplay formDisplay = createFormDisplay( nIdForm, targetQuestion );

        Question controllingQuestion = createQuestion( IdGenerator.generateId( ), targetQuestion.getIdStep( ) );
        createConditionalControl( formDisplay.getId( ), controllingQuestion.getId( ), VALIDATOR_UNKNOWN, "10", 0 );

        List<FormQuestionResponse> listResponses = Collections
                .singletonList( createResponse( controllingQuestion, "5" ) );

        // The only control targeting this FormDisplay has no resolvable validator : it must be
        // ignored rather than block the display, consistent with CompositeQuestionDisplay's
        // handling of the same case (it logs an error and skips the control on client side).
        boolean bIsVisible = FormsResponseUtils.isQuestionConditionallyVisible( targetQuestion, listResponses, nIdForm );

        assertTrue( bIsVisible );
    }

    @Test
    public void testOrGroup_OneConditionMetIsEnough( )
    {
        int nIdForm = IdGenerator.generateId( );
        Question targetQuestion = createQuestion( IdGenerator.generateId( ), IdGenerator.generateId( ) );
        FormDisplay formDisplay = createFormDisplay( nIdForm, targetQuestion );

        int nIdControlGroup = createControlGroup( LogicalOperator.OR );

        Question controllingQuestion1 = createQuestion( IdGenerator.generateId( ), targetQuestion.getIdStep( ) );
        Question controllingQuestion2 = createQuestion( IdGenerator.generateId( ), targetQuestion.getIdStep( ) );
        createConditionalControl( formDisplay.getId( ), controllingQuestion1.getId( ), VALIDATOR_NUMBER_SUPERIOR, "10", nIdControlGroup );
        createConditionalControl( formDisplay.getId( ), controllingQuestion2.getId( ), VALIDATOR_NUMBER_SUPERIOR, "10", nIdControlGroup );

        // condition 1 met (20 > 10), condition 2 not met (5 > 10 is false)
        List<FormQuestionResponse> listResponses = new ArrayList<>( );
        listResponses.add( createResponse( controllingQuestion1, "20" ) );
        listResponses.add( createResponse( controllingQuestion2, "5" ) );

        boolean bIsVisible = FormsResponseUtils.isQuestionConditionallyVisible( targetQuestion, listResponses, nIdForm );

        assertTrue( bIsVisible );
    }

    @Test
    public void testAndGroup_AllConditionsMustBeMet( )
    {
        int nIdForm = IdGenerator.generateId( );
        Question targetQuestion = createQuestion( IdGenerator.generateId( ), IdGenerator.generateId( ) );
        FormDisplay formDisplay = createFormDisplay( nIdForm, targetQuestion );

        int nIdControlGroup = createControlGroup( LogicalOperator.AND );

        Question controllingQuestion1 = createQuestion( IdGenerator.generateId( ), targetQuestion.getIdStep( ) );
        Question controllingQuestion2 = createQuestion( IdGenerator.generateId( ), targetQuestion.getIdStep( ) );
        createConditionalControl( formDisplay.getId( ), controllingQuestion1.getId( ), VALIDATOR_NUMBER_SUPERIOR, "10", nIdControlGroup );
        createConditionalControl( formDisplay.getId( ), controllingQuestion2.getId( ), VALIDATOR_NUMBER_SUPERIOR, "10", nIdControlGroup );

        // condition 1 met (20 > 10), condition 2 not met (5 > 10 is false) : AND must fail overall
        List<FormQuestionResponse> listResponses = new ArrayList<>( );
        listResponses.add( createResponse( controllingQuestion1, "20" ) );
        listResponses.add( createResponse( controllingQuestion2, "5" ) );

        boolean bIsVisible = FormsResponseUtils.isQuestionConditionallyVisible( targetQuestion, listResponses, nIdForm );

        assertFalse( bIsVisible );
    }

    @Test
    public void testAndGroup_AllConditionsMet_QuestionIsVisible( )
    {
        int nIdForm = IdGenerator.generateId( );
        Question targetQuestion = createQuestion( IdGenerator.generateId( ), IdGenerator.generateId( ) );
        FormDisplay formDisplay = createFormDisplay( nIdForm, targetQuestion );

        int nIdControlGroup = createControlGroup( LogicalOperator.AND );

        Question controllingQuestion1 = createQuestion( IdGenerator.generateId( ), targetQuestion.getIdStep( ) );
        Question controllingQuestion2 = createQuestion( IdGenerator.generateId( ), targetQuestion.getIdStep( ) );
        createConditionalControl( formDisplay.getId( ), controllingQuestion1.getId( ), VALIDATOR_NUMBER_SUPERIOR, "10", nIdControlGroup );
        createConditionalControl( formDisplay.getId( ), controllingQuestion2.getId( ), VALIDATOR_NUMBER_SUPERIOR, "10", nIdControlGroup );

        List<FormQuestionResponse> listResponses = new ArrayList<>( );
        listResponses.add( createResponse( controllingQuestion1, "20" ) );
        listResponses.add( createResponse( controllingQuestion2, "30" ) );

        boolean bIsVisible = FormsResponseUtils.isQuestionConditionallyVisible( targetQuestion, listResponses, nIdForm );

        assertTrue( bIsVisible );
    }

    // ---------------------------------------------------------------------
    // Test data builders
    // ---------------------------------------------------------------------

    /**
     * Builds a plain in-memory Question (not persisted : isQuestionConditionallyVisible only
     * ever reads the id/step already set on the object it is given, it never re-fetches the
     * question from database).
     */
    private Question createQuestion( int nId, int nIdStep )
    {
        Question question = new Question( );
        question.setId( nId );
        question.setIdStep( nIdStep );
        return question;
    }

    /**
     * Persists a FormDisplay of type "question" for the given form/question, so that
     * {@code FormDisplayHome.getFormDisplayByFormStepAndComposite} can find it back.
     */
    private FormDisplay createFormDisplay( int nIdForm, Question question )
    {
        FormDisplay formDisplay = new FormDisplay( );
        formDisplay.setFormId( nIdForm );
        formDisplay.setStepId( question.getIdStep( ) );
        formDisplay.setCompositeId( question.getId( ) );
        formDisplay.setParentId( 0 );
        formDisplay.setDisplayOrder( 1 );
        formDisplay.setCompositeType( "question" );
        formDisplay.setDepth( 0 );

        FormDisplayHome.create( formDisplay );
        _listCreatedFormDisplayIds.add( formDisplay.getId( ) );

        return formDisplay;
    }

    /**
     * Persists a {@code ControlType.CONDITIONAL} control targeting the given FormDisplay,
     * controlled by the given question id.
     */
    private Control createConditionalControl( int nIdFormDisplayTarget, int nIdControllingQuestion, String strValidatorName, String strValue,
            int nIdControlGroup )
    {
        Control control = new Control( );
        control.setControlType( ControlType.CONDITIONAL.getLabel( ) );
        control.setIdControlTarget( nIdFormDisplayTarget );
        control.setValidatorName( strValidatorName );
        control.setValue( strValue );
        control.setIdControlGroup( nIdControlGroup );

        Set<Integer> listIdQuestion = new HashSet<>( );
        listIdQuestion.add( nIdControllingQuestion );
        control.setListIdQuestion( listIdQuestion );

        ControlHome.create( control );
        _listCreatedControlIds.add( control.getId( ) );

        return control;
    }

    private int createControlGroup( LogicalOperator logicalOperator )
    {
        ControlGroup controlGroup = new ControlGroup( );
        controlGroup.setLogicalOperator( logicalOperator );

        ControlGroupHome.create( controlGroup );
        _listCreatedControlGroupIds.add( controlGroup.getId( ) );

        return controlGroup.getId( );
    }

    /**
     * Builds a plain in-memory FormQuestionResponse carrying a single Response with the given
     * raw text value, for the given (in-memory) controlling question.
     */
    private FormQuestionResponse createResponse( Question question, String strResponseValue )
    {
        Response response = new Response( );
        response.setResponseValue( strResponseValue );

        FormQuestionResponse formQuestionResponse = new FormQuestionResponse( );
        formQuestionResponse.setQuestion( question );
        formQuestionResponse.setEntryResponse( Collections.singletonList( response ) );

        return formQuestionResponse;
    }
}
