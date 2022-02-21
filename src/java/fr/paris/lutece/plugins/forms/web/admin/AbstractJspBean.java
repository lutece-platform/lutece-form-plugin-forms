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
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.web.util.LocalizedDelegatePaginator;
import fr.paris.lutece.util.html.AbstractPaginator;
import fr.paris.lutece.util.url.UrlItem;

/**
 * Abstract Jsp Bean for plugin-forms
 */
public abstract class AbstractJspBean extends MVCAdminJspBean
{
    /**
     * Generated serial UID
     */
    private static final long serialVersionUID = 3421909824044642013L;

    protected static final String UNAUTHORIZED = "Unauthorized";
    protected static final String MESSAGE_ERROR_TOKEN = "Invalid security token";

    // Properties
    protected static final String PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE = "forms.itemsPerPage";

    // Parameters
    private static final String PARAMETER_PAGE_INDEX = "page_index";

    // Markers
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";

    // Variables
    protected String _strCurrentPageIndex;
    protected int _nItemsPerPage;
    protected LocalizedDelegatePaginator<Integer> _paginator;

    protected void initiatePaginatorProperties( HttpServletRequest request )
    {
        _strCurrentPageIndex = AbstractPaginator.getPageIndex( request, AbstractPaginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        int nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_DEFAULT_LIST_ITEM_PER_PAGE, 50 );
        _nItemsPerPage = AbstractPaginator.getItemsPerPage( request, AbstractPaginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage, nDefaultItemsPerPage );

    }

    /**
     * Return a model that contains the list and paginator information
     * 
     * @param request
     *            The HTTP request
     * @param strBookmark
     *            The bookmark with the items of the current pagination
     * @param list
     *            The list of item to paginate
     * @param strUrl
     *            The url to used for the pagination
     * @return the model populated with informations of the paginator
     */
    protected Map<String, Object> getPaginatedListModel( HttpServletRequest request, String strBookmark, List<Integer> list, String strUrl, int nbTotalItems )
    {
        _strCurrentPageIndex = AbstractPaginator.getPageIndex( request, AbstractPaginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );

        // Paginator
        _paginator = new LocalizedDelegatePaginator<>( list, _nItemsPerPage, strUrl, PARAMETER_PAGE_INDEX, _strCurrentPageIndex, nbTotalItems, getLocale( ) );

        Map<String, Object> model = getModel( );
        model.put( MARK_NB_ITEMS_PER_PAGE, String.valueOf( _nItemsPerPage ) );
        model.put( MARK_PAGINATOR, _paginator );
        model.put( strBookmark, _paginator.getPageItems( ) );

        return model;
    }

    /**
     * Reset current paginator page index to 1
     */
    protected void resetCurrentPaginatorPageIndex( )
    {
        _strCurrentPageIndex = "1";
    }

    /**
     * Return the paginator
     * 
     * @return the paginator
     */
    protected LocalizedDelegatePaginator<Integer> getPaginator( )
    {
        return _paginator;
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
     * Redirect to the URL of View manage step for the given
     * 
     * @param request
     *            The HTTP request
     * @param nIdForm
     *            The Form identifier
     * @return The URL of the JSP manage step
     */
    protected String redirectToViewManageSteps( HttpServletRequest request, int nIdForm )
    {
        UrlItem url = new UrlItem( getJspManageSteps( request ) );

        url.addParameter( FormsConstants.PARAMETER_ID_FORM, nIdForm );
        return redirect( request, url.getUrl( ) );
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
     * Redirect to the URL of view manage form
     * 
     * @param request
     *            The HTTP request
     * @return The URL of the JSP manage form
     */
    protected String redirectToViewManageForm( HttpServletRequest request )
    {
        return redirect( request, getJspManageForm( request ) );
    }

    /**
     * Check is user is authorized to access to given permission, on given resource and ressource Type. Throws AccessDeniedException if he isnt
     * 
     * @param strRessourceType
     *            The resource type code
     * @param strResource
     *            The resource
     * @param strPermissionName
     *            The permission name
     * @param request
     *            The HttpServletRequest
     * @throws AccessDeniedException
     *             An access denied exception
     */
    protected void checkUserPermission( String strRessourceType, String strResource, String strPermissionName, HttpServletRequest request, String actionCsrf )
            throws AccessDeniedException
    {
        // CSRF Token control
        if ( actionCsrf != null && !SecurityTokenService.getInstance( ).validate( request, actionCsrf ) )
        {
            throw new AccessDeniedException( MESSAGE_ERROR_TOKEN );
        }
        if ( !RBACService.isAuthorized( strRessourceType, strResource, strPermissionName, (User) AdminUserService.getAdminUser( request ) ) )
        {
            throw new AccessDeniedException( UNAUTHORIZED );
        }
    }

    /**
     * Return the current page index as int
     * 
     * @return the current page index
     */
    private int getCurrentPageIndex( )
    {
        if ( _strCurrentPageIndex != null )
        {
            return Integer.parseInt( _strCurrentPageIndex );
        }
        return 1;
    }

    /**
     * Get the index start
     * 
     * @return the started index
     */
    protected int getIndexStart( )
    {
        return ( getCurrentPageIndex( ) - 1 ) * _nItemsPerPage;
    }
}
