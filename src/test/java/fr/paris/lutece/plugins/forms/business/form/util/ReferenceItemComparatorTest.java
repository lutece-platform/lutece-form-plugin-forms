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
package fr.paris.lutece.plugins.forms.business.form.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.util.ReferenceItemComparator;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.util.ReferenceItem;

/**
 * Test class for the ReferenceItemComparator class
 */
public class ReferenceItemComparatorTest extends LuteceTestCase
{
    /**
     * Test for the {@link ReferenceItemComparator#compare(ReferenceItem, ReferenceItem)} method with two empty
     * ReferenceItem
     */
	@Test
    public void testCompareEmptyReferenceItem( )
    {
        ReferenceItem referenceItemOne = new ReferenceItem( );

        ReferenceItem referenceItemTwo = new ReferenceItem( );

        ReferenceItemComparator referenceItemComparator = new ReferenceItemComparator( );
        int nCompareResult = referenceItemComparator.compare( referenceItemOne, referenceItemTwo );
        assertThat( nCompareResult, is( NumberUtils.INTEGER_ZERO ) );
    }

    /**
     * Test for the {@link ReferenceItemComparator#compare(ReferenceItem, ReferenceItem)} method with the first
     * ReferenceItem which is empty
     */
	@Test
    public void testCompareReferenceItemWithFirstEmpty( )
    {
        ReferenceItem referenceItemOne = new ReferenceItem( );

        ReferenceItem referenceItemTwo = new ReferenceItem( );
        referenceItemTwo.setName( "Test" );

        ReferenceItemComparator referenceItemComparator = new ReferenceItemComparator( );
        int nCompareResult = referenceItemComparator.compare( referenceItemOne, referenceItemTwo );
        assertThat( nCompareResult, is( NumberUtils.INTEGER_MINUS_ONE ) );
    }

    /**
     * Test for the {@link ReferenceItemComparator#compare(ReferenceItem, ReferenceItem)} method with the second
     * ReferenceItem which is empty
     */
	@Test
    public void testCompareReferenceItemWithSecondEmpty( )
    {
        ReferenceItem referenceItemOne = new ReferenceItem( );
        referenceItemOne.setName( "Test" );

        ReferenceItem referenceItemTwo = new ReferenceItem( );

        ReferenceItemComparator referenceItemComparator = new ReferenceItemComparator( );
        int nCompareResult = referenceItemComparator.compare( referenceItemOne, referenceItemTwo );
        assertThat( nCompareResult, is( NumberUtils.INTEGER_ONE ) );
    }

    /**
     * Test for the {@link ReferenceItemComparator#compare(ReferenceItem, ReferenceItem)} method with the two
     * ReferenceItems which have the same name
     */
	@Test
    public void testCompareReferenceItemSameNames( )
    {
        ReferenceItem referenceItemOne = new ReferenceItem( );
        referenceItemOne.setName( "Test" );

        ReferenceItem referenceItemTwo = new ReferenceItem( );
        referenceItemTwo.setName( "Test" );

        ReferenceItemComparator referenceItemComparator = new ReferenceItemComparator( );
        int nCompareResult = referenceItemComparator.compare( referenceItemOne, referenceItemTwo );
        assertThat( nCompareResult, is( NumberUtils.INTEGER_ZERO ) );
    }

    /**
     * Test for the {@link ReferenceItemComparator#compare(ReferenceItem, ReferenceItem)} method with the first
     * ReferenceItem which have a "greater" value than the second ReferenceItem
     */
	@Test
    public void testCompareReferenceItemFirstGreater( )
    {
        ReferenceItem referenceItemOne = new ReferenceItem( );
        referenceItemOne.setName( "Item" );

        ReferenceItem referenceItemTwo = new ReferenceItem( );
        referenceItemTwo.setName( "Test" );

        ReferenceItemComparator referenceItemComparator = new ReferenceItemComparator( );
        int nCompareResult = referenceItemComparator.compare( referenceItemOne, referenceItemTwo );
        assertTrue( nCompareResult < NumberUtils.INTEGER_ZERO );
    }

    /**
     * Test for the {@link ReferenceItemComparator#compare(ReferenceItem, ReferenceItem)} method with the second
     * ReferenceItem which have a "greater" value than the first ReferenceItem
     */
	@Test
    public void testCompareReferenceItemSecondGreater( )
    {
        ReferenceItem referenceItemOne = new ReferenceItem( );
        referenceItemOne.setName( "Test" );

        ReferenceItem referenceItemTwo = new ReferenceItem( );
        referenceItemTwo.setName( "Item" );

        ReferenceItemComparator referenceItemComparator = new ReferenceItemComparator( );
        int nCompareResult = referenceItemComparator.compare( referenceItemOne, referenceItemTwo );
        assertTrue( nCompareResult > NumberUtils.INTEGER_ZERO );
    }
}
