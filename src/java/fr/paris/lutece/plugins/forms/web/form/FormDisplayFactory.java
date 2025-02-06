/*
 * Copyright (c) 2002-2025, City of Paris
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
package fr.paris.lutece.plugins.forms.web.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.filter.FormFilter;
import fr.paris.lutece.plugins.forms.business.form.filter.configuration.IFormFilterConfiguration;
import fr.paris.lutece.plugins.forms.business.form.panel.FormPanel;
import fr.paris.lutece.plugins.forms.business.form.panel.initializer.IFormPanelInitializer;
import fr.paris.lutece.plugins.forms.service.MultiviewFormService;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.web.form.column.display.IFormColumnDisplay;
import fr.paris.lutece.plugins.forms.web.form.filter.display.IFormFilterDisplay;
import fr.paris.lutece.plugins.forms.web.form.multiview.util.FormListPositionComparator;
import fr.paris.lutece.plugins.forms.web.form.panel.display.IFormPanelDisplay;
import fr.paris.lutece.plugins.forms.web.form.panel.display.initializer.IFormPanelDisplayInitializer;

/**
 * Factory for form display & filters
 */
public class FormDisplayFactory
{
    // Constants
    private static final int INDEX_LIST_PANEL_FIRST_POSITION = NumberUtils.INTEGER_ZERO;

    private FormDisplayFactory( )
    {
    }

    /**
     * Return the list of all FormFilterDisplay ordered by their position
     * 
     * @param request
     *            The HttpServletRequest used to create the list of all FormFilterDisplay
     * @param listFormFilter
     *            The list of IFormFilter used for building IFormFilterDisplay
     * @return the list of all FormFilterDisplay ordered by their position
     */
    public static List<IFormFilterDisplay> createFormFilterDisplayList( HttpServletRequest request, List<FormFilter> listFormFilter )
    {
        if ( CollectionUtils.isEmpty( listFormFilter ) )
        {
            return new ArrayList<>( );
        }

        List<IFormFilterDisplay> listFormFilterDisplay = new ArrayList<>( );
        for ( FormFilter formFilter : listFormFilter )
        {
            IFormFilterConfiguration formFilterConfiguration = formFilter.getFormFilterConfiguration( );
            if ( formFilterConfiguration != null )
            {
                IFormFilterDisplay formFilterDisplay = formFilterConfiguration.getFormFilterDisplay( formFilter );
                formFilterDisplay.createFormParameters( request );
                formFilterDisplay.setPosition( formFilterConfiguration.getPosition( ) );

                listFormFilterDisplay.add( formFilterDisplay );
            }
        }
        // Sort the list by the position of each elements
        Collections.sort( listFormFilterDisplay, new FormListPositionComparator( ) );
        return listFormFilterDisplay;
    }

    /**
     * Create the list of all FormColumnDisplay ordered by their position
     * 
     * @param listFormColumn
     *            The list of IFormColumn to use for build the list of FormColumnDisplay
     * @return the list of all FormColumnDisplay ordered by their position
     */
    public static List<IFormColumnDisplay> createFormColumnDisplayList( Collection<IFormColumn> listFormColumn )
    {
        if ( CollectionUtils.isEmpty( listFormColumn ) )
        {
            return new ArrayList<>( );
        }

        List<IFormColumnDisplay> listFormColumnDisplay = new ArrayList<>( );
        for ( IFormColumn formColumn : listFormColumn )
        {
            IFormColumnDisplay formColumnDisplay = formColumn.getFormColumnDisplay( );
            if ( formColumnDisplay != null )
            {
                formColumnDisplay.setPosition( formColumn.getFormColumnPosition( ) );
                listFormColumnDisplay.add( formColumnDisplay );
            }
        }
        // Sort the list by the position of each elements
        Collections.sort( listFormColumnDisplay, new FormListPositionComparator( ) );
        return listFormColumnDisplay;
    }

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
    public static List<IFormPanelDisplay> createFormPanelDisplayList( HttpServletRequest request, List<FormPanel> listFormPanel,
            IFormPanelDisplay activeFormPanelDisplay )
    {
        List<IFormPanelDisplay> listFormPanelDisplay = new ArrayList<>( );
        boolean bIsFirst = true;
        for ( FormPanel formPanel : listFormPanel )
        {
            if ( formPanel.getFormPanelConfiguration( ) != null )
            {
                IFormPanelDisplay formPanelDisplay = formPanel.getFormPanelConfiguration( ).getFormPanelDisplay( formPanel );
                if ( formPanelDisplay != null )
                {
                    configureFormPanelDisplay( request, formPanelDisplay, activeFormPanelDisplay, bIsFirst );
                    bIsFirst = false;
                    listFormPanelDisplay.add( formPanelDisplay );
                }
            }
        }
        // Sort the list by the position of each elements
        Collections.sort( listFormPanelDisplay, new FormListPositionComparator( ) );

        // Manage the active FormDisplayPanel of the list
        manageActiveFormPanelDisplay( listFormPanelDisplay );
        return listFormPanelDisplay;
    }

    /**
     * Build the list of all FormPanelDisplayInitializer of all FormPanelInitializer of the FormPänel
     * 
     * @param request
     *            The HttpServletRequest used to build the list of all FormPanelDisplayInitializer
     * @param formPanel
     *            The IFormPanel to the FormPanelInitializer from
     */
    public static void buildFormPanelDisplayInitializer( HttpServletRequest request, FormPanel formPanel )
    {
        if ( formPanel != null && formPanel.getFormPanelConfiguration( ) != null )
        {
            List<IFormPanelInitializer> listFormPanelInitializer = formPanel.getListFormPanelInitializer( );
            if ( !CollectionUtils.isEmpty( listFormPanelInitializer ) )
            {
                for ( IFormPanelInitializer formPanelInitializer : listFormPanelInitializer )
                {
                    IFormPanelDisplayInitializer formPanelDisplayInitializer = formPanelInitializer.getFormPanelDisplayInitializer( );
                    if ( formPanelDisplayInitializer != null )
                    {
                        formPanelDisplayInitializer.setFormPanelInitializer( formPanelInitializer );
                        formPanelDisplayInitializer.buildFormParameters( request );
                    }
                }
            }
        }
    }

    /**
     * Build the list of all FormFilter
     * 
     * @return the list of all FormFilter
     */
    public static List<FormFilter> buildFormFilterList( Integer nIdForm, List<IFormColumn> listFormColumns, Locale locale, User user )
    {
        return MultiviewFormService.getInstance( ).getFormFiltersList( nIdForm, listFormColumns, locale, user );

    }

    /**
     * Configure the FormPanelDisplay by defining if its active or not and build all the FormPanelInitializer with their FormParameters from its FormPanel
     * 
     * @param request
     *            The request to retrieve the information from
     * @param formPanelDisplay
     *            The formPanelInitializer to configure with the information from the request
     */
    private static void configureFormPanelDisplay( HttpServletRequest request, IFormPanelDisplay formPanelDisplay, IFormPanelDisplay activeFormPanelDisplay,
            boolean bIsFirst )
    {
        boolean bIsSelectedPanel = isSelectedPanel( request, formPanelDisplay.getTechnicalCode( ), activeFormPanelDisplay, bIsFirst );
        formPanelDisplay.setActive( bIsSelectedPanel );

        buildFormPanelDisplayInitializer( request, formPanelDisplay.getFormPanel( ) );
    }

    /**
     * Check if there is an active FormDisplay Panel in the given list and if there is no one set the first element in the list (which is in first position) as
     * active
     * 
     * @param listFormPanelDisplay
     *            The list of FormDisplayPanel to analyze
     */
    private static void manageActiveFormPanelDisplay( List<IFormPanelDisplay> listFormPanelDisplay )
    {
        boolean bActivePanelPresent = false;

        for ( IFormPanelDisplay formPanelDisplaySorted : listFormPanelDisplay )
        {
            if ( formPanelDisplaySorted.isActive( ) )
            {
                bActivePanelPresent = true;
                break;
            }
        }

        if ( CollectionUtils.isNotEmpty( listFormPanelDisplay ) && !bActivePanelPresent )
        {
            listFormPanelDisplay.get( INDEX_LIST_PANEL_FIRST_POSITION ).setActive( true );
        }
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
    private static boolean isSelectedPanel( HttpServletRequest request, String strPanelTechnicalCode, IFormPanelDisplay activeFormPanelDisplay,
            boolean bIsFirst )
    {
        boolean bIsSelectedPanel = false;

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
}
