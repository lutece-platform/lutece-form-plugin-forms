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
package fr.paris.lutece.plugins.forms;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.web.FormXPage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.ConversationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;

/**
 * Normal-scoped beans of the plugin must be proxyable for every type of their hierarchy.
 * <p>
 * Weld validates it lazily: when a session written by another cluster member is read, it re-creates the client proxies held by the session beans
 * and checks every bean type. A class of the hierarchy without a non-private no-args constructor makes the deserialization fail (WELD-001435): the
 * session bean is lost, the user sees "session expired" or an error page. Weld does not report it at deployment, nor on a single instance.
 * </p>
 */
public class NormalScopedBeansProxyableTest
{
    private static final List<Class<? extends Annotation>> NORMAL_SCOPES = List.of( ApplicationScoped.class, SessionScoped.class,
            RequestScoped.class, ConversationScoped.class );

    /**
     * Every class of the hierarchy of a normal-scoped bean has a non-private no-args constructor.
     *
     * @throws IOException
     *             if the classes cannot be listed
     * @throws URISyntaxException
     *             if the classes location is invalid
     */
    @Test
    public void testNormalScopedBeansAreProxyable( ) throws IOException, URISyntaxException
    {
        Path classes = Path.of( FormXPage.class.getProtectionDomain( ).getCodeSource( ).getLocation( ).toURI( ) );
        List<String> violations = new ArrayList<>( );
        List<Path> classFiles;
        try ( Stream<Path> files = Files.walk( classes ) )
        {
            classFiles = files.filter( f -> f.toString( ).endsWith( ".class" ) && !f.toString( ).contains( "$" ) ).toList( );
        }
        assertFalse( classFiles.isEmpty( ), "No compiled class found in " + classes );
        for ( Path file : classFiles )
        {
            String strName = classes.relativize( file ).toString( ).replace( '/', '.' ).replaceAll( "\\.class$", "" );
            Class<?> clazz;
            try
            {
                clazz = Class.forName( strName, false, getClass( ).getClassLoader( ) );
            }
            catch( ClassNotFoundException | LinkageError e )
            {
                violations.add( strName + " (cannot be loaded: " + e + ")" );
                continue;
            }
            if ( NORMAL_SCOPES.stream( ).anyMatch( clazz::isAnnotationPresent ) )
            {
                for ( Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass( ) )
                {
                    if ( !hasNonPrivateNoArgsConstructor( c ) )
                    {
                        violations.add( clazz.getName( ) + ( c == clazz ? "" : " (super class " + c.getName( ) + ")" ) );
                    }
                }
            }
        }
        assertTrue( violations.isEmpty( ), "Normal-scoped beans not proxyable (no non-private no-args constructor): " + violations );
    }

    private static boolean hasNonPrivateNoArgsConstructor( Class<?> clazz )
    {
        try
        {
            Constructor<?> constructor = clazz.getDeclaredConstructor( );
            return !Modifier.isPrivate( constructor.getModifiers( ) );
        }
        catch( NoSuchMethodException e )
        {
            return false;
        }
    }
}
