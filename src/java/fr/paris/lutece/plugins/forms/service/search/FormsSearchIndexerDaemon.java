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
package fr.paris.lutece.plugins.forms.service.search;

import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import javax.inject.Inject;

/**
 * FormsSearchIndexerDaemon
 */
public class FormsSearchIndexerDaemon extends Daemon {

    public static final String DAEMON_ID = "formsIndexerDaemon";
    public static final String DATASTORE_DAEMON_MODE = "forms.index.full";
    public static final String DATASTORE_DAEMON_MODE_FULL = DatastoreService.VALUE_TRUE;
    public static final String DATASTORE_DAEMON_MODE_INCREMENTAL = DatastoreService.VALUE_FALSE;
    private static final boolean INDEXER_AUTO_INITIALIZE  = AppPropertiesService.getPropertyBoolean( "forms.index.writer.auto.initialize", true );

    @Inject
    private IFormSearchIndexer _formSearchIndexer = SpringContextService.getBean( LuceneFormSearchIndexer.BEAN_SERVICE );

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {

        if( (INDEXER_AUTO_INITIALIZE && !_formSearchIndexer.isIndexerInitialized( )) ||
                DATASTORE_DAEMON_MODE_FULL.equals(DatastoreService.getDataValue(DATASTORE_DAEMON_MODE,DATASTORE_DAEMON_MODE_INCREMENTAL)) )
        {
            try
            {
                setLastRunLogs( _formSearchIndexer.fullIndexing() );
            }
            finally
            {
                DatastoreService.setDataValue(DATASTORE_DAEMON_MODE,DATASTORE_DAEMON_MODE_INCREMENTAL);
            }
        }
        else
        {
            setLastRunLogs( _formSearchIndexer.incrementalIndexing( ) );
        }

    }
}
