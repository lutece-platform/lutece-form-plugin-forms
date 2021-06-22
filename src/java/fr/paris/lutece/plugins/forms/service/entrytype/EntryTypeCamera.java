package fr.paris.lutece.plugins.forms.service.entrytype;

import fr.paris.lutece.plugins.asynchronousupload.service.IAsyncUploadHandler;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeCamera;
import fr.paris.lutece.util.url.UrlItem;

public class EntryTypeCamera extends AbstractEntryTypeCamera
{
    private static final String JSP_DOWNLOAD_FILE = "jsp/admin/plugins/forms/DoDownloadFile.jsp";
    private static final String TEMPLATE_CREATE = "admin/plugins/forms/entries/create_entry_type_camera.html";
    private static final String TEMPLATE_MODIFY = "admin/plugins/forms/entries/modify_entry_type_camera.html";
    private static final String TEMPLATE_HTML_CODE = "skin/plugins/forms/entries/fill_entry_type_camera.html";
    private static final String TEMPLATE_READONLY_BACKOFFICE = "admin/plugins/forms/entries/readonly_entry_type_camera.html";
    private static final String TEMPLATE_READONLY_FRONTOFFICE = "skin/plugins/forms/entries/readonly_entry_type_camera.html";
    
    @Override
    public String getTemplateCreate( Entry entry, boolean bDisplayFront )
    {
        return TEMPLATE_CREATE;
    }

    @Override
    public String getTemplateModify( Entry entry, boolean bDisplayFront )
    {
        return TEMPLATE_MODIFY;
    }

    @Override
    public String getUrlDownloadFile( int nResponseId, String strBaseUrl )
    {
        UrlItem url = new UrlItem( strBaseUrl + JSP_DOWNLOAD_FILE );
        url.addParameter( PARAMETER_ID_RESPONSE, nResponseId );

        return url.getUrl( );
    }
    
    @Override
    protected boolean checkForImages( )
    {
        return true;
    }

    @Override
    public String getTemplateHtmlForm( Entry entry, boolean bDisplayFront )
    {
        return TEMPLATE_HTML_CODE;
    }

    @Override
    public IAsyncUploadHandler getAsynchronousUploadHandler( )
    {
        return null;
    }
    
    @Override
    public String getTemplateEntryReadOnly( boolean bDisplayFront )
    {
        if ( bDisplayFront )
        {
            return TEMPLATE_READONLY_FRONTOFFICE;
        }

        return TEMPLATE_READONLY_BACKOFFICE;
    }
}
