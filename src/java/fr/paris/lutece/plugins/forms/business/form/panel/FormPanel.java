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
package fr.paris.lutece.plugins.forms.business.form.panel;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.form.FormResponseItem;
import fr.paris.lutece.plugins.forms.business.form.panel.configuration.IFormPanelConfiguration;
import fr.paris.lutece.plugins.forms.business.form.panel.initializer.IFormPanelInitializer;

/**
 * Abstract class for implementation of the IFormPanel
 */
public class FormPanel
{
    // Constants
    private static final String DEFAULT_FORM_PANEL_TITLE = StringUtils.EMPTY;
    private static final String DEFAULT_FORM_PANEL_TECHNICAL_CODE = StringUtils.EMPTY;

    // Variables
    private IFormPanelConfiguration _formPanelConfiguration;
    private List<FormResponseItem> _listFormResponse = new ArrayList<>( );
    private List<IFormPanelInitializer> _listFormPanelInitializer = new ArrayList<>( );

    /**
     * Return the FormPanelConfiguration of the FormPanel. This configuration contains all informations of the FormPanel.
     * 
     * @return the FormPanelConfiguration of the FormPanel
     */
    public IFormPanelConfiguration getFormPanelConfiguration( )
    {
        return _formPanelConfiguration;
    }

    /**
     * Return the title of the panel from the configuration. This is the title which will be display on the table view.
     * 
     * @return the title of the panel from the configuration
     */
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
     * Return the technical code of the panel from the configuration. This code is used as unique identifier for a FormPanel to manage the case of the selected
     * FormPanel.
     * 
     * @return the technical code of the panel from the configuration
     */
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
     * Return the list of FormResponseItem of the Panel
     * 
     * @return the list of all FormResponseItem of the Panel
     */
    public List<FormResponseItem> getFormResponseItemList( )
    {
        return _listFormResponse;
    }

    /**
     * Set the list of FormResponseItem
     * 
     * @param listFormResponseItem
     *            The list of FormResponseItem to set to the Panel
     */
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
    public void setFormPanelConfiguration( IFormPanelConfiguration formPanelConfiguration )
    {
    	_formPanelConfiguration = formPanelConfiguration;
    }

    /**
     * Return the list of FormPanelInitializer class name of the FormPanelConfiguration
     * 
     * @return the list of FormPanelInitializer class name of the FormPanelConfiguration
     */
    public List<IFormPanelInitializer> getListFormPanelInitializer( )
    {
        return _listFormPanelInitializer;
    }

    /**
     * Set the list of IFormPanelInitializer
     * 
     * @param listFormPanelInitializer
     *            The list of IFormPanelInitializer to set to the Panel
     */
    public void setListFormPanelInitializer( List<IFormPanelInitializer> listFormPanelInitializer )
    {
        _listFormPanelInitializer = listFormPanelInitializer;
    }
    
}
