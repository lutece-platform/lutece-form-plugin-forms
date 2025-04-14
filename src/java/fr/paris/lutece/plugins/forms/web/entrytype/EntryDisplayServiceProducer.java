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
public class EntryDisplayServiceProducer 
{
	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeCheckBoxDisplayService" )
    public IEntryDisplayService produceEntryTypeCheckBoxDisplayService( 
    		@ConfigProperty( name = "forms.entryTypeCheckBoxDisplayService.name" ) String strName)
    {
        return new EntryTypeDefaultDisplayService( strName );
    }
	
	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeCommentDisplayService" )
	public IEntryDisplayService produceEntryTypeCommentDisplayService( 
			@ConfigProperty( name = "forms.entryTypeCommentDisplayService.name" ) String strName )
	{
		return new EntryTypeCommentDisplayService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeDateDisplayService" )
	public IEntryDisplayService produceEntryTypeDateDisplayService( 
			@ConfigProperty( name = "forms.entryTypeDateDisplayService.name" ) String strName )
	{
		return new EntryTypeDateDisplayService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeFileDisplayService" )
	public IEntryDisplayService produceEntryTypeFileDisplayService( 
			@ConfigProperty( name = "forms.entryTypeFileDisplayService.name" ) String strName )
	{
		return new EntryTypeFileDisplayService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeGeolocationDisplayService" )
	public IEntryDisplayService produceEntryTypeGeolocationDisplayService( 
			@ConfigProperty( name = "forms.entryTypeGeolocationDisplayService.name" ) String strName )
	{
		return new EntryTypeGeolocDisplayService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeImageDisplayService" )
	public IEntryDisplayService produceEntryTypeImageDisplayService( 
			@ConfigProperty( name = "forms.entryTypeImageDisplayService.name" ) String strName )
	{
		return new EntryTypeFileDisplayService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeGalleryImageDisplayService" )
	public IEntryDisplayService produceEntryTypeGalleryImageDisplayService( 
			@ConfigProperty( name = "forms.entryTypeGalleryImageDisplayService.name" ) String strName )
	{
		return new EntryTypeDefaultDisplayService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeMyLuteceUserDisplayService" )
	public IEntryDisplayService produceEntryTypeMyLuteceUserDisplayService( 
			@ConfigProperty( name = "forms.entryTypeMyLuteceUserDisplayService.name" ) String strName )
	{
		return new EntryTypeDefaultDisplayService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeNumberingDisplayService" )
	public IEntryDisplayService produceEntryTypeNumberingDisplayService( 
			@ConfigProperty( name = "forms.entryTypeNumberingDisplayService.name" ) String strName )
	{
		return new EntryTypeDefaultDisplayService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeRadioButtonDisplayService" )
	public IEntryDisplayService produceEntryTypeRadioButtonDisplayService( 
			@ConfigProperty( name = "forms.entryTypeRadioButtonDisplayService.name" ) String strName )
	{
		return new EntryTypeDefaultDisplayService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeSelectDisplayService" )
	public IEntryDisplayService produceEntryTypeSelectDisplayService( 
			@ConfigProperty( name = "forms.entryTypeSelectDisplayService.name" ) String strName )
	{
		return new EntryTypeDefaultDisplayService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeSelectOrderDisplayService" )
	public IEntryDisplayService produceEntryTypeSelectOrderDisplayService( 
			@ConfigProperty( name = "forms.entryTypeSelectOrderDisplayService.name" ) String strName )
	{
		return new EntryTypeDefaultDisplayService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeTextDisplayService" )
	public IEntryDisplayService produceEntryTypeTextDisplayService( 
			@ConfigProperty( name = "forms.entryTypeTextDisplayService.name" ) String strName )
	{
		return new EntryTypeDefaultDisplayService( strName );
	}
	
	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeNumberDisplayService" )
	public IEntryDisplayService produceEntryTypeNumberDisplayService( 
			@ConfigProperty( name = "forms.entryTypeNumberDisplayService.name" ) String strName )
	{
		return new EntryTypeDefaultDisplayService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeTextAreaDisplayService" )
	public IEntryDisplayService produceEntryTypeTextAreaDisplayService( 
			@ConfigProperty( name = "forms.entryTypeTextAreaDisplayService.name" ) String strName )
	{
		return new EntryTypeDefaultDisplayService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeMyLuteceUserattributeDisplayService" )
	public IEntryDisplayService produceEntryTypeMyLuteceUserattributeDisplayService( 
			@ConfigProperty( name = "forms.entryTypeMyLuteceUserattributeDisplayService.name" ) String strName )
	{
		return new EntryTypeMyLuteceUserAttributeDisplayService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeArrayDisplayService" )
	public IEntryDisplayService produceEntryTypeArrayDisplayService( 
			@ConfigProperty( name = "forms.entryTypeArrayDisplayService.name" ) String strName )
	{
		return new EntryTypeDefaultDisplayService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeTermsOfSerciceDisplayService" )
	public IEntryDisplayService produceEntryTypeTermsOfSerciceDisplayService( 
			@ConfigProperty( name = "forms.entryTypeTermsOfSerciceDisplayService.name" ) String strName )
	{
		return new EntryTypeDefaultDisplayService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeAutomaticFileReadingDisplayService" )
	public IEntryDisplayService produceEntryTypeAutomaticFileReadingDisplayService( 
			@ConfigProperty( name = "forms.entryTypeAutomaticFileReadingDisplayService.name" ) String strName )
	{
		return new EntryTypeFileReadingDisplayService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeCameraDisplayService" )
	public IEntryDisplayService produceEntryTypeCameraDisplayService( 
			@ConfigProperty( name = "forms.entryTypeCameraDisplayService.name" ) String strName )
	{
		return new EntryTypeFileDisplayService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeTelephoneNumberDisplayService" )
	public IEntryDisplayService produceEntryTypeTelephoneNumberDisplayService( 
			@ConfigProperty( name = "forms.entryTypeTelephoneNumberDisplayService.name" ) String strName )
	{
		return new EntryTypeTelephoneDisplayService( strName );
	}

	@Produces
	@ApplicationScoped
	@Named( "forms.entryTypeSlotDisplayService" )
	public IEntryDisplayService produceEntryTypeSlotDisplayService( 
			@ConfigProperty( name = "forms.entryTypeSlotDisplayService.name" ) String strName )
	{
		return new EntryTypeDefaultDisplayService( strName );
	}
}
