/*
 * Copyright (c) 2002-2022, City of Paris
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
package fr.paris.lutece.plugins.forms.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import java.util.List;

/**
 * This class provides instances management methods (create, find, ...) for FormCategory objects
 */
public final class FormCategoryHome
{
    // Static variable pointed at the DAO instance
    private static IFormCategoryDAO _dao = SpringContextService.getBean( "forms.formCategoryDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "forms" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private FormCategoryHome( )
    {
    }

    /**
     * Create an instance of the formCategory class
     * 
     * @param form
     *            The instance of the formCategory which contains the informations to store
     * @return The instance of formCategory which has been created with its primary key.
     */
    public static FormCategory create( FormCategory formCategory )
    {
        _dao.insert( formCategory, _plugin );

        return formCategory;
    }

    /**
     * Update of the formCategory which is specified in parameter
     * 
     * @param formCategory
     *            The instance of the formCategory which contains the data to store
     * @return The instance of the formCategory which has been updated
     */
    public static FormCategory update( FormCategory formCategory )
    {
        _dao.store( formCategory, _plugin );

        return formCategory;
    }

    /**
     * Remove the formCategory whose identifier is specified in parameter
     * 
     * @param nKey
     *            The formCategory Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of a formCategory whose identifier is specified in parameter
     * 
     * @param nKey
     *            The formCategory primary key
     * @return an instance of formCategory
     */
    public static FormCategory findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the formCategory objects and returns them as a list
     * 
     * @return the list which contains the data of all the formCategory objects
     */
    public static List<FormCategory> getFormCategoryList( )
    {
        return _dao.selectFormCategoryList( _plugin );
    }

}
