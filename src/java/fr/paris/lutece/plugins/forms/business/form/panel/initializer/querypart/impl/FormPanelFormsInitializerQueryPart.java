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
package fr.paris.lutece.plugins.forms.business.form.panel.initializer.querypart.impl;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.form.FormParameters;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.lucene.document.IntPoint;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;

/**
 * Implementation of the FormPanelInitializerQueryPart associate to the FormPanelFormsInitializer
 */
public class FormPanelFormsInitializerQueryPart extends AbstractFormPanelInitializerQueryPart
{
    /**
     * Constructor
     */
    public FormPanelFormsInitializerQueryPart( )
    {
        super( );
        setFormPanelInitializerSelectQuery( new MatchAllDocsQuery( ) );
    }

    /**
     * Constructor used to build a query that selects the Forms that the user can access
     * 
     * @param user
     *            The HTTP user
     */
    public FormPanelFormsInitializerQueryPart( User user )
    {
        super( );

        // Get the List of all available Forms
        List<Form> listForms = FormHome.getFormList( );
        // Only keep the Forms that can be accessed by the current user
        listForms = (List<Form>) AdminWorkgroupService.getAuthorizedCollection( listForms, user );

        // Create Lucene queries to retrieve the Forms that match the specified IDs
        List<Query> queries = listForms.stream( ).map( form -> IntPoint.newExactQuery( FormsConstants.PARAMETER_ID_FORM, form.getId( ) ) )
                .collect( Collectors.toList( ) );

        // Create a Lucene Builder for Boolean Queries
        Builder builder = new BooleanQuery.Builder( );
        // Add all the created queries that will be applied
        queries.forEach( query -> builder.add( query, BooleanClause.Occur.SHOULD ) );

        // Initialize the Query used to retrieve specific Forms
        Query queryForms = builder.build( );

        setFormPanelInitializerSelectQuery( queryForms );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void buildFormPanelInitializerQuery( FormParameters formParameters )
    {
        // There is nothing to do with the FormParameters for this FormPanelInitializer
    }

}
