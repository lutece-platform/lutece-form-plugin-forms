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
import java.util.stream.Collectors;

import javax.inject.Inject;

import fr.paris.lutece.plugins.forms.exception.LockException;
import fr.paris.lutece.plugins.forms.service.lock.LockResult;
import fr.paris.lutece.plugins.forms.service.lock.LuceneLockManager;
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
import org.apache.lucene.index.DirectoryReader;
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
import fr.paris.lutece.plugins.forms.business.form.search.IndexerActionHome;
import fr.paris.lutece.plugins.forms.service.FormsPlugin;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeCheckBox;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeDate;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeNumbering;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeRadioButton;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeSelect;
import fr.paris.lutece.plugins.forms.util.LuceneUtils;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.service.state.StateService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.search.SearchItem;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * Forms global indexer
 */
public class LuceneFormSearchIndexer implements IFormSearchIndexer
{
    public static final String BEAN_SERVICE = "forms.luceneFormsSearchIndexer";
    private static final String LOCKNAME = "forms.lucene.lock";
    private static final String FILTER_DATE_FORMAT = AppPropertiesService.getProperty( "forms.index.date.format", "dd/MM/yyyy" );
    private static final int TAILLE_LOT = AppPropertiesService.getPropertyInt( "forms.index.writer.commit.size", 100 );
    private static final boolean CLOSE_WRITER = AppPropertiesService.getPropertyBoolean( "forms.index.writer.multi.apps", true );
    private static final long MS_TIMEOUT_LOCK = AppPropertiesService.getPropertyLong( "forms.index.writer.ms.timeout.lock", 3600000L );

    @Inject
    private LuceneFormSearchFactory _luceneFormSearchFactory;
    private IndexWriter _indexWriter;
    @Autowired( required = false )
    private StateService _stateService;
    private LuceneLockManager _lockManager;

    /**
     * Constructor
     *
     * @param lockManager
     *            The LockManager
     */
    @Inject
    public LuceneFormSearchIndexer( LuceneLockManager lockManager )
    {
        _lockManager = lockManager;
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
    public String fullIndexing( )
    {

        StringBuilder log = new StringBuilder();
        Plugin plugin = PluginService.getPlugin( FormsPlugin.PLUGIN_NAME );

        try
        {
            LockResult lockResult = _lockManager.acquireLock( LOCKNAME, MS_TIMEOUT_LOCK );
            log.append("Full indexing launch");

            try
            {
                IndexerActionHome.removeAll( plugin );
                List<Integer> listFormResponsesId = FormResponseHome.selectAllFormResponsesId( );

                initIndexing( true );

                deleteIndex();
                indexFormResponseList( listFormResponsesId, plugin );

                _luceneFormSearchFactory.swapIndex();
            }
            catch (Exception e)
            {
                AppLogService.error(e.getMessage(), e);
                Thread.currentThread().interrupt();
            }
            finally
            {
                closeIndexing();
                _lockManager.releaseLock( lockResult );
            }

        }
        catch (LockException e) {
            log.append("Indexing already in progress, Full indexing Aborted");
        }

        return log.toString();
    }

    /**
     * {@inheritDoc }
     */
    public String incrementalIndexing() {

        StringBuilder log = new StringBuilder();

        try {
            LockResult lockResult = _lockManager.acquireLock(LOCKNAME, MS_TIMEOUT_LOCK);

            log.append("Incremental indexing launch");
            try {
                initIndexing( false );
                processIndexing();
            } catch (Exception e) {
                AppLogService.error(e.getMessage(), e);
                log.append("Incremental indexing with error");
            } finally {
                closeIndexing();
                _lockManager.releaseLock(lockResult);
            }

        } catch (LockException e) {
            log.append("Indexing already in progress, Incremental indexing Aborted");
        }

        return log.toString();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void indexDocument( int nIdFormResponse, int nIdTask, Plugin plugin )
    {
        addIndexerAction( nIdFormResponse, nIdTask, plugin );
    }

    /**
     * Index all the idFormResponses
     *
     * @param listFormResponsesId list id of form responses
     * @param plugin The plugin
     */
    private void indexFormResponseList( List<Integer> listFormResponsesId, Plugin plugin )
    {
        List<FormResponse> listFormResponses = new ArrayList<>(TAILLE_LOT);
        for (Integer nIdFormResponse : listFormResponsesId) {
            FormResponse response = FormResponseHome.findByPrimaryKeyForIndex(nIdFormResponse);
            if (response != null)
            {
                listFormResponses.add(response);
            }
            if (listFormResponses.size() == TAILLE_LOT)
            {
                indexFormResponseList(listFormResponses, null, false, plugin);
                listFormResponses.clear();
            }
        }
        indexFormResponseList(listFormResponses, null, false, plugin);
    }

    /**
     * Remove IndexerAction that have no use
     *
     * @param listActionAdd list of inderAction Add
     * @param listActionUpdate list of inderAction Update
     * @param listActionDelete list of inderAction Delete
     * @param plugin the plugin
     */
    private void removeConcurrentAction( List<IndexerAction> listActionAdd, List<IndexerAction> listActionUpdate, List<IndexerAction> listActionDelete, Plugin plugin)
    {
        List<IndexerAction> listActionToRemove = new ArrayList<>();

        Set<Integer> listIdsToAdd = listActionAdd.stream().map( IndexerAction::getIdFormResponse ).collect( Collectors.toSet() );
        Set<Integer> listIdsToDelete = listActionDelete.stream().map( IndexerAction::getIdFormResponse ).collect( Collectors.toSet() );


        for ( IndexerAction action : listActionDelete )
        {
            // No need to remove document
            if ( listIdsToAdd.contains( action.getIdFormResponse() ) )
            {
                listActionToRemove.add( action );
            }
        }

        for ( IndexerAction action : listActionUpdate )
        {
            // No need to update indexe, The document going to be created with last version
            if ( listIdsToAdd.contains( action.getIdFormResponse() ) )
            {
                listActionToRemove.add( action );
            }
        }

        for ( IndexerAction action : listActionAdd )
        {
            // No need to insert indexe
            if ( listIdsToDelete.contains( action.getIdFormResponse() ) )
            {
                listActionToRemove.add( action );
            }
        }

        listActionAdd.removeAll( listActionToRemove.stream().filter( a -> a.getIdTask() == IndexerAction.TASK_CREATE ).collect( Collectors.toSet() ) );
        listActionDelete.removeAll( listActionToRemove.stream().filter( a -> a.getIdTask() == IndexerAction.TASK_DELETE ).collect( Collectors.toSet() ) );
        listActionUpdate.removeAll( listActionToRemove.stream().filter( a -> a.getIdTask() == IndexerAction.TASK_MODIFY ).collect( Collectors.toSet() ) );

        removeListIndexerAction( listActionToRemove, plugin );
    }

    /**
     * process the incremental indexing
     */
    public synchronized void processIndexing( )
    {

        Plugin plugin = PluginService.getPlugin( FormsPlugin.PLUGIN_NAME );

        List<IndexerAction> listActionAdd = new ArrayList<>();
        List<IndexerAction> listActionUpdate = new ArrayList<>();
        List<IndexerAction> listActionDelete = new ArrayList<>();

        for ( IndexerAction action : getAllIndexerAction( plugin ) )
        {
            switch ( action.getIdTask() )
            {
                case IndexerAction.TASK_DELETE :
                    listActionDelete.add( action );
                    break;

                case IndexerAction.TASK_CREATE :
                    listActionAdd.add( action );
                    break;

                case IndexerAction.TASK_MODIFY :
                    listActionUpdate.add( action );
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + action.getIdTask());
            }
        }

        removeConcurrentAction(listActionAdd, listActionUpdate, listActionDelete, plugin);

        // Document to remove
        deleteDocument( listActionDelete, plugin);

        List<IndexerAction> listComputingAction = new ArrayList<>();

        //Create document
        List<FormResponse> listFormResponses = new ArrayList<>( TAILLE_LOT );
        for ( IndexerAction actionAdd : listActionAdd )
        {
            FormResponse response = FormResponseHome.findByPrimaryKeyForIndex( actionAdd.getIdFormResponse() );
            listComputingAction.add( actionAdd );
            if ( response != null )
            {
                listFormResponses.add( response );
            }
            if ( listFormResponses.size( ) == TAILLE_LOT )
            {
                indexFormResponseList( listFormResponses, listComputingAction, false, plugin );
                listFormResponses.clear( );
                listComputingAction.clear();
            }
        }
        indexFormResponseList( listFormResponses, listComputingAction, false, plugin );
        listComputingAction.clear();

        //Update document
        listFormResponses.clear();
        for ( IndexerAction actionUpdate : listActionUpdate )
        {
            FormResponse response = FormResponseHome.findByPrimaryKeyForIndex( actionUpdate.getIdFormResponse() );
            listComputingAction.add( actionUpdate );
            if ( response != null )
            {
                listFormResponses.add( response );
            }
            if ( listFormResponses.size( ) == TAILLE_LOT )
            {
                indexFormResponseList( listFormResponses, listComputingAction, true, plugin );
                listFormResponses.clear( );
                listComputingAction.clear();
            }
        }
        indexFormResponseList( listFormResponses, listComputingAction, true, plugin );
        listComputingAction.clear();

    }

    /**
     * {@inheritDoc}
     */
    public boolean isIndexerInitialized( )
    {
        try
        {
            return DirectoryReader.indexExists( _luceneFormSearchFactory.getDirectory( ) );
        }
        catch( Exception e )
        {
            AppLogService.error( "Indexing error ", e );
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    private void indexFormResponseList( List<FormResponse> listFormResponse, List<IndexerAction> listComputingAction, boolean update, Plugin plugin )
    {

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
                AppLogService.error(e.getMessage(), e);
            }

            if ( doc != null )
            {
                documentList.add( doc );
            }
        }
        if ( !documentList.isEmpty( ) )
        {
            if( update )
            {
                updateDocuments( documentList, listComputingAction, plugin );
            }
            else
            {
                addDocuments( documentList, listComputingAction, plugin );
            }

        }
    }

    /**
     * update the list of document in the writer and remove the list of action in the database
     *
     * @param documentList the documentList
     * @param listComputingAction the list of action
     * @param plugin the plugin
     */
    private void updateDocuments( List<Document> documentList, List<IndexerAction> listComputingAction, Plugin plugin)
    {
        provideExternalFields( documentList );
        try
        {

            List<Query> luceneQueryList = new ArrayList<>( );
            for (IndexerAction action : listComputingAction)
            {
                luceneQueryList.add(IntPoint.newExactQuery( FormResponseSearchItem.FIELD_ID_FORM_RESPONSE, action.getIdFormResponse() ));
            }

            _indexWriter.deleteDocuments( luceneQueryList.toArray(new Query[luceneQueryList.size()]) );
            _indexWriter.addDocuments( documentList);
            _indexWriter.commit();
            removeListIndexerAction( listComputingAction , plugin );
        }
        catch( IOException e )
        {
            AppLogService.error( "Unable to index form response", e );
        }
        documentList.clear( );
    }

    /**
     * add the list of document in the writer, and remove the list of action
     * @param documentList the documentList
     * @param listComputingAction the listComputingAction
     * @param plugin the plugin
     */
    private void addDocuments( List<Document> documentList, List<IndexerAction> listComputingAction, Plugin plugin )
    {
        provideExternalFields( documentList );
        try
        {
            _indexWriter.addDocuments( documentList );
            _indexWriter.commit();
            if( listComputingAction != null)
            {
                removeListIndexerAction( listComputingAction , plugin );
            }
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
     * @param fullIndexing
     *              The boolean which tell if the indexing are for full or incremental
     */
    private void initIndexing( boolean fullIndexing )
    {
        if (fullIndexing)
        {
            _indexWriter = _luceneFormSearchFactory.getIndexWriter( true, false );
        }
        else
        {
            _indexWriter = _luceneFormSearchFactory.getIndexWriter( false, true );
        }
    }

    /**
     * close the indexing action
     */
    private void closeIndexing( )
    {
        if ( _indexWriter != null && _indexWriter.isOpen() && CLOSE_WRITER ) {
            try {
                _indexWriter.close();
            } catch (IOException e) {
                AppLogService.error("Unable to close index writer ", e);
            }
        }

    }

    /**
     * {@inheritDoc }
     */
    private void deleteIndex( )
    {
        try
        {
            _indexWriter.deleteAll( );
        }
        catch( IOException e )
        {
            AppLogService.error( "Unable to delete all docs in index ", e );
        }
    }

    /**
     * cut listActionDelete in TAILLE_LOT, and call deleteDocumentBlock
     * @param listActionDelete
     * @param plugin
     */
    private void deleteDocument( List<IndexerAction> listActionDelete, Plugin plugin )
    {

        List<IndexerAction> listActionToCompute;

        while ( !listActionDelete.isEmpty() ) {
            if (listActionDelete.size() >= TAILLE_LOT) {
                listActionToCompute = listActionDelete.subList(0, TAILLE_LOT);
            } else {
                listActionToCompute = listActionDelete.subList(0, listActionDelete.size() );
            }

            deleteDocumentBlock( listActionToCompute , plugin );

            listActionToCompute.clear();
        }
    }

    /**
     * remove document in indexe, and remove the action after
     * @param listActionDelete the listActionDelete
     * @param plugin the plugin
     */
    private void deleteDocumentBlock( List<IndexerAction> listActionDelete, Plugin plugin )
    {
        try
        {
            Query[] queryList = new Query [listActionDelete.size( )];
            for ( int i = 0; i < listActionDelete.size( ) ; i++)
            {
                queryList[i] = IntPoint.newExactQuery( FormResponseSearchItem.FIELD_ID_FORM_RESPONSE, listActionDelete.get(i).getIdFormResponse() );
            }

            _indexWriter.deleteDocuments( queryList );
            _indexWriter.commit();
            removeListIndexerAction( listActionDelete , plugin );
        }
        catch( IOException e )
        {
            AppLogService.error( "Unable to delete document ", e );
        }
    }

    /**
     * Remove a list of indexer Action
     *
     * @param listAction
     *            the list of the action to remove
     * @param plugin
     *            the plugin
     */
    private void removeListIndexerAction( List<IndexerAction> listAction, Plugin plugin )
    {
        IndexerActionHome.remove( listAction.stream( ).map( IndexerAction::getIdAction ).distinct( ).collect( Collectors.toList( ) ), plugin );
    }

    /**
     * return a list of IndexerAction
     *
     * @param plugin
     *            the plugin
     * @return a list of IndexerAction
     */
    private List<IndexerAction> getAllIndexerAction( Plugin plugin )
    {
        return IndexerActionHome.getList( plugin );
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
                                    if ( entryTypeService instanceof EntryTypeSelect || entryTypeService instanceof EntryTypeRadioButton || entryTypeService instanceof EntryTypeCheckBox )
                                    {
                                        doc.add( new StringField( fieldNameBuilder.toString( ) + FormResponseSearchItem.FIELD_SELECT_SUFFIX, response.getResponseValue( ), Field.Store.YES ) );
                                        doc.add( new SortedDocValuesField( fieldNameBuilder.toString( ) + FormResponseSearchItem.FIELD_SELECT_SUFFIX, new BytesRef( response.getResponseValue( ) ) ) );
                                        if ( responseField != null && StringUtils.isNotEmpty( responseField.getTitle( ) ) )
                                        {
                                            doc.add( new StringField( fieldNameBuilder.toString( ) + FormResponseSearchItem.FIELD_SELECT_TITLE , responseField.getTitle( ), Field.Store.YES ) );
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
     * @param formResponse the formResponse object
     *
     * @return the index
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
