/*
 * Copyright (c) 2002-2021, City of Paris
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
package fr.paris.lutece.plugins.forms.service.download;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.service.FormsResourceIdService;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.download.AbstractFileDownloadProvider;
import fr.paris.lutece.portal.service.download.FileDownloadData;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

public class FormsFileDownloadProvider extends AbstractFileDownloadProvider
{
    public static final String PROVIDER_NAME = "forms.formsFileDownloadProvider";

    private static final String UNAUTHORIZED = "Unauthorized";
    private static final int VALIDITY_DURATION = Integer.parseInt( AppPropertiesService.getProperty( "forms.file.download.validity", "0" ) );

    @Override
    public String getProviderName( )
    {
        return PROVIDER_NAME;
    }

    @Override
    public int getLinkValidityTime( )
    {
        return VALIDITY_DURATION;
    }

    @Override
    protected void checkUserDownloadRight( User user, boolean bo, FileDownloadData fileDownloadData ) throws AccessDeniedException, UserNotSignedException
    {
        if ( bo && user != null )
        {
            AdminUser adminUser = (AdminUser) user;
            if ( !RBACService.isAuthorized( fileDownloadData.getResourceType( ), String.valueOf( fileDownloadData.getIdResource( ) ),
                    FormsResourceIdService.PERMISSION_MODIFY, (User) adminUser ) )
            {
                throw new AccessDeniedException( UNAUTHORIZED );
            }
        }
        else
            if ( !bo )
            {
                Form form = FormHome.findByPrimaryKey( fileDownloadData.getIdResource( ) );
                if ( form.isAuthentificationNeeded( ) && user == null )
                {
                    throw new UserNotSignedException( );
                }
            }
    }

}
