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

import org.junit.jupiter.api.Test;

import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;

/**
 * The page index of the paginated lists of the back office
 */
public class AbstractJspBeanPageIndexTest extends LuteceTestCase
{
    private static class PaginatedJspBean extends AbstractJspBean
    {
        private static final long serialVersionUID = 1L;
    }

    @Test
    public void testPageIndexNotAPositiveNumberIsTheFirstPage( )
    {
        PaginatedJspBean bean = new PaginatedJspBean( );

        for ( String strPageIndex : new String [ ] {
                "0", "-3", "abc"
        } )
        {
            MockHttpServletRequest request = new MockHttpServletRequest( );
            request.setParameter( "page_index", strPageIndex );
            request.setParameter( "items_per_page", "10" );
            bean.initiatePaginatorProperties( request );

            assertEquals( 0, bean.getIndexStart( ), strPageIndex );
            assertTrue( "1".equals( bean._strCurrentPageIndex ), strPageIndex );
        }

        // the next request without page index is still on the first page
        bean.initiatePaginatorProperties( new MockHttpServletRequest( ) );
        assertEquals( 0, bean.getIndexStart( ) );

        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.setParameter( "page_index", "3" );
        bean.initiatePaginatorProperties( request );
        assertEquals( 20, bean.getIndexStart( ) );
    }
}
