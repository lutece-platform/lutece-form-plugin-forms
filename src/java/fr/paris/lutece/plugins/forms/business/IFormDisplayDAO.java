/*
 * Copyright (c) 2002-2019, Mairie de Paris
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
import fr.paris.lutece.util.ReferenceList;

import java.util.List;

/**
 * IFormDisplayDAO Interface
 */
public interface IFormDisplayDAO
{
    /**
     * Insert a new record in the table.
     * 
     * @param formDisplay
     *            instance of the FormDisplay object to insert
     * @param plugin
     *            the Plugin
     */
    void insert( FormDisplay formDisplay, Plugin plugin );

    /**
     * Update the record in the table
     * 
     * @param formDisplay
     *            the reference of the FormDisplay
     * @param plugin
     *            the Plugin
     */
    void store( FormDisplay formDisplay, Plugin plugin );

    /**
     * Delete a record from the table
     * 
     * @param nKey
     *            The identifier of the FormDisplay to delete
     * @param plugin
     *            the Plugin
     */
    void delete( int nKey, Plugin plugin );

    // /////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Load the data from the table
     * 
     * @param nKey
     *            The identifier of the FormDisplay
     * @param plugin
     *            the Plugin
     * @return The instance of the FormDisplay
     */
    FormDisplay load( int nKey, Plugin plugin );

    /**
     * Load the data of all the FormDisplay objects and returns them as a list
     * 
     * @param plugin
     *            the Plugin
     * @return The list which contains the data of all the FormDisplay objects
     */
    List<FormDisplay> selectFormDisplayList( Plugin plugin );

    /**
     * Load the data of all the FormDisplay objects by parent for a form step and returns them as a list
     * 
     * @param nIdStep
     *            the step id
     * @param nIdParent
     *            the parent id
     * @param plugin
     *            the plugin to use
     * @return The list which contains the data of all the FormDisplay objects
     */
    List<FormDisplay> selectFormDisplayListByParent( int nIdStep, int nIdParent, Plugin plugin );

    /**
     * Load the data of all the FormDisplay objects of type "Group" linked to a given FormStep and returns them as a referenceList
     * 
     * @param nIdStep
     *            the step identifier
     * @param plugin
     *            the plugin to use
     * @return the referenceList which contains the data of all the Display objects of group type
     */
    ReferenceList selectGroupDisplayReferenceListByStep( int nIdStep, Plugin plugin );

    /**
     * Select the FormDisplay associated to the given parameter
     * 
     * @param nIdForm
     *            The identifier of the Form to match
     * @param nIdStep
     *            The identifier of the Step to match
     * @param nIdComposite
     *            The identifier of the Composite to match
     * @param plugin
     *            The plugin to use to execute the query
     * @return the FormDisplay associated to the given parameter
     */
    FormDisplay selectFormdisplayByFormStepAndComposite( int nIdForm, int nIdStep, int nIdComposite, Plugin plugin );
}
