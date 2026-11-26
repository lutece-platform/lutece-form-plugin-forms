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
package fr.paris.lutece.plugins.forms.web.admin;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

/**
 * The core macro modalHeader already writes the id "&lt;modal id&gt;-title" on the title of the modal: a template that also gives
 * that id to modalHeader writes the same id twice in the page (import-json-title on the forms list).
 */
public class ModalHeaderIdTemplateTest
{
    private static final Path TEMPLATES = Path.of( "webapp/WEB-INF/templates/admin/plugins/forms" );
    // a modal followed by its header given the id "<modal id>-title", the id the core already writes on the title
    private static final Pattern MODAL_HEADER_WITH_ID = Pattern.compile( "<@modal\\b[^>]*?\\bid\\s*=\\s*([\"'])([^\"']+)\\1(?:(?!<@modal\\b)[\\s\\S])*?<@modalHeader\\b[^>]*?\\bid\\s*=\\s*([\"'])\\2-title\\3" );

    @Test
    public void testNoModalHeaderRepeatsTheTitleId( ) throws IOException
    {
        List<String> listFound = new ArrayList<>( );
        try ( Stream<Path> paths = Files.walk( TEMPLATES ) )
        {
            for ( Path path : paths.filter( p -> p.toString( ).endsWith( ".html" ) ).toList( ) )
            {
                Matcher matcher = MODAL_HEADER_WITH_ID.matcher( Files.readString( path, StandardCharsets.UTF_8 ) );
                while ( matcher.find( ) )
                {
                    listFound.add( path + " : " + matcher.group( ) );
                }
            }
        }
        assertTrue( listFound.isEmpty( ), "modalHeader given the title id the core already writes: " + listFound );
    }
}
