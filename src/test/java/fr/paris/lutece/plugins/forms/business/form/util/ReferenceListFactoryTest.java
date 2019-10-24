/*
 * Copyright (c) 2002-2019, Mairie de Paris
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
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.util.ReferenceListFactory;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;

/**
 * Test class for the ReferenceListFactory
 */
public class ReferenceListFactoryTest extends LuteceTestCase
{
    // Constants
    private static final String ATTRIBUTE_CODE = "code";
    private static final String ATTRIBUTE_STRING_CODE = "stringCode";
    private static final String ATTRIBUTE_NAME = "name";
    private static final String DEFAULT_ATTRIBUTE_NAME = FormsConstants.REFERENCE_ITEM_DEFAULT_NAME;
    private static final String DEFAULT_ATTRIBUTE_CODE = FormsConstants.REFERENCE_ITEM_DEFAULT_CODE;

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
     * Test for the method {@link fr.paris.lutece.plugins.forms.util.ReferenceListFactory#createReferenceList()}
     */
    public void testCreateReferenceList( )
    {
        List<String> listName = Arrays.asList( DEFAULT_ATTRIBUTE_NAME, "code_3", "code_1", "code_2" );
        List<String> listCode = Arrays.asList( DEFAULT_ATTRIBUTE_CODE, "3", "1", "2" );
        ReferenceList referenceListExpected = createExpectedReferencelist( listName, listCode );

        Collection<Object> collection = new ArrayList<>( );
        collection.add( new ObjectItemMock( "code_3", 3 ) );
        collection.add( new ObjectItemMock( "code_1", 1 ) );
        collection.add( new ObjectItemMock( "code_2", 2 ) );

        ReferenceListFactory referenceListFactory = new ReferenceListFactory( collection, ATTRIBUTE_CODE, ATTRIBUTE_NAME );
        ReferenceList referenceListResult = referenceListFactory.createReferenceList( );

        assertThat( referenceListResult, is( not( nullValue( ) ) ) );
        manageReferenceListComparison( referenceListExpected, referenceListResult );
    }

    /**
     * Test for the method {@link fr.paris.lutece.plugins.forms.util.ReferenceListFactory#createReferenceList()} without numeric code
     */
    public void testCreateReferenceListWithoutNumericCode( )
    {
        List<String> listName = Arrays.asList( DEFAULT_ATTRIBUTE_NAME, "code_3", "code_1", "code_2" );
        List<String> listCode = Arrays.asList( DEFAULT_ATTRIBUTE_CODE, "3", "1", "2" );
        ReferenceList referenceListExpected = createExpectedReferencelist( listName, listCode );

        Collection<Object> collection = new ArrayList<>( );
        collection.add( new ObjectItemMock( "code_3", 3 ) );
        collection.add( new ObjectItemMock( "code_1", 1 ) );
        collection.add( new ObjectItemMock( "code_2", 2 ) );

        ReferenceListFactory referenceListFactory = new ReferenceListFactory( collection, ATTRIBUTE_STRING_CODE, ATTRIBUTE_NAME, Boolean.FALSE );
        ReferenceList referenceListResult = referenceListFactory.createReferenceList( );

        assertThat( referenceListResult, is( not( nullValue( ) ) ) );
        manageReferenceListComparison( referenceListExpected, referenceListResult );
    }

    /**
     * Test for the method {@link fr.paris.lutece.plugins.forms.util.ReferenceListFactory#createReferenceList()} with sort
     */
    public void testCreateReferenceListWithSort( )
    {
        List<String> listName = Arrays.asList( DEFAULT_ATTRIBUTE_NAME, "code_1", "code_2", "code_3", "code_4" );
        List<String> listCode = Arrays.asList( DEFAULT_ATTRIBUTE_CODE, "1", "2", "3", "4" );
        ReferenceList referenceListExpected = createExpectedReferencelist( listName, listCode );

        Collection<Object> collection = new ArrayList<>( );
        collection.add( new ObjectItemMock( "code_2", 2 ) );
        collection.add( new ObjectItemMock( "code_3", 3 ) );
        collection.add( new ObjectItemMock( "code_1", 1 ) );
        collection.add( new ObjectItemMock( "code_4", 4 ) );

        ReferenceListFactory referenceListFactory = new ReferenceListFactory( collection, ATTRIBUTE_CODE, ATTRIBUTE_NAME );
        referenceListFactory.setDefaultSortNeeded( Boolean.TRUE );
        ReferenceList referenceListResult = referenceListFactory.createReferenceList( );

        assertThat( referenceListResult, is( not( nullValue( ) ) ) );
        manageReferenceListComparison( referenceListExpected, referenceListResult );
    }

    /**
     * Test for the method {@link fr.paris.lutece.plugins.forms.util.ReferenceListFactory#createReferenceList()} with a changed default name
     */
    public void testCreateReferenceListWithChangedDefaultName( )
    {
        String strDefaultLabel = "default label";
        List<String> listName = Arrays.asList( strDefaultLabel, "code_3", "code_1", "code_2" );
        List<String> listCode = Arrays.asList( DEFAULT_ATTRIBUTE_CODE, "3", "1", "2" );
        ReferenceList referenceListExpected = createExpectedReferencelist( listName, listCode );

        Collection<Object> collection = new ArrayList<>( );
        collection.add( new ObjectItemMock( "code_3", 3 ) );
        collection.add( new ObjectItemMock( "code_1", 1 ) );
        collection.add( new ObjectItemMock( "code_2", 2 ) );

        ReferenceListFactory referenceListFactory = new ReferenceListFactory( collection, ATTRIBUTE_CODE, ATTRIBUTE_NAME );
        referenceListFactory.setDefaultName( strDefaultLabel );
        ReferenceList referenceListResult = referenceListFactory.createReferenceList( );

        assertThat( referenceListResult, is( not( nullValue( ) ) ) );
        manageReferenceListComparison( referenceListExpected, referenceListResult );
    }

    /**
     * Test for the method {@link fr.paris.lutece.plugins.forms.util.ReferenceListFactory#createReferenceList()} with duplicates
     */
    public void testCreateReferenceListWithDuplicates( )
    {
        List<String> listName = Arrays.asList( DEFAULT_ATTRIBUTE_NAME, "code_3", "code_1", "code_2" );
        List<String> listCode = Arrays.asList( DEFAULT_ATTRIBUTE_CODE, "3", "1", "2" );
        ReferenceList referenceListExpected = createExpectedReferencelist( listName, listCode );

        Collection<Object> collection = new ArrayList<>( );
        collection.add( new ObjectItemMock( "code_3", 3 ) );
        collection.add( new ObjectItemMock( "code_1", 1 ) );
        collection.add( new ObjectItemMock( "code_3", 3 ) );
        collection.add( new ObjectItemMock( "code_2", 2 ) );
        collection.add( new ObjectItemMock( "code_1", 1 ) );

        ReferenceListFactory referenceListFactory = new ReferenceListFactory( collection, ATTRIBUTE_CODE, ATTRIBUTE_NAME );
        ReferenceList referenceListResult = referenceListFactory.createReferenceList( );

        assertThat( referenceListResult, is( not( nullValue( ) ) ) );
        manageReferenceListComparison( referenceListExpected, referenceListResult );
    }

    /**
     * Test for the method {@link fr.paris.lutece.plugins.forms.util.ReferenceListFactory#createReferenceList()} and verify that the missing name are replaced
     */
    public void testCreateReferenceLisCheckedNameReplacement( )
    {
        List<String> listName = Arrays.asList( DEFAULT_ATTRIBUTE_NAME, "", "code_1", "" );
        List<String> listCode = Arrays.asList( DEFAULT_ATTRIBUTE_CODE, "3", "1", "2" );
        ReferenceList referenceListExpected = createExpectedReferencelist( listName, listCode );

        Collection<Object> collection = new ArrayList<>( );
        collection.add( new ObjectItemMock( null, 3 ) );
        collection.add( new ObjectItemMock( "code_1", 1 ) );
        collection.add( new ObjectItemMock( null, 2 ) );

        ReferenceListFactory referenceListFactory = new ReferenceListFactory( collection, ATTRIBUTE_CODE, ATTRIBUTE_NAME );
        ReferenceList referenceListResult = referenceListFactory.createReferenceList( );

        assertThat( referenceListResult, is( not( nullValue( ) ) ) );
        manageReferenceListComparison( referenceListExpected, referenceListResult );
    }

    /**
     * Create the expected ReferenceList
     * 
     * @param listName
     *            The list of all names of the ReferenceItem
     * @param listCode
     *            The list of all codes of the ReferenceItem
     * @return the expected ReferenceList
     */
    private ReferenceList createExpectedReferencelist( List<String> listName, List<String> listCode )
    {
        ReferenceList referenceList = new ReferenceList( );

        for ( int index = 0; index < listName.size( ); index++ )
        {
            ReferenceItem referenceItem = new ReferenceItem( );
            referenceItem.setName( listName.get( index ) );
            referenceItem.setCode( listCode.get( index ) );

            referenceList.add( referenceItem );
        }

        return referenceList;
    }

    /**
     * Compare the two given ReferenceList one which represent the expected ReferenceList and the other which represent the result ReferenceList
     * 
     * @param referenceListExpected
     *            The ReferenceList which is expected
     * @param referenceListResult
     *            The ReferenceList which must be compared with the expected ReferenceList
     */
    private void manageReferenceListComparison( ReferenceList referenceListExpected, ReferenceList referenceListResult )
    {
        if ( referenceListExpected != null && referenceListResult != null )
        {
            assertThat( referenceListResult.size( ), is( referenceListExpected.size( ) ) );

            for ( int index = 0; index < referenceListExpected.size( ); index++ )
            {
                ReferenceItem referenceItemExpected = referenceListExpected.get( index );
                ReferenceItem referenceItemResult = referenceListResult.get( index );

                assertThat( referenceItemResult.getCode( ), is( referenceItemExpected.getCode( ) ) );
                assertThat( referenceItemResult.getName( ), is( referenceItemExpected.getName( ) ) );
            }
        }
    }
}
