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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.implementation.DefaultFileDownloadService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

public class FormsFileDownloadService extends DefaultFileDownloadService
{
    private static final long serialVersionUID = -3269252917857734585L;

    private static final String SERVICE_NAME = "FormsFileDownloadService";
    private static final String SEPARATOR = "/";

    private static final int VALIDITY_DURATION = Integer.parseInt( AppPropertiesService.getProperty( "forms.file.download.validity", "0" ) );

    @Override
    public String getName( )
    {
        return SERVICE_NAME;
    }

    @Override
    public int getValidityTime( )
    {
        return VALIDITY_DURATION;
    }

    @Override
    public String getFileDownloadUrlBO( String strFileKey, Map<String, String> additionnalData, String strFileStorageServiceProviderName )
    {
        StringBuilder sbUrl = new StringBuilder( );

        sbUrl.append( AppPathService.getBaseUrl( null ) );
        sbUrl.append( URL_BO );

        if ( additionnalData == null )
        {
            additionnalData = new HashMap<>( );
        }
        additionnalData.put( FileService.PARAMETER_FILE_ID, strFileKey );
        additionnalData.put( FileService.PARAMETER_BO, String.valueOf( true ) );

        return getEncryptedUrl( sbUrl.toString( ), getDataToEncryptForms( additionnalData ), strFileStorageServiceProviderName );
    }

    @Override
    public String getFileDownloadUrlFO( String strFileKey, Map<String, String> additionnalData, String strFileStorageServiceProviderName )
    {
        StringBuilder sbUrl = new StringBuilder( );

        sbUrl.append( AppPathService.getBaseUrl( null ) );
        sbUrl.append( URL_FO );

        if ( additionnalData == null )
        {
            additionnalData = new HashMap<>( );
        }
        additionnalData.put( FileService.PARAMETER_FILE_ID, strFileKey );
        additionnalData.put( FileService.PARAMETER_BO, String.valueOf( false ) );

        return getEncryptedUrl( sbUrl.toString( ), getDataToEncryptForms( additionnalData ), strFileStorageServiceProviderName );
    }

    /**
     * get data to encrypt
     * 
     * @param fileDownloadData
     * @return the map of datas to encrypt in the url
     */
    private String getDataToEncryptForms( Map<String, String> additionnalData )
    {
        StringBuilder sb = new StringBuilder( );
        sb.append( StringUtils.defaultIfEmpty( additionnalData.get( FileService.PARAMETER_FILE_ID ), "" ) ).append( SEPARATOR );
        sb.append( StringUtils.defaultIfEmpty( additionnalData.get( FileService.PARAMETER_RESOURCE_ID ), "" ) ).append( SEPARATOR );
        sb.append( StringUtils.defaultIfEmpty( additionnalData.get( FileService.PARAMETER_RESOURCE_TYPE ), "" ) ).append( SEPARATOR );
        sb.append( StringUtils.defaultIfEmpty( additionnalData.get( FileService.PARAMETER_BO ), "" ) ).append( SEPARATOR );
        sb.append( calculateEndValidity( ) );

        return sb.toString( );
    }

    @Override
    protected Map<String, String> getDecryptedData( String strData )
    {
        String [ ] data = strData.split( SEPARATOR );
        Map<String, String> fileData = buildAdditionnalDatas( data [0], data [1], data [2] );
        fileData.put( FileService.PARAMETER_BO, data [3] );
        fileData.put( FileService.PARAMETER_VALIDITY_TIME, data [4] );

        return fileData;
    }
}
