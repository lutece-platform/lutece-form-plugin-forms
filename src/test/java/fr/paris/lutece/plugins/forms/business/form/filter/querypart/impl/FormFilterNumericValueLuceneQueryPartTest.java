/*
 * Copyright (c) 2002-2026, City of Paris
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.IntPoint;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.form.FormParameters;
import fr.paris.lutece.plugins.forms.business.form.search.FormResponseSearchItem;

/**
 * The query parts of the multiview filters that expect a number ignore a value that is not an integer
 */
public class FormFilterNumericValueLuceneQueryPartTest
{
    private static final String NOT_A_NUMBER = "e2e\"><b>x</b>";

    private static FormParameters parameters( String strKey, String strValue )
    {
        Map<String, Object> map = new LinkedHashMap<>( );
        map.put( strKey, strValue );
        FormParameters formParameters = new FormParameters( );
        formParameters.setFormParametersMap( map );
        return formParameters;
    }

    /**
     * A form id that is not an integer is ignored: no filter on the form
     */
    @Test
    public void testFormsFilterIgnoresValueThatIsNotAnInteger( )
    {
        FormFilterFormsLuceneQueryPart queryPart = new FormFilterFormsLuceneQueryPart( );
        queryPart.buildFormFilterQuery( parameters( "id_form", NOT_A_NUMBER ) );
        assertNull( queryPart.getFormFilterQuery( ) );
    }

    /**
     * A valid form id still builds the query on that form
     */
    @Test
    public void testFormsFilterKeepsAnIntegerValue( )
    {
        FormFilterFormsLuceneQueryPart queryPart = new FormFilterFormsLuceneQueryPart( );
        queryPart.buildFormFilterQuery( parameters( "id_form", "3" ) );
        assertEquals( IntPoint.newExactQuery( FormResponseSearchItem.FIELD_ID_FORM, 3 ), queryPart.getFormFilterQuery( ) );
    }

    /**
     * Workflow state ids that are not integers are ignored, the valid ones are kept
     */
    @Test
    public void testWorkflowStateFilterIgnoresValuesThatAreNotIntegers( )
    {
        FormFilterWorkflowStateLuceneQueryPart queryPart = new FormFilterWorkflowStateLuceneQueryPart( );
        queryPart.buildFormFilterQuery( parameters( "id_state", NOT_A_NUMBER ) );
        assertNull( queryPart.getFormFilterQuery( ) );

        queryPart.buildFormFilterQuery( parameters( "id_state", "2;" + NOT_A_NUMBER ) );
        assertEquals( IntPoint.newSetQuery( FormResponseSearchItem.FIELD_ID_WORKFLOW_STATE, List.of( 2 ) ), queryPart.getFormFilterQuery( ) );
    }

    /**
     * Form response ids that are not integers are ignored, the valid ones are kept
     */
    @Test
    public void testFormResponseIdFilterIgnoresValuesThatAreNotIntegers( )
    {
        Map<String, Object> map = new LinkedHashMap<>( );
        map.put( "id_1", NOT_A_NUMBER );
        map.put( "id_2", "5" );
        FormParameters formParameters = new FormParameters( );
        formParameters.setFormParametersMap( map );

        FormFilterIdFormResponseLuceneQueryPart queryPart = new FormFilterIdFormResponseLuceneQueryPart( );
        queryPart.buildFormFilterQuery( formParameters );
        assertEquals( IntPoint.newSetQuery( FormResponseSearchItem.FIELD_ID_FORM_RESPONSE, 5 ), queryPart.getFormFilterQuery( ) );
    }
}
