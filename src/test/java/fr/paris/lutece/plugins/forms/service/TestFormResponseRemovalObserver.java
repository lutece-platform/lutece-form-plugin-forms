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
package fr.paris.lutece.plugins.forms.service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import fr.paris.lutece.plugins.forms.service.event.FormResponseEvent;
import fr.paris.lutece.portal.service.event.EventAction;
import fr.paris.lutece.portal.service.event.Type;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;

/**
 * Test observer recording the identifiers of the form responses whose deletion event was fired
 */
@ApplicationScoped
public class TestFormResponseRemovalObserver
{
    private final Set<Integer> _setRemovedIds = ConcurrentHashMap.newKeySet( );

    /**
     * Records a form response deletion event
     *
     * @param event
     *            the event
     */
    public void removedFormResponse( @ObservesAsync @Type( EventAction.REMOVE ) FormResponseEvent event )
    {
        _setRemovedIds.add( event.getFormResponseId( ) );
    }

    /**
     * Gets the identifiers of the form responses whose deletion event was received
     *
     * @return the identifiers
     */
    public Set<Integer> getRemovedIds( )
    {
        return _setRemovedIds;
    }
}
