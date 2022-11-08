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
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class provides Data Access methods for ControlGroup objects
 */
public final class ControlGroupDAO implements IControlGroupDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_control_group, logical_operator FROM forms_control_group WHERE id_control_group = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO forms_control_group ( logical_operator ) VALUES ( ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM forms_control_group WHERE id_control_group = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE forms_control_group SET id_control_group = ?, logical_operator = ? WHERE id_control_group = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_control_group, logical_operator FROM forms_control_group";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_control_group FROM forms_control_group";
    private static final String SQL_QUERY_SELECTALL_BY_IDS = "SELECT id_control_group, logical_operator FROM forms_control_group WHERE id_control_group IN (  ";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( ControlGroup controlGroup, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++ , controlGroup.getLogicalOperator( ) );
            
            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) ) 
            {
                controlGroup.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
        
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<ControlGroup> load( int nKey, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
	        daoUtil.setInt( 1 , nKey );
	        daoUtil.executeQuery( );
	        ControlGroup controlGroup = null;
	
	        if ( daoUtil.next( ) )
	        {
	            controlGroup = new ControlGroup();
	            int nIndex = 1;
	            
	            controlGroup.setId( daoUtil.getInt( nIndex++ ) );
			    controlGroup.setLogicalOperator( daoUtil.getString( nIndex ) );
	        }
	
	        return Optional.ofNullable( controlGroup );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nKey, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
	        daoUtil.setInt( 1 , nKey );
	        daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( ControlGroup controlGroup, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
	        int nIndex = 1;
	        
	        daoUtil.setInt( nIndex++ , controlGroup.getId( ) );
            	daoUtil.setString( nIndex++ , controlGroup.getLogicalOperator( ) );
	        daoUtil.setInt( nIndex , controlGroup.getId( ) );
	
	        daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<ControlGroup> selectControlGroupsList( Plugin plugin )
    {
        List<ControlGroup> controlGroupList = new ArrayList<>(  );
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
	            ControlGroup controlGroup = new ControlGroup(  );
	            int nIndex = 1;
	            
	            controlGroup.setId( daoUtil.getInt( nIndex++ ) );
			    controlGroup.setLogicalOperator( daoUtil.getString( nIndex ) );
	
	            controlGroupList.add( controlGroup );
	        }
	
	        return controlGroupList;
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdControlGroupsList( Plugin plugin )
    {
        List<Integer> controlGroupList = new ArrayList<>( );
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
	            controlGroupList.add( daoUtil.getInt( 1 ) );
	        }
	
	        return controlGroupList;
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectControlGroupsReferenceList( Plugin plugin )
    {
        ReferenceList controlGroupList = new ReferenceList();
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
	        daoUtil.executeQuery(  );
	
	        while ( daoUtil.next(  ) )
	        {
	            controlGroupList.addItem( daoUtil.getInt( 1 ) , daoUtil.getString( 2 ) );
	        }
	
	        return controlGroupList;
    	}
    }
    
    @Override
    public ReferenceList selectLogicalOperatorsReferenceList() {
    	ReferenceList logicalOperatorsList = new ReferenceList();
    	logicalOperatorsList.addItem(LogicalOperator.AND.getLabel(), "et logique");
    	logicalOperatorsList.addItem(LogicalOperator.OR.getLabel(), "ou logique");
    	return logicalOperatorsList;
    }
    
    /**
     * {@inheritDoc }
     */
	@Override
	public List<ControlGroup> selectControlGroupsListByIds( Plugin plugin, List<Integer> listIds ) {
		List<ControlGroup> controlGroupList = new ArrayList<>(  );
		
		StringBuilder builder = new StringBuilder( );

		if ( !listIds.isEmpty( ) )
		{
			for( int i = 0 ; i < listIds.size(); i++ ) {
			    builder.append( "?," );
			}
	
			String placeHolders =  builder.deleteCharAt( builder.length( ) -1 ).toString( );
			String stmt = SQL_QUERY_SELECTALL_BY_IDS + placeHolders + ")";
			
			
	        try ( DAOUtil daoUtil = new DAOUtil( stmt, plugin ) )
	        {
	        	int index = 1;
				for( Integer n : listIds ) {
					daoUtil.setInt(  index++, n ); 
				}
	        	
	        	daoUtil.executeQuery(  );
	        	while ( daoUtil.next(  ) )
		        {
		        	ControlGroup controlGroup = new ControlGroup(  );
		            int nIndex = 1;
		            
		            controlGroup.setId( daoUtil.getInt( nIndex++ ) );
				    controlGroup.setLogicalOperator( daoUtil.getString( nIndex ) );
		            
		            controlGroupList.add( controlGroup );
		        }
		
		        daoUtil.free( );
		        
	        }
	    }
		return controlGroupList;
		
	}
}
