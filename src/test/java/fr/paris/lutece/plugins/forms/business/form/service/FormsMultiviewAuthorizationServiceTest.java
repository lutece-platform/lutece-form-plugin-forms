/*
 * Copyright (c) 2002-2018, Mairie de Paris
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
package fr.paris.lutece.plugins.forms.business.form.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.springframework.mock.web.MockHttpServletRequest;

import fr.paris.lutece.plugins.forms.business.form.column.FormColumnFactory;
import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.column.impl.FormColumnFormResponseDateCreationMock;
import fr.paris.lutece.plugins.forms.business.form.column.impl.FormColumnFormsMock;
import fr.paris.lutece.plugins.forms.business.form.column.impl.FormColumnWorkflowStateMock;
import fr.paris.lutece.plugins.forms.business.form.list.FormListDAOMock;
import fr.paris.lutece.plugins.forms.business.form.list.FormListFacade;
import fr.paris.lutece.plugins.forms.business.form.panel.FormPanel;
import fr.paris.lutece.plugins.forms.business.form.panel.configuration.FormPanelConfiguration;
import fr.paris.lutece.plugins.forms.business.form.panel.initializer.IFormPanelInitializer;
import fr.paris.lutece.plugins.forms.business.form.panel.initializer.impl.FormPanelFormResponseInitializerMock;
import fr.paris.lutece.plugins.forms.business.form.panel.initializer.impl.FormPanelFormsInitializerMock;
import fr.paris.lutece.plugins.forms.service.FormsMultiviewAuthorizationService;
import fr.paris.lutece.plugins.forms.service.IFormsMultiviewAuthorizationService;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test class for the FormsMultiviewAuthorizationService implementation
 */
public class FormsMultiviewAuthorizationServiceTest extends LuteceTestCase
{
    // Variables
    private FormPanel _formPanel;
    private FormColumnFactory _formColumnFactory;

    /**
     * {@inheritDoc}
     */
    @Before
    public void setUp( ) throws Exception
    {
        super.setUp( );

        List<String> listFormPanelInitializerName = new ArrayList<>( );
        listFormPanelInitializerName.add( "fr.paris.lutece.plugins.forms.business.form.panel.initializer.impl.FormPanelFormsInitializer" );
        listFormPanelInitializerName.add( "fr.paris.lutece.plugins.forms.business.form.panel.initializer.impl.FormPanelFormResponseInitializer" );

        FormPanelConfiguration formPanelConfiguration = new FormPanelConfiguration( "code_technique", 1, "titre", listFormPanelInitializerName );
        _formPanel = new FormPanel( );
        _formPanel.setFormPanelConfiguration( formPanelConfiguration );

        List<IFormColumn> listFormColumn = new ArrayList<>( );
        listFormColumn.add( new FormColumnFormsMock( 1, "form" ) );
        listFormColumn.add( new FormColumnFormResponseDateCreationMock( 2, "date creation" ) );
        listFormColumn.add( new FormColumnWorkflowStateMock( 3, "workflow state" ) );
        _formColumnFactory = new FormColumnFactory( );
    }

    /**
     * {@inheritDoc}
     */
    @After
    public void tearDown( ) throws Exception
    {
        super.tearDown( );
    }

    /**
     * Test of the method {@link FormsMultiviewAuthorizationService#isUserAuthorizedOnFormResponse(request,int)} on a form response on which the user is
     * authorized
     */
    public void testIsUserAthorizedOnFormResponseOnAuthorizedFormResponse( )
    {
        int nIdFormResponse = 3;
        List<Integer> listAuthorizedId = Arrays.asList( 1, 2, 3, 4 );

        FormListDAOMock formListDAOMock = new FormListDAOMock( listAuthorizedId );
        FormListFacade formListFacade = new FormListFacade( formListDAOMock );
        IFormsMultiviewAuthorizationService formsMultiviewAuthorizationService = new FormsMultiviewAuthorizationService(
                _formPanel.getFormPanelConfiguration( ), formListFacade, _formColumnFactory );

        boolean bIsUserAuthorize = formsMultiviewAuthorizationService.isUserAuthorizedOnFormResponse( new MockHttpServletRequest( ), nIdFormResponse );
        assertThat( bIsUserAuthorize, is( Boolean.TRUE ) );
    }

    /**
     * Test of the method {@link FormsMultiviewAuthorizationService#isUserAuthorizedOnFormResponse(request,int)} on a form response on which the user is not
     * authorized
     */
    public void testIsUserAthorizedOnFormResponseOnUnauthorizedFormResponse( )
    {
        int nIdFormResponse = 6;
        List<Integer> listAuthorizedId = Arrays.asList( 1, 2, 3, 4 );

        FormListDAOMock formListDAOMock = new FormListDAOMock( listAuthorizedId );
        FormListFacade formListFacade = new FormListFacade( formListDAOMock );
        IFormsMultiviewAuthorizationService formsMultiviewAuthorizationService = new FormsMultiviewAuthorizationService(
                _formPanel.getFormPanelConfiguration( ), formListFacade, _formColumnFactory );

        boolean bIsUserAuthorize = formsMultiviewAuthorizationService.isUserAuthorizedOnFormResponse( new MockHttpServletRequest( ), nIdFormResponse );
        assertThat( bIsUserAuthorize, is( Boolean.FALSE ) );
    }

    /**
     * Test of the method {@link FormsMultiviewAuthorizationService#isUserAuthorizedOnFormResponse(request,int)} with a bad id for a form response
     */
    public void testIsUserAthorizedWithBadIdFormResponse( )
    {
        int nIdFormResponse = -1;
        List<Integer> listAuthorizedId = Arrays.asList( 1, 2, 3, 4 );

        FormListDAOMock formListDAOMock = new FormListDAOMock( listAuthorizedId );
        FormListFacade formListFacade = new FormListFacade( formListDAOMock );
        IFormsMultiviewAuthorizationService formsMultiviewAuthorizationService = new FormsMultiviewAuthorizationService(
                _formPanel.getFormPanelConfiguration( ), formListFacade, _formColumnFactory );

        boolean bIsUserAuthorize = formsMultiviewAuthorizationService.isUserAuthorizedOnFormResponse( new MockHttpServletRequest( ), nIdFormResponse );
        assertThat( bIsUserAuthorize, is( Boolean.FALSE ) );
    }

    /**
     * Test of the method {@link FormsMultiviewAuthorizationService#isUserAuthorizedOnFormResponse(request,int)} with an empty panel
     */
    public void testIsUserAthorizedWithEmptyPanel( )
    {
        int nIdFormResponse = 2;
        List<Integer> listAuthorizedId = Arrays.asList( 1, 2, 3, 4 );

        FormListDAOMock formListDAOMock = new FormListDAOMock( listAuthorizedId );
        FormListFacade formListFacade = new FormListFacade( formListDAOMock );
        IFormsMultiviewAuthorizationService formsMultiviewAuthorizationService = new FormsMultiviewAuthorizationService( null, formListFacade,
                _formColumnFactory );

        boolean bIsUserAuthorize = formsMultiviewAuthorizationService.isUserAuthorizedOnFormResponse( new MockHttpServletRequest( ), nIdFormResponse );
        assertThat( bIsUserAuthorize, is( Boolean.FALSE ) );
    }
}
