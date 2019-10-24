/*
 * Copyright (c) 2002-2019, Mairie de Paris
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
package fr.paris.lutece.plugins.forms.business.form.column;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * Class which represent one cell of a FormColumn
 */
public class FormColumnCell
{
    // Variables
    private Map<String, Object> _mapFormColumnCellValues = new LinkedHashMap<>( );

    /**
     * Return the map which contains all values of a cell for a form column
     * 
     * @return the values of the cell of a form column
     */
    public Map<String, Object> getFormColumnCellValues( )
    {
        return _mapFormColumnCellValues;
    }

    /**
     * Set the values of the cell of a form column
     * 
     * @param mapFormColumnValues
     *            The values to set to the cell of a form column
     */
    public void setFormColumnCellValues( Map<String, Object> mapFormColumnValues )
    {
        _mapFormColumnCellValues = mapFormColumnValues;
    }

    /**
     * Return the value of FormColumnCell with the specify name
     * 
     * @param strCellName
     *            The name of the cell to retrieve the value
     * @return the value of the FormColumnCell with the specify value or null if not found
     */
    public Object getFormColumnCellValueByName( String strCellName )
    {
        if ( _mapFormColumnCellValues == null )
        {
            return null;
        }
        String key = _mapFormColumnCellValues.keySet( ).stream( ).filter( k -> k.contains( strCellName ) ).sorted( ).findFirst( ).orElse( null );

        Object objFormColumnCell = null;

        if ( StringUtils.isNotBlank( key ) )
        {
            objFormColumnCell = _mapFormColumnCellValues.get( key );
        }

        return objFormColumnCell;
    }
}
