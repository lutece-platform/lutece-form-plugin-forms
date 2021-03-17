    package fr.paris.lutece.plugins.forms.web.entrytype;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.service.download.FormsFileDownloadProvider;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.service.download.AbstractFileDownloadProvider;
import fr.paris.lutece.portal.service.download.FileDownloadData;
import fr.paris.lutece.portal.service.download.IFileDownloadProvider;

public class EntryTypeCommentDisplayService extends EntryTypeDefaultDisplayService
{
    public static final String MARK_FILENAME = "filename";
    public static final String MARK_URL_DOWNLOAD_BO = "url_download_bo";
    public static final String MARK_URL_DOWNLOAD_FO = "url_download_fo";
    
    public EntryTypeCommentDisplayService( String strEntryServiceName )
    {
        super( strEntryServiceName );
    }

    @Override
    public String getEntryTemplateDisplay( HttpServletRequest request, Entry entry, Locale locale, Map<String, Object> model, DisplayType displayType )
    {
        Field fieldFile = entry.getFieldByCode( IEntryTypeService.FIELD_DOWNLOADABLE_FILE );
        if ( fieldFile != null )
        {
            FileDownloadData fileDownloadData = new FileDownloadData( entry.getIdResource( ), Form.RESOURCE_TYPE, Integer.parseInt( fieldFile.getValue( ) ) );
            IFileDownloadProvider provider = AbstractFileDownloadProvider.findProvider( FormsFileDownloadProvider.PROVIDER_NAME );
            
            model.put( MARK_FILENAME, fieldFile.getTitle( ) );
            model.put( MARK_URL_DOWNLOAD_BO, provider.getDownloadUrl( fileDownloadData, true ) );
            model.put( MARK_URL_DOWNLOAD_FO, provider.getDownloadUrl( fileDownloadData, false ) );
        }
        return super.getEntryTemplateDisplay( request, entry, locale, model, displayType );
    }
}
