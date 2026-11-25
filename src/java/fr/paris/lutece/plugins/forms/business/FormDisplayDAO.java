/*
 * Copyright (c) 2002-2025, City of Paris
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * This class provides Data Access methods for Form objects
 */
@ApplicationScoped
public final class FormDisplayDAO implements IFormDisplayDAO
{
    // Constants
    private static final String SQL_QUERY_SELECTALL = "SELECT id_display, id_form, id_step, id_composite, id_parent, display_order, composite_type, display_depth, display_column FROM forms_display";

    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECTALL + " WHERE id_display = ?";
    private static final String SQL_QUERY_SELECT_BY_FORM = SQL_QUERY_SELECTALL + " WHERE id_form = ?";
    private static final String SQL_QUERY_SELECT_BY_PARENT = SQL_QUERY_SELECTALL + " WHERE id_step = ? AND id_parent = ? ORDER BY display_order ASC";
    private static final String SQL_QUERY_INSERT = "INSERT INTO forms_display ( id_form, id_step, id_composite, id_parent, display_order, composite_type, display_depth, display_column ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM forms_display WHERE id_display = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE forms_display SET id_display = ?, id_form = ?, id_step = ?, id_composite = ?, id_parent = ?, display_order = ?, composite_type = ?, display_depth = ?, display_column = ? WHERE id_display = ?";
    private static final String SQL_QUERY_NEXT_POSITION_BY_PARENT = "SELECT MAX(display_order) from forms_display WHERE id_step = ? AND id_parent = ?";
    private static final String SQL_QUERY_SELECTALL_GROUP_DISPLAY_BY_STEP = "SELECT d.id_display, g.title, d.id_form, d.id_step, d.id_composite, d.id_parent, d.display_order, d.composite_type, d.display_depth "
            + "FROM forms_display d INNER JOIN forms_group g ON d.id_composite = g.id_group "
            + "WHERE d.id_step = ? AND d.composite_type = ? order by d.id_parent, d.display_order";
    private static final String SQL_QUERY_SELECT_BY_FROM_STEP_COMPOSITE = SQL_QUERY_SELECTALL + " WHERE id_form = ? AND id_step = ? AND id_composite = ?";
    // The question is joined to the question displays only: the id_composite of a group display is an id_group
    private static final String SQL_QUERY_SELECT_BY_STEP_WITH_QUESTION_EXPORT_DISPLAY_ORDER = "SELECT fd.id_display, fd.id_form, fd.id_step, fd.id_composite, fd.id_parent, fd.display_order, fd.composite_type, fd.display_depth, fd.display_column, fq.export_display_order "
            + "FROM forms_display fd LEFT JOIN forms_question fq ON fd.composite_type = ? AND fd.id_composite = fq.id_question "
            + "WHERE fd.id_step = ?";
    private static final String COLUMN_EXPORT_DISPLAY_ORDER = "export_display_order";
    
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
            daoUtil.setInt( nIndex++, formDisplay.getDisplayColumn( ) );

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
            daoUtil.setInt( nIndex++, formDisplay.getDisplayColumn( ) );
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
    
    @Override
    public List<FormDisplay> selectFormDisplayListByParentOrderByQuestionExportDisplayOrder( int nIdStep, int nIdParent, Plugin plugin )
    {
        Map<Integer, List<FormDisplay>> mapChildren = new HashMap<>( );
        Map<Integer, Integer> mapQuestionExportOrder = new HashMap<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_STEP_WITH_QUESTION_EXPORT_DISPLAY_ORDER, plugin ) )
        {
            daoUtil.setString( 1, CompositeDisplayType.QUESTION.getLabel( ) );
            daoUtil.setInt( 2, nIdStep );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                FormDisplay formDisplay = dataToObject( daoUtil );
                mapChildren.computeIfAbsent( formDisplay.getParentId( ), k -> new ArrayList<>( ) ).add( formDisplay );

                if ( daoUtil.getObject( COLUMN_EXPORT_DISPLAY_ORDER ) != null )
                {
                    mapQuestionExportOrder.put( formDisplay.getId( ), daoUtil.getInt( COLUMN_EXPORT_DISPLAY_ORDER ) );
                }
            }
        }

        Map<Integer, Integer> mapExportOrder = new HashMap<>( );
        List<FormDisplay> formDisplayList = new ArrayList<>( mapChildren.getOrDefault( nIdParent, new ArrayList<>( ) ) );
        formDisplayList.sort( Comparator
                .comparing( ( FormDisplay formDisplay ) -> getExportOrder( formDisplay, mapChildren, mapQuestionExportOrder, mapExportOrder ),
                        Comparator.nullsLast( Comparator.naturalOrder( ) ) )
                .thenComparingInt( FormDisplay::getDisplayOrder ) );

        return formDisplayList;
    }

    /**
     * Returns the export order of a display: the export order of its question, or for a group the lowest export order of the questions it holds,
     * at any depth
     *
     * @param formDisplay
     *            the display
     * @param mapChildren
     *            the displays of the step by parent display id
     * @param mapQuestionExportOrder
     *            the export order of the question displays by display id
     * @param mapExportOrder
     *            the export orders already computed, by display id
     * @return the export order, or null for a group without question
     */
    private static Integer getExportOrder( FormDisplay formDisplay, Map<Integer, List<FormDisplay>> mapChildren, Map<Integer, Integer> mapQuestionExportOrder,
            Map<Integer, Integer> mapExportOrder )
    {
        if ( !CompositeDisplayType.GROUP.getLabel( ).equals( formDisplay.getCompositeType( ) ) )
        {
            return mapQuestionExportOrder.get( formDisplay.getId( ) );
        }
        if ( mapExportOrder.containsKey( formDisplay.getId( ) ) )
        {
            return mapExportOrder.get( formDisplay.getId( ) );
        }

        Integer nExportOrder = null;

        for ( FormDisplay child : mapChildren.getOrDefault( formDisplay.getId( ), new ArrayList<>( ) ) )
        {
            Integer nChildExportOrder = getExportOrder( child, mapChildren, mapQuestionExportOrder, mapExportOrder );

            if ( nChildExportOrder != null && ( nExportOrder == null || nChildExportOrder < nExportOrder ) )
            {
                nExportOrder = nChildExportOrder;
            }
        }
        mapExportOrder.put( formDisplay.getId( ), nExportOrder );

        return nExportOrder;
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
        formDisplay.setDisplayColumn( daoUtil.getInt( "display_column" ) );

        return formDisplay;
    }
}
