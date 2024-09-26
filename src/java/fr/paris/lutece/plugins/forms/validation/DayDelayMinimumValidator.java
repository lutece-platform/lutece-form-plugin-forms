package fr.paris.lutece.plugins.forms.validation;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.List;

public class DayDelayMinimumValidator extends AbstractDayDistanceValidator
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
    public DayDelayMinimumValidator( String strValidatorName, String strValidatorDisplayName, List<String> listAvailableEntryType )
    {
        super( strValidatorName, strValidatorDisplayName, listAvailableEntryType );
    }

    @Override
    public boolean validateControl( Integer controlValue, Integer value )
    {
        LocalDate today = LocalDate.now();
        LocalDate nDayDateBefore = today.minusDays( value );
        return ( nDayDateBefore.isBefore( today ) );
    }

    @Override
    protected String getLabelKey( )
    {
        return "forms.validator.delay.min.label";
    }

    @Override
    protected String getHelpKey( )
    {
        return StringUtils.EMPTY;
    }
}
