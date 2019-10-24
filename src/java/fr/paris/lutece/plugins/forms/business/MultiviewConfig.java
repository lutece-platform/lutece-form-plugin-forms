/*
 * Copyright (c) 2002-2019, Mairie de Paris
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

import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.portal.service.datastore.DatastoreService;

public class MultiviewConfig
{
    private static MultiviewConfig _instance;

    /**
     * Private constructor for singleton pattern
     */
    private MultiviewConfig( )
    {
        String strDisplayFormColumnTitle = DatastoreService.getDataValue( FormsConstants.DS_KEY_FORM_TITLE_COLUMN, "false" );
        _bDisplayFormsTitleColumn = Boolean.parseBoolean( strDisplayFormColumnTitle );
    }

    boolean _bDisplayFormsTitleColumn;

    /**
     * Return the singleton instance of the multiview config
     * 
     * @return the Multiview config
     */
    public static MultiviewConfig getInstance( )
    {
        if ( _instance == null )
        {
            _instance = new MultiviewConfig( );
        }
        return _instance;
    }

    /**
     * Is display forms title column
     * 
     * @return the display form title column boolean
     */
    public boolean isDisplayFormsTitleColumn( )
    {
        return _bDisplayFormsTitleColumn;
    }

    /**
     * Set the display forms title column boolean
     * 
     * @param bDisplayFormsTitleColumn
     *            The display forms title column boolean
     */
    public void setDisplayFormsTitleColumn( boolean bDisplayFormsTitleColumn )
    {
        _bDisplayFormsTitleColumn = bDisplayFormsTitleColumn;
    }

    /**
     * Save the config
     */
    public void save( )
    {
        String strDisplayFormsTitleColumn = Boolean.toString( _bDisplayFormsTitleColumn );
        DatastoreService.setDataValue( FormsConstants.DS_KEY_FORM_TITLE_COLUMN, strDisplayFormsTitleColumn );
    }
}
