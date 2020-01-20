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
package fr.paris.lutece.plugins.forms.web.form.column.display;

import java.util.Locale;

import fr.paris.lutece.plugins.forms.business.form.column.FormColumnCell;
import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.web.form.multiview.util.IFormListPosition;

/**
 * Interface for a FormColumnDisplay objects
 */
public interface IFormColumnDisplay extends IFormListPosition
{
    /**
     * Return the header template of the FormColumn
     * 
     * @return the header template of the FormColumn
     */
    String getFormColumnHeaderTemplate( );

    /**
     * Build the header template of the FormColumn
     * 
     * @param strSortUrl
     *            The url used to sort the column
     * @param locale
     *            The locale to use to build the template
     * @return the form column header template
     */
    String buildFormColumnHeaderTemplate( String strSortUrl, Locale locale );

    /**
     * Build the template of the given FormColumnCell for the FormColumn
     * 
     * @param formColumnCell
     *            The FormColumnCell to use for build the template of the FormColumnDisplay
     * @param locale
     *            The locale to use for build the template
     * @return the template of the given formColumnCell
     */
    String buildFormColumnCellTemplate( FormColumnCell formColumnCell, Locale locale );

    /**
     * Return the FormColumn object associated to the FormColumnDisplay
     * 
     * @return the FormColumn object associated to the FormColumnDisplay
     */
    IFormColumn getFormColumn( );

    /**
     * Set the FormColumn associated to the current FormColumnDisplay
     * 
     * @param formColumn
     *            The FormColumn to associate to the FormColumnDisplay
     */
    void setFormColumn( IFormColumn formColumn );
}
