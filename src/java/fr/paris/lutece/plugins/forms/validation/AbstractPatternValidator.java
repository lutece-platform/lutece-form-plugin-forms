package fr.paris.lutece.plugins.forms.validation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.regularexpression.business.RegularExpressionHome;
import fr.paris.lutece.portal.business.regularexpression.RegularExpression;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.regularexpression.IRegularExpressionService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

public abstract class AbstractPatternValidator extends AbstractValidator {

	private static final String BEAN_NAME_REGULAR_EXPRESSION = "regularExpressionService";
	private static final String TEMPLATE_JS_FUNCTION = "/skin/plugins/forms/validators/pattern_function.js";
	private static final String TEMPLATE_DISPLAY_HTML = "/admin/plugins/forms/validators/pattern_template.html";
	
	private Plugin _plugin = PluginService.getPlugin( "forms" );
	
	public AbstractPatternValidator( String strValidatorName, String strValidatorDisplayName,
			List<String> listAvailableEntryType )
	{
		super( strValidatorName, strValidatorDisplayName, listAvailableEntryType );
	}

    @Override
    public String getDisplayHtml( Control control )
    {
        Map<String, Object> model = new HashMap<>( );

        List<RegularExpression> listRegularExpression = RegularExpressionHome.getList( _plugin );
        ReferenceList refListRegularExpression = new ReferenceList( );

        for ( RegularExpression regularExpression : listRegularExpression )
        {
            refListRegularExpression.addItem( regularExpression.getIdExpression( ), regularExpression.getTitle( ) );
        }

        model.put( FormsConstants.PARAMETER_REF_LIST_VALUE, refListRegularExpression );
        model.put( FormsConstants.PARAMETER_CONTROL_VALUE, control.getValue( ) );

        HtmlTemplate htmlTemplateQuestion = AppTemplateService.getTemplate( TEMPLATE_DISPLAY_HTML, I18nService.getDefaultLocale( ), model );

        return htmlTemplateQuestion.getHtml( );
    }
    
    @Override
    public String getJavascriptValidation( )
    {
        Map<String, Object> model = new HashMap<>( );
        return AppTemplateService.getTemplate( TEMPLATE_JS_FUNCTION, I18nService.getDefaultLocale( ), model ).getHtml( );
    }

    @Override
    public String getJavascriptControlValue( Control control )
    {
        RegularExpression regularExpression = RegularExpressionHome.findByPrimaryKey( Integer.valueOf( control.getValue( ) ), _plugin );

        return regularExpression.getValue( );
    }
    
    @Override
    public boolean validate( FormQuestionResponse questionResponse, Control control )
    {
        RegularExpression regularExpression = RegularExpressionHome.findByPrimaryKey( Integer.valueOf( control.getValue( ) ), _plugin );

        if ( regularExpression != null )
        {
            IRegularExpressionService service = (IRegularExpressionService) SpringContextService.getBean( BEAN_NAME_REGULAR_EXPRESSION );
            for ( Response response : questionResponse.getEntryResponse( ) )
            {
            	String toValidate = getValueToValidate( response );
            	if ( StringUtils.isNotEmpty( toValidate ) )
            	{
            		return service.isMatches( toValidate, regularExpression );
            	}
            }
        }
        return false;
    }
    
    protected abstract String getValueToValidate( Response response );
}
