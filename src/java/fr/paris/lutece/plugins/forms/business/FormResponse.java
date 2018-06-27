package fr.paris.lutece.plugins.forms.business;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the business class for the object FormResponse
 */
public class FormResponse
{
    private int _nId;

    private int _nFormId;

    private String _strGuid;

    private Timestamp _dateCreation;

    private Timestamp _dateUpdate;

    private List<FormQuestionResponse> _listResponses = new ArrayList<FormQuestionResponse>();

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
     * @return the _nFormId
     */
    public int getFormId( )
    {
        return _nFormId;
    }

    /**
     * @param nFormId
     *            the nFormId to set
     */
    public void setFormId( int nFormId )
    {
        this._nFormId = nFormId;
    }

    /**
     * @return the _strGuid
     */
    public String getGuid( )
    {
        return _strGuid;
    }

    /**
     * @param strGuid
     *            the strGuid to set
     */
    public void setGuid( String strGuid )
    {
        this._strGuid = strGuid;
    }

    /**
     * @return the _dateCreation
     */
    public Timestamp getCreation( )
    {
        return _dateCreation;
    }

    /**
     * @param dateCreation
     *            the dateCreation to set
     */
    public void setDateCreation( Timestamp dateCreation )
    {
        this._dateCreation = dateCreation;
    }

    /**
     * @return the _dateUpdate
     */
    public Timestamp getUpdate( )
    {
        return _dateUpdate;
    }

    /**
     * @param dateUpdate
     *            the dateUpdate to set
     */
    public void setUpdate( Timestamp dateUpdate )
    {
        this._dateUpdate = dateUpdate;
    }

    /**
     * @return the _listResponses
     */
    public List<FormQuestionResponse> getListResponses( )
    {
        return _listResponses;
    }

    /**
     * @param listResponses
     *            the listResponses to set
     */
    public void setListResponses( List<FormQuestionResponse> listResponses )
    {
        this._listResponses = listResponses;
    }

}
