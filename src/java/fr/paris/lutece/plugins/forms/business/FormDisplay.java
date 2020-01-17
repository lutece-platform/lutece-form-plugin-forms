/*
 * Copyright (c) 2002-2020, City of Paris
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the business class for the object FormDisplay
 */
public class FormDisplay implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations
    private int _nId;

    private int _nFormId;

    private int _nStepId;

    private int _nCompositeId;

    private int _nParentId;

    private int _nDisplayOrder;

    private String _strCompositeType;

    private int _nDepth;

    private Control _displayControl;

    private List<Control> _validationControlList = new ArrayList<>( );

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
     * @return the _nStepId
     */
    public int getStepId( )
    {
        return _nStepId;
    }

    /**
     * @param nStepId
     *            the nStepId to set
     */
    public void setStepId( int nStepId )
    {
        this._nStepId = nStepId;
    }

    /**
     * @return the _nCompositeId
     */
    public int getCompositeId( )
    {
        return _nCompositeId;
    }

    /**
     * @param nCompositeId
     *            the nCompositeId to set
     */
    public void setCompositeId( int nCompositeId )
    {
        this._nCompositeId = nCompositeId;
    }

    /**
     * @return the _nParentId
     */
    public int getParentId( )
    {
        return _nParentId;
    }

    /**
     * @param nParentId
     *            the nParentId to set
     */
    public void setParentId( int nParentId )
    {
        this._nParentId = nParentId;
    }

    /**
     * @return the _nDisplayOrder
     */
    public int getDisplayOrder( )
    {
        return _nDisplayOrder;
    }

    /**
     * @param nDisplayOrder
     *            the nDisplayOrder to set
     */
    public void setDisplayOrder( int nDisplayOrder )
    {
        this._nDisplayOrder = nDisplayOrder;
    }

    /**
     * @return the _strCompositeType
     */
    public String getCompositeType( )
    {
        return _strCompositeType;
    }

    /**
     * @param strCompositeType
     *            the strCompositeType to set
     */
    public void setCompositeType( String strCompositeType )
    {
        this._strCompositeType = strCompositeType;
    }

    /**
     * @return the form display depth
     */
    public int getDepth( )
    {
        return _nDepth;
    }

    /**
     * set the form display depth
     * 
     * @param nDepth
     *            the depth to set
     */
    public void setDepth( int nDepth )
    {
        _nDepth = nDepth;
    }

    /**
     * 
     * @return the conditional display control
     */
    public Control getDisplayControl( )
    {
        return _displayControl;
    }

    /**
     * 
     * @param displayControl
     *            the control to set
     */
    public void setDisplayControl( Control displayControl )
    {
        this._displayControl = displayControl;
    }

    /**
     * @return the _validationControlList
     */
    public List<Control> getValidationControlList( )
    {
        return _validationControlList;
    }

    /**
     * @param _validationControlList
     *            the _validationControlList to set
     */
    public void setValidationControlList( List<Control> validationControlList )
    {
        _validationControlList = validationControlList;
    }

}
