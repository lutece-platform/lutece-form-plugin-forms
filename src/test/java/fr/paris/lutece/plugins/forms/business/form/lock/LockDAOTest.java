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
package fr.paris.lutece.plugins.forms.business.form.lock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.service.FormsPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.util.sql.DAOUtil;
import jakarta.inject.Inject;

/**
 * Cluster-wide named lock backed by the forms_lucene_lock table (Lucene index lock and form quota locks).
 */
public class LockDAOTest extends LuteceTestCase
{
    private static final long TTL_SECONDS = 60;

    @Inject
    private ILockDAO _lockDao;

    private Plugin plugin( )
    {
        return FormsPlugin.getPlugin( );
    }

    private static final String LOCK_NAME_PREFIX = "test.lock.";

    private static String newLockName( )
    {
        return LOCK_NAME_PREFIX + UUID.randomUUID( ).toString( ).substring( 0, 8 );
    }

    /**
     * Removes the lock rows created by the tests.
     */
    @AfterEach
    protected void removeTestLocks( )
    {
        try ( DAOUtil daoUtil = new DAOUtil( "DELETE FROM forms_lucene_lock WHERE index_name LIKE ?", plugin( ) ) )
        {
            daoUtil.setString( 1, LOCK_NAME_PREFIX + "%" );
            daoUtil.executeUpdate( );
        }
    }

    private static String newUuid( )
    {
        return UUID.randomUUID( ).toString( );
    }

    /**
     * Quota locks (forms.quota.form.&lt;id&gt;) have no row in the SQL scripts: the first acquisition must create it.
     */
    @Test
    public void testAcquireCreatesMissingLockRow( )
    {
        String lockName = newLockName( );

        assertTrue( _lockDao.acquire( lockName, "node-a", newUuid( ), TTL_SECONDS, plugin( ) ),
                "A lock with no row yet must be acquired" );
    }

    /**
     * A held lock must not be granted to another holder until it is released.
     */
    @Test
    public void testAcquireIsExclusiveUntilRelease( )
    {
        String lockName = newLockName( );
        String uuidA = newUuid( );

        assertTrue( _lockDao.acquire( lockName, "node-a", uuidA, TTL_SECONDS, plugin( ) ), "First holder must acquire the lock" );
        assertFalse( _lockDao.acquire( lockName, "node-b", newUuid( ), TTL_SECONDS, plugin( ) ),
                "A second holder must not acquire a lock that is held and not expired" );

        _lockDao.release( uuidA, plugin( ) );

        assertTrue( _lockDao.acquire( lockName, "node-b", newUuid( ), TTL_SECONDS, plugin( ) ), "The lock must be free after release" );
    }

    /**
     * The holder of a valid lock can extend it (heartbeat of the Lucene indexer).
     */
    @Test
    public void testRefreshWhileHeld( )
    {
        String lockName = newLockName( );
        String uuid = newUuid( );

        assertTrue( _lockDao.acquire( lockName, "node-a", uuid, TTL_SECONDS, plugin( ) ) );
        assertTrue( _lockDao.refresh( uuid, TTL_SECONDS, plugin( ) ), "The holder must be able to refresh its lock" );
    }

    /**
     * An expired lock (holder crashed) can be reclaimed by another holder.
     */
    @Test
    public void testExpiredLockCanBeReclaimed( )
    {
        String lockName = newLockName( );

        assertTrue( _lockDao.acquire( lockName, "node-a", newUuid( ), -1, plugin( ) ) );
        assertTrue( _lockDao.acquire( lockName, "node-b", newUuid( ), TTL_SECONDS, plugin( ) ),
                "An expired lock must be reclaimable" );
    }

    /**
     * Instances acquiring a lock whose row does not exist yet at the same time: the row is created once and only one of them gets the lock.
     *
     * @throws Exception
     *             if a thread fails
     */
    @Test
    public void testConcurrentFirstAcquisition( ) throws Exception
    {
        String lockName = newLockName( );
        int nThreads = 8;
        CountDownLatch start = new CountDownLatch( 1 );
        ExecutorService executor = Executors.newFixedThreadPool( nThreads );
        try
        {
            List<Future<Boolean>> results = new ArrayList<>( );
            for ( int i = 0; i < nThreads; i++ )
            {
                String strInstance = "node-" + i;
                Callable<Boolean> acquire = ( ) -> {
                    start.await( );
                    return _lockDao.acquire( lockName, strInstance, newUuid( ), TTL_SECONDS, plugin( ) );
                };
                results.add( executor.submit( acquire ) );
            }
            start.countDown( );
            int nAcquired = 0;
            for ( Future<Boolean> result : results )
            {
                nAcquired += Boolean.TRUE.equals( result.get( ) ) ? 1 : 0;
            }
            assertEquals( 1, nAcquired, "Exactly one instance must get the lock" );
        }
        finally
        {
            executor.shutdownNow( );
        }
    }
}
