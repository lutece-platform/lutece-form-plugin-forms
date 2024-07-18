/*
 * Copyright (c) 2002-2024, City of Paris
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
package fr.paris.lutece.plugins.forms.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponseStep;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.web.admin.MultiviewFormResponseDetailsJspBean;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.rbac.RBACRole;
import fr.paris.lutece.portal.business.right.Right;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.test.LuteceTestCase;

public class FormServiceTest extends LuteceTestCase {
	
	private static final Timestamp TIMESTAMP_NOW = Timestamp.valueOf( LocalDateTime.now( ) );
    private static final UUID DEFAULT_GUID = UUID.randomUUID( );
    private static final String RESOURCE_TYPE = "FORMS_FORM";

    // Step
    private static final String DEFAULT_STEP_DESCRIPTION = "default_step_description";
    private static final String DEFAULT_STEP_TITLE = "default_step_title";

    // Question
    private static final String DEFAULT_QUESTION_DESCRIPTION = "default_question_description";
    private static final String DEFAULT_QUESTION_TITLE = "default_question_title";
    private static final String DEFAULT_QUESTION_CODE = "default_question_code";
    private static final String DEFAULT_QUESTION_COLUMN_TITLE = "default_question_column_title";

    // Entry
    private static final int ENTRY_TYPE_FILE_ID = 108;
    private static final String ENTRY_TYPE_TITLE = "default_entry_type_title";

    // Entry type
    private static final String BEAN_NAME_ENTRY_TYPE_FILE = "forms.entryTypeFile";
	
    // Form
	private static final String FORM_TITLE = "default_form_title";
	private static final String FORM_BREADCRUMB = "forms.horizontalBreadcrumb";
	
	private static final String FORMS_ROLE_KEY = "forms_manager";
	private static final String ATTRIBUTE_ADMIN_USER = "lutece_admin_user";
	
	private FormService _formService;
	
	private Form _form;
	
	private Response _response;
	
	private FormResponse _formResponse;
	
	/**
     * {@inheritDoc }
     * 
     * @throws Exception
     */
	@Override
    protected void setUp( ) throws Exception
    {
        super.setUp( );
        
        PluginService.getPlugin( FormsPlugin.PLUGIN_NAME ).install( );
        _formService = SpringContextService.getBean( FormService.BEAN_NAME );
        createForm( FORM_TITLE );
        initFormResponse( );
    }
    
    @Test
    public void testSaveForm( ) 
    {
    	_formService.saveForm( _form, _formResponse );
    	
    	FormResponse formResponseLoaded = FormResponseHome.findByPrimaryKey( _formResponse.getId( ) );
    	
    	assertEquals( 1, formResponseLoaded.getSteps( ).size( ) );
    }
    
    
    @Test
    public void testIsFileAccessAuthorized( )
    {
    	MockHttpServletRequest request = new MockHttpServletRequest( );
    	request.getSession( true ).setAttribute( ATTRIBUTE_ADMIN_USER, getUserAdmin( ) );
    	
    	ResponseHome.create( _response );
        
    	assertTrue( _formService.isFileAccessAuthorized( request, _response.getIdResponse( ), Integer.valueOf( _response.getFile( ).getFileKey( ) ) ) );
    }
    
	
	/**
	 * create and store a form
	 * 
	 * @param strTitle form title
	 */
	private void createForm( String strTitle )
	{
		Form form = new Form( );
		form.setTitle( strTitle );
		form.setAvailabilityStartDate( TIMESTAMP_NOW );
		form.setBreadcrumbName( FORM_BREADCRUMB );
		
		_form = FormHome.create( form );
	}
	
	/**
	 * init a form response
	 * 
	 */
	private void initFormResponse( )
    {
        Step step = createStep();

        EntryType entryTypeText = new EntryType( );
        entryTypeText.setIdType( ENTRY_TYPE_FILE_ID );
        entryTypeText.setTitle( ENTRY_TYPE_TITLE );
        entryTypeText.setBeanName( BEAN_NAME_ENTRY_TYPE_FILE );

        Entry entry = new Entry( );
        entry.setIndexed( true );
        entry.setEntryType( entryTypeText );
        entry.setResourceType( RESOURCE_TYPE );
        entry.setIdResource( _form.getId( ) );
        
        EntryHome.create( entry );
        
        initResponse( entry );
        List<Response> listResponses = new ArrayList<>( Arrays.asList( _response ) );

        Question question = createQuestion( entry, step );

        List<FormQuestionResponse> listFormQuestionResponse = new ArrayList<>( );
        FormQuestionResponse formQuestionResponse = new FormQuestionResponse( );
        formQuestionResponse.setQuestion( question );
        formQuestionResponse.setEntryResponse( listResponses );
        listFormQuestionResponse.add( formQuestionResponse );
        
        FormResponseStep formResponseStep = new FormResponseStep( );
        formResponseStep.setStep( step );
        formResponseStep.setOrder( 0 );
        formResponseStep.setQuestions( listFormQuestionResponse );
        
        List<FormResponseStep> listFormResponseStep = new ArrayList<>( );
        listFormResponseStep.add( formResponseStep );

        _formResponse = new FormResponse( );
        _formResponse.setDateCreation( TIMESTAMP_NOW );
        _formResponse.setFormId( _form.getId( ) );
        _formResponse.setGuid( DEFAULT_GUID.toString( ) );
        _formResponse.setUpdate( TIMESTAMP_NOW );
        _formResponse.setSteps( listFormResponseStep );
    }
	
	/**
	 * create a step
	 * 
	 * @return the step created
	 */
	private Step createStep()
	{
		Step step = new Step( );
		step.setTitle( DEFAULT_STEP_TITLE );
        step.setDescription( DEFAULT_STEP_DESCRIPTION );
        step.setIdForm( _form.getId( ) );
        step.setInitial( true );
        step.setFinal( true );

        return StepHome.create( step );
	}
	
	/**
	 * create a question
	 * 
	 * @param entry entry related to question
	 * @param step question step
	 * @return the question created
	 */
	private Question createQuestion( Entry entry, Step step )
	{
		Question question = new Question( );
		question.setTitle( DEFAULT_QUESTION_TITLE );
		question.setDescription( DEFAULT_QUESTION_DESCRIPTION );
		question.setCode( DEFAULT_QUESTION_CODE );
		question.setColumnTitle( DEFAULT_QUESTION_COLUMN_TITLE );
		question.setStep( step );
		question.setEntry( entry );
		question.setIsVisible( true );
        
        return QuestionHome.create( question );
	}
	
	/**
     *  get an admin user for tests
     * 
     */
    private AdminUser getUserAdmin( )
    {
        AdminUser adminUser = new AdminUser( );
        adminUser.setAccessCode( "admin" );
        adminUser.setLastName( "test" );
        adminUser.setStatus( 0 );
        adminUser.setUserLevel( 0 );
        
        Map<String, Right> rights = new HashMap<>( 1 );
        rights.put( MultiviewFormResponseDetailsJspBean.RIGHT_FORMS_MULTIVIEW, new Right( ) );
        adminUser.setRights( rights );
        
        RBACRole role = new RBACRole( FORMS_ROLE_KEY, "" );
        Map<String, RBACRole> roles = new HashMap<>( 1 );
        roles.put( FORMS_ROLE_KEY, role );
        adminUser.addRoles( roles );
        
        return adminUser;
    }
    
    /**
     * init a response 
     * 
     * @param entry an entry
     */
    private void initResponse( Entry entry )
    {
    	File file = new File( );
        file.setTitle( "test" );
        file.setDateCreation( TIMESTAMP_NOW );
        file.setExtension( "txt" );
        file.setMimeType( "text/plain" );
        file.setSize( 12 );
        
    	_response = new Response( );
    	_response.setEntry( entry );
    	_response.setFile( file );
    }
}