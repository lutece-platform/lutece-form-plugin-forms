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
package fr.paris.lutece.plugins.forms.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        if ( nIdType == -1 )
        {
            return null;
        }

        Entry entry = new Entry( );
        EntryType entryType = EntryTypeHome.findByPrimaryKey( nIdType );
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
     * Build a reference list with the different entry types excluding Group and MyLuteceUser
     *
     * @return reference list of entry type
     */
    public static ReferenceList initRefListEntryType( )
    {
        ReferenceList refListEntryType = new ReferenceList( );
        List<EntryType> listEntryType = EntryTypeHome.getList( FormsPlugin.PLUGIN_NAME );

        for ( EntryType entryType : listEntryType )
        {
            if ( Boolean.FALSE.equals( entryType.getGroup( ) ) && Boolean.FALSE.equals( entryType.getMyLuteceUser( ) ) )
            {
                refListEntryType.addItem( entryType.getIdType( ), entryType.getTitle( ) );
            }
        }

        return refListEntryType;
    }

    /**
     * Build a list with the different entry types excluding Group and MyLuteceUser
     *
     * @return list of entry type
     */
    public static List<EntryType> initListEntryType( )
    {
        List<EntryType> listEntryType = new ArrayList<>( );
        List<EntryType> listRefEntryType = EntryTypeHome.getList( FormsPlugin.PLUGIN_NAME );

        for ( EntryType entryType : listRefEntryType )
        {
            if ( !entryType.isInactive( ) )
            {
                listEntryType.add( entryType );
            }
        }

        return listEntryType;
    }

    /**
     * Finds a field with the specified code in the specified entry
     *
     * @param entry
     *            the entry
     * @param strCode
     *            the code
     * @return the found field, or {@code null} otherwise
     */
    public static Field findFieldByCode( Entry entry, String strCode )
    {
        Field fieldFound = null;
        List<Field> listField = entry.getFields( );

        if ( listField != null )
        {
            for ( Field field : listField )
            {
                if ( strCode.equals( field.getCode( ) ) )
                {
                    fieldFound = field;
                    break;
                }
            }
        }

        return fieldFound;
    }

    /**
     * Check if there is a difference between the current value and the new value in a form.
     * @param listResponseReference List of current responses.
     * @param listResponseNew List of new responses.
     * @return true if the value is changed, false otherwise.
     */
    public static boolean isFirstResponseValueChanged( List<Response> listResponseReference, List<Response> listResponseNew )
    {
        String strResponseReference = getFirstResponseValue( listResponseReference );
        String strResponseNew = getFirstResponseValue( listResponseNew );

        return !Objects.equals( strResponseReference, strResponseNew );
    }

    /**
     * Get the first response value of the list given in parameter.
     * Return null if the list is null there is no value in it.
     * @param listResponses List of responses
     * @return the first response value of the list given in parameter
     */
    public static String getFirstResponseValue( List<Response> listResponses )
    {
        if ( listResponses == null || listResponses.isEmpty( ) || listResponses.get( 0 ) == null )
        {
            return null;
        }

        return listResponses.get( 0 ).getResponseValue( );
    }
}
