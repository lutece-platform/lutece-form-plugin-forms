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
import static org.junit.Assert.assertThat;

import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.form.column.FormColumnCell;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test for the FormResponseItemComparator
 */
public class FormResponseItemComparatorTest extends LuteceTestCase
{
    // Constants
    private static final int DEFAULT_COLUMN_POSITION_TO_SORT = 1;
    private static final String DEFAULT_SORT_ATTRIBUTE_NAME = "name";
    private static final boolean ASCENDANT_SORT = Boolean.TRUE;
    private static final boolean DESCENDANT_SORT = Boolean.FALSE;
    private static final int EQUALITY_COMPARISON_RESULT = NumberUtils.INTEGER_ZERO;
    private static final int SUPERIOR_COMPARISON_RESULT = NumberUtils.INTEGER_ONE;
    private static final int INFERIOR_COMPARISON_RESULT = NumberUtils.INTEGER_MINUS_ONE;

    // Variables
    private static final FormResponseItemSortConfig _defaultFormResponseItemComparatorConfig = new FormResponseItemSortConfig( DEFAULT_COLUMN_POSITION_TO_SORT,
            DEFAULT_SORT_ATTRIBUTE_NAME, ASCENDANT_SORT );

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
     * Test for the method {@link FormResponseItemComparator#compare(FormResponseItem, FormResponseItem)} with two FormResponseItem which have no values
     */
    public void testCompareWithEmptyFormResponseItems( )
    {
        FormResponseItem formResponseItemOne = new FormResponseItem( );
        FormResponseItem formResponseItemTwo = new FormResponseItem( );

        FormResponseItemComparator comparator = new FormResponseItemComparator( _defaultFormResponseItemComparatorConfig,
                _defaultFormResponseItemComparatorConfig );
        int nComparisonResult = comparator.compare( formResponseItemOne, formResponseItemTwo );
        assertThat( nComparisonResult, is( EQUALITY_COMPARISON_RESULT ) );
    }

    /**
     * Test for the method {@link FormResponseItemComparator#compare(FormResponseItem, FormResponseItem)} with the first FormResponseItem which have no values
     * with the ascendant sort
     */
    public void testCompareAscendantSortWithFirstFormResponseItemEmpty( )
    {
        FormResponseItem formResponseItemOne = new FormResponseItem( );
        formResponseItemOne.addFormColumnCell( null );

        FormColumnCell formColumnCellTwo = new FormColumnCell( );
        FormResponseItem formResponseItemTwo = new FormResponseItem( );
        formResponseItemTwo.addFormColumnCell( formColumnCellTwo );

        FormResponseItemComparator comparator = new FormResponseItemComparator( _defaultFormResponseItemComparatorConfig,
                _defaultFormResponseItemComparatorConfig );
        int nComparisonResult = comparator.compare( formResponseItemOne, formResponseItemTwo );
        assertThat( nComparisonResult, is( INFERIOR_COMPARISON_RESULT ) );
    }

    /**
     * Test for the method {@link FormResponseItemComparator#compare(FormResponseItem, FormResponseItem)} with the first FormResponseItem which have no values
     * with the descendant sort
     */
    public void testCompareDescendantSortWithFirstFormResponseItemEmpty( )
    {
        FormResponseItem formResponseItemOne = new FormResponseItem( );
        formResponseItemOne.addFormColumnCell( null );

        FormColumnCell formColumnCellTwo = new FormColumnCell( );
        FormResponseItem formResponseItemTwo = new FormResponseItem( );
        formResponseItemTwo.addFormColumnCell( formColumnCellTwo );

        FormResponseItemSortConfig formResponseItemComparatorConfig = new FormResponseItemSortConfig( DEFAULT_COLUMN_POSITION_TO_SORT,
                DEFAULT_SORT_ATTRIBUTE_NAME, DESCENDANT_SORT );
        FormResponseItemComparator comparator = new FormResponseItemComparator( formResponseItemComparatorConfig, _defaultFormResponseItemComparatorConfig );

        int nComparisonResult = comparator.compare( formResponseItemOne, formResponseItemTwo );
        assertThat( nComparisonResult, is( SUPERIOR_COMPARISON_RESULT ) );
    }

    /**
     * Test for the method {@link FormResponseItemComparator#compare(FormResponseItem, FormResponseItem)} with the second FormResponseItem which have no values
     * with the ascendant sort
     */
    public void testCompareAscendantSortWithSecondFormResponseItemEmpty( )
    {
        FormColumnCell formColumnCellOne = new FormColumnCell( );
        FormResponseItem formResponseItemOne = new FormResponseItem( );
        formResponseItemOne.addFormColumnCell( formColumnCellOne );

        FormResponseItem formResponseItemTwo = new FormResponseItem( );
        formResponseItemTwo.addFormColumnCell( null );

        FormResponseItemComparator comparator = new FormResponseItemComparator( _defaultFormResponseItemComparatorConfig,
                _defaultFormResponseItemComparatorConfig );
        int nComparisonResult = comparator.compare( formResponseItemOne, formResponseItemTwo );
        assertThat( nComparisonResult, is( SUPERIOR_COMPARISON_RESULT ) );
    }

    /**
     * Test for the method {@link FormResponseItemComparator#compare(FormResponseItem, FormResponseItem)} with the second FormResponseItem which have no values
     * with the descendant sort
     */
    public void testCompareDescendantSortWithSecondFormResponseItemEmpty( )
    {
        FormColumnCell formColumnCellOne = new FormColumnCell( );
        FormResponseItem formResponseItemOne = new FormResponseItem( );
        formResponseItemOne.addFormColumnCell( formColumnCellOne );

        FormResponseItem formResponseItemTwo = new FormResponseItem( );
        formResponseItemTwo.addFormColumnCell( null );

        FormResponseItemSortConfig formResponseItemComparatorConfig = new FormResponseItemSortConfig( DEFAULT_COLUMN_POSITION_TO_SORT,
                DEFAULT_SORT_ATTRIBUTE_NAME, DESCENDANT_SORT );
        FormResponseItemComparator comparator = new FormResponseItemComparator( formResponseItemComparatorConfig, _defaultFormResponseItemComparatorConfig );

        int nComparisonResult = comparator.compare( formResponseItemOne, formResponseItemTwo );
        assertThat( nComparisonResult, is( INFERIOR_COMPARISON_RESULT ) );
    }
}
