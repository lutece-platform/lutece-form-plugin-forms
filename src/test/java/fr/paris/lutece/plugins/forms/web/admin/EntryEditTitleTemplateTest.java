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

import org.junit.jupiter.api.Test;

/**
 * The edition screen of a question names the question and its type in the header of its box (lost with LUT-33718); the
 * form builder, which shows the same screen in its side panel next to the question, removes that header.
 */
public class EntryEditTitleTemplateTest
{
    private static final Path TEMPLATE = Path.of( "webapp/WEB-INF/templates/admin/plugins/forms/entries/all_entry_commons.html" );
    private static final Path BUILDER_JS = Path.of( "webapp/themes/admin/shared/plugins/forms/js/builder/form-builder.js" );

    /**
     * The box of the question parameters has a header with the title of the question and the type of the entry
     *
     * @throws IOException
     *             if a file cannot be read
     */
    @Test
    public void testQuestionTitleIsShownOnTheEditionScreen( ) throws IOException
    {
        String strSource = Files.readString( TEMPLATE, StandardCharsets.UTF_8 );

        assertTrue( strSource.contains( "<@boxHeader id='entry-title' title=fieldTitle />" ), "the box of the question has a titled header" );
        assertTrue( strSource.contains( "${entry.entryType.title!}" ), "the header holds the type of the question" );
    }

    /**
     * The side panel of the builder removes that header: the builder already shows the question being edited
     *
     * @throws IOException
     *             if a file cannot be read
     */
    @Test
    public void testBuilderPanelRemovesTheTitle( ) throws IOException
    {
        String strSource = Files.readString( BUILDER_JS, StandardCharsets.UTF_8 );

        assertTrue( strSource.contains( "#entry-title" ), "cleanFrame removes the header of the question box" );
    }
}
