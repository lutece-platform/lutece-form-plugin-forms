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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.CollectionUtils;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * This class represents a CSV line
 *
 */
public class CSVDataLine
{
    private static final String RESPONSE_SEPARATOR = " ";
    
    private static final String CONSTANT_COMMA = ",";

    private final Map<Integer, Map<Integer, String>> _mapDataToExport;
    private final String _commonDataToExport;

    /**
     * Constructor
     * 
     * @param formResponse
     *            the form response associated to this instance
     */
    public CSVDataLine( FormResponse formResponse, String state, String csvSeparator )
    {
        _mapDataToExport = new HashMap<>( );

        Locale locale = I18nService.getDefaultLocale( );
        DateFormat dateFormat = new SimpleDateFormat( AppPropertiesService.getProperty( FormsConstants.PROPERTY_EXPORT_FORM_DATE_CREATION_FORMAT ), locale );
        Form form = FormHome.findByPrimaryKey( formResponse.getFormId( ) );
        StringBuilder commonData = new StringBuilder( );
        commonData.append( CSVUtil.safeString( form.getTitle( ) ) ).append( csvSeparator );
        commonData.append( CSVUtil.safeString( dateFormat.format( formResponse.getCreation( ) ) ) ).append( csvSeparator );
        commonData.append( CSVUtil.safeString( dateFormat.format( formResponse.getUpdate( ) ) ) ).append( csvSeparator );
        commonData.append( CSVUtil.safeString( state ) ).append( csvSeparator );
        _commonDataToExport = commonData.toString( );
    }

    /**
     * 
     * @param formQuestionResponse
     *            The data to add
     */
    public void addData( FormQuestionResponse formQuestionResponse )
    {
        Question question = formQuestionResponse.getQuestion( );
        Map<Integer, List<String>> mapIterationsResponseValue = responseToIterationsStrings( formQuestionResponse );
        StringBuilder sbReponseValues = new StringBuilder( );
        
        for (Entry<Integer, List<String>> entry : mapIterationsResponseValue.entrySet())
        {
        	Integer iteration = entry.getKey();
        	for ( String strResponseValue : entry.getValue() )
            {
                sbReponseValues.append( strResponseValue ).append( RESPONSE_SEPARATOR );
            }
            if ( !_mapDataToExport.containsKey( question.getId( ) ) )
            {
            	_mapDataToExport.put(question.getId( ), new HashMap<>());
            }
            _mapDataToExport.get(question.getId()).put(iteration, sbReponseValues.toString() );
        }
    }
    
    private Map<Integer, List<String>> responseToIterationsStrings(FormQuestionResponse formQuestionResponse)
    {
    	Map<Integer, List<String>> mapResponseValue = new HashMap<>();
    	fr.paris.lutece.plugins.genericattributes.business.Entry entry = formQuestionResponse.getQuestion().getEntry();
    	
    	String strExportFieldsPrefix = entry.getEntryType().getBeanName() != null ? entry.getEntryType().getBeanName() : StringUtils.EMPTY;
    	
    	String strExportFieldsList = AppPropertiesService.getProperty( strExportFieldsPrefix + FormsConstants.PROPERTY_EXPORT_FIELD_LIST_PREFIX , null );
    	List<String> exportFieldsList = StringUtils.isEmpty(strExportFieldsList) ? new ArrayList<>() : Arrays.asList( strExportFieldsList.split( CONSTANT_COMMA ) );
    	
        for ( Response response : formQuestionResponse.getEntryResponse( ) )
        {
        	if ( CollectionUtils.isEmpty(exportFieldsList) || exportFieldsList.contains(response.getField().getCode()))
            {
        		String strResponseValue = EntryTypeServiceManager.getEntryTypeService( entry ).getResponseValueForExport( entry, null, response,
                        I18nService.getDefaultLocale( ) );
                if ( strResponseValue != null )
                {
                	int nIterationNumber = response.getIterationNumber();
                	if (nIterationNumber == NumberUtils.INTEGER_MINUS_ONE)
                	{
                		nIterationNumber = formQuestionResponse.getQuestion().getIterationNumber();
                	}
                	
                	if (mapResponseValue.containsKey(response.getIterationNumber()))
                	{
                		mapResponseValue.get(response.getIterationNumber()).add(strResponseValue);
                	}
                	else
                	{
                		List<String> listResponseValue = new ArrayList<>();
                		listResponseValue.add(strResponseValue);
                		mapResponseValue.put(nIterationNumber, listResponseValue);
                	}
                }
            }
        }
        return mapResponseValue;
    }

    /**
     * @param strColumnName
     *            The column name
     * @return the _mapDataToExport
     */
    public String getDataToExport( Question question )
    {
        return _mapDataToExport.get(question.getId()) != null ? _mapDataToExport.get(question.getId()).get(question.getIterationNumber()) : null;
    }

    public String getCommonDataToExport( )
    {
        return _commonDataToExport;
    }

}
