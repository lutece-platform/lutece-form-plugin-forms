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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.column.impl.FormColumnEntry;
import fr.paris.lutece.plugins.forms.business.form.column.querypart.IFormColumnQueryPart;
import fr.paris.lutece.plugins.forms.business.form.column.querypart.impl.sql.FormColumnEntrySQLQueryPart;
import fr.paris.lutece.plugins.forms.business.form.column.querypart.mock.FormColumnFormResponseDateCreationQueryPartMock;
import fr.paris.lutece.plugins.forms.business.form.column.querypart.mock.FormColumnFormsQueryPartMock;
import fr.paris.lutece.plugins.forms.business.form.column.querypart.mock.FormColumnWorkflowStateQueryPartMock;
import fr.paris.lutece.plugins.forms.business.form.column.querypart.mock.FormPanelFormResponseInitializerQueryPartMock;
import fr.paris.lutece.plugins.forms.business.form.column.querypart.mock.FormPanelFormsInitializerQueryPartMock;
import fr.paris.lutece.plugins.forms.business.form.filter.querypart.FormFilterFormsQueryPartMock;
import fr.paris.lutece.plugins.forms.business.form.filter.querypart.FormFilterWorkflowStateQueryPartMock;
import fr.paris.lutece.plugins.forms.business.form.filter.querypart.IFormFilterQueryPart;
import fr.paris.lutece.plugins.forms.business.form.filter.querypart.impl.FormFilterEntryQueryPart;
import fr.paris.lutece.plugins.forms.business.form.filter.querypart.impl.FormFilterFormsQueryPart;
import fr.paris.lutece.plugins.forms.business.form.filter.querypart.impl.FormFilterWorkflowStateQueryPart;
import fr.paris.lutece.plugins.forms.business.form.panel.initializer.querypart.IFormPanelInitializerQueryPart;
import fr.paris.lutece.plugins.forms.util.FormMultiviewFormsNameConstants;
import fr.paris.lutece.plugins.forms.util.FormMultiviewWorkflowStateNameConstants;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test cases for the QueryBuilder methods
 */
public class QueryBuilderTest extends LuteceTestCase
{
    // Variables
    private List<IFormPanelInitializerQueryPart> _listFormPanelInitializerQueryPart;
    private List<IFormColumnQueryPart> _listFormColumnQueryPart;
    private List<IFormFilterQueryPart> _listFormFilterQueryPart;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUp( ) throws Exception
    {
        super.setUp( );

        // Reset the list in session
        _listFormPanelInitializerQueryPart = new ArrayList<>( );
        _listFormColumnQueryPart = new ArrayList<>( );
        _listFormFilterQueryPart = new ArrayList<>( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tearDown( ) throws Exception
    {
        super.tearDown( );
    }

    /**
     * Test for the {@link QueryBuilder#buildQuery(List, List)} method with only the FormPanel. Without column the query built must be empty
     */
    public void testBuildQueryWithFormPanelWithoutColumn( )
    {
        String strBasicQueryToFind = StringUtils.EMPTY;

        _listFormPanelInitializerQueryPart.add( new FormPanelFormsInitializerQueryPartMock( ) );

        checkQueryToBuilt( strBasicQueryToFind );
    }

    /**
     * Test the for {@link QueryBuilder#buildQuery(List, List)} method with the columnForm without PanelInitializer
     */
    public void testBuildQueryWithColumnWithoutFormPanelInitializer( )
    {
        String strBasicQueryToFind = StringUtils.EMPTY;

        _listFormColumnQueryPart.add( new FormColumnFormsQueryPartMock( ) );

        checkQueryToBuilt( strBasicQueryToFind );
    }

    /**
     * Test the for {@link QueryBuilder#buildQuery(List, List)} method with the FormPanel and the columnForms
     */
    public void testBuildQueryWithColumnForms( )
    {
        String strBasicQueryToFind = "SELECT id_response, id_form, title FROM forms_form AS form INNER JOIN forms_response "
                + "AS response ON response.id_form = form.id_form";

        _listFormColumnQueryPart.add( new FormColumnFormsQueryPartMock( ) );

        _listFormPanelInitializerQueryPart.add( new FormPanelFormsInitializerQueryPartMock( ) );

        checkQueryToBuilt( strBasicQueryToFind );
    }

    /**
     * Test the for {@link QueryBuilder#buildQuery(List, List)} method with the FormPanel and the columnForm without the FormPanelFormResponseInitializer
     */
    public void testBuildQueryWithColumnFormsWithoutFormsInitializer( )
    {
        String strBasicQueryToFind = "SELECT id_form, title FROM";

        _listFormColumnQueryPart.add( new FormColumnFormsQueryPartMock( ) );

        _listFormPanelInitializerQueryPart.add( new FormPanelFormResponseInitializerQueryPartMock( ) );

        checkQueryToBuilt( strBasicQueryToFind );
    }

    /**
     * Test the for {@link QueryBuilder#buildQuery(List, List)} method with the FormPanel and three columns: the Form, WorkflowState and
     * FormResponseDateCreation columns
     */
    public void testBuildQueryWithColumnFormWorkflowStateDate( )
    {
        String strQueryToFind = "SELECT id_response, id_form, title, workflow_state_name, response_creation_date FROM forms_form AS "
                + "form INNER JOIN forms_response AS response ON response.id_form = form.id_form LEFT JOIN workflow_resource_workflow "
                + "AS wf_resource_workflow ON wf_resource_workflow.id_resource = response.id_response LEFT JOIN workflow_state AS "
                + "ws_workflow_state ON ws_workflow_state.id_state = wf_resource_workflow.id_state";

        _listFormColumnQueryPart.add( new FormColumnFormsQueryPartMock( ) );
        _listFormColumnQueryPart.add( new FormColumnWorkflowStateQueryPartMock( ) );
        _listFormColumnQueryPart.add( new FormColumnFormResponseDateCreationQueryPartMock( ) );

        _listFormPanelInitializerQueryPart.add( new FormPanelFormsInitializerQueryPartMock( ) );

        checkQueryToBuilt( strQueryToFind );
    }

    /**
     * Test the for {@link QueryBuilder#buildQuery(List, List)} method with the FormPanel and the ColumnForm and its filter with the good name for the item
     */
    public void testBuildQueryWithFormColumnWithFilter( )
    {
        String strQueryToFind = "SELECT id_response, id_form, title FROM forms_form AS form INNER JOIN forms_response "
                + "AS response ON response.id_form = form.id_form WHERE 1=1 AND ( form.id_form = ? )";

        _listFormColumnQueryPart.add( new FormColumnFormsQueryPartMock( ) );

        _listFormPanelInitializerQueryPart.add( new FormPanelFormsInitializerQueryPartMock( ) );

        FormParameters formParameters = new FormParameters( );
        Map<String, Object> mapFilterNameValues = new LinkedHashMap<>( );
        mapFilterNameValues.put( FormMultiviewFormsNameConstants.FILTER_ID_FORM, 4 );
        formParameters.setFormParametersMap( mapFilterNameValues );

        FormFilterFormsQueryPart formFilterFormQueryPart = new FormFilterFormsQueryPartMock( );
        formFilterFormQueryPart.buildFormFilterQuery( formParameters );
        _listFormFilterQueryPart.add( formFilterFormQueryPart );

        checkQueryToBuilt( strQueryToFind );

        List<String> listParametersValue = formParameters.getListUsedParametersValue( );
        assertThat( listParametersValue.size( ), is( NumberUtils.INTEGER_ONE ) );
        assertThat( listParametersValue.get( NumberUtils.INTEGER_ZERO ), is( "4" ) );
    }

    /**
     * Test the for {@link QueryBuilder#buildQuery(List, List)} method with the FormPanel and two columns and two filters
     */
    public void testBuildQueryWithTwoColumnsAndTwoFilters( )
    {
        String strQueryToFind = "SELECT id_response, id_form, title, workflow_state_name FROM forms_form AS form INNER JOIN "
                + "forms_response AS response ON response.id_form = form.id_form LEFT JOIN workflow_resource_workflow AS "
                + "wf_resource_workflow ON wf_resource_workflow.id_resource = response.id_response LEFT JOIN workflow_state AS "
                + "ws_workflow_state ON ws_workflow_state.id_state = wf_resource_workflow.id_state WHERE 1=1 AND ( form.id_form = ? ) "
                + "AND ( ws_workflow_state.id_state = ? )";

        _listFormColumnQueryPart.add( new FormColumnFormsQueryPartMock( ) );
        _listFormColumnQueryPart.add( new FormColumnWorkflowStateQueryPartMock( ) );

        _listFormPanelInitializerQueryPart.add( new FormPanelFormsInitializerQueryPartMock( ) );

        FormParameters formFilterItemForms = new FormParameters( );
        Map<String, Object> mapFilterNameValuesForms = new LinkedHashMap<>( );
        mapFilterNameValuesForms.put( FormMultiviewFormsNameConstants.FILTER_ID_FORM, 4 );
        formFilterItemForms.setFormParametersMap( mapFilterNameValuesForms );

        FormFilterFormsQueryPart formFilterFormsQueryPart = new FormFilterFormsQueryPartMock( );
        formFilterFormsQueryPart.buildFormFilterQuery( formFilterItemForms );
        _listFormFilterQueryPart.add( formFilterFormsQueryPart );

        FormParameters formFilterItemWorkflowState = new FormParameters( );
        Map<String, Object> mapFilterNameValuesWorkflowState = new LinkedHashMap<>( );
        mapFilterNameValuesWorkflowState.put( FormMultiviewWorkflowStateNameConstants.FILTER_ID_WORKFLOW_STATE, 12 );
        formFilterItemWorkflowState.setFormParametersMap( mapFilterNameValuesWorkflowState );

        FormFilterWorkflowStateQueryPart formFilterWorkflowStateQueryPart = new FormFilterWorkflowStateQueryPartMock( );
        formFilterWorkflowStateQueryPart.buildFormFilterQuery( formFilterItemWorkflowState );
        _listFormFilterQueryPart.add( formFilterWorkflowStateQueryPart );

        checkQueryToBuilt( strQueryToFind );

        List<String> listUsedParametersFormsValue = formFilterItemForms.getListUsedParametersValue( );
        assertThat( listUsedParametersFormsValue.size( ), is( NumberUtils.INTEGER_ONE ) );
        assertThat( listUsedParametersFormsValue.get( NumberUtils.INTEGER_ZERO ), is( "4" ) );

        List<String> listUsedParametersWorkflowValue = formFilterItemWorkflowState.getListUsedParametersValue( );
        assertThat( listUsedParametersWorkflowValue.size( ), is( NumberUtils.INTEGER_ONE ) );
        assertThat( listUsedParametersWorkflowValue.get( NumberUtils.INTEGER_ZERO ), is( "12" ) );
    }

    /**
     * Test the for {@link QueryBuilder#buildQuery(List, List)} method with the FormPanel and the ColumnForms and its filter with the item has no value. In this
     * case the condition is not added to the global query
     */
    public void testBuildQueryWithFormsColumnWithFilterWithoutName( )
    {
        String strQueryToFind = "SELECT id_response, id_form, title FROM forms_form AS form INNER JOIN forms_response AS "
                + "response ON response.id_form = form.id_form WHERE 1=1";

        _listFormColumnQueryPart.add( new FormColumnFormsQueryPartMock( ) );

        _listFormPanelInitializerQueryPart.add( new FormPanelFormsInitializerQueryPartMock( ) );

        FormFilterFormsQueryPart formFilterFormsQueryPart = new FormFilterFormsQueryPartMock( );
        formFilterFormsQueryPart.buildFormFilterQuery( new FormParameters( ) );
        _listFormFilterQueryPart.add( formFilterFormsQueryPart );

        checkQueryToBuilt( strQueryToFind );
    }

    /**
     * Test the for {@link QueryBuilder#buildQuery(List, List)} method with the FormPanel and one ColumnEntry
     */
    public void testBuildQueryWithOneColumnEntry( )
    {
        String strQueryToFind = "SELECT id_response, column_3.column_3_value FROM forms_form AS form INNER JOIN forms_response AS "
                + "response ON response.id_form = form.id_form LEFT JOIN ( SELECT form_response_3.id_response AS id_response_3, "
                + "gen_response_3.response_value AS column_3_value FROM forms_response AS form_response_3 INNER JOIN forms_question_response "
                + "AS question_response_3 ON form_response_3.id_response = question_response_3.id_form_response INNER JOIN "
                + "forms_question_entry_response AS q_entry_response_3 ON q_entry_response_3.id_question_response = "
                + "question_response_3.id_question_response INNER JOIN genatt_response AS gen_response_3 ON gen_response_3.id_response = "
                + "q_entry_response_3.id_entry_response INNER JOIN genatt_entry AS entry_3 ON entry_3.id_entry = gen_response_3.id_entry "
                + "WHERE entry_3.title IN ( 'Nom', 'Prénom' ) ) AS column_3 ON column_3.id_response_3 = response.id_response";

        IFormColumn formColumnEntry = new FormColumnEntry( 3, "Colonne 3", Arrays.asList( "Nom", "Prénom" ) );
        FormColumnEntrySQLQueryPart formColumnEntryQueryPart = new FormColumnEntryQueryPartMock( 3 );
        formColumnEntryQueryPart.setFormColumn( formColumnEntry );
        _listFormColumnQueryPart.add( formColumnEntryQueryPart );

        _listFormPanelInitializerQueryPart.add( new FormPanelFormsInitializerQueryPartMock( ) );

        checkQueryToBuilt( strQueryToFind );
    }

    /**
     * Test the for {@link QueryBuilder#buildQuery(List, List)} method with the FormPanel and three ColumnEntry
     */
    public void testBuildQueryWithThreeColumnEntrys( )
    {
        String strQueryToFind = "SELECT id_response, column_3.column_3_value, column_5.column_5_value, column_7.column_7_value FROM "
                + "forms_form AS form INNER JOIN forms_response AS response ON response.id_form = form.id_form LEFT JOIN ( SELECT "
                + "form_response_3.id_response AS id_response_3, gen_response_3.response_value AS column_3_value FROM forms_response "
                + "AS form_response_3 INNER JOIN forms_question_response AS question_response_3 ON form_response_3.id_response = "
                + "question_response_3.id_form_response INNER JOIN forms_question_entry_response AS q_entry_response_3 ON "
                + "q_entry_response_3.id_question_response = question_response_3.id_question_response INNER JOIN genatt_response AS "
                + "gen_response_3 ON gen_response_3.id_response = q_entry_response_3.id_entry_response INNER JOIN genatt_entry AS entry_3 "
                + "ON entry_3.id_entry = gen_response_3.id_entry WHERE entry_3.title IN ( 'Nom', 'Prénom' ) ) AS column_3 ON "
                + "column_3.id_response_3 = response.id_response LEFT JOIN ( SELECT form_response_5.id_response AS id_response_5, "
                + "gen_response_5.response_value AS column_5_value FROM forms_response AS form_response_5 INNER JOIN forms_question_response "
                + "AS question_response_5 ON form_response_5.id_response = question_response_5.id_form_response INNER JOIN "
                + "forms_question_entry_response AS q_entry_response_5 ON q_entry_response_5.id_question_response = "
                + "question_response_5.id_question_response INNER JOIN genatt_response AS gen_response_5 ON gen_response_5.id_response = "
                + "q_entry_response_5.id_entry_response INNER JOIN genatt_entry AS entry_5 ON entry_5.id_entry = gen_response_5.id_entry "
                + "WHERE entry_5.title IN ( 'Date de naissance' ) ) AS column_5 ON column_5.id_response_5 = response.id_response LEFT JOIN ( "
                + "SELECT form_response_7.id_response AS id_response_7, gen_response_7.response_value AS column_7_value FROM forms_response "
                + "AS form_response_7 INNER JOIN forms_question_response AS question_response_7 ON form_response_7.id_response = "
                + "question_response_7.id_form_response INNER JOIN forms_question_entry_response AS q_entry_response_7 ON "
                + "q_entry_response_7.id_question_response = question_response_7.id_question_response INNER JOIN genatt_response AS "
                + "gen_response_7 ON gen_response_7.id_response = q_entry_response_7.id_entry_response INNER JOIN genatt_entry AS entry_7 ON "
                + "entry_7.id_entry = gen_response_7.id_entry WHERE entry_7.title IN ( 'Adresse', 'Téléphone' ) ) AS column_7 ON "
                + "column_7.id_response_7 = response.id_response";

        IFormColumn formColumnEntryOne = new FormColumnEntry( 3, "Colonne 3", Arrays.asList( "Nom", "Prénom" ) );
        FormColumnEntrySQLQueryPart formColumnEntryQueryPartOne = new FormColumnEntryQueryPartMock( 3 );
        formColumnEntryQueryPartOne.setFormColumn( formColumnEntryOne );
        _listFormColumnQueryPart.add( formColumnEntryQueryPartOne );

        IFormColumn formColumnEntryTwo = new FormColumnEntry( 5, "Colonne 5", Arrays.asList( "Date de naissance" ) );
        FormColumnEntrySQLQueryPart formColumnEntryQueryPartTwo = new FormColumnEntryQueryPartMock( 5 );
        formColumnEntryQueryPartTwo.setFormColumn( formColumnEntryTwo );
        _listFormColumnQueryPart.add( formColumnEntryQueryPartTwo );

        IFormColumn formColumnEntryThree = new FormColumnEntry( 7, "Colonne 7", Arrays.asList( "Adresse", "Téléphone" ) );
        FormColumnEntrySQLQueryPart formColumnEntryQueryPartThree = new FormColumnEntryQueryPartMock( 7 );
        formColumnEntryQueryPartThree.setFormColumn( formColumnEntryThree );
        _listFormColumnQueryPart.add( formColumnEntryQueryPartThree );

        _listFormPanelInitializerQueryPart.add( new FormPanelFormsInitializerQueryPartMock( ) );

        checkQueryToBuilt( strQueryToFind );
    }

    /**
     * Test the for {@link QueryBuilder#buildQuery(List, List)} method with the FormPanel and two ColumnEntry and a Filter on one
     */
    public void testBuildQueryWithColumnEntrysWithFilter( )
    {
        String strQueryToFind = "SELECT id_response, column_3.column_3_value, column_5.column_5_value FROM forms_form AS "
                + "form INNER JOIN forms_response AS response ON response.id_form = form.id_form LEFT JOIN ( SELECT "
                + "form_response_3.id_response AS id_response_3, gen_response_3.response_value AS column_3_value FROM forms_response "
                + "AS form_response_3 INNER JOIN forms_question_response AS question_response_3 ON form_response_3.id_response = "
                + "question_response_3.id_form_response INNER JOIN forms_question_entry_response AS q_entry_response_3 ON "
                + "q_entry_response_3.id_question_response = question_response_3.id_question_response INNER JOIN genatt_response AS "
                + "gen_response_3 ON gen_response_3.id_response = q_entry_response_3.id_entry_response INNER JOIN genatt_entry AS entry_3 "
                + "ON entry_3.id_entry = gen_response_3.id_entry WHERE entry_3.title IN ( 'Nom', 'Prénom' ) ) AS column_3 ON "
                + "column_3.id_response_3 = response.id_response LEFT JOIN ( SELECT form_response_5.id_response AS id_response_5, "
                + "gen_response_5.response_value AS column_5_value FROM forms_response AS form_response_5 INNER JOIN forms_question_response "
                + "AS question_response_5 ON form_response_5.id_response = question_response_5.id_form_response INNER JOIN "
                + "forms_question_entry_response AS q_entry_response_5 ON q_entry_response_5.id_question_response = "
                + "question_response_5.id_question_response INNER JOIN genatt_response AS gen_response_5 ON gen_response_5.id_response = "
                + "q_entry_response_5.id_entry_response INNER JOIN genatt_entry AS entry_5 ON entry_5.id_entry = gen_response_5.id_entry WHERE "
                + "entry_5.title IN ( 'Date de naissance' ) ) AS column_5 ON column_5.id_response_5 = response.id_response WHERE 1=1 AND ( "
                + "column_5.column_5_value = ? )";

        IFormColumn formColumnEntryOne = new FormColumnEntry( 3, "Colonne 3", Arrays.asList( "Nom", "Prénom" ) );
        FormColumnEntrySQLQueryPart formColumnEntryQueryPartOne = new FormColumnEntryQueryPartMock( 3 );
        formColumnEntryQueryPartOne.setFormColumn( formColumnEntryOne );
        _listFormColumnQueryPart.add( formColumnEntryQueryPartOne );

        IFormColumn formColumnEntryTwo = new FormColumnEntry( 5, "Colonne 5", Arrays.asList( "Date de naissance" ) );
        FormColumnEntrySQLQueryPart formColumnEntryQueryPartTwo = new FormColumnEntryQueryPartMock( 5 );
        formColumnEntryQueryPartTwo.setFormColumn( formColumnEntryTwo );
        _listFormColumnQueryPart.add( formColumnEntryQueryPartTwo );

        _listFormPanelInitializerQueryPart.add( new FormPanelFormsInitializerQueryPartMock( ) );

        FormParameters formFilterItemEntry = new FormParameters( );
        Map<String, Object> mapFilterNameValues = new LinkedHashMap<>( );
        mapFilterNameValues.put( "column_5", "test colonne 5" );
        formFilterItemEntry.setFormParametersMap( mapFilterNameValues );

        IFormFilterQueryPart formFilterEntryQueryPart = new FormFilterEntryQueryPart( );
        formFilterEntryQueryPart.buildFormFilterQuery( formFilterItemEntry );
        _listFormFilterQueryPart.add( formFilterEntryQueryPart );

        checkQueryToBuilt( strQueryToFind );

        List<String> listUsedParametersValue = formFilterItemEntry.getListUsedParametersValue( );
        assertThat( listUsedParametersValue.size( ), is( NumberUtils.INTEGER_ONE ) );
        assertThat( listUsedParametersValue.get( NumberUtils.INTEGER_ZERO ), is( "test colonne 5" ) );
    }

    /**
     * Build the query from the list of FormColumnQueryPart and the list of FormFilterQueryPart and make the test with the built query and the given expected
     * query
     * 
     * @param strQueryToFind
     *            The query to find
     */
    private void checkQueryToBuilt( String strQueryToFind )
    {
        String strQueryBuilt = QueryBuilder.buildQuery( _listFormPanelInitializerQueryPart, _listFormColumnQueryPart, _listFormFilterQueryPart );
        assertThat( strQueryBuilt, is( not( nullValue( ) ) ) );
        assertThat( removeQuerySpaces( strQueryBuilt ), is( strQueryToFind ) );
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
