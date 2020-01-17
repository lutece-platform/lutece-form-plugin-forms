/*
 * Copyright (c) 2002-2020, City of Paris
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

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.service.FormService;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFileHome;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.util.filesystem.FileSystemUtil;

/**
 * Class used to download a File for the plugin Forms
 */
public final class FormsDownloadFile
{
    // Parameters
    private static final String PARAMETER_ID_FILE = "id_file";

    // Messages
    private static final String MESSAGE_ERROR_DURING_DOWNLOAD_FILE = "forms.error.downloadFile";

    // Constants
    private static final int ID_FILE_INCORRECT = NumberUtils.INTEGER_MINUS_ONE;
    private static final int ID_RESPONSE_INCORRECT = NumberUtils.INTEGER_MINUS_ONE;

    /**
     * Private constructor
     */
    private FormsDownloadFile( )
    {

    }

    /**
     * Write in the HttpServletResponse the file to download
     * 
     * @param request
     *            The HttpServletRequest to use to retrieve the parameter values from
     * @param response
     *            The HttpServletResponse to set the file to download
     * @return an error message if an error occurred during the download of the File
     */
    public static String doDownloadFile( HttpServletRequest request, HttpServletResponse response )
    {
        int nIdFile = NumberUtils.toInt( request.getParameter( PARAMETER_ID_FILE ), ID_FILE_INCORRECT );
        int nIdResponse = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_RESPONSE ), ID_RESPONSE_INCORRECT );

        FormService formService = SpringContextService.getBean( FormService.BEAN_NAME );
        if ( !formService.isFileAccessAuthorized( request, nIdResponse, nIdFile ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.USER_ACCESS_DENIED, AdminMessage.TYPE_STOP );
        }

        PhysicalFile physicalFile = null;
        File file = FileHome.findByPrimaryKey( nIdFile );
        if ( file != null )
        {
            physicalFile = PhysicalFileHome.findByPrimaryKey( file.getPhysicalFile( ).getIdPhysicalFile( ) );
        }

        if ( physicalFile != null )
        {
            addFileContentToResponse( response, file, physicalFile );
        }

        return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_DURING_DOWNLOAD_FILE, AdminMessage.TYPE_STOP );
    }

    /**
     * Add the information of the given File with the content of its PhysicalFile to the HttpServletResponse
     * 
     * @param response
     *            The httpServletResponse on which to add the content of the PhysicalFile
     * @param file
     *            The File with the information to set in the HttpServletResponse
     * @param physicalFile
     *            The content of the given File to set in the HttpServletResponse
     */
    private static void addFileContentToResponse( HttpServletResponse response, File file, PhysicalFile physicalFile )
    {
        try
        {
            byte [ ] byteFileOutPut = physicalFile.getValue( );
            addHeaderResponse( response, file.getTitle( ) );

            String strMimeType = file.getMimeType( );
            if ( strMimeType == null )
            {
                strMimeType = FileSystemUtil.getMIMEType( file.getTitle( ) );
            }

            response.setContentType( strMimeType );
            response.setContentLength( byteFileOutPut.length );

            OutputStream outputStream = response.getOutputStream( );
            outputStream.write( byteFileOutPut );
            outputStream.close( );
        }
        catch( IOException e )
        {
            AppLogService.error( e );
        }
    }

    /**
     * Write the Http header in the response
     *
     * @param response
     *            The HttpServletResponse to modify
     * @param strFileName
     *            The name of the file who must be insert in the HttpServletResponse
     */
    private static void addHeaderResponse( HttpServletResponse response, String strFileName )
    {
        response.setHeader( "Content-Disposition", "attachment ;filename=\"" + strFileName + "\"" );
        response.setHeader( "Pragma", "public" );
        response.setHeader( "Expires", "0" );
        response.setHeader( "Cache-Control", "must-revalidate,post-check=0,pre-check=0" );
    }
}
