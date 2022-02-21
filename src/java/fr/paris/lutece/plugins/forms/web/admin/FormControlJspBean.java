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

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.ControlType;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.Group;
import fr.paris.lutece.plugins.forms.business.GroupHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.business.Transition;
import fr.paris.lutece.plugins.forms.business.TransitionHome;
import fr.paris.lutece.plugins.forms.service.EntryServiceManager;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.validation.ControlListenerManager;
import fr.paris.lutece.plugins.forms.validation.IValidator;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.AbstractPaginator;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class provides the user interface to manage Form controls ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageControls.jsp", controllerPath = "jsp/admin/plugins/forms/", right = "FORMS_MANAGEMENT" )
public class FormControlJspBean extends AbstractJspBean
{

    private static final long serialVersionUID = -9023450166890042022L;

    private static final String TEMPLATE_MANAGE_CONTROL = "/admin/plugins/forms/manage_control.html";
    private static final String TEMPLATE_MODIFY_TRANSITION_CONTROL = "/admin/plugins/forms/modify_transition_control.html";
    private static final String TEMPLATE_MODIFY_CONDITION_CONTROL = "/admin/plugins/forms/modify_condition_control.html";
    private static final String TEMPLATE_MODIFY_QUESTION_CONTROL = "/admin/plugins/forms/modify_question_control.html";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MODIFY_CONTROL = "forms.modify_control.pageTitle";
    private static final String PROPERTY_ITEM_PER_PAGE = "forms.itemsPerPage";

    // Validations
    private static final String CONTROL_VALIDATION_ATTRIBUTES_PREFIX = "forms.model.entity.control.attribute.";

    // Views
    private static final String VIEW_MANAGE_CONTROL = "manageControl";
    private static final String VIEW_MODIFY_CONTROL = "modifyControl";
    private static final String VIEW_MODIFY_CONDITION_CONTROL = "modifyConditionControl";
    private static final String VIEW_CONFIRM_REMOVE_CONTROL = "confirmRemoveControl";

    // Actions
    private static final String ACTION_MODIFY_CONTROL = "modifyControl";
    private static final String ACTION_REMOVE_CONTROL = "removeControl";
    private static final String ACTION_CANCEL_AND_RETURN = "cancelAndReturn";

    // Infos
    private static final String INFO_CONTROL_CREATED = "forms.info.control.created";
    private static final String INFO_CONTROL_UPDATED = "forms.info.control.updated";
    private static final String INFO_CONTROL_REMOVED = "forms.info.control.removed";
    private static final String MESSAGE_CONFIRM_REMOVE_CONTROL = "forms.message.confirmRemoveControl";
    private static final String INFO_CONDITION_GROUP_TITLE = "forms.modify_condition_group_control.title";
    private static final String INFO_CONDITION_QUESTION_TITLE = "forms.modify_condition_question_control.title";

    // Errors
    private static final String ERROR_CONTROL_REMOVED = "forms.error.deleteControl";
    private static final String ERROR_QUESTION_VALIDATOR_MATCH = "forms.error.control.validatorMatch";
    private static final String ERROR_VALIDATOR_VALUE_MATCH = "forms.error.control.valueMatch";

    // Markers
    private static final String MARK_LIST_CONTROL = "control_list";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";

    private static final String PARAMETER_PAGE_INDEX = "page_index";

    // Session variable to store working values
    private int _nIdTarget;
    private Question _question;
    private Group _group;
    private Control _control;
    private Step _step;
    private String _strControlTemplate;
    private String _strControlTitle;
    private ControlType _controlType;

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
    @View( value = VIEW_MANAGE_CONTROL, defaultView = true )
    public String getManageControl( HttpServletRequest request )
    {
        clearAttributes( );
        retrieveParameters( request );

        if ( _step == null || _controlType == null )
        {
            return redirectToViewManageForm( request );
        }

        List<Control> listControl = ControlHome.getControlByControlTargetAndType( _nIdTarget, _controlType );

        LocalizedPaginator<Control> paginator = new LocalizedPaginator<>( listControl, _nItemsPerPage, getJspManageForm( request ), PARAMETER_PAGE_INDEX,
                _strCurrentPageIndex, getLocale( ) );

        _strCurrentPageIndex = AbstractPaginator.getPageIndex( request, AbstractPaginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nItemsPerPage = AbstractPaginator.getItemsPerPage( request, AbstractPaginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage, _nDefaultItemsPerPage );

        Map<String, Object> model = getModel( );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, StringUtils.EMPTY + _nItemsPerPage );

        model.put( FormsConstants.PARAMETER_CONTROL_TYPE, _controlType.name( ) );
        model.put( FormsConstants.MARK_VALIDATOR_MANAGER, EntryServiceManager.getInstance( ) );
        model.put( FormsConstants.MARK_QUESTION, _question );
        model.put( FormsConstants.MARK_STEP, _step );
        model.put( MARK_LIST_CONTROL, listControl );

        Locale locale = getLocale( );
        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_CONTROL, locale, model );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Set the retrieved parameters
     * 
     * @param request
     *            The http request
     */
    private void retrieveParameters( HttpServletRequest request )
    {
        int nIdStep = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), FormsConstants.DEFAULT_ID_VALUE );
        _step = StepHome.findByPrimaryKey( nIdStep );

        String strControlType = request.getParameter( FormsConstants.PARAMETER_CONTROL_TYPE );
        _controlType = ControlType.valueOf( strControlType );

        _nIdTarget = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_TARGET ), FormsConstants.DEFAULT_ID_VALUE );
    }

    /**
     * 
     * @param request
     *            The http request
     */
    private void initControl( HttpServletRequest request )
    {
        int nIdControl = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_CONTROL ), FormsConstants.DEFAULT_ID_VALUE );

        _control = ControlHome.findByPrimaryKey( nIdControl );

        if ( _control == null )
        {
            _control = new Control( );
            _control.setControlType( _controlType.getLabel( ) );
            _control.setIdControlTarget( _nIdTarget );

            if ( _controlType.equals( ControlType.VALIDATION ) )
            {
                Set<Integer> listQuestion = new HashSet<>( );
                listQuestion.add( _nIdTarget );
                _control.setListIdQuestion( listQuestion );
            }
        }
        else
        {
            Question question = QuestionHome.findByPrimaryKey( _control.getListIdQuestion( ).iterator( ).next( ) );
            _step = StepHome.findByPrimaryKey( question.getIdStep( ) );
        }

        if ( _controlType.equals( ControlType.TRANSITION ) )
        {
            _strControlTemplate = TEMPLATE_MODIFY_TRANSITION_CONTROL;
        }
        else
            if ( _controlType.equals( ControlType.VALIDATION ) )
            {
                _strControlTemplate = TEMPLATE_MODIFY_QUESTION_CONTROL;
            }

    }

    /**
     * Returns the form to modify a control for conditionnal question
     *
     * @param request
     *            The Http request
     * @return the html code of the control form
     */
    @View( VIEW_MODIFY_CONDITION_CONTROL )
    public String getModifyConditionControl( HttpServletRequest request )
    {
        _controlType = ControlType.CONDITIONAL;

        int nIdStep = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), FormsConstants.DEFAULT_ID_VALUE );

        _step = StepHome.findByPrimaryKey( nIdStep );

        if ( _step == null )
        {
            return redirectToViewManageForm( request );
        }

        int nIdCompositeDisplay = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY ), FormsConstants.DEFAULT_ID_VALUE );

        FormDisplay formDisplay = FormDisplayHome.findByPrimaryKey( nIdCompositeDisplay );

        if ( formDisplay == null )
        {
            return redirectToViewManageForm( request );
        }

        if ( formDisplay.getCompositeType( ).equals( FormsConstants.COMPOSITE_QUESTION_TYPE ) )
        {
            _question = QuestionHome.findByPrimaryKey( formDisplay.getCompositeId( ) );

            if ( _question == null )
            {
                return redirectToViewManageForm( request );
            }

            Object [ ] args = {
                    _question.getTitle( ),
            };

            _strControlTitle = I18nService.getLocalizedString( INFO_CONDITION_QUESTION_TITLE, args, request.getLocale( ) );
        }
        else
            if ( formDisplay.getCompositeType( ).equals( FormsConstants.COMPOSITE_GROUP_TYPE ) )
            {
                _group = GroupHome.findByPrimaryKey( formDisplay.getCompositeId( ) );

                if ( _group == null )
                {
                    return redirectToViewManageForm( request );
                }

                Object [ ] args = {
                        _group.getTitle( ),
                };

                _strControlTitle = I18nService.getLocalizedString( INFO_CONDITION_GROUP_TITLE, args, request.getLocale( ) );
            }

        List<Control> listConditionalControl = ControlHome.getControlByControlTargetAndType( nIdCompositeDisplay, _controlType );

        if ( !listConditionalControl.isEmpty( ) )
        {
            _control = listConditionalControl.get( 0 );
        }
        else
            if ( _control != null )
            {

                _control.setListIdQuestion( null );
            }

        if ( _control == null || _control.getListIdQuestion( ) == null || _control.getListIdQuestion( ).isEmpty( ) )
        {
            _control = new Control( );
            _control.setIdControlTarget( nIdCompositeDisplay );
            _control.setControlType( ControlType.CONDITIONAL.getLabel( ) );
        }
        else
        {
            Question question = QuestionHome.findByPrimaryKey( _control.getListIdQuestion( ).iterator( ).next( ) );
            _step = StepHome.findByPrimaryKey( question.getIdStep( ) );
        }

        _strControlTemplate = TEMPLATE_MODIFY_CONDITION_CONTROL;

        return redirectView( request, VIEW_MODIFY_CONTROL );
    }

    /**
     * Returns the form to modify a control
     *
     * @param request
     *            The Http request
     * @return the html code of the control form
     */
    @View( VIEW_MODIFY_CONTROL )
    public String getModifyControl( HttpServletRequest request )
    {
        if ( _step == null )
        {
            int nIdStep = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), FormsConstants.DEFAULT_ID_VALUE );

            _step = StepHome.findByPrimaryKey( nIdStep );
        }

        if ( _controlType != ControlType.CONDITIONAL && _control == null )
        {
            initControl( request );
        }

        if ( _control == null && !retrieveControlFromRequest( request ) )
        {
            return redirectToViewManageForm( request );
        }

        Map<String, Object> model = getModel( );

        if ( _control != null )
        {
            buildControlModel( request, model );
        }
        else
        {
            redirectToViewManageForm( request );
        }

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_CONTROL, _strControlTemplate, model );
    }

    /**
     * Build the model for Create and Modify Control views
     * 
     * @param request
     *            the Http request
     * @param model
     *            the Model
     */
    private void buildControlModel( HttpServletRequest request, Map<String, Object> model )
    {
        String strValidatorName = request.getParameter( FormsConstants.PARAMETER_VALIDATOR_NAME );
        String valSubmit = request.getParameter( FormsConstants.PARAMETER_VIEW_MODIFY_CONTROL );
        int idStep = _step.getId( );
        if ( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) != null )
        {
            idStep = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );
        }

        boolean bStepChanged = false;
        if ( valSubmit != null && valSubmit.equals( FormsConstants.VALIDATE_STEP ) )
        {
            bStepChanged = true;
        }

        ReferenceList referenceListQuestion = new ReferenceList( );
        for ( Question question : QuestionHome.getQuestionsListByStep( idStep ) )
        {

            referenceListQuestion.addItem( question.getId( ), question.getTitle( ) );
        }

        if ( StringUtils.isNotEmpty( strValidatorName ) && !strValidatorName.equals( _control.getValidatorName( ) ) )
        {
            _control.setValidatorName( strValidatorName );
            _control.setValue( StringUtils.EMPTY );
        }

        int nIdQuestion;

        if ( bStepChanged && !referenceListQuestion.isEmpty( ) )
        {
            _control.setListIdQuestion( null );
            nIdQuestion = FormsConstants.DEFAULT_ID_VALUE;
        }
        else
            if ( valSubmit != null && valSubmit.equals( FormsConstants.VALIDATE_VALIDATOR ) )
            {

                nIdQuestion = FormsConstants.DEFAULT_ID_VALUE;
            }
            else
            {
                nIdQuestion = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_QUESTION ), FormsConstants.DEFAULT_ID_VALUE );
            }

        String removeQuestion = request.getParameter( FormsConstants.VAL_REMOVE_QUESTION );
        if ( removeQuestion != null && removeQuestion.equals( FormsConstants.VAL_REMOVE_QUESTION ) )
        {
            String idQuestionToRemove = request.getParameter( FormsConstants.PARAMETER_ID_QUESTION_TO_REMOVE );
            Set<Integer> listIdQuestion = _control.getListIdQuestion( );
            listIdQuestion.removeIf( p -> p.equals( Integer.parseInt( idQuestionToRemove ) ) );
            _control.setListIdQuestion( listIdQuestion );
        }

        if ( nIdQuestion != FormsConstants.DEFAULT_ID_VALUE
                && ( _control.getListIdQuestion( ) == null || _control.getListIdQuestion( ).stream( ).noneMatch( p -> p.equals( nIdQuestion ) ) ) )
        {

            Set<Integer> listIdQuestion = ( _control.getListIdQuestion( ) != null ) ? _control.getListIdQuestion( ) : new HashSet<>( );
            listIdQuestion.add( nIdQuestion );
            _control.setListIdQuestion( listIdQuestion );

            _control.setValidatorName( StringUtils.EMPTY );
            _control.setValue( StringUtils.EMPTY );
        }

        if ( _control.getListIdQuestion( ) != null && !_control.getListIdQuestion( ).isEmpty( ) )
        {
            Question q = QuestionHome.findByPrimaryKey( _control.getListIdQuestion( ).iterator( ).next( ) );
            EntryType ent = q.getEntry( ).getEntryType( );

            ReferenceList refListAvailableValidator = EntryServiceManager.getInstance( ).getRefListAvailableValidator( ent );
            for ( int idQuest : _control.getListIdQuestion( ) )
            {
                Question question = QuestionHome.findByPrimaryKey( idQuest );
                if ( question != null && question.getEntry( ) != null )
                {
                    EntryType entryType = question.getEntry( ).getEntryType( );
                    ReferenceList refListAvailableValidatorTemp = EntryServiceManager.getInstance( ).getRefListAvailableValidator( entryType );
                    ReferenceList refListAvailTemp = new ReferenceList( );
                    for ( ReferenceItem refList : refListAvailableValidatorTemp )
                    {
                        if ( refListAvailableValidator.stream( ).anyMatch( p -> p.getCode( ).equals( refList.getCode( ) ) ) )
                        {
                            refListAvailTemp.add( refList );
                        }
                    }
                    refListAvailableValidator = refListAvailTemp;
                }
            }
            model.put( FormsConstants.MARK_AVAILABLE_VALIDATORS, refListAvailableValidator );

            if ( CollectionUtils.isNotEmpty( refListAvailableValidator ) && StringUtils.EMPTY.equals( _control.getValidatorName( ) ) )
            {
                _control.setValidatorName( refListAvailableValidator.get( 0 ).getCode( ) );
            }

        }

        String strValidatorTemplate = StringUtils.EMPTY;

        if ( StringUtils.isNotEmpty( _control.getValidatorName( ) ) )
        {
            IValidator validator = EntryServiceManager.getInstance( ).getValidator( _control.getValidatorName( ) );
            strValidatorTemplate = validator.getDisplayHtml( _control );
        }

        model.put( FormsConstants.MARK_QUESTION, _question );
        model.put( FormsConstants.MARK_STEP, _step );
        model.put( FormsConstants.MARK_CONTROL_TEMPLATE, strValidatorTemplate );
        model.put( FormsConstants.MARK_CONTROL, _control );
        model.put( FormsConstants.MARK_ID_STEP, idStep );
        model.put( FormsConstants.MARK_QUESTION_LIST, referenceListQuestion );
        model.put( FormsConstants.MARK_AVAILABLE_STEPS, StepHome.getStepReferenceListByForm( _step.getIdForm( ) ) );
        model.put( FormsConstants.MARK_CONDITION_TITLE, _strControlTitle );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_CONTROL ) );
    }

    /**
     * Process the data modification of a control
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException 
     */
    @Action( ACTION_MODIFY_CONTROL )
    public String doModifyControl( HttpServletRequest request ) throws AccessDeniedException
    {
        // CSRF Token control
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_CONTROL ) )
        {
            throw new AccessDeniedException( MESSAGE_ERROR_TOKEN );
        }
        if ( !populateAndValidateControl( request ) )
        {
            return redirectView( request, VIEW_MODIFY_CONTROL );
        }

        if ( _control.getId( ) > 0 )
        {
            ControlHome.update( _control );
            ControlListenerManager.notifyListenersControlUpdated( _control, request );
            request.setAttribute( FormsConstants.PARAMETER_INFO_KEY, INFO_CONTROL_UPDATED );
        }
        else
        {
            ControlHome.create( _control );
            ControlListenerManager.notifyListenersControlCreated( _control, request );
            request.setAttribute( FormsConstants.PARAMETER_INFO_KEY, INFO_CONTROL_CREATED );
        }

        return redirect( request, getControlReturnUrl( request ) );
    }

    /**
     * Manages the removal form of a Control whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @View( VIEW_CONFIRM_REMOVE_CONTROL )
    public String getConfirmRemoveControl( HttpServletRequest request )
    {
        int nIdControlToRemove = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_CONTROL ), FormsConstants.DEFAULT_ID_VALUE );

        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_CONTROL ) );
        url.addParameter( FormsConstants.PARAMETER_ID_CONTROL, nIdControlToRemove );
        url.addParameter(  SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_REMOVE_CONTROL ) );
        
        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_CONTROL, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

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
    @Action( ACTION_REMOVE_CONTROL )
    public String doRemoveControl( HttpServletRequest request ) throws AccessDeniedException
    {
        // CSRF Token control
        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_REMOVE_CONTROL ) )
        {
            throw new AccessDeniedException( MESSAGE_ERROR_TOKEN );
        }

        if ( !retrieveControlFromRequest( request ) )
        {
            return redirectToViewManageForm( request );
        }

        int nIdControlToRemove = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_CONTROL ), FormsConstants.DEFAULT_ID_VALUE );

        _control = ControlHome.findByPrimaryKey( nIdControlToRemove );

        if ( _control != null )
        {
            ControlHome.remove( _control.getId( ) );
            ControlListenerManager.notifyListenersControlRemoval( _control, request );
            request.setAttribute( FormsConstants.PARAMETER_INFO_KEY, INFO_CONTROL_REMOVED );
        }
        else
        {
            addError( ERROR_CONTROL_REMOVED, getLocale( ) );
            return redirectToViewManageForm( request );
        }

        return redirect( request, getControlReturnUrl( request ) );
    }

    /**
     * Manages the removal form of a Control whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CANCEL_AND_RETURN )
    public String getReturnUrl( HttpServletRequest request )
    {
        return redirect( request, getControlReturnUrl( request ) );
    }

    /**
     * Retrieve the control object from request parameter
     * 
     * @param request
     *            The request
     * 
     * @return false if an error occurred, true otherwise
     */
    private boolean retrieveControlFromRequest( HttpServletRequest request )
    {
        boolean bSuccess = true;
        int nIdControl = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_CONTROL ), FormsConstants.DEFAULT_ID_VALUE );

        if ( nIdControl == FormsConstants.DEFAULT_ID_VALUE )
        {
            bSuccess = false;
        }

        if ( _control == null || _control.getId( ) != nIdControl )
        {
            _control = ControlHome.findByPrimaryKey( nIdControl );
        }
        return bSuccess;
    }

    /**
     * Validate the Control field values
     * 
     * @param request
     *            the Http request
     * @return True if the control is valid
     */
    private boolean populateAndValidateControl( HttpServletRequest request )
    {
        if ( CollectionUtils.isNotEmpty( _control.getListIdQuestion( ) ) && StringUtils.isNotEmpty( _control.getValidatorName( ) ) )
        {
            Set<Integer> listIdQuestion = ( _control.getListIdQuestion( ) != null ) ? _control.getListIdQuestion( ) : new HashSet<>( );
            for ( int nIdQuest : listIdQuestion )
            {
                Question question = QuestionHome.findByPrimaryKey( nIdQuest );
                if ( question.getEntry( ) != null )
                {
                    List<IValidator> listValidator = EntryServiceManager.getInstance( ).getListAvailableValidator( question.getEntry( ).getEntryType( ) );
                    IValidator controlValidator = EntryServiceManager.getInstance( ).getValidator( _control.getValidatorName( ) );

                    if ( !listValidator.contains( controlValidator ) )
                    {
                        _control.setValidatorName( StringUtils.EMPTY );
                        _control.setValue( StringUtils.EMPTY );
                        addError( ERROR_QUESTION_VALIDATOR_MATCH, getLocale( ) );
                        return false;
                    }
                }
            }
        }

        String strValidatorName = request.getParameter( FormsConstants.PARAMETER_VALIDATOR_NAME );

        if ( StringUtils.isNotEmpty( _control.getValidatorName( ) ) && !_control.getValidatorName( ).equals( strValidatorName ) )
        {
            addError( ERROR_VALIDATOR_VALUE_MATCH, getLocale( ) );
            _control.setValidatorName( strValidatorName );
            _control.setValue( StringUtils.EMPTY );
            return false;
        }

        populate( _control, request, getLocale( ) );
        return validateBean( _control, CONTROL_VALIDATION_ATTRIBUTES_PREFIX );
    }

    /**
     * Clear all the attributes
     */
    private void clearAttributes( )
    {
        _strControlTemplate = null;
        _step = null;
        _control = null;
        _nIdTarget = 0;
        _question = null;
        _group = null;
        _strControlTitle = null;
        _controlType = null;
    }

    /**
     * 
     * @param request
     *            The Http request
     * @return the return URL string
     */
    private String getControlReturnUrl( HttpServletRequest request )
    {
        String strTargetJsp = StringUtils.EMPTY;
        int nIdStep = 0;

        switch( _controlType )
        {
            case CONDITIONAL:
                strTargetJsp = FormsConstants.JSP_MANAGE_QUESTIONS;
                if ( _question != null )
                {
                    nIdStep = _question.getIdStep( );
                }
                else
                {
                    nIdStep = _group.getIdStep( );
                }
                break;
            case TRANSITION:
                strTargetJsp = FormsConstants.JSP_MANAGE_CONTROLS;
                Transition transition = TransitionHome.findByPrimaryKey( _control.getIdControlTarget( ) );
                nIdStep = transition.getFromStep( );
                break;
            case VALIDATION:
                strTargetJsp = FormsConstants.JSP_MANAGE_QUESTIONS;
                nIdStep = _step.getId( );
                break;
            default:
                break;
        }

        UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + strTargetJsp );
        url.addParameter( FormsConstants.PARAMETER_ID_STEP, nIdStep );

        if ( _nIdTarget > 0 )
        {
            url.addParameter( FormsConstants.PARAMETER_CONTROL_TYPE, _controlType.name( ) );
            url.addParameter( FormsConstants.PARAMETER_ID_TARGET, _nIdTarget );
        }

        String strInfoKey = (String) request.getAttribute( FormsConstants.PARAMETER_INFO_KEY );
        if ( StringUtils.isNotEmpty( strInfoKey ) )
        {
            url.addParameter( FormsConstants.PARAMETER_INFO_KEY, strInfoKey );
        }

        clearAttributes( );

        return url.getUrl( );
    }
}
