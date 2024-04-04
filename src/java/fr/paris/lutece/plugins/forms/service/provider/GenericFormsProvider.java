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

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeAutomaticFileReading;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeCamera;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeComment;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeFile;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeGalleryImage;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeImage;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.web.admin.MultiviewFormResponseDetailsJspBean;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.provider.InfoMarker;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFileHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.url.UrlItem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
public abstract class GenericFormsProvider {
    // MESSAGE
    private static final String MESSAGE_DESCRIPTION = "forms.marker.provider.url.admin.detail.reponse.description";
    private static final String MESSAGE_FO_DESCRIPTION = "forms.marker.provider.url.fo.detail.reponse.description";
    private static final String MESSAGE_CREATION_DATE = "forms.marker.provider.url.detail.reponse.creation_date";
    private static final String MESSAGE_UPDATE_DATE = "forms.marker.provider.url.detail.reponse.update_date";
    private static final String MESSAGE_STATUS = "forms.marker.provider.url.detail.reponse.status";
    private static final String MESSAGE_STATUS_UPDATE_DATE = "forms.marker.provider.url.detail.reponse.status_update_date";
    private static final String MESSAGE_FORM = "forms.marker.provider.url.detail.reponse.form";

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
    private static final String MARK_BASE_URL = "base_url";
    private static final String MARKER_DESCRIPTION_BASE64 = "base64";
    // PARAMETERS
    public static final String PARAMETER_VIEW_FORM_RESPONSE_DETAILS = "view_form_response_details";
    public static final String PARAMETER_VIEW_FORM_RESPONSE_DETAILS_FO = "formResponseView";
    public static final String PARAMETER_ID_FORM_RESPONSES = "id_form_response";
    public static final String PARAMETER_ID_FORM_RESPONSES_FO = "id_response";
    public static final String PARAMETER_PAGE_FORM_RESPONSE = "formsResponse";
    private static final String FORM_TITLE = "form_title";

    // FIELDS
    protected final FormResponse _formResponse;
    private final HttpServletRequest _request;

    public GenericFormsProvider(ResourceHistory resourceHistory, HttpServletRequest request) {
        // Get the form response from the resourceHistory
        _formResponse = FormResponseHome.findByPrimaryKey(resourceHistory.getIdResource());
        _request = request;
    }



    /**
     * {@inheritDoc}
     */
      public static Map<String, InfoMarker> provideMarkerValues( FormResponse formResponse, HttpServletRequest request ){
        Map<String, InfoMarker> result = new HashMap<>( );

        List<FormQuestionResponse> listFormQuestionResponse = FormQuestionResponseHome.getFormQuestionResponseListByFormResponse( formResponse.getId( ) );
        Boolean bExportImageBase64 = AppPropertiesService.getPropertyBoolean("forms.export.image.base64", false);
          for ( FormQuestionResponse formQuestionResponse : listFormQuestionResponse ) {
            InfoMarker marker = new InfoMarker(MARK_POSITION + formQuestionResponse.getQuestion().getId());
            IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService(formQuestionResponse.getQuestion().getEntry());
            if (entryTypeService instanceof EntryTypeComment || entryTypeService instanceof EntryTypeImage || entryTypeService instanceof EntryTypeCamera || entryTypeService instanceof EntryTypeGalleryImage || entryTypeService instanceof EntryTypeFile || entryTypeService instanceof EntryTypeAutomaticFileReading) {
                List<fr.paris.lutece.plugins.genericattributes.business.Response> responses = formQuestionResponse.getEntryResponse();
                for (int i = 0; i < responses.size(); i++) {
                    if (responses.get(i).getFile() != null) {
                        File file = FileHome.findByPrimaryKey(responses.get(i).getFile().getIdFile());
                        if (entryTypeService instanceof EntryTypeImage || entryTypeService instanceof EntryTypeCamera || entryTypeService instanceof EntryTypeGalleryImage) {
                            // if property forms.export.image.base64 is true
                            if (bExportImageBase64) {
                                PhysicalFile physicalFile = PhysicalFileHome.findByPrimaryKey(file.getPhysicalFile().getIdPhysicalFile());
                                byte[] bytes = physicalFile.getValue();
                                String strBase64 = java.util.Base64.getEncoder().encodeToString(bytes);
                                responses.get(i).setResponseValue(strBase64);
                            }
                        }
                        responses.get(i).setFile(file);
                    }
                }
            }
            // in case of multiple FormQuestionResponse for the same question (when there is an iteration), we merge them into one FormQuestionResponse and update the marker
            if(result.containsKey(MARK_POSITION + formQuestionResponse.getQuestion().getId())){
                InfoMarker existingMarker = result.get(MARK_POSITION + formQuestionResponse.getQuestion().getId());
                FormQuestionResponse existingFormQuestionResponse = (FormQuestionResponse) existingMarker.getValue();
                List<Response> existingResponses = existingFormQuestionResponse.getEntryResponse();
                List<Response> newResponses = formQuestionResponse.getEntryResponse();
                List<Response> allResponses = new ArrayList<>();
                allResponses.addAll(existingResponses);
                allResponses.addAll(newResponses);
                existingFormQuestionResponse.setEntryResponse(allResponses);
                existingMarker.setValue(existingFormQuestionResponse);
                result.replace(MARK_POSITION + formQuestionResponse.getQuestion().getId(), existingMarker);
            } else {
                marker.setValue(formQuestionResponse);
                result.put(MARK_POSITION + formQuestionResponse.getQuestion().getId(), marker);
            }
        }
          InfoMarker markerBaseUrl = new InfoMarker(MARK_BASE_URL);
          markerBaseUrl.setValue(AppPathService.getBaseUrl(request));
            result.put(MARK_BASE_URL, markerBaseUrl);

          InfoMarker markerBase64 = new InfoMarker(MARKER_DESCRIPTION_BASE64);
          markerBase64.setValue(bExportImageBase64);
          result.put(MARKER_DESCRIPTION_BASE64, markerBase64);

          InfoMarker markerAdminUrl = new InfoMarker( MARK_URL_ADMIN_RESPONSE );
          UrlItem adminUrl = new UrlItem( AppPathService.getProdUrl( request ) + AppPathService.getAdminMenuUrl( ) );
          adminUrl.addParameter( FormsConstants.PARAMETER_TARGET_VIEW, PARAMETER_VIEW_FORM_RESPONSE_DETAILS );
          adminUrl.addParameter( PARAMETER_ID_FORM_RESPONSES, formResponse.getId( ) );
          markerAdminUrl.setValue( adminUrl.getUrl( ) );
          result.put( MARK_URL_ADMIN_RESPONSE, markerAdminUrl );

          InfoMarker markerFOUrl = new InfoMarker( MARK_URL_FO_RESPONSE );
          UrlItem url = new UrlItem( AppPathService.getProdUrl( request ) + AppPathService.getPortalUrl( ) );
          url.addParameter( FormsConstants.PARAMETER_PAGE, PARAMETER_PAGE_FORM_RESPONSE );
          url.addParameter( FormsConstants.PARAMETER_TARGET_VIEW, PARAMETER_VIEW_FORM_RESPONSE_DETAILS_FO );
          url.addParameter( PARAMETER_ID_FORM_RESPONSES_FO, formResponse.getId( ) );
          markerFOUrl.setValue( url.getUrl( ) );
          result.put( MARK_URL_FO_RESPONSE, markerFOUrl );

          InfoMarker creationDateMarker = new InfoMarker( MARK_CREATION_DATE );
          creationDateMarker.setValue( formResponse.getCreation( ) );
            result.put( MARK_CREATION_DATE, creationDateMarker );

          InfoMarker updateDateMarker = new InfoMarker( MARK_UPDATE_DATE );
          updateDateMarker.setValue( formResponse.getCreation( ) );
            result.put( MARK_UPDATE_DATE, updateDateMarker );
          InfoMarker statusMarker = new InfoMarker( MARK_STATUS );
          statusMarker.setValue( String.valueOf( formResponse.isPublished( ) ) );
            result.put( MARK_STATUS, statusMarker );

          InfoMarker updateStatusDateMarker = new InfoMarker( MARK_STATUS_UPDATE_DATE );
          updateStatusDateMarker.setValue( formResponse.getUpdateStatus( ) );
            result.put( MARK_STATUS_UPDATE_DATE, updateStatusDateMarker );

          InfoMarker formTitleMarker = new InfoMarker( FORM_TITLE );
          formTitleMarker.setValue(fr.paris.lutece.plugins.forms.business.FormHome.findByPrimaryKey(formResponse.getFormId()).getTitle());
          result.put(FORM_TITLE, formTitleMarker );

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
        InfoMarker formTitleMarker = new InfoMarker( FORM_TITLE );
        formTitleMarker.setDescription( I18nService.getLocalizedString( MESSAGE_FORM, I18nService.getDefaultLocale( ) ) );
        
        collectionNotifyMarkers.add( creationDateMarker );
        collectionNotifyMarkers.add( updateDateMarker );
        collectionNotifyMarkers.add( statusMarker );
        collectionNotifyMarkers.add( updateStatusDateMarker );
        collectionNotifyMarkers.add( formTitleMarker );
        
        return collectionNotifyMarkers;
    }

}
