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
package fr.paris.lutece.plugins.forms.service.cache;

import fr.paris.lutece.plugins.forms.business.ControlType;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.portal.business.event.EventRessourceListener;
import fr.paris.lutece.portal.business.event.ResourceEvent;
import fr.paris.lutece.portal.service.cache.AbstractCacheableService;
import fr.paris.lutece.portal.service.event.ResourceEventManager;

public class FormsCacheService extends AbstractCacheableService implements EventRessourceListener
{

    private static final String CACHE_NAME = "formsCacheService";

    @Override
    public void initCache( )
    {
        super.initCache( );
        ResourceEventManager.register( this );
    }

    @Override
    public String getName( )
    {
        return CACHE_NAME;
    }

    public String getStepCacheKey( int nStepID )
    {
        return new StringBuilder( "Step-id:" ).append( nStepID ).toString( );
    }

    public String getInitialStepCacheKey( int nIdForm )
    {
        return new StringBuilder( "Initial-Step-For-Form-id:" ).append( nIdForm ).toString( );
    }

    public String getFinalStepCacheKey( int nIdForm )
    {
        return new StringBuilder( "Final-Step-For-Form-id:" ).append( nIdForm ).toString( );
    }

    public String getFormCacheKey( int nIdForm )
    {
        return new StringBuilder( "Form-id:" ).append( nIdForm ).toString( );
    }

    public String getFormListCacheKey( )
    {
        return "FormList";
    }

    public String getControlCacheKey( int nIdControl )
    {
        return new StringBuilder( "Control-id:" ).append( nIdControl ).toString( );
    }

    public String getControlByControlTargetAndTypeCacheKey( int nIdControlTarget, ControlType controlType )
    {
        return new StringBuilder( "Control-by-Target:" ).append( nIdControlTarget ).append( "-and-Type:" )
                .append( controlType ).toString( );
    }

    public String getControlByQuestionAndTypeCacheKey( int nIdQuestion, String strControlType )
    {
        return new StringBuilder( "Control-by-Question:" ).append( nIdQuestion ).append( "-and-Type:" )
                .append( strControlType ).toString( );
    }

    public String getQuestionCacheKey( int nIdQuestion )
    {
        return new StringBuilder( "Question-id:" ).append( nIdQuestion ).toString( );
    }

    public String getFormDisplayCacheKey( int nIdFormDisplay )
    {
        return new StringBuilder( "FormDisplay-id:" ).append( nIdFormDisplay ).toString( );
    }

    public String getFormDisplayListByParentCacheKey( int nIdStep, int nIdParent )
    {
        return new StringBuilder( "FormDisplayList-by-Step:" ).append( nIdStep ).append( "-and-Parent:" )
                .append( nIdParent ).toString( );
    }

    public String getGroupCacheKey( int nIdGroup )
    {
        return new StringBuilder( "Group-id:" ).append( nIdGroup ).toString( );
    }

    public String getControlGroupCacheKey( int nIdControlGroup )
    {
        return new StringBuilder( "ControlGroup-id:" ).append( nIdControlGroup ).toString( );
    }

    public String getTransitionsListFromStepCacheKey( int nIdStep )
    {
        return new StringBuilder( "TransitionsList-From-Step:" ).append( nIdStep ).toString( );
    }

    public String getFormMessageByFormCacheKey( int nIdForm )
    {
        return new StringBuilder( "FormMessage-by-Form:" ).append( nIdForm ).toString( );
    }

    @Override
    public void addedResource( ResourceEvent event )
    {
        handleEvent( event );
    }

    @Override
    public void deletedResource( ResourceEvent event )
    {
        handleEvent( event );
    }

    @Override
    public void updatedResource( ResourceEvent event )
    {
        handleEvent( event );
    }

    private void handleEvent( ResourceEvent event )
    {
        if ( Form.RESOURCE_TYPE.equals( event.getTypeResource( ) ) )
        {
            // FIXME GenericAttributes sends events inside a transaction.
            // this cache reset should really happen after the transaction
            // but Lutece lacks TransactionSynchronisation infrastructure
            resetCache( );
        }
    }
}
