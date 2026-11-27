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

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormLayout;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.service.FormBuilderException;
import fr.paris.lutece.plugins.forms.service.FormBuilderService;
import fr.paris.lutece.plugins.forms.service.FormsResourceIdService;
import fr.paris.lutece.plugins.forms.service.StepService;
import fr.paris.lutece.plugins.forms.service.json.IStepTemplateProvider;
import fr.paris.lutece.plugins.forms.service.json.StepTemplatePreview;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.util.FormsEntryUtils;
import fr.paris.lutece.plugins.forms.web.builder.FormBuilderRenderer;
import fr.paris.lutece.plugins.forms.web.builder.FormBuilderResult;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.template.HtmlMarkup;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.security.SecurityTokenHandler;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.json.JsonUtil;
import fr.paris.lutece.util.url.UrlItem;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Form builder: edits all the steps and fields of a form on a single WYSIWYG screen, with drag and drop. Every change goes through one dispatching
 * action answering JSON, so that one rolling security token protects all of them.
 */
@RequestScoped
@Named
@Controller( controllerJsp = "ManageFormBuilder.jsp", controllerPath = "jsp/admin/plugins/forms/", right = "FORMS_MANAGEMENT", securityTokenEnabled = true )
public class FormBuilderJspBean extends AbstractJspBean
{
    private static final long serialVersionUID = -3360829187356405913L;

    private static final String TEMPLATE_BUILDER = "/admin/plugins/forms/builder/builder.html";

    private static final String PROPERTY_PAGE_TITLE = "forms.form_builder.pageTitle";

    private static final String VIEW_BUILDER = "builder";
    private static final String ACTION_BUILDER_OPERATION = "builderOperation";

    private static final String PARAMETER_OPERATION = "operation";
    private static final String PARAMETER_ID_STEP_TARGET = "id_step_target";
    private static final String PARAMETER_POSITION = "position";
    private static final String PARAMETER_COLUMN = "column";
    private static final String PARAMETER_KNOWN_MAX_ID_DISPLAY = "known_max_id_display";
    private static final String PARAMETER_STEP_ORDER = "step_order";
    private static final String PARAMETER_STEP_JSON = "step_json_base64";
    private static final String PARAMETER_ID_TEMPLATE = "id_template";
    private static final String PARAMETER_ID_STEP_AFTER = "id_step_after";
    private static final String STEP_ORDER_TRANSITIONS = "transitions";

    private static final String OPERATION_REFRESH = "refresh";
    private static final String OPERATION_MOVE = "move";
    private static final String OPERATION_PLACE_NEW = "placeNew";
    private static final String OPERATION_DUPLICATE = "duplicate";
    private static final String OPERATION_REMOVE = "remove";
    private static final String OPERATION_DUPLICATE_STEP = "duplicateStep";
    private static final String OPERATION_REMOVE_STEP = "removeStep";
    private static final String OPERATION_IMPORT_STEP = "importStep";

    private static final String MARK_STEPS_HTML = "steps_html";
    private static final String MARK_CANVAS_HTML = "canvas_html";
    private static final String MARK_MAX_ID_DISPLAY = "max_id_display";
    private static final String MARK_BUILDER_TOKEN = "builder_token";
    private static final String MARK_STEP_TEMPLATES = "step_templates";
    private static final String MARK_TEMPLATE_PROVIDER = "template_provider";
    private static final String MARK_STEP_TEMPLATE_DESCRIPTIONS = "step_template_descriptions";
    private static final String MARK_STEP_TEMPLATE_PREVIEWS = "step_template_previews";

    private static final String MESSAGE_MOVED = "forms.form_builder.message.moved";
    private static final String MESSAGE_PLACED = "forms.form_builder.message.created";
    private static final String MESSAGE_DUPLICATED = "forms.form_builder.message.duplicated";
    private static final String MESSAGE_REMOVED = "forms.form_builder.message.deleted";
    private static final String MESSAGE_STEP_DUPLICATED = "forms.info.step.copied";
    private static final String MESSAGE_STEP_REMOVED = "forms.info.step.removed";
    private static final String MESSAGE_STEP_IMPORTED = "forms.info.step.created";
    private static final String MESSAGE_UNKNOWN_OPERATION = "forms.form_builder.message.error.unknownOperation";
    private static final String MESSAGE_UNAUTHORIZED = "forms.form_builder.message.error.unauthorized";
    private static final String MESSAGE_FAILED = "forms.form_builder.message.error.operationFailed";

    @Inject
    private FormBuilderService _formBuilderService;

    @Inject
    private FormBuilderRenderer _formBuilderRenderer;

    @Inject
    private SecurityTokenHandler _securityTokenHandler;

    @Inject
    private StepService _stepService;

    @Inject
    private FormQuestionJspBean _formQuestionJspBean;

    /**
     * Builds the form builder page. A request without an explicit view is redirected to the view, so that the page always carries the security token of
     * the builder operations.
     *
     * @param request
     *            The HTTP request
     * @param model
     *            The model
     * @return The page
     * @throws AccessDeniedException
     *             if the user cannot modify the form
     */
    @View( value = VIEW_BUILDER, defaultView = true, securityTokenAction = ACTION_BUILDER_OPERATION )
    public String getBuilder( HttpServletRequest request, Models model ) throws AccessDeniedException
    {
        int nIdForm = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ), FormsConstants.DEFAULT_ID_VALUE );
        Form form = FormHome.findByPrimaryKey( nIdForm );
        if ( form == null )
        {
            return redirectToViewManageForm( request );
        }

        checkFormPermission( form, request );

        if ( MVCUtils.getView( request ) == null )
        {
            UrlItem url = new UrlItem( getViewFullUrl( VIEW_BUILDER ) );
            url.addParameter( FormsConstants.PARAMETER_ID_FORM, nIdForm );
            return redirect( request, url.getUrl( ) );
        }

        Step step = findCurrentStep( form, NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), FormsConstants.DEFAULT_ID_VALUE ) );
        Locale locale = getLocale( );

        model.put( FormsConstants.MARK_FORM, form );
        model.put( FormsConstants.MARK_STEP, step );
        model.put( MARK_STEPS_HTML, HtmlMarkup.of( _formBuilderRenderer.renderSteps( form, step, isTransitionOrder( request ), locale ) ) );
        model.put( MARK_CANVAS_HTML, HtmlMarkup.of( _formBuilderRenderer.renderCanvas( request, form, step, locale ) ) );
        model.put( FormsConstants.MARK_ENTRY_TYPE_REF_LIST, FormsEntryUtils.initListEntryType( ) );
        model.put( MARK_MAX_ID_DISPLAY, _formBuilderService.getMaxIdDisplay( nIdForm ) );
        model.put( MARK_BUILDER_TOKEN, _securityTokenHandler.resolveTokenValue( request ) );
        model.put( MARK_STEP_TEMPLATES, getStepTemplates( ) );
        model.put( MARK_TEMPLATE_PROVIDER, _stepService.getStepTemplateProvider( ) != null );
        putStepTemplatePreviews( model );
        model.put( FormsConstants.MARK_PROD_URL, AppPathService.getProdUrl( request ) );

        return getPage( PROPERTY_PAGE_TITLE, TEMPLATE_BUILDER, model );
    }

    /**
     * Runs one change of the form (move, place, duplicate or remove a display; duplicate or remove a step) and answers the refreshed fragments as JSON.
     * The core validates the security token of the builder (and refuses the action on GET when lutece.mvc.action.getPolicy says so); the answer
     * carries the token of the next operation.
     *
     * @param request
     *            The HTTP request
     */
    @Action( ACTION_BUILDER_OPERATION )
    public void doBuilderOperation( HttpServletRequest request )
    {
        FormBuilderResult result = new FormBuilderResult( );
        Locale locale = getLocale( );
        int nIdForm = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ), FormsConstants.DEFAULT_ID_VALUE );
        int nIdStepCurrent = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), FormsConstants.DEFAULT_ID_VALUE );
        Form form = FormHome.findByPrimaryKey( nIdForm );

        try
        {
            if ( form == null )
            {
                throw new AccessDeniedException( UNAUTHORIZED );
            }
            checkFormPermission( form, request );
            String strMessageKey = runOperation( request, form, result, locale );
            if ( strMessageKey != null )
            {
                result.setMessage( I18nService.getLocalizedString( strMessageKey, locale ) );
            }
        }
        catch( AccessDeniedException e )
        {
            result.setStatus( FormBuilderResult.STATUS_ERROR );
            result.setMessage( I18nService.getLocalizedString( MESSAGE_UNAUTHORIZED, locale ) );
        }
        catch( FormBuilderException e )
        {
            result.setStatus( FormBuilderResult.STATUS_ERROR );
            result.setMessage( I18nService.getLocalizedString( e.getMessageKey( ), locale ) );
        }
        catch( RuntimeException e )
        {
            AppLogService.getLogger( ).error( "Form builder : operation failed on form {}", nIdForm, e );
            result.setStatus( FormBuilderResult.STATUS_ERROR );
            result.setMessage( I18nService.getLocalizedString( MESSAGE_FAILED, locale ) );
        }

        if ( form != null && result.getIdStep( ) == 0 )
        {
            result.setIdStep( nIdStepCurrent );
        }
        fillFragments( request, form, result, locale, stepsMayChange( request, nIdStepCurrent ) );
        result.setToken( _securityTokenHandler.resolveTokenValue( request ) );
        writeJson( result );
    }

    /**
     * Dispatches the operation named by the request
     *
     * @param request
     *            The HTTP request
     * @param form
     *            The form
     * @param result
     *            The result, completed with the step to show and the display concerned
     * @param locale
     *            The locale
     * @return the i18n key of the success message, null for the refresh, which changes nothing
     * @throws FormBuilderException
     *             if the operation is refused
     */
    private String runOperation( HttpServletRequest request, Form form, FormBuilderResult result, Locale locale ) throws FormBuilderException
    {
        int nIdForm = form.getId( );
        int nIdDisplay = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY ), FormsConstants.DEFAULT_ID_VALUE );
        int nIdStepTarget = NumberUtils.toInt( request.getParameter( PARAMETER_ID_STEP_TARGET ), FormsConstants.DEFAULT_ID_VALUE );
        int nIdParent = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_PARENT ), 0 );
        int nPosition = NumberUtils.toInt( request.getParameter( PARAMETER_POSITION ), Integer.MAX_VALUE );
        int nColumn = NumberUtils.toInt( request.getParameter( PARAMETER_COLUMN ), FormLayout.FIRST_COLUMN );
        String strOperation = request.getParameter( PARAMETER_OPERATION );

        if ( OPERATION_REFRESH.equals( strOperation ) )
        {
            return null;
        }

        if ( OPERATION_MOVE.equals( strOperation ) )
        {
            _formBuilderService.moveDisplay( nIdForm, nIdDisplay, nIdStepTarget, nIdParent, nColumn, nPosition );
            result.setIdDisplay( nIdDisplay );
            return MESSAGE_MOVED;
        }
        if ( OPERATION_PLACE_NEW.equals( strOperation ) )
        {
            int nKnownMaxIdDisplay = NumberUtils.toInt( request.getParameter( PARAMETER_KNOWN_MAX_ID_DISPLAY ), Integer.MAX_VALUE );
            result.setIdDisplay( _formBuilderService.placeNewDisplay( nIdForm, nIdStepTarget, nIdParent, nKnownMaxIdDisplay, nColumn, nPosition,
                    _formQuestionJspBean.getWorkingQuestionId( ), _formQuestionJspBean.getWorkingGroupId( ) ) );
            result.setIdStep( nIdStepTarget );
            return MESSAGE_PLACED;
        }
        if ( OPERATION_DUPLICATE.equals( strOperation ) )
        {
            result.setIdDisplay( _formBuilderService.duplicateQuestion( nIdForm, nIdDisplay, locale ) );
            return MESSAGE_DUPLICATED;
        }
        if ( OPERATION_REMOVE.equals( strOperation ) )
        {
            _formBuilderService.removeDisplay( nIdForm, nIdDisplay );
            return MESSAGE_REMOVED;
        }
        if ( OPERATION_DUPLICATE_STEP.equals( strOperation ) )
        {
            _formBuilderService.duplicateStep( nIdForm, nIdStepTarget, locale );
            return MESSAGE_STEP_DUPLICATED;
        }
        if ( OPERATION_REMOVE_STEP.equals( strOperation ) )
        {
            _formBuilderService.removeStep( nIdForm, nIdStepTarget );
            return MESSAGE_STEP_REMOVED;
        }
        if ( OPERATION_IMPORT_STEP.equals( strOperation ) )
        {
            result.setIdStep( _formBuilderService.importStep( nIdForm, decodeBase64( request.getParameter( PARAMETER_STEP_JSON ) ),
                    NumberUtils.toInt( request.getParameter( PARAMETER_ID_TEMPLATE ), FormsConstants.DEFAULT_ID_VALUE ),
                    NumberUtils.toInt( request.getParameter( PARAMETER_ID_STEP_AFTER ), 0 ), locale ) );
            return MESSAGE_STEP_IMPORTED;
        }
        throw new FormBuilderException( MESSAGE_UNKNOWN_OPERATION );
    }

    /**
     * Decodes the JSON of an imported step. The browser sends it in base64 because the admin XSS filter rewrites the quotes of the request parameters,
     * while the base64 alphabet goes through it unchanged.
     *
     * @param strBase64
     *            the base64 encoded UTF-8 JSON, may be null
     * @return the JSON, null when the parameter is absent
     * @throws FormBuilderException
     *             if the parameter is not valid base64
     */
    private String decodeBase64( String strBase64 ) throws FormBuilderException
    {
        if ( StringUtils.isBlank( strBase64 ) )
        {
            return null;
        }
        try
        {
            return new String( Base64.getDecoder( ).decode( strBase64.trim( ) ), StandardCharsets.UTF_8 );
        }
        catch( IllegalArgumentException e )
        {
            throw new FormBuilderException( FormBuilderService.ERROR_STEP_NOT_IMPORTED );
        }
    }

    /**
     * Puts in the model the descriptions and the previews (thumbnail and fields) of the step templates offered by the template dialog
     *
     * @param model
     *            The model of the builder page
     */
    private void putStepTemplatePreviews( Models model )
    {
        IStepTemplateProvider provider = _stepService.getStepTemplateProvider( );
        Map<String, String> mapDescriptions = provider != null ? provider.getStepTemplateDescriptions( ) : null;
        Map<String, StepTemplatePreview> mapPreviews = provider != null ? provider.getStepTemplatePreviews( ) : null;
        model.put( MARK_STEP_TEMPLATE_DESCRIPTIONS, mapDescriptions != null ? mapDescriptions : Collections.emptyMap( ) );
        model.put( MARK_STEP_TEMPLATE_PREVIEWS, mapPreviews != null ? mapPreviews : Collections.emptyMap( ) );
    }

    /**
     * Returns the step templates offered by the template dialog
     *
     * @return the templates sorted by name; an empty list when no template provider is installed
     */
    private ReferenceList getStepTemplates( )
    {
        ReferenceList listTemplates = new ReferenceList( );
        IStepTemplateProvider provider = _stepService.getStepTemplateProvider( );
        if ( provider != null && provider.getStepTemplateList( ) != null )
        {
            provider.getStepTemplateList( ).stream( ).sorted( Comparator.comparing( ReferenceItem::getName, Comparator.nullsLast( String::compareToIgnoreCase ) ) )
                    .forEach( listTemplates::add );
        }
        return listTemplates;
    }

    /**
     * Tells whether an operation may change the list of the steps (titles, order, initial and final flags, transitions): the operations on the displays
     * of the current step do not, so the list is not rendered again for them
     *
     * @param request
     *            The HTTP request
     * @param nIdStepCurrent
     *            The step shown in the canvas
     * @return false for an operation that only changes displays of the current step
     */
    private boolean stepsMayChange( HttpServletRequest request, int nIdStepCurrent )
    {
        String strOperation = request.getParameter( PARAMETER_OPERATION );
        if ( OPERATION_DUPLICATE.equals( strOperation ) || OPERATION_REMOVE.equals( strOperation ) || OPERATION_PLACE_NEW.equals( strOperation ) )
        {
            return false;
        }
        return !( OPERATION_MOVE.equals( strOperation )
                && NumberUtils.toInt( request.getParameter( PARAMETER_ID_STEP_TARGET ), FormsConstants.DEFAULT_ID_VALUE ) == nIdStepCurrent );
    }

    /**
     * Completes the result with the refreshed list of the steps, the canvas of the step to show and the highest display identifier
     *
     * @param request
     *            The HTTP request
     * @param form
     *            The form, null when it does not exist
     * @param result
     *            The result to complete; its step is replaced by the initial step when it is not a step of the form anymore
     * @param locale
     *            The locale
     * @param bWithSteps
     *            true to render the list of the steps too
     */
    private void fillFragments( HttpServletRequest request, Form form, FormBuilderResult result, Locale locale, boolean bWithSteps )
    {
        if ( form == null )
        {
            return;
        }

        Step step = findCurrentStep( form, result.getIdStep( ) );
        result.setIdStep( step != null ? step.getId( ) : 0 );
        if ( bWithSteps )
        {
            result.setStepsHtml( _formBuilderRenderer.renderSteps( form, step, isTransitionOrder( request ), locale ) );
        }
        result.setCanvasHtml( _formBuilderRenderer.renderCanvas( request, form, step, locale ) );
        result.setMaxIdDisplay( _formBuilderService.getMaxIdDisplay( form.getId( ) ) );
    }

    /**
     * Returns the requested step when it belongs to the form, otherwise the initial step of the form (the first created when there is none)
     *
     * @param form
     *            The form
     * @param nIdStep
     *            The requested step
     * @return the step to show, null if the form has no step
     */
    private Step findCurrentStep( Form form, int nIdStep )
    {
        Step step = StepHome.findByPrimaryKey( nIdStep );
        if ( step != null && step.getIdForm( ) == form.getId( ) )
        {
            return step;
        }

        return _formBuilderRenderer.getDefaultStep( form.getId( ) );
    }

    /**
     * Tells whether the list of the steps is shown in transition order rather than in creation order
     *
     * @param request
     *            The HTTP request
     * @return true when the request asks for the transition order
     */
    private boolean isTransitionOrder( HttpServletRequest request )
    {
        return STEP_ORDER_TRANSITIONS.equals( request.getParameter( PARAMETER_STEP_ORDER ) );
    }

    /**
     * Checks that the user may modify the form: RBAC permission and workgroup
     *
     * @param form
     *            The form
     * @param request
     *            The HTTP request
     * @throws AccessDeniedException
     *             if the user cannot modify the form
     */
    private void checkFormPermission( Form form, HttpServletRequest request ) throws AccessDeniedException
    {
        checkUserPermission( Form.RESOURCE_TYPE, String.valueOf( form.getId( ) ), FormsResourceIdService.PERMISSION_MODIFY, request, null );
        checkWorkgroupPermission( form, request );
    }

    /**
     * Writes the result as JSON to the response of the current request (taken from the request context: the MVC parameter binder only provides the
     * request to the controller methods) and closes the writer, so that nothing the JSP adds afterwards reaches the
     * client
     *
     * @param result
     *            The result
     */
    private void writeJson( FormBuilderResult result )
    {
        HttpServletResponse response = LocalVariables.getResponse( );
        response.setContentType( JsonUtil.CONTENT_TYPE_JSON );
        response.setCharacterEncoding( "UTF-8" );
        try ( PrintWriter writer = response.getWriter( ) )
        {
            writer.write( JsonUtil.serialize( result ) );
        }
        catch( JsonProcessingException e )
        {
            AppLogService.getLogger( ).error( "Form builder : unable to serialize the result", e );
        }
        catch( IOException e )
        {
            AppLogService.getLogger( ).error( "Form builder : unable to write the result", e );
        }
    }
}
