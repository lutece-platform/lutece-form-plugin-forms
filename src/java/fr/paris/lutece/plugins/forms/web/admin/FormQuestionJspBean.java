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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.service.FormDatabaseService;
import fr.paris.lutece.plugins.forms.service.FormDisplayService;
import fr.paris.lutece.plugins.forms.service.FormService;
import fr.paris.lutece.plugins.forms.service.IFormDatabaseService;
import fr.paris.lutece.plugins.forms.service.IFormDisplayService;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.util.FormsEntryUtils;
import fr.paris.lutece.plugins.forms.web.ICompositeDisplay;
import fr.paris.lutece.plugins.forms.web.exception.CodeAlreadyExistsException;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class provides the user interface to manage Form features ( manage, create, modify, remove )
 */

@Controller( controllerJsp = "ManageQuestions.jsp", controllerPath = "jsp/admin/plugins/forms/", right = "FORMS_MANAGEMENT" )
public class FormQuestionJspBean extends AbstractFormQuestionJspBean
{
    private static final long serialVersionUID = 7515975782241863390L;

    // Templates
    private static final String TEMPLATE_MANAGE_QUESTIONS = "/admin/plugins/forms/manage_questions.html";
    private static final String TEMPLATE_CREATE_GROUP = "/admin/plugins/forms/create_group.html";
    private static final String TEMPLATE_MODIFY_GROUP = "/admin/plugins/forms/modify_group.html";
    private static final String TEMPLATE_MOVE_COMPOSITE = "/admin/plugins/forms/move_composite.html";
    private static final String TEMPLATE_CREATE_QUESTION = "/admin/plugins/forms/create_question.html";
    private static final String TEMPLATE_MODIFY_QUESTION = "/admin/plugins/forms/modify_question.html";
    private static final String TEMPLATE_BREADCRUMBS = "/admin/plugins/forms/entries/all_entry_breadcrumbs.html";

    // Properties
    private static final String PROPERTY_CREATE_GROUP_TITLE = "forms.create_group.title";

    // Views
    private static final String VIEW_MANAGE_QUESTIONS = "manageQuestions";

    // Actions
    private static final String ACTION_REMOVE_FILE = "removeFileComment";

    // Warning messages
    private static final String WARNING_CONFIRM_REMOVE_QUESTION_FORM_ACTIVE = "forms.warning.deleteComposite.confirmRemoveQuestion.formActive";
    private static final String WARNING_CONFIRM_REMOVE_GROUP_ANY_QUESTIONS_FORM_ACTIVE = "forms.warning.deleteComposite.confirmRemoveGroup.formActive";

    private static final FormService _formService = SpringContextService.getBean( FormService.BEAN_NAME );

    private Form _form;

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
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_SAVE_QUESTION ) );

        setPageTitleProperty( StringUtils.EMPTY );
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
        Map<String, Object> model = initCreateQuestionModel( request );
        if ( model == null )
        {
            return redirectToViewManageForm( request );
        }

        _form = FormHome.findByPrimaryKey( _step.getIdForm( ) );
        model.put( FormsConstants.MARK_FORM, _form );
        model.put( MARK_ADD_FILE_COMMENT, true );
        model.put( MARK_ACTION, "jsp/admin/plugins/forms/ManageQuestions.jsp" );
        model.put( FormsConstants.MARK_BREADCRUMBS, AppTemplateService.getTemplate( TEMPLATE_BREADCRUMBS, request.getLocale( ), model ).getHtml( ) );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_SAVE_QUESTION ) );
        model.put( FormsConstants.MARK_QUESTION_CREATE_TEMPLATE,
                AppTemplateService.getTemplate( TEMPLATE_CREATE_QUESTION, request.getLocale( ), model ).getHtml( ) );

        IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( _entry );
        HtmlTemplate template = AppTemplateService.getTemplate( entryTypeService.getTemplateCreate( _entry, false ), getLocale( ), model );
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

        Map<String, Object> model = initCreateGroupModel( request );
        if ( model == null )
        {
            return redirectToViewManageForm( request );
        }
        _form = FormHome.findByPrimaryKey( _step.getIdForm( ) );
        model.put( FormsConstants.MARK_FORM, _form );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_GROUP ) );

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
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_GROUP )
    public String doCreateGroup( HttpServletRequest request ) throws AccessDeniedException
    {
        // CSRF Token control
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_GROUP ) )
        {
            throw new AccessDeniedException( MESSAGE_ERROR_TOKEN );
        }
        return createGroup( request, VIEW_MANAGE_QUESTIONS );
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
        Map<String, Object> model = initModifyGroupModel( request );
        if ( model == null )
        {
            return redirectToViewManageForm( request );
        }

        _form = FormHome.findByPrimaryKey( _step.getIdForm( ) );
        model.put( FormsConstants.MARK_FORM, _form );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_GROUP ) );

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
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_GROUP )
    public String doModifyGroup( HttpServletRequest request ) throws AccessDeniedException
    {
        // CSRF Token control
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_GROUP ) )
        {
            throw new AccessDeniedException( MESSAGE_ERROR_TOKEN );
        }
        return modifyGroup( request, VIEW_MANAGE_QUESTIONS );
    }

    /**
     * Perform the Question creation with its Entry
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_QUESTION )
    public String doCreateQuestion( HttpServletRequest request ) throws AccessDeniedException
    {
        // CSRF Token control
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_SAVE_QUESTION ) )
        {
            throw new AccessDeniedException( MESSAGE_ERROR_TOKEN );
        }
        try
        {
            String strReturnUrl = processQuestionCreation( request, VIEW_MANAGE_QUESTIONS );

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
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_QUESTION_AND_MANAGE_ENTRIES )
    public String doCreateQuestionAndManageEntries( HttpServletRequest request ) throws AccessDeniedException
    {
        // CSRF Token control
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_SAVE_QUESTION ) )
        {
            throw new AccessDeniedException( MESSAGE_ERROR_TOKEN );
        }
        try
        {
            String strReturnUrl = processQuestionCreation( request, VIEW_MANAGE_QUESTIONS );
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
     * Gets the Question entry modification page
     * 
     * @param request
     *            The HTTP request
     * @return The entry modification page
     */
    @View( value = VIEW_MODIFY_QUESTION )
    public String getModifyQuestion( HttpServletRequest request )
    {
        Map<String, Object> model = initModifyQuestionModel( request );

        if ( ( _form == null ) || ( _form.getId( ) != _entry.getIdResource( ) ) )
        {
            _form = FormHome.findByPrimaryKey( _entry.getIdResource( ) );
        }
        model.put( FormsConstants.MARK_FORM, _form );
        model.put( MARK_ADD_FILE_COMMENT, true );
        model.put( MARK_ACTION, "jsp/admin/plugins/forms/ManageQuestions.jsp" );
        model.put( FormsConstants.MARK_BREADCRUMBS, AppTemplateService.getTemplate( TEMPLATE_BREADCRUMBS, request.getLocale( ), model ).getHtml( ) );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_SAVE_QUESTION ) );
        model.put( FormsConstants.MARK_QUESTION_MODIFY_TEMPLATE,
                AppTemplateService.getTemplate( TEMPLATE_MODIFY_QUESTION, request.getLocale( ), model ).getHtml( ) );

        IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( _entry );
        HtmlTemplate template = AppTemplateService.getTemplate( entryTypeService.getTemplateModify( _entry, false ), getLocale( ), model );
        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Perform the Question update with its Entry
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_QUESTION )
    public String doModifyQuestion( HttpServletRequest request ) throws AccessDeniedException
    {
        // CSRF Token control
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_SAVE_QUESTION ) )
        {
            throw new AccessDeniedException( MESSAGE_ERROR_TOKEN );
        }
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
     * @throws AccessDeniedException
     */
    @Action( ACTION_SAVE_QUESTION )
    public String doSaveQuestion( HttpServletRequest request ) throws AccessDeniedException
    {
        // CSRF Token control
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_SAVE_QUESTION ) )
        {
            throw new AccessDeniedException( MESSAGE_ERROR_TOKEN );
        }
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
     * @throws AccessDeniedException
     */
    @Action( value = ACTION_DUPLICATE_QUESTION )
    public String doDuplicateQuestion( HttpServletRequest request ) throws AccessDeniedException
    {
        // CSRF Token control
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_SAVE_QUESTION ) )
        {
            throw new AccessDeniedException( MESSAGE_ERROR_TOKEN );
        }
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
            _fileStoreProvider.delete( oldFile.getValue( ) );
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
        int nIdStep = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), INTEGER_MINUS_ONE );
        int nIdDisplay = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY ), INTEGER_MINUS_ONE );

        if ( nIdStep == INTEGER_MINUS_ONE || nIdDisplay == INTEGER_MINUS_ONE )
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

        if ( _formDisplay == null || _formDisplay.getId( ) != nIdDisplay )
        {
            _formDisplay = FormDisplayHome.findByPrimaryKey( nIdDisplay );
        }

        String strMessage = getConfirmMessageRemoveQuestion( _form, _formDisplay );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_COMPOSITE ) );
        url.addParameter( FormsConstants.PARAMETER_ID_DISPLAY, nIdDisplay );
        url.addParameter( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_REMOVE_COMPOSITE ) );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, strMessage, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    private String getConfirmMessageRemoveQuestion( Form form, FormDisplay formDisplay )
    {
        String strMessage = StringUtils.EMPTY;
        if ( CompositeDisplayType.QUESTION.getLabel( ).equalsIgnoreCase( formDisplay.getCompositeType( ) ) )
        {
            if ( form.isActive( ) )
            {
                strMessage = WARNING_CONFIRM_REMOVE_QUESTION_FORM_ACTIVE;
            }
            else
            {
                strMessage = WARNING_CONFIRM_REMOVE_QUESTION;
            }
        }
        else
            if ( CompositeDisplayType.GROUP.getLabel( ).equalsIgnoreCase( formDisplay.getCompositeType( ) ) )
            {
                if ( form.isActive( ) )
                {
                    strMessage = WARNING_CONFIRM_REMOVE_GROUP_ANY_QUESTIONS_FORM_ACTIVE;
                }
                else
                {
                    strMessage = WARNING_CONFIRM_REMOVE_GROUP_ANY_QUESTIONS;
                }

            }
        return strMessage;
    }

    /**
     * Perform the question suppression
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     * @throws AccessDeniedException
     */
    @Action( ACTION_REMOVE_COMPOSITE )
    public String doRemoveComposite( HttpServletRequest request ) throws AccessDeniedException
    {

        // CSRF Token control
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_REMOVE_COMPOSITE ) )
        {
            throw new AccessDeniedException( MESSAGE_ERROR_TOKEN );
        }
        int nIdDisplay = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY ), -1 );
        if ( _formDisplay == null || _formDisplay.getId( ) != nIdDisplay )
        {
            _formDisplay = FormDisplayHome.findByPrimaryKey( nIdDisplay );
        }

        if ( _formDisplay == null )
        {
            redirectToViewManageForm( request );
        }

        getFormDisplayService( ).deleteDisplayAndDescendants( nIdDisplay );

        List<FormDisplay> listFormDisplaySibling = getFormDatabaseService( ).getFormDisplayListByParent( _formDisplay.getStepId( ),
                _formDisplay.getParentId( ) );
        getFormDisplayService( ).rebuildDisplayPositionSequence( listFormDisplaySibling );
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
        Map<String, Object> model = initMoveCompositeModel( request );
        if ( model == null )
        {
            return redirectToViewManageForm( request );
        }

        Step stepTarget = StepHome.findByPrimaryKey( (int) model.get( FormsConstants.MARK_ID_STEP ) );
        ReferenceList listFormSteps = StepHome.getStepReferenceListByForm( stepTarget.getIdForm( ) );
        model.put( FormsConstants.MARK_LIST_STEPS, listFormSteps );

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

    @Override
    protected IFormDatabaseService initFormDatabaseService( )
    {
        return SpringContextService.getBean( FormDatabaseService.BEAN_NAME );
    }

    @Override
    protected IFormDisplayService initFormDisplayService( )
    {
        return SpringContextService.getBean( FormDisplayService.BEAN_NAME );
    }
}
