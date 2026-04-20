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
package fr.paris.lutece.plugins.forms.service.search;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.apache.commons.io.file.PathUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * Shared factory for Lucene resources used by the forms index.
 *
 * Thread-safety: all state transitions on the writer, the long-lived search
 * Directory and the {@link SearcherManager} go through {@link #_lock}. This
 * guarantees that:
 * - {@link #getIndexWriter(Boolean, boolean)} never hands out an
 *   {@link IndexWriter} that is being closed or recreated concurrently;
 * - {@link #swapIndex()} closes every open resource before moving files on
 *   disk so no reader/writer is observing a half-moved directory;
 * - the {@link SearcherManager} is rebuilt atomically after each swap.
 *
 * In a multi-instance deployment with a shared index volume, the single-writer
 * invariant across JVMs is enforced by the {@code LuceneLockManagerDB}
 * distributed lock held by {@code LuceneFormSearchIndexer}; this class only
 * guarantees intra-JVM serialisation.
 */
@ApplicationScoped
public class LuceneFormSearchFactory
{
    private static final String PATH_INDEX = "forms.internalIndexer.lucene.indexPath";
    private static final String PATH_INDEX_IN_WEBAPP = "forms.internalIndexer.lucene.indexInWebapp";
    private static final String PATH_SUFFIX_TEMPORARY_PATH = "Temp";

    @Inject
    @Named( value = "forms.luceneFrenchAnalyzer" )
    private Analyzer _analyzer;

    /** Guards all state transitions below. */
    private final Object _lock = new Object( );

    /** Current write handle, {@code null} when no indexing session is open. */
    private volatile IndexWriter _indexWriter;

    /** Long-lived Directory held open for the read path so {@link SearcherManager} has a stable target. */
    private volatile Directory _searchDirectory;

    /** Pooled searchers for the main index. {@code null} until the first search, or while the index does not yet exist. */
    private volatile SearcherManager _searcherManager;

    public Analyzer getAnalyzer( )
    {
        return _analyzer;
    }

    /**
     * Create (or return the cached) {@link IndexWriter} on either the main or the temporary directory.
     * Concurrent callers serialise through the factory lock; existing temporary writers are closed
     * before switching target.
     */
    public IndexWriter getIndexWriter( Boolean bCreateIndex, boolean mainDirectory )
    {
        synchronized ( _lock )
        {
            if ( !mainDirectory && _indexWriter != null && _indexWriter.isOpen( ) )
            {
                closeWriterQuietly( );
            }

            if ( _indexWriter == null || !_indexWriter.isOpen( ) )
            {
                try
                {
                    Directory luceneDirectory = mainDirectory ? getDirectory( ) : getDirectoryTemp( );

                    IndexWriterConfig conf = new IndexWriterConfig( getAnalyzer( ) );
                    if ( Boolean.TRUE.equals( bCreateIndex ) || !DirectoryReader.indexExists( luceneDirectory ) )
                    {
                        conf.setOpenMode( OpenMode.CREATE );
                    }
                    else
                    {
                        conf.setOpenMode( OpenMode.APPEND );
                    }
                    _indexWriter = new IndexWriter( luceneDirectory, conf );
                }
                catch ( IOException e )
                {
                    AppLogService.error( "Unable to create a new Lucene Index Writer", e );
                    return null;
                }
            }
            return _indexWriter;
        }
    }

    /**
     * Resolve the configured index path (main or temporary). Package-visible so the
     * plugin initialisation can surface a warning if the path is JVM-local.
     *
     * @param tempDirectory {@code true} to append the temporary suffix
     * @return the absolute or webapp-relative path string
     */
    public String getPathDirectory( boolean tempDirectory )
    {
        boolean indexInWebapp = AppPropertiesService.getPropertyBoolean( PATH_INDEX_IN_WEBAPP, true );
        String strIndex = indexInWebapp ? AppPathService.getPath( PATH_INDEX ) : AppPropertiesService.getProperty( PATH_INDEX );
        return tempDirectory ? strIndex + PATH_SUFFIX_TEMPORARY_PATH : strIndex;
    }

    /**
     * Open a fresh {@link FSDirectory} on the main path. The caller owns the
     * returned handle and is responsible for closing it. Used by callers that
     * need a one-off Directory for {@link DirectoryReader#indexExists(Directory)}
     * or for writer construction.
     */
    public Directory getDirectory( ) throws IOException
    {
        return FSDirectory.open( Paths.get( getPathDirectory( false ) ) );
    }

    public Directory getDirectoryTemp( ) throws IOException
    {
        return FSDirectory.open( Paths.get( getPathDirectory( true ) ) );
    }

    /**
     * Acquire a pooled {@link IndexSearcher} on the main index. Must be paired
     * with {@link #releaseSearcher(IndexSearcher)}. Returns {@code null} if the
     * index does not yet exist on disk.
     */
    public IndexSearcher acquireSearcher( ) throws IOException
    {
        SearcherManager sm = ensureSearcherManager( );
        if ( sm == null )
        {
            return null;
        }
        try
        {
            sm.maybeRefresh( );
        }
        catch ( IOException e )
        {
            AppLogService.error( "Unable to refresh Lucene SearcherManager", e );
        }
        return sm.acquire( );
    }

    public void releaseSearcher( IndexSearcher searcher ) throws IOException
    {
        if ( searcher == null )
        {
            return;
        }
        SearcherManager sm = _searcherManager;
        if ( sm != null )
        {
            sm.release( searcher );
        }
    }

    /**
     * Signal that the main index has just been updated so subsequent
     * {@link #acquireSearcher()} calls observe the new view. Safe to call
     * when no reader has been opened yet (no-op in that case).
     */
    public void refreshSearcher( )
    {
        SearcherManager sm = _searcherManager;
        if ( sm == null )
        {
            return;
        }
        try
        {
            sm.maybeRefresh( );
        }
        catch ( IOException e )
        {
            AppLogService.error( "Unable to refresh Lucene SearcherManager", e );
        }
    }

    /**
     * Swap the freshly built temporary directory onto the main location.
     * Holds the factory lock for the whole operation so neither writers nor
     * the {@link SearcherManager} observe a partially-moved directory; both
     * are closed before the move and lazily reopened by the next caller.
     */
    public void swapIndex( )
    {
        synchronized ( _lock )
        {
            closeWriterQuietly( );
            closeSearcherManagerQuietly( );
            closeSearchDirectoryQuietly( );

            Path mainPath = Paths.get( getPathDirectory( false ) );
            Path tempPath = Paths.get( getPathDirectory( true ) );
            try
            {
                PathUtils.deleteDirectory( mainPath );
                Files.move( tempPath, mainPath );
            }
            catch ( IOException e )
            {
                AppLogService.error( "Unable to swap lucene path", e );
            }
        }
    }

    /**
     * Release every held resource. Invoked at application shutdown.
     */
    @PreDestroy
    public void shutdown( )
    {
        synchronized ( _lock )
        {
            closeWriterQuietly( );
            closeSearcherManagerQuietly( );
            closeSearchDirectoryQuietly( );
        }
    }

    private SearcherManager ensureSearcherManager( ) throws IOException
    {
        SearcherManager sm = _searcherManager;
        if ( sm != null )
        {
            return sm;
        }
        synchronized ( _lock )
        {
            if ( _searcherManager != null )
            {
                return _searcherManager;
            }
            if ( _searchDirectory == null )
            {
                _searchDirectory = FSDirectory.open( Paths.get( getPathDirectory( false ) ) );
            }
            if ( !DirectoryReader.indexExists( _searchDirectory ) )
            {
                return null;
            }
            _searcherManager = new SearcherManager( _searchDirectory, new SearcherFactory( ) );
            return _searcherManager;
        }
    }

    private void closeWriterQuietly( )
    {
        IndexWriter writer = _indexWriter;
        if ( writer != null && writer.isOpen( ) )
        {
            try
            {
                writer.close( );
            }
            catch ( IOException e )
            {
                AppLogService.error( "Unable to close Lucene IndexWriter", e );
            }
        }
        _indexWriter = null;
    }

    private void closeSearcherManagerQuietly( )
    {
        SearcherManager sm = _searcherManager;
        if ( sm != null )
        {
            try
            {
                sm.close( );
            }
            catch ( IOException e )
            {
                AppLogService.error( "Unable to close Lucene SearcherManager", e );
            }
        }
        _searcherManager = null;
    }

    private void closeSearchDirectoryQuietly( )
    {
        Directory dir = _searchDirectory;
        if ( dir != null )
        {
            try
            {
                dir.close( );
            }
            catch ( IOException e )
            {
                AppLogService.error( "Unable to close Lucene search Directory", e );
            }
        }
        _searchDirectory = null;
    }
}
