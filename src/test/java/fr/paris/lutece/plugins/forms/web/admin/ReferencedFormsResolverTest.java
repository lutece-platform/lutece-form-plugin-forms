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

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.business.Transition;
import fr.paris.lutece.plugins.forms.business.TransitionHome;
import fr.paris.lutece.portal.business.rbac.RBACRole;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.test.AdminUserUtils;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;
import jakarta.inject.Inject;

import org.jboss.weld.proxy.WeldClientProxy;

/**
 * The form designer controllers check the rights of the user on every form referenced by the request
 */
public class ReferencedFormsResolverTest extends LuteceTestCase
{
    private static final String ROLE_FORMS_MANAGER = "forms_manager";

    @Inject
    private FormTransitionJspBean _transitionBean;
    private Form _form;
    private Step _step;
    private Step _step2;

    @BeforeEach
    protected void createForm( )
    {
        _form = new Form( );
        _form.setTitle( "ReferencedFormsResolverTest" );
        _form.setBreadcrumbName( "forms.horizontalBreadcrumb" );
        FormHome.create( _form );
        _step = createStep( "Initial", true );
        _step2 = createStep( "Second", false );
    }

    @AfterEach
    protected void removeForm( )
    {
        TransitionHome.removeTransitionByStep( _step.getId( ) );
        StepHome.getStepsListByForm( _form.getId( ) ).forEach( step -> StepHome.remove( step.getId( ) ) );
        FormHome.remove( _form.getId( ) );
    }

    /**
     * The form of each parameter is found, an invalid or unknown identifier is ignored
     */
    @Test
    public void testGetReferencedFormIds( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        assertTrue( ReferencedFormsResolver.getReferencedFormIds( request ).isEmpty( ) );

        request.addParameter( "id_step", "abc" );
        request.addParameter( "id_question", String.valueOf( Integer.MAX_VALUE ) );
        assertTrue( ReferencedFormsResolver.getReferencedFormIds( request ).isEmpty( ) );

        request = new MockHttpServletRequest( );
        request.addParameter( "id_step", String.valueOf( _step.getId( ) ) );
        assertEquals( Set.of( _form.getId( ) ), ReferencedFormsResolver.getReferencedFormIds( request ) );

        // The steps of a transition, populated from the request, are checked too
        request = new MockHttpServletRequest( );
        request.addParameter( "nextStep", String.valueOf( _step2.getId( ) ) );
        assertEquals( Set.of( _form.getId( ) ), ReferencedFormsResolver.getReferencedFormIds( request ) );
    }

    /**
     * A user without any right on the form cannot create a transition between its steps
     */
    @Test
    public void testCreateTransitionWithoutRightOnTheForm( )
    {
        MockHttpServletRequest request = newRequest( false );
        request.addParameter( "action_createTransition", "" );
        request.addParameter( "fromStep", String.valueOf( _step.getId( ) ) );
        request.addParameter( "nextStep", String.valueOf( _step2.getId( ) ) );

        assertThrows( AccessDeniedException.class, ( ) -> _transitionBean.processController( request, new MockHttpServletResponse( ) ) );
        assertTrue( TransitionHome.getTransitionsListFromStep( _step.getId( ) ).isEmpty( ) );
    }

    /**
     * The cycle check of a transition uses the form of its steps, not the form of the session that is not set in a fresh session (no
     * NullPointerException)
     */
    @Test
    public void testCircularTransitionFromAFreshSession( ) throws Exception
    {
        Transition transition = new Transition( );
        transition.setFromStep( _step2.getId( ) );
        transition.setNextStep( _step.getId( ) );
        TransitionHome.create( transition );

        Transition newTransition = new Transition( );
        newTransition.setFromStep( _step.getId( ) );
        newTransition.setNextStep( _step2.getId( ) );

        Method detectCircularTransition = FormTransitionJspBean.class.getDeclaredMethod( "detectCircularTransition", Transition.class );
        detectCircularTransition.setAccessible( true );
        Object bean = ( _transitionBean instanceof WeldClientProxy ) ? ( (WeldClientProxy) _transitionBean ).getMetadata( ).getContextualInstance( )
                : _transitionBean;

        // step -> step2 -> step
        assertTrue( (Boolean) detectCircularTransition.invoke( bean, newTransition ) );
        TransitionHome.remove( transition.getId( ) );
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

    private MockHttpServletRequest newRequest( boolean bFormsManager )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        AdminUser user = new AdminUser( );
        if ( bFormsManager )
        {
            user.setRoles( Map.of( ROLE_FORMS_MANAGER, new RBACRole( ROLE_FORMS_MANAGER, ROLE_FORMS_MANAGER ) ) );
        }
        AdminUserUtils.registerAdminUserWithRight( request, user, ModifyEntryJspBean.RIGHT_MANAGE_FORMS );
        return request;
    }
}
