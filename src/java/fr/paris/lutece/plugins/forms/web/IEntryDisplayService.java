package fr.paris.lutece.plugins.forms.web;

import java.util.Locale;

import fr.paris.lutece.plugins.genericattributes.business.Entry;

/**
 * Interface for the entry display services
 */
public interface IEntryDisplayService
{
    /**
     * @return the display service name
     */
    String getDisplayServiceName( );

    /**
     * Get the Html of the given step
     * 
     * @param entry
     *            The given entry
     * @param locale
     *            The given locale
     * @return the template Html of the given entry
     */
    String getEntryTemplateDisplay( Entry entry, Locale locale );
}
