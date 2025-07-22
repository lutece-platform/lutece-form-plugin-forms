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
package fr.paris.lutece.plugins.forms.web.breadcrumb;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.web.FormResponseManager;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * Horizontal display implementation of IBreadcrumb
 */
@ApplicationScoped
@Named( VerticalBreadcrumb.BEAN_NAME )
public class VerticalBreadcrumb implements IBreadcrumb
{
	public static final String BEAN_NAME = "forms.verticalBreadcrumb";
    private static final String TEMPLATE_BREADCRUMB_HTML = "/util/plugins/forms/breadcrumb/vertical_breadcrumb.html";
    private final String _strBreadcrumbBeanName;
    private final String _strBreadcrumbDisplayBeanName;

    /**
     * Constructor of the HorizontalBreadcrumb
     * 
     * @param strBreadcrumbName
     *            The breadcrumb bean name
     * @param strBreadcrumbDisplayName
     *            The Breadcrumb display name
     */
    @Inject
    public VerticalBreadcrumb( @ConfigProperty( name = "forms.verticalBreadcrumb.beanName" ) String strBreadcrumbName, 
    		@ConfigProperty( name = "forms.verticalBreadcrumb.displayBeanName" ) String strBreadcrumbDisplayName )
    {
        _strBreadcrumbBeanName = strBreadcrumbName;
        _strBreadcrumbDisplayBeanName = I18nService.getLocalizedString( strBreadcrumbDisplayName, I18nService.getDefaultLocale( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBreadcrumbBeanName( )
    {
        return _strBreadcrumbBeanName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBreadcrumbDisplayName( )
    {
        return _strBreadcrumbDisplayBeanName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTopHtml( HttpServletRequest request, FormResponseManager formResponseManager )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( FormsConstants.MARK_LIST_STEPS, formResponseManager.getValidatedSteps( ) );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_BREADCRUMB_HTML, I18nService.getDefaultLocale( ), model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBottomHtml( HttpServletRequest request, FormResponseManager formResponseManager ) {
        return null;
    }
}
