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
package fr.paris.lutece.plugins.forms.service.listener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.ControlType;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.portal.service.regularexpression.RegularExpressionRemovalListenerService;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * A regular expression used by the pattern control of a question cannot be removed
 */
public class FormsRegularExpressionRemovalListenerTest extends LuteceTestCase
{
    private static final String VALIDATOR_PATTERN = "forms.patternValidator";
    private static final String ID_REGULAR_EXPRESSION_USED = "424242";
    private static final String ID_REGULAR_EXPRESSION_UNUSED = "434343";
    private static final String FORM_TITLE = "Form of the regular expression test";
    private static final String QUESTION_TITLE = "Postal code";

    private Form _form;
    private Step _step;
    private Question _question;
    private Control _control;

    /**
     * Creates a form whose question has a pattern control using a regular expression
     */
    @BeforeEach
    protected void createForm( )
    {
        _form = new Form( );
        _form.setTitle( FORM_TITLE );
        _form.setBreadcrumbName( "forms.horizontalBreadcrumb" );
        FormHome.create( _form );

        _step = new Step( );
        _step.setTitle( "Step" );
        _step.setDescription( "" );
        _step.setIdForm( _form.getId( ) );
        StepHome.create( _step );

        _question = new Question( );
        _question.setTitle( QUESTION_TITLE );
        _question.setCode( "postal_code" );
        _question.setDescription( "" );
        _question.setColumnTitle( QUESTION_TITLE );
        _question.setIdStep( _step.getId( ) );
        QuestionHome.create( _question );

        _control = new Control( );
        _control.setControlType( ControlType.VALIDATION.getLabel( ) );
        _control.setIdControlTarget( _question.getId( ) );
        _control.setValidatorName( VALIDATOR_PATTERN );
        _control.setValue( ID_REGULAR_EXPRESSION_USED );
        Set<Integer> listIdQuestion = new HashSet<>( );
        listIdQuestion.add( _question.getId( ) );
        _control.setListIdQuestion( listIdQuestion );
        ControlHome.create( _control );
    }

    /**
     * Removes the created data
     */
    @AfterEach
    protected void removeForm( )
    {
        ControlHome.remove( _control.getId( ) );
        QuestionHome.remove( _question.getId( ) );
        StepHome.remove( _step.getId( ) );
        FormHome.remove( _form.getId( ) );
    }

    /**
     * The removal of a regular expression used by a control is refused with a message naming the form and the question; an unused one can be removed
     */
    @Test
    public void testRemovalRefusedWhileUsed( )
    {
        List<String> listMessages = new ArrayList<>( );
        assertFalse( RegularExpressionRemovalListenerService.getService( ).checkForRemoval( ID_REGULAR_EXPRESSION_USED, listMessages, Locale.ENGLISH ) );
        assertTrue( listMessages.stream( ).anyMatch( message -> message.contains( FORM_TITLE ) && message.contains( QUESTION_TITLE ) ),
                String.valueOf( listMessages ) );

        assertTrue( RegularExpressionRemovalListenerService.getService( ).checkForRemoval( ID_REGULAR_EXPRESSION_UNUSED, new ArrayList<>( ), Locale.ENGLISH ) );
    }
}
