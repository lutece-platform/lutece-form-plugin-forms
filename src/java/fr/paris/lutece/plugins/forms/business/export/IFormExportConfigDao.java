package fr.paris.lutece.plugins.forms.business.export;

import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;

public interface IFormExportConfigDao
{
    /**
     * Insert a new record in the table.
     * 
     * @param config
     *            instance of FormExportConfig object to insert
     * @param plugin
     *            the Plugin
     */
    void insert( FormExportConfig config, Plugin plugin );
    
    /**
     * Delete all record from the table
     * 
     * @param idForm
     *            The identifier of the form
     * @param plugin
     *            the Plugin
     */
    void deleteByForm( int idForm, Plugin plugin );
    
    /**
     * Load all configs for a form
     * @param idForm
     * @param plugin
     * @return
     */
    List<FormExportConfig> loadByForm( int idForm, Plugin plugin );
    
    /**
     * Load the data from the table
     * 
     * @param nKey
     *            The identifier of the config
     * @param plugin
     *            the Plugin
     * @return The instance of the form
     */
    FormExportConfig load( int nKey, Plugin plugin );
    
    /**
     * Update the record in the table
     * 
     * @param config
     *            the reference of the FormExportConfig
     * @param plugin
     *            the Plugin
     */
    void store( FormExportConfig config, Plugin plugin );
}
