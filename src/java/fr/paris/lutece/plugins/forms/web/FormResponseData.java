package fr.paris.lutece.plugins.forms.web;

import java.sql.Date;

public class FormResponseData
{
    private int _nIdFormResponse;
    private String _strFormTitle;
    private String _strWorkflowState;
    private Date _dateUpdate;
    
    /**
     * @return the nIdFormResponse
     */
    public int getIdFormResponse( )
    {
        return _nIdFormResponse;
    }
    /**
     * @param nIdFormResponse the nIdFormResponse to set
     */
    public void setIdFormResponse( int nIdFormResponse )
    {
        _nIdFormResponse = nIdFormResponse;
    }
    /**
     * @return the strFormTitle
     */
    public String getFormTitle( )
    {
        return _strFormTitle;
    }
    /**
     * @param strFormTitle the strFormTitle to set
     */
    public void setFormTitle( String strFormTitle )
    {
        _strFormTitle = strFormTitle;
    }
    /**
     * @return the strWorkflowState
     */
    public String getWorkflowState( )
    {
        return _strWorkflowState;
    }
    /**
     * @param strWorkflowState the strWorkflowState to set
     */
    public void setWorkflowState( String strWorkflowState )
    {
        _strWorkflowState = strWorkflowState;
    }
    /**
     * @return the dateUpdate
     */
    public Date getDateUpdate( )
    {
        return _dateUpdate;
    }
    /**
     * @param dateUpdate the dateUpdate to set
     */
    public void setDateUpdate( Date dateUpdate )
    {
        _dateUpdate = dateUpdate;
    }
}
