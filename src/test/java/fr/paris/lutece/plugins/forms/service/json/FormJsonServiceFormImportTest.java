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
package fr.paris.lutece.plugins.forms.service.json;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormMessage;
import fr.paris.lutece.plugins.forms.business.FormMessageHome;
import fr.paris.lutece.plugins.forms.exception.FormJsonImportException;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.inject.Inject;

/**
 * Import of a form from invalid JSON data
 */
public class FormJsonServiceFormImportTest extends LuteceTestCase
{
    @Inject
    private FormJsonService _formJsonService;
    private Form _form;

    /**
     * Creates a form with its messages
     */
    @BeforeEach
    protected void createForm( )
    {
        _form = new Form( );
        _form.setTitle( "FormJsonServiceFormImportTest" );
        _form.setBreadcrumbName( "forms.horizontalBreadcrumb" );
        FormHome.create( _form );

        FormMessage formMessage = new FormMessage( );
        formMessage.setIdForm( _form.getId( ) );
        FormMessageHome.create( formMessage );
    }

    /**
     * Removes the form and its messages
     */
    @AfterEach
    protected void removeForm( )
    {
        FormMessageHome.removeByForm( _form.getId( ) );
        FormHome.remove( _form.getId( ) );
    }

    /**
     * JSON data without the key "form" is refused and creates no form
     */
    @Test
    public void testImportWithoutFormIsRefused( )
    {
        int nFormCount = FormHome.getFormList( ).size( );

        assertThrows( FormJsonImportException.class, ( ) -> _formJsonService.jsonImportForm( "{}", Locale.FRENCH ) );
        assertEquals( nFormCount, FormHome.getFormList( ).size( ) );
    }

    /**
     * The export of a form without the key "formMessage" is refused and creates no form
     *
     * @throws Exception
     *             if the form cannot be exported
     */
    @Test
    public void testImportWithoutFormMessageIsRefused( ) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper( );
        ObjectNode json = (ObjectNode) mapper.readTree( _formJsonService.jsonExportForm( _form.getId( ) ) );
        json.remove( "formMessage" );
        String strJson = mapper.writeValueAsString( json );
        int nFormCount = FormHome.getFormList( ).size( );

        assertThrows( FormJsonImportException.class, ( ) -> _formJsonService.jsonImportForm( strJson, Locale.FRENCH ) );
        assertEquals( nFormCount, FormHome.getFormList( ).size( ) );
    }
}
