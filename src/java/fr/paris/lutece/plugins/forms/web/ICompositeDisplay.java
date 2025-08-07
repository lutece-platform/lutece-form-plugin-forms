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
package fr.paris.lutece.plugins.forms.web;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.web.entrytype.DisplayType;

/**
 * 
 * Interface composite to display step components
 *
 */
public interface ICompositeDisplay
{
    /**
     * Builds the HTML of the composite
     * 
     * @param request
     *            the request
     * @param listFormQuestionResponse
     *            the list of form question responses
     * @param locale
     *            The locale
     * @param displayType
     *            The display type
     * @return the HTML
     */
    String getCompositeHtml( HttpServletRequest request, List<FormQuestionResponse> listFormQuestionResponse, Locale locale, DisplayType displayType );

    /**
     * Iterates the specified form display
     * 
     * @param nIdFormDisplay
     *            the id of the form display to iterate
     */
    void iterate( int nIdFormDisplay );

    /**
     * Remove the specified iteration of group
     * 
     * @param request
     *            the request
     * @param nIdGroupParent
     *            the id of the group
     * @param nIndexIterationToRemove
     *            the index of the iteration to remove in group
     * @param formResponse
     *            the form responses
     */
    void removeIteration( HttpServletRequest request, int nIdGroupParent, int nIndexIterationToRemove, FormResponse formResponse );

    /**
     * Return the full list of children ICompositeDisplay of an initialized CompositeDisplay
     * 
     * @return the children composite list
     */
    List<ICompositeDisplay> getCompositeList( );

    /**
     * 
     * @return the title of the compositeDisplay
     */
    String getTitle( );

    /**
     * 
     * @return the type of the compositeDisplay
     */
    String getType( );

    /**
     * return the FormDisplay
     * 
     * @return the FormDisplay
     */
    FormDisplay getFormDisplay( );

    /**
     * @return the icon name
     */
    String getIcon( );

    /**
     * 
     * @return all the Display Controls in the child
     */
    List<Control> getAllDisplayControls( );

    /**
     * Add the questions into the specified list
     * 
     * @param listQuestion
     *            the list of question to complete
     */
    void addQuestions( List<Question> listQuestion );

    /**
     * Adds the specified model in the model of this instance
     * 
     * @param model
     *            the model to add
     */
    void addModel( Map<String, Object> model );

    /**
     * is the composite display visible.
     * 
     * @return
     */
    boolean isVisible( );

    ICompositeDisplay filter( List<Integer> listQuestionIds );

    /**
     * filter composiste listChildren with the listQuestion using iteration number
     * @param listQuestion
     *      The listQuestion
     * @return composite display
     */
    ICompositeDisplay filterFromListQuestion( List<Question> listQuestion );

    /**
     * Return the IterationNumber of the question
     *
     * @return IterationNumber
     */
    Integer getIterationNumber();

}
