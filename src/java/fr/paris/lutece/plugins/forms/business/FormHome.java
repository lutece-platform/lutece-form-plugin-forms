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

import fr.paris.lutece.plugins.forms.business.form.FormItemSortConfig;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;

/**
 * This class provides instances management methods (create, find, ...) for Form objects
 */
public final class FormHome
{
    // Static variable pointed at the DAO instance
    private static IFormDAO _dao = SpringContextService.getBean( "forms.formDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "forms" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private FormHome( )
    {
    }

    /**
     * Create an instance of the form class
     * 
     * @param form
     *            The instance of the Form which contains the informations to store
     * @return The instance of form which has been created with its primary key.
     */
    public static Form create( Form form )
    {
        _dao.insert( form, _plugin );

        return form;
    }

    /**
     * Update of the form which is specified in parameter
     * 
     * @param form
     *            The instance of the Form which contains the data to store
     * @return The instance of the form which has been updated
     */
    public static Form update( Form form )
    {
        _dao.store( form, _plugin );

        return form;
    }

    /**
     * Remove the form whose identifier is specified in parameter
     * 
     * @param nKey
     *            The form Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of a form whose identifier is specified in parameter
     * 
     * @param nKey
     *            The form primary key
     * @return an instance of Form
     */
    public static Form findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the form objects and returns them as a list
     * 
     * @return the list which contains the data of all the form objects
     */
    public static List<Form> getFormList( )
    {
        return _dao.selectFormsList( _plugin );
    }
    
    /**
     * Load the data of all the form objects and returns them as a list
     * 
     * @return the list which contains the data of all the form objects
     */
    public static List<Form> getFormListSorted( FormItemSortConfig sortConfig )
    {
        return _dao.selectFormsListSorted( _plugin, sortConfig );
    }

    /**
     * Load the data of all the form objects and returns them as a referenceList
     * 
     * @return the referenceList which contains the data of all the form objects
     */
    public static ReferenceList getFormsReferenceList( )
    {
        return _dao.selectFormsReferenceList( _plugin );
    }

    /**
     * count the number of response for a form
     * 
     * @param nIdFormm
     * @return the number of response for a form
     */
    public static int getNumberOfResponseForms( int nIdFormm )
    {
        return _dao.countNumberOfResponseForms( nIdFormm );
    }

    /**
     * count the number of response for form
     *
     * @param nIdForm
     *            id of form
     * @param strGuid
     *            the name of user
     * @return the number of response for form corresponding of user
     */
    public static int getNumberOfResponseFormByUser( int nIdForm, String strGuid )
    {
        return _dao.countNumberOfResponseFormByUser( nIdForm, strGuid );
    }

    /**
     * Load the data of all form Objects for a given list of form identifiers
     * 
     * @param listIdForm
     *            The list of form identifiers
     * @return the form objects
     */
    public static List<Form> getFormByPrimaryKeyList( List<Integer> listIdForm )
    {
        return _dao.selectFormByPrimaryKeyList( listIdForm, _plugin );
    }
}
