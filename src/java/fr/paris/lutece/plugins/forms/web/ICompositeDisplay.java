package fr.paris.lutece.plugins.forms.web;

import java.util.List;
import java.util.Locale;

import fr.paris.lutece.plugins.forms.business.FormDisplay;

/**
 * 
 * Interface composite to display step components
 *
 */
public interface ICompositeDisplay
{
    /**
     * @param locale
     *            The locale
     * 
     * @return the composite html to display
     */
    String getCompositeHtml( Locale locale );

    /**
     * 
     * @param formDisplay
     *            The parent formDisplay
     */
    void initComposite( FormDisplay formDisplay );

    /**
     * Return the full list of children ICompositeDisplay of an initialized CompositeDisplay
     * 
     * @return the children composite list
     */
    List<ICompositeDisplay> getCompositeList( );

    /**
     * 
     * @return the title of the compositeDisplay
     */
    String getTitle( );

    /**
     * 
     * @return the type of the compositeDisplay
     */
    String getType( );

    /**
     * Set the formDisplay
     * 
     * @param formDisplay
     *            the FormDisplay to set
     */
    void setFormDisplay( FormDisplay formDisplay );

    /**
     * return the FormDisplay
     * 
     * @return the FormDisplay
     */
    FormDisplay getFormDisplay( );

}
