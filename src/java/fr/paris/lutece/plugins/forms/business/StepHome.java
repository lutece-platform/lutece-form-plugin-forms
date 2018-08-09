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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;

/**
 * This class provides instances management methods (create, find, ...) for Step objects
 */
public final class StepHome
{
    // Static variable pointed at the DAO instance
    private static IStepDAO _dao = SpringContextService.getBean( "forms.stepDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "forms" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private StepHome( )
    {
    }

    /**
     * Create an instance of the step class
     * 
     * @param step
     *            The instance of the Step which contains the informations to store
     * @return The instance of step which has been created with its primary key.
     */
    public static Step create( Step step )
    {
        _dao.insert( step, _plugin );

        return step;
    }

    /**
     * Update of the step which is specified in parameter
     * 
     * @param step
     *            The instance of the Step which contains the data to store
     * @return The instance of the step which has been updated
     */
    public static Step update( Step step )
    {
        _dao.store( step, _plugin );

        return step;
    }

    /**
     * Remove the step whose identifier is specified in parameter
     * 
     * @param nKey
     *            The step Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of a step whose identifier is specified in parameter
     * 
     * @param nKey
     *            The step primary key
     * @return an instance of Step
     */
    public static Step findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Returns the initial step of the given form id
     * 
     * @param nIdForm
     *            The step form primary key
     * @return the initial step of the given form id
     */
    public static Step getInitialStep( int nIdForm )
    {
        return _dao.selectInitialStep( nIdForm, _plugin );
    }

    /**
     * Load the data of all the step objects and returns them as a list
     * 
     * @return the list which contains the data of all the step objects
     */
    public static List<Step> getStepsList( )
    {
        return _dao.selectStepsList( _plugin );
    }

    /**
     * Load the data of all the steps linked to a given form and returns them as a list
     * 
     * @param nFormId
     *            the form identifier
     * 
     * @return the list which contains the data of all the step objects
     */
    public static List<Step> getStepsListByForm( int nFormId )
    {
        return _dao.selectStepsListbyForm( nFormId, _plugin );
    }

    /**
     * Load the data of all the steps linked to a given form and returns them as a ReferenceList
     * 
     * @param nFormId
     *            the form identifier
     * 
     * @return the ReferenceList which contains the data of all the step objects
     */
    public static ReferenceList getStepReferenceListByForm( int nFormId )
    {
        return _dao.selectStepReferenceListbyForm( nFormId, _plugin );
    }

    /**
     * Load the id of all the step objects and returns them as a list
     * 
     * @return the list which contains the id of all the step objects
     */
    public static List<Integer> getIdStepsList( )
    {
        return _dao.selectIdStepsList( _plugin );
    }

    /**
     * Load the id of all the step objects linked to a given form and returns them as a list
     * 
     * @param nIdForm
     *            the form identifier
     * 
     * @return the list which contains the id of all the step objects
     */
    public static List<Integer> getIdStepsListByForm( int nIdForm )
    {
        return _dao.selectIdStepsListByForm( nIdForm, _plugin );
    }

    /**
     * Load the data of all the step objects and returns them as a referenceList
     * 
     * @return the referenceList which contains the data of all the step objects
     */
    public static ReferenceList getStepsReferenceList( )
    {
        return _dao.selectStepsReferenceList( _plugin );
    }
}
