package fr.paris.lutece.plugins.forms.validation;

import java.sql.Date;
import java.util.List;

public class DateSuperiorValidator extends AbstractDateValidator
{

    public DateSuperiorValidator( String strValidatorName, String strValidatorDisplayName, List<String> listAvailableEntryType )
    {
        super( strValidatorName, strValidatorDisplayName, listAvailableEntryType );
    }

    @Override
    protected String getHelpKey( )
    {
        return "forms.validator.date.sup.help";
    }
    
    @Override
    protected String getLabelKey( )
    {
        return "forms.validator.date.sup.label";
    }
    
    @Override
    protected boolean validateDate( Date controlDate, Date date )
    {
        return date.after( controlDate );
    }
    
    @Override
    protected String getOperation( )
    {
        return ">";
    }
}
