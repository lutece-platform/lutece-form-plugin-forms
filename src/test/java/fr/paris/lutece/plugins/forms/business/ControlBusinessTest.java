package fr.paris.lutece.plugins.forms.business;

import java.util.HashSet;

import fr.paris.lutece.test.LuteceTestCase;

public class ControlBusinessTest extends LuteceTestCase
{

    public void testBusiness()
    {
        Control control = new Control( );
        control.setValue( "test" );
        control.setValidatorName( "validator1" );
        control.setControlType( ControlType.CONDITIONAL.name( ) );
        control.setListIdQuestion( new HashSet<>( ) );
        control.getListIdQuestion( ).add( 12 );
        
        ControlHome.create( control );
        
        Control controleLoaded = ControlHome.findByPrimaryKey( control.getId( ) );
        assertEquals( "test", controleLoaded.getValue( ) );
        assertEquals( "validator1", controleLoaded.getValidatorName( ) );
        assertEquals( ControlType.CONDITIONAL.name( ), controleLoaded.getControlType( ) );
        assertEquals( 1, control.getListIdQuestion( ).size( ) );
        assertEquals( (int) 12, (int) control.getListIdQuestion( ).iterator( ).next( ) );
        
        controleLoaded.setValidatorName( "validator2" );
        
        ControlHome.update( controleLoaded );
        
        controleLoaded = ControlHome.findByPrimaryKey( control.getId( ) );
        
        assertEquals( "validator2", controleLoaded.getValidatorName( ) );
        
        ControlHome.remove( control.getId( ) );
        
        controleLoaded = ControlHome.findByPrimaryKey( control.getId( ) );
        
        assertNull( controleLoaded );
    }
}
