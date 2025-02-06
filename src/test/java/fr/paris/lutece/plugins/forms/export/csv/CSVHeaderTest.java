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
package fr.paris.lutece.plugins.forms.export.csv;

import java.util.List;

import fr.paris.lutece.plugins.forms.business.Question;
import junit.framework.TestCase;

public class CSVHeaderTest extends TestCase
{

    public void testAddHeader( )
    {
        Question q1 = getQuestion( 3, 0 );
        Question q2 = getQuestion( 2, 0 );
        Question q3 = getQuestion( 1, 0 );

        CSVHeader header = new CSVHeader( );

        header.addHeader( q1 );
        header.addHeader( q2 );
        header.addHeader( q3 );

        List<Question> questions = header.getQuestionColumns( );

        assertNotNull( questions );
        assertEquals( 3, questions.size( ) );
        assertEquals( q1, questions.get( 0 ) );
        assertEquals( q2, questions.get( 1 ) );
        assertEquals( q3, questions.get( 2 ) );
    }

    public void testAddHeaderSameQuestion( )
    {
        Question q1 = getQuestion( 1, 0 );
        Question q1_bis = getQuestion( 1, 0 );

        CSVHeader header = new CSVHeader( );

        header.addHeader( q1 );
        header.addHeader( q1_bis );

        List<Question> questions = header.getQuestionColumns( );

        assertNotNull( questions );
        assertEquals( 1, questions.size( ) );
        assertEquals( q1, questions.get( 0 ) );
    }

    public void testAddHeaderIteration( )
    {
        Question q1 = getQuestion( 1, 0 );
        Question q2 = getQuestion( 2, 0 );
        Question q1_bis = getQuestion( 1, 1 );

        CSVHeader header = new CSVHeader( );

        header.addHeader( q1 );
        header.addHeader( q2 );
        header.addHeader( q1_bis );

        List<Question> questions = header.getQuestionColumns( );

        assertNotNull( questions );
        assertEquals( 3, questions.size( ) );
        assertEquals( q1, questions.get( 0 ) );
        assertEquals( q1_bis, questions.get( 1 ) );
        assertEquals( q2, questions.get( 2 ) );
    }

    public void testAddHeaderIterationWithHole( )
    {
        Question q1 = getQuestion( 1, 0 );
        Question q2 = getQuestion( 2, 0 );
        Question q1_bis = getQuestion( 1, 2 );
        Question q1_ter = getQuestion( 1, 1 );

        CSVHeader header = new CSVHeader( );

        header.addHeader( q1 );
        header.addHeader( q2 );
        header.addHeader( q1_bis );
        header.addHeader( q1_ter );

        List<Question> questions = header.getQuestionColumns( );

        assertNotNull( questions );
        assertEquals( 4, questions.size( ) );
        assertEquals( q1, questions.get( 0 ) );
        assertEquals( q1_ter, questions.get( 1 ) );
        assertEquals( q1_bis, questions.get( 2 ) );
        assertEquals( q2, questions.get( 3 ) );
    }

    private Question getQuestion( int nId, int nIterationNumber )
    {
        Question question = new Question( );
        question.setId( nId );
        question.setIterationNumber( nIterationNumber );
        return question;
    }

}
