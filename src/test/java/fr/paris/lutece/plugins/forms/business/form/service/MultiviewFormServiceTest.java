/*
 * Copyright (c) 2002-2021, City of Paris
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
package fr.paris.lutece.plugins.forms.business.form.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.forms.service.MultiviewFormService;
import fr.paris.lutece.plugins.forms.web.form.panel.display.IFormPanelDisplay;
import fr.paris.lutece.plugins.forms.web.form.panel.display.impl.AbstractFormPanelDisplay;
import fr.paris.lutece.plugins.forms.web.form.panel.display.impl.FormPanelFormsDisplay;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Test for the MultiviewFormService class
 */
public class MultiviewFormServiceTest extends LuteceTestCase
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp( ) throws Exception
    {
        super.setUp( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDown( ) throws Exception
    {
        super.tearDown( );
    }

    /**
     * Test of the method {@link fr.paris.lutece.plugins.forms.service.MultiviewFormService#findActiveFormPanel(java.util.List)}
     */
    public void testFindActiveFormPanel( )
    {
        IFormPanelDisplay formPanelDisplayExpected = new FormPanelFormsDisplay( );
        formPanelDisplayExpected.setActive( Boolean.TRUE );

        List<IFormPanelDisplay> listFormPanelDisplay = new ArrayList<>( );
        listFormPanelDisplay.add( formPanelDisplayExpected );

        MultiviewFormService multiviewFormService = MultiviewFormService.getInstance( );
        IFormPanelDisplay formPanelDisplayResult = multiviewFormService.findActiveFormPanel( listFormPanelDisplay );

        assertThat( formPanelDisplayResult, is( formPanelDisplayExpected ) );
    }

    /**
     * Test of the method {@link fr.paris.lutece.plugins.forms.service.MultiviewFormService#findActiveFormPanel(java.util.List)} with several panels
     */
    public void testFindActiveFormPanelWithSeveralPanels( )
    {
        IFormPanelDisplay formPanelDisplayExpected = new FormPanelFormsDisplay( );
        formPanelDisplayExpected.setActive( Boolean.TRUE );

        List<IFormPanelDisplay> listFormPanelDisplay = new ArrayList<>( );
        listFormPanelDisplay.add( new FormPanelDisplayMockOne( ) );
        listFormPanelDisplay.add( formPanelDisplayExpected );
        listFormPanelDisplay.add( new FormPanelDisplayMockTwo( ) );

        MultiviewFormService multiviewFormService = MultiviewFormService.getInstance( );
        IFormPanelDisplay formPanelDisplayResult = multiviewFormService.findActiveFormPanel( listFormPanelDisplay );

        assertThat( formPanelDisplayResult, is( formPanelDisplayExpected ) );
    }

    /**
     * Test of the method {@link fr.paris.lutece.plugins.forms.service.MultiviewFormService#findActiveFormPanel(java.util.List)} with null list of
     * FormPanelDisplay
     */
    public void testFindActiveFormPanelWithNullList( )
    {
        IFormPanelDisplay formPanelDisplayExpected = null;

        List<IFormPanelDisplay> listFormPanelDisplay = null;

        MultiviewFormService multiviewFormService = MultiviewFormService.getInstance( );
        IFormPanelDisplay formPanelDisplayResult = multiviewFormService.findActiveFormPanel( listFormPanelDisplay );

        assertThat( formPanelDisplayResult, is( formPanelDisplayExpected ) );
    }

    /**
     * Test of the method {@link fr.paris.lutece.plugins.forms.service.MultiviewFormService#findActiveFormPanel(java.util.List)} with empty list of
     * FormPanelDisplay
     */
    public void testFindActiveFormPanelWithEmptyList( )
    {
        IFormPanelDisplay formPanelDisplayExpected = null;

        List<IFormPanelDisplay> listFormPanelDisplay = new ArrayList<>( );

        MultiviewFormService multiviewFormService = MultiviewFormService.getInstance( );
        IFormPanelDisplay formPanelDisplayResult = multiviewFormService.findActiveFormPanel( listFormPanelDisplay );

        assertThat( formPanelDisplayResult, is( formPanelDisplayExpected ) );
    }

    /**
     * Test of the method {@link fr.paris.lutece.plugins.forms.service.MultiviewFormService#findActiveFormPanel(java.util.List)} with none active panels
     */
    public void testFindActiveFormPanelWithNoneActivePanels( )
    {
        IFormPanelDisplay formPanelDisplayExpected = null;

        List<IFormPanelDisplay> listFormPanelDisplay = new ArrayList<>( );
        listFormPanelDisplay.add( new FormPanelDisplayMockOne( ) );
        listFormPanelDisplay.add( new FormPanelDisplayMockTwo( ) );

        MultiviewFormService multiviewFormService = MultiviewFormService.getInstance( );
        IFormPanelDisplay formPanelDisplayResult = multiviewFormService.findActiveFormPanel( listFormPanelDisplay );

        assertThat( formPanelDisplayResult, is( formPanelDisplayExpected ) );
    }

    /**
     * Test of the method {@link fr.paris.lutece.plugins.forms.service.MultiviewFormService#findActiveFormPanel(java.util.List)} with several active panels
     */
    public void testFindActiveFormPanelWithSeveralActivePanels( )
    {
        IFormPanelDisplay formPanelDisplayExpected = new FormPanelDisplayMockTwo( );
        formPanelDisplayExpected.setActive( Boolean.TRUE );

        List<IFormPanelDisplay> listFormPanelDisplay = new ArrayList<>( );
        listFormPanelDisplay.add( new FormPanelDisplayMockOne( ) );
        listFormPanelDisplay.add( formPanelDisplayExpected );

        IFormPanelDisplay formPanelFormsDisplay = new FormPanelFormsDisplay( );
        formPanelFormsDisplay.setActive( Boolean.TRUE );
        listFormPanelDisplay.add( formPanelFormsDisplay );

        MultiviewFormService multiviewFormService = MultiviewFormService.getInstance( );
        IFormPanelDisplay formPanelDisplayResult = multiviewFormService.findActiveFormPanel( listFormPanelDisplay );

        assertThat( formPanelDisplayResult, is( formPanelDisplayExpected ) );
    }

    /**
     * Mock implementation of the IFormPanelDisplay
     */
    private class FormPanelDisplayMockOne extends AbstractFormPanelDisplay
    {
    }

    /**
     * Other mock implementation of the IFormPanelDisplay
     */
    private class FormPanelDisplayMockTwo extends AbstractFormPanelDisplay
    {
    }
}
