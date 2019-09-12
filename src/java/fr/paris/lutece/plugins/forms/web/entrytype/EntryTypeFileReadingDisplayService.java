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
package fr.paris.lutece.plugins.forms.web.entrytype;

import java.util.Map;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.IOcrProvider;
import fr.paris.lutece.plugins.genericattributes.business.OcrProviderManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeUpload;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;

/**
 * The display service for entry type file
 */
public class EntryTypeFileReadingDisplayService extends EntryTypeFileDisplayService
{

    private static final String MARK_OCR_CODE_TEMPLATE = "ocr_code_template";

    /**
     * Constructor of the EntryTypeFileDisplayService
     * 
     * @param strEntryServiceName
     *            The entry service name
     */
    public EntryTypeFileReadingDisplayService( String strEntryServiceName )
    {
        super( strEntryServiceName );
    }

    /**
     * Return the completed model
     * 
     * @param entry
     *            The given entry
     * @param service
     *            The upload service
     * @param model
     *            The upload model
     * @return the completed model
     */
    public Map<String, Object> setModel( Entry entry, IEntryTypeService service, Map<String, Object> model )
    {
        Field fieldType = entry.getFieldByCode( IEntryTypeService.FIELD_FILE_TYPE );
        if ( fieldType != null )
        {
            IOcrProvider ocrProvider = OcrProviderManager.getOcrProvider( fieldType.getValue( ) );
            model.put( MARK_OCR_CODE_TEMPLATE, ocrProvider.getHtmlCode( entry.getIdEntry( ), Form.RESOURCE_TYPE ) );
        }
        model.put( FormsConstants.QUESTION_ENTRY_MARKER, entry );

        model.put( MARK_UPLOAD_HANDLER, ( (AbstractEntryTypeUpload) service ).getAsynchronousUploadHandler( ) );

        return model;
    }
}
