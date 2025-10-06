/*
 * Copyright (c) 2002-2025, City of Paris
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.paris.lutece.plugins.forms.service;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.ControlType;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.business.Transition;
import fr.paris.lutece.plugins.forms.business.TransitionHome;
import fr.paris.lutece.plugins.forms.validation.IValidator;
import fr.paris.lutece.plugins.forms.web.ICompositeDisplay;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 *
 * @author seboo
 */
public class FormGraphExportService 
{

    private static final String KEY_GRAPH = "flowchart LR";
    private static final String KEY_STEP_LABEL_START = "[\"";
    private static final String KEY_STEP_LABEL_END = "\"]";
    private static final String KEY_TRANSITION_LABEL_START = "{{\"";
    private static final String KEY_TRANSITION_LABEL_END = "\"}}";
    private static final String KEY_ASSIGN_ACTIONS_START = " --> |\"";
    private static final String KEY_ASSIGN_ACTIONS_END = " \"| ";
    private static final String KEY_ASSIGN_ALTERNATIVE_ACTION_PREFIX = "fa:fa-ban ";
    private static final String KEY_ASSIGN_DEFAULT_ACTION_PREFIX = "fa:fa-check ";
    private static final String KEY_NEWLINE = "<br /> ";
    private static final String KEY_CLICK = "click ";
    private static final String KEY_AUTOMATIC_ACTION_SYMBOL = "  fa:fa-bolt";
    private static final String KEY_STEP_END_SYMBOL = "((fa:fa-floppy-disk))";
    private static final String STEP_URL_PART = "/jsp/admin/plugins/forms/ManageQuestions.jsp?id_step=";
    private static final String TRANSITION_URL_PART = "/jsp/admin/plugins/forms/ManageTransitions.jsp?view=manageTransitions&id_step=";
    private static final String NEWLINE = "\n";

    private static final FormService _formService = SpringContextService.getBean( FormService.BEAN_NAME );
    
    /**
     * Export form as Mermaid MD graph
     * 
     * @param wf
     * @param locale
     * @return the markdown definition of the form
     */
    public static String generate( Form form, String strBaseUrl )
    {
        StringBuilder sb = new StringBuilder( KEY_GRAPH ).append( NEWLINE );

        List<Step> listSteps = StepHome.getStepsListByForm( form.getId( ) );
        List<Transition> listTransitions = TransitionHome.getTransitionsListFromForm( form.getId( ) );

        for ( Transition transition : listTransitions )
        {
            transition.setConditional(
                    CollectionUtils.isNotEmpty( ControlHome.getControlByControlTargetAndType( transition.getId( ), ControlType.TRANSITION ) ) );
        }

        listSteps = StepService.sortStepsWithTransitions( listSteps, listTransitions );

        // add final virtual step
        sb.append ( "F").append ( KEY_STEP_END_SYMBOL).append ( NEWLINE);
        
        // list steps
        for ( Step step : listSteps )
        {
            sb.append("S").append( step.getId( ) ).append( KEY_STEP_LABEL_START )
            	.append ("[")
            	.append( step.getTitle( ).replaceAll( "\"", "'" ) )
            	.append ("]"); 
            
            // list questions
            List<ICompositeDisplay> listICompositeDisplay = _formService.getStepCompositeList( step.getId( ) );
            for ( ICompositeDisplay cd : listICompositeDisplay )
            {
            	sb.append( NEWLINE ).append( cd.getTitle( ) );
            }
            
            sb.append( KEY_STEP_LABEL_END )
                    .append( NEWLINE );
            
            // link to final step if final
            if (step.isFinal ( ) )
            {
        	sb.append("S").append( step.getId( ) )
        		.append( KEY_ASSIGN_ACTIONS_START ).append( KEY_ASSIGN_ACTIONS_END )
        		.append ( "F" )
        		.append( NEWLINE );
            }
            
            // add urls
            if ( strBaseUrl != null )
            {
                sb.append( KEY_CLICK ).append( "S" ).append( step.getId( ) )
                	.append( " \"" ).append( strBaseUrl ).append( STEP_URL_PART )
                	.append( step.getId( ) ).append( "\"" ).append( NEWLINE );
            } 
        }

        
        
        // list transitions
        for ( Transition trans : listTransitions )
        {
            // from step
            sb.append("S").append( trans.getFromStep( ) )
        	.append( KEY_ASSIGN_ACTIONS_START ).append( KEY_ASSIGN_ACTIONS_END )
    		.append("T").append( trans.getId( ) ).append( KEY_TRANSITION_LABEL_START ).append("P").append( trans.getPriority( ) );
            
            // controls
            List<Control> listControl = ControlHome.getControlByControlTargetAndType( trans.getId( ), ControlType.TRANSITION );
            for ( Control control : listControl )
            {
        	// validator
        	IValidator validator = EntryServiceManager.getInstance( ).getValidator( control.getValidatorName( ) );
        	sb.append ( NEWLINE ).append ( validator.getValidatorDisplayName ( ) ).append( " : ").append ( control.getValue ( ) );
            }
            
            sb.append( KEY_TRANSITION_LABEL_END )
		.append( NEWLINE );
            
            
            // next step
            sb.append("T").append( trans.getId( ) )
            	.append( KEY_ASSIGN_ACTIONS_START ).append( KEY_ASSIGN_ACTIONS_END )
            	.append("S").append( trans.getNextStep( ) )
    		.append( NEWLINE );
            
            // add urls
            if ( strBaseUrl != null )
            {
                sb.append( KEY_CLICK ).append( "T" ).append( trans.getId( ) )
                	.append( " \"" ).append( strBaseUrl ).append( TRANSITION_URL_PART )
                	.append( trans.getFromStep( ) ).append( "\"" ).append( NEWLINE );
            } 

        }
        
        
       

        return sb.toString( );
    }

   
}
