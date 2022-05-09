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

import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods (create, find, ...) for FormResponseStep objects
 */
public final class FormResponseStepHome
{
    // Static variable pointed at the DAO instance
    private static IFormResponseStepDAO _dao = SpringContextService.getBean( "forms.formResponseStepDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "forms" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private FormResponseStepHome( )
    {
    }

    /**
     * Create an instance of the formResponseStep class
     * 
     * @param formResponseStep
     *            The instance of the FormResponseStep which contains the informations to store
     * @return The instance of formResponseStep which has been created with its primary key.
     */
    public static FormResponseStep create( FormResponseStep formResponseStep )
    {
        _dao.insert( formResponseStep, _plugin );

        return formResponseStep;
    }

    /**
     * Update of the formResponseStep which is specified in parameter
     * 
     * @param formResponseStep
     *            The instance of the FormResponseStep which contains the data to store
     * @return The instance of the formResponseStep which has been updated
     */
    public static FormResponseStep update( FormResponseStep formResponseStep )
    {
        _dao.store( formResponseStep, _plugin );

        return formResponseStep;
    }

    /**
     * Remove the formResponseStep whose identifier is specified in parameter
     * 
     * @param nKey
     *            The formResponseStep Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of a formResponseStep whose identifier is specified in parameter
     * 
     * @param nKey
     *            The formResponseStep primary key
     * @return an instance of FormResponseStep
     */
    public static FormResponseStep findByPrimaryKey( int nKey )
    {
        FormResponseStep formResponseStep = _dao.load( nKey, _plugin );
        completeWithStep( formResponseStep );
        completeWithQuestionResponse( formResponseStep );

        return formResponseStep;
    }

    /**
     * Completes the specified form response step with the step
     * 
     * @param formResponseStep
     *            the form response step
     */
    private static void completeWithStep( FormResponseStep formResponseStep )
    {
        if ( formResponseStep != null )
        {
            formResponseStep.setStep( StepHome.findByPrimaryKey( formResponseStep.getStep( ).getId( ) ) );
        }
    }

    /**
     * Completes the specified form response step with the questions containing responses
     * 
     * @param formResponseStep
     *            the form response step
     */
    private static void completeWithQuestionResponse( FormResponseStep formResponseStep )
    {
        if ( formResponseStep != null )
        {
            formResponseStep.setQuestions( FormQuestionResponseHome.findQuestionsByStepAndFormResponse( formResponseStep.getFormResponseId( ),
                    formResponseStep.getStep( ).getId( ) ) );
        }
    }

    /**
     * Completes the specified list of form response steps with the step
     * 
     * @param listFormResponseStep
     *            the list of form response steps
     */
    private static void completeWithSteps( List<FormResponseStep> listFormResponseStep )
    {
        for ( FormResponseStep formResponseStep : listFormResponseStep )
        {
            completeWithStep( formResponseStep );
        }
    }

    /**
     * Completes the specified list of form response steps with the questions containing responses
     * 
     * @param listFormResponseStep
     *            the list of form response steps
     */
    private static void completeWithQuestionResponses( List<FormResponseStep> listFormResponseStep )
    {
        for ( FormResponseStep formResponseStep : listFormResponseStep )
        {
            completeWithQuestionResponse( formResponseStep );
        }
    }

    /**
     * Remove all the formResponse linked to a given Form
     * 
     * @param nIdFormResponse
     *            The form Identifier
     */
    public static void removeByFormResponse( int nIdFormResponse )
    {
        _dao.deleteByFormResponse( nIdFormResponse, _plugin );
    }

    /**
     * Removes all the formResponse linked to a given step
     * 
     * @param nIdStep
     *            The step id
     */
    public static void removeByStep( int nIdStep )
    {
        List<Question> listQuestion = QuestionHome.getQuestionsListByStep( nIdStep );

        for ( Question question : listQuestion )
        {
            FormQuestionResponseHome.removeByQuestion( question.getId( ) );
        }

        _dao.deleteByStep( nIdStep, _plugin );
    }

    /**
     * Load the order step id list by FormResponse id
     * 
     * @param nIdFormResponse
     *            The form Identifier
     * @return the list which contains the data of all the formResponseStep objects
     */
    public static List<FormResponseStep> findStepsByFormResponse( int nIdFormResponse )
    {
        List<FormResponseStep> listFormResponseStep = _dao.selectFormResponseStepsByFormResponse( nIdFormResponse, _plugin );
        completeWithSteps( listFormResponseStep );
        completeWithQuestionResponses( listFormResponseStep );

        return listFormResponseStep;
    }

    /**
     * Load the order step id list by FormResponse id <br />
     * Doesn't load Steps and FormQuestionResponses
     * 
     * @param nIdFormResponse
     *            The form Identifier
     * @return the list which contains the data of all the formResponseStep objects
     */
    public static List<FormResponseStep> findStepsByFormResponsePartial( int nIdFormResponse )
    {
        return _dao.selectFormResponseStepsByFormResponse( nIdFormResponse, _plugin );
    }
}
