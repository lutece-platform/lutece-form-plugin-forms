package fr.paris.lutece.plugins.forms.web;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.Question;

/**
 * Interface for the entry data services
 */
public interface IEntryDataService
{
    /**
     * @return the data service name
     */
    String getDataServiceName( );

    /**
     * 
     * @param questionResponse
     *            the questionResponse to save
     * @return
     */
    void saveFormQuestionResponse( FormQuestionResponse questionResponse );

    /**
     * Get the response values from request for the given question
     * 
     * @param question
     *            the question to
     * @param request
     *            The Http request
     * @param responseInstance
     *            The instance of FormQuestionResponse
     * @return The response to the question
     */
    boolean getResponseFromRequest( Question question, HttpServletRequest request, FormQuestionResponse responseInstance );
}
