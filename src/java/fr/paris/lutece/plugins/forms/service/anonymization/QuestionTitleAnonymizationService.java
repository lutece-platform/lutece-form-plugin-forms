package fr.paris.lutece.plugins.forms.service.anonymization;

import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.anonymization.AbstractTextAnonymizationService;
import fr.paris.lutece.plugins.genericattributes.service.anonymization.IEntryTypeAnonymisationService;

/**
 * {@link IEntryTypeAnonymisationService} that replace the wildcard by the question title.
 */
public class QuestionTitleAnonymizationService extends AbstractTextAnonymizationService
{
    @Override
    protected String getAnonymisedValue( Entry entry, Response response )
    {
        return QuestionHome.findByCode( entry.getCode( ) ).getTitle( );
    }
   
}
