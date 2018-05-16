package fr.paris.lutece.plugins.forms.service;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.web.CompositeGroupDisplay;
import fr.paris.lutece.plugins.forms.web.CompositeQuestionDisplay;
import fr.paris.lutece.plugins.forms.web.CompositeStepDisplay;
import fr.paris.lutece.plugins.forms.web.ICompositeDisplay;

/**
 * This is the service class related to the form
 */
public final class FormService
{

    /**
     * Constructor
     */
    private FormService( )
    {
        throw new AssertionError( );
    }

    /**
     * Get the Html of the given step
     * 
     * @param nIdStep
     *            The step primary key
     * @param locale
     *            The locale
     * @return the Html of the given step
     */
    public static String getStepHtml( int nIdStep, Locale locale )
    {
        String strStepHtml = StringUtils.EMPTY;

        CompositeStepDisplay composite = new CompositeStepDisplay( );

        composite.initStep( nIdStep );

        strStepHtml = composite.getCompositeHtml( locale );

        return strStepHtml;
    }

    /**
     * Get the right composite from the given formDisplay
     * 
     * @param formDisplay
     *            The formDisplay
     * @return the right composite
     */
    public static ICompositeDisplay formDisplayToComposite( FormDisplay formDisplay )
    {
        if ( FormsConstants.COMPOSITE_GROUP_TYPE.equals( formDisplay.getCompositeType( ) ) )
        {
            CompositeGroupDisplay composite = new CompositeGroupDisplay( );

            return composite;
        }
        else
            if ( FormsConstants.COMPOSITE_QUESTION_TYPE.equals( formDisplay.getCompositeType( ) ) )
            {
                CompositeQuestionDisplay composite = new CompositeQuestionDisplay( );

                return composite;
            }
            else
            {
                return null;
            }
    }
}
