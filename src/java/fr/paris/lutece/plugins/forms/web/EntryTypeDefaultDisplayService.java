package fr.paris.lutece.plugins.forms.web;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.service.template.AppTemplateService;

/**
 * The default display service
 */
public class EntryTypeDefaultDisplayService implements IEntryDisplayService
{
    private String _strEntryServiceName = StringUtils.EMPTY;

    /**
     * Constructor of the EntryTypeDefaultDisplayService
     * 
     * @param strEntryServiceName
     *            The entry service name
     */
    public EntryTypeDefaultDisplayService( String strEntryServiceName )
    {
        _strEntryServiceName = strEntryServiceName;
    }

    /**
     * Return the completed model
     * 
     * @param entry
     *            The given entry
     * @return the completed model
     */
    private Map<String, Object> getModel( Entry entry )
    {
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( FormsConstants.QUESTION_ENTRY_MARKER, entry );

        return model;
    }

    @Override
    public String getDisplayServiceName( )
    {
        return _strEntryServiceName;
    }

    @Override
    public String getEntryTemplateDisplay( Entry entry, Locale locale )
    {
        IEntryTypeService service = EntryTypeServiceManager.getEntryTypeService( entry );

        String strEntryHtml = AppTemplateService.getTemplate( service.getTemplateHtmlForm( entry, true ), locale, getModel( entry ) ).getHtml( );

        return strEntryHtml;
    }

}
