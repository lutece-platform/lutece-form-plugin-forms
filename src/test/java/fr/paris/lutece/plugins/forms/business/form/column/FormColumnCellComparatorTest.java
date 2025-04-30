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
package fr.paris.lutece.plugins.forms.business.form.column;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.form.FormResponseItem;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test class for the FormColumnCellComparator
 */
public class FormColumnCellComparatorTest extends LuteceTestCase
{
    // Constants
    private static final String DEFAULT_SORT_ATTRIBUTE_NAME = "name";

    /**
     * Test for the method {@link FormColumnCellComparator#compare(FormColumnCell, FormColumnCell)} without any values on each FormColumnCell
     */
    @Test
    public void testCompareWithoutValues( )
    {
        FormColumnCell formColumnCellOne = new FormColumnCell( );

        FormColumnCell formColumnCellTwo = new FormColumnCell( );

        FormColumnCellComparator formColumnCellComparator = new FormColumnCellComparator( DEFAULT_SORT_ATTRIBUTE_NAME );

        int nComparisonResult = formColumnCellComparator.compare( formColumnCellOne, formColumnCellTwo );
        assertThat( nComparisonResult, is( NumberUtils.INTEGER_ZERO ) );
    }

    /**
     * Test for the method {@link FormColumnCellComparator#compare(FormColumnCell, FormColumnCell)} with the second FormColumnCell which have a greater value
     * than the first
     */
    @Test
    public void testCompareWithSecondCellValueGreater( )
    {
        Map<String, Object> mapFormColumnValuesOne = new LinkedHashMap<>( );
        mapFormColumnValuesOne.put( DEFAULT_SORT_ATTRIBUTE_NAME, "Martin" );
        FormColumnCell formColumnCellOne = new FormColumnCell( );
        formColumnCellOne.setFormColumnCellValues( mapFormColumnValuesOne );

        Map<String, Object> mapFormColumnValuesTwo = new LinkedHashMap<>( );
        mapFormColumnValuesTwo.put( DEFAULT_SORT_ATTRIBUTE_NAME, "Albert" );
        FormColumnCell formColumnCellTwo = new FormColumnCell( );
        formColumnCellTwo.setFormColumnCellValues( mapFormColumnValuesTwo );

        FormColumnCellComparator formColumnCellComparator = new FormColumnCellComparator( DEFAULT_SORT_ATTRIBUTE_NAME );

        int nComparisonResult = formColumnCellComparator.compare( formColumnCellOne, formColumnCellTwo );
        assertTrue( nComparisonResult > NumberUtils.INTEGER_ZERO );
    }

    /**
     * Test for the method {@link FormColumnCellComparator#compare(FormColumnCell, FormColumnCell)} with the first FormColumnCell which have a greater value
     * than the second
     */
    @Test
    public void testCompareWithFirstCellValueGreater( )
    {
        Map<String, Object> mapFormColumnValuesOne = new LinkedHashMap<>( );
        mapFormColumnValuesOne.put( DEFAULT_SORT_ATTRIBUTE_NAME, "Albert" );
        FormColumnCell formColumnCellOne = new FormColumnCell( );
        formColumnCellOne.setFormColumnCellValues( mapFormColumnValuesOne );

        Map<String, Object> mapFormColumnValuesTwo = new LinkedHashMap<>( );
        mapFormColumnValuesTwo.put( DEFAULT_SORT_ATTRIBUTE_NAME, "Martin" );
        FormColumnCell formColumnCellTwo = new FormColumnCell( );
        formColumnCellTwo.setFormColumnCellValues( mapFormColumnValuesTwo );

        FormColumnCellComparator formColumnCellComparator = new FormColumnCellComparator( DEFAULT_SORT_ATTRIBUTE_NAME );

        int nComparisonResult = formColumnCellComparator.compare( formColumnCellOne, formColumnCellTwo );
        assertTrue( nComparisonResult < NumberUtils.INTEGER_ZERO );
    }

    /**
     * Test for the method {@link FormColumnCellComparator#compare(FormColumnCell, FormColumnCell)} with integer values for the sorting key
     */
    @Test
    public void testCompareWithIntegerValues( )
    {
        Map<String, Object> mapFormColumnValuesOne = new LinkedHashMap<>( );
        mapFormColumnValuesOne.put( DEFAULT_SORT_ATTRIBUTE_NAME, 1 );
        FormColumnCell formColumnCellOne = new FormColumnCell( );
        formColumnCellOne.setFormColumnCellValues( mapFormColumnValuesOne );

        Map<String, Object> mapFormColumnValuesTwo = new LinkedHashMap<>( );
        mapFormColumnValuesTwo.put( DEFAULT_SORT_ATTRIBUTE_NAME, 10 );
        FormColumnCell formColumnCellTwo = new FormColumnCell( );
        formColumnCellTwo.setFormColumnCellValues( mapFormColumnValuesTwo );

        FormColumnCellComparator formColumnCellComparator = new FormColumnCellComparator( DEFAULT_SORT_ATTRIBUTE_NAME );

        int nComparisonResult = formColumnCellComparator.compare( formColumnCellOne, formColumnCellTwo );
        assertThat( nComparisonResult, is( NumberUtils.INTEGER_MINUS_ONE ) );
    }

    /**
     * Test for the method {@link FormResponseItemComparator#compare(FormResponseItem, FormResponseItem)} with the first FormColumnCell which have a null value
     */
    @Test
    public void testCompareWithFirstCellNullValue( )
    {
        Map<String, Object> mapFormColumnValuesOne = new LinkedHashMap<>( );
        mapFormColumnValuesOne.put( DEFAULT_SORT_ATTRIBUTE_NAME, null );
        FormColumnCell formColumnCellOne = new FormColumnCell( );
        formColumnCellOne.setFormColumnCellValues( mapFormColumnValuesOne );

        Map<String, Object> mapFormColumnValuesTwo = new LinkedHashMap<>( );
        mapFormColumnValuesTwo.put( DEFAULT_SORT_ATTRIBUTE_NAME, "Martin" );
        FormColumnCell formColumnCellTwo = new FormColumnCell( );
        formColumnCellTwo.setFormColumnCellValues( mapFormColumnValuesTwo );

        FormColumnCellComparator formColumnCellComparator = new FormColumnCellComparator( DEFAULT_SORT_ATTRIBUTE_NAME );

        int nComparisonResult = formColumnCellComparator.compare( formColumnCellOne, formColumnCellTwo );
        assertThat( nComparisonResult, is( NumberUtils.INTEGER_MINUS_ONE ) );
    }

    /**
     * Test for the method {@link FormResponseItemComparator#compare(FormResponseItem, FormResponseItem)} with the second FormColumnCell which have a null value
     */
    @Test
    public void testCompareWithSecondCellNullValue( )
    {
        Map<String, Object> mapFormColumnValuesOne = new LinkedHashMap<>( );
        mapFormColumnValuesOne.put( DEFAULT_SORT_ATTRIBUTE_NAME, "Martin" );
        FormColumnCell formColumnCellOne = new FormColumnCell( );
        formColumnCellOne.setFormColumnCellValues( mapFormColumnValuesOne );

        Map<String, Object> mapFormColumnValuesTwo = new LinkedHashMap<>( );
        mapFormColumnValuesTwo.put( DEFAULT_SORT_ATTRIBUTE_NAME, null );
        FormColumnCell formColumnCellTwo = new FormColumnCell( );
        formColumnCellTwo.setFormColumnCellValues( mapFormColumnValuesTwo );

        FormColumnCellComparator formColumnCellComparator = new FormColumnCellComparator( DEFAULT_SORT_ATTRIBUTE_NAME );

        int nComparisonResult = formColumnCellComparator.compare( formColumnCellOne, formColumnCellTwo );
        assertThat( nComparisonResult, is( NumberUtils.INTEGER_ONE ) );
    }

    /**
     * Test for the method {@link FormResponseItemComparator#compare(FormResponseItem, FormResponseItem)} with one FormColumnCell which haven't the good key
     */
    @Test
    public void testCompareWithOneMissingKey( )
    {
        Map<String, Object> mapFormColumnValuesOne = new LinkedHashMap<>( );
        mapFormColumnValuesOne.put( "test", "Albert" );
        FormColumnCell formColumnCellOne = new FormColumnCell( );
        formColumnCellOne.setFormColumnCellValues( mapFormColumnValuesOne );

        Map<String, Object> mapFormColumnValuesTwo = new LinkedHashMap<>( );
        mapFormColumnValuesTwo.put( DEFAULT_SORT_ATTRIBUTE_NAME, "Martin" );
        FormColumnCell formColumnCellTwo = new FormColumnCell( );
        formColumnCellTwo.setFormColumnCellValues( mapFormColumnValuesTwo );

        FormColumnCellComparator formColumnCellComparator = new FormColumnCellComparator( DEFAULT_SORT_ATTRIBUTE_NAME );

        int nComparisonResult = formColumnCellComparator.compare( formColumnCellOne, formColumnCellTwo );
        assertThat( nComparisonResult, is( NumberUtils.INTEGER_MINUS_ONE ) );
    }

    /**
     * Test for the method {@link FormResponseItemComparator#compare(FormResponseItem, FormResponseItem)} with the two FormColumnCell which haven't the good key
     */
    @Test
    public void testCompareWithoutSortKey( )
    {
        Map<String, Object> mapFormColumnValuesOne = new LinkedHashMap<>( );
        mapFormColumnValuesOne.put( "test", "Albert" );
        FormColumnCell formColumnCellOne = new FormColumnCell( );
        formColumnCellOne.setFormColumnCellValues( mapFormColumnValuesOne );

        Map<String, Object> mapFormColumnValuesTwo = new LinkedHashMap<>( );
        mapFormColumnValuesTwo.put( "test", "Martin" );
        FormColumnCell formColumnCellTwo = new FormColumnCell( );
        formColumnCellTwo.setFormColumnCellValues( mapFormColumnValuesTwo );

        FormColumnCellComparator formColumnCellComparator = new FormColumnCellComparator( DEFAULT_SORT_ATTRIBUTE_NAME );

        int nComparisonResult = formColumnCellComparator.compare( formColumnCellOne, formColumnCellTwo );
        assertThat( nComparisonResult, is( NumberUtils.INTEGER_ZERO ) );
    }
}
