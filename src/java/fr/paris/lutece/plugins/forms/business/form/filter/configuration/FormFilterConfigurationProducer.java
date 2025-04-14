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
package fr.paris.lutece.plugins.forms.business.form.filter.configuration;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

@ApplicationScoped
public class FormFilterConfigurationProducer 
{
	@Produces
	@ApplicationScoped
	@Named( "forms.forms.filterConfiguration" )
    public IFormFilterConfiguration produceFormFilterFormsConfiguration( 
    		@ConfigProperty( name = "forms.forms.filterConfiguration.position" ) int nPosition,
            @ConfigProperty( name = "forms.forms.filterConfiguration.filterLabel" ) String strFormFilterLabel,
            @ConfigProperty( name = "forms.forms.filterConfiguration.filterName" ) String strFormFilterName )
    {
        return new FormFilterFormsConfiguration( nPosition, strFormFilterLabel, strFormFilterName );
    }
	
	@Produces
	@ApplicationScoped
	@Named( "forms.formResponseDateCreation.filterConfiguration" )
	public IFormFilterConfiguration produceFormFilterResponseDateCreationConfiguration( 
	    @ConfigProperty( name = "forms.formResponseDateCreation.filterConfiguration.position" ) int nPosition,
	    @ConfigProperty( name = "forms.formResponseDateCreation.filterConfiguration.filterLabel" ) String strFormFilterLabel,
	    @ConfigProperty( name = "forms.formResponseDateCreation.filterConfiguration.filterName" ) String strFormFilterName )
	{
	    return new FormFilterDateConfiguration( nPosition, strFormFilterLabel, strFormFilterName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.formResponseDateUpdate.filterConfiguration" )
	public IFormFilterConfiguration produceFormFilterResponseDateUpdateConfiguration( 
	    @ConfigProperty( name = "forms.formResponseDateUpdate.filterConfiguration.position" ) int nPosition,
	    @ConfigProperty( name = "forms.formResponseDateUpdate.filterConfiguration.filterLabel" ) String strFormFilterLabel,
	    @ConfigProperty( name = "forms.formResponseDateUpdate.filterConfiguration.filterName" ) String strFormFilterName )
	{
	    return new FormFilterDateConfiguration( nPosition, strFormFilterLabel, strFormFilterName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.searchedText.filterConfiguration" )
	public IFormFilterConfiguration produceFormFilterSearchedTextConfiguration( 
	    @ConfigProperty( name = "forms.searchedText.filterConfiguration.position" ) int nPosition,
	    @ConfigProperty( name = "forms.searchedText.filterConfiguration.filterLabel" ) String strFormFilterLabel,
	    @ConfigProperty( name = "forms.searchedText.filterConfiguration.filterName" ) String strFormFilterName )
	{
	    return new FormFilterFormResponseIdConfiguration( nPosition, strFormFilterLabel, strFormFilterName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.workflowState.filterConfiguration" )
	public IFormFilterConfiguration produceFormFilterWorkflowStateConfiguration( 
	    @ConfigProperty( name = "forms.workflowState.filterConfiguration.position" ) int nPosition,
	    @ConfigProperty( name = "forms.workflowState.filterConfiguration.filterLabel" ) String strFormFilterLabel,
	    @ConfigProperty( name = "forms.workflowState.filterConfiguration.filterName" ) String strFormFilterName )
	{
	    return new FormFilterWorkflowConfiguration( nPosition, strFormFilterLabel, strFormFilterName );
	}

}
