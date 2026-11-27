/*
 * Copyright (c) 2002-2025, City of Paris
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
package fr.paris.lutece.plugins.forms.service.provider;

import freemarker.core.TemplateHTMLOutputModel;

/**
 * A line of a group iteration, as displayed by the group bookmark: a question and its response
 */
public class GroupIterationLine
{
    private final String _strQuestion;
    private final String _strValue;
    private final TemplateHTMLOutputModel _html;

    /**
     * Constructor of a line whose response is text
     *
     * @param strQuestion
     *            the question title
     * @param strValue
     *            the response as text
     */
    public GroupIterationLine( String strQuestion, String strValue )
    {
        this( strQuestion, strValue, null );
    }

    /**
     * Constructor of a line whose response is rich text
     *
     * @param strQuestion
     *            the question title
     * @param strValue
     *            the response, as entered
     * @param html
     *            the response as sanitized HTML, null if the response is text
     */
    public GroupIterationLine( String strQuestion, String strValue, TemplateHTMLOutputModel html )
    {
        _strQuestion = strQuestion;
        _strValue = strValue;
        _html = html;
    }

    /**
     * @return the question title
     */
    public String getQuestion( )
    {
        return _strQuestion;
    }

    /**
     * @return the response, as text or as entered for a rich text
     */
    public String getValue( )
    {
        return _strValue;
    }

    /**
     * @return true if the response is rich text, displayed with {@link #getHtml()}
     */
    public boolean isRichText( )
    {
        return _html != null;
    }

    /**
     * @return the response as sanitized HTML, null if the response is text
     */
    public TemplateHTMLOutputModel getHtml( )
    {
        return _html;
    }
}
