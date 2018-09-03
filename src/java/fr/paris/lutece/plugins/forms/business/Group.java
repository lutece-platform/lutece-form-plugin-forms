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
package fr.paris.lutece.plugins.forms.business;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotEmpty;
import java.io.Serializable;

/**
 * This is the business class for the object Group
 */
public class Group implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations
    private int _nId;

    @NotEmpty( message = "#i18n{forms.validation.group.Title.notEmpty}" )
    @Size( max = 255, message = "#i18n{forms.validation.group.Title.size}" )
    private String _strTitle;

    @Size( max = 255, message = "#i18n{forms.validation.group.Description.size}" )
    private String _strDescription;

    private int _nIdStep;

    private boolean _bCollapsible;

    @Min( value = 0, message = "#i18n{forms.validation.group.IterationMin.notEmpty}" )
    private int _nIterationMin;

    @Min( value = 0, message = "#i18n{forms.validation.group.IterationMax.notEmpty}" )
    private int _nIterationMax;

    private String _strIterationLabel;

    /**
     * Returns the Id
     * 
     * @return The Id
     */
    public int getId( )
    {
        return _nId;
    }

    /**
     * Sets the Id
     * 
     * @param nId
     *            The Id
     */
    public void setId( int nId )
    {
        _nId = nId;
    }

    /**
     * Returns the Title
     * 
     * @return The Title
     */
    public String getTitle( )
    {
        return _strTitle;
    }

    /**
     * Sets the Title
     * 
     * @param strTitle
     *            The Title
     */
    public void setTitle( String strTitle )
    {
        _strTitle = strTitle;
    }

    /**
     * Returns the Description
     * 
     * @return The Description
     */
    public String getDescription( )
    {
        return _strDescription;
    }

    /**
     * Sets the Description
     * 
     * @param strDescription
     *            The Description
     */
    public void setDescription( String strDescription )
    {
        _strDescription = strDescription;
    }

    /**
     * Returns the IdStep
     * 
     * @return The IdStep
     */
    public int getIdStep( )
    {
        return _nIdStep;
    }

    /**
     * Sets the IdStep
     * 
     * @param nIdStep
     *            The IdStep
     */
    public void setIdStep( int nIdStep )
    {
        _nIdStep = nIdStep;
    }

    /**
     * Returns the Collapsible
     * 
     * @return The Collapsible
     */
    public boolean getCollapsible( )
    {
        return _bCollapsible;
    }

    /**
     * Sets the Collapsible
     * 
     * @param bCollapsible
     *            The Collapsible
     */
    public void setCollapsible( boolean bCollapsible )
    {
        _bCollapsible = bCollapsible;
    }

    /**
     * Returns the _nIterationMin
     * 
     * @return The _nIterationMin
     */
    public int getIterationMin( )
    {
        return _nIterationMin;
    }

    /**
     * Sets the IterationNumber
     * 
     * @param nIterationMin
     *            The nIterationMin
     */
    public void setIterationMin( int nIterationMin )
    {
        _nIterationMin = nIterationMin;
    }

    /**
     * Returns the _nIterationMax
     * 
     * @return The _nIterationMax
     */
    public int getIterationMax( )
    {
        return _nIterationMax;
    }

    /**
     * Sets the nIterationMax
     * 
     * @param nIterationMax
     *            The nIterationMax
     */
    public void setIterationMax( int nIterationMax )
    {
        _nIterationMax = nIterationMax;
    }

    /**
     * Returns the _strIterationLabel
     * 
     * @return The _strIterationLabel
     */
    public String getIterationLabel( )
    {
        return _strIterationLabel;
    }

    /**
     * Sets the _strIterationLabel
     * 
     * @param strIterationLabel
     *            The strIterationLabel
     */
    public void setIterationLabel( String strIterationLabel )
    {
        _strIterationLabel = strIterationLabel;
    }

}
