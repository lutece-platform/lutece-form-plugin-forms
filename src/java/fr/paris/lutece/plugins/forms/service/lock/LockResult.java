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

import java.sql.Timestamp;

public class LockResult {

    private String _nameLock;
    private String _idLock;
    private Timestamp _expiredDate;

    /**
     * Create a LockResult
     * @param nameLock the name of the lock
     * @param idLock the unique identifier of the current lock
     * @param expiredDate the expired date of the lock
     * @return LockResult
     */
    public static LockResult createLockSuccess(String nameLock, String idLock, Timestamp expiredDate)
    {
        LockResult lockResult = new LockResult();
        lockResult._nameLock = nameLock;
        lockResult._idLock = idLock;
        lockResult._expiredDate = expiredDate;
        return lockResult;
    }

    /**
     * refresh a LockResult
     * @param oldLock the old lockResult
     * @param newExpiredDate the new expiredDate
     * @return LockResult
     */
    public static LockResult refreshLockSuccess(LockResult oldLock, Timestamp newExpiredDate)
    {
        LockResult lockResult = new LockResult();
        lockResult._nameLock = oldLock._nameLock;
        lockResult._idLock = oldLock._idLock;
        lockResult._expiredDate = newExpiredDate;
        return lockResult;
    }

    /**
     * The name of the lock
     * @return nameLock
     */
    public String getNameLock() {
        return _nameLock;
    }

    /**
     * The expired date of the Lock
     * @return expiredDate
     */
    public Timestamp getExpiredDate() {
        return _expiredDate;
    }

    /**
     * The unique identifier of the lock
     * @return idLock
     */
    public String getIdLock() {
        return _idLock;
    }
}
