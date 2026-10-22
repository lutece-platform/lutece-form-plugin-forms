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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.inject.Inject;

/**
 * Removal of a form and of its responses
 */
public class FormServiceRemoveFormTest extends LuteceTestCase
{
    private static final long TIMEOUT_MILLIS = 5000;
    private static final long POLL_MILLIS = 50;

    @Inject
    private FormService _formService;
    @Inject
    private TestFormResponseRemovalObserver _observer;

    /**
     * Removing a form fires the deletion event of each of its responses, as removing a response alone does, so that the indexes drop them
     *
     * @throws InterruptedException
     *             if the wait for the asynchronous events is interrupted
     */
    @Test
    public void testRemoveFormFiresTheDeletionEventOfItsResponses( ) throws InterruptedException
    {
        Form form = new Form( );
        form.setTitle( "FormServiceRemoveFormTest" );
        form.setBreadcrumbName( "forms.horizontalBreadcrumb" );
        form.setAvailabilityStartDate( Timestamp.valueOf( LocalDateTime.now( ) ) );
        FormHome.create( form );

        int nIdFirst = createFormResponse( form ).getId( );
        int nIdSecond = createFormResponse( form ).getId( );

        _formService.removeForm( form.getId( ), new AdminUser( ) );

        long lDeadline = System.currentTimeMillis( ) + TIMEOUT_MILLIS;
        while ( !_observer.getRemovedIds( ).contains( nIdSecond ) && System.currentTimeMillis( ) < lDeadline )
        {
            Thread.sleep( POLL_MILLIS );
        }
        assertTrue( _observer.getRemovedIds( ).contains( nIdFirst ), "The deletion event of the first response must be fired" );
        assertTrue( _observer.getRemovedIds( ).contains( nIdSecond ), "The deletion event of the second response must be fired" );
    }

    /**
     * Creates a form response of the given form
     *
     * @param form
     *            the form
     * @return the form response
     */
    private FormResponse createFormResponse( Form form )
    {
        FormResponse formResponse = new FormResponse( );
        formResponse.setFormId( form.getId( ) );
        formResponse.setGuid( UUID.randomUUID( ).toString( ) );
        formResponse.setDateCreation( Timestamp.valueOf( LocalDateTime.now( ) ) );
        formResponse.setUpdate( Timestamp.valueOf( LocalDateTime.now( ) ) );
        FormResponseHome.create( formResponse );
        return formResponse;
    }
}
