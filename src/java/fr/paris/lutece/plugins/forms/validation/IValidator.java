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
package fr.paris.lutece.plugins.forms.validation;

import java.util.List;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;

/**
 * 
 * Interface which describe the validation process
 *
 */
public interface IValidator
{
    /**
     * 
     * @return the validator bean name
     */
    String getValidatorBeanName( );

    /**
     * 
     * @return the validator display name
     */
    String getValidatorDisplayName( );

    /**
     * @param control
     *            The control to build model
     * @return the HTML to display
     */
    String getDisplayHtml( Control control );

    /**
     * 
     * @return the list of available entrytype for this validator
     */
    List<String> getListAvailableEntryType( );

    /**
     * @param questionResponse
     *            The response to control
     * @param control
     *            The control to verify
     * @return boolean that indicate the validation result
     */
    boolean validate( FormQuestionResponse questionResponse, Control control );

    /**
     * @param questionResponse
     *            The List response to control
     * @param control
     *            The control to verify
     * @return boolean that indicate the validation result
     */
    boolean validate( List<FormQuestionResponse> questionResponse, Control control );

    /**
     * Only the content of the function should be returned. The parameter names are in FormsConstants, JS_PARAMETER_INPUT_VALUE and JS_PARAMETER_CONTROL_VALUE
     * 
     * @return the javascript validation code for Conditional display control
     * 
     */
    String getJavascriptValidation( );

    /**
     * @param control
     *            The control
     * @return the control value needed for javascript
     */
    String getJavascriptControlValue( Control control );

}
