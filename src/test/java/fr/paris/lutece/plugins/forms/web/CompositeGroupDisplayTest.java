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
package fr.paris.lutece.plugins.forms.web;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Group;
import fr.paris.lutece.plugins.forms.business.GroupHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;

/**
 * Removal of an iteration of a group
 */
public class CompositeGroupDisplayTest extends LuteceTestCase
{
    private Form _form;
    private Step _step;
    private Group _group;
    private FormDisplay _formDisplay;

    @BeforeEach
    protected void createGroup( )
    {
        _form = new Form( );
        _form.setTitle( "CompositeGroupDisplayTest" );
        _form.setBreadcrumbName( "forms.horizontalBreadcrumb" );
        FormHome.create( _form );

        _step = new Step( );
        _step.setTitle( "Step" );
        _step.setDescription( "" );
        _step.setIdForm( _form.getId( ) );
        _step.setInitial( true );
        StepHome.create( _step );

        _group = new Group( );
        _group.setTitle( "Group" );
        _group.setIdStep( _step.getId( ) );
        _group.setIterationMin( 1 );
        _group.setIterationMax( 3 );
        GroupHome.create( _group );

        _formDisplay = new FormDisplay( );
        _formDisplay.setFormId( _form.getId( ) );
        _formDisplay.setStepId( _step.getId( ) );
        _formDisplay.setCompositeId( _group.getId( ) );
        _formDisplay.setCompositeType( CompositeDisplayType.GROUP.getLabel( ) );
        FormDisplayHome.create( _formDisplay );
    }

    @AfterEach
    protected void removeGroup( )
    {
        FormDisplayHome.remove( _formDisplay.getId( ) );
        GroupHome.remove( _group.getId( ) );
        StepHome.remove( _step.getId( ) );
        FormHome.remove( _form.getId( ) );
    }

    /**
     * An iteration that does not exist, or an iteration below the minimum number of iterations, is not removed (no exception)
     */
    @Test
    public void testRemoveIterationBounds( ) throws Exception
    {
        CompositeGroupDisplay groupDisplay = new CompositeGroupDisplay( _formDisplay, null, 0 );
        MockHttpServletRequest request = new MockHttpServletRequest( );
        assertEquals( 0, getIterationNumber( groupDisplay ) );

        // Unknown iterations
        groupDisplay.removeIteration( request, _group.getId( ), 99, newFormResponse( ) );
        groupDisplay.removeIteration( request, _group.getId( ), -1, newFormResponse( ) );
        assertEquals( 0, getIterationNumber( groupDisplay ) );

        // The only iteration of a group with a minimum of 1
        groupDisplay.removeIteration( request, _group.getId( ), 0, newFormResponse( ) );
        assertEquals( 0, getIterationNumber( groupDisplay ) );

        // A second iteration can be removed
        groupDisplay.iterate( _formDisplay.getId( ) );
        assertEquals( 1, getIterationNumber( groupDisplay ) );
        groupDisplay.removeIteration( request, _group.getId( ), 1, newFormResponse( ) );
        assertEquals( 0, getIterationNumber( groupDisplay ) );
    }

    private static FormResponse newFormResponse( )
    {
        FormResponse formResponse = new FormResponse( );
        formResponse.setSteps( new ArrayList<>( ) );
        return formResponse;
    }

    private static int getIterationNumber( CompositeGroupDisplay groupDisplay ) throws ReflectiveOperationException
    {
        Field field = CompositeGroupDisplay.class.getDeclaredField( "_nIterationNumber" );
        field.setAccessible( true );
        return field.getInt( groupDisplay );
    }
}
