/*
 * Copyright (c) 2002-2025, City of Paris
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

import org.junit.jupiter.api.Test;

import fr.paris.lutece.test.LuteceTestCase;

/**
 * This is the business class test for the object Transition
 */
public class TransitionBusinessTest extends LuteceTestCase
{
    private static final int PRIORITY1 = 1;
    private static final int PRIORITY2 = 2;
    private static final String STEP_TITLE1 = "titre1";
    private static final String STEP_TITLE2 = "titre2";

    /**
     * test Transition
     */
    @Test
    public void testBusiness( )
    {
        // Initialize an object

        Step step1 = new Step( );
        step1.setTitle( STEP_TITLE1 );
        StepHome.create( step1 );

        Transition transition = new Transition( );
        transition.setFromStep( step1.getId( ) );
        transition.setNextStep( step1.getId( ) );
        transition.setPriority( PRIORITY1 );

        // Create test
        TransitionHome.create( transition );
        Transition transitionStored = TransitionHome.findByPrimaryKey( transition.getId( ) );
        assertEquals( transitionStored.getFromStep( ), transition.getFromStep( ) );
        assertEquals( transitionStored.getNextStep( ), transition.getNextStep( ) );
        assertEquals( transitionStored.getPriority( ), transition.getPriority( ) );

        Step step2 = new Step( );
        step2.setTitle( STEP_TITLE2 );
        StepHome.create( step2 );

        // Update test
        transition.setFromStep( step2.getId( ) );
        transition.setNextStep( step2.getId( ) );
        transition.setPriority( PRIORITY2 );
        TransitionHome.update( transition );
        transitionStored = TransitionHome.findByPrimaryKey( transition.getId( ) );
        assertEquals( transitionStored.getFromStep( ), transition.getFromStep( ) );
        assertEquals( transitionStored.getNextStep( ), transition.getNextStep( ) );
        assertEquals( transitionStored.getPriority( ), transition.getPriority( ) );

        // List test
        TransitionHome.getTransitionsList( );

        // Delete test
        TransitionHome.remove( transition.getId( ) );
        transitionStored = TransitionHome.findByPrimaryKey( transition.getId( ) );
        assertNull( transitionStored );

        StepHome.remove( step1.getId( ) );
        StepHome.remove( step2.getId( ) );

    }

}
