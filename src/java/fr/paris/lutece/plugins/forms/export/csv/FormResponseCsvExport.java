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
package fr.paris.lutece.plugins.forms.export.csv;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseStep;
import fr.paris.lutece.plugins.forms.business.MultiviewConfig;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.business.Transition;
import fr.paris.lutece.plugins.forms.business.TransitionHome;
import fr.paris.lutece.plugins.forms.service.StepService;
import fr.paris.lutece.portal.service.i18n.I18nService;

/**
 * 
 * Class which contains all the data needed to build the CSV export
 *
 */
public class FormResponseCsvExport
{

    private static final String MESSAGE_EXPORT_FORM_TITLE = "forms.export.formResponse.form.title";
    private static final String MESSAGE_EXPORT_FORM_STATE = "forms.export.formResponse.form.state";
    private static final String MESSAGE_EXPORT_FORM_DATE_CREATION = "forms.export.formResponse.form.date.creation";
    private static final String MESSAGE_EXPORT_FORM_DATE_UPDATE = "forms.export.formResponse.form.date.update";

    private final CSVHeader _csvHeader = new CSVHeader( );
    private String _csvSeparator;

    public FormResponseCsvExport( )
    {
        _csvSeparator = MultiviewConfig.getInstance( ).getCsvSeparator( );
    }

    /**
     * Build the CSV string for column line
     */
    public String buildCsvColumnToExport( FormResponse formResponse )
    {
        List<Step> listSteps = StepHome.getStepsListByForm( formResponse.getFormId( ) );
        List<Transition> listTransitions = TransitionHome.getTransitionsListFromForm( formResponse.getFormId( ) );

        List<Step> orderedStepList = StepService.sortStepsWithTransitions( listSteps, listTransitions );

        for ( Step step : orderedStepList )
        {
            List<Question> questionList = QuestionHome.getQuestionsListByStep( step.getId( ) );
            if (questionList != null) {
            	questionList.sort(Comparator.comparingInt(Question::getExportDisplayOrder));
            }
            for ( Question question : questionList )
            {
                if ( question.isResponseExportable( ) )
                {
                    _csvHeader.addHeader( question );
                }
            }
        }

        StringBuilder sbCsvColumn = new StringBuilder( );

        sbCsvColumn.append( CSVUtil.safeString( I18nService.getLocalizedString( MESSAGE_EXPORT_FORM_TITLE, I18nService.getDefaultLocale( ) ) ) );
        sbCsvColumn.append( _csvSeparator );
        sbCsvColumn.append( CSVUtil.safeString( I18nService.getLocalizedString( MESSAGE_EXPORT_FORM_DATE_CREATION, I18nService.getDefaultLocale( ) ) ) );
        sbCsvColumn.append( _csvSeparator );
        sbCsvColumn.append( CSVUtil.safeString( I18nService.getLocalizedString( MESSAGE_EXPORT_FORM_DATE_UPDATE, I18nService.getDefaultLocale( ) ) ) );
        sbCsvColumn.append( _csvSeparator );
        sbCsvColumn.append( CSVUtil.safeString( I18nService.getLocalizedString( MESSAGE_EXPORT_FORM_STATE, I18nService.getDefaultLocale( ) ) ) );
        sbCsvColumn.append( _csvSeparator );

        for ( Question question : _csvHeader.getColumnToExport( ) )
        {
            sbCsvColumn.append( CSVUtil.safeString( CSVUtil.buildColumnName( question ) ) ).append( _csvSeparator );
        }

        return sbCsvColumn.toString( );
    }

    /**
     * Build the CSV string for all data lines
     */
    public String buildCsvDataToExport( FormResponse formResponse, String state )
    {
        CSVDataLine csvDataLine = new CSVDataLine( formResponse, state, _csvSeparator );

        for ( FormResponseStep formResponseStep : formResponse.getSteps( ) )
        {
            for ( FormQuestionResponse formQuestionResponse : formResponseStep.getQuestions( ) )
            {
                if ( formQuestionResponse.getQuestion( ).isResponseExportable( ) )
                {
                    csvDataLine.addData( formQuestionResponse );
                }
            }
        }

        StringBuilder sbCsvData = new StringBuilder( );

        StringBuilder sbRecordContent = new StringBuilder( );
        sbRecordContent.append( csvDataLine.getCommonDataToExport( ) );

        for ( Question question : _csvHeader.getColumnToExport( ) )
        {
            sbRecordContent.append( CSVUtil.safeString( Objects.toString( csvDataLine.getDataToExport( question ), StringUtils.EMPTY ) ) )
                    .append( _csvSeparator );
        }

        sbCsvData.append( sbRecordContent.toString( ) );

        return sbCsvData.toString( );
    }
}
