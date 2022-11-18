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

/**
 * This class provides Data Access methods for Transition objects
 */
public final class TransitionDAO implements ITransitionDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT t.id_transition, t.from_step, fromStep.title as fromTitle, t.next_step, nextStep.title as nextTitle, t.priority, f.id_form FROM forms_transition t "
            + "INNER JOIN forms_step fromStep ON fromStep.id_step = t.from_step " + "INNER JOIN forms_step nextStep ON nextStep.id_step = t.next_step "
            + " LEFT JOIN forms_form f ON fromStep.id_form = f.id_form ";
    private static final String SQL_FILTER_BY_ID = " WHERE t.id_transition = ? ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO forms_transition ( from_step, next_step, priority ) VALUES ( ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM forms_transition WHERE id_transition = ? ";
    private static final String SQL_QUERY_DELETE_BY_STEP = "DELETE FROM forms_transition WHERE from_step = ? OR next_step = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE forms_transition SET id_transition = ?, from_step = ?, next_step = ?, priority = ? WHERE id_transition = ?";
    private static final String SQL_ORDER_BY_PRIORITY = " ORDER BY priority ASC";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_transition FROM forms_transition";
    private static final String SQL_FILTER_BY_STEP = " WHERE t.from_step = ? " + SQL_ORDER_BY_PRIORITY;
    private static final String SQL_FILTER_BY_FORM = " WHERE f.id_form = ? " + SQL_ORDER_BY_PRIORITY;
    private static final String SQL_FILTER_BY_STEP_AND_PRIORITY = " WHERE t.from_step = ? AND t.priority = ?";
    private static final String SQL_QUERY_SELECT_MAX_PRIORITY_BY_STEP = " SELECT MAX( t.priority ) FROM forms_transition t WHERE t.from_step = ?";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Transition transition, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, transition.getFromStep( ) );
            daoUtil.setInt( nIndex++, transition.getNextStep( ) );
            daoUtil.setInt( nIndex++, transition.getPriority( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                transition.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Transition load( int nKey, Plugin plugin )
    {
        Transition transition = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT + SQL_FILTER_BY_ID, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                transition = dataToObject( daoUtil );
            }
        }
        return transition;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int selectMaxPriorityByStep( int nIdStep, Plugin plugin )
    {
        int nMaxPriority = -1;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_MAX_PRIORITY_BY_STEP, plugin ) )
        {
            daoUtil.setInt( 1, nIdStep );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                nMaxPriority = daoUtil.getInt( 1 );
            }
        }
        return nMaxPriority;
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
    public void store( Transition transition, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 1;

            daoUtil.setInt( nIndex++, transition.getId( ) );
            daoUtil.setInt( nIndex++, transition.getFromStep( ) );
            daoUtil.setInt( nIndex++, transition.getNextStep( ) );
            daoUtil.setInt( nIndex++, transition.getPriority( ) );
            daoUtil.setInt( nIndex, transition.getId( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Transition> selectTransitionsList( Plugin plugin )
    {
        List<Transition> transitionList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT + SQL_ORDER_BY_PRIORITY, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                transitionList.add( dataToObject( daoUtil ) );
            }
        }
        return transitionList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdTransitionsList( Plugin plugin )
    {
        List<Integer> transitionList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin ) )
        {
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                transitionList.add( daoUtil.getInt( 1 ) );
            }
        }
        return transitionList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectTransitionsReferenceList( Plugin plugin )
    {
        ReferenceList transitionList = new ReferenceList( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT + SQL_ORDER_BY_PRIORITY, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                transitionList.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
            }
        }
        return transitionList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Transition> selectTransitionsListFromStep( int nIdStep, Plugin plugin )
    {
        List<Transition> transitionList = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT + SQL_FILTER_BY_STEP, plugin ) )
        {
            daoUtil.setInt( 1, nIdStep );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                transitionList.add( dataToObject( daoUtil ) );
            }
        }
        return transitionList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Transition getTransitionByPriority( int nIdStep, int nPriority, Plugin plugin )
    {
        Transition transition = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT + SQL_FILTER_BY_STEP_AND_PRIORITY, plugin ) )
        {
            daoUtil.setInt( 1, nIdStep );
            daoUtil.setInt( 2, nPriority );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                transition = dataToObject( daoUtil );
            }
        }
        return transition;

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteByStep( int nIdStep, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_STEP, plugin ) )
        {
            daoUtil.setInt( 1, nIdStep );
            daoUtil.setInt( 2, nIdStep );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * 
     * @param daoUtil
     *            The daoutil
     * @return The populated Transition object
     */
    private Transition dataToObject( DAOUtil daoUtil )
    {
        Transition transition = new Transition( );

        transition.setId( daoUtil.getInt( "id_transition" ) );
        transition.setFromStep( daoUtil.getInt( "from_step" ) );
        transition.setFromStepTitle( daoUtil.getString( "fromTitle" ) );
        transition.setNextStep( daoUtil.getInt( "next_step" ) );
        transition.setNextStepTitle( daoUtil.getString( "nextTitle" ) );
        transition.setPriority( daoUtil.getInt( "priority" ) );

        return transition;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Transition> selectTransitionsListFromForm( int nIdForm, Plugin plugin )
    {
        List<Transition> transitionList = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT + SQL_FILTER_BY_FORM, plugin ) )
        {
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                transitionList.add( dataToObject( daoUtil ) );
            }
        }
        return transitionList;
    }

}
