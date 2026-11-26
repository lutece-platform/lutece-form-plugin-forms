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
package fr.paris.lutece.plugins.forms.service.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.Group;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.FileServiceException;
import fr.paris.lutece.portal.service.file.IFileStoreServiceProvider;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.enterprise.inject.spi.CDI;

/**
 * The bookmark of an iterable group: every iteration the user filled, whatever their number (LUT-34701)
 */
public class GroupIterationsMarkerTest extends LuteceTestCase
{
    private static final int GROUP_DISPLAY_ID = 100;
    private static final int NAME = 500;
    private static final int FIRST_NAME = 501;
    private static final int OUTSIDE = 502;

    @Test
    public void testQuestionIdsOfTheGroupInDisplayOrder( )
    {
        List<FormDisplay> listDisplay = new ArrayList<>( );
        listDisplay.add( display( FormsConstants.MARK_QUESTION, FIRST_NAME, GROUP_DISPLAY_ID, 2 ) );
        listDisplay.add( display( FormsConstants.MARK_QUESTION, OUTSIDE, 0, 1 ) );
        listDisplay.add( display( FormsConstants.MARK_QUESTION, NAME, GROUP_DISPLAY_ID, 1 ) );

        assertEquals( List.of( NAME, FIRST_NAME ),
                GroupIterationsMarker.getQuestionIds( GroupIterationsMarker.getChildDisplays( listDisplay, GROUP_DISPLAY_ID ) ) );
    }

    @Test
    public void testIterationsInOrderWithTheirQuestionsInDisplayOrder( )
    {
        // the responses come in no particular order
        List<FormQuestionResponse> listResponse = List.of( response( FIRST_NAME, "Prénom", 2, "Petit" ), response( NAME, "Nom", 2, "Léo" ),
                response( FIRST_NAME, "Prénom", 0, "Martin" ), response( NAME, "Nom", 0, "Paul" ), response( NAME, "Nom", 1, "Julie" ),
                response( OUTSIDE, "Ville", 0, "Paris" ) );

        List<GroupIteration> listIteration = GroupIterationsMarker.getIterations( List.of( NAME, FIRST_NAME ), listResponse, Locale.FRENCH );

        assertEquals( 3, listIteration.size( ) );
        assertEquals( List.of( "Nom : Paul", "Prénom : Martin" ), lines( listIteration.get( 0 ) ) );
        assertEquals( List.of( "Nom : Julie" ), lines( listIteration.get( 1 ) ) );
        assertEquals( List.of( "Nom : Léo", "Prénom : Petit" ), lines( listIteration.get( 2 ) ) );
    }

    @Test
    public void testNoResponseGivesNoIteration( )
    {
        assertTrue( GroupIterationsMarker.getIterations( List.of( NAME, FIRST_NAME ), List.of( ), Locale.FRENCH ).isEmpty( ) );
    }

    @Test
    public void testRenderEscapesTitlesAndValues( )
    {
        List<FormQuestionResponse> listResponse = List.of( response( NAME, "Nom & co", 0, "<em>Paul</em>" ), response( NAME, "Nom & co", 1, "Julie" ) );
        List<GroupIteration> listIteration = GroupIterationsMarker.getIterations( List.of( NAME ), listResponse, Locale.FRENCH );

        assertEquals( "<strong>Enfants &lt;i&gt; (1)</strong><br>Nom &amp; co : &lt;em&gt;Paul&lt;/em&gt;\n"
                + "<br><br><strong>Enfants &lt;i&gt; (2)</strong><br>Nom &amp; co : Julie",
                GroupIterationsMarker.render( group( "Enfants <i>", 0 ), listIteration, Locale.FRENCH ) );
    }

    @Test
    public void testRenderKeepsLineBreaks( )
    {
        List<GroupIteration> listIteration = GroupIterationsMarker.getIterations( List.of( NAME ),
                List.of( response( NAME, "Adresse", 0, "1 rue <b>A</b>\r\n75001 Paris" ) ), Locale.FRENCH );

        assertEquals( "<strong>Enfants (1)</strong><br>Adresse : 1 rue &lt;b&gt;A&lt;/b&gt;<br>75001 Paris",
                GroupIterationsMarker.render( group( "Enfants", 0 ), listIteration, Locale.FRENCH ) );
    }

    @Test
    public void testRenderWithoutIterationIsEmpty( )
    {
        assertEquals( "", GroupIterationsMarker.render( group( "Enfants", 0 ), List.of( ), Locale.FRENCH ) );
    }

    @Test
    public void testOnlyGroupsThatCanBeRepeatedAreIterable( )
    {
        List<FormDisplay> listQuestionDisplay = List.of( display( FormsConstants.MARK_QUESTION, NAME, GROUP_DISPLAY_ID, 1 ) );

        assertTrue( GroupIterationsMarker.isIterable( group( "Enfants", 0 ), listQuestionDisplay ) );
        assertTrue( GroupIterationsMarker.isIterable( group( "Enfants", 3 ), listQuestionDisplay ) );
        assertFalse( GroupIterationsMarker.isIterable( group( "Enfants", 1 ), listQuestionDisplay ) );
    }

    @Test
    public void testGroupHoldingASubGroupIsNotIterable( )
    {
        // as in the form, which never repeats a group holding a sub-group
        List<FormDisplay> listChildDisplay = List.of( display( FormsConstants.MARK_QUESTION, NAME, GROUP_DISPLAY_ID, 1 ),
                display( FormsConstants.MARK_GROUP, 40, GROUP_DISPLAY_ID, 2 ) );

        assertFalse( GroupIterationsMarker.isIterable( group( "Enfants", 0 ), listChildDisplay ) );
    }

    @Test
    public void testGroupsAtEveryDepth( )
    {
        FormDisplay groupOfStep = display( FormsConstants.MARK_GROUP, 11, 0, 1 );
        FormDisplay subGroup = display( FormsConstants.MARK_GROUP, 40, GROUP_DISPLAY_ID, 1 );
        FormDisplay question = display( FormsConstants.MARK_QUESTION, NAME, 0, 2 );

        assertEquals( List.of( groupOfStep, subGroup ), GroupIterationsMarker.getGroupDisplays( List.of( groupOfStep, subGroup, question ) ) );
    }

    @Test
    public void testRichTextIsKeptAsSanitizedHtml( )
    {
        FormQuestionResponse response = response( NAME, "Remarques", 0, "<p>Bonjour <strong>agent</strong></p><script>alert(1)</script>" );
        Entry entry = response.getQuestion( ).getEntry( );
        entry.getEntryType( ).setBeanName( "forms.entryTypeTextArea" );
        Field fieldRichText = new Field( );
        fieldRichText.setCode( IEntryTypeService.FIELD_RICHTEXT );
        fieldRichText.setValue( "true" );
        entry.setFields( List.of( fieldRichText ) );

        List<GroupIteration> listIteration = GroupIterationsMarker.getIterations( List.of( NAME ), List.of( response ), Locale.FRENCH );
        String strHtml = GroupIterationsMarker.render( group( "Enfants", 0 ), listIteration, Locale.FRENCH );

        assertTrue( strHtml, strHtml.contains( "Remarques : <p>Bonjour <strong>agent</strong></p>" ) );
        assertFalse( strHtml, strHtml.contains( "script" ) );
    }

    @Test
    public void testFileNameIsReadFromItsStoreWithoutChangingTheResponse( ) throws FileServiceException
    {
        IFileStoreServiceProvider fileStore = CDI.current( ).select( FileService.class ).get( ).getFileStoreServiceProvider( null );
        File file = new File( );
        file.setTitle( "certificat_paul.txt" );
        file.setMimeType( "text/plain" );
        PhysicalFile physicalFile = new PhysicalFile( );
        physicalFile.setValue( "test".getBytes( ) );
        file.setPhysicalFile( physicalFile );
        String strFileKey = fileStore.storeFile( file );
        try
        {
            // as loaded with the response: only the key and the store of the file
            File fileOfResponse = new File( );
            fileOfResponse.setFileKey( strFileKey );
            fileOfResponse.setOrigin( fileStore.getName( ) );
            FormQuestionResponse response = fileResponse( fileOfResponse );

            GroupIterationLine line = GroupIterationsMarker.getLine( response, Locale.FRENCH );

            assertEquals( "certificat_paul.txt", line.getValue( ) );
            assertSame( fileOfResponse, response.getEntryResponse( ).get( 0 ).getFile( ) );
            assertNull( fileOfResponse.getTitle( ) );
        }
        finally
        {
            fileStore.delete( strFileKey );
        }
    }

    @Test
    public void testMissingFileGivesNoName( )
    {
        File fileOfResponse = new File( );
        fileOfResponse.setFileKey( String.valueOf( Integer.MAX_VALUE ) );

        assertEquals( "", GroupIterationsMarker.getLine( fileResponse( fileOfResponse ), Locale.FRENCH ).getValue( ) );
    }

    private static Group group( String strTitle, int nIterationMax )
    {
        Group group = new Group( );
        group.setId( 12 );
        group.setTitle( strTitle );
        group.setIterationMax( nIterationMax );
        return group;
    }

    private static FormDisplay display( String strType, int nCompositeId, int nParentId, int nDisplayOrder )
    {
        FormDisplay formDisplay = new FormDisplay( );
        formDisplay.setCompositeType( strType );
        formDisplay.setCompositeId( nCompositeId );
        formDisplay.setParentId( nParentId );
        formDisplay.setDisplayOrder( nDisplayOrder );
        return formDisplay;
    }

    private static FormQuestionResponse response( int nQuestionId, String strTitle, int nIteration, String strValue )
    {
        EntryType entryType = new EntryType( );
        entryType.setBeanName( "forms.entryTypeText" );
        Entry entry = new Entry( );
        entry.setEntryType( entryType );
        Question question = new Question( );
        question.setId( nQuestionId );
        question.setTitle( strTitle );
        question.setIterationNumber( nIteration );
        question.setEntry( entry );
        Response response = new Response( );
        response.setResponseValue( strValue );
        List<Response> listResponse = new ArrayList<>( );
        listResponse.add( response );
        FormQuestionResponse formQuestionResponse = new FormQuestionResponse( );
        formQuestionResponse.setQuestion( question );
        formQuestionResponse.setEntryResponse( listResponse );
        return formQuestionResponse;
    }

    private static FormQuestionResponse fileResponse( File file )
    {
        FormQuestionResponse response = response( NAME, "Justificatif", 0, null );
        response.getQuestion( ).getEntry( ).getEntryType( ).setBeanName( "forms.entryTypeFile" );
        response.getEntryResponse( ).get( 0 ).setFile( file );
        return response;
    }

    private static List<String> lines( GroupIteration iteration )
    {
        return iteration.getLines( ).stream( ).map( line -> line.getQuestion( ) + " : " + line.getValue( ) ).toList( );
    }
}
