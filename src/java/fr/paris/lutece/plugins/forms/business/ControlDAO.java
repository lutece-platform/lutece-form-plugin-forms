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
 * This class provides Data Access methods for Control objects
 */
public final class ControlDAO implements IControlDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_control, value, error_message, id_question, validator_name, control_type FROM forms_control WHERE id_control = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO forms_control ( value, error_message, id_question, validator_name, control_type ) VALUES ( ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM forms_control WHERE id_control = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE forms_control SET id_control = ?, value = ?, error_message = ?, id_question = ?, validator_name = ?, control_type = ? WHERE id_control = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_control, value, error_message, id_question, validator_name, control_type FROM forms_control";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_control FROM forms_control";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Control control, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin );
        try
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++, control.getValue( ) );
            daoUtil.setString( nIndex++, control.getErrorMessage( ) );
            daoUtil.setInt( nIndex++, control.getIdQuestion( ) );
            daoUtil.setString( nIndex++, control.getValidatorName( ) );
            daoUtil.setString( nIndex++, control.getControlType( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                control.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
        finally
        {
            daoUtil.close( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Control load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery( );
        Control control = null;

        if ( daoUtil.next( ) )
        {
            control = new Control( );
            int nIndex = 1;

            control.setId( daoUtil.getInt( nIndex++ ) );
            control.setValue( daoUtil.getString( nIndex++ ) );
            control.setErrorMessage( daoUtil.getString( nIndex++ ) );
            control.setIdQuestion( daoUtil.getInt( nIndex++ ) );
            control.setValidatorName( daoUtil.getString( nIndex++ ) );
            control.setControlType( daoUtil.getString( nIndex++ ) );
        }

        daoUtil.close( );
        return control;
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
        daoUtil.close( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( Control control, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        int nIndex = 1;

        daoUtil.setInt( nIndex++, control.getId( ) );
        daoUtil.setString( nIndex++, control.getValue( ) );
        daoUtil.setString( nIndex++, control.getErrorMessage( ) );
        daoUtil.setInt( nIndex++, control.getIdQuestion( ) );
        daoUtil.setString( nIndex++, control.getValidatorName( ) );
        daoUtil.setString( nIndex++, control.getControlType( ) );

        daoUtil.setInt( nIndex, control.getId( ) );

        daoUtil.executeUpdate( );
        daoUtil.close( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Control> selectControlsList( Plugin plugin )
    {
        List<Control> controlList = new ArrayList<Control>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            Control control = new Control( );
            int nIndex = 1;

            control.setId( daoUtil.getInt( nIndex++ ) );
            control.setValue( daoUtil.getString( nIndex++ ) );
            control.setErrorMessage( daoUtil.getString( nIndex++ ) );
            control.setIdQuestion( daoUtil.getInt( nIndex++ ) );
            control.setValidatorName( daoUtil.getString( nIndex++ ) );
            control.setControlType( daoUtil.getString( nIndex++ ) );

            controlList.add( control );
        }

        daoUtil.close( );
        return controlList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdControlsList( Plugin plugin )
    {
        List<Integer> controlList = new ArrayList<Integer>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            controlList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.close( );
        return controlList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectControlsReferenceList( Plugin plugin )
    {
        ReferenceList controlList = new ReferenceList( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            controlList.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
        }

        daoUtil.close( );
        return controlList;
    }

}
