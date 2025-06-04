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
package fr.paris.lutece.plugins.forms.service.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Group;
import fr.paris.lutece.plugins.forms.business.GroupHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.business.Transition;
import fr.paris.lutece.plugins.forms.business.TransitionHome;
import fr.paris.lutece.plugins.forms.service.StepService;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeAutomaticFileReading;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeCamera;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeComment;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeFile;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeGalleryImage;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeImage;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.workflowcore.service.provider.InfoMarker;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFileHome;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.FileServiceException;
import fr.paris.lutece.portal.service.file.IFileStoreServiceProvider;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.url.UrlItem;

public abstract class GenericFormsProvider {

	// PROPERTY
	private static final String PROPERTY_EXPORT_IMAGES_IN_BASE64 = "forms.export.image.base64";

	// MARKS
	private static final String MARK_POSITION = "position_";
	private static final String MARK_POSITION_ITERATION = "_";
	private static final String MARK_URL_ADMIN_RESPONSE = "url_admin_forms_response_detail";
	private static final String MARK_URL_FO_RESPONSE = "url_fo_forms_response_detail";
	private static final String MARK_CREATION_DATE = "creation_date";
	private static final String MARK_UPDATE_DATE = "update_date";
	private static final String MARK_STATUS = "status";
	private static final String MARK_STATUS_UPDATE_DATE = "update_date_status";
	private static final String MARK_BASE_URL = "base_url";
	private static final String MARKER_DESCRIPTION_BASE64 = "base64";
	private static final String MARKER_FORM_TITLE = "form_title";
	private static final String MARK_URL_FO_FILES_LINK = "url_fo_forms_files_link";

	// URL PARAMETERS
	private static final String PARAMETER_VIEW_FORM_RESPONSE_DETAILS = "view_form_response_details";
	private static final String PARAMETER_VIEW_FORM_RESPONSE_DETAILS_FO = "formResponseView";
	private static final String PARAMETER_ID_FORM_RESPONSES = "id_form_response";
	private static final String PARAMETER_ID_FORM_RESPONSES_FO = "id_response";
	private static final String PARAMETER_PAGE_FORM_RESPONSE = "formsResponse";
	private static final String PARAMETER_VIEW_FORM_FILES_LINK_FO = "formFileView";

	// Infomarkers description I18n keys
	private static final String MESSAGE_I18N_DESCRIPTION = "forms.marker.provider.url.admin.detail.reponse.description";
	private static final String MESSAGE_I18N_FO_DESCRIPTION = "forms.marker.provider.url.fo.detail.reponse.description";
	private static final String MESSAGE_I18N_CREATION_DATE = "forms.marker.provider.url.detail.reponse.creation_date";
	private static final String MESSAGE_I18N_UPDATE_DATE = "forms.marker.provider.url.detail.reponse.update_date";
	private static final String MESSAGE_I18N_STATUS = "forms.marker.provider.url.detail.reponse.status";
	private static final String MESSAGE_I18N_STATUS_UPDATE_DATE = "forms.marker.provider.url.detail.reponse.status_update_date";
	private static final String MESSAGE_I18N_FORM = "forms.marker.provider.url.detail.reponse.form";
	private static final String MESSAGE_I18N_FO_FILE_LINK = "forms.marker.provider.url.fo.file.link";

	/**
	 * provide forms values as model (map)
	 *
	 * @param formResponse
	 * @param request
	 * @return the model map
	 */
	public static Map<String, Object> getValuesModel( FormResponse formResponse, HttpServletRequest request )
	{
		Map<String, Object> model = new HashMap<>( );

		List<FormQuestionResponse> listFormQuestionResponse = FormQuestionResponseHome.getFormQuestionResponseListByFormResponse( formResponse.getId( ) );
		Boolean bExportImageBase64 = AppPropertiesService.getPropertyBoolean( PROPERTY_EXPORT_IMAGES_IN_BASE64, false);

		for ( FormQuestionResponse formQuestionResponse : listFormQuestionResponse )
		{
			IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService(formQuestionResponse.getQuestion().getEntry());

			if ( entryTypeService instanceof EntryTypeComment || entryTypeService instanceof EntryTypeImage || entryTypeService instanceof EntryTypeCamera || entryTypeService instanceof EntryTypeGalleryImage || entryTypeService instanceof EntryTypeFile || entryTypeService instanceof EntryTypeAutomaticFileReading)
			{
				List<fr.paris.lutece.plugins.genericattributes.business.Response> responses = formQuestionResponse.getEntryResponse( );
				for (int i = 0; i < responses.size(); i++)
				{
					if (responses.get(i).getFile() != null)
					{
						File file = FileHome.findByPrimaryKey( responses.get(i).getFile().getIdFile( ) );
						if (entryTypeService instanceof EntryTypeImage || entryTypeService instanceof EntryTypeCamera || entryTypeService instanceof EntryTypeGalleryImage)
						{
							// if property forms.export.image.base64 is true
							if (bExportImageBase64)
							{
								PhysicalFile physicalFile = PhysicalFileHome.findByPrimaryKey(file.getPhysicalFile().getIdPhysicalFile());
								byte[] bytes = physicalFile.getValue();
								String strBase64 = java.util.Base64.getEncoder().encodeToString(bytes);
								responses.get(i).setResponseValue(strBase64);
							}
						} else if (entryTypeService instanceof EntryTypeFile) {

							if ( file != null && file.getPhysicalFile( ) == null )
							{
								try {
									IFileStoreServiceProvider fss = FileService.getInstance( ).getFileStoreServiceProvider( file.getOrigin() );
									file = fss.getFile(file.getFileKey() );

									file.setUrl(fss.getFileDownloadUrlFO( file.getFileKey()));

								}
								catch (FileServiceException e) {
									AppLogService.error("Error getting file from file store service provider", e);
								}
							}
						}
						responses.get(i).setFile(file);
					}
				}
			}

			// in case of multiple FormQuestionResponse for the same question (when there is an iteration),
			// we merge them into one FormQuestionResponse and update the marker
			String strMultipleFormQuestionResponseKey = MARK_POSITION + formQuestionResponse.getQuestion().getId( ) ;
			if (model.containsKey( strMultipleFormQuestionResponseKey ) )
			{
				FormQuestionResponse existingFormQuestionResponse = (FormQuestionResponse) model.get( strMultipleFormQuestionResponseKey );

				List<Response> existingResponses = existingFormQuestionResponse.getEntryResponse();
				List<Response> newResponses = formQuestionResponse.getEntryResponse();
				List<Response> allResponses = new ArrayList<>();

				allResponses.addAll(existingResponses);
				allResponses.addAll(newResponses);

				existingFormQuestionResponse.setEntryResponse(allResponses);
				model.replace( strMultipleFormQuestionResponseKey, existingFormQuestionResponse);
			}
			else
			{
				model.put( strMultipleFormQuestionResponseKey , formQuestionResponse);
			}
		}

		// Additional markers
		model.put( MARKER_DESCRIPTION_BASE64, bExportImageBase64 );
		model.put( MARK_CREATION_DATE, formResponse.getCreation( )  );
		model.put( MARK_UPDATE_DATE, formResponse.getUpdate( ) );
		model.put( MARK_STATUS, formResponse.isPublished( ) );
		model.put( MARK_STATUS_UPDATE_DATE, formResponse.getUpdateStatus( )  );
		model.put( MARKER_FORM_TITLE, FormHome.findByPrimaryKey( formResponse.getFormId( ) ).getTitle( ) );

		// request may be null in case of daemon execution
		if ( request != null )
		{
			model.put( MARK_BASE_URL, AppPathService.getBaseUrl( request ) );

			UrlItem adminUrl = new UrlItem( AppPathService.getProdUrl( request ) + AppPathService.getAdminMenuUrl( ) );
			adminUrl.addParameter( FormsConstants.PARAMETER_TARGET_VIEW, PARAMETER_VIEW_FORM_RESPONSE_DETAILS );
			adminUrl.addParameter( PARAMETER_ID_FORM_RESPONSES, formResponse.getId( ) );
			model.put( MARK_URL_ADMIN_RESPONSE, adminUrl.getUrl( )  );

			UrlItem url = new UrlItem( AppPathService.getProdUrl( request ) + AppPathService.getPortalUrl( ) );
			url.addParameter( FormsConstants.PARAMETER_PAGE, PARAMETER_PAGE_FORM_RESPONSE );
			url.addParameter( FormsConstants.PARAMETER_TARGET_VIEW, PARAMETER_VIEW_FORM_RESPONSE_DETAILS_FO );
			url.addParameter( PARAMETER_ID_FORM_RESPONSES_FO, formResponse.getId( ) );
			model.put( MARK_URL_FO_RESPONSE, url.getUrl( ) );

			UrlItem urlFilesLinkFo = new UrlItem( AppPathService.getProdUrl( request ) + AppPathService.getPortalUrl( ) );
			urlFilesLinkFo.addParameter( FormsConstants.PARAMETER_PAGE, PARAMETER_PAGE_FORM_RESPONSE );
			urlFilesLinkFo.addParameter( FormsConstants.PARAMETER_TARGET_VIEW, PARAMETER_VIEW_FORM_FILES_LINK_FO );
			urlFilesLinkFo.addParameter( PARAMETER_ID_FORM_RESPONSES_FO, formResponse.getId( ) );
			model.put( MARK_URL_FO_FILES_LINK, url.getUrl( ) );
		}

		return model;
	}


	/**
	 * Get the question titles as model, for the given form
	 *
	 * @param form
	 *            The form
	 * @return the map of markers
	 */
	public static Map<String, Object> getTitlesModel( Form form )
	{
		Map<String, Object> model = new HashMap<>( );

		if (form != null )
		{
			List<Question> listFormQuestions = QuestionHome.getListQuestionByIdForm( form.getId( ) );

			for ( Question formQuestion : listFormQuestions )
			{
				model.put( MARK_POSITION + formQuestion.getId( ), formQuestion.getTitle( ) );
			}
		}

		return model;
	}

	/**
	 * Get the reference list of available InfoMarkers
	 *
	 * @param form
	 *            The form
	 * @return the collection of the Markers
	 */
	public static Collection<InfoMarker> getProviderMarkerDescriptions( Form form )
	{
		Collection<InfoMarker> descriptionMarkersList = new ArrayList<>( );

		if ( form != null )
		{

			List<Step> listSteps = StepHome.getStepsListByForm( form.getId() );
			List<Transition> listTransitions = TransitionHome.getTransitionsListFromForm( form.getId() );
			listSteps = StepService.sortStepsWithTransitions( listSteps, listTransitions );

			for ( Step step : listSteps ){
				List<FormDisplay> listFormDisplay = FormDisplayHome.getFormDisplayListByParent( step.getId(), 0 );
				listFormDisplay.sort( Comparator.comparingInt( FormDisplay::getDisplayOrder ) );

				for ( FormDisplay composite : listFormDisplay )
				{
					if( FormsConstants.MARK_QUESTION.equals( composite.getCompositeType() ) )
					{
						descriptionMarkersList.addAll( buildMarkerForQuestion( QuestionHome.findByPrimaryKey( composite.getCompositeId() ) , null ));
					}
					else if( FormsConstants.MARK_GROUP.equals(composite.getCompositeType()) )
					{
						List<FormDisplay> listFormDisplayGroup = FormDisplayHome.getFormDisplayListByParent( composite.getStepId(), composite.getId() );
						listFormDisplayGroup.sort( Comparator.comparingInt( FormDisplay::getDisplayOrder ) );

						Group group = GroupHome.findByPrimaryKey( composite.getCompositeId() );

						for ( FormDisplay compositeGroup : listFormDisplayGroup)
						{
							if( FormsConstants.MARK_QUESTION.equals(compositeGroup.getCompositeType()) )
							{
								descriptionMarkersList.addAll( buildMarkerForQuestion( QuestionHome.findByPrimaryKey( compositeGroup.getCompositeId() ) , group ));
							}
						}
					}
				}
			}
		}

		InfoMarker markerAdminURl = new InfoMarker( MARK_URL_ADMIN_RESPONSE );
		markerAdminURl.setDescription( MESSAGE_I18N_DESCRIPTION );
		InfoMarker markerFoUrl = new InfoMarker( MARK_URL_FO_RESPONSE );
		markerFoUrl.setDescription( MESSAGE_I18N_FO_DESCRIPTION );
		InfoMarker creationDateMarker = new InfoMarker( MARK_CREATION_DATE );
		creationDateMarker.setDescription( MESSAGE_I18N_CREATION_DATE );
		InfoMarker updateDateMarker = new InfoMarker( MARK_UPDATE_DATE );
		updateDateMarker.setDescription( MESSAGE_I18N_UPDATE_DATE );
		InfoMarker statusMarker = new InfoMarker( MARK_STATUS );
		statusMarker.setDescription( MESSAGE_I18N_STATUS );
		InfoMarker updateStatusDateMarker = new InfoMarker( MARK_STATUS_UPDATE_DATE );
		updateStatusDateMarker.setDescription( MESSAGE_I18N_STATUS_UPDATE_DATE );
		InfoMarker formTitleMarker = new InfoMarker( MARKER_FORM_TITLE );
		formTitleMarker.setDescription( MESSAGE_I18N_FORM );
		InfoMarker markerFoFileUrl = new InfoMarker( MARK_URL_FO_FILES_LINK );
		markerFoFileUrl.setDescription( MESSAGE_I18N_FO_FILE_LINK );

		descriptionMarkersList.add( markerAdminURl );
		descriptionMarkersList.add( markerFoUrl );
		descriptionMarkersList.add( creationDateMarker );
		descriptionMarkersList.add( updateDateMarker );
		descriptionMarkersList.add( statusMarker );
		descriptionMarkersList.add( updateStatusDateMarker );
		descriptionMarkersList.add( formTitleMarker );
		descriptionMarkersList.add( markerFoFileUrl );

		return descriptionMarkersList;
	}

	private static Collection<InfoMarker> buildMarkerForQuestion(Question question, Group group)
	{
		Collection<InfoMarker> descriptionMarkersList = new ArrayList<>( );

		if( group == null || group.getIterationMax() == 1 )
		{
			InfoMarker marker = new InfoMarker( MARK_POSITION + question.getId( ) );
			marker.setDescription( question.getTitle( ) );
			descriptionMarkersList.add(marker);
		}
		else
		{
			for (int i = 0 ; i < group.getIterationMax(); i++ )
			{
				InfoMarker marker = new InfoMarker( MARK_POSITION + question.getId( ) + MARK_POSITION_ITERATION + i );
				marker.setDescription( question.getTitle( ) + " " + ( i + 1 ) );
				descriptionMarkersList.add(marker);
			}
		}
		return descriptionMarkersList;
	}

}
