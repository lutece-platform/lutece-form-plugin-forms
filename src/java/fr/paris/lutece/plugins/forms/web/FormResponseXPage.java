package fr.paris.lutece.plugins.forms.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.exception.FormResponseNotFoundException;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;

/**
 * 
 * Controller for formResponse display
 *
 */
@Controller( xpageName = FormResponseXPage.XPAGE_NAME, pageTitleI18nKey = FormResponseXPage.MESSAGE_PAGE_TITLE, pagePathI18nKey = FormResponseXPage.MESSAGE_PATH )
public class FormResponseXPage extends MVCApplication
{
	protected static final String XPAGE_NAME = "formsResponse";

	/**
	 * Generated serial id
	 */
	private static final long serialVersionUID = 8146530527615651620L;
	
	// Messages
    protected static final String MESSAGE_PAGE_TITLE = "forms.response.xpage.form.view.pageTitle";
    protected static final String MESSAGE_PATH = "forms.response.xpage.form.view.pagePathLabel";
    protected static final String MESSAGE_ERROR_NOT_PUBLISHED_FORM_RESPONSE = "forms.xpage.response.error.inactive";
	
    // Views
    private static final String VIEW_FORM_RESPONSE = "formResponseView";
    
	// Templates
    private static final String TEMPLATE_VIEW_FORM_RESPONSE = "/skin/plugins/forms/view_form_response.html";

    @View( value = VIEW_FORM_RESPONSE )
    public XPage getFormResponseView( HttpServletRequest request ) throws FormResponseNotFoundException, SiteMessageException
    {
    	FormResponse formResponse = findFormResponseFrom(request);
    	
    	Map<String, Object> model = getModel( );
    	
    	if ( formResponse.isPublished( )  )
        {
            getFormResponseModel( formResponse, model );
        }
        else
        {
        	SiteMessageService.setMessage( request, MESSAGE_ERROR_NOT_PUBLISHED_FORM_RESPONSE, SiteMessage.TYPE_ERROR );
        }
    	
    	XPage xPage = getXPage( TEMPLATE_VIEW_FORM_RESPONSE, getLocale( request ), model );
        xPage.setTitle( "FORM RESPONSE #" + formResponse.getId() );
        xPage.setPathLabel( "FORM RESPONSE" );

        return xPage;
    }
    
    /**
     * @param formResponse
     *            The formResponse to display
     * @param request
     *            The Http request
     * @param model
     *            The model for XPage
     */
    private void getFormResponseModel( FormResponse formResponse, Map<String, Object> model )
    {
    	model.put( FormsConstants.MARK_FORM_RESPONSE, formResponse );
    	List<FormQuestionResponse> formQuestionResponseList =  FormQuestionResponseHome.getFormQuestionResponseListByFormResponse(formResponse.getId());
    	model.put("formQuestionResponseList", formQuestionResponseList);
    }
    
    /**
     * Finds the formResponse from the specified request
     * 
     * @param request
     *            the request
     * @return the found formResponse, or {@code null} if not found
     * @throws FormResponseNotFoundException
     *             if the form is not found
     * @throws SiteMessageException
     *             if the formResponse is not accessible
     */
    private FormResponse findFormResponseFrom( HttpServletRequest request ) throws FormResponseNotFoundException, SiteMessageException
    {
        FormResponse formResponse = null;
        int nIdFormResponse = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_RESPONSE ), FormsConstants.DEFAULT_ID_VALUE );
        
        if ( nIdFormResponse != FormsConstants.DEFAULT_ID_VALUE )
        {
            formResponse = FormResponseHome.findByPrimaryKey( nIdFormResponse );
            
            if ( formResponse == null )
            {
                throw new FormResponseNotFoundException( );
            }
        }
        else
        {
            throw new FormResponseNotFoundException( );
        }
        
        if ( !formResponse.isPublished() )
        {
        	SiteMessageService.setMessage( request, MESSAGE_ERROR_NOT_PUBLISHED_FORM_RESPONSE, SiteMessage.TYPE_ERROR );
        }
        return formResponse;
    }
}
