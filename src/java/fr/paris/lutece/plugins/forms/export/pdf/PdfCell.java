package fr.paris.lutece.plugins.forms.export.pdf;

import org.apache.commons.lang3.StringUtils;

public class PdfCell
{
    private String _title;
    private String _value;
    private String _group;
    /**
     * @return the title
     */
    public String getTitle( )
    {
        return _title;
    }
    /**
     * @param title the title to set
     */
    public void setTitle( String title )
    {
        this._title = title;
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
        this._value = value;
    }
    /**
     * @return the group
     */
    public String getGroup( )
    {
        return _group;
    }
    /**
     * @param group the group to set
     */
    public void setGroup( String group )
    {
        this._group = group;
    }
    
    public boolean isDrawable( )
    {
        return StringUtils.isNoneEmpty( _title, _value );
    }
}
