package fr.paris.lutece.plugins.forms.service.search;

import java.util.List;

import org.apache.lucene.document.Document;

public interface ILucenDocumentExternalFieldProvider {

	/**
     * Provide external fields to Document objects
     * 
     * @param documentList
     *            list of Document objects
     *
     */
    public void provideFields( List<Document> documentList );
}
