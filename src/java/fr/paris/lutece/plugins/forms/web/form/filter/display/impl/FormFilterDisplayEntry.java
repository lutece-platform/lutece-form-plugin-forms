/*
 * Copyright (c) 2002-2018, Mairie de Paris
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
package fr.paris.lutece.plugins.forms.web.form.filter.display.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.column.impl.FormColumnEntry;
import fr.paris.lutece.plugins.forms.business.form.filter.IFormFilter;
import fr.paris.lutece.plugins.forms.business.form.filter.configuration.FormFilterConfiguration;
import fr.paris.lutece.plugins.forms.business.form.filter.configuration.FormFilterEntryConfiguration;
import fr.paris.lutece.plugins.forms.util.FormEntryNameConstants;
import fr.paris.lutece.plugins.forms.util.ReferenceListFactory;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseFilter;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.util.ReferenceList;

/**
 * Implementation of the IFormFilterDisplay interface for the filter on the Entry column
 */
public class FormFilterDisplayEntry extends AbstractFormFilterDisplay
{
    // Constants
    private static final String DEFAULT_ENTRY_VALUE = StringUtils.EMPTY;
    private static final String PARAMETER_ENTRY_VALUE_PATTERN = "multiview_entry_value_%s";
    private static final String ENTRY_RESPONSE_VALUE_ATTRIBUTE = "responseValue";
    private static final String FORM_RESOURCE_TYPE = "FORMS_FORM";

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getFilterDisplayMapValues( HttpServletRequest request )
    {
        String strEntryValue = DEFAULT_ENTRY_VALUE;
        Map<String, Object> mapFilterNameValues = new LinkedHashMap<>( );

        String strParameterName = buildElementName( PARAMETER_ENTRY_VALUE_PATTERN );
        String strEntryParameterValue = request.getParameter( strParameterName );
        if ( StringUtils.isNotBlank( strEntryParameterValue ) )
        {
            int nFormColumnPosition = NumberUtils.INTEGER_MINUS_ONE;
            IFormColumn formColumn = retrieveFormColumn( );
            if ( formColumn != null )
            {
                nFormColumnPosition = formColumn.getFormColumnPosition( );
            }

            String strEntryValueColumnName = FormEntryNameConstants.FILTER_ENTRY_BASE_NAME_PATTERN + nFormColumnPosition;
            mapFilterNameValues.put( strEntryValueColumnName, strEntryParameterValue );
            strEntryValue = strEntryParameterValue;
        }

        setValue( strEntryValue );

        return mapFilterNameValues;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void buildTemplate( HttpServletRequest request )
    {
        String strParameterName = buildElementName( PARAMETER_ENTRY_VALUE_PATTERN );
        manageFilterTemplate( request, createReferenceList( ), strParameterName );
    }

    /**
     * Create the ReferenceList based on the value of the Entry for an Entry column
     * 
     * @return the ReferenceList with all values of the Entry for an Entry column
     */
    private ReferenceList createReferenceList( )
    {
        List<Entry> listIEntryToRetrieveValueFrom = new ArrayList<>( );

        IFormColumn formColumn = retrieveFormColumn( );
        if ( formColumn instanceof FormColumnEntry )
        {
            FormColumnEntry formColumnEntry = (FormColumnEntry) formColumn;
            List<String> listEntryTitle = formColumnEntry.getListEntryTitle( );
            listIEntryToRetrieveValueFrom = getEntryListFromTitle( listEntryTitle );
        }

        // Build the list of FormFilter to use for the filter from the list of entry to search on
        List<Response> listResponse = getEntryResponseList( listIEntryToRetrieveValueFrom );
        cleanListResponse( listResponse );
        ReferenceListFactory referenceListFactory = new ReferenceListFactory( listResponse, ENTRY_RESPONSE_VALUE_ATTRIBUTE, ENTRY_RESPONSE_VALUE_ATTRIBUTE, Boolean.FALSE );

        String strDefaultReferenceListName = getFormFilterDisplayLabel( );
        referenceListFactory.setDefaultName( strDefaultReferenceListName );
        referenceListFactory.setDefaultSortNeeded( Boolean.TRUE );

        return referenceListFactory.createReferenceList( );
    }

    /**
     * Return the list of entry built from the name of the entry which are stored in configuration of the column
     * 
     * @param listEntryTitle
     *            The list of title of entry to retrieve the value from
     * @return the list of entry built from the given list of entry title
     */
    private static List<Entry> getEntryListFromTitle( List<String> listEntryTitle )
    {
        List<Entry> listEntry = new ArrayList<>( );

        if ( !CollectionUtils.isEmpty( listEntryTitle ) )
        {
            // Retrieve the list of forms
            List<Form> listForms = FormHome.getFormList( );

            if ( !CollectionUtils.isEmpty( listForms ) )
            {
                for ( Form form : listForms )
                {
                    listEntry.addAll( fillEntryListFromTitle( form.getId( ), listEntryTitle ) );
                }
            }
        }

        return listEntry;
    }

    /**
     * Return the list of all the Entry of the specified form which have the same title than the value in the given list.
     * 
     * @param nIdForm
     *            The identifier of the form to retrieve the Entry from
     * @param listEntryTitle
     *            The list of title of entry to retrieve the value from
     * @return the list of entry retrieve from the form from the given list of entry title
     */
    private static List<Entry> fillEntryListFromTitle( int nIdForm, List<String> listEntryTitle )
    {
        List<Entry> listEntry = new ArrayList<>( );

        EntryFilter entryFilter = new EntryFilter( );
        entryFilter.setIdResource( nIdForm );
        entryFilter.setResourceType( FORM_RESOURCE_TYPE );
        entryFilter.setIdIsGroup( EntryFilter.FILTER_FALSE );
        entryFilter.setIdIsComment( EntryFilter.FILTER_FALSE );

        List<Entry> entryList = EntryHome.getEntryList( entryFilter );

        for ( String strEntryTitleToFind : listEntryTitle )
        {
            for ( Entry entry : entryList )
            {
                if ( strEntryTitleToFind.equals( entry.getTitle( ) ) )
                {
                    listEntry.add( entry );
                }
            }
        }

        return listEntry;
    }

    /**
     * Return the list of Response which are associated to the specified entry of the given list
     * 
     * @param listIEntryToRetrieveValueFrom
     *            The list of entry to retrieve the response value from
     * @return the list of Response which belong to an entry of the list of Entry which we want to gather values
     */
    private static List<Response> getEntryResponseList( List<Entry> listIEntryToRetrieveValueFrom )
    {
        List<Response> listResponseFieldResult = new ArrayList<>( );

        if ( !CollectionUtils.isEmpty( listIEntryToRetrieveValueFrom ) )
        {
            for ( Entry entry : listIEntryToRetrieveValueFrom )
            {
                ResponseFilter responseFilter = new ResponseFilter( );
                responseFilter.setIdEntry( entry.getIdEntry( ) );
                
                listResponseFieldResult.addAll( ResponseHome.getResponseList( responseFilter ) );
            }
        }

        return listResponseFieldResult;
    }

    /**
     * Clean the given list of Response by removing all of those which have empty value
     * 
     * @param listResponse
     *            The list to remove the Response with empty value
     */
    private static void cleanListResponse( List<Response> listResponse )
    {
        if ( !CollectionUtils.isEmpty( listResponse ) )
        {
            Iterator<Response> iteratorResponse = listResponse.iterator( );
            while ( iteratorResponse.hasNext( ) )
            {
                Response response = iteratorResponse.next( );
                if ( response != null && StringUtils.isBlank( response.getResponseValue( ) ) )
                {
                    iteratorResponse.remove( );
                }
            }
        }
    }

    /**
     * Return the name of the element build from the given pattern and the position of the filter
     * 
     * @param strPatternElementName
     *            The pattern of the name of the element
     * @return the property key name for the current Entry filter for the entry to map
     */
    private String buildElementName( String strPatternElementName )
    {
        int nPosition = NumberUtils.INTEGER_MINUS_ONE;

        IFormFilter formFilter = getFormFilter( );
        if ( formFilter != null && formFilter.getFormFilterConfiguration( ) != null )
        {
            nPosition = formFilter.getFormFilterConfiguration( ).getPosition( );
        }

        return String.format( strPatternElementName, nPosition );
    }

    /**
     * Return the IFormColumn from the FormFilterConfiguration of the FormFilter of the FormFilterDisplay
     * 
     * @return the IFormColumn from the FormFilterConfiguration of the FormFilter of the FormFilterDisplay or null if not exist
     */
    private IFormColumn retrieveFormColumn( )
    {
        IFormColumn formColumnResult = null;

        IFormFilter formFilter = getFormFilter( );
        if ( formFilter != null )
        {
            FormFilterConfiguration formFilterConfiguration = formFilter.getFormFilterConfiguration( );
            if ( formFilterConfiguration instanceof FormFilterEntryConfiguration )
            {
                FormFilterEntryConfiguration formFilterEntryConfiguration = (FormFilterEntryConfiguration) formFilterConfiguration;
                formColumnResult = formFilterEntryConfiguration.getFormColumn( );
            }
        }

        return formColumnResult;
    }
}
