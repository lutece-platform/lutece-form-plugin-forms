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
package fr.paris.lutece.plugins.forms.business.form.column.querypart.impl;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.column.impl.FormColumnEntryGeolocation;
import fr.paris.lutece.plugins.forms.util.FormEntryNameConstants;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * Implementation of the IFormColumnQueryPart interface for an Entry column
 */
public class FormColumnEntryGeolocationQueryPart extends AbstractFormColumnQueryPart
{
    // Constants
    private static final String ENTRY_SELECT_QUERY_PART = "column_%1$s.column_%1$s_x_value, column_%1$s.column_%1$s_y_value, column_%1$s.column_%1$s_addr_value";
    private static final String ENTRY_FROM_QUERY_PART = StringUtils.EMPTY;
    private static final String ENTRY_JOIN_SELECT_QUERY_PART = "LEFT JOIN ( SELECT form_response_%1$s.id_response AS id_response_%1$s, gen_response_%1$s_x.response_value AS column_%1$s_x_value"
            + ", gen_response_%1$s_y.response_value AS column_%1$s_y_value, gen_response_%1$s_addr.response_value AS column_%1$s_addr_value";
    private static final String ENTRY_JOIN_FROM_QUERY_PART = " FROM forms_response AS form_response_%s ";
    private static final String ENTRY_JOIN_QUESTION_RESPONSE_QUERY_PART = " INNER JOIN forms_question_response AS question_response_%1$s ON form_response_%1$s.id_response = question_response_%1$s.id_form_response";
    private static final String ENTRY_JOIN_QUESTION_QUERY_PART = " INNER JOIN forms_question AS q_forms_question_%1$s ON q_forms_question_%1$s.id_question = question_response_%1$s.id_question ";
    private static final String ENTRY_JOIN_ENTRY_QUERY_PART = " INNER JOIN genatt_entry AS entry_%1$s ON entry_%1$s.id_entry = q_forms_question_%1$s.id_entry ";
    private static final String ENTRY_JOIN_QUESTION_ENTRY_RESPONSE_X_QUERY_PART = " INNER JOIN forms_question_entry_response AS q_entry_response_%1$s_x ON q_entry_response_%1$s_x.id_question_response = question_response_%1$s.id_question_response ";
    private static final String ENTRY_JOIN_ENTRY_RESPONSE_X_QUERY_PART = " INNER JOIN genatt_response AS gen_response_%1$s_x ON gen_response_%1$s_x.id_response = q_entry_response_%1$s_x.id_entry_response ";
    private static final String ENTRY_JOIN_ENTRY_FIELD_RESPONSE_X_QUERY_PART = " INNER JOIN genatt_field AS gen_field_%1$s_x ON gen_field_%1$s_x.id_field = gen_response_%1$s_x.id_field ";
    private static final String ENTRY_JOIN_QUESTION_ENTRY_RESPONSE_Y_QUERY_PART = " INNER JOIN forms_question_entry_response AS q_entry_response_%1$s_y ON q_entry_response_%1$s_y.id_question_response = question_response_%1$s.id_question_response ";
    private static final String ENTRY_JOIN_ENTRY_RESPONSE_Y_QUERY_PART = " INNER JOIN genatt_response AS gen_response_%1$s_y ON gen_response_%1$s_y.id_response = q_entry_response_%1$s_y.id_entry_response ";
    private static final String ENTRY_JOIN_ENTRY_FIELD_RESPONSE_Y_QUERY_PART = " INNER JOIN genatt_field AS gen_field_%1$s_y ON gen_field_%1$s_y.id_field = gen_response_%1$s_y.id_field ";
    private static final String ENTRY_JOIN_QUESTION_ENTRY_RESPONSE_ADDR_QUERY_PART = " INNER JOIN forms_question_entry_response AS q_entry_response_%1$s_addr ON q_entry_response_%1$s_addr.id_question_response = question_response_%1$s.id_question_response ";
    private static final String ENTRY_JOIN_ENTRY_RESPONSE_ADDR_QUERY_PART = " INNER JOIN genatt_response AS gen_response_%1$s_addr ON gen_response_%1$s_addr.id_response = q_entry_response_%1$s_addr.id_entry_response ";
    private static final String ENTRY_JOIN_ENTRY_FIELD_RESPONSE_ADDR_QUERY_PART = " INNER JOIN genatt_field AS gen_field_%1$s_addr ON gen_field_%1$s_addr.id_field = gen_response_%1$s_addr.id_field ";
    private static final String ENTRY_JOIN_WHERE_QUERY_PART = " WHERE entry_%1$s.title IN ( %2$s ) and gen_field_%1$s_x.title = 'X' and gen_field_%1$s_y.title = 'Y' and gen_field_%1$s_addr.title = 'address' ";
    private static final String ENTRY_JOIN_QUERY_PART = " AS column_%1$s ON column_%1$s.id_response_%1$s = response.id_response";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFormColumnSelectQuery( )
    {
        int nFormColumnPosition = getFormColumnPosition( );
        String strFormColumnSelectQuery = String.format( ENTRY_SELECT_QUERY_PART, nFormColumnPosition );

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
        int nFormColumnPosition = getFormColumnPosition( );

        StringBuilder stringBuilderJoinQuery = new StringBuilder( );

        String strJoinSelectQueryPart = String.format( ENTRY_JOIN_SELECT_QUERY_PART, nFormColumnPosition );
        stringBuilderJoinQuery.append( strJoinSelectQueryPart );

        String strJoinFromQueryPart = String.format( ENTRY_JOIN_FROM_QUERY_PART, nFormColumnPosition );
        stringBuilderJoinQuery.append( strJoinFromQueryPart );

        String strJoinInnerJoinQuestionResponseQueryPart = String.format( ENTRY_JOIN_QUESTION_RESPONSE_QUERY_PART, nFormColumnPosition );
        stringBuilderJoinQuery.append( strJoinInnerJoinQuestionResponseQueryPart );

        String strJoinInnerJoinQuestionQueryPart = String.format( ENTRY_JOIN_QUESTION_QUERY_PART, nFormColumnPosition );
        stringBuilderJoinQuery.append( strJoinInnerJoinQuestionQueryPart );

        String strJoinInnerJoinEntryQueryPart = String.format( ENTRY_JOIN_ENTRY_QUERY_PART, nFormColumnPosition );
        stringBuilderJoinQuery.append( strJoinInnerJoinEntryQueryPart );

        String strJoinInnerJoinQuestionentryResponseXQueryPart = String.format( ENTRY_JOIN_QUESTION_ENTRY_RESPONSE_X_QUERY_PART, nFormColumnPosition );
        stringBuilderJoinQuery.append( strJoinInnerJoinQuestionentryResponseXQueryPart );

        String strJoinInnerJoinEntryResponseXQueryPart = String.format( ENTRY_JOIN_ENTRY_RESPONSE_X_QUERY_PART, nFormColumnPosition );
        stringBuilderJoinQuery.append( strJoinInnerJoinEntryResponseXQueryPart );

        String strJoinInnerJoinEntryFieldResponseXQueryPart = String.format( ENTRY_JOIN_ENTRY_FIELD_RESPONSE_X_QUERY_PART, nFormColumnPosition );
        stringBuilderJoinQuery.append( strJoinInnerJoinEntryFieldResponseXQueryPart );

        String strJoinInnerJoinQuestionentryResponseYQueryPart = String.format( ENTRY_JOIN_QUESTION_ENTRY_RESPONSE_Y_QUERY_PART, nFormColumnPosition );
        stringBuilderJoinQuery.append( strJoinInnerJoinQuestionentryResponseYQueryPart );

        String strJoinInnerJoinEntryResponseYQueryPart = String.format( ENTRY_JOIN_ENTRY_RESPONSE_Y_QUERY_PART, nFormColumnPosition );
        stringBuilderJoinQuery.append( strJoinInnerJoinEntryResponseYQueryPart );

        String strJoinInnerJoinEntryFieldResponseYQueryPart = String.format( ENTRY_JOIN_ENTRY_FIELD_RESPONSE_Y_QUERY_PART, nFormColumnPosition );
        stringBuilderJoinQuery.append( strJoinInnerJoinEntryFieldResponseYQueryPart );

        String strJoinInnerJoinQuestionentryResponseAddrQueryPart = String.format( ENTRY_JOIN_QUESTION_ENTRY_RESPONSE_ADDR_QUERY_PART, nFormColumnPosition );
        stringBuilderJoinQuery.append( strJoinInnerJoinQuestionentryResponseAddrQueryPart );

        String strJoinInnerJoinEntryResponseAddrQueryPart = String.format( ENTRY_JOIN_ENTRY_RESPONSE_ADDR_QUERY_PART, nFormColumnPosition );
        stringBuilderJoinQuery.append( strJoinInnerJoinEntryResponseAddrQueryPart );

        String strJoinInnerJoinEntryFieldResponseAddrQueryPart = String.format( ENTRY_JOIN_ENTRY_FIELD_RESPONSE_ADDR_QUERY_PART, nFormColumnPosition );
        stringBuilderJoinQuery.append( strJoinInnerJoinEntryFieldResponseAddrQueryPart );

        StringBuilder stringBuilderListEntryTitle = new StringBuilder( );
        IFormColumn formColumn = getFormColumn( );
        if ( formColumn instanceof FormColumnEntryGeolocation )
        {
            FormColumnEntryGeolocation formColumnEntry = (FormColumnEntryGeolocation) formColumn;
            List<String> listEntryTitle = formColumnEntry.getListEntryTitle( );
            if ( !CollectionUtils.isEmpty( listEntryTitle ) )
            {
                buildListEntryTitle( stringBuilderListEntryTitle, listEntryTitle );
            }
        }

        String strJoinWhereQueryPart = String.format( ENTRY_JOIN_WHERE_QUERY_PART, nFormColumnPosition, stringBuilderListEntryTitle.toString( ) );
        stringBuilderJoinQuery.append( strJoinWhereQueryPart ).append( " ) " );

        String strJoinQueryPart = String.format( ENTRY_JOIN_QUERY_PART, nFormColumnPosition );
        stringBuilderJoinQuery.append( strJoinQueryPart );

        return Arrays.asList( stringBuilderJoinQuery.toString( ) );
    }

    /**
     * Build the list of all entry title from the given list quoting them and separate them by comma and after that append it to the given StringBuilder
     * 
     * @param stringBuilderListEntryTitle
     *            The StringBuilder to append the list of all entry title
     * @param listEntryTitle
     *            The list of entry title to manage
     */
    private void buildListEntryTitle( StringBuilder stringBuilderListEntryTitle, List<String> listEntryTitle )
    {
        Iterator<String> iteratorListEntryTitle = listEntryTitle.iterator( );
        while ( iteratorListEntryTitle.hasNext( ) )
        {
            String strEntryTitle = iteratorListEntryTitle.next( );
            stringBuilderListEntryTitle.append( '\'' ).append( strEntryTitle ).append( '\'' );

            if ( iteratorListEntryTitle.hasNext( ) )
            {
                stringBuilderListEntryTitle.append( ", " );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, Object> getMapFormColumnValues( DAOUtil daoUtil )
    {
        int nFormColumnPosition = getFormColumnPosition( );
        Map<String, Object> mapFormColumnValues = new LinkedHashMap<>( );
        String strEntryValueColumnName;
        strEntryValueColumnName = String.format( FormEntryNameConstants.COLUMN_ENTRY_GEOLOC_VALUE_PATTERN_X, nFormColumnPosition );
        mapFormColumnValues.put( strEntryValueColumnName, daoUtil.getString( strEntryValueColumnName ) );
        strEntryValueColumnName = String.format( FormEntryNameConstants.COLUMN_ENTRY_GEOLOC_VALUE_PATTERN_Y, nFormColumnPosition );
        mapFormColumnValues.put( strEntryValueColumnName, daoUtil.getString( strEntryValueColumnName ) );
        strEntryValueColumnName = String.format( FormEntryNameConstants.COLUMN_ENTRY_GEOLOC_VALUE_PATTERN_ADDR, nFormColumnPosition );
        mapFormColumnValues.put( strEntryValueColumnName, daoUtil.getString( strEntryValueColumnName ) );

        return mapFormColumnValues;
    }

    /**
     * Return the position of the FormColumn or {@linkplain NumberUtils.INTEGER_MINUS_ONE} if doesn't exist
     * 
     * @return the position of the FormColumn
     */
    private int getFormColumnPosition( )
    {
        int nFormColumnPosition = NumberUtils.INTEGER_MINUS_ONE;
        IFormColumn formColumn = getFormColumn( );
        if ( formColumn != null )
        {
            nFormColumnPosition = formColumn.getFormColumnPosition( );
        }

        return nFormColumnPosition;
    }
}
