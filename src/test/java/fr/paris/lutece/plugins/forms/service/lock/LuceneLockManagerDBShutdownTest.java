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
package fr.paris.lutece.plugins.forms.service.lock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.form.lock.ILockDAO;
import fr.paris.lutece.plugins.forms.exception.LockException;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Once the instance stops, no lock is taken: a daemon run still going on must not hold a lock released by the shutdown
 */
public class LuceneLockManagerDBShutdownTest extends LuteceTestCase
{
    @Test
    public void testNoAcquisitionAfterShutdown( ) throws Exception
    {
        CountingDAO dao = new CountingDAO( );
        LuceneLockManagerDB manager = new LuceneLockManagerDB( dao );

        manager.releaseLock( manager.acquireLock( "test.lock", 60_000L ) );
        assertEquals( 1, dao._nAcquire );

        manager.releaseOwnLocksOnShutdown( );
        assertEquals( 1, dao._nReleaseByInstance );
        assertThrows( LockException.class, ( ) -> manager.acquireLock( "test.lock", 60_000L ) );
        assertEquals( 1, dao._nAcquire, "the database is not asked for a lock once the instance stops" );
    }

    /**
     * DAO granting every lock and counting the calls
     */
    private static class CountingDAO implements ILockDAO
    {
        private int _nAcquire;
        private int _nReleaseByInstance;

        @Override
        public boolean acquire( String indexName, String instanceName, String uuid, long ttlSeconds, Plugin plugin )
        {
            _nAcquire++;
            return true;
        }

        @Override
        public void release( String uuid, Plugin plugin )
        {
            // nothing to release
        }

        @Override
        public boolean refresh( String uuid, long ttlSeconds, Plugin plugin )
        {
            return true;
        }

        @Override
        public int releaseByInstance( String instanceName, Plugin plugin )
        {
            _nReleaseByInstance++;
            return 0;
        }
    }
}
