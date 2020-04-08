/*
 * Copyright (c) 2002-2020, City of Paris
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

/**
 * This is the business class for the object FormResponse
 */
public class FormResponseStep
{

    private int _nId;

    private int _nFormResponseId;

    private Step _step;

    private int _nOrder;

    private List<FormQuestionResponse> _listFormQuestionResponse;

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
     * @return the _nFormResponseId
     */
    public int getFormResponseId( )
    {
        return _nFormResponseId;
    }

    /**
     * @param nFormResponseId
     *            the nFormResponseId to set
     */
    public void setFormResponseId( int nFormResponseId )
    {
        this._nFormResponseId = nFormResponseId;
    }

    /**
     * Gives the step associated to this response step
     * 
     * @return the step
     */
    public Step getStep( )
    {
        return _step;
    }

    /**
     * Sets the step associated to this response step
     * 
     * @param step
     *            the step
     */
    public void setStep( Step step )
    {
        _step = step;
    }

    /**
     * @return the _nOrder
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
        this._nOrder = nOrder;
    }

    /**
     * Gives the questions containing responses for this step
     * 
     * @return the questions
     */
    public List<FormQuestionResponse> getQuestions( )
    {
        return _listFormQuestionResponse;
    }

    /**
     * Sets the questions containing responses for this step
     * 
     * @param listFormQuestionResponse
     *            the list of questions
     */
    public void setQuestions( List<FormQuestionResponse> listFormQuestionResponse )
    {
        _listFormQuestionResponse = listFormQuestionResponse;
    }

}
