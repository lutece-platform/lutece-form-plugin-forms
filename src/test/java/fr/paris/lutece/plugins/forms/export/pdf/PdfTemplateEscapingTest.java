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
package fr.paris.lutece.plugins.forms.export.pdf;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.service.template.HtmlMarkup;
import freemarker.cache.FileTemplateLoader;
import freemarker.core.HTMLOutputFormat;
import freemarker.template.Configuration;

/**
 * The PDF template writes the value of a cell as HTML (escaped text, sanitized rich text, embedded images) and its
 * title as text, under auto-escaping (LUT-33153). Only the FreeMarker engine is needed.
 */
public class PdfTemplateEscapingTest
{
    private static final String TEMPLATES_ROOT = "webapp/WEB-INF/templates";
    private static final String TEMPLATE = "admin/plugins/forms/pdf/default_template_export_pdf.html";

    /**
     * An embedded image of a cell is kept, a title holding markup is escaped.
     *
     * @throws Exception
     *             if the template fails
     */
    @Test
    public void testCellValueIsHtmlAndTitleIsText( ) throws Exception
    {
        Configuration cfg = new Configuration( Configuration.VERSION_2_3_0 );
        cfg.setTemplateLoader( new FileTemplateLoader( new File( TEMPLATES_ROOT ) ) );
        cfg.setOutputFormat( HTMLOutputFormat.INSTANCE );
        cfg.setAutoEscapingPolicy( Configuration.ENABLE_IF_DEFAULT_AUTO_ESCAPING_POLICY );

        Map<String, Object> cell = new HashMap<>( );
        cell.put( "title", "<svg onload=alert(1)>" );
        cell.put( "value", HtmlMarkup.of( "a &amp; b;<img src=\"data:image/jpeg;base64, AAAA \" width=\"100px\" height=\"100px\" /> " ) );
        cell.put( "formResponseNumber", 0 );
        cell.put( "drawable", true );
        Map<String, Object> model = new HashMap<>( );
        model.put( "list_pdf_cell", List.of( cell ) );
        model.put( "form_title", "Form" );

        StringWriter out = new StringWriter( );
        cfg.getTemplate( TEMPLATE ).process( model, out );
        String strHtml = out.toString( );

        assertTrue( strHtml.contains( "<img src=\"data:image/jpeg;base64, AAAA \"" ), strHtml );
        assertTrue( strHtml.contains( "a &amp; b;" ), strHtml );
        assertTrue( strHtml.contains( "&lt;svg onload=alert(1)&gt;" ), strHtml );
        assertFalse( strHtml.contains( "&amp;amp;" ), strHtml );
    }
}
