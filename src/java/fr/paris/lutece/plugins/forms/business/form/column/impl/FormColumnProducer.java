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
package fr.paris.lutece.plugins.forms.business.form.column.impl;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

@ApplicationScoped
public class FormColumnProducer 
{
	@Produces
	@ApplicationScoped
	@Named( "forms.forms.column" )
    public IFormColumn produceFormColumnForms( 
    		@ConfigProperty( name = "forms.forms.column.position" ) int _nPosition,
            @ConfigProperty( name = "forms.forms.column.formColumnTitle" ) String _strFormColumnTitle )
    {
        return new FormColumnForms( _nPosition, _strFormColumnTitle );
    }

	@Produces
	@ApplicationScoped
	@Named( "forms.formResponseCreationDate.column" )
	public IFormColumn produceFormColumnFormResponseCreationDate( 
	        @ConfigProperty( name = "forms.formResponseCreationDate.column.position" ) int _nPosition,
	        @ConfigProperty( name = "forms.formResponseCreationDate.column.formColumnTitle" ) String _strFormColumnTitle )
	{
	    return new FormColumnFormResponseDateCreation( _nPosition, _strFormColumnTitle );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.formResponseUpdateDate.column" )
	public IFormColumn produceFormColumnFormResponseUpdateDate( 
	        @ConfigProperty( name = "forms.formResponseUpdateDate.column.position" ) int _nPosition,
	        @ConfigProperty( name = "forms.formResponseUpdateDate.column.formColumnTitle" ) String _strFormColumnTitle )
	{
	    return new FormColumnFormResponseDateUpdate( _nPosition, _strFormColumnTitle );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.workflowState.column" )
	public IFormColumn produceFormColumnWorkflowState( 
	        @ConfigProperty( name = "forms.workflowState.column.position" ) int _nPosition,
	        @ConfigProperty( name = "forms.workflowState.column.formColumnTitle" ) String _strFormColumnTitle )
	{
	    return new FormColumnWorkflowState( _nPosition, _strFormColumnTitle );
	}


}
