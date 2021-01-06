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
package fr.paris.lutece.plugins.forms.service.entrytype;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.mock.web.MockHttpServletRequest;

import fr.paris.lutece.plugins.forms.util.FormsEntryUtils;
import fr.paris.lutece.plugins.forms.util.IdGenerator;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.test.LuteceTestCase;
import static fr.paris.lutece.plugins.genericattributes.business.MockField.createField;
import static fr.paris.lutece.plugins.genericattributes.business.MockEntry.createEntry;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class EntryTypeTermsOfServiceTest extends LuteceTestCase
{
    private static final String FIELD_LINK_CODE = "link";
    private static final String FIELD_TOS_CODE = "tos";
    private static final String FIELD_AGREEMENT_CODE = "agreement";

    private static final String PARAMETER_LINK = "link";
    private static final String PARAMETER_TOS = "tos";

    private EntryTypeTermsOfService _entryTypeTermsOfService;
    private MockHttpServletRequest _request;

    public void setUp( ) throws Exception
    {
        super.setUp( );

        _entryTypeTermsOfService = new EntryTypeTermsOfService( );
        _request = new MockHttpServletRequest( );
    }

    public void testGetRequestData( )
    {
        _request.addParameter( IEntryTypeService.PARAMETER_TITLE, "testGetRequestData.title" );
        _request.addParameter( IEntryTypeService.PARAMETER_CSS_CLASS, "testGetRequestData.cssClass" );
        _request.addParameter( PARAMETER_LINK, "testGetRequestData.link" );
        _request.addParameter( PARAMETER_TOS, "testGetRequestData.tos" );
        Entry entry = new Entry( );

        String strError = _entryTypeTermsOfService.getRequestData( entry, _request, Locale.getDefault( ) );

        assertThat( strError, is( nullValue( ) ) );
        assertThat( entry.getTitle( ), is( "testGetRequestData.title" ) );
        assertThat( entry.getCSSClass( ), is( "testGetRequestData.cssClass" ) );
        assertThatFieldIsPresent( entry, FIELD_LINK_CODE, "testGetRequestData.link" );
        assertThatFieldIsPresent( entry, FIELD_TOS_CODE, "testGetRequestData.tos" );
        assertThatFieldIsPresent( entry, FIELD_AGREEMENT_CODE, "false" );
    }

    private void assertThatFieldIsPresent( Entry entry, String strFieldCode, String strFieldValue )
    {
        Field field = FormsEntryUtils.findFieldByCode( entry, strFieldCode );

        assertThat( field, is( not( nullValue( ) ) ) );
        assertThat( field.getValue( ), is( strFieldValue ) );
    }

    public void testGetRequestDataWhenTitleIsMissing( )
    {
        _request.addParameter( IEntryTypeService.PARAMETER_CSS_CLASS, "testGetRequestDataWhenTitleIsMissing.cssClass" );
        _request.addParameter( PARAMETER_LINK, "testGetRequestDataWhenTitleIsMissing.link" );
        _request.addParameter( PARAMETER_TOS, "testGetRequestDataWhenTitleIsMissing.tos" );
        Entry entry = createEntry( );

        String strError = _entryTypeTermsOfService.getRequestData( entry, _request, Locale.getDefault( ) );

        assertThat( strError, is( not( nullValue( ) ) ) );
        assertThat( entry.getTitle( ), is( nullValue( ) ) );
        assertThat( entry.getCSSClass( ), is( nullValue( ) ) );
        assertThatFieldIsNotPresent( entry, FIELD_LINK_CODE );
        assertThatFieldIsNotPresent( entry, FIELD_TOS_CODE );
        assertThatFieldIsNotPresent( entry, FIELD_AGREEMENT_CODE );
    }

    private void assertThatFieldIsNotPresent( Entry entry, String strFieldCode )
    {
        Field field = FormsEntryUtils.findFieldByCode( entry, strFieldCode );

        assertThat( field, is( nullValue( ) ) );
    }

    public void testGetRequestDataWhenCssClassIsMissing( )
    {
        _request.addParameter( IEntryTypeService.PARAMETER_TITLE, "testGetRequestDataWhenCssClassIsMissing.title" );
        _request.addParameter( PARAMETER_LINK, "testGetRequestDataWhenCssClassIsMissing.link" );
        _request.addParameter( PARAMETER_TOS, "testGetRequestDataWhenCssClassIsMissing.tos" );
        Entry entry = createEntry( );

        String strError = _entryTypeTermsOfService.getRequestData( entry, _request, Locale.getDefault( ) );

        assertThat( strError, is( nullValue( ) ) );
        assertThat( entry.getTitle( ), is( "testGetRequestDataWhenCssClassIsMissing.title" ) );
        assertThat( entry.getCSSClass( ), is( nullValue( ) ) );
        assertThatFieldIsPresent( entry, FIELD_LINK_CODE, "testGetRequestDataWhenCssClassIsMissing.link" );
        assertThatFieldIsPresent( entry, FIELD_TOS_CODE, "testGetRequestDataWhenCssClassIsMissing.tos" );
        assertThatFieldIsPresent( entry, FIELD_AGREEMENT_CODE, "false" );
    }

    public void testGetRequestDataWhenLinkIsMissing( )
    {
        _request.addParameter( IEntryTypeService.PARAMETER_TITLE, "testGetRequestDataWhenLinkIsMissing.title" );
        _request.addParameter( IEntryTypeService.PARAMETER_CSS_CLASS, "testGetRequestDataWhenLinkIsMissing.cssClass" );
        _request.addParameter( PARAMETER_TOS, "testGetRequestDataWhenLinkIsMissing.tos" );
        Entry entry = createEntry( );

        String strError = _entryTypeTermsOfService.getRequestData( entry, _request, Locale.getDefault( ) );

        assertThat( strError, is( not( nullValue( ) ) ) );
        assertThat( entry.getTitle( ), is( nullValue( ) ) );
        assertThat( entry.getCSSClass( ), is( nullValue( ) ) );
        assertThatFieldIsNotPresent( entry, FIELD_LINK_CODE );
        assertThatFieldIsNotPresent( entry, FIELD_TOS_CODE );
        assertThatFieldIsNotPresent( entry, FIELD_AGREEMENT_CODE );
    }

    public void testGetRequestDataWhenTOSIsMissing( )
    {
        _request.addParameter( IEntryTypeService.PARAMETER_TITLE, "testGetRequestDataWhenTOSIsMissing.title" );
        _request.addParameter( IEntryTypeService.PARAMETER_CSS_CLASS, "testGetRequestDataWhenTOSIsMissing.cssClass" );
        _request.addParameter( PARAMETER_LINK, "testGetRequestDataWhenTOSIsMissing.link" );
        Entry entry = createEntry( );

        String strError = _entryTypeTermsOfService.getRequestData( entry, _request, Locale.getDefault( ) );

        assertThat( strError, is( not( nullValue( ) ) ) );
        assertThat( entry.getTitle( ), is( nullValue( ) ) );
        assertThat( entry.getCSSClass( ), is( nullValue( ) ) );
        assertThatFieldIsNotPresent( entry, FIELD_LINK_CODE );
        assertThatFieldIsNotPresent( entry, FIELD_TOS_CODE );
        assertThatFieldIsNotPresent( entry, FIELD_AGREEMENT_CODE );
    }

    public void testGetResponseData( )
    {
        Entry entry = createEntry( );
        Field fieldAgreement = createField( FIELD_AGREEMENT_CODE, "false" );
        Field fieldTOS = createField( FIELD_TOS_CODE, "testGetResponseData.tos" );
        entry.getFields( ).add( fieldAgreement );
        entry.getFields( ).add( fieldTOS );
        List<Response> listResponse = new ArrayList<>( );
        _request.addParameter( IEntryTypeService.PREFIX_ATTRIBUTE + entry.getIdEntry( ), Integer.toString( fieldAgreement.getIdField( ) ) );

        GenericAttributeError error = _entryTypeTermsOfService.getResponseData( entry, _request, listResponse, Locale.getDefault( ) );

        assertThat( error, is( nullValue( ) ) );
        assertThat( listResponse.size( ), is( 2 ) );
        assertThatResponseIsPresent( listResponse, FIELD_AGREEMENT_CODE, "true" );
        assertThatResponseIsPresent( listResponse, FIELD_TOS_CODE, "testGetResponseData.tos" );
    }

    private void assertThatResponseIsPresent( List<Response> listResponse, String strFieldCode, String strFieldValue )
    {
        boolean bFound = false;

        for ( Response response : listResponse )
        {
            Field field = response.getField( );

            if ( field != null && strFieldCode.equals( field.getCode( ) ) )
            {
                assertThat( field.getValue( ), is( strFieldValue ) );
                bFound = true;
            }
        }

        assertThat( bFound, is( true ) );
    }

    public void testGetResponseDataWhenAgreementIsNotInRequest( )
    {
        Entry entry = createEntry( );
        Field fieldAgreement = createField( FIELD_AGREEMENT_CODE, "false" );
        Field fieldTOS = createField( FIELD_TOS_CODE, "testGetResponseData.tos" );
        entry.getFields( ).add( fieldAgreement );
        entry.getFields( ).add( fieldTOS );
        List<Response> listResponse = new ArrayList<>( );

        GenericAttributeError error = _entryTypeTermsOfService.getResponseData( entry, _request, listResponse, Locale.getDefault( ) );

        assertThat( error, is( not( nullValue( ) ) ) );
        assertThat( listResponse.size( ), is( 1 ) );
        assertThatResponseIsNotPresent( listResponse, FIELD_AGREEMENT_CODE );
        assertThatResponseIsNotPresent( listResponse, FIELD_TOS_CODE );
    }

    private void assertThatResponseIsNotPresent( List<Response> listResponse, String strFieldCode )
    {
        boolean bFound = false;

        for ( Response response : listResponse )
        {
            Field field = response.getField( );

            if ( field != null && strFieldCode.equals( field.getCode( ) ) )
            {
                bFound = true;
            }
        }

        assertThat( bFound, is( false ) );
    }

    public void testGetResponseDataWhenAgreementFieldIsNotInEntry( )
    {
        Entry entry = createEntry( );
        Field fieldAgreement = createField( FIELD_AGREEMENT_CODE, "false" );
        Field fieldTOS = createField( FIELD_TOS_CODE, "testGetResponseData.tos" );
        entry.getFields( ).add( fieldAgreement );
        entry.getFields( ).add( fieldTOS );
        List<Response> listResponse = new ArrayList<>( );
        _request.addParameter( IEntryTypeService.PREFIX_ATTRIBUTE + entry.getIdEntry( ), Integer.toString( IdGenerator.generateId( ) ) );

        GenericAttributeError error = _entryTypeTermsOfService.getResponseData( entry, _request, listResponse, Locale.getDefault( ) );

        assertThat( error, is( not( nullValue( ) ) ) );
        assertThat( listResponse.size( ), is( 1 ) );
        assertThatResponseIsNotPresent( listResponse, FIELD_AGREEMENT_CODE );
        assertThatResponseIsNotPresent( listResponse, FIELD_TOS_CODE );
    }
}
