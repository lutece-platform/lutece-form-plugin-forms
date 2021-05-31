package fr.paris.lutece.plugins.forms.util;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlMapping;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormMessage;
import fr.paris.lutece.plugins.forms.business.Group;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.Transition;

public class FormJsonData
{

    private Form _form;
    private FormMessage _formMessage;
    private List<Step> _stepList;
    private List<Group> _groupList;
    private List<Question> _questionList;
    private List<Transition> _transitionList;
    private List<FormDisplay> _formDisplayList;
    private List<Control> _controlList;
    private List<ControlMapping> _controlMappingList;

    /**
     * @return the form
     */
    public Form getForm( )
    {
        return _form;
    }

    /**
     * @param form the form to set
     */
    public void setForm( Form form )
    {
        _form = form;
    }

    /**
     * @return the stepList
     */
    public List<Step> getStepList( )
    {
        return new ArrayList<>( _stepList );
    }

    /**
     * @param stepList the stepList to set
     */
    public void setStepList( List<Step> stepList )
    {
        _stepList = new ArrayList<>( stepList );
    }

    /**
     * @return the questionList
     */
    public List<Question> getQuestionList( )
    {
        return new ArrayList<>( _questionList );
    }

    /**
     * @param questionList the questionList to set
     */
    public void setQuestionList( List<Question> questionList )
    {
        _questionList = new ArrayList<>( questionList );
    }

    /**
     * @return the transitionList
     */
    public List<Transition> getTransitionList( )
    {
        return new ArrayList<>( _transitionList );
    }

    /**
     * @param transitionList the transitionList to set
     */
    public void setTransitionList( List<Transition> transitionList )
    {
        _transitionList = new ArrayList<>( transitionList );
    }

    /**
     * @return the formMessage
     */
    public FormMessage getFormMessage( )
    {
        return _formMessage;
    }

    /**
     * @param formMessage the formMessage to set
     */
    public void setFormMessage( FormMessage formMessage )
    {
        _formMessage = formMessage;
    }

    /**
     * @return the formDisplayList
     */
    public List<FormDisplay> getFormDisplayList( )
    {
        return new ArrayList<>( _formDisplayList );
    }

    /**
     * @param formDisplayList the formDisplayList to set
     */
    public void setFormDisplayList( List<FormDisplay> formDisplayList )
    {
        _formDisplayList = new ArrayList<>( formDisplayList );
    }

    /**
     * @return the groupList
     */
    public List<Group> getGroupList( )
    {
        return new ArrayList<>( _groupList );
    }

    /**
     * @param groupList the groupList to set
     */
    public void setGroupList( List<Group> groupList )
    {
        _groupList = new ArrayList<>( groupList );
    }

    /**
     * @return the controlList
     */
    public List<Control> getControlList( )
    {
        return new ArrayList<>( _controlList );
    }

    /**
     * @param controlList the controlQuestionList to set
     */
    public void setControlList( List<Control> controlList )
    {
        _controlList = new ArrayList<>( controlList );
    }

    /**
     * @return the controlMappingList
     */
    public List<ControlMapping> getControlMappingList( )
    {
        return new ArrayList<>( _controlMappingList );
    }

    /**
     * @param controlMappingList the controlMappingList to set
     */
    public void setControlMappingList( List<ControlMapping> controlMappingList )
    {
        _controlMappingList = new ArrayList<>( controlMappingList );
    }
    
}
