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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.template.HtmlMarkup;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;

/**
 * A task form of a workflow action submitted from the front office with a validation error is displayed again in the front office with the
 * message of the task, instead of sending the user to the back-office message page (FP-01).
 */
public class FormResponseTaskFormErrorTest extends LuteceTestCase
{
    private static final String TEMPLATE = "/skin/plugins/forms/task_form_workflow.html";
    private static final String KEY_MESSAGE_WITH_ARGUMENT = "forms.message.confirmRemoveForm";

    /**
     * Renders the task form page
     *
     * @param taskFormError
     *            the error, or null
     * @return the HTML
     */
    private static String render( Object taskFormError )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( "id_form_response", "12" );
        model.put( "id_action", "3" );
        model.put( "token", "tok" );
        model.put( "tasks_form", HtmlMarkup.of( "<textarea name=\"comment_value_1\"></textarea>" ) );
        if ( taskFormError != null )
        {
            model.put( "task_form_error", taskFormError );
        }

        return AppTemplateService.getTemplate( TEMPLATE, Locale.ENGLISH, model ).getHtml( );
    }

    /**
     * The message the task stored in the session is read, with its argument escaped, and displayed above the task form
     */
    @Test
    public void testMessageOfTheTaskIsDisplayedOnTheTaskForm( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        String strError = AdminMessageService.getMessageUrl( request, KEY_MESSAGE_WITH_ARGUMENT, new Object [ ] {
                "Motif <b>"
        }, AdminMessage.TYPE_STOP );

        Object error = FormResponseXPage.getTaskFormError( request, strError, Locale.ENGLISH );
        String strHtml = render( error );

        assertTrue( strHtml.contains( "Motif &lt;b&gt;" ), strHtml );
        assertTrue( strHtml.contains( "delete the form" ), strHtml );
        assertTrue( strHtml.contains( "alert-danger" ), strHtml );
        assertTrue( strHtml.contains( "name=\"comment_value_1\"" ), strHtml );
        assertFalse( strHtml.contains( "AdminMessage.jsp" ), strHtml );
    }

    /**
     * An error that is not the URL of the message page gives the generic error message, escaped
     */
    @Test
    public void testOtherErrorGivesTheGenericMessage( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );

        Object error = FormResponseXPage.getTaskFormError( request, "error <x>", Locale.ENGLISH );
        String strHtml = render( error );

        assertTrue( strHtml.contains( "An error occurred while processing your action." ), strHtml );
        assertFalse( strHtml.contains( "error <x>" ), strHtml );
    }

    /**
     * No alert without error
     */
    @Test
    public void testNoAlertWithoutError( )
    {
        assertFalse( render( null ).contains( "alert-danger" ) );
    }
}
