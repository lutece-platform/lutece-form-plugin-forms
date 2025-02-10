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
package fr.paris.lutece.plugins.forms.business.form.list;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.forms.business.form.FormItemSortConfig;
import java.util.Comparator;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.filter.FormFilter;
import fr.paris.lutece.plugins.forms.business.form.panel.FormPanel;

/**
 * Facade used to populate a list of FormColumn
 */
@ApplicationScoped
public class FormListFacade
{
    // Constants
    public static final String BEAN_NAME = "forms.formList.facade";

    // Variables
    @Inject
    private IFormListDAO _formListDAO;

    public FormListFacade( )
    {
    }
    
    /**
     * Constructor
     * 
     * @param formListDAO
     *            The DAO to use for the Facade
     */
    public FormListFacade( IFormListDAO formListDAO )
    {
        _formListDAO = formListDAO;
    }

    /**
     * Populate the given FormPanel with the information of the given FormColumns and FormFilters
     * 
     * @param formPanel
     *            The FormPanel to populate
     * @param listFormColumn
     *            The list of all FormColumn to use to be populated
     * @param listFormFilter
     *            The list of FormFilter to use for retrieving the data of the columns to populate
     * @param nStartIndex
     *            The start index of doc
     * @param nPageSize
     *            The number of docs to load for pagination purpose
     * @param sortConfig
     */
    public void populateFormColumns( FormPanel formPanel, List<IFormColumn> listFormColumn, List<FormFilter> listFormFilter, int nStartIndex, int nPageSize,
            FormItemSortConfig sortConfig )
    {
        listFormColumn.sort( Comparator.comparing( IFormColumn::getFormColumnPosition ) );

        _formListDAO.populateFormColumns( formPanel, listFormColumn, listFormFilter, nStartIndex, nPageSize, sortConfig );
    }

    /**
     * Populate the given FormPanel with the information of the given FormColumns and FormFilters
     *
     * @param formPanel
     *            The FormPanel to populate
     * @param listFormColumn
     *            The list of all FormColumn to use to be populated
     * @param listFormFilter
     *            The list of FormFilter to use for retrieving the data of the columns to populate
     * @param nStartIndex
     *            The start index of doc
     * @param nPageSize
     *            The number of docs to load for pagination purpose
     * @param sortConfig
     *            The comparator config
     * @param user
     *            The current user
     */
    public void populateFormColumns( FormPanel formPanel, List<IFormColumn> listFormColumn, List<FormFilter> listFormFilter, int nStartIndex, int nPageSize,
            FormItemSortConfig sortConfig, User user )
    {
        listFormColumn.sort( Comparator.comparing( IFormColumn::getFormColumnPosition ) );

        _formListDAO.populateFormColumns( formPanel, listFormColumn, listFormFilter, nStartIndex, nPageSize, sortConfig, user );
    }
}
