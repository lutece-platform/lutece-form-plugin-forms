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
import fr.paris.lutece.plugins.forms.business.form.lock.Lock;
import fr.paris.lutece.plugins.forms.exception.LockException;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.l10n.LocaleService;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

public final class LuceneLockManagerDB implements LuceneLockManager {

    private final ILockDAO _lockDao;
    private static final Plugin _plugin = PluginService.getPlugin( "forms" );

    private static final String PROPERTY_SITE_NAME = "lutece.name";


    /**
     * Private constructor - this class need not be instantiated
     */
    private LuceneLockManagerDB( ILockDAO lockDao )
    {
        _lockDao = lockDao;
    }



    @Override
    public LockResult acquireLock(String indexName, long timeoutMs) throws LockException {

        Calendar calendar = new GregorianCalendar( LocaleService.getDefault( ) );
        Timestamp dateBegin = new Timestamp( calendar.getTimeInMillis( ) );

        calendar.setTimeInMillis(dateBegin.getTime() + timeoutMs);
        Timestamp expiredDate = new Timestamp( calendar.getTimeInMillis( ) );

        Lock lock = new Lock();
        lock.setIndexName(indexName);
        lock.setInstanceName(AppPropertiesService.getProperty( PROPERTY_SITE_NAME ));
        lock.setIsLocked(true);
        lock.setDateBegin(dateBegin);
        lock.setExpiredDate(expiredDate);
        lock.setUuid(UUID.randomUUID().toString());

        if(_lockDao.acquire(lock,_plugin))
        {
            return LockResult.createLockSuccess(lock.getIndexName(), lock.getUuid(), lock.getExpiredDate());
        }
        else
        {
            throw new LockException( );
        }
    }

    @Override
    public void releaseLock(LockResult lockResult) throws LockException {

        Lock lock = new Lock();
        lock.setUuid(lockResult.getIdLock());

        _lockDao.release(lock, _plugin);
    }

    @Override
    public LockResult refreshLock(LockResult lockResult, long timeoutMs) throws LockException {

        Calendar calendar = new GregorianCalendar( LocaleService.getDefault( ) );
        Timestamp currentDate = new Timestamp( calendar.getTimeInMillis( ) );

        calendar.setTimeInMillis(currentDate.getTime() + timeoutMs);
        Timestamp expiredDate = new Timestamp( calendar.getTimeInMillis( ) );

        Lock lock = new Lock();
        lock.setExpiredDate(expiredDate);
        lock.setUuid(lockResult.getIdLock());

        if(_lockDao.refresh(lock,_plugin))
        {
            return LockResult.refreshLockSuccess(lockResult, expiredDate);
        }
        else
        {
            throw new LockException( );
        }
    }

    @Override
    public void close() {
        _lockDao.closeAll( _plugin );
    }

}
