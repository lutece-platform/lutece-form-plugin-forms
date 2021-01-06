/*
 * Copyright (c) 2002-2021, City of Paris
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
package fr.paris.lutece.plugins.forms.business.form.filter.configuration;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.portal.service.i18n.I18nService;

/**
 * Configuration for a FormFilter object
 */
public class AbstractFormFilterConfiguration implements IFormFilterConfiguration
{
    // Variables
    private final int _nPosition;
    private final String _strFormFilterLabel;
    private final String _strFormFilterName;

    /**
     * Constructor
     * 
     * @param nPosition
     *            The position of the FormFilter
     * @param strFormFilterLabel
     *            The label of the FormFilter
     */
    public AbstractFormFilterConfiguration( int nPosition, String strFormFilterLabel, String strFormFilterName )
    {
        _nPosition = nPosition;
        _strFormFilterLabel = strFormFilterLabel;
        _strFormFilterName = strFormFilterName;
    }

    /**
     * Return the position of the FormFilter
     * 
     * @return the position of the FormFilter
     */
    public int getPosition( )
    {
        return _nPosition;
    }

    /**
     * Return the label of the FormFilter
     * 
     * @return the label of the FormFilter
     */
    @Override
    public String getFormFilterLabel( Locale locale )
    {
        String title = I18nService.getLocalizedString( _strFormFilterLabel, locale );
        if ( StringUtils.isNotEmpty( title ) )
        {
            return title;
        }

        return _strFormFilterLabel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFormFilterName( )
    {
        return _strFormFilterName;
    }

}
