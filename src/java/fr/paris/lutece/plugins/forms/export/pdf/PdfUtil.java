package fr.paris.lutece.plugins.forms.export.pdf;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.text.WordUtils;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
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
        final PDType1Font font = PDType1Font.HELVETICA_BOLD;
        final int fontSize = 12;
        final double rowHeight = 20D;
        final double tableWidth = page.findMediaBox( ).getWidth( ) - ( 2 * margin );
        final double cellMargin = 5D;

        // now add the text
        contentStream.setFont( font, fontSize );
        
        double textx = margin + cellMargin;
        double nexty = y;
        double texty = y - 15;
        
        Set<String> writtenGroup = new HashSet<>( );
        for ( PdfCell cell : listContent )
        {
            String group = cell.getGroup( );
            int nbLines = 1;
            
            int currentNbCols = 2;
            double currentColWidth = tableWidth / 2 ;
            boolean fullTopBorder = true;
            if ( group != null )
            {
                currentNbCols = 3;
                currentColWidth = tableWidth / currentNbCols;
                if ( writtenGroup.add( group ) )
                {
                    contentStream.beginText( );
                    contentStream.moveTextPositionByAmount( (float) textx, (float) texty );
                    contentStream.drawString( group );
                    contentStream.endText( );
                }
                else
                {
                    fullTopBorder = false;
                }
                textx += currentColWidth;
            }
            
            // Top row Border
            if ( fullTopBorder )
            {
                contentStream.drawLine( (float) margin, (float) nexty, (float) ( margin + tableWidth ), (float) nexty );
            }
            else
            {
                contentStream.drawLine( (float) ( margin + currentColWidth ), (float) nexty, (float) ( margin + tableWidth ), (float) nexty );
            }
            
            String text = cell.getTitle( );
            String[] wrapped = getWrappedText( font, fontSize, currentColWidth, text );
            for ( int k = 0; k < wrapped.length; k++ )
            {
                contentStream.beginText( );
                contentStream.moveTextPositionByAmount( (float) textx, (float) ( texty - ( k * rowHeight ) ) );
                contentStream.drawString( wrapped[k] );
                contentStream.endText( );
            }
            nbLines = Math.max( nbLines, wrapped.length );
            textx += currentColWidth;
            
            text = cell.getValue( );
            wrapped = getWrappedText( font, fontSize, currentColWidth, text );
            for ( int k = 0; k < wrapped.length; k++ )
            {
                contentStream.beginText( );
                contentStream.moveTextPositionByAmount( (float) textx, (float) ( texty - ( k * rowHeight ) ) );
                contentStream.drawString( wrapped[k] );
                contentStream.endText( );
            }
            nbLines = Math.max( nbLines, wrapped.length );
            
            // Left borders
            for ( int j = 0; j < currentNbCols; j++ )
            {
                contentStream.drawLine( (float) ( margin + j * currentColWidth ), (float) nexty, (float) ( margin + j * currentColWidth ), (float) ( nexty - nbLines * rowHeight ) );
            }
            
            //Right border
            contentStream.drawLine( (float) ( margin + tableWidth ), (float) nexty, (float) ( margin + tableWidth ), (float) ( nexty - nbLines * rowHeight ) );
           
            texty -= nbLines * rowHeight;
            nexty -= nbLines * rowHeight;
            textx = margin + cellMargin;
        }
        // Bottom table border
        contentStream.drawLine( (float) margin, (float) nexty, (float) ( margin + tableWidth ), (float) nexty );
    }
    
    private static String[] getWrappedText( PDFont font, int fontSize, double colWidth, String text ) throws IOException
    {
        double textWidth = (double) font.getStringWidth(text) / 1000 * fontSize;
        
        String[] wrapped = null;
        if ( textWidth < colWidth )
        {
            wrapped = new String[1];
            wrapped[0] = text;
        }
        else
        {
            int parts = 1 + (int) ( textWidth / colWidth );
            wrapped = WordUtils.wrap( text, text.length( ) / parts  ).split("\\r?\\n");
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
        stream.moveTextPositionByAmount( (float) titlex, (float) titley );
        stream.drawString( text );
        stream.endText( );
    }
}
