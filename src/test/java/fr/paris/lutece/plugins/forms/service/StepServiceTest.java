/*
 * Copyright (c) 2002-2020, City of Paris
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
package fr.paris.lutece.plugins.forms.service;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.Transition;
import fr.paris.lutece.test.LuteceTestCase;

public class StepServiceTest extends LuteceTestCase
{

    public void testSortStepsWithTransitions( )
    {
        List<Step> listSteps = new ArrayList<>( );
        Step step1 = new Step( );
        step1.setId( 1 );
        step1.setInitial( true );
        listSteps.add( step1 );

        Step step3 = new Step( );
        step3.setId( 3 );
        listSteps.add( step3 );

        Step step21 = new Step( );
        step21.setId( 21 );
        listSteps.add( step21 );

        Step step22 = new Step( );
        step22.setId( 22 );
        listSteps.add( step22 );

        List<Transition> listTransitions = new ArrayList<>( );
        Transition transition1_21 = new Transition( );
        transition1_21.setFromStep( 1 );
        transition1_21.setNextStep( 21 );
        listTransitions.add( transition1_21 );

        Transition transition1_22 = new Transition( );
        transition1_22.setFromStep( 1 );
        transition1_22.setNextStep( 22 );
        listTransitions.add( transition1_22 );

        Transition transition21_3 = new Transition( );
        transition21_3.setFromStep( 21 );
        transition21_3.setNextStep( 3 );
        listTransitions.add( transition21_3 );

        Transition transition22_3 = new Transition( );
        transition22_3.setFromStep( 22 );
        transition22_3.setNextStep( 3 );
        listTransitions.add( transition22_3 );

        List<Step> orderedList = StepService.sortStepsWithTransitions( listSteps, listTransitions );

        assertEquals( 4, orderedList.size( ) );
        assertEquals( 1, orderedList.get( 0 ).getId( ) );
        assertEquals( 21, orderedList.get( 1 ).getId( ) );
        assertEquals( 22, orderedList.get( 2 ).getId( ) );
        assertEquals( 3, orderedList.get( 3 ).getId( ) );
    }

    public void testSortStepsWithTransitionsNoInitial( )
    {
        List<Step> listSteps = new ArrayList<>( );
        Step step1 = new Step( );
        step1.setId( 1 );
        step1.setInitial( true );
        listSteps.add( step1 );

        Step step3 = new Step( );
        step3.setId( 3 );
        listSteps.add( step3 );

        List<Transition> listTransitions = new ArrayList<>( );
        List<Step> orderedList = StepService.sortStepsWithTransitions( listSteps, listTransitions );
        assertEquals( 2, orderedList.size( ) );

    }
}
