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
package fr.paris.lutece.plugins.forms.business.form.filter.querypart.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.form.FormParameters;
import fr.paris.lutece.plugins.forms.business.form.search.FormResponseSearchItem;
import fr.paris.lutece.plugins.forms.service.search.IFormSearchEngine;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;

/**
 * Implementation of the IFormFilterQueryPart for an Entry filter
 */
public class FormFilterEntryLuceneQueryPart extends AbstractFormFilterLuceneQueryPart
{
    IFormSearchEngine _formSearchEngine;

    @Override
    public void buildFormFilterQuery( FormParameters formParameters )
    {
        BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder( );
        if ( formParameters.getFormParametersMap( ).isEmpty( ) )
        {
            setFormFilterQuery( null );
            return;
        }
        
        Set<Map.Entry<String, Object>> setFormParameters = formParameters.getFormParametersMap( ).entrySet( );

        boolean bEmptyQuery = true;
        for ( Map.Entry<String, Object> formParam : setFormParameters )
        {

            String params = String.valueOf( formParam.getValue( ) );
            List<String> valueList = Arrays.asList( params.split( ";" ) ).stream( )
                    .filter( StringUtils::isNotEmpty )
                    .filter( s -> !String.valueOf( FormsConstants.DEFAULT_ID_VALUE ).equals( s ) )
                    .collect( Collectors.toList( ) );
            
            
            if ( CollectionUtils.isNotEmpty( valueList ) )
            {
                bEmptyQuery = false;
                String strQuestionCode = formParam.getKey( );
                List<Question> questionList = QuestionHome.findByCode( strQuestionCode );
                for ( Question question : questionList )
                {
                    List<Field> listFields = FieldHome.getFieldListByIdEntry( question.getEntry( ).getIdEntry( ) );

                    for ( String value : valueList )
                    {
                        Query query = new TermQuery( new Term(
                                FormResponseSearchItem.FIELD_ENTRY_CODE_SUFFIX + strQuestionCode + FormResponseSearchItem.FIELD_RESPONSE_FIELD_ITER + "0",
                                value ) );
                        booleanQueryBuilder.add( query, BooleanClause.Occur.SHOULD );
                        for ( Field field : listFields )
                        {
                            String strFieldName = getFieldName( field );
                            query = new TermQuery(
                                    new Term(
                                            FormResponseSearchItem.FIELD_ENTRY_CODE_SUFFIX + strQuestionCode + FormResponseSearchItem.FIELD_RESPONSE_FIELD_ITER
                                                    + "0" + FormResponseSearchItem.FIELD_RESPONSE_FIELD_SEPARATOR + strFieldName,
                                                    value ) );
                                booleanQueryBuilder.add( query, BooleanClause.Occur.SHOULD );
                        }
                        
                    }
                }
            }
        }
        if ( !bEmptyQuery )
        {
            setFormFilterQuery( booleanQueryBuilder.build( ) );
        }
        else
        {
            setFormFilterQuery( null );
        }
    }

    /**
     * Get the field name
     * 
     * @param responseField
     * @param response
     * @return the field name
     */
    private String getFieldName( fr.paris.lutece.plugins.genericattributes.business.Field responseField )
    {
        if ( responseField.getIdField( ) > 0 )
        {
            return String.valueOf( responseField.getIdField( ) );
        }
        if ( !StringUtils.isEmpty( responseField.getCode( ) ) )
        {
            return responseField.getCode( );
        }
        if ( !StringUtils.isEmpty( responseField.getTitle( ) ) )
        {
            return responseField.getTitle( );
        }
        return StringUtils.EMPTY;
    }
}
