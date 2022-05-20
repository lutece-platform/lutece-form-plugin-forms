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
package fr.paris.lutece.plugins.forms.web.admin;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.service.FormService;
import fr.paris.lutece.plugins.forms.service.FormsResourceIdService;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;

@Controller( controllerJsp = "ManageFormWorkflowConfig.jsp", controllerPath = "jsp/admin/plugins/forms/", right = "FORMS_MANAGEMENT" )
public class FormWorkflowConfigJspBean extends AbstractJspBean
{
    private static final long serialVersionUID = -2505310758526626253L;

    // Templates
    private static final String TEMPLATE_MANAGE_MULTIVIEW_CONFIG = "/admin/plugins/forms/manage_workflow_config.html";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MODIFY_FORM = "forms.modify_form.pageTitle";

    // Views
    private static final String VIEW_MANAGE_WORKFLOW = "manageWorkflow";
    private static final String VIEW_MANAGE_FORMS = "ManageForms.jsp?view=manageExport";

    // Marks
    private static final String MARK_FORM = "form";
    private static final String MARK_QUESTIONLIST = "questionList";
    
    // Actions
    private static final String ACTION_MANAGE_WORKFLOW = "manageWorkflowConfig";
    private static final String ACTION_MODIFY_WORKFLOW = "modifyWorkflowConfig";

    private static FormService _formService = SpringContextService.getBean( FormService.BEAN_NAME );
    
    @View( VIEW_MANAGE_WORKFLOW )
    public String getManageMultiview( HttpServletRequest request ) throws AccessDeniedException
    {
        int nId = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ), FormsConstants.DEFAULT_ID_VALUE );

        if ( nId == FormsConstants.DEFAULT_ID_VALUE )
        {
            return redirect( request, VIEW_MANAGE_FORMS );
        }

        checkUserPermission( Form.RESOURCE_TYPE, String.valueOf( nId ), FormsResourceIdService.PERMISSION_MODIFY_PARAMS, request, null );

        Form formToBeModified = FormHome.findByPrimaryKey( nId );

        if ( formToBeModified == null )
        {
            return redirect( request, VIEW_MANAGE_FORMS );
        }
        
        List<Question> questionList = QuestionHome.getListQuestionByIdForm( formToBeModified.getId( ) );
        
        Map<String, Object> model = getModel( );
        model.put( MARK_FORM, formToBeModified );
        model.put( MARK_QUESTIONLIST, questionList );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MANAGE_WORKFLOW ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_FORM, TEMPLATE_MANAGE_MULTIVIEW_CONFIG, model );
    }
    
    @Action( ACTION_MODIFY_WORKFLOW )
    public String modifyWorkflowConfig( HttpServletRequest request ) throws AccessDeniedException
    {
        int nId = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ), FormsConstants.DEFAULT_ID_VALUE );
        
        if ( nId == FormsConstants.DEFAULT_ID_VALUE )
        {
            return redirect( request, VIEW_MANAGE_FORMS );
        }
        checkUserPermission( Form.RESOURCE_TYPE, String.valueOf( nId ), FormsResourceIdService.PERMISSION_MODIFY, request, ACTION_MANAGE_WORKFLOW );

        Form formToBeModified = FormHome.findByPrimaryKey( nId );

        if ( formToBeModified == null )
        {
            return redirect( request, VIEW_MANAGE_FORMS );
        }
        
        List<Question> questionList = QuestionHome.getListQuestionByIdForm( formToBeModified.getId( ) );
        for ( Question question : questionList )
        {
            boolean usedInCorrectFormResponse = request.getParameter( FormsConstants.PARAMETER_USED_CORRECT_RESPONSE + "_" + question.getId( ) ) != null;
            boolean usedInCompleteFormResponse = request.getParameter( FormsConstants.PARAMETER_USED_COMPLETE_RESPONSE + "_" + question.getId( ) ) != null;
            
            Entry entry = question.getEntry( );
            _formService.saveOrUpdateField( entry, FormsConstants.PARAMETER_USED_CORRECT_RESPONSE, null, String.valueOf( usedInCorrectFormResponse ) );
            _formService.saveOrUpdateField( entry, FormsConstants.PARAMETER_USED_COMPLETE_RESPONSE, null, String.valueOf( usedInCompleteFormResponse ) );
        }
        
        Map<String, String> mapParameters = new LinkedHashMap<>( );
        mapParameters.put( FormsConstants.PARAMETER_ID_FORM, String.valueOf( nId ) );

        return redirect( request, VIEW_MANAGE_WORKFLOW, mapParameters );
    }
}
