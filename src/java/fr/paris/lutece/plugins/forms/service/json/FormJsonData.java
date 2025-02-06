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
package fr.paris.lutece.plugins.forms.service.json;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlMapping;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormMessage;
import fr.paris.lutece.plugins.forms.business.Group;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.Transition;
import fr.paris.lutece.plugins.forms.business.export.FormExportConfig;

public class FormJsonData
{

    private Form _form;
    private FormMessage _formMessage;
    private List<Step> _stepList;
    private List<Group> _groupList;
    private List<Question> _questionList;
    private List<Transition> _transitionList;
    private List<FormDisplay> _formDisplayList;
    private List<Control> _controlList;
    private List<ControlMapping> _controlMappingList;
    private List<FormExportConfig> _formExportConfigList;

    /**
     * @return the form
     */
    public Form getForm( )
    {
        return _form;
    }

    /**
     * @param form
     *            the form to set
     */
    public void setForm( Form form )
    {
        _form = form;
    }

    /**
     * @return the stepList
     */
    public List<Step> getStepList( )
    {
        return new ArrayList<>( _stepList );
    }

    /**
     * @param stepList
     *            the stepList to set
     */
    public void setStepList( List<Step> stepList )
    {
        _stepList = new ArrayList<>( stepList );
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
     * @return the transitionList
     */
    public List<Transition> getTransitionList( )
    {
        return new ArrayList<>( _transitionList );
    }

    /**
     * @param transitionList
     *            the transitionList to set
     */
    public void setTransitionList( List<Transition> transitionList )
    {
        _transitionList = new ArrayList<>( transitionList );
    }

    /**
     * @return the formMessage
     */
    public FormMessage getFormMessage( )
    {
        return _formMessage;
    }

    /**
     * @param formMessage
     *            the formMessage to set
     */
    public void setFormMessage( FormMessage formMessage )
    {
        _formMessage = formMessage;
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

    /**
     * @return the formExportConfigList
     */
    public List<FormExportConfig> getFormExportConfigList( )
    {
        return new ArrayList<>( _formExportConfigList );
    }

    /**
     * @param formExportConfigList
     *            the formExportConfigList to set
     */
    public void setFormExportConfigList( List<FormExportConfig> formExportConfigList )
    {
        _formExportConfigList = new ArrayList<>( formExportConfigList );
    }

}
