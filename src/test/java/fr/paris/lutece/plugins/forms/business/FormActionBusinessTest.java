package fr.paris.lutece.plugins.forms.business;

import java.util.List;
import java.util.Locale;

import org.apache.commons.collections4.CollectionUtils;

import fr.paris.lutece.plugins.forms.service.FormsPlugin;
import fr.paris.lutece.test.LuteceTestCase;

public class FormActionBusinessTest extends LuteceTestCase
{
    public void testSelect( )
    {
        List<FormAction> allActions = FormActionHome.selectAllFormActions( FormsPlugin.getPlugin( ), Locale.getDefault( ) );
        assertNotNull( allActions );
        assertTrue( CollectionUtils.isNotEmpty( allActions ) );
    }
}
