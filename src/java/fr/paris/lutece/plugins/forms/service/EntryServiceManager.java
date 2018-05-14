package fr.paris.lutece.plugins.forms.service;

import java.util.List;

import fr.paris.lutece.plugins.forms.web.IEntryDisplayService;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This is the manager class for different entry type
 */
public final class EntryServiceManager 
{
	private List<IEntryDisplayService> _listEntryDisplayService;
		
	/**
	 * Constructor for EntryServiceManager class
	 */
	private EntryServiceManager( )
	{
		_listEntryDisplayService = SpringContextService.getBeansOfType( IEntryDisplayService.class );
	}
	
	/**
     * Gives the instance
     * 
     * @return the instance
     */
    public static EntryServiceManager getInstance( )
    {
        return EntryServiceManagerHolder._instance;
    }
	
	
	/**
     * Get the right IEntryDisplayService
     * 
     * @param entryType
     *            The entrytype
     * @return the IEntryDisplayService
     */
	public IEntryDisplayService getEntryDisplayService( EntryType entryType )
	{
		IEntryDisplayService entryDisplayService = null;
		
		for( IEntryDisplayService entryDisplayServiceTemp : _listEntryDisplayService )
		{
			if( entryType.getBeanName( ).equals( entryDisplayServiceTemp.getDisplayServiceName( ) ) )
			{
				entryDisplayService = entryDisplayServiceTemp;
			}
		}
		
		return entryDisplayService;
	}
	
	/**
     * This class holds the EntryServiceManager instance
     *
     */
    private static class EntryServiceManagerHolder
    {
        private static EntryServiceManager _instance = new EntryServiceManager( );
    }
}
