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
package fr.paris.lutece.plugins.forms.business.form.filter.querypart.impl;

import fr.paris.lutece.plugins.forms.business.form.FormParameters;
import fr.paris.lutece.plugins.forms.business.form.search.FormResponseSearchItem;
import fr.paris.lutece.portal.service.util.AppLogService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.search.Query;

/**
 * Implementation of the IFormFilterQueryPart for an Entry filter
 */
public class FormFilterFormResponseDateCreationLuceneQueryPart extends AbstractFormFilterLuceneQueryPart
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void buildFormFilterQuery( FormParameters formParameters )
    {
        if ( !formParameters.getFormParametersMap( ).isEmpty( ) )
        {
            Collection<Object> setFormParameters = formParameters.getFormParametersMap( ).values( );

            String strDateFrom = String.valueOf( setFormParameters.toArray( ) [0] );
            String strDateTo = String.valueOf( setFormParameters.toArray( ) [1] );

            SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd" );
            try
            {
                Date dateFrom = (Date) formatter.parse( strDateFrom );
                Date dateTo = (Date) formatter.parse( strDateTo );
                long lFrom = dateFrom.getTime( );
                long lTo = dateTo.getTime( );
                if ( strDateFrom != null && strDateTo != null )
                {
                    Query queryCreationDate = LongPoint.newRangeQuery( FormResponseSearchItem.FIELD_DATE_CREATION, lFrom, lTo );
                    setFormFilterQuery( queryCreationDate );
                }
            }
            catch( NumberFormatException | ParseException e )
            {
                AppLogService.error( "Unable to convert given dates to longs", e );
            }
        }
        else
        {
            setFormFilterQuery( null );
        }
    }
}
