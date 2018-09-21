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
package fr.paris.lutece.plugins.forms.export;

import java.util.List;

import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

/**
 * This is the manager class for different entry type
 */
public final class ExportServiceManager
{
    private final List<IFormatExport> _listFormatExport;

    /**
     * Constructor for EntryServiceManager class
     */
    private ExportServiceManager( )
    {
        _listFormatExport = SpringContextService.getBeansOfType( IFormatExport.class );
    }

    /**
     * Gives the instance
     * 
     * @return the instance
     */
    public static ExportServiceManager getInstance( )
    {
        return EntryServiceManagerHolder._instance;
    }

    /**
     * Get the right IValidator
     * 
     * @param strFormatExport
     *            The format export name
     * @return the IValidator
     */
    public IFormatExport getFormatExport( String strFormatExport )
    {
        for ( IFormatExport formatExport : _listFormatExport )
        {
            if ( strFormatExport.equals( formatExport.getFormatExportBeanName( ) ) )
            {
                return formatExport;
            }
        }

        return null;
    }

    /**
     * Get the available IValidator list for the given entryType
     * 
     * @return the ReferenceList of IFormatExport
     */
    public ReferenceList getRefListFormatExport( )
    {
        ReferenceList refListFormatExport = new ReferenceList( );

        for ( IFormatExport formatExport : _listFormatExport )
        {
            refListFormatExport.addItem( formatExport.getFormatExportBeanName( ), formatExport.getFormatExportDisplayName( ) );
        }

        return refListFormatExport;
    }

    /**
     * This class holds the EntryServiceManager instance
     *
     */
    private static class EntryServiceManagerHolder
    {
        private static ExportServiceManager _instance = new ExportServiceManager( );
    }
}
