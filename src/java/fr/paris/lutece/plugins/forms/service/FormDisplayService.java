/*
 * Copyright (c) 2002-2025, City of Paris
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
package fr.paris.lutece.plugins.forms.service;

import java.util.List;

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlGroupHome;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.ControlType;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.GroupHome;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

/**
 * Service dedicated to management of formDisplay
 */
public class FormDisplayService extends AbstractFormDisplayService
{

    public static final String BEAN_NAME = "forms.formDisplayService";

    /**
     * Remove the formDisplay whose identifier is specified in parameter. The responses, group/question associated to this display will be deleted. All the
     * descendants of the display will also be removed
     * 
     * @param nIdDIsplay
     *            The formDisplay Id
     */
    public void deleteDisplayAndDescendants( int nIdDIsplay )
    {
        FormDisplay formDisplayToDelete = FormDisplayHome.findByPrimaryKey( nIdDIsplay );
        if ( formDisplayToDelete != null )
        {
            int formDisplayCompositeId = formDisplayToDelete.getCompositeId( );

            List<FormDisplay> listChildrenDisplay = FormDisplayHome.getFormDisplayListByParent( formDisplayToDelete.getStepId( ),
                    formDisplayToDelete.getId( ) );

            if ( CompositeDisplayType.QUESTION.getLabel( ).equalsIgnoreCase( formDisplayToDelete.getCompositeType( ) ) )
            {
                // Delete all QuestionResponses associated to the Question, including the responses of different parent iterations
                FormQuestionResponseHome.removeByQuestion( formDisplayCompositeId );

                List<Control> listControl = ControlHome.getControlByQuestionAndType( formDisplayCompositeId, ControlType.VALIDATION.getLabel( ) );

                for ( Control control : listControl )
                {
                    ControlHome.remove( control.getId( ) );
                }

                listControl = ControlHome.getControlByControlTargetAndType( formDisplayCompositeId, ControlType.CONDITIONAL );
                int nIdControlGroup = 0;
                for ( Control control : listControl )
                {
                	if (nIdControlGroup == 0) {
            			nIdControlGroup = control.getIdControlGroup();
            		}
                    ControlHome.remove( control.getId( ) );
                }
                // Delete control group
                ControlGroupHome.remove(nIdControlGroup);
                
                ControlHome.removeByControlTarget( formDisplayToDelete.getId( ), ControlType.CONDITIONAL );

                // Delete the Question and its Entry
                QuestionHome.remove( formDisplayCompositeId );
            }

            if ( CompositeDisplayType.GROUP.getLabel( ).equalsIgnoreCase( formDisplayToDelete.getCompositeType( ) ) )
            {
                GroupHome.remove( formDisplayCompositeId );
            }

            FormDisplayHome.remove( nIdDIsplay );

            for ( FormDisplay childDisplay : listChildrenDisplay )
            {
                deleteDisplayAndDescendants( childDisplay.getId( ) );
            }
        }

    }

    @Override
    protected List<FormDisplay> getFormDisplayListByParent( int nIdStep, int nIdParent )
    {
        return FormDisplayHome.getFormDisplayListByParent( nIdStep, nIdParent );
    }

    @Override
    protected ReferenceList getGroupDisplayReferenceListByStep( int nIdStep )
    {
        return FormDisplayHome.getGroupDisplayReferenceListByStep( nIdStep );
    }

    @Override
    protected IFormDatabaseService initFormDatabaseService( )
    {
        return SpringContextService.getBean( FormDatabaseService.BEAN_NAME );
    }
}
