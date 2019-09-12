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

import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.service.plugin.Plugin;
import java.util.List;

/**
 * IFormQuestionResponseDAO Interface
 */
public interface IFormQuestionResponseDAO
{
    /**
     * Insert a new record in the table.
     * 
     * @param formQuestionResponse
     *            instance of the formQuestionResponse object to insert
     * @param plugin
     *            the Plugin
     */
    void insert( FormQuestionResponse formQuestionResponse, Plugin plugin );

    /**
     * Update the record in the table
     * 
     * @param formQuestionResponse
     *            the reference of the FormQuestionResponse
     * @param plugin
     *            the Plugin
     */
    void store( FormQuestionResponse formQuestionResponse, Plugin plugin );

    /**
     * Delete a record from the table
     * 
     * @param formQuestionResponse
     *            The formQuestionResponse to delete
     * @param plugin
     *            the Plugin
     */
    void delete( FormQuestionResponse formQuestionResponse, Plugin plugin );

    // /////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Load the data from the table
     * 
     * @param nKey
     *            The identifier of the FormQuestionResponse
     * @param plugin
     *            the Plugin
     * @return The instance of the FormQuestionResponse
     */
    FormQuestionResponse load( int nKey, Plugin plugin );

    /**
     * Load the data of all the FormQuestionResponse objects and returns them as a list
     * 
     * @param plugin
     *            the Plugin
     * @return The list which contains the data of all the FormQuestionResponse objects
     */
    List<FormQuestionResponse> selectFormQuestionResponseList( Plugin plugin );

    /**
     * Load the data of all the FormQuestionResponse objects and returns them as a list
     * 
     * @param nIdFormResponse
     *            The identifier of the FormResponse
     * @param nIdStep
     *            The identifier of the Step
     * @param plugin
     *            the Plugin
     * @return The list which contains the data of all the FormQuestionResponse objects
     */
    List<FormQuestionResponse> selectFormQuestionResponseListByStepAndFormResponse( int nIdFormResponse, int nIdStep, Plugin plugin );

    /**
     * Load the data of all the FormQuestionResponse objects and returns them as a list
     * 
     * @param nIdFormResponse
     *            The identifier of the FormResponse
     * @param nIdStep
     *            The identifier of the Step
     * @param plugin
     *            the Plugin
     * @return The list which contains the data of all the FormQuestionResponse objects
     */
    List<FormQuestionResponse> selectFormQuestionResponseListByListFormResponseStep( List<FormResponseStep> listFormResponseStep, Plugin plugin );

    /**
     * Load the data of all the FormQuestionResponse objects for saving and returns them as a list
     * 
     * @param nIdFormResponse
     *            The identifier of the FormResponse
     * @param plugin
     *            the Plugin
     * @return The list which contains the data of all the FormQuestionResponse objects
     */
    List<FormQuestionResponse> selectFormQuestionResponseListByFormResponse( int nIdFormResponse, Plugin plugin );

    /**
     * Retrieves the form question responses associated to the specified question.
     * 
     * @param nIdQuestion
     *            The identifier of the Question
     * @param plugin
     *            The Plugin to use to execute the query
     * @return the list of form question responses
     */
    List<FormQuestionResponse> selectFormQuestionResponseByQuestion( int nIdQuestion, Plugin plugin );

    /**
     * Retrieves the form question responses associated to the given form response for the specified question.
     * 
     * @param nIdFormResponse
     *            The identifier of the FormResponse
     * @param nIdQuestion
     *            The identifier of the Question
     * @param plugin
     *            The Plugin to use to execute the query
     * @return the found form question responses
     */
    List<FormQuestionResponse> selectFormQuestionResponseByResponseForQuestion( int nIdFormResponse, int nIdQuestion, Plugin plugin );

    /**
     * Retrieves the form question responses associated to the given Entry Response.
     * 
     * @param response
     * @param plugin
     * @return
     */
    FormQuestionResponse selectFormQuestionResponseByEntryResponse( Response response, Plugin plugin );

    /**
     * Populates the FormQuestionResponse of the list with their Response values.
     * 
     * @param formQuestionResponsesList
     * @param plugin
     */
    void completeListWithEntryResponses( List<FormQuestionResponse> formQuestionResponsesList, Plugin plugin );
}
