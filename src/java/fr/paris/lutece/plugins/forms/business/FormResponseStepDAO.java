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
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Form objects
 */
public final class FormResponseStepDAO implements IFormResponseStepDAO
{
    // Constants
    private static final String SQL_QUERY_SELECTALL = "SELECT id, id_form_response, id_step, order_response FROM forms_response_step";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECTALL + " WHERE id_step = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO forms_response_step ( id_form_response, id_step, order_response ) VALUES ( ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM forms_response_step WHERE id = ? ";
    private static final String SQL_QUERY_DELETE_BY_FORM_RESPONSE = "DELETE FROM forms_response_step WHERE id_form_response = ? ";
    private static final String SQL_QUERY_DELETE_BY_STEP = "DELETE FROM forms_response_step WHERE id_step = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE forms_response_step SET id_form_response = ?, id_step = ?, order_response = ? WHERE id = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_RESPONSE = SQL_QUERY_SELECTALL + " WHERE id_form_response = ? ORDER BY order_response ASC";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( FormResponseStep formResponseStep, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, formResponseStep.getFormResponseId( ) );
            daoUtil.setInt( nIndex++, formResponseStep.getStep( ).getId( ) );
            daoUtil.setInt( nIndex++, formResponseStep.getOrder( ) );

            daoUtil.executeUpdate( );

            if ( daoUtil.nextGeneratedKey( ) )
            {
                formResponseStep.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FormResponseStep load( int nKey, Plugin plugin )
    {
        FormResponseStep formResponseStep = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                formResponseStep = dataToObject( daoUtil );
            }
        }
        return formResponseStep;
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
    public void store( FormResponseStep formResponseStep, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, formResponseStep.getFormResponseId( ) );
            daoUtil.setInt( nIndex++, formResponseStep.getStep( ).getId( ) );
            daoUtil.setInt( nIndex++, formResponseStep.getOrder( ) );

            daoUtil.setInt( nIndex++, formResponseStep.getId( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteByFormResponse( int nIdFormResponse, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_FORM_RESPONSE, plugin ) )
        {
            daoUtil.setInt( 1, nIdFormResponse );
            daoUtil.executeUpdate( );
        }
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
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FormResponseStep> selectFormResponseStepsByFormResponse( int nIdFormResponse, Plugin plugin )
    {
        List<FormResponseStep> listIdStep = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_RESPONSE, plugin ) )
        {
            daoUtil.setInt( 1, nIdFormResponse );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                listIdStep.add( dataToObject( daoUtil ) );
            }
        }
        return listIdStep;
    }

    /**
     * 
     * @param daoUtil
     *            The daoutil
     * @return The populated FormResponseStep object
     */
    private FormResponseStep dataToObject( DAOUtil daoUtil )
    {
        FormResponseStep formResponseStep = new FormResponseStep( );
        formResponseStep.setId( daoUtil.getInt( "id" ) );
        formResponseStep.setFormResponseId( daoUtil.getInt( "id_form_response" ) );

        Step step = new Step( );
        step.setId( daoUtil.getInt( "id_step" ) );
        formResponseStep.setStep( step );

        formResponseStep.setOrder( daoUtil.getInt( "order_response" ) );

        return formResponseStep;
    }

}
