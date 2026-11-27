/*
 * Copyright (c) 2002-2026, City of Paris
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
package fr.paris.lutece.plugins.forms.service.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The preview of a step template in the template selector: a thumbnail drawn by the provider and the fields of the step
 */
public class StepTemplatePreview implements Serializable
{
    private static final long serialVersionUID = -4416730275903315286L;

    private String _strThumbnailUrl;
    private final List<StepTemplatePreviewField> _listFields = new ArrayList<>( );

    /**
     * @return the URL of the thumbnail (a data URL or an address the browser can load), empty when the provider draws none
     */
    public String getThumbnailUrl( )
    {
        return _strThumbnailUrl;
    }

    /**
     * @param strThumbnailUrl
     *            the URL of the thumbnail
     */
    public void setThumbnailUrl( String strThumbnailUrl )
    {
        _strThumbnailUrl = strThumbnailUrl;
    }

    /**
     * @return every field of the step, groups and their content included, in display order; the depth of a field tells its nesting
     */
    public List<StepTemplatePreviewField> getFields( )
    {
        return _listFields;
    }

    /**
     * @return the number of questions of the step, groups not counted
     */
    public int getQuestionCount( )
    {
        return (int) _listFields.stream( ).filter( field -> !field.isGroup( ) ).count( );
    }
}
