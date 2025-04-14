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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class RandomValueFieldPopulator
{
    private final static Random random = new Random( );
    private static final int DATE_WINDOW_MILLIS = 10000;

    public void populate( Object object )
    {
    	Field[] fields = object.getClass( ).getDeclaredFields( );
        for (Field field : fields)
        {
        	try 
        	{
        		new RandomValueFieldSetterCallback( object ).doWith( field );
            }
        	catch ( IllegalAccessException e )
        	{
                // Skip fields that cannot be accessed
            }
        }
    }

    public static void randomlyPopulateFields( Object object )
    {
        new RandomValueFieldPopulator( ).populate( object );
    }

    private static class RandomValueFieldSetterCallback
    {
        private Object targetObject;

        public RandomValueFieldSetterCallback( Object targetObject )
        {
            this.targetObject = targetObject;
        }

        public void doWith( Field field ) throws IllegalAccessException
        {
            Class<?> fieldType = field.getType( );
            if ( !Modifier.isFinal( field.getModifiers( ) ) )
            {
                Object value = generateRandomValue( fieldType );
                if ( value != null )
                {
                	field.setAccessible(true);
                    field.set( targetObject, value );
                }
            }
        }
    }

    public static Object generateRandomValue( Class<?> fieldType )
    {
        if ( fieldType.equals( String.class ) )
        {
            return UUID.randomUUID( ).toString( );
        }
        else if ( Date.class.isAssignableFrom( fieldType ) )
        {
            return new Date( System.currentTimeMillis( ) - random.nextInt( DATE_WINDOW_MILLIS ) );
        }
        else if ( Number.class.isAssignableFrom( fieldType ) )
        {
            return random.nextInt( Byte.MAX_VALUE ) + 1;
        }
        else if ( fieldType.equals( Integer.TYPE ) )
        {
            return random.nextInt( );
        }
        else if ( fieldType.equals( Long.TYPE ) )
        {
            return random.nextInt( );
        }
        else if ( fieldType.equals( Boolean.TYPE ) )
        {
            return random.nextBoolean( );
        }
        else if ( Enum.class.isAssignableFrom( fieldType ) )
        {
            Object[ ] enumValues = fieldType.getEnumConstants( );
            return enumValues[ random.nextInt( enumValues.length ) ];
        }
        else if ( Timestamp.class.isAssignableFrom( fieldType ) )
        {
            return new Timestamp( System.currentTimeMillis( ) - random.nextInt( DATE_WINDOW_MILLIS ) );
        }
        else if ( List.class.isAssignableFrom( fieldType ) )
        {
            return Collections.EMPTY_LIST;
        }
        return null;
    }
}
