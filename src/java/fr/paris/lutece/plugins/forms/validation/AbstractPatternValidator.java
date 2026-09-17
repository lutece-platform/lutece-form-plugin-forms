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
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.regularexpression.business.RegularExpressionHome;
import fr.paris.lutece.portal.business.regularexpression.RegularExpression;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.regularexpression.IRegularExpressionService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import jakarta.enterprise.inject.spi.CDI;

public abstract class AbstractPatternValidator extends AbstractValidator
{
    private static final String TEMPLATE_JS_FUNCTION = "/skin/plugins/forms/validators/pattern_function.js";
    private static final String TEMPLATE_DISPLAY_HTML = "/admin/plugins/forms/validators/pattern_template.html";

    private Plugin _plugin = PluginService.getPlugin( "forms" );

    public AbstractPatternValidator( String strValidatorName, String strValidatorDisplayName, List<String> listAvailableEntryType )
    {
        super( strValidatorName, strValidatorDisplayName, listAvailableEntryType );
    }

    @Override
    public String getDisplayHtml( Control control )
    {
        Map<String, Object> model = new HashMap<>( );

        List<RegularExpression> listRegularExpression = RegularExpressionHome.getList( _plugin );
        ReferenceList refListRegularExpression = new ReferenceList( );

        for ( RegularExpression regularExpression : listRegularExpression )
        {
            refListRegularExpression.addItem( regularExpression.getIdExpression( ), regularExpression.getTitle( ) );
        }

        model.put( FormsConstants.PARAMETER_REF_LIST_VALUE, refListRegularExpression );
        model.put( FormsConstants.PARAMETER_CONTROL_VALUE, control.getValue( ) );

        HtmlTemplate htmlTemplateQuestion = AppTemplateService.getTemplate( TEMPLATE_DISPLAY_HTML, _locale, model );

        return htmlTemplateQuestion.getHtml( );
    }

    @Override
    public String getJavascriptValidation( )
    {
        Map<String, Object> model = new HashMap<>( );
        return AppTemplateService.getTemplate( TEMPLATE_JS_FUNCTION, _locale, model ).getHtml( );
    }

    @Override
    public String getJavascriptControlValue( Control control )
    {
        RegularExpression regularExpression = findRegularExpression( control );

        return ( regularExpression != null ) ? regularExpression.getValue( ) : StringUtils.EMPTY;
    }

    @Override
    public boolean validate( FormQuestionResponse questionResponse, Control control )
    {
        RegularExpression regularExpression = findRegularExpression( control );

        if ( regularExpression != null )
        {
            IRegularExpressionService service = CDI.current().select( IRegularExpressionService.class ).get( );
            for ( Response response : questionResponse.getEntryResponse( ) )
            {
            	String toValidate = getValueToValidate( response );
                if (StringUtils.isNotEmpty(toValidate) && !service.isMatches( toValidate, regularExpression ) )
                {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Recherche l'expression reguliere referencee par le controle.
     *
     * <p>La valeur du controle porte l'identifiant de l'expression reguliere. Elle peut etre absente
     * lorsque le controle a ete enregistre sans validateur confirme, auquel cas aucune regle n'est
     * applicable : la methode renvoie {@code null} plutot que de laisser remonter une exception qui
     * se traduirait par une erreur technique en front-office.</p>
     *
     * @param control
     *            le controle portant l'identifiant de l'expression reguliere
     * @return l'expression reguliere referencee, ou {@code null} si le controle n'en reference aucune
     *         d'exploitable
     */
    private RegularExpression findRegularExpression( Control control )
    {
        if ( StringUtils.isEmpty( control.getValue( ) ) )
        {
            AppLogService.error( "Forms - Control without regular expression value : validation refused" );
            return null;
        }

        try
        {
            return RegularExpressionHome.findByPrimaryKey( Integer.parseInt( control.getValue( ) ), _plugin );
        }
        catch( NumberFormatException e )
        {
            AppLogService.error( "Error number format", e );
            return null;
        }
    }

    protected abstract String getValueToValidate( Response response );
}
