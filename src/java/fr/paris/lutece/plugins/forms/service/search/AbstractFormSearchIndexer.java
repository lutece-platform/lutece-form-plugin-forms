/*
 * Copyright (c) 2002-2018, Mairie de Paris
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
package fr.paris.lutece.plugins.forms.service.search;

import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.form.search.IndexerAction;
import fr.paris.lutece.plugins.forms.business.form.search.IndexerActionFilter;
import fr.paris.lutece.plugins.forms.business.form.search.IndexerActionHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.util.AppLogService;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

public abstract class AbstractFormSearchIndexer implements IFormSearchIndexer
{
    @Inject
    private IFormSearchIndexer _indexer;

    /**
     * Process indexing
     * 
     * @param bCreate
     *            true for start full indexing false for begin incremental indexing
     * @return the log
     */
    protected String processIndexing( boolean bCreate )
    {
        StringBuffer sbLogs = new StringBuffer( );

        try
        {
            sbLogs.append( "\r\nIndexing form responses contents ...\r\n" );

            Date start = new Date( );

            sbLogs.append( "\r\n<strong>Indexer : " );
            sbLogs.append( _indexer.getName( ) );
            sbLogs.append( " - " );
            sbLogs.append( _indexer.getDescription( ) );
            sbLogs.append( "</strong>\r\n" );

            if ( bCreate )
            {
                sbLogs.append( "Full indexing\r\n" );
                _indexer.processFullIndexing( sbLogs );
            }
            else
            {
                sbLogs.append( "Incremental indexing\r\n" );
                _indexer.processIncrementalIndexing( sbLogs );
            }

            Date end = new Date( );

            sbLogs.append( "Duration of the treatment : " );
            sbLogs.append( end.getTime( ) - start.getTime( ) );
            sbLogs.append( " milliseconds\r\n" );
        }
        catch( Exception e )
        {
            sbLogs.append( " caught a " );
            sbLogs.append( e.getClass( ) );
            sbLogs.append( "\n with message: " );
            sbLogs.append( e.getMessage( ) );
            sbLogs.append( "\r\n" );
            AppLogService.error( "Indexing error : " + e.getMessage( ), e );
        }
        return sbLogs.toString( );
    }

    /**
     * Add Indexer Action to perform on a form response
     * 
     * @param nIdFormResponse
     *            the id of the formResponse
     * @param nIdTask
     *            the key of the action to do
     * @param plugin
     *            the plugin
     */
    @Override
    public void addIndexerAction( int nIdFormResponse, int nIdTask, Plugin plugin )
    {
        // TODO DO NOT LOAD THE ENTIRE DATA
        FormResponse formResponse = FormResponseHome.findByPrimaryKey( nIdFormResponse );
        if ( formResponse != null )
        {
            IndexerAction indexerAction = new IndexerAction( );
            indexerAction.setIdFormResponse( formResponse.getId( ) );
            indexerAction.setIdTask( nIdTask );
            IndexerActionHome.create( indexerAction, plugin );

        }
    }

    /**
     * Remove a Indexer Action
     * 
     * @param nIdAction
     *            the key of the action to remove
     * @param plugin
     *            the plugin
     */
    protected void removeIndexerAction( int nIdAction, Plugin plugin )
    {
        IndexerActionHome.remove( nIdAction, plugin );
    }

    /**
     * return a list of IndexerAction by task key
     * 
     * @param nIdTask
     *            the task kety
     * @param plugin
     *            the plugin
     * @return a list of IndexerAction
     */
    protected List<IndexerAction> getAllIndexerActionByTask( int nIdTask, Plugin plugin )
    {
        IndexerActionFilter filter = new IndexerActionFilter( );
        filter.setIdTask( nIdTask );

        return IndexerActionHome.getList( filter, plugin );
    }
}
