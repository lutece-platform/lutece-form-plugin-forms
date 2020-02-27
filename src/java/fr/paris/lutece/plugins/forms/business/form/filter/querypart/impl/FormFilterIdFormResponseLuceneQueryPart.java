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

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.lucene.document.IntPoint;
import org.apache.lucene.search.Query;

import fr.paris.lutece.plugins.forms.business.form.FormParameters;
import fr.paris.lutece.plugins.forms.business.form.search.FormResponseSearchItem;

/**
 * Implementation of the IFormFilterQueryPart for an Entry filter
 */
public class FormFilterIdFormResponseLuceneQueryPart extends AbstractFormFilterLuceneQueryPart
{
    private static final int CONSTANT_INTEGER_MINUS_ONE = -1;

    /**
     * {@inheritDoc}
     */
    @Override
    public void buildFormFilterQuery( FormParameters formParameters )
    {
        if ( !formParameters.getFormParametersMap( ).isEmpty( ) )
        {
            Set<Map.Entry<String, Object>> setFormParameters = formParameters.getFormParametersMap( ).entrySet( );
            
            
            int[] idArray = setFormParameters.stream( )
                    .map( Map.Entry::getValue )
                    .filter( Objects::nonNull )
                    .map( Object::toString )
                    .map( Integer::parseInt )
                    .filter( ( Integer i ) -> i != CONSTANT_INTEGER_MINUS_ONE )
                    .distinct( )
                    .mapToInt( Integer::intValue ).toArray( );
            
            Query query = IntPoint.newSetQuery( FormResponseSearchItem.FIELD_ID_FORM_RESPONSE, idArray );
            setFormFilterQuery( query );
        }
        else
        {
            setFormFilterQuery( null );
        }
    }
}
