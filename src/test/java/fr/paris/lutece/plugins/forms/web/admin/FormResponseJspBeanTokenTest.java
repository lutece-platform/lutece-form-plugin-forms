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
package fr.paris.lutece.plugins.forms.web.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Method;
import java.util.Set;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;

/**
 * Security token of the back office form response entry.
 */
public class FormResponseJspBeanTokenTest
{
    private static final String VIEW_STEP = "stepView";
    // Navigation actions: not checked, but they render the step form
    private static final Set<String> ACTIONS_WITHOUT_TOKEN = Set.of( "doReturnStep", "doGoToStep" );

    /**
     * The step form and the summary post several actions with the single token of the page. The token filter checks it under the securityTokenAction of
     * the action: every checked action must share the key of the step view, otherwise the first post fails ("Invalid security token for action").
     * Every invoked action also generates the token of the page it renders under its securityTokenAction: the navigation actions, which render the
     * step form without being checked, must use the same key, otherwise the next post after "previous step" is refused.
     */
    @Test
    public void testActionsShareTheTokenOfTheStepView( )
    {
        String strViewKey = null;
        for ( Method method : FormResponseJspBean.class.getDeclaredMethods( ) )
        {
            View view = method.getAnnotation( View.class );
            if ( view != null && VIEW_STEP.equals( view.value( ) ) )
            {
                strViewKey = view.securityTokenAction( );
            }
        }
        assertFalse( strViewKey == null || strViewKey.isEmpty( ), "The step view must declare a securityTokenAction" );

        for ( Method method : FormResponseJspBean.class.getDeclaredMethods( ) )
        {
            Action action = method.getAnnotation( Action.class );
            if ( action != null )
            {
                assertEquals( strViewKey, action.securityTokenAction( ), action.value( ) + " must share the token of the step view" );
                if ( !ACTIONS_WITHOUT_TOKEN.contains( action.value( ) ) )
                {
                    assertFalse( action.securityTokenDisabled( ), action.value( ) + " must check the security token" );
                }
            }
        }
    }
}
