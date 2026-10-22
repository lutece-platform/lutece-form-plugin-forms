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
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNERS OR CONTRIBUTORS BE
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.Group;
import fr.paris.lutece.plugins.forms.business.GroupHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.inject.Inject;

/**
 * Mermaid definition of the graphic view of a form
 */
public class FormGraphExportServiceTest extends LuteceTestCase
{
    private static final String STEP_TITLE = "Step \"one\" <b>bold</b> & #lt; done";
    private static final String GROUP_TITLE = "Group <img src=x>";

    @Inject
    private FormGraphExportService _formGraphExportService;

    private Form _form;
    private Step _step;
    private Group _group;
    private FormDisplay _formDisplay;

    /**
     * Creates a form with one step holding one group, both titled with markup characters.
     */
    @BeforeEach
    protected void createForm( )
    {
        _form = new Form( );
        _form.setTitle( "FormGraphExportServiceTest" );
        _form.setBreadcrumbName( "forms.horizontalBreadcrumb" );
        FormHome.create( _form );

        _step = new Step( );
        _step.setTitle( STEP_TITLE );
        _step.setDescription( "" );
        _step.setIdForm( _form.getId( ) );
        _step.setInitial( true );
        StepHome.create( _step );

        _group = new Group( );
        _group.setTitle( GROUP_TITLE );
        _group.setIdStep( _step.getId( ) );
        _group.setIterationMin( 1 );
        _group.setIterationMax( 1 );
        GroupHome.create( _group );

        _formDisplay = new FormDisplay( );
        _formDisplay.setFormId( _form.getId( ) );
        _formDisplay.setStepId( _step.getId( ) );
        _formDisplay.setCompositeId( _group.getId( ) );
        _formDisplay.setCompositeType( CompositeDisplayType.GROUP.getLabel( ) );
        FormDisplayHome.create( _formDisplay );
    }

    /**
     * Removes the form created by the test.
     */
    @AfterEach
    protected void removeForm( )
    {
        FormDisplayHome.remove( _formDisplay.getId( ) );
        GroupHome.remove( _group.getId( ) );
        StepHome.remove( _step.getId( ) );
        FormHome.remove( _form.getId( ) );
    }

    /**
     * The titles of a step and of its questions reach the graph as text: no character of theirs opens markup, closes the label or forms an entity.
     */
    @Test
    public void testTitlesAreEncodedForMermaid( )
    {
        String strGraph = _formGraphExportService.generate( _form, null );

        assertFalse( strGraph.contains( "<b>" ), strGraph );
        assertFalse( strGraph.contains( "<img" ), strGraph );
        assertTrue( strGraph.contains( "Step #34;one#34; #60;b#62;bold#60;/b#62; #38; #35;lt; done" ), strGraph );
        assertTrue( strGraph.contains( "Group #60;img src=x#62;" ), strGraph );
    }

    /**
     * Characters with no meaning for Mermaid are left as they are.
     */
    @Test
    public void testPlainTextIsKept( )
    {
        assertEquals( "Étape 1 : coordonnées (suite)", FormGraphExportService.encodeLabel( "Étape 1 : coordonnées (suite)" ) );
        assertEquals( "", FormGraphExportService.encodeLabel( null ) );
    }
}
