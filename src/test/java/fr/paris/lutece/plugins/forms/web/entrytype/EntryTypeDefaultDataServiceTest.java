/*
 * Copyright (c) 2002-2022, City of Paris
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
package fr.paris.lutece.plugins.forms.web.entrytype;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test for the decision that keeps a validation error when a file response has no content.
 *
 * When a question is saved without validation (backup, step navigation), errors are normally dropped so that an incomplete form can be saved. A file whose
 * physical content is missing is the exception : keeping it would store a file that can never be downloaded.
 */
public class EntryTypeDefaultDataServiceTest extends LuteceTestCase
{
    private final EntryTypeDefaultDataService _dataService = new EntryTypeDefaultDataService( "forms.entryTypeFile" );

    @Test
    public void testFileWithoutPhysicalFileIsFlagged( )
    {
        File file = new File( );
        file.setTitle( "test.csv" );
        file.setSize( 6 );
        // no physical file : this is what an entry type produces when the upload failed validation

        assertTrue( _dataService.hasFileWithoutContent( responseWithFile( file ) ) );
    }

    @Test
    public void testFileWithNullPhysicalValueIsFlagged( )
    {
        File file = new File( );
        file.setTitle( "test.csv" );
        file.setPhysicalFile( new PhysicalFile( ) ); // physical file present but value never read

        assertTrue( _dataService.hasFileWithoutContent( responseWithFile( file ) ) );
    }

    @Test
    public void testFileWithContentIsNotFlagged( )
    {
        PhysicalFile physicalFile = new PhysicalFile( );
        physicalFile.setValue( "azerty".getBytes( ) );

        File file = new File( );
        file.setTitle( "test.csv" );
        file.setPhysicalFile( physicalFile );

        assertFalse( _dataService.hasFileWithoutContent( responseWithFile( file ) ) );
    }

    /**
     * A mandatory question left empty builds a response without any file : it must stay saveable as a draft, so it must not be flagged.
     */
    @Test
    public void testResponseWithoutFileIsNotFlagged( )
    {
        Response response = new Response( );

        FormQuestionResponse formQuestionResponse = new FormQuestionResponse( );
        List<Response> listResponse = new ArrayList<>( );
        listResponse.add( response );
        formQuestionResponse.setEntryResponse( listResponse );

        assertFalse( _dataService.hasFileWithoutContent( formQuestionResponse ) );
    }

    @Test
    public void testBuildErrorFallsBackToDisplayableErrorWhenNoneGiven( )
    {
        Question question = new Question( );
        question.setTitle( "File question" );

        GenericAttributeError error = _dataService.buildFileWithoutContentError( question, null, Locale.FRANCE );

        assertNotNull( error, "A displayable error must be built even when getResponseData returned none" );
        assertTrue( error.getIsDisplayableError( ) );
    }

    @Test
    public void testBuildErrorKeepsTheGivenError( )
    {
        Question question = new Question( );
        question.setTitle( "File question" );

        GenericAttributeError existingError = new GenericAttributeError( );

        assertSame( existingError, _dataService.buildFileWithoutContentError( question, existingError, Locale.FRANCE ) );
    }

    private static FormQuestionResponse responseWithFile( File file )
    {
        Response response = new Response( );
        response.setFile( file );

        FormQuestionResponse formQuestionResponse = new FormQuestionResponse( );
        List<Response> listResponse = new ArrayList<>( );
        listResponse.add( response );
        formQuestionResponse.setEntryResponse( listResponse );

        return formQuestionResponse;
    }
}
