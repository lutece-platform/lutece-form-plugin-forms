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
package fr.paris.lutece.plugins.forms.business.form;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.form.column.querypart.FormColumnQueryBuilder;
import fr.paris.lutece.plugins.forms.business.form.column.querypart.IFormColumnQueryPart;
import fr.paris.lutece.plugins.forms.business.form.filter.FormFilterQueryConstants;
import fr.paris.lutece.plugins.forms.business.form.filter.querypart.IFormFilterQueryPart;
import fr.paris.lutece.plugins.forms.business.form.panel.initializer.querypart.FormPanelInitializerQueryBuilder;
import fr.paris.lutece.plugins.forms.business.form.panel.initializer.querypart.IFormPanelInitializerQueryPart;

/**
 * Class use to build a SQL query from part of query inside FormFilter and FormColumn
 */
public final class QueryBuilder
{
    /**
     * Constructor
     */
    private QueryBuilder( )
    {

    }

    /**
     * Build a SQL query from different parts from a list of FormColumn and a list of FormFilter
     * 
     * @param listFormPanelInitializerQueryPart
     *            The list of all FormPanelInitializerQueryPart to use for built the query
     * @param listFormColumnQueryPart
     *            The of FormColumnQueryPart to retrieve the select and from parts of the query
     * @param listFormFilterQueryPart
     *            The list of FormFilterQueryPart to retrieve the where parts of the query
     * @return the global query build from the FormColmuns and FormFilters
     */
    public static String buildQuery( List<IFormPanelInitializerQueryPart> listFormPanelInitializerQueryPart,
            List<IFormColumnQueryPart> listFormColumnQueryPart, List<IFormFilterQueryPart> listFormFilterQueryPart )
    {
        StringBuilder stringBuilderGlobalQuery = new StringBuilder( );

        if ( !CollectionUtils.isEmpty( listFormPanelInitializerQueryPart ) && !CollectionUtils.isEmpty( listFormColumnQueryPart ) )
        {
            // Build the select query part
            buildSelectQueryPart( stringBuilderGlobalQuery, listFormPanelInitializerQueryPart, listFormColumnQueryPart );
            stringBuilderGlobalQuery.append( FormFilterQueryConstants.SPACE_SEPARATOR );

            // Build the from query part
            buildFromQueryPart( stringBuilderGlobalQuery, listFormPanelInitializerQueryPart, listFormColumnQueryPart );
            stringBuilderGlobalQuery.append( FormFilterQueryConstants.SPACE_SEPARATOR );

            // Build the join query part
            buildJoinQueryPart( stringBuilderGlobalQuery, listFormPanelInitializerQueryPart, listFormColumnQueryPart );
            stringBuilderGlobalQuery.append( FormFilterQueryConstants.SPACE_SEPARATOR );

            // Build the where query part
            buildWhereQueryPart( stringBuilderGlobalQuery, listFormFilterQueryPart );
        }

        return stringBuilderGlobalQuery.toString( );
    }

    /**
     * Populate the StringBuilder of the global query with the select query part
     * 
     * @param stringBuilderGlobalQuery
     *            The StringBuilder of the global query to populate
     * @param listFormPanelInitializerQueryPart
     *            The list of all IFormPanelInitializerQueryPart to use
     * @param listFormColumnQueryPart
     *            The list of FormColumnQueryPart to retrieve the select query parts from
     */
    private static void buildSelectQueryPart( StringBuilder stringBuilderGlobalQuery,
            List<IFormPanelInitializerQueryPart> listFormPanelInitializerQueryPart, List<IFormColumnQueryPart> listFormColumnQueryPart )
    {
        List<String> listSelectQueryParts = new ArrayList<>( );

        // Use the query part of the FormPanelInitializer
        List<String> listFormPanelInitializerSelectQueryParts = FormPanelInitializerQueryBuilder
                .buildFormPanelInitializerSelectQueryParts( listFormPanelInitializerQueryPart );
        if ( !CollectionUtils.isEmpty( listFormPanelInitializerSelectQueryParts ) )
        {
            listSelectQueryParts.addAll( listFormPanelInitializerSelectQueryParts );
        }

        // Use the query part of the column
        List<String> listFormColumnSelectQueryParts = FormColumnQueryBuilder.buildFormColumnSelectQueryPart( listFormColumnQueryPart );
        if ( !CollectionUtils.isEmpty( listFormColumnSelectQueryParts ) )
        {
            listSelectQueryParts.addAll( listFormColumnSelectQueryParts );
        }

        stringBuilderGlobalQuery.append( buildQueryPart( listSelectQueryParts, FormFilterQueryConstants.SELECT_KEYWORD ) );
    }

    /**
     * Populate the StringBuilder of the global query with the from query part
     * 
     * @param stringBuilderGlobalQuery
     *            The StringBuilder of the global query to populate
     * @param listFormPanelInitializerQueryPart
     *            The list of all IFormPanelInitializerQueryPart to use
     * @param listFormColumnQueryPart
     *            The list of FormColumnQueryPart to retrieve the select query parts from
     */
    private static void buildFromQueryPart( StringBuilder stringBuilderGlobalQuery, List<IFormPanelInitializerQueryPart> listFormPanelInitializerQueryPart,
            List<IFormColumnQueryPart> listFormColumnQueryPart )
    {
        List<String> listFromQueryParts = new ArrayList<>( );

        // Use the query part of the FormPanelInitializer
        List<String> listFormPanelInitializerFromQueryPart = FormPanelInitializerQueryBuilder
                .buildFormPanelInitializerFromQueryParts( listFormPanelInitializerQueryPart );
        if ( !CollectionUtils.isEmpty( listFormPanelInitializerFromQueryPart ) )
        {
            listFromQueryParts.addAll( listFormPanelInitializerFromQueryPart );
        }

        // Use the query parts of the columns
        List<String> listFormColumnFromQueryParts = FormColumnQueryBuilder.buildFormColumnFromQueryParts( listFormColumnQueryPart );
        if ( !CollectionUtils.isEmpty( listFormColumnFromQueryParts ) )
        {
            listFromQueryParts.addAll( listFormColumnFromQueryParts );
        }

        stringBuilderGlobalQuery.append( buildQueryPart( listFromQueryParts, FormFilterQueryConstants.FROM_KEYWORD ) );
    }

    /**
     * Populate the StringBuilder of the global query with the join query part
     * 
     * @param stringBuilderGlobalQuery
     *            The StringBuilder of the global query to populate
     * @param listFormPanelInitializerQueryPart
     *            The list of all IFormPanelInitializerQueryPart to use
     * @param listFormColumnQueryPart
     *            The list of FormColumnQueryPart to retrieve the select query parts from
     */
    private static void buildJoinQueryPart( StringBuilder stringBuilderGlobalQuery, List<IFormPanelInitializerQueryPart> listFormPanelInitializerQueryPart,
            List<IFormColumnQueryPart> listFormColumnQueryPart )
    {
        StringBuilder stringBuilderJoinQueryPart = new StringBuilder( );

        // Use the query parts of the panel filter
        if ( !CollectionUtils.isEmpty( listFormPanelInitializerQueryPart ) )
        {
            FormPanelInitializerQueryBuilder.buildFormPanelInitializerJoinQueryParts( stringBuilderJoinQueryPart, listFormPanelInitializerQueryPart );
        }

        // Use the query parts of the columns
        if ( !CollectionUtils.isEmpty( listFormColumnQueryPart ) )
        {
            FormColumnQueryBuilder.buildFormColumnJoinQueryParts( stringBuilderJoinQueryPart, listFormColumnQueryPart );
        }

        stringBuilderGlobalQuery.append( stringBuilderJoinQueryPart.toString( ) );
    }

    /**
     * Populate the StringBuilder of the global query with the where query part
     * 
     * @param stringBuilderGlobalQuery
     *            The StringBuilder of the global query to populate
     * @param listFormFilterQueryPart
     *            The list of IFormFilterQueryPart to retrieve the where query parts from
     */
    private static void buildWhereQueryPart( StringBuilder stringBuilderGlobalQuery, List<IFormFilterQueryPart> listFormFilterQueryPart )
    {
        if ( !CollectionUtils.isEmpty( listFormFilterQueryPart ) )
        {
            stringBuilderGlobalQuery.append( FormFilterQueryConstants.WHERE_BASE_KEYWORD ).append( FormFilterQueryConstants.SPACE_SEPARATOR );
            manageFilterWhereQueryParts( stringBuilderGlobalQuery, listFormFilterQueryPart );
        }
    }

    /**
     * Build a part of the query
     * 
     * @param listQueryPart
     *            The list of query part to use
     * @param strKeyWord
     *            The key word to use for the query for the current list of parts
     * @return the part of the query
     */
    private static String buildQueryPart( List<String> listQueryPart, String strKeyWord )
    {
        StringBuilder stringBuilderFromQuery = new StringBuilder( strKeyWord );
        stringBuilderFromQuery.append( FormFilterQueryConstants.SPACE_SEPARATOR );

        Iterator<String> iteratorQueryPart = listQueryPart.iterator( );
        while ( iteratorQueryPart.hasNext( ) )
        {
            stringBuilderFromQuery.append( iteratorQueryPart.next( ) );

            if ( iteratorQueryPart.hasNext( ) )
            {
                stringBuilderFromQuery.append( FormFilterQueryConstants.COMMA_SEPARATOR );
                stringBuilderFromQuery.append( FormFilterQueryConstants.SPACE_SEPARATOR );
            }
        }

        return stringBuilderFromQuery.toString( );
    }

    /**
     * Populate the StringBuilder of the query with all the where query parts of the list of FormFilter.
     * 
     * @param stringBuilderWhereQueryPart
     *            The stringBuilder of the request to populate with the where query parts of the given filters
     * @param listformFilterQueryPart
     *            The list of FormFilterQueryPart to retrieve the where query parts
     */
    private static void manageFilterWhereQueryParts( StringBuilder stringBuilderWhereQueryPart, List<IFormFilterQueryPart> listformFilterQueryPart )
    {
        Iterator<IFormFilterQueryPart> iteratorFormFilterQueryPart = listformFilterQueryPart.iterator( );
        while ( iteratorFormFilterQueryPart.hasNext( ) )
        {
            IFormFilterQueryPart formFilterQueryPart = iteratorFormFilterQueryPart.next( );
            addAndQueryClause( stringBuilderWhereQueryPart, formFilterQueryPart.getFormFilterQuery( ) );

            if ( iteratorFormFilterQueryPart.hasNext( ) )
            {
                stringBuilderWhereQueryPart.append( FormFilterQueryConstants.SPACE_SEPARATOR );
            }
        }
    }

    /**
     * Add and AND query part to the given StringBuilder for the specified query part. If the query is null or empty nothing will be added.
     * 
     * @param stringBuilderQuery
     *            The StringBuilder of the query to complete
     * @param strQueryPart
     *            The part of the query to added to the StringBuilder
     */
    private static void addAndQueryClause( StringBuilder stringBuilderQuery, String strQueryPart )
    {
        if ( stringBuilderQuery != null && StringUtils.isNotBlank( strQueryPart ) )
        {
            stringBuilderQuery.append( FormFilterQueryConstants.AND_KEYWORD );
            stringBuilderQuery.append( FormFilterQueryConstants.AND_OPEN_CLAUSE );
            stringBuilderQuery.append( strQueryPart );
            stringBuilderQuery.append( FormFilterQueryConstants.AND_CLOSE_CLAUSE );
        }
    }
}
