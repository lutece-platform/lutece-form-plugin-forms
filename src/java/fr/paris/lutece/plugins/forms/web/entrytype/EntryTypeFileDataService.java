package fr.paris.lutece.plugins.forms.web.entrytype;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.ControlType;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.service.EntryServiceManager;
import fr.paris.lutece.plugins.forms.validation.IValidator;
import fr.paris.lutece.plugins.forms.web.http.IterationMultipartHttpServletRequestWrapper;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;

/**
 * Data service for files
 *
 */
public class EntryTypeFileDataService extends EntryTypeDefaultDataService
{

    /**
     * Constructor
     * 
     * @param strEntryServiceName
     *            the service name
     */
    public EntryTypeFileDataService( String strEntryServiceName )
    {
        super( strEntryServiceName );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FormQuestionResponse createResponseFromRequest( Question question, HttpServletRequest request )
    {
        FormQuestionResponse formQuestionResponse = createResponseFor( question );
        request = convertToIterationRequest( question, request );

        GenericAttributeError error = EntryTypeServiceManager.getEntryTypeService( question.getEntry( ) ).getResponseData( question.getEntry( ), request,
                formQuestionResponse.getEntryResponse( ), request.getLocale( ) );
        formQuestionResponse.setError( error );

        Control control = ControlHome.getControlByQuestionAndType( question.getId( ), ControlType.VALIDATION.getLabel( ) );

        if ( control != null )
        {
            IValidator validator = EntryServiceManager.getInstance( ).getValidator( control.getValidatorName( ) );
            if ( !validator.validate( formQuestionResponse, control ) )
            {
                error = new GenericAttributeError( );

                error.setIsDisplayableError( true );
                error.setErrorMessage( control.getErrorMessage( ) );

                formQuestionResponse.setError( error );
            }
        }

        return formQuestionResponse;
    }

    /**
     * Converts the specified request into an iteration request
     * 
     * @param question
     *            the question containing the iteration number
     * @param request
     *            the request
     * @return the converted request
     */
    private HttpServletRequest convertToIterationRequest( Question question, HttpServletRequest request )
    {
        if ( request instanceof MultipartHttpServletRequest )
        {
            return new IterationMultipartHttpServletRequestWrapper( (MultipartHttpServletRequest) request, question.getIterationNumber( ) );
        }

        return request;
    }

}
