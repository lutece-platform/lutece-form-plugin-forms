package fr.paris.lutece.plugins.forms.business.export;

import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

public class FormExportConfigHome
{
    private static IFormExportConfigDao _dao = SpringContextService.getBean( "forms.formExportConfigDao" );
    private static Plugin _plugin = PluginService.getPlugin( "forms" );
    
    private FormExportConfigHome( )
    {
    }
    
    /**
     * Create an instance of the FormExportConfig class
     * 
     * @param config
     *            The instance of the FormExportConfig which contains the informations to store
     * @return The instance of control which has been created with its primary key.
     */
    public static FormExportConfig create( FormExportConfig config )
    {
        _dao.insert( config, _plugin );
        return config;
    }
    
    /**
     * Update of the FormExportConfig which is specified in parameter
     * 
     * @param config
     *            The instance of the FormExportConfig which contains the data to store
     * @return The instance of the config which has been updated
     */
    public static FormExportConfig update( FormExportConfig config )
    {
        _dao.store( config, _plugin );
        return config;
    }
    
    /**
     * Returns an instance of a FormExportConfig whose identifier is specified in parameter
     * 
     * @param nKey
     *            The FormExportConfig primary key
     * @return an instance of Form
     */
    public static FormExportConfig findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }
    
    /**
     * Delete All config for a give form
     * @param idForm
     */
    public static void removeByForm( int idForm )
    {
        _dao.deleteByForm( idForm, _plugin );
    }
    
    /**
     * Find All config for a give form
     * @param idForm
     */
    public static List<FormExportConfig> findByForm( int idForm )
    {
       return _dao.loadByForm( idForm, _plugin );
    }
}
