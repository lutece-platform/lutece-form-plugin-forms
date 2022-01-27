package fr.paris.lutece.plugins.forms.service;

import java.util.List;

import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.web.FormResponseData;
import fr.paris.lutece.portal.service.security.LuteceUser;

/**
 * Service for {@link FormResponse}
 *
 */
public interface IFormResponseService
{
    /**
     * Gets the list of form response data
     * @param user
     * @return
     */
    List<FormResponseData> getFormResponseListForUser( LuteceUser user );
}
