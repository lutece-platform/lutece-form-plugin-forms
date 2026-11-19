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
package fr.paris.lutece.plugins.forms.web.admin;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.web.admin.AdminFeaturesPageJspBean;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Move of the questions of a step of a form in the back office
 */
public class FormQuestionJspBeanTest extends LuteceTestCase
{
    private Form _form;
    private Step _step;
    private Question _question;
    private FormDisplay _formDisplay;
    private FormDisplay _groupDisplay;
    private FormDisplay _displayInGroup;

    @Override
    protected void setUp( ) throws Exception
    {
        super.setUp( );

        _form = new Form( );
        _form.setTitle( "FormQuestionJspBeanTest" );
        _form.setBreadcrumbName( "forms.horizontalBreadcrumb" );
        FormHome.create( _form );

        _step = new Step( );
        _step.setTitle( "Step" );
        _step.setDescription( "" );
        _step.setIdForm( _form.getId( ) );
        _step.setInitial( true );
        StepHome.create( _step );

        // A question at the root of the step, a group holding one question: the move of the first into the second inserts it in a group that is not empty
        _question = new Question( );
        _question.setTitle( "Question to move" );
        _question.setCode( "question_to_move" );
        _question.setDescription( "" );
        _question.setColumnTitle( "" );
        _question.setIdStep( _step.getId( ) );
        QuestionHome.create( _question );

        _formDisplay = createDisplay( CompositeDisplayType.QUESTION, 0, 1 );
        _formDisplay.setCompositeId( _question.getId( ) );
        FormDisplayHome.update( _formDisplay );

        _groupDisplay = createDisplay( CompositeDisplayType.GROUP, 0, 2 );
        _displayInGroup = createDisplay( CompositeDisplayType.QUESTION, _groupDisplay.getId( ), 1 );
    }

    @Override
    protected void tearDown( ) throws Exception
    {
        FormDisplayHome.remove( _displayInGroup.getId( ) );
        FormDisplayHome.remove( _groupDisplay.getId( ) );
        FormDisplayHome.remove( _formDisplay.getId( ) );
        QuestionHome.remove( _question.getId( ) );
        StepHome.remove( _step.getId( ) );
        FormHome.remove( _form.getId( ) );

        super.tearDown( );
    }

    private FormDisplay createDisplay( CompositeDisplayType type, int nIdParent, int nDisplayOrder )
    {
        FormDisplay formDisplay = new FormDisplay( );
        formDisplay.setFormId( _form.getId( ) );
        formDisplay.setStepId( _step.getId( ) );
        formDisplay.setParentId( nIdParent );
        formDisplay.setDisplayOrder( nDisplayOrder );
        formDisplay.setCompositeType( type.getLabel( ) );
        FormDisplayHome.create( formDisplay );
        return formDisplay;
    }

    /**
     * Without position, the move into a group that is not empty is refused with a message and leaves the question where it was (it raised an
     * IndexOutOfBoundsException after having attached the question to the group)
     */
    @Test
    public void testMoveCompositeWithoutDisplayOrderIsRefused( ) throws ReflectiveOperationException
    {
        MockHttpServletResponse response = new MockHttpServletResponse( );
        FormQuestionJspBean bean = createBeanReadyToMove( response );

        bean.doMoveComposite( createMoveRequest( null ) );

        assertMoveRefused( response );
    }

    /**
     * A position that the target group does not offer is refused with a message and leaves the question where it was
     */
    @Test
    public void testMoveCompositeWithDisplayOrderOutOfTheGroupIsRefused( ) throws ReflectiveOperationException
    {
        for ( String strDisplayOrder : new String [ ] {
                "0", "3", "-1", "abc"
        } )
        {
            MockHttpServletResponse response = new MockHttpServletResponse( );
            FormQuestionJspBean bean = createBeanReadyToMove( response );

            bean.doMoveComposite( createMoveRequest( strDisplayOrder ) );

            assertMoveRefused( response );
        }
    }

    /**
     * A position offered by the move screen inserts the question in the group and renumbers it
     */
    @Test
    public void testMoveCompositeIntoGroupNotEmpty( ) throws ReflectiveOperationException
    {
        MockHttpServletResponse response = new MockHttpServletResponse( );
        FormQuestionJspBean bean = createBeanReadyToMove( response );

        bean.doMoveComposite( createMoveRequest( "1" ) );

        List<FormDisplay> listDisplayInGroup = FormDisplayHome.getFormDisplayListByParent( _step.getId( ), _groupDisplay.getId( ) );
        assertEquals( 2, listDisplayInGroup.size( ) );
        assertEquals( _formDisplay.getId( ), listDisplayInGroup.get( 0 ).getId( ) );
        assertEquals( 1, listDisplayInGroup.get( 0 ).getDisplayOrder( ) );
        assertEquals( _displayInGroup.getId( ), listDisplayInGroup.get( 1 ).getId( ) );
        assertEquals( 2, listDisplayInGroup.get( 1 ).getDisplayOrder( ) );
        assertEquals( 1, FormDisplayHome.findByPrimaryKey( _formDisplay.getId( ) ).getDepth( ) );
        String strLocation = response.getRedirectedUrl( );
        assertTrue( strLocation, strLocation.contains( "view=manageQuestions" ) );
    }

    /**
     * The bean as the move screen leaves it: the step and the group of the target validated
     */
    private FormQuestionJspBean createBeanReadyToMove( MockHttpServletResponse response ) throws ReflectiveOperationException
    {
        FormQuestionJspBean bean = new FormQuestionJspBean( );
        setField( MVCAdminJspBean.class, bean, "_response", response );
        setField( AdminFeaturesPageJspBean.class, bean, "_locale", Locale.FRENCH );
        bean._nIdStepTarget = _step.getId( );
        bean._nIdParentTarget = _groupDisplay.getId( );
        return bean;
    }

    private MockHttpServletRequest createMoveRequest( String strDisplayOrder )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( "id_display", String.valueOf( _formDisplay.getId( ) ) );
        request.addParameter( "stepValidated", "true" );
        request.addParameter( "groupValidated", "true" );
        request.addParameter( "id_step", String.valueOf( _step.getId( ) ) );
        request.addParameter( "id_parent", String.valueOf( _groupDisplay.getId( ) ) );
        if ( strDisplayOrder != null )
        {
            request.addParameter( "displayOrder", strDisplayOrder );
        }
        return request;
    }

    private void assertMoveRefused( MockHttpServletResponse response )
    {
        String strLocation = response.getRedirectedUrl( );
        assertTrue( strLocation, strLocation.contains( "view=moveComposite" ) );
        FormDisplay formDisplay = FormDisplayHome.findByPrimaryKey( _formDisplay.getId( ) );
        assertEquals( 0, formDisplay.getParentId( ) );
        assertEquals( 0, formDisplay.getDepth( ) );
        assertEquals( 1, formDisplay.getDisplayOrder( ) );
        List<FormDisplay> listDisplayInGroup = FormDisplayHome.getFormDisplayListByParent( _step.getId( ), _groupDisplay.getId( ) );
        assertEquals( 1, listDisplayInGroup.size( ) );
        assertEquals( 1, listDisplayInGroup.get( 0 ).getDisplayOrder( ) );
    }

    private static void setField( Class<?> clazz, Object bean, String strFieldName, Object value ) throws ReflectiveOperationException
    {
        Field field = clazz.getDeclaredField( strFieldName );
        field.setAccessible( true );
        field.set( bean, value );
    }
}
