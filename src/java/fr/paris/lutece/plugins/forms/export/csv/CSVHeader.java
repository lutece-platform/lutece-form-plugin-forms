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

import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.Question;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * This class represents a CSV header
 *
 */
public class CSVHeader
{
    Map <ArrayList<Integer>, Question> _mapQuestionAndIterationColumn = new HashMap<>();
    private final List<Question> _listQuestionColumns;
    private int _numberOfQuestionColumns;

    /**
     * Constructor
     */
    public CSVHeader( )
    {
        _listQuestionColumns = new ArrayList<>();
        _numberOfQuestionColumns = 0;
        _mapQuestionAndIterationColumn = new HashMap<>();
    }


    /**
     * Adds the header for the specified question
     * 
     * @param question
     *            the question to add
     */
    @Deprecated
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

    /**
     * Builds the list of column names to export
     * @param listQuestions
     * @return the list of column names to export
     */
   public List<String> getListColumnNameToExport(List<Question> listQuestions)
    {
        List <String> listQuestionNameColumnToExport = new ArrayList<>();
        if(listQuestions == null || listQuestions.isEmpty())
        {
            return listQuestionNameColumnToExport;
        }
        listQuestions.sort((q1, q2) -> q1.getExportDisplayOrder() - q2.getExportDisplayOrder());
        for (int i = 0; i < listQuestions.size(); i++) {
            Question question = listQuestions.get(i);
            if ( question.isResponseExportable( ) ) {
                int formQuestionResponseWithMaxIte = FormQuestionResponseHome.findMaxIterationNumber(question.getId());
                if(formQuestionResponseWithMaxIte > 0)
                {
                    for (int j = 0; j < formQuestionResponseWithMaxIte + 1; j++) {
                        listQuestionNameColumnToExport.add(CSVUtil.buildColumnName(question.getTitle(), j));
                    }
                }
                else
                {
                    listQuestionNameColumnToExport.add(question.getTitle());
                }
            }
        }
        return listQuestionNameColumnToExport;
    }

    /**
     * Returns the the question(and iterations) associated to the specified column index
     * @param listQuestions
     * @return the map of column index and question(and iterations)
     */
    public Map <ArrayList<Integer>, Question> getColumnNumberForQuestions(List<Question> listQuestions)
    {
        _mapQuestionAndIterationColumn = new HashMap<>();
        if(listQuestions == null || listQuestions.isEmpty())
        {
            return _mapQuestionAndIterationColumn;
        }
        for (int i = 0; i < listQuestions.size(); i++) {
            Question question = listQuestions.get(i);
            if ( question.isResponseExportable( ) ) {
                int formQuestionResponseWithMaxIte = FormQuestionResponseHome.findMaxIterationNumber(question.getId());
                if(formQuestionResponseWithMaxIte > 0)
                {                     ArrayList<Integer> columnCountAndQuestionIteration = new ArrayList<>();
                        columnCountAndQuestionIteration.add(question.getExportDisplayOrder());
                        columnCountAndQuestionIteration.add(formQuestionResponseWithMaxIte);
                    _mapQuestionAndIterationColumn.put(columnCountAndQuestionIteration, question);
                }
                else
                {
                    ArrayList<Integer> columnCountAndQuestionIteration = new ArrayList<>();
                    columnCountAndQuestionIteration.add(question.getExportDisplayOrder());
                    columnCountAndQuestionIteration.add(0);
                    _mapQuestionAndIterationColumn.put(columnCountAndQuestionIteration, question);
                }
            }
        }
       return _mapQuestionAndIterationColumn;
    }

    /**
     * Sorts the map by the first item of the key (column number) and then by the second item of the key (iteration number)
     * @param mapQuestionAndIterationColumn
     *
     * @return the sorted map
     */
    public static <K extends List<Integer>, V> Map<K, V> sortByColumnNumber(Map<K, V> map) {
        if(map == null || map.isEmpty())
        {
            return map;
        }
             return map.entrySet()
                    .stream()
                    .sorted(Comparator.comparingInt(entry -> entry.getKey().get(0)))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new
                    ));


    }

    /**
     * @return the _mapQuestionAndIterationColumn
     */
   public Map <ArrayList<Integer>, Question> getMapQuestionAndIterationColumn()
    {
        return _mapQuestionAndIterationColumn;
    }

    /**
     * Set the map of question and iteration column
     * @param mapQuestionAndIterationColumn
     */
    public void setMapQuestionAndIterationColumn(Map <ArrayList<Integer>, Question> mapQuestionAndIterationColumn)
    {
        _mapQuestionAndIterationColumn = mapQuestionAndIterationColumn;
    }


    /**
     * @return the _listFinalColumnToExport
     */
    public List<Question> getQuestionColumns( )
    {
        return _listQuestionColumns;
    }

}
