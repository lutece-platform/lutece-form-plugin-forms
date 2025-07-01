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

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import org.springframework.util.FileSystemUtils;

/**
 * Factory for the search on Directory
 */
public class LuceneFormSearchFactory
{
    // Constants
    private static final String PATH_INDEX = "forms.internalIndexer.lucene.indexPath";
    private static final String PATH_INDEX_IN_WEBAPP = "forms.internalIndexer.lucene.indexInWebapp";
    private static final String PATH_SUFFIX_TEMPORARY_PATH = "Temp";


    // Variables
    @Inject
    @Named( value = "forms.luceneFrenchAnalizer" )
    private Analyzer _analyzer;

    private IndexWriter _indexWriter;

    /**
     * Return the Analyzer to use for the search
     * 
     * @return the Analyzer to use for the search
     */
    public Analyzer getAnalyzer( )
    {
        return _analyzer;
    }

    /**
     * Create the IndexWriter with its configuration
     * 
     * @param bCreateIndex
     *            The boolean which tell if the index must be created
     * @param mainDirectory
     *            The boolean which tell if the index must be created on the main Directory or in a temporary Directory
     * @return the created IndexWriter
     */
    public IndexWriter getIndexWriter( Boolean bCreateIndex, boolean mainDirectory)
    {

        if( !mainDirectory && _indexWriter != null && _indexWriter.isOpen( ) )
        {
            try {
                _indexWriter.close();
            } catch (IOException e) {
                AppLogService.error( "Unable to close a old Lucene Index Writer", e );
            }
        }

        if ( _indexWriter == null || !_indexWriter.isOpen( ) )
        {
            try
            {
                Directory luceneDirectory;
                if ( mainDirectory )
                {
                    luceneDirectory = getDirectory();
                }
                else
                {
                    luceneDirectory = getDirectoryTemp();
                }

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
            catch( IOException e )
            {
                AppLogService.error( "Unable to create a new Lucene Index Writer", e );
                return null;
            }
        }
        return _indexWriter;

    }


    /**
     * Return path to the directory
     *
     * @param tempDirectory if true return path for the temporary directory
     * @return String Path
     */
    public String getPathDirectory(boolean tempDirectory)
    {
        String strIndex;

        boolean indexInWebapp = AppPropertiesService.getPropertyBoolean( PATH_INDEX_IN_WEBAPP, true );
        if ( indexInWebapp )
        {
            strIndex = AppPathService.getPath( PATH_INDEX );
        }
        else
        {
            strIndex = AppPropertiesService.getProperty( PATH_INDEX );
        }

        if (!tempDirectory)
        {
            return strIndex;
        }
        else {
            return strIndex + PATH_SUFFIX_TEMPORARY_PATH;
        }
    }

    /**
     * Return the Directory to use for the search
     * 
     * @return the Directory to use for the search
     * @throws IOException
     *             - if the path string cannot be converted to a Path
     */
    public Directory getDirectory( ) throws IOException
    {
        return FSDirectory.open( Paths.get( getPathDirectory( false ) ) );
    }

    /**
     * Return the temporary Directory to use for the search
     *
     * @return the Directory to use for the search
     * @throws IOException
     *             - if the path string cannot be converted to a Path
     */
    public Directory getDirectoryTemp( ) throws IOException
    {
        return FSDirectory.open( Paths.get( getPathDirectory( true ) ) );
    }

    /**
     * Delete the old main directory and move the temporary to the main Path
     *
     */
    public void swapIndex( )
    {

        if ( _indexWriter != null && _indexWriter.isOpen() ) {
            try {
                _indexWriter.close();
            } catch (IOException e) {
                AppLogService.error("Unable to close index writer ", e);
            }
        }

        Path mainPath = Paths.get( getPathDirectory( false ) );
        Path tempPath = Paths.get( getPathDirectory( true ) );

        try {
            FileSystemUtils.deleteRecursively( mainPath );
            Files.move( tempPath, mainPath );

        } catch (IOException e) {
            AppLogService.error( "Unable to swap lucene path", e );
        }

    }
}
