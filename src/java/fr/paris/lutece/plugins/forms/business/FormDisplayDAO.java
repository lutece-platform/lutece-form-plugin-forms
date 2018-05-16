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
import fr.paris.lutece.util.sql.DAOUtil;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides Data Access methods for Form objects
 */
public final class FormDisplayDAO implements IFormDisplayDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_display, id_form, id_step, id_composite, id_parent, display_order, composite_type FROM forms_display WHERE id_display = ?";
    private static final String SQL_QUERY_SELECT_BY_PARENT = "SELECT id_display, id_form, id_step, id_composite, id_parent, display_order, composite_type FROM forms_display WHERE id_step = ? AND id_parent = ? ORDER BY display_order ASC";
    private static final String SQL_QUERY_INSERT = "INSERT INTO forms_display ( id_display, id_form, id_step, id_composite, id_parent, display_order, composite_type ) VALUES ( ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM forms_display WHERE id_display = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE forms_display SET id_display = ?, id_form = ?, id_step = ?, id_composite = ?, id_parent = ?, display_order = ?, composite_type = ? WHERE id_display = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_display, id_form, id_step, id_composite, id_parent, display_order, composite_type FROM forms_display";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( FormDisplay formDisplay, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin );
        try
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, formDisplay.getFormId( ) );
            daoUtil.setInt( nIndex++, formDisplay.getStepId( ) );
            daoUtil.setInt( nIndex++, formDisplay.getCompositeId( ) );
            daoUtil.setInt( nIndex++, formDisplay.getParentId( ) );
            daoUtil.setInt( nIndex++, formDisplay.getDisplayOrder( ) );
            daoUtil.setString( nIndex++, formDisplay.getCompositeType( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                formDisplay.setId( daoUtil.getGeneratedKeyInt( 1 ) );
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
    public FormDisplay load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery( );
        FormDisplay formDisplay = null;

        if ( daoUtil.next( ) )
        {
            formDisplay = new FormDisplay( );
            int nIndex = 1;

            formDisplay.setId( daoUtil.getInt( nIndex++ ) );
            formDisplay.setFormId( daoUtil.getInt( nIndex++ ) );
            formDisplay.setStepId( daoUtil.getInt( nIndex++ ) );
            formDisplay.setCompositeId( daoUtil.getInt( nIndex++ ) );
            formDisplay.setParentId( daoUtil.getInt( nIndex++ ) );
            formDisplay.setDisplayOrder( daoUtil.getInt( nIndex++ ) );
            formDisplay.setCompositeType( daoUtil.getString( nIndex++ ) );
        }

        daoUtil.close( );

        return formDisplay;
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
    public void store( FormDisplay formDisplay, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        int nIndex = 1;

        daoUtil.setInt( nIndex++, formDisplay.getId( ) );
        daoUtil.setInt( nIndex++, formDisplay.getFormId( ) );
        daoUtil.setInt( nIndex++, formDisplay.getStepId( ) );
        daoUtil.setInt( nIndex++, formDisplay.getCompositeId( ) );
        daoUtil.setInt( nIndex++, formDisplay.getParentId( ) );
        daoUtil.setInt( nIndex++, formDisplay.getDisplayOrder( ) );
        daoUtil.setString( nIndex++, formDisplay.getCompositeType( ) );
        daoUtil.setInt( nIndex++, formDisplay.getId( ) );

        daoUtil.executeUpdate( );
        daoUtil.close( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FormDisplay> selectFormDisplayList( Plugin plugin )
    {
        List<FormDisplay> formDisplayList = new ArrayList<FormDisplay>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            FormDisplay formDisplay = new FormDisplay( );
            int nIndex = 1;

            formDisplay.setId( daoUtil.getInt( nIndex++ ) );
            formDisplay.setFormId( daoUtil.getInt( nIndex++ ) );
            formDisplay.setStepId( daoUtil.getInt( nIndex++ ) );
            formDisplay.setCompositeId( daoUtil.getInt( nIndex++ ) );
            formDisplay.setParentId( daoUtil.getInt( nIndex++ ) );
            formDisplay.setDisplayOrder( daoUtil.getInt( nIndex++ ) );
            formDisplay.setCompositeType( daoUtil.getString( nIndex++ ) );

            formDisplayList.add( formDisplay );
        }

        daoUtil.close( );

        return formDisplayList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FormDisplay> selectFormDisplayListByParent( int nIdStep, int nIdParent, Plugin plugin )
    {
        List<FormDisplay> formDisplayList = new ArrayList<FormDisplay>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_PARENT, plugin );
        daoUtil.setInt( 1, nIdStep );
        daoUtil.setInt( 2, nIdParent );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            FormDisplay formDisplay = new FormDisplay( );
            int nIndex = 1;

            formDisplay.setId( daoUtil.getInt( nIndex++ ) );
            formDisplay.setFormId( daoUtil.getInt( nIndex++ ) );
            formDisplay.setStepId( daoUtil.getInt( nIndex++ ) );
            formDisplay.setCompositeId( daoUtil.getInt( nIndex++ ) );
            formDisplay.setParentId( daoUtil.getInt( nIndex++ ) );
            formDisplay.setDisplayOrder( daoUtil.getInt( nIndex++ ) );
            formDisplay.setCompositeType( daoUtil.getString( nIndex++ ) );

            formDisplayList.add( formDisplay );
        }

        daoUtil.close( );

        return formDisplayList;
    }
}
