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
package fr.paris.lutece.plugins.forms.business.form.column;

import java.util.Locale;

import fr.paris.lutece.plugins.forms.business.form.column.querypart.IFormColumnQueryPart;
import fr.paris.lutece.plugins.forms.web.form.column.display.IFormColumnDisplay;

/**
 * Interface for a FormColumn object
 */
public interface IFormColumn
{
    /**
     * Return the position of the FormColumn
     * 
     * @return the position of the FormColumn
     */
    int getFormColumnPosition( );

    /**
     * Set the position of the FormColumn
     * 
     * @param nPosition
     *            The position to set for the FormColumn
     */
    void setFormColumnPosition( int nPosition );

    /**
     * Return the title of the FormColumn which will be displayed on the table
     * 
     * @param the
     *            locale in which to display the title
     * @return the title of the FormColumn which will be displayed on the table
     */
    String getFormColumnTitle( Locale locale );

    /**
     * Set the title of the FormColumn which will be displayed on the table
     * 
     * @param strFormColumnTitle
     *            The title to set to the FormColumn which will be displayed on the table
     */
    void setFormColumnTitle( String strFormColumnTitle );

    /**
     * Determines if the column must be displayed.
     * 
     * @return
     */
    default boolean isDisplayed( )
    {
        return true;
    }

    /**
     * Get an instance of an {@link IFormColumnQueryPart} associated to the FormColumn.
     * 
     * @return
     */
    IFormColumnQueryPart getFormColumnQueryPart( );

    /**
     * Get an instance of an {@link IFormColumnDisplay} associated to th FormColumn.
     * 
     * @return
     */
    IFormColumnDisplay getFormColumnDisplay( );
}
