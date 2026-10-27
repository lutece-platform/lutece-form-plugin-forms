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
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNERS OR CONTRIBUTORS BE
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
package fr.paris.lutece.plugins.forms.service.cache;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.cache.Cache;

import jakarta.transaction.Status;
import jakarta.transaction.Synchronization;
import jakarta.transaction.TransactionSynchronizationRegistry;

import org.junit.jupiter.api.Test;

/**
 * A failure of the cache provider never reaches the callers of the forms cache: a value the provider cannot serialize
 * (Ehcache SerializerException, Hazelcast HazelcastSerializationException) used to turn every form step into a 500.
 */
public class FormsCacheServiceTest
{
    /**
     * Builds an open cache whose reads and writes throw the given runtime exception.
     *
     * @param failure
     *            the exception thrown by get, put and remove
     * @return the failing cache
     */
    @SuppressWarnings( "unchecked" )
    private static Cache<String, Object> failingCache( RuntimeException failure )
    {
        return (Cache<String, Object>) Proxy.newProxyInstance( Cache.class.getClassLoader( ), new Class<?> [ ] {
                Cache.class
        }, ( proxy, method, args ) -> {
            switch( method.getName( ) )
            {
                case "isClosed":
                    return false;
                case "getName":
                    return "formsCacheService";
                case "get":
                case "put":
                case "remove":
                    throw failure;
                default:
                    return null;
            }
        } );
    }

    /**
     * Put, get and remove swallow a serialization failure of the provider.
     */
    @Test
    public void testProviderFailureDoesNotReachTheCaller( )
    {
        FormsCacheService service = new FormsCacheService( );
        service.setCache( failingCache( new IllegalArgumentException( "Failed to serialize 'BreadcrumbAccordionConfig'" ) ) );

        assertDoesNotThrow( ( ) -> service.put( "Form-id:1", new Object( ) ) );
        assertNull( service.get( "Form-id:1" ) );
        assertFalse( service.remove( "Form-id:1" ) );
    }

    /**
     * Builds an open cache counting its removeAll and remove calls.
     *
     * @param removeAll
     *            counter of removeAll calls
     * @param removes
     *            counter of remove calls
     * @return the cache
     */
    @SuppressWarnings( "unchecked" )
    private static Cache<String, Object> countingCache( AtomicInteger removeAll, AtomicInteger removes )
    {
        return (Cache<String, Object>) Proxy.newProxyInstance( Cache.class.getClassLoader( ), new Class<?> [ ] {
                Cache.class
        }, ( proxy, method, args ) -> {
            switch( method.getName( ) )
            {
                case "isClosed":
                    return false;
                case "removeAll":
                    removeAll.incrementAndGet( );
                    return null;
                case "remove":
                    removes.incrementAndGet( );
                    return true;
                default:
                    return null;
            }
        } );
    }

    /**
     * Builds an active transaction registry that keeps its resources and synchronizations.
     *
     * @param synchronizations
     *            the registered synchronizations
     * @return the registry
     */
    private static TransactionSynchronizationRegistry activeTransaction( List<Synchronization> synchronizations )
    {
        Map<Object, Object> resources = new HashMap<>( );
        return (TransactionSynchronizationRegistry) Proxy.newProxyInstance( TransactionSynchronizationRegistry.class.getClassLoader( ),
                new Class<?> [ ] {
                        TransactionSynchronizationRegistry.class
                }, ( proxy, method, args ) -> {
                    switch( method.getName( ) )
                    {
                        case "getTransactionStatus":
                            return Status.STATUS_ACTIVE;
                        case "getResource":
                            return resources.get( args [0] );
                        case "putResource":
                            resources.put( args [0], args [1] );
                            return null;
                        case "registerInterposedSynchronization":
                            synchronizations.add( (Synchronization) args [0] );
                            return null;
                        default:
                            return null;
                    }
                } );
    }

    private static void setRegistry( FormsCacheService service, TransactionSynchronizationRegistry registry ) throws ReflectiveOperationException
    {
        Field field = FormsCacheService.class.getDeclaredField( "_registry" );
        field.setAccessible( true );
        field.set( service, registry );
    }

    /**
     * Within a transaction, the resets of an import are done once, after the transaction completes.
     *
     * @throws ReflectiveOperationException
     *             if the registry cannot be set
     */
    @Test
    public void testResetsGroupedAfterTheTransaction( ) throws ReflectiveOperationException
    {
        AtomicInteger removeAll = new AtomicInteger( );
        List<Synchronization> synchronizations = new ArrayList<>( );
        FormsCacheService service = new FormsCacheService( );
        service.setCache( countingCache( removeAll, new AtomicInteger( ) ) );
        setRegistry( service, activeTransaction( synchronizations ) );

        for ( int i = 0; i < 300; i++ )
        {
            service.resetCache( );
        }
        assertEquals( 0, removeAll.get( ) );
        assertEquals( 1, synchronizations.size( ) );

        synchronizations.get( 0 ).afterCompletion( Status.STATUS_COMMITTED );
        assertEquals( 1, removeAll.get( ) );
    }

    /**
     * Without a transaction, a reset is done at once.
     */
    @Test
    public void testResetWithoutTransaction( )
    {
        AtomicInteger removeAll = new AtomicInteger( );
        FormsCacheService service = new FormsCacheService( );
        service.setCache( countingCache( removeAll, new AtomicInteger( ) ) );

        service.resetCache( );
        assertEquals( 1, removeAll.get( ) );
    }

    /**
     * A cache disabled on this instance still forwards a removal to the cache shared with the other instances.
     *
     * @throws ReflectiveOperationException
     *             if the status cannot be set
     */
    @Test
    public void testRemoveForwardedWhenDisabled( ) throws ReflectiveOperationException
    {
        AtomicInteger removes = new AtomicInteger( );
        FormsCacheService service = new FormsCacheService( );
        service.setCache( countingCache( new AtomicInteger( ), removes ) );
        Field disabled = fr.paris.lutece.portal.service.cache.AbstractCacheableService.class.getDeclaredField( "_bDisabled" );
        disabled.setAccessible( true );
        disabled.set( service, true );

        assertNull( service.get( "Form-id:1" ) );
        service.remove( "Form-id:1" );
        assertEquals( 1, removes.get( ) );
    }
}
