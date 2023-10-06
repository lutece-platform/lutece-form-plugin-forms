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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;

/**
 * This class represents a CSV header
 *
 */
public class CSVHeader
{
    private final List<Question> _listQuestionColumns;
    private final List<Question> _listIterationQuestionColumns;

    /**
     * Constructor
     */
    public CSVHeader( )
    {
        _listQuestionColumns = new ArrayList<>();
        _listIterationQuestionColumns = new ArrayList<>();
    }

    /**
     * Adds the header for the specified question
     * 
     * @param question
     *            the question to add
     */
    public void addHeader( Question question )
    {
        ListIterator<Question> listIterator = _listQuestionColumns.listIterator( );
        boolean foundQuestionWithSameId = false;
        while ( listIterator.hasNext( ) )
        {
            Question aQuestion = listIterator.next( );
            if ( aQuestion.getId( ) == question.getId( ) )
            {
                if ( aQuestion.getIterationNumber( ) == question.getIterationNumber( ) )
                {
                    return;
                }
                if ( aQuestion.getIterationNumber( ) > question.getIterationNumber( ) )
                {
                    listIterator.previous( );
                    listIterator.add( question );
                    return;
                }
                foundQuestionWithSameId = true;
            }
            else
                if ( foundQuestionWithSameId )
                {
                    listIterator.previous( );
                    listIterator.add( question );
                    return;
                }
        }
        _listQuestionColumns.add( question );
    }
    
    public void addHeadersWithIterations(Question question, List<FormResponse> formResponseList)
    {
    	for (FormResponse formResponse : formResponseList)
    	{
    		List<FormQuestionResponse> formQuestionResponseList = FormQuestionResponseHome.findFormQuestionResponseByResponseQuestion(formResponse.getId(), question.getId());
    		for (FormQuestionResponse formQuestionResponse : formQuestionResponseList)
        	{
    			if (!_listQuestionColumns.contains(formQuestionResponse.getQuestion()))
    			{
    				_listQuestionColumns.add(formQuestionResponse.getQuestion());
    			}
				
        		if (formQuestionResponseList.size() > 1 && !_listIterationQuestionColumns.contains(formQuestionResponse.getQuestion()))
        		{
        			_listIterationQuestionColumns.add(formQuestionResponse.getQuestion());
        		}
        	}
    	}
    }

    /**
     * @return the _listFinalColumnToExport
     */
    public List<Question> getQuestionColumns( )
    {
        return _listQuestionColumns;
    }

	public List<Question> getIterationsQuestionColumns() 
	{
		return _listIterationQuestionColumns;
	}
}
