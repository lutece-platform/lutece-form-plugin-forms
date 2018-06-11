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
package fr.paris.lutece.plugins.forms.business.form;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.form.column.FormColumnCell;
import fr.paris.lutece.plugins.forms.business.form.column.FormColumnCellComparator;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * Comparator for FormResponseItem object. The comparison is based on the value of objects with the given key
 */
public class FormResponseItemComparator implements Comparator<FormResponseItem>, Serializable
{
    /**
     * Generated serial UID
     */
    private static final long serialVersionUID = -1894179865016507190L;

    // Constants
    private static final int SORT_ASCENDANT_DIRECTION = NumberUtils.INTEGER_ONE;
    private static final int SORT_DESCENDANT_DIRECTION = NumberUtils.INTEGER_MINUS_ONE;
    private static final String DEFAULT_CONFIGURATION_BEAN_NAME = "forms.formResponseItem.comparator.configuration.default";

    // Variables
    private final String _strSortAttributeName;
    private final int _nCellPosition;
    private final int _nSortDirection;

    /**
     * Constructor
     * 
     * @param formResponseItemComparatorConfig
     *            The FormResponseItemComparatorConfig to use for sort the FormResponseItem
     */
    public FormResponseItemComparator( FormResponseItemComparatorConfig formResponseItemComparatorConfig )
    {
        if ( formResponseItemComparatorConfig != null && formResponseItemComparatorConfig.getColumnToSortPosition( ) != NumberUtils.INTEGER_MINUS_ONE )
        {
            _strSortAttributeName = formResponseItemComparatorConfig.getSortAttributeName( );
            _nCellPosition = formResponseItemComparatorConfig.getColumnToSortPosition( );
            _nSortDirection = computeSortDirection( formResponseItemComparatorConfig.isAscSort( ) );
        }
        else
        {
            FormResponseItemComparatorConfig formResponseItemComparatorConfigDefault = SpringContextService.getBean( DEFAULT_CONFIGURATION_BEAN_NAME );
            _strSortAttributeName = formResponseItemComparatorConfigDefault.getSortAttributeName( );
            _nCellPosition = formResponseItemComparatorConfigDefault.getColumnToSortPosition( );
            _nSortDirection = computeSortDirection( formResponseItemComparatorConfigDefault.isAscSort( ) );
        }
    }

    /**
     * Constructor
     * 
     * @param formResponseItemComparatorConfig
     *            The FormResponseItemComparatorConfig to use for sort the FormResponseItem
     * @param formResponseItemComparatorConfigDefault
     *            The FormResponseItemComparatorConfig to use as default configuration if the given FormResponseItemComparatorConfig doesn't have all the
     *            necessaries information
     */
    public FormResponseItemComparator( FormResponseItemComparatorConfig formResponseItemComparatorConfig,
            FormResponseItemComparatorConfig formResponseItemComparatorConfigDefault )
    {
        if ( formResponseItemComparatorConfig != null && formResponseItemComparatorConfig.getColumnToSortPosition( ) != NumberUtils.INTEGER_MINUS_ONE )
        {
            _strSortAttributeName = formResponseItemComparatorConfig.getSortAttributeName( );
            _nCellPosition = formResponseItemComparatorConfig.getColumnToSortPosition( );
            _nSortDirection = computeSortDirection( formResponseItemComparatorConfig.isAscSort( ) );
        }
        else
        {
            _strSortAttributeName = formResponseItemComparatorConfigDefault.getSortAttributeName( );
            _nCellPosition = formResponseItemComparatorConfigDefault.getColumnToSortPosition( );
            _nSortDirection = computeSortDirection( formResponseItemComparatorConfigDefault.isAscSort( ) );
        }
    }

    /**
     * Compute the sort direction modifier from the given sort direction
     * 
     * @param bSortAsc
     *            The boolean which tell if we use the ascendant or descendant sort
     * @return 1 for the ascendant sort (which is the default) or -1 for the descendant sort
     */
    private int computeSortDirection( boolean bSortAsc )
    {
        int nSortDirection = SORT_ASCENDANT_DIRECTION;

        if ( !bSortAsc )
        {
            nSortDirection = SORT_DESCENDANT_DIRECTION;
        }

        return nSortDirection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare( FormResponseItem formResponseItemOne, FormResponseItem formResponseItemTwo )
    {
        int nComparisonResult = NumberUtils.INTEGER_ZERO;

        if ( formResponseItemOne == null )
        {
            if ( formResponseItemTwo != null )
            {
                nComparisonResult = NumberUtils.INTEGER_MINUS_ONE;
            }
        }
        else
        {
            if ( formResponseItemTwo == null )
            {
                nComparisonResult = NumberUtils.INTEGER_ONE;
            }
            else
            {
                List<FormColumnCell> listFormColumnCellOne = formResponseItemOne.getFormColumnCellValues( );
                List<FormColumnCell> listFormColumnCellTwo = formResponseItemTwo.getFormColumnCellValues( );

                nComparisonResult = compareFormColumnCellList( listFormColumnCellOne, listFormColumnCellTwo );
            }
        }

        return ( _nSortDirection * nComparisonResult );
    }

    /**
     * Compare the FormColumnCell list with the specified key
     * 
     * @param listFormColumnCellOne
     *            The first list of FormColumnCell to compare
     * @param listFormColumnCellTwo
     *            The second list of FormColumnCell to compare
     * @return the result of the comparison
     */
    private int compareFormColumnCellList( List<FormColumnCell> listFormColumnCellOne, List<FormColumnCell> listFormColumnCellTwo )
    {
        int nFormColumnComparisonResult = NumberUtils.INTEGER_ZERO;

        if ( listFormColumnCellOne == null )
        {
            if ( listFormColumnCellTwo != null )
            {
                nFormColumnComparisonResult = NumberUtils.INTEGER_MINUS_ONE;
            }
        }
        else
        {
            if ( listFormColumnCellTwo == null )
            {
                nFormColumnComparisonResult = NumberUtils.INTEGER_ONE;
            }
            else
            {
                int nlistFormColumnCellOneSize = listFormColumnCellOne.size( );
                int nlistFormColumnCellTwoSize = listFormColumnCellTwo.size( );
                int nFormColumnCellPosition = _nCellPosition - 1;

                if ( nlistFormColumnCellOneSize == nlistFormColumnCellTwoSize && nlistFormColumnCellTwoSize > nFormColumnCellPosition
                        && nFormColumnCellPosition > -1 )
                {
                    FormColumnCell formColumnCellOne = listFormColumnCellOne.get( nFormColumnCellPosition );
                    FormColumnCell formColumnCellTwo = listFormColumnCellTwo.get( nFormColumnCellPosition );

                    FormColumnCellComparator formColumnCellComparator = new FormColumnCellComparator( _strSortAttributeName );
                    nFormColumnComparisonResult = formColumnCellComparator.compare( formColumnCellOne, formColumnCellTwo );
                }
            }
        }

        return nFormColumnComparisonResult;
    }
}
