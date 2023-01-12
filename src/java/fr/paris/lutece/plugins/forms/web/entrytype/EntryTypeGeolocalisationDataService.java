package fr.paris.lutece.plugins.forms.web.entrytype;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;

public class EntryTypeGeolocalisationDataService extends EntryTypeDefaultDataService {

	public EntryTypeGeolocalisationDataService(String strEntryServiceName)
	{
		super(strEntryServiceName);
	}
	
	@Override
    public List<String> responseToStrings( FormQuestionResponse formQuestionResponse )
    {
		List<String> listResponseValue = new ArrayList<>( );

        for ( Response response : formQuestionResponse.getEntryResponse( ) )
        {
            if (response.getField( ) != null && IEntryTypeService.FIELD_ADDRESS.equals((response.getField().getCode())))
    		{
            	String strResponseValue = response.getResponseValue();
            	if ( strResponseValue != null )
                {
                    listResponseValue.add( strResponseValue );
                }
    		}
        }
        return listResponseValue;
    }

}
