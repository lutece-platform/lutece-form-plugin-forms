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
package fr.paris.lutece.plugins.forms.business.form.filter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.form.FormParameters;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test for the FormFilterQueryBuilder
 */
public class FormFilterQueryBuilderTest extends LuteceTestCase
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
     * Test of the method {@link FormFilterQueryBuilder#buildFormFilterQuery(String, FormParameters)}
     */
    public void testBuildFormFilterQueryWithFormPattern( )
    {
        String strFormFilterExpected = "form.id_form = ?";

        String strFormFilterQueryPattern = "form.id_form = $id_form$";

        Map<String, Object> mapFilterNameValues = new LinkedHashMap<>( );
        mapFilterNameValues.put( "id_form", 4 );
        FormParameters formFilterItem = new FormParameters( );
        formFilterItem.setFormParametersMap( mapFilterNameValues );

        String strFormFilterQuery = FormFilterQueryBuilder.buildFormFilterQuery( strFormFilterQueryPattern, formFilterItem );
        assertThat( strFormFilterQuery, is( not( nullValue( ) ) ) );
        assertThat( strFormFilterQuery, is( strFormFilterExpected ) );

        List<String> listUsedParameterValues = formFilterItem.getListUsedParametersValue( );
        assertThat( listUsedParameterValues.size( ), is( NumberUtils.INTEGER_ONE ) );
        assertThat( listUsedParameterValues.get( NumberUtils.INTEGER_ZERO ), is( "4" ) );
    }

    /**
     * Test of the method {@link FormFilterQueryBuilder#buildFormFilterQuery(String, FormParameters)} with a null value
     */
    public void testBuildFormFilterQueryWithNullValue( )
    {
        String strFormFilterExpected = StringUtils.EMPTY;

        String strFormFilterQueryPattern = "form.id_form = $id_form$";

        Map<String, Object> mapFilterNameValues = new LinkedHashMap<>( );
        mapFilterNameValues.put( "id_form", null );
        FormParameters formFilterItem = new FormParameters( );
        formFilterItem.setFormParametersMap( mapFilterNameValues );

        String strFormFilterQuery = FormFilterQueryBuilder.buildFormFilterQuery( strFormFilterQueryPattern, formFilterItem );
        assertThat( strFormFilterQuery, is( not( nullValue( ) ) ) );
        assertThat( strFormFilterQuery, is( strFormFilterExpected ) );
    }

    /**
     * Test of the method {@link FormFilterQueryBuilder#buildFormFilterQuery(String, FormParameters)}
     */
    public void testBuildFormFilterQueryWithWrongName( )
    {
        String strFormFilterExpected = "form.id_form = $id_form$";

        String strFormFilterQueryPattern = "form.id_form = $id_form$";

        Map<String, Object> mapFilterNameValues = new LinkedHashMap<>( );
        mapFilterNameValues.put( "test", 4 );
        FormParameters formFilterItem = new FormParameters( );
        formFilterItem.setFormParametersMap( mapFilterNameValues );

        String strFormFilterQuery = FormFilterQueryBuilder.buildFormFilterQuery( strFormFilterQueryPattern, formFilterItem );
        assertThat( strFormFilterQuery, is( not( nullValue( ) ) ) );
        assertThat( strFormFilterQuery, is( strFormFilterExpected ) );
    }

    /**
     * Test of the method {@link FormFilterQueryBuilder#buildFormFilterQuery(String, FormParameters)} with a query pattern with multiple names
     */
    public void testBuildFormFilterQueryWithMultipleName( )
    {
        String strFormFilterExpected = "form.id_form = ? AND workflow_state.id_workflow_state = ?";

        String strFormFilterQueryPattern = "form.id_form = $id_form$ " + "AND workflow_state.id_workflow_state = $id_workflow_state$";

        Map<String, Object> mapFilterNameValues = new LinkedHashMap<>( );
        mapFilterNameValues.put( "id_form", 4 );
        mapFilterNameValues.put( "id_workflow_state", 42 );
        FormParameters formFilterItem = new FormParameters( );
        formFilterItem.setFormParametersMap( mapFilterNameValues );

        String strFormFilterQuery = FormFilterQueryBuilder.buildFormFilterQuery( strFormFilterQueryPattern, formFilterItem );
        assertThat( strFormFilterQuery, is( not( nullValue( ) ) ) );
        assertThat( strFormFilterQuery, is( strFormFilterExpected ) );

        List<String> listUsedParameterValues = formFilterItem.getListUsedParametersValue( );
        assertThat( listUsedParameterValues.size( ), is( 2 ) );
        assertThat( listUsedParameterValues.get( NumberUtils.INTEGER_ZERO ), is( "4" ) );
        assertThat( listUsedParameterValues.get( NumberUtils.INTEGER_ONE ), is( "42" ) );
    }

    /**
     * Test of the method {@link FormFilterQueryBuilder#buildFormFilterQuery(String, FormParameters)} with a query pattern with multiple values to replace and a
     * null value
     */
    public void testBuildFormFilterQueryWithMultipleNameWithWrongValue( )
    {
        String strFormFilterExpected = StringUtils.EMPTY;

        String strFormFilterQueryPattern = "form.id_form = $id_form$ " + "AND workflow_state.id_workflow_state = $id_workflow_state$";

        Map<String, Object> mapFilterNameValues = new LinkedHashMap<>( );
        mapFilterNameValues.put( "id_form", 4 );
        mapFilterNameValues.put( "id_workflow_state", null );
        FormParameters formFilterItem = new FormParameters( );
        formFilterItem.setFormParametersMap( mapFilterNameValues );

        String strFormFilterQuery = FormFilterQueryBuilder.buildFormFilterQuery( strFormFilterQueryPattern, formFilterItem );
        assertThat( strFormFilterQuery, is( not( nullValue( ) ) ) );
        assertThat( strFormFilterQuery, is( strFormFilterExpected ) );
    }

    /**
     * Test of the method {@link FormFilterQueryBuilder#buildFormFilterQuery(String, FormParameters)} with a query pattern with nothing to replace but with
     * element which has the same name that those in the map parameter
     */
    public void testBuildFormFilterQueryWithoutElementToReplace( )
    {
        String strFormFilterExpected = "form.id_form = id_form";

        String strFormFilterQueryPattern = "form.id_form = id_form";

        Map<String, Object> mapFilterNameValues = new LinkedHashMap<>( );
        mapFilterNameValues.put( "id_form", 4 );
        FormParameters formFilterItem = new FormParameters( );
        formFilterItem.setFormParametersMap( mapFilterNameValues );

        String strFormFilterQuery = FormFilterQueryBuilder.buildFormFilterQuery( strFormFilterQueryPattern, formFilterItem );
        assertThat( strFormFilterQuery, is( not( nullValue( ) ) ) );
        assertThat( strFormFilterQuery, is( strFormFilterExpected ) );
    }
}
