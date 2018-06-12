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
package fr.paris.lutece.plugins.forms.business.form.column.querypart.mock;

import java.sql.Timestamp;
import java.util.Date;

import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * Mock of the DAOUtil for the Form
 */
public class DAOUtilMock extends DAOUtil
{
    // Variables
    private final String _strValueToRetrieve;
    private final Date _dateValueToRetrieve;
    private final String _strColumnName;

    /**
     * Constructor
     * 
     * @param strSQL
     *            The query to execute
     * @param strColumnName
     *            The name of the column
     * @param strValueToRetrieve
     *            The value to retrieve from the column
     */
    public DAOUtilMock( String strSQL, String strColumnName, String strValueToRetrieve )
    {
        super( strSQL );
        _strValueToRetrieve = strValueToRetrieve;
        _dateValueToRetrieve = null;
        _strColumnName = strColumnName;
    }

    /**
     * Constructor
     * 
     * @param strSQL
     *            The query to execute
     * @param strColumnName
     *            The name of the column
     * @param dateValueToRetrieve
     *            The date value to retrieve from the column
     */
    public DAOUtilMock( String strSQL, String strColumnName, Date dateValueToRetrieve )
    {
        super( strSQL );
        _strValueToRetrieve = null;
        _dateValueToRetrieve = dateValueToRetrieve;
        _strColumnName = strColumnName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getString( String strColumnName ) throws AppException
    {
        String strResult = null;

        if ( _strColumnName.equals( strColumnName ) )
        {
            strResult = _strValueToRetrieve;
        }
        else
        {
            throw new AppException( );
        }

        return strResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp getTimestamp( String strColumnName )
    {
        Timestamp timestamp = null;

        if ( _strColumnName.equals( strColumnName ) )
        {
            timestamp = new Timestamp( _dateValueToRetrieve.getTime( ) );
        }
        else
        {
            throw new AppException( );
        }

        return timestamp;
    }
}
