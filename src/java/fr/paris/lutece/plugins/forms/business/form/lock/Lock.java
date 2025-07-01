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

import java.io.Serializable;
import java.sql.Timestamp;

public class Lock implements Serializable  {


    // Variables declarations
    private String _indexName;
    private String _instanceName;
    private boolean _isLocked;
    private Timestamp _dateBegin;
    private Timestamp _expiredDate;
    private String _uuid;

    /**
     * Returns the indexName
     *
     * @return The indexName
     */
    public String getIndexName() {
        return _indexName;
    }

    /**
     * Sets the indexName
     *
     * @param indexName
     *            The indexName
     */
    public void setIndexName(String indexName) {
        this._indexName = indexName;
    }

    /**
     * Returns the instanceName
     *
     * @return The instanceName
     */
    public String getInstanceName() {
        return _instanceName;
    }

    /**
     * Sets the instanceName
     *
     * @param instanceName
     *            The instanceName
     */
    public void setInstanceName(String instanceName) {
        this._instanceName = instanceName;
    }

    /**
     * Returns the isLocked
     *
     * @return true if the lock is taken
     */
    public boolean isLocked() {
        return _isLocked;
    }

    /**
     * Sets the isLocked
     *
     * @param isLocked
     *            The isLocked
     */
    public void setIsLocked(boolean isLocked) {
        this._isLocked = isLocked;
    }

    /**
     * Returns the dateBegin
     *
     * @return The dateBegin
     */
    public Timestamp getDateBegin() {
        return _dateBegin;
    }

    /**
     * Sets the dateBegin
     *
     * @param dateBegin
     *            The dateBegin
     */
    public void setDateBegin(Timestamp dateBegin) {
        this._dateBegin = dateBegin;
    }

    /**
     * Returns the expiredDate
     *
     * @return The expiredDate
     */
    public Timestamp getExpiredDate() {
        return _expiredDate;
    }

    /**
     * Sets the expiredDate
     *
     * @param expiredDate
     *            The expiredDate
     */
    public void setExpiredDate(Timestamp expiredDate) {
        this._expiredDate = expiredDate;
    }

    /**
     * Returns the uuid
     *
     * @return The uuid
     */
    public String getUuid() {
        return _uuid;
    }

    /**
     * Sets the uuid
     *
     * @param uuid
     *            The uuid
     */
    public void setUuid(String uuid) {
        this._uuid = uuid;
    }

}
