package fr.paris.lutece.plugins.forms.service;

import java.util.List;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlMapping;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.Group;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;

public interface IFormDatabaseService
{
    /**
     * Find a step by its id
     * @param idStep
     * @return
     */
    Step findStepByPrimaryKey( int idStep );
    
    /**
     * Find a qusteion by its id
     * @param idQuestion
     * @return
     */
    Question findQuestionByPrimaryKey( int idQuestion );
    
    /**
     * Find an entry by its id
     * @param idEntry
     * @return
     */
    Entry findEntryByPrimaryKey( int idEntry );
    
    /**
     * Find a group by its id
     * @param idGroup
     * @return
     */
    Group findGroupByPrimaryKey( int idGroup );
    
    /**
     * Find a field by its id
     * @param idField
     * @return
     */
    Field findFieldByPrimaryKey( int idField );
    
    /**
     * Find a formdisplay by its id
     * @param idDisplay
     * @return
     */
    FormDisplay findDisplayByPrimaryKey( int idDisplay );
    
    /**
     * copy an entry
     * @param entry
     * @return
     */
    Entry copyEntry( Entry entry );
    
    /**
     * Create an entry
     * @param entry
     */
    void createEntry( Entry entry );
    
    /**
     * Create a formDisplay
     * @param formDisplay
     */
    void createFormDisplay( FormDisplay formDisplay );
    
    /**
     *  Create a field
     * @param field
     */
    void createField( Field field );
    
    /**
     *  Create a step
     * @param step
     */
    void createStep( Step step );
    
    /**
     * Create a question
     * @param question
     */
    void createQuestion( Question question );
    
    /**
     * Create a control
     * @param control
     */
    void createControl( Control control );
    
    /**
     * Create a group
     * @param group
     */
    void createGroup( Group group );
    
    /**
     * update a group
     * @param group
     */
    void updateGroup( Group group );
    
    /**
     * update an entry
     * @param entry
     */
    void updateEntry( Entry entry );
    
    /**
     * update a field
     * @param field
     */
    void updateField( Field field );
    
    /**
     * update a question
     * @param question
     */
    void updateQuestion( Question question );
    
    /**
     * update a FormDisplay
     * @param formDisplay
     */
    void updateFormDisplay( FormDisplay formDisplay );
    
    /**
     * Find all Questions by form
     * @param nIdForm
     * @return
     */
    List<Question> getListQuestionByForm( int nIdForm );
    
    /**
     * Load the data of all the formDisplay that are direct children of a given parent and returns them as a list
     * 
     * @param nIdStep
     *            The step primary key
     * @param nIdParent
     *            The parent primary key
     * @return the list which contains the data of all the formDisplay objects by parent
     */
    List<FormDisplay> getFormDisplayListByParent( int nIdStep, int nIdParent );
    
    /**
     * Loads control list based on its attached question and control type
     * 
     * @param nIdQuestion
     *            the question id
     * @return the requested Control list
     */
    List<Control> getControlByQuestion( int nIdQuestion );
    
    /**
     * Return the FormDisplay associated to the given parameters
     * 
     * @param nIdForm
     *            The identifier of the Form to match
     * @param nIdStep
     *            The identifier of the Step to match
     * @param nIdComposite
     *            The identifier of the Composite to match
     * @return the formDisplay associated to the given parameters
     */
    FormDisplay getFormDisplayByFormStepAndComposite( int nIdForm, int nIdStep, int nIdComposite );
    
    /**
     * Load the data of all the group objects and returns them as a list
     * 
     * @param idStepList
     * @return the list which contains the data of all the group objects
     */
   List<Group> getGroupsListByIdStepList( List<Integer> idStepList );
   
   /**
    * Load the data of all the question objects and returns them as a list
    * 
    * @param nIdStep
    *            The step primary key
    * @return the list which contains the data of all the question objects
    */
   List<Question> getQuestionsListByStep( int nIdStep );
   
   /**
    * Loads records form the database.
    * 
    * @param idField
    * @return
    */
   Integer findIdReferenceItemByIdField( int idField );
   
   /**
    * Load the data of all the control objects and returns them as a list
    * 
    * @param nIdControl
    *            the Control id
    * @return the referenceList which contains the data of all the control objects
    */
   List<ControlMapping> getControlMappingListByIdControl( int nIdControl );
   
   void createMappingControl( int nIdcontrol, int nIdQuestion, String strValue );
}
