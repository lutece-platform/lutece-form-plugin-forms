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

import fr.paris.lutece.portal.service.search.SearchItem;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

/**
 * This class is use for processing searches in Forms
 */
public class FormResponseSearchItem extends SearchItem
{
    public static final String FIELD_ID_FORM_RESPONSE = "id_form_response";
    public static final String FIELD_DATE_CREATION = "date_creation";
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
    
    private int _nIdFormResponse;
    private int _nIdForm;
    private String _strFormTitle;
    private Timestamp _tDateCreation;
    private Timestamp _tDateUpdate;
    private String _strGuid;
    private int _nIdAssigneeUser;
    private int _nIdAssigneeUnit;
    private int _nIdWorkflowState;
    private String _strWorkflowStateTitle;
    private Map<String, String > _mapEntryCodeFieldsValue;
    
    public FormResponseSearchItem( Document document )
    {
        super( document );
        _nIdFormResponse = Integer.parseInt( document.get( FIELD_ID_FORM_RESPONSE ) );
        _nIdForm = Integer.parseInt( document.get( FIELD_ID_FORM ) );
        _strFormTitle = document.get( FIELD_FORM_TITLE );
        _tDateCreation = Timestamp.valueOf( document.get( FIELD_DATE_CREATION ) );
        _tDateUpdate = Timestamp.valueOf( document.get( FIELD_DATE_UPDATE ) );
        _strGuid = document.get( FIELD_GUID );
        _nIdAssigneeUser = Integer.parseInt( document.get( FIELD_ID_ASSIGNEE_USER ) );
        _nIdAssigneeUnit = Integer.parseInt( document.get( FIELD_ID_ASSIGNEE_UNIT ) );
        _nIdWorkflowState = Integer.parseInt( document.get( FIELD_ID_WORKFLOW_STATE ) );
        _strWorkflowStateTitle = document.get( FIELD_TITLE_WORKFLOW_STATE );
        populateMapEntryCodeFieldsValue( document );
    }

    public int getIdFormResponse() {
        return _nIdFormResponse;
    }

    public void setIdFormResponse(int _nIdFormResponse) {
        this._nIdFormResponse = _nIdFormResponse;
    }

    public int getIdForm() {
        return _nIdForm;
    }

    public void setIdForm(int _nIdForm) {
        this._nIdForm = _nIdForm;
    }

    public String getFormTitle() {
        return _strFormTitle;
    }

    public void setFormTitle(String _strFormTitle) {
        this._strFormTitle = _strFormTitle;
    }

    public Timestamp getDateCreation() {
        return _tDateCreation;
    }

    public void setDateCreation( Timestamp _tDateCreation) {
        this._tDateCreation = _tDateCreation;
    }

    public Timestamp getDateUpdate() {
        return _tDateUpdate;
    }

    public void setDateUpdate(Timestamp _tDateUpdate) {
        this._tDateUpdate = _tDateUpdate;
    }

    public String getGuid() {
        return _strGuid;
    }

    public void setGuid(String _strGuid) {
        this._strGuid = _strGuid;
    }

    public int getIdAssigneeUser() {
        return _nIdAssigneeUser;
    }

    public void setIdAssigneeUser(int _nIdAssigneeUser) {
        this._nIdAssigneeUser = _nIdAssigneeUser;
    }

    public int getIdAssigneeUnit() {
        return _nIdAssigneeUnit;
    }

    public void setIdAssigneeUnit(int _nIdAssigneeUnit) {
        this._nIdAssigneeUnit = _nIdAssigneeUnit;
    }

    public int getIdWorkflowState() {
        return _nIdWorkflowState;
    }

    public void setIdWorkflowState(int _nIdWorkflowState) {
        this._nIdWorkflowState = _nIdWorkflowState;
    }

    public String getWorkflowStateTitle() {
        return _strWorkflowStateTitle;
    }

    public void setWorkflowStateTitle(String _strWorkflowStateTitle) {
        this._strWorkflowStateTitle = _strWorkflowStateTitle;
    }

    public Map<String, String> getMapEntryCodeFieldsValue() {
        return _mapEntryCodeFieldsValue;
    }

    public void setMapEntryCodeFieldsValue(Map<String, String> _mapEntryCodeFieldsValue) {
        this._mapEntryCodeFieldsValue = _mapEntryCodeFieldsValue;
    }
    
    private void populateMapEntryCodeFieldsValue( Document document )
    {
        Map<String, String> mapEntryCodeFieldsValues = new HashMap<>(); 
        for ( IndexableField field : document.getFields( ) )
        {
            String strFieldName = field.name();
            if ( strFieldName.startsWith( FIELD_ENTRY_CODE_SUFFIX ) )
            {
                mapEntryCodeFieldsValues.put( strFieldName, field.stringValue( ) );
            }
        }
        _mapEntryCodeFieldsValue = mapEntryCodeFieldsValues;
    }    
}
