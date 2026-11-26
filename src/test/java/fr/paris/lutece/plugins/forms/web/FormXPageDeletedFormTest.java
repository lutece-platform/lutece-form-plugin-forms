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
import java.util.Locale;

import org.jboss.weld.proxy.WeldClientProxy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;
import jakarta.enterprise.inject.spi.CDI;

/**
 * A form removed while a user is filling it (its step still in the user's session): the front office answers with a
 * message, not with an internal error
 */
public class FormXPageDeletedFormTest extends LuteceTestCase
{
    private static final int ID_REMOVED_FORM = 987654;

    private FormXPage _xpage;
    private Object _previousStep;

    @BeforeEach
    public void keepStepOfRemovedForm( ) throws ReflectiveOperationException
    {
        _xpage = CDI.current( ).select( FormXPage.class ).get( );
        Step step = new Step( );
        step.setId( 987654 );
        step.setIdForm( ID_REMOVED_FORM );
        step.setInitial( true );
        Field field = currentStepField( );
        _previousStep = field.get( instance( ) );
        field.set( instance( ), step );
    }

    @AfterEach
    public void restore( ) throws ReflectiveOperationException
    {
        currentStepField( ).set( instance( ), _previousStep );
        LocalVariables.setLocal( null, null, null );
    }

    /**
     * The step view of a removed form gives a site message
     */
    @Test
    public void testStepViewOfRemovedFormGivesAMessage( )
    {
        MockHttpServletRequest request = request( );

        assertThrows( SiteMessageException.class, ( ) -> _xpage.getStepView( request ) );
        SiteMessage message = SiteMessageService.getMessage( request );
        assertNotNull( message );
        assertEquals( SiteMessage.TYPE_ERROR, message.getType( ) );
        assertNotNull( message.getText( Locale.ENGLISH ) );
    }

    /**
     * An action posted on a removed form gives a site message
     */
    @Test
    public void testActionOnRemovedFormGivesAMessage( )
    {
        MockHttpServletRequest request = request( );

        assertThrows( SiteMessageException.class, ( ) -> _xpage.doFormResponseSummary( request ) );
        assertNotNull( SiteMessageService.getMessage( request ) );
    }

    private MockHttpServletRequest request( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( "id_form", String.valueOf( ID_REMOVED_FORM ) );
        LocalVariables.setLocal( null, request, new MockHttpServletResponse( ) );
        return request;
    }

    private Object instance( )
    {
        return ( (WeldClientProxy) _xpage ).getMetadata( ).getContextualInstance( );
    }

    private static Field currentStepField( ) throws NoSuchFieldException
    {
        Field field = FormXPage.class.getDeclaredField( "_currentStep" );
        field.setAccessible( true );
        return field;
    }
}
