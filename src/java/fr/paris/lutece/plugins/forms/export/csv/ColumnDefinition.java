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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.Question;

/**
 * 
 * @author a614328
 *
 */
public class ColumnDefinition
{
    private Map<String, Integer> _mapColumnDef = new HashMap<String, Integer>( );

    private final List<String> _listColumnToExport = new ArrayList<String>( );

    /**
     * @return the _mapColumnDef
     */
    public Map<String, Integer> getColumnDef( )
    {
        return _mapColumnDef;
    }

    /**
     * @param mapColumnDef
     *            the mapColumnDef to set
     */
    public void setColumnDef( Map<String, Integer> mapColumnDef )
    {
        this._mapColumnDef = mapColumnDef;
    }

    /**
     * @param question
     *            the question to add
     */
    public void addColumnDef( Question question )
    {
        if ( !_mapColumnDef.containsKey( question.getTitle( ) ) || _mapColumnDef.get( question.getTitle( ) ) < question.getIterationNumber( ) )
        {
            _mapColumnDef.put( question.getTitle( ), question.getIterationNumber( ) );
        }
    }

    /**
     * Build the final list of column to export (with iteration number)
     */
    public void buildColumnToExport( )
    {
        for ( Map.Entry<String, Integer> column : _mapColumnDef.entrySet( ) )
        {
            _listColumnToExport.add( column.getKey( ) );

            if ( column.getValue( ) > 0 )
            {
                String strColumnFinalName = StringUtils.EMPTY;

                for ( int i = 1; i <= column.getValue( ); i++ )
                {
                    strColumnFinalName = column.getKey( ) + StringUtils.SPACE + i;

                    _listColumnToExport.add( strColumnFinalName );
                }
            }
        }
    }

    /**
     * @return the _listFinalColumnToExport
     */
    public List<String> getColumnToExport( )
    {
        return _listColumnToExport;
    }
}
