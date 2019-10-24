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
package fr.paris.lutece.plugins.forms.service;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.forms.validation.IValidator;
import fr.paris.lutece.plugins.forms.web.entrytype.IEntryDataService;
import fr.paris.lutece.plugins.forms.web.entrytype.IEntryDisplayService;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

/**
 * This is the manager class for different entry type
 */
public final class EntryServiceManager
{
    private final List<IEntryDisplayService> _listEntryDisplayService;

    private final List<IEntryDataService> _listEntryDataService;

    private final List<IValidator> _listValidator;

    /**
     * Constructor for EntryServiceManager class
     */
    private EntryServiceManager( )
    {
        _listEntryDisplayService = SpringContextService.getBeansOfType( IEntryDisplayService.class );
        _listEntryDataService = SpringContextService.getBeansOfType( IEntryDataService.class );
        _listValidator = SpringContextService.getBeansOfType( IValidator.class );
    }

    /**
     * Gives the instance
     * 
     * @return the instance
     */
    public static EntryServiceManager getInstance( )
    {
        return EntryServiceManagerHolder._instance;
    }

    /**
     * Get the right IEntryDisplayService
     * 
     * @param entryType
     *            The entrytype
     * @return the IEntryDisplayService
     */
    public IEntryDisplayService getEntryDisplayService( EntryType entryType )
    {
        for ( IEntryDisplayService entryDisplayServiceTemp : _listEntryDisplayService )
        {
            if ( entryType.getBeanName( ).equals( entryDisplayServiceTemp.getDisplayServiceName( ) ) )
            {
                return entryDisplayServiceTemp;
            }
        }

        return null;
    }

    /**
     * Get the right IEntryDataService
     * 
     * @param entryType
     *            The entrytype
     * @return the IEntryDataService
     */
    public IEntryDataService getEntryDataService( EntryType entryType )
    {
        for ( IEntryDataService entryDataServiceTemp : _listEntryDataService )
        {
            if ( entryType.getBeanName( ).equals( entryDataServiceTemp.getDataServiceName( ) ) )
            {
                return entryDataServiceTemp;
            }
        }

        return null;
    }

    /**
     * Get the right IValidator
     * 
     * @param strValidatorName
     *            The validator name
     * @return the IValidator
     */
    public IValidator getValidator( String strValidatorName )
    {
        for ( IValidator validator : _listValidator )
        {
            if ( strValidatorName.equals( validator.getValidatorBeanName( ) ) )
            {
                return validator;
            }
        }

        return null;
    }

    /**
     * Get the available IValidator list for the given entryType
     * 
     * @param entryType
     *            The entrytype
     * @return the ReferenceList of IValidator
     */
    public ReferenceList getRefListAvailableValidator( EntryType entryType )
    {
        ReferenceList refListAvailableValidator = new ReferenceList( );

        for ( IValidator validator : getListAvailableValidator( entryType ) )
        {
            refListAvailableValidator.addItem( validator.getValidatorBeanName( ), validator.getValidatorDisplayName( ) );
        }

        return refListAvailableValidator;
    }

    /**
     * Get the available IValidator list for the given entryType
     * 
     * @param entryType
     *            The entrytype
     * @return the List of IValidator
     */
    public List<IValidator> getListAvailableValidator( EntryType entryType )
    {
        List<IValidator> listAvailableValidator = new ArrayList< >( );

        for ( IValidator validator : _listValidator )
        {
            if ( validator.getListAvailableEntryType( ).contains( entryType.getBeanName( ) ) )
            {
                listAvailableValidator.add( validator );
            }
        }

        return listAvailableValidator;
    }

    /**
     * This class holds the EntryServiceManager instance
     *
     */
    private static class EntryServiceManagerHolder
    {
        private static EntryServiceManager _instance = new EntryServiceManager( );
    }
}
