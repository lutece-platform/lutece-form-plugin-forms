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
package fr.paris.lutece.plugins.forms.business;

import java.sql.Timestamp;
import java.util.List;

public class FormResponseFilter
{

    private List<Integer> _listIdForm;
    private Boolean _bFromSave;
    private Timestamp _dAfterDateUpdate;

    /**
     * Constructor
     */
    public FormResponseFilter( )
    {
        _listIdForm = null;
        _bFromSave = null;
        _dAfterDateUpdate = null;
    }

    /**
     * Get the list of idForm
     *
     * @return the listIdForm
     */
    public List<Integer> getListIdForm()
    {
        return _listIdForm;
    }

    /**
     * contains filter for IdForm
     *
     * @return boolean
     */
    public boolean containsListIdForm()
    {
        return _listIdForm != null;
    }

    /**
     * Set the listIdForm
     *
     * @param listIdForm the listIdForm
     */
    public void setIdForm(List<Integer> listIdForm)
    {
        this._listIdForm = listIdForm;
    }

    /**
     * is fromsave
     *
     * @return the _bFromSave
     */
    public boolean isFromSave()
    {
        return _bFromSave;
    }

    /**
     * contains filter for fromsave
     *
     * @return boolean
     */
    public boolean containsFromSave()
    {
        return _bFromSave != null;
    }

    /**
     * Set the fromSave
     *
     * @param fromSave the fromSave
     */
    public void setFromSave(Boolean fromSave)
    {
        this._bFromSave = fromSave;
    }

    /**
     * Get the filter date after update
     *
     * @return the _dAfterDateUpdate
     */
    public Timestamp getAfterDateUpdate()
    {
        return _dAfterDateUpdate;
    }

    /**
     * contains filter for afterDateUpdate
     *
     * @return boolean
     */
    public boolean containsAfterDateUpdate()
    {
        return _dAfterDateUpdate != null;
    }

    /**
     * Set the afterDateUpdate
     *
     * @param afterDateUpdate the afterDateUpdate
     */
    public void setAfterDateUpdate(Timestamp afterDateUpdate)
    {
        this._dAfterDateUpdate = afterDateUpdate;
    }

}
