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
package fr.paris.lutece.plugins.forms.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * The return URL of a form is written as a link only when it is relative or http(s) (LUT-33153).
 */
public class FormsUtilsReturnUrlTest
{
    /**
     * Relative and http(s) URLs are accepted.
     */
    @Test
    public void testSafeUrls( )
    {
        for ( String strUrl : new String [ ] {
                "jsp/site/Portal.jsp?page=forms", "/site/jsp/site/Portal.jsp", "https://lutece.paris.fr/x", "HTTP://example.org", "Portal.jsp?a=b:c", "#top"
        } )
        {
            assertTrue( FormsUtils.isSafeReturnUrl( strUrl ), strUrl );
        }
    }

    /**
     * Other schemes, another host (//, backslashes) and an empty URL are refused.
     */
    @Test
    public void testUnsafeUrls( )
    {
        for ( String strUrl : new String [ ] {
                "javascript:alert(1)", " JavaScript:alert(1)", "java\tscript:alert(1)", "data:text/html,<script>alert(1)</script>", "vbscript:x",
                "//evil.example", " //evil.example", "\\\\evil.example", "/\\evil.example", "", null
        } )
        {
            assertFalse( FormsUtils.isSafeReturnUrl( strUrl ), String.valueOf( strUrl ) );
        }
    }
}
