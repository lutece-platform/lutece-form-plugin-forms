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
package fr.paris.lutece.plugins.forms.web;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.junit.jupiter.api.Test;

import freemarker.core.HTMLOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;

/**
 * The front office control of the regular expression of a derived entry type writes the expression and its message in a
 * script: they are JavaScript string literals, JavaScript-escaped and not HTML-escaped (the skin templates are rendered
 * with HTML auto-escaping).
 */
public class FillEntryRegexControlTemplateTest
{
    private static final String TEMPLATES_ROOT = "webapp/WEB-INF/templates";
    private static final String TEMPLATE = "skin/plugins/forms/entries/fill_entry_regex_control.html";
    private static final String REGEX = "[^<>&]+";
    private static final String MESSAGE = "La valeur du champ \"Nom\" n'a pas le format attendu.";

    private static String render( ) throws IOException, TemplateException
    {
        Configuration cfg = new Configuration( Configuration.VERSION_2_3_32 );
        cfg.setOutputFormat( HTMLOutputFormat.INSTANCE );
        cfg.setAutoEscapingPolicy( Configuration.ENABLE_IF_DEFAULT_AUTO_ESCAPING_POLICY );
        cfg.setDirectoryForTemplateLoading( new File( TEMPLATES_ROOT ) );
        Map<String, Object> entryType = Map.of( "regexPattern", REGEX, "regexErrorMessage", MESSAGE );
        Map<String, Object> entry = Map.of( "entryType", entryType, "title", "Nom" );
        Map<String, Object> model = Map.of( "idName", "attribute12", "entry", entry );
        StringWriter writer = new StringWriter( );
        cfg.getTemplate( TEMPLATE ).process( model, writer );

        return writer.toString( );
    }

    /**
     * The regular expression reaches the RegExp constructor as written by the administrator (only JavaScript-escaped)
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testRegexIsNotHtmlEscaped( ) throws Exception
    {
        String strHtml = render( );

        assertTrue( strHtml.contains( "'[^<>&]+'" ), strHtml );
        assertFalse( strHtml.contains( "&lt;" ), strHtml );
        assertFalse( strHtml.contains( "&amp;" ), strHtml );
    }

    /**
     * The message is a JavaScript string literal: quotes are JavaScript-escaped, not turned into HTML entities that
     * textContent would show as such
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testMessageIsNotHtmlEscaped( ) throws Exception
    {
        String strHtml = render( );

        assertTrue( strHtml.contains( "const message = 'La valeur du champ \\\"Nom\\\" n\\'a pas le format attendu.';" ), strHtml );
        assertFalse( strHtml.contains( "&quot;" ), strHtml );
        assertFalse( strHtml.contains( "&#39;" ), strHtml );
    }
}
