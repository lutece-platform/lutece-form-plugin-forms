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
public final class FormCategoryDAO implements IFormCategoryDAO
{
    // Constants
    private static final String SQL_QUERY_SELECTALL = "SELECT id_category, code, name FROM forms_category";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECTALL + " WHERE id_category = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO forms_category ( code, name ) VALUES ( ?, ?) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM forms_category WHERE id_category = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE forms_category SET code = ?, name = ? WHERE id_category = ?";
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( FormCategory formCategory, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++, formCategory.getCode( ) );
            daoUtil.setString( nIndex++, formCategory.getName( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
            	formCategory.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FormCategory load( int nKey, Plugin plugin )
    {
        FormCategory formCategory = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                formCategory = dataToObject( daoUtil );
            }
        }
        return formCategory;
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
    public void store( FormCategory formCategory, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 1;

            daoUtil.setString( nIndex++, formCategory.getCode( ) );
            daoUtil.setString( nIndex++, formCategory.getName( ) );

            daoUtil.setInt( nIndex, formCategory.getId( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * 
     * @param daoUtil
     *            The daoutil
     * @return The populated Form object
     *
     */
    private FormCategory dataToObject( DAOUtil daoUtil )
    {
        FormCategory formCategory = new FormCategory( );

        formCategory.setId( daoUtil.getInt( "id_category" ) );
        formCategory.setCode( daoUtil.getString( "code" ) );
        formCategory.setName( daoUtil.getString( "name" ) );

        return formCategory;
    }

	@Override
	public List<FormCategory> selectFormCategoryList(Plugin plugin) {
		List<FormCategory> formCategoryList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
            	formCategoryList.add( dataToObject( daoUtil ) );
            }
        }
        return formCategoryList;
	}

}
