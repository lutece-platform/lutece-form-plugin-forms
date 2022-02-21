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

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.ControlType;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.business.Transition;
import fr.paris.lutece.plugins.forms.business.TransitionHome;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.utils.algo.helper.DetectCycleGraphBuilder;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class provides the user interface to manage Form features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageTransitions.jsp", controllerPath = "jsp/admin/plugins/forms/", right = "FORMS_MANAGEMENT" )
public class FormTransitionJspBean extends AbstractJspBean
{

    private static final long serialVersionUID = -9023450166890042022L;

    private static final String EMPTY_STRING = "";

    // Templates
    private static final String TEMPLATE_MANAGE_TRANSITIONS = "/admin/plugins/forms/manage_transitions.html";
    private static final String TEMPLATE_CREATE_TRANSITION = "/admin/plugins/forms/create_transition.html";
    private static final String TEMPLATE_MODIFY_TRANSITION = "/admin/plugins/forms/modify_transition.html";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MODIFY_TRANSITION = "forms.modify_transition.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_TRANSITION = "forms.create_transition.pageTitle";

    // Markers
    private static final String MARK_TRANSITION_LIST = "transition_list";

    // Validations
    private static final String TRANSITION_VALIDATION_ATTRIBUTES_PREFIX = "forms.model.entity.transition.attribute.";

    // Views
    private static final String VIEW_MANAGE_TRANSITIONS = "manageTransitions";
    private static final String VIEW_CREATE_TRANSITION = "createTransition";
    private static final String VIEW_MODIFY_TRANSITION = "modifyTransition";
    private static final String VIEW_CONFIRM_REMOVE_TRANSITION = "confirmRemoveTransition";

    // Actions
    private static final String ACTION_CREATE_TRANSITION = "createTransition";
    private static final String ACTION_CREATE_TRANSITION_AND_CONTROL = "createTransitionAndControl";
    private static final String ACTION_MODIFY_TRANSITION = "modifyTransition";
    private static final String ACTION_REMOVE_TRANSITION = "removeTransition";

    // Infos
    private static final String INFO_TRANSITION_CREATED = "forms.info.transition.created";
    private static final String INFO_TRANSITION_UPDATED = "forms.info.transition.updated";
    private static final String INFO_TRANSITION_REMOVED = "forms.info.transition.removed";
    private static final String MESSAGE_CONFIRM_REMOVE_TRANSITION = "forms.message.confirmRemoveTransition";

    // Warning
    private static final String WARNING_NO_TRANSITION_STEP_FINAL = "forms.warning.transition.cannotAdd.finalStep";
    // Errors
    private static final String ERROR_TRANSITION_REMOVED = "forms.error.deleteTransition";

    private static final String ACTION_DO_MOVE_PRIORITY_UP = "moveUpPriority";

    private static final String ACTION_DO_MOVE_PRIORITY_DOWN = "moveDownPriority";

    // Session variable to store working values
    private Transition _transition;
    private Step _step;
    private Form _form;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_TRANSITIONS, defaultView = true )
    public String getManageTransition( HttpServletRequest request )
    {
        _transition = null;

        int nIdStep = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), FormsConstants.DEFAULT_ID_VALUE );

        if ( nIdStep == FormsConstants.DEFAULT_ID_VALUE )
        {
            return redirectToViewManageForm( request );
        }

        _step = StepHome.findByPrimaryKey( nIdStep );

        if ( _step != null )
        {
            int nIdForm = _step.getIdForm( );

            if ( _form == null || _form.getId( ) != nIdForm )
            {
                _form = FormHome.findByPrimaryKey( nIdForm );
            }
        }

        Locale locale = getLocale( );

        if ( _step.isFinal( ) )
        {
            addWarning( WARNING_NO_TRANSITION_STEP_FINAL, locale );
        }

        String strInfoKey = request.getParameter( FormsConstants.PARAMETER_INFO_KEY );
        if ( StringUtils.isNotEmpty( strInfoKey ) )
        {
            addInfo( strInfoKey, getLocale( ) );
        }

        Map<String, Object> model = getModel( );

        List<Transition> listTransition = TransitionHome.getTransitionsListFromStep( nIdStep );
        for ( Transition transition : listTransition )
        {
            transition.setConditional(
                    CollectionUtils.isNotEmpty( ControlHome.getControlByControlTargetAndType( transition.getId( ), ControlType.TRANSITION ) ) );
        }

        model.put( MARK_TRANSITION_LIST, listTransition );
        model.put( FormsConstants.MARK_STEP, _step );
        model.put( FormsConstants.MARK_FORM, _form );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, FormStepJspBean.ACTION_MODIFY_STEP ) );
        
        setPageTitleProperty( EMPTY_STRING );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_TRANSITIONS, locale, model );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Returns the form to create a transition
     *
     * @param request
     *            The Http request
     * @return the html code of the transition form
     */
    @View( VIEW_CREATE_TRANSITION )
    public String getCreateTransition( HttpServletRequest request )
    {
        if ( !retrieveStepFromRequest( request ) )
        {
            return redirectToViewManageForm( request );
        }

        _transition = ( _transition != null ) ? _transition : new Transition( );

        Map<String, Object> model = getModel( );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_TRANSITION ) );
        if ( _step != null )
        {
            buildTransitionModel( model );
        }
        else
        {
            redirectToViewManageForm( request );
        }

        return getPage( PROPERTY_PAGE_TITLE_CREATE_TRANSITION, TEMPLATE_CREATE_TRANSITION, model );
    }

    /**
     * Returns the form to modify a transition
     *
     * @param request
     *            The Http request
     * @return the html code of the transition form
     */
    @View( VIEW_MODIFY_TRANSITION )
    public String getModifyTransition( HttpServletRequest request )
    {

        if ( !retrieveStepFromRequest( request ) )
        {
            return redirectToViewManageForm( request );
        }

        if ( !retrieveTransitionFromRequest( request ) )
        {
            return redirectToViewManageForm( request );
        }

        Map<String, Object> model = getModel( );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_TRANSITION ) );

        if ( _transition != null && _step != null )
        {
            buildTransitionModel( model );
        }
        else
        {
            redirectToViewManageForm( request );
        }

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_TRANSITION, TEMPLATE_MODIFY_TRANSITION, model );
    }

    /**
     * Build the model for Create and Modify Transition views
     * 
     * @param model
     *            the Model
     */
    private void buildTransitionModel( Map<String, Object> model )
    {
        ReferenceList listTransitionTargetSteps = getTransitionTargetStepReferenceList( _step.getIdForm( ), _step.getId( ) );

        model.put( FormsConstants.MARK_TRANSITION, _transition );
        model.put( FormsConstants.MARK_AVAILABLE_STEPS, listTransitionTargetSteps );

        model.put( FormsConstants.MARK_STEP, _step );

    }

    /**
     * Process the data capture of a new transition and redirect to transition management
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException 
     */
    @Action( ACTION_CREATE_TRANSITION )
    public String doCreateTransition( HttpServletRequest request ) throws AccessDeniedException
    {
        // CSRF Token control
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_TRANSITION ) )
        {
            throw new AccessDeniedException( MESSAGE_ERROR_TOKEN );
        }
        
        if ( !createTransition( request ) )
        {
            return redirect( request, VIEW_CREATE_TRANSITION, FormsConstants.PARAMETER_ID_STEP, _transition.getFromStep( ) );
        }

        addInfo( INFO_TRANSITION_CREATED, getLocale( ) );

        return redirect( request, VIEW_MANAGE_TRANSITIONS, FormsConstants.PARAMETER_ID_STEP, _transition.getFromStep( ) );
    }

    /**
     * Process the data capture of a new transition and redirect to control creation
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_TRANSITION_AND_CONTROL )
    public String doCreateTransitionAndControl( HttpServletRequest request )
    {
        
        if ( !createTransition( request ) )
        {
            return redirect( request, VIEW_CREATE_TRANSITION, FormsConstants.PARAMETER_ID_STEP, _transition.getFromStep( ) );
        }

        addInfo( INFO_TRANSITION_CREATED, getLocale( ) );

        UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + FormsConstants.JSP_MANAGE_CONTROLS );
        url.addParameter( FormsConstants.PARAMETER_ID_TRANSITION, _transition.getId( ) );
        url.addParameter( FormsConstants.PARAMETER_TARGET_VIEW, "modifyTransitionControl" );
        url.addParameter( FormsConstants.PARAMETER_ID_STEP, _transition.getFromStep( ) );
        url.addParameter( FormsConstants.PARAMETER_CONTROL_TYPE, ControlType.TRANSITION.toString( ) );
        url.addParameter( FormsConstants.PARAMETER_ID_TARGET, _transition.getId( ) );

        return redirect( request, url.getUrl( ) );
    }

    /**
     * Process the data capture of a new transition
     *
     * @param request
     *            The Http Request
     * @return The boolean indicate the success or failure of the transition
     */
    private boolean createTransition( HttpServletRequest request )
    {
        if ( _transition == null )
        {
            _transition = new Transition( );
        }
        populate( _transition, request, getLocale( ) );

        if ( detectCircularTransition( _transition ) || !validateTransition( _transition ) )
        {
            return false;
        }

        TransitionHome.create( _transition );

        return true;
    }

    /**
     * Process the data modification of a transition
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException 
     */
    @Action( ACTION_MODIFY_TRANSITION )
    public String doModifyTransition( HttpServletRequest request ) throws AccessDeniedException
    {
        // CSRF Token control
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_TRANSITION ) )
        {
            throw new AccessDeniedException( MESSAGE_ERROR_TOKEN );
        }
        
        if ( !retrieveTransitionFromRequest( request ) )
        {
            return redirectToViewManageForm( request );
        }

        populate( _transition, request, getLocale( ) );

        if ( detectCircularTransition( _transition ) || !validateTransition( _transition ) )
        {
            return redirect( request, VIEW_MODIFY_TRANSITION, FormsConstants.PARAMETER_ID_STEP, _transition.getFromStep( ),
                    FormsConstants.PARAMETER_ID_TRANSITION, _transition.getId( ) );
        }

        TransitionHome.update( _transition );

        addInfo( INFO_TRANSITION_UPDATED, getLocale( ) );

        return redirect( request, VIEW_MANAGE_TRANSITIONS, FormsConstants.PARAMETER_ID_STEP, _transition.getFromStep( ) );
    }

    private boolean detectCircularTransition( Transition newTransition )
    {
        List<Transition> listTransitions = TransitionHome.getTransitionsListFromForm( _form.getId( ) );
        listTransitions.add( newTransition );

        DetectCycleGraphBuilder builder = DetectCycleGraphBuilder.builder( );
        for ( Transition transition : listTransitions )
        {
            builder.addToGraph( transition.getFromStep( ), transition.getNextStep( ) );
        }
        return builder.build( ).hasCycle( );
    }

    /**
     * Manages the removal form of a Transition whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @View( VIEW_CONFIRM_REMOVE_TRANSITION )
    public String getConfirmRemoveTransition( HttpServletRequest request )
    {
        int nIdTransitionToRemove = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_TRANSITION ), FormsConstants.DEFAULT_ID_VALUE );

        if ( nIdTransitionToRemove == FormsConstants.DEFAULT_ID_VALUE )
        {
            return redirectToViewManageForm( request );
        }

        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_TRANSITION ) );
        url.addParameter( FormsConstants.PARAMETER_ID_TRANSITION, nIdTransitionToRemove );
        url.addParameter(  SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_REMOVE_TRANSITION ) );
        
        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_TRANSITION, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );

    }

    /**
     * Handles the removal of a Transition
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage Transition
     * @throws AccessDeniedException 
     */
    @Action( ACTION_REMOVE_TRANSITION )
    public String doRemoveTransition( HttpServletRequest request ) throws AccessDeniedException
    {
        // CSRF Token control
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_REMOVE_TRANSITION ) )
        {
            throw new AccessDeniedException( MESSAGE_ERROR_TOKEN );
        }
        
        if ( !retrieveTransitionFromRequest( request ) )
        {
            return redirectToViewManageForm( request );
        }

        if ( _transition != null )
        {
            TransitionHome.remove( _transition.getId( ) );

            ControlHome.removeByControlTarget( _transition.getId( ), ControlType.TRANSITION );

            TransitionHome.rebuildPrioritySequence( _transition.getFromStep( ) );

            addInfo( INFO_TRANSITION_REMOVED, getLocale( ) );
        }
        else
        {
            addError( ERROR_TRANSITION_REMOVED, getLocale( ) );
            return redirectToViewManageForm( request );
        }

        return redirect( request, VIEW_MANAGE_TRANSITIONS, FormsConstants.PARAMETER_ID_STEP, _transition.getFromStep( ) );
    }

    /**
     * Move transition priority up
     * 
     * @param request
     *            The request
     * @return The next URL to redirect to
     */
    @Action( ACTION_DO_MOVE_PRIORITY_UP )
    public String doMovePriorityUp( HttpServletRequest request )
    {
        return doMovePriority( request, true );
    }

    /**
     * Move transition priority down
     * 
     * @param request
     *            The request
     * @return The next URL to redirect to
     */
    @Action( ACTION_DO_MOVE_PRIORITY_DOWN )
    public String doMovePriorityDown( HttpServletRequest request )
    {
        return doMovePriority( request, false );
    }

    /**
     * Move transition priority up or down
     * 
     * @param request
     *            The request
     * @param bMoveUp
     *            True to move the priority up, false to move it down
     * @return The next URL to redirect to
     */
    private String doMovePriority( HttpServletRequest request, boolean bMoveUp )
    {
        if ( !retrieveStepFromRequest( request ) )
        {
            return redirectToViewManageForm( request );
        }

        if ( !retrieveTransitionFromRequest( request ) )
        {
            return redirectToViewManageForm( request );
        }

        int nOldPriority = _transition.getPriority( );
        int nNewPriority = bMoveUp ? ( nOldPriority - 1 ) : ( nOldPriority + 1 );

        Transition transitionToInversePriority = TransitionHome.getTransitionByPriority( _step.getId( ), nNewPriority );

        if ( transitionToInversePriority != null )
        {
            transitionToInversePriority.setPriority( nOldPriority );
            TransitionHome.update( transitionToInversePriority );
        }
        _transition.setPriority( nNewPriority );
        TransitionHome.update( _transition );

        return redirect( request, VIEW_MANAGE_TRANSITIONS, FormsConstants.PARAMETER_ID_STEP, _transition.getFromStep( ) );
    }

    /**
     * Retrieve the step object from request parameter
     * 
     * @param request
     *            The request
     * 
     * @return false if an error occurred, true otherwise
     */
    private boolean retrieveStepFromRequest( HttpServletRequest request )
    {
        boolean bSuccess = true;

        int nIdStep = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), FormsConstants.DEFAULT_ID_VALUE );

        if ( nIdStep == FormsConstants.DEFAULT_ID_VALUE )
        {
            bSuccess = false;
        }

        if ( _step == null || _step.getId( ) != nIdStep )
        {
            _step = StepHome.findByPrimaryKey( nIdStep );
        }
        return bSuccess;
    }

    /**
     * Retrieve the transition object from request parameter
     * 
     * @param request
     *            The request
     * 
     * @return false if an error occurred, true otherwise
     */
    private boolean retrieveTransitionFromRequest( HttpServletRequest request )
    {
        boolean bSuccess = true;
        int nIdTransition = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_TRANSITION ), FormsConstants.DEFAULT_ID_VALUE );

        if ( nIdTransition == FormsConstants.DEFAULT_ID_VALUE )
        {
            bSuccess = false;
        }

        if ( _transition == null || _transition.getId( ) != nIdTransition )
        {
            _transition = TransitionHome.findByPrimaryKey( nIdTransition );
        }
        return bSuccess;
    }

    /**
     * Validate the Transition field values
     * 
     * @param transition
     *            the Transition to validate
     * 
     * @return True if the transition is valid
     */
    private boolean validateTransition( Transition transition )
    {
        return validateBean( transition, TRANSITION_VALIDATION_ATTRIBUTES_PREFIX );
    }

    /**
     * Build a referenceList containing all the target Steps of a transition, for a given Form.
     * 
     * @param nIdForm
     *            the Form identifier
     * 
     * @param nIdStep
     *            the identifier of the transition origin step
     * 
     * @return a referenceList with all the target Steps
     */
    private ReferenceList getTransitionTargetStepReferenceList( int nIdForm, int nIdStep )
    {
        ReferenceList listTransitionTargetSteps = new ReferenceList( );
        List<Transition> listCurrentTransitions = TransitionHome.getTransitionsListFromStep( nIdStep );

        for ( ReferenceItem step : StepHome.getStepReferenceListByForm( nIdForm ) )
        {
            if ( NumberUtils.toInt( step.getCode( ) ) != nIdStep
                    && listCurrentTransitions.stream( ).allMatch( t -> NumberUtils.toInt( step.getCode( ) ) != t.getNextStep( ) ) )
            {
                listTransitionTargetSteps.add( step );
            }
        }
        return listTransitionTargetSteps;
    }

}
