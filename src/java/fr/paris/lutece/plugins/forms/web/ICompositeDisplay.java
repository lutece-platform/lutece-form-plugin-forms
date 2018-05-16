package fr.paris.lutece.plugins.forms.web;

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
}
