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
import java.util.stream.Collectors;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.resource.ExtendableResourceRemovalListenerService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods (create, find, ...) for FormResponseHome objects
 */
public final class FormResponseHome
{
    // Static variable pointed at the DAO instance
    private static IFormResponseDAO _dao = SpringContextService.getBean( "forms.formResponseDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "forms" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private FormResponseHome( )
    {
    }

    /**
     * Create an instance of the formResponse class
     * 
     * @param formResponse
     *            The instance of the FormResponse which contains the informations to store
     * @return The instance of formResponse which has been created with its primary key.
     */
    public static FormResponse create( FormResponse formResponse )
    {
        _dao.insert( formResponse, _plugin );

        return formResponse;
    }

    /**
     * Update of the formResponse which is specified in parameter
     * 
     * @param formResponse
     *            The instance of the FormResponse which contains the data to store
     * @return The instance of the formResponse which has been updated
     */
    public static FormResponse update( FormResponse formResponse )
    {
        _dao.store( formResponse, _plugin );

        return formResponse;
    }

    /**
     * Remove the formResponse whose identifier is specified in parameter
     * 
     * @param nKey
     *            The formResponse Id
     */
    public static void remove( int nKey )
    {
        FormResponse formResponse = _dao.load( nKey, _plugin );
        _dao.delete( nKey, _plugin );
        if ( formResponse != null )
        {
            ExtendableResourceRemovalListenerService.doRemoveResourceExtentions( FormResponse.RESOURCE_TYPE + "_" + formResponse.getFormId( ),
                    Integer.toString( formResponse.getId( ) ) );
        }
    }

    /**
     * Returns an instance of a formResponse whose identifier is specified in parameter
     * 
     * @param nKey
     *            The formResponse primary key
     * @return an instance of FormResponse
     */
    public static FormResponse findByPrimaryKey( int nKey )
    {
        FormResponse formResponse = _dao.load( nKey, _plugin );
        completeWithSteps( formResponse );

        return formResponse;
    }

    /**
     * Returns an instance of a formResponse whose identifier is specified in parameter<br />
     * Doesn't load the list of {@link FormResponseStep}
     * 
     * @param nKey
     *            The formResponse primary key
     * @return an instance of FormResponse
     */
    public static FormResponse loadById( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Returns an instance of a formResponse whose identifier is specified in parameter
     * 
     * @param nKey
     *            The formResponse primary key
     * @return an instance of FormResponse
     */
    public static FormResponse findByPrimaryKeyForIndex( int nKey )
    {
        // FormResponse
        FormResponse formResponse = _dao.load( nKey, _plugin );
        if ( formResponse.isFromSave( ) )
        {
            return null;
        }

        // FormResponseStep
        List<FormResponseStep> formResponseStepList = FormResponseStepHome.findStepsByFormResponsePartial( formResponse.getId( ) );
        formResponse.setSteps( formResponseStepList );

        // FormQuestionResponse
        List<FormQuestionResponse> formQuestionResponseList = FormQuestionResponseHome
                .selectFormQuestionResponseListByListFormResponseStep( formResponseStepList );

        // Questions
        List<Question> questionList = QuestionHome.findByPrimaryKeyList( formQuestionResponseList.stream( ).map( FormQuestionResponse::getQuestion )
                .map( Question::getId ).distinct( ).collect( Collectors.toList( ) ) );
        List<Entry> entryList = EntryHome
                .findByPrimaryKeyList( questionList.stream( ).map( Question::getIdEntry ).distinct( ).collect( Collectors.toList( ) ) );
        List<Field> fieldList = FieldHome.getFieldListByListIdEntry( entryList.stream( ).map( Entry::getIdEntry ).collect( Collectors.toList( ) ) );

        for ( Entry entry : entryList )
        {
            entry.setFields(
                    fieldList.stream( ).filter( ( Field f ) -> f.getParentEntry( ).getIdEntry( ) == entry.getIdEntry( ) ).collect( Collectors.toList( ) ) );
        }
        for ( Question question : questionList )
        {
            question.setEntry( entryList.stream( ).filter( entry -> entry.getIdEntry( ) == question.getIdEntry( ) ).findFirst( ).orElse( null ) );
        }

        // Populate FormQuestionResponse
        for ( FormQuestionResponse fqr : formQuestionResponseList )
        {
            fqr.setQuestion( questionList.stream( ).filter( q -> q.getId( ) == fqr.getQuestion( ).getId( ) ).findFirst( ).orElse( null ) );

            for ( Response resp : fqr.getEntryResponse( ) )
            {
                if ( resp.getField( ) != null )
                {
                    resp.setField( fieldList.stream( ).filter( ( Field f ) -> f.getIdField( ) == resp.getField( ).getIdField( ) ).findFirst( ).orElse( null ) );
                }
            }
        }

        // Populate FormResponseStep
        for ( FormResponseStep formQuestionStep : formResponseStepList )
        {
            formQuestionStep.setQuestions( formQuestionResponseList.stream( ).filter( fqr -> formQuestionStep.getStep( ).getId( ) == fqr.getIdStep( ) )
                    .collect( Collectors.toList( ) ) );
        }
        return formResponse;
    }

    /**
     * Returns an instance of a uncomplete (without steps) formResponse whose identifier is specified in parameter
     * 
     * @param nKey
     *            The formResponse primary key
     * @return an instance of FormResponse
     */
    public static FormResponse findUncompleteByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Returns all the formResponse objects, completed with the steps
     * 
     * @return all the formResponse objects completed with the steps
     */
    public static List<FormResponse> selectAllFormResponses( )
    {
        List<FormResponse> listFormResponses = _dao.selectFormResponseList( _plugin );
        for ( FormResponse formResponse : listFormResponses )
        {
            completeWithSteps( formResponse );
        }
        return listFormResponses;
    }

    /**
     * Returns all the formResponse ids
     * 
     * @return all the formResponse ids
     */
    public static List<Integer> selectAllFormResponsesId( )
    {
        return _dao.selectAllFormResponsesId( _plugin );
    }

    /**
     * Returns all the formResponse objects, completed with the steps
     * 
     * @param nIdForm
     *            the id form
     * @return all the formResponse objects completed with the steps
     */
    public static List<FormResponse> selectAllFormResponsesUncompleteByIdForm( int nIdForm )
    {
        return _dao.selectFormResponseListUncompleteByIdForm( nIdForm, _plugin );
    }

    /**
     * Completes the specified form response with the steps
     * 
     * @param formResponse
     *            the form response
     */
    private static void completeWithSteps( FormResponse formResponse )
    {
        if ( formResponse != null )
        {
            formResponse.setSteps( FormResponseStepHome.findStepsByFormResponse( formResponse.getId( ) ) );
        }
    }

    /**
     * Load the data of all the formResponse objects and returns them as a list
     * 
     * @param strGuid
     *            The user Guid
     * @param nIdForm
     *            The form primary key
     * @param fromBackup
     * @return the formResponse objects
     */
    public static List<FormResponse> getFormResponseByGuidAndForm( String strGuid, int nIdForm, boolean fromBackup )
    {
        List<FormResponse> listFormResponse = _dao.selectFormResponseByUser( strGuid, nIdForm, fromBackup, _plugin );
        for ( FormResponse formResponse : listFormResponse )
        {
            completeWithSteps( formResponse );
        }
        return listFormResponse;
    }

    /**
     * Load the data of all the formResponse objects and returns them as a list
     * 
     * @param strGuid
     *            The user Guid
     * @return the formResponse objects
     */
    public static List<FormResponse> getFormResponseByGuid( String strGuid )
    {
        List<FormResponse> listFormResponse = _dao.selectAllCompletedFormResponseByUser( strGuid, _plugin );
        for ( FormResponse formResponse : listFormResponse )
        {
            completeWithSteps( formResponse );
        }
        return listFormResponse;
    }

    /**
     * Load the data of all formResponses Objects for a given list of formResponses identifiers
     * 
     * @param listIdFormResponse
     *            The list of formResponses identifiers
     * @return the formResponse objects
     */
    public static List<FormResponse> getFormResponseUncompleteByPrimaryKeyList( List<Integer> listIdFormResponse )
    {
        return _dao.selectFormResponseByPrimaryKeyList( listIdFormResponse, _plugin );
    }

    /**
     * Remove all the formResponse linked to a given Form
     * 
     * @param nIdForm
     *            The form Identifier
     */
    public static void removeByForm( int nIdForm )
    {
        _dao.deleteByForm( nIdForm, _plugin );

    }

}
