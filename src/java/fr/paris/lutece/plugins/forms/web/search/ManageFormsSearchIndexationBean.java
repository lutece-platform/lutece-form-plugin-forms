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
package fr.paris.lutece.plugins.forms.web.search;


import fr.paris.lutece.plugins.forms.service.search.FormsSearchIndexerDaemon;
import fr.paris.lutece.portal.service.daemon.AppDaemonService;
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.web.admin.AdminFeaturesPageJspBean;
import fr.paris.lutece.util.html.HtmlTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * This class provides the user interface to manage forms indexations Portlet
 */
public class ManageFormsSearchIndexationBean extends AdminFeaturesPageJspBean
{

    public static final String RIGHT_FORMS_INDEXER = "FORMS_SEARCH_INDEXATION";
    private static final String TEMPLATE_MANAGE_INDEXER = "admin/plugins/forms/search/manage_forms_search_indexation.html";
    private static final String MARK_LOGS = "logs";

    private static final String LABEL_FULL_LAUNCH = "forms.adminFeature.manageSearchIndexation.info.full.launch";
    private static final String LABEL_INCREMENTAL_LAUNCH = "forms.adminFeature.manageSearchIndexation.info.incremental.launch";
    private static final String LABEL_FULL_INPROGRESS = "forms.adminFeature.manageSearchIndexation.info.full.inprogress";

    /**
     * Displays the indexing info
     *
     * @param request
     *            the http request
     * @return the html code which displays the parameters page
     */
    public String getIndexingProperties( HttpServletRequest request )
    {
        HashMap<String, Object> model = new HashMap<>( );

        addInfo(model);
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, TEMPLATE_MANAGE_INDEXER ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_INDEXER, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Calls the indexing process
     *
     * @param request
     *            the http request
     * @return the result of the indexing process
     */
    public String doIndexing( HttpServletRequest request )
    {
        if ( !SecurityTokenService.getInstance( ).validate( request, TEMPLATE_MANAGE_INDEXER ) )
        {
            return getIndexingProperties( request );
        }

        HashMap<String, Object> model = new HashMap<>( );

        if ( request.getParameter( "incremental" ) != null )
        {
            DatastoreService.setDataValue(FormsSearchIndexerDaemon.DATASTORE_DAEMON_MODE, FormsSearchIndexerDaemon.DATASTORE_DAEMON_MODE_INCREMENTAL);
            model.put(MARK_LOGS , I18nService.getLocalizedString(LABEL_INCREMENTAL_LAUNCH, getLocale()));
        }
        else
        {
            DatastoreService.setDataValue(FormsSearchIndexerDaemon.DATASTORE_DAEMON_MODE, FormsSearchIndexerDaemon.DATASTORE_DAEMON_MODE_FULL);
            model.put(MARK_LOGS , I18nService.getLocalizedString(LABEL_FULL_LAUNCH, getLocale()));
        }
        AppDaemonService.signalDaemon( FormsSearchIndexerDaemon.DAEMON_ID );


        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, TEMPLATE_MANAGE_INDEXER ) );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_INDEXER, null, model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * add information on indexer status on the page
     * @param model
     */
    private void addInfo(HashMap<String, Object> model)
    {
        if( FormsSearchIndexerDaemon.DATASTORE_DAEMON_MODE_FULL
                .equals( DatastoreService.getDataValue(FormsSearchIndexerDaemon.DATASTORE_DAEMON_MODE, FormsSearchIndexerDaemon.DATASTORE_DAEMON_MODE_INCREMENTAL) ) )
        {
            model.put(MARK_LOGS ,I18nService.getLocalizedString(LABEL_FULL_INPROGRESS, getLocale()));
        }
    }

}
