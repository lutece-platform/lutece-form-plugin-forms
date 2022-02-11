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

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.math.NumberUtils;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.asynchronousupload.service.AsynchronousUploadHandler;
import fr.paris.lutece.plugins.asynchronousupload.service.IAsyncUploadHandler;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormAction;
import fr.paris.lutece.plugins.forms.business.FormActionHome;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormMessage;
import fr.paris.lutece.plugins.forms.business.FormMessageHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.export.FormExportConfig;
import fr.paris.lutece.plugins.forms.business.export.FormExportConfigHome;
import fr.paris.lutece.plugins.forms.export.ExportServiceManager;
import fr.paris.lutece.plugins.forms.service.FormService;
import fr.paris.lutece.plugins.forms.service.FormsResourceIdService;
import fr.paris.lutece.plugins.forms.service.json.FormJsonService;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.util.FormsUtils;
import fr.paris.lutece.plugins.forms.web.breadcrumb.BreadcrumbManager;
import fr.paris.lutece.plugins.genericattributes.business.GenAttFileItem;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFileHome;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.accesscontrol.AccessControlService;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.captcha.CaptchaSecurityService;
import fr.paris.lutece.portal.service.captcha.ICaptchaSecurityService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.resource.ExtendableResourcePluginActionManager;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.file.FileUtil;
import fr.paris.lutece.util.filesystem.FileSystemUtil;
import fr.paris.lutece.util.html.AbstractPaginator;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class provides the user interface to manage Form features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageForms.jsp", controllerPath = "jsp/admin/plugins/forms/", right = "FORMS_MANAGEMENT" )
public class FormJspBean extends AbstractJspBean
{
    // Rights
    public static final String RIGHT_FORMS_MANAGEMENT = "FORMS_MANAGEMENT";

    private static final long serialVersionUID = 7515975782241863390L;

    private static final String EMPTY_STRING = "";

    // Templates
    private static final String TEMPLATE_MANAGE_FORMS = "/admin/plugins/forms/manage_forms.html";
    private static final String TEMPLATE_CREATE_FORM = "/admin/plugins/forms/create_form.html";
    private static final String TEMPLATE_MODIFY_FORM = "/admin/plugins/forms/modify_form.html";
    private static final String TEMPLATE_MANAGE_EXPORT = "/admin/plugins/forms/manage_export.html";
    private static final String TEMPLATE_MODIFY_FORM_PUBLICATION = "/admin/plugins/forms/modify_publication.html";

    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_ID_CONFIG = "id_config";
    private static final String PARAMETER_EXPORT_CONFIG = "export_config";
    private static final String PARAMETER_JSON_FILE = "json_file";
    private static final String PARAMETER_LOGO = "upload_logo";
    private static final String PARAMETER_ID_ACCESS_CONTROL = "id_accesscontrol";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MODIFY_FORM = "forms.modify_form.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_FORM = "forms.create_form.pageTitle";

    // Markers
    private static final String MARK_FORM_LIST = "form_list";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_FORM = "form";
    private static final String MARK_FORM_MESSAGE = "formMessage";
    private static final String MARK_BREADCRUMB_TYPE = "breadcrumbTypes";
    private static final String MARK_PERMISSION_CREATE_FORMS = "permission_create_forms";
    private static final String MARK_WORKFLOW_REF_LIST = "workflow_list";
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_IS_ACTIVE_KIBANA_FORMS_PLUGIN = "is_active_kibana_forms_plugin";
    private static final String MARK_IS_ACTIVE_CAPTCHA = "is_active_captcha";
    private static final String MARK_EXPORT_LIST = "export_list";
    private static final String MARK_EXPORT_CONFIG_LIST = "export_config_list";
    private static final String MARK_UPLOAD_HANDLER = "uploadHandler";
    private static final String MARK_ACCESSCONTROL_REF_LIST = "accesscontrol_list";
    private static final String MARK_ACCESSCONTROL_ID = "accesscontrol_id";

    // Properties
    private static final String PROPERTY_ITEM_PER_PAGE = "forms.itemsPerPage";

    // Messages
    private static final String MESSAGE_CONFIRM_REMOVE_FORM = "forms.message.confirmRemoveForm";
    private static final String MESSAGE_CONFIRM_REMOVE_ACTIVE_FORM = "forms.message.confirmRemoveActiveForm";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "forms.model.entity.form.attribute.";

    // Views
    private static final String VIEW_MANAGE_FORMS = "manageForms";
    private static final String VIEW_CREATE_FORM = "createForm";
    private static final String VIEW_MODIFY_FORM = "modifyForm";
    private static final String VIEW_MODIFY_PUBLICATION = "modifyPublication";
    private static final String VIEW_CONFIRM_REMOVE_FORM = "confirmRemoveForm";
    private static final String VIEW_MANAGE_EXPORT = "manageExport";
    private static final String VIEW_CONFIG_REMOVE_EXPORT_CONFIG = "confirmRemoveExportConfig";

    // Actions
    private static final String ACTION_CREATE_FORM = "createForm";
    private static final String ACTION_MODIFY_FORM = "modifyForm";
    private static final String ACTION_REMOVE_FORM = "removeForm";
    private static final String ACTION_DUPLICATE_FORM = "duplicateForm";
    private static final String ACTION_CREATE_EXPORT_CONFIG = "createExportConfig";
    private static final String ACTION_REMOVE_EXPORT_CONFIG = "removeExportConfig";
    private static final String ACTION_MOVE_UP_EXPORT_CONFIG = "doMoveUpExportConfig";
    private static final String ACTION_MOVE_DOWN_EXPORT_CONFIG = "doMoveDownExportConfig";
    private static final String ACTION_EXPORT_FORM = "doExportJson";
    private static final String ACTION_IMPORT_FORM = "doImportJson";

    // Infos
    private static final String INFO_FORM_CREATED = "forms.info.form.created";
    private static final String INFO_FORM_UPDATED = "forms.info.form.updated";
    private static final String INFO_FORM_REMOVED = "forms.info.form.removed";
    private static final String INFO_FORM_COPIED = "forms.info.form.copied";
    private static final String ERROR_FORM_NOT_COPIED = "forms.error.form.not.copied";
    private static final String ERROR_FORM_NOT_IMPORTED = "forms.error.form.not.imported";
    private static final String MESSAGE_CONFIRM_REMOVE_EXPORT_CONFIG = "forms.modify_form.message.confirmRemoveExportConfig";

    // Errors
    private static final String ERROR_FORM_NOT_UPDATED = "forms.error.form.notUpdated";
    private static final String ERROR_FORM_DATE_START_AFTER_END = "forms.error.form.date.startAfterEnd";

    // Plugin names
    private static final String KIBANA_FORMS_PLUGIN_NAME = "kibana-forms";

    // Session variable to store working values
    private Form _form;
    private FormMessage _formMessage;

    private final int _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_ITEM_PER_PAGE, 50 );
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;

    // Other
    private static FormService _formService = SpringContextService.getBean( FormService.BEAN_NAME );
    private ICaptchaSecurityService _captchaSecurityService = new CaptchaSecurityService( );
    private IAsyncUploadHandler _uploadHandler = AsynchronousUploadHandler.getHandler( );

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_FORMS, defaultView = true )
    public String getManageForms( HttpServletRequest request )
    {
        AdminUser adminUser = getUser( );
        Plugin plugin = getPlugin( );
        Locale locale = getLocale( );
        List<FormAction> listFormActions;
        List<FormAction> listAuthorizedFormActions;
        _form = new Form( );

        _strCurrentPageIndex = AbstractPaginator.getPageIndex( request, AbstractPaginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nItemsPerPage = AbstractPaginator.getItemsPerPage( request, AbstractPaginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage, _nDefaultItemsPerPage );

        List<Form> listForms = FormHome.getFormList( );
        listForms = (List<Form>) AdminWorkgroupService.getAuthorizedCollection( listForms, (User) adminUser );

        Map<String, Object> model = getModel( );
        LocalizedPaginator<Form> paginator = new LocalizedPaginator<>( listForms, _nItemsPerPage, getJspManageForm( request ), PARAMETER_PAGE_INDEX,
                _strCurrentPageIndex, getLocale( ) );

        listFormActions = FormActionHome.selectAllFormActions( plugin, locale );

        for ( Form form : paginator.getPageItems( ) )
        {
            listAuthorizedFormActions = (List<FormAction>) RBACService.getAuthorizedActionsCollection( listFormActions, form, (User) getUser( ) );
            form.setActions( listAuthorizedFormActions );

        }

        String strTimespamp = Long.toString( new Date( ).getTime( ) );
        Map<String, String> formIdToToken = paginator.getPageItems( ).stream( ).filter( f -> !f.isActive( ) )
                .collect( Collectors.toMap( f -> Integer.toString( f.getId( ) ), f -> FormsUtils.getInactiveBypassToken( f, strTimespamp ) ) );

        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, EMPTY_STRING + _nItemsPerPage );

        model.put( MARK_FORM_LIST, paginator.getPageItems( ) );
        model.put( MARK_LOCALE, request.getLocale( ) );
        model.put( MARK_PERMISSION_CREATE_FORMS,
                RBACService.isAuthorized( Form.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, FormsResourceIdService.PERMISSION_CREATE, (User) adminUser ) );
        model.put( MARK_IS_ACTIVE_KIBANA_FORMS_PLUGIN, PluginService.isPluginEnable( KIBANA_FORMS_PLUGIN_NAME ) );
        model.put( FormsConstants.MARK_TIMESTAMP, strTimespamp );
        model.put( FormsConstants.MARK_INACTIVEBYPASSTOKENS, formIdToToken );

        setPageTitleProperty( EMPTY_STRING );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_FORMS, locale, model );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Returns the form to create a form
     *
     * @param request
     *            The Http request
     * @return the html code of the form form
     * @throws fr.paris.lutece.portal.service.admin.AccessDeniedException
     *             the possible AccessDeniedException
     */
    @View( VIEW_CREATE_FORM )
    public String getCreateForm( HttpServletRequest request ) throws AccessDeniedException
    {
        checkUserPermission( Form.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, FormsResourceIdService.PERMISSION_CREATE, request );

        _uploadHandler.removeSessionFiles( request.getSession( ) );
        _form = ( _form != null ) ? _form : new Form( );
        _formMessage = ( _formMessage != null ) ? _formMessage : new FormMessage( );

        _formMessage.setEndMessage( I18nService.getLocalizedString( FormsConstants.FORM_DEFAULT_END_MESSAGE, getLocale( ) ) );

        Map<String, Object> model = getModel( );

        if ( WorkflowService.getInstance( ).isAvailable( ) )
        {
            AdminUser adminUser = getUser( );
            ReferenceList referenceList = WorkflowService.getInstance( ).getWorkflowsEnabled( (User) adminUser, getLocale( ) );
            model.put( MARK_WORKFLOW_REF_LIST, referenceList );
        }
        
        if ( AccessControlService.getInstance( ).isAvailable( ) )
        {
            AdminUser adminUser = getUser( );
            ReferenceList referenceList = AccessControlService.getInstance( ).getAccessControlsEnabled( adminUser, getLocale() );
            model.put( MARK_ACCESSCONTROL_REF_LIST, referenceList );
        }

        model.put( MARK_BREADCRUMB_TYPE, BreadcrumbManager.getRefListBreadcrumb( ) );
        model.put( MARK_FORM, _form );
        model.put( MARK_FORM_MESSAGE, _formMessage );
        model.put( MARK_LOCALE, request.getLocale( ).getLanguage( ) );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_IS_ACTIVE_CAPTCHA, _captchaSecurityService.isAvailable( ) );
        model.put( MARK_UPLOAD_HANDLER, _uploadHandler );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_FORM, TEMPLATE_CREATE_FORM, model );
    }

    /**
     * Process the data capture form of a new form
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_FORM )
    public String doCreateForm( HttpServletRequest request )
    {
        populate( _form, request, request.getLocale( ) );
        populate( _formMessage, request, request.getLocale( ) );

        if ( !validateForm( _form ) || !validateBean( _formMessage, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_FORM );
        }

        _form.setLogo( getLogoFromRequest( request ) );
        if ( _form.getLogo( ) != null )
        {
            FileHome.create( _form.getLogo( ) );
        }
        FormHome.create( _form );

        _formMessage.setIdForm( _form.getId( ) );
        FormMessageHome.create( _formMessage );
        
        if ( AccessControlService.getInstance( ).isAvailable( ) )
        {
            int idAccessControl = NumberUtils.toInt( request.getParameter( PARAMETER_ID_ACCESS_CONTROL ), -1 );
            AccessControlService.getInstance( ).linkResourceToAccessControl( _form.getId( ), Form.RESOURCE_TYPE, idAccessControl );
        }

        addInfo( INFO_FORM_CREATED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_FORMS );
    }

    /**
     * Manages the removal form of a form whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     * @throws fr.paris.lutece.portal.service.admin.AccessDeniedException
     *             Access denied exception if user isn't authorized
     */
    @View( VIEW_CONFIRM_REMOVE_FORM )
    public String getConfirmRemoveForm( HttpServletRequest request ) throws AccessDeniedException
    {
        int nId = -1;
        try
        {
            nId = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ) );
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( ne );

            return redirectView( request, VIEW_MANAGE_FORMS );
        }

        checkUserPermission( Form.RESOURCE_TYPE, String.valueOf( nId ), FormsResourceIdService.PERMISSION_MODIFY, request );

        Form formToBeDeleted = FormHome.findByPrimaryKey( nId );
        String strConfirmRemoveMessage = formToBeDeleted.isActive( ) ? MESSAGE_CONFIRM_REMOVE_ACTIVE_FORM : MESSAGE_CONFIRM_REMOVE_FORM;

        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_FORM ) );
        url.addParameter( FormsConstants.PARAMETER_ID_FORM, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, strConfirmRemoveMessage, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );

    }

    /**
     * Manages the copy of a form whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_DUPLICATE_FORM )
    public String doDuplicateForm( HttpServletRequest request )
    {

        int nId = -1;
        String strIdForm;

        try
        {
            strIdForm = request.getParameter( FormsConstants.PARAMETER_ID_FORM );
            nId = Integer.parseInt( strIdForm );
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( ne );
            return redirectView( request, VIEW_MANAGE_FORMS );
        }

        if ( nId != FormsConstants.DEFAULT_ID_VALUE
                && RBACService.isAuthorized( Form.RESOURCE_TYPE, strIdForm, FormsResourceIdService.PERMISSION_COPY, (User) getUser( ) ) )
        {

            try
            {
                String json = FormJsonService.getInstance( ).jsonExportForm( nId );
                FormJsonService.getInstance( ).jsonImportForm( json, getLocale( ) );
                addInfo( INFO_FORM_COPIED, getLocale( ) );
            }
            catch( JsonProcessingException e )
            {
                AppLogService.debug( e.getMessage( ) );
                addError( ERROR_FORM_NOT_COPIED, getLocale( ) );
            }
        }

        return redirectView( request, VIEW_MANAGE_FORMS );
    }

    /**
     * Handles the removal form of a form
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage forms
     * @throws fr.paris.lutece.portal.service.admin.AccessDeniedException
     *             AccessDeniedException if user isn't authorized to delete the form
     */
    @Action( ACTION_REMOVE_FORM )
    public String doRemoveForm( HttpServletRequest request ) throws AccessDeniedException
    {
        int nId = -1;
        try
        {
            nId = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ) );
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( ne );

            return redirectView( request, VIEW_MANAGE_FORMS );
        }

        checkUserPermission( Form.RESOURCE_TYPE, String.valueOf( nId ), FormsResourceIdService.PERMISSION_MODIFY, request );

        if ( AccessControlService.getInstance( ).isAvailable( ) )
        {
            // This will delete the remove the link between resource & access control.
            AccessControlService.getInstance( ).linkResourceToAccessControl( _form.getId( ), Form.RESOURCE_TYPE, -1 );
        }
        
        FormService formService = SpringContextService.getBean( FormService.BEAN_NAME );
        formService.removeForm( nId, getUser( ) );

        addInfo( INFO_FORM_REMOVED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_FORMS );
    }

    /**
     * Returns the form to update info about a form
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     * @throws fr.paris.lutece.portal.service.admin.AccessDeniedException
     *             Access denied exception if the user isn't authorized to view the form config page
     */
    @View( VIEW_MODIFY_FORM )
    public String getModifyForm( HttpServletRequest request ) throws AccessDeniedException
    {
        int nId = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ), FormsConstants.DEFAULT_ID_VALUE );

        if ( nId == FormsConstants.DEFAULT_ID_VALUE )
        {
            return redirectView( request, VIEW_MANAGE_FORMS );
        }

        checkUserPermission( Form.RESOURCE_TYPE, String.valueOf( nId ), FormsResourceIdService.PERMISSION_MODIFY_PARAMS, request );

        Form formToBeModified = FormHome.findByPrimaryKey( nId );

        if ( formToBeModified != null )
        {
            _uploadHandler.removeSessionFiles( request.getSession( ) );
            setFormResponseMessage( formToBeModified.getId( ) );

            AdminUser adminUser = getUser( );

            Map<String, Object> model = getModel( );
            model.put( MARK_FORM, formToBeModified );
            model.put( MARK_FORM_MESSAGE, _formMessage );
            model.put( MARK_LOCALE, request.getLocale( ).getLanguage( ) );
            model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
            model.put( MARK_UPLOAD_HANDLER, _uploadHandler );

            if ( formToBeModified.getLogo( ) != null )
            {
                File logo = FileHome.findByPrimaryKey( formToBeModified.getLogo( ).getIdFile( ) );
                PhysicalFile physicalFile = PhysicalFileHome.findByPrimaryKey( logo.getPhysicalFile( ).getIdPhysicalFile( ) );
                FileItem fileItem = new GenAttFileItem( physicalFile.getValue( ), logo.getTitle( ) );
                _uploadHandler.addFileItemToUploadedFilesList( fileItem, PARAMETER_LOGO, request );
            }

            if ( WorkflowService.getInstance( ).isAvailable( ) )
            {
                ReferenceList referenceList = WorkflowService.getInstance( ).getWorkflowsEnabled( (User) adminUser, getLocale( ) );
                model.put( MARK_WORKFLOW_REF_LIST, referenceList );
            }
            
            if ( AccessControlService.getInstance( ).isAvailable( ) )
            {
                ReferenceList referenceList = AccessControlService.getInstance( ).getAccessControlsEnabled( adminUser, getLocale() );
                model.put( MARK_ACCESSCONTROL_REF_LIST, referenceList );
                model.put( MARK_ACCESSCONTROL_ID, AccessControlService.getInstance( ).findAccessControlForResource( formToBeModified.getId( ), Form.RESOURCE_TYPE ) );
            }

            model.put( MARK_BREADCRUMB_TYPE, BreadcrumbManager.getRefListBreadcrumb( ) );
            model.put( MARK_IS_ACTIVE_CAPTCHA, _captchaSecurityService.isAvailable( ) );
            
            ExtendableResourcePluginActionManager.fillModel(request, getUser(), model, "*", FormResponse.RESOURCE_TYPE+ "_" + nId);
            
            return getPage( PROPERTY_PAGE_TITLE_MODIFY_FORM, TEMPLATE_MODIFY_FORM, model );
        }

        return redirectView( request, VIEW_MANAGE_FORMS );
    }

    @View( VIEW_MANAGE_EXPORT )
    public String getManageExport( HttpServletRequest request ) throws AccessDeniedException
    {
        int nId = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ), FormsConstants.DEFAULT_ID_VALUE );

        if ( nId == FormsConstants.DEFAULT_ID_VALUE )
        {
            return redirectView( request, VIEW_MANAGE_FORMS );
        }

        checkUserPermission( Form.RESOURCE_TYPE, String.valueOf( nId ), FormsResourceIdService.PERMISSION_MODIFY_PARAMS, request );

        Form formToBeModified = FormHome.findByPrimaryKey( nId );

        if ( formToBeModified == null )
        {
            return redirectView( request, VIEW_MANAGE_FORMS );
        }

        Map<String, Object> model = getModel( );
        model.put( MARK_FORM, formToBeModified );
        model.put( MARK_EXPORT_LIST, ExportServiceManager.getInstance( ).createReferenceListExportConfigOption( formToBeModified, getLocale( ) ) );
        model.put( MARK_EXPORT_CONFIG_LIST, ExportServiceManager.getInstance( ).createReferenceListExportConfig( formToBeModified, getLocale( ) ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_FORM, TEMPLATE_MANAGE_EXPORT, model );
    }

    @Action( ACTION_CREATE_EXPORT_CONFIG )
    public String doCreateExportConfig( HttpServletRequest request )
    {
        int nId = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ), FormsConstants.DEFAULT_ID_VALUE );

        if ( nId == FormsConstants.DEFAULT_ID_VALUE )
        {
            return redirectView( request, VIEW_MANAGE_FORMS );
        }

        String field = request.getParameter( PARAMETER_EXPORT_CONFIG );

        Form formToBeModified = FormHome.findByPrimaryKey( nId );

        if ( formToBeModified == null )
        {
            return redirectView( request, VIEW_MANAGE_FORMS );
        }

        List<FormExportConfig> existingList = ExportServiceManager.getInstance( ).createReferenceListExportConfig( formToBeModified, getLocale( ) );

        FormExportConfig config = new FormExportConfig( );
        config.setIdForm( nId );
        config.setField( field );
        config.setOrder( existingList.size( ) + 1 );

        FormExportConfigHome.create( config );

        Map<String, String> mapParameters = new LinkedHashMap<>( );
        mapParameters.put( FormsConstants.PARAMETER_ID_FORM, String.valueOf( nId ) );

        return redirect( request, VIEW_MANAGE_EXPORT, mapParameters );
    }

    @View( VIEW_CONFIG_REMOVE_EXPORT_CONFIG )
    public String getConfirmRemoveExportConfig( HttpServletRequest request )
    {
        int idConfig = NumberUtils.toInt( request.getParameter( PARAMETER_ID_CONFIG ), FormsConstants.DEFAULT_ID_VALUE );
        int idForm = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ), FormsConstants.DEFAULT_ID_VALUE );

        if ( idForm == FormsConstants.DEFAULT_ID_VALUE )
        {
            return redirectView( request, VIEW_MANAGE_FORMS );
        }

        if ( idConfig == FormsConstants.DEFAULT_ID_VALUE )
        {
            Map<String, String> mapParameters = new LinkedHashMap<>( );
            mapParameters.put( FormsConstants.PARAMETER_ID_FORM, String.valueOf( idForm ) );

            return redirect( request, VIEW_MANAGE_EXPORT, mapParameters );
        }

        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_EXPORT_CONFIG ) );
        url.addParameter( PARAMETER_ID_CONFIG, idConfig );
        url.addParameter( FormsConstants.PARAMETER_ID_FORM, idForm );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_EXPORT_CONFIG, url.getUrl( ),
                AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );

    }

    @Action( ACTION_REMOVE_EXPORT_CONFIG )
    public String doRemoveExportConfig( HttpServletRequest request )
    {
        int idConfig = NumberUtils.toInt( request.getParameter( PARAMETER_ID_CONFIG ), FormsConstants.DEFAULT_ID_VALUE );
        int idForm = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ), FormsConstants.DEFAULT_ID_VALUE );

        if ( idForm == FormsConstants.DEFAULT_ID_VALUE )
        {
            return redirectView( request, VIEW_MANAGE_FORMS );
        }

        if ( idConfig == FormsConstants.DEFAULT_ID_VALUE )
        {
            Map<String, String> mapParameters = new LinkedHashMap<>( );
            mapParameters.put( FormsConstants.PARAMETER_ID_FORM, String.valueOf( idForm ) );

            return redirect( request, VIEW_MANAGE_EXPORT, mapParameters );
        }

        List<FormExportConfig> existingConfigList = FormExportConfigHome.findByForm( idForm );
        int newOrder = 0;
        FormExportConfigHome.removeByForm( idForm );
        for ( FormExportConfig config : existingConfigList )
        {
            if ( config.getId( ) != idConfig )
            {
                config.setOrder( ++newOrder );
                FormExportConfigHome.create( config );
            }
        }

        Map<String, String> mapParameters = new LinkedHashMap<>( );
        mapParameters.put( FormsConstants.PARAMETER_ID_FORM, String.valueOf( idForm ) );

        return redirect( request, VIEW_MANAGE_EXPORT, mapParameters );
    }

    @Action( ACTION_MOVE_UP_EXPORT_CONFIG )
    public String doMoveUpExportConfig( HttpServletRequest request )
    {
        int idConfig = NumberUtils.toInt( request.getParameter( PARAMETER_ID_CONFIG ), FormsConstants.DEFAULT_ID_VALUE );
        int idForm = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ), FormsConstants.DEFAULT_ID_VALUE );

        if ( idConfig == FormsConstants.DEFAULT_ID_VALUE )
        {
            Map<String, String> mapParameters = new LinkedHashMap<>( );
            mapParameters.put( FormsConstants.PARAMETER_ID_FORM, String.valueOf( idForm ) );

            return redirect( request, VIEW_MANAGE_EXPORT, mapParameters );
        }

        List<FormExportConfig> existingConfigList = FormExportConfigHome.findByForm( idForm );

        FormExportConfig configMovedUp = FormExportConfigHome.findByPrimaryKey( idConfig );
        int orderMovedUp = configMovedUp.getOrder( );

        for ( FormExportConfig config : existingConfigList )
        {
            if ( config.getOrder( ) == orderMovedUp - 1 )
            {
                config.setOrder( orderMovedUp );
                FormExportConfigHome.update( config );

                configMovedUp.setOrder( orderMovedUp - 1 );
                FormExportConfigHome.update( configMovedUp );
                break;
            }
        }

        Map<String, String> mapParameters = new LinkedHashMap<>( );
        mapParameters.put( FormsConstants.PARAMETER_ID_FORM, String.valueOf( idForm ) );

        return redirect( request, VIEW_MANAGE_EXPORT, mapParameters );
    }

    @Action( ACTION_MOVE_DOWN_EXPORT_CONFIG )
    public String doMoveDownExportConfig( HttpServletRequest request )
    {
        int idConfig = NumberUtils.toInt( request.getParameter( PARAMETER_ID_CONFIG ), FormsConstants.DEFAULT_ID_VALUE );
        int idForm = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ), FormsConstants.DEFAULT_ID_VALUE );

        if ( idConfig == FormsConstants.DEFAULT_ID_VALUE )
        {
            Map<String, String> mapParameters = new LinkedHashMap<>( );
            mapParameters.put( FormsConstants.PARAMETER_ID_FORM, String.valueOf( idForm ) );

            return redirect( request, VIEW_MANAGE_EXPORT, mapParameters );
        }

        List<FormExportConfig> existingConfigList = FormExportConfigHome.findByForm( idForm );

        FormExportConfig configMovedDown = FormExportConfigHome.findByPrimaryKey( idConfig );
        int orderMovedDown = configMovedDown.getOrder( );

        for ( FormExportConfig config : existingConfigList )
        {
            if ( config.getOrder( ) == orderMovedDown + 1 )
            {
                config.setOrder( orderMovedDown );
                FormExportConfigHome.update( config );

                configMovedDown.setOrder( orderMovedDown + 1 );
                FormExportConfigHome.update( configMovedDown );
                break;
            }
        }

        Map<String, String> mapParameters = new LinkedHashMap<>( );
        mapParameters.put( FormsConstants.PARAMETER_ID_FORM, String.valueOf( idForm ) );

        return redirect( request, VIEW_MANAGE_EXPORT, mapParameters );
    }

    /**
     * Set the _formMessage
     * 
     * @param nIdForm
     *            The FormMessage FormId
     */
    private void setFormResponseMessage( int nIdForm )
    {
        _formMessage = FormMessageHome.findByForm( nIdForm );

        if ( _formMessage == null )
        {
            _formMessage = new FormMessage( );
            _formMessage.setEndMessage( I18nService.getLocalizedString( FormsConstants.FORM_DEFAULT_END_MESSAGE, getLocale( ) ) );
        }
    }

    /**
     * Returns the form to update info about a form
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     * @throws fr.paris.lutece.portal.service.admin.AccessDeniedException
     *             Access denied exception if the user isn't authorized to view the form config page
     */
    @View( VIEW_MODIFY_PUBLICATION )
    public String getModifyFormPublication( HttpServletRequest request ) throws AccessDeniedException
    {
        int nId = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ), FormsConstants.DEFAULT_ID_VALUE );

        if ( nId == FormsConstants.DEFAULT_ID_VALUE )
        {
            return redirectView( request, VIEW_MANAGE_FORMS );
        }
        checkUserPermission( Form.RESOURCE_TYPE, String.valueOf( nId ), FormsResourceIdService.PERMISSION_MODIFY_PARAMS, request );

        Form formToBeModified = FormHome.findByPrimaryKey( nId );

        if ( formToBeModified != null )
        {
            Map<String, Object> model = getModel( );

            setFormResponseMessage( formToBeModified.getId( ) );

            model.put( MARK_FORM_MESSAGE, _formMessage );
            model.put( MARK_FORM, formToBeModified );
            model.put( MARK_LOCALE, request.getLocale( ).getLanguage( ) );

            return getPage( PROPERTY_PAGE_TITLE_MODIFY_FORM, TEMPLATE_MODIFY_FORM_PUBLICATION, model );
        }

        return redirectView( request, VIEW_MANAGE_FORMS );

    }

    /**
     * Process the change form of a form
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     * @throws fr.paris.lutece.portal.service.admin.AccessDeniedException
     *             AccessDeniedException if the user isn'y authorized to process the modification of params of a form
     */
    @Action( ACTION_MODIFY_FORM )
    public String doModifyForm( HttpServletRequest request ) throws AccessDeniedException
    {
        int nId = -1;
        try
        {
            nId = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ) );
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( ne );
            addError( ERROR_FORM_NOT_UPDATED, getLocale( ) );
            return redirectView( request, VIEW_MANAGE_FORMS );
        }

        checkUserPermission( Form.RESOURCE_TYPE, String.valueOf( nId ), FormsResourceIdService.PERMISSION_MODIFY_PARAMS, request );

        _form = FormHome.findByPrimaryKey( nId );

        if ( _form == null )
        {
            addError( ERROR_FORM_NOT_UPDATED, getLocale( ) );
            return redirectView( request, VIEW_MANAGE_FORMS );
        }

        _formMessage = FormMessageHome.findByForm( _form.getId( ) );

        if ( _form.getLogo( ) != null )
        {
            FileHome.remove( _form.getLogo( ).getIdFile( ) );
            _form.setLogo( null );
        }

        populate( _form, request, request.getLocale( ) );
        populate( _formMessage, request, request.getLocale( ) );

        if ( !validateForm( _form ) || !validateBean( _formMessage, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_FORM, FormsConstants.PARAMETER_ID_FORM, _form.getId( ) );
        }

        _form.setLogo( getLogoFromRequest( request ) );
        if ( _form.getLogo( ) != null )
        {
            FileHome.create( _form.getLogo( ) );
        }

        FormHome.update( _form );
        _formService.fireFormResponseEventUpdate( _form );

        if ( _formMessage.getId( ) == 0 )
        {
            FormMessageHome.create( _formMessage );
        }
        else
        {
            FormMessageHome.update( _formMessage );
        }
        
        if ( AccessControlService.getInstance( ).isAvailable( ) )
        {
            int idAccessControl = NumberUtils.toInt( request.getParameter( PARAMETER_ID_ACCESS_CONTROL ), -1 );
            AccessControlService.getInstance( ).linkResourceToAccessControl( _form.getId( ), Form.RESOURCE_TYPE, idAccessControl );
        }

        addInfo( INFO_FORM_UPDATED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_FORMS );
    }

    private File getLogoFromRequest( HttpServletRequest request )
    {
        _uploadHandler.addFilesUploadedSynchronously( request, PARAMETER_LOGO );
        List<FileItem> listUploadedFileItems = _uploadHandler.getListUploadedFiles( PARAMETER_LOGO, request.getSession( ) );
        for ( FileItem fileItem : listUploadedFileItems )
        {
            File file = new File( );
            file.setTitle( fileItem.getName( ) );
            file.setSize( ( fileItem.getSize( ) < Integer.MAX_VALUE ) ? (int) fileItem.getSize( ) : Integer.MAX_VALUE );
            file.setMimeType( FileSystemUtil.getMIMEType( file.getTitle( ) ) );

            PhysicalFile physicalFile = new PhysicalFile( );
            physicalFile.setValue( fileItem.get( ) );
            file.setPhysicalFile( physicalFile );
            return file;
        }
        return null;
    }

    /**
     * Validate the form field values
     * 
     * @param form
     *            the Form to validate
     * 
     * @return True if the form is valid
     */
    private boolean validateForm( Form form )
    {

        boolean bIsFormValid = validateBean( form, VALIDATION_ATTRIBUTES_PREFIX );

        if ( form.getAvailabilityStartDate( ) != null && form.getAvailabilityEndDate( ) != null
                && form.getAvailabilityStartDate( ).after( form.getAvailabilityEndDate( ) ) )
        {
            addError( ERROR_FORM_DATE_START_AFTER_END, getLocale( ) );
            bIsFormValid = false;

        }
        return bIsFormValid;

    }

    @Action( ACTION_EXPORT_FORM )
    public void doExportJson( HttpServletRequest request )
    {
        int nId = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ), FormsConstants.DEFAULT_ID_VALUE );

        if ( nId == FormsConstants.DEFAULT_ID_VALUE )
        {
            return;
        }

        String content;
        try
        {
            content = FormJsonService.getInstance( ).jsonExportForm( nId );
            Form form = FormHome.findByPrimaryKey( nId );
            download( content.getBytes( StandardCharsets.UTF_8 ), FileUtil.normalizeFileName( form.getTitle( ) ) + ".json", "application/json" );
        }
        catch( JsonProcessingException e )
        {
            AppLogService.debug( e.getMessage( ) );
            addError( ERROR_FORM_NOT_COPIED, getLocale( ) );
        }
    }

    @Action( ACTION_IMPORT_FORM )
    public String doImportJson( HttpServletRequest request )
    {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        FileItem fileItem = multipartRequest.getFile( PARAMETER_JSON_FILE );
        try
        {
            FormJsonService.getInstance( ).jsonImportForm( new String( fileItem.get( ), StandardCharsets.UTF_8 ), getLocale( ) );
            addInfo( INFO_FORM_CREATED, getLocale( ) );
        }
        catch( JsonProcessingException e )
        {
            AppLogService.error( e.getMessage( ) );
            addError( ERROR_FORM_NOT_IMPORTED, getLocale( ) );
        }
        return redirectView( request, VIEW_MANAGE_FORMS );
    }
}
