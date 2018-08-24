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

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.ControlType;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.service.EntryServiceManager;
import fr.paris.lutece.plugins.forms.validation.IValidator;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDataServiceName( )
    {
        return _strEntryServiceName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save( FormQuestionResponse questionResponse )
    {
        FormQuestionResponse responseSaved = FormQuestionResponseHome.findByPrimaryKey( questionResponse.getId( ) );

        if ( responseSaved == null )
        {
            FormQuestionResponseHome.create( questionResponse );
        }
        else
        {
            FormQuestionResponseHome.update( questionResponse );
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FormQuestionResponse createResponseFromRequest( Question question, HttpServletRequest request )
    {
        FormQuestionResponse formQuestionResponse = createResponseFor( question );

        GenericAttributeError error = EntryTypeServiceManager.getEntryTypeService( question.getEntry( ) ).getResponseData( question.getEntry( ), request,
                formQuestionResponse.getEntryResponse( ), request.getLocale( ) );

        if ( error != null )
        {
            formQuestionResponse.setError( error );
        }
        else
        {
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
        }

        return formQuestionResponse;
    }

    /**
     * Creates a form question response for the specified question
     * 
     * @param question
     *            the question
     * @return the created form question response
     */
    private FormQuestionResponse createResponseFor( Question question )
    {
        FormQuestionResponse formQuestionResponse = new FormQuestionResponse( );
        formQuestionResponse.setEntryResponse( new ArrayList<Response>( ) );
        formQuestionResponse.setQuestion( question );
        formQuestionResponse.setIdStep( question.getIdStep( ) );

        return formQuestionResponse;
    }

}
