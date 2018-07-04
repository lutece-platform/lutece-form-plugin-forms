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

import java.util.List;
import java.util.ArrayList;

/**
 * This class represent the high level object to use for the display of the details of a FormResponse
 */
public class MultiviewStepDisplay
{
    private String _strTitle;
    private List<MultiviewQuestionDisplay> _listQuestionDisplay = new ArrayList<>( );
    
    /**
     * Constructor
     * 
     * @param strTitle
     *          The title of the Step
     * @param listQuestionDisplay
     *          The list of Question display associated to the Step
     */
    public MultiviewStepDisplay( String strTitle, List<MultiviewQuestionDisplay> listQuestionDisplay )
    {
        _strTitle = strTitle;
        _listQuestionDisplay = listQuestionDisplay;
    }
    
    /**
     * Return the title of the MultiviewStepDisplay
     * 
     * @return the title of the MultiviewStepDisplay
     */
    public String getTitle( )
    {
        return _strTitle;
    }
    
    /**
     * Return the list of MultiviewQuestionDisplay of the MultiviewStepDisplay
     * 
     * @return the list of MultiviewQuestionDisplay of the MultiviewStepDisplay
     */
    public List<MultiviewQuestionDisplay> getMultiviewQuestionDisplays( )
    {
        return _listQuestionDisplay;
    }
}
