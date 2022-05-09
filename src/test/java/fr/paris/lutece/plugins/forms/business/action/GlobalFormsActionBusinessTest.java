package fr.paris.lutece.plugins.forms.business.action;

import java.util.List;
import java.util.Locale;

import org.apache.commons.collections4.CollectionUtils;

import fr.paris.lutece.plugins.forms.service.FormsPlugin;
import fr.paris.lutece.test.LuteceTestCase;

public class GlobalFormsActionBusinessTest extends LuteceTestCase
{

    public void testSelect( )
    {
        List<GlobalFormsAction> allActions = GlobalFormsActionHome.selectAllFormActions( FormsPlugin.getPlugin( ), Locale.getDefault( ) );
        assertNotNull( allActions );
        assertTrue( CollectionUtils.isNotEmpty( allActions ) );
        
        GlobalFormsAction action = allActions.get( 0 );
        
        GlobalFormsAction loaded = GlobalFormsActionHome.selectGlobalFormActionByCode( action.getCode( ), FormsPlugin.getPlugin( ), Locale.getDefault( ) );
        assertNotNull( loaded );
        assertEquals( action.getCode( ), loaded.getCode( ) );
        assertEquals( action.getDescription( ), loaded.getDescription( ) );
        assertEquals( action.getDescriptionKey( ), loaded.getDescriptionKey( ) );
        assertEquals( action.getName( ), loaded.getName( ) );
        assertEquals( action.getNameKey( ), loaded.getNameKey( ) );
        assertEquals( action.getIconUrl( ), loaded.getIconUrl( ) );
        assertEquals( action.getResourceId( ), loaded.getResourceId( ) );
        assertEquals( action.getResourceTypeCode( ), loaded.getResourceTypeCode( ) );
        assertEquals( action.getUrl( ), loaded.getUrl( ) );
    }
}
