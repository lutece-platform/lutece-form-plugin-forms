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

/**
 * This class provides instances management methods (create, find, ...) for Group objects
 */
public final class GroupHome
{
    // Static variable pointed at the DAO instance
    private static IGroupDAO _dao = SpringContextService.getBean( "forms.groupDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "forms" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private GroupHome( )
    {
    }

    /**
     * Create an instance of the group class
     * 
     * @param group
     *            The instance of the Group which contains the informations to store
     * @return The instance of group which has been created with its primary key.
     */
    public static Group create( Group group )
    {
        _dao.insert( group, _plugin );

        return group;
    }

    /**
     * Update of the group which is specified in parameter
     * 
     * @param group
     *            The instance of the Group which contains the data to store
     * @return The instance of the group which has been updated
     */
    public static Group update( Group group )
    {
        _dao.store( group, _plugin );

        return group;
    }

    /**
     * Remove the group whose identifier is specified in parameter
     * 
     * @param nKey
     *            The group Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of a group whose identifier is specified in parameter
     * 
     * @param nKey
     *            The group primary key
     * @return an instance of Group
     */
    public static Group findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the group objects and returns them as a list
     * 
     * @return the list which contains the data of all the group objects
     */
    public static List<Group> getGroupsList( )
    {
        return _dao.selectGroupsList( _plugin );
    }

    /**
     * Load the id of all the group objects and returns them as a list
     * 
     * @return the list which contains the id of all the group objects
     */
    public static List<Integer> getIdGroupsList( )
    {
        return _dao.selectIdGroupsList( _plugin );
    }

    /**
     * Load the data of all the group objects and returns them as a referenceList
     * 
     * @return the referenceList which contains the data of all the group objects
     */
    public static ReferenceList getGroupsReferenceList( )
    {
        return _dao.selectGroupsReferenceList( _plugin );
    }

    /**
     * Load the data of all the group objects and returns them as a list
     * 
     * @param idStepList
     * @return the list which contains the data of all the group objects
     */
    public static List<Group> getGroupsListByIdStepList( List<Integer> idStepList )
    {
        return _dao.selectGroupsListByListIdStep( idStepList, _plugin );
    }
}
