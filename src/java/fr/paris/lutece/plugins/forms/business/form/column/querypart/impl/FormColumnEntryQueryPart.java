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
package fr.paris.lutece.plugins.forms.business.form.column.querypart.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.column.impl.FormColumnEntry;
import fr.paris.lutece.plugins.forms.business.form.search.FormResponseSearchItem;

/**
 * Implementation of the IFormColumnQueryPart interface for a form column
 */
public class FormColumnEntryQueryPart extends AbstractFormColumnQueryPart
{

    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, Object> getMapFormColumnValues( FormResponseSearchItem formResponseSearchItem )
    {
        Map<String, Object> mapFormColumnValues = new HashMap<>( );

        for ( String strFormColumnEntryCode : getListEntryCode( getFormColumn( ) ) )
        {
            mapFormColumnValues.putAll( getEntryCodeFields( strFormColumnEntryCode, formResponseSearchItem ) );
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
        if ( column instanceof FormColumnEntry )
        {
            FormColumnEntry formColumnEntry = (FormColumnEntry) column;
            return formColumnEntry.getListEntryCode( );
        }
        return new ArrayList<>( );
    }
}
