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
package fr.paris.lutece.plugins.forms.web.file;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.service.FormService;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFileHome;
import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.image.ImageResourceManager;
import fr.paris.lutece.portal.service.image.ImageResourceProvider;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.util.file.FileUtil;

/**
 * Image Resource Service for the plugin Forms
 */
public final class FormsFileImageService implements ImageResourceProvider
{
    // Constants
    private static final String IMAGE_RESOURCE_TYPE_ID = "forms_entry_img";
    private static final int ID_RESPONSE_NOT_FOUND = NumberUtils.INTEGER_MINUS_ONE;

    /**
     * Constructor
     */
    private FormsFileImageService( )
    {

    }

    /**
     * Get the unique instance of the service
     *
     * @return the unique instance of the FormsFileImageService service
     */
    public static FormsFileImageService getInstance( )
    {
        return FormsFileImageServiceHolder._singleton;
    }

    /**
     * Initializes the service
     */
    public void register( )
    {
        ImageResourceManager.registerProvider( this );
    }

    /**
     * Return the Resource id
     * 
     * @param nIdResource
     *            The resource identifier
     * @return The Resource Image
     */
    @Override
    public ImageResource getImageResource( int nIdResource )
    {
        // When using an older core version (before 5.1.5), the local variables will not
        // have been set by the image servlet. So we can get null or a request from another thread.
        // We could try to detect this by checking request.getServletPath( ) (or maybe other things?)
        // but it would break if we decide to expose this provider through another entry point.
        // Also, on Tomcat (tested 8.5.5), it seems like the request object is reused just like
        // the thread, so that even if the local variables were set in another request,
        // the object we get here is the correct one (with the correct LuteceUser or AdminUser etc).
        // Also, Portal.jsp, the main entry point of the webapp, does clean up the local variables.
        // Note that the other request could even have run code from another webapp (not even a Lutece webapp)
        // Also, we could log a warning here when request is null, but then it would prevent from using
        // this function from code not associated with a request. So no warnings.
        HttpServletRequest request = LocalVariables.getRequest( );

        ImageResource imageResource = null;
        
        File file = FileHome.findByPrimaryKey( nIdResource );
        if ( file != null && file.getPhysicalFile( ) != null && FileUtil.hasImageExtension( file.getTitle( ) ) && request != null )
        {
            FormService formService = SpringContextService.getBean( FormService.BEAN_NAME );
            int nIdResponse = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_RESPONSE ), ID_RESPONSE_NOT_FOUND );
            
            if ( formService.isFileAccessAuthorized( request, nIdResponse, nIdResource ) )
            {
                imageResource = createImageResource( file );
            }
        }

        return imageResource;
    }

    /**
     * Create an ImageResource associated to the given File
     * 
     * @param file
     *          The File on which to based to create the create the ImageResource
     * @return the ImageResource associated to the given File or null if an error occurred
     */
    private ImageResource createImageResource( File file )
    {
        ImageResource imageResource = null;

        if ( file != null && file.getPhysicalFile( ) != null )
        {
            PhysicalFile physicalFile = PhysicalFileHome.findByPrimaryKey( file.getPhysicalFile( ).getIdPhysicalFile( ) );
            if ( physicalFile != null )
            {
                imageResource = new ImageResource( );
                imageResource.setImage( physicalFile.getValue( ) );
                imageResource.setMimeType( file.getMimeType( ) );
            }
        }

        return imageResource;
    }

    /**
     * Return the Resource Type id
     * 
     * @return The Resource Type Id
     */
    @Override
    public String getResourceTypeId( )
    {
        return IMAGE_RESOURCE_TYPE_ID;
    }

    /**
     * Class holder for the singleton of the FormsFileImageService service
     */
    private static class FormsFileImageServiceHolder
    {
        // Variables
        private static final FormsFileImageService _singleton = new FormsFileImageService( );
    }
}
