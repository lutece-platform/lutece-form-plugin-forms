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

package fr.paris.lutece.plugins.forms.business;

import fr.paris.lutece.test.LuteceTestCase;

import java.sql.Date;

/**
 * This is the business class test for the object Form
 */
public class FormBusinessTest extends LuteceTestCase
{
    private static final String TITLE1 = "Title1";
    private static final String TITLE2 = "Title2";
    private static final String DESCRIPTION1 = "Description1";
    private static final String DESCRIPTION2 = "Description2";
    private static final Date CREATIONDATE1 = new Date( 1000000l );
    private static final Date CREATIONDATE2 = new Date( 2000000l );
    private static final Date STARTDATE1 = new Date( 1000000l );
    private static final Date STARTDATE2 = new Date( 2000000l );
    private static final Date ENDDATE1 = new Date( 1000000l );
    private static final Date ENDDATE2 = new Date( 2000000l );

    /**
     * test Form
     */
    public void testBusiness( )
    {
        // Initialize an object
        Form form = new Form( );
        form.setTitle( TITLE1 );
        form.setDescription( DESCRIPTION1 );
        form.setCreationDate( CREATIONDATE1 );
        form.setStartDate( STARTDATE1 );
        form.setEndDate( ENDDATE1 );

        // Create test
        FormHome.create( form );
        Form formStored = FormHome.findByPrimaryKey( form.getId( ) );
        assertEquals( formStored.getTitle( ), form.getTitle( ) );
        assertEquals( formStored.getDescription( ), form.getDescription( ) );
        assertEquals( formStored.getCreationDate( ), form.getCreationDate( ) );
        assertEquals( formStored.getStartDate( ), form.getStartDate( ) );
        assertEquals( formStored.getEndDate( ), form.getEndDate( ) );

        // Update test
        form.setTitle( TITLE2 );
        form.setDescription( DESCRIPTION2 );
        form.setCreationDate( CREATIONDATE2 );
        form.setStartDate( STARTDATE2 );
        form.setEndDate( ENDDATE2 );
        FormHome.update( form );
        formStored = FormHome.findByPrimaryKey( form.getId( ) );
        assertEquals( formStored.getTitle( ), form.getTitle( ) );
        assertEquals( formStored.getDescription( ), form.getDescription( ) );
        assertEquals( formStored.getCreationDate( ), form.getCreationDate( ) );
        assertEquals( formStored.getStartDate( ), form.getStartDate( ) );
        assertEquals( formStored.getEndDate( ), form.getEndDate( ) );

        // List test
        FormHome.getFormsList( );

        // Delete test
        FormHome.remove( form.getId( ) );
        formStored = FormHome.findByPrimaryKey( form.getId( ) );
        assertNull( formStored );

    }

}
