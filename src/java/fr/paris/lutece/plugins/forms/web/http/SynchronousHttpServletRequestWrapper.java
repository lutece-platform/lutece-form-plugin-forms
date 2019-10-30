/*
 * Copyright (c) 2002-2019, Mairie de Paris
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
package fr.paris.lutece.plugins.forms.web.http;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;

public class SynchronousHttpServletRequestWrapper extends MultipartHttpServletRequest
{
    private final Map<String, String [ ]> _modifiableParameters;
    private Map<String, String [ ]> _allParameters = null;

    /**
     * Create a new request wrapper that will merge additional parameters into the request object without prematurely reading parameters from the original
     * request.
     * 
     * @param request
     * @param additionalParams
     */
    public SynchronousHttpServletRequestWrapper( final MultipartHttpServletRequest request, final Map<String, String [ ]> additionalParams )
    {
        super( request, request.getFileListMap( ), request.getParameterMap( ) );
        _modifiableParameters = new TreeMap<>( );
        _modifiableParameters.putAll( additionalParams );

    }

    @Override
    public String getParameter( final String name )
    {
        String [ ] strings = getParameterMap( ).get( name );
        if ( strings != null )
        {
            return strings [0];
        }
        return super.getParameter( name );
    }

    @Override
    public Map<String, String [ ]> getParameterMap( )
    {
        if ( _allParameters == null )
        {
            _allParameters = new TreeMap<>( );
            _allParameters.putAll( super.getParameterMap( ) );
            _allParameters.putAll( _modifiableParameters );
        }
        // Return an unmodifiable collection because we need to uphold the interface contract.
        return Collections.unmodifiableMap( _allParameters );
    }

    @Override
    public Enumeration<String> getParameterNames( )
    {
        return Collections.enumeration( getParameterMap( ).keySet( ) );
    }

    @Override
    public String [ ] getParameterValues( final String name )
    {
        return getParameterMap( ).get( name );
    }
}
