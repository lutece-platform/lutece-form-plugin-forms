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
package fr.paris.lutece.plugins.forms.service;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.Group;
import fr.paris.lutece.plugins.forms.business.GroupHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;

/**
 * Service dedicated to management of formDisplay
 */
public final class FormDisplayService
{
    /**
     * Constructor
     */
    private FormDisplayService( )
    {
    }

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
        List<FormDisplay> listChildrenDisplay = new ArrayList<FormDisplay>( );

        if ( formDisplayToDelete != null )
        {
            int formDisplayCompositeId = formDisplayToDelete.getCompositeId( );

            listChildrenDisplay = FormDisplayHome.getFormDisplayListByParent( formDisplayToDelete.getStepId( ), formDisplayToDelete.getId( ) );

            if ( CompositeDisplayType.QUESTION.getLabel( ).equalsIgnoreCase( formDisplayToDelete.getCompositeType( ) ) )
            {
                // Delete all QuestionResponses associated to the Question, including the responses of different parent iterations
                FormQuestionResponseHome.removeByQuestion( formDisplayCompositeId );
                // Delete the Question and its Entry
                QuestionHome.remove( formDisplayCompositeId );
                // TO DO: delete all Controls associated to the composite
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

    /**
     * Rebuild the position sequence of a given FormDisplay list and update the objects in database. The list indexes will be used to set the displayOrder
     * values.
     * 
     * @param listDisplay
     *            The List of FormDisplay to update
     */
    public void rebuildDisplayPositionSequence( List<FormDisplay> listDisplay )
    {
        int nUpdatedPosition = 0;
        for ( FormDisplay displayToUpdate : listDisplay )
        {
            nUpdatedPosition++;
            displayToUpdate.setDisplayOrder( nUpdatedPosition );
            FormDisplayHome.update( displayToUpdate );
        }
    }

    /**
     * Update a given FormDisplay, and all its descendants by setting the provided depth and idStep values. Value of a children Depth is parent depth plus one.
     * Value of a children idStep is same as parent idStep.
     * 
     * @param formDisplayParent
     *            the parent Display
     * 
     * @param nDepth
     *            the parent depth value to set
     * 
     * @param nIdStep
     *            the parent idStep value to set
     * 
     */
    public void setChildrenDisplayDepthAndStep( FormDisplay formDisplayParent, int nDepth, int nIdStep )
    {
        int nOldIdStep = formDisplayParent.getStepId( );
        String strCompositeType = formDisplayParent.getCompositeType( );

        formDisplayParent.setStepId( nIdStep );
        formDisplayParent.setDepth( nDepth );
        FormDisplayHome.update( formDisplayParent );

        if ( CompositeDisplayType.QUESTION.getLabel( ).equalsIgnoreCase( strCompositeType ) )
        {
            Question question = QuestionHome.findByPrimaryKey( formDisplayParent.getCompositeId( ) );
            question.setIdStep( nIdStep );
            QuestionHome.update( question );
        }

        if ( CompositeDisplayType.GROUP.getLabel( ).equalsIgnoreCase( strCompositeType ) )
        {
            Group group = GroupHome.findByPrimaryKey( formDisplayParent.getCompositeId( ) );
            group.setIdStep( nIdStep );
            GroupHome.update( group );
        }

        List<FormDisplay> listChildDisplay = FormDisplayHome.getFormDisplayListByParent( nOldIdStep, formDisplayParent.getId( ) );

        for ( FormDisplay childDisplay : listChildDisplay )
        {
            setChildrenDisplayDepthAndStep( childDisplay, formDisplayParent.getDepth( ) + 1, nIdStep );
        }
    }

}
