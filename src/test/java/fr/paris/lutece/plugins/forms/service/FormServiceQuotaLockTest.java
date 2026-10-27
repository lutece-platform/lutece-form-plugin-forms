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
package fr.paris.lutece.plugins.forms.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.form.lock.ILockDAO;
import fr.paris.lutece.plugins.forms.exception.QuotaLockTimeoutException;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.util.sql.DAOUtil;
import jakarta.inject.Inject;

/**
 * Quota lock of a form held by another submission.
 */
public class FormServiceQuotaLockTest extends LuteceTestCase
{
    private static final int ID_FORM = 987654;
    private static final String LOCK_NAME = "forms.quota.form." + ID_FORM;

    @Inject
    private FormService _formService;
    @Inject
    private ILockDAO _lockDao;

    /**
     * Removes the lock row.
     */
    @AfterEach
    protected void removeLock( )
    {
        try ( DAOUtil daoUtil = new DAOUtil( "DELETE FROM forms_lucene_lock WHERE index_name = ?", FormsPlugin.getPlugin( ) ) )
        {
            daoUtil.setString( 1, LOCK_NAME );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * When the quota lock stays held by another submission, the response is refused after a bounded wait with a specific exception, so that the
     * user is told to try again rather than that the quota is reached.
     *
     * @throws Exception
     *             if the method cannot be called
     */
    @Test
    public void testHeldQuotaLockTimesOut( ) throws Exception
    {
        assertTrue( _lockDao.acquire( LOCK_NAME, "other-instance", UUID.randomUUID( ).toString( ), 60, FormsPlugin.getPlugin( ) ) );
        Form form = new Form( );
        form.setId( ID_FORM );

        Method acquireQuotaLock = FormService.class.getDeclaredMethod( "acquireQuotaLock", Form.class );
        acquireQuotaLock.setAccessible( true );
        long lStart = System.currentTimeMillis( );
        InvocationTargetException e = assertThrows( InvocationTargetException.class, ( ) -> acquireQuotaLock.invoke( unproxied( ), form ) );
        assertTrue( e.getCause( ) instanceof QuotaLockTimeoutException, "Unexpected exception: " + e.getCause( ) );
        assertTrue( System.currentTimeMillis( ) - lStart >= 5_000, "The submission must wait before giving up" );
    }

    /**
     * The private method is called on the contextual instance, not on the client proxy
     *
     * @return the FormService instance
     */
    private Object unproxied( )
    {
        return ( _formService instanceof org.jboss.weld.proxy.WeldClientProxy ) ? ( (org.jboss.weld.proxy.WeldClientProxy) _formService ).getMetadata( ).getContextualInstance( )
                : _formService;
    }
}
