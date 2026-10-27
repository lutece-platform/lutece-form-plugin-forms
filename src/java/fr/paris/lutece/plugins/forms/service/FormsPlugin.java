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

import java.io.File;
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
    private static final String PROPERTY_SITE_NAME = "lutece.name";
    private static final String SYSTEM_PROPERTY_TMPDIR = "java.io.tmpdir";
    private static final String QUARTZ_PLUGIN_NAME = "quartz-scheduler";

    /**
     * {@inheritDoc}
     */
    @Override
    public void init( )
    {
        // ImageResourceManager
        FormsFileImageService.getInstance( ).register( );

        warnIfIndexPathIsNodeLocal( );
        logClusterDeploymentEvidence( );
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
     * Emit a startup warning when the Lucene index is likely to be JVM-local:
     * not persistent, and not shared in a multi-instance deployment.
     *
     * Two red flags trigger the warning:
     * - {@code forms.internalIndexer.lucene.indexInWebapp=true} (path resolved
     *   under the exploded webapp, which is local to each node);
     * - or the configured path is relative (the default value of
     *   {@code forms.properties}, java.io.tmpdir/..., is not resolved and lands in
     *   the working directory) or under the {@code java.io.tmpdir} directory.
     *
     * The message stays on a single instance: such a directory is not
     * persistent, so the index is lost on a redeployment or a restart of the
     * container; in a cluster it also makes search results inconsistent.
     */
    private void warnIfIndexPathIsNodeLocal( )
    {
        String strMessage = getNodeLocalIndexMessage( AppPropertiesService.getPropertyBoolean( PROPERTY_INDEX_IN_WEBAPP, true ),
                AppPropertiesService.getProperty( PROPERTY_INDEX_PATH, "" ), System.getProperty( SYSTEM_PROPERTY_TMPDIR, "" ) );

        if ( strMessage != null )
        {
            AppLogService.error( strMessage );
        }
    }

    /**
     * Build the message that tells why the Lucene index is JVM-local and which setting to change
     *
     * @param bIndexInWebapp
     *            the value of forms.internalIndexer.lucene.indexInWebapp
     * @param strConfiguredPath
     *            the value of forms.internalIndexer.lucene.indexPath
     * @param strSystemTmpDir
     *            the value of the java.io.tmpdir system property
     * @return the message, or null when the index path is not JVM-local
     */
    static String getNodeLocalIndexMessage( boolean bIndexInWebapp, String strConfiguredPath, String strSystemTmpDir )
    {
        String strCause;
        String strAction;

        if ( bIndexInWebapp )
        {
            strCause = PROPERTY_INDEX_IN_WEBAPP + "=true resolves " + PROPERTY_INDEX_PATH + " (" + strConfiguredPath
                    + ") under the exploded webapp of each instance";
            strAction = "Set " + PROPERTY_INDEX_IN_WEBAPP + "=false and point " + PROPERTY_INDEX_PATH;
        }
        else
            if ( !new File( strConfiguredPath ).isAbsolute( ) )
            {
                // java.io.tmpdir is not replaced by the temporary directory: the path is relative to the working directory
                strCause = PROPERTY_INDEX_PATH + " (" + strConfiguredPath
                        + ") is a relative path, resolved against the working directory of each instance";

                if ( strConfiguredPath.contains( SYSTEM_PROPERTY_TMPDIR ) )
                {
                    strCause += " (" + SYSTEM_PROPERTY_TMPDIR + " is not replaced by the temporary directory)";
                }

                strAction = "Point " + PROPERTY_INDEX_PATH;
            }
            else
                if ( !strSystemTmpDir.isEmpty( ) && strConfiguredPath.startsWith( strSystemTmpDir ) )
                {
                    strCause = PROPERTY_INDEX_PATH + " (" + strConfiguredPath + ") is under the " + SYSTEM_PROPERTY_TMPDIR
                            + " directory of each instance";
                    strAction = "Point " + PROPERTY_INDEX_PATH;
                }
                else
                {
                    return null;
                }

        return "[forms] The Lucene index is JVM-local: " + strCause + ". This directory is not persistent, even on a "
                + "single instance: the index is lost on a redeployment, a restart of the container or a reboot, and "
                + "searches miss the responses until it is rebuilt. " + strAction
                + " at an absolute path on a persistent volume; in a multi-instance deployment this volume must be "
                + "shared by every instance (NFS, PV, etc.), otherwise each instance keeps its own index and search results differ depending on "
                + "which instance serves the request.";
    }

    /**
     * Log the evidence for (or against) a multi-instance deployment so operators can
     * cross-check the actual runtime against their intended topology. We surface three
     * signals: the site name ({@code lutece.name}, identical on every node of the same
     * site), the runtime instance id (unique per JVM, see {@link FormsInstanceId}) and
     * the presence of the Quartz scheduler plugin.
     */
    private void logClusterDeploymentEvidence( )
    {
        String siteName = AppPropertiesService.getProperty( PROPERTY_SITE_NAME, "" );
        Plugin quartzPlugin = PluginService.getPlugin( QUARTZ_PLUGIN_NAME );
        boolean quartzAvailable = quartzPlugin != null && quartzPlugin.isInstalled( );

        AppLogService.info( "[forms] startup audit: " + PROPERTY_SITE_NAME + "="
                + ( siteName.isEmpty( ) ? "<unset>" : siteName )
                + ", instanceId=" + FormsInstanceId.VALUE
                + ", plugin-" + QUARTZ_PLUGIN_NAME + "=" + ( quartzAvailable ? "available" : "absent" ) );
    }
}
