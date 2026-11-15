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
package fr.paris.lutece.plugins.forms.web.form.filter.display.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.util.FormMultiviewFormsNameConstants;
import fr.paris.lutece.plugins.forms.util.FormMultiviewWorkflowStateNameConstants;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;

/**
 * The multiview filters that expect a number never keep a request value that is not an integer: the filter values are kept in the session of the
 * user, so a value that cannot be converted would break every later display of the multiview
 */
public class FormFilterDisplayNumericValueTest
{
    private static final String NOT_A_NUMBER = "e2e\"><b>x</b>";
    private static final String PARAMETER_ID_WORKFLOW_STATE = "multiview_id_state_workflow";

    /**
     * A form id that is not an integer is not kept: no filter on the form
     */
    @Test
    public void testFormsFilterDoesNotKeepValueThatIsNotAnInteger( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( FormMultiviewFormsNameConstants.PARAMETER_ID_FORM, NOT_A_NUMBER );

        FormFilterDisplayForms display = new FormFilterDisplayForms( );
        Map<String, Object> map = display.getFilterDisplayMapValues( request );

        assertTrue( map.isEmpty( ) );
        assertEquals( FormFilterDisplayForms.DEFAULT_ID_FORM, display.getValue( ) );
    }

    /**
     * A valid form id is kept
     */
    @Test
    public void testFormsFilterKeepsAnIntegerValue( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( FormMultiviewFormsNameConstants.PARAMETER_ID_FORM, "3" );

        FormFilterDisplayForms display = new FormFilterDisplayForms( );
        Map<String, Object> map = display.getFilterDisplayMapValues( request );

        assertEquals( "3", map.get( FormMultiviewFormsNameConstants.FILTER_ID_FORM ) );
        assertEquals( "3", display.getValue( ) );
    }

    /**
     * Workflow state ids that are not integers are not kept, the valid ones are
     */
    @Test
    public void testWorkflowStateFilterKeepsOnlyIntegerValues( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( FormMultiviewFormsNameConstants.PARAMETER_ID_FORM, "1" );
        request.addParameter( FormMultiviewFormsNameConstants.PARAMETER_PREVIOUS_ID_FORM, "1" );
        request.addParameter( PARAMETER_ID_WORKFLOW_STATE, NOT_A_NUMBER, "2" );

        FormFilterDisplayWorkflowState display = new FormFilterDisplayWorkflowState( );
        Map<String, Object> map = display.getFilterDisplayMapValues( request );

        assertEquals( "2", map.get( FormMultiviewWorkflowStateNameConstants.FILTER_ID_WORKFLOW_STATE ) );

        request = new MockHttpServletRequest( );
        request.addParameter( FormMultiviewFormsNameConstants.PARAMETER_ID_FORM, "1" );
        request.addParameter( FormMultiviewFormsNameConstants.PARAMETER_PREVIOUS_ID_FORM, "1" );
        request.addParameter( PARAMETER_ID_WORKFLOW_STATE, NOT_A_NUMBER );

        assertTrue( display.getFilterDisplayMapValues( request ).isEmpty( ) );
    }
}
