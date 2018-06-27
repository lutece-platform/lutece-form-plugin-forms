package fr.paris.lutece.plugins.forms.business;

import java.util.List;

import fr.paris.lutece.plugins.genericattributes.business.Response;

/**
 * This is the business class for the object FormQuestionResponse
 */
public class FormQuestionResponse
{
    private int _nId;
    
    private int _nIdFormResponse;

    private Question _question;

    private int _nIterationNumber;

    private List<Response> _entryResponses;

    /**
     * @return the _nId
     */
    public int getId( )
    {
        return _nId;
    }

    /**
     * @param nId
     *            the nId to set
     */
    public void setId( int nId )
    {
        this._nId = nId;
    }
    
    /**
     * @return the _nIdFormResponse
     */
    public int getIdFormResponse( )
    {
        return _nIdFormResponse;
    }

    /**
     * @param nIdFormResponse
     *            the nIdFormResponse to set
     */
    public void setIdFormResponse( int nIdFormResponse )
    {
        this._nIdFormResponse = nIdFormResponse;
    }

    /**
     * @return the _question
     */
    public Question getQuestion( )
    {
        return _question;
    }

    /**
     * @param question
     *            the question to set
     */
    public void setQuestion( Question question )
    {
        this._question = question;
    }

    /**
     * @return the _nIterationNumber
     */
    public int getIterationNumber( )
    {
        return _nIterationNumber;
    }

    /**
     * @param nIterationNumber
     *            the nIterationNumber to set
     */
    public void setIterationNumber( int nIterationNumber )
    {
        this._nIterationNumber = nIterationNumber;
    }

    /**
     * @return the _entryResponse
     */
    public List<Response> getEntryResponse( )
    {
        return _entryResponses;
    }

    /**
     * @param entryResponse
     *            the entryResponse to set
     */
    public void setEntryResponse( List<Response> entryResponse )
    {
        this._entryResponses = entryResponse;
    }

}
