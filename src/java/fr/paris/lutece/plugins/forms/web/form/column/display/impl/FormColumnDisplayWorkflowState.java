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
package fr.paris.lutece.plugins.forms.web.form.column.display.impl;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.form.column.FormColumnCell;
import fr.paris.lutece.plugins.forms.business.form.search.FormResponseSearchItem;
import fr.paris.lutece.plugins.forms.util.FormMultiviewWorkflowStateNameConstants;
import fr.paris.lutece.portal.service.template.AppTemplateService;

/**
 * Implementation of a FormColumn for a WorkflowState column
 */
public class FormColumnDisplayWorkflowState extends AbstractFormColumnDisplay
{
    // Templates
    private static final String FORM_COLUMN_HEADER_TEMPLATE = "admin/plugins/forms/multiview/column/header/form_column_workflow_state_header.html";
    private static final String FORM_COLUMN_CELL_TEMPLATE = "admin/plugins/forms/multiview/column/cell/form_column_workflow_state_cell.html";

    // Marks
    private static final String MARK_WORKFLOW_STATE_COLUMN_TITLE = "column_title";
    private static final String MARK_WORKFLOW_STATE_NAME = "workflow_state_name";
    private static final String MARK_COLUMN_SORT_ATTRIBUTE = "column_sort_attribute";
    private static final String MARK_SORT_URL = "sort_url";

    /**
     * {@inheritDoc}
     */
    @Override
    public String buildFormColumnHeaderTemplate( String strSortUrl, Locale locale )
    {
        Map<String, Object> model = new LinkedHashMap<>( );
        model.put( MARK_WORKFLOW_STATE_COLUMN_TITLE, getFormColumnTitle( ) );
        model.put( MARK_SORT_URL, buildCompleteSortUrl( strSortUrl ) );
        model.put( MARK_COLUMN_SORT_ATTRIBUTE, FormMultiviewWorkflowStateNameConstants.COLUMN_WORKFLOW_STATE_NAME );

        String strWorkflowStateHeaderTemplate = AppTemplateService.getTemplate( FORM_COLUMN_HEADER_TEMPLATE, locale, model ).getHtml( );
        setFormColumnHeaderTemplate( strWorkflowStateHeaderTemplate );

        return strWorkflowStateHeaderTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String buildFormColumnCellTemplate( FormColumnCell formColumnCell, Locale locale )
    {
        String strWorkflowStateName = StringUtils.EMPTY;
        if ( formColumnCell != null )
        {
            Object objWorkflowStateName = formColumnCell.getFormColumnCellValueByName( FormMultiviewWorkflowStateNameConstants.COLUMN_WORKFLOW_STATE_NAME );
            if ( objWorkflowStateName != null )
            {
                strWorkflowStateName = String.valueOf( objWorkflowStateName );
            }
        }

        Map<String, Object> model = new LinkedHashMap<>( );
        model.put( MARK_WORKFLOW_STATE_NAME, strWorkflowStateName );

        String strWorkflowStateTemplate = AppTemplateService.getTemplate( FORM_COLUMN_CELL_TEMPLATE, locale, model ).getHtml( );

        return strWorkflowStateTemplate;
    }
}
