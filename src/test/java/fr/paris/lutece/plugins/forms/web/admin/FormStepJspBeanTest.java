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

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.portal.business.rbac.RBACRole;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.test.AdminUserUtils;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import jakarta.inject.Inject;

/**
 * Creation of the steps of a form in the back office
 */
public class FormStepJspBeanTest extends LuteceTestCase
{
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String ROLE_FORMS_MANAGER = "forms_manager";
    // The "final" checkbox of the step creation form
    private static final Pattern CHECKBOX_FINAL = Pattern.compile( "<input[^>]*\\bid=\"final\"[^>]*>" );

    @Inject
    private FormStepJspBean _bean;
    private Form _form;

    @BeforeEach
    protected void createForm( )
    {
        _form = new Form( );
        _form.setTitle( "FormStepJspBeanTest" );
        _form.setBreadcrumbName( "forms.horizontalBreadcrumb" );
        FormHome.create( _form );
    }

    @AfterEach
    protected void removeForm( )
    {
        StepHome.getStepsListByForm( _form.getId( ) ).forEach( step -> StepHome.remove( step.getId( ) ) );
        FormHome.remove( _form.getId( ) );
    }

    /**
     * The first step of a form, created from the list of its steps, is final unless the user unchecks it: a form of one step can be submitted
     */
    @Test
    public void testFirstStepIsFinalByDefault( ) throws AccessDeniedException
    {
        _bean.getManageSteps( newRequest( ) );

        assertTrue( isFinalChecked( _bean.getCreateStep( newRequest( ) ) ) );
    }

    /**
     * The next steps are not final by default
     */
    @Test
    public void testNextStepIsNotFinalByDefault( ) throws AccessDeniedException
    {
        Step initial = new Step( );
        initial.setTitle( "Initial" );
        initial.setDescription( "" );
        initial.setIdForm( _form.getId( ) );
        initial.setInitial( true );
        StepHome.create( initial );

        _bean.getManageSteps( newRequest( ) );

        assertFalse( isFinalChecked( _bean.getCreateStep( newRequest( ) ) ) );
    }

    /**
     * The graphical view of the steps also opens the creation of a new step
     */
    @Test
    public void testFirstStepIsFinalFromTheGraphicalView( ) throws AccessDeniedException
    {
        _bean.getGraphicalView( newRequest( ) );

        assertTrue( isFinalChecked( _bean.getCreateStep( newRequest( ) ) ) );
    }

    private MockHttpServletRequest newRequest( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( PARAMETER_ID_FORM, String.valueOf( _form.getId( ) ) );
        AdminUser user = new AdminUser( );
        user.setRoles( Map.of( ROLE_FORMS_MANAGER, new RBACRole( ROLE_FORMS_MANAGER, ROLE_FORMS_MANAGER ) ) );
        AdminUserUtils.registerAdminUserWithRight( request, user, ModifyEntryJspBean.RIGHT_MANAGE_FORMS );
        return request;
    }

    private static boolean isFinalChecked( String strHtml )
    {
        Matcher matcher = CHECKBOX_FINAL.matcher( strHtml );
        assertTrue( matcher.find( ), "The step creation form must contain the final checkbox" );
        return matcher.group( ).contains( "checked" );
    }
}
