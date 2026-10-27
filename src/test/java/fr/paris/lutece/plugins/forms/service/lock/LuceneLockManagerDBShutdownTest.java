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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.form.lock.ILockDAO;
import fr.paris.lutece.plugins.forms.exception.LockException;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Once the instance stops, it holds no lock: a daemon run still going on must not hold a lock after the shutdown released
 * the locks of the instance, the other instances would wait for its expiry (15 minutes for the index writer before the lease
 * was shortened) without indexing anything
 */
public class LuceneLockManagerDBShutdownTest extends LuteceTestCase
{
    private static final String LOCK = "test.lock";

    @Test
    public void testNoLockTakenAfterShutdown( ) throws Exception
    {
        TableDAO dao = new TableDAO( );
        LuceneLockManagerDB manager = new LuceneLockManagerDB( dao );

        manager.releaseLock( manager.acquireLock( LOCK, 60_000L ) );
        assertNull( dao.holder( LOCK ) );

        manager.releaseOwnLocksOnShutdown( );
        assertThrows( LockException.class, ( ) -> manager.acquireLock( LOCK, 60_000L ) );
        assertNull( dao.holder( LOCK ), "the stopped instance still holds the lock" );
    }

    @Test
    public void testLockTakenDuringShutdownIsReleased( ) throws Exception
    {
        TableDAO dao = new TableDAO( );
        LuceneLockManagerDB manager = new LuceneLockManagerDB( dao );
        dao._acquireStarted = new CountDownLatch( 1 );
        dao._acquireMayWrite = new CountDownLatch( 1 );

        ExecutorService executor = Executors.newSingleThreadExecutor( );
        try
        {
            // An indexing run asks for the lock; its statement is still running when the instance stops
            Future<?> acquisition = executor.submit( ( ) -> manager.acquireLock( LOCK, 60_000L ) );
            assertTrue( dao._acquireStarted.await( 10, TimeUnit.SECONDS ) );
            manager.releaseOwnLocksOnShutdown( );
            dao._acquireMayWrite.countDown( );

            try
            {
                acquisition.get( 10, TimeUnit.SECONDS );
            }
            catch( java.util.concurrent.ExecutionException e )
            {
                assertTrue( e.getCause( ) instanceof LockException );
            }
            assertNull( dao.holder( LOCK ), "the lock taken while the instance stopped stays held until its expiry" );
        }
        finally
        {
            executor.shutdownNow( );
        }
    }

    /**
     * The lock table in memory: the instance holding each lock
     */
    private static class TableDAO implements ILockDAO
    {
        private final Map<String, String> _mapHolders = new ConcurrentHashMap<>( );
        private final Map<String, String> _mapIndexByUuid = new ConcurrentHashMap<>( );
        private volatile CountDownLatch _acquireStarted;
        private volatile CountDownLatch _acquireMayWrite;

        String holder( String indexName )
        {
            return _mapHolders.get( indexName );
        }

        @Override
        public boolean acquire( String indexName, String instanceName, String uuid, long ttlSeconds, Plugin plugin )
        {
            if ( _acquireStarted != null )
            {
                _acquireStarted.countDown( );
                try
                {
                    _acquireMayWrite.await( 10, TimeUnit.SECONDS );
                }
                catch( InterruptedException e )
                {
                    Thread.currentThread( ).interrupt( );
                    return false;
                }
            }
            if ( _mapHolders.putIfAbsent( indexName, instanceName ) != null )
            {
                return false;
            }
            _mapIndexByUuid.put( uuid, indexName );
            return true;
        }

        @Override
        public void release( String uuid, Plugin plugin )
        {
            String indexName = _mapIndexByUuid.remove( uuid );
            if ( indexName != null )
            {
                _mapHolders.remove( indexName );
            }
        }

        @Override
        public boolean refresh( String uuid, long ttlSeconds, Plugin plugin )
        {
            return _mapIndexByUuid.containsKey( uuid );
        }

        @Override
        public int releaseByInstance( String instanceName, Plugin plugin )
        {
            int nReleased = 0;
            for ( Map.Entry<String, String> entry : _mapHolders.entrySet( ) )
            {
                if ( entry.getValue( ).equals( instanceName ) && _mapHolders.remove( entry.getKey( ), instanceName ) )
                {
                    _mapIndexByUuid.values( ).remove( entry.getKey( ) );
                    nReleased++;
                }
            }
            return nReleased;
        }
    }
}
