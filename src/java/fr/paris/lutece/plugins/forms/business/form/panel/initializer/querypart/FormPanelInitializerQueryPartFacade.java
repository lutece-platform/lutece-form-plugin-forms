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
package fr.paris.lutece.plugins.forms.business.form.panel.initializer.querypart;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.plugins.forms.business.form.panel.initializer.IFormPanelInitializer;
import fr.paris.lutece.plugins.forms.business.form.panel.initializer.querypart.factory.IFormPanelInitializerQueryPartFactory;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * Facade for the FormPanelInitializerQueryPart objects
 */
public class FormPanelInitializerQueryPartFacade
{
    // Variables
    private final List<IFormPanelInitializerQueryPartFactory> _listFormPanelInitializerQueryPartFactory;

    /**
     * Constructor
     */
    public FormPanelInitializerQueryPartFacade( )
    {
        _listFormPanelInitializerQueryPartFactory = SpringContextService.getBeansOfType( IFormPanelInitializerQueryPartFactory.class );
    }

    /**
     * Constructor
     * 
     * @param listFormPanelInitializerQueryPartFactory
     *            The list of IFormPanelInitializerQueryPartFactory to use for the Facade
     */
    public FormPanelInitializerQueryPartFacade( List<IFormPanelInitializerQueryPartFactory> listFormPanelInitializerQueryPartFactory )
    {
        _listFormPanelInitializerQueryPartFactory = listFormPanelInitializerQueryPartFactory;
    }

    /**
     * Retrieve the IFormPanelInitializerQueryPart associate to the given FormPanelInitializer
     * 
     * @param formPanelInitializer
     *            The FormPanelInitializer to retrieve the FormPanelInitializerQueryPart associated
     * @return the IFormPanelInitializerQueryPart associate to the given FormPanelInitializer or null if not found
     */
    public IFormPanelInitializerQueryPart getFormPanelInitializerQueryPart( IFormPanelInitializer formPanelInitializer )
    {
        IFormPanelInitializerQueryPart formPanelInitializerQueryPart = null;

        if ( formPanelInitializer != null && !CollectionUtils.isEmpty( _listFormPanelInitializerQueryPartFactory ) )
        {
            for ( IFormPanelInitializerQueryPartFactory formPanelInitializerQueryPartFactory : _listFormPanelInitializerQueryPartFactory )
            {
                formPanelInitializerQueryPart = formPanelInitializerQueryPartFactory.buildFormPanelInitializerQueryPart( formPanelInitializer );

                if ( formPanelInitializerQueryPart != null )
                {
                    break;
                }
            }
        }

        return formPanelInitializerQueryPart;
    }
}
