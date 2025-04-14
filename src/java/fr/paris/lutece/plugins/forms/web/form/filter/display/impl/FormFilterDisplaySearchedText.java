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
package fr.paris.lutece.plugins.forms.web.form.filter.display.impl;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import fr.paris.lutece.plugins.forms.business.form.FormParameters;
import fr.paris.lutece.plugins.forms.business.form.filter.FormFilter;
import fr.paris.lutece.plugins.forms.service.search.FormSearchConfig;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;
import java.util.List;
import fr.paris.lutece.plugins.forms.service.search.IFormSearchEngine;

/**
 * Implementation of the IFormFilterDisplay interface for the filter on workflow state
 */
public class FormFilterDisplaySearchedText extends AbstractFormFilterDisplay
{
    // Constants
    private static final String PARAMETER_SEARCHED_TEXT = "searched_text";
    private static final String MARK_SEARCHED_TEXT = "searched_text";
    private static final String PARAMETER_ID_RESPONSE = "id_response_";

    // Templates
    private static final String FORM_FILTER_SEARCHED_TEXT_TEMPLATE_NAME = "admin/plugins/forms/multiview/filter/searched_text_filter.html";

    // Variables
    private int _nPosition;
    private String _strTemplate;
    private String _strValue;
    private FormFilter _formFilter;
    private IFormSearchEngine _formSearchEngine;

    public FormFilterDisplaySearchedText( IFormSearchEngine formSearchEngine )
    {
        _formSearchEngine = formSearchEngine;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBaseTemplate( )
    {
        return FORM_FILTER_SEARCHED_TEXT_TEMPLATE_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getParameterName( )
    {
        return PARAMETER_SEARCHED_TEXT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, Object> getFilterDisplayMapValues( HttpServletRequest request )
    {
        Map<String, Object> mapFilterNameValues = new LinkedHashMap<>( );

        String strSearchedText = request.getParameter( PARAMETER_SEARCHED_TEXT );
        if ( StringUtils.isNotBlank( strSearchedText ) )
        {
            FormSearchConfig config = new FormSearchConfig( );
            config.setSearchedText( strSearchedText );
            List<Integer> listIdsResults = _formSearchEngine.getSearchResults( config );
            for ( int i = 0; i < listIdsResults.size( ); i++ )
            {
                mapFilterNameValues.put( PARAMETER_ID_RESPONSE + String.valueOf( i ), listIdsResults.get( i ) );
            }
            if ( listIdsResults.isEmpty( ) )
            {
                mapFilterNameValues.put( PARAMETER_ID_RESPONSE + "0", -2 );
            }
        }

        setValue( strSearchedText );

        return mapFilterNameValues;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void buildTemplate(HttpServletRequest request, Locale locale)
    {
        Map<String, Object> model = new LinkedHashMap<>( );
        String strSearchedText = request.getParameter( PARAMETER_SEARCHED_TEXT );
        model.put( MARK_SEARCHED_TEXT, strSearchedText );
        model.put( MARK_FILTER_CONFIG, getFormFilter( ).getFormFilterConfiguration( ) );
        model.put( MARK_FILTER_LABEL, getFormFilter( ).getFormFilterConfiguration( ).getFormFilterLabel( locale ) );
        HtmlTemplate htmlTemplate = AppTemplateService.getTemplate( getBaseTemplate( ), locale, model );

        _strTemplate = htmlTemplate.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue( )
    {
        return _strValue;
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
    public FormParameters createFormParameters( HttpServletRequest request )
    {
        FormParameters formParameters = null;

        if ( _formFilter != null )
        {
            formParameters = new FormParameters( );

            Map<String, Object> mapKeyNameValues = getFilterDisplayMapValues( request );
            formParameters.setFormParametersMap( mapKeyNameValues );

            _formFilter.setFormParameters( formParameters );
        }

        return formParameters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFormFilter( FormFilter formFilter )
    {
        _formFilter = formFilter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FormFilter getFormFilter( )
    {
        return _formFilter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPosition( )
    {
        return _nPosition;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPosition( int nPosition )
    {
        _nPosition = nPosition;
    }

    /**
     * Set the value
     * 
     * @param strValue
     */
    @Override
    protected void setValue( String strValue )
    {
        _strValue = strValue;
    }
}
