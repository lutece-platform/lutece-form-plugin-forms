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
package fr.paris.lutece.plugins.forms.service;

import fr.paris.lutece.plugins.forms.business.MultiviewConfig;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.form.FormResponseItemSortConfig;
import fr.paris.lutece.plugins.forms.business.form.column.FormColumnComparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.column.impl.FormColumnEntry;
import fr.paris.lutece.plugins.forms.business.form.column.impl.FormColumnEntryGeolocation;
import fr.paris.lutece.plugins.forms.business.form.column.impl.FormColumnForms;
import fr.paris.lutece.plugins.forms.business.form.filter.FormFilter;
import fr.paris.lutece.plugins.forms.business.form.filter.FormFilterForms;
import fr.paris.lutece.plugins.forms.business.form.filter.configuration.FormFilterDateConfiguration;
import fr.paris.lutece.plugins.forms.business.form.filter.configuration.FormFilterEntryConfiguration;
import fr.paris.lutece.plugins.forms.business.form.filter.configuration.FormFilterFormsConfiguration;
import fr.paris.lutece.plugins.forms.business.form.filter.configuration.IFormFilterConfiguration;
import fr.paris.lutece.plugins.forms.business.form.list.FormListFacade;
import fr.paris.lutece.plugins.forms.business.form.panel.FormPanel;
import fr.paris.lutece.plugins.forms.business.form.search.FormResponseSearchItem;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.web.entrytype.EntryTypeDateDisplayService;
import fr.paris.lutece.plugins.forms.web.entrytype.EntryTypeDefaultDisplayService;
import fr.paris.lutece.plugins.forms.web.entrytype.IEntryDisplayService;
import fr.paris.lutece.plugins.forms.web.form.panel.display.IFormPanelDisplay;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service dedicated to managing of the multiview of forms
 */
public final class MultiviewFormService
{
    /**
     * Constructor
     */
    private MultiviewFormService( )
    {

    }

    /**
     * Return the singleton of the MultiviewFormService
     * 
     * @return the singleton of the MultiviewFormService
     */
    public static MultiviewFormService getInstance( )
    {
        return MultiviewFormServiceHolder._singleton;
    }

    /**
     * Populate the given FormPanel with the information from the given list of
     * FormColumns and FormFilters
     * 
     * @param formPanel      The FormPanel used to retrieve the values of the
     *                       FormColumns
     * @param listFormColumn The list of all FormColumn to use to be populated
     * @param listFormFilter The list of FormFilter to use for retrieving the data
     *                       of the columns to populate
     * @param nStartIndex    The start index of doc to load
     * @param nPageSize      The size of page of docs to load
     * @param sortConfig
     */
    public void populateFormColumns( FormPanel formPanel, List<IFormColumn> listFormColumn,
            List<FormFilter> listFormFilter, int nStartIndex, int nPageSize, FormResponseItemSortConfig sortConfig )
    {
        FormListFacade formListFacade = SpringContextService.getBean( FormListFacade.BEAN_NAME );
        formListFacade.populateFormColumns( formPanel, listFormColumn, listFormFilter, nStartIndex, nPageSize,
                sortConfig );
    }

    /**
     * Find the FormPanel which is active in the given list
     * 
     * @param listFormPanelDisplay The list to retrieve the active FormPanelDisplay
     * @return the IFormFilterPanelDisplay which is active or null if not found
     */
    public IFormPanelDisplay findActiveFormPanel( List<IFormPanelDisplay> listFormPanelDisplay )
    {
        IFormPanelDisplay formPanelDisplayActive = null;

        if ( !CollectionUtils.isEmpty( listFormPanelDisplay ) )
        {
            for ( IFormPanelDisplay formPanelDisplay : listFormPanelDisplay )
            {
                if ( formPanelDisplay.isActive( ) )
                {
                    formPanelDisplayActive = formPanelDisplay;
                    break;
                }
            }
        }

        return formPanelDisplayActive;
    }

    /**
     * Holder class which manage the singleton of the MultiviewFormService
     */
    private static class MultiviewFormServiceHolder
    {
        // Variables
        private static final MultiviewFormService _singleton = new MultiviewFormService( );
    }

    /**
     * Get the form columns list from spring and multiview conf
     * 
     * @param nIdForm
     * @return the form columns list
     */
    public List<IFormColumn> getFormColumnsList( Integer nIdForm )
    {
        Map<String, IFormColumn> mapFormColumns = new LinkedHashMap<>( );

        // Retrieve all the column Spring beans
        List<IFormColumn> listFormColumns = SpringContextService.getBeansOfType( IFormColumn.class );
        Collections.sort( listFormColumns, new FormColumnComparator( ) ); // sort by position
        listFormColumns.forEach( column -> mapFormColumns.put( column.getFormColumnTitle( ), column ) );

        // Then add global columns from config questions
        List<Question> listQuestions = ( nIdForm == null || nIdForm == FormsConstants.DEFAULT_ID_VALUE )
                ? QuestionHome.getQuestionsListUncomplete( )
                : QuestionHome.getListQuestionByIdFormUncomplete( nIdForm );

        addColumnFromConfig( mapFormColumns, listQuestions, true );

        if ( nIdForm != null && nIdForm != FormsConstants.DEFAULT_ID_VALUE )
        {
            // Then add specific columns from config questions
            addColumnFromConfig( mapFormColumns, listQuestions, false );
        }

        // Filter the columns with the multiview config
        filterWithMultiviewConfig( mapFormColumns );

        // Set the order of the columns
        int nPosition = 1;
        for ( IFormColumn column : mapFormColumns.values( ) )
        {
            column.setFormColumnPosition( nPosition++ );
        }

        return new ArrayList<>( mapFormColumns.values( ) );
    }

    /**
     * Get the form columns list from spring and multiview conf
     * 
     * @param nIdForm
     * @return the form columns list
     */
    public List<FormFilter> getFormFiltersList( Integer nIdForm, List<IFormColumn> listFormColumn )
    {
        Map<String, FormFilter> mapFormFilter = new HashMap<>( );

        // First add the XML-based filters
        for ( IFormFilterConfiguration formFilterConfiguration : SpringContextService
                .getBeansOfType( IFormFilterConfiguration.class ) )
        {
            FormFilter formFilter;
            if ( formFilterConfiguration instanceof FormFilterFormsConfiguration )
            {
                formFilter = new FormFilterForms( );
            }
            else
            {
                formFilter = new FormFilter( );
            }
            formFilter.setFormFilterConfiguration( formFilterConfiguration );
            mapFormFilter.put( formFilter.getFormFilterConfiguration( ).getFormFilterName( ), formFilter );
        }

        // Then add the global question-based for Filters
        List<Question> listQuestions = ( nIdForm == null || nIdForm == FormsConstants.DEFAULT_ID_VALUE )
                ? QuestionHome.getQuestionsListUncomplete( )
                : QuestionHome.getListQuestionByIdFormUncomplete( nIdForm );

        addFilterFromConfig( mapFormFilter, listQuestions, listFormColumn, true );

        if ( nIdForm != null && nIdForm != FormsConstants.DEFAULT_ID_VALUE )
        {
            // Then add specific columns from config questions
            addFilterFromConfig( mapFormFilter, listQuestions, listFormColumn, false );
        }

        return new ArrayList<>( mapFormFilter.values( ) );
    }

    /**
     * Add the column config based on multiview config question
     * 
     * @param mapColumns
     * @param listQuestions
     * @param bGlobal
     */
    private void addColumnFromConfig( Map<String, IFormColumn> mapColumns, List<Question> listQuestions,
            boolean bGlobal )
    {
        int nPosition = mapColumns.size( );
        for ( Question question : listQuestions )
        {
            if ( bGlobal ? question.isVisibleMultiviewGlobal( ) : question.isVisibleMultiviewFormSelected( ) )
            {
                question = QuestionHome.findByPrimaryKey( question.getId( ) );

                if ( !mapColumns.keySet( ).contains( question.getColumnTitle( ) ) )
                {
                    IEntryDisplayService displayService = EntryServiceManager.getInstance( )
                            .getEntryDisplayService( question.getEntry( ).getEntryType( ) );
                    IFormColumn column = displayService.getFormColumn( ++nPosition, question.getColumnTitle( ) );
                    addEntryCodeToColumn( column, question );

                    mapColumns.put( column.getFormColumnTitle( ), column );
                }
                else
                {
                    IFormColumn column = mapColumns.get( question.getColumnTitle( ) );
                    addEntryCodeToColumn( column, question );
                }
            }
        }
    }

    private void addEntryCodeToColumn( IFormColumn column, Question question )
    {
        if ( column instanceof FormColumnEntry )
        {
            ( (FormColumnEntry) column ).addEntryCode( question.getCode( ) );
        }
        if ( column instanceof FormColumnEntryGeolocation )
        {
            ( (FormColumnEntryGeolocation) column ).addEntryCode( question.getCode( ) );
        }
    }

    /**
     * Add the filter based on multiview config question
     * 
     * @param mapColumns
     * @param listQuestions
     * @param bGlobal
     */
    private void addFilterFromConfig( Map<String, FormFilter> mapFilters, List<Question> listQuestions,
            List<IFormColumn> listFormColumns, boolean bGlobal )
    {
        int nPosition = mapFilters.size( );

        List<Question> listFiltrableQuestions = listQuestions.stream( )
                .filter( question -> bGlobal ? question.isFiltrableMultiviewGlobal( )
                        : question.isFiltrableMultiviewFormSelected( ) )
                .collect( Collectors.toList( ) );

        for ( Question question : listFiltrableQuestions )
        {
            question = QuestionHome.findByPrimaryKey( question.getId( ) );

            if ( mapFilters.keySet( ).contains( question.getCode( ) ) )
            {
                continue;
            }

            IEntryDisplayService displayService = EntryServiceManager.getInstance( )
                    .getEntryDisplayService( question.getEntry( ).getEntryType( ) );

            if ( displayService instanceof EntryTypeDateDisplayService )
            {
                FormFilter formFilter = new FormFilter( );
                IFormFilterConfiguration formFilterConfiguration = new FormFilterDateConfiguration( nPosition++,
                        question.getTitle( ),
                        FormResponseSearchItem.FIELD_ENTRY_CODE_SUFFIX + question.getCode( )
                                + FormResponseSearchItem.FIELD_RESPONSE_FIELD_ITER + "0"
                                + FormResponseSearchItem.FIELD_DATE_SUFFIX );

                formFilter.setFormFilterConfiguration( formFilterConfiguration );
                mapFilters.put( question.getCode( ), formFilter );
            }
            if ( displayService instanceof EntryTypeDefaultDisplayService )
            {
                FormFilter formFilter = new FormFilter( );

                List<IFormColumn> listFormColumnsEntry = listFormColumns.stream( )
                        .filter( c -> c instanceof FormColumnEntry ).collect( Collectors.toList( ) );

                for ( IFormColumn column : listFormColumnsEntry )
                {
                    if ( column.getFormColumnTitle( ).equals( question.getColumnTitle( ) ) )
                    {
                        IFormFilterConfiguration formFilterConfiguration = new FormFilterEntryConfiguration(
                                nPosition++, question.getTitle( ),
                                FormResponseSearchItem.FIELD_ENTRY_CODE_SUFFIX + question.getCode( ), column );

                        formFilter.setFormFilterConfiguration( formFilterConfiguration );
                        mapFilters.put( question.getCode( ), formFilter );
                    }
                }

            }
        }
    }

    /**
     * Filter the columns with the multiview config
     * 
     * @param mapColumns the map columns
     */
    private void filterWithMultiviewConfig( Map<String, IFormColumn> mapColumns )
    {
        MultiviewConfig config = MultiviewConfig.getInstance( );

        if ( !config.isDisplayFormsTitleColumn( ) )
        {
            mapColumns.entrySet( ).removeIf( entry -> entry.getValue( ) instanceof FormColumnForms );
        }

    }
}
