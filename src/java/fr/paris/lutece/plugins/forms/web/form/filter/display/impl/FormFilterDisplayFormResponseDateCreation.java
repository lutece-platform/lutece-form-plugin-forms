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
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.util.FormMultiviewFormResponseDateCreationNameConstants;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.util.ReferenceList;

/**
 * Implementation of the IFormFilterDisplay interface for the filter which manage the period of creation of form response
 */
public class FormFilterDisplayFormResponseDateCreation extends AbstractFormFilterDisplay
{
    // Constants
    private static final int LAST_DAY_VALUE = 1;
    private static final int LAST_WEEK_VALUE = 7;
    private static final int LAST_MONTH_VALUE = 31;
    private static final String PARAMETER_SEARCH_OPEN_SINCE = "multiview_search_open_since";

    // Messages keys
    private static final String LAST_DAY_MESSAGE_KEY = "forms.multiviewForms.labelFilter.searchSinceLastDay";
    private static final String LAST_WEEK_MESSAGE_KEY = "forms.multiviewForms.labelFilter.searcHSinceLastWeek";
    private static final String LAST_MONTH_MESSAGE_KEY = "forms.multiviewForms.labelFilter.searchSinceLastMonth";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getParameterName( )
    {
        return PARAMETER_SEARCH_OPEN_SINCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getFilterDisplayMapValues( HttpServletRequest request )
    {
        String strPeriodCreationDate = request.getParameter( PARAMETER_SEARCH_OPEN_SINCE );
        setValue( strPeriodCreationDate );

        Map<String, Object> mapFilterNameValues = new LinkedHashMap<>( );
        mapFilterNameValues.put( FormMultiviewFormResponseDateCreationNameConstants.FILTER_FORM_RESPONSE_DATE_CREATION, strPeriodCreationDate );

        return mapFilterNameValues;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void buildTemplate( HttpServletRequest request )
    {
        manageFilterTemplate( request, createReferenceList( request.getLocale( ) ), PARAMETER_SEARCH_OPEN_SINCE );
    }

    /**
     * Build the ReferenceList for the period of the form response creation
     * 
     * @param locale
     *            The locale to use for building the message for the list
     * @return the ReferenceList for the period of the form response creation
     */
    private ReferenceList createReferenceList( Locale locale )
    {
        ReferenceList referenceList = new ReferenceList( );

        // Default value
        String strDefaultDayItemName = getFormFilterDisplayLabel( );
        referenceList.addItem( FormsConstants.DEFAULT_FILTER_VALUE, strDefaultDayItemName );

        // Last day filter
        String strLastDayItemName = I18nService.getLocalizedString( LAST_DAY_MESSAGE_KEY, locale );
        referenceList.addItem( LAST_DAY_VALUE, strLastDayItemName );

        // Last week filter
        String strLastWeekItemName = I18nService.getLocalizedString( LAST_WEEK_MESSAGE_KEY, locale );
        referenceList.addItem( LAST_WEEK_VALUE, strLastWeekItemName );

        // Last month filter
        String strLastMonthItemName = I18nService.getLocalizedString( LAST_MONTH_MESSAGE_KEY, locale );
        referenceList.addItem( LAST_MONTH_VALUE, strLastMonthItemName );

        return referenceList;
    }
}
