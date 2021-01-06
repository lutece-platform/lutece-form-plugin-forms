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
package fr.paris.lutece.plugins.forms.web.form.filter.display;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.web.form.filter.IFilterable;
import fr.paris.lutece.plugins.forms.web.form.multiview.util.IFormListPosition;

/**
 * Interface for the Filter associated to a FormColumnDisplay
 */
public interface IFormFilterDisplay extends IFilterable, IFormListPosition
{
    /**
     * Return the value of the the template of the FormFilterDisplay
     * 
     * @return the value of the FormFilterDisplay
     */
    String getValue( );

    /**
     * Return the name of the parameter associated to the FormFilterDisplay. This name correspond to the name on which the filter used to retrieve the value
     * which has been selected on the screen by the user. If the name returned by this method doesn't match the name used to retrieve the value selected by the
     * user this value will be lost during the redirection between the page of the details of the form response and the page which list all the form responses.
     * 
     * @return the name of the parameter associated to the FormFilterDisplay
     */
    String getParameterName( );

    /**
     * Return the template of the FormFilterDisplay
     * 
     * @return the template of the FormFilterDisplay
     */
    String getTemplate( );

    /**
     * Return the base template of the FormFilterDisplay, before being filled
     * 
     * @return the base template of the FormFilterDisplay
     */
    String getBaseTemplate( );

    /**
     * Build the Template of the FormFilterDisplay
     * 
     * @param request
     *            The HttpServletRequest to use to build the template of the filter
     */
    void buildTemplate( HttpServletRequest request );
}
