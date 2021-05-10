/*
 * Copyright (c) 2002-2021, City of Paris
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
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.Group;
import fr.paris.lutece.plugins.forms.business.GroupHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.service.FormDisplayService;
import fr.paris.lutece.plugins.forms.service.FormService;
import fr.paris.lutece.plugins.forms.service.download.FormsFileDownloadProvider;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeCheckBox;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeComment;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeDate;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeRadioButton;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeSelect;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.util.FormsDisplayUtils;
import fr.paris.lutece.plugins.forms.util.FormsEntryUtils;
import fr.paris.lutece.plugins.forms.web.ICompositeDisplay;
import fr.paris.lutece.plugins.forms.web.entrytype.EntryTypeCommentDisplayService;
import fr.paris.lutece.plugins.forms.web.exception.CodeAlreadyExistsException;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.download.AbstractFileDownloadProvider;
import fr.paris.lutece.portal.service.download.FileDownloadData;
import fr.paris.lutece.portal.service.download.IFileDownloadProvider;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class provides the user interface to manage Form features ( manage, create, modify, remove )
 */

// TODO : Move get MoveComposite() and doMoveComposite( ) to a another dedicated
// controller

@Controller( controllerJsp = "ManageQuestions.jsp", controllerPath = "jsp/admin/plugins/forms/", right = "FORMS_MANAGEMENT" )
public class FormQuestionJspBean extends AbstractJspBean
{

    private static final Class<?> [ ] FILTERABLE = {
            EntryTypeCheckBox.class, EntryTypeRadioButton.class, EntryTypeSelect.class, EntryTypeDate.class
    };

    private static final long serialVersionUID = 7515975782241863390L;
    private static final String ERROR_CODE_EXISTS = " Provided code already exists ";

    private static final String EMPTY_STRING = "";

    // Templates
    private static final String TEMPLATE_MANAGE_QUESTIONS = "/admin/plugins/forms/manage_questions.html";
    private static final String TEMPLATE_CREATE_GROUP = "/admin/plugins/forms/create_group.html";
    private static final String TEMPLATE_MODIFY_GROUP = "/admin/plugins/forms/modify_group.html";
    private static final String TEMPLATE_MOVE_COMPOSITE = "/admin/plugins/forms/move_composite.html";
    private static final String TEMPLATE_CREATE_QUESTION = "/admin/plugins/forms/create_question.html";
    private static final String TEMPLATE_MODIFY_QUESTION = "/admin/plugins/forms/modify_question.html";

    // Properties
    private static final String PROPERTY_CREATE_COMMENT_TITLE = "forms.create_Question.titleComment";
    private static final String PROPERTY_CREATE_QUESTION_TITLE = "forms.create_Question.titleQuestion";
    private static final String PROPERTY_CREATE_GROUP_TITLE = "forms.create_group.title";
    private static final String PROPERTY_MODIFY_COMMENT_TITLE = "forms.modifyEntry.titleComment";
    private static final String PROPERTY_MODIFY_QUESTION_TITLE = "forms.modifyEntry.titleQuestion";
    private static final String PROPERTY_MOVE_GROUP_TITLE = "forms.moveComposite.group.title";
    private static final String PROPERTY_MOVE_QUESTION_TITLE = "forms.moveComposite.question.title";

    // Views
    private static final String VIEW_MANAGE_QUESTIONS = "manageQuestions";
    private static final String VIEW_CREATE_QUESTION = "createQuestion";
    private static final String VIEW_MODIFY_QUESTION = "modifyQuestion";
    private static final String VIEW_CREATE_GROUP = "createGroup";
    private static final String VIEW_MODIFY_GROUP = "modifyGroup";
    private static final String VIEW_MOVE_COMPOSITE = "moveComposite";
    private static final String VIEW_CONFIRM_REMOVE_COMPOSITE = "getConfirmRemoveComposite";

    // Actions
    private static final String ACTION_CREATE_QUESTION = "createQuestion";
    private static final String ACTION_CREATE_QUESTION_AND_MANAGE_ENTRIES = "createQuestionAndManageEntries";
    private static final String ACTION_MODIFY_QUESTION = "modifyQuestion";
    private static final String ACTION_SAVE_QUESTION = "saveQuestion";
    private static final String ACTION_CREATE_GROUP = "createGroup";
    private static final String ACTION_MODIFY_GROUP = "modifyGroup";
    private static final String ACTION_MOVE_COMPOSITE = "moveComposite";
    private static final String ACTION_REMOVE_COMPOSITE = "removeComposite";
    private static final String ACTION_DUPLICATE_QUESTION = "duplicateQuestion";
    private static final String ACTION_REMOVE_FILE = "removeFileComment";

    // Markers
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_ENTRY_TYPE_SERVICE = "entryTypeService";
    private static final String MARK_LIST_PARAM_DEFAULT_VALUES = "list_param_default_values";
    private static final String MARK_LIST = "list";
    private static final String MARK_GROUP_VALIDATED = "groupValidated";
    private static final String MARK_STEP_VALIDATED = "stepValidated";

    // Parameters
    private static final String PARAMETER_VALUE_VALIDATE_STEP = "validateStep";
    private static final String PARAMETER_VALUE_VALIDATE_GROUP = "validateGroup";

    // Error messages
    private static final String ERROR_QUESTION_NOT_CREATED = "forms.error.question.notCreated";
    private static final String ERROR_GROUP_NOT_CREATED = "forms.error.group.notCreated";
    private static final String ERROR_GROUP_NOT_UPDATED = "forms.error.group.notUpdated";
    private static final String ERROR_STEP_OR_GROUP_NOT_VALIDATED = "forms.error.moveComposite.stepOrGroup.notvalidated";
    private static final String ERROR_OCCURED_MOVING_COMPOSITE = "forms.error.moveComposite.notCompleted";
    private static final String ERROR_ITERATION_NUMBER = "forms.error.group.iterationNumber";
    private static final String ERROR_QUESTION_CODE_ALREADY_EXISTS = "forms.error.question.codeAlreadyExists";

    // Infos messages
    private static final String INFO_QUESTION_CREATED = "forms.info.question.created";
    private static final String INFO_QUESTION_UPDATED = "forms.info.question.updated";
    private static final String INFO_GROUP_UPDATED = "forms.info.group.updated";
    private static final String INFO_GROUP_CREATED = "forms.info.group.created";
    private static final String INFO_MOVE_COMPOSITE_SUCCESSFUL = "forms.info.moveComposite.successful";
    private static final String INFO_DELETE_COMPOSITE_SUCCESSFUL = "forms.info.deleteComposite.successful";
    private static final String INFO_QUESTION_DUPLICATED = "forms.info.question.duplicated";
    private static final String ENTRY_COMMENT_TITLE = "forms.manage_questions.type.comment.title";

    // Warning messages
    private static final String WARNING_CONFIRM_REMOVE_QUESTION = "forms.warning.deleteComposite.confirmRemoveQuestion";
    private static final String WARNING_CONFIRM_REMOVE_GROUP_ANY_QUESTIONS = "forms.warning.deleteComposite.confirmRemoveGroup";
    private static final String WARNING_CONFIRM_REMOVE_QUESTION_FORM_ACTIVE = "forms.warning.deleteComposite.confirmRemoveQuestion.formActive";
    private static final String WARNING_CONFIRM_REMOVE_GROUP_ANY_QUESTIONS_FORM_ACTIVE = "forms.warning.deleteComposite.confirmRemoveGroup.formActive";

    // Validations
    private static final String GROUP_VALIDATION_ATTRIBUTES_PREFIX = "forms.model.entity.group.attribute.";

    // Others
    private static final int INTEGER_MINUS_ONE = -1;

    private static final int DEFAULT_PARENT = 0;

    private static final FormDisplayService _formDisplayService = SpringContextService.getBean( FormDisplayService.BEAN_NAME );
    private static final FormService _formService = SpringContextService.getBean( FormService.BEAN_NAME );

    private Step _step;
    private Form _form;
    private Entry _entry;
    private Question _question;
    private Group _group;
    private FormDisplay _formDisplay;

    private int _nIdStepTarget;

    private int _nIdParentTarget;

    private int _nIdParentSelected = DEFAULT_PARENT;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_QUESTIONS, defaultView = true )
    public String getManageQuestions( HttpServletRequest request )
    {
        Locale locale = getLocale( );

        int nIdStep = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );

        _step = StepHome.findByPrimaryKey( nIdStep );
        _form = FormHome.findByPrimaryKey( _step.getIdForm( ) );

        String strInfoKey = request.getParameter( FormsConstants.PARAMETER_INFO_KEY );
        if ( StringUtils.isNotEmpty( strInfoKey ) )
        {
            addInfo( strInfoKey, getLocale( ) );
        }

        Map<String, Object> model = getModel( );
        model.put( FormsConstants.MARK_STEP, _step );
        model.put( FormsConstants.MARK_FORM, _form );

        List<ICompositeDisplay> listICompositeDisplay = _formService.getStepCompositeList( nIdStep );

        model.put( FormsConstants.MARK_COMPOSITE_LIST, listICompositeDisplay );
        model.put( FormsConstants.MARK_ENTRY_TYPE_REF_LIST, FormsEntryUtils.initListEntryType( ) );
        model.put( FormsConstants.MARK_ID_PARENT, _nIdParentSelected );

        setPageTitleProperty( EMPTY_STRING );
        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_QUESTIONS, locale, model );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Gets the entry creation page
     * 
     * @param request
     *            The HTTP request
     * @return The entry creation page
     */
    @View( value = VIEW_CREATE_QUESTION )
    public String getCreateQuestion( HttpServletRequest request )
    {

        int nIdStep = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );
        int nIdTypeEntry = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_BUTTON_TYPE_ENTRY ) );
        _nIdParentSelected = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY_PARENT ) );

        _step = StepHome.findByPrimaryKey( nIdStep );
        if ( ( _step == null ) )
        {
            return redirectToViewManageForm( request );
        }

        _entry = FormsEntryUtils.createEntryByType( nIdTypeEntry );

        if ( ( _entry == null ) )
        {
            return redirectToViewManageForm( request );
        }

        _entry.setIdResource( _step.getIdForm( ) );
        _entry.setResourceType( Form.RESOURCE_TYPE );

        _form = FormHome.findByPrimaryKey( _step.getIdForm( ) );

        ReferenceList listParamDefaultValues = new ReferenceList( );

        IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( _entry );

        Map<String, Object> model = new HashMap<>( );
        model.put( FormsConstants.MARK_ENTRY, _entry );
        model.put( FormsConstants.MARK_FORM, _form );
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

        boolean canBeFiltered = Arrays.asList( FILTERABLE ).contains( entryTypeService.getClass( ) );

        model.put( FormsConstants.MARK_CAN_BE_FILTERED, canBeFiltered );
        model.put( FormsConstants.MARK_QUESTION_CREATE_TEMPLATE,
                AppTemplateService.getTemplate( TEMPLATE_CREATE_QUESTION, request.getLocale( ), model ).getHtml( ) );

        HtmlTemplate template = AppTemplateService.getTemplate( EntryTypeServiceManager.getEntryTypeService( _entry ).getTemplateCreate( _entry, false ),
                getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Gets the entry creation page
     * 
     * @param request
     *            The HTTP request
     * @return The entry creation page
     */
    @View( value = VIEW_CREATE_GROUP )
    public String getCreateGroup( HttpServletRequest request )
    {

        int nIdStep = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), FormsConstants.DEFAULT_ID_VALUE );
        _nIdParentSelected = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY_PARENT ), FormsConstants.DEFAULT_ID_VALUE );

        if ( ( _step == null ) || ( nIdStep != FormsConstants.DEFAULT_ID_VALUE && nIdStep != _step.getId( ) ) )
        {
            _step = StepHome.findByPrimaryKey( nIdStep );
        }

        if ( ( _step == null ) )
        {
            return redirectToViewManageForm( request );
        }

        _form = FormHome.findByPrimaryKey( _step.getIdForm( ) );

        _group = new Group( );
        _group.setIterationMin( NumberUtils.INTEGER_ONE );
        _group.setIterationMax( NumberUtils.INTEGER_ONE );

        Map<String, Object> model = getModel( );
        model.put( FormsConstants.MARK_FORM, _form );
        model.put( FormsConstants.MARK_STEP, _step );
        model.put( FormsConstants.MARK_GROUP, _group );
        model.put( FormsConstants.MARK_ID_PARENT, _nIdParentSelected );

        setPageTitleProperty( PROPERTY_CREATE_GROUP_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_GROUP, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Perform the Group creation
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_CREATE_GROUP )
    public String doCreateGroup( HttpServletRequest request )
    {

        int nIdStep = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), FormsConstants.DEFAULT_ID_VALUE );
        int nParentGroup = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY_PARENT ), FormsConstants.DEFAULT_ID_VALUE );

        if ( ( _step == null ) || nIdStep != _step.getId( ) )
        {
            _step = StepHome.findByPrimaryKey( nIdStep );
        }

        _group = ( _group != null ) ? _group : new Group( );
        populate( _group, request );

        if ( !validateGroup( ) )
        {
            return redirect( request, VIEW_CREATE_GROUP, FormsConstants.PARAMETER_ID_DISPLAY_PARENT, nParentGroup );
        }

        GroupHome.create( _group );

        int nDisplayDepth = FormsDisplayUtils.getDisplayDepthFromParent( nParentGroup );

        if ( _group.getId( ) != -1 )
        {
            FormDisplay formDisplay = new FormDisplay( );
            formDisplay.setFormId( _step.getIdForm( ) );
            formDisplay.setStepId( nIdStep );
            formDisplay.setParentId( nParentGroup );
            formDisplay.setCompositeId( _group.getId( ) );
            formDisplay.setCompositeType( CompositeDisplayType.GROUP.getLabel( ) );
            formDisplay.setDepth( nDisplayDepth );
            FormDisplayHome.create( formDisplay );

            if ( formDisplay.getId( ) == -1 )
            {
                addError( ERROR_GROUP_NOT_CREATED, getLocale( ) );
            }
        }
        else
        {
            addError( ERROR_GROUP_NOT_CREATED, getLocale( ) );
        }

        addInfo( INFO_GROUP_CREATED, getLocale( ) );
        return redirect( request, VIEW_MANAGE_QUESTIONS, FormsConstants.PARAMETER_ID_STEP, _step.getId( ) );

    }

    /**
     * Gets the entry creation page
     * 
     * @param request
     *            The HTTP request
     * @return The entry creation page
     */
    @View( value = VIEW_MODIFY_GROUP )
    public String getModifyGroup( HttpServletRequest request )
    {

        int nIdStep = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), FormsConstants.DEFAULT_ID_VALUE );
        int nIdGroup = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_GROUP ), FormsConstants.DEFAULT_ID_VALUE );

        if ( ( _step == null ) || ( nIdStep != FormsConstants.DEFAULT_ID_VALUE && nIdStep != _step.getId( ) ) )
        {
            _step = StepHome.findByPrimaryKey( nIdStep );
        }

        if ( ( _step == null ) )
        {
            return redirectToViewManageForm( request );
        }

        _form = FormHome.findByPrimaryKey( _step.getIdForm( ) );

        if ( _group == null || ( nIdGroup != FormsConstants.DEFAULT_ID_VALUE && _group.getId( ) != nIdGroup ) )
        {
            _group = GroupHome.findByPrimaryKey( nIdGroup );
        }

        Map<String, Object> model = getModel( );
        model.put( FormsConstants.MARK_FORM, _form );
        model.put( FormsConstants.MARK_STEP, _step );
        model.put( FormsConstants.MARK_GROUP, _group );

        setPageTitleProperty( PROPERTY_CREATE_GROUP_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_GROUP, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Perform the Group creation
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_MODIFY_GROUP )
    public String doModifyGroup( HttpServletRequest request )
    {

        int nIdStep = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );

        if ( ( _step == null ) || nIdStep != _step.getId( ) )
        {
            _step = StepHome.findByPrimaryKey( nIdStep );
        }

        _group = ( _group != null ) ? _group : new Group( );
        populate( _group, request );

        if ( !validateGroup( ) )
        {
            return redirectView( request, VIEW_MODIFY_GROUP );
        }

        GroupHome.update( _group );

        if ( _group.getId( ) == INTEGER_MINUS_ONE )
        {
            addError( ERROR_GROUP_NOT_UPDATED, getLocale( ) );
        }
        else
        {
            addInfo( INFO_GROUP_UPDATED, getLocale( ) );
        }

        return redirect( request, VIEW_MANAGE_QUESTIONS, FormsConstants.PARAMETER_ID_STEP, _step.getId( ) );

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
     * Perform the Question creation with its Entry
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_CREATE_QUESTION )
    public String doCreateQuestion( HttpServletRequest request )
    {
        try
        {
            String strReturnUrl = processQuestionCreation( request );

            if ( strReturnUrl != null )
            {
                return redirect( request, strReturnUrl );
            }
            else
            {
                addInfo( INFO_QUESTION_CREATED, getLocale( ) );
            }
        }
        catch( CodeAlreadyExistsException e )
        {
            AppLogService.error( ERROR_CODE_EXISTS + e.getCode( ), e );
            addError( ERROR_QUESTION_CODE_ALREADY_EXISTS, getLocale( ) );
        }
        return redirect( request, VIEW_MANAGE_QUESTIONS, FormsConstants.PARAMETER_ID_STEP, _step.getId( ) );

    }

    /**
     * Perform the Question creation with its Entry and redirect to Entry fields management view
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_CREATE_QUESTION_AND_MANAGE_ENTRIES )
    public String doCreateQuestionAndManageEntries( HttpServletRequest request )
    {
        try
        {
            String strReturnUrl = processQuestionCreation( request );
            return strReturnUrl != null ? strReturnUrl
                    : redirect( request, VIEW_MODIFY_QUESTION, FormsConstants.PARAMETER_ID_STEP, _step.getId( ), FormsConstants.PARAMETER_ID_QUESTION,
                            _question.getId( ) );

        }
        catch( CodeAlreadyExistsException e )
        {
            AppLogService.error( ERROR_CODE_EXISTS, e );
            addError( ERROR_QUESTION_CODE_ALREADY_EXISTS, getLocale( ) );
            return redirect( request, VIEW_MANAGE_QUESTIONS, FormsConstants.PARAMETER_ID_STEP, _step.getId( ) );
        }
    }

    /**
     * Perform the Question creation with its Entry
     * 
     * @param request
     *            The HTTP request
     * @return The URL to display error message after performing the action or null if no error occurred
     */
    private String processQuestionCreation( HttpServletRequest request ) throws CodeAlreadyExistsException
    {

        int nIdStep = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );
        int nParentGroup = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY_PARENT ) );
        int nIdType = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_ENTRY_TYPE ) );

        if ( ( _step == null ) || nIdStep != _step.getId( ) )
        {
            _step = StepHome.findByPrimaryKey( nIdStep );
        }

        _entry = FormsEntryUtils.createEntryByType( nIdType );

        if ( _entry == null )
        {
            addError( ERROR_QUESTION_NOT_CREATED, getLocale( ) );
            return redirect( request, VIEW_MANAGE_QUESTIONS, FormsConstants.PARAMETER_ID_STEP, nIdStep );
        }

        String strError = EntryTypeServiceManager.getEntryTypeService( _entry ).getRequestData( _entry, request, getLocale( ) );

        if ( strError != null )
        {
            return strError;
        }

        _entry.setIdResource( _step.getIdForm( ) );
        _entry.setResourceType( Form.RESOURCE_TYPE );
        _entry.setIdEntry( EntryHome.create( _entry ) );

        // If the entry code is empty, provide a new one
        if ( StringUtils.isEmpty( _entry.getCode( ) ) )
        {
            _entry.setCode( "question_" + _entry.getIdEntry( ) );
        }

        if ( checkCodeAlreadyExists( _entry.getCode( ), _step.getIdForm( ), _entry.getIdEntry( ) ) )
        {
            throw new CodeAlreadyExistsException( _entry.getCode( ) );
        }

        EntryHome.update( _entry );

        if ( _entry.getFields( ) != null )
        {
            for ( Field field : _entry.getFields( ) )
            {
                field.setParentEntry( _entry );
                FieldHome.create( field );
            }
        }

        _question = new Question( );
        String strTitle = Boolean.TRUE.equals( _entry.getEntryType( ).getComment( ) ) ? I18nService.getLocalizedString( ENTRY_COMMENT_TITLE, getLocale( ) )
                : _entry.getTitle( );
        _question.setTitle( strTitle );
        _question.setCode( _entry.getCode( ) );
        _question.setDescription( _entry.getComment( ) );
        _question.setIdEntry( _entry.getIdEntry( ) );
        _question.setIdStep( nIdStep );
        _question.setVisibleMultiviewGlobal( request.getParameter( FormsConstants.PARAMETER_MULTIVIEW_GLOBAL ) != null );
        _question.setVisibleMultiviewFormSelected( request.getParameter( FormsConstants.PARAMETER_MULTIVIEW_FORM_SELECTED ) != null );
        _question.setFiltrableMultiviewGlobal( request.getParameter( FormsConstants.PARAMETER_FILTERABLE_MULTIVIEW_GLOBAL ) != null );
        _question.setFiltrableMultiviewFormSelected( request.getParameter( FormsConstants.PARAMETER_FILTERABLE_MULTIVIEW_FORM_SELECTED ) != null );

        String columnTitle = request.getParameter( FormsConstants.PARAMETER_COLUMN_TITLE );
        columnTitle = ( columnTitle == null || columnTitle.isEmpty( ) ) ? _question.getTitle( ) : columnTitle;
        _question.setColumnTitle( columnTitle );
        QuestionHome.create( _question );

        int nDisplayDepth = FormsDisplayUtils.getDisplayDepthFromParent( nParentGroup );

        if ( _question.getId( ) != INTEGER_MINUS_ONE )
        {
            FormDisplay formDisplay = new FormDisplay( );
            formDisplay.setFormId( _step.getIdForm( ) );
            formDisplay.setStepId( nIdStep );
            formDisplay.setParentId( nParentGroup );
            formDisplay.setCompositeId( _question.getId( ) );
            formDisplay.setCompositeType( CompositeDisplayType.QUESTION.getLabel( ) );
            formDisplay.setDepth( nDisplayDepth );
            FormDisplayHome.create( formDisplay );
        }
        return null;

    }

    /**
     * Perform the Question creation with its Entry
     * 
     * @param request
     *            The HTTP request
     * @return The URL to display error message after performing the action or null if no error occurred
     */
    private String processQuestionDuplication( HttpServletRequest request )
    {
        int nIdQuestion = INTEGER_MINUS_ONE;

        String strIdQuestion = request.getParameter( FormsConstants.PARAMETER_ID_TARGET );
        int nIdStep = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );
        Question questionToCopy = new Question( );

        if ( ( _step == null ) || nIdStep != _step.getId( ) )
        {
            _step = StepHome.findByPrimaryKey( nIdStep );
        }

        if ( ( strIdQuestion != null ) && !strIdQuestion.equals( EMPTY_STRING ) )
        {
            nIdQuestion = Integer.parseInt( strIdQuestion );
        }

        // Get the question to copy and clone it
        try
        {
            _question = QuestionHome.findByPrimaryKey( nIdQuestion );
            questionToCopy = _question.clone( );
            questionToCopy.setEntry( _question.getEntry( ) );
        }
        catch( CloneNotSupportedException e )
        {
            AppLogService.error( "Unable to clone question with id " + nIdQuestion, e );
        }

        // Create a duplicated entry
        Entry duplicatedEntry = EntryHome.copy( _question.getEntry( ) );
        duplicatedEntry.setTitle( "Copie de " + duplicatedEntry.getTitle( ) );
        duplicatedEntry.setCode( "question_" + duplicatedEntry.getIdEntry( ) );
        duplicatedEntry.setPosition( duplicatedEntry.getPosition( ) + 1 );

        EntryHome.update( duplicatedEntry );

        // Get the question controls
        List<Control> listControlsToDuplicate = ControlHome.getControlByQuestion( _question.getId( ) );

        FormDisplay formDisplayToCopy = FormDisplayHome.getFormDisplayByFormStepAndComposite( _step.getIdForm( ), _step.getId( ), _question.getId( ) );

        questionToCopy.setEntry( duplicatedEntry );
        questionToCopy.setIdEntry( duplicatedEntry.getIdEntry( ) );
        questionToCopy.setTitle( "Copie de " + questionToCopy.getTitle( ) );
        questionToCopy.setCode( duplicatedEntry.getCode( ) );

        QuestionHome.create( questionToCopy );

        // Duplicates the controls of the question
        for ( Control control : listControlsToDuplicate )
        {

            Set<Integer> listQuestion = new HashSet<>( );
            listQuestion.add( questionToCopy.getId( ) );
            control.setListIdQuestion( listQuestion );
            control.setIdControlTarget( questionToCopy.getId( ) );
            ControlHome.create( control );
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
            FormDisplayHome.create( formDisplay );
        }

        return null;
    }

    /**
     * Gets the Question entry modification page
     * 
     * @param request
     *            The HTTP request
     * @return The entry modification page
     */
    @View( value = VIEW_MODIFY_QUESTION )
    public String getModifyQuestion( HttpServletRequest request )
    {

        String strIdStep = request.getParameter( FormsConstants.PARAMETER_ID_STEP );

        if ( StringUtils.isNotEmpty( strIdStep ) )
        {
            int nIdStep = Integer.parseInt( strIdStep );

            if ( ( _step == null ) || ( _step.getId( ) != nIdStep ) )
            {
                _step = StepHome.findByPrimaryKey( nIdStep );
            }
        }

        String strIdQuestion = request.getParameter( FormsConstants.PARAMETER_ID_QUESTION );
        int nIdQuestion = INTEGER_MINUS_ONE;

        if ( StringUtils.isNotEmpty( strIdQuestion ) )
        {
            nIdQuestion = Integer.parseInt( strIdQuestion );
        }

        _question = QuestionHome.findByPrimaryKey( nIdQuestion );

        _entry = EntryHome.findByPrimaryKey( _question.getIdEntry( ) );

        List<Field> listField = new ArrayList<>( _entry.getFields( ).size( ) );

        for ( Field field : _entry.getFields( ) )
        {
            field = FieldHome.findByPrimaryKey( field.getIdField( ) );
            listField.add( field );
        }

        _entry.setFields( listField );

        if ( ( _form == null ) || ( _form.getId( ) != _entry.getIdResource( ) ) )
        {
            _form = FormHome.findByPrimaryKey( _entry.getIdResource( ) );
        }

        IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( _entry );

        Map<String, Object> model = new HashMap<>( );
        model.put( FormsConstants.MARK_ENTRY, _entry );
        model.put( FormsConstants.MARK_FORM, _form );
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

        boolean canBeFiltered = Arrays.asList( FILTERABLE ).contains( entryTypeService.getClass( ) );

        model.put( FormsConstants.MARK_CAN_BE_FILTERED, canBeFiltered );
        model.put( FormsConstants.MARK_QUESTION_MODIFY_TEMPLATE,
                AppTemplateService.getTemplate( TEMPLATE_MODIFY_QUESTION, request.getLocale( ), model ).getHtml( ) );

        if ( entryTypeService instanceof EntryTypeComment )
        {
            Field fieldFile = _entry.getFieldByCode( IEntryTypeService.FIELD_DOWNLOADABLE_FILE );
            if ( fieldFile != null )
            {
                FileDownloadData fileDownloadData = new FileDownloadData( _entry.getIdResource( ), Form.RESOURCE_TYPE,
                        Integer.parseInt( fieldFile.getValue( ) ) );
                IFileDownloadProvider provider = AbstractFileDownloadProvider.findProvider( FormsFileDownloadProvider.PROVIDER_NAME );

                model.put( EntryTypeCommentDisplayService.MARK_FILENAME, fieldFile.getTitle( ) );
                model.put( EntryTypeCommentDisplayService.MARK_URL_DOWNLOAD_BO, provider.getDownloadUrl( fileDownloadData, true ) );
            }
        }
        HtmlTemplate template = AppTemplateService.getTemplate( entryTypeService.getTemplateModify( _entry, false ), getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Perform the entry modification
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String processQuestionUpdate( HttpServletRequest request ) throws CodeAlreadyExistsException
    {
        String strIdStep = request.getParameter( FormsConstants.PARAMETER_ID_STEP );
        int nIdStep = -INTEGER_MINUS_ONE;

        if ( StringUtils.isNotEmpty( strIdStep ) )
        {
            nIdStep = Integer.parseInt( strIdStep );
        }
        if ( ( _step == null ) || ( _step.getId( ) != nIdStep ) )
        {
            _step = StepHome.findByPrimaryKey( nIdStep );
        }

        String strIdQuestion = request.getParameter( FormsConstants.PARAMETER_ID_QUESTION );
        int nIdQuestion = INTEGER_MINUS_ONE;

        if ( StringUtils.isNotEmpty( strIdQuestion ) )
        {
            nIdQuestion = Integer.parseInt( strIdQuestion );
        }

        if ( ( _question == null ) || ( _question.getId( ) != nIdQuestion ) )
        {
            _question = QuestionHome.findByPrimaryKey( nIdQuestion );
        }

        int nIdEntry = _question.getIdEntry( );
        _entry = EntryHome.findByPrimaryKey( nIdEntry );

        String strError = EntryTypeServiceManager.getEntryTypeService( _entry ).getRequestData( _entry, request, getLocale( ) );

        if ( strError != null )
        {
            return strError;
        }

        if ( checkCodeAlreadyExists( _entry.getCode( ), _step.getIdForm( ), _entry.getIdEntry( ) ) )
        {
            throw new CodeAlreadyExistsException( _entry.getCode( ) );
        }

        EntryHome.update( _entry );

        if ( _entry.getFields( ) != null )
        {
            for ( Field field : _entry.getFields( ) )
            {
                // Check if the field already exists in the database
                Field fieldStored = FieldHome.findByPrimaryKey( field.getIdField( ) );

                if ( fieldStored != null )
                {
                    // If it exists, update
                    FieldHome.update( field );
                }
                else
                {
                    // If it does not exist, create
                    FieldHome.create( field );
                }
            }
        }

        String strTitle = Boolean.TRUE.equals( _entry.getEntryType( ).getComment( ) ) ? I18nService.getLocalizedString( ENTRY_COMMENT_TITLE, getLocale( ) )
                : _entry.getTitle( );
        _question.setVisibleMultiviewGlobal( request.getParameter( FormsConstants.PARAMETER_MULTIVIEW_GLOBAL ) != null );
        _question.setVisibleMultiviewFormSelected( request.getParameter( FormsConstants.PARAMETER_MULTIVIEW_FORM_SELECTED ) != null );
        _question.setFiltrableMultiviewGlobal( request.getParameter( FormsConstants.PARAMETER_FILTERABLE_MULTIVIEW_GLOBAL ) != null );
        _question.setFiltrableMultiviewFormSelected( request.getParameter( FormsConstants.PARAMETER_FILTERABLE_MULTIVIEW_FORM_SELECTED ) != null );
        String columnTitle = request.getParameter( FormsConstants.PARAMETER_COLUMN_TITLE );
        columnTitle = StringUtils.isEmpty( columnTitle ) ? _question.getTitle( ) : columnTitle;
        _question.setColumnTitle( columnTitle );
        _question.setTitle( strTitle );
        _question.setCode( _entry.getCode( ) );
        _question.setDescription( _entry.getComment( ) );
        QuestionHome.update( _question );

        return null;

    }

    /**
     * Perform the Question update with its Entry
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_MODIFY_QUESTION )
    public String doModifyQuestion( HttpServletRequest request )
    {
        try
        {
            String strReturnUrl = processQuestionUpdate( request );

            if ( strReturnUrl != null )
            {
                return redirect( request, strReturnUrl );
            }
            else
            {
                addInfo( INFO_QUESTION_UPDATED, getLocale( ) );
            }
        }
        catch( CodeAlreadyExistsException e )
        {
            AppLogService.error( ERROR_CODE_EXISTS + e.getCode( ), e );
            addError( ERROR_QUESTION_CODE_ALREADY_EXISTS, getLocale( ) );
        }
        return redirect( request, VIEW_MANAGE_QUESTIONS, FormsConstants.PARAMETER_ID_STEP, _step.getId( ) );

    }

    /**
     * Perform the Question update with its Entry and redirect to the ModifyQuestion view
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_SAVE_QUESTION )
    public String doSaveQuestion( HttpServletRequest request )
    {
        try
        {
            String strReturnUrl = processQuestionUpdate( request );

            if ( strReturnUrl != null )
            {
                return strReturnUrl;
            }
        }
        catch( CodeAlreadyExistsException e )
        {
            AppLogService.error( ERROR_CODE_EXISTS, e );
            addError( ERROR_QUESTION_CODE_ALREADY_EXISTS, getLocale( ) );
        }
        return redirect( request, VIEW_MANAGE_QUESTIONS, FormsConstants.PARAMETER_ID_STEP, _step.getId( ), FormsConstants.PARAMETER_ID_QUESTION,
                _question.getId( ) );
    }

    /**
     * Action for duplicate a question of the form
     * 
     * @param request
     *            The HttpServletRequest the request
     * @return the manage questions page
     */
    @Action( value = ACTION_DUPLICATE_QUESTION )
    public String doDuplicateQuestion( HttpServletRequest request )
    {
        String strReturnUrl = processQuestionDuplication( request );

        if ( strReturnUrl != null )
        {
            return redirect( request, strReturnUrl );
        }
        else
        {
            addInfo( INFO_QUESTION_DUPLICATED, getLocale( ) );
        }
        return redirect( request, VIEW_MANAGE_QUESTIONS, FormsConstants.PARAMETER_ID_STEP, _step.getId( ) );
    }

    @Action( value = ACTION_REMOVE_FILE )
    public String doRemoveFileComment( HttpServletRequest request )
    {
        String idEntry = request.getParameter( FormsConstants.PARAMETER_ID_ENTRY );
        int id = Integer.parseInt( idEntry );

        Entry entry = EntryHome.findByPrimaryKey( id );
        Field oldFile = entry.getFieldByCode( IEntryTypeService.FIELD_DOWNLOADABLE_FILE );
        if ( oldFile != null )
        {
            FileHome.remove( Integer.parseInt( oldFile.getValue( ) ) );
            FieldHome.remove( oldFile.getIdField( ) );
        }

        Map<String, String> additionalParameters = new HashMap<>( );
        additionalParameters.put( FormsConstants.PARAMETER_ID_STEP, String.valueOf( _step.getId( ) ) );
        additionalParameters.put( FormsConstants.PARAMETER_ID_QUESTION, String.valueOf( _question.getId( ) ) );
        return redirect( request, VIEW_MODIFY_QUESTION, additionalParameters );
    }

    /**
     * Gets the confirmation page of question/group deletion
     * 
     * @param request
     *            The HTTP request
     * @return the confirmation page of delete entry
     */
    @View( value = VIEW_CONFIRM_REMOVE_COMPOSITE )
    public String getConfirmRemoveComposite( HttpServletRequest request )
    {

        String strMessage = StringUtils.EMPTY;

        int nIdStep = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), INTEGER_MINUS_ONE );

        if ( nIdStep == INTEGER_MINUS_ONE )
        {
            redirectToViewManageForm( request );
        }

        if ( ( _step == null ) || ( _step.getId( ) != nIdStep ) )
        {
            _step = StepHome.findByPrimaryKey( nIdStep );
        }

        int nIdForm = _step.getIdForm( );

        if ( nIdForm == INTEGER_MINUS_ONE )
        {
            redirectToViewManageForm( request );
        }

        if ( ( _form == null ) || _form.getId( ) != _step.getIdForm( ) )
        {
            _form = FormHome.findByPrimaryKey( _step.getIdForm( ) );
        }

        int nIdDisplay = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY ), INTEGER_MINUS_ONE );

        if ( nIdDisplay == INTEGER_MINUS_ONE )
        {
            redirectToViewManageForm( request );
        }

        if ( _formDisplay == null || _formDisplay.getId( ) != nIdDisplay )
        {
            _formDisplay = FormDisplayHome.findByPrimaryKey( nIdDisplay );
        }

        if ( CompositeDisplayType.QUESTION.getLabel( ).equalsIgnoreCase( _formDisplay.getCompositeType( ) ) )
        {
            if ( _form.isActive( ) )
            {
                strMessage = WARNING_CONFIRM_REMOVE_QUESTION_FORM_ACTIVE;
            }
            else
            {
                strMessage = WARNING_CONFIRM_REMOVE_QUESTION;
            }
        }

        if ( CompositeDisplayType.GROUP.getLabel( ).equalsIgnoreCase( _formDisplay.getCompositeType( ) ) )
        {
            if ( _form.isActive( ) )
            {
                strMessage = WARNING_CONFIRM_REMOVE_GROUP_ANY_QUESTIONS_FORM_ACTIVE;
            }
            else
            {
                strMessage = WARNING_CONFIRM_REMOVE_GROUP_ANY_QUESTIONS;
            }

        }

        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_COMPOSITE ) );
        url.addParameter( FormsConstants.PARAMETER_ID_DISPLAY, nIdDisplay );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, strMessage, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Perform the question suppression
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_REMOVE_COMPOSITE )
    public String doRemoveComposite( HttpServletRequest request )
    {

        int nIdDisplay = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY ), INTEGER_MINUS_ONE );

        if ( nIdDisplay == INTEGER_MINUS_ONE )
        {
            redirectToViewManageForm( request );
        }

        if ( _formDisplay == null || _formDisplay.getId( ) != nIdDisplay )
        {
            _formDisplay = FormDisplayHome.findByPrimaryKey( nIdDisplay );
        }

        _formDisplayService.deleteDisplayAndDescendants( nIdDisplay );

        List<FormDisplay> listFormDisplaySibling = FormDisplayHome.getFormDisplayListByParent( _formDisplay.getStepId( ), _formDisplay.getParentId( ) );
        _formDisplayService.rebuildDisplayPositionSequence( listFormDisplaySibling );

        addInfo( INFO_DELETE_COMPOSITE_SUCCESSFUL, getLocale( ) );
        return redirect( request, VIEW_MANAGE_QUESTIONS, FormsConstants.PARAMETER_ID_STEP, _formDisplay.getStepId( ) );
    }

    /**
     * Gets the Move component page
     * 
     * @param request
     *            The HTTP request
     * @return The move component page
     */
    @View( value = VIEW_MOVE_COMPOSITE )
    public String getMoveComposite( HttpServletRequest request )
    {
        Step stepTarget = new Step( );
        boolean bStepValidated = false;
        boolean bGroupValidated = false;

        int nIdDisplay = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY ), INTEGER_MINUS_ONE );

        if ( nIdDisplay == INTEGER_MINUS_ONE )
        {
            redirectToViewManageForm( request );
        }

        if ( _formDisplay == null || _formDisplay.getId( ) != nIdDisplay )
        {
            _formDisplay = FormDisplayHome.findByPrimaryKey( nIdDisplay );
        }
        int nTargetPosition = _formDisplay.getDisplayOrder( );

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

        if ( nIdStepTarget != INTEGER_MINUS_ONE )
        {
            stepTarget = StepHome.findByPrimaryKey( nIdStepTarget );
        }
        else
        {
            redirectToViewManageForm( request );
        }

        int nIdParentTarget = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_PARENT ), -1 );
        if ( bGroupValidated )
        {
            if ( nIdParentTarget == INTEGER_MINUS_ONE )
            {
                nIdParentTarget = _formDisplay.getParentId( );
            }
            _nIdParentTarget = nIdParentTarget;
        }

        ReferenceList listFormSteps = StepHome.getStepReferenceListByForm( stepTarget.getIdForm( ) );

        ReferenceList listStepGroups = FormsDisplayUtils.getTargetGroupDisplayListByStep( nIdStepTarget, _formDisplay, getLocale( ) );

        boolean bMoveToDifferentGroup = ( ( _formDisplay.getParentId( ) != nIdParentTarget ) || ( _formDisplay.getStepId( ) != nIdStepTarget ) );
        ReferenceList listAvailablePositionsInParentGroup = FormsDisplayUtils.getlistAvailablePositionsInGroup( nIdStepTarget, nIdParentTarget,
                bMoveToDifferentGroup, getLocale( ) );

        if ( bIsValidationButtonUsed )
        {
            nTargetPosition = listAvailablePositionsInParentGroup.size( );
        }

        Map<String, Object> model = getModel( );

        model.put( FormsConstants.MARK_STEP, _step );
        model.put( FormsConstants.MARK_DISPLAY, _formDisplay );
        model.put( FormsConstants.MARK_DISPLAY_TITLE, FormsDisplayUtils.getDisplayTitle( _formDisplay ) );
        model.put( FormsConstants.MARK_LIST_STEPS, listFormSteps );
        model.put( FormsConstants.MARK_LIST_GROUPS, listStepGroups );
        model.put( FormsConstants.MARK_LIST_AVAILABLE_POSITIONS, listAvailablePositionsInParentGroup );
        model.put( FormsConstants.MARK_ID_STEP, bStepValidated ? nIdStepTarget : _formDisplay.getStepId( ) );
        model.put( FormsConstants.MARK_ID_PARENT, bGroupValidated ? nIdParentTarget : 0 );
        model.put( FormsConstants.MARK_DISPLAY_ORDER, nTargetPosition );
        model.put( MARK_STEP_VALIDATED, bStepValidated );
        model.put( MARK_GROUP_VALIDATED, bGroupValidated );

        if ( _formDisplay.getCompositeType( ).equalsIgnoreCase( CompositeDisplayType.GROUP.getLabel( ) ) )
        {
            setPageTitleProperty( PROPERTY_MOVE_GROUP_TITLE );
        }
        else
            if ( _formDisplay.getCompositeType( ).equalsIgnoreCase( CompositeDisplayType.QUESTION.getLabel( ) ) )
            {
                setPageTitleProperty( PROPERTY_MOVE_QUESTION_TITLE );
            }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MOVE_COMPOSITE, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Process the FormDisplay moving action
     * 
     * @param request
     *            The HTTP request
     * @return The move component page
     */
    @Action( ACTION_MOVE_COMPOSITE )
    public String doMoveComposite( HttpServletRequest request )
    {

        boolean bStepValidated = false;
        boolean bGroupValidated = false;

        int nIdDisplay = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY ), -1 );

        if ( nIdDisplay == INTEGER_MINUS_ONE )
        {
            redirectToViewManageForm( request );
        }

        if ( _formDisplay == null || _formDisplay.getId( ) != nIdDisplay )
        {
            _formDisplay = FormDisplayHome.findByPrimaryKey( nIdDisplay );
        }

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

        int nIdStepTarget = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), INTEGER_MINUS_ONE );
        int nIdParentTarget = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_PARENT ), INTEGER_MINUS_ONE );
        int nDisplayOrderTarget = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_DISPLAY_ORDER ), INTEGER_MINUS_ONE );

        if ( ( nIdStepTarget == INTEGER_MINUS_ONE ) || ( nIdParentTarget == INTEGER_MINUS_ONE ) )
        {
            addError( ERROR_OCCURED_MOVING_COMPOSITE, getLocale( ) );
            return redirect( request, VIEW_MANAGE_QUESTIONS, FormsConstants.PARAMETER_ID_STEP, _formDisplay.getStepId( ) );
        }

        if ( nIdStepTarget != _nIdStepTarget )
        {
            bStepValidated = false;
        }

        if ( nIdParentTarget != _nIdParentTarget )
        {
            bGroupValidated = false;
        }

        if ( !( bStepValidated && bGroupValidated ) )
        {
            addError( ERROR_STEP_OR_GROUP_NOT_VALIDATED, getLocale( ) );
            return redirect( request, VIEW_MOVE_COMPOSITE, FormsConstants.PARAMETER_ID_DISPLAY, nIdDisplay );
        }

        moveDisplay( _formDisplay, nIdStepTarget, nIdParentTarget, nDisplayOrderTarget );

        addInfo( INFO_MOVE_COMPOSITE_SUCCESSFUL, getLocale( ) );

        return redirect( request, VIEW_MANAGE_QUESTIONS, FormsConstants.PARAMETER_ID_STEP, nIdStepTarget );
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
    private void moveDisplay( FormDisplay formDisplayToMove, int nIdStepTarget, int nIdParentTarget, int nDisplayOrderTarget )
    {
        int nIdOriginStep = _formDisplay.getStepId( );
        int nIdOriginParent = _formDisplay.getParentId( );
        int nOriginDisplayOrder = _formDisplay.getDisplayOrder( );

        List<FormDisplay> listDisplayInTargetGroup = FormDisplayHome.getFormDisplayListByParent( nIdStepTarget, nIdParentTarget );

        formDisplayToMove.setParentId( nIdParentTarget );

        int nTargetDepth = FormsDisplayUtils.getDisplayDepthFromParent( nIdParentTarget );
        // update the idStep and depth of the display and all its descendants
        _formDisplayService.setChildrenDisplayDepthAndStep( formDisplayToMove, nTargetDepth, nIdStepTarget );

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

            _formDisplayService.rebuildDisplayPositionSequence( listDisplayInTargetGroup );
        }

        // If move to a different group, rebuild the positions sequence within
        // the origin group
        if ( ( nIdOriginParent != nIdParentTarget ) || ( nIdOriginStep != nIdStepTarget ) )
        {
            List<FormDisplay> listDisplayInOriginGroup = FormDisplayHome.getFormDisplayListByParent( nIdOriginStep, nIdOriginParent );
            _formDisplayService.rebuildDisplayPositionSequence( listDisplayInOriginGroup );
        }
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
        List<Question> listQuestionOfForm = QuestionHome.getListQuestionByIdForm( nIdForm );

        for ( Question question : listQuestionOfForm )
        {
            if ( question.getCode( ).equals( strCode ) && nIdEntry != question.getIdEntry( ) )
            {
                return true;
            }
        }
        return false;
    }

}
