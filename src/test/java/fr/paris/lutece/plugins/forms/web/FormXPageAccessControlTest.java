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
package fr.paris.lutece.plugins.forms.web;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Locale;

import org.jboss.weld.proxy.WeldClientProxy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.portal.business.accesscontrol.AccessControlSessionData;
import fr.paris.lutece.portal.service.accesscontrol.AccessControlService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;
import jakarta.enterprise.inject.spi.CDI;

/**
 * The actions of a form protected by an access control are refused until the access control has been passed
 */
public class FormXPageAccessControlTest extends LuteceTestCase
{
    private static final String ACCESS_CONTROL_MESSAGE = "You must first pass the access control of this form.";

    private Form _form;
    private FormXPage _xpage;
    private Object _previousService;

    @BeforeEach
    public void createForm( ) throws ReflectiveOperationException
    {
        Form form = new Form( );
        form.setTitle( "FormXPageAccessControlTest" );
        form.setAvailabilityStartDate( new Timestamp( System.currentTimeMillis( ) - 86_400_000L ) );
        form.setBreadcrumbName( "forms.horizontalBreadcrumb" );
        _form = FormHome.create( form );
        _xpage = CDI.current( ).select( FormXPage.class ).get( );
        Field field = FormXPage.class.getDeclaredField( "_accessControlService" );
        field.setAccessible( true );
        Object instance = ( (WeldClientProxy) _xpage ).getMetadata( ).getContextualInstance( );
        _previousService = field.get( instance );
        field.set( instance, new ProtectedFormAccessControlService( _form.getId( ) ) );
    }

    @AfterEach
    public void removeForm( ) throws ReflectiveOperationException
    {
        Field field = FormXPage.class.getDeclaredField( "_accessControlService" );
        field.setAccessible( true );
        field.set( ( (WeldClientProxy) _xpage ).getMetadata( ).getContextualInstance( ), _previousService );
        FormHome.remove( _form.getId( ) );
        LocalVariables.setLocal( null, null, null );
    }

    /**
     * The summary of a protected form is refused when its access control was not passed
     */
    @Test
    public void testSummaryRefusedWithoutAccessControl( )
    {
        MockHttpServletRequest request = summaryRequest( );

        assertThrows( SiteMessageException.class, ( ) -> _xpage.doFormResponseSummary( request ) );
        assertEquals( ACCESS_CONTROL_MESSAGE, SiteMessageService.getMessage( request ).getText( Locale.ENGLISH ) );
    }

    /**
     * Control: once the access control is passed, the action is not refused for the access control
     */
    @Test
    public void testSummaryNotRefusedOnceAccessControlPassed( )
    {
        MockHttpServletRequest request = summaryRequest( );
        AccessControlSessionData sessionData = new AccessControlSessionData( _form.getId( ), Form.RESOURCE_TYPE );
        sessionData.setAccessControlResult( true );
        request.getSession( true ).setAttribute( sessionData.getSessionKey( ), sessionData );

        try
        {
            _xpage.doFormResponseSummary( request );
        }
        catch( SiteMessageException e )
        {
            SiteMessage message = SiteMessageService.getMessage( request );
            assertNotEquals( ACCESS_CONTROL_MESSAGE, ( message != null ) ? message.getText( Locale.ENGLISH ) : null );
        }
        catch( Exception e )
        {
            // the form has no step: any other outcome is out of the scope of this test
        }
    }

    private MockHttpServletRequest summaryRequest( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( "id_form", String.valueOf( _form.getId( ) ) );
        LocalVariables.setLocal( null, request, new MockHttpServletResponse( ) );
        return request;
    }

    /**
     * Access control service that protects one form
     */
    private static class ProtectedFormAccessControlService extends AccessControlService
    {
        private final int _nIdForm;

        ProtectedFormAccessControlService( int nIdForm )
        {
            _nIdForm = nIdForm;
        }

        @Override
        public boolean isAvailable( )
        {
            return true;
        }

        @Override
        public int findAccessControlForResource( int idResource, String resourceType )
        {
            return ( idResource == _nIdForm && Form.RESOURCE_TYPE.equals( resourceType ) ) ? 1 : -1;
        }
    }
}
