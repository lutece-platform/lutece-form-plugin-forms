/*
 * Copyright (c) 2002-2021, City of Paris
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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;

/**
 * This class provides instances management methods (create, find, ...) for Transition objects
 */
public final class TransitionHome
{
    // Static variable pointed at the DAO instance
    private static ITransitionDAO _dao = SpringContextService.getBean( "forms.transitionDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "forms" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private TransitionHome( )
    {
    }

    /**
     * Create an instance of the transition class
     * 
     * @param transition
     *            The instance of the Transition which contains the informations to store
     * @return The instance of transition which has been created with its primary key.
     */
    public static Transition create( Transition transition )
    {
        int nPriority = getCurrentPriorityByStep( transition.getFromStep( ) );
        if ( nPriority != -1 )
        {
            transition.setPriority( nPriority + 1 );
        }
        _dao.insert( transition, _plugin );

        return transition;
    }
    
    /**
     * Create an instance of the transition class
     * 
     * @param transition
     *            The instance of the Transition which contains the informations to store
     * @return The instance of transition which has been created with its primary key.
     */
    public static Transition createWithoutPriorityCalculation( Transition transition )
    {
        _dao.insert( transition, _plugin );
        return transition;
    }

    /**
     * Update of the transition which is specified in parameter
     * 
     * @param transition
     *            The instance of the Transition which contains the data to store
     * @return The instance of the transition which has been updated
     */
    public static Transition update( Transition transition )
    {
        _dao.store( transition, _plugin );

        return transition;
    }

    /**
     * Remove the transition whose identifier is specified in parameter
     * 
     * @param nKey
     *            The transition Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of a transition whose identifier is specified in parameter
     * 
     * @param nKey
     *            The transition primary key
     * @return an instance of Transition
     */
    public static Transition findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the transition objects and returns them as a list
     * 
     * @return the list which contains the data of all the transition objects
     */
    public static List<Transition> getTransitionsList( )
    {
        return _dao.selectTransitionsList( _plugin );
    }

    /**
     * Load the id of all the transition objects and returns them as a list
     * 
     * @return the list which contains the id of all the transition objects
     */
    public static List<Integer> getIdTransitionsList( )
    {
        return _dao.selectIdTransitionsList( _plugin );
    }

    /**
     * Load the data of all the transition objects and returns them as a referenceList
     * 
     * @return the referenceList which contains the data of all the transition objects
     */
    public static ReferenceList getTransitionsReferenceList( )
    {
        return _dao.selectTransitionsReferenceList( _plugin );
    }

    /**
     * Load the data of all the transition objects from the given step and returns them as a list
     * 
     * @param nIdStep
     *            The identifier of the starting step
     * @return the list which contains the data of all the transition objects
     */
    public static List<Transition> getTransitionsListFromStep( int nIdStep )
    {
        return _dao.selectTransitionsListFromStep( nIdStep, _plugin );
    }

    /**
     * Load the data of a transition object from the given step and the given priority
     * 
     * @param nIdStep
     *            The identifier of the step
     * @param nPriority
     *            The priority value
     * @return the transition object if existing
     */
    public static Transition getTransitionByPriority( int nIdStep, int nPriority )
    {

        return _dao.getTransitionByPriority( nIdStep, nPriority, _plugin );
    }

    /**
     * Rebuild the priority sequence of the transitions sharing the same starting Step
     * 
     * @param fromStep
     *            The starting step
     */
    public static void rebuildPrioritySequence( int fromStep )
    {
        List<Transition> listTransitions = getTransitionsListFromStep( fromStep );
        int nPriority = 1;

        for ( Transition transition : listTransitions )
        {
            transition.setPriority( nPriority++ );
            TransitionHome.update( transition );
        }
    }

    /**
     * Select the lowest priority of the transitions sharing the same starting Step Priority increase when value decrease.
     * 
     * @param fromStep
     *            The starting step
     * 
     * @return the Minimum priority ( maximum value )
     */
    public static int getCurrentPriorityByStep( int fromStep )
    {
        return _dao.selectMaxPriorityByStep( fromStep, _plugin );
    }

    /**
     * Remove all the transitions starting from or leading to a given step
     * 
     * @param nIdStep
     *            The Step Id
     */
    public static void removeTransitionByStep( int nIdStep )
    {
        _dao.deleteByStep( nIdStep, _plugin );

    }

    /**
     * Load the data of all the transition objects from the given form and returns them as a list
     * 
     * @param nIdForm
     * 
     * @return the list which contains the data of all the transition objects for given form
     */
    public static List<Transition> getTransitionsListFromForm( int nIdForm )
    {
        return _dao.selectTransitionsListFromForm( nIdForm, _plugin );
    }

}
