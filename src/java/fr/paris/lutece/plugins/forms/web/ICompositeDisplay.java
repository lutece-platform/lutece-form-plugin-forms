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
package fr.paris.lutece.plugins.forms.web;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.web.display.DisplayType;
import fr.paris.lutece.plugins.genericattributes.business.Response;

/**
 * 
 * Interface composite to display step components
 *
 */
public interface ICompositeDisplay
{
    /**
     * @param locale
     *            The locale
     * @param displayType
     *            The display type
     * @return the composite html to display
     */
    String getCompositeHtml( Locale locale, DisplayType displayType );

    /**
     * 
     * @param formDisplay
     *            The parent formDisplay
     */
    void initComposite( FormDisplay formDisplay );

    /**
     * 
     * @param mapStepResponses
     *            The map containing question responses and potential errors
     */
    void setResponses( Map<Integer, List<Response>> mapStepResponses );

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
     * Set the formDisplay
     * 
     * @param formDisplay
     *            the FormDisplay to set
     */
    void setFormDisplay( FormDisplay formDisplay );

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
     * @param strIconName
     *            the icon name to set
     */
    void setIcon( String strIconName );

    /**
     * 
     * @return all the Display Controls in the child
     */
    List<Control> getAllDisplayControls( );

}
