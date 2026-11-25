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
package fr.paris.lutece.plugins.forms.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test of the layouts of the questions of a step or of a group
 */
public class FormLayoutTest
{
    /**
     * The known layouts are valid and kept
     */
    @Test
    public void testKnownLayouts( )
    {
        assertTrue( FormLayout.isValid( FormLayout.ONE_COLUMN ) );
        assertTrue( FormLayout.isValid( FormLayout.TWO_COLUMNS ) );
        assertEquals( FormLayout.ONE_COLUMN, FormLayout.normalize( FormLayout.ONE_COLUMN ) );
        assertEquals( FormLayout.TWO_COLUMNS, FormLayout.normalize( FormLayout.TWO_COLUMNS ) );
    }

    /**
     * An unknown value, as a missing column or a forged request gives, falls back to one column
     */
    @Test
    public void testUnknownLayouts( )
    {
        assertFalse( FormLayout.isValid( 0 ) );
        assertFalse( FormLayout.isValid( 3 ) );
        assertEquals( FormLayout.ONE_COLUMN, FormLayout.normalize( 0 ) );
        assertEquals( FormLayout.ONE_COLUMN, FormLayout.normalize( -1 ) );
        assertEquals( FormLayout.ONE_COLUMN, FormLayout.normalize( 3 ) );
    }

    /**
     * A new step and a new group are in one column
     */
    @Test
    public void testDefaultLayout( )
    {
        assertEquals( FormLayout.ONE_COLUMN, new Step( ).getLayout( ) );
        assertEquals( FormLayout.ONE_COLUMN, new Group( ).getLayout( ) );
    }

    /**
     * A question or a group is placed in the first column by default
     */
    @Test
    public void testDefaultColumn( )
    {
        assertEquals( FormLayout.FIRST_COLUMN, new FormDisplay( ).getDisplayColumn( ) );
    }

    /**
     * The column of a question is kept when the layout has it, and falls back to the last column of the layout otherwise
     */
    @Test
    public void testNormalizeColumn( )
    {
        assertEquals( 1, FormLayout.getColumnCount( FormLayout.ONE_COLUMN ) );
        assertEquals( 2, FormLayout.getColumnCount( FormLayout.TWO_COLUMNS ) );
        assertEquals( 1, FormLayout.getColumnCount( 7 ) );
        assertEquals( 2, FormLayout.normalizeColumn( 2, FormLayout.TWO_COLUMNS ) );
        assertEquals( 1, FormLayout.normalizeColumn( 1, FormLayout.TWO_COLUMNS ) );
        assertEquals( 2, FormLayout.normalizeColumn( 5, FormLayout.TWO_COLUMNS ) );
        assertEquals( 1, FormLayout.normalizeColumn( 2, FormLayout.ONE_COLUMN ) );
        assertEquals( 1, FormLayout.normalizeColumn( 0, FormLayout.TWO_COLUMNS ) );
    }
}
