package fr.paris.lutece.plugins.forms.service.resource;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.resource.IExtendableResource;
import fr.paris.lutece.portal.service.resource.IExtendableResourceService;

public class FormResponseExtendableResourceService implements IExtendableResourceService
{

	private static final String MESSAGE_FORMS_RESOURCE_TYPE_DESCRIPTION = "forms.resource.resourceTypeDescription";

	@Override
	public IExtendableResource getResource(String strIdResource, String strResourceType )
	{
		if ( StringUtils.isNotBlank( strIdResource ) && StringUtils.isNumeric( strIdResource ) )
        {
            int nIdFormResponse = Integer.parseInt( strIdResource );
            return FormResponseHome.findByPrimaryKey( nIdFormResponse );
        }
        return null;
	}

	@Override
	public String getResourceType()
	{
		return FormResponse.RESOURCE_TYPE;
	}

	@Override
	public String getResourceTypeDescription(Locale locale)
	{
		return I18nService.getLocalizedString( MESSAGE_FORMS_RESOURCE_TYPE_DESCRIPTION, locale );
	}

	@Override
	public String getResourceUrl(String strIdResource, String strResourceType )
	{
		return null;
	}

	@Override
	public boolean isInvoked(String strResourceType )
	{
		return FormResponse.RESOURCE_TYPE.equals(strResourceType );
	}

}
