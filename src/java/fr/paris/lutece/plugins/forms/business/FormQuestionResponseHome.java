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

import java.util.List;

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
     * @param nKey
     *            The formQuestionResponse Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Remove the formQuestionResponse related to a Question whose identifier is specified in parameter
     * 
     * @param nIdQuestion
     *            The form Question Id
     */
    public static void removeByQuestion( int nIdQuestion )
    {
        _dao.deleteByQuestion( nIdQuestion, _plugin );
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
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the formQuestionResponse objects and returns them as a list
     * 
     * @return the list which contains the data of all the formQuestionResponse objects
     */
    public static List<FormQuestionResponse> getFormQuestionResponseList( )
    {
        return _dao.selectFormQuestionResponseList( _plugin );
    }
    
    /**
     * Retrieve all the FormQuestionResponse associated to the given FormResponse for the specified Question.
     * Return an empty list if there is no FormQuestionResponse associated to the given parameters.
     * 
     * @param nIdFormResponse
     *          The identifier of the FormResponse
     * @param nIdQuestion
     *          The identifier of the Question
     * @return the list of all FormQuestionResponse associated to the given formResponse and Question, return an empty list if there is no result.
     */
    public static List<FormQuestionResponse> getFormQuestionResponseListByResponseQuestion( int nIdFormResponse, int nIdQuestion )
    {
        return _dao.selectFormQuestionResponseListByResponseForQuestion( nIdFormResponse, nIdQuestion, _plugin );
    }
}
