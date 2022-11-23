package fr.paris.lutece.plugins.forms.exception;

public class MaxFormResponseException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7763828168767505977L;
	
	/**
     * Constructor
     *
     * @param strMessage
     *            The error message
     */
    public MaxFormResponseException( String strMessage )
    {
        super( strMessage );
    }
    
    /**
     * Constructor
     *
     * @param strMessage
     *            The error message
     * @param exception
     *            The initial exception
     */
    public MaxFormResponseException( String strMessage, Exception exception )
    {
        super( strMessage, exception );
    }
    
    /**
     * Constructor
     */
    public MaxFormResponseException( )
    {
        super( );
    }
}
