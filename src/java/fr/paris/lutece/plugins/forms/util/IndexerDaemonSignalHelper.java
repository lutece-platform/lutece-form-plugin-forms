/*
 * Copyright (c) 2002-2026, City of Paris
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
package fr.paris.lutece.plugins.forms.util;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.service.search.FormsSearchIndexerDaemon;
import fr.paris.lutece.portal.service.daemon.AppDaemonService;
import fr.paris.lutece.portal.web.LocalVariables;

/**
 * This request listener ensures that when multiple indexer actions are created
 * in the scope of a single request, the indexer daemon is signaled only once.
 */
@WebListener
public class IndexerDaemonSignalHelper implements ServletRequestListener
{
    private static final String ATTRIBUTE_SIGNAL_INDEXER_DAEMON = "forms.signalIndexerDaemon";
    private static boolean _initialized;

    public IndexerDaemonSignalHelper( )
    {
        // recored that this listener was initialized
        _initialized = true;
    }

    /**
     * Record that the FormsSearchIndexerDaemon should be signaled at the end of this request.
     * <p>
     * If no request is found, or if the listener was not initialized, the daemon is signaled immediately.
     */
    public static void signal( )
    {
        HttpServletRequest request = LocalVariables.getRequest( );
        if ( request == null || !_initialized )
        {
            // Not in a request context, or the listener was not registered.
            // Signal immediately
            AppDaemonService.signalDaemon( FormsSearchIndexerDaemon.DAEMON_ID );
            return;
        }
        // inform the request that the daemon should be signaled
        request.setAttribute( ATTRIBUTE_SIGNAL_INDEXER_DAEMON, Boolean.TRUE );
    }

    @Override
    public void requestDestroyed( ServletRequestEvent sre )
    {
        if ( sre.getServletRequest( ).getAttribute( ATTRIBUTE_SIGNAL_INDEXER_DAEMON ) != null )
        {
            AppDaemonService.signalDaemon( FormsSearchIndexerDaemon.DAEMON_ID );
        }
    }

    @Override
    public void requestInitialized( ServletRequestEvent sre )
    {
        // NOOP
    }
}
