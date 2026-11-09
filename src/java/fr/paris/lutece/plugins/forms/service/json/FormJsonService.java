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
package fr.paris.lutece.plugins.forms.service.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import fr.paris.lutece.plugins.forms.business.ControlGroup;
import fr.paris.lutece.plugins.forms.business.ControlGroupHome;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.ControlMapping;
import fr.paris.lutece.plugins.forms.business.ControlType;
import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormCategory;
import fr.paris.lutece.plugins.forms.business.FormCategoryHome;
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
import fr.paris.lutece.plugins.forms.service.FormsPlugin;
import fr.paris.lutece.plugins.forms.service.cache.FormsCacheService;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.ReferenceItemFieldHome;
import fr.paris.lutece.plugins.referencelist.business.ReferenceItemHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.util.sql.TransactionManager;

/**
 * Json service to handle import/export
 */
public class FormJsonService extends AbstractFormJsonService
{
    private static final String PROPERTY_COPY_FORM_TITLE = "forms.copyForm.title";
    private static final String MESSAGE_IMPORT_WORKFLOW_LOST = "forms.import.report.workflowLost";
    private static final String MESSAGE_IMPORT_AVAILABILITY_LOST = "forms.import.report.availabilityLost";
    private static final String MESSAGE_IMPORT_LOGO_LOST = "forms.import.report.logoLost";
    /** {0} the name of the category the form was in */
    private static final String MESSAGE_IMPORT_CATEGORY_LOST = "forms.import.report.categoryLost";

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
        Form form = FormHome.findByPrimaryKey( idForm );
        jsonData.setForm( form );
        jsonData.setCategory( form.getIdCategory( ) > 0 ? FormCategoryHome.findByPrimaryKey( form.getIdCategory( ) ) : null );
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
            addControlsOnce( controlList, ControlHome.getControlByQuestion( question.getId( ) ) );
        }
        List<ControlMapping> controlMappingList = new ArrayList<>( );
        for ( Control control : controlList )
        {
            controlMappingList.addAll( ControlHome.getControlMappingListByIdControl( control.getId( ) ) );
        }

        Set<Integer> listControlGroup = controlList.stream().map( Control::getIdControlGroup ).collect( Collectors.toSet() );
        List<ControlGroup> controlGroupList = ControlGroupHome.getControlGroupsListByIds( new ArrayList<>(listControlGroup) );

        jsonData.setQuestionList( questionList );
        jsonData.setControlList( controlList );
        jsonData.setControlGroupList( controlGroupList );
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
    public List<String> jsonImportForm( String json, Locale locale ) throws JsonProcessingException
    {
        FormJsonData jsonData = _objectMapper.readValue( json, FormJsonData.class );

        List<FormExportConfig> formExportConfigList = jsonData.getFormExportConfigList( );
        List<Step> stepList = jsonData.getStepList( );
        List<Group> groupList = jsonData.getGroupList( );
        List<Transition> transitionList = jsonData.getTransitionList( );
        List<Question> questionList = jsonData.getQuestionList( );
        List<FormDisplay> formDisplayList = jsonData.getFormDisplayList( );
        List<Control> controlList = jsonData.getControlList( );
        List<ControlMapping> controlMappingList = jsonData.getControlMappingList( );
        List<ControlGroup> controlGroupList = jsonData.getControlGroupList();

        // what the import cannot carry, gathered as it runs and shown to whoever asked for it
        List<String> listReport = new ArrayList<>( );

        TransactionManager.beginTransaction( FormsPlugin.getPlugin( ) );

        try
        {
            int newIdForm = importForm( jsonData.getForm( ), jsonData.getFormMessage( ), jsonData.getCategory( ), locale, listReport );

            importSteps( newIdForm, stepList, groupList, transitionList, questionList, formDisplayList );
            importQuestions( newIdForm, questionList, controlList, controlMappingList, formDisplayList, formExportConfigList, locale, listReport );
            importGroups( groupList, formDisplayList );
            importFormDisplay( newIdForm, formDisplayList, controlList );
            importTransitions( transitionList, controlList );
            importControls( controlList, controlMappingList, controlGroupList );
            importFormExportConfig( newIdForm, formExportConfigList );

            TransactionManager.commitTransaction( FormsPlugin.getPlugin( ) );

            return listReport;
        }
        catch( Exception e )
        {
            TransactionManager.rollBack( FormsPlugin.getPlugin( ) );
            // the caches were repopulated while the transaction was running : they now describe rows the rollback has undone
            SpringContextService.<FormsCacheService> getBean( FormsCacheService.BEAN_NAME ).resetCache( );
            throw new AppException( "Forms : the import of the form failed", e );
        }
    }

    private int importForm( Form form, FormMessage formMessage, FormCategory category, Locale locale, List<String> listReport )
    {
        Object [ ] tabFormTitleCopy = {
                form.getTitle( ),
        };
        String strTitleCopyForm = I18nService.getLocalizedString( PROPERTY_COPY_FORM_TITLE, tabFormTitleCopy, locale );
        if ( strTitleCopyForm != null )
        {
            form.setTitle( strTitleCopyForm );
        }
        // these have always been emptied, the import holding nothing that would let it find them again here. What was missing was saying so :
        // whoever imports a form had no way of knowing what was left for them to set
        if ( form.getIdWorkflow( ) > 0 )
        {
            report( listReport, MESSAGE_IMPORT_WORKFLOW_LOST, locale );
        }
        if ( form.getAvailabilityStartDate( ) != null || form.getAvailabilityEndDate( ) != null )
        {
            report( listReport, MESSAGE_IMPORT_AVAILABILITY_LOST, locale );
        }
        if ( form.getLogo( ) != null )
        {
            report( listReport, MESSAGE_IMPORT_LOGO_LOST, locale );
        }

        form.setIdWorkflow( 0 );
        form.setAvailabilityStartDate( null );
        form.setAvailabilityEndDate( null );
        form.setLogo( null );
        form.setIdCategory( categoryOfThisInstance( category, locale, listReport ) );
        FormHome.create( form );

        int newIdForm = form.getId( );

        formMessage.setIdForm( newIdForm );
        FormMessageHome.create( formMessage );

        return newIdForm;
    }

    /**
     * The identifier, in this instance, of the category the imported form was in.
     *
     * A category is carried whole rather than by its identifier, because a number designates another category, or none, in the instance the form
     * is imported into. Its code is the same wherever that category was declared, so the category of this instance bearing that code is the one
     * meant. None bears it, and the form is imported without a category rather than into one chosen by accident.
     *
     * @param category
     *            the category the exported form was in, null when it was in none
     * @param locale
     *            the administrator's locale
     * @param listReport
     *            what the import gathers as it runs
     * @return the identifier of the category here, 0 for none
     */
    private int categoryOfThisInstance( FormCategory category, Locale locale, List<String> listReport )
    {
        if ( category == null )
        {
            return 0;
        }

        for ( FormCategory categoryHere : FormCategoryHome.getFormCategoryList( ) )
        {
            if ( StringUtils.equals( categoryHere.getCode( ), category.getCode( ) ) )
            {
                return categoryHere.getId( );
            }
        }

        report( listReport, MESSAGE_IMPORT_CATEGORY_LOST, locale, category.getName( ) );

        return 0;
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
        controlList.removeIf( control -> ControlType.TRANSITION.getLabel( ).equals( control.getControlType( ) )
                && isUnresolvedReference( mapIdTransitions, control.getIdControlTarget( ), "control " + control.getId( ), "target transition" ) );

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
