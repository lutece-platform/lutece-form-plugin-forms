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
package fr.paris.lutece.plugins.forms.web.entrytype;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.service.download.FormsFileDownloadProvider;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.service.download.AbstractFileDownloadProvider;
import fr.paris.lutece.portal.service.download.FileDownloadData;
import fr.paris.lutece.portal.service.download.IFileDownloadProvider;

public class EntryTypeCommentDisplayService extends EntryTypeDefaultDisplayService
{
    public static final String MARK_FILENAME = "filename";
    public static final String MARK_URL_DOWNLOAD_BO = "url_download_bo";
    public static final String MARK_URL_DOWNLOAD_FO = "url_download_fo";

    public EntryTypeCommentDisplayService( String strEntryServiceName )
    {
        super( strEntryServiceName );
    }

    @Override
    public String getEntryTemplateDisplay( HttpServletRequest request, Entry entry, Locale locale, Map<String, Object> model, DisplayType displayType )
    {
        Field fieldFile = entry.getFieldByCode( IEntryTypeService.FIELD_DOWNLOADABLE_FILE );
        if ( fieldFile != null )
        {
            FileDownloadData fileDownloadData = new FileDownloadData( entry.getIdResource( ), Form.RESOURCE_TYPE, Integer.parseInt( fieldFile.getValue( ) ) );
            IFileDownloadProvider provider = AbstractFileDownloadProvider.findProvider( FormsFileDownloadProvider.PROVIDER_NAME );

            model.put( MARK_FILENAME, fieldFile.getTitle( ) );
            model.put( MARK_URL_DOWNLOAD_BO, provider.getDownloadUrl( fileDownloadData, true ) );
            model.put( MARK_URL_DOWNLOAD_FO, provider.getDownloadUrl( fileDownloadData, false ) );
        }
        return super.getEntryTemplateDisplay( request, entry, locale, model, displayType );
    }
}
