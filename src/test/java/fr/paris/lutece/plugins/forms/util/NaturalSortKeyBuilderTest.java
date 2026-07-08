/*
 * Copyright (c) 2002-2026, City of Paris
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
package fr.paris.lutece.plugins.forms.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link NaturalSortKeyBuilder}.
 *
 * <p>
 * The assertions describe the <em>ordering</em>, not the encoding : the binary format may change as long as the
 * resulting order does not. {@link Collator} is used as an independent oracle here, in the test only — it is never
 * invoked at query time in production.
 * </p>
 */
public class NaturalSortKeyBuilderTest
{
    /** Unsigned byte-wise comparison, matching how Lucene orders docvalues terms. */
    private static final Comparator<byte [ ]> BYTES = ( a, b ) -> {
        int n = Math.min( a.length, b.length );
        for ( int i = 0; i < n; i++ )
        {
            int x = a [ i ] & 0xFF;
            int y = b [ i ] & 0xFF;
            if ( x != y )
            {
                return x < y ? -1 : 1;
            }
        }
        return Integer.compare( a.length, b.length );
    };

    private NaturalSortKeyBuilder _builder;

    @Before
    public void setUp( )
    {
        _builder = new NaturalSortKeyBuilder( );
    }

    private int compare( String s1, String s2 )
    {
        return Integer.signum( BYTES.compare( _builder.build( s1 ), _builder.build( s2 ) ) );
    }

    private void assertOrder( String... values )
    {
        for ( int i = 0; i < values.length - 1; i++ )
        {
            assertTrue( "[" + values [ i ] + "] should sort before [" + values [ i + 1 ] + "]",
                    compare( values [ i ], values [ i + 1 ] ) < 0 );
        }
    }

    // ------------------------------------------------------------------ numbers

    @Test
    public void testNumbersOrderedByValueNotLexicographically( )
    {
        assertOrder( "item1", "item2", "item9", "item10", "item100" );
    }

    @Test
    public void testNumberOfAnyMagnitude( )
    {
        // beyond Long.MAX_VALUE : no parsing occurs, so no overflow
        assertOrder( "99999999999999999999999", "100000000000000000000000" );
    }

    @Test
    public void testLeadingZerosDoNotChangeValueButStillBreakTies( )
    {
        assertEquals( 0, Integer.signum( 0 ) );
        // same numeric value, so level A ties and level B decides deterministically
        assertTrue( compare( "A01", "A1" ) != 0 );
        assertEquals( -compare( "A1", "A01" ), compare( "A01", "A1" ) );
    }

    @Test
    public void testMultipleNumericSegments( )
    {
        assertOrder( "v1.2.9", "v1.2.10", "v1.10.0", "v2.0.0" );
    }

    // ------------------------------------------------------------------ collation

    @Test
    public void testCaseIsNotDominant( )
    {
        // the bug in the original implementation : uppercase sorted before all lowercase
        assertOrder( "abricot", "Zebre" );
    }

    @Test
    public void testAccentsAreNotDominant( )
    {
        // 'É' must not land after 'Z'
        assertOrder( "École", "Zoo" );
    }

    @Test
    public void testCaseBreaksTiesOnly( )
    {
        // the regression the naive segment-major key introduced :
        // a case difference in the first segment must NOT outrank the numeric value
        assertOrder( "item2", "Item2", "ITEM2", "item9", "item10" );
    }

    @Test
    public void testAccentsBreakTiesBeforeCase( )
    {
        assertOrder( "elodie", "Elodie", "Élodie" );
    }

    @Test
    public void testAccentOrderIsForward( )
    {
        // CLDR order for fr-FR and en : accents compared left to right.
        // NB: Locale.FRENCH on the JDK still applies the legacy backward rule,
        // which is why the builder defaults to Locale.ENGLISH.
        assertOrder( "cote", "coté", "côte", "côté" );
    }

    @Test
    public void testLigaturesExpand( )
    {
        assertOrder( "coeur", "cœur", "cote" );
    }

    // ------------------------------------------------------------------ edge cases

    @Test
    public void testNullAndEmptyProduceEmptyKeyAndSortFirst( )
    {
        assertArrayEquals( new byte [ 0 ], _builder.build( null ) );
        assertArrayEquals( new byte [ 0 ], _builder.build( "" ) );
        assertTrue( compare( "", "a" ) < 0 );
    }

    @Test
    public void testAllZeroDigitRuns( )
    {
        assertEquals( 0, compare( "0", "0" ) );
        assertTrue( compare( "0", "1" ) < 0 );
    }

    @Test
    public void testVeryLongValueStaysWithinLuceneTermLimit( )
    {
        String strLong = repeat( "é1A", 5000 );
        assertTrue( "sort key must stay under the 32766-byte docvalues term limit",
                _builder.build( strLong ).length < 32766 );
    }

    // ------------------------------------------------------------------ invariants

    @Test
    public void testTotalOrderOnRandomInput( )
    {
        Random random = new Random( 42 );
        String strAlphabet = "abcçeéèêëiîoôuùûyAZÉÈÀÔœæßnñ 0123456789-'.";
        List<String> listValues = new ArrayList<>( );

        for ( int i = 0; i < 2000; i++ )
        {
            StringBuilder sb = new StringBuilder( );
            int nLen = random.nextInt( 14 );
            for ( int j = 0; j < nLen; j++ )
            {
                sb.append( strAlphabet.charAt( random.nextInt( strAlphabet.length( ) ) ) );
            }
            listValues.add( sb.toString( ) );
        }

        for ( int i = 0; i < 2000; i++ )
        {
            String x = listValues.get( random.nextInt( listValues.size( ) ) );
            String y = listValues.get( random.nextInt( listValues.size( ) ) );
            String z = listValues.get( random.nextInt( listValues.size( ) ) );

            assertEquals( "antisymmetry on [" + x + "] [" + y + "]", -compare( x, y ), compare( y, x ) );

            if ( compare( x, y ) < 0 && compare( y, z ) < 0 )
            {
                assertTrue( "transitivity on [" + x + "] [" + y + "] [" + z + "]", compare( x, z ) < 0 );
            }
        }
    }

    @Test
    public void testMatchesCollatorOnPureText( )
    {
        // Independent oracle. Restricted to letter-only values : digits and punctuation
        // are where this builder deliberately departs from plain collation.
        String [ ] samples = { "cote", "coté", "côte", "côté", "Zebre", "abricot", "École", "Zoo",
                               "Elodie", "elodie", "Élodie", "coeur", "cœur", "ca", "ça", "zebre" };

        Collator oracle = Collator.getInstance( Locale.ENGLISH );
        oracle.setStrength( Collator.TERTIARY );
        oracle.setDecomposition( Collator.CANONICAL_DECOMPOSITION );

        for ( String x : samples )
        {
            for ( String y : samples )
            {
                assertEquals( "[" + x + "] vs [" + y + "]",
                        Integer.signum( oracle.compare( x, y ) ), compare( x, y ) );
            }
        }
    }

    @Test
    public void testSortedListMatchesExpectedOrder( )
    {
        List<String> listValues = new ArrayList<>( Arrays.asList(
                "item10", "Zoo", "item2", "abricot", "École", "Item2", "item9", "salle 10", "salle 2" ) );

        listValues.sort( ( a, b ) -> BYTES.compare( _builder.build( a ), _builder.build( b ) ) );

        assertEquals( Arrays.asList(
                "abricot", "École", "item2", "Item2", "item9", "item10", "salle 2", "salle 10", "Zoo" ),
                listValues );
    }

    private static String repeat( String s, int n )
    {
        StringBuilder sb = new StringBuilder( s.length( ) * n );
        for ( int i = 0; i < n; i++ )
        {
            sb.append( s );
        }
        return sb.toString( );
    }
}
