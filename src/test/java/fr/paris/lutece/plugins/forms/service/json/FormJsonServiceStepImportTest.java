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
package fr.paris.lutece.plugins.forms.service.json;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.ControlType;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.exception.FormJsonImportException;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Copy and import of a step of a form
 */
public class FormJsonServiceStepImportTest extends LuteceTestCase
{
    @Inject
    private FormJsonService _formJsonService;
    private Form _form;

    /**
     * Creates a form
     */
    @BeforeEach
    protected void createForm( )
    {
        _form = new Form( );
        _form.setTitle( "FormJsonServiceStepImportTest" );
        _form.setBreadcrumbName( "forms.horizontalBreadcrumb" );
        FormHome.create( _form );
    }

    /**
     * Removes the steps and the form
     */
    @AfterEach
    protected void removeForm( )
    {
        StepHome.getStepsListByForm( _form.getId( ) ).forEach( step -> StepHome.remove( step.getId( ) ) );
        FormHome.remove( _form.getId( ) );
    }

    /**
     * The copy of the initial step of a form is not initial: a form keeps a single initial step
     *
     * @throws Exception
     *             if the step cannot be exported
     */
    @Test
    public void testCopyOfInitialStepIsNotInitial( ) throws Exception
    {
        Step initial = new Step( );
        initial.setTitle( "Initial" );
        initial.setDescription( "" );
        initial.setIdForm( _form.getId( ) );
        initial.setInitial( true );
        StepHome.create( initial );

        String strJson = _formJsonService.jsonExportStep( _form.getId( ), initial.getId( ) );
        _formJsonService.jsonImportStep( _form.getId( ), strJson, Locale.FRENCH );

        assertEquals( 2, StepHome.getStepsListByForm( _form.getId( ) ).size( ) );
        assertEquals( 1, StepHome.getStepsListByForm( _form.getId( ) ).stream( ).filter( Step::isInitial ).count( ) );
    }

    /**
     * JSON data without the key "step" is refused and creates no step
     */
    @Test
    public void testImportWithoutStepIsRefused( )
    {
        assertThrows( FormJsonImportException.class, ( ) -> _formJsonService.jsonImportStep( _form.getId( ), "{}", Locale.FRENCH ) );
        assertEquals( 0, StepHome.getStepsListByForm( _form.getId( ) ).size( ) );
    }

    /**
     * The export of a step (used by its duplication) keeps the validation controls of its questions and the conditions of its displays, not the
     * conditions that its questions drive on the displays of other steps or on transitions
     *
     * @throws Exception
     *             if the step cannot be exported
     */
    @Test
    public void testStepExportKeepsOnlyTheControlsOfTheStep( ) throws Exception
    {
        Step step1 = createStep( "Step 1", true );
        Step step2 = createStep( "Step 2", false );
        List<Integer> listIdEntry = new ArrayList<>( );
        List<Question> listQuestion = new ArrayList<>( );
        List<FormDisplay> listDisplay = new ArrayList<>( );
        List<Control> listControl = new ArrayList<>( );
        try
        {
            Question q1 = createQuestion( step1, listIdEntry, listQuestion );
            Question d = createQuestion( step2, listIdEntry, listQuestion );
            FormDisplay displayQ1 = createDisplay( step1, q1, listDisplay );
            FormDisplay displayD = createDisplay( step2, d, listDisplay );

            Control validation = createControl( ControlType.VALIDATION, q1.getId( ), q1, listControl );
            Control conditionInStep = createControl( ControlType.CONDITIONAL, displayQ1.getId( ), q1, listControl );
            createControl( ControlType.CONDITIONAL, displayD.getId( ), q1, listControl );
            createControl( ControlType.TRANSITION, 999999, q1, listControl );

            Set<Integer> setExported = _formJsonService.jsonExportStepAsData( _form.getId( ), step1.getId( ) ).getControlList( ).stream( )
                    .map( Control::getId ).collect( Collectors.toSet( ) );

            assertEquals( Set.of( validation.getId( ), conditionInStep.getId( ) ), setExported );
        }
        finally
        {
            listControl.forEach( control -> ControlHome.remove( control.getId( ) ) );
            listDisplay.forEach( display -> FormDisplayHome.remove( display.getId( ) ) );
            listQuestion.forEach( question -> QuestionHome.remove( question.getId( ) ) );
            listIdEntry.forEach( EntryHome::remove );
        }
    }

    private Step createStep( String strTitle, boolean bInitial )
    {
        Step step = new Step( );
        step.setTitle( strTitle );
        step.setDescription( "" );
        step.setIdForm( _form.getId( ) );
        step.setInitial( bInitial );
        StepHome.create( step );
        return step;
    }

    private Question createQuestion( Step step, List<Integer> listIdEntry, List<Question> listQuestion )
    {
        Entry entry = new Entry( );
        EntryType entryType = new EntryType( );
        entryType.setIdType( 1 );
        entry.setEntryType( entryType );
        entry.setResourceType( Form.RESOURCE_TYPE );
        entry.setIdResource( _form.getId( ) );
        EntryHome.create( entry );
        listIdEntry.add( entry.getIdEntry( ) );

        Question question = new Question( );
        question.setTitle( "Question of " + step.getTitle( ) );
        question.setCode( "question_" + entry.getIdEntry( ) );
        question.setColumnTitle( "column" );
        question.setIdEntry( entry.getIdEntry( ) );
        question.setIdStep( step.getId( ) );
        QuestionHome.create( question );
        listQuestion.add( question );
        return question;
    }

    private FormDisplay createDisplay( Step step, Question question, List<FormDisplay> listDisplay )
    {
        FormDisplay display = new FormDisplay( );
        display.setFormId( _form.getId( ) );
        display.setStepId( step.getId( ) );
        display.setCompositeId( question.getId( ) );
        display.setCompositeType( CompositeDisplayType.QUESTION.getLabel( ) );
        display.setParentId( 0 );
        display.setDisplayOrder( 1 );
        FormDisplayHome.create( display );
        listDisplay.add( display );
        return display;
    }

    private Control createControl( ControlType controlType, int nIdTarget, Question source, List<Control> listControl )
    {
        Control control = new Control( );
        control.setControlType( controlType.getLabel( ) );
        control.setIdControlTarget( nIdTarget );
        control.setListIdQuestion( Set.of( source.getId( ) ) );
        control.setValidatorName( "forms.mandatoryValidator" );
        control.setValue( "" );
        control.setErrorMessage( "error" );
        ControlHome.create( control );
        listControl.add( control );
        return control;
    }
}
