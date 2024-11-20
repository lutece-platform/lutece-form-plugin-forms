/*
 * Copyright (c) 2002-2024, City of Paris
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
 *     and the following disclaimer in the Formsation and/or other materials
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
package fr.paris.lutece.plugins.forms.business.portlet;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.util.sql.DAOUtil;

public class FormsListPortletDAO implements IFormsListPortletDAO
{
    private static final String SQL_QUERY_SELECT = "SELECT id_form FROM forms_list_portlet WHERE id_portlet = ? ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO forms_list_portlet ( id_portlet, id_form ) VALUES ( ?, ? )";
    private static final String SQL_QUERY_DELETE = "DELETE FROM forms_list_portlet WHERE id_portlet = ? ";

    /**
     * { @inheritDoc }
     */
    @Override
    public void insert( Portlet portlet )
    {
    	FormsListPortlet formsListPortlet = (FormsListPortlet) portlet;
    	
    	if ( CollectionUtils.isNotEmpty( formsListPortlet.getFormsIdList( ) ) )
    	{
    	    for (Integer formId : formsListPortlet.getFormsIdList( ) )
    	    {
    	    	try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT ) )
    	        {
    	            daoUtil.setInt( 1, formsListPortlet.getId( ) );
    	            daoUtil.setInt( 2, formId );

    	            daoUtil.executeUpdate( );
    	        }
    	    }
    	}
    }

    /**
     * { @inheritDoc }
     */
    @Override
    public Portlet load( int nPortletId )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT ) )
        {
            daoUtil.setInt( 1, nPortletId );
            daoUtil.executeQuery( );

            FormsListPortlet formsListPortlet = new FormsListPortlet( );
            formsListPortlet.setId( nPortletId );
            
            List<Integer> formsIdList = new ArrayList<>( );
            while ( daoUtil.next( ) )
            {
                formsIdList.add( daoUtil.getInt( 1 ) );
            }
            formsListPortlet.setFormsIdList( formsIdList );

            return formsListPortlet;
        }
    }

    /**
     * { @inheritDoc }
     */
    @Override
    public void delete( int nPortletId )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE ) )
        {
            daoUtil.setInt( 1, nPortletId );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * { @inheritDoc }
     */
    @Override
    public void store( Portlet portlet )
    {
        delete( portlet.getId( ) );
        insert( portlet );
    }
}
