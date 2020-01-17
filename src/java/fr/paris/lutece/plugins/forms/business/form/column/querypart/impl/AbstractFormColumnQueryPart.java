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

import fr.paris.lutece.plugins.forms.business.form.column.FormColumnCell;
import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.column.querypart.IFormColumnQueryPart;
import fr.paris.lutece.plugins.forms.business.form.search.FormResponseSearchItem;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Abstract class for FormColumnQueryPart
 */
public abstract class AbstractFormColumnQueryPart implements IFormColumnQueryPart
{
    /**
     * Get a map of values fetched from Lucene document
     * 
     * @param formResponseSearchItem
     * @return a Map of values feteched form Lucene document
     */
    protected abstract Map<String, Object> getMapFormColumnValues( FormResponseSearchItem formResponseSearchItem );

    // Variables
    private IFormColumn _formColumn;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFormColumn( IFormColumn formColumn )
    {
        _formColumn = formColumn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IFormColumn getFormColumn( )
    {
        return _formColumn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FormColumnCell getFormColumnCell( FormResponseSearchItem formResponseSearchItem )
    {
        Map<String, Object> mapFormColumnValues = new LinkedHashMap<>( );

        IFormColumn formColumn = getFormColumn( );
        if ( formColumn != null )
        {
            mapFormColumnValues = getMapFormColumnValues( formResponseSearchItem );
        }

        FormColumnCell formColumnCell = new FormColumnCell( );
        formColumnCell.setFormColumnCellValues( mapFormColumnValues );

        return formColumnCell;
    }

    /**
     * Get the list of Lucene indexable fields based on a document and an entry code prefix.
     * 
     * @param strEntryCode
     * @param document
     * @return The list of Lucene indexable fields based on a document and an entry code prefix
     */
    protected Map<String, String> getEntryCodeFields( String strEntryCode, FormResponseSearchItem formResponseSearchItem )
    {
        Map<String, String> listFields = new HashMap<>( );
        for ( Map.Entry<String, String> entry : formResponseSearchItem.getMapEntryCodeFieldsValue( ).entrySet( ) )
        {
            String strFieldSuffixEntryCode = FormResponseSearchItem.FIELD_ENTRY_CODE_SUFFIX + strEntryCode;

            if ( entry.getKey( ).startsWith( strFieldSuffixEntryCode ) )
            {
                listFields.put( entry.getKey( ), entry.getValue( ) );
            }
        }
        return listFields;
    }
}
