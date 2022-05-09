/*
 * Copyright (c) 2002-2022, City of Paris
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

import fr.paris.lutece.portal.service.resource.IExtendableResource;

/**
 * This is the business class for the object FormResponse
 */
public class FormResponse implements IExtendableResource
{
    /**
     * Form response resource type
     */
    public static final String RESOURCE_TYPE = "FORMS_FORM_RESPONSE";

    private int _nId;

    private int _nFormId;

    private String _strGuid;

    private Timestamp _dateCreation;

    private Timestamp _dateUpdate;

    private boolean _bFromSave;

    private boolean _bPublished;

    private Timestamp _dateUpdateStatus;

    private List<FormResponseStep> _listFormResponseStep;

    /**
     * @return the _nId
     */
    public int getId( )
    {
        return _nId;
    }

    /**
     * @param nId
     *            the nId to set
     */
    public void setId( int nId )
    {
        this._nId = nId;
    }

    /**
     * @return the _nFormId
     */
    public int getFormId( )
    {
        return _nFormId;
    }

    /**
     * @param nFormId
     *            the nFormId to set
     */
    public void setFormId( int nFormId )
    {
        this._nFormId = nFormId;
    }

    /**
     * @return the _strGuid
     */
    public String getGuid( )
    {
        return _strGuid;
    }

    /**
     * @param strGuid
     *            the strGuid to set
     */
    public void setGuid( String strGuid )
    {
        this._strGuid = strGuid;
    }

    /**
     * @return the _dateCreation
     */
    public Timestamp getCreation( )
    {
        return _dateCreation;
    }

    /**
     * @param dateCreation
     *            the dateCreation to set
     */
    public void setDateCreation( Timestamp dateCreation )
    {
        this._dateCreation = dateCreation;
    }

    /**
     * @return the _dateUpdate
     */
    public Timestamp getUpdate( )
    {
        return _dateUpdate;
    }

    /**
     * @param dateUpdate
     *            the dateUpdate to set
     */
    public void setUpdate( Timestamp dateUpdate )
    {
        this._dateUpdate = dateUpdate;
    }

    /**
     * @return the _bFromSave
     */
    public boolean isFromSave( )
    {
        return _bFromSave;
    }

    /**
     * @param bFromSave
     *            the bFromSave to set
     */
    public void setFromSave( boolean bFromSave )
    {
        this._bFromSave = bFromSave;
    }

    /**
     * @return the _bPublished
     */
    public boolean isPublished( )
    {
        return _bPublished;
    }

    /**
     * @param bPublished
     *            the bPublished to set
     */
    public void setPublished( boolean bPublished )
    {
        this._bPublished = bPublished;
    }

    /**
     * @return the _dateUpdate
     */
    public Timestamp getUpdateStatus( )
    {
        return _dateUpdateStatus;
    }

    /**
     * @param dateUpdate
     *            the dateUpdate to set
     */
    public void setUpdateStatus( Timestamp dateUpdateStatus )
    {
        this._dateUpdateStatus = dateUpdateStatus;
    }

    /**
     * Gives the steps containing responses
     * 
     * @return the steps
     */
    public List<FormResponseStep> getSteps( )
    {
        return _listFormResponseStep;
    }

    /**
     * Sets the steps containing responses
     * 
     * @param listFormResponseStep
     *            the list of steps
     */
    public void setSteps( List<FormResponseStep> listFormResponseStep )
    {
        _listFormResponseStep = listFormResponseStep;
    }

    @Override
    public String getExtendableResourceDescription( )
    {
        return RESOURCE_TYPE + "_" + _nFormId;
    }

    @Override
    public String getExtendableResourceImageUrl( )
    {
        return null;
    }

    @Override
    public String getExtendableResourceName( )
    {
        return RESOURCE_TYPE + "_" + _nId;
    }

    @Override
    public String getExtendableResourceType( )
    {
        return RESOURCE_TYPE + "_" + _nFormId;
    }

    @Override
    public String getIdExtendableResource( )
    {
        return Integer.toString( _nId );
    }

}
