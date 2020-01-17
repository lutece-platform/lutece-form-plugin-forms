/*
 * Copyright (c) 2002-2020, City of Paris
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
package fr.paris.lutece.plugins.forms.business.form.column.querypart.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.column.impl.FormColumnEntryGeolocation;
import fr.paris.lutece.plugins.forms.business.form.search.FormResponseSearchItem;
import fr.paris.lutece.plugins.forms.util.FormEntryNameConstants;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;

/**
 * Implementation of the IFormColumnQueryPart interface for a form column
 */
public class FormColumnEntryGeolocationQueryPart extends AbstractFormColumnQueryPart
{
    private static final String CONSTANT_FIELD_ADDRESS = "address";
    private static final String CONSTANT_FIELD_X = "X";
    private static final String CONSTANT_FIELD_Y = "Y";

    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, Object> getMapFormColumnValues( FormResponseSearchItem formResponseSearchItem )
    {
        Map<String, Object> mapFormColumnValues = new HashMap<>( );

        String strAddressValue = String.format( FormEntryNameConstants.COLUMN_ENTRY_GEOLOC_VALUE_PATTERN_ADDR, getFormColumn( ).getFormColumnPosition( ) );
        String strXValue = String.format( FormEntryNameConstants.COLUMN_ENTRY_GEOLOC_VALUE_PATTERN_X, getFormColumn( ).getFormColumnPosition( ) );
        String strYValue = String.format( FormEntryNameConstants.COLUMN_ENTRY_GEOLOC_VALUE_PATTERN_Y, getFormColumn( ).getFormColumnPosition( ) );

        for ( String strFormColumnEntryCode : getListEntryCode( getFormColumn( ) ) )
        {
            Map<String, String> listFields = getEntryCodeFields( strFormColumnEntryCode, formResponseSearchItem );
            for ( Map.Entry<String, String> field : listFields.entrySet( ) )
            {
                String [ ] splits = field.getKey( ).split( "_" );
                String strIdField = splits [splits.length - 1];
                int nIdField = Integer.parseInt( strIdField );
                Field fieldGenatt = FieldHome.findByPrimaryKey( nIdField );
                switch( fieldGenatt.getValue( ) )
                {
                    case CONSTANT_FIELD_ADDRESS:
                        mapFormColumnValues.put( strAddressValue, field.getValue( ) );
                        break;

                    case CONSTANT_FIELD_X:
                        mapFormColumnValues.put( strXValue, field.getValue( ) );
                        break;

                    case CONSTANT_FIELD_Y:
                        mapFormColumnValues.put( strYValue, field.getValue( ) );
                        break;
                    default:
                        break;
                }
            }
        }

        return mapFormColumnValues;
    }

    /**
     * Get the list of entry codes from the form column
     * 
     * @param column
     * @return the list of entry codes of the given column
     */
    private List<String> getListEntryCode( IFormColumn column )
    {
        if ( column instanceof FormColumnEntryGeolocation )
        {
            FormColumnEntryGeolocation formColumnEntry = (FormColumnEntryGeolocation) column;
            return formColumnEntry.getListEntryCode( );
        }
        return new ArrayList<>( );
    }
}
