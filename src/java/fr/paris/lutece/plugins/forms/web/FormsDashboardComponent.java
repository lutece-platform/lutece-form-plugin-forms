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
package fr.paris.lutece.plugins.forms.web;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.portal.service.util.AppPathService;
import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormAction;
import fr.paris.lutece.plugins.forms.business.FormActionHome;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.util.FormsUtils;
import fr.paris.lutece.portal.business.right.Right;
import fr.paris.lutece.portal.business.right.RightHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.dashboard.DashboardComponent;
import fr.paris.lutece.portal.service.database.AppConnectionService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

public class FormsDashboardComponent extends DashboardComponent
{

    // MARKS
    private static final String MARK_URL = "url";
    private static final String MARK_FORM_LIST = "form_list";

    // PARAMETERS
    private static final String PARAMETER_PLUGIN_NAME = "plugin_name";

    // TEMPLATES
    private static final String TEMPLATE_DASHBOARD = "/admin/plugins/forms/dashboard/form_dashboard.html";

    @Override
    public String getDashboardData( AdminUser user, HttpServletRequest request )
    {
        Right right = RightHome.findByPrimaryKey( getRight( ) );
        Plugin plugin = PluginService.getPlugin( right.getPluginName( ) );

        if ( !( ( plugin.getDbPoolName( ) != null ) && !AppConnectionService.NO_POOL_DEFINED.equals( plugin.getDbPoolName( ) ) ) )
        {
            return StringUtils.EMPTY;
        }

        UrlItem url = new UrlItem( right.getUrl( ) );
        url.addParameter( PARAMETER_PLUGIN_NAME, right.getPluginName( ) );

        List<Form> listForms = FormHome.getFormList( );
        listForms = (List<Form>) AdminWorkgroupService.getAuthorizedCollection( listForms, (User) user );
        listForms.sort( Comparator.comparing( Form::getCreationDate ).reversed( ) );

        List<Form> displayList = listForms;
        if ( listForms.size( ) > 10 )
        {
            displayList = listForms.subList( 0, 9 );
        }

        List<FormAction> listFormActions = FormActionHome.selectAllFormActions( plugin, user.getLocale( ) );

        for ( Form form : displayList )
        {
            form.setCurrentNumberResponse( FormHome.getNumberOfResponseForms( form.getId( ) ) );
            List<FormAction> listAuthorisedActions = (List<FormAction>) RBACService.getAuthorizedActionsCollection( listFormActions, form, (User) user );
            form.setActions( listAuthorisedActions );
        }
        String strTimespamp = Long.toString( new Date( ).getTime( ) );
        Map<String, String> formIdToToken = displayList.stream( ).filter( f -> !f.isActive( ) )
                .collect( Collectors.toMap( f -> Integer.toString( f.getId( ) ), f -> FormsUtils.getInactiveBypassToken( f, strTimespamp ) ) );

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_FORM_LIST, displayList );
        model.put( MARK_URL, url.getUrl( ) );
        model.put( FormsConstants.MARK_TIMESTAMP, strTimespamp );
        model.put( FormsConstants.MARK_INACTIVEBYPASSTOKENS, formIdToToken );
        model.put( FormsConstants.MARK_PROD_URL, AppPathService.getProdUrl( request ) );

        HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_DASHBOARD, user.getLocale( ), model );

        return t.getHtml( );
    }

}
