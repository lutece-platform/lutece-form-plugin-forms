package fr.paris.lutece.plugins.forms.service.resource;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.resource.IExtendableResource;
import fr.paris.lutece.portal.service.resource.IExtendableResourceService;

public class FormExtendableResourceService implements IExtendableResourceService
{

	private static final String MESSAGE_FORM_RESOURCE_TYPE_DESCRIPTION = "forms.resource.resourceTypeDescription";

	@Override
	public IExtendableResource getResource(String strIdResource, String strResourceType)
	{
		if ( StringUtils.isNotBlank( strIdResource ) && StringUtils.isNumeric( strIdResource ) )
        {
            int nIdDocument = Integer.parseInt( strIdResource );

            return FormHome.findByPrimaryKey( nIdDocument );
        }

        return null;
	}

	@Override
	public String getResourceType()
	{
		return Form.RESOURCE_TYPE;
	}

	@Override
	public String getResourceTypeDescription(Locale locale)
	{
		return I18nService.getLocalizedString( MESSAGE_FORM_RESOURCE_TYPE_DESCRIPTION , locale );
	}

	@Override
	public String getResourceUrl(String strIdResource, String strResourceType)
	{
		return null;
	}

	@Override
	public boolean isInvoked(String strResourceType)
	{
		return Form.RESOURCE_TYPE.equals( strResourceType );
	}

}
