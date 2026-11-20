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
package fr.paris.lutece.plugins.forms.validation;

import fr.paris.lutece.portal.service.template.AppTemplateService;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;

public class DayDelayMinimumValidator extends AbstractDayDistanceValidator
{
    private static final String TEMPLATE_JAVASCRIPT = "/skin/plugins/forms/validators/day_delay_minimum_javascript.html";

    /**
     * Constructor of the AbstractValidator
     *
     * @param strValidatorName
     *         The validator bean name
     * @param strValidatorDisplayName
     *         The validator display name
     * @param listAvailableEntryType
     *         The list of available entrytype
     */
    public DayDelayMinimumValidator( String strValidatorName, String strValidatorDisplayName, List<String> listAvailableEntryType )
    {
        super( strValidatorName, strValidatorDisplayName, listAvailableEntryType );
    }

    /**
     * The date is at least the number of days of the control before today (0: today or before)
     */
    @Override
    public boolean validateControl( int nControlValue, LocalDate date )
    {
        return ChronoUnit.DAYS.between( date, LocalDate.now( ) ) >= nControlValue;
    }

    @Override
    public String getJavascriptValidation( )
    {
        return AppTemplateService.getTemplate( TEMPLATE_JAVASCRIPT, _locale, new HashMap<>( ) ).getHtml( );
    }

    @Override
    protected String getLabelKey( )
    {
        return "forms.validator.delay.min.label";
    }

    @Override
    protected String getHelpKey( )
    {
        return StringUtils.EMPTY;
    }
}
