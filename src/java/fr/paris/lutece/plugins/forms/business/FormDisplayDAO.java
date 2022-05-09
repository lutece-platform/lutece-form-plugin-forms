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
 * This class provides Data Access methods for Form objects
 */
public final class FormDisplayDAO implements IFormDisplayDAO
{
    // Constants
    private static final String SQL_QUERY_SELECTALL = "SELECT id_display, id_form, id_step, id_composite, id_parent, display_order, composite_type, display_depth FROM forms_display";

    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECTALL + " WHERE id_display = ?";
    private static final String SQL_QUERY_SELECT_BY_FORM = SQL_QUERY_SELECTALL + " WHERE id_form = ?";
    private static final String SQL_QUERY_SELECT_BY_PARENT = SQL_QUERY_SELECTALL + " WHERE id_step = ? AND id_parent = ? ORDER BY display_order ASC";
    private static final String SQL_QUERY_INSERT = "INSERT INTO forms_display ( id_form, id_step, id_composite, id_parent, display_order, composite_type, display_depth ) VALUES ( ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM forms_display WHERE id_display = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE forms_display SET id_display = ?, id_form = ?, id_step = ?, id_composite = ?, id_parent = ?, display_order = ?, composite_type = ?, display_depth = ? WHERE id_display = ?";
    private static final String SQL_QUERY_NEXT_POSITION_BY_PARENT = "SELECT MAX(display_order) from forms_display WHERE id_step = ? AND id_parent = ?";
    private static final String SQL_QUERY_SELECTALL_GROUP_DISPLAY_BY_STEP = "SELECT d.id_display, g.title, d.id_form, d.id_step, d.id_composite, d.id_parent, d.display_order, d.composite_type, d.display_depth "
            + "FROM forms_display d INNER JOIN forms_group g ON d.id_composite = g.id_group "
            + "WHERE d.id_step = ? AND d.composite_type = ? order by d.id_parent, d.display_order";
    private static final String SQL_QUERY_SELECT_BY_FROM_STEP_COMPOSITE = SQL_QUERY_SELECTALL + " WHERE id_form = ? AND id_step = ? AND id_composite = ?";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( FormDisplay formDisplay, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, formDisplay.getFormId( ) );
            daoUtil.setInt( nIndex++, formDisplay.getStepId( ) );
            daoUtil.setInt( nIndex++, formDisplay.getCompositeId( ) );
            daoUtil.setInt( nIndex++, formDisplay.getParentId( ) );
            int nDisplayOrder = getNextPositionInGroup( formDisplay.getStepId( ), formDisplay.getParentId( ), plugin );
            daoUtil.setInt( nIndex++, nDisplayOrder );
            daoUtil.setString( nIndex++, formDisplay.getCompositeType( ) );
            daoUtil.setInt( nIndex++, formDisplay.getDepth( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                formDisplay.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FormDisplay load( int nKey, Plugin plugin )
    {
        FormDisplay formDisplay = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                formDisplay = dataToObject( daoUtil );
            }
        }
        return formDisplay;
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
    public void store( FormDisplay formDisplay, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 1;

            daoUtil.setInt( nIndex++, formDisplay.getId( ) );
            daoUtil.setInt( nIndex++, formDisplay.getFormId( ) );
            daoUtil.setInt( nIndex++, formDisplay.getStepId( ) );
            daoUtil.setInt( nIndex++, formDisplay.getCompositeId( ) );
            daoUtil.setInt( nIndex++, formDisplay.getParentId( ) );
            daoUtil.setInt( nIndex++, formDisplay.getDisplayOrder( ) );
            daoUtil.setString( nIndex++, formDisplay.getCompositeType( ) );
            daoUtil.setInt( nIndex++, formDisplay.getDepth( ) );
            daoUtil.setInt( nIndex, formDisplay.getId( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FormDisplay> selectFormDisplayList( Plugin plugin )
    {
        List<FormDisplay> formDisplayList = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                formDisplayList.add( dataToObject( daoUtil ) );
            }
        }

        return formDisplayList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<FormDisplay> selectFormDisplayListByParent( int nIdStep, int nIdParent, Plugin plugin )
    {
        List<FormDisplay> formDisplayList = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_PARENT, plugin ) )
        {
            daoUtil.setInt( 1, nIdStep );
            daoUtil.setInt( 2, nIdParent );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                formDisplayList.add( dataToObject( daoUtil ) );
            }
        }

        return formDisplayList;
    }

    /**
     * Return the next available position with a given parent group
     * 
     * @param nIdStep
     *            the Step id
     * @param nIdParent
     *            the parent display identifier
     * @param plugin
     *            the Plugin to use
     * @return the next available position value in the group
     */
    private int getNextPositionInGroup( int nIdStep, int nIdParent, Plugin plugin )
    {
        int nNextPosition = 0;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEXT_POSITION_BY_PARENT, plugin ) )
        {
            daoUtil.setInt( 1, nIdStep );
            daoUtil.setInt( 2, nIdParent );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                nNextPosition = daoUtil.getInt( 1 ) + 1;
            }
        }
        return nNextPosition;
    }

    @Override
    public ReferenceList selectGroupDisplayReferenceListByStep( int nIdStep, Plugin plugin )
    {
        ReferenceList groupList = new ReferenceList( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_GROUP_DISPLAY_BY_STEP, plugin ) )
        {
            daoUtil.setInt( 1, nIdStep );
            daoUtil.setString( 2, CompositeDisplayType.GROUP.getLabel( ) );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                groupList.addItem( daoUtil.getInt( "id_display" ), daoUtil.getString( "title" ) );
            }
        }
        return groupList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FormDisplay selectFormdisplayByFormStepAndComposite( int nIdForm, int nIdStep, int nIdComposite, Plugin plugin )
    {
        FormDisplay formDisplay = null;

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_FROM_STEP_COMPOSITE, plugin ) )
        {
            daoUtil.setInt( 1, nIdForm );
            daoUtil.setInt( 2, nIdStep );
            daoUtil.setInt( 3, nIdComposite );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                formDisplay = dataToObject( daoUtil );
            }
        }
        return formDisplay;
    }

    @Override
    public List<FormDisplay> selectFormDisplayListByForm( int nIdForm, Plugin plugin )
    {
        List<FormDisplay> list = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_FORM, plugin ) )
        {
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                list.add( dataToObject( daoUtil ) );
            }
        }
        return list;
    }

    /**
     * 
     * @param daoUtil
     *            The daoutil
     * @return The populated FormDisplay object
     */
    private FormDisplay dataToObject( DAOUtil daoUtil )
    {
        FormDisplay formDisplay = new FormDisplay( );

        formDisplay.setId( daoUtil.getInt( "id_display" ) );
        formDisplay.setFormId( daoUtil.getInt( "id_form" ) );
        formDisplay.setStepId( daoUtil.getInt( "id_step" ) );
        formDisplay.setCompositeId( daoUtil.getInt( "id_composite" ) );
        formDisplay.setParentId( daoUtil.getInt( "id_parent" ) );
        formDisplay.setDisplayOrder( daoUtil.getInt( "display_order" ) );
        formDisplay.setCompositeType( daoUtil.getString( "composite_type" ) );
        formDisplay.setDepth( daoUtil.getInt( "display_depth" ) );

        return formDisplay;
    }
}
