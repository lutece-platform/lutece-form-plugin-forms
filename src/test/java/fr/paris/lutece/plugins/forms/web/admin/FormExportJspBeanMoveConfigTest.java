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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.export.FormExportConfig;
import fr.paris.lutece.plugins.forms.business.export.FormExportConfigHome;
import fr.paris.lutece.portal.business.rbac.RBACRole;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.test.AdminUserUtils;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import jakarta.inject.Inject;

/**
 * Reordering of the name components of the export files
 */
public class FormExportJspBeanMoveConfigTest extends LuteceTestCase
{
    private static final String ROLE_FORMS_MANAGER = "forms_manager";

    @Inject
    private FormExportJspBean _bean;
    private Form _formF;
    private Form _formG;
    private FormExportConfig _configF1;
    private FormExportConfig _configF2;
    private FormExportConfig _configG1;
    private FormExportConfig _configG2;

    @BeforeEach
    protected void createForms( )
    {
        _formF = createForm( "FormExportJspBeanMoveConfigTest F" );
        _formG = createForm( "FormExportJspBeanMoveConfigTest G" );
        _configF1 = createConfig( _formF, 1 );
        _configF2 = createConfig( _formF, 2 );
        _configG1 = createConfig( _formG, 1 );
        _configG2 = createConfig( _formG, 2 );
    }

    @AfterEach
    protected void removeForms( )
    {
        FormExportConfigHome.removeByForm( _formF.getId( ) );
        FormExportConfigHome.removeByForm( _formG.getId( ) );
        FormHome.remove( _formF.getId( ) );
        FormHome.remove( _formG.getId( ) );
    }

    /**
     * Moving up a component of another form than the posted one is refused and no order changes
     */
    @Test
    public void testMoveUpConfigOfAnotherFormRefused( )
    {
        MockHttpServletRequest request = newRequest( managerUser( ), _formG, _configF2 );
        assertThrows( AccessDeniedException.class, ( ) -> _bean.doMoveUpExportConfig( request ) );
        MockHttpServletRequest requestDown = newRequest( managerUser( ), _formG, _configF1 );
        assertThrows( AccessDeniedException.class, ( ) -> _bean.doMoveDownExportConfig( requestDown ) );
        assertOrders( 1, 2, 1, 2 );
    }

    /**
     * Without the MODIFY permission on the form, the move actions are refused
     */
    @Test
    public void testMoveRefusedWithoutPermission( )
    {
        MockHttpServletRequest request = newRequest( new AdminUser( ), _formF, _configF2 );
        assertThrows( AccessDeniedException.class, ( ) -> _bean.doMoveUpExportConfig( request ) );
        MockHttpServletRequest requestDown = newRequest( new AdminUser( ), _formF, _configF1 );
        assertThrows( AccessDeniedException.class, ( ) -> _bean.doMoveDownExportConfig( requestDown ) );
        assertOrders( 1, 2, 1, 2 );
    }

    private void assertOrders( int nF1, int nF2, int nG1, int nG2 )
    {
        assertEquals( nF1, FormExportConfigHome.findByPrimaryKey( _configF1.getId( ) ).getOrder( ) );
        assertEquals( nF2, FormExportConfigHome.findByPrimaryKey( _configF2.getId( ) ).getOrder( ) );
        assertEquals( nG1, FormExportConfigHome.findByPrimaryKey( _configG1.getId( ) ).getOrder( ) );
        assertEquals( nG2, FormExportConfigHome.findByPrimaryKey( _configG2.getId( ) ).getOrder( ) );
    }

    private static Form createForm( String strTitle )
    {
        Form form = new Form( );
        form.setTitle( strTitle );
        form.setBreadcrumbName( "forms.horizontalBreadcrumb" );
        FormHome.create( form );
        return form;
    }

    private static FormExportConfig createConfig( Form form, int nOrder )
    {
        FormExportConfig config = new FormExportConfig( );
        config.setIdForm( form.getId( ) );
        config.setField( "field" + nOrder );
        config.setOrder( nOrder );
        return FormExportConfigHome.create( config );
    }

    private static AdminUser managerUser( )
    {
        AdminUser user = new AdminUser( );
        user.setRoles( Map.of( ROLE_FORMS_MANAGER, new RBACRole( ROLE_FORMS_MANAGER, ROLE_FORMS_MANAGER ) ) );
        return user;
    }

    private static MockHttpServletRequest newRequest( AdminUser user, Form form, FormExportConfig config )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( "id_form", String.valueOf( form.getId( ) ) );
        request.addParameter( "id_config", String.valueOf( config.getId( ) ) );
        AdminUserUtils.registerAdminUserWithRight( request, user, ModifyEntryJspBean.RIGHT_MANAGE_FORMS );
        return request;
    }
}
