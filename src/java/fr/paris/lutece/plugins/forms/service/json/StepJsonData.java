/*
 * Copyright (c) 2002-2022, City of Paris
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

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlMapping;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.Group;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;

public class StepJsonData
{

    private Step _step;
    private List<Group> _groupList;
    private List<Question> _questionList;
    private List<FormDisplay> _formDisplayList;
    private List<Control> _controlList;
    private List<ControlMapping> _controlMappingList;

    /**
     * @return the step
     */
    public Step getStep( )
    {
        return _step;
    }

    /**
     * @param step
     *            the step to set
     */
    public void setStep( Step step )
    {
        _step = step;
    }

    /**
     * @return the questionList
     */
    public List<Question> getQuestionList( )
    {
        return new ArrayList<>( _questionList );
    }

    /**
     * @param questionList
     *            the questionList to set
     */
    public void setQuestionList( List<Question> questionList )
    {
        _questionList = new ArrayList<>( questionList );
    }

    /**
     * @return the formDisplayList
     */
    public List<FormDisplay> getFormDisplayList( )
    {
        return new ArrayList<>( _formDisplayList );
    }

    /**
     * @param formDisplayList
     *            the formDisplayList to set
     */
    public void setFormDisplayList( List<FormDisplay> formDisplayList )
    {
        _formDisplayList = new ArrayList<>( formDisplayList );
    }

    /**
     * @return the groupList
     */
    public List<Group> getGroupList( )
    {
        return new ArrayList<>( _groupList );
    }

    /**
     * @param groupList
     *            the groupList to set
     */
    public void setGroupList( List<Group> groupList )
    {
        _groupList = new ArrayList<>( groupList );
    }

    /**
     * @return the controlList
     */
    public List<Control> getControlList( )
    {
        return new ArrayList<>( _controlList );
    }

    /**
     * @param controlList
     *            the controlQuestionList to set
     */
    public void setControlList( List<Control> controlList )
    {
        _controlList = new ArrayList<>( controlList );
    }

    /**
     * @return the controlMappingList
     */
    public List<ControlMapping> getControlMappingList( )
    {
        return new ArrayList<>( _controlMappingList );
    }

    /**
     * @param controlMappingList
     *            the controlMappingList to set
     */
    public void setControlMappingList( List<ControlMapping> controlMappingList )
    {
        _controlMappingList = new ArrayList<>( controlMappingList );
    }
}
