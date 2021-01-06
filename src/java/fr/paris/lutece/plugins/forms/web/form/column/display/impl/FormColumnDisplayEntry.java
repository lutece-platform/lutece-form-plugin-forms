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
package fr.paris.lutece.plugins.forms.web.form.column.display.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.form.column.FormColumnCell;
import fr.paris.lutece.plugins.forms.business.form.column.impl.FormColumnEntry;
import fr.paris.lutece.plugins.forms.business.form.search.FormResponseSearchItem;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeDate;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeNumbering;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * Implementation of the IFormColumnDisplay for the Entry column
 */
public class FormColumnDisplayEntry extends AbstractFormColumnDisplay
{
    // Templates
    private static final String FORM_COLUMN_HEADER_TEMPLATE = "admin/plugins/forms/multiview/column/header/form_column_entry_header.html";
    private static final String FORM_COLUMN_CELL_TEMPLATE = "admin/plugins/forms/multiview/column/cell/form_column_entry_cell.html";

    // Marks
    private static final String MARK_ENTRY_VALUE_COLUMN_TITLE = "column_title";
    private static final String MARK_ENTRY_VALUE_COLUMN_POSITION = "entry_column_position";
    private static final String MARK_ENTRY_VALUES = "entry_values";
    private static final String MARK_COLUMN_SORT_ATTRIBUTE = "column_sort_attribute";
    private static final String MARK_SORT_URL = "sort_url";

    private static final String FILTER_DATE_FORMAT = AppPropertiesService.getProperty( "forms.index.date.format", "dd/MM/yyyy" );
    private final DateFormat _dateFormat = new SimpleDateFormat( FILTER_DATE_FORMAT );

    /**
     * {@inheritDoc}
     */
    @Override
    public String buildFormColumnHeaderTemplate( String strSortUrl, Locale locale )
    {
        Map<String, Object> model = new LinkedHashMap<>( );
        model.put( MARK_SORT_URL, buildCompleteSortUrl( strSortUrl ) );
        model.put( MARK_ENTRY_VALUE_COLUMN_TITLE, getFormColumnTitle( locale ) );
        model.put( MARK_ENTRY_VALUE_COLUMN_POSITION, getPosition( ) );

        if ( getFormColumn( ) instanceof FormColumnEntry )
        {
            FormColumnEntry column = ( (FormColumnEntry) getFormColumn( ) );
            String columSort = column.getListEntryCode( ).stream( ).distinct( ).collect( Collectors.joining( "," ) );
            String strAttributeSort = FormResponseSearchItem.FIELD_ENTRY_CODE_SUFFIX + columSort + FormResponseSearchItem.FIELD_RESPONSE_FIELD_ITER + "0";

            String strEntryCode = column.getListEntryCode( ).get( 0 );
            Question question = QuestionHome.findByCode( strEntryCode );
            Entry entry = question.getEntry( );
            IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( entry );
            if ( entryTypeService instanceof EntryTypeNumbering )
            {
                strAttributeSort += FormResponseSearchItem.FIELD_INT_SUFFIX;
            }
            if ( entryTypeService instanceof EntryTypeDate )
            {
                strAttributeSort += FormResponseSearchItem.FIELD_DATE_SUFFIX;
            }
            model.put( MARK_COLUMN_SORT_ATTRIBUTE, strAttributeSort );
        }

        String strColumnHeaderTemplate = AppTemplateService.getTemplate( FORM_COLUMN_HEADER_TEMPLATE, locale, model ).getHtml( );
        setFormColumnHeaderTemplate( strColumnHeaderTemplate );

        return strColumnHeaderTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String buildFormColumnCellTemplate( FormColumnCell formColumnCell, Locale locale )
    {
        List<String> listEntryValues = new ArrayList<>( );
        if ( formColumnCell != null && formColumnCell.getFormColumnCellValues( ).size( ) > 0 )
        {
            for ( java.util.Map.Entry<String, Object> entry : formColumnCell.getFormColumnCellValues( ).entrySet( ) )
            {
                Object objEntryValue = entry.getValue( );
                String objEntryKey = entry.getKey( );
                if ( objEntryValue == null )
                {
                    continue;
                }

                if ( objEntryKey.endsWith( FormResponseSearchItem.FIELD_DATE_SUFFIX ) )
                {
                    String stringToConvert = String.valueOf( objEntryValue );
                    try
                    {
                        Long convertedLong = Long.parseLong( stringToConvert );
                        Date date = new Date( convertedLong );
                        String strDate = _dateFormat.format( date );

                        listEntryValues.add( strDate );
                    }
                    catch( Exception e )
                    {
                        listEntryValues.add( stringToConvert );
                    }

                }
                else
                {
                    listEntryValues.add( String.valueOf( objEntryValue ) );
                }
            }
        }

        Map<String, Object> model = new LinkedHashMap<>( );
        model.put( MARK_ENTRY_VALUES, listEntryValues );

        return AppTemplateService.getTemplate( FORM_COLUMN_CELL_TEMPLATE, locale, model ).getHtml( );
    }

}
