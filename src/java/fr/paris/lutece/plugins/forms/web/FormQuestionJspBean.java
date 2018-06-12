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

package fr.paris.lutece.plugins.forms.web;

import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.util.FormsEntryUtils;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
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
import fr.paris.lutece.plugins.forms.service.FormService;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * This class provides the user interface to manage Form features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageQuestions.jsp", controllerPath = "jsp/admin/plugins/forms/", right = "FORMS_MANAGEMENT" )
public class FormQuestionJspBean extends MVCAdminJspBean
{

    private static final long serialVersionUID = 7515975782241863390L;

    private static final String EMPTY_STRING = "";

    // Templates
    private static final String TEMPLATE_MANAGE_QUESTIONS = "/admin/plugins/forms/manage_questions.html";
    private static final String TEMPLATE_CREATE_GROUP = "/admin/plugins/forms/create_group.html";
    private static final String TEMPLATE_MODIFY_GROUP = "/admin/plugins/forms/modify_group.html";

    // Properties
    private static final String PROPERTY_CREATE_COMMENT_TITLE = "forms.create_Question.titleComment";
    private static final String PROPERTY_CREATE_QUESTION_TITLE = "forms.create_Question.titleQuestion";
    private static final String PROPERTY_CREATE_GROUP_TITLE = "forms.create_group.title";
    private static final String PROPERTY_MODIFY_COMMENT_TITLE = "forms.modifyEntry.titleComment";
    private static final String PROPERTY_MODIFY_QUESTION_TITLE = "forms.modifyEntry.titleQuestion";

    // Views
    private static final String VIEW_MANAGE_QUESTIONS = "manageQuestions";
    private static final String VIEW_CREATE_QUESTION = "createQuestion";
    private static final String VIEW_MODIFY_QUESTION = "modifyQuestion";
    private static final String VIEW_CREATE_GROUP = "createGroup";
    private static final String VIEW_MODIFY_GROUP = "modifyGroup";

    // Actions
    private static final String ACTION_CREATE_QUESTION = "createQuestion";
    private static final String ACTION_CREATE_QUESTION_AND_MANAGE_ENTRIES = "createQuestionAndManageEntries";
    private static final String ACTION_MODIFY_QUESTION = "modifyQuestion";
    private static final String ACTION_SAVE_QUESTION = "saveQuestion";
    private static final String ACTION_CREATE_GROUP = "createGroup";
    private static final String ACTION_MODIFY_GROUP = "modifyGroup";

    // Markers
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_ENTRY_TYPE_SERVICE = "entryTypeService";
    private static final String MARK_LIST_PARAM_DEFAULT_VALUES = "list_param_default_values";
    private static final String MARK_IS_AUTHENTIFICATION_ENABLED = "is_authentification_enabled";
    private static final String MARK_LIST = "list";



    // Error messages
    private static final String ERROR_QUESTION_NOT_CREATED = "forms.error.question.notCreated";
    private static final String ERROR_GROUP_NOT_CREATED = "forms.error.group.notCreated";
    private static final String ERROR_GROUP_NOT_UPDATED = "forms.error.group.notUpdated";
    private static final String ERROR_QUESTION_NOT_UPDATED = "forms.error.question.notUpdated";

    // Infos messages
    private static final String INFO_QUESTION_CREATED = "forms.info.question.created";
    private static final String INFO_QUESTION_UPDATED = "forms.info.question.updated";
    private static final String INFO_GROUP_UPDATED = "forms.info.group.updated";
    private static final String INFO_GROUP_CREATED = "forms.info.group.created";


    // Others
    private static final String ENTRY_COMMENT_TITLE = "forms.manage_questions.type.comment.title";

    private Step _step;
    private Form _form;
    private Entry _entry;
    private Question _question;
    private Group _group;

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
        int nIdStep = -1;
        List<ICompositeDisplay> listICompositeDisplay = new ArrayList<ICompositeDisplay>( );
        nIdStep = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );

        _step = StepHome.findByPrimaryKey( nIdStep );
        _form = FormHome.findByPrimaryKey( _step.getIdForm( ) );

        Map<String, Object> model = getModel( );
        model.put( FormsConstants.MARK_STEP, _step );

        listICompositeDisplay = FormService.getStepCompositeList( nIdStep );
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

        int nIdStep = -1;
        int nIdTypeEntry = -1;
        int nParentGroup;

        nIdStep = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );
        nIdTypeEntry = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_BUTTON_TYPE_ENTRY ) );
        nParentGroup = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY_PARENT ) );

        _step = StepHome.findByPrimaryKey( nIdStep );
        if ( ( _step == null ) )
        {
            return getJspManageForm( request );
        }

        _entry = FormsEntryUtils.createEntryByType( nIdTypeEntry );

        if ( ( _entry == null ) )
        {
            return getJspManageForm( request );
        }

        _entry.setIdResource( _step.getIdForm( ) );
        _entry.setResourceType( Form.RESOURCE_TYPE );

        _form = FormHome.findByPrimaryKey( _step.getIdForm( ) );

        // Default Values
        // TODO: implement EntryParameterService to handle default values
        // ReferenceList listParamDefaultValues = EntryParameterService.getService( ).findAll( );
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

        int nIdStep = -1;
        int nParentGroup;

        nIdStep = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );
        nParentGroup = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY_PARENT ) );

        if ( ( _step == null ) || nIdStep != _step.getId( ) )
        {
            _step = StepHome.findByPrimaryKey( nIdStep );
        }

        if ( ( _step == null ) )
        {
            return getJspManageForm( request );
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
        int nIdStep = -1;
        int nParentGroup = -1;

        nIdStep = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );
        nParentGroup = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY_PARENT ) );

        if ( ( _step == null ) || nIdStep != _step.getId( ) )
        {
            _step = StepHome.findByPrimaryKey( nIdStep );
        }

        _group = ( _group != null ) ? _group : new Group( );
        populate( _group, request );

        GroupHome.create( _group );

        int nDisplayDepth = getDisplayDepthFromParent( nParentGroup );

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

        int nIdStep = -1;
        int nIdGroup = -1;

        nIdStep = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );
        nIdGroup = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_GROUP ) );

        if ( ( _step == null ) || nIdStep != _step.getId( ) )
        {
            _step = StepHome.findByPrimaryKey( nIdStep );
        }

        if ( ( _step == null ) )
        {
            return getJspManageForm( request );
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
        int nIdStep = -1;

        nIdStep = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );

        if ( ( _step == null ) || nIdStep != _step.getId( ) )
        {
            _step = StepHome.findByPrimaryKey( nIdStep );
        }

        _group = ( _group != null ) ? _group : new Group( );
        populate( _group, request );

        GroupHome.update( _group );

        if ( _group.getId( ) == -1 )
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

        // TODO : redirect to modifyQuestion view
        return strReturnUrl != null ? strReturnUrl : redirect( request, VIEW_MANAGE_QUESTIONS, FormsConstants.PARAMETER_ID_STEP, _step.getId( ) );

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

        int nIdStep = -1;
        int nParentGroup = -1;
        int nIdType = -1;

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

        Question formQuestion = new Question( );
        String strTitle = _entry.getEntryType( ).getComment( ) ? I18nService.getLocalizedString( ENTRY_COMMENT_TITLE, getLocale( ) ) : _entry.getTitle( );
        formQuestion.setTitle( strTitle );
        formQuestion.setDescription( _entry.getComment( ) );
        formQuestion.setIdEntry( _entry.getIdEntry( ) );
        formQuestion.setIdStep( nIdStep );
        QuestionHome.create( formQuestion );

        int nDisplayDepth = getDisplayDepthFromParent( nParentGroup );

        if ( formQuestion.getId( ) != -1 )
        {
            FormDisplay formDisplay = new FormDisplay( );
            formDisplay.setFormId( _step.getIdForm( ) );
            formDisplay.setStepId( nIdStep );
            formDisplay.setParentId( nParentGroup );
            formDisplay.setCompositeId( formQuestion.getId( ) );
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

        int nIdStep = -1;

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
        int nIdQuestion = -1;

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
        
        int nIdEntry = -1;
        String strIdStep = request.getParameter( FormsConstants.PARAMETER_ID_STEP );
        int nIdStep = -1;

        if ( ( strIdStep != null ) && !strIdStep.equals( EMPTY_STRING ) )
        {
            nIdStep = Integer.parseInt( strIdStep );
        }
        if ( ( _step == null ) || ( _step.getId( ) != nIdStep ) )
        {
            _step = StepHome.findByPrimaryKey( nIdStep );
        }

        String strIdQuestion = request.getParameter( FormsConstants.PARAMETER_ID_QUESTION );
        int nIdQuestion = -1;

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
        return redirect( request, VIEW_MODIFY_QUESTION, FormsConstants.PARAMETER_ID_STEP, _step.getId( ), FormsConstants.PARAMETER_ID_QUESTION, _question.getId( ) );

    }
    /**
     * Gets the confirmation page of delete entry
     * 
     * @param request
     *            The HTTP request
     * @return the confirmation page of delete entry
     */
    public String getConfirmRemoveQuestion( HttpServletRequest request )
    {
        // TODO
        return null;
    }

    /**
     * Perform the entry suppression
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doRemoveEntry( HttpServletRequest request )
    {
        // TODO
        return null;
    }

    /**
     * Returns the display depth of a child display element
     * 
     * @param nParentGroup
     *            the Identifier of the parent display element (zero if we are at the step root)
     * 
     * @return the display depth
     */
    private int getDisplayDepthFromParent( int nParentGroup )
    {
        int nDisplayDepth = 0;
        if ( nParentGroup > 0 )
        {
            FormDisplay formDisplayParent = FormDisplayHome.findByPrimaryKey( nParentGroup );
            if ( formDisplayParent != null )
            {
                nDisplayDepth = formDisplayParent.getDepth( ) + 1;
            }
        }
        return nDisplayDepth;
    }

    /**
     * Return the URL of the JSP manage step
     * 
     * @param request
     *            The HTTP request
     * @return The URL of the JSP manage step
     */
    protected String getJspManageSteps( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + FormsConstants.JSP_MANAGE_STEPS;
    }

    /**
     * Return the URL of the JSP manage form
     * 
     * @param request
     *            The HTTP request
     * @return The URL of the JSP manage form
     */
    protected String getJspManageForm( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + FormsConstants.JSP_MANAGE_FORMS;
    }

    /**
     * Return the id of the current form
     * 
     * @return The id of the current form
     */
    protected int getFormId( )
    {

        return _form.getId( );
    }
}
