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
package fr.paris.lutece.plugins.forms.web.form.filter.display.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.util.FormMultiviewWorkflowStateNameConstants;
import fr.paris.lutece.plugins.forms.util.ReferenceListFactory;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.util.ReferenceList;

/**
 * Implementation of the IFormFilterDisplay interface for the filter on workflow state
 */
public class FormFilterDisplayWorkflowState extends AbstractFormFilterDisplay
{
    // Constants
    private static final String PARAMETER_ID_WORKFLOW_STATE = "multiview_id_state_workflow";
    private static final String PARAMETER_ID_FORM = "multiview_id_form";
    private static final String WORKFLOW_STATE_CODE_ATTRIBUTE = "id";
    private static final String WORKFLOW_STATE_NAME_ATTRIBUTE = "name";
    private static final int DEFAULT_FORM_VALUE = NumberUtils.INTEGER_MINUS_ONE;
    private static final int ID_WORKFLOW_UNSET = NumberUtils.INTEGER_ZERO;

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getFilterDisplayMapValues( HttpServletRequest request )
    {
        Map<String, Object> mapFilterNameValues = new LinkedHashMap<>( );

        String strIdWorkflowState = request.getParameter( PARAMETER_ID_WORKFLOW_STATE );
        setValue( strIdWorkflowState );

        if ( StringUtils.isNotBlank( strIdWorkflowState ) )
        {
            mapFilterNameValues.put( FormMultiviewWorkflowStateNameConstants.FILTER_ID_WORKFLOW_STATE, strIdWorkflowState );
        }

        return mapFilterNameValues;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void buildTemplate( HttpServletRequest request )
    {
        // If no form has been selected we will return an empty list
        ReferenceList referenceList = new ReferenceList( );

        int nIdForm = NumberUtils.toInt( request.getParameter( PARAMETER_ID_FORM ), DEFAULT_FORM_VALUE );
        if ( nIdForm != DEFAULT_FORM_VALUE )
        {
            referenceList = createReferenceList( request, nIdForm );
        }

        manageFilterTemplate( request, referenceList, PARAMETER_ID_WORKFLOW_STATE );
    }

    /**
     * Build the ReferenceList for the workflow state associated to the form
     * 
     * @param request
     *            The request used to retrieve the values from
     * @param nIdForm
     *            The id of the Form to retrieve the list of workflow state from
     * @return the ReferenceList for the workflow state associated to the form
     */
    private ReferenceList createReferenceList( HttpServletRequest request, int nIdForm )
    {
        List<State> listWorkflowState = getFormWorkflowStateList( request, nIdForm );

        ReferenceList referenceList = new ReferenceList( );

        if ( listWorkflowState != null && !listWorkflowState.isEmpty( ) )
        {
            ReferenceListFactory referenceListFactory = new ReferenceListFactory( listWorkflowState, WORKFLOW_STATE_CODE_ATTRIBUTE,
                    WORKFLOW_STATE_NAME_ATTRIBUTE );
            referenceList = referenceListFactory.createReferenceList( );
        }

        return referenceList;
    }

    /**
     * Return the list of workflow state for a form
     * 
     * @param request
     *            The request
     * @param nIdForm
     *            The identifier of the form to retrieve the state from
     * @return the list of workflow state for a form
     */
    private List<State> getFormWorkflowStateList( HttpServletRequest request, int nIdForm )
    {
        List<State> listWorkflowState = new ArrayList<>( );
        
        /** [FIXME]
        Form form = FormHome.findByPrimaryKey( nIdForm );

        if ( form != null && form.getIdWorkflow( ) > ID_WORKFLOW_UNSET )
        {
            listWorkflowState.addAll( WorkflowService.getInstance( )
                    .getAllStateByWorkflow( form.getIdWorkflow( ), AdminUserService.getAdminUser( request ) ) );
        }
         **/
        
        return listWorkflowState;
    }
}
