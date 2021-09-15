package fr.paris.lutece.plugins.forms.service.anonymization;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.anonymization.AbstractTextAnonymizationService;
import fr.paris.lutece.plugins.genericattributes.service.anonymization.IEntryTypeAnonymisationService;

/**
 * {@link IEntryTypeAnonymisationService} that replace the wildcard by the form id.
 */
public class FormIdAnonymizationService extends AbstractTextAnonymizationService
{
    @Override
    protected String getAnonymisedValue( Entry entry, Response response )
    {
        return String.valueOf( entry.getIdResource( ) );
    }
   
}
