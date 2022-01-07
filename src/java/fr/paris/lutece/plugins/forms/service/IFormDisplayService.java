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
package fr.paris.lutece.plugins.forms.service;

import java.util.List;
import java.util.Locale;

import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.util.ReferenceList;

public interface IFormDisplayService
{

    /**
     * Method to retrieve the Move composite page title from a given FormDisplay
     * 
     * @param formDisplayToMove
     *            the FormDisplay
     * 
     * @return the admin page title
     */
    String getDisplayTitle( FormDisplay formDisplayToMove );

    /**
     * Method to retrieve referenceList containing all the Displays of a given Step that could be a potential destination parent for the given FormDisplay. The
     * ReferenceList begins with a first root level item, followed by all the relevant children displays of type "Group". If the Display to be moved is a group,
     * this Display and its child groups will be excluded from the list
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
    ReferenceList getTargetGroupDisplayListByStep( int nIdStep, FormDisplay formDisplayToMove, Locale locale );

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
    ReferenceList getlistAvailablePositionsInGroup( int nIdStep, int nIdDisplayGroup, boolean bAddPosition, Locale locale );

    int getDisplayDepthFromParent( int parentGroup );

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
    void setChildrenDisplayDepthAndStep( FormDisplay formDisplayParent, int nDepth, int nIdStep );

    /**
     * Rebuild the position sequence of a given FormDisplay list and update the objects in database. The list indexes will be used to set the displayOrder
     * values.
     * 
     * @param listDisplay
     *            The List of FormDisplay to update
     */
    void rebuildDisplayPositionSequence( List<FormDisplay> listDisplay );

    /**
     * Remove the formDisplay whose identifier is specified in parameter. The responses, group/question associated to this display will be deleted. All the
     * descendants of the display will also be removed
     * 
     * @param nIdDIsplay
     *            The formDisplay Id
     */
    void deleteDisplayAndDescendants( int nIdDIsplay );
}
