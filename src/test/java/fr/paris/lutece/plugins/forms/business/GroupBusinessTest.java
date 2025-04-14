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

import org.junit.jupiter.api.Test;

import fr.paris.lutece.test.LuteceTestCase;

/**
 * This is the business class test for the object Group
 */
public class GroupBusinessTest extends LuteceTestCase
{
    private static final String TITLE1 = "Title1";
    private static final String TITLE2 = "Title2";
    private static final String DESCRIPTION1 = "Description1";
    private static final String DESCRIPTION2 = "Description2";
    private static final int IDSTEP1 = 1;
    private static final int IDSTEP2 = 2;
    private static final int ITERATIONNUMBER1 = 1;
    private static final int ITERATIONNUMBER2 = 2;

    /**
     * test Group
     */
    @Test
    public void testBusiness( )
    {
        // Initialize an object
        Group group = new Group( );
        group.setTitle( TITLE1 );
        group.setDescription( DESCRIPTION1 );
        group.setIdStep( IDSTEP1 );
        group.setIterationMin( ITERATIONNUMBER1 );

        // Create test
        GroupHome.create( group );
        Group groupStored = GroupHome.findByPrimaryKey( group.getId( ) );
        assertEquals( groupStored.getTitle( ), group.getTitle( ) );
        assertEquals( groupStored.getDescription( ), group.getDescription( ) );
        assertEquals( groupStored.getIdStep( ), group.getIdStep( ) );
        assertEquals( groupStored.getIterationMin( ), group.getIterationMin( ) );

        // Update test
        group.setTitle( TITLE2 );
        group.setDescription( DESCRIPTION2 );
        group.setIdStep( IDSTEP2 );
        group.setIterationMin( ITERATIONNUMBER2 );
        GroupHome.update( group );
        groupStored = GroupHome.findByPrimaryKey( group.getId( ) );
        assertEquals( groupStored.getTitle( ), group.getTitle( ) );
        assertEquals( groupStored.getDescription( ), group.getDescription( ) );
        assertEquals( groupStored.getIdStep( ), group.getIdStep( ) );
        assertEquals( groupStored.getIterationMin( ), group.getIterationMin( ) );

        // List test
        List<Group> list = GroupHome.getGroupsList( );
        assertEquals( 1, list.size( ) );

        List<Integer> idStepList = new ArrayList<>( );
        list = GroupHome.getGroupsListByIdStepList( idStepList );
        assertEquals( 0, list.size( ) );

        idStepList.add( IDSTEP2 );
        list = GroupHome.getGroupsListByIdStepList( idStepList );
        assertEquals( 1, list.size( ) );

        // Delete test
        GroupHome.remove( group.getId( ) );
        groupStored = GroupHome.findByPrimaryKey( group.getId( ) );
        assertNull( groupStored );

    }

}
