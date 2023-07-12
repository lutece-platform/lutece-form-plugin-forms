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
import fr.paris.lutece.util.ReferenceList;
import java.util.List;

/**
 * IQuestionDAO Interface
 */
public interface IQuestionDAO
{
    /**
     * Insert a new record in the table.
     * 
     * @param question
     *            instance of the Question object to insert
     * @param plugin
     *            the Plugin
     */
    void insert( Question question, Plugin plugin );

    /**
     * Update the record in the table
     * 
     * @param question
     *            the reference of the Question
     * @param plugin
     *            the Plugin
     */
    void store( Question question, Plugin plugin );

    /**
     * Delete a record from the table
     * 
     * @param nKey
     *            The identifier of the Question to delete
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
     *            The identifier of the question
     * @param plugin
     *            the Plugin
     * @return The instance of the question
     */
    Question load( int nKey, Plugin plugin );

    /**
     * Load the data from the table
     * 
     * @param strCode
     *            The code of the question
     * @param plugin
     *            the Plugin
     * @return The instance of the question
     */
    List<Question> loadByCode( String strCode, Plugin plugin );

    /**
     * Load the data from the table
     * 
     * @param strCode
     *            The code of the question
     * @param plugin
     *            the Plugin
     * @return The instance of the question
     */
    Question loadByCodeAndEntry( String strCode, int idEntry, Plugin plugin );

    /**
     * Load the data from the table
     * 
     * @param keyList
     *            The identifiers of the questions
     * @param plugin
     *            the Plugin
     * @return The list of questions
     */
    List<Question> loadMultiple( List<Integer> keyList, Plugin plugin );

    /**
     * Load the data of all the question objects and returns them as a list
     * 
     * @param plugin
     *            the Plugin
     * @return The list which contains the data of all the question objects
     */
    List<Question> selectQuestionsList( Plugin plugin );

    /**
     * Load the data of all the question objects and returns them as a list
     * 
     * @param plugin
     *            the Plugin
     * @return The list which contains the uncomplete data of all the question objects
     */
    List<Question> selectQuestionsListUncomplete( Plugin plugin );

    /**
     * Load the data of all the question objects by step and returns them as a list
     * 
     * @param plugin
     *            the Plugin
     * @param nIdStep
     *            the step id
     * @return The list which contains the data of all the question objects
     */
    List<Question> selectQuestionsListByStep( int nIdStep, Plugin plugin );

    /**
     * Load the data of all the question objects by form id and returns them as a list
     * 
     * @param nIdForm
     *            The id of the form
     * @param plugin
     *            The plugin
     * @return The list which contains the data of all the question objects by given form id
     */
    List<Question> selectQuestionsListByFormId( int nIdForm, Plugin plugin );

    /**
     * Load the data of all the question objects by form id and returns them as a list
     * 
     * @param nIdForm
     *            The id of the form
     * @param plugin
     *            The plugin
     * @return The list which contains the data of all the question objects by given form id
     */
    List<Question> selectQuestionsListByFormIdUncomplete( int nIdForm, Plugin plugin );

    /**
     * Load the id of all the question objects and returns them as a list
     * 
     * @param plugin
     *            the Plugin
     * @return The list which contains the id of all the question objects
     */
    List<Integer> selectIdQuestionsList( Plugin plugin );

    /**
     * Load the data of all the question objects and returns them as a referenceList
     * 
     * @param plugin
     *            the Plugin
     * @return The referenceList which contains the data of all the question objects
     */
    ReferenceList selectQuestionsReferenceList( Plugin plugin );

    /**
     * Load the data of all the question objects for the given form and returns them as a referenceList
     *
     * @param nIdForm
     *            the form id
     * @param plugin
     *            the Plugin
     * @return The referenceList which contains the data of all the question objects
     */
    ReferenceList selectQuestionsReferenceListByForm( int nIdForm, Plugin plugin );
}
