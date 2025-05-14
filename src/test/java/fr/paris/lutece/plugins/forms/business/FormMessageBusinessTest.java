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
package fr.paris.lutece.plugins.forms.business;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.test.LuteceTestCase;

public class FormMessageBusinessTest extends LuteceTestCase
{
    @Test
    public void testCRUD( )
    {
        FormMessage formMessage = new FormMessage( );
        formMessage.setIdForm( 1 );
        formMessage.setEndMessageDisplay( true );
        formMessage.setEndMessage( "Message" );

        FormMessageHome.create( formMessage );

        FormMessage loaded = FormMessageHome.findByForm( formMessage.getId( ) );
        assertNotNull( loaded );
        assertEquals( formMessage.getIdForm( ), loaded.getIdForm( ) );
        assertEquals( formMessage.getEndMessageDisplay( ), loaded.getEndMessageDisplay( ) );
        assertEquals( formMessage.getEndMessage( ), loaded.getEndMessage( ) );

        formMessage.setEndMessage( "Message 2" );
        FormMessageHome.update( formMessage );

        loaded = FormMessageHome.findByForm( formMessage.getId( ) );
        assertNotNull( loaded );
        assertEquals( formMessage.getEndMessage( ), loaded.getEndMessage( ) );

        FormMessageHome.remove( formMessage.getId( ) );

        loaded = FormMessageHome.findByForm( formMessage.getId( ) );
        assertNull( loaded );
    }
}
