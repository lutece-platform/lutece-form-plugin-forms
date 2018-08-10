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

package fr.paris.lutece.plugins.forms.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.regularexpression.business.RegularExpressionHome;
import fr.paris.lutece.portal.business.regularexpression.RegularExpression;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.regularexpression.IRegularExpressionService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * 
 * Validator to verify the pattern of a response
 *
 */
public class PatternValidator implements IValidator
{
	private static final String BEAN_NAME_REGULAR_EXPRESSION = "regularExpressionService";
	
    private static final String TEMPLATE_JS_FUNCTION = "/skin/plugins/forms/validators/pattern_function.js";
    private static final String TEMPLATE_DISPLAY_HTML = "/admin/plugins/forms/validators/pattern_template.html";
    private String _strValidatorName = StringUtils.EMPTY;
    private String _strDisplayName = StringUtils.EMPTY;
    private List<String> _listAvailableEntryType = new ArrayList<String>( );
    private Plugin _plugin = PluginService.getPlugin( "forms" ) ;

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
    public PatternValidator( String strValidatorName, String strValidatorDisplayName, List<String> listAvailableEntryType )
    {
        _strValidatorName = strValidatorName;
        _strDisplayName = I18nService.getLocalizedString( strValidatorDisplayName, I18nService.getDefaultLocale( ) );
        _listAvailableEntryType = listAvailableEntryType;
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
        Map<String, Object> model = new HashMap<String, Object>( );
        
        List<RegularExpression> listRegularExpression = RegularExpressionHome.getList( _plugin );
        ReferenceList refListRegularExpression = new ReferenceList( );
        
        for( RegularExpression regularExpression : listRegularExpression )
        {
        	refListRegularExpression.addItem( regularExpression.getIdExpression( ), regularExpression.getTitle( ) );
        }

        model.put( FormsConstants.PARAMETER_REF_LIST_VALUE, refListRegularExpression );
        model.put( FormsConstants.PARAMETER_CONTROL_VALUE, control.getValue( ) );

        HtmlTemplate htmlTemplateQuestion = AppTemplateService.getTemplate( TEMPLATE_DISPLAY_HTML, I18nService.getDefaultLocale( ), model );

        return htmlTemplateQuestion.getHtml( );
    }

    @Override
    public List<String> getListAvailableEntryType( )
    {
        return _listAvailableEntryType;
    }

    @Override
    public boolean validate( FormQuestionResponse questionResponse, Control control )
    {
        RegularExpression regularExpression = RegularExpressionHome.findByPrimaryKey( Integer.valueOf( control.getValue( ) ), _plugin );

        if( regularExpression != null )
        {
        	IRegularExpressionService service = (IRegularExpressionService) SpringContextService.getBean( BEAN_NAME_REGULAR_EXPRESSION );
	        for ( Response response : questionResponse.getEntryResponse( ) )
	        {
	        	if( StringUtils.isNoneEmpty( response.getResponseValue( ) ) )
	        	{
	        		return service.isMatches( response.getResponseValue( ), regularExpression );
	        	}
	        }
        }
        return false;
    }

    @Override
    public String getJavascriptValidation( )
    {

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( FormsConstants.MARKER_JS_PARAMETER_CONTROL_VALUE, FormsConstants.JS_PARAMETER_CONTROL_VALUE );
        model.put( FormsConstants.MARKER_JS_PARAMETER_INPUT_VALUE, FormsConstants.JS_PARAMETER_INPUT_VALUE );

        return AppTemplateService.getTemplate( TEMPLATE_JS_FUNCTION, I18nService.getDefaultLocale( ), model ).getHtml( );
    }
}
