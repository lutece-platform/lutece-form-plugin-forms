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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlMapping;
import fr.paris.lutece.plugins.forms.business.ControlType;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.Group;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.Transition;
import fr.paris.lutece.plugins.forms.business.export.FormExportConfig;
import fr.paris.lutece.plugins.forms.service.IFormDatabaseService;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.referencelist.business.ReferenceItemHome;
import fr.paris.lutece.portal.service.i18n.I18nService;

public abstract class AbstractFormJsonService
{
    private static final String PROPERTY_COPY_STEP_TITLE = "forms.copyStep.title";

    protected final IFormDatabaseService _formDatabaseService;
    protected ObjectMapper _objectMapper;

    protected AbstractFormJsonService( IFormDatabaseService formDatabaseService )
    {
        _formDatabaseService = formDatabaseService;
        SimpleModule timestampModule = new SimpleModule( "TimestampModule" );
        timestampModule.addSerializer( Timestamp.class, new TimestampSerializer( ) );
        timestampModule.addDeserializer( Timestamp.class, new TimestampDeserializer( ) );

        _objectMapper = new ObjectMapper( ).configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false ).registerModule( timestampModule );
    }

    /**
     * Export the step as a Json Object.
     * 
     * @return
     * @throws JsonProcessingException
     */
    public String jsonExportStep( int idForm, int idStep ) throws JsonProcessingException
    {
        StepJsonData jsonData = jsonExportStepAsData( idForm, idStep );
        return _objectMapper.writeValueAsString( jsonData );
    }

    /**
     * Export the step as a StepJsonData Object.
     * 
     * @return
     * @throws JsonProcessingException
     */
    public StepJsonData jsonExportStepAsData( int idForm, int idStep ) throws JsonProcessingException
    {
        StepJsonData jsonData = new StepJsonData( );

        jsonData.setStep( _formDatabaseService.findStepByPrimaryKey( idStep ) );
        jsonData.setGroupList( _formDatabaseService.getGroupsListByIdStepList( Collections.singletonList( idStep ) ) );

        List<Control> controlList = new ArrayList<>( );
        List<Question> questionList = _formDatabaseService.getQuestionsListByStep( idStep );
        for ( Question question : questionList )
        {
            for ( Field field : question.getEntry( ).getFields( ) )
            {
                Integer idItem = _formDatabaseService.findIdReferenceItemByIdField( field.getIdField( ) );
                if ( idItem > 0 )
                {
                    field.setLinkedItem( ReferenceItemHome.findByPrimaryKey( idItem ) );
                }
            }
            controlList.addAll( _formDatabaseService.getControlByQuestion( question.getId( ) ) );
        }
        List<ControlMapping> controlMappingList = new ArrayList<>( );
        for ( Control control : controlList )
        {
            controlMappingList.addAll( _formDatabaseService.getControlMappingListByIdControl( control.getId( ) ) );
        }
        jsonData.setQuestionList( questionList );
        jsonData.setControlList( controlList );
        jsonData.setControlMappingList( controlMappingList );

        List<FormDisplay> formDisplayList = getAllFormDisplays( idForm, idStep );
        jsonData.setFormDisplayList( formDisplayList.stream( ).filter( fd -> fd.getStepId( ) == idStep ).collect( Collectors.toList( ) ) );

        return jsonData;
    }

    /**
     * Import the step from a Json Object.
     * 
     * @return
     */
    @Transactional( FormsConstants.BEAN_TRANSACTION_MANAGER )
    public void jsonImportStep( int idForm, String json, Locale locale ) throws JsonProcessingException
    {
        StepJsonData jsonData = _objectMapper.readValue( json, StepJsonData.class );

        jsonImportStep( idForm, jsonData, locale );
    }

    @Transactional( FormsConstants.BEAN_TRANSACTION_MANAGER )
    public void jsonImportStep( int idForm, StepJsonData jsonData, Locale locale )
    {
        Step step = jsonData.getStep( );
        List<Group> groupList = jsonData.getGroupList( );
        List<Question> questionList = jsonData.getQuestionList( );
        List<Control> controlList = jsonData.getControlList( );
        List<ControlMapping> controlMappingList = jsonData.getControlMappingList( );
        List<FormDisplay> formDisplayList = jsonData.getFormDisplayList( );

        Object [ ] tabSTepTitleCopy = {
                step.getTitle( ),
        };
        String strTitleCopyStep = I18nService.getLocalizedString( PROPERTY_COPY_STEP_TITLE, tabSTepTitleCopy, locale );

        if ( strTitleCopyStep != null )
        {
            step.setTitle( strTitleCopyStep );
        }

        importSteps( idForm, Collections.singletonList( step ), groupList, null, questionList, formDisplayList );
        importQuestions( idForm, questionList, controlList, controlMappingList, formDisplayList, null );
        importGroups( groupList, formDisplayList );
        importFormDisplay( idForm, formDisplayList, controlList );
        importControls( controlList, controlMappingList );
    }

    protected void importSteps( int newIdForm, List<Step> stepList, List<Group> groupList, List<Transition> transitionList, List<Question> questionList,
            List<FormDisplay> formDisplayList )
    {
        Map<Integer, Integer> mapIdSteps = new HashMap<>( );
        for ( Step step : stepList )
        {
            int oldStepId = step.getId( );
            step.setIdForm( newIdForm );

            _formDatabaseService.createStep( step );
            int newStepId = step.getId( );

            mapIdSteps.put( oldStepId, newStepId );
        }

        if ( CollectionUtils.isNotEmpty( transitionList ) )
        {
            updateTransitionWithNewStep( transitionList, mapIdSteps );
        }
        updateGroupWithNewStep( groupList, mapIdSteps );
        updateQuestionWithNewStep( questionList, mapIdSteps );
        updateFormDisplayWithNewStep( formDisplayList, mapIdSteps );
    }

    protected void importQuestions( int newIdForm, List<Question> questionList, List<Control> controlList, List<ControlMapping> controlMappingList,
            List<FormDisplay> formDisplayList, List<FormExportConfig> formExportConfigList )
    {
        Map<Integer, Integer> mapIdQuestions = new HashMap<>( );
        for ( Question question : questionList )
        {
            int oldIdQuestion = question.getId( );

            Entry entry = question.getEntry( );
            entry.setIdResource( newIdForm );
            entry.setResourceType( Form.RESOURCE_TYPE );
            _formDatabaseService.createEntry( entry );
            question.setIdEntry( entry.getIdEntry( ) );

            List<Field> fieldList = entry.getFields( );
            for ( Field field : fieldList )
            {
                field.setParentEntry( entry );
                _formDatabaseService.createField( field );
            }

            _formDatabaseService.createQuestion( question );

            int newIdQuestion = question.getId( );

            mapIdQuestions.put( oldIdQuestion, newIdQuestion );
        }
        updateControlWithNewQuestion( controlMappingList, controlList, mapIdQuestions );
        updateFormDisplayWithNewQuestion( formDisplayList, mapIdQuestions );
        if ( CollectionUtils.isNotEmpty( formExportConfigList ) )
        {
            updateExportConfigWithNewQuestion( formExportConfigList, mapIdQuestions );
        }
    }

    protected void importGroups( List<Group> groupList, List<FormDisplay> formDisplayList )
    {
        Map<Integer, Integer> mapIdGroups = new HashMap<>( );
        for ( Group group : groupList )
        {
            int oldId = group.getId( );

            _formDatabaseService.createGroup( group );

            int newId = group.getId( );

            mapIdGroups.put( oldId, newId );
        }
        updateFormDisplayWithNewGroup( formDisplayList, mapIdGroups );
    }

    protected void importFormDisplay( int newIdForm, List<FormDisplay> formDisplayList, List<Control> controlList )
    {
        Map<Integer, Integer> mapIdFormDisplay = new HashMap<>( );
        for ( FormDisplay formDisplay : formDisplayList )
        {
            int oldId = formDisplay.getId( );
            formDisplay.setFormId( newIdForm );
            _formDatabaseService.createFormDisplay( formDisplay );

            int newId = formDisplay.getId( );

            mapIdFormDisplay.put( oldId, newId );
        }
        for ( FormDisplay formDisplay : formDisplayList )
        {
            if ( formDisplay.getParentId( ) > 0 )
            {
                formDisplay.setParentId( mapIdFormDisplay.get( formDisplay.getParentId( ) ) );
            }
            // Update id parent and display order
            _formDatabaseService.updateFormDisplay( formDisplay );
        }
        updateControlWithFormDisplay( controlList, mapIdFormDisplay );
    }

    protected void importControls( List<Control> controlList, List<ControlMapping> controlMappingList )
    {
        Map<Integer, Integer> mapIdControls = new HashMap<>( );
        for ( Control control : controlList )
        {
            int oldId = control.getId( );
            _formDatabaseService.createControl( control );
            int newId = control.getId( );

            mapIdControls.put( oldId, newId );
        }

        for ( ControlMapping controlMapping : controlMappingList )
        {
            _formDatabaseService.createMappingControl( mapIdControls.get( controlMapping.getIdControl( ) ), controlMapping.getIdQuestion( ),
                    controlMapping.getValue( ) );
        }
    }

    private void updateGroupWithNewStep( List<Group> groupList, Map<Integer, Integer> mapIdSteps )
    {
        for ( Group group : groupList )
        {
            group.setIdStep( mapIdSteps.get( group.getIdStep( ) ) );
        }
    }

    private void updateQuestionWithNewStep( List<Question> questionList, Map<Integer, Integer> mapIdSteps )
    {
        for ( Question question : questionList )
        {
            question.setIdStep( mapIdSteps.get( question.getIdStep( ) ) );
        }
    }

    private void updateFormDisplayWithNewStep( List<FormDisplay> formDisplayList, Map<Integer, Integer> mapIdSteps )
    {
        for ( FormDisplay formDisplay : formDisplayList )
        {
            formDisplay.setStepId( mapIdSteps.get( formDisplay.getStepId( ) ) );
        }
    }

    private void updateExportConfigWithNewQuestion( List<FormExportConfig> formExportConfigList, Map<Integer, Integer> mapIdQuestions )
    {
        for ( FormExportConfig config : formExportConfigList )
        {
            int idQuestion = NumberUtils.toInt( config.getField( ), -1 );
            if ( idQuestion != -1 )
            {
                config.setField( String.valueOf( mapIdQuestions.get( idQuestion ) ) );
            }
        }
    }

    private void updateControlWithNewQuestion( List<ControlMapping> controlMappingList, List<Control> controlList, Map<Integer, Integer> mapIdQuestions )
    {
        for ( Control control : controlList )
        {
            Set<Integer> newSetId = new HashSet<>( );
            Set<Integer> oldSetId = control.getListIdQuestion( );

            for ( Integer oldId : oldSetId )
            {
                newSetId.add( mapIdQuestions.get( oldId ) );
            }
            control.setListIdQuestion( newSetId );

            if ( ControlType.VALIDATION.getLabel( ).equals( control.getControlType( ) ) )
            {
                control.setIdControlTarget( mapIdQuestions.get( control.getIdControlTarget( ) ) );
            }
        }
        for ( ControlMapping controlMapping : controlMappingList )
        {
            controlMapping.setIdQuestion( mapIdQuestions.get( controlMapping.getIdQuestion( ) ) );
        }
    }

    private void updateFormDisplayWithNewQuestion( List<FormDisplay> formDisplayList, Map<Integer, Integer> mapIdQuestions )
    {
        for ( FormDisplay formDisplay : formDisplayList )
        {
            if ( CompositeDisplayType.QUESTION.getLabel( ).equals( formDisplay.getCompositeType( ) ) )
            {
                formDisplay.setCompositeId( mapIdQuestions.get( formDisplay.getCompositeId( ) ) );
            }
        }
    }

    private void updateFormDisplayWithNewGroup( List<FormDisplay> formDisplayList, Map<Integer, Integer> mapIdGroup )
    {
        for ( FormDisplay formDisplay : formDisplayList )
        {
            if ( CompositeDisplayType.GROUP.getLabel( ).equals( formDisplay.getCompositeType( ) ) )
            {
                formDisplay.setCompositeId( mapIdGroup.get( formDisplay.getCompositeId( ) ) );
            }
        }
    }

    private void updateControlWithFormDisplay( List<Control> controlList, Map<Integer, Integer> mapIdFormDisplay )
    {
        for ( Control control : controlList )
        {
            if ( ControlType.CONDITIONAL.getLabel( ).equals( control.getControlType( ) ) && mapIdFormDisplay.containsKey( control.getIdControlTarget( ) ) )
            {
                control.setIdControlTarget( mapIdFormDisplay.get( control.getIdControlTarget( ) ) );
            }
        }
    }

    private void updateTransitionWithNewStep( List<Transition> transitionList, Map<Integer, Integer> mapIdSteps )
    {
        for ( Transition transition : transitionList )
        {
            transition.setFromStep( mapIdSteps.get( transition.getFromStep( ) ) );
            transition.setNextStep( mapIdSteps.get( transition.getNextStep( ) ) );
        }
    }

    protected abstract List<FormDisplay> getAllFormDisplays( int idForm, int idStep );
}
