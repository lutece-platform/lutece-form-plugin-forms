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

/**
 * Layouts of the questions of a step or of a group in the front office
 */
public final class FormLayout
{
    /**
     * The questions are displayed one below the other
     */
    public static final int ONE_COLUMN = 1;

    /**
     * The questions are displayed in two columns on large screens, one below the other on small screens
     */
    public static final int TWO_COLUMNS = 2;

    /**
     * The first column of a step or of a group, where a question or a group is placed by default
     */
    public static final int FIRST_COLUMN = 1;

    /**
     * Private constructor
     */
    private FormLayout( )
    {
    }

    /**
     * Tells whether a value is a known layout
     *
     * @param nLayout
     *            the layout
     * @return true if the value is a known layout, false otherwise
     */
    public static boolean isValid( int nLayout )
    {
        return nLayout == ONE_COLUMN || nLayout == TWO_COLUMNS;
    }

    /**
     * Returns a known layout for a value read from the database or from a request
     *
     * @param nLayout
     *            the layout
     * @return the layout if it is known, {@link #ONE_COLUMN} otherwise
     */
    public static int normalize( int nLayout )
    {
        return isValid( nLayout ) ? nLayout : ONE_COLUMN;
    }

    /**
     * Returns the number of columns of a layout
     *
     * @param nLayout
     *            the layout
     * @return the number of columns, 1 for an unknown layout
     */
    public static int getColumnCount( int nLayout )
    {
        return normalize( nLayout ) == TWO_COLUMNS ? 2 : 1;
    }

    /**
     * Returns the column where a question or a group is displayed in a step or a group of the given layout: a column the layout does not have (the
     * layout went from 2 columns to 1, or an unknown value) is read as the last column of the layout
     *
     * @param nColumn
     *            the column of the question or of the group, starting at 1
     * @param nLayout
     *            the layout of the step or of the group that holds it
     * @return a column between 1 and the number of columns of the layout
     */
    public static int normalizeColumn( int nColumn, int nLayout )
    {
        return Math.max( FIRST_COLUMN, Math.min( nColumn, getColumnCount( nLayout ) ) );
    }
}
