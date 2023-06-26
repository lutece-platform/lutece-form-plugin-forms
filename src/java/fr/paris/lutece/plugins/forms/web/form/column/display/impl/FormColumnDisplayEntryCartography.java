/*
 * Copyright (c) 2002-2022, City of Paris
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

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.form.column.FormColumnCell;
import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.util.FormEntryNameConstants;
import fr.paris.lutece.portal.service.template.AppTemplateService;

/**
 * Implementation of the IFormColumnDisplay for the EntryGeolocation column
 */
public class FormColumnDisplayEntryCartography extends AbstractFormColumnDisplay
{
    // Templates
    private static final String FORM_COLUMN_HEADER_TEMPLATE = "admin/plugins/forms/multiview/column/header/form_column_entry_cartography_header.html";
    private static final String FORM_COLUMN_CELL_TEMPLATE = "admin/plugins/forms/multiview/column/cell/form_column_entry_cartography_cell.html";

    // Marks
    private static final String MARK_ENTRY_VALUE_COLUMN_TITLE = "column_title";
    private static final String MARK_ENTRY_VALUE_COLUMN_POSITION = "entry_column_position";
    private static final String MARK_COLUMN_SORT_ATTRIBUTE = "column_sort_attribute";
    private static final String MARK_SORT_URL = "sort_url";

    private static final String MARK_ENTRY_GEOJSON = "entry_geojson";

    /**
     * {@inheritDoc}
     */
    @Override
    public String buildFormColumnHeaderTemplate( String strSortUrl, Locale locale )
    {
        Map<String, Object> model = new LinkedHashMap<>( );
        model.put( MARK_ENTRY_VALUE_COLUMN_TITLE, getFormColumnTitle( locale ) );
        model.put( MARK_ENTRY_VALUE_COLUMN_POSITION, getPosition( ) );

        model.put( MARK_COLUMN_SORT_ATTRIBUTE, String.format( FormEntryNameConstants.COLUMN_ENTRY_GEOLOC_VALUE_PATTERN_ADDR, getPosition( ) ) );
        model.put( MARK_SORT_URL, buildCompleteSortUrl( strSortUrl ) );

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
        String strEntryGeoJSON = StringUtils.EMPTY;
        if ( formColumnCell != null )
        {

            String strEntryYName = String.format( FormEntryNameConstants.COLUMN_ENTRY_CARTO_VALUE_PATTERN_GEOJSON, getFormColumnPosition( ) );
            Object objEntryGeoJSON = formColumnCell.getFormColumnCellValueByName( strEntryYName );
            if ( objEntryGeoJSON != null )
            {
                strEntryGeoJSON = String.valueOf( objEntryGeoJSON );
            }
        }

        Map<String, Object> model = new LinkedHashMap<>( );
        model.put( MARK_ENTRY_GEOJSON, StringUtils.EMPTY );

        return AppTemplateService.getTemplate( FORM_COLUMN_CELL_TEMPLATE, locale, model ).getHtml( );
    }

    /**
     * Return the position of the FormColumn or {@linkplain NumberUtils.INTEGER_MINUS_ONE} if doesn't exist
     * 
     * @return the position of the FormColumn
     */
    private int getFormColumnPosition( )
    {
        int nFormColumnPosition = NumberUtils.INTEGER_MINUS_ONE;
        IFormColumn formColumn = getFormColumn( );
        if ( formColumn != null )
        {
            nFormColumnPosition = formColumn.getFormColumnPosition( );
        }

        return nFormColumnPosition;
    }
    
    /**
     * Extracts the GeoJSON value
     *
     * @param formColumnCell
     *            the FormColumnCell
     *
     * @return the GeoJSON String
     */
    public Object buildGeoJsonString( FormColumnCell geolocFormColumnCell )
    {
        String strEntryGeoJSONName = String.format( FormEntryNameConstants.COLUMN_ENTRY_CARTO_VALUE_PATTERN_GEOJSON, getFormColumnPosition( ) );
        Object objEntryGeoJSON = geolocFormColumnCell.getFormColumnCellValueByName( strEntryGeoJSONName );
        return objEntryGeoJSON ;
    }

}
