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
package fr.paris.lutece.plugins.forms.business.form.search;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class provides Data Access methods for Indexer Action objects
 */
@ApplicationScoped
public class IndexerActionDAO implements IIndexerActionDAO
{	
    // Constants
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_action,id_form_response,id_task" + " FROM forms_indexer_action WHERE id_action = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO forms_indexer_action( id_form_response,id_task)" + " VALUES(?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM forms_indexer_action";
    private static final String SQL_QUERY_DELETE_ID = SQL_QUERY_DELETE + " WHERE id_action = ? ";
    private static final String SQL_QUERY_DELETE_IN = SQL_QUERY_DELETE + " WHERE id_action IN (";
    private static final String SQL_QUERY_UPDATE = "UPDATE forms_indexer_action SET id_action=?,id_form_response=?,id_task=? WHERE id_action = ? ";
    private static final String SQL_QUERY_SELECT = "SELECT id_action,id_form_response,id_task" + " FROM forms_indexer_action  ";
    private static final String SQL_FILTER_ID_TASK = " WHERE id_task = ? ";

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( IndexerAction indexerAction, Plugin plugin )
    {

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            daoUtil.setInt( 1, indexerAction.getIdFormResponse( ) );
            daoUtil.setInt( 2, indexerAction.getIdTask( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                indexerAction.setIdAction( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IndexerAction load( int nId, Plugin plugin )
    {
        IndexerAction indexerAction = null;

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin ) )
        {
            daoUtil.setInt( 1, nId );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                indexerAction = new IndexerAction( );
                indexerAction.setIdAction( daoUtil.getInt( 1 ) );
                indexerAction.setIdFormResponse( daoUtil.getInt( 2 ) );
                indexerAction.setIdTask( daoUtil.getInt( 3 ) );
            }
        }

        return indexerAction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nId, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_ID, plugin ) )
        {

            daoUtil.setInt( 1, nId );
            daoUtil.executeUpdate( );
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( List<Integer> idList, Plugin plugin )
    {
        if( !idList.isEmpty() ) {
            String query = SQL_QUERY_DELETE_IN + idList.stream().distinct().map(i -> "?").collect(Collectors.joining(",")) + " )";

            try (DAOUtil daoUtil = new DAOUtil(query, plugin)) {
                for (int i = 0; i < idList.size(); i++) {
                    daoUtil.setInt(i + 1, idList.get(i));
                }
                daoUtil.executeUpdate();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll( Plugin plugin )
    {
        try (DAOUtil daoUtil = new DAOUtil(SQL_QUERY_DELETE, plugin)) {
            daoUtil.executeUpdate();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( IndexerAction indexerAction, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {

            daoUtil.setInt( 1, indexerAction.getIdAction( ) );
            daoUtil.setInt( 2, indexerAction.getIdFormResponse( ) );
            daoUtil.setInt( 3, indexerAction.getIdTask( ) );

            daoUtil.setInt( 4, indexerAction.getIdAction( ) );

            daoUtil.executeUpdate( );
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IndexerAction> selectList( IndexerActionFilter filter, Plugin plugin )
    {
        List<IndexerAction> indexerActionList = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT + SQL_FILTER_ID_TASK, plugin ) )
        {

            int nIndex = 1;

            if ( filter.containsIdTask( ) )
            {
                daoUtil.setInt( nIndex, filter.getIdTask( ) );
            }

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                indexerActionList.add( dataToObject(daoUtil) );
            }
        }

        return indexerActionList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IndexerAction> selectList( Plugin plugin )
    {
        List<IndexerAction> indexerActionList = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT , plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                indexerActionList.add( dataToObject(daoUtil) );
            }
        }

        return indexerActionList;
    }

    /**
     *
     * @param daoUtil
     *            The daoutil
     * @return The populated FormAction object
     */
    private IndexerAction dataToObject(DAOUtil daoUtil )
    {
        IndexerAction indexerAction = new IndexerAction( );

        indexerAction.setIdAction( daoUtil.getInt("id_action") );
        indexerAction.setIdFormResponse( daoUtil.getInt("id_form_response") );
        indexerAction.setIdTask( daoUtil.getInt("id_task") );

        return indexerAction;
    }

}
