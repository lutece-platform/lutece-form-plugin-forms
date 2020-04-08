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
package fr.paris.lutece.plugins.forms.business;

import java.util.List;
import java.util.Set;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;

/**
 * IControlDAO Interface
 */
public interface IControlDAO
{
    /**
     * Insert a new record in the table.
     * 
     * @param control
     *            instance of the Control object to insert
     * @param plugin
     *            the Plugin
     */
    void insert( Control control, Plugin plugin );

    /**
     * Insert a new record in the table.
     * 
     * @param nIdcontrol
     *            id of the Control object to insert
     * @param nIdQuestion
     *            the question id
     * @param plugin
     *            the Plugin
     */
    void insert( int nIdControl, int nIdQuestion, Plugin plugin );

    /**
     * Insert a new record in the table.
     * 
     * @param nIdcontrol
     *            id of the Control object to insert
     * @param nIdQuestion
     *            the question id
     * @param strValue
     *            the value
     * @param plugin
     *            the Plugin
     */
    void insert( int nIdControl, int nIdQuestion, String strValue, Plugin plugin );

    /**
     * Update the record in the table
     * 
     * @param control
     *            the reference of the Control
     * @param plugin
     *            the Plugin
     */
    void store( Control control, Plugin plugin );

    /**
     * Delete a record from the table
     * 
     * @param nKey
     *            The identifier of the Control to delete
     * @param plugin
     *            the Plugin
     */
    void delete( int nKey, Plugin plugin );

    /**
     * Delete a record from the table
     * 
     * @param nKey
     *            The identifier of the Control to delete
     * @param plugin
     *            the Plugin
     */
    void deleteControlQuestion( int nControl, Plugin plugin );

    /**
     * Delete a record from the table
     * 
     * @param nKey
     *            The identifier of the Control to delete
     * @param plugin
     *            the Plugin
     */
    void deleteControlQuestionValue( int nControl, Plugin plugin );

    /**
     * Delete a record from the table by the control target
     * 
     * @param nIdControlTarget
     *            The identifier of the Control target
     * @param controlType
     *            The control type
     * @param plugin
     *            the Plugin
     */
    void deleteByControlTarget( int nIdControlTarget, ControlType controlType, Plugin plugin );

    // /////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Load the data from the table
     * 
     * @param nKey
     *            The identifier of the control
     * @param plugin
     *            the Plugin
     * @return The instance of the control
     */
    Control load( int nKey, Plugin plugin );

    /**
     * Load the data from the table
     * 
     * @param nIdControl
     *            The identifier of the control
     * @param plugin
     *            the Plugin
     * @return The list of id questions
     */
    Set<Integer> loadIdQuestions( int nIdControl, Plugin plugin );

    /**
     * Load the data of all the control objects and returns them as a list
     * 
     * @param plugin
     *            the Plugin
     * @return The list which contains the data of all the control objects
     */
    List<Control> selectControlsList( Plugin plugin );

    /**
     * Load the id of all the control objects and returns them as a list
     * 
     * @param plugin
     *            the Plugin
     * @return The list which contains the id of all the control objects
     */
    List<Integer> selectIdControlsList( Plugin plugin );

    /**
     * Load the data of all the control objects and returns them as a referenceList
     * 
     * @param plugin
     *            the Plugin
     * @return The referenceList which contains the data of all the control objects
     */
    ReferenceList selectControlsReferenceList( Plugin plugin );

    /**
     * Select a control for conditional display based on its attached display id
     * 
     * @param nIdControlTarget
     *            the control target id
     * @param controlType
     *            the control type
     * @param plugin
     *            the Plugin
     * @return The control
     */
    List<Control> selectControlByControlTargetAndType( int nIdControlTarget, ControlType controlType, Plugin plugin );

    /**
     * Select control list based on its attached question and control type
     * 
     * @param nIdQuestion
     *            the question id
     * @param strControlType
     *            the control type
     * @param plugin
     *            the Plugin
     * @return The control
     */
    List<Control> selectControlByQuestionAndType( int nIdQuestion, String strControlType, Plugin plugin );

    /**
     * Select control list based on its attached question and control type
     * 
     * @param nIdQuestion
     *            the question id
     * @param plugin
     *            the Plugin
     * @return The control
     */
    List<Control> selectControlByQuestion( int nIdQuestion, Plugin plugin );

    /**
     * Load the data of all the control mapping and returns them as a referenceList
     * 
     * @param plugin
     *            the Plugin
     * @return The referenceList which contains the data of all the control mapping object
     */
    ReferenceList selectMappingControlReferenceList( int nIdControl, Plugin plugin );

}
