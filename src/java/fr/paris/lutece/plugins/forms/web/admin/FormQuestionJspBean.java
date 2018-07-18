/*
 * Copyright (c) 2002-2018, Mairie de Paris
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.ControlType;
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
import fr.paris.lutece.plugins.forms.service.EntryServiceManager;
import fr.paris.lutece.plugins.forms.service.FormDisplayService;
import fr.paris.lutece.plugins.forms.service.FormService;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.util.FormsDisplayUtils;
import fr.paris.lutece.plugins.forms.util.FormsEntryUtils;
import fr.paris.lutece.plugins.forms.web.ICompositeDisplay;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
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

    private static final long serialVersionUID = 7515975782241863390L;

    private static final String EMPTY_STRING = "";

    // Templates
    private static final String TEMPLATE_MANAGE_QUESTIONS = "/admin/plugins/forms/manage_questions.html";
    private static final String TEMPLATE_CREATE_GROUP = "/admin/plugins/forms/create_group.html";
    private static final String TEMPLATE_MODIFY_GROUP = "/admin/plugins/forms/modify_group.html";
    private static final String TEMPLATE_MOVE_COMPOSITE = "/admin/plugins/forms/move_composite.html";
    private static final String TEMPLATE_MANAGE_CONDITION = "/admin/plugins/forms/modify_condition.html";

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
    private static final String VIEW_MODIFY_CONDITION = "modifyCondition";

    // Actions
    private static final String ACTION_CREATE_QUESTION = "createQuestion";
    private static final String ACTION_CREATE_QUESTION_AND_MANAGE_ENTRIES = "createQuestionAndManageEntries";
    private static final String ACTION_MODIFY_QUESTION = "modifyQuestion";
    private static final String ACTION_SAVE_QUESTION = "saveQuestion";
    private static final String ACTION_CREATE_GROUP = "createGroup";
    private static final String ACTION_MODIFY_GROUP = "modifyGroup";
    private static final String ACTION_MOVE_COMPOSITE = "moveComposite";
    private static final String ACTION_REMOVE_COMPOSITE = "removeComposite";
    private static final String ACTION_MODIFY_CONDITION = "modifyCondition";

    // Markers
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_ENTRY_TYPE_SERVICE = "entryTypeService";
    private static final String MARK_LIST_PARAM_DEFAULT_VALUES = "list_param_default_values";
    private static final String MARK_IS_AUTHENTIFICATION_ENABLED = "is_authentification_enabled";
    private static final String MARK_LIST = "list";
    private static final String MARK_GROUP_VALIDATED = "groupValidated";
    private static final String MARK_STEP_VALIDATED = "stepValidated";

    // Parameters
    private static final String PARAMETER_VALUE_VALIDATE_STEP = "validateStep";
    private static final String PARAMETER_VALUE_VALIDATE_GROUP = "validateGroup";
    private static final String PARAMETER_VALUE_OLD_QUESTION = "old_question";

    // Error messages
    private static final String ERROR_QUESTION_NOT_CREATED = "forms.error.question.notCreated";
    private static final String ERROR_GROUP_NOT_CREATED = "forms.error.group.notCreated";
    private static final String ERROR_GROUP_NOT_UPDATED = "forms.error.group.notUpdated";
    private static final String ERROR_QUESTION_NOT_UPDATED = "forms.error.question.notUpdated";
    private static final String ERROR_STEP_OR_GROUP_NOT_VALIDATED = "forms.error.moveComposite.stepOrGroup.notvalidated";
    private static final String ERROR_OCCURED_MOVING_COMPOSITE = "forms.error.moveComposite.notCompleted";
    private static final String ERROR_MODIFY_CONDITION_QUESTION = "forms.error.modifyCondition.question";
    private static final String ERROR_MODIFY_CONDITION_VALIDATOR = "forms.error.modifyCondition.validator";

    // Infos messages
    private static final String INFO_QUESTION_CREATED = "forms.info.question.created";
    private static final String INFO_QUESTION_UPDATED = "forms.info.question.updated";
    private static final String INFO_GROUP_UPDATED = "forms.info.group.updated";
    private static final String INFO_GROUP_CREATED = "forms.info.group.created";
    private static final String INFO_MOVE_COMPOSITE_SUCCESSFUL = "forms.info.moveComposite.successful";
    private static final String INFO_DELETE_COMPOSITE_SUCCESSFUL = "forms.info.deleteComposite.successful";
    private static final String ENTRY_COMMENT_TITLE = "forms.manage_questions.type.comment.title";
    private static final String CONDITIONAL_DISPLAY_NONE = "forms.manageCondition.question.none";

    // Warning messages
    private static final String WARNING_CONFIRM_REMOVE_QUESTION = "forms.warning.deleteComposite.confirmRemoveQuestion";
    private static final String WARNING_CONFIRM_REMOVE_GROUP_ANY_QUESTIONS = "forms.warning.deleteComposite.confirmRemoveGroup";
    private static final String WARNING_CONFIRM_REMOVE_QUESTION_FORM_ACTIVE = "forms.warning.deleteComposite.confirmRemoveQuestion.formActive";
    private static final String WARNING_CONFIRM_REMOVE_GROUP_ANY_QUESTIONS_FORM_ACTIVE = "forms.warning.deleteComposite.confirmRemoveGroup.formActive";

    // Others
    private static final int INTEGER_MINUS_ONE = -1;

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
        int nIdStep = INTEGER_MINUS_ONE;
        List<ICompositeDisplay> listICompositeDisplay = new ArrayList<ICompositeDisplay>( );
        nIdStep = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );

        _step = StepHome.findByPrimaryKey( nIdStep );
        _form = FormHome.findByPrimaryKey( _step.getIdForm( ) );

        Map<String, Object> model = getModel( );
        model.put( FormsConstants.MARK_STEP, _step );
        model.put( FormsConstants.MARK_FORM, _form );

        listICompositeDisplay = _formService.getStepCompositeList( nIdStep );
        model.put( FormsConstants.MARK_COMPOSITE_LIST, listICompositeDisplay );

        model.put( FormsConstants.MARK_ENTRY_TYPE_REF_LIST, FormsEntryUtils.initListEntryType( ) );

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

        int nIdStep = INTEGER_MINUS_ONE;
        int nIdTypeEntry = INTEGER_MINUS_ONE;
        int nParentGroup;

        nIdStep = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );
        nIdTypeEntry = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_BUTTON_TYPE_ENTRY ) );
        nParentGroup = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY_PARENT ) );

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

        // Default Values
        // TODO: implement EntryParameterService to handle default values
        // ReferenceList listParamDefaultValues =
        // EntryParameterService.getService( ).findAll( );
        ReferenceList listParamDefaultValues = new ReferenceList( );

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( FormsConstants.MARK_ENTRY, _entry );
        model.put( FormsConstants.MARK_FORM, _form );
        model.put( FormsConstants.MARK_STEP, _step );
        model.put( FormsConstants.MARK_ID_PARENT, nParentGroup );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage( ) );
        model.put( MARK_LIST_PARAM_DEFAULT_VALUES, listParamDefaultValues );
        model.put( MARK_ENTRY_TYPE_SERVICE, EntryTypeServiceManager.getEntryTypeService( _entry ) );
        model.put( MARK_IS_AUTHENTIFICATION_ENABLED, SecurityService.isAuthenticationEnable( ) );

        if ( _entry.getEntryType( ).getComment( ) )
        {
            setPageTitleProperty( PROPERTY_CREATE_COMMENT_TITLE );
        }
        else
        {
            setPageTitleProperty( PROPERTY_CREATE_QUESTION_TITLE );
        }

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

        int nIdStep = INTEGER_MINUS_ONE;
        int nParentGroup;

        nIdStep = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );
        nParentGroup = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY_PARENT ) );

        if ( ( _step == null ) || nIdStep != _step.getId( ) )
        {
            _step = StepHome.findByPrimaryKey( nIdStep );
        }

        if ( ( _step == null ) )
        {
            return redirectToViewManageForm( request );
        }

        _form = FormHome.findByPrimaryKey( _step.getIdForm( ) );

        _group = new Group( );

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( FormsConstants.MARK_FORM, _form );
        model.put( FormsConstants.MARK_STEP, _step );
        model.put( FormsConstants.MARK_GROUP, _group );
        model.put( FormsConstants.MARK_ID_PARENT, nParentGroup );

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
        int nIdStep = INTEGER_MINUS_ONE;
        int nParentGroup = INTEGER_MINUS_ONE;

        nIdStep = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );
        nParentGroup = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY_PARENT ) );

        if ( ( _step == null ) || nIdStep != _step.getId( ) )
        {
            _step = StepHome.findByPrimaryKey( nIdStep );
        }

        _group = ( _group != null ) ? _group : new Group( );
        populate( _group, request );

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

        int nIdStep = INTEGER_MINUS_ONE;
        int nIdGroup = INTEGER_MINUS_ONE;

        nIdStep = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );
        nIdGroup = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_GROUP ) );

        if ( ( _step == null ) || nIdStep != _step.getId( ) )
        {
            _step = StepHome.findByPrimaryKey( nIdStep );
        }

        if ( ( _step == null ) )
        {
            return redirectToViewManageForm( request );
        }

        _form = FormHome.findByPrimaryKey( _step.getIdForm( ) );

        if ( _group == null || _group.getId( ) != nIdGroup )
        {
            _group = GroupHome.findByPrimaryKey( nIdGroup );
        }

        Map<String, Object> model = new HashMap<String, Object>( );
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
        int nIdStep = INTEGER_MINUS_ONE;

        nIdStep = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );

        if ( ( _step == null ) || nIdStep != _step.getId( ) )
        {
            _step = StepHome.findByPrimaryKey( nIdStep );
        }

        _group = ( _group != null ) ? _group : new Group( );
        populate( _group, request );

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
     * Perform the Question creation with its Entry
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_CREATE_QUESTION )
    public String doCreateQuestion( HttpServletRequest request )
    {
        String strReturnUrl = processQuestionCreation( request );

        if ( strReturnUrl != null )
        {
            return strReturnUrl;
        }
        else
        {
            addInfo( INFO_QUESTION_CREATED, getLocale( ) );
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
        String strReturnUrl = processQuestionCreation( request );

        return strReturnUrl != null ? strReturnUrl : redirect( request, VIEW_MODIFY_QUESTION, FormsConstants.PARAMETER_ID_STEP, _step.getId( ),
                FormsConstants.PARAMETER_ID_QUESTION, _question.getId( ) );

    }

    /**
     * Perform the Question creation with its Entry
     * 
     * @param request
     *            The HTTP request
     * @return The URL to display error message after performing the action or null if no error occurred
     */
    private String processQuestionCreation( HttpServletRequest request )
    {

        int nIdStep = INTEGER_MINUS_ONE;
        int nParentGroup = INTEGER_MINUS_ONE;
        int nIdType = INTEGER_MINUS_ONE;

        nIdStep = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );
        nParentGroup = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY_PARENT ) );
        nIdType = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_ENTRY_TYPE ) );

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

        if ( _entry.getFields( ) != null )
        {
            for ( Field field : _entry.getFields( ) )
            {
                field.setParentEntry( _entry );
                FieldHome.create( field );
            }
        }

        _question = new Question( );
        String strTitle = _entry.getEntryType( ).getComment( ) ? I18nService.getLocalizedString( ENTRY_COMMENT_TITLE, getLocale( ) ) : _entry.getTitle( );
        _question.setTitle( strTitle );
        _question.setDescription( _entry.getComment( ) );
        _question.setIdEntry( _entry.getIdEntry( ) );
        _question.setIdStep( nIdStep );
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
     * Gets the Question entry modification page
     * 
     * @param request
     *            The HTTP request
     * @return The entry modification page
     */
    @View( value = VIEW_MODIFY_QUESTION )
    public String getModifyQuestion( HttpServletRequest request )
    {

        int nIdStep = INTEGER_MINUS_ONE;

        String strIdStep = request.getParameter( FormsConstants.PARAMETER_ID_STEP );

        if ( ( strIdStep != null ) && !strIdStep.equals( EMPTY_STRING ) )
        {
            nIdStep = Integer.parseInt( strIdStep );

            if ( ( _step == null ) || ( _step.getId( ) != nIdStep ) )
            {
                _step = StepHome.findByPrimaryKey( nIdStep );
            }
        }

        String strIdQuestion = request.getParameter( FormsConstants.PARAMETER_ID_QUESTION );
        int nIdQuestion = INTEGER_MINUS_ONE;

        if ( ( strIdQuestion != null ) && !strIdQuestion.equals( EMPTY_STRING ) )
        {
            nIdQuestion = Integer.parseInt( strIdQuestion );
        }

        _question = QuestionHome.findByPrimaryKey( nIdQuestion );

        _entry = EntryHome.findByPrimaryKey( _question.getIdEntry( ) );

        List<Field> listField = new ArrayList<Field>( _entry.getFields( ).size( ) );

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

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( FormsConstants.MARK_ENTRY, _entry );
        model.put( FormsConstants.MARK_FORM, _form );
        model.put( FormsConstants.MARK_STEP, _step );
        model.put( FormsConstants.MARK_QUESTION, _question );

        model.put( MARK_LIST, _entry.getFields( ) );

        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage( ) );
        model.put( MARK_ENTRY_TYPE_SERVICE, EntryTypeServiceManager.getEntryTypeService( _entry ) );
        model.put( MARK_IS_AUTHENTIFICATION_ENABLED, SecurityService.isAuthenticationEnable( ) );

        if ( _entry.getEntryType( ).getComment( ) )
        {
            setPageTitleProperty( PROPERTY_MODIFY_COMMENT_TITLE );
        }
        else
        {
            setPageTitleProperty( PROPERTY_MODIFY_QUESTION_TITLE );
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
    public String processQuestionUpdate( HttpServletRequest request )
    {

        int nIdEntry = INTEGER_MINUS_ONE;
        String strIdStep = request.getParameter( FormsConstants.PARAMETER_ID_STEP );
        int nIdStep = -INTEGER_MINUS_ONE;

        if ( ( strIdStep != null ) && !strIdStep.equals( EMPTY_STRING ) )
        {
            nIdStep = Integer.parseInt( strIdStep );
        }
        if ( ( _step == null ) || ( _step.getId( ) != nIdStep ) )
        {
            _step = StepHome.findByPrimaryKey( nIdStep );
        }

        String strIdQuestion = request.getParameter( FormsConstants.PARAMETER_ID_QUESTION );
        int nIdQuestion = INTEGER_MINUS_ONE;

        if ( ( strIdQuestion != null ) && !strIdQuestion.equals( EMPTY_STRING ) )
        {
            nIdQuestion = Integer.parseInt( strIdQuestion );
        }

        if ( ( _question == null ) || ( _question.getId( ) != nIdQuestion ) )
        {
            _question = QuestionHome.findByPrimaryKey( nIdQuestion );
        }

        nIdEntry = _question.getIdEntry( );
        _entry = EntryHome.findByPrimaryKey( nIdEntry );

        String strError = EntryTypeServiceManager.getEntryTypeService( _entry ).getRequestData( _entry, request, getLocale( ) );

        if ( strError != null )
        {
            return strError;
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

        String strTitle = _entry.getEntryType( ).getComment( ) ? I18nService.getLocalizedString( ENTRY_COMMENT_TITLE, getLocale( ) ) : _entry.getTitle( );
        _question.setTitle( strTitle );
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
        String strReturnUrl = processQuestionUpdate( request );

        if ( strReturnUrl != null )
        {
            addError( ERROR_QUESTION_NOT_UPDATED, getLocale( ) );
            return strReturnUrl;
        }
        else
        {
            addInfo( INFO_QUESTION_UPDATED, getLocale( ) );
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
        String strReturnUrl = processQuestionUpdate( request );

        if ( strReturnUrl != null )
        {
            return strReturnUrl;
        }
        return redirect( request, VIEW_MODIFY_QUESTION, FormsConstants.PARAMETER_ID_STEP, _step.getId( ), FormsConstants.PARAMETER_ID_QUESTION,
                _question.getId( ) );

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

        int nIdStep = INTEGER_MINUS_ONE;
        int nIdDisplay = INTEGER_MINUS_ONE;
        String strMessage = StringUtils.EMPTY;

        nIdStep = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), INTEGER_MINUS_ONE );

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

        nIdDisplay = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY ), INTEGER_MINUS_ONE );

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
        int nIdDisplay = INTEGER_MINUS_ONE;

        nIdDisplay = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY ), INTEGER_MINUS_ONE );

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
        int nIdStepTarget = INTEGER_MINUS_ONE;
        Step stepTarget = new Step( );
        int nIdParentTarget = INTEGER_MINUS_ONE;
        int nIdDisplay = INTEGER_MINUS_ONE;
        int nTargetPosition = INTEGER_MINUS_ONE;
        boolean bStepValidated = false;
        boolean bGroupValidated = false;

        nIdDisplay = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY ), INTEGER_MINUS_ONE );

        if ( nIdDisplay == INTEGER_MINUS_ONE )
        {
            redirectToViewManageForm( request );
        }

        if ( _formDisplay == null || _formDisplay.getId( ) != nIdDisplay )
        {
            _formDisplay = FormDisplayHome.findByPrimaryKey( nIdDisplay );
        }
        nTargetPosition = _formDisplay.getDisplayOrder( );

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

        if ( StringUtils.isNotBlank( strValidateButtonValue ) )
        {
            nTargetPosition = 1;

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

        nIdStepTarget = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), -1 );
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

        nIdParentTarget = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_PARENT ), -1 );
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
        int nIdStepTarget = INTEGER_MINUS_ONE;
        int nIdParentTarget = INTEGER_MINUS_ONE;
        int nIdDisplay = INTEGER_MINUS_ONE;
        int nDisplayOrderTarget = INTEGER_MINUS_ONE;
        boolean bStepValidated = false;
        boolean bGroupValidated = false;

        nIdDisplay = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY ), -1 );

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

        nIdStepTarget = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), INTEGER_MINUS_ONE );
        nIdParentTarget = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_PARENT ), INTEGER_MINUS_ONE );
        nDisplayOrderTarget = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_DISPLAY_ORDER ), INTEGER_MINUS_ONE );

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
     * Gets the Conditional display component page
     * 
     * @param request
     *            The HTTP request
     * @return The conditional display component page
     */
    @View( value = VIEW_MODIFY_CONDITION )
    public String getModifyCondition( HttpServletRequest request )
    {

        // Get the display we are working on
        int nIdDisplay = INTEGER_MINUS_ONE;

        nIdDisplay = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY ), INTEGER_MINUS_ONE );

        if ( nIdDisplay == INTEGER_MINUS_ONE )
        {
            return redirectView( request, VIEW_MANAGE_QUESTIONS );
        }

        FormDisplay display = FormDisplayHome.findByPrimaryKey( nIdDisplay );

        // Retrieve the attached control
        Control control = ControlHome.getConditionalDisplayControlByDisplay( nIdDisplay );
        if ( control == null )
        {
            control = new Control( );
        }

        // The selected question should be the input parameter
        int nIdQuestion = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_QUESTION ), INTEGER_MINUS_ONE );

        // If not specified, it should be the question stored in the Control
        if ( nIdQuestion == INTEGER_MINUS_ONE )
        {
            nIdQuestion = control.getIdQuestion( );
        }

        ReferenceList validatorList = new ReferenceList( );

        // If still nothing, no question is loaded, therefore no validators

        Question selectedQuestion = QuestionHome.findByPrimaryKey( nIdQuestion );

        // Get all available validators
        if ( selectedQuestion != null )
        {
            validatorList = EntryServiceManager.getInstance( ).getAvailableValidator( selectedQuestion.getEntry( ).getEntryType( ) );
        }

        // Get all questions from current step
        // TODO Only get relevant question (not child questions)
        List<Question> availableQuestions = QuestionHome.getQuestionsListByStep( display.getStepId( ) );

        // Sort questions in alphabetical order
        availableQuestions.sort( new Comparator<Question>( )
        {
            public int compare( Question q1, Question q2 )
            {
                return q1.getTitle( ).compareTo( q2.getTitle( ) );
            };
        } );

        // Put all the questions in a ReferenceList containing with their id and
        // label
        ReferenceList questionsRefList = new ReferenceList( );
        questionsRefList.addItem( NumberUtils.INTEGER_ZERO, I18nService.getLocalizedString( CONDITIONAL_DISPLAY_NONE, getLocale( ) ) );
        for ( Question question : availableQuestions )
        {
            questionsRefList.addItem( question.getId( ), question.getTitle( ) );
        }

        // Fill the model
        Map<String, Object> model = getModel( );
        model.put( FormsConstants.MARK_ID_STEP, display.getStepId( ) );
        model.put( FormsConstants.MARK_ID_QUESTION, nIdQuestion );
        model.put( FormsConstants.MARK_ID_DISPLAY, nIdDisplay );
        model.put( FormsConstants.MARK_LIST_QUESTIONS, questionsRefList );
        model.put( FormsConstants.MARK_LIST_VALIDATORS, validatorList );
        model.put( FormsConstants.MARK_CONTROL, control );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_CONDITION, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Action to process Condition modification
     * 
     * @param request
     *            The httpRequest
     * 
     * @return the manage question page
     */
    @Action( value = ACTION_MODIFY_CONDITION )
    public String doModifyCondition( HttpServletRequest request )
    {
        int nIdDisplay = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY ), INTEGER_MINUS_ONE );

        int nIdStep = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), INTEGER_MINUS_ONE );

        // Possibly Existing condition
        Control oldControl = ControlHome.getConditionalDisplayControlByDisplay( nIdDisplay );

        int nIdQuestion = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_QUESTION ), INTEGER_MINUS_ONE );

        int nIdOldQuestion = NumberUtils.toInt( request.getParameter( PARAMETER_VALUE_OLD_QUESTION ), INTEGER_MINUS_ONE );

        String strValidatorName = request.getParameter( FormsConstants.PARAMETER_VALIDATOR_NAME );

        if ( nIdQuestion <= NumberUtils.INTEGER_ZERO )
        {
            // No condition were selected, delete existing condition
            if ( oldControl != null )
            {
                ControlHome.remove( oldControl.getId( ) );
            }
        }
        else
        {

            if ( nIdOldQuestion > NumberUtils.INTEGER_ZERO && nIdOldQuestion != nIdQuestion )
            {
                // a different question was selected, and not validated
                addError( ERROR_MODIFY_CONDITION_QUESTION, request.getLocale( ) );
                return redirect( request, VIEW_MODIFY_CONDITION, FormsConstants.PARAMETER_ID_DISPLAY, nIdDisplay );
            }

            if ( strValidatorName == null || EMPTY_STRING.equals( strValidatorName ) )
            {
                // No validator selected
                addError( ERROR_MODIFY_CONDITION_VALIDATOR, request.getLocale( ) );
                return redirect( request, VIEW_MODIFY_CONDITION, FormsConstants.PARAMETER_ID_DISPLAY, nIdDisplay );
            }

            Control control = new Control( );

            if ( oldControl != null )
            {
                control.setId( oldControl.getId( ) );
            }

            // Assign values
            control.setValidatorName( strValidatorName );
            control.setIdQuestion( nIdQuestion );
            control.setIdTargetFormDisplay( nIdDisplay );
            control.setValue( request.getParameter( FormsConstants.PARAMETER_CONTROL_VALUE ) );
            control.setControlType( ControlType.CONDITIONAL.getLabel( ) );

            // Save or update Control
            if ( control.getId( ) > NumberUtils.INTEGER_ZERO )
            {
                ControlHome.update( control );
            }
            else
            {
                ControlHome.create( control );
            }

        }
        return redirect( request, VIEW_MANAGE_QUESTIONS, FormsConstants.PARAMETER_ID_STEP, nIdStep );
    }

}
