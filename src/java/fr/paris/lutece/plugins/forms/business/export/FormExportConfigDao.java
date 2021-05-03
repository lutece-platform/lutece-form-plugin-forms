package fr.paris.lutece.plugins.forms.business.export;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

public class FormExportConfigDao implements IFormExportConfigDao
{
    private static final String SQL_SELECT_ALL = "SELECT id, id_form, field, display_order FROM  forms_export_config ";
    private static final String SQL_SELECT = SQL_SELECT_ALL + " WHERE id = ? ";
    private static final String SQL_UPDATE = "UPDATE forms_export_config SET id = ?, id_form = ?, field = ?, display_order = ? WHERE id = ? ";
    private static final String SQL_INSERT = "INSERT INTO forms_export_config ( id_form, field, display_order ) VALUES ( ?, ?, ? ) ";
    private static final String SQL_DELETE_BY_FORM = "DELETE FROM forms_export_config WHERE id_form = ? ";
    private static final String SQL_SELECT_BY_FORM = SQL_SELECT_ALL + " WHERE id_form = ? ORDER BY display_order ";

    @Override
    public void insert( FormExportConfig config, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int index = 0 ;
            daoUtil.setInt( ++index, config.getIdForm( ) );
            daoUtil.setString( ++index, config.getField( ) );
            daoUtil.setInt( ++index, config.getOrder( ) );
            
            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                config.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }
    
    @Override
    public void store( FormExportConfig config, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_UPDATE, plugin ) )
        {
            int index = 0 ;
            daoUtil.setInt( ++index, config.getId( ) );
            daoUtil.setInt( ++index, config.getIdForm( ) );
            daoUtil.setString( ++index, config.getField( ) );
            daoUtil.setInt( ++index, config.getOrder( ) );
            
            daoUtil.setInt( ++index, config.getId( ) );
            
            daoUtil.executeUpdate( );
        }
    }
    
    @Override
    public FormExportConfig load( int nKey, Plugin plugin )
    {
        FormExportConfig config = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );
            
            if ( daoUtil.next( ) )
            {
                config = dataToObject( daoUtil );
            }
        }
        return config;
    }
    
    @Override
    public void deleteByForm( int idForm, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_DELETE_BY_FORM, plugin ) )
        {
            daoUtil.setInt( 1, idForm );
            daoUtil.executeUpdate( );
        }
        
    }
    
    @Override
    public List<FormExportConfig> loadByForm( int idForm, Plugin plugin )
    {
        List<FormExportConfig> configList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_SELECT_BY_FORM, plugin ) )
        {
            daoUtil.setInt( 1, idForm );
            daoUtil.executeQuery( );
            
            while ( daoUtil.next( ) )
            {
                configList.add( dataToObject( daoUtil ) );
            }
        }
        return configList;
    }
    
    private FormExportConfig dataToObject( DAOUtil daoUtil )
    {
        FormExportConfig config = new FormExportConfig( );
        
        int index = 0;
        config.setId( daoUtil.getInt( ++index ) );
        config.setIdForm( daoUtil.getInt( ++index ) );
        config.setField( daoUtil.getString( ++index ) );
        config.setOrder( daoUtil.getInt( ++index ) );
        
        return config;
    }
}
