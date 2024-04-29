package fr.paris.lutece.plugins.forms.validation;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractDayDistanceValidator extends AbstractValidator
{

    // TODO
    private static final String TEMPLATE_DISPLAY_HTML = "/admin/plugins/forms/validators/day_distance_value_template.html";

    private static final String MARK_LABEL = "label_key";

    private static final String MARK_HELP = "help_key";

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
    public AbstractDayDistanceValidator( String strValidatorName, String strValidatorDisplayName, List<String> listAvailableEntryType )
    {
        super( strValidatorName, strValidatorDisplayName, listAvailableEntryType );
    }

    @Override
    public String getDisplayHtml( Control control )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( FormsConstants.PARAMETER_CONTROL_VALUE, control.getValue( ) );
        model.put( MARK_LABEL, getLabelKey( ) );
        model.put( MARK_HELP, getHelpKey( ) );

        HtmlTemplate htmlTemplateQuestion = AppTemplateService.getTemplate( TEMPLATE_DISPLAY_HTML, I18nService.getDefaultLocale( ), model );

        return htmlTemplateQuestion.getHtml( );
    }

    @Override
    public boolean validate( FormQuestionResponse questionResponse, Control control )
    {
        if ( questionResponse.getEntryResponse().isEmpty() ){
            return false;
        }
        Response response = questionResponse.getEntryResponse( ).get( 0 );
        if ( ! response.getResponseValue().isEmpty())
        {
            try
            {
                return validateControl(Integer.parseInt( control.getValue( ) ),
                        Integer.parseInt( response.getResponseValue( ) ) );
            }
            catch( NumberFormatException e )
            {
                AppLogService.error( "Error number format", e );
            }
        }
        return false;
    }

    public abstract boolean validateControl( Integer controlValue, Integer value );

    protected abstract String getLabelKey( );
    protected abstract String getHelpKey( );
}
