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
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

/**
 * This class provides Data Access methods for Group objects
 */
public final class GroupDAO implements IGroupDAO
{
    // Constants
    private static final String SQL_QUERY_SELECTALL = "SELECT id_group, title, description, id_step, iteration_min, iteration_max, iteration_add_label, iteration_remove_label FROM forms_group";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECTALL + " WHERE id_group = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO forms_group ( title, description, id_step, iteration_min, iteration_max, iteration_add_label, iteration_remove_label ) VALUES ( ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM forms_group WHERE id_group = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE forms_group SET id_group = ?, title = ?, description = ?, id_step = ?, iteration_min = ?, iteration_max = ?, iteration_add_label = ?, iteration_remove_label = ? WHERE id_group = ?";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_group FROM forms_group";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Group group, Plugin plugin )
    {

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++, group.getTitle( ) );
            daoUtil.setString( nIndex++, group.getDescription( ) );
            daoUtil.setInt( nIndex++, group.getIdStep( ) );
            daoUtil.setInt( nIndex++, group.getIterationMin( ) );
            daoUtil.setInt( nIndex++, group.getIterationMax( ) );
            daoUtil.setString( nIndex++, group.getIterationAddLabel( ) );
            daoUtil.setString( nIndex++, group.getIterationRemoveLabel( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                group.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Group load( int nKey, Plugin plugin )
    {

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );
            Group group = null;

            if ( daoUtil.next( ) )
            {
                group = dataToObject( daoUtil );
            }

            return group;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nKey, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {

            daoUtil.setInt( 1, nKey );
            daoUtil.executeUpdate( );
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( Group group, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {

            int nIndex = 1;

            daoUtil.setInt( nIndex++, group.getId( ) );
            daoUtil.setString( nIndex++, group.getTitle( ) );
            daoUtil.setString( nIndex++, group.getDescription( ) );
            daoUtil.setInt( nIndex++, group.getIdStep( ) );
            daoUtil.setInt( nIndex++, group.getIterationMin( ) );
            daoUtil.setInt( nIndex++, group.getIterationMax( ) );
            daoUtil.setString( nIndex++, group.getIterationAddLabel( ) );
            daoUtil.setString( nIndex++, group.getIterationRemoveLabel( ) );
            daoUtil.setInt( nIndex, group.getId( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Group> selectGroupsList( Plugin plugin )
    {
        List<Group> groupList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                groupList.add( dataToObject( daoUtil ) );
            }

        }
        return groupList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdGroupsList( Plugin plugin )
    {
        List<Integer> groupList = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                groupList.add( daoUtil.getInt( 1 ) );
            }

        }
        return groupList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectGroupsReferenceList( Plugin plugin )
    {
        ReferenceList groupList = new ReferenceList( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                groupList.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
            }

        }
        return groupList;
    }

    @Override
    public List<Group> selectGroupsListByListIdStep( List<Integer> idSteplist, Plugin plugin )
    {
        List<Group> groupList = new ArrayList<>( );
        if ( CollectionUtils.isEmpty( idSteplist ) )
        {
            return groupList;
        }

        String query = SQL_QUERY_SELECTALL + " WHERE id_step IN ( ";
        query += idSteplist.stream( ).map( i -> "?" ).collect( Collectors.joining( "," ) );
        query += " )";

        try ( DAOUtil daoUtil = new DAOUtil( query, plugin ) )
        {
            int index = 0;
            for ( Integer id : idSteplist )
            {
                daoUtil.setInt( ++index, id );
            }
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                groupList.add( dataToObject( daoUtil ) );
            }
        }
        return groupList;
    }

    /**
     * 
     * @param daoUtil
     *            The daoutil
     * @return The populated Group object
     */
    private Group dataToObject( DAOUtil daoUtil )
    {
        Group group = new Group( );

        group.setId( daoUtil.getInt( "id_group" ) );
        group.setTitle( daoUtil.getString( "title" ) );
        group.setDescription( daoUtil.getString( "description" ) );
        group.setIdStep( daoUtil.getInt( "id_step" ) );
        group.setIterationMin( daoUtil.getInt( "iteration_min" ) );
        group.setIterationMax( daoUtil.getInt( "iteration_max" ) );
        group.setIterationAddLabel( daoUtil.getString( "iteration_add_label" ) );
        group.setIterationRemoveLabel( daoUtil.getString( "iteration_remove_label" ) );

        return group;
    }
}
