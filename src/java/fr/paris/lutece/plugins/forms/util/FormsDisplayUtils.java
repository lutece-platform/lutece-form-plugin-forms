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
package fr.paris.lutece.plugins.forms.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.Group;
import fr.paris.lutece.plugins.forms.business.GroupHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;

/**
 * Utility class for plugin Forms
 */
//TODO : Move the methods calling "Home" classes to a another class
public final class FormsDisplayUtils
{
    private static final String PROPERTY_POSITION_LABEL = "forms.moveComposite.position.label";
    private static final String PROPERTY_ROOT_LEVEL_POSITION_LABEL = "forms.moveComposite.rootLevel.label";

    /**
     * FormsEntryUtils private constructor. DO not call
     *
     */
    private FormsDisplayUtils( )
    {
        throw new AssertionError( );
    }

    /**
     * Utility Method to retrieve referenceList containing all the Displays of a given Step that could be a potential destination parent for the given
     * FormDisplay. The ReferenceList begins with a first root level item, followed by all the relevant children displays of type "Group". If the Display to be
     * moved is a group, this Display and its child groups will be excluded from the list
     * 
     * @param nIdStep
     *            The Step identifier
     * 
     * @param formDisplayToMove
     *            The Display to move.
     * 
     * @param locale
     *            The Locale
     * 
     * @return a ReferenceList containing the Groups of the step
     */
    public static ReferenceList getTargetGroupDisplayListByStep( int nIdStep, FormDisplay formDisplayToMove, Locale locale )
    {
        ReferenceList listGroupDisplay = new ReferenceList( );

        boolean bIsGroup = CompositeDisplayType.GROUP.getLabel( ).equalsIgnoreCase( formDisplayToMove.getCompositeType( ) );

        listGroupDisplay.addItem( 0, I18nService.getLocalizedString( PROPERTY_ROOT_LEVEL_POSITION_LABEL, locale ) );

        if ( bIsGroup )
        {
            for ( ReferenceItem display : FormDisplayHome.getGroupDisplayReferenceListByStep( nIdStep ) )
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
            listGroupDisplay.addAll( FormDisplayHome.getGroupDisplayReferenceListByStep( nIdStep ) );
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
    public static boolean isDescendantDisplay( int nDisplayId, FormDisplay formDisplay )
    {
        boolean bIsChild = false;

        if ( nDisplayId == formDisplay.getId( ) )
        {
            return true;
        }

        List<FormDisplay> listFormDisplay = FormDisplayHome.getFormDisplayListByParent( formDisplay.getStepId( ), formDisplay.getId( ) );

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

    /**
     * Utility Method to retrieve the Move composite page title from a given FormDisplay
     * 
     * @param formDisplayToMove
     *            the FormDisplay
     * 
     * @return the admin page title
     */
    public static String getDisplayTitle( FormDisplay formDisplayToMove )
    {
        String strDisplayTitle = StringUtils.EMPTY;

        if ( CompositeDisplayType.GROUP.getLabel( ).equalsIgnoreCase( formDisplayToMove.getCompositeType( ) ) )
        {
            Group groupToMove = GroupHome.findByPrimaryKey( formDisplayToMove.getCompositeId( ) );
            if ( groupToMove != null )
            {
                strDisplayTitle = groupToMove.getTitle( );
            }
        }
        else
            if ( CompositeDisplayType.QUESTION.getLabel( ).equalsIgnoreCase( formDisplayToMove.getCompositeType( ) ) )
            {
                Question questionToMove = QuestionHome.findByPrimaryKey( formDisplayToMove.getCompositeId( ) );
                if ( questionToMove != null )
                {
                    strDisplayTitle = questionToMove.getTitle( );
                }
            }
        return strDisplayTitle;
    }

    /**
     * Return a ReferenceList containing all the available positions within a given parent FormDisplay, in a given Step. Includes the position after the last
     * element of the group.
     * 
     * @param nIdStep
     *            the identifier of the Step
     * 
     * @param nIdDisplayGroup
     *            the identifier of the group DIsplay parent. Zero for the step root level
     *
     * @param bAddPosition
     *            if true a position is added at the end of the list
     * 
     * @param locale
     *            The locale
     * 
     * @return the available positions sorted in a ReferenceList
     */
    public static ReferenceList getlistAvailablePositionsInGroup( int nIdStep, int nIdDisplayGroup, boolean bAddPosition, Locale locale )
    {
        ReferenceList listAvailablePositions = new ReferenceList( );
        List<FormDisplay> listDisplay = new ArrayList<FormDisplay>( );

        listDisplay = FormDisplayHome.getFormDisplayListByParent( nIdStep, nIdDisplayGroup );

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

    /**
     * Returns the display depth of a child display element
     * 
     * @param nParentDisplay
     *            the Identifier of the parent display element (zero if we are at the step root)
     * 
     * @return the display depth
     */
    public static int getDisplayDepthFromParent( int nParentDisplay )
    {
        int nDisplayDepth = 0;
        if ( nParentDisplay > 0 )
        {
            FormDisplay formDisplayParent = FormDisplayHome.findByPrimaryKey( nParentDisplay );
            if ( formDisplayParent != null )
            {
                nDisplayDepth = formDisplayParent.getDepth( ) + 1;
            }
        }
        return nDisplayDepth;
    }
}
