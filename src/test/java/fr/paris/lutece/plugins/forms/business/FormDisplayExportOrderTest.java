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
package fr.paris.lutece.plugins.forms.business;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.test.LuteceTestCase;

/**
 * The displays of a step in the export order of the PDF export (FP-03): a group takes the place of its first question in the export order, not
 * the export order of an unrelated question whose id equals the id of the group.
 */
public class FormDisplayExportOrderTest extends LuteceTestCase
{
    private static final int ID_STEP = 987001;
    private static final int ID_OTHER_STEP = 987002;

    private final List<Integer> _listQuestions = new ArrayList<>( );
    private final List<Integer> _listDisplays = new ArrayList<>( );

    /**
     * Removes the data of the test
     */
    @AfterEach
    public void removeData( )
    {
        _listDisplays.forEach( FormDisplayHome::remove );
        _listQuestions.forEach( QuestionHome::remove );
    }

    private Question question( String strTitle, int nIdStep, int nExportOrder )
    {
        Question question = new Question( );
        question.setTitle( strTitle );
        question.setCode( strTitle );
        question.setColumnTitle( strTitle );
        question.setDescription( strTitle );
        question.setIdStep( nIdStep );
        question.setExportDisplayOrder( nExportOrder );
        QuestionHome.create( question );
        _listQuestions.add( question.getId( ) );

        return question;
    }

    private FormDisplay display( String strType, int nIdComposite, int nIdParent, int nDisplayOrder )
    {
        FormDisplay formDisplay = new FormDisplay( );
        formDisplay.setStepId( ID_STEP );
        formDisplay.setCompositeType( strType );
        formDisplay.setCompositeId( nIdComposite );
        formDisplay.setParentId( nIdParent );
        FormDisplayHome.create( formDisplay );
        _listDisplays.add( formDisplay.getId( ) );
        // create appends the display to its parent: the order of the test is set by an update
        formDisplay.setDisplayOrder( nDisplayOrder );
        FormDisplayHome.update( formDisplay );

        return formDisplay;
    }

    private static List<Integer> ids( int nIdParent )
    {
        return FormDisplayHome.getFormDisplayListByParentOrderByQuestionExportDisplayOrder( ID_STEP, nIdParent ).stream( ).map( FormDisplay::getId )
                .collect( Collectors.toList( ) );
    }

    /**
     * Step: QA, group G (holding a sub-group S holding QB), QC, and an empty group E placed first
     */
    @Test
    public void testGroupTakesThePlaceOfItsFirstQuestion( )
    {
        Question qa = question( "QA", ID_STEP, 1 );
        Question qb = question( "QB", ID_STEP, 2 );
        Question qc = question( "QC", ID_STEP, 3 );
        // a question of another form whose id is the id_composite of the group G, with a high export order
        Question homonym = question( "QZ", ID_OTHER_STEP, 99 );

        FormDisplay e = display( "group", homonym.getId( ) + 1, 0, 0 );
        FormDisplay da = display( "question", qa.getId( ), 0, 1 );
        FormDisplay g = display( "group", homonym.getId( ), 0, 2 );
        FormDisplay dc = display( "question", qc.getId( ), 0, 3 );
        FormDisplay s = display( "group", homonym.getId( ) + 2, g.getId( ), 0 );
        FormDisplay db = display( "question", qb.getId( ), s.getId( ), 0 );

        assertEquals( List.of( da.getId( ), g.getId( ), dc.getId( ), e.getId( ) ), ids( 0 ) );
        assertEquals( List.of( s.getId( ) ), ids( g.getId( ) ) );
        assertEquals( List.of( db.getId( ) ), ids( s.getId( ) ) );

        // export order set by hand: QC, QA, QB
        qc.setExportDisplayOrder( 1 );
        qa.setExportDisplayOrder( 2 );
        qb.setExportDisplayOrder( 3 );
        QuestionHome.update( qc );
        QuestionHome.update( qa );
        QuestionHome.update( qb );

        assertEquals( List.of( dc.getId( ), da.getId( ), g.getId( ), e.getId( ) ), ids( 0 ) );
    }

    /**
     * Export order never set (all 0): the order of the form
     */
    @Test
    public void testNoExportOrderKeepsTheOrderOfTheForm( )
    {
        Question qa = question( "QA", ID_STEP, 0 );
        Question qb = question( "QB", ID_STEP, 0 );
        Question qc = question( "QC", ID_STEP, 0 );

        FormDisplay dc = display( "question", qc.getId( ), 0, 3 );
        FormDisplay da = display( "question", qa.getId( ), 0, 1 );
        FormDisplay g = display( "group", qc.getId( ) + 1000, 0, 2 );
        display( "question", qb.getId( ), g.getId( ), 0 );

        assertEquals( List.of( da.getId( ), g.getId( ), dc.getId( ) ), ids( 0 ) );
    }
}
