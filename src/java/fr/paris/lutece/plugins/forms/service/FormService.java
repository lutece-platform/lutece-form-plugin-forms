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
package fr.paris.lutece.plugins.forms.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.transaction.annotation.Transactional;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormMessageHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponseStep;
import fr.paris.lutece.plugins.forms.business.FormResponseStepHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.business.export.FormExportConfigHome;
import fr.paris.lutece.plugins.forms.service.workflow.IFormWorkflowService;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
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
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeFile;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeGalleryImage;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeImage;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.portal.business.event.ResourceEvent;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.event.ResourceEventManager;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.util.sql.TransactionManager;

/**
 * This is the service class related to the form
 */
public class FormService
{
    public static final String BEAN_NAME = "forms.formService";

    @Inject
    private IFormWorkflowService _formWorkflowService;

    /**
     * Saves the specified form
     * 
     * @param form
     *            the form to save
     * @param formResponse
     *            the form response to save
     */
    public void saveForm( Form form, FormResponse formResponse )
    {
        TransactionManager.beginTransaction( FormsPlugin.getPlugin( ) );

        try
        {
            formResponse.setFromSave( Boolean.FALSE );

            filterFinalSteps( formResponse );
            saveFormResponse( formResponse );
            saveFormResponseSteps( formResponse );
            TransactionManager.commitTransaction( FormsPlugin.getPlugin( ) );
        }
        catch( Exception e )
        {
            TransactionManager.rollBack( FormsPlugin.getPlugin( ) );
            throw new AppException( e.getMessage( ), e );
        }
        fireFormResponseEventCreation( formResponse );
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
     * Saves the form response
     * 
     * @param formResponse
     *            the form response to save
     */
    private void saveFormResponse( FormResponse formResponse )
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
        fireFormResponseEventUpdate( formResponse );
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
                IEntryDataService dataService = EntryServiceManager.getInstance( ).getEntryDataService( question.getEntry( ).getEntryType( ) );
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
    @Transactional( FormsConstants.BEAN_TRANSACTION_MANAGER )
    public void saveFormForBackup( FormResponse formResponse )
    {
        formResponse.setFromSave( Boolean.TRUE );

        saveFormResponse( formResponse );
        saveFormResponseSteps( formResponse );
    }

    /**
     * Removes the specified form's backup
     * 
     * @param formResponse
     *            The form response to remove
     */
    @Transactional( FormsConstants.BEAN_TRANSACTION_MANAGER )
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
    @Transactional( FormsConstants.BEAN_TRANSACTION_MANAGER )
    public void removeForm( int nIdForm, AdminUser adminUser )
    {
        StepService stepService = SpringContextService.getBean( StepService.BEAN_NAME );

        List<Step> listStep = StepHome.getStepsListByForm( nIdForm );

        for ( Step step : listStep )
        {
            stepService.removeStep( step.getId( ) );
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
        ResourceEvent formResponseEvent = new ResourceEvent( );
        formResponseEvent.setIdResource( String.valueOf( formResponse.getId( ) ) );
        formResponseEvent.setTypeResource( FormResponse.RESOURCE_TYPE );

        ResourceEventManager.fireAddedResource( formResponseEvent );
    }

    /**
     * Fire the create event on all the form responses associated to given form
     * 
     * @param form
     *            The form
     */
    public void fireFormResponseEventCreation( Form form )
    {
        new Thread( ( ) -> {
            List<FormResponse> listFormResponse = FormResponseHome.selectAllFormResponsesUncompleteByIdForm( form.getId( ) );

            for ( FormResponse formResponse : listFormResponse )
            {
                fireFormResponseEventCreation( formResponse );
            }
        } ).start( );
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
        ResourceEvent formResponseEvent = new ResourceEvent( );
        formResponseEvent.setIdResource( String.valueOf( formResponse.getId( ) ) );
        formResponseEvent.setTypeResource( FormResponse.RESOURCE_TYPE );

        ResourceEventManager.fireUpdatedResource( formResponseEvent );
    }

    /**
     * Fire the update event on all the form responses associated to given form
     * 
     * @param form
     *            The form
     */
    public void fireFormResponseEventUpdate( Form form )
    {
        new Thread( ( ) -> {
            List<FormResponse> listFormResponse = FormResponseHome.selectAllFormResponsesUncompleteByIdForm( form.getId( ) );

            for ( FormResponse formResponse : listFormResponse )
            {
                fireFormResponseEventUpdate( formResponse );
            }
        } ).start( );
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
        ResourceEvent formResponseEvent = new ResourceEvent( );
        formResponseEvent.setIdResource( String.valueOf( formResponse.getId( ) ) );
        formResponseEvent.setTypeResource( FormResponse.RESOURCE_TYPE );

        ResourceEventManager.fireDeletedResource( formResponseEvent );
    }

    /**
     * Fire the delete event of all the form responses associated to given form
     * 
     * @param form
     *            the form
     */
    public void fireFormResponseEventDelete( Form form )
    {
        new Thread( ( ) -> {
            List<FormResponse> listFormResponse = FormResponseHome.selectAllFormResponsesUncompleteByIdForm( form.getId( ) );

            for ( FormResponse formResponse : listFormResponse )
            {
                fireFormResponseEventDelete( formResponse );
            }
        } ).start( );
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
}
