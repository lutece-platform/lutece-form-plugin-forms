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
 * This class provides instances management methods (create, find, ...) for FormResponseStep objects
 */
public final class FormResponseStepHome
{
    // Static variable pointed at the DAO instance
    private static IFormResponseStepDAO _dao = SpringContextService.getBean( "forms.formResponseStepDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "forms" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private FormResponseStepHome( )
    {
    }

    /**
     * Create an instance of the formResponseStep class
     * 
     * @param formResponseStep
     *            The instance of the FormResponseStep which contains the informations to store
     * @return The instance of formResponseStep which has been created with its primary key.
     */
    public static FormResponseStep create( FormResponseStep formResponseStep )
    {
        _dao.insert( formResponseStep, _plugin );

        return formResponseStep;
    }

    /**
     * Update of the formResponseStep which is specified in parameter
     * 
     * @param formResponseStep
     *            The instance of the FormResponseStep which contains the data to store
     * @return The instance of the formResponseStep which has been updated
     */
    public static FormResponseStep update( FormResponseStep formResponseStep )
    {
        _dao.store( formResponseStep, _plugin );

        return formResponseStep;
    }

    /**
     * Remove the formResponseStep whose identifier is specified in parameter
     * 
     * @param nKey
     *            The formResponseStep Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of a formResponseStep whose identifier is specified in parameter
     * 
     * @param nKey
     *            The formResponseStep primary key
     * @return an instance of FormResponseStep
     */
    public static FormResponseStep findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the formResponseStep objects and returns them as a list
     * 
     * @return the list which contains the data of all the formResponseStep objects
     */
    public static List<FormResponseStep> getFormResponseStepList( )
    {
        return _dao.selectFormResponseStepList( _plugin );
    }

    /**
     * Remove all the formResponse linked to a given Form
     * 
     * @param nIdFormResponse
     *            The form Identifier
     */
    public static void removeByFormResponse( int nIdFormResponse )
    {
        _dao.deleteByFormResponse( nIdFormResponse, _plugin );
    }

    /**
     * Load the order step id list by FormResponse id
     * 
     * @param nIdFormResponse
     *            The form Identifier
     * @return the list which contains the data of all the formResponseStep objects
     */
    public static List<Integer> getListIdStepByFormResponse( int nIdFormResponse )
    {
        return _dao.selectListIdStepByFormResponse( nIdFormResponse, _plugin );
    }

}
