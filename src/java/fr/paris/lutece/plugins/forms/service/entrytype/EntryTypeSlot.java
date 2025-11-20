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
package fr.paris.lutece.plugins.forms.service.entrytype;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeSlot;
import fr.paris.lutece.portal.service.i18n.I18nService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

@ApplicationScoped
@Named( "forms.entryTypeSlot" )
public class EntryTypeSlot extends AbstractEntryTypeSlot implements IResponseComparator
{
    /**
     * Name of the bean of this service
     */
    public static final String BEAN_NAME = "forms.entryTypeSlot";

    private static final String TEMPLATE_CREATE               = "admin/plugins/forms/entries/create_entry_type_slot.html";
    private static final String TEMPLATE_MODIFY               = "admin/plugins/forms/entries/modify_entry_type_slot.html";
    private static final String TEMPLATE_READONLY_BACKOFFICE  = "admin/plugins/forms/entries/readonly_entry_type_slot.html";
    private static final String TEMPLATE_EDITION_BACKOFFICE   = "admin/plugins/forms/entries/fill_entry_type_slot.html";
    private static final String TEMPLATE_EDITION_FRONTOFFICE  = "skin/plugins/forms/entries/fill_entry_type_slot.html";
    private static final String TEMPLATE_READONLY_FRONTOFFICE = "skin/plugins/forms/entries/readonly_entry_type_slot.html";

    private static final String FIELD_BEGIN_HOUR = "begin_hour";
    private static final String FIELD_END_HOUR = "end_hour";

    public static final String LABEL_PROPOSITION_PREFIX_FROM = "forms.message.slot.preposition.from";
    public static final String LABEL_PROPOSITION_PREFIX_TO   = "forms.message.slot.preposition.to";
    public static final String LABEL_SEPARATOR_NO_LOCAL   = "-";

    @Override
    public String getTemplateCreate( Entry entry, boolean bDisplayFront )
    {
        return TEMPLATE_CREATE;
    }

    @Override
    public String getTemplateHtmlForm( Entry entry, boolean bDisplayFront )
    {
        if ( bDisplayFront )
        {
            return TEMPLATE_EDITION_FRONTOFFICE;
        }

        return TEMPLATE_EDITION_BACKOFFICE;
    }

    @Override
    public String getTemplateModify( Entry entry, boolean bDisplayFront )
    {
        return TEMPLATE_MODIFY;
    }

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

        for ( int i = 0; i < listResponseReference.size( ); i++ )
        {
            Response responseReference = listResponseReference.get( i );
            Response responseNew = listResponseNew.get( i );

            if ( responseReference == null || responseReference.getResponseValue( ) == null || !responseReference.getResponseValue( ).equals( responseNew.getResponseValue( ) ) )
            {
                bAllResponsesEquals = false;
                break;
            }
        }

        return !bAllResponsesEquals;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponseValueForExport(Entry entry, HttpServletRequest request, Response response, Locale locale )
    {
        StringBuilder str = new StringBuilder();
        if ( response.getField() != null )
        {
            if( FIELD_BEGIN_HOUR.equals(response.getField().getCode()) )
            {
                str.append( I18nService.getLocalizedString( LABEL_PROPOSITION_PREFIX_FROM, locale ) ).append(" ");
            }
            if (FIELD_END_HOUR.equals(response.getField().getCode()) )
            {
                str.append( I18nService.getLocalizedString( LABEL_PROPOSITION_PREFIX_TO, locale ) ).append(" ");
            }
        }

        str.append(response.getResponseValue( ));
        return str.toString();
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public String getResponseValueForRecap( Entry entry, HttpServletRequest request, Response response, Locale locale )
    {

        if ( response.getField() != null && FIELD_BEGIN_HOUR.equals(response.getField().getCode()) )
        {
            FormQuestionResponse formQuestionResponse = FormQuestionResponseHome.selectFormQuestionResponseByEntryResponse(response);

            Response responseEnd = formQuestionResponse.getEntryResponse().stream()
                    .filter( r -> r.getField()!=null
                            && FIELD_END_HOUR.equals(r.getField().getCode()) ).findFirst().orElse(null);

            StringBuilder str = new StringBuilder();
            if( locale != null )
            {
                str.append( I18nService.getLocalizedString( LABEL_PROPOSITION_PREFIX_FROM, locale ) ).append(" ");
            }
            str.append( response.getResponseValue( ) );

            if( locale != null )
            {
                str.append(" ").append( I18nService.getLocalizedString( LABEL_PROPOSITION_PREFIX_TO, locale ) ).append(" ");
            }
            else
            {
                str.append( LABEL_SEPARATOR_NO_LOCAL );
            }

            if ( responseEnd != null)
            {
                str.append( responseEnd.getResponseValue() );
            }

            return str.toString();
        }

        return null;
    }

}
