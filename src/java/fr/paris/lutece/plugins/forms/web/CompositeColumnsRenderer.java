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
package fr.paris.lutece.plugins.forms.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fr.paris.lutece.plugins.forms.business.FormLayout;
import fr.paris.lutece.plugins.forms.util.FormsUtils;
import fr.paris.lutece.plugins.forms.web.entrytype.DisplayType;
import fr.paris.lutece.portal.service.template.AppTemplateService;

/**
 * Lays out the rendered children of a step or of a group in the columns of its layout, in the front office views where the questions are filled
 */
public final class CompositeColumnsRenderer
{
    private static final String TEMPLATE_COLUMNS_FRONTOFFICE = "/skin/plugins/forms/composite_template/view_columns.html";
    private static final String TEMPLATE_COLUMNS_BACKOFFICE = "/admin/plugins/forms/composite/view_columns.html";
    private static final String MARK_COLUMNS = "columns_html";

    private static final List<DisplayType> COLUMN_DISPLAY_TYPES = List.of( DisplayType.EDITION_FRONTOFFICE, DisplayType.RESUBMIT_FRONTOFFICE,
            DisplayType.COMPLETE_FRONTOFFICE, DisplayType.SUBMIT_BACKOFFICE );

    /**
     * Private constructor
     */
    private CompositeColumnsRenderer( )
    {
    }

    /**
     * Tells whether the children of a step or of a group are laid out in columns: only in the views where the questions are filled (front office, and
     * the back office entry of a response, which is also the preview of the form), and only for a layout of more than one column. The read-only
     * views keep one column.
     *
     * @param displayType
     *            the display type
     * @param nLayout
     *            the layout of the step or of the group
     * @return true when the children are laid out in columns
     */
    public static boolean isLaidOutInColumns( DisplayType displayType, int nLayout )
    {
        return COLUMN_DISPLAY_TYPES.contains( displayType ) && FormLayout.getColumnCount( nLayout ) > 1;
    }

    /**
     * Renders the children of a step or of a group in the columns of its layout, each column keeping the display order of its children
     *
     * @param listChildren
     *            the children
     * @param listChildrenHtml
     *            the HTML of each child, in the order of the children
     * @param nLayout
     *            the layout of the step or of the group
     * @param displayType
     *            the display type, which chooses the front office or the back office template
     * @param locale
     *            the locale
     * @return the HTML of the columns
     */
    public static String render( List<ICompositeDisplay> listChildren, List<String> listChildrenHtml, int nLayout, DisplayType displayType,
            Locale locale )
    {
        List<List<String>> listColumns = new ArrayList<>( );
        for ( int i = 0; i < FormLayout.getColumnCount( nLayout ); i++ )
        {
            listColumns.add( new ArrayList<>( ) );
        }

        for ( int i = 0; i < listChildren.size( ) && i < listChildrenHtml.size( ); i++ )
        {
            ICompositeDisplay child = listChildren.get( i );
            int nColumn = ( child.getFormDisplay( ) != null ) ? child.getFormDisplay( ).getDisplayColumn( ) : FormLayout.FIRST_COLUMN;
            listColumns.get( FormLayout.normalizeColumn( nColumn, nLayout ) - 1 ).add( listChildrenHtml.get( i ) );
        }

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_COLUMNS, listColumns );

        String strTemplate = ( displayType == DisplayType.SUBMIT_BACKOFFICE ) ? TEMPLATE_COLUMNS_BACKOFFICE : TEMPLATE_COLUMNS_FRONTOFFICE;

        return AppTemplateService.getTemplate( strTemplate, locale, FormsUtils.withHtmlMarkup( model, MARK_COLUMNS ) ).getHtml( );
    }
}
