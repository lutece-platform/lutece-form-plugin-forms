/*
 * Copyright (c) 2002-2025, City of Paris
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
import java.util.Locale;

import fr.paris.lutece.plugins.forms.business.form.panel.configuration.IFormPanelConfiguration;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.rbac.Permission;
import fr.paris.lutece.portal.service.rbac.ResourceIdService;
import fr.paris.lutece.portal.service.rbac.ResourceType;
import fr.paris.lutece.portal.service.rbac.ResourceTypeManager;
import fr.paris.lutece.util.ReferenceList;
import jakarta.enterprise.inject.spi.CDI;

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
            _listFormPanelConfiguration = CDI.current().select( IFormPanelConfiguration.class ).stream( ).toList( );
        }
        return new ArrayList<>( _listFormPanelConfiguration );
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
