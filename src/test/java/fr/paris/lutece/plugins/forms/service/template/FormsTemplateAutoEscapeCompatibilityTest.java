/*
 * Copyright (c) 2026, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *  3. Neither the name of the City of Paris nor the names of its contributors
 *     may be used to endorse or promote products derived from this software
 *     without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package fr.paris.lutece.plugins.forms.service.template;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import freemarker.core.HTMLOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * Ensures that every Forms template parses whether FreeMarker HTML auto-escaping is enabled or disabled.
 */
public class FormsTemplateAutoEscapeCompatibilityTest
{
    private static final String TEMPLATES_ROOT = "webapp/WEB-INF/templates";

    /**
     * Parses every Forms template with both FreeMarker configurations.
     *
     * @throws IOException
     *             if the template tree cannot be read
     */
    @Test
    @DisplayName( "Every Forms template parses with auto-escaping both on and off" )
    public void everyFormsTemplateParsesInBothModes( ) throws IOException
    {
        List<Path> listTemplates = listFormsTemplates( );
        assertTrue( !listTemplates.isEmpty( ), "Expected to find templates under " + TEMPLATES_ROOT );

        List<String> listFailures = new ArrayList<>( );
        for ( Path path : listTemplates )
        {
            String strSource = Files.readString( path, StandardCharsets.UTF_8 );
            for ( boolean bAutoEscape : new boolean [ ] {
                    false, true
            } )
            {
                try
                {
                    new Template( path.toString( ), strSource, newConfiguration( bAutoEscape ) );
                }
                catch( Exception e )
                {
                    listFailures.add( path + " [autoEscape=" + bAutoEscape + "] : " + firstLine( e.getMessage( ) ) );
                }
            }
        }

        if ( !listFailures.isEmpty( ) )
        {
            fail( listFailures.size( ) + " Forms template(s) do not parse in both auto-escaping modes:\n  "
                    + String.join( "\n  ", listFailures ) );
        }
    }

    /**
     * Creates the FreeMarker configuration used by the two Lutece rendering modes.
     *
     * @param bAutoEscape
     *            whether HTML auto-escaping is enabled
     * @return the configured FreeMarker engine
     */
    private static Configuration newConfiguration( boolean bAutoEscape )
    {
        Configuration configuration = new Configuration( Configuration.VERSION_2_3_31 );
        configuration.setLocalizedLookup( false );
        if ( bAutoEscape )
        {
            configuration.setOutputFormat( HTMLOutputFormat.INSTANCE );
            configuration.setAutoEscapingPolicy( Configuration.ENABLE_IF_DEFAULT_AUTO_ESCAPING_POLICY );
        }
        return configuration;
    }

    /**
     * Lists the FreeMarker templates shipped by Forms.
     *
     * @return the template paths
     * @throws IOException
     *             if the template tree cannot be read
     */
    private static List<Path> listFormsTemplates( ) throws IOException
    {
        try ( Stream<Path> streamPaths = Files.walk( Paths.get( TEMPLATES_ROOT ) ) )
        {
            return streamPaths.filter( Files::isRegularFile ).filter( FormsTemplateAutoEscapeCompatibilityTest::isTemplate )
                    .collect( Collectors.toList( ) );
        }
    }

    /**
     * Determines whether a path is a FreeMarker template supported by Forms.
     *
     * @param path
     *            the candidate path
     * @return true when the file is an HTML or FTL template
     */
    private static boolean isTemplate( Path path )
    {
        String strName = path.getFileName( ).toString( );
        return strName.endsWith( ".html" ) || strName.endsWith( ".ftl" );
    }

    /**
     * Extracts the useful first line of a parser error.
     *
     * @param strMessage
     *            the exception message
     * @return the first line, or the exception type fallback
     */
    private static String firstLine( String strMessage )
    {
        if ( strMessage == null || strMessage.isEmpty( ) )
        {
            return "No diagnostic";
        }
        int nNewline = strMessage.indexOf( '\n' );
        return nNewline < 0 ? strMessage : strMessage.substring( 0, nNewline );
    }
}
