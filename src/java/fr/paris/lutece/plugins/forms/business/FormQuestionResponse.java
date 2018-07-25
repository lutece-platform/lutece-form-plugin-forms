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

import java.util.List;

import fr.paris.lutece.plugins.genericattributes.business.Response;

/**
 * This is the business class for the object FormQuestionResponse
 */
public class FormQuestionResponse
{
    private int _nId;

    private int _nIdFormResponse;

    private int _nIdQuestion;

    private Question _question;

    private int _nIdStep;

    private int _nIterationNumber;

    private boolean _bFromSave;

    private List<Response> _entryResponses;

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
     * @return the _nIdFormResponse
     */
    public int getIdFormResponse( )
    {
        return _nIdFormResponse;
    }

    /**
     * @param nIdFormResponse
     *            the nIdFormResponse to set
     */
    public void setIdFormResponse( int nIdFormResponse )
    {
        this._nIdFormResponse = nIdFormResponse;
    }

    /**
     * @return the nIdQuestion
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
     * @return the _question
     */
    public Question getQuestion( )
    {
        return _question;
    }

    /**
     * @param question
     *            the question to set
     */
    public void setQuestion( Question question )
    {
        this._question = question;
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
     * @return the _nIterationNumber
     */
    public int getIterationNumber( )
    {
        return _nIterationNumber;
    }

    /**
     * @param nIterationNumber
     *            the nIterationNumber to set
     */
    public void setIterationNumber( int nIterationNumber )
    {
        this._nIterationNumber = nIterationNumber;
    }

    /**
     * @return the _bFromSave
     */
    public boolean getFromSave( )
    {
        return _bFromSave;
    }

    /**
     * @param bFromSave
     *            the bFromSave to set
     */
    public void setFromSave( boolean bFromSave )
    {
        this._bFromSave = bFromSave;
    }

    /**
     * @return the _entryResponse
     */
    public List<Response> getEntryResponse( )
    {
        return _entryResponses;
    }

    /**
     * @param entryResponse
     *            the entryResponse to set
     */
    public void setEntryResponse( List<Response> entryResponse )
    {
        this._entryResponses = entryResponse;
    }

}
