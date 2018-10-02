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
package fr.paris.lutece.plugins.forms.business.form.panel.configuration;

import java.util.List;

/**
 * Configuration for a FormPanel
 */
public class FormPanelConfiguration implements IFormPanelConfiguration
{
    // Variables
    private final int _nPosition;
    private final String _strTechnicalCode;
    private final String _strTitle;
    private final List<String> _listFormPanelInitializerName;

    /**
     * Constructor
     * 
     * @param strTechnicalCode
     *            The technical code of the FormPanel
     * @param nPosition
     *            The position of the FormPanel
     * @param strTitle
     *            The title of the FormPanel
     * @param listFormPanelInitializerName
     *            The list of all FormPanelInitializer names used to built the initialize list of the table associated to the given panel
     */
    public FormPanelConfiguration( String strTechnicalCode, int nPosition, String strTitle, List<String> listFormPanelInitializerName )
    {
        _strTechnicalCode = strTechnicalCode;
        _nPosition = nPosition;
        _strTitle = strTitle;
        _listFormPanelInitializerName = listFormPanelInitializerName;
    }

    @Override
    public String getTechnicalCode( )
    {
        return _strTechnicalCode;
    }

    @Override
    public int getPosition( )
    {
        return _nPosition;
    }

    @Override
    public String getTitle( )
    {
        return _strTitle;
    }

    @Override
    public List<String> getListFormPanelInitializerName( )
    {
        return _listFormPanelInitializerName;
    }
}
