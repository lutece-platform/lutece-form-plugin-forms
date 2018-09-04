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

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.ControlType;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.service.EntryServiceManager;
import fr.paris.lutece.plugins.forms.validation.IValidator;
import fr.paris.lutece.plugins.forms.web.http.IterationMultipartHttpServletRequestWrapper;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;

/**
 * Data service for files
 *
 */
public class EntryTypeFileDataService extends EntryTypeDefaultDataService
{

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
    public FormQuestionResponse createResponseFromRequest( Question question, HttpServletRequest request )
    {
        FormQuestionResponse formQuestionResponse = createResponseFor( question );
        request = convertToIterationRequest( question, request );

        GenericAttributeError error = EntryTypeServiceManager.getEntryTypeService( question.getEntry( ) ).getResponseData( question.getEntry( ), request,
                formQuestionResponse.getEntryResponse( ), request.getLocale( ) );
        formQuestionResponse.setError( error );

        Control control = ControlHome.getControlByQuestionAndType( question.getId( ), ControlType.VALIDATION.getLabel( ) );

        if ( control != null )
        {
            IValidator validator = EntryServiceManager.getInstance( ).getValidator( control.getValidatorName( ) );
            if ( !validator.validate( formQuestionResponse, control ) )
            {
                error = new GenericAttributeError( );

                error.setIsDisplayableError( true );
                error.setErrorMessage( control.getErrorMessage( ) );

                formQuestionResponse.setError( error );
            }
        }

        return formQuestionResponse;
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

}
