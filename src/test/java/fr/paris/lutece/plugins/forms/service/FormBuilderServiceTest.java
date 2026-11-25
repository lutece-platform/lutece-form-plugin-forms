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
package fr.paris.lutece.plugins.forms.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

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
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormLayout;
import fr.paris.lutece.plugins.forms.business.Group;
import fr.paris.lutece.plugins.forms.business.GroupHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.business.Transition;
import fr.paris.lutece.plugins.forms.business.TransitionHome;
import fr.paris.lutece.plugins.forms.service.json.FormJsonService;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.inject.Inject;

/**
 * Tests of the form builder operations on the displays of a form
 */
public class FormBuilderServiceTest extends LuteceTestCase
{
    private static final String FORM_BREADCRUMB = "forms.horizontalBreadcrumb";

    @Inject
    private FormBuilderService _formBuilderService;

    @Inject
    private FormJsonService _formJsonService;

    private Form _form;
    private Step _step1;
    private Step _step2;
    private final List<FormDisplay> _listCreatedDisplays = new ArrayList<>( );

    /**
     * Creates a form with two steps for each test
     */
    @BeforeEach
    public void createForm( )
    {
        Form form = new Form( );
        form.setTitle( "form_builder_test" );
        form.setAvailabilityStartDate( new Timestamp( System.currentTimeMillis( ) ) );
        form.setBreadcrumbName( FORM_BREADCRUMB );
        _form = FormHome.create( form );
        _step1 = createStep( "step 1", true );
        _step2 = createStep( "step 2", false );
    }

    /**
     * Removes the data created by the test, so that the tests counting the groups or steps of the database are not affected
     */
    @AfterEach
    public void removeForm( )
    {
        for ( FormDisplay display : FormDisplayHome.getFormDisplayByForm( _form.getId( ) ) )
        {
            if ( CompositeDisplayType.GROUP.getLabel( ).equalsIgnoreCase( display.getCompositeType( ) ) )
            {
                GroupHome.remove( display.getCompositeId( ) );
            }
            FormDisplayHome.remove( display.getId( ) );
        }
        for ( FormDisplay display : _listCreatedDisplays )
        {
            GroupHome.remove( display.getCompositeId( ) );
        }
        _listCreatedDisplays.clear( );
        for ( Step step : StepHome.getStepsListByForm( _form.getId( ) ) )
        {
            TransitionHome.removeTransitionByStep( step.getId( ) );
            StepHome.remove( step.getId( ) );
        }
        FormHome.remove( _form.getId( ) );
    }

    /**
     * A display moved inside its parent takes the target position and its siblings are renumbered
     *
     * @throws FormBuilderException
     *             if the move is refused
     */
    @Test
    public void testReorderInSameParent( ) throws FormBuilderException
    {
        FormDisplay a = createGroupDisplay( _step1, 0, "A" );
        FormDisplay b = createGroupDisplay( _step1, 0, "B" );
        FormDisplay c = createGroupDisplay( _step1, 0, "C" );

        _formBuilderService.moveDisplay( _form.getId( ), c.getId( ), _step1.getId( ), 0, 1 );

        assertEquals( List.of( c.getId( ), a.getId( ), b.getId( ) ), childrenIds( _step1, 0 ) );
        assertEquals( List.of( 1, 2, 3 ), childrenOrders( _step1, 0 ) );
    }

    /**
     * In a step of two columns, a display moved to a column takes the target position within that column, and the siblings are ordered column after
     * column
     *
     * @throws FormBuilderException
     *             if the move is refused
     */
    @Test
    public void testMoveToColumn( ) throws FormBuilderException
    {
        _step1.setLayout( FormLayout.TWO_COLUMNS );
        StepHome.update( _step1 );
        FormDisplay a = createGroupDisplay( _step1, 0, "A" );
        FormDisplay b = createGroupDisplay( _step1, 0, "B" );
        FormDisplay c = createGroupDisplay( _step1, 0, "C" );

        _formBuilderService.moveDisplay( _form.getId( ), a.getId( ), _step1.getId( ), 0, 2, 1 );
        _formBuilderService.moveDisplay( _form.getId( ), c.getId( ), _step1.getId( ), 0, 2, 1 );

        assertEquals( List.of( b.getId( ), c.getId( ), a.getId( ) ), childrenIds( _step1, 0 ) );
        assertEquals( List.of( 1, 2, 3 ), childrenOrders( _step1, 0 ) );
        assertEquals( 1, FormDisplayHome.findByPrimaryKey( b.getId( ) ).getDisplayColumn( ) );
        assertEquals( 2, FormDisplayHome.findByPrimaryKey( c.getId( ) ).getDisplayColumn( ) );
        assertEquals( 2, FormDisplayHome.findByPrimaryKey( a.getId( ) ).getDisplayColumn( ) );
    }

    /**
     * In a step of one column, a column the layout does not have is read as the first column
     *
     * @throws FormBuilderException
     *             if the move is refused
     */
    @Test
    public void testMoveToMissingColumn( ) throws FormBuilderException
    {
        FormDisplay a = createGroupDisplay( _step1, 0, "A" );
        FormDisplay b = createGroupDisplay( _step1, 0, "B" );

        _formBuilderService.moveDisplay( _form.getId( ), b.getId( ), _step1.getId( ), 0, 2, 1 );

        assertEquals( List.of( b.getId( ), a.getId( ) ), childrenIds( _step1, 0 ) );
        assertEquals( 1, FormDisplayHome.findByPrimaryKey( b.getId( ) ).getDisplayColumn( ) );
    }

    /**
     * A display moved into a group gets the group as parent, a depth one level deeper, and the origin parent is renumbered
     *
     * @throws FormBuilderException
     *             if the move is refused
     */
    @Test
    public void testMoveIntoGroup( ) throws FormBuilderException
    {
        FormDisplay a = createGroupDisplay( _step1, 0, "A" );
        FormDisplay b = createGroupDisplay( _step1, 0, "B" );
        FormDisplay c = createGroupDisplay( _step1, 0, "C" );

        _formBuilderService.moveDisplay( _form.getId( ), b.getId( ), _step1.getId( ), a.getId( ), 1 );

        FormDisplay moved = FormDisplayHome.findByPrimaryKey( b.getId( ) );
        assertEquals( a.getId( ), moved.getParentId( ) );
        assertEquals( 1, moved.getDepth( ) );
        assertEquals( List.of( a.getId( ), c.getId( ) ), childrenIds( _step1, 0 ) );
        assertEquals( List.of( 1, 2 ), childrenOrders( _step1, 0 ) );
    }

    /**
     * A group cannot be dropped into one of its descendants
     */
    @Test
    public void testRefuseGroupIntoDescendant( )
    {
        FormDisplay a = createGroupDisplay( _step1, 0, "A" );
        FormDisplay d = createGroupDisplay( _step1, a.getId( ), "D" );

        FormBuilderException exception = assertThrows( FormBuilderException.class,
                ( ) -> _formBuilderService.moveDisplay( _form.getId( ), a.getId( ), _step1.getId( ), d.getId( ), 1 ) );
        assertEquals( FormBuilderService.ERROR_GROUP_INTO_ITSELF, exception.getMessageKey( ) );
    }

    /**
     * A group moved to another step takes its descendants with it
     *
     * @throws FormBuilderException
     *             if the move is refused
     */
    @Test
    public void testMoveAcrossSteps( ) throws FormBuilderException
    {
        FormDisplay a = createGroupDisplay( _step1, 0, "A" );
        FormDisplay d = createGroupDisplay( _step1, a.getId( ), "D" );

        _formBuilderService.moveDisplay( _form.getId( ), a.getId( ), _step2.getId( ), 0, Integer.MAX_VALUE );

        assertEquals( _step2.getId( ), FormDisplayHome.findByPrimaryKey( a.getId( ) ).getStepId( ) );
        assertEquals( _step2.getId( ), FormDisplayHome.findByPrimaryKey( d.getId( ) ).getStepId( ) );
        assertEquals( _step2.getId( ), GroupHome.findByPrimaryKey( d.getCompositeId( ) ).getIdStep( ) );
        assertTrue( childrenIds( _step1, 0 ).isEmpty( ) );
        assertEquals( List.of( a.getId( ) ), childrenIds( _step2, 0 ) );
    }

    /**
     * The display created by this session after the known highest identifier is placed at the drop position; a display created meanwhile by someone
     * else in the same parent is never taken
     *
     * @throws FormBuilderException
     *             if the placement is refused
     */
    @Test
    public void testPlaceNewDisplay( ) throws FormBuilderException
    {
        FormDisplay a = createGroupDisplay( _step1, 0, "A" );
        int nKnownMaxId = _formBuilderService.getMaxIdDisplay( _form.getId( ) );

        assertEquals( 0, _formBuilderService.placeNewDisplay( _form.getId( ), _step1.getId( ), 0, nKnownMaxId, 1, 1, 0, 0 ) );

        FormDisplay created = createGroupDisplay( _step1, 0, "E" );
        FormDisplay createdByOther = createGroupDisplay( _step1, 0, "Other" );

        assertEquals( 0, _formBuilderService.placeNewDisplay( _form.getId( ), _step1.getId( ), 0, nKnownMaxId, 1, 1, 0, 0 ) );

        int nIdPlaced = _formBuilderService.placeNewDisplay( _form.getId( ), _step1.getId( ), 0, nKnownMaxId, 1, 1, 0, created.getCompositeId( ) );

        assertEquals( created.getId( ), nIdPlaced );
        assertEquals( List.of( created.getId( ), a.getId( ), createdByOther.getId( ) ), childrenIds( _step1, 0 ) );
    }

    /**
     * A display of another form is refused
     */
    @Test
    public void testRefuseDisplayOfAnotherForm( )
    {
        FormDisplay a = createGroupDisplay( _step1, 0, "A" );

        FormBuilderException exception = assertThrows( FormBuilderException.class,
                ( ) -> _formBuilderService.moveDisplay( _form.getId( ) + 1000, a.getId( ), _step1.getId( ), 0, 1 ) );
        assertEquals( FormBuilderService.ERROR_DISPLAY_NOT_FOUND, exception.getMessageKey( ) );
    }

    /**
     * The initial step cannot be removed
     */
    @Test
    public void testRefuseRemoveInitialStep( )
    {
        FormBuilderException exception = assertThrows( FormBuilderException.class,
                ( ) -> _formBuilderService.removeStep( _form.getId( ), _step1.getId( ) ) );
        assertEquals( FormBuilderService.ERROR_REMOVE_INITIAL_STEP, exception.getMessageKey( ) );
    }

    /**
     * The copy of the initial step is not initial: the form keeps a single initial step
     *
     * @throws FormBuilderException
     *             if the duplication is refused
     */
    @Test
    public void testDuplicateInitialStepIsNotInitial( ) throws FormBuilderException
    {
        createGroupDisplay( _step1, 0, "A" );

        _formBuilderService.duplicateStep( _form.getId( ), _step1.getId( ), Locale.FRENCH );

        List<Step> listSteps = StepHome.getStepsListByForm( _form.getId( ) );
        assertEquals( 3, listSteps.size( ) );
        assertEquals( 1, listSteps.stream( ).filter( Step::isInitial ).count( ) );
        assertTrue( StepHome.findByPrimaryKey( _step1.getId( ) ).isInitial( ) );
    }

    /**
     * A step imported after another one gets the first transition of that step, the existing transitions keeping their order behind it
     *
     * @throws Exception
     *             if the export or the import fails
     */
    @Test
    public void testImportStepAfterGetsFirstPriority( ) throws Exception
    {
        Transition existing = new Transition( );
        existing.setFromStep( _step1.getId( ) );
        existing.setNextStep( _step2.getId( ) );
        TransitionHome.create( existing );
        String strJson = _formJsonService.jsonExportStep( _form.getId( ), _step2.getId( ) );

        int nIdImported = _formBuilderService.importStep( _form.getId( ), strJson, 0, _step1.getId( ), Locale.FRENCH );

        List<Transition> listTransitions = TransitionHome.getTransitionsListFromStep( _step1.getId( ) );
        assertEquals( 2, listTransitions.size( ) );
        assertEquals( nIdImported, listTransitions.get( 0 ).getNextStep( ) );
        assertEquals( 1, listTransitions.get( 0 ).getPriority( ) );
        assertEquals( _step2.getId( ), listTransitions.get( 1 ).getNextStep( ) );
        assertEquals( 2, listTransitions.get( 1 ).getPriority( ) );
        assertFalse( StepHome.findByPrimaryKey( nIdImported ).isInitial( ) );
    }

    /**
     * Removing a group removes its descendants and renumbers its former siblings
     *
     * @throws FormBuilderException
     *             if the removal is refused
     */
    @Test
    public void testRemoveGroupWithDescendants( ) throws FormBuilderException
    {
        FormDisplay a = createGroupDisplay( _step1, 0, "A" );
        FormDisplay d = createGroupDisplay( _step1, a.getId( ), "D" );
        FormDisplay b = createGroupDisplay( _step1, 0, "B" );

        _formBuilderService.removeDisplay( _form.getId( ), a.getId( ) );

        assertNull( FormDisplayHome.findByPrimaryKey( a.getId( ) ) );
        assertNull( FormDisplayHome.findByPrimaryKey( d.getId( ) ) );
        assertEquals( List.of( b.getId( ) ), childrenIds( _step1, 0 ) );
        assertEquals( List.of( 1 ), childrenOrders( _step1, 0 ) );
    }

    /**
     * A group cannot leave its step nor be removed while one of its questions conditions a display that stays behind
     */
    @Test
    public void testRefuseBreakingCondition( )
    {
        FormDisplay group = createGroupDisplay( _step1, 0, "G" );
        FormDisplay source = createQuestionDisplay( _step1, group.getId( ), "source" );
        FormDisplay conditioned = createQuestionDisplay( _step1, 0, "conditioned" );

        Control control = new Control( );
        control.setControlType( ControlType.CONDITIONAL.getLabel( ) );
        control.setIdControlTarget( conditioned.getId( ) );
        control.setListIdQuestion( Set.of( source.getCompositeId( ) ) );
        control.setValidatorName( "forms.patternValidator" );
        ControlHome.create( control );
        try
        {
            FormBuilderException moveRefused = assertThrows( FormBuilderException.class,
                    ( ) -> _formBuilderService.moveDisplay( _form.getId( ), group.getId( ), _step2.getId( ), 0, 1 ) );
            assertEquals( FormBuilderService.ERROR_CROSS_STEP_CONDITION, moveRefused.getMessageKey( ) );

            FormBuilderException removeRefused = assertThrows( FormBuilderException.class,
                    ( ) -> _formBuilderService.removeDisplay( _form.getId( ), group.getId( ) ) );
            assertEquals( FormBuilderService.ERROR_CONDITION_DEPENDENCY, removeRefused.getMessageKey( ) );

            assertEquals( _step1.getId( ), FormDisplayHome.findByPrimaryKey( group.getId( ) ).getStepId( ) );
        }
        finally
        {
            ControlHome.remove( control.getId( ) );
            QuestionHome.remove( source.getCompositeId( ) );
            QuestionHome.remove( conditioned.getCompositeId( ) );
        }
    }

    /**
     * Creates a question without entry and its display, at the end of the given parent
     *
     * @param step
     *            the step
     * @param nIdParent
     *            the parent display, 0 for the root of the step
     * @param strTitle
     *            the title of the question
     * @return the created display
     */
    private FormDisplay createQuestionDisplay( Step step, int nIdParent, String strTitle )
    {
        Question question = new Question( );
        question.setTitle( strTitle );
        question.setColumnTitle( strTitle );
        question.setCode( strTitle + "_" + System.nanoTime( ) );
        question.setIdStep( step.getId( ) );
        QuestionHome.create( question );

        FormDisplay parent = nIdParent > 0 ? FormDisplayHome.findByPrimaryKey( nIdParent ) : null;
        FormDisplay display = new FormDisplay( );
        display.setFormId( _form.getId( ) );
        display.setStepId( step.getId( ) );
        display.setParentId( nIdParent );
        display.setCompositeId( question.getId( ) );
        display.setCompositeType( CompositeDisplayType.QUESTION.getLabel( ) );
        display.setDepth( parent != null ? parent.getDepth( ) + 1 : 0 );
        FormDisplayHome.create( display );
        return FormDisplayHome.findByPrimaryKey( display.getId( ) );
    }

    /**
     * Creates a step of the test form
     *
     * @param strTitle
     *            the title
     * @param bInitial
     *            true for the initial step
     * @return the created step
     */
    private Step createStep( String strTitle, boolean bInitial )
    {
        Step step = new Step( );
        step.setIdForm( _form.getId( ) );
        step.setTitle( strTitle );
        step.setInitial( bInitial );
        step.setFinal( !bInitial );
        return StepHome.create( step );
    }

    /**
     * Creates a group and its display, at the end of the given parent
     *
     * @param step
     *            the step
     * @param nIdParent
     *            the parent display, 0 for the root of the step
     * @param strTitle
     *            the title of the group
     * @return the created display
     */
    private FormDisplay createGroupDisplay( Step step, int nIdParent, String strTitle )
    {
        Group group = new Group( );
        group.setTitle( strTitle );
        group.setIdStep( step.getId( ) );
        group.setIterationMin( 1 );
        group.setIterationMax( 1 );
        GroupHome.create( group );

        FormDisplay parent = nIdParent > 0 ? FormDisplayHome.findByPrimaryKey( nIdParent ) : null;
        FormDisplay display = new FormDisplay( );
        display.setFormId( _form.getId( ) );
        display.setStepId( step.getId( ) );
        display.setParentId( nIdParent );
        display.setCompositeId( group.getId( ) );
        display.setCompositeType( CompositeDisplayType.GROUP.getLabel( ) );
        display.setDepth( parent != null ? parent.getDepth( ) + 1 : 0 );
        FormDisplayHome.create( display );
        _listCreatedDisplays.add( display );
        return FormDisplayHome.findByPrimaryKey( display.getId( ) );
    }

    /**
     * Returns the identifiers of the children of a parent, in display order
     *
     * @param step
     *            the step
     * @param nIdParent
     *            the parent display, 0 for the root of the step
     * @return the identifiers
     */
    private List<Integer> childrenIds( Step step, int nIdParent )
    {
        return FormDisplayHome.getFormDisplayListByParent( step.getId( ), nIdParent ).stream( ).map( FormDisplay::getId ).collect( Collectors.toList( ) );
    }

    /**
     * Returns the display orders of the children of a parent
     *
     * @param step
     *            the step
     * @param nIdParent
     *            the parent display, 0 for the root of the step
     * @return the display orders
     */
    private List<Integer> childrenOrders( Step step, int nIdParent )
    {
        return FormDisplayHome.getFormDisplayListByParent( step.getId( ), nIdParent ).stream( ).map( FormDisplay::getDisplayOrder )
                .collect( Collectors.toList( ) );
    }
}
