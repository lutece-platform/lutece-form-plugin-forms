package fr.paris.lutece.plugins.forms.service;

import java.util.List;
import java.util.Locale;

import fr.paris.lutece.plugins.forms.business.form.panel.configuration.IFormPanelConfiguration;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.rbac.Permission;
import fr.paris.lutece.portal.service.rbac.ResourceIdService;
import fr.paris.lutece.portal.service.rbac.ResourceType;
import fr.paris.lutece.portal.service.rbac.ResourceTypeManager;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

public class FormPanelConfigIdService extends ResourceIdService
{

    /**
     * Permission to view a panel
     */
    public static final String PERMISSION_VIEW = "VIEW";

    private static final String PROPERTY_LABEL_RESOURCE_TYPE = "forms.permission.panel.label.resourceType";

    private static final String PROPERTY_LABEL_VIEW = "forms.permission.panel.label.view";

    private List<IFormPanelConfiguration> _listFormPanelConfiguration;

    @Override
    public ReferenceList getResourceIdList( Locale locale )
    {
        ReferenceList list = new ReferenceList( );
        List<IFormPanelConfiguration> listFormPanelConfiguration = getListFormPanelConfiguration( );
        for ( IFormPanelConfiguration panelConfiguration : listFormPanelConfiguration )
        {
            list.addItem( panelConfiguration.getResourceId( ), I18nService.getLocalizedString( panelConfiguration.getTitle( ), locale ) );
        }
        return list;
    }

    @Override
    public void register( )
    {
        ResourceType resourceType = new ResourceType( );
        resourceType.setResourceIdServiceClass( FormPanelConfigIdService.class.getName( ) );
        resourceType.setPluginName( FormsPlugin.PLUGIN_NAME );
        resourceType.setResourceTypeKey( IFormPanelConfiguration.RESOURCE_TYPE );
        resourceType.setResourceTypeLabelKey( PROPERTY_LABEL_RESOURCE_TYPE );

        Permission permission = new Permission( );
        permission.setPermissionKey( PERMISSION_VIEW );
        permission.setPermissionTitleKey( PROPERTY_LABEL_VIEW );
        resourceType.registerPermission( permission );

        ResourceTypeManager.registerResourceType( resourceType );
    }

    private List<IFormPanelConfiguration> getListFormPanelConfiguration( )
    {
        if ( _listFormPanelConfiguration == null )
        {
            _listFormPanelConfiguration = SpringContextService.getBeansOfType( IFormPanelConfiguration.class );
        }
        return _listFormPanelConfiguration;
    }

    @Override
    public String getTitle( String strId, Locale locale )
    {
        String title = "";
        List<IFormPanelConfiguration> listFormPanelConfiguration = getListFormPanelConfiguration( );
        for ( IFormPanelConfiguration panelConfiguration : listFormPanelConfiguration )
        {
            if ( panelConfiguration.getResourceId( ).equals( strId ) )
            {
                title = I18nService.getLocalizedString( panelConfiguration.getTitle( ), locale );
                break;
            }
        }
        return title;
    }
}
