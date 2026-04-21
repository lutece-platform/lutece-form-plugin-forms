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
import fr.paris.lutece.util.sql.DAOUtil;
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.Statement;

/**
 * JDBC-escape {@code {fn TIMESTAMPADD(SQL_TSI_SECOND, ?, CURRENT_TIMESTAMP)}} is portable
 * across HSQLDB, MySQL, PostgreSQL and Oracle; all timestamp comparisons use the DB clock
 * so JVM clock skew between cluster members cannot steal a still-valid lock or extend a
 * stale one beyond its real TTL.
 */
@ApplicationScoped
public class LockDAO implements ILockDAO
{
    private static final String SQL_QUERY_ACQUIRE =
            "UPDATE forms_lucene_lock " +
            "SET instance_name=?, is_locked=true, date_begin=CURRENT_TIMESTAMP, " +
            "    expired_date={fn TIMESTAMPADD(SQL_TSI_SECOND, ?, CURRENT_TIMESTAMP)}, uuid=? " +
            "WHERE index_name=? AND (is_locked=false OR expired_date < CURRENT_TIMESTAMP)";

    private static final String SQL_QUERY_RELEASE =
            "UPDATE forms_lucene_lock SET is_locked=false, date_begin=NULL, expired_date=NULL WHERE uuid=?";

    /**
     * Refresh ONLY if this row is still ours AND not yet expired from the DB's view.
     * Returning no row update means the lock was reclaimed by someone else — the caller
     * must abort to avoid a double-writer situation on the shared index.
     */
    private static final String SQL_QUERY_REFRESH =
            "UPDATE forms_lucene_lock " +
            "SET expired_date={fn TIMESTAMPADD(SQL_TSI_SECOND, ?, CURRENT_TIMESTAMP)} " +
            "WHERE uuid=? AND is_locked=true AND expired_date > CURRENT_TIMESTAMP";

    private static final String SQL_QUERY_RELEASE_BY_INSTANCE =
            "UPDATE forms_lucene_lock SET is_locked=false, date_begin=NULL, expired_date=NULL " +
            "WHERE instance_name=? AND is_locked=true";

    @Override
    public boolean acquire( String indexName, String instanceName, String uuid, long ttlSeconds, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_ACQUIRE, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++, instanceName );
            daoUtil.setLong( nIndex++, ttlSeconds );
            daoUtil.setString( nIndex++, uuid );
            daoUtil.setString( nIndex++, indexName );

            daoUtil.executeUpdate( );
            return daoUtil.getReturnedRowCount( ) == 1;
        }
    }

    @Override
    public void release( String uuid, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_RELEASE, plugin ) )
        {
            daoUtil.setString( 1, uuid );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public boolean refresh( String uuid, long ttlSeconds, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_REFRESH, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setLong( nIndex++, ttlSeconds );
            daoUtil.setString( nIndex++, uuid );

            daoUtil.executeUpdate( );
            return daoUtil.getReturnedRowCount( ) == 1;
        }
    }

    @Override
    public int releaseByInstance( String instanceName, Plugin plugin )
    {
        if ( instanceName == null || instanceName.isBlank( ) )
        {
            return 0;
        }
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_RELEASE_BY_INSTANCE, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            daoUtil.setString( 1, instanceName );
            daoUtil.executeUpdate( );
            return daoUtil.getReturnedRowCount( );
        }
    }
}
