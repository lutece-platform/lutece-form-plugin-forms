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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.service.FormsResourceIdService;
import fr.paris.lutece.plugins.forms.service.IFormsMultiviewAuthorizationService;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.web.StepDisplayTree;
import fr.paris.lutece.plugins.forms.web.form.response.view.FormResponseViewModelProcessorFactory;
import fr.paris.lutece.plugins.forms.web.form.response.view.IFormResponseViewModelProcessor;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.url.UrlItem;

/**
 * Jsp Bean associated to the page which display the details of a form response
 */
@Controller( controllerJsp = "ManageDirectoryFormResponseDetails.jsp", controllerPath = "jsp/admin/plugins/forms/", right = "FORMS_MULTIVIEW" )
public class MultiviewFormResponseDetailsJspBean extends AbstractJspBean
{
    // Rights
    public static final String RIGHT_FORMS_MULTIVIEW = "FORMS_MULTIVIEW";

    // Generated serial UID
    private static final long serialVersionUID = 3673744119212180461L;

    // JSP path
    private static final String CONTROLLER_JSP_NAME_WITH_PATH = "jsp/admin/plugins/forms/ManageDirectoryFormResponseDetails.jsp";

    // Templates
    private static final String TEMPLATE_VIEW_FORM_RESPONSE = "admin/plugins/forms/multiview/view_form_response.html";

    // Views
    private static final String VIEW_FORM_RESPONSE_DETAILS = "view_form_response_details";

    // Parameters
    private static final String PARAMETER_ID_FORM_RESPONSE = "id_form_response";
    private static final String PARAMETER_BACK_FROM_ACTION = "back_form_action";

    // Marks
    private static final String MARK_LIST_FILTER_VALUES = "list_filter_values";
    private static final String MARK_FORM = "form";
    private static final String MARK_FORM_RESPONSE = "form_response";
    private static final String MARK_LIST_MULTIVIEW_STEP_DISPLAY = "list_multiview_step_display";

    // Messages
    private static final String MESSAGE_ACCESS_DENIED = "Acces denied";
    private static final String MESSAGE_MULTIVIEW_FORM_RESPONSE_TITLE = "forms.multiviewForms.pageTitle";

    // Variables
    private Map<String, String> _mapFilterValues = new LinkedHashMap<>( );
    private final transient IFormsMultiviewAuthorizationService _formsMultiviewAuthorizationService = SpringContextService
            .getBean( IFormsMultiviewAuthorizationService.BEAN_NAME );

    /**
     * Return the page with the details of a form response
     * 
     * @param request
     *            The request used the retrieve the values of the selected parameters
     * @return the page with the details of the form response
     * @throws AccessDeniedException
     *             if the user is not authorize to access the details of the form response
     */
    @View( value = VIEW_FORM_RESPONSE_DETAILS, defaultView = true )
    public String getRecordDetails( HttpServletRequest request ) throws AccessDeniedException
    {
        int nIdFormResponse = NumberUtils.toInt( request.getParameter( PARAMETER_ID_FORM_RESPONSE ), NumberUtils.INTEGER_MINUS_ONE );
        FormResponse formResponse = FormResponseHome.findByPrimaryKey( nIdFormResponse );

        boolean bRBACAuthorization = RBACService.isAuthorized( Form.RESOURCE_TYPE, Integer.toString( nIdFormResponse ),
                FormsResourceIdService.PERMISSION_VIEW_FORM_RESPONSE, getUser( ) );
        boolean bAuthorizedRecord = _formsMultiviewAuthorizationService.isUserAuthorizedOnFormResponse( request, nIdFormResponse );

        if ( formResponse == null || !bRBACAuthorization || !bAuthorizedRecord )
        {
            throw new AccessDeniedException( MESSAGE_ACCESS_DENIED );
        }

        // Build the base model for the page of the details of a FormResponse
        Map<String, Object> model = buildFormResponseDetailsModel( formResponse );

        // Build the model of all ModelProcessors
        FormResponseViewModelProcessorFactory formResponseViewModelProcessorFactory = new FormResponseViewModelProcessorFactory( );
        List<IFormResponseViewModelProcessor> listFormResponseViewModelProcesor = formResponseViewModelProcessorFactory
                .buildFormResponseViewModelProcessorList( );
        if ( !CollectionUtils.isEmpty( listFormResponseViewModelProcesor ) )
        {
            Locale locale = getLocale( );
            for ( IFormResponseViewModelProcessor formResponseViewModelProcessor : listFormResponseViewModelProcesor )
            {
                formResponseViewModelProcessor.populateModel( request, model, nIdFormResponse, locale );
            }
        }

        // Fill the map which store the values of all filters and search previously selected if we are not coming from an action
        if ( request.getParameter( PARAMETER_BACK_FROM_ACTION ) == null )
        {
            _mapFilterValues = fillFilterMapValues( request );
        }
        populateModelWithFilterValues( _mapFilterValues, model );

        return getPage( MESSAGE_MULTIVIEW_FORM_RESPONSE_TITLE, TEMPLATE_VIEW_FORM_RESPONSE, model );
    }

    /**
     * Build the model of the page which display the details of a FormResponse
     * 
     * @param formResponse
     *            The FormResponse on which the model must be built
     * @return the model associate for the details of the given FormResponse
     */
    private Map<String, Object> buildFormResponseDetailsModel( FormResponse formResponse )
    {
        Form form = FormHome.findByPrimaryKey( formResponse.getFormId( ) );

        Map<String, Object> mapFormResponseDetailsModel = new HashMap<>( );
        mapFormResponseDetailsModel.put( MARK_FORM_RESPONSE, formResponse );
        mapFormResponseDetailsModel.put( MARK_FORM, form );

        // [TODO] - Retrieve the list of Step which correspond to the selected path
        // of the user who is associated to the given FormResponse rather than retrieve
        // the list of all Step associated to the Form of the given FormResponse when
        // it will be ready
        List<Step> listStep = StepHome.getStepsListByForm( form.getId( ) );

        List<String> listStepDisplayTree = buildFormStepDisplayTreeList( listStep, formResponse.getId( ) );
        mapFormResponseDetailsModel.put( MARK_LIST_MULTIVIEW_STEP_DISPLAY, listStepDisplayTree );

        return mapFormResponseDetailsModel;
    }

    /**
     * Return the list of all DisplayTree for the given list of Step
     * 
     * @param listStep
     *            The list of all Step on which the DisplayTree must be build
     * @param nIdFormResponse
     *            The identifier of FormResponse on which to retrieve the Response objects
     * @return the list of all DisplayTree for the given list of Step
     */
    private List<String> buildFormStepDisplayTreeList( List<Step> listStep, int nIdFormResponse )
    {
        List<String> listFormDisplayTrees = new ArrayList<>( );

        if ( !CollectionUtils.isEmpty( listStep ) )
        {
            for ( Step step : listStep )
            {
                int nIdStep = step.getId( );
                boolean bIsForEdition = Boolean.FALSE;

                StepDisplayTree stepDisplayTree = new StepDisplayTree( nIdStep );
                stepDisplayTree.setResponses( buildStepMapResponse( nIdStep, nIdFormResponse ) );
                listFormDisplayTrees.add( stepDisplayTree.getCompositeHtml( getLocale( ), bIsForEdition ) );
            }
        }

        return listFormDisplayTrees;
    }

    /**
     * Return the map which associate for each Question of a Step its List of Response for the given FormResponse
     * 
     * @param nIdStep
     *            The identifier of the Step to retrieve the Question from
     * @param nIdFormResponse
     *            The identifier of the FormResponse on which to retrieve the Response from
     * @return the map which associate for each Question of a Step its List of Response for the given FormResponse
     */
    private Map<Integer, List<Response>> buildStepMapResponse( int nIdStep, int nIdFormResponse )
    {
        Map<Integer, List<Response>> mapStepResponses = new LinkedHashMap<>( );

        List<Question> listQuestions = QuestionHome.getQuestionsListByStep( nIdStep );
        if ( !CollectionUtils.isEmpty( listQuestions ) )
        {
            for ( Question question : listQuestions )
            {
                int nIdQuestion = question.getId( );
                mapStepResponses.put( nIdQuestion, buildQuestionResponseList( nIdFormResponse, nIdQuestion ) );
            }
        }

        return mapStepResponses;
    }

    /**
     * Return the list of all Response for the given iFormResponse for the given Question
     * 
     * @param nIdFormResponse
     *            The identifier of the FormResponse on which to retrieve the list of Response
     * @param nIdQuestion
     *            The identifier of the Question on which to retrieve the list of Response from
     * @return the list of all Response for the given iFormResponse for the given Question
     */
    private List<Response> buildQuestionResponseList( int nIdFormResponse, int nIdQuestion )
    {
        List<Response> listResponseQuestions = new ArrayList<>( );

        List<FormQuestionResponse> formQuestionResponses = FormQuestionResponseHome
                .getFormQuestionResponseListByResponseQuestion( nIdFormResponse, nIdQuestion );
        if ( !CollectionUtils.isEmpty( formQuestionResponses ) )
        {
            for ( FormQuestionResponse formQuestionResponse : formQuestionResponses )
            {
                listResponseQuestions.addAll( formQuestionResponse.getEntryResponse( ) );
            }
        }

        return listResponseQuestions;
    }

    /**
     * Fill the map which contains the values of all filters with the data of the request
     * 
     * @param request
     *            The request used to retrieve the values of the parameters of the filters
     * @return the map which associate for each filter parameter its value
     */
    private Map<String, String> fillFilterMapValues( HttpServletRequest request )
    {
        Map<String, String> mapFilterValues = new LinkedHashMap<>( );

        Set<String> setFilterParameterName = new LinkedHashSet<>( );
        Enumeration<String> enumerationParameterName = request.getParameterNames( );

        if ( enumerationParameterName != null )
        {
            List<String> listFilterParameterName = Collections.list( enumerationParameterName );
            setFilterParameterName = listFilterParameterName.stream( )
                    .filter( strParameterName -> strParameterName.startsWith( FormsConstants.PARAMETER_URL_FILTER_PREFIX ) ).collect( Collectors.toSet( ) );
        }

        if ( !CollectionUtils.isEmpty( setFilterParameterName ) )
        {
            for ( String strFilterParameterName : setFilterParameterName )
            {
                mapFilterValues.put( strFilterParameterName.split( FormsConstants.PARAMETER_URL_FILTER_PREFIX ) [1],
                        request.getParameter( strFilterParameterName ) );
            }
        }

        String strSelectedTechnicalCode = request.getParameter( FormsConstants.PARAMETER_SELECTED_PANEL );
        if ( !StringUtils.isBlank( strSelectedTechnicalCode ) )
        {
            mapFilterValues.put( FormsConstants.PARAMETER_CURRENT_SELECTED_PANEL, strSelectedTechnicalCode );
        }

        if ( request.getParameter( FormsConstants.PARAMETER_SORT_COLUMN_POSITION ) != null )
        {
            addSortConfigParameterValues( request );
        }

        return mapFilterValues;
    }

    /**
     * Fill the map which contains the values of all filters with informations of the sort to use
     * 
     * @param request
     *            The request to use to retrieve the value of the sort
     */
    private void addSortConfigParameterValues( HttpServletRequest request )
    {
        String strPositionToSort = request.getParameter( FormsConstants.PARAMETER_SORT_COLUMN_POSITION );
        String strAttributeName = request.getParameter( FormsConstants.PARAMETER_SORT_ATTRIBUTE_NAME );
        String strAscSort = request.getParameter( FormsConstants.PARAMETER_SORT_ASC_VALUE );

        _mapFilterValues.put( FormsConstants.PARAMETER_SORT_COLUMN_POSITION, strPositionToSort );
        _mapFilterValues.put( FormsConstants.PARAMETER_SORT_ATTRIBUTE_NAME, strAttributeName );
        _mapFilterValues.put( FormsConstants.PARAMETER_SORT_ASC_VALUE, strAscSort );
    }

    /**
     * Populate the given model with the data associated to the filters from the request
     * 
     * @param mapFilterNameValues
     *            The map which contains the name of all parameters used to filter and their values
     * @param model
     *            The given model to populate
     */
    private void populateModelWithFilterValues( Map<String, String> mapFilterNameValues, Map<String, Object> model )
    {
        if ( !MapUtils.isEmpty( mapFilterNameValues ) )
        {
            ReferenceList referenceListFilterValues = new ReferenceList( );

            for ( Map.Entry<String, String> entryFilterNameValue : mapFilterNameValues.entrySet( ) )
            {
                ReferenceItem referenceItem = new ReferenceItem( );
                referenceItem.setCode( entryFilterNameValue.getKey( ) );
                referenceItem.setName( entryFilterNameValue.getValue( ) );

                referenceListFilterValues.add( referenceItem );
            }

            model.put( MARK_LIST_FILTER_VALUES, referenceListFilterValues );
        }
    }

    /**
     * Return the default view base url for the MultiviewFormResponseDetailsJspBean
     * 
     * @return the default view base url for the MultiviewFormResponseDetailsJspBean
     */
    protected static String getMultiviewRecordDetailsBaseUrl( )
    {
        UrlItem urlRecordDetailsBase = new UrlItem( CONTROLLER_JSP_NAME_WITH_PATH );
        urlRecordDetailsBase.addParameter( MVCUtils.PARAMETER_VIEW, VIEW_FORM_RESPONSE_DETAILS );

        return urlRecordDetailsBase.getUrl( );
    }
}
