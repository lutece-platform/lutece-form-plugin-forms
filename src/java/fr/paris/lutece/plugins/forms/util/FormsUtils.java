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
package fr.paris.lutece.plugins.forms.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.Transition;
import fr.paris.lutece.portal.service.util.CryptoService;

/**
 * Utilities for forms
 */
public class FormsUtils
{
    private FormsUtils( )
    {
        // utility class
    }

    /**
     * Get a token to bypass the inactive state of the form
     * 
     * @param form
     *            the form
     * @param strTimestamp
     *            the timestamp for the start of the bypass period
     * @return a token
     */
    public static String getInactiveBypassToken( Form form, String strTimestamp )
    {
        StringBuilder builder = new StringBuilder( );
        builder.append( "formId:" ).append( form.getId( ) ).append( ":timestamp:" ).append( strTimestamp );
        return CryptoService.hmacSHA256( builder.toString( ) );
    }

    /**
     * Return a list of steps based on given step list and transition between steps list
     * 
     * @param listSteps
     *            the list of steps
     * @param listTransitions
     *            the list of transitions
     * @return the list of given steps based on given transitions
     */
    public static List<Step> sortStepsWithTransitions( List<Step> listSteps, List<Transition> listTransitions )
    {
        List<Step> listStepOrdered = new ArrayList<>( );
        Step initialStep = listSteps.stream( ).filter( Step::isInitial ).findFirst( ).orElse( null );
        if ( initialStep == null )
        {
            return listSteps;
        }

        Set<Integer> listIdStepOrderWithTransitions = new LinkedHashSet<>( );
        listIdStepOrderWithTransitions.add( initialStep.getId( ) );
        listIdStepOrderWithTransitions.addAll( getNextStep( initialStep.getId( ), listTransitions ) );
        for ( Integer nIdStep : listIdStepOrderWithTransitions )
        {
            for ( Step stepToOrder : listSteps )
            {
                if ( nIdStep == stepToOrder.getId( ) )
                {
                    listStepOrdered.add( stepToOrder );
                }
            }
        }
        for ( Step step : listSteps )
        {
            if ( !listIdStepOrderWithTransitions.contains( step.getId( ) ) )
            {
                listStepOrdered.add( step );
            }
        }

        return listStepOrdered;
    }

    /**
     * Get iteratively the next steps based on transitions list
     * 
     * @param idFromStep
     * @param listTransitions
     * @return the list of integer of the steps based on the transitions list
     */
    private static List<Integer> getNextStep( Integer idFromStep, List<Transition> listTransitions )
    {
        List<Integer> listIdNextSteps = new ArrayList<>( );
        if ( idFromStep != null )
        {
            List<Transition> nextTransitionsList = new ArrayList<>( );
            for ( Transition transition : listTransitions )
            {
                if ( transition.getFromStep( ) == idFromStep )
                {
                    listIdNextSteps.add( transition.getNextStep( ) );
                    nextTransitionsList.add( transition );
                }
            }
            for ( Transition transition : nextTransitionsList )
            {
                listIdNextSteps.addAll( getNextStep( transition.getNextStep( ), listTransitions ) );
            }
        }
        return listIdNextSteps;
    }
}
