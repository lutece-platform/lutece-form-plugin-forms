package fr.paris.lutece.plugins.forms.service.download;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.service.FormsResourceIdService;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.download.AbstractFileDownloadProvider;
import fr.paris.lutece.portal.service.download.FileDownloadData;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

public class FormsFileDownloadProvider extends AbstractFileDownloadProvider
{
    public static final String PROVIDER_NAME = "forms.formsFileDownloadProvider";
    
    private static final String UNAUTHORIZED = "Unauthorized";
    private static final int VALIDITY_DURATION =Integer.parseInt( AppPropertiesService.getProperty( "forms.file.download.validity", "0" ) );
    
    @Override
    public String getProviderName( )
    {
        return PROVIDER_NAME;
    }
    
    @Override
    public int getLinkValidityTime( )
    {
        return VALIDITY_DURATION;
    }

    @Override
    protected void checkUserDownloadRight( User user, boolean bo, FileDownloadData fileDownloadData ) throws AccessDeniedException, UserNotSignedException
    {
        if ( bo && user != null )
        {
            AdminUser adminUser = (AdminUser) user;
            if ( !RBACService.isAuthorized( fileDownloadData.getResourceType( ), String.valueOf( fileDownloadData.getIdResource( ) ), FormsResourceIdService.PERMISSION_MODIFY, (User) adminUser ) )
            {
                throw new AccessDeniedException( UNAUTHORIZED );
            }
        }
        else if ( !bo )
        {
            Form form = FormHome.findByPrimaryKey( fileDownloadData.getIdResource( ) );
            if ( form.isAuthentificationNeeded( ) && user == null )
            {
                throw new UserNotSignedException( );
            }
        }
    }

}
