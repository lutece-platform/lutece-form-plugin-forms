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
package fr.paris.lutece.plugins.forms.web.entrytype;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

@ApplicationScoped
public class EntryDataServiceProducer 
{
	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeCheckBoxDataService" )
    public IEntryDataService produceEntryTypeCheckBoxDataService( @ConfigProperty( name = "forms.entryTypeCheckBoxDataService.name" ) String strName )
    {
        return new EntryTypeIterableDataService( strName );
    }
	
	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeCommentDataService" )
	public IEntryDataService produceEntryTypeCommentDataService( @ConfigProperty( name = "forms.entryTypeCommentDataService.name" ) String strName )
	{
		return new EntryTypeDefaultDataService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeDateDataService" )
	public IEntryDataService produceEntryTypeDateDataService( @ConfigProperty( name = "forms.entryTypeDateDataService.name" ) String strName )
	{
		return new EntryTypeIterableDataService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeFileDataService" )
	public IEntryDataService produceEntryTypeFileDataService( @ConfigProperty( name = "forms.entryTypeFileDataService.name" ) String strName )
	{
		return new EntryTypeFileDataService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeGeolocationDataService" )
	public IEntryDataService produceEntryTypeGeolocationDataService( @ConfigProperty( name = "forms.entryTypeGeolocationDataService.name" ) String strName )
	{
		return new EntryTypeDefaultDataService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeCartographyDataService" )
	public IEntryDataService produceEntryTypeCartographyDataService( @ConfigProperty( name = "forms.entryTypeCartographyDataService.name" ) String strName )
	{
		return new EntryTypeDefaultDataService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeGroupDataService" )
	public IEntryDataService produceEntryTypeGroupDataService( @ConfigProperty( name = "forms.entryTypeGroupDataService.name" ) String strName )
	{
		return new EntryTypeDefaultDataService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeImageDataService" )
	public IEntryDataService produceEntryTypeImageDataService( @ConfigProperty( name = "forms.entryTypeImageDataService.name" ) String strName )
	{
		return new EntryTypeFileDataService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeGalleryImageDataService" )
	public IEntryDataService produceEntryTypeGalleryImageDataService( @ConfigProperty( name = "forms.entryTypeGalleryImageDataService.name" ) String strName )
	{
		return new EntryTypeDefaultDataService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeMyLuteceUserDataService" )
	public IEntryDataService produceEntryTypeMyLuteceUserDataService( @ConfigProperty( name = "forms.entryTypeMyLuteceUserDataService.name" ) String strName )
	{
		return new EntryTypeDefaultDataService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeNumberingDataService" )
	public IEntryDataService produceEntryTypeNumberingDataService( @ConfigProperty( name = "forms.entryTypeNumberingDataService.name" ) String strName )
	{
		return new EntryTypeDefaultDataService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeRadioButtonDataService" )
	public IEntryDataService produceEntryTypeRadioButtonDataService( @ConfigProperty( name = "forms.entryTypeRadioButtonDataService.name" ) String strName )
	{
		return new EntryTypeIterableDataService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeSelectDataService" )
	public IEntryDataService produceEntryTypeSelectDataService( @ConfigProperty( name = "forms.entryTypeSelectDataService.name" ) String strName )
	{
		return new EntryTypeIterableDataService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeSelectOrderDataService" )
	public IEntryDataService produceEntryTypeSelectOrderDataService( @ConfigProperty( name = "forms.entryTypeSelectOrderDataService.name" ) String strName )
	{
		return new EntryTypeIterableDataService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeTextDataService" )
	public IEntryDataService produceEntryTypeTextDataService( @ConfigProperty( name = "forms.entryTypeTextDataService.name" ) String strName )
	{
		return new EntryTypeIterableDataService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeNumberDataService" )
	public IEntryDataService produceEntryTypeNumberDataService( @ConfigProperty( name = "forms.entryTypeNumberDataService.name" ) String strName )
	{
		return new EntryTypeIterableDataService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeTextAreaDataService" )
	public IEntryDataService produceEntryTypeTextAreaDataService( @ConfigProperty( name = "forms.entryTypeTextAreaDataService.name" ) String strName )
	{
		return new EntryTypeIterableDataService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeMyLuteceUserattributeDataService" )
	public IEntryDataService produceEntryTypeMyLuteceUserattributeDataService( @ConfigProperty( name = "forms.entryTypeMyLuteceUserattributeDataService.name" ) String strName )
	{
		return new EntryTypeIterableDataService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeArrayDataService" )
	public IEntryDataService produceEntryTypeArrayDataService( @ConfigProperty( name = "forms.entryTypeArrayDataService.name" ) String strName )
	{
		return new EntryTypeIterableDataService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeTermsOfServiceDataService" )
	public IEntryDataService produceEntryTypeTermsOfServiceDataService( @ConfigProperty( name = "forms.entryTypeTermsOfServiceDataService.name" ) String strName )
	{
		return new EntryTypeIterableDataService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeAutomaticFileReadingDataService" )
	public IEntryDataService produceEntryTypeAutomaticFileReadingDataService( @ConfigProperty( name = "forms.entryTypeAutomaticFileReadingDataService.name" ) String strName )
	{
		return new EntryTypeFileDataService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeTelephoneNumberDataService" )
	public IEntryDataService produceEntryTypeTelephoneNumberDataService( @ConfigProperty( name = "forms.entryTypeTelephoneNumberDataService.name" ) String strName )
	{
		return new EntryTypeIterableDataService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeCameraDataService" )
	public IEntryDataService produceEntryTypeCameraDataService( @ConfigProperty( name = "forms.entryTypeCameraDataService.name" ) String strName )
	{
		return new EntryTypeFileDataService( strName );
	}
	
	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeSlotDataService" )
	public IEntryDataService produceEntryTypeIterableDataService( @ConfigProperty( name = "forms.entryTypeSlotDataService.name" ) String strName )
	{
		return new EntryTypeIterableDataService( strName );
	}
}
