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

import org.hibernate.validator.constraints.NotEmpty;

import fr.paris.lutece.portal.service.util.AppLogService;

/**
 * This is the business class for the object Control
 */
public class Control implements Cloneable
{
    private int _nId;

    @NotEmpty( message = "#i18n{forms.validation.control.Value.notEmpty}" )
    private String _strValue;

    private String _strErrorMessage;

    @Min( value = 1, message = "#i18n{forms.validation.control.Question.notEmpty}" )
    private int _nIdQuestion;

    @NotEmpty( message = "#i18n{forms.validation.control.ValidatorName.notEmpty}" )
    private String _strValidatorName;

    @NotEmpty( message = "#i18n{forms.validation.control.ControlType.notEmpty}" )
    private String _strControlType;

    private int _nIdTargetFormDisplay;

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
     * @return the _strValue
     */
    public String getValue( )
    {
        return _strValue;
    }

    /**
     * @param strValue
     *            the strValue to set
     */
    public void setValue( String strValue )
    {
        this._strValue = strValue;
    }

    /**
     * @return the _strErrorMessage
     */
    public String getErrorMessage( )
    {
        return _strErrorMessage;
    }

    /**
     * @param strErrorMessage
     *            the strErrorMessage to set
     */
    public void setErrorMessage( String strErrorMessage )
    {
        this._strErrorMessage = strErrorMessage;
    }

    /**
     * @return the _nIdQuestion
     */
    public int getIdQuestion( )
    {
        return _nIdQuestion;
    }

    /**
     * @param nIdQuestion
     *            the nIdQuestion to set
     */
    public void setIdQuestion( int nIdQuestion )
    {
        this._nIdQuestion = nIdQuestion;
    }

    /**
     * @return the _strValidatorName
     */
    public String getValidatorName( )
    {
        return _strValidatorName;
    }

    /**
     * @param strValidatorName
     *            the strValidatorName to set
     */
    public void setValidatorName( String strValidatorName )
    {
        this._strValidatorName = strValidatorName;
    }

    /**
     * @return the _strControlType
     */
    public String getControlType( )
    {
        return _strControlType;
    }

    /**
     * @param strControlType
     *            the strControlType to set
     */
    public void setControlType( String strControlType )
    {
        this._strControlType = strControlType;
    }

    /**
     * 
     * @return the _nIdTargetFormDisplay
     */
    public int getIdTargetFormDisplay( )
    {
        return _nIdTargetFormDisplay;
    }

    /**
     * 
     * @param nIdTargetFormDisplay
     *            the nIdTargetFormDisplay to set
     */
    public void setIdTargetFormDisplay( int nIdTargetFormDisplay )
    {
        this._nIdTargetFormDisplay = nIdTargetFormDisplay;
    }

    /**
     * {@inheritDoc}
     * 
     * @throws CloneNotSupportedException
     */
    @Override
    public Control clone( )
    {
        Control controlNew = new Control( );

        try
        {
            controlNew = (Control) super.clone( );
        }
        catch( CloneNotSupportedException e )
        {
            AppLogService.error( e );
        }

        controlNew.setId( _nId );
        controlNew.setIdQuestion( _nIdQuestion );
        controlNew.setControlType( _strControlType );
        controlNew.setErrorMessage( _strErrorMessage );
        controlNew.setIdTargetFormDisplay( _nIdTargetFormDisplay );
        controlNew.setValidatorName( _strValidatorName );
        controlNew.setValue( _strValue );

        return controlNew;
    }
}
