/*
 * Copyright (c) 2002-2017, Mairie de Paris
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

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.MandatoryError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.portal.service.util.AppLogService;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * class EntryTypeCheckBox
 *
 */
public class EntryTypeMandatoryCheckBox extends EntryTypeCheckBox
{
    private static final String TEMPLATE_CREATE = "admin/plugins/forms/entries/create_entry_type_mandatory_check_box.html";
    private static final String TEMPLATE_MODIFY = "admin/plugins/forms/entries/modify_entry_type_mandatory_check_box.html";

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
    public GenericAttributeError getResponseData( Entry entry, HttpServletRequest request, List<Response> listResponse, Locale locale )
    {
        String [ ] strTabIdField = request.getParameterValues( PREFIX_ATTRIBUTE + entry.getIdEntry( ) );
        List<Field> listFieldInResponse = new ArrayList<Field>( );
        int nIdField = -1;
        Field field = null;
        Response response;

        if ( strTabIdField != null )
        {
            for ( int cpt = 0; cpt < strTabIdField.length; cpt++ )
            {
                try
                {
                    nIdField = Integer.parseInt( strTabIdField [cpt] );
                }
                catch( NumberFormatException ne )
                {
                    AppLogService.error( ne );
                }

                field = GenericAttributesUtils.findFieldByIdInTheList( nIdField, entry.getFields( ) );

                if ( field != null )
                {
                    listFieldInResponse.add( field );
                }
            }
        }

        if ( listFieldInResponse.size( ) == 0 )
        {
            response = new Response( );
            response.setEntry( entry );
            response.setIterationNumber( getResponseIterationValue( request ) );
            listResponse.add( response );
        }
        else
        {
            for ( Field fieldInResponse : listFieldInResponse )
            {
                response = new Response( );
                response.setEntry( entry );
                response.setResponseValue( fieldInResponse.getValue( ) );
                response.setField( fieldInResponse );
                response.setIterationNumber( getResponseIterationValue( request ) );
                listResponse.add( response );
            }
        }

        int nSubmitedFields = 0;

        for ( Field fieldInResponse : listFieldInResponse )
        {
            if ( StringUtils.isNotEmpty( fieldInResponse.getValue( ) ) )
            {
                nSubmitedFields++;
            }
        }

        if ( nSubmitedFields < entry.getFields( ).size( ) )
        {
            if ( StringUtils.isNotBlank( entry.getErrorMessage( ) ) )
            {
                GenericAttributeError formError = new GenericAttributeError( );
                formError.setMandatoryError( true );
                formError.setErrorMessage( entry.getErrorMessage( ) );

                return formError;
            }

            return new MandatoryError( entry, locale );
        }

        return null;
    }
}
