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
package fr.paris.lutece.plugins.forms.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;

/**
 * This class provides instances management methods (create, find, ...) for FormDisplay objects
 */
public final class FormResponseHome
{
    // Static variable pointed at the DAO instance
    private static IFormResponseDAO _dao = SpringContextService.getBean( "forms.formResponseDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "forms" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private FormResponseHome( )
    {
    }

    /**
     * Create an instance of the formResponse class
     * 
     * @param formResponse
     *            The instance of the FormResponse which contains the informations to store
     * @return The instance of formResponse which has been created with its primary key.
     */
    public static FormResponse create( FormResponse formResponse )
    {
        _dao.insert( formResponse, _plugin );

        return formResponse;
    }

    /**
     * Update of the formResponse which is specified in parameter
     * 
     * @param formResponse
     *            The instance of the FormResponse which contains the data to store
     * @return The instance of the formResponse which has been updated
     */
    public static FormResponse update( FormResponse formResponse )
    {
        _dao.store( formResponse, _plugin );

        return formResponse;
    }

    /**
     * Remove the formResponse whose identifier is specified in parameter
     * 
     * @param nKey
     *            The formResponse Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of a formResponse whose identifier is specified in parameter
     * 
     * @param nKey
     *            The formResponse primary key
     * @return an instance of FormResponse
     */
    public static FormResponse findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the formResponse objects and returns them as a list
     * 
     * @return the list which contains the data of all the formResponse objects
     */
    public static List<FormResponse> getFormResponseList( )
    {
        return _dao.selectFormResponseList( _plugin );
    }

    /**
     * Remove all the formResponse linked to a given Form
     * 
     * @param nIdForm
     *            The form Identifier
     */
    public static void removeByForm( int nIdForm )
    {
        _dao.deleteByForm( nIdForm, _plugin );
        
    }
}
