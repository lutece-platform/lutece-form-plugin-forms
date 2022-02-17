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

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.filegenerator.service.TemporaryFileGeneratorService;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.MultiviewConfig;
import fr.paris.lutece.plugins.forms.business.action.GlobalFormsAction;
import fr.paris.lutece.plugins.forms.business.action.GlobalFormsActionHome;
import fr.paris.lutece.plugins.forms.business.form.FormResponseItemSortConfig;
import fr.paris.lutece.plugins.forms.business.form.column.FormColumnFactory;
import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.filter.FormFilter;
import fr.paris.lutece.plugins.forms.business.form.filter.FormFilterForms;
import fr.paris.lutece.plugins.forms.business.form.panel.FormPanel;
import fr.paris.lutece.plugins.forms.business.form.panel.FormPanelFactory;
import fr.paris.lutece.plugins.forms.export.ExportServiceManager;
import fr.paris.lutece.plugins.forms.export.IFormatExport;
import fr.paris.lutece.plugins.forms.service.FormPanelConfigIdService;
import fr.paris.lutece.plugins.forms.service.FormsPlugin;
import fr.paris.lutece.plugins.forms.service.MultiviewFormService;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.web.form.FormDisplayFactory;
import fr.paris.lutece.plugins.forms.web.form.column.display.IFormColumnDisplay;
import fr.paris.lutece.plugins.forms.web.form.filter.display.IFormFilterDisplay;
import fr.paris.lutece.plugins.forms.web.form.filter.display.impl.FormFilterDisplayForms;
import fr.paris.lutece.plugins.forms.web.form.multiview.util.FormListPositionComparator;
import fr.paris.lutece.plugins.forms.web.form.multiview.util.FormListTemplateBuilder;
import fr.paris.lutece.plugins.forms.web.form.multiview.util.MultiviewFormUtil;
import fr.paris.lutece.plugins.forms.web.form.panel.display.IFormPanelDisplay;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACResource;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.html.AbstractPaginator;
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

    // Views
    private static final String VIEW_MULTIVIEW_FORMS = "view_multiview_forms";
    private static final String VIEW_MULTIVIEW_CONFIG = "view_multiview_config";

    // Parameters
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_FORMAT_EXPORT = "format_export";
    private static final String PARAMETER_DISPLAY_FORMS_TITLE_COLUMN = "display_forms_title_column";
    private static final String PARAMETER_DISPLAY_FORMS_CSV_SEPARATOR = "csv_separator";
    private static final String PARAMETER_DISPLAY_ASSIGNEE_COLUMN = "display_assignee_column";
    private static final String PARAMETER_CHANGE_PANEL = "change_panel";

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
    private transient FormResponseItemSortConfig _formResponseItemComparatorConfig;
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
        // Retrieve the list of all filters, columns and panels if the pagination and
        // the sort are not used
        boolean bIsSessionLost = isSessionLost( );
        if ( isPaginationAndSortNotUsed( request ) || bIsSessionLost )
        {
            initFormRelatedLists( request );
            manageSelectedPanel( );
        }
        _listAuthorizedFormPanelDisplay = _listFormPanelDisplay.stream( )
                .filter( fpd -> RBACService.isAuthorized( fpd.getFormPanel( ).getFormPanelConfiguration( ), FormPanelConfigIdService.PERMISSION_VIEW,
                        (User) AdminUserService.getAdminUser( request ) ) )
                .collect( Collectors.toList( ) );

        // Build the Column for the Panel and save their values for the active panel
        initiatePaginatorProperties( request );
        buildFormResponseItemComparatorConfiguration( request );
        buildFormPanelDisplayWithData( request, getIndexStart( ), _nItemsPerPage, _formResponseItemComparatorConfig );

        // Build the template of each form filter display
        if ( isPaginationAndSortNotUsed( request ) || bIsSessionLost )
        {
            _listFormFilterDisplay.stream( ).forEach( formFilterDisplay -> formFilterDisplay.buildTemplate( request ) );
            Collections.sort( _listFormFilterDisplay, new FormListPositionComparator( ) );
        }

        // Retrieve the list of all id of form response of the active FormPanelDisplay
        List<Integer> listIdFormResponse = MultiviewFormUtil.getListIdFormResponseOfFormPanel( _formPanelDisplayActive );

        // Build the model
        if ( _formPanelDisplayActive == null && CollectionUtils.isNotEmpty( _listAuthorizedFormPanelDisplay ) )
        {
            _formPanelDisplayActive = _listAuthorizedFormPanelDisplay.get( 0 );
            _formPanelDisplayActive.setActive( true );
        }
        Map<String, Object> model = getPaginatedListModel( request, AbstractPaginator.PARAMETER_PAGE_INDEX, listIdFormResponse, buildPaginatorUrl( ),
                _formPanelDisplayActive.getFormPanel( ).getTotalFormResponseItemCount( ) );

        // Get the config multiview action if the current admin user is authorized
        GlobalFormsAction multiviewConfigAction = GlobalFormsActionHome.selectGlobalFormActionByCode( FormsConstants.ACTION_FORMS_MANAGE_MULTIVIEW_CONFIG,
                FormsPlugin.getPlugin( ), request.getLocale( ) );

        if ( RBACService.isAuthorized( (RBACResource) multiviewConfigAction, GlobalFormsAction.PERMISSION_PERFORM_ACTION,
                (User) AdminUserService.getAdminUser( request ) ) )
        {
            model.put( FormsConstants.MARK_MULTIVIEW_CONFIG_ACTION, multiviewConfigAction );
        }

        GlobalFormsAction multiviewExportAction = GlobalFormsActionHome.selectGlobalFormActionByCode( FormsConstants.ACTION_FORMS_EXPORT_RESPONSES,
                FormsPlugin.getPlugin( ), request.getLocale( ) );

        if ( RBACService.isAuthorized( (RBACResource) multiviewExportAction, GlobalFormsAction.PERMISSION_PERFORM_ACTION,
                (User) AdminUserService.getAdminUser( request ) ) )
        {
            model.put( FormsConstants.MARK_MULTIVIEW_EXPORT_ACTION, multiviewExportAction );
        }

        model.put( MARK_PAGINATOR, getPaginator( ) );
        model.put( MARK_LOCALE, getLocale( ) );
        model.put( MARK_FORM_FILTER_LIST, _listFormFilterDisplay );

        // Add the template of column to the model
        String strSortUrl = String.format( BASE_SORT_URL_PATTERN, _strSelectedPanelTechnicalCode );
        String strRedirectionDetailsBaseUrl = buildRedirectionDetailsBaseUrl( );
        String strTableTemplate = "";
        if ( _formPanelDisplayActive != null )
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
     * @throws AccessDeniedException
     */
    @Action( ACTION_EXPORT_RESPONSES )
    public String doExportResponses( HttpServletRequest request ) throws AccessDeniedException
    {
        GlobalFormsAction multiviewExportAction = GlobalFormsActionHome.selectGlobalFormActionByCode( FormsConstants.ACTION_FORMS_EXPORT_RESPONSES,
                FormsPlugin.getPlugin( ), request.getLocale( ) );
        AdminUser user = AdminUserService.getAdminUser( request );
        if ( !RBACService.isAuthorized( (RBACResource) multiviewExportAction, GlobalFormsAction.PERMISSION_PERFORM_ACTION, (User) user ) )
        {
            throw new AccessDeniedException( UNAUTHORIZED );
        }

        boolean filteredByForm = false;
        FormFilterDisplayForms formFilter = (FormFilterDisplayForms) _listFormFilterDisplay.stream( ).filter( f -> f instanceof FormFilterDisplayForms )
                .findFirst( ).orElse( null );
        String strIdForm = "";
        if ( formFilter != null )
        {
            strIdForm = formFilter.getValue( );
            filteredByForm = StringUtils.isNotEmpty( strIdForm ) && !FormFilterDisplayForms.DEFAULT_ID_FORM.contentEquals( strIdForm );
        }

        if ( !filteredByForm )
        {
            addError( "forms.export.error.filter", getLocale( ) );
            return getMultiviewFormsView( request );
        }

        IFormatExport formatExport = ExportServiceManager.getInstance( ).getFormatExport( request.getParameter( PARAMETER_FORMAT_EXPORT ) );

        if ( formatExport != null )
        {
            int idForm = Integer.parseInt( strIdForm );
            Form form = FormHome.findByPrimaryKey( idForm );
            List<FormFilter> listFormFilter = _listFormFilterDisplay.stream( ).map( IFormFilterDisplay::getFormFilter ).collect( Collectors.toList( ) );

            TemporaryFileGeneratorService.getInstance( ).generateFile( formatExport.createFileGenerator( form.getTitle( ),
                    _formPanelDisplayActive.getFormPanel( ), _listFormColumn, listFormFilter, _formResponseItemComparatorConfig ), user );
        }
        addInfo( "forms.export.async.message", getLocale( ) );

        return getMultiviewFormsView( request );

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
                (User) AdminUserService.getAdminUser( request ) ) )
        {
            throw new AccessDeniedException( UNAUTHORIZED );
        }

        MultiviewConfig config = MultiviewConfig.getInstance( );

        Map<String, Object> model = getModel( );
        model.put( FormsConstants.MARK_MULTIVIEW_CONFIG, config );
        model.put( FormsConstants.MARK_FORM_USERASSIGNMENT_ENABLED, PluginService.isPluginEnable( "forms-userassignment" ) );

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
                (User) AdminUserService.getAdminUser( request ) ) )
        {
            throw new AccessDeniedException( UNAUTHORIZED );
        }

        MultiviewConfig config = MultiviewConfig.getInstance( );

        config.setCsvSeparator( request.getParameter( PARAMETER_DISPLAY_FORMS_CSV_SEPARATOR ) );

        String strDisplayFormColumnTitle = request.getParameter( PARAMETER_DISPLAY_FORMS_TITLE_COLUMN );
        config.setDisplayFormsTitleColumn( strDisplayFormColumnTitle != null );

        String strDisplayFormColumnAssignee = request.getParameter( PARAMETER_DISPLAY_ASSIGNEE_COLUMN );
        config.setDisplayFormsAssigneeColumn( strDisplayFormColumnAssignee != null );

        config.save( );

        return redirectView( request, VIEW_MULTIVIEW_FORMS );
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
        catch( NumberFormatException e )
        {
            // Nothing to do
        }

        boolean changePanel = Boolean.parseBoolean( request.getParameter( PARAMETER_CHANGE_PANEL ) );
        if ( CollectionUtils.isEmpty( _listFormFilterDisplay ) || !changePanel )
        {
            _listFormColumn = formColumnFactory.buildFormColumnList( nIdForm, request.getLocale( ), (User) AdminUserService.getAdminUser( request ) );
            List<FormFilter> listFormFilter = FormDisplayFactory.buildFormFilterList( nIdForm, _listFormColumn, request.getLocale( ),
                    (User) AdminUserService.getAdminUser( request ) );
            _listFormFilterDisplay = FormDisplayFactory.createFormFilterDisplayList( request, listFormFilter );
            _listFormColumnDisplay = FormDisplayFactory.createFormColumnDisplayList( _listFormColumn );
        }

        _listFormPanelDisplay = FormDisplayFactory.createFormPanelDisplayList( request, listFormPanel, _formPanelDisplayActive );
        for ( IFormPanelDisplay formPanelDisplay : _listFormPanelDisplay )
        {
            if ( formPanelDisplay.isActive( ) )
            {
                _formPanelDisplayActive = formPanelDisplay;
            }
        }
        _formResponseItemComparatorConfig = new FormResponseItemSortConfig( -1, null, true );
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
    private void buildFormPanelDisplayWithData( HttpServletRequest request, int nIndexStart, int nPageSize, FormResponseItemSortConfig sortConfig )
    {
        // Retrieve the list of all FormFilter
        List<FormFilter> listFormFilter = _listFormFilterDisplay.stream( ).map( IFormFilterDisplay::getFormFilter ).collect( Collectors.toList( ) );

        // Check in filters if the columns list has to be fetch again
        reloadFormColumnList( listFormFilter, request.getLocale( ), (User) AdminUserService.getAdminUser( request ) );
        if ( formSelectedAsChanged( request ) )
        {
            _strFormSelectedValue = request.getParameter( FormsConstants.PARAMETER_ID_FORM );
            reloadFormFilterList( listFormFilter, request );
        }

        for ( IFormPanelDisplay formPanelDisplay : _listAuthorizedFormPanelDisplay )
        {
            // Retrieve the FormPanel from the FormPanelDisplay
            FormPanel formPanel = formPanelDisplay.getFormPanel( );

            // Populate the FormColumns from the information of the list of FormResponseItem
            // of the given FormPanel
            MultiviewFormService.getInstance( ).populateFormColumns( formPanel, _listFormColumn, listFormFilter, nIndexStart, nPageSize, sortConfig );

            // Associate for each FormColumnDisplay its FormColumnValues if the panel is
            // active
            if ( formPanelDisplay.isActive( ) )
            {
                _formPanelDisplayActive = formPanelDisplay;
            }

            // Build the template of the form list panel
            formPanelDisplay.buildTemplate( getLocale( ) );
        }
    }

    /**
     * Build the configuration to use for sort the FormResponseItem with the information from the request
     * 
     * @param request
     *            The request to retrieve the values for the sort from
     */
    private void buildFormResponseItemComparatorConfiguration( HttpServletRequest request )
    {
        String strColumnToSortPosition = request.getParameter( FormsConstants.PARAMETER_SORT_COLUMN_POSITION );
        if ( strColumnToSortPosition != null )
        {
            int nColumnToSortPosition = NumberUtils.toInt( strColumnToSortPosition, NumberUtils.INTEGER_MINUS_ONE );

            String strParamSortKey = request.getParameter( FormsConstants.PARAMETER_SORT_ATTRIBUTE_NAME );

            String strAscSort = request.getParameter( FormsConstants.PARAMETER_SORT_ASC_VALUE );
            boolean bAscSort = Boolean.parseBoolean( strAscSort );

            _formResponseItemComparatorConfig = new FormResponseItemSortConfig( nColumnToSortPosition, strParamSortKey, bAscSort );
        }
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
        return "MultiviewForms.jsp?view=" + VIEW_MULTIVIEW_FORMS;
    }

    /**
     * Reload the form column list form the form filter list
     * 
     * @param listFormFilter
     *            the form filter list
     */
    private void reloadFormColumnList( List<FormFilter> listFormFilter, Locale locale, User user )
    {
        FormColumnFactory formColumnFactory = SpringContextService.getBean( FormColumnFactory.BEAN_NAME );

        for ( FormFilter filter : listFormFilter )
        {
            if ( filter instanceof FormFilterForms )
            {
                Integer nIdForm = ( (FormFilterForms) filter ).getSelectedIdForm( );

                if ( nIdForm != FormsConstants.DEFAULT_ID_VALUE )
                {
                    _listFormColumn = formColumnFactory.buildFormColumnList( nIdForm, locale, user );
                }
                else
                {
                    _listFormColumn = formColumnFactory.buildFormColumnList( null, locale, user );
                }

                _listFormColumnDisplay = FormDisplayFactory.createFormColumnDisplayList( _listFormColumn );
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
                List<FormFilter> listFormFilterReloaded = ( nIdForm != FormsConstants.DEFAULT_ID_VALUE )
                        ? FormDisplayFactory.buildFormFilterList( nIdForm, _listFormColumn, request.getLocale( ),
                                (User) AdminUserService.getAdminUser( request ) )
                        : FormDisplayFactory.buildFormFilterList( null, _listFormColumn, request.getLocale( ),
                                (User) AdminUserService.getAdminUser( request ) );

                _listFormFilterDisplay = FormDisplayFactory.createFormFilterDisplayList( request, listFormFilterReloaded );
            }
        }
    }

    /**
     * Return true if the form selection has changed, false otherwise
     * 
     * @param request
     * @return true if the form selection has changed, false otherwise
     */
    private boolean formSelectedAsChanged( HttpServletRequest request )
    {
        String strFormSelectedNewValue = request.getParameter( FormsConstants.PARAMETER_ID_FORM );
        boolean bIdFormHasChanged = false;
        if ( strFormSelectedNewValue != null )
        {
            bIdFormHasChanged = !strFormSelectedNewValue.equals( _strFormSelectedValue );
            _strFormSelectedValue = strFormSelectedNewValue;
        }

        return bIdFormHasChanged;
    }
}
