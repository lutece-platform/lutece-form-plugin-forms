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
package fr.paris.lutece.plugins.forms.business.form;

/**
 * Configuration class for compare FormResponseItem
 */
public class FormResponseItemSortConfig
{
    // Variables
    private final int _nColumnToSortPosition;
    private final String _strSortAttributeName;
    private final boolean _bAscSort;

    /**
     * Constructor
     * 
     * @param nColumnToSortPosition
     *            The position of the column to sort
     * @param strSortAttributeName
     *            The name of the attribute of the column to sort
     * @param bAscSort
     *            The boolean which tell if the sort must be ascendant or descendant
     */
    public FormResponseItemSortConfig( int nColumnToSortPosition, String strSortAttributeName, boolean bAscSort )
    {
        _nColumnToSortPosition = nColumnToSortPosition;
        _bAscSort = bAscSort;
        _strSortAttributeName = strSortAttributeName;
    }

    /**
     * Return the position of the column to sort
     * 
     * @return the position of the column sort
     */
    public int getColumnToSortPosition( )
    {
        return _nColumnToSortPosition;
    }

    /**
     * Return the name of the attribute of the column to sort
     * 
     * @return the name of the attribute of the column to sort
     */
    public String getSortAttributeName( )
    {
        return _strSortAttributeName;
    }

    /**
     * Return the boolean which tell if the sort of the values of the column must be ascendant or descendant
     * 
     * @return the boolean which tell if the values of the column must be ascendant or descendant
     */
    public boolean isAscSort( )
    {
        return _bAscSort;
    }
}
