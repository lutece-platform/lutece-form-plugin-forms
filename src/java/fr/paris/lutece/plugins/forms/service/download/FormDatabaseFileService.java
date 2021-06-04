package fr.paris.lutece.plugins.forms.service.download;

import fr.paris.lutece.portal.service.file.IFileDownloadUrlService;
import fr.paris.lutece.portal.service.file.IFileRBACService;
import fr.paris.lutece.portal.service.file.implementation.LocalDatabaseFileService;

public class FormDatabaseFileService extends LocalDatabaseFileService
{

    private static final long serialVersionUID = -5737231818071574668L;

    public FormDatabaseFileService( IFileDownloadUrlService fileDownloadUrlService, IFileRBACService fileRBACService )
    {
        super( fileDownloadUrlService, fileRBACService );
    }

    public static final String FILE_STORE_PROVIDER_NAME = "formsDatabaseFileStoreProvider";
    
    @Override
    public String getName( )
    {
        return FILE_STORE_PROVIDER_NAME;
    }
}
