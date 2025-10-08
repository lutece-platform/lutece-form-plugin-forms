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
package fr.paris.lutece.plugins.forms.business;

import java.io.Serializable;

import jakarta.validation.constraints.Size;

/**
 * This is the business class for the object Form
 */
public class FormMessage implements Serializable
{
	private static final long serialVersionUID = 1L;

    // Variables declarations
    private int _nId;

    private int _nIdForm;

    private boolean _bEndMessageDisplay;

    @Size( max = 3000, message = "#i18n{forms.validation.form.EndMessage.size}" )
    private String _strEndMessage;

    private String _strLabelEndMessageButton;

    /**
     * @return the _nId
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
        this._nId = nId;
    }

    /**
     * @return the _nIdForm
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
        this._nIdForm = nIdForm;
    }

    /**
     * @return the _bEndMessageDisplay
     */
    public boolean getEndMessageDisplay( )
    {
        return _bEndMessageDisplay;
    }

    /**
     * @param bEndMessageDisplay
     *            the bEndMessageDisplay to set
     */
    public void setEndMessageDisplay( boolean bEndMessageDisplay )
    {
        this._bEndMessageDisplay = bEndMessageDisplay;
    }

    /**
     * @return the _strEndMessage
     */
    public String getEndMessage( )
    {
        return _strEndMessage;
    }

    /**
     * @param strEndMessage
     *            the strEndMessage to set
     */
    public void setEndMessage( String strEndMessage )
    {
        this._strEndMessage = strEndMessage;
    }

    /**
     * @return the _strLabelEndMessageButton
     */
    public String getLabelEndMessageButton( )
    {
        return _strLabelEndMessageButton;
    }

    /**
     * @param strLabelEndMessageButton
     *            the strLabelEndMessageButton to set
     */
    public void setLabelEndMessageButton( String strLabelEndMessageButton )
    {
        this._strLabelEndMessageButton = strLabelEndMessageButton;
    }

}
