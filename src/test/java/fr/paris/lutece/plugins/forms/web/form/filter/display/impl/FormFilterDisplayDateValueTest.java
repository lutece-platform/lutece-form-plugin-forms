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
package fr.paris.lutece.plugins.forms.web.form.filter.display.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.form.filter.FormFilter;
import fr.paris.lutece.plugins.forms.business.form.filter.configuration.FormFilterDateConfiguration;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;

/**
 * The date filter of the multiview never keeps a request value that is not a valid date: the filter values are kept in the session of the user,
 * so a value that cannot be converted would break every later display of the multiview
 */
public class FormFilterDisplayDateValueTest
{
    private static final String FILTER_NAME = "date_creation";

    private static FormFilterDisplayFormResponseDate createDisplay( )
    {
        FormFilter formFilter = new FormFilter( );
        FormFilterDateConfiguration configuration = new FormFilterDateConfiguration( 1, "label", FILTER_NAME );
        formFilter.setFormFilterConfiguration( configuration );
        return (FormFilterDisplayFormResponseDate) configuration.getFormFilterDisplay( formFilter );
    }

    /**
     * Malformed from / to dates are ignored: no date filter
     */
    @Test
    public void testMalformedFromToDatesAreIgnored( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( FILTER_NAME + "_from", "2026-13-45" );
        request.addParameter( FILTER_NAME + "_to", "e2e\"><x" );

        assertTrue( createDisplay( ).getFilterDisplayMapValues( request ).isEmpty( ) );
    }

    /**
     * A period that has the expected shape but holds an invalid date is ignored and not kept
     */
    @Test
    public void testPeriodWithInvalidDateIsIgnored( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( FILTER_NAME, "45/13/2026 - 01/01/2026" );

        FormFilterDisplayFormResponseDate display = createDisplay( );
        assertTrue( display.getFilterDisplayMapValues( request ).isEmpty( ) );
        assertTrue( StringUtils.isEmpty( display.getValue( ) ) );
    }

    /**
     * A period that is not a date range is ignored and not kept
     */
    @Test
    public void testPeriodThatIsNotADateRangeIsNotKept( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( FILTER_NAME, "e2e\"><x" );

        FormFilterDisplayFormResponseDate display = createDisplay( );
        assertTrue( display.getFilterDisplayMapValues( request ).isEmpty( ) );
        assertTrue( StringUtils.isEmpty( display.getValue( ) ) );
    }

    /**
     * A valid period still builds the filter, the end date being included
     */
    @Test
    public void testValidPeriodIsKept( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( FILTER_NAME, "01/02/2026 - 03/02/2026" );

        FormFilterDisplayFormResponseDate display = createDisplay( );
        Map<String, Object> map = display.getFilterDisplayMapValues( request );

        assertEquals( "2026-02-01", map.get( FILTER_NAME + "_from" ) );
        assertEquals( "2026-02-04", map.get( FILTER_NAME + "_to" ) );
        assertEquals( "01/02/2026 - 03/02/2026", display.getValue( ) );
    }
}
