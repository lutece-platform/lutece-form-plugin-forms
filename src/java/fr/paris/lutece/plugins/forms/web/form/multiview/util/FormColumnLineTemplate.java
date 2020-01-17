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
package fr.paris.lutece.plugins.forms.web.form.multiview.util;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represent a line of the Multiview Forms table
 */
public class FormColumnLineTemplate
{
    // Variables
    private final int _nIdFormResponse;
    private List<String> _listFormColumnCellTemplate;

    /**
     * Constructor
     * 
     * @param nIdFormResponse
     *            The identifier of the FormResponse associate to the FormColumnLineTemplate
     */
    public FormColumnLineTemplate( int nIdFormResponse )
    {
        _nIdFormResponse = nIdFormResponse;
        _listFormColumnCellTemplate = new ArrayList<>( );
    }

    /**
     * Return the identifier of the form response of the FormColumnLineTemplate
     * 
     * @return the identifier of the form response of the FormColumnLineTemplate
     */
    public int getIdFormResponse( )
    {
        return _nIdFormResponse;
    }

    /**
     * Return the list of the FormColumnCell template
     * 
     * @return the list of the FormColumnCell template
     */
    public List<String> getFormColumnCellTemplateList( )
    {
        return _listFormColumnCellTemplate;
    }

    /**
     * Set the list of the FormColumnCell template
     * 
     * @param listFormColumnCellTemplate
     *            The list of the FormColumnCell template
     */
    public void setFormColumnCellTemplate( List<String> listFormColumnCellTemplate )
    {
        _listFormColumnCellTemplate = listFormColumnCellTemplate;
    }

    /**
     * Add a form column cell template
     * 
     * @param strFormColumnCell
     *            The form column cell template to add to the FormColumn
     */
    public void addFormColumnCellTemplate( String strFormColumnCell )
    {
        _listFormColumnCellTemplate.add( strFormColumnCell );
    }
}
