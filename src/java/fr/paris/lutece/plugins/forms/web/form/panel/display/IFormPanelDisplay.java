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
package fr.paris.lutece.plugins.forms.web.form.panel.display;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import fr.paris.lutece.plugins.forms.business.form.FormResponseItem;
import fr.paris.lutece.plugins.forms.business.form.panel.FormPanel;
import fr.paris.lutece.plugins.forms.web.form.multiview.util.IFormListPosition;

/**
 * Interface for Filter of a FormPanelDisplay
 */
public interface IFormPanelDisplay extends IFormListPosition
{
    /**
     * Return the boolean which tell if the PanelDisplay is for the active Panel or not
     * 
     * @return the boolean which tell if the PanelDisplay is for the active Panel or not
     */
    boolean isActive( );

    /**
     * Set the boolean which tell if the PanelDisplay is for the active Panel or not
     * 
     * @param bActive
     *            The boolean which tell if the PanelDisplay is for the active Panel or not
     */
    void setActive( boolean bActive );

    /**
     * Return the technical code of the FormPanel
     * 
     * @return the technical code of the FormPanel
     */
    String getTechnicalCode( );

    /**
     * Return the form response number of the FormPanelDisplay
     * 
     * @return the form response number of the FormPanelDisplay
     */
    int getFormResponseNumber( );

    /**
     * Return the template of the FormPanelDisplay
     * 
     * @return the template of the FormPanelDisplay
     */
    String getTemplate( );

    /**
     * Return the formPanel of the FormPanelDisplay
     * 
     * @return the formPanel
     */
    FormPanel getFormPanel( );

    /**
     * Set the FormPanel of the FormPanelDisplay
     * 
     * @param formPanel
     *            The FormPanel of the FormPanelDisplay
     */
    void setFormPanel( FormPanel formPanel );

    /**
     * Return the list of FormItem of the FormPanelDisplay
     * 
     * @return the list of FormItem of the FormPanelDisplay
     */
    List<FormResponseItem> getFormResponseItemList( );
    
    /**
     * Build the template of the FormPanelDisplay
     * 
     * @param locale
     *            The locale used to build the template
     * @return the built template of the FormPanelDisplay
     * @deprecated Use buildTemplate( Map<String, Object>, Locale ) instead
     */
    @Deprecated
    String buildTemplate( Locale locale );

    /**
     * Build the template of the FormPanelDisplay
     * 
     * @param data
     *            The data used to build the template
     * @param locale
     *            The locale used to build the template
     * @return the built template of the FormPanelDisplay
     */
    default String buildTemplate( Map<String, Object> data, Locale locale )
    {
    	throw new UnsupportedOperationException( "The method buildTemplate( Map<String, Object>, Locale ) is not implemented yet." );
    }
}
