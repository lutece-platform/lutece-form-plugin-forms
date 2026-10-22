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
package fr.paris.lutece.plugins.forms.service.listener;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.BeanUtils;
import fr.paris.lutece.portal.service.util.RemovalListener;
import fr.paris.lutece.portal.service.util.RemovalListenerService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Refuses the removal of a regular expression while a control of a form (pattern validator) uses it: once removed, the control would reject every
 * non-empty answer and the form could no longer be submitted.
 */
@ApplicationScoped
public class FormsRegularExpressionRemovalListener implements RemovalListener
{
    private static final String MESSAGE_REGULAR_EXPRESSION_USED = "forms.message.regularExpressionUsed";
    private static final String MESSAGE_USAGE = "forms.message.regularExpressionUsed.usage";
    private static final String SEPARATOR = ", ";

    @Inject
    @Named( BeanUtils.BEAN_REGULAR_EXPRESSION_REMOVAL_SERVICE )
    private RemovalListenerService _regularExpressionRemovalService;
    @Inject
    @ConfigProperty( name = "forms.patternValidator.beanName" )
    private String _strPatternValidator;
    @Inject
    @ConfigProperty( name = "forms.patternFileTypeValidator.beanName" )
    private String _strPatternFileTypeValidator;
    @Inject
    @ConfigProperty( name = "forms.geolocationAddressPatternValidator.beanName" )
    private String _strGeolocationAddressPatternValidator;

    private boolean _bRegistered;

    /**
     * Registers this listener with the removal service of the regular expressions when the application starts.
     *
     * @param context
     *            the context of the started application
     */
    public void register( @Observes @Initialized( ApplicationScoped.class ) Object context )
    {
        if ( !_bRegistered )
        {
            _regularExpressionRemovalService.registerListener( this );
            _bRegistered = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canBeRemoved( String strId )
    {
        return getControlsUsing( strId ).isEmpty( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRemovalRefusedMessage( String strId, Locale locale )
    {
        Set<String> setUsages = new LinkedHashSet<>( );
        for ( Control control : getControlsUsing( strId ) )
        {
            Control controlWithQuestions = ControlHome.findByPrimaryKey( control.getId( ) );
            if ( controlWithQuestions != null && controlWithQuestions.getListIdQuestion( ) != null )
            {
                for ( int nIdQuestion : controlWithQuestions.getListIdQuestion( ) )
                {
                    setUsages.add( getUsage( nIdQuestion, locale ) );
                }
            }
        }
        return I18nService.getLocalizedString( MESSAGE_REGULAR_EXPRESSION_USED, new Object [ ] {
                String.join( SEPARATOR, setUsages )
        }, locale );
    }

    /**
     * Gets the controls of the forms whose pattern validator uses the given regular expression.
     *
     * @param strId
     *            the id of the regular expression
     * @return the controls using it
     */
    private List<Control> getControlsUsing( String strId )
    {
        List<Control> listControls = new ArrayList<>( );
        if ( StringUtils.isBlank( strId ) )
        {
            return listControls;
        }
        for ( Control control : ControlHome.getControlsList( ) )
        {
            if ( strId.trim( ).equals( StringUtils.trim( control.getValue( ) ) ) && isPatternValidator( control.getValidatorName( ) ) )
            {
                listControls.add( control );
            }
        }
        return listControls;
    }

    /**
     * Checks whether a validator checks the answers against a regular expression whose id is the value of the control (the validators are CDI
     * proxies of IValidator, so their class cannot be tested).
     *
     * @param strValidatorName
     *            the bean name of the validator of a control
     * @return true for the pattern, file type pattern and geolocation address pattern validators
     */
    private boolean isPatternValidator( String strValidatorName )
    {
        return strValidatorName != null && ( strValidatorName.equals( _strPatternValidator ) || strValidatorName.equals( _strPatternFileTypeValidator )
                || strValidatorName.equals( _strGeolocationAddressPatternValidator ) );
    }

    /**
     * Describes a question using the regular expression: the title of its form and its own title.
     *
     * @param nIdQuestion
     *            the id of the question
     * @param locale
     *            the locale
     * @return the description of the usage
     */
    private String getUsage( int nIdQuestion, Locale locale )
    {
        Question question = QuestionHome.findByPrimaryKey( nIdQuestion );
        String strQuestion = ( question != null ) ? question.getTitle( ) : String.valueOf( nIdQuestion );
        Step step = ( question != null ) ? StepHome.findByPrimaryKey( question.getIdStep( ) ) : null;
        Form form = ( step != null ) ? FormHome.findByPrimaryKey( step.getIdForm( ) ) : null;
        String strForm = ( form != null ) ? form.getTitle( ) : StringUtils.EMPTY;
        return I18nService.getLocalizedString( MESSAGE_USAGE, new Object [ ] {
                strForm, strQuestion
        }, locale );
    }
}
