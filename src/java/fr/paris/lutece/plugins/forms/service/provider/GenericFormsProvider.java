/*
 * Copyright (c) 2002-2022, City of Paris
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
package fr.paris.lutece.plugins.forms.service.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeComment;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.web.admin.MultiviewFormResponseDetailsJspBean;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.provider.IProvider;
import fr.paris.lutece.plugins.workflowcore.service.provider.InfoMarker;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.IFileStoreServiceProvider;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.url.UrlItem;

public abstract class GenericFormsProvider implements IProvider
{
    // MESSAGE
    private static final String MESSAGE_DESCRIPTION = "forms.marker.provider.url.admin.detail.reponse.description";
    private static final String MESSAGE_FO_DESCRIPTION = "forms.marker.provider.url.fo.detail.reponse.description";
    private static final String MESSAGE_CREATION_DATE = "forms.marker.provider.url.detail.reponse.creation_date";
    private static final String MESSAGE_UPDATE_DATE = "forms.marker.provider.url.detail.reponse.update_date";
    private static final String MESSAGE_STATUS = "forms.marker.provider.url.detail.reponse.status";
    private static final String MESSAGE_STATUS_UPDATE_DATE = "forms.marker.provider.url.detail.reponse.status_update_date";

    // PROPERTY
    private static final String PROPERTY_LUTECE_ADMIN_PROD_URL = "lutece.admin.prod.url";
    // MARKS
    private static final String MARK_POSITION = "position_";
    private static final String MARK_URL_ADMIN_RESPONSE = "url_admin_forms_response_detail";
    private static final String MARK_URL_FO_RESPONSE = "url_fo_forms_response_detail";
    private static final String MARK_CREATION_DATE = "creation_date";
    private static final String MARK_UPDATE_DATE = "update_date";
    private static final String MARK_STATUS = "status";
    private static final String MARK_STATUS_UPDATE_DATE = "update_date_status";
    // PARAMETERS
    public static final String PARAMETER_VIEW_FORM_RESPONSE_DETAILS = "view_form_response_details";
    public static final String PARAMETER_VIEW_FORM_RESPONSE_DETAILS_FO = "formResponseView";
    public static final String PARAMETER_ID_FORM_RESPONSES = "id_form_response";
    public static final String PARAMETER_ID_FORM_RESPONSES_FO = "id_response";
    public static final String PARAMETER_PAGE_FORM_RESPONSE = "formsResponse";

    // FIELDS
    protected final FormResponse _formResponse;
    private final HttpServletRequest _request;

    public GenericFormsProvider( ResourceHistory resourceHistory, HttpServletRequest request )
    {
        // Get the form response from the resourceHistory
        _formResponse = FormResponseHome.findByPrimaryKey( resourceHistory.getIdResource( ) );
        _request = request;
    }

    @Override
    public String provideDemandId( )
    {
        return String.valueOf( _formResponse.getId( ) );
    }

    @Override
    public String provideDemandSubtypeId( )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<InfoMarker> provideMarkerValues( )
    {
        Collection<InfoMarker> result = new ArrayList<>( );

        List<FormQuestionResponse> listFormQuestionResponse = FormQuestionResponseHome.getFormQuestionResponseListByFormResponse( _formResponse.getId( ) );

        Map<Integer, InfoMarker> markers = new HashMap<>( );
        for ( FormQuestionResponse formQuestionResponse : listFormQuestionResponse )
        {
            InfoMarker notifyMarker = markers.computeIfAbsent( formQuestionResponse.getQuestion( ).getId( ),
                    ( k ) -> new InfoMarker( MARK_POSITION + formQuestionResponse.getQuestion( ).getId( ) ) );
            IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( formQuestionResponse.getQuestion( ).getEntry( ) );
            String value = "";
            if ( entryTypeService instanceof EntryTypeComment )
            {
                Entry entry = formQuestionResponse.getQuestion( ).getEntry( );
                Field fieldFile = entry.getFieldByCode( IEntryTypeService.FIELD_DOWNLOADABLE_FILE );
                if ( fieldFile != null )
                {
                    IFileStoreServiceProvider fileStoreprovider = FileService.getInstance( ).getFileStoreServiceProvider( "formsDatabaseFileStoreProvider" );

                    Map<String, String> additionnalData = new HashMap<>( );
                    additionnalData.put( FileService.PARAMETER_RESOURCE_ID, String.valueOf( entry.getIdResource( ) ) );
                    additionnalData.put( FileService.PARAMETER_RESOURCE_TYPE, Form.RESOURCE_TYPE );
                    additionnalData.put( FileService.PARAMETER_PROVIDER, fileStoreprovider.getName( ) );

                    value = fileStoreprovider.getFileDownloadUrlFO( fieldFile.getValue( ), additionnalData );
                }
            }
            else
                if ( CollectionUtils.isNotEmpty( formQuestionResponse.getEntryResponse( ) ) )
                {
                    value = formQuestionResponse.getEntryResponse( ).stream( ).map(
                            response -> entryTypeService.getResponseValueForRecap( formQuestionResponse.getQuestion( ).getEntry( ), _request, response, null ) )
                            .filter( StringUtils::isNotEmpty ).collect( Collectors.joining( ", " ) );
                }
            if ( notifyMarker.getValue( ) == null )
            {
                notifyMarker.setValue( value );
            }
            else
            {
                notifyMarker.setValue( notifyMarker.getValue( ) + "<br>" + value );
            }
            AppLogService.debug( "Adding infomarker " + notifyMarker.getMarker( ) + "=" + notifyMarker.getValue( ) );
        }
        result.addAll( markers.values( ) );

        InfoMarker notifyMarkerAdminUrl = new InfoMarker( MARK_URL_ADMIN_RESPONSE );
        UrlItem adminUrl = new UrlItem( AppPropertiesService.getProperty( PROPERTY_LUTECE_ADMIN_PROD_URL ) + MultiviewFormResponseDetailsJspBean.CONTROLLER_JSP_NAME_WITH_PATH );
        adminUrl.addParameter( FormsConstants.PARAMETER_TARGET_VIEW, PARAMETER_VIEW_FORM_RESPONSE_DETAILS );
        adminUrl.addParameter( PARAMETER_ID_FORM_RESPONSES, _formResponse.getId( ) );
        notifyMarkerAdminUrl.setValue( adminUrl.getUrl( ) );
        result.add( notifyMarkerAdminUrl );
        
        InfoMarker notifyMarkerFOUrl = new InfoMarker( MARK_URL_FO_RESPONSE );
        UrlItem url = new UrlItem( AppPathService.getProdUrl( _request ) + AppPathService.getPortalUrl( ) );
        url.addParameter( FormsConstants.PARAMETER_PAGE, PARAMETER_PAGE_FORM_RESPONSE );
        url.addParameter( FormsConstants.PARAMETER_TARGET_VIEW, PARAMETER_VIEW_FORM_RESPONSE_DETAILS_FO );
        url.addParameter( PARAMETER_ID_FORM_RESPONSES_FO, _formResponse.getId( ) );
        notifyMarkerFOUrl.setValue( url.getUrl( ) );
        result.add( notifyMarkerFOUrl );
        
        InfoMarker creationDateMarker = new InfoMarker( MARK_CREATION_DATE );
        creationDateMarker.setValue( _formResponse.getCreation( ).toString( ) );
        InfoMarker updateDateMarker = new InfoMarker( MARK_UPDATE_DATE );
        updateDateMarker.setValue( _formResponse.getCreation( ).toString( ) );
        InfoMarker statusMarker = new InfoMarker( MARK_STATUS );
        statusMarker.setValue( String.valueOf( _formResponse.isPublished( ) ) );
        InfoMarker updateStatusDateMarker = new InfoMarker( MARK_STATUS_UPDATE_DATE );
        updateStatusDateMarker.setValue( _formResponse.getUpdateStatus( ).toString( ) );
        return result;
    }

    /**
     * Get the collection of InfoMarker, for the given form
     * 
     * @param form
     *            The form
     * @return the collection of the notifyMarkers
     */
    public static Collection<InfoMarker> getProviderMarkerDescriptions( Form form )
    {
        Collection<InfoMarker> collectionNotifyMarkers = new ArrayList<>( );

        List<Question> listFormQuestions = QuestionHome.getListQuestionByIdForm( form.getId( ) );

        for ( Question formQuestion : listFormQuestions )
        {
            InfoMarker notifyMarker = new InfoMarker( MARK_POSITION + formQuestion.getId( ) );
            notifyMarker.setDescription( formQuestion.getTitle( ) );
            collectionNotifyMarkers.add( notifyMarker );
        }
        InfoMarker notifyMarkerAdminURl = new InfoMarker( MARK_URL_ADMIN_RESPONSE );
        notifyMarkerAdminURl.setDescription( I18nService.getLocalizedString( MESSAGE_DESCRIPTION, I18nService.getDefaultLocale( ) ) );
        collectionNotifyMarkers.add( notifyMarkerAdminURl );
        
        InfoMarker notifyMarkerFOURl = new InfoMarker( MARK_URL_FO_RESPONSE );
        notifyMarkerFOURl.setDescription( I18nService.getLocalizedString( MESSAGE_FO_DESCRIPTION, I18nService.getDefaultLocale( ) ) );
        collectionNotifyMarkers.add( notifyMarkerFOURl );
        
        InfoMarker creationDateMarker = new InfoMarker( MARK_CREATION_DATE );
        creationDateMarker.setDescription( I18nService.getLocalizedString( MESSAGE_CREATION_DATE, I18nService.getDefaultLocale( ) ) );
        InfoMarker updateDateMarker = new InfoMarker( MARK_UPDATE_DATE );
        updateDateMarker.setDescription( I18nService.getLocalizedString( MESSAGE_UPDATE_DATE, I18nService.getDefaultLocale( ) ) );
        InfoMarker statusMarker = new InfoMarker( MARK_STATUS );
        statusMarker.setDescription( I18nService.getLocalizedString( MESSAGE_STATUS, I18nService.getDefaultLocale( ) ) );
        InfoMarker updateStatusDateMarker = new InfoMarker( MARK_STATUS_UPDATE_DATE );
        updateStatusDateMarker.setDescription( I18nService.getLocalizedString( MESSAGE_STATUS_UPDATE_DATE, I18nService.getDefaultLocale( ) ) );
        
        collectionNotifyMarkers.add( creationDateMarker );
        collectionNotifyMarkers.add( updateDateMarker );
        collectionNotifyMarkers.add( statusMarker );
        collectionNotifyMarkers.add( updateStatusDateMarker );
        
        return collectionNotifyMarkers;
    }

}
