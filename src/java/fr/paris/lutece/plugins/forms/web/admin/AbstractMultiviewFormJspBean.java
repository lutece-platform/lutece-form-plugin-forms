/*
 * Copyright (c) 2002-2024, City of Paris
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
package fr.paris.lutece.plugins.forms.web.admin;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.util.string.StringUtil;

/**
 * Abstract Jsp Bean for multiviewForm
 */
public abstract class AbstractMultiviewFormJspBean extends AbstractJspBean
{
	// Generated serial UID
    private static final long serialVersionUID = 3673744119874180461L;
    
    // Parameters
    protected static final String PARAMETER_BACK_FROM_ACTION = "back_form_action";
    protected static final String PARAMETER_ID_ACTION = "id_action";
    protected static final String PARAMETER_ACTION_HISTORY_RESOURCE_ID = "action_history_resource_id";
    
    // Marks
    protected static final String MARK_PROCESS_ACTION_CONFIRMATION = "process_action_confirmation";
    
    /**
     * add the action confirmation to the model
     * 
     * @param request
     *            the HttpServletRequest
     * @param model
     *            The given model
     */
    protected void addActionConfirmationToModel( HttpServletRequest request, Map<String, Object> model )
    {
    	int nIdAction = NumberUtils.toInt( request.getParameter( PARAMETER_ID_ACTION ), NumberUtils.INTEGER_MINUS_ONE );
    	String actionHistoryResourceId = request.getParameter( PARAMETER_ACTION_HISTORY_RESOURCE_ID );
    	
    	if ( NumberUtils.INTEGER_MINUS_ONE != nIdAction && StringUtils.isNotBlank( actionHistoryResourceId ) )
    	{
    		List<Integer> actionHistoryResourceIdList = StringUtil.convertStringToIntList( actionHistoryResourceId, FormsConstants.SEPARATOR_UNDERSCORE );
    		
    		model.put( MARK_PROCESS_ACTION_CONFIRMATION, WorkflowService.getInstance( ).getDisplayProcessActionConfirmation( nIdAction, getLocale( ), actionHistoryResourceIdList ) );
    	}
    }
}
