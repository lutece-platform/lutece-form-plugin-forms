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
package fr.paris.lutece.plugins.forms.business.export;

public class FormExportConfig
{
    private int _nId;
    private int _nIdForm;
    private String _strField;
    private String _strFieldTitle;
    private int _nOrder;

    /**
     * @return the nId
     */
    public int getId( )
    {
        return _nId;
    }

    /**
     * @param nId
     *            the nId to set
     */
    public void setId( int nId )
    {
        _nId = nId;
    }

    /**
     * @return the nIdForm
     */
    public int getIdForm( )
    {
        return _nIdForm;
    }

    /**
     * @param nIdForm
     *            the nIdForm to set
     */
    public void setIdForm( int nIdForm )
    {
        _nIdForm = nIdForm;
    }

    /**
     * @return the strField
     */
    public String getField( )
    {
        return _strField;
    }

    /**
     * @param strField
     *            the strField to set
     */
    public void setField( String strField )
    {
        _strField = strField;
    }

    /**
     * @return the nOrder
     */
    public int getOrder( )
    {
        return _nOrder;
    }

    /**
     * @param nOrder
     *            the nOrder to set
     */
    public void setOrder( int nOrder )
    {
        _nOrder = nOrder;
    }

    /**
     * @return the strFieldTitle
     */
    public String getFieldTitle( )
    {
        return _strFieldTitle;
    }

    /**
     * @param strFieldTitle
     *            the strFieldTitle to set
     */
    public void setFieldTitle( String strFieldTitle )
    {
        _strFieldTitle = strFieldTitle;
    }

}
