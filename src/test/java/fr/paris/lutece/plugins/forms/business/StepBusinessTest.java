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

/**
 * This is the business class test for the object Step
 */
public class StepBusinessTest extends LuteceTestCase
{
    private static final String TITLE1 = "Title1";
    private static final String TITLE2 = "Title2";
    private static final String DESCRIPTION1 = "Description1";
    private static final String DESCRIPTION2 = "Description2";
    private static final int IDFORM1 = 1;
    private static final int IDFORM2 = 2;

    /**
     * test Step
     */
    public void testBusiness( )
    {
        // Initialize an object
        Step step = new Step( );
        step.setTitle( TITLE1 );
        step.setDescription( DESCRIPTION1 );
        step.setIdForm( IDFORM1 );
        step.setInitial( true );
        step.setFinal( true );

        // Create test
        StepHome.create( step );
        Step stepStored = StepHome.findByPrimaryKey( step.getId( ) );
        assertEquals( stepStored.getTitle( ), step.getTitle( ) );
        assertEquals( stepStored.getDescription( ), step.getDescription( ) );
        assertEquals( stepStored.getIdForm( ), step.getIdForm( ) );
        assertEquals( stepStored.isFinal( ), step.isFinal( ) );
        assertEquals( stepStored.isInitial( ), step.isInitial( ) );

        // Update test
        step.setTitle( TITLE2 );
        step.setDescription( DESCRIPTION2 );
        step.setIdForm( IDFORM2 );
        StepHome.update( step );
        stepStored = StepHome.findByPrimaryKey( step.getId( ) );
        assertEquals( stepStored.getTitle( ), step.getTitle( ) );
        assertEquals( stepStored.getDescription( ), step.getDescription( ) );
        assertEquals( stepStored.getIdForm( ), step.getIdForm( ) );
        assertEquals( stepStored.isFinal( ), step.isFinal( ) );
        assertEquals( stepStored.isInitial( ), step.isInitial( ) );

        // List test
        StepHome.getStepsList( );

        // Delete test
        StepHome.remove( step.getId( ) );
        stepStored = StepHome.findByPrimaryKey( step.getId( ) );
        assertNull( stepStored );

    }

}
