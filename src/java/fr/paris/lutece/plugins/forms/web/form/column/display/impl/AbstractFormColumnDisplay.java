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
package fr.paris.lutece.plugins.forms.web.form.column.display.impl;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.web.form.column.display.IFormColumnDisplay;

/**
 * Abstract class for the implementation of IFormColumnDisplay
 */
public abstract class AbstractFormColumnDisplay implements IFormColumnDisplay
{
    // Constants
    private static final String COLUMN_POSITION_SORT_ATTRIBUTE = "&column_position=%s";

    // Variables
    private String _strFormColumnHeaderTemplate;
    private int _nPosition;
    private IFormColumn _formColumn;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFormColumnHeaderTemplate( )
    {
        return _strFormColumnHeaderTemplate;
    }

    /**
     * Set the form column header template to the form column
     * 
     * @param strFormColumnHeaderTemplate
     *            The form column header template to set to the form column
     */
    protected void setFormColumnHeaderTemplate( String strFormColumnHeaderTemplate )
    {
        _strFormColumnHeaderTemplate = strFormColumnHeaderTemplate;
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
    public void setFormColumn( IFormColumn formColumn )
    {
        _formColumn = formColumn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPosition( )
    {
        return _nPosition;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPosition( int nPosition )
    {
        _nPosition = nPosition;
    }

    /**
     * Return the title of the FormColumn or {@linkplain StringUtils.EMPTY} if not found
     * 
     * @return the title of the FormColumn or {@linkplain StringUtils.EMPTY} if not found
     */
    protected String getFormColumnTitle( )
    {
        String strFormColumnTitle = StringUtils.EMPTY;

        IFormColumn formColumn = getFormColumn( );
        if ( formColumn != null )
        {
            strFormColumnTitle = formColumn.getFormColumnTitle( );
        }

        return strFormColumnTitle;
    }

    /**
     * Build the complete sort url for the column
     * 
     * @param strSortUrl
     *            The base url for the sort of the column values
     * @return the complete url for the sort of the column values
     */
    protected String buildCompleteSortUrl( String strSortUrl )
    {
        StringBuilder stringBuilderSortUrl = new StringBuilder( );

        if ( StringUtils.isNotBlank( strSortUrl ) )
        {
            stringBuilderSortUrl.append( strSortUrl );

            String strColumnPositionAttribute = String.format( COLUMN_POSITION_SORT_ATTRIBUTE, getPosition( ) );
            stringBuilderSortUrl.append( strColumnPositionAttribute );
        }

        return stringBuilderSortUrl.toString( );
    }
}
