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
package fr.paris.lutece.plugins.forms.business;

import java.util.List;

import fr.paris.lutece.test.LuteceTestCase;

/**
 * This is the business class test for the object Question
 */
public class QuestionBusinessTest extends LuteceTestCase
{
    private static final String TITLE1 = "Title1";
    private static final String TITLE2 = "Title2";
    private static final String DESCRIPTION1 = "Description1";
    private static final String DESCRIPTION2 = "Description2";
    private static final int IDENTRY1 = 1;
    private static final int IDENTRY2 = 2;

    /**
     * test Question
     */
    public void testBusiness( )
    {
        // Initialize an object
        Question question = new Question( );
        question.setCode( "code" );
        question.setColumnTitle( "column_title" );
        question.setTitle( TITLE1 );
        question.setDescription( DESCRIPTION1 );
        question.setIdEntry( IDENTRY1 );

        // Create test
        QuestionHome.create( question );
        Question questionStored = QuestionHome.findByPrimaryKey( question.getId( ) );
        assertEquals( questionStored.getTitle( ), question.getTitle( ) );
        assertEquals( questionStored.getDescription( ), question.getDescription( ) );
        assertEquals( questionStored.getIdEntry( ), question.getIdEntry( ) );

        // Update test
        question.setTitle( TITLE2 );
        question.setDescription( DESCRIPTION2 );
        question.setIdEntry( IDENTRY2 );
        question.setIterationNumber( 1000 );
        QuestionHome.update( question );
        questionStored = QuestionHome.findByPrimaryKey( question.getId( ) );
        assertEquals( questionStored.getTitle( ), question.getTitle( ) );
        assertEquals( questionStored.getDescription( ), question.getDescription( ) );
        assertEquals( questionStored.getIdEntry( ), question.getIdEntry( ) );
        assertEquals( 0, questionStored.getIterationNumber( ) );

        // List test
        List<Question> list = QuestionHome.getQuestionsList( );
        assertNotNull( list );
        list.forEach( q -> {
            assertEquals( 0, q.getIterationNumber( ) );
        });

        // Delete test
        QuestionHome.remove( question.getId( ) );
        questionStored = QuestionHome.findByPrimaryKey( question.getId( ) );
        assertNull( questionStored );

    }

}
