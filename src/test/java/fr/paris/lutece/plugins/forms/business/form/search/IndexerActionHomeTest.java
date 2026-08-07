/*
 * Copyright (c) 2002-2026, City of Paris
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
package fr.paris.lutece.plugins.forms.business.form.search;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.service.FormsPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.test.LuteceTestCase;


public class IndexerActionHomeTest extends LuteceTestCase
{
    private static final int ID_FORM_RESPONSE_TO_DELETE = 999901;
    private static final int ID_FORM_RESPONSE_TO_KEEP = 999902;

    private Plugin _plugin;

    @BeforeEach
    protected void setUp( ) throws Exception
    {
        super.setUp( );
        _plugin = FormsPlugin.getPlugin( );
    }

    /**
     * removeByFormResponse should delete every IndexerAction row for the given form response id,
     * without touching rows that belong to a different form response.
     */
    @Test
    public void testRemoveByFormResponse_onlyDeletesRowsForGivenResponse( )
    {
        IndexerAction actionCreate = createIndexerAction( ID_FORM_RESPONSE_TO_DELETE, IndexerAction.TASK_CREATE );
        IndexerAction actionModify = createIndexerAction( ID_FORM_RESPONSE_TO_DELETE, IndexerAction.TASK_MODIFY );
        IndexerAction actionOther = createIndexerAction( ID_FORM_RESPONSE_TO_KEEP, IndexerAction.TASK_CREATE );

        try
        {
            assertEquals( 2, countActionsForResponse( ID_FORM_RESPONSE_TO_DELETE ),
                    "Sanity check: 2 pending actions expected for the response to delete" );
            assertEquals( 1, countActionsForResponse( ID_FORM_RESPONSE_TO_KEEP ),
                    "Sanity check: 1 pending action expected for the other response" );

            IndexerActionHome.removeByFormResponse( ID_FORM_RESPONSE_TO_DELETE, _plugin );

            assertEquals( 0, countActionsForResponse( ID_FORM_RESPONSE_TO_DELETE ),
                    "All indexer actions for the deleted response should be gone" );
            assertEquals( 1, countActionsForResponse( ID_FORM_RESPONSE_TO_KEEP ),
                    "The other response's indexer action must not be affected" );
        }
        finally
        {
            IndexerActionHome.remove( actionCreate.getIdAction( ), _plugin );
            IndexerActionHome.remove( actionModify.getIdAction( ), _plugin );
            IndexerActionHome.remove( actionOther.getIdAction( ), _plugin );
        }
    }

    /**
     * Calling removeByFormResponse for a response id that has no pending indexer action should be a
     * harmless no-op (no exception, nothing else affected).
     */
    @Test
    public void testRemoveByFormResponse_noRowsForResponse_doesNothing( )
    {
        IndexerAction actionOther = createIndexerAction( ID_FORM_RESPONSE_TO_KEEP, IndexerAction.TASK_CREATE );

        try
        {
            IndexerActionHome.removeByFormResponse( ID_FORM_RESPONSE_TO_DELETE, _plugin );

            assertEquals( 1, countActionsForResponse( ID_FORM_RESPONSE_TO_KEEP ),
                    "Unrelated response's indexer action must still be present" );
        }
        finally
        {
            IndexerActionHome.remove( actionOther.getIdAction( ), _plugin );
        }
    }

    private IndexerAction createIndexerAction( int nIdFormResponse, int nIdTask )
    {
        IndexerAction action = new IndexerAction( );
        action.setIdFormResponse( nIdFormResponse );
        action.setIdTask( nIdTask );
        IndexerActionHome.create( action, _plugin );
        return action;
    }

    private long countActionsForResponse( int nIdFormResponse )
    {
        List<IndexerAction> listActions = IndexerActionHome.getList( _plugin );
        return listActions.stream( ).filter( a -> a.getIdFormResponse( ) == nIdFormResponse ).count( );
    }
}