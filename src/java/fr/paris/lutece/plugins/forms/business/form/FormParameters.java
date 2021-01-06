/*
 * Copyright (c) 2002-2021, City of Paris
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
package fr.paris.lutece.plugins.forms.business.form;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class which contains all parameter names and their values used for build the query to execute
 */
public class FormParameters
{
    // Variables
    private List<String> _listUsedParametersValue = new ArrayList<>( );
    private Map<String, Object> _mapFormParameters = new LinkedHashMap<>( );

    /**
     * Return the list of all values of the parameters which will be used for the creation of the final query
     * 
     * @return the list of all values of the parameters which will be used for the creation of the final query
     */
    public List<String> getListUsedParametersValue( )
    {
        return _listUsedParametersValue;
    }

    /**
     * Set the list of values of all parameters which will be used for build the final query
     * 
     * @param listUsedParametersValue
     *            The list of all values to used for build the final query
     */
    public void setListUsedParametersValue( List<String> listUsedParametersValue )
    {
        _listUsedParametersValue = listUsedParametersValue;
    }

    /**
     * Return the map which associate for each parameter name its value
     * 
     * @return the map which associate for each parameter its value
     */
    public Map<String, Object> getFormParametersMap( )
    {
        return _mapFormParameters;
    }

    /**
     * Set the map of all parameters and their values
     * 
     * @param mapFormParameters
     *            The map of all parameters and their values
     */
    public void setFormParametersMap( Map<String, Object> mapFormParameters )
    {
        _mapFormParameters = mapFormParameters;
    }
}
