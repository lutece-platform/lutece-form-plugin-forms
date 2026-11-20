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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Tests of the "minimum elapsed days" control of a date question
 */
public class DayDelayMinimumValidatorTest extends LuteceTestCase
{
    private DayDelayMinimumValidator _validator;

    @Override
    protected void setUp( ) throws Exception
    {
        super.setUp( );
        _validator = new DayDelayMinimumValidator( "forms.dayDelayMinimumValidator", "name", new ArrayList<>( ) );
    }

    private static FormQuestionResponse newResponse( String strValue )
    {
        List<Response> listResponses = new ArrayList<>( );

        if ( strValue != null )
        {
            Response response = new Response( );
            response.setResponseValue( strValue );
            listResponses.add( response );
        }

        FormQuestionResponse questionResponse = new FormQuestionResponse( );
        questionResponse.setEntryResponse( listResponses );

        return questionResponse;
    }

    private static String daysAgo( int nDays )
    {
        return String.valueOf( LocalDate.now( ).minusDays( nDays ).atStartOfDay( ZoneId.systemDefault( ) ).toInstant( ).toEpochMilli( ) );
    }

    private static Control newControl( String strDays )
    {
        Control control = new Control( );
        control.setValue( strDays );

        return control;
    }

    @Test
    public void testDateAtLeastTheDaysBeforeToday( )
    {
        Control control = newControl( "2" );

        assertTrue( _validator.validate( newResponse( daysAgo( 2 ) ), control ) );
        assertTrue( _validator.validate( newResponse( daysAgo( 400 ) ), control ) );
        assertFalse( _validator.validate( newResponse( daysAgo( 1 ) ), control ) );
        assertFalse( _validator.validate( newResponse( daysAgo( 0 ) ), control ) );
        assertFalse( _validator.validate( newResponse( daysAgo( -5 ) ), control ) );
    }

    @Test
    public void testEmptyDateIsNotControlled( )
    {
        Control control = newControl( "2" );

        assertTrue( _validator.validate( newResponse( null ), control ) );
        assertTrue( _validator.validate( newResponse( "" ), control ) );

        Response response = new Response( );
        FormQuestionResponse questionResponse = new FormQuestionResponse( );
        List<Response> listResponses = new ArrayList<>( );
        listResponses.add( response );
        questionResponse.setEntryResponse( listResponses );
        assertTrue( "a response without value", _validator.validate( questionResponse, control ) );
    }
}
