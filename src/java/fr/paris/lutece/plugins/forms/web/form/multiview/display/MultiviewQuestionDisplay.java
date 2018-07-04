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
package fr.paris.lutece.plugins.forms.web.form.multiview.display;

/**
 * This class is a representation of an Question object for the page of the details of a FormResponse
 */
public class MultiviewQuestionDisplay
{
    private String _strTitle;
    private int _depth;
    private MultiviewParentQuestionDisplay _multiviewParentQuestionDisplay;
    private MultiviewEntryResponseDisplay _multiviewEntryResponseDisplay;
    
    /**
     * Constructor
     * 
     * @param strTitle
     *          The of the Question associated to the MultiviewQuestionDisplay
     * @param depth
     *          The depth of the question in the Form
     * @param multiviewParentQuestionDisplay
     *          The MultiviewEntryResponseDisplay associated to the MultiviewQuestionDisplay
     * @param multiviewEntryResponseDisplay
     *          The MultiviewEntryResponseDisplay associated to the MultiviewQuestionDisplay
     */
    public MultiviewQuestionDisplay( String strTitle, int depth, MultiviewParentQuestionDisplay multiviewParentQuestionDisplay, MultiviewEntryResponseDisplay multiviewEntryResponseDisplay )
    {
        _strTitle = strTitle;
        _depth = depth;
        _multiviewParentQuestionDisplay = multiviewParentQuestionDisplay;
        _multiviewEntryResponseDisplay = multiviewEntryResponseDisplay;
    }
    
    /**
     * Return the title of the MultiviewQuestionDisplay
     * 
     * @return the title of the MultiviewQuestionDisplay
     */
    public String getTitle( )
    {
        return _strTitle;
    }
    
    /**
     * Return the depth of the Question in the Form
     * 
     * @return the depth of the Question in the Form
     */
    public int getDepth( )
    {
        return _depth;
    }
    
    /**
     * Return the MultiviewParentQuestionDisplay associated to the MultiviewQuestionDisplay
     * 
     * @return the MultiviewParentQuestionDisplay associated to the MultiviewQuestionDisplay
     */
    public MultiviewParentQuestionDisplay getMultiviewParentQuestionDisplay( )
    {
        return _multiviewParentQuestionDisplay;
    }
    
    /**
     * Return the MultiviewEntryResponseDisplay of the MultiviewQuestionDisplay
     * 
     * @return the MultiviewEntryResponseDisplay of the MultiviewQuestionDisplay
     */
    public MultiviewEntryResponseDisplay getMultiviewEntryResponseDisplay( )
    {
        return _multiviewEntryResponseDisplay;
    }
}
