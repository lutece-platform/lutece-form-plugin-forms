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
package fr.paris.lutece.plugins.forms.export.pdf;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.text.WordUtils;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public final class PdfUtil
{
    private PdfUtil( )
    {
    }

    /**
     * @param page
     * @param contentStream
     * @param y
     *            the y-coordinate of the first row
     * @param margin
     *            the padding on left and right of table
     * @param content
     *            a 2d array containing the table data
     * @throws IOException
     */
    public static void drawTable( PDPage page, PDPageContentStream contentStream, double y, double margin, List<PdfCell> listContent ) throws IOException
    {
        final PDType1Font font = PDType1Font.HELVETICA;
        final int fontSize = 10;
        final double rowHeight = 20D;
        final double tableWidth = page.getMediaBox( ).getWidth( ) - ( 2 * margin );
        final double cellMargin = 5D;

        // now add the text
        contentStream.setFont( font, fontSize );

        double textx = margin + cellMargin;
        double texty = y - 15;

        Set<String> writtenGroup = new HashSet<>( );
        for ( PdfCell cell : listContent )
        {
            String group = cell.getGroup( );
            int nbLines = 1;

            double coldWidthTitle = tableWidth / 5;
            double currentColWidth = tableWidth - coldWidthTitle;

            if ( group != null )
            {
                String [ ] wrappedGroup = getWrappedText( font, fontSize, coldWidthTitle, group );
                currentColWidth = tableWidth - 2 * coldWidthTitle;
                if ( writtenGroup.add( group ) )
                {
                    for ( int k = 0; k < wrappedGroup.length; k++ )
                    {
                        contentStream.beginText( );
                        contentStream.newLineAtOffset( (float) textx, (float) ( texty - ( k * rowHeight ) ) );
                        contentStream.showText( wrappedGroup [k] );
                        contentStream.endText( );
                    }
                }
                textx += coldWidthTitle;
            }

            String text = cell.getTitle( );
            String [ ] wrapped = getWrappedText( font, fontSize, coldWidthTitle, text );
            for ( int k = 0; k < wrapped.length; k++ )
            {
                contentStream.beginText( );
                contentStream.newLineAtOffset( (float) textx, (float) ( texty - ( k * rowHeight ) ) );
                contentStream.showText( wrapped [k] );
                contentStream.endText( );
            }
            nbLines = Math.max( nbLines, wrapped.length );
            textx += coldWidthTitle;

            text = cell.getValue( );
            wrapped = getWrappedText( font, fontSize, currentColWidth, text );
            for ( int k = 0; k < wrapped.length; k++ )
            {
                contentStream.beginText( );
                contentStream.newLineAtOffset( (float) textx, (float) ( texty - ( k * rowHeight ) ) );
                contentStream.showText( wrapped [k] );
                contentStream.endText( );
            }
            nbLines = Math.max( nbLines, wrapped.length );
            texty -= nbLines * rowHeight;
            textx = margin + cellMargin;
        }
    }

    private static String [ ] getWrappedText( PDFont font, int fontSize, double colWidth, String text ) throws IOException
    {
        double textWidth = (double) font.getStringWidth( text ) / 1000 * fontSize;

        String [ ] wrapped = null;
        if ( textWidth < colWidth )
        {
            wrapped = new String [ 1];
            wrapped [0] = text;
        }
        else
        {
            int parts = 1 + (int) ( textWidth / colWidth );
            wrapped = WordUtils.wrap( text, text.length( ) / parts ).split( "\\r?\\n" );
        }

        return wrapped;
    }

    public static void addCenteredText( PDPage page, PDPageContentStream stream, PDFont font, int fontSize, int marginTop, String text ) throws IOException
    {
        double titleWidth = (double) font.getStringWidth( text ) / 1000 * fontSize;
        double titleHeight = (double) font.getFontDescriptor( ).getFontBoundingBox( ).getHeight( ) / 1000 * fontSize;

        double titlex = ( page.getMediaBox( ).getWidth( ) - titleWidth ) / 2;
        double titley = (double) page.getMediaBox( ).getHeight( ) - marginTop - titleHeight;
        stream.beginText( );
        stream.setFont( font, fontSize );
        stream.newLineAtOffset( (float) titlex, (float) titley );
        stream.showText( text );
        stream.endText( );
    }
}
