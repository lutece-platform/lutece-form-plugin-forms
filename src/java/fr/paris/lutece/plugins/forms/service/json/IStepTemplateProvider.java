package fr.paris.lutece.plugins.forms.service.json;

import fr.paris.lutece.util.ReferenceList;

/**
 * Provider of Step templates to import in a form.
 *
 */
public interface IStepTemplateProvider
{
    /**
     * Get the list of available Step Templates
     * @return
     */
    ReferenceList getStepTemplateList( );
    
    /**
     * Get the step template data.
     * @param idTemplate
     * @return
     */
    StepJsonData getStepTemplateData( int idTemplate );
    
    /**
     * THe bean name of the provider
     * @return
     */
    String getProviderBeanName( );
}
