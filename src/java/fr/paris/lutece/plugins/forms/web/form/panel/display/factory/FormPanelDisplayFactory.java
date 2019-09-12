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
package fr.paris.lutece.plugins.forms.web.form.panel.display.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.form.panel.FormPanel;
import fr.paris.lutece.plugins.forms.business.form.panel.configuration.IFormPanelConfiguration;
import fr.paris.lutece.plugins.forms.business.form.panel.initializer.IFormPanelInitializer;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.web.form.multiview.util.FormListPositionComparator;
import fr.paris.lutece.plugins.forms.web.form.panel.display.IFormPanelDisplay;
import fr.paris.lutece.plugins.forms.web.form.panel.display.initializer.IFormPanelDisplayInitializer;
import fr.paris.lutece.plugins.forms.web.form.panel.display.initializer.factory.FormDisplayInitializerFactoryFacade;
import fr.paris.lutece.plugins.forms.web.form.panel.display.initializer.factory.IFormPanelDisplayInitializerFactory;

/**
 * Factory for FormPanel objects
 */
public class FormPanelDisplayFactory
{
    // Constants
    private static final int INDEX_LIST_PANEL_FIRST_POSITION = NumberUtils.INTEGER_ZERO;
    private static final boolean ACTIVE_LIST_PANEL = Boolean.TRUE;

    /**
     * Create the list of all FormPanelDisplay ordered by their position
     * 
     * @param request
     *            The HttpServletRequest to retrieve the information from
     * @param listFormPanel
     *            The list of all IFormPanel used for building IFormFilterDisplay
     * @param activeFormPanelDisplay
     * @return the list of all FormPanelDisplay ordered by their position
     */
    public List<IFormPanelDisplay> createFormPanelDisplayList( HttpServletRequest request, List<FormPanel> listFormPanel,
            IFormPanelDisplay activeFormPanelDisplay )
    {
        List<IFormPanelDisplay> listFormPanelDisplay = new ArrayList<>( );
        List<IFormPanelDisplayFactory> listFormPanelDisplayFactory = new FormPanelDisplayFactoryFacade( ).buildFormPanelDisplayFactoryList( );

        if ( !CollectionUtils.isEmpty( listFormPanelDisplayFactory ) )
        {
            IFormPanelDisplay formPanelDisplay = null;
            boolean bIsFirst = true;
            for ( FormPanel formPanel : listFormPanel )
            {
                for ( IFormPanelDisplayFactory formPanelDisplayFactory : listFormPanelDisplayFactory )
                {
                    formPanelDisplay = formPanelDisplayFactory.buildFormPanelDisplay( formPanel );
                    if ( formPanelDisplay != null )
                    {
                        configureFormPanelDisplay( request, formPanelDisplay, activeFormPanelDisplay, bIsFirst );
                        bIsFirst = false;
                        listFormPanelDisplay.add( formPanelDisplay );
                        break;
                    }
                }
            }

            // Sort the list by the position of each elements
            Collections.sort( listFormPanelDisplay, new FormListPositionComparator( ) );

            // Manage the active FormDisplayPanel of the list
            manageActiveFormPanelDisplay( listFormPanelDisplay );
        }

        return listFormPanelDisplay;
    }

    /**
     * Configure the FormPanelDisplay by defining if its active or not and build all the FormPanelInitializer with their FormParameters from its FormPanel
     * 
     * @param request
     *            The request to retrieve the information from
     * @param formPanelDisplay
     *            The formPanelInitializer to configure with the information from the request
     */
    private void configureFormPanelDisplay( HttpServletRequest request, IFormPanelDisplay formPanelDisplay, IFormPanelDisplay activeFormPanelDisplay,
            boolean bIsFirst )
    {
        boolean bIsSelectedPanel = isSelectedPanel( request, formPanelDisplay.getTechnicalCode( ), activeFormPanelDisplay, bIsFirst );
        formPanelDisplay.setActive( bIsSelectedPanel );

        buildFormPanelDisplayInitializer( request, formPanelDisplay.getFormPanel( ) );
    }

    /**
     * Check if the panel is the selected panel or not. Activate the default panel if not found
     * 
     * @param request
     *            The HttpServletRequest to retrieve the information from
     * @param strPanelTechnicalCode
     *            The name of the panel to analyze
     * @return true if the panel of the given name is the panel to analyze false otherwise
     */
    private boolean isSelectedPanel( HttpServletRequest request, String strPanelTechnicalCode, IFormPanelDisplay activeFormPanelDisplay, boolean bIsFirst )
    {
        boolean bIsSelectedPanel = Boolean.FALSE;

        // We will retrieve the name of the current selected panel
        String strFormPanelSelected = request.getParameter( FormsConstants.PARAMETER_SELECTED_PANEL );
        if ( StringUtils.isNotBlank( strFormPanelSelected ) )
        {
            bIsSelectedPanel = strPanelTechnicalCode.equals( strFormPanelSelected );
        }
        else
        {
            // If the previous selected panel cannot be found in the request, we look into the session-based formPanelDisplay
            if ( activeFormPanelDisplay != null )
            {
                bIsSelectedPanel = strPanelTechnicalCode.equals( activeFormPanelDisplay.getTechnicalCode( ) );
            }
            else
            {
                if ( request.getParameter( FormsConstants.PARAMETER_CURRENT_SELECTED_PANEL ) != null )
                {
                    String strPreviousFormPanelSelected = request.getParameter( FormsConstants.PARAMETER_CURRENT_SELECTED_PANEL );
                    if ( StringUtils.isNotBlank( strPreviousFormPanelSelected ) )
                    {
                        bIsSelectedPanel = strPanelTechnicalCode.equals( strPreviousFormPanelSelected );
                    }
                }
                else
                    if ( bIsFirst )
                    {
                        bIsSelectedPanel = true;
                    }
            }
        }

        return bIsSelectedPanel;
    }

    /**
     * Build the list of all FormPanelDisplayInitializer of all FormPanelInitializer of the FormPänel
     * 
     * @param request
     *            The HttpServletRequest used to build the list of all FormPanelDisplayInitializer
     * @param formPanel
     *            The IFormPanel to the FormPanelInitializer from
     */
    public void buildFormPanelDisplayInitializer( HttpServletRequest request, FormPanel formPanel )
    {
        List<IFormPanelDisplayInitializerFactory> listFormPanelDisplayInitializerFactory = new FormDisplayInitializerFactoryFacade( )
                .buildFormPanelDisplayInitializerList( );

        if ( !CollectionUtils.isEmpty( listFormPanelDisplayInitializerFactory ) && formPanel != null )
        {
            IFormPanelConfiguration formPanelConfiguration = formPanel.getFormPanelConfiguration( );

            if ( formPanelConfiguration != null )
            {
                for ( IFormPanelDisplayInitializerFactory formPanelDisplayInitializerFactory : listFormPanelDisplayInitializerFactory )
                {
                    List<IFormPanelInitializer> listFormPanelInitializer = formPanel.getListFormPanelInitializer( );

                    buildPanelDisplayInitializerFormParameters( request, formPanelDisplayInitializerFactory, listFormPanelInitializer );
                }
            }
        }
    }

    /**
     * Build the FormPanelDisplayInitializer associated to the given IFormPanelDisplayInitializerFactory from the specified list of IFormPanelInitializer and
     * build its FormParameters with the request
     * 
     * @param request
     *            The request used to build the FormParameters of the FormPanelInitializer
     * @param formPanelDisplayInitializerFactory
     *            The IFormPanelDisplayInitializerFactory used to build the IFormPanelDisplayInitializer
     * @param listFormPanelInitializer
     *            The list of all FormPanelInitializer to retrieve those which is associated to the given IFormPanelDisplayInitializerFactory
     */
    private void buildPanelDisplayInitializerFormParameters( HttpServletRequest request,
            IFormPanelDisplayInitializerFactory formPanelDisplayInitializerFactory, List<IFormPanelInitializer> listFormPanelInitializer )
    {
        if ( formPanelDisplayInitializerFactory != null && !CollectionUtils.isEmpty( listFormPanelInitializer ) )
        {
            for ( IFormPanelInitializer formPanelInitializer : listFormPanelInitializer )
            {
                IFormPanelDisplayInitializer formPanelDisplayInitializer = formPanelDisplayInitializerFactory.buildFormPanelDisplay( formPanelInitializer );
                if ( formPanelDisplayInitializer != null )
                {
                    formPanelDisplayInitializer.setFormPanelInitializer( formPanelInitializer );
                    formPanelDisplayInitializer.buildFormParameters( request );
                }
            }
        }
    }

    /**
     * Check if there is an active FormDisplay Panel in the given list and if there is no one set the first element in the list (which is in first position) as
     * active
     * 
     * @param listFormPanelDisplay
     *            The list of FormDisplayPanel to analyze
     */
    private void manageActiveFormPanelDisplay( List<IFormPanelDisplay> listFormPanelDisplay )
    {
        boolean bActivePanelPresent = Boolean.FALSE;

        for ( IFormPanelDisplay formPanelDisplaySorted : listFormPanelDisplay )
        {
            if ( formPanelDisplaySorted.isActive( ) )
            {
                bActivePanelPresent = Boolean.TRUE;
                break;
            }
        }

        if ( CollectionUtils.isNotEmpty( listFormPanelDisplay ) && !bActivePanelPresent )
        {
            listFormPanelDisplay.get( INDEX_LIST_PANEL_FIRST_POSITION ).setActive( ACTIVE_LIST_PANEL );
        }
    }
}
