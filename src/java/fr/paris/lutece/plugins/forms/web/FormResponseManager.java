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

package fr.paris.lutece.plugins.forms.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Step;

/**
 * 
 * Class for breadcrumb management and responses history
 *
 */
public class FormResponseManager
{
    private List<Step> _listValidatedStep;

    private Map<Integer, List<FormQuestionResponse>> _mapStepFormResponses;

    private FormResponse _formResponse;

    /**
     * Constructor
     */
    public FormResponseManager( )
    {
        _listValidatedStep = new ArrayList<Step>( );
        _mapStepFormResponses = new HashMap<Integer, List<FormQuestionResponse>>( );
    }

    /**
     * @return the _listValidatedStep
     */
    public List<Step> getListValidatedStep( )
    {
        return _listValidatedStep;
    }

    /**
     * @param listValidatedStep
     *            the listValidatedStep to set
     */
    public void setListValidatedStep( List<Step> listValidatedStep )
    {
        this._listValidatedStep = listValidatedStep;
    }

    /**
     * @return the _mapStepFormResponses
     */
    public Map<Integer, List<FormQuestionResponse>> getMapStepFormResponses( )
    {
        return _mapStepFormResponses;
    }

    /**
     * @param mapStepFormResponses
     *            the mapStepFormResponses to set
     */
    public void setMapStepFormResponses( Map<Integer, List<FormQuestionResponse>> mapStepFormResponses )
    {
        this._mapStepFormResponses = mapStepFormResponses;
    }

    /**
     * @return the _formResponse
     */
    public FormResponse getFormResponse( )
    {
        return _formResponse;
    }

    /**
     * @param formResponse
     *            the formResponse to set
     */
    public void setFormResponse( FormResponse formResponse )
    {
        this._formResponse = formResponse;
    }

}
