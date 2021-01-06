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
package fr.paris.lutece.plugins.forms.service.entrytype;

import java.util.List;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeArray;

/**
 *
 * class EntryTypeCheckBox
 *
 */
public class EntryTypeArray extends AbstractEntryTypeArray implements IResponseComparator
{
    private static final String TEMPLATE_CREATE = "admin/plugins/forms/entries/create_entry_type_array.html";
    private static final String TEMPLATE_MODIFY = "admin/plugins/forms/entries/modify_entry_type_array.html";
    private static final String TEMPLATE_READONLY_BACKOFFICE = "admin/plugins/forms/entries/readonly_entry_type_array.html";
    private static final String TEMPLATE_EDITION_BACKOFFICE = "admin/plugins/forms/entries/fill_entry_type_array.html";
    private static final String TEMPLATE_EDITION_FRONTOFFICE = "skin/plugins/forms/entries/fill_entry_type_array.html";
    private static final String TEMPLATE_READONLY_FRONTOFFICE = "skin/plugins/forms/entries/readonly_entry_type_array.html";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateHtmlForm( Entry entry, boolean bDisplayFront )
    {
        if ( bDisplayFront )
        {
            return TEMPLATE_EDITION_FRONTOFFICE;
        }

        return TEMPLATE_EDITION_BACKOFFICE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateCreate( Entry entry, boolean bDisplayFront )
    {
        return TEMPLATE_CREATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateModify( Entry entry, boolean bDisplayFront )
    {
        return TEMPLATE_MODIFY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateEntryReadOnly( boolean bDisplayFront )
    {
        if ( bDisplayFront )
        {
            return TEMPLATE_READONLY_FRONTOFFICE;
        }

        return TEMPLATE_READONLY_BACKOFFICE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isResponseChanged( List<Response> listResponseReference, List<Response> listResponseNew )
    {
        if ( listResponseReference.size( ) != listResponseNew.size( ) )
        {
            return true;
        }

        boolean bAllResponsesEquals = true;

        for ( Response responseNew : listResponseNew )
        {
            Response responseReference = findReferenceResponseAssociatedToNewResponse( responseNew, listResponseReference );

            if ( responseReference == null || !responseReference.getResponseValue( ).equals( responseNew.getResponseValue( ) ) )
            {
                bAllResponsesEquals = false;
                break;
            }
        }

        return !bAllResponsesEquals;
    }

    /**
     * Finds the reference response associated to the new response
     * 
     * @param responseNew
     *            the new response
     * @param listResponseReference
     *            the list of reference responses
     * @return the found response or {@code null} if not found
     */
    private Response findReferenceResponseAssociatedToNewResponse( Response responseNew, List<Response> listResponseReference )
    {
        Response response = null;

        for ( Response responseReference : listResponseReference )
        {
            if ( responseReference.getField( ).getValue( ).equals( responseNew.getField( ).getValue( ) ) )
            {
                response = responseReference;
                break;
            }
        }

        return response;
    }
}
