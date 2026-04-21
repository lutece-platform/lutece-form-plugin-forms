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
package fr.paris.lutece.plugins.forms.service.lock;

import fr.paris.lutece.plugins.forms.business.form.lock.ILockDAO;
import fr.paris.lutece.plugins.forms.exception.LockException;
import fr.paris.lutece.plugins.forms.service.FormsInstanceId;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppLogService;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
@Named( "forms.luceneLockManager" )
public class LuceneLockManagerDB implements LuceneLockManager
{
    private static final Plugin _plugin = PluginService.getPlugin( "forms" );

    private final ILockDAO _lockDao;

    @Inject
    LuceneLockManagerDB( ILockDAO lockDao )
    {
        _lockDao = lockDao;
    }

    @Override
    public LockResult acquireLock( String indexName, long timeoutMs ) throws LockException
    {
        String uuid = UUID.randomUUID( ).toString( );
        long ttlSeconds = toCeilSeconds( timeoutMs );

        if ( _lockDao.acquire( indexName, FormsInstanceId.VALUE, uuid, ttlSeconds, _plugin ) )
        {
            // The real expired_date lives DB-side now; expose a best-effort local estimate so
            // callers keep a roughly-correct handle (not used for any safety-critical decision).
            Timestamp estimatedExpiry = Timestamp.from( Instant.now( ).plusSeconds( ttlSeconds ) );
            return LockResult.createLockSuccess( indexName, uuid, estimatedExpiry );
        }
        throw new LockException( );
    }

    @Override
    public void releaseLock( LockResult lockResult ) throws LockException
    {
        _lockDao.release( lockResult.getIdLock( ), _plugin );
    }

    @Override
    public LockResult refreshLock( LockResult lockResult, long timeoutMs ) throws LockException
    {
        long ttlSeconds = toCeilSeconds( timeoutMs );

        if ( _lockDao.refresh( lockResult.getIdLock( ), ttlSeconds, _plugin ) )
        {
            Timestamp estimatedExpiry = Timestamp.from( Instant.now( ).plusSeconds( ttlSeconds ) );
            return LockResult.refreshLockSuccess( lockResult, estimatedExpiry );
        }
        throw new LockException( );
    }

    /**
     * Release every lock currently held by THIS JVM. Kept to honour the interface contract;
     * it is deliberately scoped to the current instance — blanket-releasing every row in the
     * cluster would let this node cancel indexing runs owned by peers.
     */
    @Override
    public void close( )
    {
        releaseOwnInstanceLocks( );
    }

    @Override
    public int releaseOwnInstanceLocks( )
    {
        return _lockDao.releaseByInstance( FormsInstanceId.VALUE, _plugin );
    }

    /**
     * Graceful-shutdown hook: release every lock still owned by THIS JVM before the CDI
     * container stops. Covers orderly redeploys / {@code SIGTERM}; hard crashes are
     * recovered by the TTL + heartbeat mechanism in the DAO's acquire path.
     */
    @PreDestroy
    void releaseOwnLocksOnShutdown( )
    {
        try
        {
            int released = releaseOwnInstanceLocks( );
            if ( released > 0 )
            {
                AppLogService.info( "[forms] released " + released + " lock(s) owned by instance "
                        + FormsInstanceId.VALUE + " on shutdown" );
            }
        }
        catch ( Exception e )
        {
            AppLogService.error( "[forms] failed to release locks on shutdown for instance "
                    + FormsInstanceId.VALUE, e );
        }
    }

    /**
     * SQL {@code TIMESTAMPADD(SQL_TSI_SECOND, ?, ...)} has second granularity; round millis up
     * so a submillisecond TTL never becomes zero (which would make the lock immediately expired).
     */
    private static long toCeilSeconds( long ms )
    {
        long seconds = ( ms + 999L ) / 1000L;
        return Math.max( 1L, seconds );
    }
}
