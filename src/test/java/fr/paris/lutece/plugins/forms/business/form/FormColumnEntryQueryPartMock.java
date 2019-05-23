/*
 * Copyright (c) 2002-2018, Mairie de Paris
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
package fr.paris.lutece.plugins.forms.business.form;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.column.impl.FormColumnEntry;
import fr.paris.lutece.plugins.forms.business.form.column.querypart.impl.sql.FormColumnEntrySQLQueryPart;

/**
 * Mock for a FormColumnEntryQueryPart
 */
public class FormColumnEntryQueryPartMock extends FormColumnEntrySQLQueryPart
{
    // Constants
    private static final String ENTRY_SELECT_QUERY_PART = "column_%1$s.column_%1$s_value";
    private static final String ENTRY_FROM_QUERY_PART = StringUtils.EMPTY;
    private static final String ENTRY_JOIN_SELECT_QUERY_PART = "LEFT JOIN ( SELECT form_response_%1$s.id_response AS id_response_%1$s, gen_response_%1$s.response_value AS column_%1$s_value ";
    private static final String ENTRY_JOIN_FROM_QUERY_PART = " FROM forms_response AS form_response_%s ";
    private static final String ENTRY_JOIN_QUESTION_RESPONSE_QUERY_PART = " INNER JOIN forms_question_response AS question_response_%1$s ON form_response_%1$s.id_response = question_response_%1$s.id_form_response";
    private static final String ENTRY_JOIN_QUESTION_ENTRY_RESPONSE_QUERY_PART = " INNER JOIN forms_question_entry_response AS q_entry_response_%1$s ON q_entry_response_%1$s.id_question_response = question_response_%1$s.id_question_response ";
    private static final String ENTRY_JOIN_ENTRY_RESPONSE_QUERY_PART = " INNER JOIN genatt_response AS gen_response_%1$s ON gen_response_%1$s.id_response = q_entry_response_%1$s.id_entry_response ";
    private static final String ENTRY_JOIN_ENTRY_QUERY_PART = " INNER JOIN genatt_entry AS entry_%1$s ON entry_%1$s.id_entry = gen_response_%1$s.id_entry ";
    private static final String ENTRY_JOIN_WHERE_QUERY_PART = " WHERE entry_%1$s.code IN ( %2$s ) ";
    private static final String ENTRY_JOIN_QUERY_PART = " AS column_%1$s ON column_%1$s.id_response_%1$s = response.id_response";

    // Variables
    private final int _nPosition;

    /**
     * Constructor
     * 
     * @param nPosition
     *            The position to use for build the name
     */
    public FormColumnEntryQueryPartMock( int nPosition )
    {
        super( );
        _nPosition = nPosition;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFormColumnSelectQuery( )
    {
        String strFormColumnSelectQuery = String.format( ENTRY_SELECT_QUERY_PART, _nPosition );

        return strFormColumnSelectQuery;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFormColumnFromQuery( )
    {
        return ENTRY_FROM_QUERY_PART;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getFormColumnJoinQueries( )
    {
        StringBuilder stringBuilderJoinQuery = new StringBuilder( );

        String strJoinSelectQueryPart = String.format( ENTRY_JOIN_SELECT_QUERY_PART, _nPosition );
        stringBuilderJoinQuery.append( strJoinSelectQueryPart );

        String strJoinFromQueryPart = String.format( ENTRY_JOIN_FROM_QUERY_PART, _nPosition );
        stringBuilderJoinQuery.append( strJoinFromQueryPart );

        String strJoinInnerJoinQuestionResponseQueryPart = String.format( ENTRY_JOIN_QUESTION_RESPONSE_QUERY_PART, _nPosition );
        stringBuilderJoinQuery.append( strJoinInnerJoinQuestionResponseQueryPart );

        String strJoinInnerJoinQuestionentryResponseQueryPart = String.format( ENTRY_JOIN_QUESTION_ENTRY_RESPONSE_QUERY_PART, _nPosition );
        stringBuilderJoinQuery.append( strJoinInnerJoinQuestionentryResponseQueryPart );

        String strJoinInnerJoinEntryResponseQueryPart = String.format( ENTRY_JOIN_ENTRY_RESPONSE_QUERY_PART, _nPosition );
        stringBuilderJoinQuery.append( strJoinInnerJoinEntryResponseQueryPart );

        String strJoinInnerJoinEntryQueryPart = String.format( ENTRY_JOIN_ENTRY_QUERY_PART, _nPosition );
        stringBuilderJoinQuery.append( strJoinInnerJoinEntryQueryPart );

        StringBuilder stringBuilderListEntryCode = new StringBuilder( );
        IFormColumn formColumn = getFormColumn( );
        if ( formColumn instanceof FormColumnEntry )
        {
            FormColumnEntry formColumnEntry = (FormColumnEntry) formColumn;
            List<String> listEntryCode = formColumnEntry.getListEntryCode( );
            if ( !CollectionUtils.isEmpty( listEntryCode ) )
            {
                buildListEntryCode( stringBuilderListEntryCode, listEntryCode );
            }
        }

        String strJoinWhereQueryPart = String.format( ENTRY_JOIN_WHERE_QUERY_PART, _nPosition, stringBuilderListEntryCode.toString( ) );
        stringBuilderJoinQuery.append( strJoinWhereQueryPart ).append( " ) " );

        String strJoinQueryPart = String.format( ENTRY_JOIN_QUERY_PART, _nPosition );
        stringBuilderJoinQuery.append( strJoinQueryPart );

        return Arrays.asList( stringBuilderJoinQuery.toString( ) );
    }

    /**
     * Build the list of all entry code from the given list quoting them and separate them by comma and after that append it to the given StringBuilder
     * 
     * @param stringBuilderListEntryCode
     *            The StringBuilder to append the list of all entry code
     * @param listEntryCode
     *            The list of entry code to manage
     */
    private void buildListEntryCode( StringBuilder stringBuilderListEntryCode, List<String> listEntryCode )
    {
        Iterator<String> iteratorListEntryCode = listEntryCode.iterator( );
        while ( iteratorListEntryCode.hasNext( ) )
        {
            String strEntryCode = iteratorListEntryCode.next( );
            stringBuilderListEntryCode.append( '\'' ).append( strEntryCode ).append( '\'' );

            if ( iteratorListEntryCode.hasNext( ) )
            {
                stringBuilderListEntryCode.append( ", " );
            }
        }
    }
}
