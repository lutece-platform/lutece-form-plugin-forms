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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * 
 * Validator to verify the exact value of a response
 *
 */
public abstract class AbstractListQuestionValidator extends AbstractValidator
{
    protected List<String> _listAvailableFieldControl;

    private static final String TEMPLATE_DISPLAY_HTML = "/admin/plugins/forms/validators/list_question_template.html";
    private static final String TEMPLATE_JAVASCRIPT = "/skin/plugins/forms/validators/list_question_javascript.html";

    /**
     * Constructor of the PatternValidator
     * 
     * @param strValidatorName
     *            The validator bean name
     * @param strValidatorDisplayName
     *            The validator display name
     * @param listAvailableEntryType
     *            The list of available entrytype
     */
    public AbstractListQuestionValidator( String strValidatorName, String strValidatorDisplayName, List<String> listAvailableEntryType,
            List<String> listAvailableFieldControl )
    {
        super( strValidatorName, strValidatorDisplayName, listAvailableEntryType );
        _listAvailableFieldControl = listAvailableFieldControl;

    }

    @Override
    public String getDisplayHtml( Control control )
    {
        Map<String, Object> model = new HashMap<>( );

        ReferenceList referenceListQuestion = new ReferenceList( );
        referenceListQuestion.addItem( -1, StringUtils.EMPTY );
        ReferenceList refListMapping = new ReferenceList( );

        if ( control.getListIdQuestion( ) != null )
        {

            refListMapping = ControlHome.getCtrlMappingListByIdControl( control.getId( ) );

            for ( int nIdQuestion : control.getListIdQuestion( ) )
            {
                Question question = QuestionHome.findByPrimaryKey( nIdQuestion );

                referenceListQuestion.addItem( question.getId( ), question.getTitle( ) );
            }

        }
        model.put( FormsConstants.PARAMETRE_VALIDATOR_LISTQUESTION_NAME, FormsConstants.VALUE_VALIDATOR_LISTEQUESTION_NAME );
        model.put( FormsConstants.PARAMETER_REF_LIST_MAPPING, refListMapping );
        model.put( FormsConstants.PARAMETER_REF_LIST_VALUE, referenceListQuestion );
        model.put( FormsConstants.PARAMETER_REF_LIST_FIELD, _listAvailableFieldControl );

        HtmlTemplate htmlTemplateQuestion = AppTemplateService.getTemplate( TEMPLATE_DISPLAY_HTML, _locale, model );

        return htmlTemplateQuestion.getHtml( );
    }

    @Override
    public String getJavascriptValidation( )
    {
        Map<String, Object> model = new HashMap<>( );
        HtmlTemplate htmlTemplate = AppTemplateService.getTemplate( TEMPLATE_JAVASCRIPT, _locale, model );

        return htmlTemplate.getHtml( );
    }

    @Override
    public String getJavascriptControlValue( Control control )
    {
        return control.getValue( );
    }

    public List<String> getListAvailableFieldControl( )
    {
        return _listAvailableFieldControl;
    }
}
