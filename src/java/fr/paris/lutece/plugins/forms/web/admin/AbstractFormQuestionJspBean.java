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
package fr.paris.lutece.plugins.forms.web.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.Group;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.service.IFormDatabaseService;
import fr.paris.lutece.plugins.forms.service.IFormDisplayService;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeCheckBox;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeComment;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeRadioButton;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeSelect;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.util.FormsEntryUtils;
import fr.paris.lutece.plugins.forms.web.entrytype.EntryTypeCommentDisplayService;
import fr.paris.lutece.plugins.forms.web.exception.CodeAlreadyExistsException;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.referencelist.service.ReferenceListService;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.IFileStoreServiceProvider;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.util.ReferenceList;

public abstract class AbstractFormQuestionJspBean extends AbstractJspBean
{
    private static final long serialVersionUID = -8828358457153413756L;

    private static final Class<?> [ ] ENTRY_TYPE_USER_REF_LIT = {
            EntryTypeCheckBox.class, EntryTypeRadioButton.class, EntryTypeSelect.class
    };

    // Actions
    protected static final String ACTION_CREATE_QUESTION = "createQuestion";
    protected static final String ACTION_CREATE_QUESTION_AND_MANAGE_ENTRIES = "createQuestionAndManageEntries";
    protected static final String ACTION_MODIFY_QUESTION = "modifyQuestion";
    protected static final String ACTION_MOVE_COMPOSITE = "moveComposite";
    protected static final String ACTION_REMOVE_COMPOSITE = "removeComposite";
    protected static final String ACTION_CREATE_GROUP = "createGroup";
    protected static final String ACTION_MODIFY_GROUP = "modifyGroup";
    protected static final String ACTION_DUPLICATE_QUESTION = "duplicateQuestion";
    protected static final String ACTION_SAVE_QUESTION = "saveQuestion";

    // Views
    protected static final String VIEW_CREATE_GROUP = "createGroup";
    protected static final String VIEW_MODIFY_GROUP = "modifyGroup";
    protected static final String VIEW_MOVE_COMPOSITE = "moveComposite";
    protected static final String VIEW_CREATE_QUESTION = "createQuestion";
    protected static final String VIEW_MODIFY_QUESTION = "modifyQuestion";
    protected static final String VIEW_CONFIRM_REMOVE_COMPOSITE = "getConfirmRemoveComposite";

    // Markers
    protected static final String MARK_ACTION = "action";
    private static final String MARK_WEBAPP_URL = "webapp_url";
    protected static final String MARK_LOCALE = "locale";
    private static final String MARK_LIST = "list";
    private static final String MARK_ENTRY_TYPE_SERVICE = "entryTypeService";
    private static final String MARK_LIST_PARAM_DEFAULT_VALUES = "list_param_default_values";
    private static final String MARK_GROUP_VALIDATED = "groupValidated";
    private static final String MARK_STEP_VALIDATED = "stepValidated";
    protected static final String MARK_ADD_FILE_COMMENT = "canAddFile";

    // Parameters
    private static final String PARAMETER_VALUE_VALIDATE_STEP = "validateStep";
    private static final String PARAMETER_VALUE_VALIDATE_GROUP = "validateGroup";

    // Errors Messages
    private static final String ERROR_ITERATION_NUMBER = "forms.error.group.iterationNumber";
    private static final String ERROR_QUESTION_NOT_CREATED = "forms.error.question.notCreated";
    private static final String ERROR_GROUP_NOT_CREATED = "forms.error.group.notCreated";
    private static final String ERROR_GROUP_NOT_UPDATED = "forms.error.group.notUpdated";
    protected static final String ERROR_STEP_OR_GROUP_NOT_VALIDATED = "forms.error.moveComposite.stepOrGroup.notvalidated";
    protected static final String ERROR_OCCURED_MOVING_COMPOSITE = "forms.error.moveComposite.notCompleted";
    protected static final String ERROR_QUESTION_CODE_ALREADY_EXISTS = "forms.error.question.codeAlreadyExists";
    protected static final String ERROR_CODE_EXISTS = " Provided code already exists ";

    // Warning messages
    protected static final String WARNING_CONFIRM_REMOVE_QUESTION = "forms.warning.deleteComposite.confirmRemoveQuestion";
    protected static final String WARNING_CONFIRM_REMOVE_GROUP_ANY_QUESTIONS = "forms.warning.deleteComposite.confirmRemoveGroup";

    // Infos Messages
    protected static final String INFO_QUESTION_DUPLICATED = "forms.info.question.duplicated";
    private static final String INFO_GROUP_UPDATED = "forms.info.group.updated";
    private static final String INFO_GROUP_CREATED = "forms.info.group.created";
    protected static final String INFO_QUESTION_CREATED = "forms.info.question.created";
    protected static final String INFO_QUESTION_UPDATED = "forms.info.question.updated";
    protected static final String INFO_DELETE_COMPOSITE_SUCCESSFUL = "forms.info.deleteComposite.successful";
    protected static final String INFO_MOVE_COMPOSITE_SUCCESSFUL = "forms.info.moveComposite.successful";

    // Properties
    private static final String PROPERTY_CREATE_COMMENT_TITLE = "forms.create_Question.titleComment";
    private static final String PROPERTY_CREATE_QUESTION_TITLE = "forms.create_Question.titleQuestion";
    private static final String PROPERTY_MODIFY_COMMENT_TITLE = "forms.modifyEntry.titleComment";
    private static final String PROPERTY_MODIFY_QUESTION_TITLE = "forms.modifyEntry.titleQuestion";
    private static final String PROPERTY_MOVE_GROUP_TITLE = "forms.moveComposite.group.title";
    private static final String PROPERTY_MOVE_QUESTION_TITLE = "forms.moveComposite.question.title";

    private static final String GROUP_VALIDATION_ATTRIBUTES_PREFIX = "forms.model.entity.group.attribute.";
    private static final String ENTRY_COMMENT_TITLE = "forms.manage_questions.type.comment.title";

    // Others
    protected static final int INTEGER_MINUS_ONE = -1;

    protected IFileStoreServiceProvider _fileStoreProvider = FileService.getInstance( ).getFileStoreServiceProvider( "formsDatabaseFileStoreProvider" );

    private IFormDatabaseService _formDatabaseService;
    private IFormDisplayService _formDisplayService;

    protected Group _group;
    protected Step _step;
    protected Entry _entry;
    protected Question _question;
    protected FormDisplay _formDisplay;
    protected int _nIdParentSelected = 0;
    protected int _nIdStepTarget;
    protected int _nIdParentTarget;

    /**
     * Perform the Question creation with its Entry
     * 
     * @param request
     *            The HTTP request
     * @return The URL to display error message after performing the action or null if no error occurred
     */
    protected String processQuestionCreation( HttpServletRequest request, String viewManageQuestions ) throws CodeAlreadyExistsException
    {
        int nIdStep = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );
        int nParentGroup = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY_PARENT ), 0 );
        int nIdType = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_ENTRY_TYPE ) );

        if ( ( _step == null ) || nIdStep != _step.getId( ) )
        {
            _step = getFormDatabaseService( ).findStepByPrimaryKey( nIdStep );
        }

        _entry = FormsEntryUtils.createEntryByType( nIdType );

        if ( _entry == null )
        {
            addError( ERROR_QUESTION_NOT_CREATED, getLocale( ) );
            return redirect( request, viewManageQuestions, FormsConstants.PARAMETER_ID_STEP, nIdStep );
        }

        String strError = EntryTypeServiceManager.getEntryTypeService( _entry ).getRequestData( _entry, request, getLocale( ) );

        if ( strError != null )
        {
            return strError;
        }

        _entry.setIdResource( _step.getIdForm( ) );
        _entry.setResourceType( Form.RESOURCE_TYPE );
        getFormDatabaseService( ).createEntry( _entry );

        // If the entry code is empty, provide a new one
        if ( StringUtils.isEmpty( _entry.getCode( ) ) )
        {
            _entry.setCode( "question_" + _entry.getIdEntry( ) );
        }

        if ( checkCodeAlreadyExists( _entry.getCode( ), _step.getIdForm( ), _entry.getIdEntry( ) ) )
        {
            throw new CodeAlreadyExistsException( _entry.getCode( ) );
        }

        getFormDatabaseService( ).updateEntry( _entry );

        if ( _entry.getFields( ) != null )
        {
            for ( Field field : _entry.getFields( ) )
            {
                field.setParentEntry( _entry );
                getFormDatabaseService( ).createField( field );
            }
        }

        _question = new Question( );
        String strTitle = Boolean.TRUE.equals( _entry.getEntryType( ).getComment( ) ) ? I18nService.getLocalizedString( ENTRY_COMMENT_TITLE, getLocale( ) )
                : _entry.getTitle( );
        _question.setTitle( strTitle );
        _question.setColumnTitle( strTitle );
        _question.setCode( _entry.getCode( ) );
        _question.setDescription( _entry.getComment( ) );
        _question.setIdEntry( _entry.getIdEntry( ) );
        _question.setIdStep( nIdStep );

        getFormDatabaseService( ).createQuestion( _question );

        int nDisplayDepth = getFormDisplayService( ).getDisplayDepthFromParent( nParentGroup );

        if ( _question.getId( ) != INTEGER_MINUS_ONE )
        {
            FormDisplay formDisplay = new FormDisplay( );
            formDisplay.setFormId( _step.getIdForm( ) );
            formDisplay.setStepId( nIdStep );
            formDisplay.setParentId( nParentGroup );
            formDisplay.setCompositeId( _question.getId( ) );
            formDisplay.setCompositeType( CompositeDisplayType.QUESTION.getLabel( ) );
            formDisplay.setDepth( nDisplayDepth );
            getFormDatabaseService( ).createFormDisplay( formDisplay );
        }
        return null;
    }

    /**
     * Perform the entry modification
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    protected String processQuestionUpdate( HttpServletRequest request ) throws CodeAlreadyExistsException
    {
        String strIdStep = request.getParameter( FormsConstants.PARAMETER_ID_STEP );
        int nIdStep = NumberUtils.toInt( strIdStep, INTEGER_MINUS_ONE );

        if ( _step == null || _step.getId( ) != nIdStep )
        {
            _step = getFormDatabaseService( ).findStepByPrimaryKey( nIdStep );
        }

        String strIdQuestion = request.getParameter( FormsConstants.PARAMETER_ID_QUESTION );
        int nIdQuestion = NumberUtils.toInt( strIdQuestion, INTEGER_MINUS_ONE );
        if ( _question == null || _question.getId( ) != nIdQuestion )
        {
            _question = getFormDatabaseService( ).findQuestionByPrimaryKey( nIdQuestion );
        }

        int nIdEntry = _question.getIdEntry( );
        _entry = getFormDatabaseService( ).findEntryByPrimaryKey( nIdEntry );

        String strError = EntryTypeServiceManager.getEntryTypeService( _entry ).getRequestData( _entry, request, getLocale( ) );

        if ( strError != null )
        {
            return strError;
        }

        if ( checkCodeAlreadyExists( _entry.getCode( ), _step.getIdForm( ), _entry.getIdEntry( ) ) )
        {
            throw new CodeAlreadyExistsException( _entry.getCode( ) );
        }

        getFormDatabaseService( ).updateEntry( _entry );

        if ( _entry.getFields( ) != null )
        {
            for ( Field field : _entry.getFields( ) )
            {
                // Check if the field already exists in the database
                Field fieldStored = getFormDatabaseService( ).findFieldByPrimaryKey( field.getIdField( ) );

                if ( fieldStored != null )
                {
                    // If it exists, update
                    getFormDatabaseService( ).updateField( field );
                }
                else
                {
                    // If it does not exist, create
                    getFormDatabaseService( ).createField( field );
                }
            }
        }

        String strTitle = Boolean.TRUE.equals( _entry.getEntryType( ).getComment( ) ) ? I18nService.getLocalizedString( ENTRY_COMMENT_TITLE, getLocale( ) )
                : _entry.getTitle( );
        _question.setTitle( strTitle );
        _question.setCode( _entry.getCode( ) );
        _question.setDescription( _entry.getComment( ) );
        _question.setMultiviewColumnOrder( NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_MULTIVIEW_ORDER ), 0 ) );
        getFormDatabaseService( ).updateQuestion( _question );
        return null;

    }

    protected String createGroup( HttpServletRequest request, String viewReturnOk )
    {
        int nIdStep = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), FormsConstants.DEFAULT_ID_VALUE );
        int nParentGroup = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY_PARENT ), 0 );

        if ( _step == null || nIdStep != _step.getId( ) )
        {
            _step = getFormDatabaseService( ).findStepByPrimaryKey( nIdStep );
        }

        _group = ( _group != null ) ? _group : new Group( );
        populate( _group, request );

        if ( !validateGroup( ) )
        {
            return redirect( request, VIEW_CREATE_GROUP, FormsConstants.PARAMETER_ID_DISPLAY_PARENT, nParentGroup );
        }

        getFormDatabaseService( ).createGroup( _group );

        int nDisplayDepth = getFormDisplayService( ).getDisplayDepthFromParent( nParentGroup );

        if ( _group.getId( ) != INTEGER_MINUS_ONE )
        {
            FormDisplay templateDisplay = new FormDisplay( );
            templateDisplay.setFormId( _step.getIdForm( ) );
            templateDisplay.setStepId( nIdStep );
            templateDisplay.setParentId( nParentGroup );
            templateDisplay.setCompositeId( _group.getId( ) );
            templateDisplay.setCompositeType( CompositeDisplayType.GROUP.getLabel( ) );
            templateDisplay.setDepth( nDisplayDepth );
            getFormDatabaseService( ).createFormDisplay( templateDisplay );

            if ( templateDisplay.getId( ) == INTEGER_MINUS_ONE )
            {
                addError( ERROR_GROUP_NOT_CREATED, getLocale( ) );
            }
        }
        else
        {
            addError( ERROR_GROUP_NOT_CREATED, getLocale( ) );
        }

        addInfo( INFO_GROUP_CREATED, getLocale( ) );
        return redirect( request, viewReturnOk, FormsConstants.PARAMETER_ID_STEP, _step.getId( ) );
    }

    protected String modifyGroup( HttpServletRequest request, String viewReturnOk )
    {
        int nIdStep = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );

        if ( _step == null || nIdStep != _step.getId( ) )
        {
            _step = getFormDatabaseService( ).findStepByPrimaryKey( nIdStep );
        }

        _group = _group != null ? _group : new Group( );
        populate( _group, request );

        if ( !validateGroup( ) )
        {
            return redirectView( request, VIEW_MODIFY_GROUP );
        }

        getFormDatabaseService( ).updateGroup( _group );

        if ( _group.getId( ) == INTEGER_MINUS_ONE )
        {
            addError( ERROR_GROUP_NOT_UPDATED, getLocale( ) );
        }
        else
        {
            addInfo( INFO_GROUP_UPDATED, getLocale( ) );
        }

        return redirect( request, viewReturnOk, FormsConstants.PARAMETER_ID_STEP, _step.getId( ) );
    }

    protected Map<String, Object> initCreateGroupModel( HttpServletRequest request )
    {
        int nIdStep = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), FormsConstants.DEFAULT_ID_VALUE );
        _nIdParentSelected = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY_PARENT ), 0 );

        if ( ( _step == null ) || ( nIdStep != FormsConstants.DEFAULT_ID_VALUE && nIdStep != _step.getId( ) ) )
        {
            _step = getFormDatabaseService( ).findStepByPrimaryKey( nIdStep );
        }

        if ( ( _step == null ) )
        {
            return null;
        }

        _group = new Group( );
        _group.setIterationMin( NumberUtils.INTEGER_ONE );
        _group.setIterationMax( NumberUtils.INTEGER_ONE );

        Map<String, Object> model = getModel( );
        model.put( FormsConstants.MARK_STEP, _step );
        model.put( FormsConstants.MARK_GROUP, _group );
        model.put( FormsConstants.MARK_ID_PARENT, _nIdParentSelected );

        return model;
    }

    protected Map<String, Object> initModifyGroupModel( HttpServletRequest request )
    {
        int nIdStep = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), FormsConstants.DEFAULT_ID_VALUE );
        int nIdGroup = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_GROUP ), FormsConstants.DEFAULT_ID_VALUE );

        if ( _step == null || ( nIdStep != FormsConstants.DEFAULT_ID_VALUE && nIdStep != _step.getId( ) ) )
        {
            _step = getFormDatabaseService( ).findStepByPrimaryKey( nIdStep );
        }

        if ( _step == null )
        {
            return null;
        }

        if ( _group == null || ( nIdGroup != FormsConstants.DEFAULT_ID_VALUE && _group.getId( ) != nIdGroup ) )
        {
            _group = getFormDatabaseService( ).findGroupByPrimaryKey( nIdGroup );
        }

        Map<String, Object> model = getModel( );
        model.put( FormsConstants.MARK_STEP, _step );
        model.put( FormsConstants.MARK_GROUP, _group );

        return model;
    }

    protected Map<String, Object> initCreateQuestionModel( HttpServletRequest request )
    {
        int nIdStep = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );
        int nIdTypeEntry = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_BUTTON_TYPE_ENTRY ) );
        _nIdParentSelected = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY_PARENT ), 0 );

        _step = getFormDatabaseService( ).findStepByPrimaryKey( nIdStep );
        if ( _step == null )
        {
            return null;
        }

        _entry = FormsEntryUtils.createEntryByType( nIdTypeEntry );

        if ( _entry == null )
        {
            return null;
        }

        _entry.setIdResource( _step.getIdForm( ) );
        _entry.setResourceType( Form.RESOURCE_TYPE );

        ReferenceList listParamDefaultValues = new ReferenceList( );

        IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( _entry );

        Map<String, Object> model = new HashMap<>( );
        model.put( FormsConstants.MARK_ENTRY, _entry );
        model.put( FormsConstants.MARK_STEP, _step );
        model.put( FormsConstants.MARK_ID_PARENT, _nIdParentSelected );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage( ) );
        model.put( MARK_LIST_PARAM_DEFAULT_VALUES, listParamDefaultValues );
        model.put( MARK_ENTRY_TYPE_SERVICE, entryTypeService );

        if ( Boolean.TRUE.equals( _entry.getEntryType( ).getComment( ) ) )
        {
            setPageTitleProperty( PROPERTY_CREATE_COMMENT_TITLE );
        }
        else
        {
            setPageTitleProperty( PROPERTY_CREATE_QUESTION_TITLE );
        }

        model.put( FormsConstants.MARK_ANONYMIZATION_HELP, entryTypeService.getAnonymizationHelpMessage( request.getLocale( ) ) );

        if ( Arrays.asList( ENTRY_TYPE_USER_REF_LIT ).contains( entryTypeService.getClass( ) ) )
        {
            model.put( FormsConstants.MARK_REFERENCE_LIST_SELECT, ReferenceListService.getInstance( ).getReferencesList( ) );
        }
        return model;
    }

    protected Map<String, Object> initModifyQuestionModel( HttpServletRequest request )
    {
        String strIdStep = request.getParameter( FormsConstants.PARAMETER_ID_STEP );

        if ( StringUtils.isNotEmpty( strIdStep ) )
        {
            int nIdStep = Integer.parseInt( strIdStep );

            if ( ( _step == null ) || ( _step.getId( ) != nIdStep ) )
            {
                _step = getFormDatabaseService( ).findStepByPrimaryKey( nIdStep );
            }
        }

        String strIdQuestion = request.getParameter( FormsConstants.PARAMETER_ID_QUESTION );
        int nIdQuestion = INTEGER_MINUS_ONE;

        if ( StringUtils.isNotEmpty( strIdQuestion ) )
        {
            nIdQuestion = Integer.parseInt( strIdQuestion );
        }

        _question = getFormDatabaseService( ).findQuestionByPrimaryKey( nIdQuestion );

        _entry = getFormDatabaseService( ).findEntryByPrimaryKey( _question.getIdEntry( ) );

        List<Field> listField = new ArrayList<>( _entry.getFields( ).size( ) );

        for ( Field field : _entry.getFields( ) )
        {
            field = getFormDatabaseService( ).findFieldByPrimaryKey( field.getIdField( ) );
            listField.add( field );
        }

        _entry.setFields( listField );

        IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( _entry );

        Map<String, Object> model = new HashMap<>( );
        model.put( FormsConstants.MARK_ENTRY, _entry );
        model.put( FormsConstants.MARK_STEP, _step );
        model.put( FormsConstants.MARK_QUESTION, _question );

        model.put( MARK_LIST, _entry.getFields( ) );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage( ) );
        model.put( MARK_ENTRY_TYPE_SERVICE, EntryTypeServiceManager.getEntryTypeService( _entry ) );

        if ( Boolean.TRUE.equals( _entry.getEntryType( ).getComment( ) ) )
        {
            setPageTitleProperty( PROPERTY_MODIFY_COMMENT_TITLE );
        }
        else
        {
            setPageTitleProperty( PROPERTY_MODIFY_QUESTION_TITLE );
        }

        model.put( FormsConstants.MARK_ANONYMIZATION_HELP, entryTypeService.getAnonymizationHelpMessage( request.getLocale( ) ) );

        if ( entryTypeService instanceof EntryTypeComment )
        {
            Field fieldFile = _entry.getFieldByCode( IEntryTypeService.FIELD_DOWNLOADABLE_FILE );
            if ( fieldFile != null )
            {
                Map<String, String> additionnalData = new HashMap<>( );
                additionnalData.put( FileService.PARAMETER_RESOURCE_ID, String.valueOf( _entry.getIdResource( ) ) );
                additionnalData.put( FileService.PARAMETER_RESOURCE_TYPE, Form.RESOURCE_TYPE );
                additionnalData.put( FileService.PARAMETER_PROVIDER, _fileStoreProvider.getName( ) );

                model.put( EntryTypeCommentDisplayService.MARK_FILENAME, fieldFile.getTitle( ) );
                model.put( EntryTypeCommentDisplayService.MARK_URL_DOWNLOAD_BO,
                        _fileStoreProvider.getFileDownloadUrlBO( fieldFile.getValue( ), additionnalData ) );
            }
        }
        if ( Arrays.asList( ENTRY_TYPE_USER_REF_LIT ).contains( entryTypeService.getClass( ) ) )
        {
            model.put( FormsConstants.MARK_REFERENCE_LIST_SELECT, ReferenceListService.getInstance( ).getReferencesList( ) );
        }

        return model;
    }

    protected Map<String, Object> initMoveCompositeModel( HttpServletRequest request )
    {
        Map<String, Object> model = getModel( );

        int nIdDisplay = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY ), INTEGER_MINUS_ONE );
        if ( nIdDisplay == INTEGER_MINUS_ONE )
        {
            return null;
        }

        if ( _formDisplay == null || _formDisplay.getId( ) != nIdDisplay )
        {
            _formDisplay = getFormDatabaseService( ).findDisplayByPrimaryKey( nIdDisplay );
        }

        model.put( FormsConstants.MARK_STEP, _step );
        model.put( FormsConstants.MARK_DISPLAY, _formDisplay );
        model.put( FormsConstants.MARK_DISPLAY_TITLE, getFormDisplayService( ).getDisplayTitle( _formDisplay ) );
        if ( _formDisplay.getCompositeType( ).equalsIgnoreCase( CompositeDisplayType.GROUP.getLabel( ) ) )
        {
            setPageTitleProperty( PROPERTY_MOVE_GROUP_TITLE );
        }
        else
            if ( _formDisplay.getCompositeType( ).equalsIgnoreCase( CompositeDisplayType.QUESTION.getLabel( ) ) )
            {
                setPageTitleProperty( PROPERTY_MOVE_QUESTION_TITLE );
            }

        boolean bStepValidated = false;
        boolean bGroupValidated = false;
        String strIsStepValidated = request.getParameter( FormsConstants.PARAMETER_STEP_VALIDATED );
        if ( StringUtils.isNotBlank( strIsStepValidated ) )
        {
            bStepValidated = BooleanUtils.toBoolean( strIsStepValidated );
        }

        String strIsGroupValidated = request.getParameter( FormsConstants.PARAMETER_GROUP_VALIDATED );
        if ( StringUtils.isNotBlank( strIsGroupValidated ) )
        {
            bGroupValidated = BooleanUtils.toBoolean( strIsGroupValidated );
        }

        String strValidateButtonValue = request.getParameter( "view_moveComposite" );
        boolean bIsValidationButtonUsed = StringUtils.isNotBlank( strValidateButtonValue );

        if ( bIsValidationButtonUsed )
        {

            if ( PARAMETER_VALUE_VALIDATE_STEP.equals( strValidateButtonValue ) )
            {
                bStepValidated = true;
                bGroupValidated = false;
            }
            else
                if ( PARAMETER_VALUE_VALIDATE_GROUP.equals( strValidateButtonValue ) && bStepValidated )
                {
                    bGroupValidated = true;
                }
        }
        else
        {
            bStepValidated = true;
            bGroupValidated = true;
        }

        int nIdStepTarget = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), -1 );
        if ( bStepValidated )
        {
            if ( nIdStepTarget == INTEGER_MINUS_ONE )
            {
                nIdStepTarget = _formDisplay.getStepId( );
            }
            _nIdStepTarget = nIdStepTarget;
        }

        if ( nIdStepTarget == INTEGER_MINUS_ONE )
        {
            return null;

        }

        int nTargetPosition = _formDisplay.getDisplayOrder( );
        int nIdParentTarget = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_PARENT ), -1 );
        if ( bGroupValidated )
        {
            if ( nIdParentTarget == INTEGER_MINUS_ONE )
            {
                nIdParentTarget = _formDisplay.getParentId( );
            }
            _nIdParentTarget = nIdParentTarget;
        }

        ReferenceList listStepGroups = getFormDisplayService( ).getTargetGroupDisplayListByStep( nIdStepTarget, _formDisplay, getLocale( ) );

        boolean bMoveToDifferentGroup = ( ( _formDisplay.getParentId( ) != nIdParentTarget ) || ( _formDisplay.getStepId( ) != nIdStepTarget ) );
        ReferenceList listAvailablePositionsInParentGroup = getFormDisplayService( ).getlistAvailablePositionsInGroup( nIdStepTarget, nIdParentTarget,
                bMoveToDifferentGroup, getLocale( ) );

        if ( bIsValidationButtonUsed )
        {
            nTargetPosition = listAvailablePositionsInParentGroup.size( );
        }

        model.put( FormsConstants.MARK_ID_STEP, bStepValidated ? nIdStepTarget : _formDisplay.getStepId( ) );
        model.put( FormsConstants.MARK_LIST_GROUPS, listStepGroups );
        model.put( FormsConstants.MARK_LIST_AVAILABLE_POSITIONS, listAvailablePositionsInParentGroup );
        model.put( MARK_STEP_VALIDATED, bStepValidated );
        model.put( MARK_GROUP_VALIDATED, bGroupValidated );
        model.put( FormsConstants.MARK_DISPLAY_ORDER, nTargetPosition );
        model.put( FormsConstants.MARK_ID_STEP, bStepValidated ? nIdStepTarget : _formDisplay.getStepId( ) );
        model.put( FormsConstants.MARK_ID_PARENT, bGroupValidated ? nIdParentTarget : 0 );

        return model;
    }

    /**
     * Perform the group bean validation
     * 
     * @return The boolean which indicate if a group is valid
     */
    private boolean validateGroup( )
    {
        boolean bValidGroup = true;

        if ( _group.getIterationMax( ) != 0 && _group.getIterationMin( ) > _group.getIterationMax( ) )
        {
            bValidGroup = false;
            addError( ERROR_ITERATION_NUMBER, getLocale( ) );
        }

        if ( !validateBean( _group, GROUP_VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            bValidGroup = false;
        }

        return bValidGroup;
    }

    /**
     * Process the FormDisplay move: Update the idParent, idStep and depth of the display and all its descendants Rebuild the Position values of the Display and
     * all its sibling inside Parent If move to a different group, rebuild the positions sequence within the origin group
     * 
     * @param formDisplayToMove
     *            the FormDisplay to be moved
     * 
     * @param nIdStepTarget
     *            the target Step identifier
     * 
     * @param nIdParentTarget
     *            the target Parent Display identifier (0 if root level of the step )
     * 
     * @param nDisplayOrderTarget
     *            the target position within parent
     */
    protected void moveDisplay( FormDisplay formDisplayToMove, int nIdStepTarget, int nIdParentTarget, int nDisplayOrderTarget )
    {
        int nIdOriginStep = _formDisplay.getStepId( );
        int nIdOriginParent = _formDisplay.getParentId( );
        int nOriginDisplayOrder = _formDisplay.getDisplayOrder( );

        List<FormDisplay> listDisplayInTargetGroup = getFormDatabaseService( ).getFormDisplayListByParent( nIdStepTarget, nIdParentTarget );

        formDisplayToMove.setParentId( nIdParentTarget );

        int nTargetDepth = getFormDisplayService( ).getDisplayDepthFromParent( nIdParentTarget );
        // update the idStep and depth of the display and all its descendants
        getFormDisplayService( ).setChildrenDisplayDepthAndStep( formDisplayToMove, nTargetDepth, nIdStepTarget );

        // Rebuild the Position values of the Display and all its sibling inside
        // Parent
        if ( !listDisplayInTargetGroup.isEmpty( ) )
        {
            // If move inside same group, first remove the Display from the list
            // before adding it at the right index
            if ( ( nIdOriginStep == nIdStepTarget && nIdOriginParent == nIdParentTarget ) )
            {
                listDisplayInTargetGroup.remove( nOriginDisplayOrder - 1 );
            }
            listDisplayInTargetGroup.add( nDisplayOrderTarget - 1, formDisplayToMove );

            getFormDisplayService( ).rebuildDisplayPositionSequence( listDisplayInTargetGroup );
        }

        // If move to a different group, rebuild the positions sequence within
        // the origin group
        if ( ( nIdOriginParent != nIdParentTarget ) || ( nIdOriginStep != nIdStepTarget ) )
        {
            List<FormDisplay> listDisplayInOriginGroup = getFormDatabaseService( ).getFormDisplayListByParent( nIdOriginStep, nIdOriginParent );
            getFormDisplayService( ).rebuildDisplayPositionSequence( listDisplayInOriginGroup );
        }
    }

    /**
     * Perform the Question creation with its Entry
     * 
     * @param request
     *            The HTTP request
     * @return The URL to display error message after performing the action or null if no error occurred
     */
    protected String processQuestionDuplication( HttpServletRequest request )
    {
        int nIdQuestion = INTEGER_MINUS_ONE;

        String strIdQuestion = request.getParameter( FormsConstants.PARAMETER_ID_TARGET );
        int nIdStep = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );
        Question questionToCopy = null;

        if ( ( _step == null ) || nIdStep != _step.getId( ) )
        {
            _step = getFormDatabaseService( ).findStepByPrimaryKey( nIdStep );
        }

        if ( StringUtils.isNotEmpty( strIdQuestion ) )
        {
            nIdQuestion = Integer.parseInt( strIdQuestion );
        }

        // Get the question to copy and clone it
        try
        {
            _question = getFormDatabaseService( ).findQuestionByPrimaryKey( nIdQuestion );
            questionToCopy = _question.clone( );
            questionToCopy.setEntry( _question.getEntry( ) );
        }
        catch( CloneNotSupportedException e )
        {
            AppLogService.error( "Unable to clone question with id " + nIdQuestion, e );
            return null;
        }

        // Create a duplicated entry
        Entry duplicatedEntry = getFormDatabaseService( ).copyEntry( _question.getEntry( ) );
        duplicatedEntry.setTitle( "Copie de " + duplicatedEntry.getTitle( ) );
        duplicatedEntry.setCode( "question_" + duplicatedEntry.getIdEntry( ) );

        getFormDatabaseService( ).updateEntry( duplicatedEntry );

        // Get the question controls
        List<Control> listControlsToDuplicate = getFormDatabaseService( ).getControlByQuestion( _question.getId( ) );

        FormDisplay formDisplayToCopy = getFormDatabaseService( ).getFormDisplayByFormStepAndComposite( _step.getIdForm( ), _step.getId( ),
                _question.getId( ) );

        questionToCopy.setEntry( duplicatedEntry );
        questionToCopy.setIdEntry( duplicatedEntry.getIdEntry( ) );
        questionToCopy.setTitle( "Copie de " + questionToCopy.getTitle( ) );
        questionToCopy.setCode( duplicatedEntry.getCode( ) );

        getFormDatabaseService( ).createQuestion( questionToCopy );

        // Duplicates the controls of the question
        for ( Control control : listControlsToDuplicate )
        {

            Set<Integer> listQuestion = new HashSet<>( );
            listQuestion.add( questionToCopy.getId( ) );
            control.setListIdQuestion( listQuestion );
            control.setIdControlTarget( questionToCopy.getId( ) );
            getFormDatabaseService( ).createControl( control );
        }

        if ( questionToCopy.getId( ) != INTEGER_MINUS_ONE )
        {
            FormDisplay formDisplay = new FormDisplay( );
            formDisplay.setDisplayOrder( formDisplayToCopy.getDisplayOrder( ) + 1 );
            formDisplay.setFormId( _step.getIdForm( ) );
            formDisplay.setStepId( questionToCopy.getIdStep( ) );
            formDisplay.setParentId( formDisplayToCopy.getParentId( ) );
            formDisplay.setCompositeId( questionToCopy.getId( ) );
            formDisplay.setCompositeType( CompositeDisplayType.QUESTION.getLabel( ) );
            formDisplay.setDepth( formDisplayToCopy.getDepth( ) );
            getFormDatabaseService( ).createFormDisplay( formDisplay );
        }

        return null;
    }

    /**
     * Check if the question code already exists or not
     * 
     * @param strCode
     *            The question code
     * @param nIdForm
     *            The id of the form
     * @return true if the code already exists; false otherwise
     */
    private boolean checkCodeAlreadyExists( String strCode, int nIdForm, int nIdEntry )
    {
        List<Question> listQuestionOfForm = getFormDatabaseService( ).getListQuestionByForm( nIdForm );

        for ( Question question : listQuestionOfForm )
        {
            if ( question.getCode( ).equals( strCode ) && nIdEntry != question.getIdEntry( ) )
            {
                return true;
            }
        }
        return false;
    }

    protected IFormDisplayService getFormDisplayService( )
    {
        if ( _formDisplayService == null )
        {
            _formDisplayService = initFormDisplayService( );
        }
        return _formDisplayService;
    }

    protected abstract IFormDisplayService initFormDisplayService( );

    protected IFormDatabaseService getFormDatabaseService( )
    {
        if ( _formDatabaseService == null )
        {
            _formDatabaseService = initFormDatabaseService( );
        }
        return _formDatabaseService;
    }

    protected abstract IFormDatabaseService initFormDatabaseService( );
}
