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

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.test.AdminUserUtils;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import jakarta.inject.Inject;

/**
 * Permissions of the back office response entry
 */
public class FormResponseJspBeanPermissionTest extends LuteceTestCase
{
    @Inject
    private FormResponseJspBean _bean;
    private Form _form;

    @BeforeEach
    protected void createForm( )
    {
        _form = new Form( );
        _form.setTitle( "FormResponseJspBeanPermissionTest" );
        _form.setBreadcrumbName( "forms.horizontalBreadcrumb" );
        FormHome.create( _form );

        Step initial = new Step( );
        initial.setTitle( "Initial" );
        initial.setDescription( "" );
        initial.setIdForm( _form.getId( ) );
        initial.setInitial( true );
        initial.setFinal( true );
        StepHome.create( initial );
    }

    @AfterEach
    protected void removeForm( )
    {
        StepHome.getStepsListByForm( _form.getId( ) ).forEach( step -> StepHome.remove( step.getId( ) ) );
        FormHome.remove( _form.getId( ) );
    }

    /**
     * Without the VIEW_FORM_RESPONSE permission on the form, the entry screen of a response is refused, as the link of the list of the forms is hidden
     */
    @Test
    public void testStepViewRefusedWithoutPermission( )
    {
        MockHttpServletRequest request = newRequest( new AdminUser( ) );
        assertThrows( AccessDeniedException.class, ( ) -> _bean.getStepView( request ) );
    }

    /**
     * Without the permission, posting the response is refused too
     */
    @Test
    public void testSaveResponseRefusedWithoutPermission( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( "id_form", String.valueOf( _form.getId( ) ) );
        AdminUserUtils.registerAdminUserWithRight( request, new AdminUser( ), ModifyEntryJspBean.RIGHT_MANAGE_FORMS );
        assertThrows( AccessDeniedException.class, ( ) -> _bean.doSaveFormResponse( request ) );
    }

    private MockHttpServletRequest newRequest( AdminUser user )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( "id_form", String.valueOf( _form.getId( ) ) );
        request.addParameter( "init", "true" );
        AdminUserUtils.registerAdminUserWithRight( request, user, ModifyEntryJspBean.RIGHT_MANAGE_FORMS );
        return request;
    }
}
