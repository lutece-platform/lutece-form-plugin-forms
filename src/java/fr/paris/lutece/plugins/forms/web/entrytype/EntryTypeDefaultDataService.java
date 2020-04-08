/*
 * Copyright (c) 2002-2020, City of Paris
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
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.ControlType;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.service.EntryServiceManager;
import fr.paris.lutece.plugins.forms.service.entrytype.IResponseComparator;
import fr.paris.lutece.plugins.forms.validation.IValidator;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.service.i18n.I18nService;

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
    public FormQuestionResponse createResponseFromRequest( Question question, HttpServletRequest request, boolean bValidateQuestion )
    {
        FormQuestionResponse formQuestionResponse = createResponseFor( question );

        GenericAttributeError error = EntryTypeServiceManager.getEntryTypeService( question.getEntry( ) ).getResponseData( question.getEntry( ), request,
                formQuestionResponse.getEntryResponse( ), request.getLocale( ) );

        if ( bValidateQuestion )
        {
            formQuestionResponse.setError( error );

            List<Control> listControl = ControlHome.getControlByQuestionAndType( question.getId( ), ControlType.VALIDATION.getLabel( ) );

            for ( Control control : listControl )
            {
                IValidator validator = EntryServiceManager.getInstance( ).getValidator( control.getValidatorName( ) );
                if ( !validator.validate( formQuestionResponse, control ) )
                {
                    error = new GenericAttributeError( );

                    error.setIsDisplayableError( true );
                    error.setErrorMessage( control.getErrorMessage( ) );

                    formQuestionResponse.setError( error );

                    break;
                }
            }
        }
        else
        {
            formQuestionResponse.setError( null );
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isResponseChanged( FormQuestionResponse responseReference, FormQuestionResponse responseNew )
    {
        if ( responseReference == null )
        {
            return responseNew != null;
        }

        if ( responseNew == null )
        {
            return true;
        }

        boolean bIsChanged = false;

        IEntryTypeService service = EntryTypeServiceManager.getEntryTypeService( responseReference.getQuestion( ).getEntry( ) );

        if ( service instanceof IResponseComparator )
        {
            bIsChanged = ( (IResponseComparator) service ).isResponseChanged( responseReference.getEntryResponse( ), responseNew.getEntryResponse( ) );
        }

        return bIsChanged;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> responseToStrings( FormQuestionResponse formQuestionResponse )
    {
        List<String> listResponseValue = new ArrayList<>( );
        Entry entry = formQuestionResponse.getQuestion( ).getEntry( );

        for ( Response response : formQuestionResponse.getEntryResponse( ) )
        {
            String strResponseValue = EntryTypeServiceManager.getEntryTypeService( entry ).getResponseValueForExport( entry, null, response,
                    I18nService.getDefaultLocale( ) );

            if ( strResponseValue != null )
            {
                listResponseValue.add( strResponseValue );
            }
        }

        return listResponseValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void questionRemoved( HttpServletRequest request, Question question )
    {
        // Nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void questionMoved( HttpServletRequest request, Question question, int nNewIterationNumber )
    {
        // Nothing to do
    }

}
