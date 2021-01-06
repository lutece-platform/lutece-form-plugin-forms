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
package fr.paris.lutece.plugins.forms.web.form.filter.display.impl;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.form.FormParameters;
import fr.paris.lutece.plugins.forms.business.form.filter.FormFilter;
import fr.paris.lutece.plugins.forms.web.form.filter.display.IFormFilterDisplay;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * Abstract class for the FormFilterDisplay objects
 */
public abstract class AbstractFormFilterDisplay implements IFormFilterDisplay
{
    // Template
    protected static final String FILTER_TEMPLATE_NAME = "admin/plugins/forms/multiview/form_filter.html";

    // Marks
    protected static final String MARK_FILTER_LIST = "filter_list";
    protected static final String MARK_FILTER_CONFIG = "filter_config";
    protected static final String MARK_FILTER_NAME = "filter_name";
    protected static final String MARK_FILTER_LABEL = "filter_label";
    protected static final String MARK_FILTER_LIST_VALUE = "filter_list_value";

    // Constants
    private static final String DEFAULT_FORM_FILTER_LABEL = "-";

    // Variables
    private int _nPosition;
    private String _strValue = StringUtils.EMPTY;
    private String _strTemplate = StringUtils.EMPTY;
    private FormFilter _formFilter;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBaseTemplate( )
    {
        return FILTER_TEMPLATE_NAME;
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
     * Set the value for the FormFilterDisplay
     * 
     * @param strValue
     *            The value to set to the FormFilterDisplay
     */
    protected void setValue( String strValue )
    {
        _strValue = strValue;
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
     * {@inheritDoc}
     */
    @Override
    public String getTemplate( )
    {
        return _strTemplate;
    }

    /**
     * Set the template
     * 
     * @param strTemplate
     *            The template to set to the filter
     */
    protected void setTemplate( String strTemplate )
    {
        _strTemplate = strTemplate;
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
    public void setFormFilter( FormFilter formFilter )
    {
        _formFilter = formFilter;
    }

    /**
     * Get the map of all parameter names and values used by the filter
     * 
     * @param request
     *            The request used to retrieve the informations of the filter
     * @return the map which contains all the parameter names and values of the filter
     */
    protected abstract Map<String, Object> getFilterDisplayMapValues( HttpServletRequest request );

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
     * Build the filter template with the given list for the specified parameter and set it to the filter
     * 
     * @param request
     *            The HttpServletRequest to use for building the template
     * @param referenceList
     *            The ReferenceList to use to populate the filter template
     * @param strParameterName
     *            The name of the parameter to attached the value of the ReferenceList
     */
    protected void manageFilterTemplate( HttpServletRequest request, ReferenceList referenceList, String strParameterName )
    {
        String strTemplateResult = StringUtils.EMPTY;

        Map<String, Object> model = new LinkedHashMap<>( );
        model.put( MARK_FILTER_LIST, referenceList );
        model.put( MARK_FILTER_LIST_VALUE, getValue( ) );
        model.put( MARK_FILTER_NAME, strParameterName );
        model.put( MARK_FILTER_CONFIG, getFormFilter( ).getFormFilterConfiguration( ) );
        model.put( MARK_FILTER_LABEL, getFormFilter( ).getFormFilterConfiguration( ).getFormFilterLabel( request.getLocale( ) ) );

        HtmlTemplate htmlTemplate = AppTemplateService.getTemplate( getBaseTemplate( ), request.getLocale( ), model );
        if ( htmlTemplate != null )
        {
            strTemplateResult = htmlTemplate.getHtml( );
        }

        setTemplate( strTemplateResult );
    }

    /**
     * Return the label of the FormFilter from its configuration
     * 
     * @return the label of the FormFilter from its configuration or the default label if not found
     */
    protected String getFormFilterDisplayLabel( Locale locale )
    {
        String strFormFilterDisplayLabel = DEFAULT_FORM_FILTER_LABEL;

        FormFilter formFilter = getFormFilter( );
        if ( formFilter != null && formFilter.getFormFilterConfiguration( ) != null )
        {
            strFormFilterDisplayLabel = formFilter.getFormFilterConfiguration( ).getFormFilterLabel( locale );
        }

        return strFormFilterDisplayLabel;
    }
}
