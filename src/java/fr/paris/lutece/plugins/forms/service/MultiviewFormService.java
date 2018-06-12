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
package fr.paris.lutece.plugins.forms.service;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.filter.IFormFilter;
import fr.paris.lutece.plugins.forms.business.form.list.FormListFacade;
import fr.paris.lutece.plugins.forms.business.form.panel.IFormPanel;
import fr.paris.lutece.plugins.forms.web.form.panel.display.IFormPanelDisplay;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * Service dedicated to managing of the multiview of forms
 */
public final class MultiviewFormService
{
    /**
     * Constructor
     */
    private MultiviewFormService( )
    {

    }

    /**
     * Return the singleton of the MultiviewFormService
     * 
     * @return the singleton of the MultiviewFormService
     */
    public static MultiviewFormService getInstance( )
    {
        return MultiviewFormServiceHolder._singleton;
    }

    /**
     * Populate the given FormPanel with the information from the given list of FormColumns and FormFilters
     * 
     * @param formPanel
     *            The FormPanel used to retrieve the values of the FormColumns
     * @param listFormColumn
     *            The list of all FormColumn to use to be populated
     * @param listFormFilter
     *            The list of FormFilter to use for retrieving the data of the columns to populate
     */
    public void populateFormColumns( IFormPanel formPanel, List<IFormColumn> listFormColumn, List<IFormFilter> listFormFilter )
    {
        FormListFacade formListFacade = SpringContextService.getBean( FormListFacade.BEAN_NAME );
        formListFacade.populateFormColumns( formPanel, listFormColumn, listFormFilter );
    }

    /**
     * Find the FormPanel which is active in the given list
     * 
     * @param listFormPanelDisplay
     *            The list to retrieve the active FormPanelDisplay
     * @return the IFormFilterPanelDisplay which is active or null if not found
     */
    public IFormPanelDisplay findActiveFormPanel( List<IFormPanelDisplay> listFormPanelDisplay )
    {
        IFormPanelDisplay formPanelDisplayActive = null;

        if ( !CollectionUtils.isEmpty( listFormPanelDisplay ) )
        {
            for ( IFormPanelDisplay formPanelDisplay : listFormPanelDisplay )
            {
                if ( formPanelDisplay.isActive( ) )
                {
                    formPanelDisplayActive = formPanelDisplay;
                    break;
                }
            }
        }

        return formPanelDisplayActive;
    }

    /**
     * Holder class which manage the singleton of the MultiviewFormService
     */
    private static class MultiviewFormServiceHolder
    {
        // Variables
        private static final MultiviewFormService _singleton = new MultiviewFormService( );
    }
}
