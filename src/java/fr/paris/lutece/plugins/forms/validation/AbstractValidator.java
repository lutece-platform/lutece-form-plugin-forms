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
package fr.paris.lutece.plugins.forms.validation;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.portal.service.i18n.I18nService;

public abstract class AbstractValidator implements IValidator
{

    protected final String _strValidatorName;
    protected final String _strDisplayName;
    protected List<String> _listAvailableEntryType;

    /**
     * Constructor of the AbstractValidator
     * 
     * @param strValidatorName
     *            The validator bean name
     * @param strValidatorDisplayName
     *            The validator display name
     * @param listAvailableEntryType
     *            The list of available entrytype
     */
    public AbstractValidator( String strValidatorName, String strValidatorDisplayName, List<String> listAvailableEntryType )
    {
        _strValidatorName = strValidatorName;
        _strDisplayName = I18nService.getLocalizedString( strValidatorDisplayName, I18nService.getDefaultLocale( ) );
        _listAvailableEntryType = listAvailableEntryType;
    }

    @Override
    public boolean validate( List<FormQuestionResponse> formQuestionResponse, Control control )
    {

        for ( FormQuestionResponse questionResponse : formQuestionResponse )
        {

            if ( !validate( questionResponse, control ) )
            {

                return false;
            }

        }

        return true;
    }

    @Override
    public String getValidatorBeanName( )
    {

        return _strValidatorName;
    }

    @Override
    public String getValidatorDisplayName( )
    {

        return _strDisplayName;
    }

    @Override
    public String getDisplayHtml( Control control )
    {

        return StringUtils.EMPTY;
    }

    @Override
    public List<String> getListAvailableEntryType( )
    {

        return _listAvailableEntryType;
    }

    @Override
    public String getJavascriptValidation( )
    {

        return StringUtils.EMPTY;
    }

    @Override
    public String getJavascriptControlValue( Control control )
    {

        return StringUtils.EMPTY;
    }

}
