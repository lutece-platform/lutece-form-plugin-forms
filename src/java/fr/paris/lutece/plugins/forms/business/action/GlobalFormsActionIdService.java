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
package fr.paris.lutece.plugins.forms.business.action;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.service.FormsPlugin;
import fr.paris.lutece.portal.service.rbac.Permission;
import fr.paris.lutece.portal.service.rbac.ResourceIdService;
import fr.paris.lutece.portal.service.rbac.ResourceType;
import fr.paris.lutece.portal.service.rbac.ResourceTypeManager;
import fr.paris.lutece.util.ReferenceList;

public class GlobalFormsActionIdService extends ResourceIdService
{

    /**
     * {@inheritDoc}
     */
    @Override
    public void register( )
    {
        ResourceType rt = new ResourceType( );
        rt.setResourceIdServiceClass( GlobalFormsActionIdService.class.getName( ) );
        rt.setPluginName( FormsPlugin.PLUGIN_NAME );
        rt.setResourceTypeKey( GlobalFormsAction.RESOURCE_TYPE_CODE );
        rt.setResourceTypeLabelKey( GlobalFormsAction.RESOURCE_TYPE_LABEL_KEY );

        Permission p = new Permission( );
        p.setPermissionKey( GlobalFormsAction.PERMISSION_PERFORM_ACTION );
        p.setPermissionTitleKey( GlobalFormsAction.PERMISSION_PERFORM_ACTION_LABEL_KEY );
        rt.registerPermission( p );

        ResourceTypeManager.registerResourceType( rt );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceList getResourceIdList( Locale locale )
    {
        ReferenceList referenceList = new ReferenceList( );
        GlobalFormsActionHome.selectAllFormActions( FormsPlugin.getPlugin( ), locale ).stream( )
                .forEach( ( GlobalFormsAction action ) -> referenceList.addItem( action.getCode( ), action.getName( ) ) );
        return referenceList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle( String strCode, Locale locale )
    {
        GlobalFormsAction action = GlobalFormsActionHome.selectGlobalFormActionByCode( strCode, FormsPlugin.getPlugin( ), locale );
        return ( action != null ) ? action.getName( ) : StringUtils.EMPTY;
    }

}
