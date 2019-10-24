/*
 * Copyright (c) 2002-2019, Mairie de Paris
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
package fr.paris.lutece.plugins.forms.business.form;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.forms.business.form.column.FormColumnCell;

/**
 * This object represent the identifier of a form of a line of the Multiview form list with the list of all its FormColumnCell
 */
public class FormResponseItem
{
    // Variables
    private int _nIdFormResponse;
    private final List<FormColumnCell> _listFormColumnCell;

    /**
     * Constructor
     */
    public FormResponseItem( )
    {
        _listFormColumnCell = new ArrayList<>( );
    }

    /**
     * Return the identifier of the form response of the FormResponseItem
     * 
     * @return the identifier of the form response
     */
    public int getIdFormResponse( )
    {
        return _nIdFormResponse;
    }

    /**
     * Set the identifier of the FormResponse
     * 
     * @param nIdFormResponse
     *            The identifier of the FormResponse to set
     */
    public void setIdFormResponse( int nIdFormResponse )
    {
        _nIdFormResponse = nIdFormResponse;
    }

    /**
     * Return the list of all FormColumnCell for the FormResponseItem
     * 
     * @return the list of all FormColumnCell
     */
    public List<FormColumnCell> getFormColumnCellValues( )
    {
        return _listFormColumnCell;
    }

    /**
     * Add a FormColumnCell for the FormResponseItem
     * 
     * @param formColumnCell
     *            The FormColumnCell to add to the FormResponseItem
     */
    public void addFormColumnCell( FormColumnCell formColumnCell )
    {
        _listFormColumnCell.add( formColumnCell );
    }
}
