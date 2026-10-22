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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.portal.business.rbac.RBACRole;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.test.AdminUserUtils;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import jakarta.inject.Inject;

/**
 * Modification of the publication dates of a form in the back office
 */
public class FormJspBeanPublicationTest extends LuteceTestCase
{
    private static final String ROLE_FORMS_MANAGER = "forms_manager";
    private static final int MAX_NUMBER_RESPONSE = 10;

    @Inject
    private FormJspBean _bean;
    private Form _form;

    /**
     * Creates a form with a maximum number of responses, the counting of the responses and one response by user
     */
    @BeforeEach
    protected void createForm( )
    {
        _form = new Form( );
        _form.setTitle( "FormJspBeanPublicationTest" );
        _form.setBreadcrumbName( "forms.horizontalBreadcrumb" );
        _form.setMaxNumberResponse( MAX_NUMBER_RESPONSE );
        _form.setCountResponses( true );
        _form.setOneResponseByUser( true );
        FormHome.create( _form );
    }

    /**
     * Removes the form
     */
    @AfterEach
    protected void removeForm( )
    {
        FormHome.remove( _form.getId( ) );
    }

    /**
     * The publication form only posts the publication dates to its own action: the other settings of the form are not posted, so they cannot be
     * reset by the populate of the whole form
     *
     * @throws AccessDeniedException
     *             if the user is not authorized
     */
    @Test
    public void testPublicationFormPostsOnlyTheDates( ) throws AccessDeniedException
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( "id_form", String.valueOf( _form.getId( ) ) );
        AdminUser user = new AdminUser( );
        user.setRoles( Map.of( ROLE_FORMS_MANAGER, new RBACRole( ROLE_FORMS_MANAGER, ROLE_FORMS_MANAGER ) ) );
        AdminUserUtils.registerAdminUserWithRight( request, user, ModifyEntryJspBean.RIGHT_MANAGE_FORMS );

        String strHtml = _bean.getModifyFormPublication( request );

        assertTrue( strHtml.contains( "name=\"action_modifyFormPublication\"" ) );
        assertFalse( strHtml.contains( "name=\"action_modifyForm\"" ) );
        assertFalse( strHtml.contains( "name=\"maxNumberResponse\"" ) );
        assertFalse( strHtml.contains( "name=\"oneResponseByUser\"" ) );
    }
}
