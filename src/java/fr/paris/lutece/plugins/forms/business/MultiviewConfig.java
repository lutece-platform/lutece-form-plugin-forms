/*
 * Copyright (c) 2002-2022, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.forms.business;

import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.portal.service.datastore.DatastoreService;

public class MultiviewConfig
{
    private static MultiviewConfig _instance;

    boolean _bDisplayFormsTitleColumn;
    boolean _bDisplayFormsAssigneeColumn;
    String _strCsvSeparator;
    int _intNumberOfFormResponsesPerPdf;
    int _nIdFileTemplatePdf;

	/**
     * Private constructor for singleton pattern
     */
    private MultiviewConfig( )
    {
        String strDisplayFormColumnTitle = DatastoreService.getDataValue( FormsConstants.DS_KEY_FORM_TITLE_COLUMN, "false" );
        _bDisplayFormsTitleColumn = Boolean.parseBoolean( strDisplayFormColumnTitle );

        String strDisplayFormColumnAssignee = DatastoreService.getDataValue( FormsConstants.DS_KEY_FORM_ASSIGNEE_COLUMN, "false" );
        _bDisplayFormsAssigneeColumn = Boolean.parseBoolean( strDisplayFormColumnAssignee );

        _strCsvSeparator = DatastoreService.getDataValue( FormsConstants.DS_KEY_FORM_CSV_SEPARATOR, ";" );
        
        String strNumberOfFormResponsesPerPdf = DatastoreService.getDataValue( FormsConstants.DS_KEY_FORM_PDF_NUMBER_OF_RESPONSES_PER_FILE, "1" );
        _intNumberOfFormResponsesPerPdf = Integer.parseInt(strNumberOfFormResponsesPerPdf);
        
        String strIdFileTemplatePdf = DatastoreService.getDataValue( FormsConstants.DS_KEY_FORM_ID_FILE_TEMPLATE_PDF, null );
        _nIdFileTemplatePdf = strIdFileTemplatePdf != null ? Integer.parseInt(strIdFileTemplatePdf) : FormsConstants.DEFAULT_ID_VALUE;
    }

	/**
     * Return the singleton instance of the multiview config
     * 
     * @return the Multiview config
     */
    public static MultiviewConfig getInstance( )
    {
        if ( _instance == null )
        {
            _instance = new MultiviewConfig( );
        }
        return _instance;
    }

    /**
     * Is display forms title column
     * 
     * @return the display form title column boolean
     */
    public boolean isDisplayFormsTitleColumn( )
    {
        return _bDisplayFormsTitleColumn;
    }

    /**
     * Set the display forms title column boolean
     * 
     * @param bDisplayFormsTitleColumn
     *            The display forms title column boolean
     */
    public void setDisplayFormsTitleColumn( boolean bDisplayFormsTitleColumn )
    {
        _bDisplayFormsTitleColumn = bDisplayFormsTitleColumn;
    }

    /**
     * @return the bDisplayFormsAssigneeColumn
     */
    public boolean isDisplayFormsAssigneeColumn( )
    {
        return _bDisplayFormsAssigneeColumn;
    }

    /**
     * @param bDisplayFormsAssigneeColumn
     *            the bDisplayFormsAssigneeColumn to set
     */
    public void setDisplayFormsAssigneeColumn( boolean bDisplayFormsAssigneeColumn )
    {
        _bDisplayFormsAssigneeColumn = bDisplayFormsAssigneeColumn;
    }

    /**
     * @return the strCsvSeparator
     */
    public String getCsvSeparator( )
    {
        return _strCsvSeparator;
    }

    /**
     * @param strCsvSeparator
     *            the strCsvSeparator to set
     */
    public void setCsvSeparator( String strCsvSeparator )
    {
        _strCsvSeparator = strCsvSeparator;
    }
    
    public int getNumberOfFormResponsesPerPdf()
    {
		return _intNumberOfFormResponsesPerPdf;
	}

	public void setNumberOfFormResponsesPerPdf(int intNumberOfFormResponsesPerPdf)
	{
		_intNumberOfFormResponsesPerPdf = intNumberOfFormResponsesPerPdf;
	}

    public int getIdFileTemplatePdf() {
		return _nIdFileTemplatePdf;
	}

	public void setIdFileTemplatePdf(int nIdFileTemplatePdf) {
		this._nIdFileTemplatePdf = nIdFileTemplatePdf;
	}

	/**
     * Save the config
     */
    public void save( )
    {
        String strDisplayFormsTitleColumn = Boolean.toString( _bDisplayFormsTitleColumn );
        DatastoreService.setDataValue( FormsConstants.DS_KEY_FORM_TITLE_COLUMN, strDisplayFormsTitleColumn );
        String strDisplayFormsAssigneeColumn = Boolean.toString( _bDisplayFormsAssigneeColumn );
        DatastoreService.setDataValue( FormsConstants.DS_KEY_FORM_ASSIGNEE_COLUMN, strDisplayFormsAssigneeColumn );
        DatastoreService.setDataValue( FormsConstants.DS_KEY_FORM_CSV_SEPARATOR, _strCsvSeparator );
        
        String strNumberOfFormResponsesPerPdf = Integer.toString(_intNumberOfFormResponsesPerPdf);
        DatastoreService.setDataValue( FormsConstants.DS_KEY_FORM_PDF_NUMBER_OF_RESPONSES_PER_FILE, strNumberOfFormResponsesPerPdf );
        
        String strIdFileTemplatePdf = Integer.toString(_nIdFileTemplatePdf);
        DatastoreService.setDataValue( FormsConstants.DS_KEY_FORM_ID_FILE_TEMPLATE_PDF, strIdFileTemplatePdf );
    }
}
