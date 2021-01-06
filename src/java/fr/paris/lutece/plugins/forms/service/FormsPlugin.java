/*
 * Copyright (c) 2002-2021, City of Paris
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
import fr.paris.lutece.portal.service.event.ResourceEventManager;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginDefaultImplementation;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * class FormPlugin
 */
public final class FormsPlugin extends PluginDefaultImplementation implements Serializable
{
    /** The Constant PLUGIN_NAME. */
    public static final String PLUGIN_NAME = "forms";

    // Generated serial UID
    private static final long serialVersionUID = 363631628732516426L;

    // Beans
    private static final String BEAN_FORM_RESPONSE_EVENT_LISTENER = "forms.formResponseEventListener";

    /**
     * {@inheritDoc}
     */
    @Override
    public void init( )
    {
        // ImageResourceManager
        FormsFileImageService.getInstance( ).register( );
        ResourceEventManager.register( SpringContextService.getBean( BEAN_FORM_RESPONSE_EVENT_LISTENER ) );
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
}
