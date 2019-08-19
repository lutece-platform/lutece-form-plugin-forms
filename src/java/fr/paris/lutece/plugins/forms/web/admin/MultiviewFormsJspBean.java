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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponseStep;
import fr.paris.lutece.plugins.forms.business.MultiviewConfig;
import fr.paris.lutece.plugins.forms.business.action.GlobalFormsAction;
import fr.paris.lutece.plugins.forms.business.action.GlobalFormsActionHome;
import fr.paris.lutece.plugins.forms.business.form.FormResponseItem;
import fr.paris.lutece.plugins.forms.business.form.FormResponseItemComparator;
import fr.paris.lutece.plugins.forms.business.form.FormResponseItemComparatorConfig;
import fr.paris.lutece.plugins.forms.business.form.column.FormColumnFactory;
import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.filter.FormFilter;
import fr.paris.lutece.plugins.forms.business.form.filter.FormFilterFactory;
import fr.paris.lutece.plugins.forms.business.form.filter.FormFilterForms;
import fr.paris.lutece.plugins.forms.business.form.panel.FormPanel;
import fr.paris.lutece.plugins.forms.business.form.panel.FormPanelFactory;
import fr.paris.lutece.plugins.forms.export.ExportServiceManager;
import fr.paris.lutece.plugins.forms.export.IFormatExport;
import fr.paris.lutece.plugins.forms.service.FormPanelConfigIdService;
import fr.paris.lutece.plugins.forms.service.FormsPlugin;
import fr.paris.lutece.plugins.forms.service.MultiviewFormService;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.web.form.column.display.FormColumnDisplayFactory;
import fr.paris.lutece.plugins.forms.web.form.column.display.IFormColumnDisplay;
import fr.paris.lutece.plugins.forms.web.form.filter.display.FormFilterDisplayFactory;
import fr.paris.lutece.plugins.forms.web.form.filter.display.IFormFilterDisplay;
import fr.paris.lutece.plugins.forms.web.form.multiview.util.FormListPositionComparator;
import fr.paris.lutece.plugins.forms.web.form.multiview.util.FormListTemplateBuilder;
import fr.paris.lutece.plugins.forms.web.form.multiview.util.MultiviewFormUtil;
import fr.paris.lutece.plugins.forms.web.form.panel.display.IFormPanelDisplay;
import fr.paris.lutece.plugins.forms.web.form.panel.display.factory.FormPanelDisplayFactory;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.rbac.RBACResource;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.util.LocalizedDelegatePaginator;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

/**
 * Controller which manage the multiview of responses of all Forms
 */
@Controller( controllerJsp = "MultiviewForms.jsp", controllerPath = "jsp/admin/plugins/forms/", right = "FORMS_MULTIVIEW" )
public class MultiviewFormsJspBean extends AbstractJspBean
{
    // Generated serial UID
    private static final long serialVersionUID = 2122079505317782087L;

    // Actions
    private static final String ACTION_EXPORT_RESPONSES = "doExportResponses";
    private static final String ACTION_SAVE_MULTIVIEW_CONFIG = "doSaveMultiviewConfig";

    // Templates
    private static final String TEMPLATE_FORMS_MULTIVIEW = "admin/plugins/forms/multiview/forms_multiview.html";
    private static final String TEMPLATE_FORMS_MULTIVIEW_CONFIG = "admin/plugins/forms/multiview/forms_multiview_config.html";

    // Properties
    private static final String PROPERTY_FORMS_MULTIVIEW_PAGE_TITLE = "forms.multiviewForms.pageTitle";

    // JSP URL
    private static final String JSP_FORMS_MULTIVIEW = "jsp/admin/plugins/forms/MultiviewForms.jsp";

    // Constants
    private static final String BASE_SORT_URL_PATTERN = JSP_FORMS_MULTIVIEW + "?current_selected_panel=%s";
    private static final String EXPORT_FILE_NAME = "forms.adminFeature.multiviewForms.export.filePath";

    // Views
    private static final String VIEW_MULTIVIEW_FORMS = "view_multiview_forms";
    private static final String VIEW_MULTIVIEW_CONFIG = "view_multiview_config";

    // Parameters
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_FORMAT_EXPORT = "format_export";
    private static final String PARAMETER_DISPLAY_FORMS_TITLE_COLUMN = "display_forms_title_column";

    // Marks
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_FORM_PANEL_LIST = "form_panel_list";
    private static final String MARK_CURRENT_SELECTED_PANEL = "current_selected_panel";
    private static final String MARK_FORM_FILTER_LIST = "form_filter_list";
    private static final String MARK_TABLE_TEMPLATE = "table_template";
    private static final String MARK_LIST_FORMAT_EXPORT = "format_export_list";
    
    // Session variables
    private String _strSelectedPanelTechnicalCode = StringUtils.EMPTY;
    private transient List<IFormColumn> _listFormColumn;
    private transient List<IFormFilterDisplay> _listFormFilterDisplay;
    private transient List<IFormColumnDisplay> _listFormColumnDisplay;
    private transient List<IFormPanelDisplay> _listFormPanelDisplay;
    private transient IFormPanelDisplay _formPanelDisplayActive;
    private transient FormResponseItemComparatorConfig _formResponseItemComparatorConfig;
    private transient String _strFormSelectedValue = StringUtils.EMPTY;
    private transient List<IFormPanelDisplay> _listAuthorizedFormPanelDisplay;

    /**
     * Return the view with the responses of all the forms
     * 
     * @param request
     *            The request on which to retrieve informations
     * @return the view associated to the responses value of all forms
     */
    @View( value = VIEW_MULTIVIEW_FORMS, defaultView = true )
    public String getMultiviewFormsView( HttpServletRequest request )
    {
        // Retrieve the list of all filters, columns and panels if the pagination and the sort are not used
        boolean bIsSessionLost = isSessionLost( );
        if ( isPaginationAndSortNotUsed( request ) || bIsSessionLost )
        {
            initFormRelatedLists( request );
            manageSelectedPanel( );
        }
        _listAuthorizedFormPanelDisplay = _listFormPanelDisplay.stream( )
    			.filter( fpd -> RBACService.isAuthorized( fpd.getFormPanel( ).getFormPanelConfiguration( ), FormPanelConfigIdService.PERMISSION_VIEW, AdminUserService.getAdminUser( request ) ) )
    			.collect( Collectors.toList( ) );

        // Build the Column for the Panel and save their values for the active panel
        LocalizedDelegatePaginator<Integer> paginator = getPaginator();
        int nPageSize = paginator != null ? getPaginator().getItemsPerPage() : 50;
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        int nCurrentPageIndex = Integer.parseInt( _strCurrentPageIndex );
        int nIndexStart = paginator != null ? ( nCurrentPageIndex - 1) * nPageSize : 0;
       
        FormResponseItemComparatorConfig comparatorConfig = buildFormResponseItemComparatorConfiguration( request ); 
        
        buildFormPanelDisplayWithData( request, nIndexStart, nPageSize, comparatorConfig );

        // Build the template of each form filter display
        if ( isPaginationAndSortNotUsed( request ) || bIsSessionLost )
        {
            _listFormFilterDisplay.stream( ).forEach( formFilterDisplay -> formFilterDisplay.buildTemplate( request ) );
            Collections.sort( _listFormFilterDisplay, new FormListPositionComparator( ) );
        }

        // Retrieve the list of all id of form response of the active FormPanelDisplay
        List<Integer> listIdFormResponse = MultiviewFormUtil.getListIdFormResponseOfFormPanel( _formPanelDisplayActive );

        // Build the model
        Map<String, Object> model = getPaginatedListModel( request, Paginator.PARAMETER_PAGE_INDEX, listIdFormResponse, buildPaginatorUrl( ), _formPanelDisplayActive.getFormPanel( ).getTotalFormResponseItemCount( ) );

        // Get the config multiview action if the current admin user is authorized
        GlobalFormsAction multiviewConfigAction = GlobalFormsActionHome.selectGlobalFormActionByCode( FormsConstants.ACTION_FORMS_MANAGE_MULTIVIEW_CONFIG,
                FormsPlugin.getPlugin( ), request.getLocale( ) );

        if ( RBACService.isAuthorized( (RBACResource) multiviewConfigAction, GlobalFormsAction.PERMISSION_PERFORM_ACTION,
                AdminUserService.getAdminUser( request ) ) )
        {
            model.put( FormsConstants.MARK_MULTIVIEW_CONFIG_ACTION, multiviewConfigAction );
        }

        model.put( MARK_PAGINATOR, getPaginator( ) );
        model.put( MARK_LOCALE, getLocale( ) );
        model.put( MARK_FORM_FILTER_LIST, _listFormFilterDisplay );

        // Add the template of column to the model
        String strSortUrl = String.format( BASE_SORT_URL_PATTERN, _strSelectedPanelTechnicalCode );
        String strRedirectionDetailsBaseUrl = buildRedirectionDetailsBaseUrl( );
        String strTableTemplate = "";
        if (_formPanelDisplayActive != null )
        {
        	strTableTemplate = FormListTemplateBuilder.buildTableTemplate( _listFormColumnDisplay, _formPanelDisplayActive.getFormResponseItemList( ),
                    getLocale( ), strRedirectionDetailsBaseUrl, strSortUrl, getPaginator( ).getPageItems( ) );
        }
        model.put( MARK_TABLE_TEMPLATE, strTableTemplate );

        // Add the list of all form panel
        model.put( MARK_FORM_PANEL_LIST, _listAuthorizedFormPanelDisplay );
        model.put( MARK_CURRENT_SELECTED_PANEL, _strSelectedPanelTechnicalCode );
        model.put( MARK_LIST_FORMAT_EXPORT, ExportServiceManager.getInstance( ).getRefListFormatExport( ) );

        return getPage( PROPERTY_FORMS_MULTIVIEW_PAGE_TITLE, TEMPLATE_FORMS_MULTIVIEW, model );
    }

    /**
     * Perform the responses export
     * 
     * @param request
     *            The HTTP request
     */
    @Action( ACTION_EXPORT_RESPONSES )
    public void doExportResponses( HttpServletRequest request )
    {
        IFormatExport formatExport = ExportServiceManager.getInstance( ).getFormatExport( request.getParameter( PARAMETER_FORMAT_EXPORT ) );

        List<FormResponseItem> listFormResponseItemToDisplay = _formPanelDisplayActive.getFormResponseItemList( );

        if ( formatExport != null && CollectionUtils.isNotEmpty( listFormResponseItemToDisplay ) )
        {
            byte [ ] arrByteExportFile = formatExport.getByteExportFile( getFormResponseToExport( listFormResponseItemToDisplay ) );

            download( arrByteExportFile, I18nService.getLocalizedString( EXPORT_FILE_NAME, getLocale( ) ), formatExport.getFormatContentType( ) );
        }
    }

    /**
     * View the multiview config page
     * 
     * @param request
     *            The HTTP request
     * @return The multiview config page
     * @throws fr.paris.lutece.portal.service.admin.AccessDeniedException
     */
    @View( value = VIEW_MULTIVIEW_CONFIG )
    public String getMultiviewConfig( HttpServletRequest request ) throws AccessDeniedException
    {
        GlobalFormsAction multiviewConfigAction = GlobalFormsActionHome.selectGlobalFormActionByCode( FormsConstants.ACTION_FORMS_MANAGE_MULTIVIEW_CONFIG,
                FormsPlugin.getPlugin( ), request.getLocale( ) );
        if ( !RBACService.isAuthorized( (RBACResource) multiviewConfigAction, GlobalFormsAction.PERMISSION_PERFORM_ACTION,
                AdminUserService.getAdminUser( request ) ) )
        {
            throw new AccessDeniedException( );
        }

        MultiviewConfig config = MultiviewConfig.getInstance( );

        Map<String, Object> model = getModel( );
        model.put( FormsConstants.MARK_MULTIVIEW_CONFIG, config );

        return getPage( PROPERTY_FORMS_MULTIVIEW_PAGE_TITLE, TEMPLATE_FORMS_MULTIVIEW_CONFIG, model );
    }

    /**
     * View the multiview config page
     * 
     * @param request
     *            The HTTP request
     * @return The multiview config page
     */
    @Action( value = ACTION_SAVE_MULTIVIEW_CONFIG )
    public String doSaveMultiviewConfig( HttpServletRequest request ) throws AccessDeniedException
    {
        GlobalFormsAction multiviewConfigAction = GlobalFormsActionHome.selectGlobalFormActionByCode( FormsConstants.ACTION_FORMS_MANAGE_MULTIVIEW_CONFIG,
                FormsPlugin.getPlugin( ), request.getLocale( ) );
        if ( !RBACService.isAuthorized( (RBACResource) multiviewConfigAction, GlobalFormsAction.PERMISSION_PERFORM_ACTION,
                AdminUserService.getAdminUser( request ) ) )
        {
            throw new AccessDeniedException( );
        }

        MultiviewConfig config = MultiviewConfig.getInstance( );

        String strDisplayFormColumnTitle = request.getParameter( PARAMETER_DISPLAY_FORMS_TITLE_COLUMN );
        config.setDisplayFormsTitleColumn( strDisplayFormColumnTitle != null );

        config.save( );

        return redirectView( request, VIEW_MULTIVIEW_FORMS );
    }

    /**
     * 
     * @param listFormResponseItemToDisplay
     *            The list of FormResponse to display
     * @return the list of FormResponse to export
     */
    private List<FormResponse> getFormResponseToExport( List<FormResponseItem> listFormResponseItemToDisplay )
    {
        List<FormResponse> listFormResponse = new ArrayList<FormResponse>( );

        for ( FormResponseItem formResponseItem : listFormResponseItemToDisplay )
        {
            FormResponse formResponse = FormResponseHome.findByPrimaryKey( formResponseItem.getIdFormResponse( ) );

            for ( FormResponseStep formResponseStep : formResponse.getSteps( ) )
            {
                for ( FormQuestionResponse formQuestionResponse : formResponseStep.getQuestions( ) )
                {
                    // TODO Remove the formQuestionResponse from the formResponse if the associated question is not exportable
                }
            }

            listFormResponse.add( formResponse );
        }

        return listFormResponse;
    }

    /**
     * Return the boolean which tell if the session is lost
     * 
     * @return the boolean which tell if the session is lost
     */
    private boolean isSessionLost( )
    {
        return _listFormColumn == null || _listFormFilterDisplay == null || _listFormColumnDisplay == null || _listFormPanelDisplay == null
                || _formPanelDisplayActive == null;
    }

    /**
     * Return the boolean which tell if the pagination and the sort are not used
     * 
     * @param request
     *            The request to retrieve the information from
     * @return the boolean which tell if the pagination and the sort are not used
     */
    private boolean isPaginationAndSortNotUsed( HttpServletRequest request )
    {
        return request.getParameter( PARAMETER_PAGE_INDEX ) == null && request.getParameter( FormsConstants.PARAMETER_SORT_COLUMN_POSITION ) == null;
    }

    /**
     * Build the list of all columns, filters and forms panels and all of their display equivalents
     * 
     * @param request
     *            The request used to build the list of the forms elements
     */
    private void initFormRelatedLists( HttpServletRequest request )
    {
        
        
        List<FormPanel> listFormPanel = new FormPanelFactory( ).buildFormPanelList( );

        FormColumnFactory formColumnFactory = SpringContextService.getBean( FormColumnFactory.BEAN_NAME );

        Integer nIdForm = null;
        try
        {
            nIdForm = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_FORM ) );
        }
        catch ( NumberFormatException e )
        {
            //Nothing to do
        }

        _listFormColumn = formColumnFactory.buildFormColumnList( nIdForm );
        List<FormFilter> listFormFilter = new FormFilterFactory( ).buildFormFilterList( nIdForm, _listFormColumn );
        _listFormFilterDisplay = new FormFilterDisplayFactory( ).createFormFilterDisplayList( request, listFormFilter );
        _listFormColumnDisplay = new FormColumnDisplayFactory( ).createFormColumnDisplayList( _listFormColumn );
        _listFormPanelDisplay = new FormPanelDisplayFactory( ).createFormPanelDisplayList( request, listFormPanel );
    }

    /**
     * Retrieve the technical code of the selected panel and change the value of the previous selected one if it wasn't the same and reset the pagination in
     * this case
     */
    private void manageSelectedPanel( )
    {
        IFormPanelDisplay formPanelDisplay = MultiviewFormService.getInstance( ).findActiveFormPanel( _listAuthorizedFormPanelDisplay );
        if ( formPanelDisplay != null )
        {
            String strSelectedPanelTechnicalCode = formPanelDisplay.getTechnicalCode( );
            if ( StringUtils.isNotBlank( strSelectedPanelTechnicalCode ) && !_strSelectedPanelTechnicalCode.equals( strSelectedPanelTechnicalCode ) )
            {
                _strSelectedPanelTechnicalCode = strSelectedPanelTechnicalCode;
                resetCurrentPaginatorPageIndex( );
            }
        }
    }

    /**
     * Build all the form panels by building their template and retrieve the data of their columns for the given list of filter and the specified text to search
     */
    private void buildFormPanelDisplayWithData(  HttpServletRequest request, int nIndexStart, int nPageSize, FormResponseItemComparatorConfig comparatorConfig )
    {
        // Retrieve the list of all FormFilter
        List<FormFilter> listFormFilter = _listFormFilterDisplay.stream( ).map( IFormFilterDisplay::getFormFilter ).collect( Collectors.toList( ) );

        // Check in filters if the columns list has to be fetch again
        reloadFormColumnList( listFormFilter );
        if ( formSelectedAsChanged( request ) )
        {
            _strFormSelectedValue = request.getParameter( FormsConstants.PARAMETER_ID_FORM );
            reloadFormFilterList( listFormFilter, request );
        }

        for ( IFormPanelDisplay formPanelDisplay : _listAuthorizedFormPanelDisplay )
        {
            // Retrieve the FormPanel from the FormPanelDisplay
            FormPanel formPanel = formPanelDisplay.getFormPanel( );

            // Populate the FormColumns from the information of the list of FormResponseItem of the given FormPanel
            MultiviewFormService.getInstance( ).populateFormColumns( formPanel, _listFormColumn, listFormFilter, nIndexStart, nPageSize, comparatorConfig );

            // Associate for each FormColumnDisplay its FormColumnValues if the panel is active
            if ( formPanelDisplay.isActive( ) )
            {
                _formPanelDisplayActive = formPanelDisplay;
            }

            // Build the template of the form list panel
            formPanelDisplay.buildTemplate( getLocale( ) );
        }
        
    }

    /**
     * Sort the given list of FormResponseItem from the values contains in the request
     * 
     * @param request
     *            The request to retrieve the values used for the sort
     * @param listFormResponseItem
     *            The list of FormResponseItem to sort
     */
    private void sortFormResponseItemList( HttpServletRequest request, List<FormResponseItem> listFormResponseItem )
    {
        if ( request.getParameter( FormsConstants.PARAMETER_SORT_COLUMN_POSITION ) != null )
        {
            buildFormResponseItemComparatorConfiguration( request );
        }

        if ( listFormResponseItem != null && !listFormResponseItem.isEmpty( ) )
        {
            FormResponseItemComparator formResponseItemComparator = new FormResponseItemComparator( _formResponseItemComparatorConfig );
            Collections.sort( listFormResponseItem, formResponseItemComparator );
        }
    }

    /**
     * Build the configuration to use for sort the FormResponseItem with the information from the request
     * 
     * @param request
     *            The request to retrieve the values for the sort from
     */
    private FormResponseItemComparatorConfig buildFormResponseItemComparatorConfiguration( HttpServletRequest request )
    {
        String strColumnToSortPosition = request.getParameter( FormsConstants.PARAMETER_SORT_COLUMN_POSITION );
        int nColumnToSortPosition = NumberUtils.toInt( strColumnToSortPosition, NumberUtils.INTEGER_MINUS_ONE );

        String strSortKey = request.getParameter( FormsConstants.PARAMETER_SORT_ATTRIBUTE_NAME );

        String strAscSort = request.getParameter( FormsConstants.PARAMETER_SORT_ASC_VALUE );
        boolean bAscSort = Boolean.parseBoolean( strAscSort );

        return new FormResponseItemComparatorConfig( nColumnToSortPosition, strSortKey, bAscSort );
    }

    /**
     * Build the base url to use for redirect to the page of the details of a form response
     * 
     * @return the base url to use for redirect to the details page of a form response
     */
    private String buildRedirectionDetailsBaseUrl( )
    {
        UrlItem urlRedirectionDetails = new UrlItem( MultiviewFormResponseDetailsJspBean.getMultiviewRecordDetailsBaseUrl( ) );

        if ( !CollectionUtils.isEmpty( _listFormFilterDisplay ) )
        {
            for ( IFormFilterDisplay formFilterDisplay : _listFormFilterDisplay )
            {
                // Add all the filters values
                String strFilterValue = formFilterDisplay.getValue( );
                if ( !StringUtils.isEmpty( strFilterValue ) )
                {
                    String strFilterFullName = FormsConstants.PARAMETER_URL_FILTER_PREFIX + formFilterDisplay.getParameterName( );
                    urlRedirectionDetails.addParameter( strFilterFullName, strFilterValue );
                }
            }
        }

        // Add the selected panel technical code
        urlRedirectionDetails.addParameter( FormsConstants.PARAMETER_SELECTED_PANEL, _strSelectedPanelTechnicalCode );

        // Add sort filter data to the url
        addFilterSortConfigToUrl( urlRedirectionDetails );

        return urlRedirectionDetails.getUrl( );
    }

    /**
     * Add the information for rebuild the used sort
     * 
     * @param urlRedirectionDetails
     *            The UrlItem which represent the url to use for redirect to the form response details page
     */
    private void addFilterSortConfigToUrl( UrlItem urlRedirectionDetails )
    {
        if ( _formResponseItemComparatorConfig != null )
        {
            String strSortPosition = Integer.toString( _formResponseItemComparatorConfig.getColumnToSortPosition( ) );
            String strAttributeName = _formResponseItemComparatorConfig.getSortAttributeName( );
            String strAscSort = String.valueOf( _formResponseItemComparatorConfig.isAscSort( ) );

            urlRedirectionDetails.addParameter( FormsConstants.PARAMETER_SORT_COLUMN_POSITION, strSortPosition );
            urlRedirectionDetails.addParameter( FormsConstants.PARAMETER_SORT_ATTRIBUTE_NAME, strAttributeName );
            urlRedirectionDetails.addParameter( FormsConstants.PARAMETER_SORT_ASC_VALUE, strAscSort );
        }
    }

    /**
     * Build the url to use for the pagination
     * 
     * @return the url to use for the pagination
     */
    private String buildPaginatorUrl( )
    {
        UrlItem url = new UrlItem( JSP_FORMS_MULTIVIEW );
        url.addParameter( FormsConstants.PARAMETER_CURRENT_SELECTED_PANEL, _strSelectedPanelTechnicalCode );

        return url.getUrl( );
    }

    /**
     * Return the base url of the controller for the view which display the list of reponses
     * 
     * @return the base url of the controller for the view which display the list of reponses
     */
    protected static String getMultiviewBaseViewUrl( )
    {
        return JSP_FORMS_MULTIVIEW + "?view=" + VIEW_MULTIVIEW_FORMS;
    }

    /**
     * Reload the form column list form the form filter list
     * 
     * @param listFormFilter
     *            the form filter list
     */
    private void reloadFormColumnList( List<FormFilter> listFormFilter )
    {
        FormColumnFactory formColumnFactory = SpringContextService.getBean( FormColumnFactory.BEAN_NAME );

        for ( FormFilter filter : listFormFilter )
        {
            if ( filter instanceof FormFilterForms )
            {
                Integer nIdForm = ( (FormFilterForms) filter ).getSelectedIdForm( );

                if ( nIdForm != FormsConstants.DEFAULT_ID_VALUE )
                {
                    _listFormColumn = formColumnFactory.buildFormColumnList( nIdForm );
                }
                else
                {
                    _listFormColumn = formColumnFactory.buildFormColumnList( null );
                }

                _listFormColumnDisplay = new FormColumnDisplayFactory( ).createFormColumnDisplayList( _listFormColumn );
            }
        }
    }

    private void reloadFormFilterList( List<FormFilter> listFormFilter, HttpServletRequest request )
    {
        for ( FormFilter filter : listFormFilter )
        {
            if ( filter instanceof FormFilterForms )
            {
                Integer nIdForm = ( (FormFilterForms) filter ).getSelectedIdForm( );
                List<FormFilter> listFormFilterReloaded = new ArrayList<>();
                listFormFilterReloaded = (nIdForm != FormsConstants.DEFAULT_ID_VALUE)  ? 
                        new FormFilterFactory().buildFormFilterList( nIdForm, _listFormColumn ) :
                        new FormFilterFactory().buildFormFilterList( null, _listFormColumn );
                
                
                _listFormFilterDisplay = new FormFilterDisplayFactory( ).createFormFilterDisplayList( request, listFormFilterReloaded );
            }
        }
    }
    
    /**
     * Return true if the form selection has changed, false otherwise
     * @param request
     * @return true if the form selection has changed, false otherwise
     */
    private boolean formSelectedAsChanged( HttpServletRequest request )
    {
        String strFormSelectedNewValue = request.getParameter( FormsConstants.PARAMETER_ID_FORM );
        boolean bIdFormHasChanged = false;
        if ( strFormSelectedNewValue != null )
        {
            bIdFormHasChanged = !strFormSelectedNewValue.equals( _strFormSelectedValue ) ;
            _strFormSelectedValue = strFormSelectedNewValue;
        }

        return bIdFormHasChanged;
    }
}
