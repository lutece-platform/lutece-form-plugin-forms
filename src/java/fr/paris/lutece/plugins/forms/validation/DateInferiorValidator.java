package fr.paris.lutece.plugins.forms.validation;

import java.sql.Date;
import java.util.List;

public class DateInferiorValidator extends AbstractDateValidator
{

    public DateInferiorValidator( String strValidatorName, String strValidatorDisplayName, List<String> listAvailableEntryType )
    {
        super( strValidatorName, strValidatorDisplayName, listAvailableEntryType );
    }

    @Override
    protected String getHelpKey( )
    {
        return "forms.validator.date.inf.help";
    }
    
    @Override
    protected String getLabelKey( )
    {
        return "forms.validator.date.inf.label";
    }
    
    @Override
    protected boolean validateDate( Date controlDate, Date date )
    {
        return date.before( controlDate );
    }
    
    @Override
    protected String getOperation( )
    {
        return "<";
    }
}
