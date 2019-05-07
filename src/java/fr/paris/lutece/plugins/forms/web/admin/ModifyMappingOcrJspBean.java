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
package fr.paris.lutece.plugins.forms.web.admin;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.service.FormsResourceIdService;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.genericattributes.business.ITypeDocumentOcrProvider;
import fr.paris.lutece.plugins.genericattributes.business.Mapping;
import fr.paris.lutece.plugins.genericattributes.business.MappingHome;
import fr.paris.lutece.plugins.genericattributes.business.TypeDocumentProviderManager;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class provides the user interface to manage Form features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ModifyMappingOcr.jsp", controllerPath = "jsp/admin/plugins/forms/", right = "FORMS_MANAGEMENT" )
public class ModifyMappingOcrJspBean extends AbstractJspBean
{
	private static final long serialVersionUID = 8210813454305009601L;

	/**
     * Right to manage forms
     */
    public static final String RIGHT_MANAGE_FORMS = "FORMS_MANAGEMENT";

    // message
    private static final String MESSAGE_CONFIRM_REMOVE_MAPPING= "forms.message.confirmRemoveMapping";

    // Views
    private static final String VIEW_CONFIRM_REMOVE_MAPPING = "confirmRemoveMapping";
    private static final String VIEW_MODIFY_QUESTION = "modifyQuestion";

    // Actions
    private static final String ACTION_CREATE_MAPPING = "createMapping";
    private static final String ACTION_REMOVE_MAPPING = "removeMapping";

    // other constants
    private static final String EMPTY_STRING = "";
    private Step _step;
    private Question _question;
    
    // Jsp Definition
    private static final String JSP_MODIFY_QUESTION = "jsp/admin/plugins/forms/ManageQuestions.jsp";
    /**
     * Do create mapping.
     *
     * @param request the request
     * @return the string
     */
    @Action( ACTION_CREATE_MAPPING )
    public String doCreateMapping( HttpServletRequest request )
    {
    	if ( !updateStepAndQuestion( request ) )
        {
            return redirectToViewManageForm( request );
        }
    	
        String strIdQuestionMapping = request.getParameter( FormsConstants.PARAMETER_ID_QUESTION_MAPPING );
        String strIdFieldOcr = request.getParameter( FormsConstants.PARAMETER_ID_FIELD_OCR );
        String strIdStep = request.getParameter( FormsConstants.PARAMETER_ID_STEP );
        String strTypeDocumentKey= request.getParameter( FormsConstants.PARAMETER_TYPE_DOCUMENT_KEY );
        int nIdQuestionMapping = Integer.parseInt( strIdQuestionMapping );
        int nIdFieldOcr = Integer.parseInt( strIdFieldOcr );
        int nIdStep = Integer.parseInt( strIdStep );
        
        Mapping mapping = new Mapping();
        mapping.setIdFieldOcr(nIdFieldOcr);
        mapping.setIdQuestion(nIdQuestionMapping);
        mapping.setIdStep(nIdStep);
        
        //Set ocr field name
    	ITypeDocumentOcrProvider  typeDocument = TypeDocumentProviderManager.getTypeDocumentProvider(strTypeDocumentKey);
    	if(typeDocument != null && typeDocument.getFieldById(mapping.getIdFieldOcr()) != null) {
    		mapping.setFieldOcrTitle(typeDocument.getFieldById(mapping.getIdFieldOcr()).getName());
    	}
    	
    	//Set question field name
    	Question question = QuestionHome.findByPrimaryKey(nIdQuestionMapping);
    	if(question != null) {
    		mapping.setQuestionTitle(question.getTitle());
    	}
    	
        MappingHome.create( mapping );

        return redirectToViewModifyQuestion( request, _step.getId( ), _question.getId( ) );
    }

    
    /**
     * Gets the confirm remove mapping.
     *
     * @param request the request
     * @return the confirm remove mapping
     */
    @View( value = VIEW_CONFIRM_REMOVE_MAPPING )
    public String getConfirmRemoveMapping( HttpServletRequest request )
    {
        if ( !updateStepAndQuestion( request ) )
        {
            return redirectToViewManageForm( request );
        }

        if ( ( request.getParameter( FormsConstants.PARAMETER_ID_MAPPING ) == null )
                || !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _step.getIdForm( ), FormsResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return redirectToViewManageForm( request );
        }

        String idMapping = request.getParameter( FormsConstants.PARAMETER_ID_MAPPING );

        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_MAPPING ) );
        url.addParameter( FormsConstants.PARAMETER_ID_MAPPING, idMapping );
        url.addParameter( FormsConstants.PARAMETER_ID_QUESTION, _question.getId( ) );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_MAPPING, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Perform suppression field
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_REMOVE_MAPPING )
    public String doRemoveMapping( HttpServletRequest request )
    {
        String strIdMapping = request.getParameter( FormsConstants.PARAMETER_ID_MAPPING );
        int nIdMapping = -1;

        if ( !updateStepAndQuestion( request ) )
        {
            return redirectToViewManageForm( request );
        }

        if ( ( strIdMapping == null )
                || !RBACService.isAuthorized( Form.RESOURCE_TYPE, EMPTY_STRING + _step.getIdForm( ), FormsResourceIdService.PERMISSION_MODIFY, getUser( ) ) )
        {
            return redirectToViewManageForm( request );
        }

        try
        {
        	nIdMapping = Integer.parseInt( strIdMapping );
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( ne );

            return redirectToViewManageForm( request );
        }

        if ( nIdMapping != -1 )
        {
        	MappingHome.remove(nIdMapping);
        }

        return redirectToViewModifyQuestion( request, _step.getId( ), _question.getId( ) );
    }
    
    /**
     * Return the URL of the JSP manage question
     * 
     * @param request
     *            The HTTP request
     * @param nIdStep
     *            the parent step identifier
     * @param nIdQuestion
     *            the parent question identifier
     * @return return URL of the JSP modify entry
     */
    private String redirectToViewModifyQuestion( HttpServletRequest request, int nIdStep, int nIdQuestion )
    {
        UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MODIFY_QUESTION );
        url.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_MODIFY_QUESTION );

        url.addParameter( FormsConstants.PARAMETER_ID_STEP, nIdStep );
        url.addParameter( FormsConstants.PARAMETER_ID_QUESTION, nIdQuestion );

        return redirect( request, url.getUrl( ) );
    }
    
    /**
     * Update Step and Question session variables using request parameter question id
     * 
     * @param request
     *            the request
     * 
     * @return false if any error occured
     */
    private boolean updateStepAndQuestion( HttpServletRequest request )
    {
        boolean bSuccess = true;
        int nIdStep = -1;
        int nIdQuestion = -1;

        String strIdQuestion = request.getParameter( FormsConstants.PARAMETER_ID_QUESTION );
        nIdQuestion = Integer.parseInt( strIdQuestion );

        if ( StringUtils.isBlank( strIdQuestion ) || ( nIdQuestion == -1 ) )
        {
            bSuccess = false;
        }

        if ( ( _question == null ) || ( _question.getId( ) != nIdQuestion ) )
        {
            _question = QuestionHome.findByPrimaryKey( nIdQuestion );
        }

        nIdStep = _question.getIdStep( );
        if ( nIdStep == -1 )
        {
            bSuccess = false;
        }

        if ( ( _step == null ) || ( _step.getId( ) != nIdStep ) )
        {
            _step = StepHome.findByPrimaryKey( nIdStep );
        }

        return bSuccess;
    }

}
