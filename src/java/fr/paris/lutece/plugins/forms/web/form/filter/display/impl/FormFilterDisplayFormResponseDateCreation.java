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
package fr.paris.lutece.plugins.forms.web.form.filter.display.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.util.FormMultiviewFormResponseDateCreationNameConstants;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.html.HtmlTemplate;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

/**
 * Implementation of the IFormFilterDisplay interface for the filter which manage the period of creation of form response
 */
public class FormFilterDisplayFormResponseDateCreation extends AbstractFormFilterDisplay
{
    // Constants
    private static final String PARAMETER_SEARCH_OPEN_BETWEEN = "multiview_search_open_between";
    private static final String FROM = "_from";
    private static final String TO = "_to";

    // Templates
    private static final String FORM_FILTER_CREATION_DATE_TEMPLATE_NAME = "admin/plugins/forms/multiview/filter/date_filter.html";

    // Regex
    private static final String REGEX_DATE_FORMAT = "([0-9]{2}.[0-9]{2}.[0-9]{4}).([0-9]{2}.[0-9]{2}.[0-9]{4})";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getParameterName( )
    {
        return PARAMETER_SEARCH_OPEN_BETWEEN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getFilterDisplayMapValues( HttpServletRequest request )
    {
        String strPeriodCreationDate = request.getParameter( PARAMETER_SEARCH_OPEN_BETWEEN );
        String strPeriodCreationFrom = request.getParameter( PARAMETER_SEARCH_OPEN_BETWEEN + FROM );
        String strPeriodCreationTo = request.getParameter( PARAMETER_SEARCH_OPEN_BETWEEN + TO );
        setValue( strPeriodCreationDate );

        Map<String, Object> mapFilterNameValues = new LinkedHashMap<>( );
        if ( !StringUtils.isEmpty( strPeriodCreationFrom ) && !StringUtils.isEmpty( strPeriodCreationTo ) )
        {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern( AppPropertiesService
                    .getProperty( FormsConstants.PROPERTY_EXPORT_FORM_DATE_CREATION_FORMAT ) );

            LocalDate dateFrom = LocalDate.parse( strPeriodCreationFrom, formatter );
            LocalDate dateTo = LocalDate.parse( strPeriodCreationTo, formatter );
            dateTo = dateTo.plusDays( 1 );
            DateTimeFormatter sqlFormatter = DateTimeFormatter.ofPattern( "yyyy-MM-dd" );

            mapFilterNameValues
                    .put( FormMultiviewFormResponseDateCreationNameConstants.FILTER_FORM_RESPONSE_DATE_CREATION_FROM, dateFrom.format( sqlFormatter ) );
            mapFilterNameValues.put( FormMultiviewFormResponseDateCreationNameConstants.FILTER_FORM_RESPONSE_DATE_CREATION_TO, dateTo.format( sqlFormatter ) );
        }

        return mapFilterNameValues;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void buildTemplate( HttpServletRequest request )
    {
        String strTemplateResult = StringUtils.EMPTY;

        Map<String, Object> model = new LinkedHashMap<>( );
        model.put( MARK_FILTER_LIST_VALUE, getValue( ) );

        addDateRange( model );

        model.put( MARK_FILTER_NAME, PARAMETER_SEARCH_OPEN_BETWEEN );

        HtmlTemplate htmlTemplate = AppTemplateService.getTemplate( getBaseTemplate( ), request.getLocale( ), model );
        if ( htmlTemplate != null )
        {
            strTemplateResult = htmlTemplate.getHtml( );
        }

        setTemplate( strTemplateResult );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBaseTemplate( )
    {
        return FORM_FILTER_CREATION_DATE_TEMPLATE_NAME;
    }

    /**
     * Add date range from ... to to the model
     * 
     * @param model
     *            The model
     */
    private void addDateRange( Map<String, Object> model )
    {
        String strDateRange = getValue( );

        if ( strDateRange != null )
        {
            strDateRange = getValue( ).replaceAll( " ", "" );
            Matcher m = Pattern.compile( REGEX_DATE_FORMAT ).matcher( strDateRange );

            try
            {
                m.find( );
                String strDateFromValue = m.group( 1 );
                String strDateToValue = m.group( 2 );

                model.put( MARK_FILTER_LIST_VALUE + FROM, strDateFromValue );
                model.put( MARK_FILTER_LIST_VALUE + TO, strDateToValue );
            }
            catch( Exception e )
            {
                AppLogService.error( "Unable to parse date range and extract date_from and date_to in " + strDateRange + ". Please check date formats.", e );
            }
        }
    }
}
