/*
 * Copyright (c) 2002-2018, Mairie de Paris
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
package fr.paris.lutece.plugins.forms.business.form.search;

import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

import fr.paris.lutece.portal.service.search.SearchItem;
import fr.paris.lutece.portal.service.util.AppLogService;

/**
 * This class is use for processing searches in Forms
 */
public class FormResponseSearchItem extends SearchItem
{
    public static final String FIELD_ID_FORM_RESPONSE = "id_form_response";
    public static final String FIELD_DATE_CREATION = "response_creation_date";
    public static final String FIELD_DATE_UPDATE = "date_update";
    public static final String FIELD_GUID = "guid";
    public static final String FIELD_ID_FORM = "id_form";
    public static final String FIELD_FORM_TITLE = "form_title";
    public static final String FIELD_ID_WORKFLOW_STATE = "id_workflow_state";
    public static final String FIELD_TITLE_WORKFLOW_STATE = "title_workflow_state";
    public static final String FIELD_ID_ASSIGNEE_USER = "id_assignee_user";
    public static final String FIELD_ID_ASSIGNEE_UNIT = "id_assignee_unit";
    public static final String FIELD_ENTRY_CODE_SUFFIX = "entry_code_";
    public static final String FIELD_RESPONSE_FIELD_SEPARATOR_ = "_field_";
    public static final String FIELD_RESPONSE_FIELD_ITER_ = "_iter_";
    public static final String FIELD_RESPONSE_ID_ = "_response_id_";
    public static final String FIELD_DATE_SUFFIX = "_date";
    public static final String FIELD_INT_SUFFIX = "_int";

    private static final int INTEGER_MINUS_ONE = -1;

    private int _nIdFormResponse;
    private int _nIdForm;
    private String _strFormTitle;
    private String _strDateCreation;
    private String _strDateUpdate;
    private String _strGuid;
    private int _nIdAssigneeUser;
    private int _nIdAssigneeUnit;
    private int _nIdWorkflowState;
    private String _strWorkflowStateTitle;
    private Map<String, String> _mapEntryCodeFieldsValue;

    /**
     * Constructor based on a Lucene Document
     * 
     * @param document
     *            the Lucene Document
     */
    public FormResponseSearchItem( Document document )
    {
        super( document );

        _nIdFormResponse = manageNullValue( document.get( FIELD_ID_FORM_RESPONSE ) );
        _nIdForm = manageNullValue( document.get( FIELD_ID_FORM ) );
        _strFormTitle = document.get( FIELD_FORM_TITLE );
        _strDateCreation = document.get( FIELD_DATE_CREATION );
        _strDateUpdate = document.get( FIELD_DATE_UPDATE );
        _strGuid = document.get( FIELD_GUID );
        _nIdAssigneeUser = manageNullValue( document.get( FIELD_ID_ASSIGNEE_USER ) );
        _nIdAssigneeUnit = manageNullValue( document.get( FIELD_ID_ASSIGNEE_UNIT ) );
        _nIdWorkflowState = manageNullValue( document.get( FIELD_ID_WORKFLOW_STATE ) );
        _strWorkflowStateTitle = document.get( FIELD_TITLE_WORKFLOW_STATE );
        populateMapEntryCodeFieldsValue( document );
    }

    /**
     * Get the id form response
     * 
     * @return the id form response
     */
    public int getIdFormResponse( )
    {
        return _nIdFormResponse;
    }

    /**
     * Set the id form response
     * 
     * @param nIdFormResponse
     *            the id form response
     */
    public void setIdFormResponse( int nIdFormResponse )
    {
        _nIdFormResponse = nIdFormResponse;
    }

    /**
     * Get the id of the form
     * 
     * @return the id of the form
     */
    public int getIdForm( )
    {
        return _nIdForm;
    }

    /**
     * Set the id of the form
     * 
     * @param nIdForm
     *            the id of the form
     */
    public void setIdForm( int nIdForm )
    {
        _nIdForm = nIdForm;
    }

    /**
     * Get the form title
     * 
     * @return the form title
     */
    public String getFormTitle( )
    {
        return _strFormTitle;
    }

    /**
     * Set the form title
     * 
     * @param strFormTitle
     *            the form title
     */
    public void setFormTitle( String strFormTitle )
    {
        _strFormTitle = strFormTitle;
    }

    /**
     * Get the creation date
     * 
     * @return the creation date of the form
     */
    public String getDateCreation( )
    {
        return _strDateCreation;
    }

    /**
     * Set the creation date
     * 
     * @param strDateCreation
     */
    public void setDateCreation( String strDateCreation )
    {
        _strDateCreation = strDateCreation;
    }

    /**
     * Get the date update
     * 
     * @return the date update
     */
    public String getDateUpdate( )
    {
        return _strDateUpdate;
    }

    /**
     * Set the date update
     * 
     * @param strDateUpdate
     *            the date update
     */
    public void setDateUpdate( String strDateUpdate )
    {
        _strDateUpdate = strDateUpdate;
    }

    /**
     * Get the guid
     * 
     * @return the guid
     */
    public String getGuid( )
    {
        return _strGuid;
    }

    /**
     * Set the guid
     * 
     * @param strGuid
     *            the guid
     */
    public void setGuid( String strGuid )
    {
        _strGuid = strGuid;
    }

    /**
     * Get the id of assignee user
     * 
     * @return the id of assignee user
     */
    public int getIdAssigneeUser( )
    {
        return _nIdAssigneeUser;
    }

    /**
     * Get the id of assignee user
     * 
     * @param nIdAssigneeUser
     *            the id of assignee user
     */
    public void setIdAssigneeUser( int nIdAssigneeUser )
    {
        _nIdAssigneeUser = nIdAssigneeUser;
    }

    /**
     * Get the id of assignee unit
     * 
     * @return the id of assignee unit
     */
    public int getIdAssigneeUnit( )
    {
        return _nIdAssigneeUnit;
    }

    /**
     * Set the id of the assignee unit
     * 
     * @param nIdAssigneeUnit
     *            the id of the assignee unit
     */
    public void setIdAssigneeUnit( int nIdAssigneeUnit )
    {
        _nIdAssigneeUnit = nIdAssigneeUnit;
    }

    /**
     * Get the id of the workflow state
     * 
     * @return the id of the workflow state
     */
    public int getIdWorkflowState( )
    {
        return _nIdWorkflowState;
    }

    /**
     * Set the id of the workflow state
     * 
     * @param nIdWorkflowState
     *            the id of the workflow state
     */
    public void setIdWorkflowState( int nIdWorkflowState )
    {
        _nIdWorkflowState = nIdWorkflowState;
    }

    /**
     * Get the workflow state title
     * 
     * @return the workflow state title
     */
    public String getWorkflowStateTitle( )
    {
        return _strWorkflowStateTitle;
    }

    /**
     * Set the workflow state title
     * 
     * @param strWorkflowStateTitle
     *            the workflow state title
     */
    public void setWorkflowStateTitle( String strWorkflowStateTitle )
    {
        _strWorkflowStateTitle = strWorkflowStateTitle;
    }

    /**
     * Get the map of entry cde fields value
     * 
     * @return the map of entry code fields value
     */
    public Map<String, String> getMapEntryCodeFieldsValue( )
    {
        return _mapEntryCodeFieldsValue;
    }

    /**
     * Set the map of entry code fields value
     * 
     * @param mapEntryCodeFieldsValue
     *            the map of entry code fields value
     */
    public void setMapEntryCodeFieldsValue( Map<String, String> mapEntryCodeFieldsValue )
    {
        _mapEntryCodeFieldsValue = mapEntryCodeFieldsValue;
    }

    /**
     * Populate the map of entry code fields value with the Lucene document
     * 
     * @param document
     *            the Lucene document
     */
    private void populateMapEntryCodeFieldsValue( Document document )
    {
        Map<String, String> mapEntryCodeFieldsValues = new HashMap<>( );
        for ( IndexableField field : document.getFields( ) )
        {
            String strFieldName = field.name( );
            if ( strFieldName.startsWith( FIELD_ENTRY_CODE_SUFFIX ) )
            {
                mapEntryCodeFieldsValues.put( strFieldName, field.stringValue( ) );
            }
        }
        _mapEntryCodeFieldsValue = mapEntryCodeFieldsValues;
    }

    private Integer manageNullValue( String strDocumentValue )
    {
        Integer nReturn = INTEGER_MINUS_ONE;
        if ( strDocumentValue != null )
        {
            try
            {
                nReturn = Integer.parseInt( strDocumentValue );
            }
            catch( NumberFormatException e )
            {
                AppLogService.error( "Unable to convert " + strDocumentValue + " to integer." );
            }
        }
        return nReturn;
    }
}
