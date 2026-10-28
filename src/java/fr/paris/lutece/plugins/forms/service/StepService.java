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
package fr.paris.lutece.plugins.forms.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.ControlType;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.FormResponseStepHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.business.Transition;
import fr.paris.lutece.plugins.forms.business.TransitionHome;
import fr.paris.lutece.plugins.forms.service.json.IStepTemplateProvider;
import fr.paris.lutece.plugins.forms.service.json.StepJsonData;
import fr.paris.lutece.plugins.forms.service.cache.FormsCacheService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.util.sql.TransactionManager;

/**
 * Service dedicated to management of formDisplay
 */
public final class StepService
{
    public static final String BEAN_NAME = "forms.stepService";

    private static final int DISPLAY_ROOT_PARENT_ID = 0;

    @Autowired( required = false )
    private IStepTemplateProvider _provider;

    /**
     * Constructor
     */
    private StepService( )
    {
    }

    /**
     * Lists the transitions a step takes part in, whether it is their origin or their destination.
     * 
     * @param nIdStep
     *            The Step Id
     * @return the transitions leaving the step and those arriving at it
     */
    private static List<Transition> getTransitionsAttachedToStep( int nIdStep )
    {
        List<Transition> listTransition = new ArrayList<>( TransitionHome.getTransitionsListFromStep( nIdStep ) );
        Step step = StepHome.findByPrimaryKey( nIdStep );

        if ( step != null )
        {
            for ( Transition transition : TransitionHome.getTransitionsListFromForm( step.getIdForm( ) ) )
            {
                if ( transition.getNextStep( ) == nIdStep )
                {
                    listTransition.add( transition );
                }
            }
        }

        return listTransition;
    }

    /**
     * Remove a given Step and the all its formDisplays. The responses, group/question associated to the displays will be deleted. All the descendants of the
     * displays will also be removed
     * 
     * @param nIdStep
     *            The Step Id
     */
    public void removeStep( int nIdStep )
    {
        FormDisplayService displayService = SpringContextService.getBean( FormDisplayService.BEAN_NAME );

        TransactionManager.beginTransaction( FormsPlugin.getPlugin( ) );

        try
        {
            // one accumulator for the whole removal : the groups of the displays and those of the transitions are reclaimed together, once
            Set<Integer> setIdControlGroup = new HashSet<>( );

            List<FormDisplay> listChildrenDisplay = FormDisplayHome.getFormDisplayListByParent( nIdStep, DISPLAY_ROOT_PARENT_ID );

            for ( FormDisplay childDisplay : listChildrenDisplay )
            {
                displayService.deleteDisplayAndDescendantsInTransaction( childDisplay.getId( ), setIdControlGroup );
            }

            // removeTransitionByStep deletes the transitions arriving at the step as well as those leaving it,
            // so the controls of both have to go, not only those of the outgoing ones
            for ( Transition transition : getTransitionsAttachedToStep( nIdStep ) )
            {
                setIdControlGroup.addAll( ControlHome.getControlGroupsByControlTargetAndType( transition.getId( ), ControlType.TRANSITION ) );

                ControlHome.removeByControlTarget( transition.getId( ), ControlType.TRANSITION );
            }

            ControlHome.removeEmptyControlGroups( setIdControlGroup );

            TransitionHome.removeTransitionByStep( nIdStep );

            FormResponseStepHome.removeByStep( nIdStep );

            StepHome.remove( nIdStep );

            TransactionManager.commitTransaction( FormsPlugin.getPlugin( ) );
        }
        catch( Exception e )
        {
            TransactionManager.rollBack( FormsPlugin.getPlugin( ) );
            // the caches were repopulated while the transaction was running : they now describe rows the rollback has undone
            SpringContextService.<FormsCacheService> getBean( FormsCacheService.BEAN_NAME ).resetCache( );
            throw new AppException( "Forms : the removal of the step " + nIdStep + " failed", e );
        }
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

    public IStepTemplateProvider getStepTemplateProvider( )
    {
        return _provider;
    }

    public StepJsonData getStepTemplateData( int idTemplate )
    {
        if ( _provider == null )
        {
            return null;
        }
        return _provider.getStepTemplateData( idTemplate );
    }
}
