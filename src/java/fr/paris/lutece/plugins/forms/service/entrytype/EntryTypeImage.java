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
package fr.paris.lutece.plugins.forms.service.entrytype;

import fr.paris.lutece.plugins.forms.service.upload.FormsAsynchronousUploadHandler;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeFile;
import fr.paris.lutece.plugins.genericattributes.service.upload.AbstractGenAttUploadHandler;
import fr.paris.lutece.util.url.UrlItem;

/**
 *
 * class EntryTypeImage
 *
 */
public class EntryTypeImage extends AbstractEntryTypeFile
{
    /**
     * Name of the bean of this service
     */
    public static final String BEAN_NAME = "forms.entryTypeImage";

    private static final String JSP_DOWNLOAD_FILE = "jsp/admin/plugins/forms/DoDownloadFile.jsp";

    private static final String TEMPLATE_CREATE = "admin/plugins/forms/entries/create_entry_type_image.html";
    private static final String TEMPLATE_MODIFY = "admin/plugins/forms/entries/modify_entry_type_image.html";
    private static final String TEMPLATE_ENTRY_READ_ONLY = "admin/plugins/forms/entries/template_read_only_entry_type_image.html";
    private static final String TEMPLATE_HTML_CODE = "skin/plugins/forms/entries/html_code_entry_type_image.html";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateHtmlForm( Entry entry, boolean bDisplayFront )
    {
        return TEMPLATE_HTML_CODE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateCreate( Entry entry, boolean bDisplayFront )
    {
        return TEMPLATE_CREATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateModify( Entry entry, boolean bDisplayFront )
    {
        return TEMPLATE_MODIFY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractGenAttUploadHandler getAsynchronousUploadHandler( )
    {
        return FormsAsynchronousUploadHandler.getHandler( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUrlDownloadFile( int nResponseId, String strBaseUrl )
    {
        UrlItem url = new UrlItem( strBaseUrl + JSP_DOWNLOAD_FILE );
        url.addParameter( PARAMETER_ID_RESPONSE, nResponseId );

        return url.getUrl( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkForImages( )
    {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateEntryReadOnly( )
    {
        return TEMPLATE_ENTRY_READ_ONLY;
    }
}
