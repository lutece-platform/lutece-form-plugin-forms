package fr.paris.lutece.plugins.forms.validation;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class NumberSuperiorValidator extends AbstractNumberValidator
{
    /**
     * Constructor of the AbstractValidator
     *
     * @param strValidatorName
     *         The validator bean name
     * @param strValidatorDisplayName
     *         The validator display name
     * @param listAvailableEntryType
     *         The list of available entrytype
     */
    protected NumberSuperiorValidator( String strValidatorName, String strValidatorDisplayName, List<String> listAvailableEntryType )
    {
        super( strValidatorName, strValidatorDisplayName, listAvailableEntryType );
    }

    @Override
    public boolean validateControl( Double controlValue, Double value )
    {
        return value < controlValue;
    }

    @Override
    protected String getLabelKey( )
    {
        return "forms.validator.number.sup.label";
    }

    @Override
    protected String getHelpKey( )
    {
        return StringUtils.EMPTY;
    }
}
