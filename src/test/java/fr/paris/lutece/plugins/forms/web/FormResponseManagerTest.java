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
package fr.paris.lutece.plugins.forms.web;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseStep;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.test.LuteceTestCase;

public class FormResponseManagerTest extends LuteceTestCase
{

    private static final Timestamp TIMESTAMP_NOW = Timestamp.valueOf( LocalDateTime.now( ) );
    private static final int DEFAULT_INT = -1;
    private static final UUID DEFAULT_GUID = UUID.randomUUID( );

    // Step
    private static final String DEFAULT_STEP_DESCRIPTION = "default_step_description";
    private static final String DEFAULT_STEP_TITLE = "default_step_title";

    // Question
    private static final String DEFAULT_QUESTION_DESCRIPTION = "default_question_description";

    // Entry
    private static final int ENTRY_TYPE_TEXT_ID = 106;
    private static final String ENTRY_TYPE_TITLE = "default_entry_type_title";

    // Entry type
    private static final String BEAN_NAME_ENTRY_TYPE_TEXT = "forms.entryTypeText";

    public void testConstructorFromFormResponse( )
    {
        FormResponse response = createFormResponse( "e" );

        FormResponseManager manager = new FormResponseManager( response );

        assertEquals( 2, manager.getValidatedSteps( ).size( ) );

        for ( FormResponseStep step : manager.getFormResponse( ).getSteps( ) )
        {
            Question q = step.getQuestions( ).get( 0 ).getQuestion( );
            if ( q.getId( ) == 1 )
            {
                assertTrue( q.isVisible( ) );
            }
            else
                if ( q.getId( ) == 2 )
                {
                    assertFalse( q.isVisible( ) );
                }
                else
                {
                    fail( );
                }

        }

    }

    /**
     * Create a form
     * 
     * @param strValueText
     * @return
     */
    private FormResponse createFormResponse( String strValueText )
    {
        FormResponse formResponse = new FormResponse( );
        formResponse.setDateCreation( TIMESTAMP_NOW );
        formResponse.setFormId( DEFAULT_INT );
        formResponse.setGuid( DEFAULT_GUID.toString( ) );
        formResponse.setId( DEFAULT_INT );
        formResponse.setUpdate( TIMESTAMP_NOW );

        EntryType entryTypeText = new EntryType( );
        entryTypeText.setIdType( ENTRY_TYPE_TEXT_ID );
        entryTypeText.setTitle( ENTRY_TYPE_TITLE );
        entryTypeText.setBeanName( BEAN_NAME_ENTRY_TYPE_TEXT );

        Entry entry = new Entry( );
        entry.setIndexed( true );
        entry.setEntryType( entryTypeText );

        Question question1 = new Question( );
        question1.setDescription( DEFAULT_QUESTION_DESCRIPTION );
        question1.setId( 1 );
        question1.setEntry( entry );

        Question question2 = new Question( );
        question2.setDescription( DEFAULT_QUESTION_DESCRIPTION );
        question2.setId( 2 );
        question2.setEntry( entry );

        List<Response> listResponses1 = new ArrayList<>( );
        Response response1 = new Response( );
        response1.setEntry( entry );
        response1.setIdResponse( DEFAULT_INT );
        response1.setResponseValue( strValueText );
        listResponses1.add( response1 );

        List<FormQuestionResponse> listFormQuestionResponse1 = new ArrayList<>( );
        FormQuestionResponse formQuestionResponse1 = new FormQuestionResponse( );
        formQuestionResponse1.setQuestion( question1 );
        formQuestionResponse1.setEntryResponse( listResponses1 );
        listFormQuestionResponse1.add( formQuestionResponse1 );

        Step step1 = new Step( );
        step1.setDescription( DEFAULT_STEP_DESCRIPTION );
        step1.setFinal( true );
        step1.setInitial( true );
        step1.setIdForm( DEFAULT_INT );
        step1.setTitle( DEFAULT_STEP_TITLE );

        FormResponseStep formResponseStep1 = new FormResponseStep( );
        formResponseStep1.setStep( step1 );
        formResponseStep1.setFormResponseId( DEFAULT_INT );
        formResponseStep1.setOrder( 0 );
        formResponseStep1.setId( DEFAULT_INT );
        formResponseStep1.setQuestions( listFormQuestionResponse1 );

        List<FormQuestionResponse> listFormQuestionResponse2 = new ArrayList<>( );
        FormQuestionResponse formQuestionResponse2 = new FormQuestionResponse( );
        formQuestionResponse2.setQuestion( question2 );
        listFormQuestionResponse2.add( formQuestionResponse2 );

        Step step2 = new Step( );
        step2.setDescription( DEFAULT_STEP_DESCRIPTION );
        step2.setFinal( true );
        step2.setInitial( true );
        step2.setIdForm( DEFAULT_INT );
        step2.setTitle( DEFAULT_STEP_TITLE );

        FormResponseStep formResponseStep2 = new FormResponseStep( );
        formResponseStep2.setStep( step2 );
        formResponseStep2.setFormResponseId( DEFAULT_INT );
        formResponseStep2.setOrder( 1 );
        formResponseStep2.setId( DEFAULT_INT );
        formResponseStep2.setQuestions( listFormQuestionResponse2 );

        List<FormResponseStep> listFormResponseStep = new ArrayList<>( );
        listFormResponseStep.add( formResponseStep1 );
        listFormResponseStep.add( formResponseStep2 );

        formResponse.setSteps( listFormResponseStep );

        return formResponse;
    }
}
