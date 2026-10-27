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
package fr.paris.lutece.plugins.forms.business.form.panel.initializer.querypart.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * The query of the multiview that selects the responses of the forms a user can access
 */
public class FormPanelFormsInitializerQueryPartTest extends LuteceTestCase
{
    private static final int UNKNOWN_FORM_ID = -42;

    private List<Form> _listForms;

    /**
     * Creates one form more than the clauses a boolean query of Lucene accepts.
     */
    @BeforeEach
    protected void createForms( )
    {
        _listForms = new ArrayList<>( );
        for ( int i = 0; i <= BooleanQuery.getMaxClauseCount( ); i++ )
        {
            Form form = new Form( );
            form.setTitle( "FormPanelFormsInitializerQueryPartTest " + i );
            form.setBreadcrumbName( "forms.horizontalBreadcrumb" );
            FormHome.create( form );
            _listForms.add( form );
        }
    }

    /**
     * Removes the forms created by the test.
     */
    @AfterEach
    protected void removeForms( )
    {
        _listForms.forEach( form -> FormHome.remove( form.getId( ) ) );
    }

    /**
     * With more forms than the clauses Lucene accepts in a boolean query, the query is still built and selects the responses of the first and of
     * the last form the user can access, and no response of a form that does not exist.
     *
     * @throws Exception
     *             if the index cannot be written or searched
     */
    @Test
    public void testQueryOfMoreFormsThanTheClauseLimit( ) throws Exception
    {
        Query query = new FormPanelFormsInitializerQueryPart( new AdminUser( ) ).getFormPanelInitializerSelectQuery( );

        try ( Directory directory = new ByteBuffersDirectory( ) )
        {
            try ( IndexWriter writer = new IndexWriter( directory, new IndexWriterConfig( new StandardAnalyzer( ) ) ) )
            {
                writer.addDocument( responseOfForm( _listForms.get( 0 ).getId( ) ) );
                writer.addDocument( responseOfForm( _listForms.get( _listForms.size( ) - 1 ).getId( ) ) );
                writer.addDocument( responseOfForm( UNKNOWN_FORM_ID ) );
            }
            try ( DirectoryReader reader = DirectoryReader.open( directory ) )
            {
                assertEquals( 2, new IndexSearcher( reader ).count( query ) );
            }
        }
    }

    /**
     * Builds the indexed document of a response, holding only the id of its form.
     *
     * @param nIdForm
     *            the id of the form
     * @return the document
     */
    private static Document responseOfForm( int nIdForm )
    {
        Document document = new Document( );
        document.add( new IntPoint( FormsConstants.PARAMETER_ID_FORM, nIdForm ) );
        return document;
    }
}
