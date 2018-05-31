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

package fr.paris.lutece.plugins.forms.web;

import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.html.HtmlTemplate;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * This class provides the user interface to manage Form features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageTransitions.jsp", controllerPath = "jsp/admin/plugins/forms/", right = "FORMS_MANAGEMENT" )
public class FormTransitionJspBean extends MVCAdminJspBean
{

    private static final long serialVersionUID = -9023450166890042022L;

    private static final String EMPTY_STRING = "";

    private static final String JSP_MANAGE_FORMS = "jsp/admin/plugins/forms/ManageForms.jsp";

    // Templates
    private static final String TEMPLATE_MANAGE_TRANSITIONS = "/admin/plugins/forms/manage_transitions.html";
    private static final String TEMPLATE_CREATE_TRANSITION = "/admin/plugins/forms/create_transition.html";
    private static final String TEMPLATE_MODIFY_TRANSITION = "/admin/plugins/forms/modify_transition.html";

    // Parameters
    private static final String PARAMETER_PAGE_INDEX = "page_index";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MODIFY_TRANSITION = "forms.modify_transition.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_TRANSITION = "forms.create_transition.pageTitle";

    // Markers
    private static final String MARK_STEP_LIST = "step_list";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_LOCALE = "locale";

    // Properties
    private static final String PROPERTY_ITEM_PER_PAGE = "forms.itemsPerPage";
    private static final String PROPERTY_COPY_FORM_TITLE = "forms.copyStep.title";

    // Messages
    private static final String MESSAGE_CONFIRM_REMOVE_STEP = "forms.message.confirmRemoveStep";
    private static final String MESSAGE_CONFIRM_REMOVE_STEP_ACTIVE_FORM = "forms.message.confirmRemoveStepActiveForm";

    // Validations
    private static final String STEP_VALIDATION_ATTRIBUTES_PREFIX = "forms.model.entity.step.attribute.";

    // Views
    private static final String VIEW_MANAGE_TRANSITIONS = "manageTransitions";
    private static final String VIEW_CREATE_TRANSITION = "createTransition";
    private static final String VIEW_MODIFY_TRANSITION = "modifyTransition";
    private static final String VIEW_CONFIRM_REMOVE_TRANSITION = "confirmRemoveTransition";

    // Actions
    private static final String ACTION_CREATE_TRANSITION = "createTransition";
    private static final String ACTION_MODIFY_TRANSITION = "modifyTransition";
    private static final String ACTION_REMOVE_TRANSITION = "removeTransition";

    // Infos
    private static final String INFO_TRANSITION_CREATED = "forms.info.transition.created";
    private static final String INFO_TRANSITION_UPDATED = "forms.info.transition.updated";
    private static final String INFO_TRANSITION_REMOVED = "forms.info.transition.removed";

    private final int _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_ITEM_PER_PAGE, 50 );
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_TRANSITIONS, defaultView = true )
    public String getManageSteps( HttpServletRequest request )
    {
        // TO DO : implement feature
        Locale locale = getLocale( );

        int nIdStep = -1;
        try
        {
            nIdStep = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( ne );
            return redirect( request, getJspManageForm( request ) );

        }

        Step step = StepHome.findByPrimaryKey( nIdStep );

        Map<String, Object> model = getModel( );
        model.put( FormsConstants.MARK_STEP, step );

        setPageTitleProperty( EMPTY_STRING );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_TRANSITIONS, locale, model );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Return the URL of the JSP manage form
     * 
     * @param request
     *            The HTTP request
     * @return The URL of the JSP manage form
     */
    protected String getJspManageForm( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MANAGE_FORMS;
    }

}
