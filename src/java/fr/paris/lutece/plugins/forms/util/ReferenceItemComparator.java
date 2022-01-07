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
package fr.paris.lutece.plugins.forms.util;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.util.ReferenceItem;

/**
 * Comparator for ReferenceItem
 */
public class ReferenceItemComparator implements Comparator<ReferenceItem>, Serializable
{
    /**
     * Generated serial UID
     */
    private static final long serialVersionUID = 5041550030882422319L;

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare( ReferenceItem referenceItemOne, ReferenceItem referenceItemTwo )
    {
        int nComparisonResult;

        if ( referenceItemOne != null )
        {
            if ( referenceItemTwo != null )
            {
                String strReferenceItemNameOne = referenceItemOne.getName( );
                String strReferenceItemNameTwo = referenceItemTwo.getName( );

                nComparisonResult = compareReferenceItemName( strReferenceItemNameOne, strReferenceItemNameTwo );
            }
            else
            {
                nComparisonResult = NumberUtils.INTEGER_ONE;
            }
        }
        else
        {
            if ( referenceItemTwo != null )
            {
                nComparisonResult = NumberUtils.INTEGER_MINUS_ONE;
            }
            else
            {
                nComparisonResult = NumberUtils.INTEGER_ZERO;
            }
        }

        return nComparisonResult;
    }

    /**
     * Make the comparison between the two given ReferenceItem names
     * 
     * @param strReferenceItemNameOne
     *            The first name of the ReferenceItem to compare
     * @param strReferenceItemNameTwo
     *            The second name of the ReferenceItem to compare
     * @return the comparison between the two given ReferenceItem names
     */
    private int compareReferenceItemName( String strReferenceItemNameOne, String strReferenceItemNameTwo )
    {
        int nComparisonResult = NumberUtils.INTEGER_ZERO;

        if ( strReferenceItemNameOne == null )
        {
            if ( strReferenceItemNameTwo != null )
            {
                nComparisonResult = NumberUtils.INTEGER_MINUS_ONE;
            }
        }
        else
        {
            if ( strReferenceItemNameTwo == null )
            {
                nComparisonResult = NumberUtils.INTEGER_ONE;
            }
            else
            {
                nComparisonResult = strReferenceItemNameOne.compareTo( strReferenceItemNameTwo );
            }
        }

        return nComparisonResult;
    }
}
