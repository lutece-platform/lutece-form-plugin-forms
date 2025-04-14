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
package fr.paris.lutece.plugins.forms.validation;

import java.util.Arrays;
import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

@ApplicationScoped
public class ValidatorProducer 
{
	@Produces
	@ApplicationScoped
	@Named( "forms.patternValidator" )
    public IValidator producePatternValidator( @ConfigProperty( name = "forms.patternValidator.beanName" ) String strBeanName,
            @ConfigProperty( name = "forms.patternValidator.displayName" ) String strDisplayName,
            @ConfigProperty( name = "forms.patternValidator.availableEntryTypeItems" ) String strAvailableEntryTypeItems )
    {
        return new PatternValidator( strBeanName, strDisplayName, convertStringToList( strAvailableEntryTypeItems )  );
    }
	
	@Produces
	@ApplicationScoped
	@Named( "forms.patternFileTypeValidator" )
	public IValidator producePatternFileTypeValidator(
	        @ConfigProperty( name = "forms.patternFileTypeValidator.beanName" ) String strBeanName,
	        @ConfigProperty( name = "forms.patternFileTypeValidator.displayName" ) String strDisplayName,
	        @ConfigProperty( name = "forms.patternFileTypeValidator.availableEntryTypeItems" ) String strAvailableEntryTypeItems )
	{
	    return new PatternFileTypeValidator( strBeanName, strDisplayName, convertStringToList( strAvailableEntryTypeItems )  );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.listValueValidator" )
	public IValidator produceListValueValidator(
	        @ConfigProperty( name = "forms.listValueValidator.beanName" ) String strBeanName,
	        @ConfigProperty( name = "forms.listValueValidator.displayName" ) String strDisplayName,
	        @ConfigProperty( name = "forms.listValueValidator.availableEntryTypeItems" ) String strAvailableEntryTypeItems )
	{
	    return new ListValueValidator( strBeanName, strDisplayName, convertStringToList( strAvailableEntryTypeItems )  );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.listMaxResponseValidator" )
	public IValidator produceListMaxResponseValidator(
	        @ConfigProperty( name = "forms.listMaxResponseValidator.beanName" ) String strBeanName,
	        @ConfigProperty( name = "forms.listMaxResponseValidator.displayName" ) String strDisplayName,
	        @ConfigProperty( name = "forms.listMaxResponseValidator.availableEntryTypeItems" ) String strAvailableEntryTypeItems )
	{
	    return new ListMaxResponseValidator( strBeanName, strDisplayName, convertStringToList( strAvailableEntryTypeItems )  );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.listMinResponseValidator" )
	public IValidator produceListMinResponseValidator(
	        @ConfigProperty( name = "forms.listMinResponseValidator.beanName" ) String strBeanName,
	        @ConfigProperty( name = "forms.listMinResponseValidator.displayName" ) String strDisplayName,
	        @ConfigProperty( name = "forms.listMinResponseValidator.availableEntryTypeItems" ) String strAvailableEntryTypeItems )
	{
	    return new ListMinResponseValidator( strBeanName, strDisplayName, convertStringToList( strAvailableEntryTypeItems )  );
	}
	
	@Produces
	@ApplicationScoped
	@Named( "forms.listExactResponseValidator" )
	public IValidator produceListExactResponseValidator(
	        @ConfigProperty( name = "forms.listExactResponseValidator.beanName" ) String strBeanName,
	        @ConfigProperty( name = "forms.listExactResponseValidator.displayName" ) String strDisplayName,
	        @ConfigProperty( name = "forms.listExactResponseValidator.availableEntryTypeItems" ) String strAvailableEntryTypeItems )
	{
	    return new ListExactResponseValidator( strBeanName, strDisplayName, convertStringToList( strAvailableEntryTypeItems )  );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.uniqueValidator" )
	public IValidator produceUniqueValidator(
	        @ConfigProperty( name = "forms.uniqueValidator.beanName" ) String strBeanName,
	        @ConfigProperty( name = "forms.uniqueValidator.displayName" ) String strDisplayName,
	        @ConfigProperty( name = "forms.uniqueValidator.availableEntryTypeItems" ) String strAvailableEntryTypeItems )
	{
	    return new UniqueValidator( strBeanName, strDisplayName, convertStringToList( strAvailableEntryTypeItems )  );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.dateInferiorValidator" )
	public IValidator produceDateInferiorValidator(
	        @ConfigProperty( name = "forms.dateInferiorValidator.beanName" ) String strBeanName,
	        @ConfigProperty( name = "forms.dateInferiorValidator.displayName" ) String strDisplayName,
	        @ConfigProperty( name = "forms.dateInferiorValidator.availableEntryTypeItems" ) String strAvailableEntryTypeItems )
	{
	    return new DateInferiorValidator( strBeanName, strDisplayName, convertStringToList( strAvailableEntryTypeItems )  );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.dateSuperiorValidator" )
	public IValidator produceDateSuperiorValidator(
	        @ConfigProperty( name = "forms.dateSuperiorValidator.beanName" ) String strBeanName,
	        @ConfigProperty( name = "forms.dateSuperiorValidator.displayName" ) String strDisplayName,
	        @ConfigProperty( name = "forms.dateSuperiorValidator.availableEntryTypeItems" ) String strAvailableEntryTypeItems )
	{
	    return new DateSuperiorValidator( strBeanName, strDisplayName, convertStringToList( strAvailableEntryTypeItems )  );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.numberInferiorValidator" )
	public IValidator produceNumberInferiorValidator(
	        @ConfigProperty( name = "forms.numberInferiorValidator.beanName" ) String strBeanName,
	        @ConfigProperty( name = "forms.numberInferiorValidator.displayName" ) String strDisplayName,
	        @ConfigProperty( name = "forms.numberInferiorValidator.availableEntryTypeItems" ) String strAvailableEntryTypeItems )
	{
	    return new NumberInferiorValidator( strBeanName, strDisplayName, convertStringToList( strAvailableEntryTypeItems )  );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.numberSuperiorValidator" )
	public IValidator produceNumberSuperiorValidator(
	        @ConfigProperty( name = "forms.numberSuperiorValidator.beanName" ) String strBeanName,
	        @ConfigProperty( name = "forms.numberSuperiorValidator.displayName" ) String strDisplayName,
	        @ConfigProperty( name = "forms.numberSuperiorValidator.availableEntryTypeItems" ) String strAvailableEntryTypeItems )
	{
	    return new NumberSuperiorValidator( strBeanName, strDisplayName, convertStringToList( strAvailableEntryTypeItems ) );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.dayDelayMinimumValidator" )
	public IValidator produceDayDelayMinimumValidator(
	        @ConfigProperty( name = "forms.dayDelayMinimumValidator.beanName" ) String strBeanName,
	        @ConfigProperty( name = "forms.dayDelayMinimumValidator.displayName" ) String strDisplayName,
	        @ConfigProperty( name = "forms.dayDelayMinimumValidator.availableEntryTypeItems" ) String strAvailableEntryTypeItems )
	{
	    return new DayDelayMinimumValidator( strBeanName, strDisplayName, convertStringToList( strAvailableEntryTypeItems ) );
	}

	private List<String> convertStringToList( String strInput) 
	{
        return Arrays.stream( strInput.split( "," ) )
                     .map( String::trim )
                     .toList( );
    }
}
