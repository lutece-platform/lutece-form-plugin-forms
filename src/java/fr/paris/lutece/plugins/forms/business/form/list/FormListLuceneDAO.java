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
package fr.paris.lutece.plugins.forms.business.form.list;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.plugins.forms.business.form.FormParameters;
import fr.paris.lutece.plugins.forms.business.form.FormResponseItem;
import fr.paris.lutece.plugins.forms.business.form.FormResponseItemSortConfig;
import fr.paris.lutece.plugins.forms.business.form.column.FormColumnCell;
import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.column.querypart.FormColumnQueryPartFacade;
import fr.paris.lutece.plugins.forms.business.form.column.querypart.IFormColumnQueryPart;
import fr.paris.lutece.plugins.forms.business.form.filter.FormFilter;
import fr.paris.lutece.plugins.forms.business.form.filter.querypart.FormFilterQueryPartFacade;
import fr.paris.lutece.plugins.forms.business.form.filter.querypart.IFormFilterQueryPart;
import fr.paris.lutece.plugins.forms.business.form.panel.FormPanel;
import fr.paris.lutece.plugins.forms.business.form.panel.configuration.IFormPanelConfiguration;
import fr.paris.lutece.plugins.forms.business.form.panel.initializer.IFormPanelInitializer;
import fr.paris.lutece.plugins.forms.business.form.panel.initializer.querypart.FormPanelInitializerQueryPartFacade;
import fr.paris.lutece.plugins.forms.business.form.panel.initializer.querypart.IFormPanelInitializerQueryPart;
import fr.paris.lutece.plugins.forms.business.form.search.FormResponseSearchItem;
import javax.inject.Inject;
import fr.paris.lutece.plugins.forms.service.search.IFormSearchEngine;

/**
 * Implementation of the IFormListDAO interface
 */
public class FormListLuceneDAO implements IFormListDAO
{
    @Inject
    IFormSearchEngine _formSearchEngine;

    /**
     * {@inheritDoc}
     */
    @Override
    public void populateFormColumns( FormPanel formPanel, List<IFormColumn> listFormColumn, List<FormFilter> listFormFilter, int nStartIndex, int nPageSize,
            FormResponseItemSortConfig sortConfig )
    {
        // To retrieve the values to display on the table we must have a FormPanel and a list of FormColumn
        if ( formPanel == null || CollectionUtils.isEmpty( listFormColumn ) )
        {
            return;
        }

        List<FormResponseItem> listFormResponseItem = searchFormResponseItem( formPanel, listFormColumn, listFormFilter, nStartIndex, nPageSize, sortConfig );

        formPanel.setFormResponseItemList( listFormResponseItem );
    }

    @Override
    public List<FormResponseItem> searchAllFormResponseItem( FormPanel formPanel, List<IFormColumn> listFormColumn, List<FormFilter> listFormFilter,
            FormResponseItemSortConfig sortConfig )
    {
        return searchFormResponseItem( formPanel, listFormColumn, listFormFilter, 0, 0, sortConfig );
    }

    private List<FormResponseItem> searchFormResponseItem( FormPanel formPanel, List<IFormColumn> listFormColumn, List<FormFilter> listFormFilter,
            int nStartIndex, int nPageSize, FormResponseItemSortConfig sortConfig )
    {
        // Create the list of all values of the parameter to used
        List<String> listQueryParametersValues = new ArrayList<>( );

        // Build the list of query part from the formPanel, the list of columns and the list of filters
        List<IFormPanelInitializerQueryPart> listFormPanelInitializerQueryPart = buildFormPanelInitializerQueryPartList( formPanel, listQueryParametersValues );
        List<IFormColumnQueryPart> listFormColumnQueryPart = buildformColumnQueryPartList( listFormColumn );
        List<IFormFilterQueryPart> listFormFilterQueryPart = buildFormFilterQueryPartList( listFormFilter, listQueryParametersValues );

        List<FormResponseItem> listFormResponseItem = new ArrayList<>( );

        for ( FormResponseSearchItem formResponseSearchItem : _formSearchEngine.getSearchResults( listFormPanelInitializerQueryPart, listFormColumnQueryPart,
                listFormFilterQueryPart, sortConfig, nStartIndex, nPageSize, formPanel ) )
        {
            // Create a FormResponseItem sppfor the current result line
            FormResponseItem formResponseItem = createFormResponseItem( formResponseSearchItem );
            listFormResponseItem.add( formResponseItem );

            for ( IFormColumnQueryPart formColumnQueryPart : listFormColumnQueryPart )
            {
                FormColumnCell formColumnCell = formColumnQueryPart.getFormColumnCell( formResponseSearchItem );
                formResponseItem.addFormColumnCell( formColumnCell );
            }
        }

        return listFormResponseItem;
    }

    /**
     * Create a FormResponseItem from a DAOUtil
     * 
     * @param daoUtil
     *            The daoUtil to retrieve the values of the request from
     * @return the created FormResponseItem
     */
    private FormResponseItem createFormResponseItem( FormResponseSearchItem formResponseSearchItem )
    {
        FormResponseItem formResponseItem = new FormResponseItem( );
        formResponseItem.setIdFormResponse( formResponseSearchItem.getIdFormResponse( ) );

        return formResponseItem;
    }

    /**
     * Build the list of all FormPanelInitializerQueryPart associate to all the FormPanelInitializer to retrieve from the given FormPanel
     * 
     * @param formPanel
     *            The FormPanel used to retrieve the list of all FormPanelInitializer to retrieve the list of FormPanelInitializerQueryPart
     * @param listQueryParametersValue
     *            The list of all parameter values to used to fill the DAOUtil statement
     * @return the list of all FormPanelInitializerQueryPart associate to all the FormPanelInitializer to retrieve from the given FormPanel
     */
    private static List<IFormPanelInitializerQueryPart> buildFormPanelInitializerQueryPartList( FormPanel formPanel, List<String> listQueryParametersValue )
    {
        List<IFormPanelInitializerQueryPart> listFormPanelInitializerQueryPart = new ArrayList<>( );

        IFormPanelConfiguration formPanelConfiguration = formPanel.getFormPanelConfiguration( );

        if ( formPanelConfiguration != null && !CollectionUtils.isEmpty( formPanel.getListFormPanelInitializer( ) ) )
        {
            List<IFormPanelInitializer> listFormPanelInitializer = formPanel.getListFormPanelInitializer( );

            for ( IFormPanelInitializer formPanelInitializer : listFormPanelInitializer )
            {
                IFormPanelInitializerQueryPart formPanelInitializerQueryPart = retrieveFormPanelInitializerQueryPart( formPanelInitializer,
                        listQueryParametersValue );
                if ( formPanelInitializerQueryPart != null )
                {
                    listFormPanelInitializerQueryPart.add( formPanelInitializerQueryPart );
                }
            }
        }

        return listFormPanelInitializerQueryPart;
    }

    /**
     * Retrieve the IformPanelInitializerQueryPart associate to the givenFormPanelInitializer
     * 
     * @param formPanelInitializer
     *            The formPanelInitializer used to retrieve the associated IFormPanelInitializerQueryPart
     * @param listQueryParametersPositionValue
     *            The list of all parameter values to used to fill the DAOUtil statement
     * @return the IFormPanelInitializerQueryPart associate to the given FormPanelInitializer or null if not found
     */
    private static IFormPanelInitializerQueryPart retrieveFormPanelInitializerQueryPart( IFormPanelInitializer formPanelInitializer,
            List<String> listQueryParametersPositionValue )
    {
        IFormPanelInitializerQueryPart formPanelInitializerQueryPartResult = null;

        if ( formPanelInitializer != null )
        {
            formPanelInitializerQueryPartResult = new FormPanelInitializerQueryPartFacade( ).getFormPanelInitializerQueryPart( formPanelInitializer );

            if ( formPanelInitializerQueryPartResult != null )
            {
                FormParameters formParameters = formPanelInitializer.getFormParameters( );
                formPanelInitializerQueryPartResult.buildFormPanelInitializerQuery( formParameters );

                List<String> listUsedParametersValues = formParameters.getListUsedParametersValue( );
                listQueryParametersPositionValue.addAll( listUsedParametersValues );
            }
        }

        return formPanelInitializerQueryPartResult;
    }

    /**
     * Build the list of IFormColumnQueryPart to use for build the global query from the given list of IFormColumn
     * 
     * @param listFormColumn
     *            The list of IFormColumn to use for retrieving the list of IFormColumnQueryPart
     * @return the list of IFormColumnQueryPart to use for build the global query
     */
    private static List<IFormColumnQueryPart> buildformColumnQueryPartList( List<IFormColumn> listFormColumn )
    {
        List<IFormColumnQueryPart> listFormColumnQueryPart = new ArrayList<>( );

        if ( !CollectionUtils.isEmpty( listFormColumn ) )
        {
            for ( IFormColumn formColumn : listFormColumn )
            {
                IFormColumnQueryPart formColumnQueryPart = retrieveFormColumnQueryPart( formColumn );
                if ( formColumnQueryPart != null )
                {
                    formColumnQueryPart.setFormColumn( formColumn );
                    listFormColumnQueryPart.add( formColumnQueryPart );
                }
            }
        }

        return listFormColumnQueryPart;
    }

    /**
     * Return the form column query part for the given form column
     * 
     * @param formColumn
     *            The FormColumn to build the queries from
     * @return the form column query part associated to the given form column
     */
    private static IFormColumnQueryPart retrieveFormColumnQueryPart( IFormColumn formColumn )
    {
        IFormColumnQueryPart fomColumnQueryPartResult = null;

        if ( formColumn != null )
        {
            fomColumnQueryPartResult = new FormColumnQueryPartFacade( ).getFormFilterQueryPart( formColumn );
        }

        return fomColumnQueryPartResult;
    }

    /**
     * Build the list of form filter query part for the given list of form filter
     * 
     * @param listFormFilter
     *            The list of form filter to build the list of form filter query part from
     * @param listQueryParametersPositionValue
     *            The list of all parameter values to used to fill the DAOUtil statement
     * @return the list of form filter query part from the given list of form filter
     */
    private static List<IFormFilterQueryPart> buildFormFilterQueryPartList( List<FormFilter> listFormFilter, List<String> listQueryParametersPositionValue )
    {
        List<IFormFilterQueryPart> listFormFilterQueryPart = new ArrayList<>( );

        if ( !CollectionUtils.isEmpty( listFormFilter ) )
        {
            for ( FormFilter formFilter : listFormFilter )
            {
                IFormFilterQueryPart formFilterQueryPart = retrieveFormFilterQueryPart( formFilter, listQueryParametersPositionValue );
                if ( formFilterQueryPart != null )
                {
                    listFormFilterQueryPart.add( formFilterQueryPart );
                }
            }
        }

        return listFormFilterQueryPart;
    }

    /**
     * Retrieve the query part for the given filter
     * 
     * @param formFilter
     *            The FormFilter to build the query
     * @param listQueryParametersPositionValue
     *            The list of all parameter values to used to fill the DAOUtil statement
     * @return the form filter query part associated to the given form filter
     */
    private static IFormFilterQueryPart retrieveFormFilterQueryPart( FormFilter formFilter, List<String> listQueryParametersPositionValue )
    {
        IFormFilterQueryPart formFilterLuceneQueryPartResult = null;

        if ( formFilter != null )
        {
            IFormFilterQueryPart formFilterQueryPart = new FormFilterQueryPartFacade( ).getFormFilterQueryPart( formFilter );
            if ( formFilterQueryPart != null )
            {
                FormParameters formParameters = formFilter.getFormParameters( );
                formFilterQueryPart.buildFormFilterQuery( formParameters );
                formFilterLuceneQueryPartResult = formFilterQueryPart;

                List<String> listUsedParametersValue = formParameters.getListUsedParametersValue( );
                listQueryParametersPositionValue.addAll( listUsedParametersValue );
            }
        }

        return formFilterLuceneQueryPartResult;
    }
}
