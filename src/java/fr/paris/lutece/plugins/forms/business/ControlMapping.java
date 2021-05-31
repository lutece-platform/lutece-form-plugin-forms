package fr.paris.lutece.plugins.forms.business;

public class ControlMapping
{
    private int _idControl;
    private int _idQuestion;
    private String _value;
    /**
     * @return the idControl
     */
    public int getIdControl( )
    {
        return _idControl;
    }
    /**
     * @param idControl the idControl to set
     */
    public void setIdControl( int idControl )
    {
        _idControl = idControl;
    }
    /**
     * @return the idQuestion
     */
    public int getIdQuestion( )
    {
        return _idQuestion;
    }
    /**
     * @param idQuestion the idQuestion to set
     */
    public void setIdQuestion( int idQuestion )
    {
        _idQuestion = idQuestion;
    }
    /**
     * @return the value
     */
    public String getValue( )
    {
        return _value;
    }
    /**
     * @param value the value to set
     */
    public void setValue( String value )
    {
        _value = value;
    }
    
}
