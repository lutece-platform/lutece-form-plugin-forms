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

import java.util.List;

import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods (create, find, ...) for FormDisplay objects
 */
public final class FormQuestionResponseHome
{
    // Static variable pointed at the DAO instance
    private static IFormQuestionResponseDAO _dao = SpringContextService.getBean( "forms.formQuestionResponseDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "forms" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private FormQuestionResponseHome( )
    {
    }

    /**
     * Create an instance of the formQuestionResponse class
     * 
     * @param formQuestionResponse
     *            The instance of the FormQuestionResponse which contains the informations to store
     * @return The instance of formQuestionResponse which has been created with its primary key.
     */
    public static FormQuestionResponse create( FormQuestionResponse formQuestionResponse )
    {
        _dao.insert( formQuestionResponse, _plugin );

        return formQuestionResponse;
    }

    /**
     * Update of the formQuestionResponse which is specified in parameter
     * 
     * @param formQuestionResponse
     *            The instance of the FormQuestionResponse which contains the data to store
     * @return The instance of the formQuestionResponse which has been updated
     */
    public static FormQuestionResponse update( FormQuestionResponse formQuestionResponse )
    {
        _dao.store( formQuestionResponse, _plugin );

        return formQuestionResponse;
    }

    /**
     * Remove the formQuestionResponse whose identifier is specified in parameter
     * 
     * @param formQuestionResponse
     *            The formQuestionResponse to remove
     */
    public static void remove( FormQuestionResponse formQuestionResponse )
    {
        _dao.delete( formQuestionResponse, _plugin );
    }

    /**
     * Remove the formQuestionResponse related to a Question whose identifier is specified in parameter
     * 
     * @param nIdQuestion
     *            The form Question Id
     */
    public static void removeByQuestion( int nIdQuestion )
    {
        List<FormQuestionResponse> listFormQuestionResponse = findFormQuestionResponseByQuestion( nIdQuestion );

        for ( FormQuestionResponse formQuestionResponse : listFormQuestionResponse )
        {
            remove( formQuestionResponse );
        }
    }

    /**
     * Returns an instance of a formQuestionResponse whose identifier is specified in parameter
     * 
     * @param nKey
     *            The formQuestionResponse primary key
     * @return an instance of FormQuestionResponse
     */
    public static FormQuestionResponse findByPrimaryKey( int nKey )
    {
        FormQuestionResponse formQuestionResponse = _dao.load( nKey, _plugin );
        completeWithQuestion( formQuestionResponse );

        return formQuestionResponse;
    }

    /**
     * Completes the specified form question response with the question
     * 
     * @param formQuestionResponse
     *            the specified form question response
     */
    private static void completeWithQuestion( FormQuestionResponse formQuestionResponse )
    {
        if ( formQuestionResponse != null )
        {
            Question questionIncomplete = formQuestionResponse.getQuestion( );
            if ( questionIncomplete != null )
            {
                Question questionSaved = QuestionHome.findByPrimaryKey( questionIncomplete.getId( ) );
                if ( questionSaved != null )
                {
                    questionSaved.setIterationNumber( questionIncomplete.getIterationNumber( ) );
                    formQuestionResponse.setQuestion( questionSaved );
                }
            }
        }
    }

    /**
     * Load the data of all the formQuestionResponse objects and returns them as a list
     * 
     * @return the list which contains the data of all the formQuestionResponse objects
     */
    public static List<FormQuestionResponse> getFormQuestionResponseList( )
    {
        List<FormQuestionResponse> listFormQuestionResponse = _dao.selectFormQuestionResponseList( _plugin );
        completeWithQuestions( listFormQuestionResponse );

        return listFormQuestionResponse;
    }

    /**
     * Completes the specified list of form question responses with the questions
     * 
     * @param listFormQuestionResponse
     *            the list of form question responses
     */
    private static void completeWithQuestions( List<FormQuestionResponse> listFormQuestionResponse )
    {
        for ( FormQuestionResponse formQuestionResponse : listFormQuestionResponse )
        {
            completeWithQuestion( formQuestionResponse );
        }
    }

    /**
     * Load the data of all the formQuestionResponse objects and returns them as a list
     * 
     * @param nIdFormResponse
     *            The identifier of the FormResponse
     * @param nIdStep
     *            The identifier of the Step
     * @return the list which contains the data of all the formQuestionResponse objects
     */
    public static List<FormQuestionResponse> findQuestionsByStepAndFormResponse( int nIdFormResponse, int nIdStep )
    {
        List<FormQuestionResponse> listFormQuestionResponse = _dao.selectFormQuestionResponseListByStepAndFormResponse( nIdFormResponse, nIdStep, _plugin );
        completeWithQuestions( listFormQuestionResponse );

        return listFormQuestionResponse;
    }

    /**
     * Load the data of all the formQuestionResponse objects and returns them as a list
     * 
     * @param listFormResponseStep
     *            The list of FormResponse
     * @param nIdStep
     *            The identifier of the Step
     * @return the list which contains the data of all the formQuestionResponse objects
     */
    public static List<FormQuestionResponse> selectFormQuestionResponseListByListFormResponseStep( List<FormResponseStep> listFormResponseStep )
    {
        List<FormQuestionResponse> list = _dao.selectFormQuestionResponseListByListFormResponseStep( listFormResponseStep, _plugin );
        _dao.completeListWithEntryResponses( list, _plugin );
        return list;
    }

    /**
     * Load the data of all the formQuestionResponse objects for saving and returns them as a list
     * 
     * @param nIdFormResponse
     *            The identifier of the FormResponse
     * @return the list which contains the data of all the formQuestionResponse objects
     */
    public static List<FormQuestionResponse> getFormQuestionResponseListByFormResponse( int nIdFormResponse )
    {
        List<FormQuestionResponse> listFormQuestionResponse = _dao.selectFormQuestionResponseListByFormResponse( nIdFormResponse, _plugin );
        completeWithQuestions( listFormQuestionResponse );

        return listFormQuestionResponse;
    }

    /**
     * Retrieves the list of form question responses associated to the specified question.
     * 
     * @param nIdQuestion
     *            The identifier of the Question
     * @return the list of form question responses
     */
    public static List<FormQuestionResponse> findFormQuestionResponseByQuestion( int nIdQuestion )
    {
        List<FormQuestionResponse> listFormQuestionResponse = _dao.selectFormQuestionResponseByQuestion( nIdQuestion, _plugin );
        completeWithQuestions( listFormQuestionResponse );

        return listFormQuestionResponse;
    }

    /**
     * Retrieves the form question responses associated to the given form response for the specified question.
     * 
     * @param nIdFormResponse
     *            The identifier of the FormResponse
     * @param nIdQuestion
     *            The identifier of the Question
     * @return the found form question responses
     */
    public static List<FormQuestionResponse> findFormQuestionResponseByResponseQuestion( int nIdFormResponse, int nIdQuestion )
    {
        List<FormQuestionResponse> listFormQuestionResponse = _dao.selectFormQuestionResponseByResponseForQuestion( nIdFormResponse, nIdQuestion, _plugin );
        completeWithQuestions( listFormQuestionResponse );

        return listFormQuestionResponse;
    }

    /**
     * Retrieves the form question responses associated to the given Entry Response.
     * 
     * @param response
     * @param plugin
     * @return
     */
    public static FormQuestionResponse selectFormQuestionResponseByEntryResponse( Response response )
    {
        FormQuestionResponse res = _dao.selectFormQuestionResponseByEntryResponse( response, _plugin );
        completeWithQuestion( res );
        return res;
    }
}
