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
package fr.paris.lutece.plugins.forms.business.form.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import fr.paris.lutece.plugins.forms.business.form.column.FormColumnFactory;
import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.column.impl.FormColumnFormResponseDateCreationMock;
import fr.paris.lutece.plugins.forms.business.form.column.impl.FormColumnFormsMock;
import fr.paris.lutece.plugins.forms.business.form.column.impl.FormColumnWorkflowStateMock;
import fr.paris.lutece.plugins.forms.business.form.list.FormListDAOMock;
import fr.paris.lutece.plugins.forms.business.form.list.FormListFacade;
import fr.paris.lutece.plugins.forms.business.form.list.IFormListDAO;
import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.forms.business.form.FormItemSortConfig;
import fr.paris.lutece.plugins.forms.business.form.FormResponseItem;
import fr.paris.lutece.plugins.forms.business.form.filter.FormFilter;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.business.form.panel.FormPanel;
import fr.paris.lutece.plugins.forms.business.form.panel.configuration.FormPanelConfiguration;
import fr.paris.lutece.plugins.forms.service.FormsMultiviewAuthorizationService;
import fr.paris.lutece.plugins.forms.service.IFormsMultiviewAuthorizationService;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test class for the FormsMultiviewAuthorizationService implementation
 */
public class FormsMultiviewAuthorizationServiceTest extends LuteceTestCase
{
    private static final String ATTRIBUTE_ADMIN_USER = "lutece_admin_user";

    // Variables
    private FormPanel _formPanel;
    @Inject
    private FormColumnFactory _formColumnFactory;

    /**
     * Build a request with a minimal admin user in session so that AdminUserService.getAdminUser(...) does not return null.
     */
    private HttpServletRequest buildAuthenticatedRequest( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        AdminUser adminUser = new AdminUser( );
        adminUser.setAccessCode( "admin" );
        request.getSession( true ).setAttribute( ATTRIBUTE_ADMIN_USER, adminUser );
        return request;
    }

    /**
     * {@inheritDoc}
     */
    @BeforeEach
    public void setUp( ) throws Exception
    {

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
    }

    /**
     * Test of the method {@link FormsMultiviewAuthorizationService#isUserAuthorizedOnFormResponse(request,int)} on a form response on which the user is
     * authorized
     */
    @Test
    public void testIsUserAthorizedOnFormResponseOnAuthorizedFormResponse( )
    {
        int nIdFormResponse = 3;
        List<Integer> listAuthorizedId = Arrays.asList( 1, 2, 3, 4 );

        FormListDAOMock formListDAOMock = new FormListDAOMock( listAuthorizedId );
        FormListFacade formListFacade = new FormListFacade( formListDAOMock );
        IFormsMultiviewAuthorizationService formsMultiviewAuthorizationService = new FormsMultiviewAuthorizationService(
                _formPanel.getFormPanelConfiguration( ), formListFacade, _formColumnFactory );

        boolean bIsUserAuthorize = formsMultiviewAuthorizationService.isUserAuthorizedOnFormResponse( buildAuthenticatedRequest( ), nIdFormResponse );
        assertThat( bIsUserAuthorize, is( Boolean.TRUE ) );
    }

    /**
     * Test of the method {@link FormsMultiviewAuthorizationService#isUserAuthorizedOnFormResponse(request,int)} on a form response on which the user is not
     * authorized
     */
    @Test
    public void testIsUserAthorizedOnFormResponseOnUnauthorizedFormResponse( )
    {
        int nIdFormResponse = 6;
        List<Integer> listAuthorizedId = Arrays.asList( 1, 2, 3, 4 );

        FormListDAOMock formListDAOMock = new FormListDAOMock( listAuthorizedId );
        FormListFacade formListFacade = new FormListFacade( formListDAOMock );
        IFormsMultiviewAuthorizationService formsMultiviewAuthorizationService = new FormsMultiviewAuthorizationService(
                _formPanel.getFormPanelConfiguration( ), formListFacade, _formColumnFactory );

        boolean bIsUserAuthorize = formsMultiviewAuthorizationService.isUserAuthorizedOnFormResponse( buildAuthenticatedRequest( ), nIdFormResponse );
        assertThat( bIsUserAuthorize, is( Boolean.FALSE ) );
    }

    /**
     * Test of the method {@link FormsMultiviewAuthorizationService#isUserAuthorizedOnFormResponse(request,int)} with a bad id for a form response
     */
    @Test
    public void testIsUserAthorizedWithBadIdFormResponse( )
    {
        int nIdFormResponse = -1;
        List<Integer> listAuthorizedId = Arrays.asList( 1, 2, 3, 4 );

        FormListDAOMock formListDAOMock = new FormListDAOMock( listAuthorizedId );
        FormListFacade formListFacade = new FormListFacade( formListDAOMock );
        IFormsMultiviewAuthorizationService formsMultiviewAuthorizationService = new FormsMultiviewAuthorizationService(
                _formPanel.getFormPanelConfiguration( ), formListFacade, _formColumnFactory );

        boolean bIsUserAuthorize = formsMultiviewAuthorizationService.isUserAuthorizedOnFormResponse( buildAuthenticatedRequest( ), nIdFormResponse );
        assertThat( bIsUserAuthorize, is( Boolean.FALSE ) );
    }

    /**
     * Test of the method {@link FormsMultiviewAuthorizationService#isUserAuthorizedOnFormResponse(request,int)} with an empty panel
     */
    @Test
    public void testIsUserAthorizedWithEmptyPanel( )
    {
        int nIdFormResponse = 2;
        List<Integer> listAuthorizedId = Arrays.asList( 1, 2, 3, 4 );

        FormListDAOMock formListDAOMock = new FormListDAOMock( listAuthorizedId );
        FormListFacade formListFacade = new FormListFacade( formListDAOMock );
        IFormsMultiviewAuthorizationService formsMultiviewAuthorizationService = new FormsMultiviewAuthorizationService( null, formListFacade,
                _formColumnFactory );

        boolean bIsUserAuthorize = formsMultiviewAuthorizationService.isUserAuthorizedOnFormResponse( buildAuthenticatedRequest( ), nIdFormResponse );
        assertThat( bIsUserAuthorize, is( Boolean.FALSE ) );
    }

    /**
     * Test of the method {@link FormsMultiviewAuthorizationService#isUserAuthorizedOnFormResponse(request,int)} called concurrently by several requests on
     * the same service instance: each call must get the result of its own form response
     *
     * @throws Exception
     *             if a call fails
     */
    @Test
    public void testIsUserAthorizedOnFormResponseConcurrently( ) throws Exception
    {
        int nThreads = 16;
        int nCalls = 400;
        List<Integer> listAuthorizedId = Arrays.asList( 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 );

        FormListFacade formListFacade = new FormListFacade( new FormListDAOFilterMock( listAuthorizedId ) );
        IFormsMultiviewAuthorizationService formsMultiviewAuthorizationService = new FormsMultiviewAuthorizationService(
                _formPanel.getFormPanelConfiguration( ), formListFacade, _formColumnFactory );

        ExecutorService executor = Executors.newFixedThreadPool( nThreads );
        CountDownLatch latchStart = new CountDownLatch( 1 );
        List<Future<Boolean>> listResult = new ArrayList<>( );

        try
        {
            for ( int i = 0; i < nCalls; i++ )
            {
                // Odd ids 1..19: half of them authorized
                int nIdFormResponse = ( i % 10 ) * 2 + 1;
                boolean bExpected = listAuthorizedId.contains( nIdFormResponse );
                Callable<Boolean> call = ( ) -> {
                    latchStart.await( );
                    return formsMultiviewAuthorizationService.isUserAuthorizedOnFormResponse( buildAuthenticatedRequest( ), nIdFormResponse ) == bExpected;
                };
                listResult.add( executor.submit( call ) );
            }
            latchStart.countDown( );

            int nWrongResults = 0;
            for ( Future<Boolean> result : listResult )
            {
                if ( !result.get( 30, TimeUnit.SECONDS ) )
                {
                    nWrongResults++;
                }
            }
            assertThat( nWrongResults, is( 0 ) );
        }
        finally
        {
            executor.shutdownNow( );
        }
    }

    /**
     * Mock of the DAO which, like the real one, returns only the form response of the id filter when it is authorized, after a short delay so that the calls
     * overlap
     */
    private static final class FormListDAOFilterMock implements IFormListDAO
    {
        private final List<Integer> _listIdAuthorizedFormResponse;

        FormListDAOFilterMock( List<Integer> listIdAuthorizedFormResponse )
        {
            _listIdAuthorizedFormResponse = listIdAuthorizedFormResponse;
        }

        @Override
        public void populateFormColumns( FormPanel formPanel, List<IFormColumn> listFormColumn, List<FormFilter> listFormFilter, int nStartIndex,
                int nPageSize, FormItemSortConfig sortConfig )
        {
            List<FormResponseItem> listFormResponseItem = new ArrayList<>( );
            for ( FormFilter formFilter : listFormFilter )
            {
                Object idFormResponse = formFilter.getFormParameters( ).getFormParametersMap( ).get( FormsConstants.PARAMETER_ID_RESPONSE );
                if ( idFormResponse instanceof Integer && _listIdAuthorizedFormResponse.contains( idFormResponse ) )
                {
                    FormResponseItem formResponseItem = new FormResponseItem( );
                    formResponseItem.setIdFormResponse( (Integer) idFormResponse );
                    listFormResponseItem.add( formResponseItem );
                }
            }
            try
            {
                Thread.sleep( 2 );
            }
            catch( InterruptedException e )
            {
                Thread.currentThread( ).interrupt( );
            }
            formPanel.setFormResponseItemList( listFormResponseItem );
        }

        @Override
        public void populateFormColumns( FormPanel formPanel, List<IFormColumn> listFormColumn, List<FormFilter> listFormFilter, int nStartIndex,
                int nPageSize, FormItemSortConfig sortConfig, User user )
        {
            populateFormColumns( formPanel, listFormColumn, listFormFilter, nStartIndex, nPageSize, sortConfig );
        }

        @Override
        public List<FormResponseItem> searchAllFormResponseItem( FormPanel formPanel, List<IFormColumn> listFormColumn, List<FormFilter> listFormFilter,
                FormItemSortConfig sortConfig )
        {
            return new ArrayList<>( );
        }
    }
}
