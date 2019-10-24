/*
 * Copyright (c) 2002-2019, Mairie de Paris
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

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.portal.service.spring.SpringContextService;

public class ListQuestionListener implements IControlListener
{

    @Override
    public void notifyControlRemoval( Control control, HttpServletRequest request )
    {

        String paramValidator = request.getParameter( FormsConstants.PARAMETRE_VALIDATOR_LISTQUESTION_NAME );

        if ( paramValidator != null && paramValidator.equals( FormsConstants.VALUE_VALIDATOR_LISTEQUESTION_NAME ) )
        {
            ControlHome.removeMappingControl( control.getId( ) );
        }

    }

    @Override
    public void notifyControlCreated( Control control, HttpServletRequest request )
    {
        String paramValidator = request.getParameter( FormsConstants.PARAMETRE_VALIDATOR_LISTQUESTION_NAME );
        if ( paramValidator != null && paramValidator.equals( FormsConstants.VALUE_VALIDATOR_LISTEQUESTION_NAME ) )
        {
            AbstractListQuestionValidator listQuestValidator = SpringContextService.getBean( control.getValidatorName( ) );
            if ( listQuestValidator.getListAvailableFieldControl( ) != null )
            {
                for ( String param : listQuestValidator.getListAvailableFieldControl( ) )
                {
                    String idQuestion = request.getParameter( param );
                    if ( idQuestion != null && !idQuestion.isEmpty( ) && !idQuestion.equals( FormsConstants.REFERENCE_ITEM_DEFAULT_CODE ) )
                    {
                        ControlHome.createMappingControl( control.getId( ), Integer.parseInt( idQuestion ), param );
                    }

                }
            }

        }
    }

    @Override
    public void notifyControlUpdated( Control control, HttpServletRequest request )
    {
        String paramValidator = request.getParameter( FormsConstants.PARAMETRE_VALIDATOR_LISTQUESTION_NAME );
        if ( paramValidator != null && paramValidator.equals( FormsConstants.VALUE_VALIDATOR_LISTEQUESTION_NAME ) )
        {
            notifyControlRemoval( control, request );
            notifyControlCreated( control, request );
        }

    }

}
