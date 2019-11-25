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
package fr.paris.lutece.plugins.forms.business.form.service;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseStep;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.service.search.IFormSearchIndexer;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.test.LuteceTestCase;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Ignore;

import fr.paris.lutece.plugins.forms.service.search.IFormSearchEngine;

//@RunWith( SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations={"classpath:/conf/plugins/forms_context.xml"})
@Ignore
public class LuceneFormSearchTest extends LuteceTestCase
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

    private IFormSearchIndexer _formFormSearchIndexer;
    private IFormSearchEngine _formFormSearchEngine;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUp( ) throws Exception
    {
        super.setUp( );
        _formFormSearchIndexer = SpringContextService.getBean( "forms.luceneFormsSearchIndexer" );
        _formFormSearchEngine = SpringContextService.getBean( "forms.luceneFormsSearchEngine" );
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

        Question question = new Question( );
        question.setDescription( DEFAULT_QUESTION_DESCRIPTION );
        question.setId( DEFAULT_INT );
        question.setEntry( entry );

        List<Response> listResponses = new ArrayList<>( );
        Response response = new Response( );
        response.setEntry( entry );
        response.setIdResponse( DEFAULT_INT );
        response.setResponseValue( strValueText );
        listResponses.add( response );

        List<FormQuestionResponse> listFormQuestionResponse = new ArrayList<>( );
        FormQuestionResponse formQuestionResponse = new FormQuestionResponse( );
        formQuestionResponse.setQuestion( question );
        formQuestionResponse.setEntryResponse( listResponses );
        listFormQuestionResponse.add( formQuestionResponse );

        Step step = new Step( );
        step.setDescription( DEFAULT_STEP_DESCRIPTION );
        step.setFinal( true );
        step.setInitial( true );
        step.setIdForm( DEFAULT_INT );
        step.setTitle( DEFAULT_STEP_TITLE );

        FormResponseStep formResponseStep = new FormResponseStep( );
        formResponseStep.setStep( step );
        formResponseStep.setFormResponseId( DEFAULT_INT );
        formResponseStep.setOrder( DEFAULT_INT );
        formResponseStep.setId( DEFAULT_INT );
        formResponseStep.setQuestions( listFormQuestionResponse );
        List<FormResponseStep> listFormResponseStep = new ArrayList<>( );
        listFormResponseStep.add( formResponseStep );

        formResponse.setSteps( listFormResponseStep );

        return formResponse;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tearDown( ) throws Exception
    {
        super.tearDown( );
    }
}
