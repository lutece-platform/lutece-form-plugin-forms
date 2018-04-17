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
 * This class provides Data Access methods for Transition objects
 */
public final class TransitionDAO implements ITransitionDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_transition, from_step, next_step, id_control, priority FROM forms_transition WHERE id_transition = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO forms_transition ( from_step, next_step, id_control, priority ) VALUES ( ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM forms_transition WHERE id_transition = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE forms_transition SET id_transition = ?, from_step = ?, next_step = ?, id_control = ?, priority = ? WHERE id_transition = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_transition, from_step, next_step, id_control, priority FROM forms_transition";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_transition FROM forms_transition";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Transition transition, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin );
        try
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, transition.getFromStep( ) );
            daoUtil.setInt( nIndex++, transition.getNextStep( ) );
            daoUtil.setInt( nIndex++, transition.getIdControl( ) );
            daoUtil.setInt( nIndex++, transition.getPriority( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                transition.setId( daoUtil.getGeneratedKeyInt( 1 ) );
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
    public Transition load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery( );
        Transition transition = null;

        if ( daoUtil.next( ) )
        {
            transition = new Transition( );
            int nIndex = 1;

            transition.setId( daoUtil.getInt( nIndex++ ) );
            transition.setFromStep( daoUtil.getInt( nIndex++ ) );
            transition.setNextStep( daoUtil.getInt( nIndex++ ) );
            transition.setIdControl( daoUtil.getInt( nIndex++ ) );
            transition.setPriority( daoUtil.getInt( nIndex++ ) );
        }

        daoUtil.free( );
        return transition;
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
    public void store( Transition transition, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        int nIndex = 1;

        daoUtil.setInt( nIndex++, transition.getId( ) );
        daoUtil.setInt( nIndex++, transition.getFromStep( ) );
        daoUtil.setInt( nIndex++, transition.getNextStep( ) );
        daoUtil.setInt( nIndex++, transition.getIdControl( ) );
        daoUtil.setInt( nIndex++, transition.getPriority( ) );
        daoUtil.setInt( nIndex, transition.getId( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Transition> selectTransitionsList( Plugin plugin )
    {
        List<Transition> transitionList = new ArrayList<Transition>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            Transition transition = new Transition( );
            int nIndex = 1;

            transition.setId( daoUtil.getInt( nIndex++ ) );
            transition.setFromStep( daoUtil.getInt( nIndex++ ) );
            transition.setNextStep( daoUtil.getInt( nIndex++ ) );
            transition.setIdControl( daoUtil.getInt( nIndex++ ) );
            transition.setPriority( daoUtil.getInt( nIndex++ ) );

            transitionList.add( transition );
        }

        daoUtil.free( );
        return transitionList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdTransitionsList( Plugin plugin )
    {
        List<Integer> transitionList = new ArrayList<Integer>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            transitionList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free( );
        return transitionList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectTransitionsReferenceList( Plugin plugin )
    {
        ReferenceList transitionList = new ReferenceList( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            transitionList.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
        }

        daoUtil.free( );
        return transitionList;
    }
}
