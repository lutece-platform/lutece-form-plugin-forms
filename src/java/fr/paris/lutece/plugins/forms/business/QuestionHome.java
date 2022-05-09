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

import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;

/**
 * This class provides instances management methods (create, find, ...) for Question objects
 */
public final class QuestionHome
{
    // Static variable pointed at the DAO instance
    private static IQuestionDAO _dao = SpringContextService.getBean( "forms.questionDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "forms" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private QuestionHome( )
    {
    }

    /**
     * Create an instance of the question class
     * 
     * @param question
     *            The instance of the Question which contains the informations to store
     * @return The instance of question which has been created with its primary key.
     */
    public static Question create( Question question )
    {
        _dao.insert( question, _plugin );

        return question;
    }

    /**
     * Update of the question which is specified in parameter
     * 
     * @param question
     *            The instance of the Question which contains the data to store
     * @return The instance of the question which has been updated
     */
    public static Question update( Question question )
    {
        _dao.store( question, _plugin );

        return question;
    }

    /**
     * Remove the question whose identifier is specified in parameter. The associated Entry and EntryResponses will be also deleted
     * 
     * @param nKey
     *            The question Id
     */
    public static void remove( int nKey )
    {
        Question questionToDelete = findByPrimaryKey( nKey );
        if ( questionToDelete != null )
        {
            EntryHome.remove( questionToDelete.getIdEntry( ) );
        }
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of a question whose identifier is specified in parameter
     * 
     * @param nKey
     *            The question primary key
     * @return an instance of Question
     */
    public static Question findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Returns all questions with the given technical code.
     * 
     * @param strCode
     *            The question code
     * @return an instance of Question
     */
    public static List<Question> findByCode( String strCode )
    {
        return _dao.loadByCode( strCode, _plugin );
    }

    /**
     * Returns all questions with the given technical code and entry.
     * 
     * @param strCode
     *            The question code
     * @return an instance of Question
     */
    public static Question findByCodeAndEntry( String strCode, int entryId )
    {
        return _dao.loadByCodeAndEntry( strCode, entryId, _plugin );
    }

    /**
     * Returns an instance of a question whose identifier is specified in parameter
     * 
     * @param keyList
     *            The questions primary keys
     * @return a list of Questions
     */
    public static List<Question> findByPrimaryKeyList( List<Integer> keyList )
    {
        return _dao.loadMultiple( keyList, _plugin );
    }

    /**
     * Load the data of all the question objects and returns them as a list
     * 
     * @return the list which contains the data of all the question objects
     */
    public static List<Question> getQuestionsList( )
    {
        return _dao.selectQuestionsList( _plugin );
    }

    /**
     * Load the data of all the question objects and returns them as a list
     * 
     * @return the list which contains the data of all the question objects
     */
    public static List<Question> getQuestionsListUncomplete( )
    {
        return _dao.selectQuestionsListUncomplete( _plugin );
    }

    /**
     * Load the data of all the question objects and returns them as a list
     * 
     * @param nIdStep
     *            The step primary key
     * @return the list which contains the data of all the question objects
     */
    public static List<Question> getQuestionsListByStep( int nIdStep )
    {
        return _dao.selectQuestionsListByStep( nIdStep, _plugin );
    }

    /**
     * Load the data of all the question objects for given form id and returns them as a list
     * 
     * @param nIdForm
     *            The id of the form
     * @return the list of all the question objects for the given form id
     */
    public static List<Question> getListQuestionByIdForm( int nIdForm )
    {
        return _dao.selectQuestionsListByFormId( nIdForm, _plugin );
    }

    /**
     * Load the data of all the question objects for given form id and returns them as a list
     * 
     * @param nIdForm
     *            The id of the form
     * @return the list of all the question objects for the given form id
     */
    public static List<Question> getListQuestionByIdFormUncomplete( int nIdForm )
    {
        return _dao.selectQuestionsListByFormIdUncomplete( nIdForm, _plugin );
    }

    /**
     * Load the id of all the question objects and returns them as a list
     * 
     * @return the list which contains the id of all the question objects
     */
    public static List<Integer> getIdQuestionsList( )
    {
        return _dao.selectIdQuestionsList( _plugin );
    }

    /**
     * Load the data of all the question objects and returns them as a referenceList
     * 
     * @return the referenceList which contains the data of all the question objects
     */
    public static ReferenceList getQuestionsReferenceList( )
    {
        return _dao.selectQuestionsReferenceList( _plugin );
    }

    /**
     * Load the data of all the question objects for the given form and returns them as a referenceList
     * 
     * @param nIdForm
     *            The form primary key
     * @return the referenceList which contains the data of all the question objects
     */
    public static ReferenceList getQuestionsReferenceListByForm( int nIdForm )
    {
        return _dao.selectQuestionsReferenceListByForm( nIdForm, _plugin );
    }

}
