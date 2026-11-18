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
package fr.paris.lutece.plugins.forms.validation;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * The "number of answers" controls count the ticked boxes only
 */
public class ListResponseValidatorTest extends LuteceTestCase
{
    /**
     * The responses of a check box question: one per ticked box, or one empty response when nothing is ticked
     */
    private static FormQuestionResponse newResponse( int nTicked )
    {
        List<Response> listResponses = new ArrayList<>( );

        for ( int i = 0; i < nTicked; i++ )
        {
            Response response = new Response( );
            Field field = new Field( );
            field.setIdField( i + 1 );
            response.setField( field );
            listResponses.add( response );
        }

        if ( nTicked == 0 )
        {
            listResponses.add( new Response( ) );
        }

        FormQuestionResponse questionResponse = new FormQuestionResponse( );
        questionResponse.setEntryResponse( listResponses );

        return questionResponse;
    }

    private static Control newControl( String strValue )
    {
        Control control = new Control( );
        control.setValue( strValue );

        return control;
    }

    @Test
    public void testMinimum( )
    {
        ListMinResponseValidator validator = new ListMinResponseValidator( "forms.listMinResponseValidator", "name", new ArrayList<>( ) );

        assertFalse( validator.validate( newResponse( 0 ), newControl( "1" ) ) );
        assertTrue( validator.validate( newResponse( 1 ), newControl( "1" ) ) );
        assertFalse( validator.validate( newResponse( 1 ), newControl( "2" ) ) );
        assertTrue( validator.validate( newResponse( 0 ), newControl( "0" ) ) );
    }

    @Test
    public void testExact( )
    {
        ListExactResponseValidator validator = new ListExactResponseValidator( "forms.listExactResponseValidator", "name", new ArrayList<>( ) );

        assertFalse( validator.validate( newResponse( 0 ), newControl( "1" ) ) );
        assertTrue( validator.validate( newResponse( 1 ), newControl( "1" ) ) );
        assertTrue( validator.validate( newResponse( 0 ), newControl( "0" ) ) );
    }
}
