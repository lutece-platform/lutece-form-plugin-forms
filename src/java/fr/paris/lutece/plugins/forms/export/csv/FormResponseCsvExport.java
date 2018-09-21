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
package fr.paris.lutece.plugins.forms.export.csv;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseStep;
import fr.paris.lutece.plugins.forms.util.FormsConstants;

/**
 * 
 * Class which contains all the data needed to build the CSV export
 *
 */
public class FormResponseCsvExport
{
	private final ColumnDefinition _columnDefinition = new ColumnDefinition( );
	
	private final List<CSVDataLine> _listDataToExport = new ArrayList<CSVDataLine>( );
		
	private String _strCsvColumnToExport;
	
	private String _strCsvDataToExport;
	
	/**
	 * Constructor
	 * 
	 * @param listFormResponse
	 * 			The list of data to export
	 */
	public FormResponseCsvExport( List<FormResponse> listFormResponse )
	{
		for( FormResponse formResponse : listFormResponse )
		{
			CSVDataLine csvDataLine = new CSVDataLine( );
			
			for( FormResponseStep formResponseStep : formResponse.getSteps( ) )
			{
				for( FormQuestionResponse formQuestionResponse : formResponseStep.getQuestions( ) )
				{
					_columnDefinition.addColumnDef( formQuestionResponse.getQuestion( ) );
					csvDataLine.addData( formQuestionResponse );
				}
			}
			
			_listDataToExport.add( csvDataLine );
		}
		
		buildCsvColumnToExport( );
		buildCsvDataToExport( );
	}
	
	/**
	 * Build the CSV string for column line
	 */
	private void buildCsvColumnToExport( )
	{
		StringBuilder sbCsvColumn = new StringBuilder( );
		_columnDefinition.buildColumnToExport( );
		
		for ( String strColumnName : _columnDefinition.getColumnToExport( ) ) 
		{
			sbCsvColumn.append( strColumnName ).append( FormsConstants.SEPARATOR_SEMICOLON );
		}
		
		_strCsvColumnToExport = sbCsvColumn.append( FormsConstants.END_OF_LINE ).toString( );
	}
	
	/**
	 * Build the CSV string for all data lines
	 */
	private void buildCsvDataToExport( )
	{
		StringBuilder sbCsvData = new StringBuilder( );
		
		for( CSVDataLine csvDataLine : _listDataToExport )
		{
			StringBuilder sbRecordContent = new StringBuilder( );
			
			for( String strColumnName : _columnDefinition.getColumnToExport( ) )
			{
				sbRecordContent.append( Objects.toString( csvDataLine.getDataToExport( strColumnName ), StringUtils.EMPTY ) ).append( FormsConstants.SEPARATOR_SEMICOLON );
			}
			
			sbCsvData.append( sbRecordContent.toString( ) ).append( FormsConstants.END_OF_LINE );
		}
		
		_strCsvDataToExport = sbCsvData.toString( );
	}

	/**
	 * @return the _strCsvColumnToExport
	 */
	public String getCsvColumnToExport( )
	{
		return _strCsvColumnToExport;
	}
	
	/**
	 * @return the _strCsvDataToExport
	 */
	public String getCsvDataToExport( )
	{
		return _strCsvDataToExport;
	}

}
