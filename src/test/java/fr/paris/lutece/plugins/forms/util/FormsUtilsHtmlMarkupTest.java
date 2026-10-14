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
package fr.paris.lutece.plugins.forms.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

import freemarker.core.HTMLOutputFormat;
import freemarker.core.TemplateHTMLOutputModel;
import freemarker.template.TemplateModelException;

/**
 * The display objects kept in session hold their rendered HTML as Strings, and hand the template a copy where it is
 * markup (LUT-33153): FreeMarker markup values are not Serializable.
 */
public class FormsUtilsHtmlMarkupTest
{
    /**
     * The copy types the listed values as markup, Strings, lists and maps of lists alike, and leaves the model as it
     * was, serializable.
     *
     * @throws IOException
     *             if the model cannot be serialized
     * @throws TemplateModelException
     *             if a markup value cannot be read
     */
    @Test
    public void testCopyHoldsMarkupAndModelStaysSerializable( ) throws IOException, TemplateModelException
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( "content", "<p>a</p>" );
        model.put( "children", List.of( "<li>1</li>", "<li>2</li>" ) );
        TreeMap<Integer, List<String>> byIteration = new TreeMap<>( );
        byIteration.put( 0, List.of( "<li>3</li>" ) );
        model.put( "byIteration", byIteration );
        model.put( "title", "<b>text</b>" );

        Map<String, Object> copy = FormsUtils.withHtmlMarkup( model, "content", "children", "byIteration", "missing" );

        assertEquals( "<p>a</p>", HTMLOutputFormat.INSTANCE.getMarkupString( (TemplateHTMLOutputModel) copy.get( "content" ) ) );
        assertTrue( ( (List<?>) copy.get( "children" ) ).get( 1 ) instanceof TemplateHTMLOutputModel );
        assertTrue( ( (List<?>) ( (Map<?, ?>) copy.get( "byIteration" ) ).get( 0 ) ).get( 0 ) instanceof TemplateHTMLOutputModel );
        assertEquals( "<b>text</b>", copy.get( "title" ) );
        assertTrue( model.get( "content" ) instanceof String );

        try ( ObjectOutputStream out = new ObjectOutputStream( new ByteArrayOutputStream( ) ) )
        {
            out.writeObject( new HashMap<>( model ) );
        }
    }
}
