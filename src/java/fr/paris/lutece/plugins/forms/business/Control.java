package fr.paris.lutece.plugins.forms.business;

/**
 * This is the business class for the object Control
 */
public class Control
{
    private int _nId;

    private String _strValue;

    private String _strErrorMessage;

    private int _nIdQuestion;

    private String _strValidatorName;

    private String _strControlType;

    /**
     * @return the _nId
     */
    public int getId( )
    {
        return _nId;
    }

    /**
     * @param nId
     *            the nId to set
     */
    public void setId( int nId )
    {
        this._nId = nId;
    }

    /**
     * @return the _strValue
     */
    public String getValue( )
    {
        return _strValue;
    }

    /**
     * @param strValue
     *            the strValue to set
     */
    public void setValue( String strValue )
    {
        this._strValue = strValue;
    }

    /**
     * @return the _strErrorMessage
     */
    public String getErrorMessage( )
    {
        return _strErrorMessage;
    }

    /**
     * @param strErrorMessage
     *            the strErrorMessage to set
     */
    public void setErrorMessage( String strErrorMessage )
    {
        this._strErrorMessage = strErrorMessage;
    }

    /**
     * @return the _nIdQuestion
     */
    public int getIdQuestion( )
    {
        return _nIdQuestion;
    }

    /**
     * @param nIdQuestion
     *            the nIdQuestion to set
     */
    public void setIdQuestion( int nIdQuestion )
    {
        this._nIdQuestion = nIdQuestion;
    }

    /**
     * @return the _strValidatorName
     */
    public String getValidatorName( )
    {
        return _strValidatorName;
    }

    /**
     * @param strValidatorName
     *            the strValidatorName to set
     */
    public void setValidatorName( String strValidatorName )
    {
        this._strValidatorName = strValidatorName;
    }

    /**
     * @return the _strControlType
     */
    public String getControlType( )
    {
        return _strControlType;
    }

    /**
     * @param strControlType
     *            the strControlType to set
     */
    public void setControlType( String strControlType )
    {
        this._strControlType = strControlType;
    }
}
