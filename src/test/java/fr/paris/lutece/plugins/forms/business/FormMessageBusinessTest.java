package fr.paris.lutece.plugins.forms.business;

import fr.paris.lutece.test.LuteceTestCase;

public class FormMessageBusinessTest extends LuteceTestCase
{

    public void testCRUD( )
    {
        FormMessage formMessage = new FormMessage( );
        formMessage.setIdForm( 1 );
        formMessage.setEndMessageDisplay( true );
        formMessage.setEndMessage( "Message" );
        
        FormMessageHome.create( formMessage );
        
        FormMessage loaded = FormMessageHome.findByForm( formMessage.getId( ) );
        assertNotNull( loaded );
        assertEquals( formMessage.getIdForm( ), loaded.getIdForm( ) );
        assertEquals( formMessage.getEndMessageDisplay( ), loaded.getEndMessageDisplay( ) );
        assertEquals( formMessage.getEndMessage( ), loaded.getEndMessage( ) );
        
        formMessage.setEndMessage( "Message 2" );
        FormMessageHome.update( formMessage );
        
        loaded = FormMessageHome.findByForm( formMessage.getId( ) );
        assertNotNull( loaded );
        assertEquals( formMessage.getEndMessage( ), loaded.getEndMessage( ) );
        
        FormMessageHome.remove( formMessage.getId( ) );
        
        loaded = FormMessageHome.findByForm( formMessage.getId( ) );
        assertNull( loaded );
    }
}
