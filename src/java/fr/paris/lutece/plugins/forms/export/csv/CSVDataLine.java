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
package fr.paris.lutece.plugins.forms.export.csv;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.service.EntryServiceManager;
import fr.paris.lutece.plugins.forms.web.entrytype.IEntryDataService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * This class represents a CSV line
 *
 */
public class CSVDataLine
{
    private static final String MESSAGE_EXPORT_FORM_TITLE = "forms.export.formResponse.form.title";
    private static final String MESSAGE_EXPORT_FORM_DATE_CREATION = "forms.export.formResponse.form.date.creation";
    private static final String PROPERTY_EXPORT_FORM_DATE_CREATION_FORMAT = "forms.export.formResponse.form.date.creation.format";

    private static final String RESPONSE_SEPARATOR = " ";

    private final Map<String, String> _mapDataToExport;

    /**
     * Constructor
     * 
     * @param formResponse
     *            the form response associated to this instance
     */
    public CSVDataLine( FormResponse formResponse )
    {
        _mapDataToExport = new HashMap<>( );

        Locale locale = I18nService.getDefaultLocale( );
        DateFormat dateFormat = new SimpleDateFormat( AppPropertiesService.getProperty( PROPERTY_EXPORT_FORM_DATE_CREATION_FORMAT ), locale );
        Form form = FormHome.findByPrimaryKey( formResponse.getFormId( ) );
        _mapDataToExport.put( I18nService.getLocalizedString( MESSAGE_EXPORT_FORM_TITLE, locale ), form.getTitle( ) );
        _mapDataToExport.put( I18nService.getLocalizedString( MESSAGE_EXPORT_FORM_DATE_CREATION, locale ), dateFormat.format( formResponse.getCreation( ) ) );
    }

    /**
     * 
     * @param formQuestionResponse
     *            The data to add
     */
    public void addData( FormQuestionResponse formQuestionResponse )
    {
        IEntryDataService entryDataService = EntryServiceManager.getInstance( ).getEntryDataService(
                formQuestionResponse.getQuestion( ).getEntry( ).getEntryType( ) );

        List<String> listResponseValue = entryDataService.responseToStrings( formQuestionResponse );
        StringBuilder sbReponseValues = new StringBuilder( );

        for ( String strResponseValue : listResponseValue )
        {
            sbReponseValues.append( strResponseValue ).append( RESPONSE_SEPARATOR );
        }

        _mapDataToExport.put( CSVUtil.buildColumnName( formQuestionResponse.getQuestion( ) ), sbReponseValues.toString( ) );
    }

    /**
     * @param strColumnName
     *            The column name
     * @return the _mapDataToExport
     */
    public String getDataToExport( String strColumnName )
    {
        return _mapDataToExport.get( strColumnName );
    }

}
