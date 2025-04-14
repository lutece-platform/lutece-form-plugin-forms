package fr.paris.lutece.plugins.forms.service.listener;

import fr.paris.lutece.portal.service.util.RemovalListenerService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

@ApplicationScoped
public class RemovalListenerServicesProducer 
{
	@Produces
    @Named("forms.formRemovalService")
    @ApplicationScoped
    public RemovalListenerService produceFormRemovalService( ) 
	{
        return new RemovalListenerService( );
    }

	@Produces
    @Named("forms.questionRemovalService")
    @ApplicationScoped
    public RemovalListenerService produceQuestionRemovalService( ) 
	{
        return new RemovalListenerService( );
    }
}
