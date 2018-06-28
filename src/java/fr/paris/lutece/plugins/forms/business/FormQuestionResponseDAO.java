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

import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides Data Access methods for Form objects
 */
public final class FormQuestionResponseDAO implements IFormQuestionResponseDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_question_response, id_form_response, id_question, iteration_number FROM forms_question_response WHERE id = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO forms_question_response ( id_form_response, id_question, iteration_number ) VALUES ( ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM forms_question_response WHERE id = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE forms_question_response SET id_question = ?, iteration_number = ? WHERE id = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_question_response, id_question, iteration_number FROM forms_question_response";

    private static final String SQL_QUERY_INSERT_ENTRY_RESPONSE = "INSERT INTO forms_question_entry_response ( id_question_response, id_entry_response ) VALUES ( ?, ? ) ";
    private static final String SQL_QUERY_DELETE_BY_QUESTION = "DELETE FROM forms_question_response WHERE id_question = ? ";
    private static final String SQL_QUERY_DELETE_QUESTION_ENTRY_RESPONSE_BY_QUESTION = "DELETE FROM forms_question_entry_response "
            + "WHERE id_question_response IN ( SELECT id_question_response FROM forms_question_response WHERE id_question = ? )";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( FormQuestionResponse formQuestionResponse, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin );

        try
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, formQuestionResponse.getIdFormResponse( ) );
            daoUtil.setInt( nIndex++, formQuestionResponse.getIdQuestion( ) );
            daoUtil.setInt( nIndex++, formQuestionResponse.getIterationNumber( ) );

            daoUtil.executeUpdate( );

            if ( daoUtil.nextGeneratedKey( ) )
            {
                formQuestionResponse.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
        finally
        {
            daoUtil.close( );
        }

        daoUtil = new DAOUtil( SQL_QUERY_INSERT_ENTRY_RESPONSE, Statement.RETURN_GENERATED_KEYS, plugin );

        try
        {
            for ( Response response : formQuestionResponse.getEntryResponse( ) )
            {
                ResponseHome.create( response );

                int nIndex = 1;
                daoUtil.setInt( nIndex++, formQuestionResponse.getId( ) );
                daoUtil.setInt( nIndex++, response.getIdResponse( ) );

                daoUtil.executeUpdate( );
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
    public FormQuestionResponse load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );

        FormQuestionResponse formQuestionResponse = new FormQuestionResponse( );

        daoUtil.close( );

        return formQuestionResponse;
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
    public void store( FormQuestionResponse formQuestionResponse, Plugin plugin )
    {

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FormQuestionResponse> selectFormQuestionResponseList( Plugin plugin )
    {
        List<FormQuestionResponse> formQuestionResponseList = new ArrayList<FormQuestionResponse>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );

        daoUtil.close( );

        return formQuestionResponseList;
    }

    @Override
    public void deleteByQuestion( int nIdQuestion, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_QUESTION_ENTRY_RESPONSE_BY_QUESTION, plugin );
        try
        {
            daoUtil.setInt( 1, nIdQuestion );
            daoUtil.executeUpdate( );
        }
        finally
        {
            daoUtil.close( );
        }

        daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_QUESTION, plugin );
        try
        {
            daoUtil.setInt( 1, nIdQuestion );
            daoUtil.executeUpdate( );
        }
        finally
        {
            daoUtil.close( );
        }
    }
}
