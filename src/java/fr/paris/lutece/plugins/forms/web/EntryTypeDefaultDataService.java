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

package fr.paris.lutece.plugins.forms.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;

/**
 * 
 * Default entry data service
 *
 */
public class EntryTypeDefaultDataService implements IEntryDataService
{
    private String _strEntryServiceName = StringUtils.EMPTY;

    /**
     * Constructor
     * 
     * @param strEntryServiceName
     *            the service name
     */
    public EntryTypeDefaultDataService( String strEntryServiceName )
    {
        _strEntryServiceName = strEntryServiceName;
    }

    @Override
    public String getDataServiceName( )
    {
        return _strEntryServiceName;
    }

    @Override
    public void saveFormQuestionResponse( FormQuestionResponse questionResponse )
    {
        FormQuestionResponseHome.create( questionResponse );
    }

    @Override
    public boolean getResponseFromRequest( Question question, HttpServletRequest request, FormQuestionResponse responseInstance )
    {

    	boolean bHasError = false;
    	
    	List<Response> listResponses = new ArrayList<Response>( );
        GenericAttributeError error = EntryTypeServiceManager.getEntryTypeService( question.getEntry( ) ).getResponseData( question.getEntry( ), request,
                listResponses, request.getLocale( ) );
        
        if( error != null )
        {
        	bHasError = true;
        	if( listResponses.size( ) > 0 && listResponses.get( 0 ).getEntry() != null )
        	{
        		listResponses.get( 0 ).getEntry().setError( error );
        	}
        }

    	responseInstance.setEntryResponse( listResponses );
        responseInstance.setIdQuestion( question.getId( ) );

        return bHasError;
    }

}
