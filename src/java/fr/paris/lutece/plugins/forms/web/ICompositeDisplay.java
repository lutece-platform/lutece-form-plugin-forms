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
     * 
     * @return the display depth indent of the compositeDisplay
     */
    int getDepthIndent( );

    /**
     * 
     * Set the display depth indent of the compositeDisplay
     */
    /**
     * @param nDepth
     *            the display depth
     */
    void setDepthIndent( int nDepth );

    /**
     * 
     * Set the parent identifier the compositeDisplay
     * 
     * @param nParentId
     *            the parent id
     */
    void setParentId( int nParentId );

    /**
     * return the parent identifier the compositeDisplay
     * 
     * @return the parent identifier
     */
    int getParentId( );

    /**
     * @return the IdDisplay
     */
    int getIdDisplay( );

    /**
     * @param nIdDisplay
     *            the IdDisplay to set
     */
    void setIdDisplay( int nIdDisplay );

}
