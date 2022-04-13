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

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.asynchronousupload.service.AsynchronousUploadHandler;
import fr.paris.lutece.plugins.asynchronousupload.service.IAsyncUploadHandler;
import fr.paris.lutece.plugins.forms.business.FormCategory;
import fr.paris.lutece.plugins.forms.business.FormCategoryHome;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class provides the user interface to manage FormCategory features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageFormsCategories.jsp", controllerPath = "jsp/admin/plugins/forms/", right = "FORMS_CATEGORIES" )
public class FormCategoriesJspBean extends AbstractJspBean
{
	private static final long serialVersionUID = 1992087822103151373L;
	
	// Templates
    private static final String TEMPLATE_MANAGE_CATEGORIES = "/admin/plugins/forms/manage_categories.html";
    private static final String TEMPLATE_CREATE_CATEGORY = "/admin/plugins/forms/create_category.html";
    private static final String TEMPLATE_MODIFY_CATEGORY = "/admin/plugins/forms/modify_category.html";
    
    // Parameters
    private static final String PARAMETER_ID_CATEGORY = "id_category";
	
	// Views
    private static final String VIEW_MANAGE_CATEGORY = "manageCategories";
    private static final String VIEW_CREATE_CATEGORY = "createCategory";
    private static final String VIEW_MODIFY_CATEGORY = "modifyCategory";
    
    // Actions
    private static final String ACTION_CREATE_CATEGORY = "createCategory";
    private static final String ACTION_MODIFY_CATEGORY = "modifyCategory";
    private static final String ACTION_REMOVE_CATEGORY = "removeCategory";
    private static final String ACTION_CONFIRM_REMOVE_CATEGORY = "confirmRemoveCategory";
    
    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_CREATE_CATEGORY = "forms.create_form_category.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_CATEGORY = "forms.modify_form_category.pageTitle";
    
    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "forms.create_form_category.attribute.";

    // Markers
    private static final String MARK_CATEGORY = "category";
    private static final String MARK_CATEGORY_LIST = "form_category_list";
    private static final String MARK_UPLOAD_HANDLER = "uploadHandler";
    
    // Infos
    private static final String INFO_CATEGORY_CREATED = "forms.info.formCategory.created";
    private static final String INFO_CATEGORY_UPDATED = "forms.info.formCategory.updated";
    private static final String INFO_CATEGORY_REMOVED = "forms.info.formCategory.removed";
    
    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_CATEGORY = "appointment.message.confirmRemoveCategory";
    private static final String MESSAGE_ERROR_REMOVE_CATEGORY = "appointment.message.categoryIsAffected.errorRemoveCategory";
    
    // Session variable to store working values
    private FormCategory _formCategory;
    
    // Other
    private IAsyncUploadHandler _uploadHandler = AsynchronousUploadHandler.getHandler( );
	
	/**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_CATEGORY, defaultView = true )
    public String getManageCategories( HttpServletRequest request )
    {
        Locale locale = getLocale( );
        Map<String, Object> model = getModel( );
        
        List<FormCategory> listFormCategories = FormCategoryHome.getFormCategoryList();
        
        model.put( MARK_CATEGORY_LIST, listFormCategories );
        
        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_CATEGORIES, locale, model );

        return getAdminPage( templateList.getHtml( ) );
    }
    
    /**
     * Returns the form to create a formCategory
     *
     * @param request
     *            The Http request
     * @return the html code of the formCategory form
     * @throws fr.paris.lutece.portal.service.admin.AccessDeniedException
     *             the possible AccessDeniedException
     */
    @View( VIEW_CREATE_CATEGORY )
    public String getCreateForm( HttpServletRequest request ) throws AccessDeniedException
    {
    	Map<String, Object> model = getModel( );
    	
    	_uploadHandler.removeSessionFiles( request.getSession( ) );
    	_formCategory = ( _formCategory == null ) ? new FormCategory( ) : _formCategory;
    	
    	model.put( MARK_UPLOAD_HANDLER, _uploadHandler );
    	model.put( MARK_CATEGORY, _formCategory);
    	
        return getPage( PROPERTY_PAGE_TITLE_CREATE_CATEGORY, TEMPLATE_CREATE_CATEGORY, model );
    }
    
    /**
     * Process the data capture form of a new formCategory
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException 
     */
    @Action( ACTION_CREATE_CATEGORY )
    public String doCreateForm( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _formCategory, request, request.getLocale( ) );
        
        if ( !validateBean( _formCategory, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_CATEGORY );
        }

        FormCategoryHome.create(_formCategory);
        addInfo( INFO_CATEGORY_CREATED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_CATEGORY );
    }
    
    /**
     * Remove the category selected
     * 
     * @param request
     *            the request
     * @return The JSP URL of the process result
     * @throws AccessDeniedException
     *             If the user is not authorized
     */
    @Action( ACTION_REMOVE_CATEGORY )
    public String doRemoveCategory( HttpServletRequest request )
    {
        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY );
        FormCategoryHome.remove( Integer.parseInt( strIdCategory ) );
        addInfo( INFO_CATEGORY_REMOVED, getLocale( ) );
        return redirectView( request, VIEW_MANAGE_CATEGORY );
    }
    
    /**
     * Display a popup to ask the user if he really wants to delete the category he selected
     * 
     * @param request
     *            the request
     * @return the HTML code to confirm
     * @throws AccessDeniedException
     *             If the user is not authorized
     */
    @Action( ACTION_CONFIRM_REMOVE_CATEGORY )
    public String getConfirmRemoveCategory( HttpServletRequest request )
    {
        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY );
        if ( StringUtils.isEmpty( strIdCategory ) )
        {
            return redirectView( request, VIEW_MANAGE_CATEGORY );
        }
        int nIdCategory = Integer.parseInt( strIdCategory );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_CATEGORY ) );
        url.addParameter( PARAMETER_ID_CATEGORY, nIdCategory );
        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_CATEGORY, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );
        return redirect( request, strMessageUrl );
    }
    
    /**
     * Get the view to modify an existed category
     * 
     * @param request
     *            the request
     * @return The HTML content to display
     * @throws AccessDeniedException
     *             If the user is not authorized
     */
    @View( VIEW_MODIFY_CATEGORY )
    public String getModifyCategory( HttpServletRequest request )
    {
        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY );
        int nIdCategory = Integer.parseInt( strIdCategory );
        _formCategory = FormCategoryHome.findByPrimaryKey( nIdCategory );
        Map<String, Object> model = getModel( );
        model.put( MARK_CATEGORY, _formCategory );
        return getPage( PROPERTY_PAGE_TITLE_MODIFY_CATEGORY, TEMPLATE_MODIFY_CATEGORY, model );
    }

    /**
     * Modify a category
     * 
     * @param request
     *            the request
     * @return The JSP URL of the process result
     * @throws AccessDeniedException
     *             If the user is not authorized
     */
    @Action( ACTION_MODIFY_CATEGORY )
    public String doModifyCategory( HttpServletRequest request )
    {
        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY );
        int nIdCategory = Integer.parseInt( strIdCategory );
        _formCategory = ( _formCategory == null || _formCategory.getId() != nIdCategory ) ? new FormCategory( ) : _formCategory;
        _formCategory.setId( nIdCategory );
        populate( _formCategory, request );
        // Check constraints
        if ( !validateBean( _formCategory, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_MODIFY_CATEGORY );
        }
        FormCategoryHome.update( _formCategory );
        addInfo( INFO_CATEGORY_UPDATED, getLocale( ) );
        return redirectView( request, VIEW_MANAGE_CATEGORY );
    }
}
