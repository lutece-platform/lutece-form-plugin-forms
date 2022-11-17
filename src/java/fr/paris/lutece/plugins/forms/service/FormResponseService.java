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
package fr.paris.lutece.plugins.forms.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponseStepHome;
import fr.paris.lutece.plugins.forms.util.FormsResponseUtils;
import fr.paris.lutece.plugins.forms.web.FormResponseData;
import fr.paris.lutece.portal.service.accesscontrol.AccessControlService;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.util.sql.TransactionManager;

/**
 * FormResponseService
 *
 */
public class FormResponseService
{
    private FormService _formService;

    private static FormResponseService _formResponseService;

    private FormResponseService( )
    {
        _formService = SpringContextService.getBean( FormService.BEAN_NAME );
    }

    public static FormResponseService getInstance( )
    {
        if ( _formResponseService == null )
        {
            _formResponseService = new FormResponseService( );
        }

        return _formResponseService;
    }

    public List<FormResponseData> getFormResponseListForUser( LuteceUser user )
    {
        
		return FormsResponseUtils.filterFormResponseListForLuteceUser(FormsResponseUtils.getFormResponseListByRoleAndGuid(user), user) ;
    }
    
    public List<FormResponse> getFormResponseForUser( LuteceUser user )
    {
      
		return FormsResponseUtils.filterFormResponseForLuteceUser(FormsResponseUtils.getFormResponseListByRoleAndGuid( user ), user) ;
    }
    
    /**
     * Saves the form response
     * 
     * @param formResponse
     *            the form response to save
     */
    public void saveFormResponse( FormResponse formResponse )
    {
        FormResponseHome.update( formResponse );
    }
    
    /**
     * save the response of form
     * 
     * @param form
     *            the form
     * @param request
     *            The Http request
     *  @param formResponse
     *            The FormResponse
     */
    public void saveFormResponseBO( Form form, HttpServletRequest request, FormResponse formResponse )
    {
		User adminUser = AdminUserService.getAdminUser( request );
    	formResponse.setAdmin( adminUser.getEmail( ) );

        if ( ( form.getMaxNumberResponse( ) != 0 ) )
        {
            Object lock = FormsResponseUtils.getLockOnForm( form );
            synchronized( lock )
            {
                int nNumberReponseForm = FormsResponseUtils.getNumberReponseForm( form.getId( ) );
                if ( nNumberReponseForm >= form.getMaxNumberResponse( ) )
                {
                    return;
                }
                _formService.saveForm( form, formResponse );
                FormsResponseUtils.increaseNumberResponse( form );
            }
        }
        else
        {
            _formService.saveForm( form, formResponse );
        }
        AccessControlService.getInstance( ).cleanSessionData( request, form.getId( ), Form.RESOURCE_TYPE );

        _formService.processFormAction( form, formResponse );
    }

    public void deleteFormResponse( FormResponse formResponse )
    {
        TransactionManager.beginTransaction( FormsPlugin.getPlugin( ) );
        try
        {
            for ( FormQuestionResponse formQuestionResponse : FormQuestionResponseHome.getFormQuestionResponseListByFormResponse( formResponse.getId( ) ) )
            {
                FormQuestionResponseHome.remove( formQuestionResponse );
            }

            FormResponseStepHome.removeByFormResponse( formResponse.getId( ) );

            FormResponseHome.remove( formResponse.getId( ) );

            List<Integer> listIdResource = new ArrayList<>( );
            listIdResource.add( formResponse.getId( ) );

            WorkflowService.getInstance( ).doRemoveWorkFlowResource( formResponse.getId( ), FormResponse.RESOURCE_TYPE );

            TransactionManager.commitTransaction( FormsPlugin.getPlugin( ) );
        }
        catch( Exception e )
        {
            TransactionManager.rollBack( FormsPlugin.getPlugin( ) );
            throw new AppException( e.getMessage( ), e );
        }

        _formService.fireFormResponseEventDelete( formResponse );
    }
}
