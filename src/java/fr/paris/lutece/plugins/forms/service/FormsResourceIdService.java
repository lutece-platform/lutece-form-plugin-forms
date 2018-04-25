/*
 * Copyright (c) 2002-2018, Mairie de Paris
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

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.portal.service.rbac.Permission;
import fr.paris.lutece.portal.service.rbac.ResourceIdService;
import fr.paris.lutece.portal.service.rbac.ResourceType;
import fr.paris.lutece.portal.service.rbac.ResourceTypeManager;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.ReferenceList;

import java.util.Locale;

/**
 *
 * class FormResourceIdService
 *
 */
public class FormsResourceIdService extends ResourceIdService
{
    /**
     * Permission to create a form
     */
    public static final String PERMISSION_CREATE = "CREATE";

    /** Permission for testing a form */
    public static final String PERMISSION_TEST = "TEST";

    /** Permission for deleting a form */
    public static final String PERMISSION_DELETE = "DELETE";

    /** Permission for modifying a form */
    public static final String PERMISSION_MODIFY = "MODIFY";

    /** Permission for copying a form */
    public static final String PERMISSION_COPY = "COPY";

    /** Permission for viewing result of a form */
    public static final String PERMISSION_VIEW_RESULT = "VIEW_RESULT";

    /** Permission for enable form */
    public static final String PERMISSION_CHANGE_STATE = "CHANGE_STATE";

    /** Permission for managing Validator */
    public static final String PERMISSION_MANAGE_VALIDATOR = "MANAGE_VALIDATOR";

    private static final String PROPERTY_LABEL_RESOURCE_TYPE = "form.permission.label.resourceType";
    private static final String PROPERTY_LABEL_CREATE = "form.permission.label.create";
    private static final String PROPERTY_LABEL_TEST = "form.permission.label.test";
    private static final String PROPERTY_LABEL_DELETE = "form.permission.label.delete";
    private static final String PROPERTY_LABEL_MODIFY = "form.permission.label.modify";
    private static final String PROPERTY_LABEL_COPY = "form.permission.label.copy";
    private static final String PROPERTY_LABEL_VIEW_RESULT = "form.permission.label.viewResult";
    private static final String PROPERTY_LABEL_CHANGE_STATE = "form.permission.label.changeState";
    private static final String PROPERTY_LABEL_MANAGE_VALIDATOR = "form.permission.label.manageValidator";

    /** Creates a new instance of DocumentTypeResourceIdService */
    public FormsResourceIdService( )
    {
        setPluginName( FormsPlugin.PLUGIN_NAME );
    }

    /**
     * Initializes the service
     */
    @Override
    public void register( )
    {
        ResourceType rt = new ResourceType( );
        rt.setResourceIdServiceClass( FormsResourceIdService.class.getName( ) );
        rt.setPluginName( FormsPlugin.PLUGIN_NAME );
        rt.setResourceTypeKey( Form.RESOURCE_TYPE );
        rt.setResourceTypeLabelKey( PROPERTY_LABEL_RESOURCE_TYPE );

        Permission p = new Permission( );
        p.setPermissionKey( PERMISSION_CREATE );
        p.setPermissionTitleKey( PROPERTY_LABEL_CREATE );
        rt.registerPermission( p );

        p = new Permission( );
        p.setPermissionKey( PERMISSION_MODIFY );
        p.setPermissionTitleKey( PROPERTY_LABEL_MODIFY );
        rt.registerPermission( p );

        p = new Permission( );
        p.setPermissionKey( PERMISSION_COPY );
        p.setPermissionTitleKey( PROPERTY_LABEL_COPY );
        rt.registerPermission( p );

        p = new Permission( );
        p.setPermissionKey( PERMISSION_TEST );
        p.setPermissionTitleKey( PROPERTY_LABEL_TEST );
        rt.registerPermission( p );

        p = new Permission( );
        p.setPermissionKey( PERMISSION_CHANGE_STATE );
        p.setPermissionTitleKey( PROPERTY_LABEL_CHANGE_STATE );
        rt.registerPermission( p );

        p = new Permission( );
        p.setPermissionKey( PERMISSION_MANAGE_VALIDATOR );
        p.setPermissionTitleKey( PROPERTY_LABEL_MANAGE_VALIDATOR );
        rt.registerPermission( p );

        p = new Permission( );
        p.setPermissionKey( PERMISSION_VIEW_RESULT );
        p.setPermissionTitleKey( PROPERTY_LABEL_VIEW_RESULT );
        rt.registerPermission( p );

        p = new Permission( );
        p.setPermissionKey( PERMISSION_DELETE );
        p.setPermissionTitleKey( PROPERTY_LABEL_DELETE );
        rt.registerPermission( p );

        ResourceTypeManager.registerResourceType( rt );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle( String strId, Locale locale )
    {
        int nIdForm = -1;

        try
        {
            nIdForm = Integer.parseInt( strId );
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }

        Form form = FormHome.findByPrimaryKey( nIdForm );

        return form.getTitle( );
    }

    @Override
    public ReferenceList getResourceIdList( Locale locale )
    {

        return FormHome.getFormsReferenceList( );
    }
}
