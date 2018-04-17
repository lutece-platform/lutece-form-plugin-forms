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

import java.io.Serializable;

/**
 * This is the business class for the object Transition
 */
public class Transition implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations
    private int _nId;

    private int _nFromStep;

    private int _nNextStep;

    private int _nIdControl;

    private int _nPriority;

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
     * Returns the FromStep
     * 
     * @return The FromStep
     */
    public int getFromStep( )
    {
        return _nFromStep;
    }

    /**
     * Sets the FromStep
     * 
     * @param nFromStep
     *            The FromStep
     */
    public void setFromStep( int nFromStep )
    {
        _nFromStep = nFromStep;
    }

    /**
     * Returns the NextStep
     * 
     * @return The NextStep
     */
    public int getNextStep( )
    {
        return _nNextStep;
    }

    /**
     * Sets the NextStep
     * 
     * @param nNextStep
     *            The NextStep
     */
    public void setNextStep( int nNextStep )
    {
        _nNextStep = nNextStep;
    }

    /**
     * Returns the IdControl
     * 
     * @return The IdControl
     */
    public int getIdControl( )
    {
        return _nIdControl;
    }

    /**
     * Sets the IdControl
     * 
     * @param nIdControl
     *            The IdControl
     */
    public void setIdControl( int nIdControl )
    {
        _nIdControl = nIdControl;
    }

    /**
     * Returns the Priority
     * 
     * @return The Priority
     */
    public int getPriority( )
    {
        return _nPriority;
    }

    /**
     * Sets the Priority
     * 
     * @param nPriority
     *            The Priority
     */
    public void setPriority( int nPriority )
    {
        _nPriority = nPriority;
    }
}
