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
package fr.paris.lutece.plugins.forms.business.form.column;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * Comparator for FormColumnCell objects
 */
public class FormColumnCellComparator implements Comparator<FormColumnCell>, Serializable
{
    /**
     * Generated serial UID
     */
    private static final long serialVersionUID = 3458483480534971093L;
    
    // Variables
    private final String _strSortKey;

    /**
     * Constructor
     * 
     * @param strSortKey
     *            The key used to sort the FormColumnCell
     */
    public FormColumnCellComparator( String strSortKey )
    {
        _strSortKey = strSortKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare( FormColumnCell formColumnCellOne, FormColumnCell formColumnCellTwo )
    {
        int nComparisonResult = NumberUtils.INTEGER_ZERO;

        if ( formColumnCellOne == null )
        {
            if ( formColumnCellTwo != null )
            {
                nComparisonResult = NumberUtils.INTEGER_MINUS_ONE;
            }
        }
        else
        {
            if ( formColumnCellTwo == null )
            {
                nComparisonResult = NumberUtils.INTEGER_ONE;
            }
            else
            {
                Object objectOne = formColumnCellOne.getFormColumnCellValueByName( _strSortKey );
                Object objectTwo = formColumnCellTwo.getFormColumnCellValueByName( _strSortKey );

                nComparisonResult = compareFormColumnCellObject( objectOne, objectTwo );
            }
        }

        return nComparisonResult;
    }

    /**
     * Compare two FormColumnCell object values by converting them as String
     * 
     * @param objectOne
     *            The first object to compare
     * @param objectTwo
     *            The second object to compare
     * @return the result of the comparison of the two given objects
     */
    private int compareFormColumnCellObject( Object objectOne, Object objectTwo )
    {
        int nComparisonResult = NumberUtils.INTEGER_ZERO;

        if ( objectOne == null )
        {
            if ( objectTwo != null )
            {
                nComparisonResult = NumberUtils.INTEGER_MINUS_ONE;
            }
        }
        else
        {
            if ( objectTwo == null )
            {
                nComparisonResult = NumberUtils.INTEGER_ONE;
            }
            else
            {
                nComparisonResult = compareObject( objectOne, objectTwo );
            }
        }

        return nComparisonResult;
    }

    /**
     * Make the comparison between the two given object. If there are both Date a comparison will be make on the two Dates otherwise it will be 
     * their comparison as String which will be made
     * 
     * @param objectOne
     *            The first object to compare
     * @param objectTwo
     *            The second object to compare
     * @return the result of the comparison between the two given objects
     */
    private int compareObject( Object objectOne, Object objectTwo )
    {
        int nComparisonResult = NumberUtils.INTEGER_ZERO;

        if ( objectOne instanceof Date && objectTwo instanceof Date )
        {
            Date dateOne = (Date) objectOne;
            Date dateTwo = (Date) objectTwo;

            nComparisonResult = dateOne.compareTo( dateTwo );
        }
        else
        {
            String strObjectOneRepresentation = String.valueOf( objectOne );
            String strObjectTwoRepresentation = String.valueOf( objectTwo );

            nComparisonResult = strObjectOneRepresentation.compareTo( strObjectTwoRepresentation );
        }

        return nComparisonResult;
    }
}
