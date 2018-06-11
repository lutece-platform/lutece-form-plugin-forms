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
package fr.paris.lutece.plugins.forms.business.form.panel.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.form.FormResponseItem;
import fr.paris.lutece.plugins.forms.business.form.panel.IFormPanel;
import fr.paris.lutece.plugins.forms.business.form.panel.configuration.FormPanelConfiguration;

/**
 * Abstract class for implementation of the IFormPanel
 */
public abstract class AbstractFormPanel implements IFormPanel
{
    // Constants
    private static final String DEFAULT_FORM_PANEL_TITLE = StringUtils.EMPTY;
    private static final String DEFAULT_FORM_PANEL_TECHNICAL_CODE = StringUtils.EMPTY;

    // Variables
    private FormPanelConfiguration _formPanelConfiguration;
    private List<FormResponseItem> _listFormResponse = new ArrayList<>( );

    /**
     * {@inheritDoc}
     */
    @Override
    public FormPanelConfiguration getFormPanelConfiguration( )
    {
        return _formPanelConfiguration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle( )
    {
        String strTitle = DEFAULT_FORM_PANEL_TITLE;

        if ( _formPanelConfiguration != null )
        {
            strTitle = _formPanelConfiguration.getTitle( );
        }

        return strTitle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTechnicalCode( )
    {
        String strTechnicalCode = DEFAULT_FORM_PANEL_TECHNICAL_CODE;

        if ( _formPanelConfiguration != null )
        {
            strTechnicalCode = _formPanelConfiguration.getTechnicalCode( );
        }

        return strTechnicalCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FormResponseItem> getFormResponseItemList( )
    {
        return _listFormResponse;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFormResponseItemList( List<FormResponseItem> listFormResponseItem )
    {
        _listFormResponse = listFormResponseItem;
    }

    /**
     * Set the FormPanelConfiguration of the FormPanel
     * 
     * @param formPanelConfiguration
     *            The FormPanelconfiguration to set the FormPanel
     */
    protected void setFormPanelConfiguration( FormPanelConfiguration formPanelConfiguration )
    {
        _formPanelConfiguration = formPanelConfiguration;
    }
}
