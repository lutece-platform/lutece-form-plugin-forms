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
package fr.paris.lutece.plugins.forms.web.portlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.portlet.FormsListPortlet;
import fr.paris.lutece.plugins.forms.business.portlet.FormsListPortletHome;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.web.portlet.PortletJspBean;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.json.ErrorJsonResponse;
import fr.paris.lutece.util.json.JsonResponse;
import fr.paris.lutece.util.json.JsonUtil;

/**
 * This class provides the user interface to manage FormsList Portlet
 */
@SessionScoped
@Named
public class FormsListPortletJspBean extends PortletJspBean
{
    private static final long serialVersionUID = 4077345110036566513L;

    // Parameters
    private static final String PARAMETER_ACTION_PORTLET = "action";
    private static final String PARAMETER_ACTION_PORTLET_ADD = "add";
    private static final String PARAMETER_ACTION_PORTLET_REMOVE = "remove";
    private static final String PARAMETER_FROM_ID = "form_id";

    // Marks
    private static final String MARK_AVAILABLE_FORMS_LIST = "available_forms_list";
    private static final String MARK_PUBLISHED_FORMS_LIST = "published_forms_list";

    // Constants
    private static final String RESPONSE_SUCCESS = "SUCCESS";
    private static final String RESPONSE_ERROR = "ERROR";
    private static final String MESSAGE_PORTLET_NOT_FOUND = "forms.message.error.portlet.notFound";
    
    // Variables
    private FormsListPortlet _portlet;

    @Inject
    private FormsListPortletHome _formsListPortletHome;

    /**
     * Returns the FormsListPortlet form of creation
     * 
     * @param request
     *            The Http rquest
     * @return the html code of the FormsListPortlet portlet form
     */
    @Override
    public String getCreate( HttpServletRequest request ) 
    {
        HashMap<String, Object> model = new HashMap<>( );
        String strPageId = request.getParameter( PARAMETER_PAGE_ID );
        String strPortletTypeId = request.getParameter( PARAMETER_PORTLET_TYPE_ID );
        // Always a new portlet: the one kept in session may be a portlet opened for modification, whose identifier and forms the
        // creation would take over while this screen shows an empty list of published forms
        _portlet = new FormsListPortlet( );

        model.put( MARK_AVAILABLE_FORMS_LIST, getActiveForms( ) );
        model.put( MARK_PUBLISHED_FORMS_LIST, new ArrayList<>( ) );
        HtmlTemplate template = getCreateTemplate( strPageId, strPortletTypeId, model );

        return template.getHtml(  );
    }
    
    /**
     * Treats the creation form of a new FormsListPortlet portlet
     * 
     * @param request
     *            The Http request
     * @return The jsp URL which displays the view of the created FormsListPortlet portlet
     */
    @Override
    public String doCreate( HttpServletRequest request ) 
    {
        String strPageId = request.getParameter( PARAMETER_PAGE_ID );
        int nPageId = NumberUtils.toInt( strPageId, NumberUtils.INTEGER_MINUS_ONE );

        if ( _portlet == null )
        {
            // Session expired since the creation screen: the portlet is created without form
            _portlet = new FormsListPortlet( );
        }

        String strErrorUrl = setPortletCommonData( request, _portlet );

        if ( strErrorUrl != null )
        {
            return strErrorUrl;
        }

        _portlet.setPageId( nPageId );

        _formsListPortletHome.create( _portlet );

        return getPageUrl( nPageId );    
    }

    /**
     * Returns the FormsListPortlet form for update
     * 
     * @param request
     *            The Http request
     * @return the html code of the FormsListPortlet form
     */
    @Override
    public String getModify( HttpServletRequest request ) 
    {
        HashMap<String, Object> model = new HashMap<>( );
        List<Form> availableFormsList = new ArrayList<>( );
        List<Form> publishedFormsList = new ArrayList<>( );
        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );
        int nPortletId = NumberUtils.toInt( strPortletId, NumberUtils.INTEGER_MINUS_ONE );
        Portlet portlet = PortletHome.findByPrimaryKey( nPortletId );
        if ( !( portlet instanceof FormsListPortlet ) )
        {
            // Unknown portlet, or a portlet of another type: a message rather than an internal error
            return "<div class=\"alert alert-danger\" role=\"alert\">" + I18nService.getLocalizedString( MESSAGE_PORTLET_NOT_FOUND, getLocale( ) )
                    + "</div>";
        }
        _portlet = (FormsListPortlet) portlet;

        availableFormsList = getActiveForms( );
        if ( CollectionUtils.isNotEmpty( _portlet.getFormsIdList( ) ) )
        {
            publishedFormsList = FormHome.getFormByPrimaryKeyList( _portlet.getFormsIdList( ) );
            List<Integer> publishedFormIds = publishedFormsList.stream( ).map( Form::getId ).collect( Collectors.toList( ) );
            availableFormsList.removeIf( f -> publishedFormIds.contains( f.getId( ) ) );
        }
        model.put( MARK_AVAILABLE_FORMS_LIST, availableFormsList );
        model.put( MARK_PUBLISHED_FORMS_LIST, publishedFormsList );
        
        HtmlTemplate template = getModifyTemplate( _portlet, model );

        return template.getHtml(  );
    }
    
    /**
     * Treats the update form of the FormsListPortlet portlet whose identifier is in the http request
     * 
     * @param request
     *            The Http request
     * @return The jsp URL which displays the view of the updated portlet
     */
    @Override
    public String doModify( HttpServletRequest request ) 
    {
        int nPortletId = NumberUtils.toInt( request.getParameter( PARAMETER_PORTLET_ID ), NumberUtils.INTEGER_MINUS_ONE );
        if ( _portlet == null || _portlet.getId( ) != nPortletId )
        {
            // Session expired, or another portlet opened since: the portlet of the request, with its stored forms
            Portlet portlet = PortletHome.findByPrimaryKey( nPortletId );
            if ( !( portlet instanceof FormsListPortlet ) )
            {
                return getPageUrl( NumberUtils.toInt( request.getParameter( PARAMETER_PAGE_ID ), NumberUtils.INTEGER_MINUS_ONE ) );
            }
            _portlet = (FormsListPortlet) portlet;
        }
        String strErrorUrl = setPortletCommonData( request, _portlet );

        if ( strErrorUrl != null )
        {
            return strErrorUrl;
        }
        
        _portlet.update( );

        return getPageUrl( _portlet.getPageId( ) );
    }

    /**
     * Update form portlet
     *
     * @param request
     *           The Http request
     * @return Json 
     *           The Json succes or echec
     */
    public String updatePortletForm( HttpServletRequest request ) 
    {
        if ( _portlet == null )
        {
            return JsonUtil.buildJsonResponse( new ErrorJsonResponse( RESPONSE_ERROR ) );
        }
        String strAction = request.getParameter( PARAMETER_ACTION_PORTLET );
        String strFormId = request.getParameter( PARAMETER_FROM_ID );

        if ( StringUtils.isNoneBlank( strAction ) && strAction.equals( PARAMETER_ACTION_PORTLET_ADD ) )
        {
            _portlet.addFormId( NumberUtils.toInt( strFormId ) );

        }
        else if ( StringUtils.isNoneBlank( strAction ) && strAction.equals( PARAMETER_ACTION_PORTLET_REMOVE ) )
        {
            _portlet.removeFormId( NumberUtils.toInt( strFormId ) );
        }

        return JsonUtil.buildJsonResponse( new JsonResponse( RESPONSE_SUCCESS ) );
    }
    
    /**
     * get the active forms
     * 
     * @return active forms
     */
    private List<Form> getActiveForms( )
    {
        List<Form> formsList = FormHome.getFormList( );
        formsList = (List<Form>) AdminWorkgroupService.getAuthorizedCollection( formsList, (User) getUser( ) );
        
        if ( CollectionUtils.isNotEmpty(formsList) )
        {
            return formsList.stream( ).filter( Form::isActive ).collect( Collectors.toList( ) );
        }
        else
        {
            return new ArrayList<>( );
        }
    }
}
