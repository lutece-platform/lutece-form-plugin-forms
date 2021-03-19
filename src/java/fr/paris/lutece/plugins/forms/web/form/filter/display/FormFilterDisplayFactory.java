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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.plugins.forms.business.form.filter.FormFilter;
import fr.paris.lutece.plugins.forms.business.form.filter.configuration.IFormFilterConfiguration;
import fr.paris.lutece.plugins.forms.web.form.multiview.util.FormListPositionComparator;

/**
 * Factory for FormFilterDisplay objects
 */
public class FormFilterDisplayFactory
{
    /**
     * Return the list of all FormFilterDisplay ordered by their position
     * 
     * @param request
     *            The HttpServletRequest used to create the list of all FormFilterDisplay
     * @param listFormFilter
     *            The list of IFormFilter used for building IFormFilterDisplay
     * @return the list of all FormFilterDisplay ordered by their position
     */
    public List<IFormFilterDisplay> createFormFilterDisplayList( HttpServletRequest request, List<FormFilter> listFormFilter )
    {
        if ( CollectionUtils.isEmpty( listFormFilter ) )
        {
            return new ArrayList<>( );
        }
        
        List<IFormFilterDisplay> listFormFilterDisplay = new ArrayList<>( );
        for ( FormFilter formFilter : listFormFilter )
        {
            IFormFilterConfiguration formFilterConfiguration = formFilter.getFormFilterConfiguration( );
            if ( formFilterConfiguration != null )
            {
                IFormFilterDisplay formFilterDisplay = formFilterConfiguration.getFormFilterDisplay( formFilter );
                formFilterDisplay.createFormParameters( request );
                formFilterDisplay.setPosition( formFilterConfiguration.getPosition( ) );
                
                listFormFilterDisplay.add( formFilterDisplay );
            }
        }
        // Sort the list by the position of each elements
        Collections.sort( listFormFilterDisplay, new FormListPositionComparator( ) );
        return listFormFilterDisplay;
    }
}
