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
package fr.paris.lutece.plugins.forms.service.provider;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.Group;
import fr.paris.lutece.plugins.forms.business.GroupHome;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.FileServiceException;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.template.HtmlMarkup;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import jakarta.enterprise.inject.spi.CDI;

/**
 * The bookmark of an iterable group: <code>group_&lt;id&gt;</code>. Its value is the HTML of every iteration the user filled, whatever their number,
 * rendered by the template {@value #TEMPLATE_GROUP_ITERATIONS} that a site can override. The iterations the user left empty are not displayed, so the
 * remaining ones are numbered 1, 2, … in their order.
 * <p>
 * A group is iterable as in the form: its iteration max is not 1 and it holds no sub-group (a group holding a sub-group is never repeated). A sub-group
 * can be iterable, at any depth, its parent groups then being displayed once.
 */
public final class GroupIterationsMarker
{
    /** Prefix of the bookmark of an iterable group, followed by the group id */
    public static final String MARK_GROUP_ITERATIONS = "group_";

    static final String TEMPLATE_GROUP_ITERATIONS = "admin/plugins/forms/markers/group_iterations.html";
    private static final String MARK_GROUP = "group";
    private static final String MARK_ITERATIONS = "iterations";
    private static final String VALUE_SEPARATOR = ", ";
    private static final String RICH_TEXT_SEPARATOR = "<br>";

    private GroupIterationsMarker( )
    {
    }

    /**
     * Tells whether a group can be repeated, as in the form, and so gets a group bookmark
     *
     * @param group
     *            the group
     * @param listChildDisplay
     *            the displays of the questions and groups of the group
     * @return true if the iteration max of the group is not 1 and the group holds no sub-group
     */
    public static boolean isIterable( Group group, List<FormDisplay> listChildDisplay )
    {
        return group != null && group.getIterationMax( ) != 1
                && listChildDisplay.stream( ).noneMatch( display -> FormsConstants.MARK_GROUP.equals( display.getCompositeType( ) ) );
    }

    /**
     * Gets the value of the bookmark of each iterable group of a form, sub-groups included, for a response
     *
     * @param nIdForm
     *            the form id
     * @param listResponse
     *            the responses of the form response
     * @param locale
     *            the locale
     * @return the HTML of each group (empty when the user filled no iteration), by bookmark name
     */
    public static Map<String, String> getMarkerValues( int nIdForm, List<FormQuestionResponse> listResponse, Locale locale )
    {
        Map<String, String> mapValues = new LinkedHashMap<>( );
        List<FormDisplay> listFormDisplay = FormDisplayHome.getFormDisplayByForm( nIdForm );

        for ( FormDisplay groupDisplay : getGroupDisplays( listFormDisplay ) )
        {
            List<FormDisplay> listChildDisplay = getChildDisplays( listFormDisplay, groupDisplay.getId( ) );
            Group group = GroupHome.findByPrimaryKey( groupDisplay.getCompositeId( ) );
            if ( isIterable( group, listChildDisplay ) )
            {
                List<Integer> listQuestionId = getQuestionIds( listChildDisplay );
                List<GroupIteration> listIteration = getIterations( listQuestionId, listResponse, locale );
                mapValues.put( MARK_GROUP_ITERATIONS + group.getId( ), render( group, listIteration, locale ) );
            }
        }
        return mapValues;
    }

    /**
     * Gets the displays of the groups of a form, sub-groups included
     *
     * @param listFormDisplay
     *            the displays of the form
     * @return the displays of the groups
     */
    static List<FormDisplay> getGroupDisplays( List<FormDisplay> listFormDisplay )
    {
        return listFormDisplay.stream( ).filter( display -> FormsConstants.MARK_GROUP.equals( display.getCompositeType( ) ) ).toList( );
    }

    /**
     * Gets the displays of the questions and groups of a group
     *
     * @param listFormDisplay
     *            the displays of the form
     * @param nIdGroupDisplay
     *            the id of the display of the group
     * @return the displays of the children of the group
     */
    static List<FormDisplay> getChildDisplays( List<FormDisplay> listFormDisplay, int nIdGroupDisplay )
    {
        return listFormDisplay.stream( ).filter( display -> display.getParentId( ) == nIdGroupDisplay ).toList( );
    }

    /**
     * Same as {@link #getMarkerValues(int, List, Locale)}, for a FreeMarker model: the values are marked as HTML, so that the auto-escaping of the
     * template keeps them as they are
     *
     * @param nIdForm
     *            the form id
     * @param listResponse
     *            the responses of the form response
     * @param locale
     *            the locale
     * @return the HTML of each group, by bookmark name
     */
    public static Map<String, Object> getMarkerModel( int nIdForm, List<FormQuestionResponse> listResponse, Locale locale )
    {
        Map<String, Object> model = new LinkedHashMap<>( );
        getMarkerValues( nIdForm, listResponse, locale ).forEach( ( strMarker, strHtml ) -> model.put( strMarker, HtmlMarkup.of( strHtml ) ) );
        return model;
    }

    /**
     * Gets the ids of the questions of a group, in their display order
     *
     * @param listChildDisplay
     *            the displays of the children of the group
     * @return the question ids
     */
    static List<Integer> getQuestionIds( List<FormDisplay> listChildDisplay )
    {
        return listChildDisplay.stream( ).filter( display -> FormsConstants.MARK_QUESTION.equals( display.getCompositeType( ) ) )
                .sorted( Comparator.comparingInt( FormDisplay::getDisplayOrder ) ).map( FormDisplay::getCompositeId ).toList( );
    }

    /**
     * Gets the iterations the user filled for the questions of a group
     *
     * @param listQuestionId
     *            the questions of the group, in their display order
     * @param listResponse
     *            the responses of the form response
     * @param locale
     *            the locale
     * @return the iterations, in their order, without the ones the user left empty
     */
    static List<GroupIteration> getIterations( List<Integer> listQuestionId, List<FormQuestionResponse> listResponse, Locale locale )
    {
        int nIterationCount = listResponse.stream( ).filter( response -> listQuestionId.contains( response.getQuestion( ).getId( ) ) )
                .mapToInt( response -> response.getQuestion( ).getIterationNumber( ) + 1 ).max( ).orElse( 0 );

        List<GroupIteration> listIteration = new ArrayList<>( );
        for ( int nIteration = 0; nIteration < nIterationCount; nIteration++ )
        {
            GroupIteration iteration = new GroupIteration( );
            for ( Integer nIdQuestion : listQuestionId )
            {
                FormQuestionResponse response = findResponse( listResponse, nIdQuestion, nIteration );
                if ( response != null )
                {
                    iteration.addLine( getLine( response, locale ) );
                }
            }
            if ( !iteration.isEmpty( ) )
            {
                listIteration.add( iteration );
            }
        }
        return listIteration;
    }

    /**
     * Renders the iterations of a group with the template {@value #TEMPLATE_GROUP_ITERATIONS}
     *
     * @param group
     *            the group
     * @param listIteration
     *            the iterations
     * @param locale
     *            the locale
     * @return the HTML of the iterations, empty if there is none
     */
    static String render( Group group, List<GroupIteration> listIteration, Locale locale )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_GROUP, group );
        model.put( MARK_ITERATIONS, listIteration );
        return AppTemplateService.getTemplate( TEMPLATE_GROUP_ITERATIONS, locale, model ).getHtml( ).trim( );
    }

    /**
     * Finds the response to a question for an iteration
     *
     * @param listResponse
     *            the responses of the form response
     * @param nIdQuestion
     *            the question id
     * @param nIteration
     *            the iteration number
     * @return the response, or null if the user did not answer
     */
    private static FormQuestionResponse findResponse( List<FormQuestionResponse> listResponse, int nIdQuestion, int nIteration )
    {
        return listResponse.stream( )
                .filter( response -> response.getQuestion( ).getId( ) == nIdQuestion && response.getQuestion( ).getIterationNumber( ) == nIteration )
                .findFirst( ).orElse( null );
    }

    /**
     * Gets the line of a question: its title and its response, as in the recap of the form. A rich text response stays HTML, sanitized.
     *
     * @param formQuestionResponse
     *            the response to a question
     * @param locale
     *            the locale
     * @return the line
     */
    static GroupIterationLine getLine( FormQuestionResponse formQuestionResponse, Locale locale )
    {
        String strQuestion = formQuestionResponse.getQuestion( ).getTitle( );
        Entry entry = formQuestionResponse.getQuestion( ).getEntry( );
        if ( entry == null || formQuestionResponse.getEntryResponse( ) == null )
        {
            return new GroupIterationLine( strQuestion, StringUtils.EMPTY );
        }

        IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( entry );
        List<String> listValue = new ArrayList<>( );
        for ( Response response : formQuestionResponse.getEntryResponse( ) )
        {
            String strValue = getResponseValue( entryTypeService, entry, response, locale );
            if ( StringUtils.isNotEmpty( strValue ) )
            {
                listValue.add( strValue );
            }
        }

        if ( isRichText( entry ) )
        {
            String strHtml = String.join( RICH_TEXT_SEPARATOR, listValue );
            return new GroupIterationLine( strQuestion, strHtml, HtmlMarkup.ofSanitized( strHtml ) );
        }
        return new GroupIterationLine( strQuestion, String.join( VALUE_SEPARATOR, listValue ) );
    }

    /**
     * Gets a response as text. The file of a response is only known by its key: its name is read from the file store it was saved in, without
     * changing the response, which is shared with the other bookmarks.
     *
     * @param entryTypeService
     *            the entry type service of the question
     * @param entry
     *            the entry of the question
     * @param response
     *            the response
     * @param locale
     *            the locale
     * @return the response as text
     */
    private static String getResponseValue( IEntryTypeService entryTypeService, Entry entry, Response response, Locale locale )
    {
        File file = response.getFile( );
        if ( file != null && StringUtils.isBlank( file.getTitle( ) ) && StringUtils.isNotBlank( file.getFileKey( ) ) )
        {
            return getFileName( file );
        }
        return entryTypeService.getResponseValueForRecap( entry, null, response, locale );
    }

    /**
     * Gets the name of a file from the file store it was saved in
     *
     * @param file
     *            the file, with its key and its origin
     * @return the file name, empty if the file is not found
     */
    private static String getFileName( File file )
    {
        try
        {
            File fileMetaData = CDI.current( ).select( FileService.class ).get( ).getFileStoreServiceProvider( file.getOrigin( ) )
                    .getFileMetaData( file.getFileKey( ) );
            if ( fileMetaData != null && fileMetaData.getTitle( ) != null )
            {
                return fileMetaData.getTitle( );
            }
        }
        catch( FileServiceException | AppException e )
        {
            AppLogService.error( "Unable to read the file {} of the store {}", file.getFileKey( ), file.getOrigin( ), e );
        }
        return StringUtils.EMPTY;
    }

    /**
     * Tells whether the response to a question is rich text (a text area with the rich text editor)
     *
     * @param entry
     *            the entry of the question
     * @return true if the response is HTML
     */
    private static boolean isRichText( Entry entry )
    {
        Field fieldRichText = entry.getFieldByCode( IEntryTypeService.FIELD_RICHTEXT );
        return fieldRichText != null && Boolean.parseBoolean( fieldRichText.getValue( ) );
    }
}
