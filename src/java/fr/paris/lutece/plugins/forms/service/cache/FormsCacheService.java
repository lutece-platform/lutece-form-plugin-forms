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
import fr.paris.lutece.plugins.genericattributes.business.EntryEvent;
import fr.paris.lutece.portal.service.cache.AbstractCacheableService;
import fr.paris.lutece.portal.service.cache.CacheConfigUtil;
import fr.paris.lutece.portal.service.util.AppLogService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Status;
import jakarta.transaction.Synchronization;
import jakarta.transaction.TransactionSynchronizationRegistry;

import java.io.Serializable;

import org.apache.commons.lang3.SerializationUtils;

@ApplicationScoped
public class FormsCacheService extends AbstractCacheableService<String, Object>
{
    private static final String CACHE_NAME = "formsCacheService";
    private static final String KEY_RESET_PENDING = FormsCacheService.class.getName( ) + ".resetPending";

    @Resource
    private TransactionSynchronizationRegistry _registry;

    @PostConstruct
    public void initCache( )
    {
    	initCache( CACHE_NAME, String.class, Object.class );
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

    public String getIdStepByFormKey( int nIdQuestion )
    {
        return new StringBuilder( "Step-For-Form-id:" ).append( nIdQuestion ).toString( );
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

    public String getUncompleteQuestionCacheKey( )
    {
        return new StringBuilder( "Question-Uncomplete" ).toString( );
    }

    public String getUncompleteQuestionByFormCacheKey( int nIdForm )
    {
        return new StringBuilder( "Question-Uncomplete-by-Form:" ).append( nIdForm ).toString( );
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

    /**
     * Puts a value in the cache. A failure of the cache (a value the provider cannot serialize, a closed cache, a provider
     * error) is logged and never reaches the caller: the value is then read from the database again.
     */
    @Override
    public void put( String key, Object value ) 
    {
        if ( !isCacheEnable( ) )
        {
        	return;
        }

        if ( value == null )
        {
            // JCache refuses a null value: the key no longer has a value
            remove( key );
            return;
        }

        try
        {
            if ( isCacheAvailable( ) ) 
            {
            	_cache.put( key, value );
            }
        } catch ( RuntimeException e )
        {
            AppLogService.error( "Forms cache: cannot put the key {} (value {}): {}", key,
                    ( value != null ) ? value.getClass( ).getName( ) : null, e.getMessage( ), e );
        }
    }

    @Override
    public Object get( String key ) 
    {
    	if ( !isCacheEnable( ) )
        {
        	return null;
        }
    	
    	try
        {
            if ( isCacheAvailable( ) ) 
            {
                Object value = _cache.get( key );
                // The objects of the forms are modified by their readers (back office screens, conditional displays).
                // Ehcache gives a copy on each read; a shared provider may give its own instance (Hazelcast near cache in
                // OBJECT format, the same instance for every request of a node): each reader then gets its own copy.
                if ( value instanceof Serializable && CacheConfigUtil.isDistributed( _cache ) )
                {
                    return SerializationUtils.clone( (Serializable) value );
                }
                return value;
            }
        } catch ( RuntimeException e ) 
        {
            AppLogService.error( e.getMessage( ), e );
        }

    	return null;
    }

    @Override
    public boolean remove( String key ) 
    {
        // No test of the status: a cache disabled on this instance still forwards the removal to the cache shared
        // with the other instances (the core keeps it open for that)
        try
        {
            return super.remove( key );
        }
        catch ( RuntimeException e ) 
        {
            AppLogService.error( e.getMessage( ), e );
        }
        return false;
    }
    
    /**
     * Resets the cache. Within a transaction (form import or copy, which write hundreds of objects), the reset is done
     * once, after the transaction completes: the other requests cannot cache again the data read before the commit,
     * and the cache (shared by every instance under Hazelcast) is not emptied for each written object.
     */
    @Override
    public void resetCache( )
    {
        TransactionSynchronizationRegistry registry = _registry;
        if ( registry != null && registry.getTransactionStatus( ) == Status.STATUS_ACTIVE )
        {
            if ( registry.getResource( KEY_RESET_PENDING ) == null )
            {
                registry.putResource( KEY_RESET_PENDING, Boolean.TRUE );
                registry.registerInterposedSynchronization( new Synchronization( )
                {
                    @Override
                    public void beforeCompletion( )
                    {
                        // The reset is done once the transaction is completed
                    }

                    @Override
                    public void afterCompletion( int nStatus )
                    {
                        // Committed or rolled back: the cache may hold values read during the transaction
                        resetNow( );
                    }
                } );
            }
            return;
        }
        resetNow( );
    }

    /**
     * Resets the cache at once
     */
    private void resetNow( )
    {
        super.resetCache( );
    }

    /**
     * Checks whether the cache instance is initialized and currently open.
     *
     * @return true if the cache is not null and not closed, false otherwise.
     */
    private boolean isCacheAvailable( )
    {
        return _cache != null && !_cache.isClosed( );
    }

    /**
     * Process an entry event
     *
     * @param event
     *            The event to process
     */
    public void processEntryEvent( @Observes EntryEvent event )
    {
        // No test of the status: the reset reaches the cache shared with the other instances when this one has it
        // disabled
        if ( Form.RESOURCE_TYPE.equals( event.getResourceType( ) ) )
        {
            resetCache( );
        }
    }
}
