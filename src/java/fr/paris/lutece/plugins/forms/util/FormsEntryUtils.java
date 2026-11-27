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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.service.FormsPlugin;
import fr.paris.lutece.plugins.genericattributes.business.*;
import fr.paris.lutece.plugins.genericattributes.service.file.GenericAttributeFileService;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFileHome;
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

    /**
     * Stores a copy of the file a downloadable file field designates, for a copied entry to hold its own.
     *
     * A copied field keeps the key of its source : removing the file from the copy, or the copy itself, deleted the file of the source. The file
     * found under that key is only taken for the one exported when it bears the same name : an entry imported from another instance carries a key
     * designating another file, or none, here.
     *
     * @param field
     *            the downloadable file field of the source
     * @param bCheckName
     *            whether the file has to bear the name the field gives it, for a field that may come from another instance
     * @return the key of the stored copy, null when no such file is found here
     */
    public static String copyDownloadableFile( Field field, boolean bCheckName )
    {
        File file = loadDownloadableFile( field.getValue( ) );

        if ( file == null || file.getPhysicalFile( ) == null || ( bCheckName && !StringUtils.equals( file.getTitle( ), field.getTitle( ) ) ) )
        {
            return null;
        }

        return GenericAttributeFileService.getInstance( ).save( file );
    }

    /**
     * Loads, with its content, the file a downloadable file field designates.
     *
     * The default file store reads the key as a number and throws on any other : a key exported from an instance whose store uses another kind of
     * key designates nothing here, and must not fail the whole import. A file stored before files recorded the provider that stored them is not
     * handed out by that provider, although it is a file of the database store : it is read from that store directly.
     *
     * @param strKey
     *            the key the field holds
     * @return the file, null when none is found
     */
    private static File loadDownloadableFile( String strKey )
    {
        if ( !NumberUtils.isDigits( strKey ) )
        {
            return null;
        }

        File file = GenericAttributeFileService.getInstance( ).load( strKey, null );

        if ( file != null )
        {
            return file;
        }

        File fileWithoutOrigin = FileHome.findByPrimaryKey( Integer.parseInt( strKey ) );

        if ( fileWithoutOrigin == null || fileWithoutOrigin.getOrigin( ) != null || fileWithoutOrigin.getPhysicalFile( ) == null )
        {
            return null;
        }

        fileWithoutOrigin.setPhysicalFile( PhysicalFileHome.findByPrimaryKey( fileWithoutOrigin.getPhysicalFile( ).getIdPhysicalFile( ) ) );

        return fileWithoutOrigin;
    }
}
