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
package fr.paris.lutece.plugins.forms.web.form.multiview.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.form.FormResponseItem;
import fr.paris.lutece.plugins.forms.business.form.column.FormColumnCell;
import fr.paris.lutece.plugins.forms.web.form.column.display.IFormColumnDisplay;
import fr.paris.lutece.portal.service.template.AppTemplateService;

/**
 * Builder class for the template of FormColumnDisplay and FormFilterDisplay objects
 */
public final class FormListTemplateBuilder
{
    // Templates
    private static final String TEMPLATE_MULTIVIEW_FORM_TABLE = "admin/plugins/forms/multiview/includes/include_manage_multiview_forms_table.html";

    // Marks
    private static final String FROM_RESPONSE_ITEM_LIST = "from_response_item_list";
    private static final String FORM_RESPONSE_COLUMN_HEADER_TEMPLATE_LIST = "form_response_column_header_template_list";
    private static final String FORM_RESPONSE_LINE_TEMPLATE_LIST = "form_response_line_template_list";

    /**
     * Constructor
     */
    private FormListTemplateBuilder( )
    {

    }
    
    /**
     * Build the template of the table of all form response
     * 
     * @param listFormColumnDisplay
     *            The list of all FormColumnDisplay objects to build the global template of the form columns from
     * @param listFormResponseItem
     *            The list of all FormResponseItem used to build the tab with all form responses
     * @param locale
     *            The locale to used for build template
     * @param strSortUrl
     *            The url to use for sort a column
     * @return the global template of all FormColumnDisplay objects
     */
    public static String buildTableTemplate( List<IFormColumnDisplay> listFormColumnDisplay, List<FormResponseItem> listFormResponseItem,
            Locale locale, String strSortUrl )
    {
        String strTableTemplate = StringUtils.EMPTY;

        if ( !CollectionUtils.isEmpty( listFormColumnDisplay ) && !CollectionUtils.isEmpty( listFormResponseItem ) )
        {
            // Build the list of all from column header template
            List<String> listFormColumnHeaderTemplate = buildFormColumnHeaderTemplateList( listFormColumnDisplay, locale, strSortUrl );

            // Build the list of all FormColumnLineTemplate
            List<FormColumnLineTemplate> listFormColumnLineTemplate = buildFormColumnLineTemplateList( listFormColumnDisplay, listFormResponseItem,
                    locale );

            // Build the model
            Map<String, Object> model = new LinkedHashMap<>( );
            model.put( FORM_RESPONSE_COLUMN_HEADER_TEMPLATE_LIST, listFormColumnHeaderTemplate );
            model.put( FROM_RESPONSE_ITEM_LIST, listFormResponseItem );
            model.put( FORM_RESPONSE_LINE_TEMPLATE_LIST, listFormColumnLineTemplate );

            strTableTemplate = AppTemplateService.getTemplate( TEMPLATE_MULTIVIEW_FORM_TABLE, locale, model ).getHtml( );
        }

        return strTableTemplate;
    }

    /**
     * Build the list of all form column header template
     * 
     * @param listFormColumnDisplay
     *            The list of all form column display to retrieve the header template from
     * @param locale
     *            The locale to use for build the template
     * @param strSortUrl
     *            The url to use for sort a column (can be null)
     * @return the list of all form column header template
     */
    private static List<String> buildFormColumnHeaderTemplateList( List<IFormColumnDisplay> listFormColumnDisplay, Locale locale, String strSortUrl )
    {
        List<String> listFormColumnHeaderTemplate = new ArrayList<>( );

        if ( !CollectionUtils.isEmpty( listFormColumnDisplay ) )
        {
            for ( IFormColumnDisplay formColumnDisplay : listFormColumnDisplay )
            {
                String strFormColumnDisplayHeaderTemplate = formColumnDisplay.buildFormColumnHeaderTemplate( strSortUrl, locale );
                listFormColumnHeaderTemplate.add( strFormColumnDisplayHeaderTemplate );
            }
        }

        return listFormColumnHeaderTemplate;
    }

    /**
     * Build the list of all FormColumnLineTemplate
     * 
     * @param listFormColumnDisplay
     *            The list of form column display to use for build the map of line template
     * @param listFormResponseItem
     *            The list of form response item to use for build the map of line template
     * @param locale
     *            The locale to use for build the template
     * @return the list of all FormColumnLineTemplate
     */
    private static List<FormColumnLineTemplate> buildFormColumnLineTemplateList( List<IFormColumnDisplay> listFormColumnDisplay,
            List<FormResponseItem> listFormResponseItem, Locale locale )
    {
        List<FormColumnLineTemplate> listFormColumnLineTemplate = new ArrayList<>( );

        if ( !CollectionUtils.isEmpty( listFormColumnDisplay ) && !CollectionUtils.isEmpty( listFormResponseItem ) )
        {
            for ( FormResponseItem formResponseItem : listFormResponseItem )
            {
                int nIdFormResponse = formResponseItem.getIdFormResponse( );
                FormColumnLineTemplate formResponseColumnLineTemplate = new FormColumnLineTemplate( nIdFormResponse );

                List<FormColumnCell> listFormColumnCell = formResponseItem.getFormColumnCellValues( );
                populateLineTemplateFromCellValues( formResponseColumnLineTemplate, listFormColumnCell, listFormColumnDisplay, locale );

                listFormColumnLineTemplate.add( formResponseColumnLineTemplate );
            }
        }

        return listFormColumnLineTemplate;
    }

    /**
     * Populate the given FormColumnLineTemplate with the value from the list of FormColumnCell and the display from the given list of IFormColumnDisplay
     * 
     * @param formColumnLineTemplate
     *            The FormColumnLineTemplate to populate
     * @param listFormColumnCell
     *            The list of FormColumnCell to retrieve the values from
     * @param listFormColumnDisplay
     *            The list of IFormColumnDisplay to use for the column
     * @param locale
     *            The locale to use for build the templates
     */
    private static void populateLineTemplateFromCellValues( FormColumnLineTemplate formColumnLineTemplate, List<FormColumnCell> listFormColumnCell,
            List<IFormColumnDisplay> listFormColumnDisplay, Locale locale )
    {
        if ( !CollectionUtils.isEmpty( listFormColumnCell ) )
        {
            for ( int index = 0; index < listFormColumnCell.size( ); index++ )
            {
                // We will increment the index to retrieve the form column display because the first column is at position 1
                int nColumnDisplayPosition = index + 1;
                IFormColumnDisplay formColumnDisplay = findFormColumnDisplayByPosition( nColumnDisplayPosition, listFormColumnDisplay );
                if ( formColumnDisplay != null )
                {
                    FormColumnCell formColumnCell = listFormColumnCell.get( index );
                    String strFormColunmCellTemplate = formColumnDisplay.buildFormColumnCellTemplate( formColumnCell, locale );
                    formColumnLineTemplate.addFormColumnCellTemplate( strFormColunmCellTemplate );
                }
            }
        }
    }

    /**
     * Find the FormColumnDisplay in the given list with the specified position or null if not found
     * 
     * @param nFormColumnPosition
     *            The position of the FormColumnDisplay to retrieve
     * @param listFormColumnDisplay
     *            The list of FormColumnDisplay from where to find the column with the specified position
     * @return the FormColumnDisplay with the specified position nor null if not found
     */
    private static IFormColumnDisplay findFormColumnDisplayByPosition( int nFormColumnPosition, List<IFormColumnDisplay> listFormColumnDisplay )
    {
        IFormColumnDisplay formColumnDisplayResult = null;

        if ( !CollectionUtils.isEmpty( listFormColumnDisplay ) )
        {
            for ( IFormColumnDisplay formColumnDisplay : listFormColumnDisplay )
            {
                if ( formColumnDisplay.getPosition( ) == nFormColumnPosition )
                {
                    formColumnDisplayResult = formColumnDisplay;
                    break;
                }
            }
        }

        return formColumnDisplayResult;
    }
}
