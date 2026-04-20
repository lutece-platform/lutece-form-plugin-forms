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
package fr.paris.lutece.plugins.forms.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormMessageHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseFilter;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponseStep;
import fr.paris.lutece.plugins.forms.business.FormResponseStepHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.business.export.FormExportConfigHome;
import fr.paris.lutece.plugins.forms.exception.LockException;
import fr.paris.lutece.plugins.forms.exception.MaxFormResponseException;
import fr.paris.lutece.plugins.forms.service.event.FormResponseEvent;
import fr.paris.lutece.plugins.forms.service.lock.FormsDistributedLockManager;
import fr.paris.lutece.plugins.forms.service.lock.LockResult;
import fr.paris.lutece.plugins.forms.service.workflow.IFormWorkflowService;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.util.FormsResponseUtils;
import fr.paris.lutece.plugins.forms.web.CompositeGroupDisplay;
import fr.paris.lutece.plugins.forms.web.CompositeQuestionDisplay;
import fr.paris.lutece.plugins.forms.web.FormResponseManager;
import fr.paris.lutece.plugins.forms.web.ICompositeDisplay;
import fr.paris.lutece.plugins.forms.web.StepDisplayTree;
import fr.paris.lutece.plugins.forms.web.admin.MultiviewFormResponseDetailsJspBean;
import fr.paris.lutece.plugins.forms.web.entrytype.IEntryDataService;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseFilter;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeFile;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeGalleryImage;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeImage;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.event.EventAction;
import fr.paris.lutece.portal.service.event.Type.TypeQualifier;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.util.sql.TransactionManager;

/**
 * This is the service class related to the form
 */
@ApplicationScoped
public class FormService
{
    public static final String BEAN_NAME = "forms.formService";

    private static final String QUOTA_LOCK_PREFIX = "forms.quota.form.";
    private static final long QUOTA_LOCK_TIMEOUT_MS = 30_000L;
    private static final int QUOTA_LOCK_MAX_RETRIES = 5;
    private static final long QUOTA_LOCK_BACKOFF_MS = 100L;

    @Inject
    private IFormWorkflowService _formWorkflowService;

    @Inject
    private Event<FormResponseEvent> _formResponseEvent;

    @Inject
    private StepService _stepService;

    @Inject
    private EntryServiceManager _entryServiceManager;

    @Inject
    @Named( "forms.luceneLockManager" )
    private FormsDistributedLockManager _distributedLockManager;

    /**
     * Saves the specified form
     *      * 
     * @param form
     *            the form to save
     * @param formResponse
     *            the form response to save
     * @throws MaxFormResponseException 
     * 			 the MaxFormResponseException Runtime Exception
     * 
     */
    public void saveForm( Form form, FormResponse formResponse )
    {
        boolean quotaGuarded = ( form.getMaxNumberResponse( ) != 0 || form.isOneResponseByUser( ) )
                && ( formResponse.getId( ) == 0 || !formResponse.isFromSave( ) );

        if ( quotaGuarded )
        {
            saveFormUnderQuotaLock( form, formResponse );
        }
        else
        {
            saveFormInTransaction( formResponse );
        }

        fireFormResponseEventCreation( formResponse );
    }

    /**
     * Quota-protected path: acquire a cluster-wide lock scoped to this form, then
     * re-check the quota against the database (the only source of truth) and
     * persist the response inside a single transaction. The lock covers the
     * check + insert + commit so the next instance can only observe committed rows.
     */
    private void saveFormUnderQuotaLock( Form form, FormResponse formResponse )
    {
        LockResult lockResult = acquireQuotaLock( form );
        try
        {
            TransactionManager.beginTransaction( FormsPlugin.getPlugin( ) );
            try
            {
                if ( !FormsResponseUtils.checkNumberMaxResponseForm( form ) )
                {
                    throw new MaxFormResponseException(
                            "The maximum number of response has been reached for the form: " + form.getTitle( ) );
                }
                if ( !FormsResponseUtils.checkIfUserResponseForm( form, formResponse.getGuid( ) ) )
                {
                    throw new MaxFormResponseException(
                            "The maximum number of response has been reached for the user with the guid: "
                                    + formResponse.getGuid( ) );
                }
                saveForm( formResponse );
                TransactionManager.commitTransaction( FormsPlugin.getPlugin( ) );
            }
            catch ( Exception e )
            {
                TransactionManager.rollBack( FormsPlugin.getPlugin( ) );
                throw e;
            }
        }
        finally
        {
            releaseQuotaLock( lockResult );
        }
    }

    private void saveFormInTransaction( FormResponse formResponse )
    {
        TransactionManager.beginTransaction( FormsPlugin.getPlugin( ) );
        try
        {
            saveForm( formResponse );
            TransactionManager.commitTransaction( FormsPlugin.getPlugin( ) );
        }
        catch ( Exception e )
        {
            TransactionManager.rollBack( FormsPlugin.getPlugin( ) );
            throw e;
        }
    }

    /**
     * Acquire the quota lock for this form. Retries briefly on contention so two simultaneous
     * submissions serialise cleanly; gives up after a short window and treats sustained
     * contention as if the quota were reached (safer than accepting an unguarded insert).
     */
    private LockResult acquireQuotaLock( Form form )
    {
        String lockName = QUOTA_LOCK_PREFIX + form.getId( );
        LockException lastFailure = null;
        for ( int attempt = 0; attempt < QUOTA_LOCK_MAX_RETRIES; attempt++ )
        {
            try
            {
                return _distributedLockManager.acquireLock( lockName, QUOTA_LOCK_TIMEOUT_MS );
            }
            catch ( LockException e )
            {
                lastFailure = e;
                try
                {
                    Thread.sleep( QUOTA_LOCK_BACKOFF_MS );
                }
                catch ( InterruptedException ie )
                {
                    Thread.currentThread( ).interrupt( );
                    throw new MaxFormResponseException(
                            "Interrupted while acquiring quota lock for form " + form.getId( ), ie );
                }
            }
        }
        throw new MaxFormResponseException(
                "Could not acquire quota lock for form " + form.getId( ) + " (contention timeout)", lastFailure );
    }

    private void releaseQuotaLock( LockResult lockResult )
    {
        if ( lockResult == null )
        {
            return;
        }
        try
        {
            _distributedLockManager.releaseLock( lockResult );
        }
        catch ( LockException e )
        {
            AppLogService.error( "Failed to release quota lock " + lockResult.getNameLock( ), e );
        }
    }
    /**
     * Save the response of form
     * 
     *  @param formResponse
     *            The FormResponse
      * @throws MaxFormResponseException 
     * 			 the MaxFormResponseException Runtime Exception
     */
    public void saveFormResponse( FormResponse formResponse )
    {	
    	saveForm( FormHome.findByPrimaryKey( formResponse.getFormId( ) ), formResponse );
    }

    /**
     * Process action on form creation
     *
     * @param form
     *            the workflow form
     * @param formResponse
     *            the form response
     */
    public void processFormAction( Form form, FormResponse formResponse )
    {
        _formWorkflowService.doProcessActionOnFormCreation( form, formResponse );
        fireFormResponseEventUpdate( formResponse );
    }

    /**
     * Filters the responses to keep the final responses
     * 
     * @param formResponse
     *            the form response containing the responses to filter
     */
    private void filterFinalSteps( FormResponse formResponse )
    {
        formResponse.setSteps(
                formResponse.getSteps( ).stream( ).filter( step -> step.getOrder( ) != FormsConstants.ORDER_NOT_SET ).collect( Collectors.toList( ) ) );
    }
    /**
     * Saves the form
     * @param formResponse
     */
    private void saveForm(FormResponse formResponse) 
    {
    	formResponse.setFromSave( Boolean.FALSE );	
        filterFinalSteps( formResponse );
        save( formResponse );
        saveFormResponseSteps( formResponse );
    }
    /**
     * Saves the form response
     * 
     * @param formResponse
     *            the form response to save
     */
    private void save( FormResponse formResponse )
    {
        if ( formResponse.getId( ) > 0 )
        {
            FormResponseHome.update( formResponse );

            for ( FormQuestionResponse formQuestionResponse : FormQuestionResponseHome.getFormQuestionResponseListByFormResponse( formResponse.getId( ) ) )
            {
                FormQuestionResponseHome.remove( formQuestionResponse );
            }

            FormResponseStepHome.removeByFormResponse( formResponse.getId( ) );
        }
        else
        {
        	FormResponseHome.create( formResponse );
        }
    }
    
    /**
     * Saves the form response
     * 
     * @param formResponse
     *            the form response to save
     */
    public void saveFormResponseWithoutQuestionResponse( FormResponse formResponse )
    {
        FormResponseHome.update( formResponse );
        fireFormResponseEventUpdate( formResponse, false );
    }

    /**
     * Saves the form response steps
     * 
     * @param formResponse
     *            the form response containing the form response steps to save
     */
    private void saveFormResponseSteps( FormResponse formResponse )
    {
        for ( FormResponseStep formResponseStep : formResponse.getSteps( ) )
        {
            formResponseStep.setFormResponseId( formResponse.getId( ) );

            saveFormQuestionResponse( formResponseStep );

            FormResponseStepHome.create( formResponseStep );
        }
    }

    /**
     * Saves the form question responses of the specified step
     * 
     * @param formResponseStep
     *            the form response step containing the form questions responses to save
     */
    private void saveFormQuestionResponse( FormResponseStep formResponseStep )
    {
        for ( FormQuestionResponse formQuestionResponse : formResponseStep.getQuestions( ) )
        {
            Question question = formQuestionResponse.getQuestion( );

            if ( question != null && question.isVisible( ) )
            {
                IEntryDataService dataService = _entryServiceManager.getEntryDataService( question.getEntry( ).getEntryType( ) );
                formQuestionResponse.setIdFormResponse( formResponseStep.getFormResponseId( ) );
                dataService.save( formQuestionResponse );
            }
        }
    }

    /**
     * Saves the specified form for a backup
     * 
     * @param formResponse
     *            The form response to save
     */
    @Transactional
    public void saveFormForBackup( FormResponse formResponse )
    {
        formResponse.setFromSave( Boolean.TRUE );

        save( formResponse );
        saveFormResponseSteps( formResponse );
    }

    /**
     * Removes the specified form's backup
     * 
     * @param formResponse
     *            The form response to remove
     */
    @Transactional
    public void removeFormBackup( FormResponse formResponse )
    {
        if ( formResponse.isFromSave( ) )
        {
            FormResponseHome.remove( formResponse.getId( ) );

            for ( FormQuestionResponse formQuestionResponse : FormQuestionResponseHome.getFormQuestionResponseListByFormResponse( formResponse.getId( ) ) )
            {
                FormQuestionResponseHome.remove( formQuestionResponse );
            }

            FormResponseStepHome.removeByFormResponse( formResponse.getId( ) );
        }
    }

    /**
     * Removes all backup form responses (from_save = 1) linked to a given Form,
     * including their question responses
     *
     * @param nIdForm
     *            The identifier of the form
     */
    @Transactional
    public void removeBackupsByForm( int nIdForm )
    {
        FormResponseFilter filter = new FormResponseFilter( );
        filter.setIdForm( List.of( nIdForm ) );
        filter.setFromSave( true );
        List<FormResponse> listBackups = FormResponseHome.getFormResponseByFilter( filter );

        for ( FormResponse backup : listBackups )
        {
            for ( FormQuestionResponse formQuestionResponse : FormQuestionResponseHome.getFormQuestionResponseListByFormResponse( backup.getId( ) ) )
            {
                FormQuestionResponseHome.remove( formQuestionResponse );
            }

            FormResponseStepHome.removeByFormResponse( backup.getId( ) );
            FormResponseHome.remove( backup.getId( ) );
        }
    }

    /**
     * Get the full children composite list of the given step
     * 
     * @param nIdStep
     *            The step primary key
     * @return the Html of the given step
     */
    public List<ICompositeDisplay> getStepCompositeList( int nIdStep )
    {
        StepDisplayTree displayTree = new StepDisplayTree( nIdStep );

        return displayTree.getCompositeList( );
    }

    /**
     * Get the right composite from the given formDisplay
     * 
     * @param formDisplay
     *            The formDisplay
     * @param formResponse
     *            the form response
     * @param nIterationNumber
     *            the iteration number
     * @return the right composite
     */
    public ICompositeDisplay formDisplayToComposite( FormDisplay formDisplay, FormResponse formResponse, int nIterationNumber )
    {
        ICompositeDisplay composite = null;
        if ( FormsConstants.COMPOSITE_GROUP_TYPE.equals( formDisplay.getCompositeType( ) ) )
        {
            composite = new CompositeGroupDisplay( formDisplay, formResponse, nIterationNumber );

        }
        else
            if ( FormsConstants.COMPOSITE_QUESTION_TYPE.equals( formDisplay.getCompositeType( ) ) )
            {
                composite = new CompositeQuestionDisplay( formDisplay, formResponse, nIterationNumber );
            }

        return composite;
    }

    /**
     * Remove a given Form, all its steps and composites, workflow resources. Also remove all the related formResponses, QuestionsResposes, EntryResponses and
     * entries.
     * 
     * @param nIdForm
     *            The identifier of the form to be deleted
     * @param adminUser
     *            the user
     */
    @Transactional
    public void removeForm( int nIdForm, AdminUser adminUser )
    {
        List<Step> listStep = StepHome.getStepsListByForm( nIdForm );

        for ( Step step : listStep )
        {
            _stepService.removeStep( step.getId( ) );
        }

        FormResponseHome.removeByForm( nIdForm );
        FormMessageHome.removeByForm( nIdForm );
        FormExportConfigHome.removeByForm( nIdForm );

        Form form = FormHome.findByPrimaryKey( nIdForm );
        int nIdWorkflow = form.getIdWorkflow( );
        if ( form.getLogo( ) != null )
        {
            FileHome.remove( form.getLogo( ).getIdFile( ) );
        }

        FormHome.remove( nIdForm );

        _formWorkflowService.removeResources( nIdWorkflow, nIdForm, adminUser );
    }

    /**
     * Check if a user is authorized to access a File from its given identifier
     * 
     * @param request
     *            The request to use to retrieve information of the current user
     * @param nIdResponse
     *            The identifier of the Response which have the file
     * @param nIdFile
     *            The identifier of the file to access
     * @return the boolean which tell if the user is authorize to access the given File or not
     */
    public boolean isFileAccessAuthorized( HttpServletRequest request, int nIdResponse, int nIdFile )
    {
        boolean bFileAccessAuthorized = Boolean.FALSE;

        Response response = ResponseHome.findByPrimaryKey( nIdResponse );
        if ( response != null && response.getEntry( ) != null && response.getFile( ) != null && response.getFile( ).getIdFile( ) == nIdFile )
        {
            Entry entryResponse = EntryHome.findByPrimaryKey( response.getEntry( ).getIdEntry( ) );
            IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( entryResponse );

            if ( ( entryTypeService instanceof AbstractEntryTypeFile || entryTypeService instanceof AbstractEntryTypeImage 
                   || entryTypeService instanceof AbstractEntryTypeGalleryImage ) && Form.RESOURCE_TYPE.equals( entryResponse.getResourceType( ) ) )
            {
                bFileAccessAuthorized = canUserAccessFile( request, entryResponse.getIdResource( ) );
            }
        }

        return bFileAccessAuthorized;
    }

    /**
     * Check if a user have all necessaries permissions to access to file in the formResponse details view
     * 
     * @param request
     *            The request to use to retrieve the user
     * @param nIdForm
     *            The identifier of the Form to use to check the permissions
     * @return true if the user can access File false if the user doesn't have necessary permissions
     */
    private boolean canUserAccessFile( HttpServletRequest request, int nIdForm )
    {
        boolean bUserAccessFile = Boolean.FALSE;

        AdminUser adminUser = AdminUserService.getAdminUser( request );
        if ( adminUser != null && adminUser.checkRight( MultiviewFormResponseDetailsJspBean.RIGHT_FORMS_MULTIVIEW ) )
        {
            Form form = FormHome.findByPrimaryKey( nIdForm );
            if ( form != null && AdminWorkgroupService.isAuthorized( form, (User) adminUser ) )
            {
                boolean bRbacModify = RBACService.isAuthorized( Form.RESOURCE_TYPE, Integer.toString( form.getId( ) ),
                        FormsResourceIdService.PERMISSION_MODIFY_FORM_RESPONSE, (User) adminUser );

                boolean bRbacManage = RBACService.isAuthorized( Form.RESOURCE_TYPE, Integer.toString( form.getId( ) ),
                        FormsResourceIdService.PERMISSION_MANAGE_FORM_RESPONSE, (User) adminUser );

                boolean bRbacView = RBACService.isAuthorized( Form.RESOURCE_TYPE, Integer.toString( form.getId( ) ),
                        FormsResourceIdService.PERMISSION_VIEW_FORM_RESPONSE, (User) adminUser );

                bUserAccessFile = bRbacModify || bRbacManage || bRbacView;
            }
        }

        return bUserAccessFile;
    }

    /**
     * Creates a {@code FormResponseManager} object from a back up
     * 
     * @param form
     *            The form
     * @param strUserGuid
     *            The user guid
     * @return the created {@code FormResponseManager} object
     */
    public FormResponseManager createFormResponseManagerFromBackUp( Form form, String strUserGuid )
    {
        FormResponseManager formResponseManager = null;
        List<FormResponse> listFormResponse = FormResponseHome.getFormResponseByGuidAndForm( strUserGuid, form.getId( ), true );
        if ( CollectionUtils.isNotEmpty( listFormResponse ) )
        {
            formResponseManager = new FormResponseManager( listFormResponse.get( 0 ) );
            formResponseManager.setIsResponseLoadedFromBackup(true);
        }
        else
        {
            formResponseManager = new FormResponseManager( form );
        }
        return formResponseManager;
    }

    // FORM RESPONSE CREATION
    /**
     * Fire the create event on given form Response
     * 
     * @param formResponse
     *            the form Response
     */
    public void fireFormResponseEventCreation( FormResponse formResponse )
    {
    	FormResponseEvent formResponseEvent = new FormResponseEvent( formResponse.getId( ) );
        
    	_formResponseEvent.select( FormResponseEvent.class, new TypeQualifier( EventAction.CREATE ) ).fireAsync( formResponseEvent );
    }

    /**
     * Fire the create event on all the form responses associated to given form
     * 
     * @param form
     *            The form
     */
    public void fireFormResponseEventCreation( Form form )
    {
    	List<FormResponse> listFormResponse = FormResponseHome.selectAllFormResponsesUncompleteByIdForm( form.getId( ) );

        for ( FormResponse formResponse : listFormResponse )
        {
            fireFormResponseEventCreation( formResponse );
        }
    }

    // FORM RESPONSE UPDATE
    /**
     * Fire the form response event update on given formResponse
     * 
     * @param formResponse
     *            the formResponse
     */
    public void fireFormResponseEventUpdate( FormResponse formResponse )
    {
        fireFormResponseEventUpdate( formResponse, true );
    }

    /**
     * Fire the form response event update on given formResponse
     *
     * @param formResponse
     *            the formResponse
     * @param updateDateFormResponse
     *            true if the update date of the form response must be updated
     */
    public void fireFormResponseEventUpdate( FormResponse formResponse, boolean updateDateFormResponse )
    {
        FormResponseEvent formResponseEvent = new FormResponseEvent( formResponse.getId( ), updateDateFormResponse );

        _formResponseEvent.select( FormResponseEvent.class, new TypeQualifier( EventAction.UPDATE ) ).fireAsync( formResponseEvent );
    }

    /**
     * Fire the update event on all the form responses associated to given form
     * 
     * @param form
     *            The form
     */
    public void fireFormResponseEventUpdate( Form form )
    {
    	List<FormResponse> listFormResponse = FormResponseHome.selectAllFormResponsesUncompleteByIdForm( form.getId( ) );

        for ( FormResponse formResponse : listFormResponse )
        {
            fireFormResponseEventUpdate( formResponse, false );
        }
    }

    // FORM RESPONSE DELETION

    /**
     * Fire the form response deletion event
     * 
     * @param formResponse
     *            the form response
     */
    public void fireFormResponseEventDelete( FormResponse formResponse )
    {
        FormResponseEvent formResponseEvent = new FormResponseEvent( formResponse.getId( ) );
        
    	_formResponseEvent.select( FormResponseEvent.class, new TypeQualifier( EventAction.REMOVE ) ).fireAsync( formResponseEvent );
    }

    /**
     * Fire the delete event of all the form responses associated to given form
     * 
     * @param form
     *            the form
     */
    public void fireFormResponseEventDelete( Form form )
    {
    	List<FormResponse> listFormResponse = FormResponseHome.selectAllFormResponsesUncompleteByIdForm( form.getId( ) );

        for ( FormResponse formResponse : listFormResponse )
        {
            fireFormResponseEventDelete( formResponse );
        }
    }
    
    /**
     * Save or update the field of the entry
     * @param entry
     * @param fieldName
     * @param title
     * @param value
     */
    public void saveOrUpdateField( Entry entry, String fieldName, String title, String value )
    {
        if ( entry.getFieldByCode( fieldName ) == null )
        {
            FieldHome.create( GenericAttributesUtils.createOrUpdateField( entry, fieldName, title, value ) );
        }
        else
        {
            FieldHome.update( GenericAttributesUtils.createOrUpdateField( entry, fieldName, title, value ) );
        }
    }

    /**
     * Check if responses exist for a composite (question or group of questions)
     * 
     * @param formDisplay
     *            the form display
     * @return true if responses exist, false otherwise
     */
    public boolean existCompositeResponses( FormDisplay formDisplay )
    {
        boolean existCompositeResponses = false;
        
        if ( CollectionUtils.isEmpty( FormResponseHome.selectAllFormResponsesUncompleteByIdForm( formDisplay.getFormId( ) ) ) )
        {
            return false;
        }
        
        if ( CompositeDisplayType.GROUP.getLabel( ).equalsIgnoreCase( formDisplay.getCompositeType( ) ) )
        {
            List<Question> questionsList = new ArrayList<>( );
            ICompositeDisplay composite = formDisplayToComposite( formDisplay, null, 0 );
            composite.addQuestions( questionsList );
            
            if ( CollectionUtils.isNotEmpty( questionsList ) )
            {
                List<Integer> idEntryList = questionsList.stream( ).map( Question::getIdEntry ).collect( Collectors.toList( ) );
                ResponseFilter responsefilter = new ResponseFilter( );
                responsefilter.setListIdEntry( idEntryList );
                
                existCompositeResponses = existsFilledResponse( responsefilter );
            }
        }
        else if ( CompositeDisplayType.QUESTION.getLabel( ).equalsIgnoreCase( formDisplay.getCompositeType( ) ) )
        {
            Question question = QuestionHome.findByPrimaryKey( formDisplay.getCompositeId( ) );
            
            if ( question != null && question.getIdEntry( ) > 0 )
            {
                ResponseFilter responsefilter = new ResponseFilter( );
                responsefilter.setIdEntry( question.getIdEntry( ) );
                
                existCompositeResponses = existsFilledResponse( responsefilter );
            }
        }
        
        return existCompositeResponses;
    }
    
    /**
     * Check if any response value filled exists for a question
     * 
     * @param responseFilter
     *            the response filter
     * @return true if a response filled exist, false otherwise
     */
    private boolean existsFilledResponse( ResponseFilter responseFilter )
    {
    	boolean existsFilledResponse = false ;
    	
    	List<Response> responseList = ResponseHome.getResponseList( responseFilter );

		if ( CollectionUtils.isNotEmpty( responseList ) 
				&& responseList.stream( ).anyMatch( response -> StringUtils.isNotBlank( response.getResponseValue( ) ) ) )
		{
			existsFilledResponse = true;
		}
    	
    	return existsFilledResponse;
    }
}
