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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormLayout;
import fr.paris.lutece.plugins.forms.web.entrytype.DisplayType;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Renders the children of a step or of a group in the columns of its layout, with the front office and the back office templates
 */
public class CompositeColumnsRendererTest extends LuteceTestCase
{
    /**
     * Builds a child placed in the given column
     *
     * @param nColumn
     *            the column
     * @return the child
     */
    private static ICompositeDisplay child( int nColumn )
    {
        FormDisplay display = new FormDisplay( );
        display.setDisplayColumn( nColumn );
        return (ICompositeDisplay) Proxy.newProxyInstance( ICompositeDisplay.class.getClassLoader( ), new Class<?>[ ] {
                ICompositeDisplay.class
        }, ( proxy, method, args ) -> "getFormDisplay".equals( method.getName( ) ) ? display : null );
    }

    /**
     * Renders three children, the second one in column 2, and returns the HTML
     *
     * @param displayType
     *            the display type
     * @return the HTML
     */
    private static String renderThreeChildren( DisplayType displayType )
    {
        return CompositeColumnsRenderer.render( List.of( child( 1 ), child( 2 ), child( 1 ) ), List.of( "<p>A</p>", "<p>B</p>", "<p>C</p>" ),
                FormLayout.TWO_COLUMNS, displayType, Locale.FRENCH );
    }

    /**
     * The front office puts each child in its column, keeping the order inside a column, and the HTML of the children is not escaped
     */
    @Test
    public void testFrontOfficeColumns( )
    {
        String strHtml = renderThreeChildren( DisplayType.EDITION_FRONTOFFICE );

        assertEquals( 2, strHtml.split( "forms-column", -1 ).length - 1, strHtml );
        assertTrue( strHtml.indexOf( "<p>A</p>" ) < strHtml.indexOf( "<p>C</p>" ), strHtml );
        assertTrue( strHtml.indexOf( "<p>C</p>" ) < strHtml.indexOf( "<p>B</p>" ), strHtml );
        assertTrue( strHtml.contains( "col-md-6" ), strHtml );
    }

    /**
     * The back office entry of a response, which is the preview of the form, lays out the columns too
     */
    @Test
    public void testBackOfficeColumns( )
    {
        String strHtml = renderThreeChildren( DisplayType.SUBMIT_BACKOFFICE );

        assertTrue( strHtml.contains( "forms-columns" ), strHtml );
        assertEquals( 2, strHtml.split( "forms-column\\b(?!s)", -1 ).length - 1, strHtml );
        assertTrue( strHtml.indexOf( "<p>C</p>" ) < strHtml.indexOf( "<p>B</p>" ), strHtml );
        assertFalse( strHtml.contains( "&lt;p&gt;" ), strHtml );
    }

    /**
     * The read-only views keep one column
     */
    @Test
    public void testReadOnlyKeepsOneColumn( )
    {
        assertFalse( CompositeColumnsRenderer.isLaidOutInColumns( DisplayType.READONLY_FRONTOFFICE, FormLayout.TWO_COLUMNS ) );
        assertFalse( CompositeColumnsRenderer.isLaidOutInColumns( DisplayType.EDITION_FRONTOFFICE, FormLayout.ONE_COLUMN ) );
        assertTrue( CompositeColumnsRenderer.isLaidOutInColumns( DisplayType.SUBMIT_BACKOFFICE, FormLayout.TWO_COLUMNS ) );
    }
}
