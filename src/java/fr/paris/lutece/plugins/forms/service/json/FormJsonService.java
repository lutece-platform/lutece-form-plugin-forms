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
package fr.paris.lutece.plugins.forms.service.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.ControlMapping;
import fr.paris.lutece.plugins.forms.business.ControlType;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormMessage;
import fr.paris.lutece.plugins.forms.business.FormMessageHome;
import fr.paris.lutece.plugins.forms.business.Group;
import fr.paris.lutece.plugins.forms.business.GroupHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.business.Transition;
import fr.paris.lutece.plugins.forms.business.TransitionHome;
import fr.paris.lutece.plugins.forms.business.export.FormExportConfig;
import fr.paris.lutece.plugins.forms.business.export.FormExportConfigHome;
import fr.paris.lutece.plugins.forms.service.FormDatabaseService;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.ReferenceItemFieldHome;
import fr.paris.lutece.plugins.referencelist.business.ReferenceItemHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * Json service to handle import/export
 */
public class FormJsonService extends AbstractFormJsonService
{
    private static final String PROPERTY_COPY_FORM_TITLE = "forms.copyForm.title";

    public static final FormJsonService INSTANCE = new FormJsonService( );

    private FormJsonService( )
    {
        super( SpringContextService.getBean( FormDatabaseService.BEAN_NAME ) );
    }

    public static FormJsonService getInstance( )
    {
        return INSTANCE;
    }

    /**
     * Export the form as a Json Object.
     * 
     * @return
     * @throws JsonProcessingException
     */
    public String jsonExportForm( int idForm ) throws JsonProcessingException
    {
        FormJsonData jsonData = new FormJsonData( );
        jsonData.setForm( FormHome.findByPrimaryKey( idForm ) );
        jsonData.getForm( ).setLogo( null );

        List<Step> stepList = StepHome.getStepsListByForm( idForm );
        jsonData.setStepList( stepList );
        jsonData.setGroupList( GroupHome.getGroupsListByIdStepList( stepList.stream( ).map( Step::getId ).collect( Collectors.toList( ) ) ) );

        List<Control> controlList = new ArrayList<>( );
        List<Question> questionList = QuestionHome.getListQuestionByIdForm( idForm );
        for ( Question question : questionList )
        {
            for ( Field field : question.getEntry( ).getFields( ) )
            {
                Integer idItem = ReferenceItemFieldHome.findIdItemByIdField( field.getIdField( ) );
                if ( idItem > 0 )
                {
                    field.setLinkedItem( ReferenceItemHome.findByPrimaryKey( idItem ) );
                }
            }
            controlList.addAll( ControlHome.getControlByQuestion( question.getId( ) ) );
        }
        List<ControlMapping> controlMappingList = new ArrayList<>( );
        for ( Control control : controlList )
        {
            controlMappingList.addAll( ControlHome.getControlMappingListByIdControl( control.getId( ) ) );
        }
        jsonData.setQuestionList( questionList );
        jsonData.setControlList( controlList );

        jsonData.setControlMappingList( controlMappingList );

        jsonData.setTransitionList( TransitionHome.getTransitionsListFromForm( idForm ) );
        jsonData.setFormMessage( FormMessageHome.findByForm( idForm ) );
        jsonData.setFormDisplayList( FormDisplayHome.getFormDisplayByForm( idForm ) );

        jsonData.setFormExportConfigList( FormExportConfigHome.findByForm( idForm ) );

        return _objectMapper.writeValueAsString( jsonData );
    }

    /**
     * Import the form from a Json Object.
     * 
     * @return
     */
    @Transactional( FormsConstants.BEAN_TRANSACTION_MANAGER )
    public void jsonImportForm( String json, Locale locale ) throws JsonProcessingException
    {
        FormJsonData jsonData = _objectMapper.readValue( json, FormJsonData.class );

        int newIdForm = importForm( jsonData.getForm( ), jsonData.getFormMessage( ), locale );

        List<FormExportConfig> formExportConfigList = jsonData.getFormExportConfigList( );
        List<Step> stepList = jsonData.getStepList( );
        List<Group> groupList = jsonData.getGroupList( );
        List<Transition> transitionList = jsonData.getTransitionList( );
        List<Question> questionList = jsonData.getQuestionList( );
        List<FormDisplay> formDisplayList = jsonData.getFormDisplayList( );
        List<Control> controlList = jsonData.getControlList( );
        List<ControlMapping> controlMappingList = jsonData.getControlMappingList( );

        importSteps( newIdForm, stepList, groupList, transitionList, questionList, formDisplayList );
        importQuestions( newIdForm, questionList, controlList, controlMappingList, formDisplayList, formExportConfigList );
        importGroups( groupList, formDisplayList );
        importFormDisplay( newIdForm, formDisplayList, controlList );
        importTransitions( transitionList, controlList );
        importControls( controlList, controlMappingList );
        importFormExportConfig( newIdForm, formExportConfigList );
    }

    private int importForm( Form form, FormMessage formMessage, Locale locale )
    {
        Object [ ] tabFormTitleCopy = {
                form.getTitle( ),
        };
        String strTitleCopyForm = I18nService.getLocalizedString( PROPERTY_COPY_FORM_TITLE, tabFormTitleCopy, locale );
        if ( strTitleCopyForm != null )
        {
            form.setTitle( strTitleCopyForm );
        }
        form.setIdWorkflow( 0 );
        form.setAvailabilityStartDate( null );
        form.setAvailabilityEndDate( null );
        form.setLogo( null );
        FormHome.create( form );

        int newIdForm = form.getId( );

        formMessage.setIdForm( newIdForm );
        FormMessageHome.create( formMessage );

        return newIdForm;
    }

    private void importFormExportConfig( int newIdForm, List<FormExportConfig> formExportConfigList )
    {
        for ( FormExportConfig config : formExportConfigList )
        {
            config.setIdForm( newIdForm );
            FormExportConfigHome.create( config );
        }
    }

    private void importTransitions( List<Transition> transitionList, List<Control> controlList )
    {
        Map<Integer, Integer> mapIdTransitions = new HashMap<>( );
        for ( Transition transition : transitionList )
        {
            int oldId = transition.getId( );
            TransitionHome.createWithoutPriorityCalculation( transition );

            int newId = transition.getId( );

            mapIdTransitions.put( oldId, newId );
        }
        updateControlWithNewTransition( controlList, mapIdTransitions );
    }

    private void updateControlWithNewTransition( List<Control> controlList, Map<Integer, Integer> mapIdTransitions )
    {
        for ( Control control : controlList )
        {
            if ( ControlType.TRANSITION.getLabel( ).equals( control.getControlType( ) ) )
            {
                control.setIdControlTarget( mapIdTransitions.get( control.getIdControlTarget( ) ) );
            }
        }
    }

    @Override
    protected List<FormDisplay> getAllFormDisplays( int idForm, int idStep )
    {
        return FormDisplayHome.getFormDisplayByForm( idForm );
    }
}
