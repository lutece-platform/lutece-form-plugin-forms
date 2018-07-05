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
package fr.paris.lutece.plugins.forms.service;

import java.util.List;

import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.web.CompositeGroupDisplay;
import fr.paris.lutece.plugins.forms.web.CompositeQuestionDisplay;
import fr.paris.lutece.plugins.forms.web.StepDisplayTree;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.plugins.forms.web.ICompositeDisplay;
import fr.paris.lutece.plugins.forms.web.IEntryDataService;

/**
 * This is the service class related to the form
 */
public final class FormService
{

    public static final String BEAN_NAME = "forms.formService";

    /**
     * Constructor
     */
    private FormService( )
    {
    }
    /**
     * Save the FormResponse instance
     * 
     * @param formResponse
     *            The formResponse to save
     */
    public void saveForm( FormResponse formResponse )
    {
        FormResponseHome.create( formResponse );

        for ( FormQuestionResponse formQuestionResponse : formResponse.getListResponses( ) )
        {
            Question question = QuestionHome.findByPrimaryKey( formQuestionResponse.getIdQuestion( ) );
            IEntryDataService dataService = EntryServiceManager.getInstance( ).getEntryDataService( question.getEntry( ).getEntryType( ) );
            formQuestionResponse.setIdFormResponse( formResponse.getId( ) );
            dataService.saveFormQuestionResponse( formQuestionResponse );
        }

    }

    /**
     * Get the full children composite list of the given step
     * 
     * @param nIdStep
     *            The step primary key
     * @return the Html of the given step
     */
    public List<ICompositeDisplay> getStepCompositeList( int nIdStep )
    {
        StepDisplayTree displayTree = new StepDisplayTree( nIdStep );

        return displayTree.getCompositeList( );
    }

    /**
     * Get the right composite from the given formDisplay
     * 
     * @param formDisplay
     *            The formDisplay
     * @return the right composite
     */
    public ICompositeDisplay formDisplayToComposite( FormDisplay formDisplay )
    {
        ICompositeDisplay composite = null;
        if ( FormsConstants.COMPOSITE_GROUP_TYPE.equals( formDisplay.getCompositeType( ) ) )
        {
            composite = new CompositeGroupDisplay( );
            composite.setFormDisplay( formDisplay );

        }
        else
            if ( FormsConstants.COMPOSITE_QUESTION_TYPE.equals( formDisplay.getCompositeType( ) ) )
            {
                composite = new CompositeQuestionDisplay( );
                composite.setFormDisplay( formDisplay );
            }

        return composite;
    }

    /**
     * Remove a given Form, all its steps and composites, workflow resources.
     * Also remove all the related formResponses, QuestionsResposes, EntryResponses and entries.
     * 
     * @param nIdForm
     *            The identifier of the form to be deleted
     */
    public void removeForm( int nIdForm )
    {
        StepService stepService = SpringContextService.getBean( StepService.BEAN_NAME );
        
        List<Step> listStep = StepHome.getStepsListByForm( nIdForm );
        
        for ( Step step : listStep )
        {
            stepService.removeStep( step.getId( ) );
        }
        
        FormResponseHome.removeByForm( nIdForm );
        FormHome.remove( nIdForm );
        
        //TODO: remove workflow resources linked to this form

    }
}
