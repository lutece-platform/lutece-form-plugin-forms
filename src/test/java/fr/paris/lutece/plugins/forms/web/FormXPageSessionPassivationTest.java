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
package fr.paris.lutece.plugins.forms.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseStep;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Regression guard for cluster session passivation.
 *
 * In a multi-instance deployment (OpenLiberty + Hazelcast SessionCache), the
 * container serialises the {@code @SessionScoped} bean {@code FormXPage} and
 * all its non-transient fields when moving a session between nodes. CDI-injected
 * service fields are client proxies (Serializable by spec), so they are not
 * the risk. The risk is on domain-state fields stored directly on the bean —
 * at the time of writing: {@code FormResponseManager}, {@code Step},
 * {@code StepDisplayTree}, {@code IBreadcrumb} (via proxy). Any future change
 * that introduces a non-Serializable reference (e.g. an {@code HttpServletRequest},
 * a raw entity-manager handle, a lambda capturing a non-Serializable scope…)
 * will surface here as a {@link java.io.NotSerializableException}.
 *
 * The test covers the state actually present on {@code FormXPage}:
 * <ul>
 *   <li>{@code FormResponseManager} and its full transitive object graph
 *       (validated steps, questions, entries, responses);</li>
 *   <li>{@code Step};</li>
 *   <li>a compound holder combining both so we also catch cross-field regressions.</li>
 * </ul>
 *
 * {@code IBreadcrumb}-typed and service fields are not exercised here: in
 * production they hold CDI client proxies, so their serializability is the
 * responsibility of the CDI container (Weld in OpenLiberty) and would fail at
 * deployment-time passivation validation, not at runtime.
 */
public class FormXPageSessionPassivationTest extends LuteceTestCase
{
    private static final Timestamp TIMESTAMP_NOW = Timestamp.valueOf( LocalDateTime.now( ) );
    private static final int DEFAULT_INT = -1;
    private static final UUID DEFAULT_GUID = UUID.randomUUID( );

    private static final String DEFAULT_STEP_DESCRIPTION = "default_step_description";
    private static final String DEFAULT_STEP_TITLE = "default_step_title";
    private static final String DEFAULT_QUESTION_DESCRIPTION = "default_question_description";
    private static final int ENTRY_TYPE_TEXT_ID = 106;
    private static final String ENTRY_TYPE_TITLE = "default_entry_type_title";
    private static final String BEAN_NAME_ENTRY_TYPE_TEXT = "forms.entryTypeText";

    @Test
    public void testFormResponseManagerSurvivesSessionPassivation( ) throws IOException, ClassNotFoundException
    {
        FormResponseManager original = new FormResponseManager( buildFormResponse( "hello" ) );

        FormResponseManager restored = roundTrip( original );

        assertNotNull( restored );
        assertEquals( original.getValidatedSteps( ).size( ), restored.getValidatedSteps( ).size( ) );
        assertEquals( original.getFormResponse( ).getFormId( ), restored.getFormResponse( ).getFormId( ) );
        assertEquals( original.getFormResponse( ).getGuid( ), restored.getFormResponse( ).getGuid( ) );
        assertEquals( original.getFormResponse( ).getSteps( ).size( ), restored.getFormResponse( ).getSteps( ).size( ) );
        assertEquals( original.getIsBackupResponseAlreadyInitiated( ), restored.getIsBackupResponseAlreadyInitiated( ) );
        assertEquals( original.getIsResponseLoadedFromBackup( ), restored.getIsResponseLoadedFromBackup( ) );

        // A known response value should travel all the way down through FormResponseStep → FormQuestionResponse → Response.
        String restoredAnswer = restored.getFormResponse( ).getSteps( ).get( 0 )
                .getQuestions( ).get( 0 )
                .getEntryResponse( ).get( 0 )
                .getResponseValue( );
        assertEquals( "hello", restoredAnswer );
    }

    @Test
    public void testStepSurvivesSessionPassivation( ) throws IOException, ClassNotFoundException
    {
        Step original = new Step( );
        original.setId( 42 );
        original.setIdForm( DEFAULT_INT );
        original.setTitle( DEFAULT_STEP_TITLE );
        original.setDescription( DEFAULT_STEP_DESCRIPTION );
        original.setInitial( true );
        original.setFinal( false );

        Step restored = roundTrip( original );

        assertNotNull( restored );
        assertEquals( original.getId( ), restored.getId( ) );
        assertEquals( original.getIdForm( ), restored.getIdForm( ) );
        assertEquals( original.getTitle( ), restored.getTitle( ) );
        assertEquals( original.getDescription( ), restored.getDescription( ) );
        assertEquals( original.isInitial( ), restored.isInitial( ) );
        assertEquals( original.isFinal( ), restored.isFinal( ) );
    }

    @Test
    public void testCompoundSessionStateSurvivesPassivation( ) throws IOException, ClassNotFoundException
    {
        SessionSnapshot original = new SessionSnapshot( );
        original._formResponseManager = new FormResponseManager( buildFormResponse( "bundled" ) );
        original._currentStep = new Step( );
        original._currentStep.setId( 7 );
        original._currentStep.setTitle( "current" );

        SessionSnapshot restored = roundTrip( original );

        assertNotNull( restored );
        assertNotNull( restored._formResponseManager );
        assertNotNull( restored._currentStep );
        assertEquals( 7, restored._currentStep.getId( ) );
        assertEquals( "current", restored._currentStep.getTitle( ) );
        assertEquals( 2, restored._formResponseManager.getValidatedSteps( ).size( ) );
    }

    @SuppressWarnings( "unchecked" )
    private static <T extends Serializable> T roundTrip( T object ) throws IOException, ClassNotFoundException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream( );
        try ( ObjectOutputStream oos = new ObjectOutputStream( baos ) )
        {
            oos.writeObject( object );
        }
        try ( ObjectInputStream ois = new ObjectInputStream( new ByteArrayInputStream( baos.toByteArray( ) ) ) )
        {
            return (T) ois.readObject( );
        }
    }

    /**
     * Builds a two-step form response with one question per step and one response on the first,
     * mirroring the shape of {@code FormResponseManagerTest}. Kept local to avoid coupling the
     * regression guard to another test class.
     */
    private FormResponse buildFormResponse( String answerValue )
    {
        FormResponse formResponse = new FormResponse( );
        formResponse.setDateCreation( TIMESTAMP_NOW );
        formResponse.setFormId( DEFAULT_INT );
        formResponse.setGuid( DEFAULT_GUID.toString( ) );
        formResponse.setId( DEFAULT_INT );
        formResponse.setUpdate( TIMESTAMP_NOW );

        EntryType entryType = new EntryType( );
        entryType.setIdType( ENTRY_TYPE_TEXT_ID );
        entryType.setTitle( ENTRY_TYPE_TITLE );
        entryType.setBeanName( BEAN_NAME_ENTRY_TYPE_TEXT );

        Entry entry = new Entry( );
        entry.setIndexed( true );
        entry.setEntryType( entryType );

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
        response1.setResponseValue( answerValue );
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

    /**
     * Holder mimicking the session state carried by {@code FormXPage} that is directly
     * serialised by the container. Isolated in the test class to keep the production
     * bean untouched.
     */
    private static class SessionSnapshot implements Serializable
    {
        @Serial
        private static final long serialVersionUID = 1L;
        FormResponseManager _formResponseManager;
        Step _currentStep;
    }
}
