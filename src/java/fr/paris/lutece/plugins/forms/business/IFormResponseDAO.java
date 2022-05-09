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

import fr.paris.lutece.portal.service.plugin.Plugin;
import java.util.List;

/**
 * IFormResponseDAO Interface
 */
public interface IFormResponseDAO
{
    /**
     * Insert a new record in the table.
     * 
     * @param formResponse
     *            instance of the formResponse object to insert
     * @param plugin
     *            the Plugin
     */
    void insert( FormResponse formResponse, Plugin plugin );

    /**
     * Update the record in the table
     * 
     * @param formResponse
     *            the reference of the FormResponse
     * @param plugin
     *            the Plugin
     */
    void store( FormResponse formResponse, Plugin plugin );

    /**
     * Delete a record from the table
     * 
     * @param nKey
     *            The identifier of the FormResponse to delete
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
     *            The identifier of the FormResponse
     * @param plugin
     *            the Plugin
     * @return The instance of the FormResponse
     */
    FormResponse load( int nKey, Plugin plugin );

    /**
     * Load the data of all the FormResponse objects and returns them as a list
     * 
     * @param plugin
     *            the Plugin
     * @return The list which contains the data of all the FormResponse objects
     */
    List<FormResponse> selectFormResponseList( Plugin plugin );

    /**
     * Load the primary keys of all the FormResponse objects and returns them as a list
     * 
     * @param plugin
     * @return the list of the primary keys of all the form responses
     */
    List<Integer> selectAllFormResponsesId( Plugin plugin );

    /**
     * Load the data of all the FormResponse objects and returns them as a list
     * 
     * @param nIdForm
     *            the id of the form
     * @param plugin
     *            the Plugin
     * @return The list which contains the data of all the FormResponse objects
     */
    List<FormResponse> selectFormResponseListUncompleteByIdForm( int nIdForm, Plugin plugin );

    /**
     * Load the data of all the FormResponse objects and returns them as a list
     * 
     * @param strGuid
     *            the given guid
     * @param nIdForm
     *            the form id key
     * @param plugin
     *            the Plugin
     * @return The list which contains the data of all the FormResponse objects
     */
    List<FormResponse> selectFormResponseByUser( String strGuid, int nIdForm, boolean fromBackup, Plugin plugin );

    /**
     * Load the data of all the FormResponse objects and returns them as a list
     * 
     * @param strGuid
     *            the given guid
     * @param plugin
     *            the Plugin
     * @return The list which contains the data of all the FormResponse objects
     */
    List<FormResponse> selectAllCompletedFormResponseByUser( String strGuid, Plugin plugin );

    /**
     * Load the data of all formResponses Objects for a given list of formResponses identifiers
     * 
     * @param listIdFormResponse
     *            the list of formResponses identifiers
     * @param plugin
     *            the Plugin
     * @return The list which contains the data of all the FormResponse objects
     */
    List<FormResponse> selectFormResponseByPrimaryKeyList( List<Integer> listIdFormResponse, Plugin plugin );

    /**
     * Remove all the formResponse linked to a given Form
     * 
     * @param nIdForm
     *            The form Identifier
     * @param plugin
     *            the Plugin
     */
    void deleteByForm( int nIdForm, Plugin plugin );

}
