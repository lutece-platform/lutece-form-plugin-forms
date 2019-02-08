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
package fr.paris.lutece.plugins.forms.web.form.filter.display.impl;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.util.FormMultiviewFormsNameConstants;
import fr.paris.lutece.plugins.forms.util.ReferenceListFactory;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * Implementation of the IFormFilterDisplay interface for the filter on form
 */
public class FormFilterDisplayForms extends AbstractFormFilterDisplay
{
    // Templates
    private static final String FORM_FORMS_FILTER_TEMPLATE_NAME = "admin/plugins/forms/multiview/filter/record_form_filter.html";

    // Constants
    private static final String FORM_CODE_ATTRIBUTE = "id";
    private static final String FORM_NAME_ATTRIBUTE = "title";
    private static final String DEFAULT_ID_FORM = "-1";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBaseTemplate( )
    {
        return FORM_FORMS_FILTER_TEMPLATE_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getParameterName( )
    {
        return FormMultiviewFormsNameConstants.PARAMETER_ID_FORM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getFilterDisplayMapValues( HttpServletRequest request )
    {
        String strIdFormValue = DEFAULT_ID_FORM;
        Map<String, Object> mapFilterNameValues = new LinkedHashMap<>( );

        String strIdForm = request.getParameter( FormMultiviewFormsNameConstants.PARAMETER_ID_FORM );
        if ( StringUtils.isNotBlank( strIdForm ) )
        {
            mapFilterNameValues.put( FormMultiviewFormsNameConstants.FILTER_ID_FORM, strIdForm );
            strIdFormValue = strIdForm;
        }
        else
        {
            // Check if there is just one form
            ReferenceList refListForms = createReferenceList( );
            if ( refListForms.size( ) == 2 )
            {
                strIdForm = refListForms.get( 1 ).getCode( );
                if ( StringUtils.isNotBlank( strIdForm ) )
                {
                    mapFilterNameValues.put( FormMultiviewFormsNameConstants.FILTER_ID_FORM, strIdForm );
                    strIdFormValue = strIdForm;
                }
            }
        }

        setValue( strIdFormValue );

        return mapFilterNameValues;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void buildTemplate( HttpServletRequest request )
    {
        ReferenceList refListForms = createReferenceList( );

        if ( refListForms.size( ) > 2 )
        {
            String strTemplateResult = StringUtils.EMPTY;

            Map<String, Object> model = new LinkedHashMap<>( );
            model.put( MARK_FILTER_LIST, refListForms );
            model.put( MARK_FILTER_LIST_VALUE, getValue( ) );
            model.put( MARK_FILTER_NAME, FormMultiviewFormsNameConstants.PARAMETER_ID_FORM );
            model.put( FormMultiviewFormsNameConstants.PARAMETER_PREVIOUS_ID_FORM, request.getParameter( FormMultiviewFormsNameConstants.PARAMETER_ID_FORM ) );

            HtmlTemplate htmlTemplate = AppTemplateService.getTemplate( getBaseTemplate( ), request.getLocale( ), model );
            if ( htmlTemplate != null )
            {
                strTemplateResult = htmlTemplate.getHtml( );
            }

            setTemplate( strTemplateResult );
        }
        setTemplate( StringUtils.EMPTY );
    }

    /**
     * Create the ReferenceList of the Forms on which we can filter
     * 
     * @return the ReferenceList of Forms on which we can filter
     */
    private ReferenceList createReferenceList( )
    {
        ReferenceListFactory referenceListFactory = new ReferenceListFactory( getFormsList( ), FORM_CODE_ATTRIBUTE, FORM_NAME_ATTRIBUTE );

        return referenceListFactory.createReferenceList( );
    }

    /**
     * Return the list of all Form
     * 
     * @return the list of all Form
     */
    private List<Form> getFormsList( )
    {
        List<Form> listForm = FormHome.getFormList( );

        if ( !CollectionUtils.isEmpty( listForm ) )
        {
            listForm.sort( Comparator.comparing( Form::getTitle, Comparator.nullsLast( Comparator.naturalOrder( ) ) ) );
        }

        return listForm;
    }
}
