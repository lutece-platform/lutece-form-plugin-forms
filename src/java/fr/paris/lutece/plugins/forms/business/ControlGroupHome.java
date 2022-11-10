/*
 * Copyright (c) 2002-2022, City of Paris
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
import fr.paris.lutece.util.ReferenceList;


import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * This class provides instances management methods (create, find, ...) for ControlGroup objects
 */
public final class ControlGroupHome
{
    // Static variable pointed at the DAO instance
    private static IControlGroupDAO _dao = SpringContextService.getBean( "forms.controlGroupDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "forms" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private ControlGroupHome(  )
    {
    }

    /**
     * Create an instance of the controlGroup class
     * @param controlGroup The instance of the ControlGroup which contains the informations to store
     * @return The  instance of controlGroup which has been created with its primary key.
     */
    public static ControlGroup create( ControlGroup controlGroup )
    {
        _dao.insert( controlGroup, _plugin );

        return controlGroup;
    }

    /**
     * Update of the controlGroup which is specified in parameter
     * @param controlGroup The instance of the ControlGroup which contains the data to store
     * @return The instance of the  controlGroup which has been updated
     */
    public static ControlGroup update( ControlGroup controlGroup )
    {
        _dao.store( controlGroup, _plugin );

        return controlGroup;
    }

    /**
     * Remove the controlGroup whose identifier is specified in parameter
     * @param nKey The controlGroup Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of a controlGroup whose identifier is specified in parameter
     * @param nKey The controlGroup primary key
     * @return an instance of ControlGroup
     */
    public static Optional<ControlGroup> findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the controlGroup objects and returns them as a list
     * @return the list which contains the data of all the controlGroup objects
     */
    public static List<ControlGroup> getControlGroupsList( )
    {
        return _dao.selectControlGroupsList( _plugin );
    }
    
    /**
     * Load the id of all the controlGroup objects and returns them as a list
     * @return the list which contains the id of all the controlGroup objects
     */
    public static List<Integer> getIdControlGroupsList( )
    {
        return _dao.selectIdControlGroupsList( _plugin );
    }
    
    /**
     * Load the data of all the controlGroup objects and returns them as a referenceList
     * @return the referenceList which contains the data of all the controlGroup objects
     */
    public static ReferenceList getControlGroupsReferenceList( )
    {
        return _dao.selectControlGroupsReferenceList( _plugin );
    }
    
    /**
     * Load the data of all the logical operators from enum and returns them as a referenceList
     * @param locale TODO
     * @return the referenceList which contains the data of all the logical operators objects
     */
    public static ReferenceList getLogicalOperatorsReferenceList(Locale locale )
    {
    	return _dao.selectLogicalOperatorsReferenceList(locale);
    }
    
	
    /**
     * Load the data of all the avant objects and returns them as a list
     * @param listIds liste of ids
     * @return the list which contains the data of all the avant objects
     */
    public static List<ControlGroup> getControlGroupsListByIds( List<Integer> listIds )
    {
        return _dao.selectControlGroupsListByIds( _plugin, listIds );
    }

}

