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
 * This class provides Data Access methods for Step objects
 */
public final class StepDAO implements IStepDAO
{
    // Constants
    private static final String SQL_QUERY_SELECTALL = "SELECT id_step, title, description, id_form, is_initial, is_final FROM forms_step";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECTALL + " WHERE id_step = ?";
    private static final String SQL_QUERY_SELECT_INITIAL_STEP = SQL_QUERY_SELECTALL + " WHERE id_form = ? AND is_initial = 1";
    private static final String SQL_QUERY_INSERT = "INSERT INTO forms_step ( title, description, id_form, is_initial, is_final ) VALUES ( ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM forms_step WHERE id_step = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE forms_step SET id_step = ?, title = ?, description = ?, id_form = ? ,is_initial = ?, is_final = ? WHERE id_step = ?";
    private static final String SQL_QUERY_SELECTALL_BY_FORM = SQL_QUERY_SELECTALL + " where id_form = ?";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_step FROM forms_step";
    private static final String SQL_QUERY_SELECTALL_ID_BY_FORM = "SELECT id_step FROM forms_step WHERE id_form = ?";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Step step, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++, step.getTitle( ) );
            daoUtil.setString( nIndex++, step.getDescription( ) );
            daoUtil.setInt( nIndex++, step.getIdForm( ) );
            daoUtil.setBoolean( nIndex++, step.isInitial( ) );
            daoUtil.setBoolean( nIndex++, step.isFinal( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                step.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Step load( int nKey, Plugin plugin )
    {
        Step step = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                step = dataToObject( daoUtil );
            }
        }
        return step;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Step selectInitialStep( int nIdForm, Plugin plugin )
    {
        Step step = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_INITIAL_STEP, plugin ) )
        {
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                step = dataToObject( daoUtil );
            }
        }
        return step;
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
    public void store( Step step, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 1;

            daoUtil.setInt( nIndex++, step.getId( ) );
            daoUtil.setString( nIndex++, step.getTitle( ) );
            daoUtil.setString( nIndex++, step.getDescription( ) );
            daoUtil.setInt( nIndex++, step.getIdForm( ) );
            daoUtil.setBoolean( nIndex++, step.isInitial( ) );
            daoUtil.setBoolean( nIndex++, step.isFinal( ) );

            daoUtil.setInt( nIndex, step.getId( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Step> selectStepsList( Plugin plugin )
    {
        List<Step> stepList = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                stepList.add( dataToObject( daoUtil ) );
            }
        }
        return stepList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdStepsList( Plugin plugin )
    {
        List<Integer> stepList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin ) )
        {
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                stepList.add( daoUtil.getInt( 1 ) );
            }
        }
        return stepList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdStepsListByForm( int nIdForm, Plugin plugin )
    {
        List<Integer> stepList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID_BY_FORM, plugin ) )
        {
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                stepList.add( daoUtil.getInt( 1 ) );
            }
        }
        return stepList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectStepsReferenceList( Plugin plugin )
    {
        ReferenceList stepList = new ReferenceList( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                stepList.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
            }

        }
        return stepList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Step> selectStepsListbyForm( int nFormId, Plugin plugin )
    {
        List<Step> stepList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_BY_FORM, plugin ) )
        {
            daoUtil.setInt( 1, nFormId );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                stepList.add( dataToObject( daoUtil ) );
            }
        }
        return stepList;
    }

    @Override
    public ReferenceList selectStepReferenceListbyForm( int nFormId, Plugin plugin )
    {
        ReferenceList stepReferenceList = new ReferenceList( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_BY_FORM, plugin ) )
        {
            daoUtil.setInt( 1, nFormId );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                stepReferenceList.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
            }
        }
        return stepReferenceList;
    }

    /**
     * 
     * @param daoUtil
     *            The daoutil
     * @return The populated Step object
     */
    private Step dataToObject( DAOUtil daoUtil )
    {
        Step step = new Step( );

        step.setId( daoUtil.getInt( "id_step" ) );
        step.setTitle( daoUtil.getString( "title" ) );
        step.setDescription( daoUtil.getString( "description" ) );
        step.setIdForm( daoUtil.getInt( "id_form" ) );
        step.setInitial( daoUtil.getBoolean( "is_initial" ) );
        step.setFinal( daoUtil.getBoolean( "is_final" ) );

        return step;
    }

}
