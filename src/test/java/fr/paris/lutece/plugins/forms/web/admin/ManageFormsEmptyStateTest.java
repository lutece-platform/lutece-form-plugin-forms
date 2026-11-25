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
package fr.paris.lutece.plugins.forms.web.admin;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * An empty list of forms shows the empty state of the forms list, not the label of the empty history of a response
 * (forms.multiviewForms.view_record_history.history_empty, written there by LUT-29997)
 */
public class ManageFormsEmptyStateTest extends LuteceTestCase
{
    private static final String TEMPLATE = "/admin/plugins/forms/manage_forms.html";
    private static final String KEY_HISTORY_EMPTY = "forms.multiviewForms.view_record_history.history_empty";
    private static final String KEY_NO_FORM = "forms.manageForm.noForm";

    private static String render( boolean bCanCreate, Locale locale )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( "form_list", new ArrayList<>( ) );
        model.put( "permission_create_forms", bCanCreate );
        model.put( "link_tokens", new HashMap<>( ) );
        model.put( "token", "tok" );
        return AppTemplateService.getTemplate( TEMPLATE, locale, model ).getHtml( );
    }

    @Test
    public void testEmptyListShowsTheNoFormLabel( )
    {
        for ( Locale locale : new Locale [ ] {
                Locale.ENGLISH, Locale.FRENCH
        } )
        {
            String strNoForm = I18nService.getLocalizedString( KEY_NO_FORM, locale );
            assertFalse( strNoForm.isEmpty( ) || strNoForm.equals( KEY_NO_FORM ), "the key " + KEY_NO_FORM + " exists in " + locale );
            for ( boolean bCanCreate : new boolean [ ] {
                    true, false
            } )
            {
                String strHtml = render( bCanCreate, locale );
                assertFalse( strHtml.contains( I18nService.getLocalizedString( KEY_HISTORY_EMPTY, locale ) ), strHtml );
                assertTrue( strHtml.contains( strNoForm ), strHtml );
            }
        }
    }
}
