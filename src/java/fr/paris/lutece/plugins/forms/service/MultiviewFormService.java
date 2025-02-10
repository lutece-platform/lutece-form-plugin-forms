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
package fr.paris.lutece.plugins.forms.service;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.form.FormResponseItem;
import fr.paris.lutece.plugins.forms.business.form.FormItemSortConfig;
import fr.paris.lutece.plugins.forms.business.form.column.FormColumnComparator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.column.impl.FormColumnEntry;
import fr.paris.lutece.plugins.forms.business.form.column.impl.FormColumnEntryCartography;
import fr.paris.lutece.plugins.forms.business.form.column.impl.FormColumnEntryGeolocation;
import fr.paris.lutece.plugins.forms.business.form.filter.FormFilter;
import fr.paris.lutece.plugins.forms.business.form.filter.FormFilterForms;
import fr.paris.lutece.plugins.forms.business.form.filter.configuration.FormFilterDateConfiguration;
import fr.paris.lutece.plugins.forms.business.form.filter.configuration.FormFilterEntryConfiguration;
import fr.paris.lutece.plugins.forms.business.form.filter.configuration.FormFilterFormsConfiguration;
import fr.paris.lutece.plugins.forms.business.form.filter.configuration.IFormFilterConfiguration;
import fr.paris.lutece.plugins.forms.business.form.list.FormListFacade;
import fr.paris.lutece.plugins.forms.business.form.list.IFormListDAO;
import fr.paris.lutece.plugins.forms.business.form.panel.FormPanel;
import fr.paris.lutece.plugins.forms.business.form.search.FormResponseSearchItem;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.web.entrytype.EntryTypeDateDisplayService;
import fr.paris.lutece.plugins.forms.web.entrytype.EntryTypeDefaultDisplayService;
import fr.paris.lutece.plugins.forms.web.entrytype.IEntryDisplayService;
import fr.paris.lutece.plugins.forms.web.form.panel.display.IFormPanelDisplay;
import fr.paris.lutece.portal.service.rbac.RBACService;
import jakarta.enterprise.inject.spi.CDI;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
     * Populate the given FormPanel with the information from the given list of FormColumns and FormFilters
     *
     * @param formPanel
     *            The FormPanel used to retrieve the values of the FormColumns
     * @param listFormColumn
     *            The list of all FormColumn to use to be populated
     * @param listFormFilter
     *            The list of FormFilter to use for retrieving the data of the columns to populate
     * @param nStartIndex
     *            The start index of doc to load
     * @param nPageSize
     *            The size of page of docs to load
     * @param sortConfig
     *            The comparator config used for sorting
     * @param user
     *            The current user
     */
    public void populateFormColumns( FormPanel formPanel, List<IFormColumn> listFormColumn, List<FormFilter> listFormFilter, int nStartIndex, int nPageSize,
            FormItemSortConfig sortConfig, User user )
    {
        FormListFacade formListFacade = CDI.current( ).select( FormListFacade.class ).get( );
        formListFacade.populateFormColumns( formPanel, listFormColumn, listFormFilter, nStartIndex, nPageSize, sortConfig, user );
    }

    public List<FormResponseItem> searchAllListFormResponseItem( FormPanel formPanel, List<IFormColumn> listFormColumn, List<FormFilter> listFormFilter,
            FormItemSortConfig sortConfig )
    {
        IFormListDAO formListDAO = CDI.current( ).select( IFormListDAO.class ).get( );
        return formListDAO.searchAllFormResponseItem( formPanel, listFormColumn, listFormFilter, sortConfig );
    }

    /**
     * Find the FormPanel which is active in the given list
     * 
     * @param listFormPanelDisplay
     *            The list to retrieve the active FormPanelDisplay
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
     * Get the form columns list from CDI container and multiview conf
     * 
     * @param nIdForm
     * @return the form columns list
     */
    public List<IFormColumn> getFormColumnsList( Integer nIdForm, Locale locale, User user )
    {
        Map<String, IFormColumn> mapFormColumns = new LinkedHashMap<>( );

        // Retrieve all the column CDI beans
        List<IFormColumn> listFormColumns = CDI.current( ).select( IFormColumn.class ).stream( ).collect( Collectors.toList( ) );

        Collections.sort( listFormColumns, new FormColumnComparator( ) ); // sort by position
        listFormColumns.forEach( column -> mapFormColumns.put( column.getFormColumnTitle( locale ), column ) );

        if ( nIdForm == null || nIdForm == FormsConstants.DEFAULT_ID_VALUE )
        {
            List<Form> listForm = FormHome.getFormList( );
            listForm.removeIf( f -> !RBACService.isAuthorized( Form.RESOURCE_TYPE, String.valueOf( f.getId( ) ),
                    FormsResourceIdService.PERMISSION_VIEW_FORM_RESPONSE, user ) );

            if ( listForm.size( ) == 1 )
            {
                nIdForm = listForm.get( 0 ).getId( );
            }
        }

        // Get the List of questions the user is authorized to access
        List<Question> listQuestions = getQuestionListFromAuthorizedForms( nIdForm, user );

        // Sort questions by multiview order
        listQuestions.sort( Comparator.comparing( Question::getMultiviewColumnOrder ) );

        // Then add global columns from config questions
        addColumnFromConfig( mapFormColumns, listQuestions, true, locale );

        if ( nIdForm != null && nIdForm != FormsConstants.DEFAULT_ID_VALUE )
        {
            // Then add specific columns from config questions
            addColumnFromConfig( mapFormColumns, listQuestions, false, locale );
        }

        // Filter the columns that must be displayed
        mapFormColumns.entrySet( ).removeIf( entry -> !entry.getValue( ).isDisplayed( ) );

        // Set the order of the columns
        int nPosition = 1;
        for ( IFormColumn column : mapFormColumns.values( ) )
        {
            column.setFormColumnPosition( nPosition++ );
        }

        return new ArrayList<>( mapFormColumns.values( ) );
    }

    /**
     * Get the form columns list from CDI container and multiview conf
     * 
     * @param nIdForm
     * @return the form columns list
     */
    public List<FormFilter> getFormFiltersList( Integer nIdForm, List<IFormColumn> listFormColumn, Locale locale, User user )
    {
        Map<String, FormFilter> mapFormFilter = new HashMap<>( );

        // First add the XML-based filters
        List<IFormFilterConfiguration> formFilterConfigurations = CDI.current().select( IFormFilterConfiguration.class ).stream( ).toList( );
        
        for ( IFormFilterConfiguration formFilterConfiguration : formFilterConfigurations )
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

        if ( nIdForm == null || nIdForm == FormsConstants.DEFAULT_ID_VALUE )
        {
            List<Form> listForm = FormHome.getFormList( );
            listForm.removeIf( f -> !RBACService.isAuthorized( Form.RESOURCE_TYPE, String.valueOf( f.getId( ) ),
                    FormsResourceIdService.PERMISSION_VIEW_FORM_RESPONSE, user ) );

            if ( listForm.size( ) == 1 )
            {
                nIdForm = listForm.get( 0 ).getId( );
            }
        }

        // Get the List of questions the user is authorized to access
        List<Question> listQuestions = getQuestionListFromAuthorizedForms( nIdForm, user );

        // Then add the global question-based for Filters
        addFilterFromConfig( mapFormFilter, listQuestions, listFormColumn, true, locale );

        if ( nIdForm != null && nIdForm != FormsConstants.DEFAULT_ID_VALUE )
        {
            // Then add specific columns from config questions
            addFilterFromConfig( mapFormFilter, listQuestions, listFormColumn, false, locale );
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
    private void addColumnFromConfig( Map<String, IFormColumn> mapColumns, List<Question> listQuestions, boolean bGlobal, Locale locale )
    {
        int nPosition = mapColumns.size( );
        for ( Question question : listQuestions )
        {
            if ( bGlobal ? question.isVisibleMultiviewGlobal( ) : question.isVisibleMultiviewFormSelected( ) )
            {
                question = QuestionHome.findByPrimaryKey( question.getId( ) );

                if ( !mapColumns.keySet( ).contains( question.getColumnTitle( ) ) )
                {
                    IEntryDisplayService displayService = EntryServiceManager.getInstance( ).getEntryDisplayService( question.getEntry( ).getEntryType( ) );
                    IFormColumn column = displayService.getFormColumn( ++nPosition, question.getColumnTitle( ) );
                    if (column != null)
                    {
                    	addEntryCodeToColumn( column, question );
                        mapColumns.put( column.getFormColumnTitle( locale ), column );
                    }
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
        if ( column instanceof FormColumnEntryCartography )
        {
            ( (FormColumnEntryCartography) column ).addEntryCode( question.getCode( ) );

        }
    }

    /**
     * Add the filter based on multiview config question
     * 
     * @param mapColumns
     * @param listQuestions
     * @param bGlobal
     */
    private void addFilterFromConfig( Map<String, FormFilter> mapFilters, List<Question> listQuestions, List<IFormColumn> listFormColumns, boolean bGlobal,
            Locale locale )
    {
        int nPosition = mapFilters.size( );

        List<Question> listFiltrableQuestions = listQuestions.stream( )
                .filter( question -> bGlobal ? question.isFiltrableMultiviewGlobal( ) : question.isFiltrableMultiviewFormSelected( ) )
                .collect( Collectors.toList( ) );

        for ( Question question : listFiltrableQuestions )
        {
            Question currentQuestion = QuestionHome.findByPrimaryKey( question.getId( ) );

            if ( mapFilters.keySet( ).contains( currentQuestion.getCode( ) ) )
            {
                continue;
            }

            IEntryDisplayService displayService = EntryServiceManager.getInstance( ).getEntryDisplayService( currentQuestion.getEntry( ).getEntryType( ) );

            if ( displayService instanceof EntryTypeDateDisplayService )
            {
                FormFilter formFilter = new FormFilter( );
                IFormFilterConfiguration formFilterConfiguration = new FormFilterDateConfiguration( nPosition++, currentQuestion.getTitle( ),
                        FormResponseSearchItem.FIELD_ENTRY_CODE_SUFFIX + currentQuestion.getCode( ) + FormResponseSearchItem.FIELD_RESPONSE_FIELD_ITER + "0"
                                + FormResponseSearchItem.FIELD_DATE_SUFFIX );

                formFilter.setFormFilterConfiguration( formFilterConfiguration );
                mapFilters.put( currentQuestion.getCode( ), formFilter );
            }
            if ( displayService instanceof EntryTypeDefaultDisplayService )
            {
                FormFilter formFilter = new FormFilter( );

                List<IFormColumn> listFormColumnsEntry = listFormColumns.stream( ).filter( c -> c instanceof FormColumnEntry )
                        .filter( c -> c.getFormColumnTitle( locale ).equals( currentQuestion.getColumnTitle( ) ) ).collect( Collectors.toList( ) );

                for ( IFormColumn column : listFormColumnsEntry )
                {
                    IFormFilterConfiguration formFilterConfiguration = new FormFilterEntryConfiguration( nPosition++, currentQuestion.getTitle( ),
                            FormResponseSearchItem.FIELD_ENTRY_CODE_SUFFIX + currentQuestion.getCode( ), column );

                    formFilter.setFormFilterConfiguration( formFilterConfiguration );
                    mapFilters.put( currentQuestion.getCode( ), formFilter );
                }

            }
        }
    }

    /**
     * Get the List of questions from the Forms that are part of the User's workgroups
     * 
     * @param nIdForm
     *            The Form being processed. Can be null or -1 if there are multiple Forms
     * @param user
     *            The current User
     * @return a List of Question objects
     */
    private List<Question> getQuestionListFromAuthorizedForms( Integer nIdForm, User user )
    {
        List<Question> listQuestions = new ArrayList<>( );

        // Retrieve the authorized List of Forms (same workgroups as the User)
        List<Form> listForms = FormHome.getFormList( );
        listForms = (List<Form>) AdminWorkgroupService.getAuthorizedCollection( listForms, user );

        // If there are multiple Forms to check
        if ( nIdForm == null || nIdForm == FormsConstants.DEFAULT_ID_VALUE )
        {
            // Retrieve the questions from all the Forms
            for ( Form authorizedForm : listForms )
            {
                listQuestions.addAll( QuestionHome.getListQuestionByIdFormUncomplete( authorizedForm.getId( ) ) );
            }
        }
        // If only one Form has to be checked
        else
        {
            // If the given Form is part of the authorized Forms
            if ( listForms.stream( ).anyMatch( form -> form.getId( ) == nIdForm ) )
            {
                // Retrieve the List of questions from the specified Form
                listQuestions = QuestionHome.getListQuestionByIdFormUncomplete( nIdForm );
            }
        }
        return listQuestions;
    }
}
