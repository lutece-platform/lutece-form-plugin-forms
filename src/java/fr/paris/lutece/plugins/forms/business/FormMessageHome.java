/*
 * Copyright (c) 2002-2019, Mairie de Paris
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
package fr.paris.lutece.plugins.forms.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods (create, find, ...) for FormResponseHome objects
 */
public final class FormMessageHome
{
    // Static variable pointed at the DAO instance
    private static IFormMessageDAO _dao = SpringContextService.getBean( "forms.formMessageDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "forms" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private FormMessageHome( )
    {
    }

    /**
     * Create an instance of the formMessage class
     * 
     * @param formMessage
     *            The instance of the FormMessage which contains the informations to store
     * @return The instance of formMessage which has been created with its primary key.
     */
    public static FormMessage create( FormMessage formMessage )
    {
        _dao.insert( formMessage, _plugin );

        return formMessage;
    }

    /**
     * Update of the formMessage which is specified in parameter
     * 
     * @param formMessage
     *            The instance of the FormMessage which contains the data to store
     * @return The instance of the formMessage which has been updated
     */
    public static FormMessage update( FormMessage formMessage )
    {
        _dao.store( formMessage, _plugin );

        return formMessage;
    }

    /**
     * Remove the formMessage whose identifier is specified in parameter
     * 
     * @param nKey
     *            The formMessage Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Remove the formMessage whose form identifier is specified in parameter
     * 
     * @param nIdForm
     *            The form Id
     */
    public static void removeByForm( int nIdForm )
    {
        _dao.deleteByForm( nIdForm, _plugin );
    }

    /**
     * Returns an instance of a formMessage whose identifier is specified in parameter
     * 
     * @param nKey
     *            The formMessage primary key
     * @return an instance of FormMessage
     */
    public static FormMessage findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Returns an instance of a formMessage for the given form id
     * 
     * @param nIdForm
     *            The form primary key
     * @return an instance of FormMessage
     */
    public static FormMessage findByForm( int nIdForm )
    {
        return _dao.selectByForm( nIdForm, _plugin );
    }
}
