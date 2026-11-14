/*
 * Copyright (c) 2002-2026, City of Paris
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.core.HTMLOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;

/**
 * Renders the forms widget of the admin dashboard with HTML auto-escaping: the button of the actions menu of each form
 * has an accessible name holding the form title. The core macros the template calls are replaced by stand-ins that
 * write their arguments; the input stand-in writes the id as the core macro does, defaulting to the name: only the
 * FreeMarker engine is needed.
 */
public class FormDashboardTemplateRenderTest
{
    private static final String TEMPLATES_ROOT = "webapp/WEB-INF/templates";
    private static final String TEMPLATE = "admin/plugins/forms/dashboard/form_dashboard.html";
    private static final String MACROS = "macros.ftl";
    private static final String MACROS_COMMON = "<#macro adminDashboardWidget rest...><#nested></#macro>"
            + "<#macro listGroup rest...><#nested></#macro><#macro listGroupItem rest...><#nested></#macro>"
            + "<#macro card rest...><#nested></#macro><#macro row rest...><#nested></#macro><#macro div rest...><#nested></#macro>"
            + "<#macro h level=1 rest...><h${level}><#nested></h${level}></#macro><#macro icon rest...><span class=\"ti\"></span></#macro>"
            + "<#macro tag rest...><span><#nested></span></#macro><#macro empty rest...><div class=\"empty\"></div></#macro>"
            + "<#macro link href='' label='' rest...><a href=\"${href}\">${label}<#nested></a></#macro>"
            + "<#macro aButton href='' title='' rest...><a href=\"${href}\" title=\"${title}\"></a></#macro>"
            + "<#macro tform action='' method='' rest...><form action=\"${action}\" method=\"${method}\"><#nested></form></#macro>"
            + "<#macro button id='' title='' dropdownMenu=false rest...><button<#if id?has_content> id=\"${id}\"</#if><#if title?has_content> title=\"${title}\"</#if>"
            + "<#if dropdownMenu> data-bs-toggle=\"dropdown\"</#if>><#if title?has_content><span class=\"visually-hidden\">${title}</span></#if>"
            + "<#if !dropdownMenu><#nested></#if></button><#if dropdownMenu><div class=\"dropdown-menu\"><#nested></div></#if></#macro>";
    private static final String INPUT_ID_DEFAULTS_TO_NAME = "<#macro input name id='' type='text' value='' rest...>"
            + "<input type=\"${type}\" id=\"${id?has_content?then( id, name )}\" name=\"${name}\" value=\"${value}\"></#macro>";
    private static final Pattern MENU_BUTTON = Pattern.compile( "<button([^>]*)data-bs-toggle=\"dropdown\"[^>]*>(.*?)</button>", Pattern.DOTALL );
    private static final Pattern TITLE = Pattern.compile( "title=\"([^\"]*)\"" );
    private static final String [ ] TITLES = {
            "Inscription <b>A</b>", "Survey \"B\""
    };
    private static final String [ ] TITLES_ESCAPED = {
            "Inscription &lt;b&gt;A&lt;/b&gt;", "Survey &quot;B&quot;"
    };

    /**
     * Renders the widget with two forms, each with a link action and an action posted with its token.
     *
     * @param strInputMacro
     *            the stand-in of the input macro
     * @return the HTML
     * @throws IOException
     *             if the template cannot be read
     * @throws TemplateException
     *             if the rendering fails
     */
    private static String render( String strInputMacro ) throws IOException, TemplateException
    {
        StringTemplateLoader macros = new StringTemplateLoader( );
        macros.putTemplate( MACROS, MACROS_COMMON + strInputMacro );
        Configuration cfg = new Configuration( Configuration.VERSION_2_3_0 );
        cfg.setOutputFormat( HTMLOutputFormat.INSTANCE );
        cfg.setAutoEscapingPolicy( Configuration.ENABLE_IF_DEFAULT_AUTO_ESCAPING_POLICY );
        cfg.setTemplateLoader( new MultiTemplateLoader( new TemplateLoader [ ] {
                macros, new FileTemplateLoader( new File( TEMPLATES_ROOT ) )
        } ) );
        cfg.addAutoInclude( MACROS );

        List<Map<String, Object>> listActions = new ArrayList<>( );
        listActions.add( action( "jsp/admin/plugins/forms/ManageForms.jsp?view=confirmRemoveForm", "DELETE" ) );
        listActions.add( action( "jsp/admin/plugins/forms/ManageForms.jsp?action=duplicateForm", "MODIFY" ) );
        List<Map<String, Object>> listForms = new ArrayList<>( );
        for ( int i = 0; i < TITLES.length; i++ )
        {
            Map<String, Object> form = new HashMap<>( );
            form.put( "id", i + 1 );
            form.put( "title", TITLES [i] );
            form.put( "active", true );
            form.put( "currentNumberResponse", 0 );
            form.put( "actions", listActions );
            listForms.add( form );
        }
        Map<String, Object> model = new HashMap<>( );
        model.put( "form_list", listForms );
        model.put( "url", "jsp/admin/plugins/forms/ManageForms.jsp" );
        model.put( "link_tokens", Map.of( "duplicateForm", "tok1" ) );
        model.put( "token", "tok2" );

        StringWriter writer = new StringWriter( );
        cfg.getTemplate( TEMPLATE ).process( model, writer );

        return writer.toString( );
    }

    /**
     * A form action as the dashboard component lists it.
     *
     * @param strUrl
     *            the URL of the action
     * @param strPermission
     *            the permission, also used as name
     * @return the action
     */
    private static Map<String, Object> action( String strUrl, String strPermission )
    {
        Map<String, Object> action = new HashMap<>( );
        action.put( "url", strUrl );
        action.put( "permission", strPermission );
        action.put( "name", strPermission );
        action.put( "iconUrl", "cog" );

        return action;
    }

    /**
     * The button opening the actions menu of a form shows only an icon: it is named by its title, which holds the form
     * title as text, also written for screen readers.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testActionsMenuButtonHasAName( ) throws Exception
    {
        String strHtml = render( INPUT_ID_DEFAULTS_TO_NAME );
        Matcher button = MENU_BUTTON.matcher( strHtml );
        int nButtons = 0;
        while ( button.find( ) )
        {
            Matcher title = TITLE.matcher( button.group( 1 ) );
            assertTrue( title.find( ), "the actions menu button has a title: " + button.group( ) );
            assertTrue( title.group( 1 ).contains( "#i18n{forms.dashboard.labelFormActions}" ), button.group( ) );
            assertTrue( title.group( 1 ).endsWith( TITLES_ESCAPED [nButtons] ), "the title holds the form title as text: " + button.group( ) );
            assertTrue( button.group( 2 ).contains( TITLES_ESCAPED [nButtons] ), "the name is also written as text in the button: " + button.group( ) );
            nButtons++;
        }
        assertEquals( TITLES.length, nButtons, strHtml );
    }
}
