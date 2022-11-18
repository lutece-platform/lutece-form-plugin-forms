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
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * SpaceActionDAO
 */
public class FormActionDAO implements IFormActionDAO
{
    private static final String SQL_QUERY_SELECT_ALL_ACTIONS = "SELECT a.name_key, a.description_key, a.action_url, a.icon_url, a.action_permission ,a.form_state"
            + " FROM forms_action a";
    private static final String SQL_QUERY_SELECT_ACTIONS = SQL_QUERY_SELECT_ALL_ACTIONS + " where a.form_state=? ";

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FormAction> selectActionsByFormState( int nState, Plugin plugin )
    {
        List<FormAction> listActions = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ACTIONS, plugin ) )
        {
            daoUtil.setInt( 1, nState );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                listActions.add( dataToObject( daoUtil ) );
            }
        }

        return listActions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FormAction> selectAllFormActions( Plugin plugin )
    {
        List<FormAction> listActions = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ALL_ACTIONS, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                listActions.add( dataToObject( daoUtil ) );
            }

        }
        return listActions;
    }

    /**
     * 
     * @param daoUtil
     *            The daoutil
     * @return The populated FormAction object
     */
    private FormAction dataToObject( DAOUtil daoUtil )
    {
        FormAction formAction = new FormAction( );

        formAction.setNameKey( daoUtil.getString( "name_key" ) );
        formAction.setDescriptionKey( daoUtil.getString( "description_key" ) );
        formAction.setUrl( daoUtil.getString( "action_url" ) );
        formAction.setIconUrl( daoUtil.getString( "icon_url" ) );
        formAction.setPermission( daoUtil.getString( "action_permission" ) );
        formAction.setFormState( daoUtil.getInt( "form_state" ) );

        return formAction;
    }
}
