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
package fr.paris.lutece.plugins.forms.web;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.ControlType;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponseStep;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.service.EntryServiceManager;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.validation.IValidator;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;

/**
 * 
 * Class for breadcrumb management and responses history
 *
 */
public class FormResponseManager
{
    private final List<Step> _listValidatedStep;
    private final FormResponse _formResponse;
    private boolean _bIsResponseLoadedFromBackup = false;


    /**
     * Constructor
     * 
     * @param form
     *            the form
     */
    public FormResponseManager( Form form )
    {
        _listValidatedStep = new ArrayList<>( );
        _formResponse = new FormResponse( );
        _formResponse.setFormId( form.getId( ) );
        _formResponse.setSteps( new ArrayList<>( ) );
    }

    /**
     * Constructor
     * 
     * @param formResponse
     *            the form response
     */
    public FormResponseManager( FormResponse formResponse )
    {
        _listValidatedStep = new ArrayList<>( );
        _formResponse = formResponse;

        initValidatedStep( );
    }

    /**
     * Initializes the validated steps
     */
    private void initValidatedStep( )
    {
        for ( FormResponseStep formResponseStep : _formResponse.getSteps( ) )
        {
            int nStepOrder = formResponseStep.getOrder( );

            if ( nStepOrder != FormsConstants.ORDER_NOT_SET )
            {
                _listValidatedStep.add( nStepOrder, formResponseStep.getStep( ) );
            }
            for ( FormQuestionResponse formQuestionResponse : formResponseStep.getQuestions( ) )
            {
                if ( CollectionUtils.isNotEmpty( formQuestionResponse.getEntryResponse( ) ) )
                {
                    formQuestionResponse.getQuestion( ).setIsVisible( true );
                }
            }
        }
    }

    /**
     * Gives the current step
     * 
     * @return the current step
     */
    public Step getCurrentStep( )
    {
        Step step = null;

        if ( !_listValidatedStep.isEmpty( ) )
        {
            step = _listValidatedStep.get( _listValidatedStep.size( ) - 1 );
        }

        return step;
    }

    /**
     * Gives the form response
     * 
     * @return the form response
     */
    public FormResponse getFormResponse( )
    {
        initStepsOrder( );

        return _formResponse;
    }
    
    /**
     * Return the form Response update date
     * 
     * @return
     */
    public Timestamp getFormResponseUpdateDate()
    {
    	FormResponse formResponse = getFormResponse();
    	Timestamp updateDate = formResponse.getUpdate();
    	if (updateDate == null)
    	{
    		FormResponse formResponseFromDB = FormResponseHome.findUncompleteByPrimaryKey(formResponse.getId());
    		updateDate = formResponseFromDB != null ? formResponseFromDB.getUpdate() : null;
    	}
    	return updateDate;
    }

    public Boolean getIsResponseLoadedFromBackup () {
    	return _bIsResponseLoadedFromBackup;
    }

    public void setIsResponseLoadedFromBackup (Boolean bIsResponseLoadedFromBackup) {
    	_bIsResponseLoadedFromBackup = bIsResponseLoadedFromBackup;
    }
    /**
     * Initializes the steps order
     */
    private void initStepsOrder( )
    {
        for ( FormResponseStep formResponseStep : _formResponse.getSteps( ) )
        {
            formResponseStep.setOrder( FormsConstants.ORDER_NOT_SET );
        }

        for ( int i = 0; i < _listValidatedStep.size( ); i++ )
        {
            Step step = _listValidatedStep.get( i );

            for ( FormResponseStep formResponseStep : _formResponse.getSteps( ) )
            {
                if ( formResponseStep.getStep( ).getId( ) == step.getId( ) )
                {
                    formResponseStep.setOrder( i );
                    break;
                }
            }
        }
    }

    /**
     * Adds the specified step
     * 
     * @param step
     *            the step to add
     */
    public void add( Step step )
    {
        if ( isStepValidated( step ) )
        {
            throw new IllegalStateException( "The step is already validated !" );
        }

        _listValidatedStep.add( step );

        FormResponseStep formResponseStep = findFormResponseStepFor( step );

        if ( formResponseStep == null )
        {
            _formResponse.getSteps( ).add( createFormResponseStepFrom( step ) );
        }
    }

    /**
     * Tests if the specified step is validated or not
     * 
     * @param step
     *            the step
     * @return {@code true} if the step is validated, {@code false} otherwise
     */
    private boolean isStepValidated( Step step )
    {
        return _listValidatedStep.stream( ).anyMatch( stepValidate -> stepValidate.getId( ) == step.getId( ) );
    }

    /**
     * Creates a form response step from the specified step
     * 
     * @param step
     *            the step
     * @return the created form response step
     */
    private FormResponseStep createFormResponseStepFrom( Step step )
    {
        FormResponseStep formResponseStep = new FormResponseStep( );
        formResponseStep.setFormResponseId( _formResponse.getId( ) );
        formResponseStep.setQuestions( new ArrayList<>( ) );
        formResponseStep.setStep( step );

        return formResponseStep;
    }

    /**
     * Goes to the step of the specified index
     * 
     * @param nStepIndex
     *            the step index
     * @return the Step
     */
    public Step goTo( int nStepIndex )
    {
        if ( nStepIndex + 1 < _listValidatedStep.size( ) )
        {
            _listValidatedStep.subList( nStepIndex + 1, _listValidatedStep.size( ) ).clear( );
        }

        return getCurrentStep( );
    }

    /**
     * Finds the responses for the specified step
     * 
     * @param step
     *            the step
     * @return the found responses
     */
    public List<FormQuestionResponse> findResponsesFor( Step step )
    {
        List<FormQuestionResponse> listFormQuestionResponse = new ArrayList<>( );

        if ( isStepValidated( step ) )
        {
            FormResponseStep formResponseStep = findFormResponseStepFor( step );
            if ( formResponseStep != null )
            {
                listFormQuestionResponse = formResponseStep.getQuestions( );
            }
        }

        return listFormQuestionResponse;
    }

    /**
     * Finds all responses
     *
     * @return the found responses
     */
    public List<FormQuestionResponse> findAllResponses( )
    {
        return _formResponse.getSteps( ).stream( ).flatMap( step -> step.getQuestions( ).stream( ) ).collect( Collectors.toList( ) );
    }

    /**
     * Finds the form response step for the specified step
     * 
     * @param step
     *            the step
     * @return the found form response step
     */
    private FormResponseStep findFormResponseStepFor( Step step )
    {
        FormResponseStep formResponseStepResult = null;

        for ( FormResponseStep formResponseStep : _formResponse.getSteps( ) )
        {
            if ( formResponseStep.getStep( ).getId( ) == step.getId( ) )
            {
                formResponseStepResult = formResponseStep;
                break;
            }
        }

        return formResponseStepResult;
    }

    /**
     * Adds the specified responses
     * 
     * @param listFormQuestionResponse
     *            the responses to add
     */
    public void addResponses( List<FormQuestionResponse> listFormQuestionResponse )
    {
        FormResponseStep formResponseStep = findFormResponseStepFor( getCurrentStep( ) );

        if ( formResponseStep != null )
        {
            formResponseStep.setQuestions( listFormQuestionResponse );
        }
    }

    /**
     * Pops the last step
     * 
     * @return the last step, or {@code null} if there no step to pop
     */
    public Step popStep( )
    {
        Step step = getCurrentStep( );

        if ( !_listValidatedStep.isEmpty( ) )
        {
            _listValidatedStep.remove( _listValidatedStep.size( ) - 1 );
        }

        return step;
    }

    /**
     * Gives the validated steps
     * 
     * @return the validated steps
     */
    public List<Step> getValidatedSteps( )
    {
        List<Step> listStep = new ArrayList<>( _listValidatedStep.size( ) );

        for ( Step step : _listValidatedStep )
        {
            listStep.add( step );
        }

        return listStep;
    }

    /**
     * 
     * @return the form validation result
     */
    public boolean validateFormResponses( )
    {
        for ( Step step : _listValidatedStep )
        {
            List<FormQuestionResponse> listFormQuestionResponse = findResponsesFor( step );

            for ( FormQuestionResponse formQuestionResponse : listFormQuestionResponse )
            {
                if ( formQuestionResponse.getQuestion( ).isVisible( ) )
                {
                    List<Control> listControl = ControlHome.getControlByQuestionAndType( formQuestionResponse.getQuestion( ).getId( ),
                            ControlType.VALIDATION.getLabel( ) );

                    for ( Control control : listControl )
                    {
                        IValidator validator = EntryServiceManager.getInstance( ).getValidator( control.getValidatorName( ) );

                        if ( !validator.validate( formQuestionResponse, control ) )
                        {
                            GenericAttributeError error = new GenericAttributeError( );

                            error.setIsDisplayableError( true );
                            error.setErrorMessage( control.getErrorMessage( ) );

                            formQuestionResponse.setError( error );

                            goTo( _listValidatedStep.indexOf( step ) );

                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

}
