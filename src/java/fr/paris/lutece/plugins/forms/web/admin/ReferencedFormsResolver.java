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
package fr.paris.lutece.plugins.forms.web.admin;

import java.util.HashSet;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.ControlType;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.Group;
import fr.paris.lutece.plugins.forms.business.GroupHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.business.Transition;
import fr.paris.lutece.plugins.forms.business.TransitionHome;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;

/**
 * Finds the forms referenced by the parameters of a request of the back office form designer (form, step, question, group, display, entry, transition,
 * control), so that the controller can check the rights of the user on each of them whatever the identifier it reads
 */
public final class ReferencedFormsResolver
{
    private static final String [ ] PARAMETERS_TRANSITION_STEP = {
            "fromStep", "from_step", "nextStep", "next_step"
    };

    /**
     * Private constructor
     */
    private ReferencedFormsResolver( )
    {
    }

    /**
     * Get the identifiers of the forms referenced by the parameters of the request. An identifier that is not a number or that matches no resource is
     * ignored: the controller handles it.
     * 
     * @param request
     *            the request
     * @return the identifiers of the forms
     */
    public static Set<Integer> getReferencedFormIds( HttpServletRequest request )
    {
        Set<Integer> setIdForm = new HashSet<>( );

        int nIdForm = getId( request, FormsConstants.PARAMETER_ID_FORM );
        if ( nIdForm > 0 )
        {
            setIdForm.add( nIdForm );
        }
        addFormOfStep( setIdForm, getId( request, FormsConstants.PARAMETER_ID_STEP ) );
        // Steps of a transition, populated from the request
        for ( String strParameter : PARAMETERS_TRANSITION_STEP )
        {
            addFormOfStep( setIdForm, getId( request, strParameter ) );
        }
        addFormOfQuestion( setIdForm, getId( request, FormsConstants.PARAMETER_ID_QUESTION ) );
        addFormOfGroup( setIdForm, getId( request, FormsConstants.PARAMETER_ID_GROUP ) );
        addFormOfDisplay( setIdForm, getId( request, FormsConstants.PARAMETER_ID_DISPLAY ) );
        addFormOfDisplay( setIdForm, getId( request, FormsConstants.PARAMETER_ID_PARENT ) );
        addFormOfEntry( setIdForm, getId( request, FormsConstants.PARAMETER_ID_ENTRY ) );
        addFormOfTransition( setIdForm, getId( request, FormsConstants.PARAMETER_ID_TRANSITION ) );
        addFormOfControl( setIdForm, getId( request, FormsConstants.PARAMETER_ID_CONTROL ) );

        // The target is a transition, a display or a question depending on the type of control (a question outside the controls)
        int nIdTarget = getId( request, FormsConstants.PARAMETER_ID_TARGET );
        String strControlType = request.getParameter( FormsConstants.PARAMETER_CONTROL_TYPE );
        if ( ControlType.TRANSITION.name( ).equals( strControlType ) )
        {
            addFormOfTransition( setIdForm, nIdTarget );
        }
        else
            if ( ControlType.CONDITIONAL.name( ).equals( strControlType ) )
            {
                addFormOfDisplay( setIdForm, nIdTarget );
            }
            else
            {
                addFormOfQuestion( setIdForm, nIdTarget );
            }

        return setIdForm;
    }

    /**
     * Get a positive identifier from a request parameter
     * 
     * @param request
     *            the request
     * @param strParameter
     *            the parameter
     * @return the identifier, or -1 if the parameter is missing or not a number
     */
    private static int getId( HttpServletRequest request, String strParameter )
    {
        return NumberUtils.toInt( request.getParameter( strParameter ), NumberUtils.INTEGER_MINUS_ONE );
    }

    private static void addFormOfStep( Set<Integer> setIdForm, int nIdStep )
    {
        Step step = ( nIdStep > 0 ) ? StepHome.findByPrimaryKey( nIdStep ) : null;
        if ( step != null )
        {
            setIdForm.add( step.getIdForm( ) );
        }
    }

    private static void addFormOfQuestion( Set<Integer> setIdForm, int nIdQuestion )
    {
        Question question = ( nIdQuestion > 0 ) ? QuestionHome.findByPrimaryKey( nIdQuestion ) : null;
        if ( question != null )
        {
            addFormOfStep( setIdForm, question.getIdStep( ) );
        }
    }

    private static void addFormOfGroup( Set<Integer> setIdForm, int nIdGroup )
    {
        Group group = ( nIdGroup > 0 ) ? GroupHome.findByPrimaryKey( nIdGroup ) : null;
        if ( group != null )
        {
            addFormOfStep( setIdForm, group.getIdStep( ) );
        }
    }

    private static void addFormOfDisplay( Set<Integer> setIdForm, int nIdDisplay )
    {
        FormDisplay formDisplay = ( nIdDisplay > 0 ) ? FormDisplayHome.findByPrimaryKey( nIdDisplay ) : null;
        if ( formDisplay != null )
        {
            setIdForm.add( formDisplay.getFormId( ) );
        }
    }

    private static void addFormOfEntry( Set<Integer> setIdForm, int nIdEntry )
    {
        Entry entry = ( nIdEntry > 0 ) ? EntryHome.findByPrimaryKey( nIdEntry ) : null;
        if ( entry != null && Form.RESOURCE_TYPE.equals( entry.getResourceType( ) ) )
        {
            setIdForm.add( entry.getIdResource( ) );
        }
    }

    private static void addFormOfTransition( Set<Integer> setIdForm, int nIdTransition )
    {
        Transition transition = ( nIdTransition > 0 ) ? TransitionHome.findByPrimaryKey( nIdTransition ) : null;
        if ( transition != null )
        {
            addFormOfStep( setIdForm, transition.getFromStep( ) );
        }
    }

    private static void addFormOfControl( Set<Integer> setIdForm, int nIdControl )
    {
        Control control = ( nIdControl > 0 ) ? ControlHome.findByPrimaryKey( nIdControl ) : null;
        if ( control == null )
        {
            return;
        }
        if ( control.getListIdQuestion( ) != null )
        {
            for ( int nIdQuestion : control.getListIdQuestion( ) )
            {
                addFormOfQuestion( setIdForm, nIdQuestion );
            }
        }
        if ( ControlType.TRANSITION.getLabel( ).equals( control.getControlType( ) ) )
        {
            addFormOfTransition( setIdForm, control.getIdControlTarget( ) );
        }
    }
}
