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
package fr.paris.lutece.plugins.forms.service.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.workflow.WorkflowService;

/**
 * This class represents a workflow service for the forms
 *
 */
public class FormWorkflowServiceImpl implements IFormWorkflowService
{
    public static final String BEAN_NAME = "forms.formWorkflowService";

    /**
     * {@inheritDoc}
     */
    @Override
    public void doProcessActionOnFormCreation( Form form, FormResponse formResponse )
    {
        int nIdWorkflow = form.getIdWorkflow( );
        int nIdFormResponse = formResponse.getId( );
        WorkflowService workflowService = WorkflowService.getInstance( );

        if ( nIdWorkflow > 0 && workflowService.isAvailable( ) )
        {
            workflowService.getState( nIdFormResponse, FormResponse.RESOURCE_TYPE, nIdWorkflow, form.getId( ) );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeResources( int nIdWorkflow, int nIdForm, AdminUser adminUser )
    {
        WorkflowService workflowService = WorkflowService.getInstance( );

        if ( workflowService.isAvailable( ) )
        {
            List<Integer> listIdWorkflowState = getListIdWorkflowState( nIdWorkflow, adminUser );
            List<Integer> listIdResources = workflowService.getAuthorizedResourceList( FormResponse.RESOURCE_TYPE, nIdWorkflow, listIdWorkflowState, nIdForm,
                    (User) adminUser );

            workflowService.doRemoveWorkFlowResourceByListId( listIdResources, FormResponse.RESOURCE_TYPE, nIdWorkflow );
        }
    }

    /**
     * Retrieves the list of state identifiers for the specified workflow. The list is filtered depending on the permissions of the specified user.
     * 
     * @param nIdWorkflow
     *            the workflow id
     * 
     * @param adminUser
     *            the user
     * 
     * @return the list of workflow state identifiers
     */
    private List<Integer> getListIdWorkflowState( int nIdWorkflow, AdminUser adminUser )
    {
        List<Integer> listIdState = new ArrayList<>( );
        Collection<State> collState = WorkflowService.getInstance( ).getAllStateByWorkflow( nIdWorkflow, (User) adminUser );

        for ( State state : collState )
        {
            listIdState.add( state.getId( ) );
        }

        return listIdState;
    }

}
