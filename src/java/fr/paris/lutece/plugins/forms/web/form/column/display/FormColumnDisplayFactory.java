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
package fr.paris.lutece.plugins.forms.web.form.column.display;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.web.form.column.display.factory.FormColumnDisplayFactoryFacade;
import fr.paris.lutece.plugins.forms.web.form.column.display.factory.IFormColumnDisplayFactory;
import fr.paris.lutece.plugins.forms.web.form.multiview.util.FormListPositionComparator;
import java.util.Collection;

/**
 * Factory for FormColumnDisplay objects
 */
public class FormColumnDisplayFactory
{
    /**
     * Create the list of all FormColumnDisplay ordered by their position
     * 
     * @param listFormColumn
     *            The list of IFormColumn to use for build the list of FormColumnDisplay
     * @return the list of all FormColumnDisplay ordered by their position
     */
    public List<IFormColumnDisplay> createFormColumnDisplayList( Collection<IFormColumn> listFormColumn )
    {
        List<IFormColumnDisplay> listFormColumnDisplay = new ArrayList<>( );
        List<IFormColumnDisplayFactory> listFormColumnDisplayFactory = new FormColumnDisplayFactoryFacade( ).buildFormColumnDisplayFactoryList( );

        if ( !CollectionUtils.isEmpty( listFormColumn ) && !CollectionUtils.isEmpty( listFormColumnDisplayFactory ) )
        {
            for ( IFormColumn formColumn : listFormColumn )
            {
                IFormColumnDisplay formColumnDisplay = null;
                for ( IFormColumnDisplayFactory formColumnDisplayFactory : listFormColumnDisplayFactory )
                {
                    formColumnDisplay = formColumnDisplayFactory.buildFormColumnDisplay( formColumn );
                    if ( formColumnDisplay != null )
                    {
                        formColumnDisplay.setPosition( formColumn.getFormColumnPosition( ) );
                        listFormColumnDisplay.add( formColumnDisplay );
                        break;
                    }
                }
            }

            // Sort the list by the position of each elements
            Collections.sort( listFormColumnDisplay, new FormListPositionComparator( ) );
        }

        return listFormColumnDisplay;
    }
}
