/*
 * Copyright (c) 2002-2017, Mairie de Paris
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
package fr.paris.lutece.plugins.forms.util;

import java.util.List;

import fr.paris.lutece.plugins.forms.service.FormsPlugin;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.plugins.genericattributes.business.EntryTypeHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.util.ReferenceList;

/**
 * Utility class for plugin Form
 */
public final class FormsEntryUtils
{

    /**
     * FormsEntryUtils private constructor. DO not call
     *
     */
    private FormsEntryUtils( )
    {
        throw new AssertionError( );
    }

    /**
     * Return an instance of IEntry function of type entry
     * 
     * @param nIdType
     *            the entry type id
     * @return an instance of IEntry function of type entry
     */
    public static Entry createEntryByType( int nIdType )
    {
        Entry entry = null;
        EntryType entryType;

        if ( nIdType == -1 )
        {
            return null;
        }

        entryType = EntryTypeHome.findByPrimaryKey( nIdType );

        entry = new Entry( );
        entry.setEntryType( entryType );

        return entry;
    }

    /**
     * Return the index in the list of the field whose key is specified in parameter
     * 
     * @param nIdField
     *            the key of the field
     * @param listField
     *            the list of field
     * @return the index in the list of the field whose key is specified in parameter
     */
    public static int getIndexFieldInTheFieldList( int nIdField, List<Field> listField )
    {
        int nIndex = 0;

        for ( Field field : listField )
        {
            if ( field.getIdField( ) == nIdField )
            {
                return nIndex;
            }

            nIndex++;
        }

        return nIndex;
    }

    /**
     * Return true if the field which key is specified in parameter is in the response list
     * 
     * @param nIdField
     *            the id of the field who is search
     * @param listResponse
     *            the list of object Response
     * @return true if the field which key is specified in parameter is in the response list
     */
    public static Boolean isFieldInTheResponseList( int nIdField, List<Response> listResponse )
    {
        for ( Response response : listResponse )
        {
            if ( ( response.getField( ) != null ) && ( response.getField( ).getIdField( ) == nIdField ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Build a reference list whith the different entry types excluding Group and MyLuteceUser
     * 
     * @return reference list of entry type
     */
    public static ReferenceList initRefListEntryType( )
    {
        ReferenceList refListEntryType = new ReferenceList( );
        List<EntryType> listEntryType = EntryTypeHome.getList( FormsPlugin.PLUGIN_NAME );

        for ( EntryType entryType : listEntryType )
        {
            if ( !entryType.getGroup( ) && !entryType.getMyLuteceUser( ) )
            {
                refListEntryType.addItem( entryType.getIdType( ), entryType.getTitle( ) );
            }
        }

        return refListEntryType;
    }
}
