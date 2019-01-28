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

import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotEmpty;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.portal.service.util.AppLogService;

import java.io.Serializable;

/**
 * This is the business class for the object Question
 */
public class Question implements Serializable, Cloneable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations
    private int _nId;

    @NotEmpty( message = "#i18n{forms.validation.question.Title.notEmpty}" )
    @Size( max = 255, message = "#i18n{forms.validation.question.Title.size}" )
    private String _strTitle;

    @Size( max = 255, message = "#i18n{forms.validation.question.Description.size}" )
    private String _strDescription;

    private int _nIdEntry;

    private Entry _entry;

    private int _nIdStep;

    private Step _step;

    private int _nIterationNumber;

    private boolean _bIsVisible;
    
    private boolean _bIsResponsesIndexed;

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
     * @return the isVisible
     */
    public boolean isVisible( )
    {
        return _bIsVisible;
    }

    /**
     * @param isVisible
     *            the isVisible to set
     */
    public void setIsVisible( boolean isVisible )
    {
        _bIsVisible = isVisible;
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
     * Returns the IdEntry
     * 
     * @return The IdEntry
     */
    public int getIdEntry( )
    {
        return _nIdEntry;
    }

    /**
     * Sets the IdEntry
     * 
     * @param nIdEntry
     *            The IdEntry
     */
    public void setIdEntry( int nIdEntry )
    {
        _nIdEntry = nIdEntry;
    }

    /**
     * Returns the Entry
     * 
     * @return The Entry
     */
    public Entry getEntry( )
    {
        return _entry;
    }

    /**
     * Sets the Entry
     * 
     * @param entry
     *            The Entry
     */
    public void setEntry( Entry entry )
    {
        _entry = entry;
    }

    /**
     * @return the _nIdStep
     */
    public int getIdStep( )
    {
        return _nIdStep;
    }

    /**
     * @param nIdStep
     *            the nIdStep to set
     */
    public void setIdStep( int nIdStep )
    {
        this._nIdStep = nIdStep;
    }

    /**
     * @return the _step
     */
    public Step getStep( )
    {
        return _step;
    }

    /**
     * @param step
     *            the step to set
     */
    public void setStep( Step step )
    {
        this._step = step;
    }

    /**
     * Returns the iteration number
     * 
     * @return The iteration number
     */
    public int getIterationNumber( )
    {
        return _nIterationNumber;
    }

    /**
     * Sets the iteration number
     * 
     * @param nIterationNumber
     *            The iteration number
     */
    public void setIterationNumber( int nIterationNumber )
    {
        _nIterationNumber = nIterationNumber;
    }

    /**
     * {@inheritDoc}
     * 
     * @throws CloneNotSupportedException
     */
    @Override
    public Question clone( ) throws CloneNotSupportedException
    {
        Question question = new Question( );

        try
        {
            question = (Question) super.clone( );
        }
        catch( CloneNotSupportedException e )
        {
            AppLogService.error( e );
        }

        question._entry = _entry;
        question._nId = _nId;
        question._nIdEntry = _nIdEntry;
        question._nIdStep = _nIdStep;
        question._nIterationNumber = _nIterationNumber;
        question._step = _step;
        question._strDescription = _strDescription;
        question._strTitle = _strTitle;
        question._bIsVisible = _bIsVisible;

        return question;
    }

    /**
     * Return the responses index boolean
     * @return true if the responses of this question are indexed, false otherwise
     */
    public boolean isResponsesIndexed() 
    {
        return _bIsResponsesIndexed;
    }

    /**
     * Set the responses indexed boolean
     * @param bIsResponsesIndexed  the responses index boolean
     */
    public void setResponsesIndexed( boolean bIsResponsesIndexed) 
    {
        _bIsResponsesIndexed = bIsResponsesIndexed;
    }
}
