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
package fr.paris.lutece.plugins.forms.web.entrytype;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.math.NumberUtils;

import fr.paris.lutece.plugins.asynchronousupload.service.IAsyncUploadHandler;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.service.upload.FormsAsynchronousUploadHandler;
import fr.paris.lutece.plugins.forms.web.http.IterationMultipartHttpServletRequestWrapper;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;

/**
 * Data service for files
 *
 */
public class EntryTypeFileDataService extends EntryTypeDefaultDataService
{
    private static final String SEPARATOR = "_";

    /**
     * Constructor
     * 
     * @param strEntryServiceName
     *            the service name
     */
    public EntryTypeFileDataService( String strEntryServiceName )
    {
        super( strEntryServiceName );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FormQuestionResponse createResponseFromRequest( Question question, HttpServletRequest request, boolean bValidateQuestion )
    {
        HttpServletRequest requestConverted = convertToIterationRequest( question, request );
        return super.createResponseFromRequest( question, requestConverted, bValidateQuestion );
    }

    /**
     * Converts the specified request into an iteration request
     * 
     * @param question
     *            the question containing the iteration number
     * @param request
     *            the request
     * @return the converted request
     */
    private HttpServletRequest convertToIterationRequest( Question question, HttpServletRequest request )
    {
        if ( request instanceof MultipartHttpServletRequest )
        {
            return new IterationMultipartHttpServletRequestWrapper( (MultipartHttpServletRequest) request, question.getIterationNumber( ) );
        }

        return request;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void questionRemoved( HttpServletRequest request, Question question )
    {
        IAsyncUploadHandler handler = FormsAsynchronousUploadHandler.getHandler( );
        String strAttributeName = getAttributeName( question, question.getIterationNumber( ) );
        List<FileItem> listFileItem = handler.getListUploadedFiles( strAttributeName, request.getSession( ) );

        for ( int i = 0; i < listFileItem.size( ); i++ )
        {
            handler.removeFileItem( strAttributeName, request.getSession( ), i );
        }
    }

    /**
     * Gives the attribute name of the specified question for the specified iteration number
     * 
     * @param question
     *            the question
     * @param nIterationNumber
     *            the iteration number
     * @return the attribute name
     */
    private String getAttributeName( Question question, int nIterationNumber )
    {
        Entry entry = question.getEntry( );
        StringBuilder sbAttributeName = new StringBuilder( IEntryTypeService.PREFIX_ATTRIBUTE ).append( Integer.toString( entry.getIdEntry( ) ) );

        if ( nIterationNumber != NumberUtils.INTEGER_MINUS_ONE )
        {
            sbAttributeName = new StringBuilder( IEntryTypeService.PREFIX_ITERATION_ATTRIBUTE ).append( nIterationNumber ).append( SEPARATOR )
                    .append( sbAttributeName );
        }

        return sbAttributeName.toString( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void questionMoved( HttpServletRequest request, Question question, int nNewIterationNumber )
    {
        IAsyncUploadHandler handler = FormsAsynchronousUploadHandler.getHandler( );
        String strCurrentAttributeName = getAttributeName( question, question.getIterationNumber( ) );
        String strNewAttributeName = getAttributeName( question, nNewIterationNumber );
        List<FileItem> listCurrentFileItem = handler.getListUploadedFiles( strCurrentAttributeName, request.getSession( ) );
        List<FileItem> listNewFileItem = handler.getListUploadedFiles( strNewAttributeName, request.getSession( ) );
        int nCurrentFileItemNumber = listCurrentFileItem.size( );
        int nNewFileItemNumber = listNewFileItem.size( );

        // Adds previous file items into new file item
        listNewFileItem.addAll( listCurrentFileItem );

        // Adds new file items into previous file item
        for ( int i = 0; i < nNewFileItemNumber; i++ )
        {
            listCurrentFileItem.add( listNewFileItem.get( i ) );
        }

        // Removes previous file items
        for ( int i = 0; i < nCurrentFileItemNumber; i++ )
        {
            listCurrentFileItem.remove( i );
        }

        // Removes new file items
        for ( int i = 0; i < nNewFileItemNumber; i++ )
        {
            listNewFileItem.remove( i );
        }
    }

}
