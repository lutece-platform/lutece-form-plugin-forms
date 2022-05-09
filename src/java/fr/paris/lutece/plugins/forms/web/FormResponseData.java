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
package fr.paris.lutece.plugins.forms.web;

import java.sql.Date;

public class FormResponseData
{
    private int _nIdFormResponse;
    private String _strFormTitle;
    private String _strWorkflowState;
    private Date _dateUpdate;

    /**
     * @return the nIdFormResponse
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
        _nIdFormResponse = nIdFormResponse;
    }

    /**
     * @return the strFormTitle
     */
    public String getFormTitle( )
    {
        return _strFormTitle;
    }

    /**
     * @param strFormTitle
     *            the strFormTitle to set
     */
    public void setFormTitle( String strFormTitle )
    {
        _strFormTitle = strFormTitle;
    }

    /**
     * @return the strWorkflowState
     */
    public String getWorkflowState( )
    {
        return _strWorkflowState;
    }

    /**
     * @param strWorkflowState
     *            the strWorkflowState to set
     */
    public void setWorkflowState( String strWorkflowState )
    {
        _strWorkflowState = strWorkflowState;
    }

    /**
     * @return the dateUpdate
     */
    public Date getDateUpdate( )
    {
        return _dateUpdate;
    }

    /**
     * @param dateUpdate
     *            the dateUpdate to set
     */
    public void setDateUpdate( Date dateUpdate )
    {
        _dateUpdate = dateUpdate;
    }
}
