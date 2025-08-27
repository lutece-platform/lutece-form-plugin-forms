/*
 * Copyright (c) 2002-2025, City of Paris
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

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.export.FormExportConfig;
import fr.paris.lutece.plugins.forms.business.export.FormExportConfigHome;
import fr.paris.lutece.plugins.forms.export.ExportServiceManager;
import fr.paris.lutece.plugins.forms.service.FormService;
import fr.paris.lutece.plugins.forms.service.FormsResourceIdService;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.url.UrlItem;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

@RequestScoped
@Named
@Controller( controllerJsp = "ManageFormExport.jsp", controllerPath = "jsp/admin/plugins/forms/", right = "FORMS_MANAGEMENT", securityTokenEnabled=true )
public class FormExportJspBean extends AbstractJspBean
{
    private static final long serialVersionUID = -2505310758526626253L;

    // Templates
    private static final String TEMPLATE_MANAGE_EXPORT = "/admin/plugins/forms/manage_export.html";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MODIFY_FORM = "forms.modify_form.pageTitle";

    // Views
    private static final String VIEW_MANAGE_EXPORT = "manageExport";
    private static final String VIEW_MANAGE_FORMS = "ManageForms.jsp?view=manageExport";
    private static final String VIEW_CONFIG_REMOVE_EXPORT_CONFIG = "confirmRemoveExportConfig";

    // Marks
    private static final String MARK_EXPORT_LIST = "export_list";
    private static final String MARK_EXPORT_CONFIG_LIST = "export_config_list";
    private static final String MARK_FORM = "form";
    private static final String MARK_QUESTIONLIST = "questionList";
    private static final String MARK_ACTIVE_TAB_PANNEL_2 = "activeTabPannel2"; 
    private static final String MARK_NUMBER_MAX_ORDER = "number_max_order";
    private static final String MARK_EXPORT_ALL = "export_all";
    
    // Actions
    private static final String ACTION_CREATE_EXPORT_CONFIG = "createExportConfig";
    private static final String ACTION_REMOVE_EXPORT_CONFIG = "removeExportConfig";
    private static final String ACTION_MOVE_UP_EXPORT_CONFIG = "doMoveUpExportConfig";
    private static final String ACTION_MOVE_DOWN_EXPORT_CONFIG = "doMoveDownExportConfig";
    private static final String ACTION_MODIFY_EXPORTABLE_QUESTION = "modifyExportableQuestion";
    private static final String ACTION_MODIFY_EXPORT_DISPLAY_ORDER= "modifyExportDisplayOrder";
    private static final String ACTION_SET_DEFAULT_ORDER = "doSetDefaultOrder";

    // Parameters
    private static final String PARAMETER_EXPORT_CONFIG = "export_config";
    private static final String PARAMETER_ID_CONFIG = "id_config";
    private static final String PARAMETER_ACTIVE_TAB_PANNEL_2 = "activeTabPannel2";
    private static final String MESSAGE_CONFIRM_REMOVE_EXPORT_CONFIG = "forms.modify_form.message.confirmRemoveExportConfig";
    private static final String PARAMETER_EXPORT_ALL = "export_all";

    @Inject
    private FormService _formService;
    @Inject
    private SecurityTokenService _securityTokenService;
    
    @View( VIEW_MANAGE_EXPORT )
    public String getManageExport( HttpServletRequest request ) throws AccessDeniedException
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
        
        List<Question> listQuestions = QuestionHome.getQuestionListByIdFormInQuestionOrder( formToBeModified.getId( ) );
        // check if there is a question with exportable = false
        boolean exportAll = true;
        for (Question question : listQuestions) {
            if (!question.getEntry().isExportable()) {
                exportAll = false;
                break;
            }
        }
        Map<String, Object> model = getModel( );
        model.put( MARK_FORM, formToBeModified );
        model.put( MARK_QUESTIONLIST, listQuestions );
        model.put( MARK_EXPORT_LIST, ExportServiceManager.getInstance( ).createReferenceListExportConfigOption( formToBeModified, getLocale( ) ) );
        model.put( MARK_EXPORT_CONFIG_LIST, ExportServiceManager.getInstance( ).createReferenceListExportConfig( formToBeModified, getLocale( ) ) );
        model.put( MARK_ACTIVE_TAB_PANNEL_2, Boolean.parseBoolean( request.getParameter( PARAMETER_ACTIVE_TAB_PANNEL_2 )));
        model.put( SecurityTokenService.MARK_TOKEN, _securityTokenService.getToken( request, ACTION_CREATE_EXPORT_CONFIG ) );

        if (CollectionUtils.isNotEmpty(listQuestions))
        {
        	model.put( MARK_NUMBER_MAX_ORDER, listQuestions.size());
        }
        model.put( MARK_EXPORT_ALL, exportAll);

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_FORM, TEMPLATE_MANAGE_EXPORT, model );
    }

    @Action( ACTION_MODIFY_EXPORT_DISPLAY_ORDER )
    public String doModifyExportDisplayOrder(HttpServletRequest request) throws AccessDeniedException {
        Integer nIdForm = Integer.parseInt(request.getParameter(FormsConstants.PARAMETER_ID_FORM));
        Integer nIdQuestion = Integer.parseInt(request.getParameter(FormsConstants.PARAMETER_ID_QUESTION));
        int nId = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ), FormsConstants.DEFAULT_ID_VALUE );

        if ( nId == FormsConstants.DEFAULT_ID_VALUE )
        {
            return redirect( request, VIEW_MANAGE_FORMS );
        }
        checkUserPermission( Form.RESOURCE_TYPE, String.valueOf( nId ), FormsResourceIdService.PERMISSION_MODIFY, request, ACTION_CREATE_EXPORT_CONFIG );

        Integer nOrderToSet = Integer.parseInt(request.getParameter(FormsConstants.PARAMETER_EXPORT_DISPLAY_ORDER));
        Question questionToChangeOrder = QuestionHome.findByPrimaryKey(nIdQuestion);
        Integer nCurrentOrder = questionToChangeOrder.getExportDisplayOrder();
        List<Question> questionList = QuestionHome.getListQuestionByIdForm(nIdForm);
        questionToChangeOrder.setExportDisplayOrder(nOrderToSet);
        int isOrderExportDecrementing = (nCurrentOrder > nOrderToSet) ? 1 : -1;
        questionList.forEach(question -> {
            int order = question.getExportDisplayOrder();
            if ((nCurrentOrder > nOrderToSet && order >= nOrderToSet && order <= nCurrentOrder) ||
                    (nCurrentOrder <= nOrderToSet && order <= nOrderToSet && order >= nCurrentOrder)) {
                question.setExportDisplayOrder(order + isOrderExportDecrementing);
            }
        });

        questionList.add(questionToChangeOrder);

        // update the list
        questionList.forEach(question -> QuestionHome.update(question));
        Map<String, String> mapParameters = new LinkedHashMap<>();
        mapParameters.put(FormsConstants.PARAMETER_ID_FORM, String.valueOf(nIdForm));
        mapParameters.put(PARAMETER_ACTIVE_TAB_PANNEL_2, "true");

        return redirect(request, VIEW_MANAGE_EXPORT, mapParameters);
    }

    @Action( ACTION_MODIFY_EXPORTABLE_QUESTION )
    public String doModifyExportableQuestion (HttpServletRequest request)  throws AccessDeniedException {
        Integer nIdForm = Integer.parseInt(request.getParameter(FormsConstants.PARAMETER_ID_FORM));
        int nId = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ), FormsConstants.DEFAULT_ID_VALUE );

        if ( nId == FormsConstants.DEFAULT_ID_VALUE )
        {
            return redirect( request, VIEW_MANAGE_FORMS );
        }
        checkUserPermission( Form.RESOURCE_TYPE, String.valueOf( nId ), FormsResourceIdService.PERMISSION_MODIFY, request, ACTION_CREATE_EXPORT_CONFIG );

        if( request.getParameter(FormsConstants.PARAMETER_ID_QUESTION) != null ) {
        Integer nIdQuestion = Integer.parseInt(request.getParameter(FormsConstants.PARAMETER_ID_QUESTION));
        Question questionToUpdate = QuestionHome.findByPrimaryKey(nIdQuestion);
        Entry entry = questionToUpdate.getEntry( );
        if( request.getParameter("exportable_pdf") != null ) {
            boolean bIsExportablePdf = Boolean.parseBoolean(request.getParameter("exportable_pdf"));
            _formService.saveOrUpdateField( entry, IEntryTypeService.FIELD_EXPORTABLE_PDF, null, String.valueOf( bIsExportablePdf ) );
        }
        if( request.getParameter("exportable") != null ) {
            boolean exportable = Boolean.parseBoolean(request.getParameter("exportable"));
            _formService.saveOrUpdateField( entry, IEntryTypeService.FIELD_EXPORTABLE, null, String.valueOf( exportable ) );
        }

        QuestionHome.update(questionToUpdate);
        }
        if( request.getParameter(PARAMETER_EXPORT_ALL) != null ) {
            boolean exportAll = Boolean.parseBoolean(request.getParameter(PARAMETER_EXPORT_ALL));
            List<Question> questionList = QuestionHome.getListQuestionByIdForm(nIdForm);
            questionList.forEach(question -> {
                Entry entry = question.getEntry();
                _formService.saveOrUpdateField( entry, IEntryTypeService.FIELD_EXPORTABLE, null, String.valueOf( exportAll ) );
                _formService.saveOrUpdateField( entry, IEntryTypeService.FIELD_EXPORTABLE_PDF, null, String.valueOf( exportAll ) );
                QuestionHome.update(question);
            });
        }
        Map<String, String> mapParameters = new LinkedHashMap<>();
        mapParameters.put(FormsConstants.PARAMETER_ID_FORM, String.valueOf(nIdForm));
        mapParameters.put(PARAMETER_ACTIVE_TAB_PANNEL_2, "true");

        return redirect(request, VIEW_MANAGE_EXPORT, mapParameters);
    }

    @Action( value = ACTION_CREATE_EXPORT_CONFIG, securityTokenDisabled = true )
    public String doCreateExportConfig( HttpServletRequest request ) throws AccessDeniedException
    {
        int nId = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ), FormsConstants.DEFAULT_ID_VALUE );

        if ( nId == FormsConstants.DEFAULT_ID_VALUE )
        {
            return redirect( request, VIEW_MANAGE_FORMS );
        }
        checkUserPermission( Form.RESOURCE_TYPE, String.valueOf( nId ), FormsResourceIdService.PERMISSION_MODIFY, request, ACTION_CREATE_EXPORT_CONFIG );

        String field = request.getParameter( PARAMETER_EXPORT_CONFIG );

        Form formToBeModified = FormHome.findByPrimaryKey( nId );

        if ( formToBeModified == null )
        {
            return redirect( request, VIEW_MANAGE_FORMS );
        }

        List<FormExportConfig> existingList = ExportServiceManager.getInstance( ).createReferenceListExportConfig( formToBeModified, getLocale( ) );

        FormExportConfig config = new FormExportConfig( );
        config.setIdForm( nId );
        config.setField( field );
        config.setOrder( existingList.size( ) + 1 );

        FormExportConfigHome.create( config );

        Map<String, String> mapParameters = new LinkedHashMap<>( );
        mapParameters.put( FormsConstants.PARAMETER_ID_FORM, String.valueOf( nId ) );

        return redirect( request, VIEW_MANAGE_EXPORT, mapParameters );
    }

    @View( value = VIEW_CONFIG_REMOVE_EXPORT_CONFIG, securityTokenAction = ACTION_REMOVE_EXPORT_CONFIG )
    public String getConfirmRemoveExportConfig( HttpServletRequest request ) throws AccessDeniedException
    {
        int idConfig = NumberUtils.toInt( request.getParameter( PARAMETER_ID_CONFIG ), FormsConstants.DEFAULT_ID_VALUE );
        int idForm = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ), FormsConstants.DEFAULT_ID_VALUE );

        if ( idForm == FormsConstants.DEFAULT_ID_VALUE )
        {
            return redirect( request, VIEW_MANAGE_FORMS );
        }

        checkUserPermission( Form.RESOURCE_TYPE, String.valueOf( idForm ), FormsResourceIdService.PERMISSION_MODIFY, request, null );

        if ( idConfig == FormsConstants.DEFAULT_ID_VALUE )
        {
            Map<String, String> mapParameters = new LinkedHashMap<>( );
            mapParameters.put( FormsConstants.PARAMETER_ID_FORM, String.valueOf( idForm ) );

            return redirect( request, VIEW_MANAGE_EXPORT, mapParameters );
        }

        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_EXPORT_CONFIG ) );
        url.addParameter( PARAMETER_ID_CONFIG, idConfig );
        url.addParameter( FormsConstants.PARAMETER_ID_FORM, idForm );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_EXPORT_CONFIG, url.getUrl( ),
                AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );

    }

    @Action( ACTION_REMOVE_EXPORT_CONFIG )
    public String doRemoveExportConfig( HttpServletRequest request ) throws AccessDeniedException
    {
        int idConfig = NumberUtils.toInt( request.getParameter( PARAMETER_ID_CONFIG ), FormsConstants.DEFAULT_ID_VALUE );
        int idForm = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ), FormsConstants.DEFAULT_ID_VALUE );

        if ( idForm == FormsConstants.DEFAULT_ID_VALUE )
        {
            return redirect( request, VIEW_MANAGE_FORMS );
        }
        checkUserPermission( Form.RESOURCE_TYPE, String.valueOf( idForm ), FormsResourceIdService.PERMISSION_MODIFY, request, null );

        if ( idConfig == FormsConstants.DEFAULT_ID_VALUE )
        {
            Map<String, String> mapParameters = new LinkedHashMap<>( );
            mapParameters.put( FormsConstants.PARAMETER_ID_FORM, String.valueOf( idForm ) );

            return redirect( request, VIEW_MANAGE_EXPORT, mapParameters );
        }

        List<FormExportConfig> existingConfigList = FormExportConfigHome.findByForm( idForm );
        int newOrder = 0;
        FormExportConfigHome.removeByForm( idForm );
        for ( FormExportConfig config : existingConfigList )
        {
            if ( config.getId( ) != idConfig )
            {
                config.setOrder( ++newOrder );
                FormExportConfigHome.create( config );
            }
        }

        Map<String, String> mapParameters = new LinkedHashMap<>( );
        mapParameters.put( FormsConstants.PARAMETER_ID_FORM, String.valueOf( idForm ) );

        return redirect( request, VIEW_MANAGE_EXPORT, mapParameters );
    }

    @Action( ACTION_MOVE_UP_EXPORT_CONFIG )
    public String doMoveUpExportConfig( HttpServletRequest request )
    {
        int idConfig = NumberUtils.toInt( request.getParameter( PARAMETER_ID_CONFIG ), FormsConstants.DEFAULT_ID_VALUE );
        int idForm = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ), FormsConstants.DEFAULT_ID_VALUE );

        if ( idConfig == FormsConstants.DEFAULT_ID_VALUE )
        {
            Map<String, String> mapParameters = new LinkedHashMap<>( );
            mapParameters.put( FormsConstants.PARAMETER_ID_FORM, String.valueOf( idForm ) );

            return redirect( request, VIEW_MANAGE_EXPORT, mapParameters );
        }

        List<FormExportConfig> existingConfigList = FormExportConfigHome.findByForm( idForm );

        FormExportConfig configMovedUp = FormExportConfigHome.findByPrimaryKey( idConfig );
        int orderMovedUp = configMovedUp.getOrder( );

        for ( FormExportConfig config : existingConfigList )
        {
            if ( config.getOrder( ) == orderMovedUp - 1 )
            {
                config.setOrder( orderMovedUp );
                FormExportConfigHome.update( config );

                configMovedUp.setOrder( orderMovedUp - 1 );
                FormExportConfigHome.update( configMovedUp );
                break;
            }
        }

        Map<String, String> mapParameters = new LinkedHashMap<>( );
        mapParameters.put( FormsConstants.PARAMETER_ID_FORM, String.valueOf( idForm ) );

        return redirect( request, VIEW_MANAGE_EXPORT, mapParameters );
    }

    @Action( ACTION_MOVE_DOWN_EXPORT_CONFIG )
    public String doMoveDownExportConfig( HttpServletRequest request )
    {
        int idConfig = NumberUtils.toInt( request.getParameter( PARAMETER_ID_CONFIG ), FormsConstants.DEFAULT_ID_VALUE );
        int idForm = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ), FormsConstants.DEFAULT_ID_VALUE );

        if ( idConfig == FormsConstants.DEFAULT_ID_VALUE )
        {
            Map<String, String> mapParameters = new LinkedHashMap<>( );
            mapParameters.put( FormsConstants.PARAMETER_ID_FORM, String.valueOf( idForm ) );

            return redirect( request, VIEW_MANAGE_EXPORT, mapParameters );
        }

        List<FormExportConfig> existingConfigList = FormExportConfigHome.findByForm( idForm );

        FormExportConfig configMovedDown = FormExportConfigHome.findByPrimaryKey( idConfig );
        int orderMovedDown = configMovedDown.getOrder( );

        for ( FormExportConfig config : existingConfigList )
        {
            if ( config.getOrder( ) == orderMovedDown + 1 )
            {
                config.setOrder( orderMovedDown );
                FormExportConfigHome.update( config );

                configMovedDown.setOrder( orderMovedDown + 1 );
                FormExportConfigHome.update( configMovedDown );
                break;
            }
        }

        Map<String, String> mapParameters = new LinkedHashMap<>( );
        mapParameters.put( FormsConstants.PARAMETER_ID_FORM, String.valueOf( idForm ) );

        return redirect( request, VIEW_MANAGE_EXPORT, mapParameters );
    }

    /**
     * Set ExportDisplayOrder property of the questions to the apparition in form order
     */
    @Action( ACTION_SET_DEFAULT_ORDER )
    public String doSetDefaultDisplayOrder(HttpServletRequest request) throws AccessDeniedException
    {
        Integer nIdForm = Integer.parseInt(request.getParameter(FormsConstants.PARAMETER_ID_FORM));
        int nId = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ), FormsConstants.DEFAULT_ID_VALUE );

        if ( nId == FormsConstants.DEFAULT_ID_VALUE )
        {
            return redirect( request, VIEW_MANAGE_FORMS );
        }
       checkUserPermission( Form.RESOURCE_TYPE, String.valueOf( nId ), FormsResourceIdService.PERMISSION_MODIFY, request, ACTION_CREATE_EXPORT_CONFIG );

        List<Question> questionList = QuestionHome.getQuestionListByIdFormInQuestionOrder(nIdForm);
        for (int i = 0; i < questionList.size();i++){
            questionList.get( i ).setExportDisplayOrder( i+1 );
        }
        // update the list
        questionList.forEach( QuestionHome::update );
        Map<String, String> mapParameters = new LinkedHashMap<>();
        mapParameters.put(FormsConstants.PARAMETER_ID_FORM, String.valueOf(nIdForm));
        mapParameters.put(PARAMETER_ACTIVE_TAB_PANNEL_2, "true");

        return redirect(request, VIEW_MANAGE_EXPORT, mapParameters);
    }
}
