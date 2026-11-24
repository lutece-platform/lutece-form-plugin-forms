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
package fr.paris.lutece.plugins.forms.web.admin;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.util.ReferenceList;

/**
 * Access control linked to a form when its settings are saved
 */
public class FormJspBeanAccessControlLinkTest extends LuteceTestCase
{
    private static final int LINKED_NOT_OFFERED = 25;
    private static final int OFFERED = 7;

    private static ReferenceList offered( )
    {
        ReferenceList list = new ReferenceList( );
        list.addItem( -1, "-" );
        list.addItem( OFFERED, "offered" );
        return list;
    }

    /**
     * The form is linked to a disabled access control (not offered): saving the form unchanged keeps the link
     */
    @Test
    public void testLinkNotOfferedIsKept( )
    {
        assertEquals( LINKED_NOT_OFFERED, FormJspBean.getAccessControlToLink( -1, LINKED_NOT_OFFERED, offered( ) ) );
    }

    /**
     * The user explicitly chooses an offered access control: it replaces the link
     */
    @Test
    public void testOfferedChoiceReplacesTheLink( )
    {
        assertEquals( OFFERED, FormJspBean.getAccessControlToLink( OFFERED, LINKED_NOT_OFFERED, offered( ) ) );
    }

    /**
     * Controls: "-" on a form linked to an offered access control removes the link; a value not offered is ignored
     */
    @Test
    public void testControls( )
    {
        assertEquals( -1, FormJspBean.getAccessControlToLink( -1, OFFERED, offered( ) ) );
        assertEquals( OFFERED, FormJspBean.getAccessControlToLink( 999, OFFERED, offered( ) ) );
    }
}
