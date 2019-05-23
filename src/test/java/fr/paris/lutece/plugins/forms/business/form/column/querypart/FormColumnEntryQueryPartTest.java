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
package fr.paris.lutece.plugins.forms.business.form.column.querypart;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.form.column.FormColumnCell;
import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.column.impl.FormColumnEntry;
import fr.paris.lutece.plugins.forms.business.form.column.querypart.impl.sql.FormColumnEntrySQLQueryPart;
import fr.paris.lutece.plugins.forms.business.form.column.querypart.mock.DAOUtilMock;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * Test of the implementation of the IFormColumnQueryPart for the Entry column
 */
public class FormColumnEntryQueryPartTest extends LuteceTestCase
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp( ) throws Exception
    {
        super.setUp( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDown( ) throws Exception
    {
        super.tearDown( );
    }

    /**
     * Test for the {@link IFormColumnQueryPart#getFormColumnCell(fr.paris.lutece.util.sql.DAOUtil)}
     */
    public void testGetFormColumnCellEntryFormField( )
    {
        String strFormEntryFormFieldValueToRetrieve = "entry Form field value";
        DAOUtil daoUtil = new DAOUtilMock( StringUtils.EMPTY, "column_1_value", strFormEntryFormFieldValueToRetrieve );

        IFormColumn formColumn = new FormColumnEntry( 1, "Entry Form Field", new ArrayList<String>( ) );
        FormColumnEntrySQLQueryPart formColumnEntryFormFieldQueryPart = new FormColumnEntrySQLQueryPart( );
        formColumnEntryFormFieldQueryPart.setFormColumn( formColumn );

        FormColumnCell formColumnCell = formColumnEntryFormFieldQueryPart.getFormColumnCell( daoUtil );
        assertThat( formColumnCell, is( not( nullValue( ) ) ) );

        Map<String, Object> mapFormColumnCellValues = formColumnCell.getFormColumnCellValues( );
        assertThat( mapFormColumnCellValues, is( not( nullValue( ) ) ) );
        assertThat( mapFormColumnCellValues.size( ), is( 1 ) );

        Object objFormsResult = formColumnCell.getFormColumnCellValueByName( "column_1_value" );
        assertThat( objFormsResult, is( not( nullValue( ) ) ) );
        assertThat( String.valueOf( objFormsResult ), is( strFormEntryFormFieldValueToRetrieve ) );
    }

    /**
     * Test for the {@link IFormColumnQueryPart#getFormColumnCell(fr.paris.lutece.util.sql.DAOUtil)} using a column that doesn't exist
     */
    public void testGetFormColumnCellEntryFormFieldWithWrongColumnName( )
    {
        String strFormEntryFormFieldValueToRetrieve = "entry Form field value";
        DAOUtil daoUtil = new DAOUtilMock( StringUtils.EMPTY, "colonne", strFormEntryFormFieldValueToRetrieve );

        IFormColumn formColumn = new FormColumnEntry( 1, "Entry Form Field", new ArrayList<String>( ) );
        FormColumnEntrySQLQueryPart formColumnEntryFormFieldQueryPart = new FormColumnEntrySQLQueryPart( );
        formColumnEntryFormFieldQueryPart.setFormColumn( formColumn );

        try
        {
            formColumnEntryFormFieldQueryPart.getFormColumnCell( daoUtil );
            fail( "Test fail : AppException hasn't been thrown !" );
        }
        catch( AppException exception )
        {

        }
    }

    /**
     * Test for the {@link FormColumnEntrySQLQueryPart#getFormColumnJoinQueries()} method with a column which have no Entry title
     */
    public void testGetFormColumnJoinQueriesWithoutEntryTitle( )
    {
        String strJoinQueryExpected = "LEFT JOIN ( SELECT form_response_1.id_response AS id_response_1, gen_response_1.response_value "
                + "AS column_1_value FROM forms_response AS form_response_1 INNER JOIN forms_question_response AS question_response_1 "
                + "ON form_response_1.id_response = question_response_1.id_form_response INNER JOIN forms_question_entry_response AS "
                + "q_entry_response_1 ON q_entry_response_1.id_question_response = question_response_1.id_question_response INNER JOIN "
                + "genatt_response AS gen_response_1 ON gen_response_1.id_response = q_entry_response_1.id_entry_response INNER JOIN "
                + "genatt_entry AS entry_1 ON entry_1.id_entry = gen_response_1.id_entry WHERE entry_1.title IN ( ) ) AS column_1 ON "
                + "column_1.id_response_1 = response.id_response";

        IFormColumn formColumn = new FormColumnEntry( 1, "Titre", new ArrayList<>( ) );
        FormColumnEntrySQLQueryPart formColumnEntryQueryPart = new FormColumnEntrySQLQueryPart( );
        formColumnEntryQueryPart.setFormColumn( formColumn );

        List<String> listFormColumnJoinQueries = formColumnEntryQueryPart.getFormColumnJoinQueries( );
        assertThat( listFormColumnJoinQueries, is( not( nullValue( ) ) ) );
        assertThat( listFormColumnJoinQueries.size( ), is( 1 ) );

        String strFormColumnJoinQuery = removeQuerySpaces( listFormColumnJoinQueries.get( 0 ) );
        assertThat( strFormColumnJoinQuery, is( strJoinQueryExpected ) );
    }

    /**
     * Test for the {@link FormColumnEntrySQLQueryPart#getFormColumnJoinQueries()} method with a column which have no Entry title and no position
     */
    public void testGetFormColumnJoinQueriesWithoutEntryTitleAndColumnPosition( )
    {
        String strJoinQueryExpected = "LEFT JOIN ( SELECT form_response_-1.id_response AS id_response_-1, gen_response_-1.response_value "
                + "AS column_-1_value FROM forms_response AS form_response_-1 INNER JOIN forms_question_response AS question_response_-1 "
                + "ON form_response_-1.id_response = question_response_-1.id_form_response INNER JOIN forms_question_entry_response AS "
                + "q_entry_response_-1 ON q_entry_response_-1.id_question_response = question_response_-1.id_question_response INNER JOIN "
                + "genatt_response AS gen_response_-1 ON gen_response_-1.id_response = q_entry_response_-1.id_entry_response INNER JOIN "
                + "genatt_entry AS entry_-1 ON entry_-1.id_entry = gen_response_-1.id_entry WHERE entry_-1.title IN ( ) ) AS column_-1 ON "
                + "column_-1.id_response_-1 = response.id_response";

        FormColumnEntrySQLQueryPart formColumnEntryQueryPart = new FormColumnEntrySQLQueryPart( );

        List<String> listFormColumnJoinQueries = formColumnEntryQueryPart.getFormColumnJoinQueries( );
        assertThat( listFormColumnJoinQueries, is( not( nullValue( ) ) ) );
        assertThat( listFormColumnJoinQueries.size( ), is( 1 ) );

        String strFormColumnJoinQuery = removeQuerySpaces( listFormColumnJoinQueries.get( 0 ) );
        assertThat( strFormColumnJoinQuery, is( strJoinQueryExpected ) );
    }

    /**
     * Test for the {@link FormColumnEntrySQLQueryPart#getFormColumnJoinQueries()} method
     */
    public void testGetFormColumnJoinQueries( )
    {
        String strJoinQueryExpected = "LEFT JOIN ( SELECT form_response_5.id_response AS id_response_5, gen_response_5.response_value "
                + "AS column_5_value FROM forms_response AS form_response_5 INNER JOIN forms_question_response AS question_response_5 "
                + "ON form_response_5.id_response = question_response_5.id_form_response INNER JOIN forms_question_entry_response AS "
                + "q_entry_response_5 ON q_entry_response_5.id_question_response = question_response_5.id_question_response INNER JOIN "
                + "genatt_response AS gen_response_5 ON gen_response_5.id_response = q_entry_response_5.id_entry_response INNER JOIN "
                + "genatt_entry AS entry_5 ON entry_5.id_entry = gen_response_5.id_entry WHERE entry_5.title IN ( 'FirstName', 'LastName' ) ) "
                + "AS column_5 ON column_5.id_response_5 = response.id_response";

        List<String> listEntryTitle = Arrays.asList( "FirstName", "LastName" );
        IFormColumn formColumn = new FormColumnEntry( 5, "Titre", listEntryTitle );

        FormColumnEntrySQLQueryPart formColumnEntryQueryPart = new FormColumnEntrySQLQueryPart( );
        formColumnEntryQueryPart.setFormColumn( formColumn );

        List<String> listFormColumnJoinQueries = formColumnEntryQueryPart.getFormColumnJoinQueries( );
        assertThat( listFormColumnJoinQueries, is( not( nullValue( ) ) ) );
        assertThat( listFormColumnJoinQueries.size( ), is( 1 ) );

        String strFormColumnJoinQuery = removeQuerySpaces( listFormColumnJoinQueries.get( 0 ) );
        assertThat( strFormColumnJoinQuery, is( strJoinQueryExpected ) );
    }

    /**
     * Remove all the unnecessary spaces of a query
     * 
     * @param strQuery
     *            The query to remove the spaces
     * @return the given query without all unnecessary spaces
     */
    private String removeQuerySpaces( String strQuery )
    {
        String strQueryResult = StringUtils.EMPTY;

        if ( StringUtils.isNotBlank( strQuery ) )
        {
            strQueryResult = strQuery.trim( ).replaceAll( " +", " " );
            strQueryResult = strQueryResult.replaceAll( " +,", "," );
        }

        return strQueryResult;
    }
}
