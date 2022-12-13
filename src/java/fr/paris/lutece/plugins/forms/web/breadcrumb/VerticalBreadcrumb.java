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
package fr.paris.lutece.plugins.forms.web.breadcrumb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.web.FormResponseManager;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * Horizontal display implementation of IBreadcrumb
 */
public class VerticalBreadcrumb implements IBreadcrumb
{
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
    public VerticalBreadcrumb( String strBreadcrumbName, String strBreadcrumbDisplayName )
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
        Step currentStep = formResponseManager.getValidatedSteps( ).get( formResponseManager.getValidatedSteps( ).size( ) - 1 );

        List<Step> listStep = StepHome.getStepsListByForm( currentStep.getIdForm( ) );

        int nCurrentStepIndex = getCurrentStepIndex( listStep, currentStep.getId( ) );

        listStep.subList( nCurrentStepIndex, listStep.size( ) ).clear( );

        Map<String, Object> model = new HashMap<>( );
        model.put( FormsConstants.MARK_LIST_STEPS, listStep );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_BREADCRUMB_HTML, I18nService.getDefaultLocale( ), model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBottomHtml( HttpServletRequest request, FormResponseManager formResponseManager )
    {
        Step currentStep = formResponseManager.getValidatedSteps( ).get( formResponseManager.getValidatedSteps( ).size( ) - 1 );

        List<Step> listStep = StepHome.getStepsListByForm( currentStep.getIdForm( ) );

        int nCurrentStepIndex = getCurrentStepIndex( listStep, currentStep.getId( ) );

        listStep.subList( 0, nCurrentStepIndex + 1 ).clear( );

        Map<String, Object> model = new HashMap<>( );
        model.put( FormsConstants.MARK_LIST_STEPS, listStep );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_BREADCRUMB_HTML, I18nService.getDefaultLocale( ), model );

        return template.getHtml( );
    }

    /**
     * 
     * @param listStep
     *            the list of step
     * @param nIdCurrentStep
     *            the current step id
     * @return the index of current step in list
     */
    private int getCurrentStepIndex( List<Step> listStep, int nIdCurrentStep )
    {
        for ( Step step : listStep )
        {
            if ( step.getId( ) == nIdCurrentStep )
            {
                return listStep.indexOf( step );
            }
        }

        return 0;
    }

}
