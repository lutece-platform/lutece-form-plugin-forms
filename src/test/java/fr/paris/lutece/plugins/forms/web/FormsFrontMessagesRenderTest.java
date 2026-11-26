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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.util.mvc.utils.MVCMessage;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * FO forms pages: an info or a warning added by the core MVC (addInfo / addWarning store a plain text since core LUT-30580) or by the
 * plugin (an MVCMessage) is shown, escaped once, and never breaks the page
 */
public class FormsFrontMessagesRenderTest extends LuteceTestCase
{
    private static final String TEXT = "L'action a été effectuée";
    private static final String ESCAPED = "L&#39;action a été effectuée";

    private Map<String, Object> model( String strMark, Object messages )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( strMark, messages );
        model.put( "formResponse", new FormResponse( ) );
        model.put( "list_step_display", List.of( ) );
        model.put( "workflow_action_list", List.of( ) );
        model.put( "listFiles", List.of( ) );
        model.put( "is_action_success", false );
        model.put( "labelInfoResponse", "" );
        model.put( "back_url", "" );
        model.put( "token", "" );
        return model;
    }

    private void check( String strTemplate, String strMark, Object messages )
    {
        String strHtml = AppTemplateService.getTemplate( "skin/plugins/forms/" + strTemplate, Locale.FRENCH, model( strMark, messages ) ).getHtml( );
        assertTrue( strHtml.contains( ESCAPED ), strTemplate + " " + strMark + "\n" + strHtml );
        assertFalse( strHtml.contains( "&amp;#39;" ), strTemplate + " " + strMark + "\n" + strHtml );
    }

    private static Set<String> text( )
    {
        Set<String> messages = new LinkedHashSet<>( );
        messages.add( TEXT );
        return messages;
    }

    @Test
    public void testMessagesAddedByTheCoreAsText( )
    {
        check( "view_form_response.html", "infos", text( ) );
        check( "view_form_response.html", "warnings", text( ) );
        check( "view_form_file.html", "infos", text( ) );
        check( "view_form_file.html", "warnings", text( ) );
        check( "form_submitted_view.html", "warnings", text( ) );
    }

    @Test
    public void testMessagesAddedAsMVCMessage( )
    {
        check( "view_form_response.html", "infos", List.of( new MVCMessage( TEXT ) ) );
        check( "view_form_response.html", "warnings", List.of( new MVCMessage( TEXT ) ) );
        check( "view_form_file.html", "infos", List.of( new MVCMessage( TEXT ) ) );
        check( "view_form_file.html", "warnings", List.of( new MVCMessage( TEXT ) ) );
        check( "form_submitted_view.html", "warnings", List.of( new MVCMessage( TEXT ) ) );
    }
}
