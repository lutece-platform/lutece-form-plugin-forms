package fr.paris.lutece.plugins.forms.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.service.FormService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * 
 * Management class for the display tree of a Step
 *
 */
public class StepDisplayTree
{
    private static final String STEP_TEMPLATE = "/skin/plugins/forms/composite_template/view_step.html";
    private static final String STEP_TITLE_MARKER = "stepTitle";
    private static final String STEP_CONTENT_MARKER = "stepContent";

    private List<ICompositeDisplay> _listChildren = new ArrayList<ICompositeDisplay>( );
    private List<ICompositeDisplay> _listICompositeDisplay = new ArrayList<ICompositeDisplay>( );
    private Step _step;


    /** Constructor
     * 
     * @param nIdStep the step identifier
     */
    public StepDisplayTree ( int nIdStep )
    {
        initStepTree( nIdStep );
    }


    /**
     * Initialize the composite tree
     * 
     * @param nIdStep
     *            The step primary key
     */
    public void initStepTree( int nIdStep )
    {
        _step = StepHome.findByPrimaryKey( nIdStep );

        if ( _step != null )
        {
            List<FormDisplay> listStepFormDisplay = FormDisplayHome.getFormDisplayListByParent( nIdStep, 0 );

            for ( FormDisplay formDisplayChild : listStepFormDisplay )
            {
                ICompositeDisplay composite = FormService.formDisplayToComposite( formDisplayChild );
                _listChildren.add( composite );
                composite.initComposite( formDisplayChild );
            }
        }
    }



    /**
     * Build and return the html template of the tree for Front-Office display
     * @param locale the locale
     * @return the html template of the tree as a String
     */
    public String getCompositeHtml( Locale locale )
    {
        Map<String, Object> model = new HashMap<String, Object>( );

        StringBuilder strBuilder = new StringBuilder( );

        for ( ICompositeDisplay child : _listChildren )
        {
            strBuilder.append( child.getCompositeHtml( locale ) );
        }

        model.put( STEP_TITLE_MARKER, _step.getTitle( ) );
        model.put( STEP_CONTENT_MARKER, strBuilder.toString( ) );

        HtmlTemplate t = AppTemplateService.getTemplate( STEP_TEMPLATE, locale, model );

        return t.getHtml( );
    }

    /**
     * Build and return all the composite display of the tree as a flat List
     * @return the list of composite display
     */
    public List<ICompositeDisplay> getCompositeList( )
    {        
        for ( ICompositeDisplay child : _listChildren )
        {
            _listICompositeDisplay.addAll( child.getCompositeList( ) );
        }
        return _listICompositeDisplay;
    }


}
