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

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Runtime-unique identifier for THIS JVM.
 *
 * {@code lutece.name} identifies the site (product), not the node — it is identical on every
 * instance of the same deployment, so it cannot be used to track who owns a distributed lock.
 * We need a value that is unique across concurrent JVMs and changes on every restart, so the
 * lock table can safely attribute rows to a single live JVM.
 *
 * Shape: {@code hostname-pid} (fallback {@code unknown-<pid>} if the hostname is not available).
 * Stable across the lifetime of this JVM, different in any other JVM — including the same JVM
 * restarted on the same host (PID changes).
 *
 * Recovery of locks left behind by a crashed JVM is NOT done by matching this ID on restart
 * (a new JVM has a new ID); it relies on the TTL + heartbeat scheme in the lock DAO, which
 * reclaims expired rows at {@code acquire()} time.
 */
public final class FormsInstanceId
{
    public static final String VALUE = compute( );

    private FormsInstanceId( )
    {
    }

    private static String compute( )
    {
        String host = System.getenv( "HOSTNAME" );
        if ( host == null || host.isBlank( ) )
        {
            try
            {
                host = InetAddress.getLocalHost( ).getHostName( );
            }
            catch ( UnknownHostException e )
            {
                host = "unknown";
            }
        }
        return host + "-" + ProcessHandle.current( ).pid( );
    }
}
