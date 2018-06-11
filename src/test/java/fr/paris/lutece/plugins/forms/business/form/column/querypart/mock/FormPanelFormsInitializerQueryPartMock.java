/*
 * Copyright (c) 2002-2018, Mairie de Paris
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
package fr.paris.lutece.plugins.forms.business.form.column.querypart.mock;

import java.util.Arrays;
import java.util.List;

import fr.paris.lutece.plugins.forms.business.form.panel.initializer.querypart.impl.FormPanelFormsInitializerQueryPart;

/**
 * Mock of a FormPanelFormsInitializerQueryPart
 */
public class FormPanelFormsInitializerQueryPartMock extends FormPanelFormsInitializerQueryPart
{
    private static final String FORM_PANEL_FORMS_INITIALIZER_SELECT_QUERY = "id_response";
    private static final String FORM_PANEL_FORMS_INITIALIZER_FROM_QUERY = "forms_form AS form";
    private static final String FORM_PANEL_FORMS_INITIALIZER_JOIN_QUERY = "INNER JOIN forms_response AS response ON response.id_form = form.id_form";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFormPanelInitializerSelectQuery( )
    {
        return FORM_PANEL_FORMS_INITIALIZER_SELECT_QUERY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFormPanelInitializerFromQuery( )
    {
        return FORM_PANEL_FORMS_INITIALIZER_FROM_QUERY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getFormPanelInitializerJoinQueries( )
    {
        return Arrays.asList( FORM_PANEL_FORMS_INITIALIZER_JOIN_QUERY );
    }
}
