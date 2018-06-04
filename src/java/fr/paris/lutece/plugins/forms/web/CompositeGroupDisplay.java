package fr.paris.lutece.plugins.forms.web;

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.Group;
import fr.paris.lutece.plugins.forms.business.GroupHome;
import fr.paris.lutece.plugins.forms.service.FormService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * Implementation of ICompositeDisplay for Groups
 *
 */
public class CompositeGroupDisplay implements ICompositeDisplay
{
    private static final String GROUP_TEMPLATE = "/skin/plugins/forms/composite_template/view_group.html";
    private static final String GROUP_TITLE_MARKER = "groupTitle";
    private static final String GROUP_CONTENT_MARKER = "groupContent";

    private List<ICompositeDisplay> _listChildren = new ArrayList<ICompositeDisplay>( );
    private Group _group;
    private FormDisplay _formDisplay;

    @Override
    public void initComposite( FormDisplay formDisplay )
    {
        if ( !StringUtils.isEmpty( formDisplay.getCompositeType( ) ) )
        {
            _group = GroupHome.findByPrimaryKey( formDisplay.getCompositeId( ) );
        }

        List<FormDisplay> formDisplayList = FormDisplayHome.getFormDisplayListByParent( formDisplay.getStepId( ), formDisplay.getId( ) );

        for ( FormDisplay formDisplayChild : formDisplayList )
        {
            ICompositeDisplay composite = FormService.formDisplayToComposite( formDisplayChild );
            _listChildren.add( composite );
            composite.initComposite( formDisplayChild );
        }
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

        model.put( GROUP_TITLE_MARKER, _group.getTitle( ) );
        model.put( GROUP_CONTENT_MARKER, strBuilder.toString( ) );

        HtmlTemplate t = AppTemplateService.getTemplate( GROUP_TEMPLATE, locale, model );

        return t.getHtml( );
    }

    @Override
    public List<ICompositeDisplay> getCompositeList( )
    {
        List<ICompositeDisplay> listICompositeDisplay = new ArrayList<ICompositeDisplay>( );
        listICompositeDisplay.add( this );

        for ( ICompositeDisplay child : _listChildren )
        {
            listICompositeDisplay.addAll( child.getCompositeList( ) );
        }
        return listICompositeDisplay;
    }

    @Override
    public String getTitle( )
    {
        String strTitle = "";
        if ( _group != null && StringUtils.isNotEmpty( _group.getTitle( ) ) )
        {
            strTitle = _group.getTitle( );
        }
        return strTitle;
    }

    @Override
    public String getType( )
    {
        return _group != null ? CompositeDisplayType.GROUP.getLabel( ) : StringUtils.EMPTY;
    }


    @Override
    public void setFormDisplay( FormDisplay formDisplay )
    {
        _formDisplay = formDisplay;
        
    }

    @Override
    public FormDisplay getFormDisplay( )
    {
        return _formDisplay;
    }
}
