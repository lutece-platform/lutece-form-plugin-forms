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
package fr.paris.lutece.plugins.forms.business.form.column.querypart;

import fr.paris.lutece.plugins.forms.business.form.column.querypart.impl.lucene.IFormColumnLuceneQueryPart;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.BooleanClause;

import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.Query;


/**
 * Query builder utility class for FormColumn class
 */
public final class FormColumnLuceneQueryBuilder
{
    /**
     * Constructor
     */
    private FormColumnLuceneQueryBuilder( )
    {

    }

    /**
     * Return the list of all select query part from the given list of form columns
     * 
     * @param listFormColumnQueryPart
     *            The list of form column query part to retrieve the query parts from
     * @return the list of all select query parts from the given list of form columns
     */
    public static Query buildFormColumnsQueryPart( List<IFormColumnLuceneQueryPart> listFormColumnQueryPart )
    {
        Builder booleanQueryBuilder = new Builder( );
        if ( !CollectionUtils.isEmpty( listFormColumnQueryPart ) )
        {
            for ( IFormColumnLuceneQueryPart formColumnQueryPart : listFormColumnQueryPart )
            {
                Query queryFormColumnSelectQueryPart = formColumnQueryPart.getFormColumnSelectQuery();
                if ( queryFormColumnSelectQueryPart != null )
                {
                    booleanQueryBuilder.add( queryFormColumnSelectQueryPart, BooleanClause.Occur.SHOULD );
                }
            }
        }

        return booleanQueryBuilder.build();
    }
}
