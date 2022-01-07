/*
 * Copyright (c) 2002-2022, City of Paris
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

import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;

/**
 * This is the business class for the object FormQuestionResponse
 */
public class FormQuestionResponse
{
    private int _nId;

    private int _nIdFormResponse;

    private Question _question;

    private int _nIdStep;

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
     * Gives the question associated to this question response
     * 
     * @return the question
     */
    public Question getQuestion( )
    {
        return _question;
    }

    /**
     * Sets the question associated to this question response
     * 
     * @param question
     *            the question
     */
    public void setQuestion( Question question )
    {
        _question = question;
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

    /**
     * Tests if the instance has an error
     * 
     * @return {@code true} if the instance has an error, {@code false} otherwise
     */
    public boolean hasError( )
    {
        boolean bHasError = false;

        if ( !_entryResponses.isEmpty( ) && _entryResponses.get( 0 ).getEntry( ) != null && _entryResponses.get( 0 ).getEntry( ).getError( ) != null )
        {
            bHasError = true;
        }

        return bHasError;
    }

    /**
     * Gives the error
     * 
     * @return the error
     */
    public GenericAttributeError getError( )
    {
        GenericAttributeError error = null;

        if ( hasError( ) )
        {
            error = _entryResponses.get( 0 ).getEntry( ).getError( );
        }

        return error;
    }

    /**
     * Sets an error
     * 
     * @param error
     *            the error to set
     */
    public void setError( GenericAttributeError error )
    {
        if ( !_entryResponses.isEmpty( ) && _entryResponses.get( 0 ).getEntry( ) != null )
        {
            _entryResponses.get( 0 ).getEntry( ).setError( error );
        }
    }

}
