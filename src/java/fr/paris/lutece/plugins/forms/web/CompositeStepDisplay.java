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
 * Implementation of ICompositeDisplay for Step
 *
 */
public class CompositeStepDisplay implements ICompositeDisplay
{
    private static final String STEP_TEMPLATE = "/skin/plugins/forms/composite_template/view_step.html";
    private static final String STEP_TITLE_MARKER = "stepTitle";
    private static final String STEP_CONTENT_MARKER = "stepContent";

    private List<ICompositeDisplay> _listChildren = new ArrayList<ICompositeDisplay>( );
    private Step _step;

    /**
     * Init the composite tree
     * 
     * @param nIdStep
     *            The step primary key
     */
    public void initStep( int nIdStep )
    {
        _step = StepHome.findByPrimaryKey( nIdStep );

        if ( _step != null )
        {
            FormDisplay stepFormDisplay = FormDisplayHome.getFormDisplayListByParent( _step.getId( ), 0 ).get( 0 );
            List<FormDisplay> listStepFormDisplay = FormDisplayHome.getFormDisplayListByParent( stepFormDisplay.getStepId( ),
                    stepFormDisplay.getId( ) );

            for ( FormDisplay formDisplayChild : listStepFormDisplay )
            {
                ICompositeDisplay composite = FormService.formDisplayToComposite( formDisplayChild );
                _listChildren.add( composite );
                composite.initComposite( formDisplayChild );
            }
        }
    }

    @Override
    public void initComposite( FormDisplay formDisplay )
    {
        _step = StepHome.findByPrimaryKey( formDisplay.getCompositeId( ) );
    }

    @Override
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
    
    
}
