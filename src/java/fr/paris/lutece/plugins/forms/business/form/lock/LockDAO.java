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
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.sql.DAOUtil;
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.SQLException;
import java.sql.Statement;

@ApplicationScoped
public class LockDAO implements ILockDAO {

    // Constants

    private static final String SQL_QUERY_ACQUIRE = "UPDATE forms_lucene_lock set instance_name=?, is_locked=?, date_begin=?, expired_date=?, uuid=? " +
            " where index_name=? and (is_locked=false OR expired_date<?) ";

    private static final String SQL_QUERY_RELEASE = "UPDATE forms_lucene_lock SET is_locked=false, date_begin=NULL, expired_date=NULL WHERE uuid=? ";

    private static final String SQL_QUERY_REFRESH = "UPDATE forms_lucene_lock SET expired_date=NULL WHERE uuid=? ";

    private static final String SQL_QUERY_CLOSE_ALL = "UPDATE forms_lucene_lock SET is_locked=false";


    @Override
    public boolean acquire(Lock lock, Plugin plugin )
    {

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_ACQUIRE, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++, lock.getInstanceName() );
            daoUtil.setBoolean( nIndex++, lock.isLocked() );
            daoUtil.setTimestamp( nIndex++, lock.getDateBegin() );
            daoUtil.setTimestamp( nIndex++, lock.getExpiredDate() );
            daoUtil.setString( nIndex++, lock.getUuid() );

            daoUtil.setString( nIndex++, lock.getIndexName() );
            daoUtil.setTimestamp( nIndex++, lock.getDateBegin() );

            try {
                daoUtil.executeUpdate();
                return daoUtil.getGeneratedKeysResultSet().getStatement().getUpdateCount() == 1;
            }
            catch (AppException | SQLException e)
            {
                AppLogService.error( e.getMessage() );
                return false;
            }
        }
    }

    @Override
    public void release(Lock lock, Plugin plugin )
    {

        try (DAOUtil daoUtil = new DAOUtil(SQL_QUERY_RELEASE, plugin)) {
            daoUtil.setString(1, lock.getUuid() );
            daoUtil.executeUpdate();
        }

    }

    @Override
    public boolean refresh(Lock lock, Plugin plugin )
    {
        try (DAOUtil daoUtil = new DAOUtil(SQL_QUERY_REFRESH, plugin))
        {
            int nIndex = 1;
            daoUtil.setTimestamp(nIndex++, lock.getExpiredDate() );

            daoUtil.setString(nIndex++, lock.getUuid() );

            try {
                daoUtil.executeUpdate();
                return daoUtil.getGeneratedKeysResultSet().getStatement().getUpdateCount() == 1;
            }
            catch (AppException | SQLException e)
            {
                AppLogService.error( e.getMessage() );
                return false;
            }
        }
    }

    @Override
    public void closeAll(Plugin plugin ) {

        try (DAOUtil daoUtil = new DAOUtil(SQL_QUERY_CLOSE_ALL, plugin)) {
            daoUtil.executeUpdate();
        }

    }

}
