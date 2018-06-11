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
package fr.paris.lutece.plugins.forms.business.form.column.querypart;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.column.querypart.factory.IFormColumnQueryPartFactory;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * Facade for a FormColumn to build its request
 */
public class FormColumnQueryPartFacade
{
    // Variables
    private final List<IFormColumnQueryPartFactory> _listFormColumnQueryPartFactory;

    /**
     * Constructor
     */
    public FormColumnQueryPartFacade( )
    {
        _listFormColumnQueryPartFactory = SpringContextService.getBeansOfType( IFormColumnQueryPartFactory.class );
    }

    /**
     * Constructor
     * 
     * @param listFormColumnQueryPartFactory
     *            The list of IFormColumnQueryPartFactory to use for the facade
     */
    public FormColumnQueryPartFacade( List<IFormColumnQueryPartFactory> listFormColumnQueryPartFactory )
    {
        _listFormColumnQueryPartFactory = listFormColumnQueryPartFactory;
    }

    /**
     * Return the appropriate QueryPart for the given FormColumn
     * 
     * @param formColumn
     *            The FormColumn to retrieve the associated QueryPart
     * @return the IFormColumnQueryPart linked to the given column or null if not found
     */
    public IFormColumnQueryPart getFormFilterQueryPart( IFormColumn formColumn )
    {
        IFormColumnQueryPart formColumnQueryPart = null;

        if ( formColumn != null && !CollectionUtils.isEmpty( _listFormColumnQueryPartFactory ) )
        {
            for ( IFormColumnQueryPartFactory formColumnQueryPartFactory : _listFormColumnQueryPartFactory )
            {
                formColumnQueryPart = formColumnQueryPartFactory.buildFormColumnQueryPart( formColumn );
                if ( formColumnQueryPart != null )
                {
                    break;
                }
            }
        }

        return formColumnQueryPart;
    }
}
