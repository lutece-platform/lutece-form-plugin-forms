/*
 * Copyright (c) 2002-2021, City of Paris
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
package fr.paris.lutece.plugins.forms.export;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseStep;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.export.FormExportConfig;
import fr.paris.lutece.plugins.forms.business.export.FormExportConfigHome;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeMyLuteceUserAttribute;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeNumbering;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeText;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

/**
 * This is the manager class for different entry type
 */
public final class ExportServiceManager
{
    private static final String FORM_NAME_KEY = "form_name";
    private static final String FORM_NAME_TITLE_KEY = "forms.modify_form.export.form.title.key";
    private static final String ID_TECH_KEY = "id_tech";
    private static final String ID_TECH_TITLE_KEY = "forms.modify_form.export.id.title.key";
    private final List<IFormatExport> _listFormatExport;

    /**
     * Constructor for EntryServiceManager class
     */
    private ExportServiceManager( )
    {
        _listFormatExport = SpringContextService.getBeansOfType( IFormatExport.class );
    }

    /**
     * Gives the instance
     * 
     * @return the instance
     */
    public static ExportServiceManager getInstance( )
    {
        return EntryServiceManagerHolder._instance;
    }

    /**
     * Get the right IValidator
     * 
     * @param strFormatExport
     *            The format export name
     * @return the IValidator
     */
    public IFormatExport getFormatExport( String strFormatExport )
    {
        for ( IFormatExport formatExport : _listFormatExport )
        {
            if ( strFormatExport.equals( formatExport.getFormatExportBeanName( ) ) )
            {
                return formatExport;
            }
        }

        return null;
    }

    /**
     * Get the available IValidator list for the given entryType
     * 
     * @return the ReferenceList of IFormatExport
     */
    public ReferenceList getRefListFormatExport( )
    {
        ReferenceList refListFormatExport = new ReferenceList( );

        for ( IFormatExport formatExport : _listFormatExport )
        {
            refListFormatExport.addItem( formatExport.getFormatExportBeanName( ), formatExport.getFormatExportDisplayName( ) );
        }

        return refListFormatExport;
    }

    /**
     * This class holds the EntryServiceManager instance
     *
     */
    private static class EntryServiceManagerHolder
    {
        private static ExportServiceManager _instance = new ExportServiceManager( );
    }

    public ReferenceList createReferenceListExportConfigOption( Form form, Locale locale )
    {
        ReferenceList referenceList = new ReferenceList( );
        referenceList.addItem( FORM_NAME_KEY, I18nService.getLocalizedString( FORM_NAME_TITLE_KEY, locale ) );
        referenceList.addItem( ID_TECH_KEY, I18nService.getLocalizedString( ID_TECH_TITLE_KEY, locale ) );

        List<Question> questionList = QuestionHome.getListQuestionByIdForm( form.getId( ) );

        for ( Question question : questionList )
        {
            IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( question.getEntry( ) );

            if ( entryTypeService instanceof EntryTypeText || entryTypeService instanceof EntryTypeNumbering
                    || entryTypeService instanceof EntryTypeMyLuteceUserAttribute )
            {
                referenceList.addItem( question.getId( ), question.getTitle( ) );
            }
        }

        return referenceList;
    }

    public List<FormExportConfig> createReferenceListExportConfig( Form form, Locale locale )
    {
        List<FormExportConfig> configList = FormExportConfigHome.findByForm( form.getId( ) );
        for ( FormExportConfig config : configList )
        {
            if ( config.getField( ).equals( FORM_NAME_KEY ) )
            {
                config.setFieldTitle( I18nService.getLocalizedString( FORM_NAME_TITLE_KEY, locale ) );
            }
            else
                if ( config.getField( ).equals( ID_TECH_KEY ) )
                {
                    config.setFieldTitle( I18nService.getLocalizedString( ID_TECH_TITLE_KEY, locale ) );
                }
                else
                {
                    Question question = QuestionHome.findByPrimaryKey( Integer.parseInt( config.getField( ) ) );
                    if ( question != null )
                    {
                        config.setFieldTitle( question.getTitle( ) );
                    }
                }
        }
        return configList;
    }

    public List<String> generateNameComponents( Form form, FormResponse response, List<FormExportConfig> configList )
    {
        List<String> valueList = new ArrayList<>( );

        for ( FormExportConfig config : configList )
        {
            if ( config.getField( ).equals( FORM_NAME_KEY ) )
            {
                valueList.add( form.getTitle( ) );
            }
            else
                if ( config.getField( ).equals( ID_TECH_KEY ) )
                {
                    valueList.add( String.valueOf( response.getId( ) ) );
                }
                else
                {
                    for ( FormResponseStep step : response.getSteps( ) )
                    {
                        FormQuestionResponse formQuestionResponse = step.getQuestions( ).stream( )
                                .filter( fqr -> fqr.getQuestion( ).getId( ) == Integer.parseInt( config.getField( ) ) ).findFirst( ).orElse( null );
                        if ( formQuestionResponse != null )
                        {
                            IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( formQuestionResponse.getQuestion( ).getEntry( ) );
                            valueList.add( entryTypeService.getResponseValueForExport( formQuestionResponse.getQuestion( ).getEntry( ), null,
                                    formQuestionResponse.getEntryResponse( ).get( 0 ), null ) );
                            break;
                        }
                    }
                }
        }

        return valueList;
    }
}
