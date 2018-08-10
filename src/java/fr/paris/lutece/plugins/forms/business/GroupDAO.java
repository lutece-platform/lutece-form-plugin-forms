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
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides Data Access methods for Group objects
 */
public final class GroupDAO implements IGroupDAO
{
    // Constants
	private static final String SQL_QUERY_SELECTALL = "SELECT id_group, title, description, id_step, collapsible, iteration_number FROM forms_group";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECTALL + " WHERE id_group = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO forms_group ( title, description, id_step, collapsible, iteration_number ) VALUES ( ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM forms_group WHERE id_group = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE forms_group SET id_group = ?, title = ?, description = ?, id_step = ?, collapsible = ?, iteration_number = ? WHERE id_group = ?";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_group FROM forms_group";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Group group, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin );
        try
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++, group.getTitle( ) );
            daoUtil.setString( nIndex++, group.getDescription( ) );
            daoUtil.setInt( nIndex++, group.getIdStep( ) );
            daoUtil.setBoolean( nIndex++, group.getCollapsible( ) );
            daoUtil.setInt( nIndex++, group.getIterationNumber( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                group.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
        finally
        {
            daoUtil.free( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Group load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery( );
        Group group = null;

        if ( daoUtil.next( ) )
        {
            group = dataToObject( daoUtil );
        }

        daoUtil.free( );
        return group;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( Group group, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        int nIndex = 1;

        daoUtil.setInt( nIndex++, group.getId( ) );
        daoUtil.setString( nIndex++, group.getTitle( ) );
        daoUtil.setString( nIndex++, group.getDescription( ) );
        daoUtil.setInt( nIndex++, group.getIdStep( ) );
        daoUtil.setBoolean( nIndex++, group.getCollapsible( ) );
        daoUtil.setInt( nIndex++, group.getIterationNumber( ) );
        daoUtil.setInt( nIndex, group.getId( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Group> selectGroupsList( Plugin plugin )
    {
        List<Group> groupList = new ArrayList<Group>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            groupList.add( dataToObject( daoUtil ) );
        }

        daoUtil.free( );
        return groupList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdGroupsList( Plugin plugin )
    {
        List<Integer> groupList = new ArrayList<Integer>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            groupList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free( );
        return groupList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectGroupsReferenceList( Plugin plugin )
    {
        ReferenceList groupList = new ReferenceList( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            groupList.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
        }

        daoUtil.free( );
        return groupList;
    }
    
    /**
     * 
     * @param daoUtil
     * 			The daoutil
     * @return
     * 		The populated Group object
     */
    private Group dataToObject( DAOUtil daoUtil )
    {
    	Group group = new Group( );
    	
    	group.setId( daoUtil.getInt( "id_group" ) );
        group.setTitle( daoUtil.getString( "title" ) );
        group.setDescription( daoUtil.getString( "description" ) );
        group.setIdStep( daoUtil.getInt( "id_step" ) );
        group.setCollapsible( daoUtil.getBoolean( "collapsible" ) );
        group.setIterationNumber( daoUtil.getInt( "iteration_number" ) );
        
        return group;
    }
}
