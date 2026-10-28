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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.ControlType;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.GroupHome;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.service.cache.FormsCacheService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.TransactionManager;

/**
 * Service dedicated to management of formDisplay
 */
public class FormDisplayService extends AbstractFormDisplayService
{

    public static final String BEAN_NAME = "forms.formDisplayService";

    /** The control types a question drives : what they target is another element, which the question is only a condition of */
    private static final ControlType [ ] CONTROL_TYPES_DRIVEN_BY_A_QUESTION = {
            ControlType.CONDITIONAL, ControlType.TRANSITION
    };

    /**
     * Remove the formDisplay whose identifier is specified in parameter. The responses, group/question associated to this display will be deleted. All the
     * descendants of the display will also be removed
     * 
     * @param nIdDIsplay
     *            The formDisplay Id
     */
    public void deleteDisplayAndDescendants( int nIdDIsplay )
    {
        TransactionManager.beginTransaction( FormsPlugin.getPlugin( ) );

        try
        {
            Set<Integer> setIdControlGroup = new HashSet<>( );

            deleteDisplayAndDescendantsInTransaction( nIdDIsplay, setIdControlGroup );
            ControlHome.removeEmptyControlGroups( setIdControlGroup );

            TransactionManager.commitTransaction( FormsPlugin.getPlugin( ) );
        }
        catch( Exception e )
        {
            TransactionManager.rollBack( FormsPlugin.getPlugin( ) );
            // the caches were repopulated while the transaction was running : they now describe rows the rollback has undone
            SpringContextService.<FormsCacheService> getBean( FormsCacheService.BEAN_NAME ).resetCache( );
            throw new AppException( "Forms : the removal of the display " + nIdDIsplay + " failed", e );
        }
    }

    /**
     * Remove the formDisplay and its descendants, within the transaction opened by the caller.
     * 
     * Reserved to the callers of this package that already own a transaction : the public twin opens one of its own.
     * 
     * The control groups left empty are collected rather than reclaimed here, and the caller reclaims them once the whole removal is over. Reclaiming at
     * every display meant one count query per display, and it asked the question too early : a group gathering controls of two displays was still counted
     * as used at the turn of the first one.
     *
     * @param nIdDIsplay
     *            The formDisplay Id
     * @param setIdControlGroup
     *            collects the control groups the removed controls belonged to
     */
    void deleteDisplayAndDescendantsInTransaction( int nIdDIsplay, Set<Integer> setIdControlGroup )
    {
        FormDisplay formDisplayToDelete = FormDisplayHome.findByPrimaryKey( nIdDIsplay );
        if ( formDisplayToDelete != null )
        {
            int formDisplayCompositeId = formDisplayToDelete.getCompositeId( );

            List<FormDisplay> listChildrenDisplay = FormDisplayHome.getFormDisplayListByParent( formDisplayToDelete.getStepId( ),
                    formDisplayToDelete.getId( ) );

            // The conditions shown on this display disappear with it, whatever it holds
            for ( Control control : ControlHome.getControlByControlTargetAndType( formDisplayToDelete.getId( ), ControlType.CONDITIONAL ) )
            {
                setIdControlGroup.add( control.getIdControlGroup( ) );
            }
            ControlHome.removeByControlTarget( formDisplayToDelete.getId( ), ControlType.CONDITIONAL );

            if ( CompositeDisplayType.QUESTION.getLabel( ).equalsIgnoreCase( formDisplayToDelete.getCompositeType( ) ) )
            {
                // Delete all QuestionResponses associated to the Question, including the responses of different parent iterations
                FormQuestionResponseHome.removeByQuestion( formDisplayCompositeId );

                List<Control> listControl = ControlHome.getControlByQuestionAndType( formDisplayCompositeId, ControlType.VALIDATION.getLabel( ) );

                for ( Control control : listControl )
                {
                    setIdControlGroup.add( control.getIdControlGroup( ) );
                    ControlHome.remove( control.getId( ) );
                }

                // What other elements condition on this question can no longer be evaluated : the display of another composite, or a step transition
                for ( ControlType controlType : CONTROL_TYPES_DRIVEN_BY_A_QUESTION )
                {
                    for ( Control control : ControlHome.getControlByQuestionAndType( formDisplayCompositeId, controlType.getLabel( ) ) )
                    {
                        setIdControlGroup.add( control.getIdControlGroup( ) );
                        ControlHome.remove( control.getId( ) );
                    }
                }

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
                deleteDisplayAndDescendantsInTransaction( childDisplay.getId( ), setIdControlGroup );
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
