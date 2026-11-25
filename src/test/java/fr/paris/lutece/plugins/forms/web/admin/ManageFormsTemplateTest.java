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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

/**
 * The list of forms shows the messages of the last action whether the list is empty or not.
 */
public class ManageFormsTemplateTest
{
    private static final Path TEMPLATE = Path.of( "webapp/WEB-INF/templates/admin/plugins/forms/manage_forms.html" );
    // the messages macro, with or without default values for the lists (LUT-33722 writes errors=errors![] ...)
    private static final Pattern MESSAGES = Pattern.compile( "<@messages errors=errors(?:!\\[\\])? infos=infos(?:!\\[\\])?" );
    // the test on the list, written ?size gt 0 or ?has_content
    private static final Pattern LIST_NOT_EMPTY = Pattern.compile( "<#if form_list\\?(?:size gt 0|has_content)>" );

    /**
     * Removing the last form leads to an empty list: its message must still be shown.
     *
     * @throws IOException
     *             if the template cannot be read
     */
    @Test
    public void testMessagesAreShownWhenTheListIsEmpty( ) throws IOException
    {
        String strSource = Files.readString( TEMPLATE, StandardCharsets.UTF_8 );
        Matcher messages = MESSAGES.matcher( strSource );
        Matcher listNotEmpty = LIST_NOT_EMPTY.matcher( strSource );
        int nMessages = messages.find( ) ? messages.start( ) : -1;
        int nListNotEmpty = listNotEmpty.find( ) ? listNotEmpty.start( ) : -1;

        assertTrue( nMessages >= 0, "manage_forms.html shows the messages" );
        assertTrue( nListNotEmpty >= 0, "manage_forms.html tests whether the list is empty" );
        assertTrue( nMessages < nListNotEmpty, "the messages are shown before the test on the list, so also when it is empty" );
    }
}
