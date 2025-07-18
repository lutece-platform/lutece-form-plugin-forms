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
package fr.paris.lutece.plugins.forms.service.listener;

import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.form.search.IndexerAction;
import fr.paris.lutece.plugins.forms.service.FormsPlugin;
import fr.paris.lutece.plugins.forms.service.search.IFormSearchIndexer;
import fr.paris.lutece.portal.business.event.EventRessourceListener;
import fr.paris.lutece.portal.business.event.ResourceEvent;
import fr.paris.lutece.portal.service.util.AppLogService;
import javax.inject.Inject;
import java.sql.Timestamp;

public class FormResponseEventListener implements EventRessourceListener
{
    private static final String CONSTANT_FORM_RESPONSE_LISTENER_NAME = "formResponseEventListener";
    @Inject
    private IFormSearchIndexer _formSearchIndexer;

    @Override
    public String getName( )
    {
        return CONSTANT_FORM_RESPONSE_LISTENER_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addedResource( ResourceEvent event )
    {
        indexResource( event, IndexerAction.TASK_CREATE );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deletedResource( ResourceEvent event )
    {
        indexResource( event, IndexerAction.TASK_DELETE );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updatedResource( ResourceEvent event )
    {
        updateDateFormResponse( event );
        indexResource( event, IndexerAction.TASK_MODIFY );
    }

    /**
     * Index the forms response
     *
     * @param event
     *            the event
     * @param nIdTask
     *            The id task
     */
    private void indexResource( ResourceEvent event, int nIdTask )
    {
        if ( !checkResourceType( event ) )
        {
            return;
        }
        try
        {
            int nIdResource = Integer.parseInt( event.getIdResource( ) );
            _formSearchIndexer.indexDocument( nIdResource, nIdTask, FormsPlugin.getPlugin( ) );
        }
        catch( NumberFormatException e )
        {
            AppLogService.error( "Unable to parse given event id ressource to integer " + event.getIdResource( ), e );
        }
    }

    /**
     * Update the date update of the forms response
     *
     * @param event
     *            the event
     */
    private void updateDateFormResponse( ResourceEvent event )
    {
        if ( !checkResourceType( event ) )
        {
            return;
        }
        try
        {
            int nIdResource = Integer.parseInt( event.getIdResource( ) );

            FormResponse response = FormResponseHome.findByPrimaryKey( nIdResource );
            response.setUpdate( new Timestamp( new java.util.Date( ).getTime( ) ) );
            FormResponseHome.update( response );

        }
        catch( NumberFormatException e )
        {
            AppLogService.error( "Unable to parse given event id ressource to integer " + event.getIdResource( ), e );
        }
    }

    private boolean checkResourceType( ResourceEvent event )
    {
        return FormResponse.RESOURCE_TYPE.equals( event.getTypeResource( ) );
    }
}
