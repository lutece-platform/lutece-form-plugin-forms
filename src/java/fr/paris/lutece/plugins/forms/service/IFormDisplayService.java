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
     * Method to retrieve referenceList containing all the Displays of a given Step that could be a potential destination parent for the given
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
