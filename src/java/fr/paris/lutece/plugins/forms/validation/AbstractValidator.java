package fr.paris.lutece.plugins.forms.validation;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.portal.service.i18n.I18nService;

public abstract class AbstractValidator implements IValidator
{

    protected final String _strValidatorName;
    protected final String _strDisplayName;
    protected List<String> _listAvailableEntryType;

    /**
     * Constructor of the AbstractValidator
     * 
     * @param strValidatorName
     *            The validator bean name
     * @param strValidatorDisplayName
     *            The validator display name
     * @param listAvailableEntryType
     *            The list of available entrytype
     */
    public AbstractValidator( String strValidatorName, String strValidatorDisplayName, List<String> listAvailableEntryType )
    {
        _strValidatorName = strValidatorName;
        _strDisplayName = I18nService.getLocalizedString( strValidatorDisplayName, I18nService.getDefaultLocale( ) );
        _listAvailableEntryType = listAvailableEntryType;
    }

    @Override
    public abstract boolean validate( FormQuestionResponse formQuestionResponse, Control control );

    @Override
    public boolean validate( List<FormQuestionResponse> formQuestionResponse, Control control )
    {

        for ( FormQuestionResponse questionResponse : formQuestionResponse )
        {

            if ( !validate( questionResponse, control ) )
            {

                return false;
            }

        }

        return true;
    }

    @Override
    public String getValidatorBeanName( )
    {

        return _strValidatorName;
    }

    @Override
    public String getValidatorDisplayName( )
    {

        return _strDisplayName;
    }

    @Override
    public String getDisplayHtml( Control control )
    {

        return StringUtils.EMPTY;
    }

    @Override
    public List<String> getListAvailableEntryType( )
    {

        return _listAvailableEntryType;
    }

    @Override
    public String getJavascriptValidation( )
    {

        return StringUtils.EMPTY;
    }

    @Override
    public String getJavascriptControlValue( Control control )
    {

        return StringUtils.EMPTY;
    }

}
