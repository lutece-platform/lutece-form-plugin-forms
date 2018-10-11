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

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.export.IFormatExport;
import fr.paris.lutece.portal.service.i18n.I18nService;

/**
 * This class performs a CSV export
 *
 */
public class CSVExport implements IFormatExport
{
    private static final String CONSTANT_MIME_TYPE_CSV = "application/csv";
    private static final char CHARACTER_UTF_8_BOM = '\uFEFF';

    private final String _strFormatExportName;
    private final String _strFormatExportDisplayName;

    /**
     * Constructor of the PatternValidator
     * 
     * @param strFormatExportName
     *            The export format bean name
     * @param strFormatExportDisplayName
     *            The export format display name
     */
    public CSVExport( String strFormatExportName, String strFormatExportDisplayName )
    {
        _strFormatExportName = strFormatExportName;
        _strFormatExportDisplayName = I18nService.getLocalizedString( strFormatExportDisplayName, I18nService.getDefaultLocale( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFormatExportBeanName( )
    {
        return _strFormatExportName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFormatExportDisplayName( )
    {
        return _strFormatExportDisplayName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFormatContentType( )
    {
        return CONSTANT_MIME_TYPE_CSV;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte [ ] getByteExportFile( List<FormResponse> listFormResponse )
    {
        byte [ ] byteExportFile = { };

        if ( CollectionUtils.isNotEmpty( listFormResponse ) )
        {
            FormResponseCsvExport formResponseExport = new FormResponseCsvExport( listFormResponse );

            StringBuilder sbExportFile = new StringBuilder( );
            sbExportFile.append( CHARACTER_UTF_8_BOM ).append( formResponseExport.getCsvColumnToExport( ) ).append( formResponseExport.getCsvDataToExport( ) );

            byteExportFile = sbExportFile.toString( ).getBytes( StandardCharsets.UTF_8 );
        }

        return byteExportFile;
    }

}
