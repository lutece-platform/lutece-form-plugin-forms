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
package fr.paris.lutece.plugins.forms.service.entrytype;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.util.ReferenceList;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Labels of the MyLutece user attributes
 */
public class EntryTypeMyLuteceUserAttributeTest extends LuteceTestCase
{
    private static final String ATTRIBUTE_FAMILY_NAME = "user.name.family";

    @Inject
    @Named( "forms.entryTypeMyLuteceUserattribute" )
    private EntryTypeMyLuteceUserAttribute _entryType;

    /**
     * The labels are in the language of each caller, whatever the language of the first one
     */
    @Test
    public void testLabelsInTheLanguageOfTheCaller( )
    {
        assertEquals( "Nom de famille", getLabel( _entryType.getLuteceUserAttributesRefList( "fr" ) ) );
        assertEquals( "Last name", getLabel( _entryType.getLuteceUserAttributesRefList( "en" ) ) );
    }

    private static String getLabel( ReferenceList referenceList )
    {
        return referenceList.stream( ).filter( item -> ATTRIBUTE_FAMILY_NAME.equals( item.getCode( ) ) ).findFirst( ).map( item -> item.getName( ) )
                .orElse( null );
    }
}
