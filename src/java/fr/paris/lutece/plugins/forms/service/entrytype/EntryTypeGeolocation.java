/*
 * Copyright (c) 2002-2018, Mairie de Paris
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
package fr.paris.lutece.plugins.forms.service.entrytype;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.util.FormsDisplayUtils;
import fr.paris.lutece.plugins.forms.util.FormsEntryUtils;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.MapProviderManager;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeGeolocation;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;

/**
 * The Class EntryTypeGeolocation.
 */
public class EntryTypeGeolocation extends AbstractEntryTypeGeolocation implements IResponseComparator
{
    /** The Constant CONSTANT_ID_ADDRESS. */
    private static final String TEMPLATE_CREATE = "admin/plugins/forms/entries/create_entry_type_geolocation.html";
    private static final String TEMPLATE_MODIFY = "admin/plugins/forms/entries/modify_entry_type_geolocation.html";
    private static final String TEMPLATE_READONLY_BACKOFFICE = "admin/plugins/forms/entries/readonly_entry_type_geolocation.html";
    private static final String TEMPLATE_EDITION_BACKOFFICE = "admin/plugins/forms/entries/fill_entry_type_geolocation.html";
    private static final String TEMPLATE_EDITION_FRONTOFFICE = "skin/plugins/forms/entries/fill_entry_type_geolocation.html";
    private static final String TEMPLATE_READONLY_FRONTOFFICE = "skin/plugins/forms/entries/readonly_entry_type_geolocation.html";
    private static final int INTEGER_MINUS_ONE = -1;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateCreate( Entry entry, boolean bDisplayFront )
    {
        return TEMPLATE_CREATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateModify( Entry entry, boolean bDisplayFront )
    {
        return TEMPLATE_MODIFY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateHtmlForm( Entry entry, boolean bDisplayFront )
    {
        if ( bDisplayFront )
        {
            return TEMPLATE_EDITION_FRONTOFFICE;
        }

        return TEMPLATE_EDITION_BACKOFFICE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateEntryReadOnly( boolean bDisplayFront )
    {
        if ( bDisplayFront )
        {
            return TEMPLATE_READONLY_FRONTOFFICE;
        }

        return TEMPLATE_READONLY_BACKOFFICE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isResponseChanged( List<Response> listResponseReference, List<Response> listResponseNew )
    {
        if ( listResponseReference.size( ) != listResponseNew.size( ) )
        {
            return true;
        }

        boolean bAllResponsesEquals = true;

        for ( Response responseNew : listResponseNew )
        {
            Response responseReference = findReferenceResponseAssociatedToNewResponse( responseNew, listResponseReference );

            if ( responseReference == null || !responseReference.getResponseValue( ).equals( responseNew.getResponseValue( ) ) )
            {
                bAllResponsesEquals = false;
                break;
            }
        }

        return !bAllResponsesEquals;
    }

    /**
     * Finds the reference response associated to the new response
     * 
     * @param responseNew
     *            the new response
     * @param listResponseReference
     *            the list of reference responses
     * @return the found response or {@code null} if not found
     */
    private Response findReferenceResponseAssociatedToNewResponse( Response responseNew, List<Response> listResponseReference )
    {
        Response response = null;

        for ( Response responseReference : listResponseReference )
        {
            if ( responseReference.getField( ).getTitle( ).equals( responseNew.getField( ).getTitle( ) ) )
            {
                response = responseReference;
                break;
            }
        }

        return response;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequestData( Entry entry, HttpServletRequest request, Locale locale )
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strHelpMessage = ( request.getParameter( PARAMETER_HELP_MESSAGE ) != null ) ? request.getParameter( PARAMETER_HELP_MESSAGE ).trim( ) : null;
        String strComment = request.getParameter( PARAMETER_COMMENT );
        String strMandatory = request.getParameter( PARAMETER_MANDATORY );
        String strMapProvider = request.getParameter( PARAMETER_MAP_PROVIDER );
        String strEditMode = request.getParameter( PARAMETER_EDIT_MODE );
        String strViewNumber = request.getParameter( PARAMETER_VIEW_NUMBER );
        String strCSSClass = request.getParameter( PARAMETER_CSS_CLASS );

        int nIdStep = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ) );

        int nParentGroup = Integer.parseInt( request.getParameter( FormsConstants.PARAMETER_ID_DISPLAY_PARENT ) );

        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        int nIdEntry = Integer.parseInt( strIdEntry );

        String strIdResource = request.getParameter( PARAMETER_ID_RESOURCE );
        int nIdResource = Integer.parseInt( strIdResource );

        String strFieldError = StringUtils.EMPTY;

        if ( StringUtils.isBlank( strTitle ) )
        {
            strFieldError = FIELD_TITLE;
        }

        if ( StringUtils.isNotBlank( strFieldError ) )
        {
            Object [ ] tabRequiredFields = {
                    I18nService.getLocalizedString( strFieldError, locale ),
            };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        /**
         * we need 10 fields : 1 for map provider, 1 for id address, 1 for label address, 1 for x address, 1 for y address, 1 for id geographical object, 1 for
         * description geographical object, 1 for centroid geographical object, 1 for label geographical object and 1 for thematic geographical object
         **/
        if ( entry.getFields( ) == null )
        {
            List<Field> listFields = new ArrayList<Field>( );
            listFields.add( buildFieldMapProvider( entry, strMapProvider ) );
            listFields.add( buildField( entry, CONSTANT_EDIT_MODE, strEditMode ) );
            listFields.add( buildField( entry, CONSTANT_VIEW_NUMBER, strViewNumber ) );
            listFields.add( buildField( entry, CONSTANT_ID_ADDRESS, CONSTANT_ID_ADDRESS ) );
            listFields.add( buildField( entry, CONSTANT_ADDRESS, CONSTANT_ADDRESS ) );
            // listFields.add( buildField( entry, CONSTANT_ADDITIONAL_ADDRESS, CONSTANT_ADDITIONAL_ADDRESS ) );
            listFields.add( buildField( entry, CONSTANT_X, CONSTANT_X ) );
            listFields.add( buildField( entry, CONSTANT_Y, CONSTANT_Y ) );
            listFields.add( buildField( entry, CONSTANT_GEOMETRY, CONSTANT_GEOMETRY ) );

            entry.setFields( listFields );
        }
        else
        {
            entry.getFields( ).get( 0 ).setTitle( buildFieldMapProvider( entry, strMapProvider ).getTitle( ) );
            entry.getFields( ).get( 0 ).setValue( buildFieldMapProvider( entry, strMapProvider ).getValue( ) );

            entry.getFields( ).get( 1 ).setTitle( buildField( entry, CONSTANT_EDIT_MODE, strEditMode ).getTitle( ) );
            entry.getFields( ).get( 1 ).setValue( buildField( entry, CONSTANT_EDIT_MODE, strEditMode ).getValue( ) );

            entry.getFields( ).get( 2 ).setTitle( buildField( entry, CONSTANT_VIEW_NUMBER, strViewNumber ).getTitle( ) );
            entry.getFields( ).get( 2 ).setValue( buildField( entry, CONSTANT_VIEW_NUMBER, strViewNumber ).getValue( ) );

            entry.getFields( ).get( 3 ).setTitle( buildField( entry, CONSTANT_ID_ADDRESS, CONSTANT_ID_ADDRESS ).getTitle( ) );
            entry.getFields( ).get( 3 ).setValue( buildField( entry, CONSTANT_ID_ADDRESS, CONSTANT_ID_ADDRESS ).getValue( ) );

            entry.getFields( ).get( 4 ).setTitle( buildField( entry, CONSTANT_ADDRESS, CONSTANT_ADDRESS ).getTitle( ) );
            entry.getFields( ).get( 4 ).setValue( buildField( entry, CONSTANT_ADDRESS, CONSTANT_ADDRESS ).getValue( ) );

            // entry.getFields().get(5).setTitle(buildField( entry, CONSTANT_ADDITIONAL_ADDRESS, CONSTANT_ADDITIONAL_ADDRESS ).getTitle());
            // entry.getFields().get(5).setValue(buildField( entry, CONSTANT_ADDITIONAL_ADDRESS, CONSTANT_ADDITIONAL_ADDRESS).getValue());
            entry.getFields( ).get( 5 ).setTitle( buildField( entry, CONSTANT_X, CONSTANT_X ).getTitle( ) );
            entry.getFields( ).get( 5 ).setValue( buildField( entry, CONSTANT_X, CONSTANT_X ).getValue( ) );

            entry.getFields( ).get( 6 ).setTitle( buildField( entry, CONSTANT_Y, CONSTANT_Y ).getTitle( ) );
            entry.getFields( ).get( 6 ).setValue( buildField( entry, CONSTANT_Y, CONSTANT_Y ).getValue( ) );

            entry.getFields( ).get( 7 ).setTitle( buildField( entry, CONSTANT_GEOMETRY, CONSTANT_GEOMETRY ).getTitle( ) );
            entry.getFields( ).get( 7 ).setValue( buildField( entry, CONSTANT_GEOMETRY, CONSTANT_GEOMETRY ).getValue( ) );
        }

        entry.setTitle( strTitle );
        entry.setHelpMessage( strHelpMessage );
        entry.setComment( strComment );
        entry.setCSSClass( strCSSClass );
        entry.setMandatory( strMandatory != null );

        Entry entryGeolocation = EntryHome.findByPrimaryKey( nIdEntry );
        boolean update = false;

        if ( entryGeolocation != null )
        {
            update = true;
        }
        else
        {
            Entry entryAdditionalAddress = new Entry( );

            // EntryType entryType = EntryTypeHome.findByPrimaryKey( 6 );
            EntryType entryType = FormsEntryUtils.getEntryTypebyBeanName( "forms.entryTypeText" );
            entryAdditionalAddress.setEntryType( entryType );
            entryAdditionalAddress.setIdResource( nIdResource );
            entryAdditionalAddress.setResourceType( Form.RESOURCE_TYPE );

            getAdditionalAddressEntry( entryAdditionalAddress, locale, update );

            Question question = new Question( );
            String strTitleAdditionnalAddress =  entryAdditionalAddress.getTitle( );
            question.setTitle( strTitleAdditionnalAddress );
            question.setDescription( entryAdditionalAddress.getComment( ) );
            question.setIdEntry( entryAdditionalAddress.getIdEntry( ) );
            question.setIdStep( nIdStep );
            QuestionHome.create( question );

            int nDisplayDepth = FormsDisplayUtils.getDisplayDepthFromParent( nParentGroup );

            if ( question.getId( ) != INTEGER_MINUS_ONE )
            {
                Step step = StepHome.findByPrimaryKey( nIdStep );

                FormDisplay formDisplay = new FormDisplay( );
                formDisplay.setFormId( step.getIdForm( ) );
                formDisplay.setStepId( nIdStep );
                formDisplay.setParentId( nParentGroup );
                formDisplay.setCompositeId( question.getId( ) );
                formDisplay.setCompositeType( CompositeDisplayType.QUESTION.getLabel( ) );
                formDisplay.setDepth( nDisplayDepth );
                FormDisplayHome.create( formDisplay );
            }
        }
        return null;
    }
    



    /**
     * Builds the field.
     * 
     * @param entry
     *            The entry
     * @param strFieldTitle
     *            the str field title
     * @param strFieldValue
     *            the str field value
     * @return the field
     */
    private Field buildField( Entry entry, String strFieldTitle, String strFieldValue )
    {
        Field field = new Field( );
        field.setTitle( strFieldTitle );
        field.setValue( strFieldValue );
        field.setParentEntry( entry );

        return field;
    }

    /**
     * Builds the field map provider.
     * 
     * @param entry
     *            The entry
     * @param strMapProvider
     *            the map provider
     * @return the field
     */
    private Field buildFieldMapProvider( Entry entry, String strMapProvider )
    {
        Field fieldMapProvider = new Field( );
        fieldMapProvider.setTitle( CONSTANT_PROVIDER );

        if ( StringUtils.isNotBlank( strMapProvider ) )
        {
            String strTrimedMapProvider = strMapProvider.trim( );
            fieldMapProvider.setValue( strTrimedMapProvider );
            entry.setMapProvider( MapProviderManager.getMapProvider( strTrimedMapProvider ) );
        }
        else
        {
            fieldMapProvider.setValue( StringUtils.EMPTY );
        }

        fieldMapProvider.setParentEntry( entry );

        return fieldMapProvider;
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public void getAdditionalAddressEntry( Entry entry, Locale locale, boolean update )
    {
        entry.setTitle( I18nService.getLocalizedString( "forms.entrytype.geolocation.additional_address.title", locale ) );
        entry.setHelpMessage( "" );
        entry.setComment( "" );
        entry.setCSSClass( "" );
        entry.setErrorMessage( "" );

        if ( entry.getFields( ) == null )
        {
            ArrayList<Field> listFields = new ArrayList<Field>( );
            Field field = new Field( );
            field.setTitle( "additionalAddress" );
            field.setValue( "" );
            // FieldHome.create(field, pluginDirectory);
            listFields.add( field );
            entry.setFields( listFields );
        }

        entry.getFields( ).get( 0 ).setWidth( 0 );
        entry.getFields( ).get( 0 ).setMaxSizeEnter( 250 );

        EntryHome.create( entry );

        if ( entry.getFields( ) != null )
        {
            for ( Field field : entry.getFields( ) )
            {
                field.setParentEntry( entry );
                FieldHome.create( field );
            }
        }

    }
}
