/*
 * Copyright (c) 2002-2021, City of Paris
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
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.Group;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;

public abstract class AbstractFormDisplayService implements IFormDisplayService
{
    private static final String PROPERTY_ROOT_LEVEL_POSITION_LABEL = "forms.moveComposite.rootLevel.label";
    private static final String PROPERTY_POSITION_LABEL = "forms.moveComposite.position.label";

    private IFormDatabaseService _formDatabaseService = null;

    @Override
    public String getDisplayTitle( FormDisplay formDisplayToMove )
    {
        String strDisplayTitle = StringUtils.EMPTY;

        if ( CompositeDisplayType.GROUP.getLabel( ).equalsIgnoreCase( formDisplayToMove.getCompositeType( ) ) )
        {
            Group groupToMove = getFormDatabaseService( ).findGroupByPrimaryKey( formDisplayToMove.getCompositeId( ) );
            if ( groupToMove != null )
            {
                strDisplayTitle = groupToMove.getTitle( );
            }
        }
        else
            if ( CompositeDisplayType.QUESTION.getLabel( ).equalsIgnoreCase( formDisplayToMove.getCompositeType( ) ) )
            {
                Question questionToMove = getFormDatabaseService( ).findQuestionByPrimaryKey( formDisplayToMove.getCompositeId( ) );
                if ( questionToMove != null )
                {
                    strDisplayTitle = questionToMove.getTitle( );
                }
            }
        return strDisplayTitle;
    }

    @Override
    public ReferenceList getTargetGroupDisplayListByStep( int nIdStep, FormDisplay formDisplayToMove, Locale locale )
    {
        ReferenceList listGroupDisplay = new ReferenceList( );

        boolean bIsGroup = CompositeDisplayType.GROUP.getLabel( ).equalsIgnoreCase( formDisplayToMove.getCompositeType( ) );

        listGroupDisplay.addItem( 0, I18nService.getLocalizedString( PROPERTY_ROOT_LEVEL_POSITION_LABEL, locale ) );

        if ( bIsGroup )
        {
            for ( ReferenceItem display : getGroupDisplayReferenceListByStep( nIdStep ) )
            {
                int nDisplayId = NumberUtils.toInt( display.getCode( ) );
                if ( !isDescendantDisplay( nDisplayId, formDisplayToMove ) )
                {
                    listGroupDisplay.addItem( display.getCode( ), display.getName( ) );
                }
            }
        }
        else
        {
            listGroupDisplay.addAll( getGroupDisplayReferenceListByStep( nIdStep ) );
        }

        return listGroupDisplay;
    }

    /**
     * Return true if the provided FormDisplay identifier A is a descendant (a child, grandchild, great-grandchild, etc.) of the second FormDisplay parameter
     * 
     * @param nDisplayId
     *            the FormDisplay identifier A
     * 
     * @param formDisplay
     *            the FormDisplay B
     * 
     * @return true if A is a descendant of B
     */
    private boolean isDescendantDisplay( int nDisplayId, FormDisplay formDisplay )
    {
        if ( nDisplayId == formDisplay.getId( ) )
        {
            return true;
        }

        boolean bIsChild = false;

        List<FormDisplay> listFormDisplay = getFormDisplayListByParent( formDisplay.getStepId( ), formDisplay.getId( ) );

        for ( FormDisplay displayChild : listFormDisplay )
        {
            bIsChild = isDescendantDisplay( nDisplayId, displayChild );
            if ( bIsChild )
            {
                break;
            }
        }
        return bIsChild;
    }

    @Override
    public ReferenceList getlistAvailablePositionsInGroup( int nIdStep, int nIdDisplayGroup, boolean bAddPosition, Locale locale )
    {
        ReferenceList listAvailablePositions = new ReferenceList( );
        List<FormDisplay> listDisplay = getFormDisplayListByParent( nIdStep, nIdDisplayGroup );

        for ( int i = 1; i <= listDisplay.size( ); i++ )
        {
            listAvailablePositions.addItem( i, I18nService.getLocalizedString( PROPERTY_POSITION_LABEL, locale ) + i );
        }
        if ( bAddPosition )
        {
            int nPosition = listDisplay.size( ) + 1;
            listAvailablePositions.addItem( nPosition, I18nService.getLocalizedString( PROPERTY_POSITION_LABEL, locale ) + nPosition );
        }

        return listAvailablePositions;
    }

    @Override
    public int getDisplayDepthFromParent( int parentGroup )
    {
        int nDisplayDepth = 0;
        if ( parentGroup > 0 )
        {
            FormDisplay formDisplayParent = getFormDatabaseService( ).findDisplayByPrimaryKey( parentGroup );
            if ( formDisplayParent != null )
            {
                nDisplayDepth = formDisplayParent.getDepth( ) + 1;
            }
        }
        return nDisplayDepth;
    }

    @Override
    public void setChildrenDisplayDepthAndStep( FormDisplay formDisplayParent, int nDepth, int nIdStep )
    {
        int nOldIdStep = formDisplayParent.getStepId( );
        String strCompositeType = formDisplayParent.getCompositeType( );

        formDisplayParent.setStepId( nIdStep );
        formDisplayParent.setDepth( nDepth );
        getFormDatabaseService( ).updateFormDisplay( formDisplayParent );
        ;

        if ( CompositeDisplayType.QUESTION.getLabel( ).equalsIgnoreCase( strCompositeType ) )
        {
            Question question = getFormDatabaseService( ).findQuestionByPrimaryKey( formDisplayParent.getCompositeId( ) );
            question.setIdStep( nIdStep );
            getFormDatabaseService( ).updateQuestion( question );
        }

        if ( CompositeDisplayType.GROUP.getLabel( ).equalsIgnoreCase( strCompositeType ) )
        {
            Group group = getFormDatabaseService( ).findGroupByPrimaryKey( formDisplayParent.getCompositeId( ) );
            group.setIdStep( nIdStep );
            getFormDatabaseService( ).updateGroup( group );
        }

        List<FormDisplay> listChildDisplay = getFormDatabaseService( ).getFormDisplayListByParent( nOldIdStep, formDisplayParent.getId( ) );

        for ( FormDisplay childDisplay : listChildDisplay )
        {
            setChildrenDisplayDepthAndStep( childDisplay, formDisplayParent.getDepth( ) + 1, nIdStep );
        }
    }

    @Override
    public void rebuildDisplayPositionSequence( List<FormDisplay> listDisplay )
    {
        int nUpdatedPosition = 0;
        for ( FormDisplay displayToUpdate : listDisplay )
        {
            nUpdatedPosition++;
            displayToUpdate.setDisplayOrder( nUpdatedPosition );
            getFormDatabaseService( ).updateFormDisplay( displayToUpdate );
        }
    }

    /**
     * Load the data of all the FormDisplay objects of type "Group" linked to a given FormStep and returns them as a referenceList
     * 
     * @param nIdStep
     *            the step identifier
     * @return the referenceList which contains the data of all the Display objects of group type
     */
    protected abstract ReferenceList getGroupDisplayReferenceListByStep( int nIdStep );

    /**
     * Load the data of all the formDisplay that are direct children of a given parent and returns them as a list
     * 
     * @param nIdStep
     *            The step primary key
     * @param nIdParent
     *            The parent primary key
     * @return the list which contains the data of all the formDisplay objects by parent
     */
    protected abstract List<FormDisplay> getFormDisplayListByParent( int nIdStep, int nIdParent );

    protected IFormDatabaseService getFormDatabaseService( )
    {
        if ( _formDatabaseService == null )
        {
            _formDatabaseService = initFormDatabaseService( );
        }
        return _formDatabaseService;
    }

    protected abstract IFormDatabaseService initFormDatabaseService( );
}
