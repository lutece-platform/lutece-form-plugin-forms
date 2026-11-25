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
package fr.paris.lutece.plugins.forms.web.builder;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.Group;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;

/**
 * A node of the form builder canvas: a question or a group of a step, with what the canvas shows about it
 */
public class FormBuilderNode
{
    private final FormDisplay _display;
    private Question _question;
    private Group _group;
    private EntryType _entryType;
    private String _strContentHtml;
    private boolean _bMandatory;
    private boolean _bDisabled;
    private boolean _bConditional;
    private boolean _bValidated;
    private final List<FormBuilderNode> _listChildren = new ArrayList<>( );
    private final List<List<FormBuilderNode>> _listColumns = new ArrayList<>( );

    /**
     * Constructor
     *
     * @param display
     *            the display of the node
     */
    public FormBuilderNode( FormDisplay display )
    {
        _display = display;
    }

    /**
     * Returns the display of the node
     *
     * @return the display
     */
    public FormDisplay getDisplay( )
    {
        return _display;
    }

    /**
     * Tells whether the node is a group
     *
     * @return true for a group, false for a question
     */
    public boolean isGroupNode( )
    {
        return _group != null;
    }

    /**
     * Returns the question of the node, null for a group
     *
     * @return the question
     */
    public Question getQuestion( )
    {
        return _question;
    }

    /**
     * Sets the question of the node
     *
     * @param question
     *            the question
     */
    public void setQuestion( Question question )
    {
        _question = question;
    }

    /**
     * Returns the group of the node, null for a question
     *
     * @return the group
     */
    public Group getGroup( )
    {
        return _group;
    }

    /**
     * Sets the group of the node
     *
     * @param group
     *            the group
     */
    public void setGroup( Group group )
    {
        _group = group;
    }

    /**
     * Returns the entry type of the question
     *
     * @return the entry type, null for a group
     */
    public EntryType getEntryType( )
    {
        return _entryType;
    }

    /**
     * Sets the entry type of the question
     *
     * @param entryType
     *            the entry type
     */
    public void setEntryType( EntryType entryType )
    {
        _entryType = entryType;
    }

    /**
     * Returns the rendered widget of the question
     *
     * @return the HTML of the widget, empty for a group
     */
    public String getContentHtml( )
    {
        return _strContentHtml;
    }

    /**
     * Sets the rendered widget of the question
     *
     * @param strContentHtml
     *            the HTML of the widget
     */
    public void setContentHtml( String strContentHtml )
    {
        _strContentHtml = strContentHtml;
    }

    /**
     * Tells whether the question is mandatory
     *
     * @return true if mandatory
     */
    public boolean isMandatory( )
    {
        return _bMandatory;
    }

    /**
     * Sets whether the question is mandatory
     *
     * @param bMandatory
     *            true if mandatory
     */
    public void setMandatory( boolean bMandatory )
    {
        _bMandatory = bMandatory;
    }

    /**
     * Tells whether the question is disabled (hidden from the filled form)
     *
     * @return true if disabled
     */
    public boolean isDisabled( )
    {
        return _bDisabled;
    }

    /**
     * Sets whether the question is disabled
     *
     * @param bDisabled
     *            true if disabled
     */
    public void setDisabled( boolean bDisabled )
    {
        _bDisabled = bDisabled;
    }

    /**
     * Tells whether the display has a conditional display control
     *
     * @return true if conditional
     */
    public boolean isConditional( )
    {
        return _bConditional;
    }

    /**
     * Sets whether the display has a conditional display control
     *
     * @param bConditional
     *            true if conditional
     */
    public void setConditional( boolean bConditional )
    {
        _bConditional = bConditional;
    }

    /**
     * Tells whether the question has validation controls
     *
     * @return true if validated
     */
    public boolean isValidated( )
    {
        return _bValidated;
    }

    /**
     * Sets whether the question has validation controls
     *
     * @param bValidated
     *            true if validated
     */
    public void setValidated( boolean bValidated )
    {
        _bValidated = bValidated;
    }

    /**
     * Returns the children of a group node
     *
     * @return the children, ordered by display order
     */
    public List<FormBuilderNode> getChildren( )
    {
        return _listChildren;
    }

    /**
     * Returns the children of a group node split by column of the layout of the group, each column in display order
     *
     * @return the columns, one list per column of the layout; empty for a question node
     */
    public List<List<FormBuilderNode>> getColumns( )
    {
        return _listColumns;
    }
}
