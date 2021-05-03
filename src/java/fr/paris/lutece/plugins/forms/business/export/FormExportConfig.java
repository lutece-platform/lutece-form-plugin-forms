package fr.paris.lutece.plugins.forms.business.export;

public class FormExportConfig
{
    private int _nId;
    private int _nIdForm;
    private String _strField;
    private String _strFieldTitle;
    private int _nOrder;
    
    /**
     * @return the nId
     */
    public int getId( )
    {
        return _nId;
    }
    /**
     * @param nId the nId to set
     */
    public void setId( int nId )
    {
        _nId = nId;
    }
    /**
     * @return the nIdForm
     */
    public int getIdForm( )
    {
        return _nIdForm;
    }
    /**
     * @param nIdForm the nIdForm to set
     */
    public void setIdForm( int nIdForm )
    {
        _nIdForm = nIdForm;
    }
    /**
     * @return the strField
     */
    public String getField( )
    {
        return _strField;
    }
    /**
     * @param strField the strField to set
     */
    public void setField( String strField )
    {
        _strField = strField;
    }
    /**
     * @return the nOrder
     */
    public int getOrder( )
    {
        return _nOrder;
    }
    /**
     * @param nOrder the nOrder to set
     */
    public void setOrder( int nOrder )
    {
        _nOrder = nOrder;
    }
    /**
     * @return the strFieldTitle
     */
    public String getFieldTitle( )
    {
        return _strFieldTitle;
    }
    /**
     * @param strFieldTitle the strFieldTitle to set
     */
    public void setFieldTitle( String strFieldTitle )
    {
        _strFieldTitle = strFieldTitle;
    }
    
}
