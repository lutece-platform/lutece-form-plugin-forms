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

import java.sql.Statement;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for FormMessage objects
 */
public final class FormMessageDAO implements IFormMessageDAO
{
    // Constants
    private static final String SQL_QUERY_SELECTALL = "SELECT id, id_form, end_message_display, end_message FROM forms_message";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECTALL + " WHERE id = ?";
    private static final String SQL_QUERY_SELECT_BY_FORM = SQL_QUERY_SELECTALL + " WHERE id_form = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO forms_message ( id_form, end_message_display, end_message ) VALUES ( ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM forms_message WHERE id = ? ";
    private static final String SQL_QUERY_DELETE_BY_FORM = "DELETE FROM forms_message WHERE id_form = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE forms_message SET id_form = ?, end_message_display = ?, end_message = ? WHERE id = ?";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( FormMessage formMessage, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, formMessage.getIdForm( ) );
            daoUtil.setBoolean( nIndex++, formMessage.getEndMessageDisplay( ) );
            daoUtil.setString( nIndex++, formMessage.getEndMessage( ) );

            daoUtil.executeUpdate( );

            if ( daoUtil.nextGeneratedKey( ) )
            {
                formMessage.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FormMessage load( int nKey, Plugin plugin )
    {
        FormMessage formMessage = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                formMessage = dataToObject( daoUtil );
            }
        }
        return formMessage;
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
    public void deleteByForm( int nIdForm, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_FORM, plugin ) )
        {
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( FormMessage formMessage, Plugin plugin )
    {

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, formMessage.getIdForm( ) );
            daoUtil.setBoolean( nIndex++, formMessage.getEndMessageDisplay( ) );
            daoUtil.setString( nIndex++, formMessage.getEndMessage( ) );

            daoUtil.setInt( nIndex++, formMessage.getId( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FormMessage selectByForm( int nIdForm, Plugin plugin )
    {
        FormMessage formMessage = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_FORM, plugin ) )
        {
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                formMessage = dataToObject( daoUtil );
            }

        }

        return formMessage;
    }

    /**
     * 
     * @param daoUtil
     *            The daoutil
     * @return The populated FormMessage object
     */
    private FormMessage dataToObject( DAOUtil daoUtil )
    {
        FormMessage formMessage = new FormMessage( );
        formMessage.setId( daoUtil.getInt( "id" ) );
        formMessage.setIdForm( daoUtil.getInt( "id_form" ) );
        formMessage.setEndMessageDisplay( daoUtil.getBoolean( "end_message_display" ) );
        formMessage.setEndMessage( daoUtil.getString( "end_message" ) );

        return formMessage;
    }

}
