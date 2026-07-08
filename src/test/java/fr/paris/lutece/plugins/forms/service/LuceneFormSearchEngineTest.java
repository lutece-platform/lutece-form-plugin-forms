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
package fr.paris.lutece.plugins.forms.service;

import fr.paris.lutece.plugins.forms.business.form.FormItemSortConfig;
import fr.paris.lutece.plugins.forms.business.form.FormParameters;
import fr.paris.lutece.plugins.forms.business.form.filter.querypart.IFormFilterLuceneQueryPart;
import fr.paris.lutece.plugins.forms.business.form.filter.querypart.IFormFilterQueryPart;
import fr.paris.lutece.plugins.forms.business.form.panel.FormPanel;
import fr.paris.lutece.plugins.forms.business.form.search.FormResponseSearchItem;

import fr.paris.lutece.plugins.forms.service.search.LuceneFormSearchEngine;
import fr.paris.lutece.plugins.forms.service.search.LuceneFormSearchFactory;
import fr.paris.lutece.plugins.forms.util.NaturalSortKeyBuilder;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.SortedNumericDocValuesField;
import org.apache.lucene.document.SortedSetDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class LuceneFormSearchEngineTest
{
    private static final String FIELD_ID = FormResponseSearchItem.FIELD_ID_FORM_RESPONSE;

    private LuceneFormSearchEngine _engine;
    private Directory _directory;

    @Before
    public void setUp( )
    {
        _engine = new LuceneFormSearchEngine( );
    }

    private List<IFormFilterQueryPart> matchAllFilter( )
    {
        return Collections.singletonList( new MatchAllFilterQueryPart( ) );
    }

    @Test
    public void testGetSearchResults_indexDoesNotExist_returnsEmptyList( ) throws Exception
    {
        _directory = new ByteBuffersDirectory( );
        injectFactory( new StubLuceneFormSearchFactory( _directory ) );

        FormPanel formPanel = new FormPanel( );
        List<FormResponseSearchItem> listResults = _engine.getSearchResults(
                Collections.emptyList( ), Collections.emptyList( ), matchAllFilter( ),
                null, 0, 10, formPanel );

        assertEquals( 0, listResults.size( ) );
    }

    @Test
    public void testGetSearchResults_noSortConfig_returnsUnsortedResults( ) throws Exception
    {
        String sFieldName = "entry_code_question_1_iter_0";
        buildIndex( new String [ ] { "3", "1", "2" }, sFieldName, new String [ ] { "Cherry", "Apple", "Banana" } );
        injectFactory( new StubLuceneFormSearchFactory( _directory ) );

        FormPanel formPanel = new FormPanel( );
        List<FormResponseSearchItem> listResults = _engine.getSearchResults(
                Collections.emptyList( ), Collections.emptyList( ), matchAllFilter( ),
                null, 0, 10, formPanel );

        assertEquals( 3, listResults.size( ) );
    }

    @Test
    public void testGetSearchResults_simpleFieldSort_ascending( ) throws Exception
    {
        String sFieldName = "entry_code_question_1_iter_0";
        buildIndex( new String [ ] { "3", "1", "2" }, sFieldName, new String [ ] { "Cherry", "Apple", "Banana" } );
        injectFactory( new StubLuceneFormSearchFactory( _directory ) );

        FormItemSortConfig sortConfig = new FormItemSortConfig( 0, sFieldName, false );

        FormPanel formPanel = new FormPanel( );
        List<FormResponseSearchItem> listResults = _engine.getSearchResults(
                Collections.emptyList( ), Collections.emptyList( ), matchAllFilter( ),
                sortConfig, 0, 10, formPanel );

        List<String> listIds = extractIds( listResults );
        // Apple(id 1) < Banana(id 2) < Cherry(id 3)
        assertEquals( Arrays.asList( "1", "2", "3" ), listIds );
    }

    @Test
    public void testGetSearchResults_dateSuffixSort_usesNumericSort( ) throws Exception
    {
        String sFieldName = "entry_code_question_1_iter_0" + FormResponseSearchItem.FIELD_DATE_SUFFIX;
        buildLongValueIndex( new String [ ] { "3", "1", "2" }, sFieldName, new long [ ] { 3000L, 1000L, 2000L } );
        injectFactory( new StubLuceneFormSearchFactory( _directory ) );

        FormItemSortConfig sortConfig = new FormItemSortConfig( 0, sFieldName, false );

        FormPanel formPanel = new FormPanel( );
        List<FormResponseSearchItem> listResults = _engine.getSearchResults(
                Collections.emptyList( ), Collections.emptyList( ), matchAllFilter( ),
                sortConfig, 0, 10, formPanel );

        List<String> listIds = extractIds( listResults );
        assertEquals( Arrays.asList( "1", "2", "3" ), listIds );
    }

    @Test
    public void testGetSearchResults_intSuffixSort_usesNumericSort( ) throws Exception
    {
        String sFieldName = "entry_code_question_1_iter_0" + FormResponseSearchItem.FIELD_INT_SUFFIX;
        buildLongValueIndex( new String [ ] { "3", "1", "2" }, sFieldName, new long [ ] { 30L, 1L, 2L } );
        injectFactory( new StubLuceneFormSearchFactory( _directory ) );

        FormItemSortConfig sortConfig = new FormItemSortConfig( 0, sFieldName, false );

        FormPanel formPanel = new FormPanel( );
        List<FormResponseSearchItem> listResults = _engine.getSearchResults(
                Collections.emptyList( ), Collections.emptyList( ), matchAllFilter( ),
                sortConfig, 0, 10, formPanel );

        List<String> listIds = extractIds( listResults );
        assertEquals( Arrays.asList( "1", "2", "3" ), listIds );
    }

    @Test
    public void testGetSearchResults_selectFieldSort_alphaNumericOrder( ) throws Exception
    {

        String sFieldName = "entry_code_question_2_iter_0" + FormResponseSearchItem.FIELD_SELECT_SUFFIX;
        buildIndex(
                new String [ ] { "1", "2", "3", "4", "5" },
                sFieldName,
                new String [ ] { "rep_10", "rep_2", "rep_1", "rep_3", "rep_20" }
        );
        injectFactory( new StubLuceneFormSearchFactory( _directory ) );

        FormItemSortConfig sortConfig = new FormItemSortConfig( 0, sFieldName, false );

        FormPanel formPanel = new FormPanel( );
        List<FormResponseSearchItem> listResults = _engine.getSearchResults(
                Collections.emptyList( ), Collections.emptyList( ), matchAllFilter( ),
                sortConfig, 0, 10, formPanel );

        List<String> listIds = extractIds( listResults );
        assertEquals( Arrays.asList( "3", "2", "4", "1", "5" ), listIds );
    }

    @Test
    public void testGetSearchResults_selectFieldSort_descending( ) throws Exception
    {
        String sFieldName = "entry_code_question_2_iter_0" + FormResponseSearchItem.FIELD_SELECT_SUFFIX;
        buildIndex(
                new String [ ] { "1", "2", "3" },
                sFieldName,
                new String [ ] { "rep_10", "rep_2", "rep_1" }
        );
        injectFactory( new StubLuceneFormSearchFactory( _directory ) );

        FormItemSortConfig sortConfig = new FormItemSortConfig( 0, sFieldName, true );

        FormPanel formPanel = new FormPanel( );
        List<FormResponseSearchItem> listResults = _engine.getSearchResults(
                Collections.emptyList( ), Collections.emptyList( ), matchAllFilter( ),
                sortConfig, 0, 10, formPanel );

        List<String> listIds = extractIds( listResults );
        assertEquals( Arrays.asList( "1", "2", "3" ), listIds );
    }

    @Test
    public void testGetSearchResults_selectFieldSort_pagination( ) throws Exception
    {
        String sFieldName = "entry_code_question_2_iter_0" + FormResponseSearchItem.FIELD_SELECT_SUFFIX;
        buildIndex(
                new String [ ] { "1", "2", "3" },
                sFieldName,
                new String [ ] { "rep_10", "rep_2", "rep_1" }
        );
        injectFactory( new StubLuceneFormSearchFactory( _directory ) );

        FormItemSortConfig sortConfig = new FormItemSortConfig( 0, sFieldName, false );

        FormPanel formPanel = new FormPanel( );
        List<FormResponseSearchItem> listResults = _engine.getSearchResults(
                Collections.emptyList( ), Collections.emptyList( ), matchAllFilter( ),
                sortConfig, 1, 1, formPanel );

        List<String> listIds = extractIds( listResults );
        assertEquals( 1, listIds.size( ) );
        assertEquals( "2", listIds.get( 0 ) );
    }

    @Test
    public void testGetSearchResults_multiValuedField_doesNotThrowAndSortsByMinValue( ) throws Exception
    {
        // Reproduces a checkbox with several options ticked at the same iteration : several
        // SortedSetDocValuesField values under the same field name, on the same document.
        // Before the fix, a single-valued SortedDocValuesField here made Lucene throw
        // IllegalArgumentException, and the response was never indexed at all.
        String sFieldName = "entry_code_question_3_iter_0" + FormResponseSearchItem.FIELD_SELECT_SUFFIX;
        NaturalSortKeyBuilder sortKeyBuilder = new NaturalSortKeyBuilder( );

        _directory = new ByteBuffersDirectory( );
        try ( IndexWriter writer = new IndexWriter( _directory, new IndexWriterConfig( new StandardAnalyzer( ) ) ) )
        {
            // doc "1" : options "rep_10" and "rep_2" both ticked -> MIN should be "rep_2"
            Document doc1 = new Document( );
            doc1.add( new StringField( FIELD_ID, "1", Field.Store.YES ) );
            doc1.add( new NumericDocValuesField( FIELD_ID, 1 ) );
            doc1.add( new SortedSetDocValuesField( sFieldName, new BytesRef( sortKeyBuilder.build( "rep_10" ) ) ) );
            doc1.add( new SortedSetDocValuesField( sFieldName, new BytesRef( sortKeyBuilder.build( "rep_2" ) ) ) );
            writer.addDocument( doc1 );

            // doc "2" : single option "rep_1" ticked
            Document doc2 = new Document( );
            doc2.add( new StringField( FIELD_ID, "2", Field.Store.YES ) );
            doc2.add( new NumericDocValuesField( FIELD_ID, 2 ) );
            doc2.add( new SortedSetDocValuesField( sFieldName, new BytesRef( sortKeyBuilder.build( "rep_1" ) ) ) );
            writer.addDocument( doc2 );
        }
        injectFactory( new StubLuceneFormSearchFactory( _directory ) );

        FormItemSortConfig sortConfig = new FormItemSortConfig( 0, sFieldName, false );

        FormPanel formPanel = new FormPanel( );
        List<FormResponseSearchItem> listResults = _engine.getSearchResults(
                Collections.emptyList( ), Collections.emptyList( ), matchAllFilter( ),
                sortConfig, 0, 10, formPanel );

        List<String> listIds = extractIds( listResults );
        // doc "2" (min="rep_1") sorts before doc "1" (min="rep_2", since "rep_10" is discarded by MIN)
        assertEquals( Arrays.asList( "2", "1" ), listIds );
    }

    private void buildIndex( String [ ] arrIds, String sFieldName, String [ ] arrValues ) throws Exception
    {
        _directory = new ByteBuffersDirectory( );
        NaturalSortKeyBuilder sortKeyBuilder = new NaturalSortKeyBuilder( );
        try ( IndexWriter writer = new IndexWriter( _directory, new IndexWriterConfig( new StandardAnalyzer( ) ) ) )
        {
            for ( int i = 0; i < arrIds.length; i++ )
            {
                Document doc = new Document( );
                doc.add( new StringField( FIELD_ID, arrIds [i], Field.Store.YES ) );
                // Tie-break criterion in buildLuceneSort reads FIELD_ID_FORM_RESPONSE via SortField.Type.INT,
                // which requires a NumericDocValuesField, matching what LuceneFormSearchIndexer#getDocument produces.
                doc.add( new NumericDocValuesField( FIELD_ID, Integer.parseInt( arrIds [i] ) ) );
                doc.add( new StringField( sFieldName, arrValues [i], Field.Store.YES ) );
                // Same docvalues type (SortedSetDocValuesField) and encoding (NaturalSortKeyBuilder) as production,
                // so that SortedSetSortField in buildLuceneSort can be exercised the same way it runs in practice.
                doc.add( new SortedSetDocValuesField( sFieldName, new BytesRef( sortKeyBuilder.build( arrValues [i] ) ) ) );
                writer.addDocument( doc );
            }
        }
    }

    private void buildLongValueIndex( String [ ] arrIds, String sFieldName, long [ ] arrValues ) throws Exception
    {
        _directory = new ByteBuffersDirectory( );
        try ( IndexWriter writer = new IndexWriter( _directory, new IndexWriterConfig( new StandardAnalyzer( ) ) ) )
        {
            for ( int i = 0; i < arrIds.length; i++ )
            {
                Document doc = new Document( );
                doc.add( new StringField( FIELD_ID, arrIds [i], Field.Store.YES ) );
                doc.add( new StringField( sFieldName, String.valueOf( arrValues [i] ), Field.Store.YES ) );
                doc.add( new SortedNumericDocValuesField( sFieldName, arrValues [i] ) );
                writer.addDocument( doc );
            }
        }
    }

    private List<String> extractIds( List<FormResponseSearchItem> listResults )
    {
        List<String> listIds = new ArrayList<>( );
        for ( FormResponseSearchItem item : listResults )
        {
            listIds.add( String.valueOf( item.getIdFormResponse( ) ) );
        }
        return listIds;
    }

    private void injectFactory( LuceneFormSearchFactory factory ) throws Exception
    {
        java.lang.reflect.Field field = LuceneFormSearchEngine.class.getDeclaredField( "_luceneFormSearchFactory" );
        field.setAccessible( true );
        field.set( _engine, factory );
    }

    private static class StubLuceneFormSearchFactory extends LuceneFormSearchFactory
    {
        private final Directory _stubDirectory;

        StubLuceneFormSearchFactory( Directory directory )
        {
            _stubDirectory = directory;
        }

        @Override
        public Directory getDirectory( )
        {
            return _stubDirectory;
        }
    }

    private static class MatchAllFilterQueryPart implements IFormFilterQueryPart, IFormFilterLuceneQueryPart
    {
        @Override
        public Query getFormFilterQuery( )
        {
            return new MatchAllDocsQuery( );
        }

        @Override
        public void buildFormFilterQuery( FormParameters formParameters )
        {
        }
    }
}