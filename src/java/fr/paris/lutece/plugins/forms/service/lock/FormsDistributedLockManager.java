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

import fr.paris.lutece.plugins.forms.exception.LockException;

/**
 * Cluster-wide named lock for the forms plugin. Backed by the {@code forms_lucene_lock}
 * table so it works on any deployment (no Hazelcast requirement) and survives
 * JVM restarts via the lock expiration timestamp.
 *
 * Intended for short critical sections that must be serialised across instances
 * (response-quota enforcement, index writer acquisition, daemon runs, etc.).
 */
public interface FormsDistributedLockManager
{
    /**
     * Try to acquire a named lock. Non-blocking: returns immediately on contention.
     *
     * @param lockName  unique lock identifier (e.g. {@code "forms.quota.form.42"})
     * @param timeoutMs how long the lock remains valid before it auto-expires, in milliseconds
     * @return a {@link LockResult} handle to pass to {@link #releaseLock} or {@link #refreshLock}
     * @throws LockException if the lock is already held by someone else
     */
    LockResult acquireLock( String lockName, long timeoutMs ) throws LockException;

    /**
     * Release a lock previously acquired via {@link #acquireLock}.
     *
     * @param lockResult the handle returned by {@link #acquireLock}
     * @throws LockException on release failure
     */
    void releaseLock( LockResult lockResult ) throws LockException;

    /**
     * Extend the expiration date of a held lock (heartbeat pattern for long-running sections).
     *
     * @param lockResult the lock handle
     * @param timeoutMs  new expiration delay from now, in milliseconds
     * @return a refreshed {@link LockResult}
     * @throws LockException if the lock was lost (e.g. already expired and reclaimed)
     */
    LockResult refreshLock( LockResult lockResult, long timeoutMs ) throws LockException;

    /**
     * Release all locks (administrative reset, typically at shutdown).
     */
    void close( );
}
