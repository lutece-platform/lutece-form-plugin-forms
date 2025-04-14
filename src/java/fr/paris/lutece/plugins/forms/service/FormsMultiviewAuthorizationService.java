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
package fr.paris.lutece.plugins.forms.service;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.forms.business.form.FormParameters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.form.FormResponseItem;
import fr.paris.lutece.plugins.forms.business.form.column.FormColumnFactory;
import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.filter.FormFilter;
import fr.paris.lutece.plugins.forms.business.form.filter.configuration.FormFilterFormResponseIdConfiguration;
import fr.paris.lutece.plugins.forms.business.form.list.FormListFacade;
import fr.paris.lutece.plugins.forms.business.form.panel.FormPanel;
import fr.paris.lutece.plugins.forms.business.form.panel.FormPanelFactory;
import fr.paris.lutece.plugins.forms.business.form.panel.configuration.IFormPanelConfiguration;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.web.form.FormDisplayFactory;
import fr.paris.lutece.portal.service.admin.AdminUserService;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 * Implementation of the IFormsMultiviewAuthorizationService interface
 */
@ApplicationScoped
public class FormsMultiviewAuthorizationService implements IFormsMultiviewAuthorizationService
{
    public static final String BEAN_NAME = "forms.formsMultiviewAuthorizationService";

    // Variables
    private FormPanel _formPanel;

    @Inject
    private FormListFacade _formListFacade;

    @Inject
    private FormColumnFactory _formColumnFactory;

    @Inject
    private IFormPanelConfiguration _formPanelConfiguration;

	public FormsMultiviewAuthorizationService( )
	{
	}

	@PostConstruct
    public void initialize( ) 
	{
		_formPanel = new FormPanel( );
		_formPanel.setFormPanelConfiguration( _formPanelConfiguration );
	}
	
	/**
     * Constructor
     * 
     * @param formPanelConfiguration
     *            The FormPanelConfiguration to set to the FormPanel
     * @param formListFacade
     *            The FormListFacade to use by the service
     * @param formColumnFactory
     *            The FormColumnFactory to use by the service
     */
    public FormsMultiviewAuthorizationService( IFormPanelConfiguration formPanelConfiguration, FormListFacade formListFacade,
            FormColumnFactory formColumnFactory )
    {
        _formPanel = new FormPanel( );
        _formPanel.setFormPanelConfiguration( formPanelConfiguration );
        _formListFacade = formListFacade;
        _formColumnFactory = formColumnFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUserAuthorizedOnFormResponse( HttpServletRequest request, int nIdFormResponse )
    {
        boolean bIsUserAuthorizedOnFormResponse = Boolean.FALSE;

        if ( nIdFormResponse != NumberUtils.INTEGER_MINUS_ONE && _formPanel.getFormPanelConfiguration( ) != null )
        {
            _formPanel = new FormPanelFactory( ).buildFormPanel( _formPanel.getFormPanelConfiguration( ) );

            List<IFormColumn> listFormResponseColumn = _formColumnFactory.buildFormColumnList( null, request.getLocale( ),
                    (User) AdminUserService.getAdminUser( request ) );

            // Rebuild all the FormPanelInitializer to reset the previous data
            FormDisplayFactory.buildFormPanelDisplayInitializer( request, _formPanel );

            List<FormFilter> listFormFilters = new ArrayList<>( );
            listFormFilters.add( buildFormResponseIdFilter( nIdFormResponse ) );

            _formListFacade.populateFormColumns( _formPanel, listFormResponseColumn, listFormFilters, 0, 1, null );
            List<FormResponseItem> listFormResponseItem = _formPanel.getFormResponseItemList( );

            if ( !CollectionUtils.isEmpty( listFormResponseItem ) )
            {
                List<Integer> listIdFormResponse = listFormResponseItem.stream( ).map( FormResponseItem::getIdFormResponse ).collect( Collectors.toList( ) );
                bIsUserAuthorizedOnFormResponse = listIdFormResponse.contains( nIdFormResponse );
            }
        }

        return bIsUserAuthorizedOnFormResponse;
    }

    /**
     * Build a form response id filter from an id response
     * 
     * @param nIdFormResponse
     *            The form response id
     * @return the FormFilter
     */
    private FormFilter buildFormResponseIdFilter( int nIdFormResponse )
    {
        FormFilter formFilterId = new FormFilter( );
        FormFilterFormResponseIdConfiguration config = new FormFilterFormResponseIdConfiguration( 1, StringUtils.EMPTY, StringUtils.EMPTY );
        FormParameters formParam = new FormParameters( );

        Map<String, Object> mapFormParam = new HashMap<>( );
        mapFormParam.put( FormsConstants.PARAMETER_ID_RESPONSE, nIdFormResponse );

        formParam.setFormParametersMap( mapFormParam );
        formFilterId.setFormFilterConfiguration( config );
        formFilterId.setFormParameters( formParam );

        return formFilterId;
    }
}
