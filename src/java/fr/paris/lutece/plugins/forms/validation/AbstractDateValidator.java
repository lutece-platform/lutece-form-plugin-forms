package fr.paris.lutece.plugins.forms.validation;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.html.HtmlTemplate;

public abstract class AbstractDateValidator extends AbstractValidator
{
    private static final String MARK_LABEL = "date_label_key";
    private static final String MARK_HELP = "date_help_key";
    private static final String MARK_OPERATION = "operation";
    
    private static final String TEMPLATE_DISPLAY_HTML = "/admin/plugins/forms/validators/date_value_template.html";
    private static final String TEMPLATE_JAVASCRIPT = "/skin/plugins/forms/validators/date_value_javascript.html";
    
    public AbstractDateValidator( String strValidatorName, String strValidatorDisplayName, List<String> listAvailableEntryType )
    {
        super( strValidatorName, strValidatorDisplayName, listAvailableEntryType );
    }

    @Override
    public boolean validate( FormQuestionResponse questionResponse, Control control )
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        try
        {
            Date dateControl = new Date( dateFormat.parse( control.getValue( ) ).getTime( ) );
            for ( Response response : questionResponse.getEntryResponse( ) )
            {
                Date date = new Date( Integer.parseInt( response.getResponseValue( ) ) );
                if ( !validateDate( dateControl, date ) )
                {
                    return false;
                }
            }
            return true;
        }
        catch( ParseException e )
        {
            AppLogService.error( "Error formatingdate", e );
            return false;
        }
    }
    
    @Override
    public String getJavascriptValidation( )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_OPERATION, getOperation( ) );
        HtmlTemplate htmlTemplate = AppTemplateService.getTemplate( TEMPLATE_JAVASCRIPT, I18nService.getDefaultLocale( ), model );

        return htmlTemplate.getHtml( );
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
    public String getJavascriptControlValue( Control control )
    {
        return control.getValue( );
    }
    
    protected abstract String getOperation( );
    
    protected abstract boolean validateDate( Date controlDate, Date date);
    
    protected abstract String getLabelKey( );
    
    protected abstract String getHelpKey( );
}
