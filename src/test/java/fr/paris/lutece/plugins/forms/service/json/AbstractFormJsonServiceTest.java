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
package fr.paris.lutece.plugins.forms.service.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlGroup;
import fr.paris.lutece.plugins.forms.business.ControlMapping;
import fr.paris.lutece.plugins.forms.business.ControlType;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.Group;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.Transition;
import fr.paris.lutece.plugins.forms.service.IFormDatabaseService;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import junit.framework.TestCase;

/**
 * Tests the identifier remapping performed when a form or a step is imported.
 * 
 * An imported element whose reference cannot be remapped does not belong to the copy : it has to be dropped, never created pointing at the source form.
 */
public class AbstractFormJsonServiceTest extends TestCase
{
    private static final int NEW_ID_FORM = 100;
    private static final int ID_STEP = 1;
    private static final int ID_QUESTION = 20;
    private static final int ID_UNKNOWN = 999;

    private FormDatabaseServiceStub _databaseService;
    private AbstractFormJsonService _service;

    @Override
    protected void setUp( ) throws Exception
    {
        super.setUp( );

        _databaseService = new FormDatabaseServiceStub( );
        _service = new AbstractFormJsonService( _databaseService )
        {
            @Override
            protected List<FormDisplay> getAllFormDisplays( int idForm, int idStep )
            {
                return new ArrayList<>( );
            }
        };
    }

    /**
     * A consistent step is imported without losing anything.
     */
    public void testImportStepsKeepsEveryElementOfAKnownStep( )
    {
        Step step = newStep( ID_STEP );
        Group group = newGroup( 10, ID_STEP );
        Question question = newQuestion( ID_QUESTION, ID_STEP );
        FormDisplay formDisplay = newQuestionDisplay( 30, ID_STEP, ID_QUESTION );

        List<Group> groupList = newList( group );
        List<Question> questionList = newList( question );
        List<FormDisplay> formDisplayList = newList( formDisplay );

        _service.importSteps( NEW_ID_FORM, Collections.singletonList( step ), groupList, null, questionList, formDisplayList );

        assertEquals( 1, groupList.size( ) );
        assertEquals( 1, questionList.size( ) );
        assertEquals( 1, formDisplayList.size( ) );
        assertEquals( step.getId( ), group.getIdStep( ) );
        assertEquals( step.getId( ), question.getIdStep( ) );
        assertEquals( step.getId( ), formDisplay.getStepId( ) );
    }

    /**
     * An element attached to a step that is not part of the import does not belong to the copy.
     */
    public void testImportStepsDropsElementsOfAnUnknownStep( )
    {
        Step step = newStep( ID_STEP );

        List<Group> groupList = newList( newGroup( 10, ID_UNKNOWN ) );
        List<Question> questionList = newList( newQuestion( ID_QUESTION, ID_UNKNOWN ) );
        List<FormDisplay> formDisplayList = newList( newQuestionDisplay( 30, ID_UNKNOWN, ID_QUESTION ) );
        List<Transition> transitionList = newList( newTransition( 40, ID_STEP, ID_UNKNOWN ) );

        _service.importSteps( NEW_ID_FORM, Collections.singletonList( step ), groupList, transitionList, questionList, formDisplayList );

        assertTrue( groupList.isEmpty( ) );
        assertTrue( questionList.isEmpty( ) );
        assertTrue( formDisplayList.isEmpty( ) );
        assertTrue( transitionList.isEmpty( ) );
    }

    /**
     * A validation control whose target question is missing cannot be remapped at all.
     */
    public void testImportQuestionsDropsControlWithoutItsTargetQuestion( )
    {
        List<Question> questionList = newList( newQuestion( ID_QUESTION, ID_STEP ) );
        List<Control> controlList = newList( newControl( 50, ControlType.VALIDATION, ID_UNKNOWN, ID_QUESTION ) );

        _service.importQuestions( NEW_ID_FORM, questionList, controlList, new ArrayList<>( ), new ArrayList<>( ), null );

        assertTrue( controlList.isEmpty( ) );
    }

    /**
     * A control linked to several questions keeps the links that can be remapped.
     */
    public void testImportQuestionsKeepsTheResolvableQuestionsOfAControl( )
    {
        Question question = newQuestion( ID_QUESTION, ID_STEP );
        Control control = newControl( 50, ControlType.VALIDATION, ID_QUESTION, ID_QUESTION, ID_UNKNOWN );

        List<Question> questionList = newList( question );
        List<Control> controlList = newList( control );

        _service.importQuestions( NEW_ID_FORM, questionList, controlList, new ArrayList<>( ), new ArrayList<>( ), null );

        assertEquals( 1, controlList.size( ) );
        assertEquals( 1, control.getListIdQuestion( ).size( ) );
        assertEquals( Integer.valueOf( question.getId( ) ), control.getListIdQuestion( ).iterator( ).next( ) );
        assertEquals( question.getId( ), control.getIdControlTarget( ) );
    }

    /**
     * A control attached to no question cannot be evaluated : the engine reads the first controlling question of a control without guarding against an empty
     * list, so persisting such a control would only break the screens that read it afterwards.
     */
    public void testImportQuestionsDropsAControlWithoutQuestionList( )
    {
        Control controlWithNullList = newControl( 50, ControlType.CONDITIONAL, ID_QUESTION );
        controlWithNullList.setListIdQuestion( null );

        Control controlWithEmptyList = newControl( 51, ControlType.CONDITIONAL, ID_QUESTION );

        List<Question> questionList = newList( newQuestion( ID_QUESTION, ID_STEP ) );
        List<Control> controlList = newList( controlWithNullList, controlWithEmptyList );

        _service.importQuestions( NEW_ID_FORM, questionList, controlList, new ArrayList<>( ), new ArrayList<>( ), null );

        assertTrue( controlList.isEmpty( ) );
    }

    /**
     * A control mapping is dropped along with the question it refers to.
     */
    public void testImportQuestionsDropsTheMappingOfAnUnknownQuestion( )
    {
        List<Question> questionList = newList( newQuestion( ID_QUESTION, ID_STEP ) );
        List<ControlMapping> controlMappingList = newList( newControlMapping( 50, ID_UNKNOWN ) );

        _service.importQuestions( NEW_ID_FORM, questionList, new ArrayList<>( ), controlMappingList, new ArrayList<>( ), null );

        assertTrue( controlMappingList.isEmpty( ) );
    }

    /**
     * The display is already created when its parent turns out to be missing : it is moved to the root of its step.
     */
    public void testImportFormDisplayMovesAnOrphanDisplayToTheRoot( )
    {
        FormDisplay formDisplay = newQuestionDisplay( 30, ID_STEP, ID_QUESTION );
        formDisplay.setParentId( ID_UNKNOWN );

        List<FormDisplay> formDisplayList = newList( formDisplay );

        _service.importFormDisplay( NEW_ID_FORM, formDisplayList, new ArrayList<>( ) );

        assertEquals( 1, formDisplayList.size( ) );
        assertEquals( 0, formDisplay.getParentId( ) );
        assertEquals( NEW_ID_FORM, formDisplay.getFormId( ) );
    }

    /**
     * A conditional control whose target display is missing would otherwise keep pointing at the source form.
     */
    public void testImportFormDisplayDropsConditionalControlWithoutItsTarget( )
    {
        List<FormDisplay> formDisplayList = newList( newQuestionDisplay( 30, ID_STEP, ID_QUESTION ) );
        List<Control> controlList = newList( newControl( 50, ControlType.CONDITIONAL, ID_UNKNOWN, ID_QUESTION ) );

        _service.importFormDisplay( NEW_ID_FORM, formDisplayList, controlList );

        assertTrue( controlList.isEmpty( ) );
    }

    /**
     * A control group is remapped like any other reference.
     */
    public void testImportControlsRemapsTheControlGroup( )
    {
        ControlGroup controlGroup = newControlGroup( 60 );
        Control control = newControl( 50, ControlType.VALIDATION, ID_QUESTION, ID_QUESTION );
        control.setIdControlGroup( 60 );

        _service.importControls( newList( control ), new ArrayList<>( ), newList( controlGroup ) );

        assertEquals( controlGroup.getId( ), control.getIdControlGroup( ) );
    }

    /**
     * A control whose control group is missing is detached, instead of pointing at a group of the source form.
     */
    public void testImportControlsDetachesControlWithoutItsGroup( )
    {
        Control control = newControl( 50, ControlType.VALIDATION, ID_QUESTION, ID_QUESTION );
        control.setIdControlGroup( ID_UNKNOWN );

        _service.importControls( newList( control ), new ArrayList<>( ), new ArrayList<>( ) );

        assertEquals( 0, control.getIdControlGroup( ) );
    }

    /**
     * A mapping whose control has been dropped must not be created.
     */
    public void testImportControlsSkipsTheMappingOfADroppedControl( )
    {
        List<ControlMapping> controlMappingList = newList( newControlMapping( ID_UNKNOWN, ID_QUESTION ) );

        _service.importControls( new ArrayList<>( ), controlMappingList, new ArrayList<>( ) );

        assertTrue( _databaseService.getCreatedMappingList( ).isEmpty( ) );
    }

    @SafeVarargs
    private static <T> List<T> newList( T... items )
    {
        return new ArrayList<>( Arrays.asList( items ) );
    }

    private static Step newStep( int nId )
    {
        Step step = new Step( );
        step.setId( nId );

        return step;
    }

    private static Group newGroup( int nId, int nIdStep )
    {
        Group group = new Group( );
        group.setId( nId );
        group.setIdStep( nIdStep );

        return group;
    }

    private static Question newQuestion( int nId, int nIdStep )
    {
        Entry entry = new Entry( );
        entry.setCode( "question_" + nId );
        entry.setFields( new ArrayList<Field>( ) );

        Question question = new Question( );
        question.setId( nId );
        question.setIdStep( nIdStep );
        question.setEntry( entry );

        return question;
    }

    private static FormDisplay newQuestionDisplay( int nId, int nIdStep, int nIdQuestion )
    {
        FormDisplay formDisplay = new FormDisplay( );
        formDisplay.setId( nId );
        formDisplay.setStepId( nIdStep );
        formDisplay.setCompositeId( nIdQuestion );
        formDisplay.setCompositeType( CompositeDisplayType.QUESTION.getLabel( ) );

        return formDisplay;
    }

    private static Transition newTransition( int nId, int nFromStep, int nNextStep )
    {
        Transition transition = new Transition( );
        transition.setId( nId );
        transition.setFromStep( nFromStep );
        transition.setNextStep( nNextStep );

        return transition;
    }

    private static Control newControl( int nId, ControlType controlType, int nIdControlTarget, int... listIdQuestion )
    {
        Control control = new Control( );
        control.setId( nId );
        control.setControlType( controlType.getLabel( ) );
        control.setIdControlTarget( nIdControlTarget );

        Set<Integer> setIdQuestion = new HashSet<>( );
        for ( int nIdQuestion : listIdQuestion )
        {
            setIdQuestion.add( nIdQuestion );
        }
        control.setListIdQuestion( setIdQuestion );

        return control;
    }

    private static ControlGroup newControlGroup( int nId )
    {
        ControlGroup controlGroup = new ControlGroup( );
        controlGroup.setId( nId );

        return controlGroup;
    }

    private static ControlMapping newControlMapping( int nIdControl, int nIdQuestion )
    {
        ControlMapping controlMapping = new ControlMapping( );
        controlMapping.setIdControl( nIdControl );
        controlMapping.setIdQuestion( nIdQuestion );

        return controlMapping;
    }

    /**
     * In memory database service : every created element receives a new identifier, high enough not to collide with the imported ones.
     */
    private static final class FormDatabaseServiceStub implements IFormDatabaseService
    {
        private int _nNextId = 1000;
        private final List<ControlMapping> _listCreatedMapping = new ArrayList<>( );

        List<ControlMapping> getCreatedMappingList( )
        {
            return _listCreatedMapping;
        }

        @Override
        public void createStep( Step step )
        {
            step.setId( _nNextId++ );
        }

        @Override
        public void createQuestion( Question question )
        {
            question.setId( _nNextId++ );
        }

        @Override
        public void createGroup( Group group )
        {
            group.setId( _nNextId++ );
        }

        @Override
        public void createFormDisplay( FormDisplay formDisplay )
        {
            formDisplay.setId( _nNextId++ );
        }

        @Override
        public void createControl( Control control )
        {
            control.setId( _nNextId++ );
        }

        @Override
        public void createControlGroup( ControlGroup controlGroup )
        {
            controlGroup.setId( _nNextId++ );
        }

        @Override
        public void createEntry( Entry entry )
        {
            entry.setIdEntry( _nNextId++ );
        }

        @Override
        public void createField( Field field )
        {
            field.setIdField( _nNextId++ );
        }

        @Override
        public void createMappingControl( int nIdcontrol, int nIdQuestion, String strValue )
        {
            _listCreatedMapping.add( newControlMapping( nIdcontrol, nIdQuestion ) );
        }

        @Override
        public List<Question> getListQuestionByForm( int nIdForm )
        {
            return new ArrayList<>( );
        }

        @Override
        public Step findStepByPrimaryKey( int idStep )
        {
            return null;
        }

        @Override
        public Question findQuestionByPrimaryKey( int idQuestion )
        {
            return null;
        }

        @Override
        public Entry findEntryByPrimaryKey( int idEntry )
        {
            return null;
        }

        @Override
        public Group findGroupByPrimaryKey( int idGroup )
        {
            return null;
        }

        @Override
        public Field findFieldByPrimaryKey( int idField )
        {
            return null;
        }

        @Override
        public FormDisplay findDisplayByPrimaryKey( int idDisplay )
        {
            return null;
        }

        @Override
        public Entry copyEntry( Entry entry )
        {
            return entry;
        }

        @Override
        public void updateGroup( Group group )
        {
            // no persistence in this stub
        }

        @Override
        public void updateEntry( Entry entry )
        {
            // no persistence in this stub
        }

        @Override
        public void updateField( Field field )
        {
            // no persistence in this stub
        }

        @Override
        public void updateQuestion( Question question )
        {
            // no persistence in this stub
        }

        @Override
        public void updateFormDisplay( FormDisplay formDisplay )
        {
            // no persistence in this stub
        }

        @Override
        public List<FormDisplay> getFormDisplayListByParent( int nIdStep, int nIdParent )
        {
            return new ArrayList<>( );
        }

        @Override
        public List<Control> getControlByQuestion( int nIdQuestion )
        {
            return new ArrayList<>( );
        }

        @Override
        public FormDisplay getFormDisplayByFormStepAndComposite( int nIdForm, int nIdStep, int nIdComposite )
        {
            return null;
        }

        @Override
        public List<Group> getGroupsListByIdStepList( List<Integer> idStepList )
        {
            return new ArrayList<>( );
        }

        @Override
        public List<Question> getQuestionsListByStep( int nIdStep )
        {
            return new ArrayList<>( );
        }

        @Override
        public Integer findIdReferenceItemByIdField( int idField )
        {
            return 0;
        }

        @Override
        public List<ControlMapping> getControlMappingListByIdControl( int nIdControl )
        {
            return new ArrayList<>( );
        }
    }
}
