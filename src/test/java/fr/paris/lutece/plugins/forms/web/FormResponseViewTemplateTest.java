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
package fr.paris.lutece.plugins.forms.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.util.mvc.utils.MVCMessage;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Renders the front office view of a form response (skin/plugins/forms/view_form_response.html).
 */
public class FormResponseViewTemplateTest extends LuteceTestCase
{
    private static final String TEMPLATE = "/skin/plugins/forms/view_form_response.html";
    private static final String DSKEY_SHOW_WARNING = "portal.theme.site_property.forms.showResponseFormWarning.checkbox";
    private static final String DSKEY_LABEL_INFO = "portal.theme.site_property.forms.labelInfoResponse";
    private static final String LABEL_OF_THE_PROPERTY = "Information sur la réponse au formulaire";

    private String _strShowWarning;
    private String _strLabelInfo;

    /**
     * Keeps the site properties the tests change
     */
    @BeforeEach
    public void keepSiteProperties( )
    {
        _strShowWarning = DatastoreService.getDataValue( DSKEY_SHOW_WARNING, null );
        _strLabelInfo = DatastoreService.getDataValue( DSKEY_LABEL_INFO, null );
    }

    /**
     * Restores the site properties the tests changed
     */
    @AfterEach
    public void restoreSiteProperties( )
    {
        restore( DSKEY_SHOW_WARNING, _strShowWarning );
        restore( DSKEY_LABEL_INFO, _strLabelInfo );
    }

    private static void restore( String strKey, String strValue )
    {
        if ( strValue == null )
        {
            DatastoreService.removeData( strKey );
        }
        else
        {
            DatastoreService.setDataValue( strKey, strValue );
        }
    }

    /**
     * Renders the view of a response with no step and no action
     *
     * @return the HTML
     */
    private static String render( Map<String, Object> modelExtra )
    {
        FormResponse formResponse = new FormResponse( );
        formResponse.setId( 4321 );

        Map<String, Object> model = new HashMap<>( );
        model.put( FormsConstants.MARK_FORM_RESPONSE, formResponse );
        model.put( "list_step_display", new ArrayList<>( ) );
        model.put( "is_action_success", false );
        model.put( "workflow_action_list", new ArrayList<>( ) );
        model.putAll( modelExtra );

        return AppTemplateService.getTemplate( TEMPLATE, Locale.FRENCH, model ).getHtml( );
    }

    /**
     * The warning shows the text typed in the site properties, escaped, and not the label of the property (FP-02)
     */
    @Test
    public void testWarningShowsTheValueOfTheSiteProperty( )
    {
        DatastoreService.setDataValue( DSKEY_SHOW_WARNING, "1" );
        DatastoreService.setDataValue( DSKEY_LABEL_INFO, "FP02 texte <b>configuré</b>" );

        String strHtml = render( new HashMap<>( ) );

        assertTrue( strHtml.contains( "FP02 texte &lt;b&gt;configuré&lt;/b&gt;" ), strHtml );
        assertFalse( strHtml.contains( LABEL_OF_THE_PROPERTY ), strHtml );
    }

    /**
     * No warning when the property is unchecked, nor when its text is empty
     */
    @Test
    public void testNoWarningWhenUncheckedOrEmpty( )
    {
        DatastoreService.setDataValue( DSKEY_SHOW_WARNING, "0" );
        DatastoreService.setDataValue( DSKEY_LABEL_INFO, "FP02 texte" );
        assertFalse( render( new HashMap<>( ) ).contains( "FP02 texte" ) );

        DatastoreService.setDataValue( DSKEY_SHOW_WARNING, "1" );
        DatastoreService.setDataValue( DSKEY_LABEL_INFO, "" );
        String strHtml = render( new HashMap<>( ) );
        assertFalse( strHtml.contains( "alert-warning" ), strHtml );
        assertFalse( strHtml.contains( LABEL_OF_THE_PROPERTY ), strHtml );

        DatastoreService.removeData( DSKEY_LABEL_INFO );
        strHtml = render( new HashMap<>( ) );
        assertFalse( strHtml.contains( "DS Value Missing" ), strHtml );
    }

    /**
     * The errors and infos of the XPage, added by a refused or failed workflow action, are displayed (FP-04)
     */
    @Test
    public void testErrorsAndInfosAreDisplayed( )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( "errors", List.of( new MVCMessage( "FP04 erreur <script>" ) ) );
        model.put( "infos", List.of( new MVCMessage( "FP04 info" ) ) );

        String strHtml = render( model );

        assertTrue( strHtml.contains( "FP04 erreur &lt;script&gt;" ), strHtml );
        assertTrue( strHtml.contains( "alert-danger" ), strHtml );
        assertTrue( strHtml.contains( "FP04 info" ), strHtml );
    }
}
