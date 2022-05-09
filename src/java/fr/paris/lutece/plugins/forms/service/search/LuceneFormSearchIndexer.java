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
package fr.paris.lutece.plugins.forms.service.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.BytesRef;
import org.springframework.beans.factory.annotation.Autowired;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponseStep;
import fr.paris.lutece.plugins.forms.business.form.search.FormResponseSearchItem;
import fr.paris.lutece.plugins.forms.business.form.search.IndexerAction;
import fr.paris.lutece.plugins.forms.business.form.search.IndexerActionFilter;
import fr.paris.lutece.plugins.forms.business.form.search.IndexerActionHome;
import fr.paris.lutece.plugins.forms.service.FormsPlugin;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeDate;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeNumbering;
import fr.paris.lutece.plugins.forms.util.LuceneUtils;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.service.state.StateService;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.search.SearchItem;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.url.UrlItem;

/**
 * Forms global indexer
 */
public class LuceneFormSearchIndexer implements IFormSearchIndexer
{
    public static final String INDEXER_NAME = "FormsIndexer";
    private static final String INDEXER_DESCRIPTION = "Indexer service for forms";
    private static final String FORMS = "forms";
    private static final String INDEXER_VERSION = "1.0.0";
    private static final String PROPERTY_INDEXER_ENABLE = "forms.globalIndexer.enable";
    private static final String FILTER_DATE_FORMAT = AppPropertiesService.getProperty( "forms.index.date.format", "dd/MM/yyyy" );
    private static final int TAILLE_LOT = AppPropertiesService.getPropertyInt( "forms.index.writer.commit.size", 100 );

    private static AtomicBoolean _bIndexIsRunning = new AtomicBoolean( false );
    private static AtomicBoolean _bIndexToLunch = new AtomicBoolean( false );

    @Inject
    private LuceneFormSearchFactory _luceneFormSearchFactory;
    private IndexWriter _indexWriter;
    @Autowired( required = false )
    private StateService _stateService;

    public LuceneFormSearchIndexer( )
    {
        IndexationService.registerIndexer( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName( )
    {
        return INDEXER_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription( )
    {
        return INDEXER_DESCRIPTION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getVersion( )
    {
        return INDEXER_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnable( )
    {
        return AppPropertiesService.getPropertyBoolean( PROPERTY_INDEXER_ENABLE, false );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getListType( )
    {
        List<String> listType = new ArrayList<>( 1 );
        listType.add( FORMS );

        return listType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSpecificSearchAppUrl( )
    {
        UrlItem url = new UrlItem( AppPathService.getPortalUrl( ) );
        url.addParameter( XPageAppService.PARAM_XPAGE_APP, FORMS );

        return url.getUrl( );
    }

    /**
     * Add Indexer Action to perform on a form response
     * 
     * @param nIdFormResponse
     *            the id of the formResponse
     * @param nIdTask
     *            the key of the action to do
     * @param plugin
     *            the plugin
     */
    @Override
    public void addIndexerAction( int nIdFormResponse, int nIdTask, Plugin plugin )
    {
        IndexerAction indexerAction = new IndexerAction( );
        indexerAction.setIdFormResponse( nIdFormResponse );
        indexerAction.setIdTask( nIdTask );
        IndexerActionHome.create( indexerAction, plugin );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized void indexDocuments( ) throws IOException, InterruptedException, SiteMessageException
    {
        List<Integer> listFormResponsesId = FormResponseHome.selectAllFormResponsesId( );

        deleteIndex( );
        _bIndexToLunch.set( true );
        if ( _bIndexIsRunning.compareAndSet( false, true ) )
        {
            new Thread( ( ) -> {
                try
                {
                    List<FormResponse> listFormResponses = new ArrayList<>( TAILLE_LOT );
                    for ( Integer nIdFormResponse : listFormResponsesId )
                    {
                        FormResponse response = FormResponseHome.findByPrimaryKeyForIndex( nIdFormResponse );
                        if ( response != null )
                        {
                            listFormResponses.add( response );
                        }
                        if ( listFormResponses.size( ) == TAILLE_LOT )
                        {
                            indexFormResponseList( listFormResponses );
                            listFormResponses.clear( );
                        }
                    }
                    indexFormResponseList( listFormResponses );
                    // Indexation increment
                    while ( _bIndexToLunch.compareAndSet( true, false ) )
                    {
                        processIndexing( );

                    }
                }
                catch( Exception e )
                {
                    AppLogService.error( e.getMessage( ), e );
                    Thread.currentThread( ).interrupt( );
                }
                finally
                {
                    _bIndexIsRunning.set( false );
                }

            } ).start( );
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void indexDocument( int nIdFormResponse, int nIdTask, Plugin plugin )
    {
        addIndexerAction( nIdFormResponse, nIdTask, plugin );
        _bIndexToLunch.set( true );
        if ( _bIndexIsRunning.compareAndSet( false, true ) )
        {
            new Thread( ( ) -> {
                try
                {
                    while ( _bIndexToLunch.compareAndSet( true, false ) )
                    {
                        processIndexing( );
                    }
                }
                catch( Exception e )
                {
                    AppLogService.error( e.getMessage( ), e );
                    Thread.currentThread( ).interrupt( );
                }
                finally
                {
                    _bIndexIsRunning.set( false );
                }

            } ).start( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized void processIndexing( )
    {
        initIndexing( false );

        Plugin plugin = PluginService.getPlugin( FormsPlugin.PLUGIN_NAME );
        Set<Integer> listIdsToAdd = new HashSet<>( );
        Set<Integer> listIdsToDelete = new HashSet<>( );

        // Delete all record which must be delete
        for ( IndexerAction action : getAllIndexerActionByTask( IndexerAction.TASK_DELETE, plugin ) )
        {
            listIdsToDelete.add( action.getIdFormResponse( ) );
            removeIndexerAction( action.getIdAction( ), plugin );
        }

        // Update all record which must be update
        for ( IndexerAction action : getAllIndexerActionByTask( IndexerAction.TASK_MODIFY, plugin ) )
        {
            listIdsToDelete.add( action.getIdFormResponse( ) );
            listIdsToAdd.add( action.getIdFormResponse( ) );
            removeIndexerAction( action.getIdAction( ), plugin );
        }

        // Add all form response which must be add
        for ( IndexerAction action : getAllIndexerActionByTask( IndexerAction.TASK_CREATE, plugin ) )
        {
            listIdsToAdd.add( action.getIdFormResponse( ) );
            removeIndexerAction( action.getIdAction( ), plugin );
        }

        List<Query> queryList = new ArrayList<>( TAILLE_LOT );
        for ( Integer nIdFormResponse : listIdsToDelete )
        {
            queryList.add( IntPoint.newExactQuery( FormResponseSearchItem.FIELD_ID_FORM_RESPONSE, nIdFormResponse ) );
            if ( queryList.size( ) == TAILLE_LOT )
            {
                deleteDocument( queryList );
                queryList.clear( );
            }
        }
        deleteDocument( queryList );

        List<FormResponse> listFormResponses = new ArrayList<>( TAILLE_LOT );
        for ( Integer nIdFormResponse : listIdsToAdd )
        {
            FormResponse response = FormResponseHome.findByPrimaryKeyForIndex( nIdFormResponse );
            if ( response != null )
            {
                listFormResponses.add( response );
            }
            if ( listFormResponses.size( ) == TAILLE_LOT )
            {
                indexFormResponseList( listFormResponses );
                listFormResponses.clear( );
            }
        }
        indexFormResponseList( listFormResponses );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Document> getDocuments( String formResponseId ) throws IOException, InterruptedException, SiteMessageException
    {
        int nIdFormResponse;

        try
        {
            nIdFormResponse = Integer.parseInt( formResponseId );
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( formResponseId + " not parseable to an int", ne );

            return new ArrayList<>( 0 );
        }

        FormResponse formResponse = FormResponseHome.findByPrimaryKey( nIdFormResponse );
        Form form = FormHome.findByPrimaryKey( formResponse.getFormId( ) );
        State formResponseState = null;
        if ( _stateService != null )
        {
            formResponseState = _stateService.findByResource( formResponse.getId( ), FormResponse.RESOURCE_TYPE, form.getIdWorkflow( ) );
        }

        Document doc = getDocument( formResponse, form, formResponseState );
        if ( doc != null )
        {
            List<Document> listDocument = new ArrayList<>( 1 );
            listDocument.add( doc );

            return listDocument;
        }

        return new ArrayList<>( 0 );
    }

    /**
     * {@inheritDoc}
     */
    private void indexFormResponseList( List<FormResponse> listFormResponse )
    {
        if ( _indexWriter == null || !_indexWriter.isOpen( ) )
        {
            initIndexing( true );
        }

        Map<Integer, Form> mapForms = FormHome.getFormList( ).stream( ).collect( Collectors.toMap( Form::getId, form -> form ) );
        List<Document> documentList = new ArrayList<>( );
        for ( FormResponse formResponse : listFormResponse )
        {
            Document doc = null;
            Form form = mapForms.get( formResponse.getFormId( ) );
            State formResponseState = null;
            if ( _stateService != null )
            {
                formResponseState = _stateService.findByResource( formResponse.getId( ), FormResponse.RESOURCE_TYPE, form.getIdWorkflow( ) );
            }
            else
            {
                formResponseState = new State( );
                formResponseState.setId( -1 );
                formResponseState.setName( StringUtils.EMPTY );
            }

            try
            {
                doc = getDocument( formResponse, form, formResponseState );
            }
            catch( Exception e )
            {
                IndexationService.error( this, e, null );
            }

            if ( doc != null )
            {
                documentList.add( doc );
            }
        }
        if ( !documentList.isEmpty( ) )
        {
            addDocuments( documentList );
        }
        endIndexing( );
    }

    private void addDocuments( List<Document> documentList )
    {
        provideExternalFields( documentList );
        try
        {
            _indexWriter.addDocuments( documentList );
        }
        catch( IOException e )
        {
            AppLogService.error( "Unable to index form response", e );
        }
        documentList.clear( );
    }

    /**
     * Provide external fields to Document objects
     * 
     * @param documentList
     *            list of Document objects
     *
     */
    private void provideExternalFields( List<Document> documentList )
    {
        for ( ILucenDocumentExternalFieldProvider provider : SpringContextService.getBeansOfType( ILucenDocumentExternalFieldProvider.class ) )
        {
            provider.provideFields( documentList );
        }
    }

    /**
     * Init the indexing action
     * 
     * @param bCreate
     */
    private void initIndexing( boolean bCreate )
    {
        Boolean boolCreate = Boolean.valueOf( bCreate );
        _indexWriter = _luceneFormSearchFactory.getIndexWriter( boolCreate );
    }

    /**
     * End the indexing action
     */
    private void endIndexing( )
    {
        if ( _indexWriter != null )
        {
            try
            {
                _indexWriter.commit( );
            }
            catch( IOException e )
            {
                AppLogService.error( "Unable to close index writer ", e );
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    private void deleteIndex( )
    {
        if ( _indexWriter == null || !_indexWriter.isOpen( ) )
        {
            initIndexing( true );
        }
        try
        {
            _indexWriter.deleteAll( );
        }
        catch( IOException e )
        {
            AppLogService.error( "Unable to delete all docs in index ", e );
        }
        finally
        {
            endIndexing( );
        }
    }

    private void deleteDocument( List<Query> luceneQueryList )
    {
        try
        {
            _indexWriter.deleteDocuments( luceneQueryList.toArray( new Query [ luceneQueryList.size( )] ) );
        }
        catch( IOException e )
        {
            AppLogService.error( "Unable to delete document ", e );
        }
    }

    /**
     * Remove a Indexer Action
     * 
     * @param nIdAction
     *            the key of the action to remove
     * @param plugin
     *            the plugin
     */
    private void removeIndexerAction( int nIdAction, Plugin plugin )
    {
        IndexerActionHome.remove( nIdAction, plugin );
    }

    /**
     * return a list of IndexerAction by task key
     * 
     * @param nIdTask
     *            the task kety
     * @param plugin
     *            the plugin
     * @return a list of IndexerAction
     */
    private List<IndexerAction> getAllIndexerActionByTask( int nIdTask, Plugin plugin )
    {
        IndexerActionFilter filter = new IndexerActionFilter( );
        filter.setIdTask( nIdTask );

        return IndexerActionHome.getList( filter, plugin );
    }

    /**
     * Builds a document which will be used by Lucene during the indexing of this record
     * 
     * @param formResponse
     *            the formResponse object
     * @param form
     *            the form
     * @return a lucene document filled with the record data
     */
    private Document getDocument( FormResponse formResponse, Form form, State formResponseState )
    {
        // make a new, empty document
        Document doc = new Document( );

        int nIdFormResponse = formResponse.getId( );

        // --- document identifier
        doc.add( new StringField( SearchItem.FIELD_UID, String.valueOf( nIdFormResponse ), Field.Store.YES ) );

        // --- form response identifier
        doc.add( new IntPoint( FormResponseSearchItem.FIELD_ID_FORM_RESPONSE, nIdFormResponse ) );
        doc.add( new NumericDocValuesField( FormResponseSearchItem.FIELD_ID_FORM_RESPONSE, nIdFormResponse ) );
        doc.add( new StoredField( FormResponseSearchItem.FIELD_ID_FORM_RESPONSE, nIdFormResponse ) );

        // --- field contents
        doc.add( new TextField( SearchItem.FIELD_CONTENTS, manageNullValue( getContentToIndex( formResponse ) ), Field.Store.NO ) );

        // --- form title
        String strFormTitle = manageNullValue( form.getTitle( ) );
        doc.add( new StringField( FormResponseSearchItem.FIELD_FORM_TITLE, strFormTitle, Field.Store.YES ) );
        doc.add( new SortedDocValuesField( FormResponseSearchItem.FIELD_FORM_TITLE, new BytesRef( strFormTitle ) ) );

        // --- id form
        doc.add( new IntPoint( FormResponseSearchItem.FIELD_ID_FORM, form.getId( ) ) );
        doc.add( new NumericDocValuesField( FormResponseSearchItem.FIELD_ID_FORM, form.getId( ) ) );
        doc.add( new StoredField( FormResponseSearchItem.FIELD_ID_FORM, form.getId( ) ) );

        // --- form response date create
        Long longCreationDate = formResponse.getCreation( ).getTime( );
        doc.add( new LongPoint( FormResponseSearchItem.FIELD_DATE_CREATION, longCreationDate ) );
        doc.add( new NumericDocValuesField( FormResponseSearchItem.FIELD_DATE_CREATION, longCreationDate ) );
        doc.add( new StoredField( FormResponseSearchItem.FIELD_DATE_CREATION, longCreationDate ) );

        // --- form response date closure
        Long longUpdateDate = formResponse.getUpdate( ).getTime( );
        doc.add( new LongPoint( FormResponseSearchItem.FIELD_DATE_UPDATE, longUpdateDate ) );
        doc.add( new NumericDocValuesField( FormResponseSearchItem.FIELD_DATE_UPDATE, longUpdateDate ) );
        doc.add( new StoredField( FormResponseSearchItem.FIELD_DATE_UPDATE, longUpdateDate ) );

        if ( formResponseState != null )
        {
            // --- id form response workflow state
            int nIdFormResponseWorkflowState = formResponseState.getId( );
            doc.add( new IntPoint( FormResponseSearchItem.FIELD_ID_WORKFLOW_STATE, nIdFormResponseWorkflowState ) );
            doc.add( new NumericDocValuesField( FormResponseSearchItem.FIELD_ID_WORKFLOW_STATE, nIdFormResponseWorkflowState ) );
            doc.add( new StoredField( FormResponseSearchItem.FIELD_ID_WORKFLOW_STATE, nIdFormResponseWorkflowState ) );

            // --- form response workflow state title
            String strFormResponseWorkflowStateTitle = manageNullValue( formResponseState.getName( ) );
            doc.add( new StringField( FormResponseSearchItem.FIELD_TITLE_WORKFLOW_STATE, strFormResponseWorkflowStateTitle, Field.Store.YES ) );
            doc.add( new SortedDocValuesField( FormResponseSearchItem.FIELD_TITLE_WORKFLOW_STATE, new BytesRef( strFormResponseWorkflowStateTitle ) ) );
        }

        // --- form response entry code / fields
        Set<String> setFieldNameBuilderUsed = new HashSet<>( );
        for ( FormResponseStep formResponseStep : formResponse.getSteps( ) )
        {
            for ( FormQuestionResponse formQuestionResponse : formResponseStep.getQuestions( ) )
            {
                String strQuestionCode = formQuestionResponse.getQuestion( ).getCode( );
                Entry entry = formQuestionResponse.getQuestion( ).getEntry( );
                IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( entry );

                for ( Response response : formQuestionResponse.getEntryResponse( ) )
                {
                    fr.paris.lutece.plugins.genericattributes.business.Field responseField = response.getField( );

                    if ( !StringUtils.isEmpty( response.getResponseValue( ) ) )
                    {
                        StringBuilder fieldNameBuilder = new StringBuilder(
                                LuceneUtils.createLuceneEntryKey( strQuestionCode, response.getIterationNumber( ) ) );

                        if ( responseField != null )
                        {
                            String getFieldName = getFieldName( responseField, response );
                            fieldNameBuilder.append( FormResponseSearchItem.FIELD_RESPONSE_FIELD_SEPARATOR );
                            fieldNameBuilder.append( getFieldName );
                        }

                        if ( !setFieldNameBuilderUsed.contains( fieldNameBuilder.toString( ) ) )
                        {
                            setFieldNameBuilderUsed.add( fieldNameBuilder.toString( ) );
                            if ( entryTypeService instanceof EntryTypeDate )
                            {
                                try
                                {
                                    Long timestamp = Long.valueOf( response.getResponseValue( ) );
                                    doc.add( new LongPoint( fieldNameBuilder.toString( ) + FormResponseSearchItem.FIELD_DATE_SUFFIX, timestamp ) );
                                    doc.add( new NumericDocValuesField( fieldNameBuilder.toString( ) + FormResponseSearchItem.FIELD_DATE_SUFFIX, timestamp ) );
                                    doc.add( new StoredField( fieldNameBuilder.toString( ) + FormResponseSearchItem.FIELD_DATE_SUFFIX, timestamp ) );
                                }
                                catch( Exception e )
                                {
                                    AppLogService.error( "Unable to parse " + response.getResponseValue( ) + " with date formatter " + FILTER_DATE_FORMAT, e );
                                }
                            }
                            else
                                if ( entryTypeService instanceof EntryTypeNumbering )
                                {
                                    try
                                    {
                                        Integer value = Integer.valueOf( response.getResponseValue( ) );
                                        doc.add( new IntPoint( fieldNameBuilder.toString( ) + FormResponseSearchItem.FIELD_INT_SUFFIX, value ) );
                                        doc.add( new NumericDocValuesField( fieldNameBuilder.toString( ) + FormResponseSearchItem.FIELD_INT_SUFFIX, value ) );
                                        doc.add( new StoredField( fieldNameBuilder.toString( ) + FormResponseSearchItem.FIELD_INT_SUFFIX, value ) );
                                    }
                                    catch( NumberFormatException e )
                                    {
                                        AppLogService.error( "Unable to parse " + response.getResponseValue( ) + " to integer ", e );
                                    }
                                }
                                else
                                {
                                    doc.add( new StringField( fieldNameBuilder.toString( ), response.getResponseValue( ), Field.Store.YES ) );
                                    doc.add( new SortedDocValuesField( fieldNameBuilder.toString( ), new BytesRef( response.getResponseValue( ) ) ) );
                                }

                        }
                        else
                        {
                            AppLogService.error( " FieldNameBuilder " + fieldNameBuilder.toString( ) + "  already used for formResponse.getId( )  "
                                    + formResponse.getId( ) + "  formQuestionResponse.getId( )  " + formQuestionResponse.getId( )
                                    + " response.getIdResponse( ) " + response.getIdResponse( ) + " formResponseStep" + formResponseStep.getId( ) );

                        }

                    }
                }
            }
        }

        return doc;
    }

    /**
     * Concatenates the value of the specified field in this record
     * 
     * @param record
     *            the record to seek
     * @param listEntry
     *            the list of field to concatenate
     * @param plugin
     *            the plugin object
     * @return
     */
    private String getContentToIndex( FormResponse formResponse )
    {

        StringBuilder sb = new StringBuilder( );

        for ( FormResponseStep formResponseStep : formResponse.getSteps( ) )
        {
            for ( FormQuestionResponse questionResponse : formResponseStep.getQuestions( ) )
            {

                // Only index the indexable entries
                if ( questionResponse.getQuestion( ).isResponsesIndexed( ) )
                {
                    Entry entry = questionResponse.getQuestion( ).getEntry( );
                    for ( Response response : questionResponse.getEntryResponse( ) )
                    {

                        String responseString = EntryTypeServiceManager.getEntryTypeService( entry ).getResponseValueForExport( entry, null, response, null );
                        if ( !StringUtils.isEmpty( responseString ) )
                        {
                            sb.append( responseString );
                            sb.append( " " );
                        }
                    }
                }
            }
        }

        return sb.toString( );
    }

    /**
     * Get the field name
     * 
     * @param responseField
     * @param response
     * @return the field name
     */
    private String getFieldName( fr.paris.lutece.plugins.genericattributes.business.Field responseField, Response response )
    {
        if ( responseField.getIdField( ) > 0 )
        {
            return String.valueOf( responseField.getIdField( ) );
        }
        if ( !StringUtils.isEmpty( responseField.getCode( ) ) )
        {
            return responseField.getCode( );
        }
        if ( !StringUtils.isEmpty( responseField.getTitle( ) ) )
        {
            return responseField.getTitle( );
        }
        return String.valueOf( response.getIdResponse( ) );
    }

    /**
     * Manage a given string null value
     * 
     * @param strValue
     * @return the string if not null, empty string otherwise
     */
    private String manageNullValue( String strValue )
    {
        if ( strValue == null )
        {
            return StringUtils.EMPTY;
        }
        return strValue;
    }

}
