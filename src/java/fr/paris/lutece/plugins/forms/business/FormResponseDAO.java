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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Form objects
 */
public final class FormResponseDAO implements IFormResponseDAO
{
    // Constants
    private static final String SQL_QUERY_SELECTALL = "SELECT id_response, id_form, guid, creation_date, update_date, from_save, status,role, update_date_status FROM forms_response";
    private static final String SQL_QUERY_SELECT_ID = "SELECT id_response FROM forms_response";
    private static final String SQL_QUERY_SELECTALL_BY_ID_FORM = SQL_QUERY_SELECTALL + " WHERE id_form = ? ";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECTALL + " WHERE id_response = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO forms_response ( id_form, guid, creation_date, update_date, from_save, status, role, update_date_status ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM forms_response WHERE id_response = ? ";
    private static final String SQL_QUERY_DELETE_BY_FORM = "DELETE FROM forms_response WHERE id_form = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE forms_response SET id_form = ?, guid = ?, update_date = ?, from_save = ?, status = ?, role = ?, update_date_status = ? WHERE id_response = ?";
    private static final String SQL_QUERY_SELECT_FOR_BACKUP = SQL_QUERY_SELECTALL + " WHERE guid = ? AND id_form = ? AND from_save = ? ";
    private static final String SQL_QUERY_SELECT_ALL_BY_USER = SQL_QUERY_SELECTALL + " WHERE guid = ? AND from_save = 0 ";
    private static final String SQL_QUERY_SELECT_ALL_BY_ROLE = SQL_QUERY_SELECTALL + " WHERE from_save = 0 AND role IN ( ? ";
    private static final String SQL_QUERY_SELECT_BY_LIST_FORM_RESPONSE = SQL_QUERY_SELECTALL + " WHERE id_response IN (?";
    private static final String SQL_CLOSE_PARENTHESIS = " ) ";
    private static final String SQL_ADITIONAL_PARAMETER = ",?";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( FormResponse formResponse, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, formResponse.getFormId( ) );
            daoUtil.setString( nIndex++, formResponse.getGuid( ) );

            Timestamp timestampCurrentTime = new Timestamp( System.currentTimeMillis( ) );
            daoUtil.setTimestamp( nIndex++, timestampCurrentTime );
            daoUtil.setTimestamp( nIndex++, timestampCurrentTime );
            daoUtil.setBoolean( nIndex++, formResponse.isFromSave( ) );
            daoUtil.setBoolean( nIndex++, formResponse.isPublished( ) );
            daoUtil.setString( nIndex++, formResponse.getRole( ) );
            daoUtil.setTimestamp( nIndex++, timestampCurrentTime );
            daoUtil.executeUpdate( );

            if ( daoUtil.nextGeneratedKey( ) )
            {
                formResponse.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FormResponse load( int nKey, Plugin plugin )
    {
        FormResponse formResponse = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                formResponse = dataToObject( daoUtil );
            }
        }

        return formResponse;
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
    public void store( FormResponse formResponse, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, formResponse.getFormId( ) );
            daoUtil.setString( nIndex++, formResponse.getGuid( ) );

            Timestamp timestampCurrentTime = new Timestamp( System.currentTimeMillis( ) );
            daoUtil.setTimestamp( nIndex++, timestampCurrentTime );
            daoUtil.setBoolean( nIndex++, formResponse.isFromSave( ) );
            daoUtil.setBoolean( nIndex++, formResponse.isPublished( ) );
            daoUtil.setString( nIndex++, formResponse.getRole( ) );
            daoUtil.setTimestamp( nIndex++, formResponse.getUpdateStatus( ) );
            daoUtil.setInt( nIndex++, formResponse.getId( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FormResponse> selectFormResponseList( Plugin plugin )
    {
        List<FormResponse> formResponseList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                formResponseList.add( dataToObject( daoUtil ) );
            }
        }
        return formResponseList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectAllFormResponsesId( Plugin plugin )
    {
        List<Integer> formResponseIdList = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ID, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                formResponseIdList.add( daoUtil.getInt( 1 ) );
            }
        }
        return formResponseIdList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FormResponse> selectFormResponseListUncompleteByIdForm( int nIdForm, Plugin plugin )
    {
        List<FormResponse> formResponseList = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_BY_ID_FORM, plugin ) )
        {
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                formResponseList.add( dataToObject( daoUtil ) );
            }
        }

        return formResponseList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FormResponse> selectFormResponseByUser( String strGuid, int nIdForm, boolean fromBackup, Plugin plugin )
    {
        List<FormResponse> list = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_FOR_BACKUP, plugin ) )
        {
            daoUtil.setString( 1, strGuid );
            daoUtil.setInt( 2, nIdForm );
            daoUtil.setBoolean( 3, fromBackup );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                list.add( dataToObject( daoUtil ) );
            }
        }
        return list;
    }

    @Override
    public List<FormResponse> selectAllCompletedFormResponseByUser( String strGuid, Plugin plugin )
    {
        List<FormResponse> list = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ALL_BY_USER, plugin ) )
        {
            daoUtil.setString( 1, strGuid );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                list.add( dataToObject( daoUtil ) );
            }
        }
        return list;
    }
    @Override
    public List<FormResponse> selectFormResponseByRole( List<String> listRole, Plugin plugin )
    {
    	 List<FormResponse> list = new ArrayList<>( );

    	 int nlistRole = listRole.size( );

         if ( nlistRole > 0 )
         {
             StringBuilder sbSQL = new StringBuilder( SQL_QUERY_SELECT_ALL_BY_ROLE );

             for ( int i = 1; i < nlistRole; i++ )
             {
                 sbSQL.append( SQL_ADITIONAL_PARAMETER );
             }

             sbSQL.append( SQL_CLOSE_PARENTHESIS );

             try ( DAOUtil daoUtil = new DAOUtil( sbSQL.toString( ), plugin ) )
             {
            	 for ( int i = 0; i < nlistRole; i++ )
                 {
                     daoUtil.setString( i+1 , listRole.get( i ) );
                 }
	             daoUtil.executeQuery( );
	
	             while ( daoUtil.next( ) )
	             {
	                 list.add( dataToObject( daoUtil ) );
	             }
             }
         }
         return list;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<FormResponse> selectFormResponseByPrimaryKeyList( List<Integer> listIdFormResponse, Plugin plugin )
    {
        List<FormResponse> list = new ArrayList<>( );
        int nlistIdFormResponseSize = listIdFormResponse.size( );

        if ( nlistIdFormResponseSize > 0 )
        {
            StringBuilder sbSQL = new StringBuilder( SQL_QUERY_SELECT_BY_LIST_FORM_RESPONSE );

            for ( int i = 1; i < nlistIdFormResponseSize; i++ )
            {
                sbSQL.append( SQL_ADITIONAL_PARAMETER );
            }

            sbSQL.append( SQL_CLOSE_PARENTHESIS );

            try ( DAOUtil daoUtil = new DAOUtil( sbSQL.toString( ), plugin ) )
            {

                for ( int i = 0; i < nlistIdFormResponseSize; i++ )
                {
                    daoUtil.setInt( i + 1, listIdFormResponse.get( i ) );
                }

                daoUtil.executeQuery( );

                while ( daoUtil.next( ) )
                {
                    list.add( dataToObject( daoUtil ) );
                }
            }
        }
        return list;
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
     * 
     * @param daoUtil
     *            The daoutil
     * @return The populated FormResponse object
     */
    private FormResponse dataToObject( DAOUtil daoUtil )
    {
        FormResponse formResponse = new FormResponse( );
        formResponse.setId( daoUtil.getInt( "id_response" ) );
        formResponse.setFormId( daoUtil.getInt( "id_form" ) );
        formResponse.setGuid( daoUtil.getString( "guid" ) );
        formResponse.setFromSave( daoUtil.getBoolean( "from_save" ) );
        formResponse.setPublished( daoUtil.getBoolean( "status" ) );
        formResponse.setRole(daoUtil.getString( "role" ));

        Timestamp timestampCreationDate = daoUtil.getTimestamp( "creation_date" );
        formResponse.setDateCreation( timestampCreationDate );
        formResponse.setUpdateStatus( daoUtil.getTimestamp( "update_date_status" ) );
        try
        {
            formResponse.setUpdate( daoUtil.getTimestamp( "update_date" ) );
        }
        catch( AppException exception )
        {
            AppLogService.error( "The update date of the FormResponse si not valid !" );

            formResponse.setUpdate( timestampCreationDate );
        }

        return formResponse;
    }
}
