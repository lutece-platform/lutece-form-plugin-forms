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

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.Question;

/**
 * Interface for the entry data services
 */
public interface IEntryDataService
{
    /**
     * @return the data service name
     */
    String getDataServiceName( );

    /**
     * 
     * @param questionResponse
     *            the questionResponse to save
     * @return
     */
    void save( FormQuestionResponse questionResponse );

    /**
     * Creates the response values from request for the given question
     * 
     * @param question
     *            the question to
     * @param request
     *            The Http request
     * @param bValidateQuestion
     *            the boolean to validate question
     * @return The response to the question
     */
    FormQuestionResponse createResponseFromRequest( Question question, HttpServletRequest request, boolean bValidateQuestion );

    /**
     * Tests if a response has changed
     * 
     * @param responseReference
     *            the reference response
     * @param responseNew
     *            the new response
     * @return {@code true} if the response has changed, {@code false} otherwise
     */
    boolean isResponseChanged( FormQuestionResponse responseReference, FormQuestionResponse responseNew );

    /**
     * Extracts the responses from the specified form question response as a list of {@code String} objects
     * 
     * @param formQuestionResponse
     *            the response to convert
     * @return the list of {@code String} objects
     */
    List<String> responseToStrings( FormQuestionResponse formQuestionResponse );

    /**
     * Performs a treatment when the question is removed in front-office
     * 
     * @param request
     *            the request
     * @param question
     *            the removed question
     */
    void questionRemoved( HttpServletRequest request, Question question );

    /**
     * Performs a treatment when the question is moved in front-office
     * 
     * @param request
     *            the request
     * @param question
     *            the moved question
     * @param nNewIterationNumber
     *            the new iteration number
     */
    void questionMoved( HttpServletRequest request, Question question, int nNewIterationNumber );
}
