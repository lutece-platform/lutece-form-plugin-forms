/*
 * Copyright (c) 2002-2022, City of Paris
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
package fr.paris.lutece.plugins.forms.export.csv;

import org.apache.commons.text.StringEscapeUtils;

import fr.paris.lutece.plugins.forms.business.Question;

/**
 * This class provides utilitary methods for CSV export
 *
 */
public final class CSVUtil
{
    private static final String ITERATION_PREFIX = " (";
    private static final String ITERATION_SUFFIX = ")";

    /**
     * Constructor
     */
    private CSVUtil( )
    {

    }

    /**
     * Builds the column name for the specified question
     * 
     * @param question
     *            the question
     * @param isIteration TODO
     * @return the column name
     */
    public static String buildColumnName( Question question, boolean isIteration )
    {
        String strColumnName = question.getTitle( );

        if ( isIteration )
        {
            strColumnName = new StringBuilder( question.getTitle( ) ).append( ITERATION_PREFIX ).append( question.getIterationNumber( ) + 1)
                    .append( ITERATION_SUFFIX ).toString( );
        }

        return strColumnName;
    }

    /**
     * Make the specified value safe for the CSV export
     *
     * @param strValue
     *            the value
     * @return the safe value
     */
    static String safeString( String strValue )
    {
        return StringEscapeUtils.escapeCsv( strValue );
    }
}
