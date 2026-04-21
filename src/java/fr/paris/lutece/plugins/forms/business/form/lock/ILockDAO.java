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
package fr.paris.lutece.plugins.forms.business.form.lock;

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * Low-level DB operations on the {@code forms_lucene_lock} table.
 *
 * All timestamp arithmetic is computed DB-side ({@code CURRENT_TIMESTAMP}), so JVM clock skew
 * between instances of a cluster cannot cause a valid lock to appear expired (or vice versa).
 */
public interface ILockDAO
{
    /**
     * Try to acquire the row for {@code indexName}. Succeeds if the row is currently
     * unlocked or if its {@code expired_date} is in the past (reclaim after crash).
     *
     * @param indexName    lock key
     * @param instanceName runtime-unique identifier of THIS JVM (see {@code FormsInstanceId})
     * @param uuid         handle tying the caller to the row, used by {@link #refresh}/{@link #release}
     * @param ttlSeconds   how long the lock stays valid from now, in seconds
     * @param plugin       plugin handle for the DB pool
     * @return {@code true} if the row was taken, {@code false} if another instance already holds it
     */
    boolean acquire( String indexName, String instanceName, String uuid, long ttlSeconds, Plugin plugin );

    /**
     * Release the row owned by {@code uuid}. No-op if the handle is unknown.
     */
    void release( String uuid, Plugin plugin );

    /**
     * Extend the expiration of the row owned by {@code uuid}, but ONLY if it still
     * belongs to us (row not stolen after TTL expiration). Returns {@code false} if
     * the lock has been lost — the caller must abort its critical section.
     *
     * @return {@code true} if still ours and extended, {@code false} if lost
     */
    boolean refresh( String uuid, long ttlSeconds, Plugin plugin );

    /**
     * Release every row currently owned by the given instance. Safe in cluster:
     * rows owned by other instances are never touched.
     *
     * @return the number of rows released
     */
    int releaseByInstance( String instanceName, Plugin plugin );
}
