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
package fr.paris.lutece.plugins.forms.service;

import java.io.Serializable;

import fr.paris.lutece.plugins.forms.web.file.FormsFileImageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginDefaultImplementation;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * class FormPlugin
 */
public final class FormsPlugin extends PluginDefaultImplementation implements Serializable
{
    /** The Constant PLUGIN_NAME. */
    public static final String PLUGIN_NAME = "forms";

    // Generated serial UID
    private static final long serialVersionUID = 363631628732516426L;

    private static final String PROPERTY_INDEX_PATH = "forms.internalIndexer.lucene.indexPath";
    private static final String PROPERTY_INDEX_IN_WEBAPP = "forms.internalIndexer.lucene.indexInWebapp";
    private static final String SYSTEM_PROPERTY_TMPDIR = "java.io.tmpdir";

    /**
     * {@inheritDoc}
     */
    @Override
    public void init( )
    {
        // ImageResourceManager
        FormsFileImageService.getInstance( ).register( );

        warnIfIndexPathIsNodeLocal( );
    }

    /**
     * Return the plugin
     *
     * @return the plugin
     */
    public static Plugin getPlugin( )
    {
        return PluginService.getPlugin( PLUGIN_NAME );
    }

    /**
     * Emit a startup warning when the Lucene index is likely to be JVM-local,
     * which breaks search consistency in a multi-instance deployment.
     *
     * Two red flags trigger the warning:
     * - {@code forms.internalIndexer.lucene.indexInWebapp=true} (path resolved
     *   under the exploded webapp, which is local to each node);
     * - or the configured path contains/starts with {@code java.io.tmpdir}
     *   (default value in {@code forms.properties}, still JVM-local).
     *
     * Operators running a single-instance deployment can safely ignore the
     * message; the warning is meant to flag the misconfiguration explicitly
     * before it surfaces as inconsistent search results in production.
     */
    private void warnIfIndexPathIsNodeLocal( )
    {
        boolean indexInWebapp = AppPropertiesService.getPropertyBoolean( PROPERTY_INDEX_IN_WEBAPP, true );
        String configuredPath = AppPropertiesService.getProperty( PROPERTY_INDEX_PATH, "" );
        String systemTmpDir = System.getProperty( SYSTEM_PROPERTY_TMPDIR, "" );

        boolean underSystemTmpdir = !systemTmpDir.isEmpty( ) && configuredPath.startsWith( systemTmpDir );
        boolean referencesTmpdirLiteral = configuredPath.contains( SYSTEM_PROPERTY_TMPDIR );

        if ( indexInWebapp || underSystemTmpdir || referencesTmpdirLiteral )
        {
            AppLogService.error(
                    "[forms] Lucene index path appears to be JVM-local (indexInWebapp=" + indexInWebapp
                            + ", configuredPath=" + configuredPath + "). "
                            + "In a multi-instance deployment, set forms.internalIndexer.lucene.indexInWebapp=false "
                            + "and point forms.internalIndexer.lucene.indexPath at a shared volume (NFS, PV, etc.) "
                            + "shared by every instance; otherwise each node will maintain a separate index and "
                            + "search results will differ depending on which instance serves the request." );
        }
    }
}
