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
package fr.paris.lutece.plugins.forms.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests the message about a JVM-local Lucene index
 */
public class FormsPluginTest
{
    @Test
    public void testDefaultTmpdirPathDoesNotAdviseIndexInWebapp( )
    {
        String strMessage = FormsPlugin.getNodeLocalIndexMessage( false, "java.io.tmpdir/plugins/forms/index", "/tmp" );

        assertTrue( strMessage.contains( "(java.io.tmpdir/plugins/forms/index) is a relative path" ) );
        assertTrue( strMessage.contains( "java.io.tmpdir is not replaced by the temporary directory" ) );
        assertFalse( strMessage.contains( "indexInWebapp" ) );
        assertTrue( strMessage.contains( "not persistent, even on a single instance" ) );
    }

    @Test
    public void testResolvedTmpdirPath( )
    {
        String strMessage = FormsPlugin.getNodeLocalIndexMessage( false, "/tmp/plugins/forms/index", "/tmp" );

        assertTrue( strMessage.contains( "is under the java.io.tmpdir directory" ) );
        assertFalse( strMessage.contains( "indexInWebapp" ) );
    }

    @Test
    public void testIndexInWebapp( )
    {
        String strMessage = FormsPlugin.getNodeLocalIndexMessage( true, "/WEB-INF/plugins/forms/index", "/tmp" );

        assertTrue( strMessage.contains( "indexInWebapp=true resolves" ) );
        assertTrue( strMessage.contains( "Set forms.internalIndexer.lucene.indexInWebapp=false" ) );
    }

    @Test
    public void testSharedPathHasNoMessage( )
    {
        assertNull( FormsPlugin.getNodeLocalIndexMessage( false, "/mnt/shared/forms/index", "/tmp" ) );
    }

    @Test
    public void testOtherRelativePath( )
    {
        String strMessage = FormsPlugin.getNodeLocalIndexMessage( false, "index/forms", "/tmp" );

        assertTrue( strMessage.contains( "(index/forms) is a relative path" ) );
        assertFalse( strMessage.contains( "is not replaced" ) );
    }
}
