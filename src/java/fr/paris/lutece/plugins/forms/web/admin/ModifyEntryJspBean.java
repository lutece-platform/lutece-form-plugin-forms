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
package fr.paris.lutece.plugins.forms.web.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.service.FormsResourceIdService;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.util.FormsEntryUtils;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.service.fileimage.FileImagePublicService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.image.ImageResourceManager;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.upload.MultipartItem;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.file.FileUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.string.StringUtil;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class provides the user interface to manage Form features ( manage, create, modify, remove )
 */
@SessionScoped
@Named
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
    private static final String MESSAGE_ERROR_FILE_IMAGE = "Error importing file.";

    // properties
    private static final String PROPERTY_CREATE_FIELD_TITLE = "forms.createField.title";
    private static final String PROPERTY_MODIFY_FIELD_TITLE = "forms.modifyField.title";

    private static final String MARK_OPTION_NO_DISPLAY_TITLE = "option_no_display_title";

    // Jsp Definition
    private static final String JSP_MODIFY_QUESTION = "jsp/admin/plugins/forms/ManageQuestions.jsp";

    // parameters form
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_VALUE = "value";
    private static final String PARAMETER_DEFAULT_VALUE = "default_value";
    private static final String PARAMETER_NO_DISPLAY_TITLE = "no_display_title";
    private static final String PARAMETER_COMMENT = "comment";
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

    /**
     * Key of the security token of the links that move a field, put in the page of the question by {@link AbstractFormQuestionJspBean}
     */
    public static final String TOKEN_ACTION_MOVE_FIELD = "moveField";

    // other constants
    private static final String EMPTY_STRING = "";
    private static final String ANCHOR_LIST = "list";

    private int _nIdEntry = -1;
    private Step _step;
    private Question _question;

    @Inject
    private SecurityTokenService _securityTokenService;

    /* -------- Fields management ---------- */

    /**
     * {@inheritDoc} The user must be allowed to modify every form referenced by the request, whatever the view or action
     */
    @Override
    public String processController( HttpServletRequest request, HttpServletResponse response ) throws AccessDeniedException
    {
        checkUserPermissionOnReferencedForms( request );

        return super.processController( request, response );
    }

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

        if ( ( ( _nIdEntry == -1 ) || ( entry == null ) ) || !isUserAuthorizedOnForm( request ) )
        {
            return getJspManageQuestions( request, _step.getId( ) );
        }

        Field field = new Field( );
        field.setParentEntry( entry );

        Map<String, Object> model = getModel( );
        Locale locale = getLocale( );
        model.put( MARK_OPTION_NO_DISPLAY_TITLE, request.getParameter( PARAMETER_OPTION_NO_DISPLAY_TITLE ) != null );

        model.put( FormsConstants.MARK_FIELD, field );
        model.put( FormsConstants.MARK_QUESTION, _question );
        model.put( FormsConstants.MARK_ID_ENTRY, _nIdEntry );
        model.put( SecurityTokenService.MARK_TOKEN, _securityTokenService.getToken( request, ACTION_CREATE_FIELD ) );

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

        Field field = findFieldOfQuestion( request );

        if ( ( field == null ) || !isUserAuthorizedOnForm( request ) )
        {
            return getJspManageQuestions( request, _step.getId( ) );
        }

        _nIdEntry = _question.getIdEntry( );

        Entry entry = EntryHome.findByPrimaryKey( _nIdEntry );

        field.setParentEntry( entry );
        
        Map<String, Object> model = getModel( );
        Locale locale = getLocale( );
        model.put( MARK_OPTION_NO_DISPLAY_TITLE, request.getParameter( PARAMETER_OPTION_NO_DISPLAY_TITLE ) != null );

        model.put( FormsConstants.MARK_FIELD, field );
        model.put( FormsConstants.MARK_QUESTION, _question );
        model.put( FormsConstants.MARK_ID_ENTRY, _nIdEntry );
        model.put( SecurityTokenService.MARK_TOKEN, _securityTokenService.getToken( request, ACTION_MODIFY_FIELD ) );

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
     * @throws AccessDeniedException
     *             if the security token is invalid or if the user is not authorized on the form of the question
     */
    @Action( ACTION_CREATE_FIELD )
    public String doCreateField( HttpServletRequest request ) throws AccessDeniedException
    {
        MultipartHttpServletRequest multipartRequest = ( MultipartHttpServletRequest ) request;
        MultipartItem imageFileItem = multipartRequest.getFile( FormsConstants.PARAMETER_ILLUSTRATION_IMAGE );

        if ( !updateStepAndQuestion( request ) )
        {
            return redirectToViewManageForm( request );
        }
        checkTokenAndAuthorization( request, ACTION_CREATE_FIELD );

        // The field is created on the entry of the question, whose form has been checked
        _nIdEntry = _question.getIdEntry( );

        Entry entry = new Entry( );
        entry.setIdEntry( _nIdEntry );

        Field field = new Field( );
        field.setParentEntry( entry );

        String strError = getFieldData( request, field, getJspModifyQuestion( request, _step.getId( ), _question.getId( ) ) );

        if ( strError != null )
        {
            return redirect( request, strError );
        }
        
        if ( imageFileItem != null && imageFileItem.getSize( ) > 0 )
        {
            try
            {
                String strFileStoreKey = ImageResourceManager.addImageResource( FileImagePublicService.IMAGE_RESOURCE_TYPE_ID, imageFileItem );
                File fileImage = new File( );
                fileImage.setFileKey( strFileStoreKey );
                field.setFileImage( fileImage );
            }
            catch ( Exception e ) 
            {
            	AppLogService.error( MESSAGE_ERROR_FILE_IMAGE, e );
                throw new AppException( MESSAGE_ERROR_FILE_IMAGE, e );
            }
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
     * @throws AccessDeniedException
     *             if the security token is invalid or if the user is not authorized on the form of the question
     */
    @Action( ACTION_MODIFY_FIELD )
    public String doModifyField( HttpServletRequest request ) throws AccessDeniedException
    {
        MultipartHttpServletRequest multipartRequest = ( MultipartHttpServletRequest ) request;
        MultipartItem imageFileItem = multipartRequest.getFile( FormsConstants.PARAMETER_ILLUSTRATION_IMAGE );

        if ( !updateStepAndQuestion( request ) )
        {
            return redirectToViewManageForm( request );
        }
        checkTokenAndAuthorization( request, ACTION_MODIFY_FIELD );

        Field field = findFieldOfQuestion( request );

        if ( field != null )
        {
            String strError = getFieldData( request, field, getJspModifyQuestion( request, _step.getId( ), _question.getId( ) ) );

            if ( strError != null )
            {
                return redirect( request, strError );
            }
            
            if ( imageFileItem != null && imageFileItem.getSize( ) > 0 )
            {
                try
                {
                	String strFileStoreKey = ImageResourceManager.addImageResource( FileImagePublicService.IMAGE_RESOURCE_TYPE_ID, imageFileItem );
                    File fileImage = new File( );
                    fileImage.setFileKey( strFileStoreKey );
                    field.setFileImage( fileImage );
                }
                catch ( Exception e ) 
                {
                	AppLogService.error( MESSAGE_ERROR_FILE_IMAGE, e );
                    throw new AppException( MESSAGE_ERROR_FILE_IMAGE, e );
                }
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
    @View( value = VIEW_CONFIRM_REMOVE_FIELD , securityTokenAction = ACTION_REMOVE_FIELD )
    public String getConfirmRemoveField( HttpServletRequest request )
    {
        if ( !updateStepAndQuestion( request ) )
        {
            return redirectToViewManageForm( request );
        }

        Field field = findFieldOfQuestion( request );

        if ( ( field == null ) || !isUserAuthorizedOnForm( request ) )
        {
            return redirectToViewManageForm( request );
        }

        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_FIELD ) );
        url.addParameter( FormsConstants.PARAMETER_ID_FIELD, field.getIdField( ) );
        url.addParameter( FormsConstants.PARAMETER_ID_QUESTION, _question.getId( ) );
        url.addParameter( SecurityTokenService.PARAMETER_TOKEN, _securityTokenService.getToken( request, ACTION_REMOVE_FIELD ) );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_FIELD, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Perform suppression field
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     * @throws AccessDeniedException
     *             if the security token is invalid or if the user is not authorized on the form of the question
     */
    @Action( ACTION_REMOVE_FIELD )
    public String doRemoveField( HttpServletRequest request ) throws AccessDeniedException
    {
        if ( !updateStepAndQuestion( request ) )
        {
            return redirectToViewManageForm( request );
        }
        checkTokenAndAuthorization( request, ACTION_REMOVE_FIELD );

        Field field = findFieldOfQuestion( request );

        if ( field != null )
        {
            FieldHome.remove( field.getIdField( ) );

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
     * @throws AccessDeniedException
     *             if the security token is invalid or if the user is not authorized on the form of the question
     */
    @Action( ACTION_MOVE_FIELD_UP )
    public String doMoveFieldUp( HttpServletRequest request ) throws AccessDeniedException
    {
        return doMoveField( request, true );
    }

    /**
     * Move a field down
     * 
     * @param request
     *            The request
     * @return The next URL to redirect to
     * @throws AccessDeniedException
     *             if the security token is invalid or if the user is not authorized on the form of the question
     */
    @Action( ACTION_MOVE_FIELD_DOWN )
    public String doMoveFieldDown( HttpServletRequest request ) throws AccessDeniedException
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
     * @throws AccessDeniedException
     *             if the security token is invalid or if the user is not authorized on the form of the question
     */
    private String doMoveField( HttpServletRequest request, boolean bMoveUp ) throws AccessDeniedException
    {
        if ( !updateStepAndQuestion( request ) )
        {
            return redirectToViewManageForm( request );
        }
        checkTokenAndAuthorization( request, TOKEN_ACTION_MOVE_FIELD );

        Field field = findFieldOfQuestion( request );

        if ( field == null )
        {
            return redirectToViewModifyQuestion( request, _step.getId( ), _question.getId( ) );
        }

        List<Field> listField = FieldHome.getFieldListByIdEntry( field.getParentEntry( ).getIdEntry( ) );

        int nIndexField = FormsEntryUtils.getIndexFieldInTheFieldList( field.getIdField( ), listField );

        if ( nIndexField != -1 && ( ( bMoveUp && nIndexField > 0 ) || ( !bMoveUp && nIndexField < ( listField.size( ) - 1 ) ) ) )
        {
            Field fieldToInversePosition = listField.get( bMoveUp ? ( nIndexField - 1 ) : ( nIndexField + 1 ) );
            int nNewPosition = fieldToInversePosition.getPosition( );
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
     * @param strReturnUrl
     *            the URL of the screen of the question, opened by the button of an error message when there is no page to go back to
     * @return null if there is no error or else return the error page URL
     */
    private String getFieldData( HttpServletRequest request, Field field, String strReturnUrl )
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strValue = request.getParameter( PARAMETER_VALUE );
        String strDefaultValue = request.getParameter( PARAMETER_DEFAULT_VALUE );
        String strNoDisplayTitle = request.getParameter( PARAMETER_NO_DISPLAY_TITLE );
        String strComment = request.getParameter( PARAMETER_COMMENT );

        String strFieldError = EMPTY_STRING;

        if ( ( strTitle == null ) || strTitle.trim( ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_TITLE_FIELD;
        }
        else
            if ( StringUtils.isBlank( strValue ) )
            {
                strValue = FileUtil.normalizeFileName( strTitle );
            }
        if ( !strFieldError.equals( EMPTY_STRING ) )
        {
            Object [ ] tabRequiredFields = {
                    I18nService.getLocalizedString( strFieldError, getLocale( ) ),
            };

            return AdminMessageService.getMessageUrlReturningTo( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP, strReturnUrl );
        }

        if ( !StringUtil.checkCodeKey( strValue ) )
        {
            return AdminMessageService.getMessageUrlReturningTo( request, MESSAGE_FIELD_VALUE_FIELD, AdminMessage.TYPE_STOP, strReturnUrl );
        }

        field.setCode( IEntryTypeService.FIELD_ANSWER_CHOICE );
        field.setTitle( strTitle );
        field.setValue( strValue );
        field.setComment( strComment );
        field.setDefaultValue( strDefaultValue != null );
        field.setNoDisplayTitle( strNoDisplayTitle != null );

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
        return redirect( request, getJspModifyQuestion( request, nIdStep, nIdQuestion ) );
    }

    /**
     * Return the URL of the JSP modify question, on the list of its fields
     * 
     * @param request
     *            The HTTP request
     * @param nIdStep
     *            the parent step identifier
     * @param nIdQuestion
     *            the question identifier
     * @return the URL of the JSP modify question
     */
    private String getJspModifyQuestion( HttpServletRequest request, int nIdStep, int nIdQuestion )
    {
        UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MODIFY_QUESTION );
        url.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_MODIFY_QUESTION );

        url.addParameter( FormsConstants.PARAMETER_ID_STEP, nIdStep );
        url.addParameter( FormsConstants.PARAMETER_ID_QUESTION, nIdQuestion );
        url.setAnchor( ANCHOR_LIST );

        return url.getUrl( );
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
        int nIdQuestion = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_QUESTION ), NumberUtils.INTEGER_MINUS_ONE );

        if ( nIdQuestion == NumberUtils.INTEGER_MINUS_ONE )
        {
            return false;
        }

        if ( ( _question == null ) || ( _question.getId( ) != nIdQuestion ) )
        {
            _question = QuestionHome.findByPrimaryKey( nIdQuestion );
        }

        if ( _question == null )
        {
            return false;
        }

        int nIdStep = _question.getIdStep( );

        if ( ( _step == null ) || ( _step.getId( ) != nIdStep ) )
        {
            _step = StepHome.findByPrimaryKey( nIdStep );
        }

        return _step != null;
    }

    /**
     * Find the field given by the request parameter, if it belongs to the entry of the current question
     * 
     * @param request
     *            the request
     * @return the field, or null if the parameter is not a field of the entry of the question
     */
    private Field findFieldOfQuestion( HttpServletRequest request )
    {
        int nIdField = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_FIELD ), NumberUtils.INTEGER_MINUS_ONE );
        Field field = ( nIdField != NumberUtils.INTEGER_MINUS_ONE ) ? FieldHome.findByPrimaryKey( nIdField ) : null;

        if ( field == null || field.getParentEntry( ) == null || field.getParentEntry( ).getIdEntry( ) != _question.getIdEntry( ) )
        {
            return null;
        }

        return field;
    }

    /**
     * Check if the user can modify the form of the current step: the permission on the form and its workgroup
     * 
     * @param request
     *            the request
     * @return true if the user is authorized, false otherwise
     */
    private boolean isUserAuthorizedOnForm( HttpServletRequest request )
    {
        Form form = FormHome.findByPrimaryKey( _step.getIdForm( ) );

        return ( form != null )
                && RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + form.getId( ), FormsResourceIdService.PERMISSION_MODIFY, (User) getUser( ) )
                && AdminWorkgroupService.isAuthorized( form, (User) getUser( ) );
    }

    /**
     * Check the security token of the action and the authorization of the user on the form of the current step
     * 
     * @param request
     *            the request
     * @param strTokenAction
     *            the action of the security token
     * @throws AccessDeniedException
     *             if the token is invalid or if the user is not authorized on the form
     */
    private void checkTokenAndAuthorization( HttpServletRequest request, String strTokenAction ) throws AccessDeniedException
    {
        if ( !_securityTokenService.validate( request, strTokenAction ) )
        {
            throw new AccessDeniedException( MESSAGE_ERROR_TOKEN );
        }
        if ( !isUserAuthorizedOnForm( request ) )
        {
            throw new AccessDeniedException( UNAUTHORIZED );
        }
    }
}
