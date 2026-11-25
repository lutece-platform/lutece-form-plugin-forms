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
package fr.paris.lutece.plugins.forms.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.ControlType;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormResponseStepHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.business.Transition;
import fr.paris.lutece.plugins.forms.business.TransitionHome;
import fr.paris.lutece.plugins.forms.service.cache.FormsCacheService;
import fr.paris.lutece.plugins.forms.service.json.FormJsonService;
import fr.paris.lutece.plugins.forms.service.json.StepJsonData;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.util.sql.TransactionManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.inject.Inject;

/**
 * Business operations of the form builder: moving, placing, duplicating and removing the displays of a form, and duplicating or removing its steps.
 * Every check made by the historical screens (ManageQuestions, ManageSteps) is applied here as well.
 */
@ApplicationScoped
public class FormBuilderService
{
    public static final String ERROR_DISPLAY_NOT_FOUND = "forms.form_builder.message.error.displayNotFound";
    public static final String ERROR_STEP_NOT_FOUND = "forms.form_builder.message.error.stepNotFound";
    public static final String ERROR_INVALID_TARGET = "forms.form_builder.message.error.invalidTarget";
    public static final String ERROR_GROUP_INTO_ITSELF = "forms.form_builder.message.error.groupIntoItself";
    public static final String ERROR_HAS_RESPONSES = "forms.form_builder.message.error.hasResponses";
    public static final String ERROR_CONDITION_DEPENDENCY = "forms.form_builder.message.error.conditionDependency";
    public static final String ERROR_CROSS_STEP_CONDITION = "forms.form_builder.message.error.crossStepCondition";
    public static final String ERROR_ONLY_QUESTION_DUPLICABLE = "forms.form_builder.message.error.onlyQuestionDuplicable";
    public static final String ERROR_REMOVE_INITIAL_STEP = "forms.error.step.remove.initial";
    public static final String ERROR_REMOVE_LAST_FINAL_STEP = "forms.error.step.remove.lastFinal";
    public static final String ERROR_STEP_NOT_COPIED = "forms.error.step.not.copied";
    public static final String ERROR_STEP_NOT_IMPORTED = "forms.error.step.not.imported";

    private static final String PROPERTY_COPY_PREFIX = "forms.form_builder.copyOfPrefix";
    private static final String CODE_PREFIX_QUESTION = "question_";

    @Inject
    private FormDisplayService _formDisplayService;

    @Inject
    private FormDatabaseService _formDatabaseService;

    @Inject
    private FormService _formService;

    @Inject
    private StepService _stepService;

    @Inject
    private FormJsonService _formJsonService;

    @Inject
    private FormsCacheService _formsCacheService;

    /**
     * Moves a display (and its descendants) to the given position of the given parent in the given step. The positions of the target siblings and, when
     * the parent changes, of the origin siblings are renumbered from 1.
     *
     * @param nIdForm
     *            the form the display must belong to
     * @param nIdDisplay
     *            the identifier of the display to move
     * @param nIdStepTarget
     *            the target step
     * @param nIdParentTarget
     *            the target parent display, 0 for the root of the step
     * @param nPosition
     *            the target position within the parent, starting at 1, clamped to the available positions
     * @throws FormBuilderException
     *             if the display, the step or the parent is not valid for this form, or if a move to another step would break a condition
     */
    public void moveDisplay( int nIdForm, int nIdDisplay, int nIdStepTarget, int nIdParentTarget, int nPosition ) throws FormBuilderException
    {
        moveDisplay( nIdForm, nIdDisplay, nIdStepTarget, nIdParentTarget, getDisplayOfForm( nIdForm, nIdDisplay ).getDisplayColumn( ), nPosition );
    }

    /**
     * Moves a display (and its descendants) to the given position of the given column of the given parent in the given step. The siblings are ordered
     * column after column and their positions renumbered from 1, in the target parent and, when the parent changes, in the origin parent.
     *
     * @param nIdForm
     *            the form the display must belong to
     * @param nIdDisplay
     *            the identifier of the display to move
     * @param nIdStepTarget
     *            the target step
     * @param nIdParentTarget
     *            the target parent display, 0 for the root of the step
     * @param nColumn
     *            the target column, starting at 1; a column the layout of the target parent does not have is read as its last column
     * @param nPosition
     *            the target position within the column, starting at 1, clamped to the available positions
     * @throws FormBuilderException
     *             if the display, the step or the parent is not valid for this form, or if a move to another step would break a condition
     */
    public void moveDisplay( int nIdForm, int nIdDisplay, int nIdStepTarget, int nIdParentTarget, int nColumn, int nPosition ) throws FormBuilderException
    {
        FormDisplay display = getDisplayOfForm( nIdForm, nIdDisplay );
        getStepOfForm( nIdForm, nIdStepTarget );
        checkTargetParent( nIdForm, display, nIdStepTarget, nIdParentTarget );
        if ( display.getStepId( ) != nIdStepTarget )
        {
            checkNoConditionLeavesStep( display );
        }

        TransactionManager.beginTransaction( FormsPlugin.getPlugin( ) );
        try
        {
            moveDisplay( display, nIdStepTarget, nIdParentTarget, nColumn, nPosition );
            TransactionManager.commitTransaction( FormsPlugin.getPlugin( ) );
        }
        catch( RuntimeException e )
        {
            TransactionManager.rollBack( FormsPlugin.getPlugin( ) );
            _formsCacheService.resetCache( );
            throw e;
        }
    }

    /**
     * Moves a display without any check nor transaction. Used by the historical move screen, which validates its input itself.
     *
     * @param display
     *            the display to move
     * @param nIdStepTarget
     *            the target step
     * @param nIdParentTarget
     *            the target parent display, 0 for the root of the step
     * @param nColumn
     *            the target column, starting at 1
     * @param nPosition
     *            the target position within the column, starting at 1, clamped to the available positions
     */
    public void moveDisplay( FormDisplay display, int nIdStepTarget, int nIdParentTarget, int nColumn, int nPosition )
    {
        moveDisplay( display, nIdStepTarget, nIdParentTarget, nColumn, nPosition, _formDatabaseService, _formDisplayService );
    }

    /**
     * Moves a display without any check nor transaction, reading and writing through the given services. Used by the historical move screen, whose
     * bean chooses its services.
     *
     * @param display
     *            the display to move
     * @param nIdStepTarget
     *            the target step
     * @param nIdParentTarget
     *            the target parent display, 0 for the root of the step
     * @param nColumn
     *            the target column, starting at 1; a column the layout of the target parent does not have is read as its last column
     * @param nPosition
     *            the target position within the column, starting at 1, clamped to the available positions
     * @param formDatabaseService
     *            the service reading the displays
     * @param formDisplayService
     *            the service updating the displays
     */
    public void moveDisplay( FormDisplay display, int nIdStepTarget, int nIdParentTarget, int nColumn, int nPosition, IFormDatabaseService formDatabaseService,
            IFormDisplayService formDisplayService )
    {
        int nIdOriginStep = display.getStepId( );
        int nIdOriginParent = display.getParentId( );

        List<FormDisplay> listTarget = new ArrayList<>( formDatabaseService.getFormDisplayListByParent( nIdStepTarget, nIdParentTarget ) );
        listTarget.removeIf( sibling -> sibling.getId( ) == display.getId( ) );

        display.setParentId( nIdParentTarget );
        formDisplayService.setChildrenDisplayDepthAndStep( display, formDisplayService.getDisplayDepthFromParent( nIdParentTarget ), nIdStepTarget );

        int nLayout = formDisplayService.getContainerLayout( nIdStepTarget, nIdParentTarget );
        formDisplayService.rebuildDisplayPositionSequence( formDisplayService.placeInColumn( listTarget, display, nColumn, nPosition, nLayout ) );

        if ( nIdOriginParent != nIdParentTarget || nIdOriginStep != nIdStepTarget )
        {
            List<FormDisplay> listOrigin = new ArrayList<>( formDatabaseService.getFormDisplayListByParent( nIdOriginStep, nIdOriginParent ) );
            listOrigin.removeIf( sibling -> sibling.getId( ) == display.getId( ) );
            formDisplayService.rebuildDisplayPositionSequence( listOrigin );
        }
    }

    /**
     * Moves the display just created by the creation screen opened from the palette to the position where its entry type was dropped. The display is
     * the one of the given question or group (the one the creation screen of this user's session just created) in the given parent, created after the
     * known highest identifier: a display created meanwhile by another user or tab is never taken.
     *
     * @param nIdForm
     *            the form
     * @param nIdStep
     *            the step where the display was created
     * @param nIdParent
     *            the parent where the display was created, 0 for the root of the step
     * @param nKnownMaxIdDisplay
     *            the highest display identifier of the form when the entry type was dropped
     * @param nColumn
     *            the column where the entry type was dropped, starting at 1
     * @param nPosition
     *            the position within the column where the entry type was dropped, starting at 1
     * @param nIdQuestion
     *            the question created by the creation screen, 0 if none
     * @param nIdGroup
     *            the group created by the creation screen, 0 if none
     * @return the identifier of the placed display, or 0 when no such display exists (creation cancelled)
     * @throws FormBuilderException
     *             if the step or the parent is not valid for this form
     */
    public int placeNewDisplay( int nIdForm, int nIdStep, int nIdParent, int nKnownMaxIdDisplay, int nColumn, int nPosition, int nIdQuestion, int nIdGroup )
            throws FormBuilderException
    {
        getStepOfForm( nIdForm, nIdStep );

        FormDisplay displayCreated = null;
        for ( FormDisplay display : _formDatabaseService.getFormDisplayListByParent( nIdStep, nIdParent ) )
        {
            boolean bCreatedByUser = ( CompositeDisplayType.QUESTION.getLabel( ).equalsIgnoreCase( display.getCompositeType( ) )
                    && display.getCompositeId( ) == nIdQuestion )
                    || ( CompositeDisplayType.GROUP.getLabel( ).equalsIgnoreCase( display.getCompositeType( ) ) && display.getCompositeId( ) == nIdGroup );
            if ( display.getId( ) > nKnownMaxIdDisplay && bCreatedByUser )
            {
                displayCreated = display;
            }
        }

        if ( displayCreated == null )
        {
            return 0;
        }

        moveDisplay( nIdForm, displayCreated.getId( ), nIdStep, nIdParent, nColumn, nPosition );
        return displayCreated.getId( );
    }

    /**
     * Returns the highest display identifier of a form
     *
     * @param nIdForm
     *            the form
     * @return the highest display identifier, 0 if the form has no display
     */
    public int getMaxIdDisplay( int nIdForm )
    {
        return FormDisplayHome.getMaxIdDisplayByForm( nIdForm );
    }

    /**
     * Duplicates a question (entry, fields and validation controls) right after the original one
     *
     * @param nIdForm
     *            the form
     * @param nIdDisplay
     *            the display of the question to duplicate
     * @param locale
     *            the locale used for the title prefix of the copy
     * @return the identifier of the display of the copy
     * @throws FormBuilderException
     *             if the display is not a question of this form
     */
    public int duplicateQuestion( int nIdForm, int nIdDisplay, Locale locale ) throws FormBuilderException
    {
        FormDisplay displayOrigin = getDisplayOfForm( nIdForm, nIdDisplay );
        if ( !CompositeDisplayType.QUESTION.getLabel( ).equalsIgnoreCase( displayOrigin.getCompositeType( ) ) )
        {
            throw new FormBuilderException( ERROR_ONLY_QUESTION_DUPLICABLE );
        }

        String strCopyPrefix = I18nService.getLocalizedString( PROPERTY_COPY_PREFIX, locale );

        TransactionManager.beginTransaction( FormsPlugin.getPlugin( ) );
        try
        {
            Question question = _formDatabaseService.findQuestionByPrimaryKey( displayOrigin.getCompositeId( ) );
            Question questionCopy = new Question( question );

            Entry entryCopy = _formDatabaseService.copyEntry( question.getEntry( ) );
            entryCopy.setTitle( strCopyPrefix + entryCopy.getTitle( ) );
            entryCopy.setCode( CODE_PREFIX_QUESTION + entryCopy.getIdEntry( ) );
            _formDatabaseService.updateEntry( entryCopy );

            questionCopy.setEntry( entryCopy );
            questionCopy.setIdEntry( entryCopy.getIdEntry( ) );
            questionCopy.setTitle( strCopyPrefix + question.getTitle( ) );
            questionCopy.setCode( entryCopy.getCode( ) );
            _formDatabaseService.createQuestion( questionCopy );

            for ( Control control : _formDatabaseService.getControlByQuestion( question.getId( ) ) )
            {
                if ( ControlType.VALIDATION.getLabel( ).equals( control.getControlType( ) ) )
                {
                    Set<Integer> setQuestion = new HashSet<>( );
                    setQuestion.add( questionCopy.getId( ) );
                    control.setListIdQuestion( setQuestion );
                    control.setIdControlTarget( questionCopy.getId( ) );
                    _formDatabaseService.createControl( control );
                }
            }

            FormDisplay displayCopy = new FormDisplay( );
            displayCopy.setFormId( nIdForm );
            displayCopy.setStepId( displayOrigin.getStepId( ) );
            displayCopy.setParentId( displayOrigin.getParentId( ) );
            displayCopy.setCompositeId( questionCopy.getId( ) );
            displayCopy.setCompositeType( CompositeDisplayType.QUESTION.getLabel( ) );
            displayCopy.setDepth( displayOrigin.getDepth( ) );
            _formDatabaseService.createFormDisplay( displayCopy );

            moveDisplay( _formDatabaseService.findDisplayByPrimaryKey( displayCopy.getId( ) ), displayOrigin.getStepId( ), displayOrigin.getParentId( ),
                    displayOrigin.getDisplayColumn( ), _formDisplayService.getPositionInColumn( displayOrigin ) + 1 );

            TransactionManager.commitTransaction( FormsPlugin.getPlugin( ) );
            return displayCopy.getId( );
        }
        catch( RuntimeException e )
        {
            TransactionManager.rollBack( FormsPlugin.getPlugin( ) );
            _formsCacheService.resetCache( );
            throw e;
        }
    }

    /**
     * Removes a display and its descendants, then renumbers its siblings, in one transaction. Refused when responses exist, or when a conditional display
     * uses the question or, for a group, any question inside it.
     *
     * @param nIdForm
     *            the form
     * @param nIdDisplay
     *            the display to remove
     * @throws FormBuilderException
     *             if the display does not belong to the form or cannot be removed
     */
    public void removeDisplay( int nIdForm, int nIdDisplay ) throws FormBuilderException
    {
        FormDisplay display = getDisplayOfForm( nIdForm, nIdDisplay );

        if ( _formService.existCompositeResponses( display ) )
        {
            throw new FormBuilderException( ERROR_HAS_RESPONSES );
        }

        List<FormDisplay> listRemoved = getSubtree( display );
        Set<Integer> setRemovedDisplayIds = new HashSet<>( );
        listRemoved.forEach( removed -> setRemovedDisplayIds.add( removed.getId( ) ) );
        for ( FormDisplay removed : listRemoved )
        {
            if ( CompositeDisplayType.QUESTION.getLabel( ).equalsIgnoreCase( removed.getCompositeType( ) ) && ControlHome
                    .getControlByQuestionAndType( removed.getCompositeId( ), ControlType.CONDITIONAL.getLabel( ) ).stream( )
                    .anyMatch( control -> !setRemovedDisplayIds.contains( control.getIdControlTarget( ) ) ) )
            {
                throw new FormBuilderException( ERROR_CONDITION_DEPENDENCY );
            }
        }

        TransactionManager.beginTransaction( FormsPlugin.getPlugin( ) );
        try
        {
            _formDisplayService.deleteDisplayAndDescendants( nIdDisplay );
            _formDisplayService.rebuildDisplayPositionSequence(
                    new ArrayList<>( _formDatabaseService.getFormDisplayListByParent( display.getStepId( ), display.getParentId( ) ) ) );
            TransactionManager.commitTransaction( FormsPlugin.getPlugin( ) );
        }
        catch( RuntimeException e )
        {
            TransactionManager.rollBack( FormsPlugin.getPlugin( ) );
            _formsCacheService.resetCache( );
            throw e;
        }
    }

    /**
     * Refuses to take a display to another step when a condition links what is moved to a question that stays: a conditional display of the moved
     * questions depending on a question left behind, a question left behind conditioned by a moved question, or a transition of the step relying on a
     * moved question. The condition would point to a question of another page and the field could never be shown, or the transition never taken.
     *
     * @param display
     *            the display to move, with its descendants
     * @throws FormBuilderException
     *             if such a condition exists
     */
    private void checkNoConditionLeavesStep( FormDisplay display ) throws FormBuilderException
    {
        List<FormDisplay> listMoved = getSubtree( display );
        Set<Integer> setMovedDisplayIds = new HashSet<>( );
        Set<Integer> setMovedQuestionIds = new HashSet<>( );
        for ( FormDisplay moved : listMoved )
        {
            setMovedDisplayIds.add( moved.getId( ) );
            if ( CompositeDisplayType.QUESTION.getLabel( ).equalsIgnoreCase( moved.getCompositeType( ) ) )
            {
                setMovedQuestionIds.add( moved.getCompositeId( ) );
            }
        }

        for ( FormDisplay moved : listMoved )
        {
            for ( Control control : ControlHome.getControlByControlTargetAndType( moved.getId( ), ControlType.CONDITIONAL ) )
            {
                if ( control.getListIdQuestion( ) != null && !setMovedQuestionIds.containsAll( control.getListIdQuestion( ) ) )
                {
                    throw new FormBuilderException( ERROR_CROSS_STEP_CONDITION );
                }
            }
        }

        for ( int nIdQuestion : setMovedQuestionIds )
        {
            if ( ControlHome.getControlByQuestionAndType( nIdQuestion, ControlType.CONDITIONAL.getLabel( ) ).stream( )
                    .anyMatch( control -> !setMovedDisplayIds.contains( control.getIdControlTarget( ) ) )
                    || CollectionUtils.isNotEmpty( ControlHome.getControlByQuestionAndType( nIdQuestion, ControlType.TRANSITION.getLabel( ) ) ) )
            {
                throw new FormBuilderException( ERROR_CROSS_STEP_CONDITION );
            }
        }
    }

    /**
     * Returns a display and all its descendants
     *
     * @param display
     *            the display
     * @return the display followed by its descendants, depth first
     */
    private List<FormDisplay> getSubtree( FormDisplay display )
    {
        List<FormDisplay> listSubtree = new ArrayList<>( );
        listSubtree.add( display );
        if ( CompositeDisplayType.GROUP.getLabel( ).equalsIgnoreCase( display.getCompositeType( ) ) )
        {
            for ( FormDisplay child : _formDatabaseService.getFormDisplayListByParent( display.getStepId( ), display.getId( ) ) )
            {
                listSubtree.addAll( getSubtree( child ) );
            }
        }
        return listSubtree;
    }

    /**
     * Duplicates a step with all its content, through the JSON export / import used by the step management screen. The copy is never initial: a form has
     * a single initial step.
     *
     * @param nIdForm
     *            the form
     * @param nIdStep
     *            the step to duplicate
     * @param locale
     *            the locale
     * @throws FormBuilderException
     *             if the step does not belong to the form or cannot be copied
     */
    @Transactional
    public void duplicateStep( int nIdForm, int nIdStep, Locale locale ) throws FormBuilderException
    {
        getStepOfForm( nIdForm, nIdStep );
        try
        {
            StepJsonData stepData = _formJsonService.readStepJson( _formJsonService.jsonExportStep( nIdForm, nIdStep ) );
            stepData.getStep( ).setInitial( false );
            _formJsonService.jsonImportStep( nIdForm, stepData, locale );
        }
        catch( JsonProcessingException e )
        {
            throw new FormBuilderException( ERROR_STEP_NOT_COPIED );
        }
        catch( RuntimeException e )
        {
            _formsCacheService.resetCache( );
            throw e;
        }
    }

    /**
     * Imports a step (from its JSON export or from a step template) into the form. The imported step is initial only when the form has no initial step
     * yet. The step and its transition are created in one transaction, joined by the JSON import. When a previous step is given, a transition from it to
     * the imported step is created with the first priority, so that the imported step is
     * the one reached after it; the existing transitions of the previous step keep their order behind it.
     *
     * @param nIdForm
     *            the form
     * @param strStepJson
     *            the JSON export of a step, or null to import a template
     * @param nIdTemplate
     *            the step template to import when no JSON is given
     * @param nIdStepAfter
     *            the step the imported step follows, 0 for none
     * @param locale
     *            the locale
     * @return the identifier of the imported step
     * @throws FormBuilderException
     *             if the JSON or the template cannot be read, or the previous step does not belong to the form
     */
    @Transactional
    public int importStep( int nIdForm, String strStepJson, int nIdTemplate, int nIdStepAfter, Locale locale ) throws FormBuilderException
    {
        if ( nIdStepAfter > 0 )
        {
            getStepOfForm( nIdForm, nIdStepAfter );
        }

        StepJsonData stepData;
        try
        {
            if ( StringUtils.isNotBlank( strStepJson ) )
            {
                stepData = _formJsonService.readStepJson( strStepJson );
            }
            else
            {
                StepJsonData template = _stepService.getStepTemplateData( nIdTemplate );
                stepData = template != null ? _formJsonService.copyStepData( template ) : null;
            }
        }
        catch( JsonProcessingException e )
        {
            throw new FormBuilderException( ERROR_STEP_NOT_IMPORTED );
        }
        if ( stepData == null || stepData.getStep( ) == null )
        {
            throw new FormBuilderException( ERROR_STEP_NOT_IMPORTED );
        }

        stepData.getStep( ).setInitial( StepHome.getInitialStep( nIdForm ) == null );
        try
        {
            _formJsonService.jsonImportStep( nIdForm, stepData, locale );
            int nIdStepImported = stepData.getStep( ).getId( );

            if ( nIdStepAfter > 0 )
            {
                Transition transition = new Transition( );
                transition.setFromStep( nIdStepAfter );
                transition.setNextStep( nIdStepImported );
                transition.setPriority( 0 );
                TransitionHome.createWithoutPriorityCalculation( transition );
                TransitionHome.rebuildPrioritySequence( nIdStepAfter );
            }
            _formService.removeBackupsByForm( nIdForm );

            return nIdStepImported;
        }
        catch( RuntimeException e )
        {
            _formsCacheService.resetCache( );
            throw e;
        }
    }

    /**
     * Removes a step with the rules of the step management screen: the initial step and, for an active form, the last final step cannot be removed.
     *
     * @param nIdForm
     *            the form
     * @param nIdStep
     *            the step to remove
     * @throws FormBuilderException
     *             if the step does not belong to the form or cannot be removed
     */
    public void removeStep( int nIdForm, int nIdStep ) throws FormBuilderException
    {
        Step step = getStepOfForm( nIdForm, nIdStep );

        if ( step.isInitial( ) )
        {
            throw new FormBuilderException( ERROR_REMOVE_INITIAL_STEP );
        }

        Form form = FormHome.findByPrimaryKey( nIdForm );
        if ( form != null && form.isActive( ) && step.isFinal( ) && StepHome.countOtherFinalSteps( nIdForm, nIdStep ) == 0 )
        {
            throw new FormBuilderException( ERROR_REMOVE_LAST_FINAL_STEP );
        }

        _stepService.removeStep( nIdStep );
        FormResponseStepHome.removeByStep( nIdStep );
        _formService.removeBackupsByForm( nIdForm );
    }

    /**
     * Loads a display and checks that it belongs to the form
     *
     * @param nIdForm
     *            the form
     * @param nIdDisplay
     *            the display
     * @return the display
     * @throws FormBuilderException
     *             if the display does not exist or belongs to another form
     */
    public FormDisplay getDisplayOfForm( int nIdForm, int nIdDisplay ) throws FormBuilderException
    {
        FormDisplay display = _formDatabaseService.findDisplayByPrimaryKey( nIdDisplay );
        if ( display == null || display.getFormId( ) != nIdForm )
        {
            throw new FormBuilderException( ERROR_DISPLAY_NOT_FOUND );
        }
        return display;
    }

    /**
     * Loads a step and checks that it belongs to the form
     *
     * @param nIdForm
     *            the form
     * @param nIdStep
     *            the step
     * @return the step
     * @throws FormBuilderException
     *             if the step does not exist or belongs to another form
     */
    public Step getStepOfForm( int nIdForm, int nIdStep ) throws FormBuilderException
    {
        Step step = _formDatabaseService.findStepByPrimaryKey( nIdStep );
        if ( step == null || step.getIdForm( ) != nIdForm )
        {
            throw new FormBuilderException( ERROR_STEP_NOT_FOUND );
        }
        return step;
    }

    /**
     * Checks that the target parent is the root of the step or a group of the same form and step, which is neither the display itself nor one of its
     * descendants
     *
     * @param nIdForm
     *            the form
     * @param display
     *            the display to move
     * @param nIdStepTarget
     *            the target step
     * @param nIdParentTarget
     *            the target parent, 0 for the root of the step
     * @throws FormBuilderException
     *             if the target parent is not valid
     */
    private void checkTargetParent( int nIdForm, FormDisplay display, int nIdStepTarget, int nIdParentTarget ) throws FormBuilderException
    {
        if ( nIdParentTarget == 0 )
        {
            return;
        }

        FormDisplay parent = _formDatabaseService.findDisplayByPrimaryKey( nIdParentTarget );
        if ( parent == null || parent.getFormId( ) != nIdForm || parent.getStepId( ) != nIdStepTarget
                || !CompositeDisplayType.GROUP.getLabel( ).equalsIgnoreCase( parent.getCompositeType( ) ) )
        {
            throw new FormBuilderException( ERROR_INVALID_TARGET );
        }

        FormDisplay ancestor = parent;
        while ( ancestor != null )
        {
            if ( ancestor.getId( ) == display.getId( ) )
            {
                throw new FormBuilderException( ERROR_GROUP_INTO_ITSELF );
            }
            ancestor = ancestor.getParentId( ) > 0 ? _formDatabaseService.findDisplayByPrimaryKey( ancestor.getParentId( ) ) : null;
        }
    }
}
