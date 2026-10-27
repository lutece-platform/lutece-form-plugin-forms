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
package fr.paris.lutece.plugins.forms.util;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.FormAction;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import jakarta.inject.Inject;

/**
 * Tokens of the form actions called by a link
 */
public class FormsUtilsTest extends LuteceTestCase
{
    @Inject
    private SecurityTokenService _securityTokenService;

    /**
     * A form action calling an action of a controller gets a token of this action, that the core accepts as _csrftoken; a view gets none
     */
    @Test
    public void testGetFormActionLinkTokens( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );

        Map<String, String> mapTokens = FormsUtils.getFormActionLinkTokens( request,
                List.of( newFormAction( "jsp/admin/plugins/forms/ManageForms.jsp?action=duplicateForm" ),
                        newFormAction( "jsp/admin/plugins/forms/ManageForms.jsp?action=doExportJson&format=json" ),
                        newFormAction( "jsp/admin/plugins/forms/ManageSteps.jsp?view=manageSteps" ) ) );

        assertEquals( 2, mapTokens.size( ) );
        assertTrue( _securityTokenService.consumeToken( request, "duplicateForm", mapTokens.get( "duplicateForm" ) ) );
        assertTrue( _securityTokenService.consumeToken( request, "doExportJson", mapTokens.get( "doExportJson" ) ) );
    }

    private static FormAction newFormAction( String strUrl )
    {
        FormAction formAction = new FormAction( );
        formAction.setUrl( strUrl );
        return formAction;
    }
}
