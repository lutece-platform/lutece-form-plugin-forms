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
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * NOTE : {@link LuceneFormSearchEngine} lit ici via {@code LuceneFormSearchFactory#acquireSearcher()}/
 * {@code releaseSearcher()} (pool de recherche géré par un {@code SearcherManager}), pas via un {@code Directory}
 * ouvert directement. Le stub ci-dessous surcharge donc {@code acquireSearcher()}/{@code releaseSearcher()} plutôt que
 * {@code getDirectory()}, en construisant un {@link IndexSearcher} unique à partir du {@link Directory} de test.
 */
public class LuceneFormSearchEngineTest
{
    private static final String FIELD_ID = FormResponseSearchItem.FIELD_ID_FORM_RESPONSE;

    private LuceneFormSearchEngine _engine;
    private Directory _directory;

    @BeforeEach
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

        // isAscSort()=true : demande explicitement l'ordre ascendant (voir buildLuceneSort, qui négie ce booléen
        // pour obtenir le "reverse" attendu par Lucene).
        FormItemSortConfig sortConfig = new FormItemSortConfig( 0, sFieldName, true );

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

        FormItemSortConfig sortConfig = new FormItemSortConfig( 0, sFieldName, true );

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

        FormItemSortConfig sortConfig = new FormItemSortConfig( 0, sFieldName, true );

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

        FormItemSortConfig sortConfig = new FormItemSortConfig( 0, sFieldName, true );

        FormPanel formPanel = new FormPanel( );
        List<FormResponseSearchItem> listResults = _engine.getSearchResults(
                Collections.emptyList( ), Collections.emptyList( ), matchAllFilter( ),
                sortConfig, 0, 10, formPanel );

        List<String> listIds = extractIds( listResults );
        // Ordre alphanumérique attendu : rep_1, rep_2, rep_3, rep_10, rep_20
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

        // isAscSort()=false : demande explicitement l'ordre descendant.
        FormItemSortConfig sortConfig = new FormItemSortConfig( 0, sFieldName, false );

        FormPanel formPanel = new FormPanel( );
        List<FormResponseSearchItem> listResults = _engine.getSearchResults(
                Collections.emptyList( ), Collections.emptyList( ), matchAllFilter( ),
                sortConfig, 0, 10, formPanel );

        List<String> listIds = extractIds( listResults );
        // Descendant : rep_10, rep_2, rep_1 => id 1, 2, 3
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

        FormItemSortConfig sortConfig = new FormItemSortConfig( 0, sFieldName, true );

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
        // Reproduit une checkbox avec plusieurs options cochées à la même itération : plusieurs
        // valeurs SortedSetDocValuesField sous le même nom de champ, sur le même document.
        // Avant le fix, un SortedDocValuesField mono-valeur ici faisait planter Lucene avec
        // IllegalArgumentException, et la réponse n'était jamais indexée du tout.
        String sFieldName = "entry_code_question_3_iter_0" + FormResponseSearchItem.FIELD_SELECT_SUFFIX;
        NaturalSortKeyBuilder sortKeyBuilder = new NaturalSortKeyBuilder( );

        _directory = new ByteBuffersDirectory( );
        try ( IndexWriter writer = new IndexWriter( _directory, new IndexWriterConfig( new StandardAnalyzer( ) ) ) )
        {
            // doc "1" : options "rep_10" et "rep_2" cochées -> MIN doit être "rep_2"
            Document doc1 = new Document( );
            doc1.add( new StringField( FIELD_ID, "1", Field.Store.YES ) );
            doc1.add( new NumericDocValuesField( FIELD_ID, 1 ) );
            doc1.add( new SortedSetDocValuesField( sFieldName, new BytesRef( sortKeyBuilder.build( "rep_10" ) ) ) );
            doc1.add( new SortedSetDocValuesField( sFieldName, new BytesRef( sortKeyBuilder.build( "rep_2" ) ) ) );
            writer.addDocument( doc1 );

            // doc "2" : une seule option "rep_1" cochée
            Document doc2 = new Document( );
            doc2.add( new StringField( FIELD_ID, "2", Field.Store.YES ) );
            doc2.add( new NumericDocValuesField( FIELD_ID, 2 ) );
            doc2.add( new SortedSetDocValuesField( sFieldName, new BytesRef( sortKeyBuilder.build( "rep_1" ) ) ) );
            writer.addDocument( doc2 );
        }
        injectFactory( new StubLuceneFormSearchFactory( _directory ) );

        FormItemSortConfig sortConfig = new FormItemSortConfig( 0, sFieldName, true );

        FormPanel formPanel = new FormPanel( );
        List<FormResponseSearchItem> listResults = _engine.getSearchResults(
                Collections.emptyList( ), Collections.emptyList( ), matchAllFilter( ),
                sortConfig, 0, 10, formPanel );

        List<String> listIds = extractIds( listResults );
        // doc "2" (min="rep_1") passe avant doc "1" (min="rep_2", "rep_10" étant écarté par MIN)
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
                // Le critère de départage dans buildLuceneSort lit FIELD_ID_FORM_RESPONSE via
                // SortField.Type.INT, qui nécessite un NumericDocValuesField, comme en production.
                doc.add( new NumericDocValuesField( FIELD_ID, Integer.parseInt( arrIds [i] ) ) );
                doc.add( new StringField( sFieldName, arrValues [i], Field.Store.YES ) );
                // Même type de docvalues (SortedSetDocValuesField) et même encodage (NaturalSortKeyBuilder)
                // qu'en production, pour exercer SortedSetSortField exactement comme en pratique.
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

    /**
     * Stub adapté à l'architecture v8 : surcharge {@code acquireSearcher()}/{@code releaseSearcher()} (pool via
     * {@code SearcherManager} dans la classe réelle) plutôt que {@code getDirectory()}, en ouvrant un unique
     * {@link IndexSearcher} sur le {@link Directory} de test. Retourne {@code null} si l'index n'existe pas encore,
     * pour reproduire le comportement "index non trouvé" de la factory réelle.
     */
    private static class StubLuceneFormSearchFactory extends LuceneFormSearchFactory
    {
        private final Directory _stubDirectory;
        private IndexSearcher _stubSearcher;

        StubLuceneFormSearchFactory( Directory directory )
        {
            _stubDirectory = directory;
        }

        @Override
        public IndexSearcher acquireSearcher( ) throws IOException
        {
            if ( !DirectoryReader.indexExists( _stubDirectory ) )
            {
                return null;
            }
            if ( _stubSearcher == null )
            {
                _stubSearcher = new IndexSearcher( DirectoryReader.open( _stubDirectory ) );
            }
            return _stubSearcher;
        }

        @Override
        public void releaseSearcher( IndexSearcher searcher )
        {
            // Pas de SearcherManager ici : rien à relâcher, le reader reste ouvert pour la durée du test.
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
