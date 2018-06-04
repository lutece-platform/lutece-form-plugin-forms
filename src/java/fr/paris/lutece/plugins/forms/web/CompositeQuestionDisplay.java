package fr.paris.lutece.plugins.forms.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.service.EntryServiceManager;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * 
 * Implementation of ICompositeDisplay for Question
 *
 */
public class CompositeQuestionDisplay implements ICompositeDisplay
{
    private static final String QUESTION_TEMPLATE = "/skin/plugins/forms/composite_template/view_question.html";
    private static final String QUESTION_CONTENT_MARKER = "questionContent";

    private Question _question;
    private FormDisplay _formDisplay;

    @Override
    public void initComposite( FormDisplay formDisplay )
    {
        _question = QuestionHome.findByPrimaryKey( formDisplay.getCompositeId( ) );
    }

    @Override
    public String getCompositeHtml( Locale locale )
    {
        String strQuestionTemplate = StringUtils.EMPTY;

        if ( _question.getEntry( ) != null )
        {
            IEntryDisplayService displayService = EntryServiceManager.getInstance( ).getEntryDisplayService( _question.getEntry( ).getEntryType( ) );

            if ( displayService != null )
            {
                strQuestionTemplate = displayService.getEntryTemplateDisplay( _question.getEntry( ), locale );

                Map<String, Object> model = new HashMap<String, Object>( );

                model.put( QUESTION_CONTENT_MARKER, strQuestionTemplate );

                HtmlTemplate t = AppTemplateService.getTemplate( QUESTION_TEMPLATE, locale, model );

                strQuestionTemplate = t != null ? t.getHtml( ) : StringUtils.EMPTY;
            }
        }

        return strQuestionTemplate;
    }

    @Override
    public List<ICompositeDisplay> getCompositeList( )
    {
        List<ICompositeDisplay> listICompositeDisplay = new ArrayList<ICompositeDisplay>( );
        listICompositeDisplay.add( this );
        return listICompositeDisplay;
    }

    @Override
    public String getTitle( )
    {
        String strTitle = "";
        if ( _question != null && StringUtils.isNotEmpty( _question.getTitle( ) ) )
        {
            strTitle = _question.getTitle( );
        }
        return strTitle;
    }

    @Override
    public String getType( )
    {
        return _question != null ? CompositeDisplayType.QUESTION.getLabel( ) : StringUtils.EMPTY;
    }

    /**
     * @return the formDisplay
     */
    public FormDisplay getFormDisplay( )
    {
        return _formDisplay;
    }

    /**
     * @param formDisplay
     *            the formDisplay to set
     */
    public void setFormDisplay( FormDisplay formDisplay )
    {
        _formDisplay = formDisplay;
    }

}
