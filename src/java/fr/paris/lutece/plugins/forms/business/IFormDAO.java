/*
 * Copyright (c) 2002-2022, City of Paris
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

import fr.paris.lutece.plugins.forms.business.form.FormItemSortConfig;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;
import java.util.List;

/**
 * IFormDAO Interface
 */
public interface IFormDAO
{
    /**
     * Insert a new record in the table.
     * 
     * @param form
     *            instance of the Form object to insert
     * @param plugin
     *            the Plugin
     */
    void insert( Form form, Plugin plugin );

    /**
     * Update the record in the table
     * 
     * @param form
     *            the reference of the Form
     * @param plugin
     *            the Plugin
     */
    void store( Form form, Plugin plugin );

    /**
     * Delete a record from the table
     * 
     * @param nKey
     *            The identifier of the Form to delete
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
     *            The identifier of the form
     * @param plugin
     *            the Plugin
     * @return The instance of the form
     */
    Form load( int nKey, Plugin plugin );

    /**
     * Load the data of all the form objects and returns them as a list
     * 
     * @param plugin
     *            the Plugin
     * @return The list which contains the data of all the form objects
     */
    List<Form> selectFormsList( Plugin plugin );
    
    /**
	 * Load the data of all the form objects and returns them as a sorted list
     * 
     * @param plugin
     *            the Plugin
     *  @param sortConfig
     *            the sort configuration (column to sort, order type)           
     * @return The list which contains the data of all the form objects
	 */
	List<Form> selectFormsListSorted(Plugin plugin, FormItemSortConfig sortConfig);

    /**
     * Load the data of all the form objects and returns them as a referenceList
     * 
     * @param plugin
     *            the Plugin
     * @return The referenceList which contains the data of all the form objects
     */
    ReferenceList selectFormsReferenceList( Plugin plugin );

    /**
     * count the number of response for a form
     * 
     * @param nidForm
     *            id of form
     * @return the number of the responses for a form
     */
    int countNumberOfResponseForms( int nidForm );

    /**
     * count the number of response by the name of user
     * 
     * @param nIdForm
     *            id of form
     * @param strGuid
     *            the name of user
     * @return return the number of response of form of user
     */
    int countNumberOfResponseFormByUser( int nIdForm, String strGuid );
    
    /**
     * Load the data of all form Objects for a given list of form identifiers
     * 
     * @param listIdForm
     *            the list of form identifiers
     * @param plugin
     *            the Plugin
     * @return The list which contains the data of all the Form objects
     */
    List<Form> selectFormByPrimaryKeyList( List<Integer> listIdForm, Plugin plugin );

}
