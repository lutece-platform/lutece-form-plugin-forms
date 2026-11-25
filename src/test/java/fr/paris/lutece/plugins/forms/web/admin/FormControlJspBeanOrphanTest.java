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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.ControlType;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.business.Transition;
import fr.paris.lutece.plugins.forms.business.TransitionHome;
import fr.paris.lutece.plugins.forms.service.event.ControlEvent;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.web.admin.AdminFeaturesPageJspBean;
import fr.paris.lutece.test.AdminUserUtils;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

/**
 * Removal of a control from the screen, including a control whose target (transition, display) no longer exists: since
 * LUT-35553 it was refused and the orphan control could only be removed in SQL.
 */
public class FormControlJspBeanOrphanTest extends LuteceTestCase
{
    private static final int ID_MISSING_TARGET = 987654;

    @Inject
    private Event<ControlEvent> _controlEvent;

    private Form _form;
    private Step _step;
    private Question _question;
    private Transition _transition;
    private Control _control;

    @BeforeEach
    protected void createData( )
    {
        _form = new Form( );
        _form.setTitle( "FormControlJspBeanOrphanTest" );
        _form.setBreadcrumbName( "forms.horizontalBreadcrumb" );
        FormHome.create( _form );

        _step = new Step( );
        _step.setTitle( "Step" );
        _step.setDescription( "" );
        _step.setIdForm( _form.getId( ) );
        _step.setInitial( true );
        StepHome.create( _step );

        _question = new Question( );
        _question.setTitle( "Question" );
        _question.setCode( "question_orphan" );
        _question.setColumnTitle( "Question" );
        _question.setDescription( "" );
        _question.setIdStep( _step.getId( ) );
        QuestionHome.create( _question );
    }

    @AfterEach
    protected void removeData( )
    {
        if ( _control != null && ControlHome.findByPrimaryKey( _control.getId( ) ) != null )
        {
            ControlHome.remove( _control.getId( ) );
        }
        if ( _transition != null )
        {
            TransitionHome.remove( _transition.getId( ) );
        }
        QuestionHome.remove( _question.getId( ) );
        StepHome.remove( _step.getId( ) );
        FormHome.remove( _form.getId( ) );
    }

    private Control createTransitionControl( int nIdTarget )
    {
        Control control = new Control( );
        control.setControlType( ControlType.TRANSITION.getLabel( ) );
        control.setIdControlTarget( nIdTarget );
        control.setValidatorName( "forms.equalsSelectedValueValidator" );
        control.setValue( "1" );
        control.setErrorMessage( "" );
        Set<Integer> setIdQuestion = new HashSet<>( );
        setIdQuestion.add( _question.getId( ) );
        control.setListIdQuestion( setIdQuestion );
        ControlHome.create( control );
        return control;
    }

    private String remove( Control control ) throws ReflectiveOperationException
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( "id_control", String.valueOf( control.getId( ) ) );
        AdminUserUtils.registerAdminUserWithRight( request, new AdminUser( ), ModifyEntryJspBean.RIGHT_MANAGE_FORMS );

        // processController needs a Bean Validation provider the test classpath does not have: the action is called directly
        FormControlJspBean bean = new FormControlJspBean( );
        RedirectCapturingResponse response = new RedirectCapturingResponse( );
        setField( MVCAdminJspBean.class, bean, "_response", response );
        setField( AdminFeaturesPageJspBean.class, bean, "_locale", Locale.FRENCH );
        setField( FormControlJspBean.class, bean, "_controlRemovedEvent", _controlEvent );

        bean.doRemoveControl( request );
        return response.getRedirectLocation( );
    }

    /**
     * A control whose transition was removed is removed from the screen, and the return is the questions of the step of
     * the question it reads, with the message of the removal
     */
    @Test
    public void testOrphanControlIsRemoved( ) throws ReflectiveOperationException
    {
        _control = createTransitionControl( ID_MISSING_TARGET );

        String strLocation = remove( _control );

        assertNull( ControlHome.findByPrimaryKey( _control.getId( ) ), "the orphan control is removed" );
        assertNotNull( strLocation );
        assertTrue( strLocation.contains( "ManageQuestions.jsp" ) && strLocation.contains( "id_step=" + _step.getId( ) ), strLocation );
        assertTrue( strLocation.contains( "forms.info.control.removed" ), strLocation );
    }

    /**
     * A control of an existing transition is removed as before, back to the transitions of its step
     */
    @Test
    public void testControlOfAnExistingTransitionIsRemoved( ) throws ReflectiveOperationException
    {
        _transition = new Transition( );
        _transition.setFromStep( _step.getId( ) );
        _transition.setNextStep( _step.getId( ) );
        TransitionHome.create( _transition );
        _control = createTransitionControl( _transition.getId( ) );

        String strLocation = remove( _control );

        assertNull( ControlHome.findByPrimaryKey( _control.getId( ) ) );
        assertTrue( strLocation.contains( "ManageTransitions.jsp" ) && strLocation.contains( "id_step=" + _step.getId( ) ), strLocation );
    }

    private static void setField( Class<?> clazz, Object bean, String strFieldName, Object value ) throws ReflectiveOperationException
    {
        Field field = clazz.getDeclaredField( strFieldName );
        field.setAccessible( true );
        field.set( bean, value );
    }

    private static final class RedirectCapturingResponse extends MockHttpServletResponse
    {
        private String _strRedirectLocation;

        @Override
        public void sendRedirect( String strLocation )
        {
            _strRedirectLocation = strLocation;
        }

        String getRedirectLocation( )
        {
            return _strRedirectLocation;
        }
    }
}
