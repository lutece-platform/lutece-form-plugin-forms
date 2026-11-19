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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;

/**
 * The redirection to the summary of a response just created must not carry an MVC action (LUT-32213): a parameter starting with "action_" is read
 * as an action, which the security token filter refuses without a token (403).
 */
public class FormResponseCreatedParameterTest
{
    /**
     * The URL built after the validation of a form only names the view of the summary.
     */
    @Test
    public void testResponseCreatedParameterIsNotAnAction( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( MVCUtils.PARAMETER_PAGE, FormResponseXPage.XPAGE_NAME );
        request.addParameter( MVCUtils.PARAMETER_VIEW, FormResponseXPage.VIEW_FORM_RESPONSE );
        request.addParameter( FormsConstants.PARAMETER_ID_RESPONSE, "1" );
        request.addParameter( FormsConstants.PARAMETER_ACTION_SUCCESS, "true" );

        assertNull( MVCUtils.getAction( request ) );
        assertEquals( FormResponseXPage.VIEW_FORM_RESPONSE, MVCUtils.getView( request ) );
    }
}
