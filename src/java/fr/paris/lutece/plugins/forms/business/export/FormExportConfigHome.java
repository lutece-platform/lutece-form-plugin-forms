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
package fr.paris.lutece.plugins.forms.business.export;

import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

public class FormExportConfigHome
{
    private static IFormExportConfigDao _dao = SpringContextService.getBean( "forms.formExportConfigDao" );
    private static Plugin _plugin = PluginService.getPlugin( "forms" );

    private FormExportConfigHome( )
    {
    }

    /**
     * Create an instance of the FormExportConfig class
     * 
     * @param config
     *            The instance of the FormExportConfig which contains the informations to store
     * @return The instance of control which has been created with its primary key.
     */
    public static FormExportConfig create( FormExportConfig config )
    {
        _dao.insert( config, _plugin );
        return config;
    }

    /**
     * Update of the FormExportConfig which is specified in parameter
     * 
     * @param config
     *            The instance of the FormExportConfig which contains the data to store
     * @return The instance of the config which has been updated
     */
    public static FormExportConfig update( FormExportConfig config )
    {
        _dao.store( config, _plugin );
        return config;
    }

    /**
     * Returns an instance of a FormExportConfig whose identifier is specified in parameter
     * 
     * @param nKey
     *            The FormExportConfig primary key
     * @return an instance of Form
     */
    public static FormExportConfig findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Delete All config for a give form
     * 
     * @param idForm
     */
    public static void removeByForm( int idForm )
    {
        _dao.deleteByForm( idForm, _plugin );
    }

    /**
     * Find All config for a give form
     * 
     * @param idForm
     */
    public static List<FormExportConfig> findByForm( int idForm )
    {
        return _dao.loadByForm( idForm, _plugin );
    }
}
