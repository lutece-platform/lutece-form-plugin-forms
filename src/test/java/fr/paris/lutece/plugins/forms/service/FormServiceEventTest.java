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
package fr.paris.lutece.plugins.forms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.RejectedExecutionException;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.service.event.FormResponseEvent;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.NotificationOptions;
import jakarta.enterprise.util.TypeLiteral;

/**
 * Form response events fired while the server stops: the executor refuses the task, the observers are notified in the
 * current thread
 */
public class FormServiceEventTest
{
    @Test
    public void testEventFiredWithTheDefaultExecutor( )
    {
        RecordingEvent event = new RecordingEvent( false );
        FormService.fireAsync( event, new FormResponseEvent( 1 ) );

        assertEquals( 1, event._listOptions.size( ) );
        assertNull( event._listOptions.get( 0 ) );
    }

    @Test
    public void testEventNotifiedInTheCurrentThreadWhenTheExecutorIsShutDown( )
    {
        RecordingEvent event = new RecordingEvent( true );
        FormService.fireAsync( event, new FormResponseEvent( 1 ) );

        assertEquals( 2, event._listOptions.size( ) );
        NotificationOptions options = event._listOptions.get( 1 );
        assertNotNull( options );
        List<String> listRun = new ArrayList<>( );
        options.getExecutor( ).execute( ( ) -> listRun.add( Thread.currentThread( ).getName( ) ) );
        assertEquals( List.of( Thread.currentThread( ).getName( ) ), listRun );
    }

    /**
     * Event recording the notification options of each fireAsync; the default executor can refuse the first one
     */
    private static class RecordingEvent implements Event<FormResponseEvent>
    {
        private final boolean _bRejectDefaultExecutor;
        private final List<NotificationOptions> _listOptions = new ArrayList<>( );

        RecordingEvent( boolean bRejectDefaultExecutor )
        {
            _bRejectDefaultExecutor = bRejectDefaultExecutor;
        }

        @Override
        public void fire( FormResponseEvent event )
        {
            throw new UnsupportedOperationException( );
        }

        @Override
        public <U extends FormResponseEvent> CompletionStage<U> fireAsync( U event )
        {
            _listOptions.add( null );
            if ( _bRejectDefaultExecutor )
            {
                throw new RejectedExecutionException( "executor shut down" );
            }
            return CompletableFuture.completedFuture( event );
        }

        @Override
        public <U extends FormResponseEvent> CompletionStage<U> fireAsync( U event, NotificationOptions options )
        {
            _listOptions.add( options );
            return CompletableFuture.completedFuture( event );
        }

        @Override
        public Event<FormResponseEvent> select( Annotation... qualifiers )
        {
            return this;
        }

        @Override
        public <U extends FormResponseEvent> Event<U> select( Class<U> subtype, Annotation... qualifiers )
        {
            throw new UnsupportedOperationException( );
        }

        @Override
        public <U extends FormResponseEvent> Event<U> select( TypeLiteral<U> subtype, Annotation... qualifiers )
        {
            throw new UnsupportedOperationException( );
        }
    }
}
