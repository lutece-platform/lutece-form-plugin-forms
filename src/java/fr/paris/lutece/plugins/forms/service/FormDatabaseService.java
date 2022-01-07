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
package fr.paris.lutece.plugins.forms.service;

import java.util.List;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.ControlMapping;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.Group;
import fr.paris.lutece.plugins.forms.business.GroupHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.ReferenceItemFieldHome;

public class FormDatabaseService implements IFormDatabaseService
{
    public static final String BEAN_NAME = "forms.formDatabaseService";

    @Override
    public void createEntry( Entry entry )
    {
        EntryHome.create( entry );
    }

    @Override
    public void createField( Field field )
    {
        FieldHome.create( field );
    }

    @Override
    public void createQuestion( Question question )
    {
        QuestionHome.create( question );
    }

    @Override
    public void createGroup( Group group )
    {
        GroupHome.create( group );
    }

    @Override
    public void updateGroup( Group group )
    {
        GroupHome.update( group );
    }

    @Override
    public Group findGroupByPrimaryKey( int idGroup )
    {
        return GroupHome.findByPrimaryKey( idGroup );
    }

    @Override
    public Step findStepByPrimaryKey( int idStep )
    {
        return StepHome.findByPrimaryKey( idStep );
    }

    @Override
    public void updateEntry( Entry entry )
    {
        EntryHome.update( entry );
    }

    @Override
    public void createFormDisplay( FormDisplay formDisplay )
    {
        FormDisplayHome.create( formDisplay );
    }

    @Override
    public List<Question> getListQuestionByForm( int nIdForm )
    {
        return QuestionHome.getListQuestionByIdForm( nIdForm );
    }

    @Override
    public Question findQuestionByPrimaryKey( int idQuestion )
    {
        return QuestionHome.findByPrimaryKey( idQuestion );
    }

    @Override
    public Entry findEntryByPrimaryKey( int idEntry )
    {
        return EntryHome.findByPrimaryKey( idEntry );
    }

    @Override
    public void updateField( Field field )
    {
        FieldHome.update( field );
    }

    @Override
    public Field findFieldByPrimaryKey( int idField )
    {
        return FieldHome.findByPrimaryKey( idField );
    }

    @Override
    public FormDisplay findDisplayByPrimaryKey( int idDisplay )
    {
        return FormDisplayHome.findByPrimaryKey( idDisplay );
    }

    @Override
    public void updateQuestion( Question question )
    {
        QuestionHome.update( question );
    }

    @Override
    public List<FormDisplay> getFormDisplayListByParent( int nIdStep, int nIdParent )
    {
        return FormDisplayHome.getFormDisplayListByParent( nIdStep, nIdParent );
    }

    @Override
    public void updateFormDisplay( FormDisplay formDisplay )
    {
        FormDisplayHome.update( formDisplay );
    }

    @Override
    public Entry copyEntry( Entry entry )
    {
        return EntryHome.copy( entry );
    }

    @Override
    public List<Control> getControlByQuestion( int nIdQuestion )
    {
        return ControlHome.getControlByQuestion( nIdQuestion );
    }

    @Override
    public FormDisplay getFormDisplayByFormStepAndComposite( int nIdForm, int nIdStep, int nIdComposite )
    {
        return FormDisplayHome.getFormDisplayByFormStepAndComposite( nIdForm, nIdStep, nIdComposite );
    }

    @Override
    public void createControl( Control control )
    {
        ControlHome.create( control );
    }

    @Override
    public List<Group> getGroupsListByIdStepList( List<Integer> idStepList )
    {
        return GroupHome.getGroupsListByIdStepList( idStepList );
    }

    @Override
    public List<Question> getQuestionsListByStep( int nIdStep )
    {
        return QuestionHome.getQuestionsListByStep( nIdStep );
    }

    @Override
    public Integer findIdReferenceItemByIdField( int idField )
    {
        return ReferenceItemFieldHome.findIdItemByIdField( idField );
    }

    @Override
    public List<ControlMapping> getControlMappingListByIdControl( int nIdControl )
    {
        return ControlHome.getControlMappingListByIdControl( nIdControl );
    }

    @Override
    public void createStep( Step step )
    {
        StepHome.create( step );
    }

    @Override
    public void createMappingControl( int nIdcontrol, int nIdQuestion, String strValue )
    {
        ControlHome.createMappingControl( nIdcontrol, nIdQuestion, strValue );
    }
}
