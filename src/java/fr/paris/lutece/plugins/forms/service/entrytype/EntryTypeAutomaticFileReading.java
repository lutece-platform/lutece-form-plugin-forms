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
package fr.paris.lutece.plugins.forms.service.entrytype;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.service.upload.FormsAsynchronousUploadHandler;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.IOcrProvider;
import fr.paris.lutece.plugins.genericattributes.business.OcrProviderManager;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeFile;
import fr.paris.lutece.plugins.genericattributes.service.upload.AbstractGenAttUploadHandler;
import fr.paris.lutece.plugins.genericattributes.util.GenericAttributesUtils;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class is a service for the entry type Automatic File Reading
 *
 */
public class EntryTypeAutomaticFileReading extends AbstractEntryTypeFile implements IResponseComparator
{
    private static final String JSP_DOWNLOAD_FILE = "jsp/admin/plugins/forms/DoDownloadFile.jsp";
    private static final String TEMPLATE_CREATE = "admin/plugins/forms/entries/create_entry_type_auto_file_reading.html";
    private static final String TEMPLATE_MODIFY = "admin/plugins/forms/entries/modify_entry_type_auto_file_reading.html";
    private static final String TEMPLATE_READONLY_BACKOFFICE = "admin/plugins/forms/entries/readonly_entry_type_auto_file_reading.html";
    private static final String TEMPLATE_EDITION_BACKOFFICE = "admin/plugins/forms/entries/fill_entry_type_auto_file_reading.html";
    private static final String TEMPLATE_EDITION_FRONTOFFICE = "skin/plugins/forms/entries/fill_entry_type_auto_file_reading.html";
    private static final String TEMPLATE_READONLY_FRONTOFFICE = "skin/plugins/forms/entries/readonly_entry_type_auto_file_reading.html";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateHtmlForm( Entry entry, boolean bDisplayFront )
    {
        if ( bDisplayFront )
        {
            return TEMPLATE_EDITION_FRONTOFFICE;
        }

        return TEMPLATE_EDITION_BACKOFFICE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateCreate( Entry entry, boolean bDisplayFront )
    {
        return TEMPLATE_CREATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateModify( Entry entry, boolean bDisplayFront )
    {
    	
        return TEMPLATE_MODIFY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractGenAttUploadHandler getAsynchronousUploadHandler( )
    {
        return FormsAsynchronousUploadHandler.getHandler( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUrlDownloadFile( int nResponseId, String strBaseUrl )
    {
        UrlItem url = new UrlItem( strBaseUrl + JSP_DOWNLOAD_FILE );
        url.addParameter( PARAMETER_ID_RESPONSE, nResponseId );

        return url.getUrl( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkForImages( )
    {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateEntryReadOnly( boolean bDisplayFront )
    {
        if ( bDisplayFront )
        {
            return TEMPLATE_READONLY_FRONTOFFICE;
        }

        return TEMPLATE_READONLY_BACKOFFICE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isResponseChanged( List<Response> listResponseReference, List<Response> listResponseNew )
    {
        return CollectionUtils.isNotEmpty( listResponseNew );
    }
    
    /**
     * Set the list of fields
     * 
     * @param entry
     *            The entry
     * @param request
     *            the HTTP request
     */
    @Override
    protected void setFields( Entry entry, HttpServletRequest request )
    {
    	List<Field> listFields = new ArrayList<>( );
        listFields.add( buildDefaultField( entry, request ) );
        listFields.add( buildFieldFileMaxSize( entry, request ) );
        listFields.add( buildFieldFileType( entry, request ) );
        listFields.add( buildExportBinaryField( entry, request ) );

        entry.setFields( listFields );
    }
    
    /**
     * Build the field for file max size
     * 
     * @param entry
     *            The entry
     * @param request
     *            the HTTP request
     * @return the field
     */
    private Field buildFieldFileType( Entry entry, HttpServletRequest request )
    {
        String strOcrProvider = request.getParameter( PARAMETER_FILE_TYPE );
        
        Field fieldFileType = GenericAttributesUtils.findFieldByTitleInTheList( CONSTANT_FILE_TYPE, entry.getFields( ) );

        if ( fieldFileType == null )
        {
        	fieldFileType = new Field( );
        }
        
        fieldFileType.setTitle( CONSTANT_FILE_TYPE );
        fieldFileType.setValue(strOcrProvider);

        fieldFileType.setParentEntry( entry );

        return fieldFileType;
    }

    /**
     * Builds the {@link ReferenceList} of all available files type
     * 
     * @return the {@link ReferenceList}
     */
    public ReferenceList getFileTypeRefList( )
    {
        ReferenceList refList = new ReferenceList( );

        //refList.addItem( StringUtils.EMPTY, StringUtils.EMPTY );

        for ( IOcrProvider typeDocumentProvider : OcrProviderManager.getOcrProvidersList() )
        {
            refList.add( typeDocumentProvider.toRefItem( ) );
        }

        return refList;
    }
    /**
     * Get the config html code
     * @param nIdStep the Id step
     * @param nIdQuestion the question id
     * @param nIdEntryQuestion the id entry
     * @param strOcrKey the ocr provider
     * @return the config html code
     */
    public String getOcrConfigTemplate( int nIdStep, int nIdQuestion, int nIdEntryQuestion,String strOcrKey )
    {
    	
    	IOcrProvider ocrProvider= getOcrProvider( strOcrKey );
    	
        ReferenceList refList =  getQuestionsByStep(  nIdStep,  nIdQuestion,  strOcrKey );

       
        return ocrProvider.getConfigHtmlCode(refList, nIdEntryQuestion, Form.RESOURCE_TYPE);
    }
    
    

    /**
     * Gets the ocr provider.
     *
     * @param strKey
     *            the str key
     * @return the type document provider
     */
    public IOcrProvider getOcrProvider( String strKey )
    {
        return OcrProviderManager.getOcrProvider(strKey);
    }

    /**
     * Gets the list field.
     *
     * @param strKey
     *            the str key
     * @return the list field
     */
    public ReferenceList getListField( String strKey )
    {
        ReferenceList refListField = new ReferenceList( );
        if ( OcrProviderManager.getOcrProvider( strKey ) != null )
        {
            refListField.addAll( OcrProviderManager.getOcrProvider( strKey ).getListField( ) );
        }

        return refListField;
    }

    /**
     * Gets the steps list by form.
     *
     * @param nIdForm
     *            the n id form
     * @return the steps list by form
     */
    public ReferenceList getQuestionsByStep( int nIdStep, int nIdQuestion, String strKey )
    {
        ReferenceList refList = new ReferenceList( );

       // List<Integer> listAuthorizedEntryType = OcrProviderManager.getOcrProvider( strKey ).getAuthorizedEntryType( );
        for ( Question question : QuestionHome.getQuestionsListByStep( nIdStep ) )
        {
            Entry entry = EntryHome.findByPrimaryKey( question.getIdEntry( ) );
            if ( question.getId( ) != nIdQuestion && entry != null /*&& listAuthorizedEntryType.contains( entry.getEntryType( ).getIdType( ) )*/ )
            {
                refList.addItem( question.getId( ), question.getTitle( ) );
            }
        }

        return refList;
    }
    
    
   /* public List<FormQuestionResponse> fillResponseManagerWithResponses(List<FormQuestionResponse> listFormQuestionResponse, FormQuestionResponse formQuestionResponse, HttpServletRequest request ){
    	
    	Entry entry = EntryHome.findByPrimaryKey( formQuestionResponse.getQuestion().getIdEntry() );
    	List<Field> listField= entry.getFields();
    	 IOcrProvider ocrProvider= null;
    	if(listField!= null &&listField.size() > 0){
    		
    	     List<Field> list= listField.stream().filter(p -> (p.getTitle()!= null && p.getTitle().equals(AbstractEntryTypeFile.CONSTANT_FILE_TYPE))).collect( Collectors.toList() );
    	     ocrProvider= OcrProviderManager.getOcrProvider( list.get(0).getValue() );
    	
    	}else{
    		
    		return listFormQuestionResponse;
    	}
    	// process OCR
        Map<String, String> mapOcrResult = ocrProvider.process( this.getAsynchronousUploadHandler().getListUploadedFiles("filedName", request.getSession( )).get(0) );
         if ( mapOcrResult == null )
         {
        	 return listFormQuestionResponse;
         }
    	
    	
         for ( FormQuestionResponse response : listFormQuestionResponse )
         {
        	 
        	 String strKey= String.valueOf( response.getQuestion().getId());
        	 String strValue= mapOcrResult.get(strKey);
         }	 
   /*     	 if( strValue != null ){
        		// response.getEntryResponse()entry.get
        	 }
        	 
        	 
             String strOcrResult = mapOcrResult.get( mapping.getFieldOcrTitle( ) );
             if ( strOcrResult != null )
             {
                 Question question = QuestionHome.findByPrimaryKey( mapping.getIdQuestion( ) );
                 FormQuestionResponse formQuestionResponse = new FormQuestionResponse( );
                 formQuestionResponse.setQuestion( question );

                 Response response = new Response( );
                 response.setEntry( question.getEntry( ) );
                 // set Ocr result
                 response.setToStringValueResponse( strOcrResult );
                 List<Response> listResponse = new ArrayList<>( );
                 listResponse.add( response );
                 formQuestionResponse.setEntryResponse( listResponse );

                 listFormQuestionResponse.add( formQuestionResponse );
             }
         }
         
         return listFormQuestionResponse;
    }*/

    
}
