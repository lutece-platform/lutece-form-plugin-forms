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
package fr.paris.lutece.plugins.forms.business.form.panel;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.forms.business.form.panel.configuration.IFormPanelConfiguration;
import fr.paris.lutece.plugins.forms.business.form.panel.initializer.IFormPanelInitializer;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;

/**
 * Factory for all FormPanel
 */
public class FormPanelFactory
{
    // Variables
    private final List<IFormPanelConfiguration> _listFormPanelConfiguration;

    /**
     * Constructor
     */
    public FormPanelFactory( )
    {
        _listFormPanelConfiguration = SpringContextService.getBeansOfType( IFormPanelConfiguration.class );
    }

    /**
     * Constructor
     * 
     * @param listFormPanelConfiguration
     *            The list of FormPanelConfiguration to use for the Factory
     */
    public FormPanelFactory( List<IFormPanelConfiguration> listFormPanelConfiguration )
    {
        _listFormPanelConfiguration = listFormPanelConfiguration;
    }

    /**
     * Build the list of all FormPanel
     * 
     * @return the list of all FormPanel
     */
    public List<FormPanel> buildFormPanelList( )
    {
        List<FormPanel> listFormPanelForms = new ArrayList<>( );

        for ( IFormPanelConfiguration formPanelConfiguration : _listFormPanelConfiguration )
        {
            listFormPanelForms.add( buildFormPanel( formPanelConfiguration ) );
        }

        return listFormPanelForms;
    }

    /**
     * 
     * @param formPanelConfiguration
     *            The FormPanelConfiguration to use for the FormPanel
     * @return the list of all FormPanel
     */
    public FormPanel buildFormPanel( IFormPanelConfiguration formPanelConfiguration )
    {
        FormPanel formPanel = new FormPanel( );
        formPanel.setFormPanelConfiguration( formPanelConfiguration );

        for ( String strInitializerName : formPanelConfiguration.getListFormPanelInitializerName( ) )
        {
            Class<? extends IFormPanelInitializer> formPanelInitializerClass;
            try
            {
                formPanelInitializerClass = Class.forName( strInitializerName ).asSubclass( IFormPanelInitializer.class );
                IFormPanelInitializer formPanelInitializer = formPanelInitializerClass.newInstance( );
                formPanel.getListFormPanelInitializer( ).add( formPanelInitializer );
            }
            catch( ClassNotFoundException | InstantiationException | IllegalAccessException e )
            {
                AppLogService.error( e );
            }
        }

        return formPanel;
    }

}
