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
package fr.paris.lutece.plugins.forms.web.form.filter.display.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.service.FormsResourceIdService;
import fr.paris.lutece.plugins.forms.util.FormMultiviewFormsNameConstants;
import fr.paris.lutece.plugins.forms.util.FormMultiviewWorkflowStateNameConstants;
import fr.paris.lutece.plugins.forms.util.ReferenceListFactory;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.util.ReferenceList;

/**
 * Implementation of the IFormFilterDisplay interface for the filter on workflow state
 */
public class FormFilterDisplayWorkflowState extends AbstractFormFilterDisplay
{
    // Constants
    private static final String PARAMETER_ID_WORKFLOW_STATE = "multiview_id_state_workflow";
    private static final String WORKFLOW_STATE_CODE_ATTRIBUTE = "id";
    private static final String WORKFLOW_STATE_NAME_ATTRIBUTE = "name";
    private static final int DEFAULT_FORM_VALUE = NumberUtils.INTEGER_MINUS_ONE;
    private static final int DEFAULT_PREVIOUS_FORM_VALUE = NumberUtils.INTEGER_MINUS_ONE;
    private static final int ID_WORKFLOW_UNSET = NumberUtils.INTEGER_ZERO;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getParameterName( )
    {
        return PARAMETER_ID_WORKFLOW_STATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getFilterDisplayMapValues( HttpServletRequest request )
    {
        Map<String, Object> mapFilterNameValues = new LinkedHashMap<>( );

        int nIdForm = NumberUtils.toInt( request.getParameter( FormMultiviewFormsNameConstants.PARAMETER_ID_FORM ), DEFAULT_FORM_VALUE );
        int nIdPreviousForm = NumberUtils.toInt( request.getParameter( FormMultiviewFormsNameConstants.PARAMETER_PREVIOUS_ID_FORM ),
                DEFAULT_PREVIOUS_FORM_VALUE );
        String strIdWorkflowState = request.getParameter( PARAMETER_ID_WORKFLOW_STATE );

        if ( nIdForm != nIdPreviousForm )
        {
            strIdWorkflowState = StringUtils.EMPTY;
        }

        if ( StringUtils.isNotBlank( strIdWorkflowState ) )
        {
            mapFilterNameValues.put( FormMultiviewWorkflowStateNameConstants.FILTER_ID_WORKFLOW_STATE, strIdWorkflowState );
        }

        setValue( strIdWorkflowState );

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

        int nIdForm = NumberUtils.toInt( request.getParameter( FormMultiviewFormsNameConstants.PARAMETER_ID_FORM ), DEFAULT_FORM_VALUE );
        if ( nIdForm != DEFAULT_FORM_VALUE )
        {
            referenceList = createReferenceList( request, nIdForm );
        }
        else
        {
            List<Form> formList = FormHome.getFormList( );

            formList.removeIf( f -> !RBACService.isAuthorized( Form.RESOURCE_TYPE, String.valueOf( f.getId( ) ),
                    FormsResourceIdService.PERMISSION_VIEW_FORM_RESPONSE, (User) AdminUserService.getAdminUser( request ) ) );

            if ( formList.size( ) == 1 )
            {
                nIdForm = formList.get( 0 ).getId( );
                referenceList = createReferenceList( request, nIdForm );
            }
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

        if ( CollectionUtils.isNotEmpty( listWorkflowState ) )
        {
            ReferenceListFactory referenceListFactory = new ReferenceListFactory( listWorkflowState, WORKFLOW_STATE_CODE_ATTRIBUTE,
                    WORKFLOW_STATE_NAME_ATTRIBUTE );
            referenceListFactory.setDefaultName( getFormFilterDisplayLabel( request.getLocale( ) ) );
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

        Form form = FormHome.findByPrimaryKey( nIdForm );

        if ( form != null && form.getIdWorkflow( ) > ID_WORKFLOW_UNSET )
        {
            listWorkflowState
                    .addAll( WorkflowService.getInstance( ).getAllStateByWorkflow( form.getIdWorkflow( ), (User) AdminUserService.getAdminUser( request ) ) );
        }

        return listWorkflowState;
    }
}
