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
package fr.paris.lutece.plugins.forms.web.form.filter.display.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.column.impl.FormColumnEntry;
import fr.paris.lutece.plugins.forms.business.form.filter.FormFilter;
import fr.paris.lutece.plugins.forms.business.form.filter.configuration.FormFilterEntryConfiguration;
import fr.paris.lutece.plugins.forms.business.form.filter.configuration.IFormFilterConfiguration;
import fr.paris.lutece.plugins.forms.util.ReferenceListFactory;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.util.ReferenceList;

/**
 * Implementation of the IFormFilterDisplay interface for the filter on the Entry column
 */
public class FormFilterDisplayEntry extends AbstractFormFilterDisplay
{
    // Constants
    private static final String DEFAULT_ENTRY_VALUE = StringUtils.EMPTY;
    private static final String PARAMETER_ENTRY_VALUE_PATTERN = "multiview_entry_value_%s";
    private static final String FORM_RESOURCE_TYPE = Form.RESOURCE_TYPE;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getParameterName( )
    {
        return buildElementName( PARAMETER_ENTRY_VALUE_PATTERN );
    }

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
            IFormColumn formColumn = retrieveFormColumn( );
            String strEntryValueColumnName = StringUtils.EMPTY;
            if ( formColumn != null )
            {
                FormColumnEntry formColumnEntry = (FormColumnEntry) formColumn;
                List<String> listEntryCode = formColumnEntry.getListEntryCode( );
                strEntryValueColumnName = listEntryCode.get( 0 );
            }

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
        manageFilterTemplate( request, createReferenceList( request.getLocale( ) ), strParameterName );
    }

    /**
     * Create the ReferenceList based on the value of the Entry for an Entry column
     * 
     * @return the ReferenceList with all values of the Entry for an Entry column
     */
    private ReferenceList createReferenceList( Locale locale )
    {
        List<Entry> listIEntryToRetrieveValueFrom = new ArrayList<>( );

        IFormColumn formColumn = retrieveFormColumn( );
        if ( formColumn instanceof FormColumnEntry )
        {
            FormColumnEntry formColumnEntry = (FormColumnEntry) formColumn;
            List<String> listEntryCode = formColumnEntry.getListEntryCode( );
            listIEntryToRetrieveValueFrom = getEntryListFromCode( listEntryCode );
        }
        ReferenceList referenceListResult = new ReferenceList( );
        // Add the default ReferenceItem if necessary
        referenceListResult.addItem( ReferenceListFactory.DEFAULT_CODE, getFormFilterDisplayLabel( locale ) );

        FieldHome.getFieldListByListIdEntry( listIEntryToRetrieveValueFrom.stream( ).map( Entry::getIdEntry ).distinct( ).collect( Collectors.toList( ) ) )
                .stream( ).filter( field -> IEntryTypeService.FIELD_ANSWER_CHOICE.equals( field.getCode( ) ) )
                .forEach( ( Field field ) -> referenceListResult.addItem( field.getValue( ), field.getTitle( ) ) );

        return referenceListResult;

    }

    /**
     * Return the list of entry built from the name of the entry which are stored in configuration of the column
     * 
     * @param listEntryCode
     *            The list of code of entry to retrieve the value from
     * @return the list of entry built from the given list of entry code
     */
    private static List<Entry> getEntryListFromCode( List<String> listEntryCode )
    {
        List<Entry> listEntry = new ArrayList<>( );

        if ( !CollectionUtils.isEmpty( listEntryCode ) )
        {
            // Retrieve the list of forms
            List<Form> listForms = FormHome.getFormList( );

            if ( !CollectionUtils.isEmpty( listForms ) )
            {
                for ( Form form : listForms )
                {
                    listEntry.addAll( fillEntryListFromCode( form.getId( ), listEntryCode ) );
                }
            }
        }

        return listEntry;
    }

    /**
     * Return the list of all the Entry of the specified form which have the same code than the value in the given list.
     * 
     * @param nIdForm
     *            The identifier of the form to retrieve the Entry from
     * @param listEntryCode
     *            The list of code of entry to retrieve the value from
     * @return the list of entry retrieve from the form from the given list of entry code
     */
    private static List<Entry> fillEntryListFromCode( int nIdForm, List<String> listEntryCode )
    {
        List<Entry> listEntry = new ArrayList<>( );

        EntryFilter entryFilter = new EntryFilter( );
        entryFilter.setIdResource( nIdForm );
        entryFilter.setResourceType( FORM_RESOURCE_TYPE );
        entryFilter.setIdIsGroup( EntryFilter.FILTER_FALSE );
        entryFilter.setIdIsComment( EntryFilter.FILTER_FALSE );

        List<Entry> entryList = EntryHome.getEntryList( entryFilter );

        for ( String strEntryCodeToFind : listEntryCode )
        {
            for ( Entry entry : entryList )
            {
                if ( strEntryCodeToFind.equals( entry.getCode( ) ) )
                {
                    listEntry.add( entry );
                }
            }
        }

        return listEntry;
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

        FormFilter formFilter = getFormFilter( );
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

        FormFilter formFilter = getFormFilter( );
        if ( formFilter != null )
        {
            IFormFilterConfiguration formFilterConfiguration = formFilter.getFormFilterConfiguration( );
            if ( formFilterConfiguration instanceof FormFilterEntryConfiguration )
            {
                FormFilterEntryConfiguration formFilterEntryConfiguration = (FormFilterEntryConfiguration) formFilterConfiguration;
                formColumnResult = formFilterEntryConfiguration.getFormColumn( );
            }
        }

        return formColumnResult;
    }
}
