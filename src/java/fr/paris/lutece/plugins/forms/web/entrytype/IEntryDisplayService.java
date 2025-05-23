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
package fr.paris.lutece.plugins.forms.web.entrytype;

import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.util.FormsConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.service.util.AppLogService;

/**
 * Interface for the entry display services
 */
public interface IEntryDisplayService
{
    /**
     * @return the display service name
     */
    String getDisplayServiceName( );

    /**
     * Get the Html of the given step
     * 
     * @param request
     *            the request
     * @param entry
     *            The given entry
     * @param locale
     *            The given locale
     * @param model
     *            The given model
     * @param displayType
     *            the display type
     * @return the template Html of the given entry
     */
    String getEntryTemplateDisplay( HttpServletRequest request, Entry entry, Locale locale, Map<String, Object> model, DisplayType displayType );

    /**
     * Get the column of the entry display service
     * 
     * @param nFormColumnPosition
     * @param strColumnTitle
     * @return the column corresponding the the current entryDisplayService
     */
    IFormColumn getFormColumn( int nFormColumnPosition, String strColumnTitle );

    /**
     * Return from the given map the list of Response of the model
     * 
     * @param model
     *            The model on which to retrieve the list of all Response
     * @return the list of Response form the given model or an empty collection if its missing or if an error occurred
     */
    @SuppressWarnings( "unchecked" )
    default List<Response> retrieveResponseListFromModel( Map<String, Object> model )
    {
        List<Response> listResponse = new ArrayList<>( );

        if ( !MapUtils.isEmpty( model ) && model.containsKey( FormsConstants.MARK_QUESTION_LIST_RESPONSES ) )
        {
            try
            {
                listResponse = (List<Response>) model.get( FormsConstants.MARK_QUESTION_LIST_RESPONSES );
            }
            catch( ClassCastException exception )
            {
                AppLogService.error( "The object associated to the list of Responses doesn't have the good format !" );

                // Erase the value to avoid future errors
                model.put( FormsConstants.MARK_QUESTION_LIST_RESPONSES, new ArrayList<>( ) );
            }
        }

        return listResponse;
    }
}
