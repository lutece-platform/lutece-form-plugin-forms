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
package fr.paris.lutece.plugins.forms.web.form.panel.display.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.form.FormResponseItem;
import fr.paris.lutece.plugins.forms.business.form.panel.FormPanel;
import fr.paris.lutece.plugins.forms.business.form.panel.configuration.IFormPanelConfiguration;
import fr.paris.lutece.plugins.forms.web.form.multiview.util.IFormListPosition;
import fr.paris.lutece.plugins.forms.web.form.panel.display.IFormPanelDisplay;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.template.AppTemplateService;

/**
 * Abstract class for FormPanelDisplay class
 */
public abstract class AbstractFormPanelDisplay implements IFormPanelDisplay, IFormListPosition
{
    // Template
    private static final String TEMPLATE_MULTIVIEW_FORM_PANEL = "admin/plugins/forms/multiview/panel/multiview_form_panel_template.html";

    // Marks
    private static final String MARK_PANEL_ACTIVE = "panel_active";
    private static final String MARK_PANEL_TECHNICAL_CODE = "panel_technical_code";
    private static final String MARK_PANEL_TITLE = "panel_title";
    private static final String MARK_PANEL_FORM_RESPONSE_NUMBER = "panel_formResponseNumber";

    // Constants
    private static final int DEFAULT_FORM_RESPONSE_NUMBER = NumberUtils.INTEGER_ZERO;
    private static final int DEFAULT_FORM_PANEL_POSITION = NumberUtils.INTEGER_MINUS_ONE;

    // Variables
    private boolean _bActive;
    private String _strTemplate;
    private FormPanel _formPanel;

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPosition( )
    {
        int nFormPanelPosition = DEFAULT_FORM_PANEL_POSITION;

        if ( _formPanel != null )
        {
            IFormPanelConfiguration formPanelConfiguration = _formPanel.getFormPanelConfiguration( );

            if ( formPanelConfiguration != null )
            {
                nFormPanelPosition = formPanelConfiguration.getPosition( );
            }
        }

        return nFormPanelPosition;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFormResponseNumber( )
    {
        int nFormResponseNumber = DEFAULT_FORM_RESPONSE_NUMBER;

        if ( _formPanel != null )
        {
            nFormResponseNumber = _formPanel.getTotalFormResponseItemCount( );
        }

        return nFormResponseNumber;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplate( )
    {
        return _strTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isActive( )
    {
        return _bActive;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setActive( boolean bActive )
    {
        _bActive = bActive;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTechnicalCode( )
    {
        String strTechnicalCode = StringUtils.EMPTY;

        FormPanel formPanel = getFormPanel( );
        if ( formPanel != null )
        {
            strTechnicalCode = formPanel.getTechnicalCode( );
        }

        return strTechnicalCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FormResponseItem> getFormResponseItemList( )
    {
        List<FormResponseItem> listFormResponseItemResult = new ArrayList<>( );

        if ( _formPanel != null )
        {
            listFormResponseItemResult = _formPanel.getFormResponseItemList( );
        }

        return listFormResponseItemResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FormPanel getFormPanel( )
    {
        return _formPanel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFormPanel( FormPanel formPanel )
    {
        _formPanel = formPanel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String buildTemplate( Locale locale )
    {
        String strTechnicalCode = StringUtils.EMPTY;
        String strTitle = StringUtils.EMPTY;

        FormPanel formPanel = getFormPanel( );
        if ( formPanel != null )
        {
            strTechnicalCode = formPanel.getTechnicalCode( );
            strTitle = formPanel.getTitle( );
        }

        Map<String, Object> model = new LinkedHashMap<>( );
        model.put( MARK_PANEL_ACTIVE, _bActive );
        model.put( MARK_PANEL_TECHNICAL_CODE, strTechnicalCode );
        model.put( MARK_PANEL_TITLE, I18nService.getLocalizedString( strTitle, locale ) );
        model.put( MARK_PANEL_FORM_RESPONSE_NUMBER, getFormResponseNumber( ) );

        String strFormPanelDisplayTemplate = AppTemplateService.getTemplate( TEMPLATE_MULTIVIEW_FORM_PANEL, locale, model ).getHtml( );
        _strTemplate = strFormPanelDisplayTemplate;

        return _strTemplate;
    }
}
