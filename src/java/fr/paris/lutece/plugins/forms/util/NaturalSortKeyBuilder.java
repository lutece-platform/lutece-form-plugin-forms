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

import java.io.ByteArrayOutputStream;
import java.text.Collator;
import java.util.Locale;


public final class NaturalSortKeyBuilder
{
    /**
     * Version of the binary key format. Bump on any change to {@link #build(String)} : previously indexed keys are then
     * no longer comparable with newly produced ones and a full reindexation is required.
     */
    public static final int FORMAT_VERSION = 1;

    /** Reserved delimiter. Escaped out of all content, so it always compares lower than any encoded byte. */
    private static final byte SEP = 0x00;
    /** Escape marker for the two reserved bytes. */
    private static final byte ESC = 0x01;
    /** Segment type markers : numeric segments sort before textual ones. */
    private static final byte TYPE_NUM = 0x02;
    private static final byte TYPE_TXT = 0x03;

    /**
     * Beyond this length a sort key carries no further discriminating power, and an unbounded key could exceed Lucene's
     * 32766-byte limit for a docvalues term. Longer values are truncated for sorting only ; the stored field keeps the
     * full value for display.
     */
    private static final int MAX_INPUT_CHARS = 256;

    /** Guards the two-byte length prefix against a pathological run of digits. */
    private static final int MAX_DIGITS = 0xFFFF;

    private final Collator collatorPrimary;
    private final Collator collatorTertiary;

    /**
     * Creates a builder using the collation order shared by {@code fr-FR} and {@code en} under CLDR.
     */
    public NaturalSortKeyBuilder( )
    {
        this( Locale.ENGLISH );
    }

    /**
     * Creates a builder for an explicit collation locale.
     *
     * @param locale
     *            the collation locale, {@code null} meaning {@link Locale#ENGLISH}
     */
    public NaturalSortKeyBuilder( Locale locale )
    {
        Locale effective = locale == null ? Locale.ENGLISH : locale;

        collatorPrimary = Collator.getInstance( effective );
        collatorPrimary.setStrength( Collator.PRIMARY );
        collatorPrimary.setDecomposition( Collator.CANONICAL_DECOMPOSITION );

        collatorTertiary = Collator.getInstance( effective );
        collatorTertiary.setStrength( Collator.TERTIARY );
        collatorTertiary.setDecomposition( Collator.CANONICAL_DECOMPOSITION );
    }

    /**
     * Builds the sort key for a value.
     *
     * @param strValue
     *            the raw response value, may be {@code null}
     * @return the binary sort key, never {@code null} ; empty for a {@code null} or empty input
     */
    public byte [ ] build( String strValue )
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream( 96 );

        if ( strValue == null || strValue.isEmpty( ) )
        {
            return out.toByteArray( );
        }

        String strInput = strValue.length( ) > MAX_INPUT_CHARS ? strValue.substring( 0, MAX_INPUT_CHARS ) : strValue;

        writeLevelA( out, strInput );

        // Level separator : three reserved bytes, strictly lower than any level A content,
        // so a value whose segments are a prefix of another's still sorts first.
        out.write( SEP );
        out.write( SEP );
        out.write( SEP );

        writeEscapedAll( out, collatorTertiary.getCollationKey( strInput ).toByteArray( ) );

        return out.toByteArray( );
    }

    /**
     * Writes level A : alternating numeric and textual segments, numbers compared by value and text by primary
     * collation (case and accents ignored).
     *
     * @param out
     *            the target stream
     * @param strInput
     *            the truncated input value
     */
    private void writeLevelA( ByteArrayOutputStream out, String strInput )
    {
        int nLength = strInput.length( );
        int nPos = 0;
        boolean bFirst = true;

        while ( nPos < nLength )
        {
            if ( !bFirst )
            {
                out.write( SEP );
                out.write( SEP );
            }
            bFirst = false;

            boolean bNumeric = Character.isDigit( strInput.codePointAt( nPos ) );
            int nStart = nPos;
            StringBuilder sbDigits = bNumeric ? new StringBuilder( ) : null;

            while ( nPos < nLength )
            {
                int nCodePoint = strInput.codePointAt( nPos );

                if ( Character.isDigit( nCodePoint ) != bNumeric )
                {
                    break;
                }
                if ( bNumeric )
                {
                    // normalises non-ASCII digits to their ASCII counterpart
                    sbDigits.append( (char) ( '0' + Character.digit( nCodePoint, 10 ) ) );
                }
                nPos += Character.charCount( nCodePoint );
            }

            if ( bNumeric )
            {
                writeNumericSegment( out, stripLeadingZeros( sbDigits.toString( ) ) );
            }
            else
            {
                out.write( TYPE_TXT );
                writeEscapedAll( out, collatorPrimary.getCollationKey( strInput.substring( nStart, nPos ) ).toByteArray( ) );
            }
        }
    }

    /**
     * Writes a numeric segment as a two-byte digit-count prefix followed by the digits. Putting the count first makes
     * "more digits means greater" hold under plain byte comparison, so values of any magnitude are supported without
     * parsing and without overflow.
     *
     * @param out
     *            the target stream
     * @param strDigits
     *            the digits, leading zeros already removed
     */
    private static void writeNumericSegment( ByteArrayOutputStream out, String strDigits )
    {
        out.write( TYPE_NUM );

        int nCount = Math.min( strDigits.length( ), MAX_DIGITS );
        writeEscaped( out, (byte) ( nCount >>> 8 ) );
        writeEscaped( out, (byte) nCount );

        for ( int i = 0; i < nCount; i++ )
        {
            writeEscaped( out, (byte) strDigits.charAt( i ) );
        }
    }

    /**
     * Writes a collation key, escaping the reserved bytes.
     *
     * @param out
     *            the target stream
     * @param bytes
     *            the raw bytes to escape
     */
    private static void writeEscapedAll( ByteArrayOutputStream out, byte [ ] bytes )
    {
        for ( byte b : bytes )
        {
            writeEscaped( out, b );
        }
    }

    /**
     * Escapes the two reserved bytes, preserving the relative order of the original bytes so that byte-wise comparison
     * of escaped sequences matches byte-wise comparison of the originals.
     *
     * @param out
     *            the target stream
     * @param b
     *            the byte to write
     */
    private static void writeEscaped( ByteArrayOutputStream out, byte b )
    {
        if ( b == SEP )
        {
            out.write( ESC );
            out.write( 0x01 );
        }
        else
            if ( b == ESC )
            {
                out.write( ESC );
                out.write( 0x02 );
            }
            else
            {
                out.write( b );
            }
    }

    /**
     * Removes leading zeros, keeping at least one digit so that {@code "000"} reduces to {@code "0"}.
     *
     * @param strDigits
     *            the digit run
     * @return the digit run without leading zeros
     */
    private static String stripLeadingZeros( String strDigits )
    {
        int i = 0;

        while ( i < strDigits.length( ) - 1 && strDigits.charAt( i ) == '0' )
        {
            i++;
        }

        return strDigits.substring( i );
    }
}
