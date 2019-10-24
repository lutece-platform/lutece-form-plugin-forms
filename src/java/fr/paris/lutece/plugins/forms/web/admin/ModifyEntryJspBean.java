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
package fr.paris.lutece.plugins.forms.web.admin;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.service.FormsResourceIdService;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.util.FormsEntryUtils;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.business.role.RoleHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.string.StringUtil;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * This class provides the user interface to manage Form features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ModifyEntry.jsp", controllerPath = "jsp/admin/plugins/forms/", right = "FORMS_MANAGEMENT" )
public class ModifyEntryJspBean extends AbstractJspBean
{

    /**
     * Right to manage forms
     */
    public static final String RIGHT_MANAGE_FORMS = "FORMS_MANAGEMENT";

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 2261595222578282162L;

    // templates
    private static final String TEMPLATE_CREATE_FIELD = "admin/plugins/forms/create_field.html";
    private static final String TEMPLATE_MODIFY_FIELD = "admin/plugins/forms/modify_field.html";

    // message
    private static final String MESSAGE_CONFIRM_REMOVE_FIELD = "forms.message.confirmRemoveField";
    private static final String MESSAGE_MANDATORY_FIELD = "forms.message.mandatory.field";
    private static final String MESSAGE_FIELD_VALUE_FIELD = "forms.message.field_value_field";
    private static final String FIELD_TITLE_FIELD = "forms.createField.labelTitle";
    private static final String FIELD_VALUE_FIELD = "forms.createField.labelValue";

    // properties
    private static final String PROPERTY_CREATE_FIELD_TITLE = "forms.createField.title";
    private static final String PROPERTY_MODIFY_FIELD_TITLE = "forms.modifyField.title";

    private static final String MARK_ROLE_REF_LIST = "role_list";
    private static final String MARK_OPTION_NO_DISPLAY_TITLE = "option_no_display_title";

    // Jsp Definition
    private static final String JSP_MODIFY_QUESTION = "jsp/admin/plugins/forms/ManageQuestions.jsp";

    // parameters form
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_VALUE = "value";
    private static final String PARAMETER_DEFAULT_VALUE = "default_value";
    private static final String PARAMETER_NO_DISPLAY_TITLE = "no_display_title";
    private static final String PARAMETER_COMMENT = "comment";
    private static final String PARAMETER_ROLE_KEY = "role_key";
    private static final String PARAMETER_OPTION_NO_DISPLAY_TITLE = "option_no_display_title";

    // Views
    private static final String VIEW_MODIFY_FIELD = "modifyField";
    private static final String VIEW_CREATE_FIELD = "createField";
    private static final String VIEW_CONFIRM_REMOVE_FIELD = "confirmRemoveField";
    private static final String VIEW_MANAGE_QUESTION = "manageQuestions";
    private static final String VIEW_MODIFY_QUESTION = "modifyQuestion";

    // Actions
    private static final String ACTION_CREATE_FIELD = "createField";
    private static final String ACTION_MODIFY_FIELD = "modifyField";
    private static final String ACTION_REMOVE_FIELD = "removeField";
    private static final String ACTION_MOVE_FIELD_UP = "moveFieldUp";
    private static final String ACTION_MOVE_FIELD_DOWN = "moveFieldDown";

    // other constants
    private static final String EMPTY_STRING = "";
    private static final String ANCHOR_LIST = "list";

    private int _nIdEntry = -1;
    private Step _step;
    private Question _question;
    private Field _field;

    /* -------- Fields management ---------- */

    /**
     * Gets the field creation page
     * 
     * @param request
     *            The HTTP request
     * @return the field creation page
     */
    @View( value = VIEW_CREATE_FIELD )
    public String getCreateField( HttpServletRequest request )
    {

        if ( !updateStepAndQuestion( request ) )
        {
            return redirectToViewManageForm( request );
        }

        _nIdEntry = _question.getIdEntry( );
        Entry entry = EntryHome.findByPrimaryKey( _nIdEntry );

        if ( ( ( _nIdEntry == -1 ) || ( entry == null ) )
                || !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _step.getIdForm( ), FormsResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return getJspManageQuestions( request, _step.getId( ) );
        }

        _field = new Field( );
        _field.setParentEntry( entry );

        Map<String, Object> model = new HashMap<String, Object>( );
        Locale locale = getLocale( );

        if ( request.getParameter( PARAMETER_OPTION_NO_DISPLAY_TITLE ) != null )
        {
            model.put( MARK_OPTION_NO_DISPLAY_TITLE, true );
        }
        else
        {
            model.put( MARK_OPTION_NO_DISPLAY_TITLE, false );
        }

        model.put( FormsConstants.MARK_FIELD, _field );
        model.put( FormsConstants.MARK_QUESTION, _question );
        model.put( FormsConstants.MARK_ID_ENTRY, _nIdEntry );
        if ( SecurityService.isAuthenticationEnable( ) )
        {
            model.put( MARK_ROLE_REF_LIST, RoleHome.getRolesList( ) );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_FIELD, locale, model );
        setPageTitleProperty( PROPERTY_CREATE_FIELD_TITLE );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Gets the field modification page
     * 
     * @param request
     *            The HTTP request
     * @return the field modification page
     */
    @View( value = VIEW_MODIFY_FIELD )
    public String getModifyField( HttpServletRequest request )
    {
        if ( !updateStepAndQuestion( request ) )
        {
            return redirectToViewManageForm( request );
        }

        if ( request.getParameter( FormsConstants.PARAMETER_ID_FIELD ) == null )
        {
            return redirectToViewManageForm( request );
        }

        String strIdField = request.getParameter( FormsConstants.PARAMETER_ID_FIELD );

        int nIdField = -1;

        try
        {
            nIdField = Integer.parseInt( strIdField );
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( ne );

            return getJspManageQuestions( request, _step.getId( ) );
        }

        Field field = null;

        if ( nIdField != -1 )
        {
            field = FieldHome.findByPrimaryKey( nIdField );
        }

        if ( ( field == null )
                || !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _step.getIdForm( ), FormsResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return getJspManageQuestions( request, _step.getId( ) );
        }

        Entry entry = null;

        _nIdEntry = _question.getIdEntry( );

        entry = EntryHome.findByPrimaryKey( field.getParentEntry( ).getIdEntry( ) );

        field.setParentEntry( entry );

        HashMap<String, Object> model = new HashMap<String, Object>( );
        Locale locale = getLocale( );

        if ( request.getParameter( PARAMETER_OPTION_NO_DISPLAY_TITLE ) != null )
        {
            model.put( MARK_OPTION_NO_DISPLAY_TITLE, true );
        }
        else
        {
            model.put( MARK_OPTION_NO_DISPLAY_TITLE, false );
        }

        model.put( FormsConstants.MARK_FIELD, field );
        model.put( FormsConstants.MARK_QUESTION, _question );
        model.put( FormsConstants.MARK_ID_ENTRY, _nIdEntry );

        if ( SecurityService.isAuthenticationEnable( ) )
        {
            model.put( MARK_ROLE_REF_LIST, RoleHome.getRolesList( ) );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_FIELD, locale, model );

        setPageTitleProperty( PROPERTY_MODIFY_FIELD_TITLE );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Perform creation field
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_CREATE_FIELD )
    public String doCreateField( HttpServletRequest request )
    {

        String strIdEntry = request.getParameter( FormsConstants.PARAMETER_ID_ENTRY );
        _nIdEntry = Integer.parseInt( strIdEntry );

        if ( !updateStepAndQuestion( request ) )
        {
            return redirectToViewManageForm( request );
        }

        Entry entry = new Entry( );
        entry.setIdEntry( _nIdEntry );

        Field field = new Field( );
        field.setParentEntry( entry );

        String strError = getFieldData( request, field );

        if ( strError != null )
        {
            return redirect( request, strError );
        }

        FieldHome.create( field );

        return redirectToViewModifyQuestion( request, _step.getId( ), _question.getId( ) );
    }

    /**
     * Perform modification field
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_MODIFY_FIELD )
    public String doModifyField( HttpServletRequest request )
    {
        Field field = null;

        String strIdField = request.getParameter( FormsConstants.PARAMETER_ID_FIELD );
        int nIdField = -1;

        if ( !updateStepAndQuestion( request ) )
        {
            return redirectToViewManageForm( request );
        }

        if ( strIdField != null )
        {
            try
            {
                nIdField = Integer.parseInt( strIdField );
            }
            catch( NumberFormatException ne )
            {
                AppLogService.error( ne );
            }
        }

        if ( ( nIdField != -1 )
                || !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _step.getIdForm( ), FormsResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            field = FieldHome.findByPrimaryKey( nIdField );

            String strError = getFieldData( request, field );

            if ( strError != null )
            {
                return redirect( request, strError );
            }

            FieldHome.update( field );

        }
        else
        {
            return getJspManageQuestions( request, _step.getId( ) );
        }

        return redirectToViewModifyQuestion( request, _step.getId( ), _question.getId( ) );
    }

    /**
     * Gets the confirmation page of delete field
     * 
     * @param request
     *            The HTTP request
     * @return the html code to confirm
     */
    @View( value = VIEW_CONFIRM_REMOVE_FIELD )
    public String getConfirmRemoveField( HttpServletRequest request )
    {
        if ( !updateStepAndQuestion( request ) )
        {
            return redirectToViewManageForm( request );
        }

        if ( ( request.getParameter( FormsConstants.PARAMETER_ID_FIELD ) == null )
                || !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _step.getIdForm( ), FormsResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return redirectToViewManageForm( request );
        }

        String strIdField = request.getParameter( FormsConstants.PARAMETER_ID_FIELD );

        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_FIELD ) );
        url.addParameter( FormsConstants.PARAMETER_ID_FIELD, strIdField );
        url.addParameter( FormsConstants.PARAMETER_ID_QUESTION, _question.getId( ) );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_FIELD, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Perform suppression field
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_REMOVE_FIELD )
    public String doRemoveField( HttpServletRequest request )
    {
        String strIdField = request.getParameter( FormsConstants.PARAMETER_ID_FIELD );
        int nIdField = -1;

        if ( !updateStepAndQuestion( request ) )
        {
            return redirectToViewManageForm( request );
        }

        if ( ( strIdField == null )
                || !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _step.getIdForm( ), FormsResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return redirectToViewManageForm( request );
        }

        try
        {
            nIdField = Integer.parseInt( strIdField );
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( ne );

            return redirectToViewManageForm( request );
        }

        if ( nIdField != -1 )
        {
            FieldHome.remove( nIdField );

            return redirectToViewModifyQuestion( request, _step.getId( ), _question.getId( ) );
        }

        return getJspManageQuestions( request, _step.getId( ) );
    }

    /**
     * Move a field up
     * 
     * @param request
     *            The request
     * @return The next URL to redirect to
     */
    @Action( ACTION_MOVE_FIELD_UP )
    public String doMoveFieldUp( HttpServletRequest request )
    {
        return doMoveField( request, true );
    }

    /**
     * Move a field down
     * 
     * @param request
     *            The request
     * @return The next URL to redirect to
     */
    @Action( ACTION_MOVE_FIELD_DOWN )
    public String doMoveFieldDown( HttpServletRequest request )
    {
        return doMoveField( request, false );
    }

    /**
     * Move a field up or down
     * 
     * @param request
     *            The request
     * @param bMoveUp
     *            True to move the field up, false to move it down
     * @return The next URL to redirect to
     */
    private String doMoveField( HttpServletRequest request, boolean bMoveUp )
    {
        List<Field> listField;
        Field field;
        String strIdField = request.getParameter( FormsConstants.PARAMETER_ID_FIELD );
        int nIdField = -1;

        if ( ( request.getParameter( FormsConstants.PARAMETER_ID_FIELD ) == null ) )
        {
            return redirectToViewManageForm( request );
        }

        if ( !updateStepAndQuestion( request ) )
        {
            return redirectToViewManageForm( request );
        }

        try
        {
            nIdField = Integer.parseInt( strIdField );
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( ne );

            return redirectToViewManageForm( request );
        }

        field = FieldHome.findByPrimaryKey( nIdField );

        listField = FieldHome.getFieldListByIdEntry( field.getParentEntry( ).getIdEntry( ) );

        int nIndexField = FormsEntryUtils.getIndexFieldInTheFieldList( nIdField, listField );

        if ( nIndexField != -1 && ( ( bMoveUp && nIndexField > 0 ) || ( !bMoveUp && nIndexField < ( listField.size( ) - 1 ) ) ) )
        {
            int nNewPosition;
            Field fieldToInversePosition;
            fieldToInversePosition = listField.get( bMoveUp ? ( nIndexField - 1 ) : ( nIndexField + 1 ) );
            nNewPosition = fieldToInversePosition.getPosition( );
            fieldToInversePosition.setPosition( field.getPosition( ) );
            field.setPosition( nNewPosition );
            FieldHome.update( field );
            FieldHome.update( fieldToInversePosition );
        }

        return redirectToViewModifyQuestion( request, _step.getId( ), _question.getId( ) );
    }

    /**
     * Get the request data and if there is no error insert the data in the field specified in parameter. return null if there is no error or else return the
     * error page URL
     * 
     * @param request
     *            the request
     * @param field
     *            field
     * @return null if there is no error or else return the error page URL
     */
    private String getFieldData( HttpServletRequest request, Field field )
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strValue = request.getParameter( PARAMETER_VALUE );
        String strDefaultValue = request.getParameter( PARAMETER_DEFAULT_VALUE );
        String strNoDisplayTitle = request.getParameter( PARAMETER_NO_DISPLAY_TITLE );
        String strComment = request.getParameter( PARAMETER_COMMENT );
        String strRoleKey = request.getParameter( PARAMETER_ROLE_KEY );

        String strFieldError = EMPTY_STRING;

        if ( ( strTitle == null ) || strTitle.trim( ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_TITLE_FIELD;
        }
        else
            if ( StringUtils.isBlank( strValue ) )
            {
                strFieldError = FIELD_VALUE_FIELD;
            }
            else
                if ( !StringUtil.checkCodeKey( strValue ) )
                {
                    return AdminMessageService.getMessageUrl( request, MESSAGE_FIELD_VALUE_FIELD, AdminMessage.TYPE_STOP );
                }

        if ( !strFieldError.equals( EMPTY_STRING ) )
        {
            Object [ ] tabRequiredFields = {
                I18nService.getLocalizedString( strFieldError, getLocale( ) ),
            };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        field.setCode( IEntryTypeService.FIELD_ANSWER_CHOICE );
        field.setTitle( strTitle );
        field.setValue( strValue );
        field.setComment( strComment );
        field.setDefaultValue( strDefaultValue != null );
        field.setNoDisplayTitle( strNoDisplayTitle != null );
        field.setRoleKey( strRoleKey );

        return null; // No error
    }

    /**
     * Return the URL of the JSP manage question
     * 
     * @param request
     *            The HTTP request
     * @param nIdStep
     *            the parent step identifier
     * @param nIdQuestion
     *            the parent question identifier
     * @return return URL of the JSP modify entry
     */
    private String redirectToViewModifyQuestion( HttpServletRequest request, int nIdStep, int nIdQuestion )
    {
        UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MODIFY_QUESTION );
        url.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_MODIFY_QUESTION );

        url.addParameter( FormsConstants.PARAMETER_ID_STEP, nIdStep );
        url.addParameter( FormsConstants.PARAMETER_ID_QUESTION, nIdQuestion );
        url.setAnchor( ANCHOR_LIST );

        return redirect( request, url.getUrl( ) );

    }

    /**
     * Return the URL of the JSP manage questions
     * 
     * @param request
     *            The HTTP request
     * @param nIdStep
     *            The step identifier
     * @return The URL of the JSP manage questions
     */
    private String getJspManageQuestions( HttpServletRequest request, int nIdStep )
    {
        UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MODIFY_QUESTION );
        url.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_MANAGE_QUESTION );

        url.addParameter( FormsConstants.PARAMETER_ID_STEP, nIdStep );
        return redirect( request, url.getUrl( ) );
    }

    /**
     * Update Step and Question session variables using request parameter question id
     * 
     * @param request
     *            the request
     * 
     * @return false if any error occured
     */
    private boolean updateStepAndQuestion( HttpServletRequest request )
    {
        boolean bSuccess = true;
        int nIdStep = -1;
        int nIdQuestion = -1;

        String strIdQuestion = request.getParameter( FormsConstants.PARAMETER_ID_QUESTION );
        nIdQuestion = Integer.parseInt( strIdQuestion );

        if ( StringUtils.isBlank( strIdQuestion ) || ( nIdQuestion == -1 ) )
        {
            bSuccess = false;
        }

        if ( ( _question == null ) || ( _question.getId( ) != nIdQuestion ) )
        {
            _question = QuestionHome.findByPrimaryKey( nIdQuestion );
        }

        nIdStep = _question.getIdStep( );
        if ( nIdStep == -1 )
        {
            bSuccess = false;
        }

        if ( ( _step == null ) || ( _step.getId( ) != nIdStep ) )
        {
            _step = StepHome.findByPrimaryKey( nIdStep );
        }

        return bSuccess;
    }
}
