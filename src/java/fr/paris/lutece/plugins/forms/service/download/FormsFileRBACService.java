package fr.paris.lutece.plugins.forms.service.download;

import java.util.Map;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.service.FormsResourceIdService;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.IFileRBACService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;

public class FormsFileRBACService implements IFileRBACService
{
    private static final long serialVersionUID = 108613457653577978L;
    private static final String UNAUTHORIZED = "Unauthorized";
    
    @Override
    public void checkAccessRights( Map<String, String> fileData, User user ) throws AccessDeniedException, UserNotSignedException
    {
        boolean bo = Boolean.parseBoolean( fileData.get( FileService.PARAMETER_BO ) );
        String resourceType = fileData.get( FileService.PARAMETER_RESOURCE_TYPE );
        String resourceId = fileData.get( FileService.PARAMETER_RESOURCE_ID );
        
        if ( bo && user != null )
        {
            AdminUser adminUser = (AdminUser) user;
            if ( !RBACService.isAuthorized( resourceType, String.valueOf( resourceId ),
                    FormsResourceIdService.PERMISSION_MODIFY, (User) adminUser ) )
            {
                throw new AccessDeniedException( UNAUTHORIZED );
            }
        }
        else
            if ( !bo )
            {
                Form form = FormHome.findByPrimaryKey( Integer.parseInt( resourceId ) );
                if ( form.isAuthentificationNeeded( ) && user == null )
                {
                    throw new UserNotSignedException( );
                }
            }
        
    }

}
