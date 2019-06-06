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

import fr.paris.lutece.plugins.forms.business.form.column.querypart.IFormColumnQueryPart;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import fr.paris.lutece.plugins.forms.business.form.filter.querypart.IFormFilterLuceneQueryPart;
import fr.paris.lutece.plugins.forms.business.form.filter.querypart.IFormFilterQueryPart;
import fr.paris.lutece.plugins.forms.business.form.panel.initializer.querypart.IFormPanelInitializerLuceneQueryPart;
import fr.paris.lutece.plugins.forms.business.form.panel.initializer.querypart.IFormPanelInitializerQueryPart;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

/**
 * Class use to build a SQL query from part of query inside FormFilter and FormColumn
 */
public final class LuceneQueryBuilder
{
    /**
     * Constructor
     */
    private LuceneQueryBuilder( )
    {

    }

    /**
     * Build a Lucene query from different parts from a list of FormColumn and a list of FormFilter
     * 
     * @param listFormPanelInitializerQueryPart
     *            The list of all FormPanelInitializerQueryPart to use for built the query
     * @param listFormColumnQueryPart
     *            The of FormColumnQueryPart to retrieve the select and from parts of the query
     * @param listFormFilterQueryPart
     *            The list of FormFilterQueryPart to retrieve the where parts of the query
     * @return the global lucene query build from the FormColmuns and FormFilters
     */
    public static Query buildQuery( List<IFormPanelInitializerQueryPart> listFormPanelInitializerQueryPart,
            List<IFormColumnQueryPart> listFormColumnQueryPart, List<IFormFilterQueryPart> listFormFilterQueryPart )
    {
        BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder( );
        if ( !CollectionUtils.isEmpty( listFormColumnQueryPart ) )
        {
            for ( IFormFilterQueryPart formFilterQueryPart : listFormFilterQueryPart )
            {
                if ( formFilterQueryPart instanceof IFormFilterLuceneQueryPart )
                {
                    Query queryFormFilterQueryPart = ((IFormFilterLuceneQueryPart) formFilterQueryPart).getFormFilterQuery( );
                    if ( queryFormFilterQueryPart != null )
                    {
                        booleanQueryBuilder.add( queryFormFilterQueryPart, BooleanClause.Occur.FILTER );
                    }
                }
            }
            for ( IFormPanelInitializerQueryPart formPanelInitializerQueryPart : listFormPanelInitializerQueryPart )
            {
                if ( formPanelInitializerQueryPart instanceof IFormPanelInitializerLuceneQueryPart )
                {
                    Query queryFormFilterQueryPart = ((IFormPanelInitializerLuceneQueryPart) formPanelInitializerQueryPart).getFormPanelInitializerSelectQuery();
                    if ( queryFormFilterQueryPart != null )
                    {
                        booleanQueryBuilder.add( queryFormFilterQueryPart, BooleanClause.Occur.FILTER );
                    }
                }
            }
        }

        return booleanQueryBuilder.build( );
    }

}