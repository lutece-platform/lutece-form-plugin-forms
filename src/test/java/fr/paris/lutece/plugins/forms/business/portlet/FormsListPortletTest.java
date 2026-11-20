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
package fr.paris.lutece.plugins.forms.business.portlet;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import jakarta.enterprise.inject.spi.CDI;

/**
 * The forms list portlet only shows the forms still published, and a deleted form is removed from the portlets
 */
public class FormsListPortletTest extends LuteceTestCase
{
    private static final int ID_PORTLET = 987654;

    private static int createForm( String strTitle, boolean bActive )
    {
        Form form = new Form( );
        form.setTitle( strTitle );
        form.setDescription( strTitle );
        form.setBreadcrumbName( "horizontalBreadcrumb" );
        long lNow = System.currentTimeMillis( );
        form.setAvailabilityStartDate( bActive ? new Timestamp( lNow - 3600000L ) : null );
        form.setAvailabilityEndDate( null );
        FormHome.create( form );
        return form.getId( );
    }

    @Test
    public void testUnpublishedFormIsNotShownAndDeletedFormIsRemoved( )
    {
        int nIdActive = createForm( "fo30-active-form", true );
        int nIdInactive = createForm( "fo30-inactive-form", false );
        IFormsListPortletDAO dao = CDI.current( ).select( IFormsListPortletDAO.class ).get( );
        FormsListPortlet portlet = new FormsListPortlet( );
        portlet.setId( ID_PORTLET );
        portlet.setFormsIdList( new ArrayList<>( List.of( nIdActive, nIdInactive ) ) );
        dao.insert( portlet );
        try
        {
            String strHtml = portlet.getHtmlContent( new MockHttpServletRequest( ) );
            assertTrue( strHtml.contains( "fo30-active-form" ), strHtml );
            assertFalse( strHtml.contains( "fo30-inactive-form" ), strHtml );

            FormsListPortletHome.removeForm( nIdInactive );
            assertEquals( List.of( nIdActive ), ( (FormsListPortlet) dao.load( ID_PORTLET ) ).getFormsIdList( ) );
        }
        finally
        {
            dao.delete( ID_PORTLET );
            FormHome.remove( nIdActive );
            FormHome.remove( nIdInactive );
        }
    }
}
