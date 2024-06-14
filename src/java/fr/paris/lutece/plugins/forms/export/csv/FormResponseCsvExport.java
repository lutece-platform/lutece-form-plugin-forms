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

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.MultiviewConfig;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.form.FormResponseItem;
import fr.paris.lutece.portal.service.i18n.I18nService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public String buildCsvColumnToExport( List<Question> listQuestions)
    {

        StringBuilder sbCsvColumn = new StringBuilder( );

        sbCsvColumn.append( CSVUtil.safeString( I18nService.getLocalizedString( MESSAGE_EXPORT_FORM_TITLE, I18nService.getDefaultLocale( ) ) ) );
        sbCsvColumn.append( _csvSeparator );
        sbCsvColumn.append( CSVUtil.safeString( I18nService.getLocalizedString( MESSAGE_EXPORT_FORM_DATE_CREATION, I18nService.getDefaultLocale( ) ) ) );
        sbCsvColumn.append( _csvSeparator );
        sbCsvColumn.append( CSVUtil.safeString( I18nService.getLocalizedString( MESSAGE_EXPORT_FORM_DATE_UPDATE, I18nService.getDefaultLocale( ) ) ) );
        sbCsvColumn.append( _csvSeparator );
        sbCsvColumn.append( CSVUtil.safeString( I18nService.getLocalizedString( MESSAGE_EXPORT_FORM_STATE, I18nService.getDefaultLocale( ) ) ) );
        sbCsvColumn.append( _csvSeparator );
        List<String> listColumnNames =   _csvHeader.getListColumnNameToExport( listQuestions );
        for (int i = 0; i < listColumnNames.size( ); i++)
        {
            sbCsvColumn.append( CSVUtil.safeString( listColumnNames.get( i ) ) );
            sbCsvColumn.append( _csvSeparator );

        }
        Map <ArrayList<Integer>, Question> mapQuestionAndIterationColumn =   _csvHeader.getColumnNumberForQuestions( listQuestions );
        // sort the map by the column number
        mapQuestionAndIterationColumn = CSVHeader.sortByColumnNumber(mapQuestionAndIterationColumn);
        // set the mapQuestionAndIterationColumn in the CSVHeader so it can be used in the buildCsvDataToExport method
        _csvHeader.setMapQuestionAndIterationColumn(mapQuestionAndIterationColumn);
        return sbCsvColumn.toString( );
    }

    public static List<FormResponse> getFormResponseFromItemList(List<FormResponseItem> formResponseItems)
    {
    	List<FormResponse> formResponseList = new ArrayList<>();
    	for (FormResponseItem formResponseItem : formResponseItems)
    	{
    		 FormResponse formResponse = FormResponseHome.findByPrimaryKeyForIndex( formResponseItem.getIdFormResponse() );
    		 if (formResponse != null)
    		 {
    			 formResponseList.add(formResponse);
    		 }
    	}
    	return formResponseList;
    }

    /**
     * Build mapQuestionData (a map with the question id as key and a list of responses as value) for all data lines
     * @param formResponse
     * @param csvDataLine
     * @return mapQuestionData
     */
    public Map<Integer, List<String>> buildMapQuestionDataToExport( FormResponse formResponse,  CSVDataLine csvDataLine)
    {
        // one mapQuestionData item per Question containing a list of responses (one response per iteration)
        Map<Integer, List<String>> mapQuestionData = new java.util.HashMap<>( );
        for ( FormQuestionResponse formQuestionResponse : FormQuestionResponseHome.getFormQuestionResponseListByFormResponse( formResponse.getId( ))) {
            if(formQuestionResponse != null && formQuestionResponse.getQuestion() != null) {
                Integer questionId = formQuestionResponse.getQuestion().getId();
                List<String> responseValue = csvDataLine.responseToIterationsStrings(formQuestionResponse);
                if (responseValue != null && !responseValue.isEmpty()) {
                    String responseValueString = String.join(" ", responseValue);
                    if (mapQuestionData.containsKey(questionId)) {
                        List<String> listResponse = mapQuestionData.get(questionId);
                        listResponse.add(responseValueString);
                        mapQuestionData.put(questionId, listResponse);
                    } else {
                        List<String> listResponse = new ArrayList<>();
                        listResponse.add(responseValueString);
                        mapQuestionData.put(questionId, listResponse);
                    }
                }
            }
        }
        return mapQuestionData;
    }

    /**
     * Append the response to the CSV data line (StringBuilder sbResponse)
     * @param mapQuestionData
     * @param questionIterationKey
     * @param mapQuestionAndIterationColumn
     * @param sbResponse
     * @return void
     */
    public void appendResponseToCsvDataLine(Map<Integer, List<String>> mapQuestionData, ArrayList<Integer> questionIterationKey, Map <ArrayList<Integer>, Question> mapQuestionAndIterationColumn, StringBuilder sbResponse)
    {
        Integer questionID = mapQuestionAndIterationColumn.get(questionIterationKey).getId();
        List<String> listResponse = mapQuestionData.get(questionID);
        int maxIterationNumber = questionIterationKey.get(1) + 1;
        // check not null && not empty
        for (int i = 0; i < maxIterationNumber; i++) {
            // check if the question is in the mapQuestionData and if the size of the list(iteration for this response) is greater than the iteration number of the column
            if (mapQuestionData.containsKey(questionID) && listResponse.size() > i) {
                sbResponse.append(CSVUtil.safeString(listResponse.get(i))).append(_csvSeparator);
            } else {
                sbResponse.append(CSVUtil.safeString("")).append(_csvSeparator);
            }
        }
    }
    /**
     * Build the CSV string for all data lines
     */
    public String buildCsvDataToExport( FormResponse formResponse, String state)
    {
        CSVDataLine csvDataLine = new CSVDataLine( formResponse, state, _csvSeparator );
        StringBuilder sbResponse = new StringBuilder( );
        // one mapQuestionData item per Question containing a list of responses (one response per iteration)
        Map<Integer, List<String>> mapQuestionData = buildMapQuestionDataToExport( formResponse, csvDataLine );
        Map <ArrayList<Integer>, Question> mapQuestionAndIterationColumn = _csvHeader.getMapQuestionAndIterationColumn();
        if(mapQuestionAndIterationColumn != null && !mapQuestionAndIterationColumn.isEmpty()) {
            for (ArrayList<Integer> questionIterationKey : mapQuestionAndIterationColumn.keySet()) {
               appendResponseToCsvDataLine(mapQuestionData, questionIterationKey, mapQuestionAndIterationColumn, sbResponse);
            }
        }
        StringBuilder sbCsvData = new StringBuilder( );

        StringBuilder sbRecordContent = new StringBuilder( );
        sbRecordContent.append( csvDataLine.getCommonDataToExport( ) );
        sbRecordContent.append( sbResponse.toString( ) );

        sbCsvData.append( sbRecordContent.toString( ) );

        return sbCsvData.toString( );
    }
}
