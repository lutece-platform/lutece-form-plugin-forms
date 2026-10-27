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
package fr.paris.lutece.plugins.forms.service.json;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.inject.Inject;
import java.util.Locale;

/**
 * Copy and import of a step of a form
 */
public class FormJsonServiceStepImportTest extends LuteceTestCase
{
    @Inject
    private FormJsonService _formJsonService;
    private Form _form;

    /**
     * Creates a form
     */
    @BeforeEach
    protected void createForm( )
    {
        _form = new Form( );
        _form.setTitle( "FormJsonServiceStepImportTest" );
        _form.setBreadcrumbName( "forms.horizontalBreadcrumb" );
        FormHome.create( _form );
    }

    /**
     * Removes the steps and the form
     */
    @AfterEach
    protected void removeForm( )
    {
        StepHome.getStepsListByForm( _form.getId( ) ).forEach( step -> StepHome.remove( step.getId( ) ) );
        FormHome.remove( _form.getId( ) );
    }

    /**
     * The copy of the initial step of a form is not initial: a form keeps a single initial step
     *
     * @throws Exception
     *             if the step cannot be exported
     */
    @Test
    public void testCopyOfInitialStepIsNotInitial( ) throws Exception
    {
        Step initial = new Step( );
        initial.setTitle( "Initial" );
        initial.setDescription( "" );
        initial.setIdForm( _form.getId( ) );
        initial.setInitial( true );
        StepHome.create( initial );

        String strJson = _formJsonService.jsonExportStep( _form.getId( ), initial.getId( ) );
        _formJsonService.jsonImportStep( _form.getId( ), strJson, Locale.FRENCH );

        assertEquals( 2, StepHome.getStepsListByForm( _form.getId( ) ).size( ) );
        assertEquals( 1, StepHome.getStepsListByForm( _form.getId( ) ).stream( ).filter( Step::isInitial ).count( ) );
    }
}
