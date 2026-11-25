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
package fr.paris.lutece.plugins.forms.web.builder;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.ControlType;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.FormLayout;
import fr.paris.lutece.plugins.forms.business.GroupHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.business.Transition;
import fr.paris.lutece.plugins.forms.business.TransitionHome;
import fr.paris.lutece.plugins.forms.service.EntryServiceManager;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.web.entrytype.DisplayType;
import fr.paris.lutece.plugins.forms.web.entrytype.IEntryDisplayService;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.service.image.ImageResourceManager;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Renders the fragments of the form builder: the list of the steps and the canvas of a step, where each question is drawn with its real back office
 * widget
 */
@ApplicationScoped
public class FormBuilderRenderer
{
    @Inject
    private EntryServiceManager _entryServiceManager;

    private static final String TEMPLATE_STEPS = "/admin/plugins/forms/builder/builder_steps.html";
    private static final String TEMPLATE_CANVAS = "/admin/plugins/forms/builder/builder_canvas.html";

    private static final String MARK_STEP_LIST = "step_list";
    private static final String MARK_NODE_LIST = "node_list";
    private static final String MARK_COLUMN_LIST = "column_list";
    private static final String MARK_TRANSITION_LIST = "transition_list";
    private static final String MARK_CONDITIONAL_TRANSITIONS = "conditional_transitions";
    private static final String MARK_TRANSITION_ORDER = "transition_order";
    private static final String MARK_TRANSITIONS_BY_STEP = "transitions_by_step";
    private static final String MARK_UNREACHABLE_STEPS = "unreachable_steps";
    private static final String MARK_ENTRY_ITERATION_NUMBER = "entry_iteration_number";
    private static final String MARK_QUESTION_ENTRY = "questionEntry";
    private static final String MARK_COMPLETENESS_FO = "is_completeness_bo";
    private static final String MARK_CONFIRM_FIELD_VALUE = "confirmFieldValue";
    private static final String MARK_FIELDS_LIST_BY_ID_ENTRIES = "fields_list_by_id_entries";

    private static final String PUBLIC_IMAGE_RESOURCE = "public_image_resource";
    private static final String ILLUSTRATION_IMAGE = "illustration_image";

    private static final Pattern PATTERN_SCRIPT = Pattern.compile( "<script\\b[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL );

    /**
     * Returns the steps of a form in creation order
     *
     * @param nIdForm
     *            the form
     * @return the steps, ordered by identifier
     */
    public List<Step> getOrderedSteps( int nIdForm )
    {
        List<Step> listSteps = new ArrayList<>( StepHome.getStepsListByForm( nIdForm ) );
        listSteps.sort( Comparator.comparingInt( Step::getId ) );
        return listSteps;
    }

    /**
     * Returns the steps a user can reach, in the order he goes through them: from the initial step, following the transitions in priority order, each
     * step listed once at the first transition that reaches it (breadth first, so a loop back to an earlier step is harmless)
     *
     * @param listSteps
     *            the steps of the form
     * @param mapTransitionsByStep
     *            the outgoing transitions of each step, in priority order, keyed by step identifier
     * @return the identifiers of the reachable steps, in transition order
     */
    private Set<Integer> getReachableStepIds( List<Step> listSteps, Map<String, List<Transition>> mapTransitionsByStep )
    {
        Set<Integer> setStepIds = new HashSet<>( );
        listSteps.forEach( step -> setStepIds.add( step.getId( ) ) );

        Set<Integer> setReachable = new LinkedHashSet<>( );
        Deque<Integer> queue = new ArrayDeque<>( );
        listSteps.stream( ).filter( Step::isInitial ).findFirst( ).ifPresent( initial -> {
            setReachable.add( initial.getId( ) );
            queue.add( initial.getId( ) );
        } );

        while ( !queue.isEmpty( ) )
        {
            int nIdStep = queue.poll( );
            for ( Transition transition : mapTransitionsByStep.getOrDefault( String.valueOf( nIdStep ), new ArrayList<>( ) ) )
            {
                if ( setStepIds.contains( transition.getNextStep( ) ) && setReachable.add( transition.getNextStep( ) ) )
                {
                    queue.add( transition.getNextStep( ) );
                }
            }
        }
        return setReachable;
    }

    /**
     * Groups the transitions of a form by starting step, each group in priority order
     *
     * @param nIdForm
     *            the form
     * @return the outgoing transitions of each step, keyed by step identifier as a string (for the templates)
     */
    private Map<String, List<Transition>> getTransitionsByStep( int nIdForm )
    {
        Map<String, List<Transition>> mapTransitionsByStep = new HashMap<>( );
        List<Transition> listTransitions = new ArrayList<>( TransitionHome.getTransitionsListFromForm( nIdForm ) );
        listTransitions.sort( Comparator.comparingInt( Transition::getPriority ) );
        for ( Transition transition : listTransitions )
        {
            mapTransitionsByStep.computeIfAbsent( String.valueOf( transition.getFromStep( ) ), key -> new ArrayList<>( ) ).add( transition );
        }
        return mapTransitionsByStep;
    }

    /**
     * Returns the step shown first when none is requested: the initial step, or the first step created when the form has no initial step
     *
     * @param nIdForm
     *            the form
     * @return the default step, null if the form has no step
     */
    public Step getDefaultStep( int nIdForm )
    {
        List<Step> listSteps = getOrderedSteps( nIdForm );
        return listSteps.stream( ).filter( Step::isInitial ).findFirst( ).orElse( listSteps.isEmpty( ) ? null : listSteps.get( 0 ) );
    }

    /**
     * Renders the list of the steps, in creation order or in transition order. In transition order each step also shows the steps it leads to, and the
     * steps no transition reaches are flagged.
     *
     * @param form
     *            the form
     * @param stepCurrent
     *            the step shown in the canvas
     * @param bTransitionOrder
     *            true for the transition order, false for the creation order
     * @param locale
     *            the locale
     * @return the HTML of the list
     */
    public String renderSteps( Form form, Step stepCurrent, boolean bTransitionOrder, Locale locale )
    {
        List<Step> listSteps = getOrderedSteps( form.getId( ) );

        Map<String, Object> model = new HashMap<>( );
        model.put( FormsConstants.MARK_FORM, form );
        model.put( FormsConstants.MARK_STEP, stepCurrent );
        model.put( MARK_TRANSITION_ORDER, bTransitionOrder );

        if ( bTransitionOrder )
        {
            Map<String, List<Transition>> mapTransitionsByStep = getTransitionsByStep( form.getId( ) );
            Set<Integer> setReachable = getReachableStepIds( listSteps, mapTransitionsByStep );
            Map<Integer, Step> mapSteps = new HashMap<>( );
            listSteps.forEach( step -> mapSteps.put( step.getId( ), step ) );

            List<Step> listOrdered = new ArrayList<>( );
            setReachable.forEach( nIdStep -> listOrdered.add( mapSteps.get( nIdStep ) ) );
            List<Integer> listUnreachable = new ArrayList<>( );
            for ( Step step : listSteps )
            {
                if ( !setReachable.contains( step.getId( ) ) )
                {
                    listOrdered.add( step );
                    listUnreachable.add( step.getId( ) );
                }
            }

            List<Transition> listAllTransitions = new ArrayList<>( );
            mapTransitionsByStep.values( ).forEach( listAllTransitions::addAll );

            model.put( MARK_STEP_LIST, listOrdered );
            model.put( MARK_TRANSITIONS_BY_STEP, mapTransitionsByStep );
            model.put( MARK_CONDITIONAL_TRANSITIONS, getConditionalTransitionIds( listAllTransitions ) );
            model.put( MARK_UNREACHABLE_STEPS, listUnreachable );
        }
        else
        {
            model.put( MARK_STEP_LIST, listSteps );
        }

        return AppTemplateService.getTemplate( TEMPLATE_STEPS, locale, model ).getHtml( );
    }

    /**
     * Renders the canvas of a step
     *
     * @param request
     *            the request, needed by some entry types to render their widget
     * @param form
     *            the form
     * @param step
     *            the step
     * @param locale
     *            the locale
     * @return the HTML of the canvas
     */
    public String renderCanvas( HttpServletRequest request, Form form, Step step, Locale locale )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( FormsConstants.MARK_FORM, form );
        model.put( FormsConstants.MARK_STEP, step );
        List<FormBuilderNode> listNodes = step != null ? buildNodes( request, step.getId( ), 0, locale ) : new ArrayList<>( );
        model.put( MARK_NODE_LIST, listNodes );
        model.put( MARK_COLUMN_LIST, splitByColumn( listNodes, step != null ? step.getLayout( ) : FormLayout.ONE_COLUMN ) );
        model.put( MARK_STEP_LIST, getOrderedSteps( form.getId( ) ) );

        List<Transition> listTransitions = step != null ? TransitionHome.getTransitionsListFromStep( step.getId( ) ) : new ArrayList<>( );
        model.put( MARK_TRANSITION_LIST, listTransitions );
        model.put( MARK_CONDITIONAL_TRANSITIONS, getConditionalTransitionIds( listTransitions ) );

        return AppTemplateService.getTemplate( TEMPLATE_CANVAS, locale, model ).getHtml( );
    }

    /**
     * Returns the identifiers of the transitions that carry a control, without changing the transitions themselves (they come from the cache)
     *
     * @param listTransitions
     *            the transitions
     * @return the identifiers of the conditional transitions
     */
    private List<Integer> getConditionalTransitionIds( List<Transition> listTransitions )
    {
        List<Integer> listConditional = new ArrayList<>( );
        for ( Transition transition : listTransitions )
        {
            if ( CollectionUtils.isNotEmpty( ControlHome.getControlByControlTargetAndType( transition.getId( ), ControlType.TRANSITION ) ) )
            {
                listConditional.add( transition.getId( ) );
            }
        }
        return listConditional;
    }

    /**
     * Builds the nodes of a parent, recursively
     *
     * @param request
     *            the request
     * @param nIdStep
     *            the step
     * @param nIdParent
     *            the parent display, 0 for the root of the step
     * @param locale
     *            the locale
     * @return the nodes, ordered by display order
     */
    private List<FormBuilderNode> buildNodes( HttpServletRequest request, int nIdStep, int nIdParent, Locale locale )
    {
        List<FormBuilderNode> listNodes = new ArrayList<>( );

        for ( FormDisplay display : FormDisplayHome.getFormDisplayListByParent( nIdStep, nIdParent ) )
        {
            FormBuilderNode node = new FormBuilderNode( display );
            node.setConditional( display.getDisplayControl( ) != null );

            if ( CompositeDisplayType.GROUP.getLabel( ).equalsIgnoreCase( display.getCompositeType( ) ) )
            {
                node.setGroup( GroupHome.findByPrimaryKey( display.getCompositeId( ) ) );
                if ( node.getGroup( ) == null )
                {
                    continue;
                }
                node.getChildren( ).addAll( buildNodes( request, nIdStep, display.getId( ), locale ) );
                node.getColumns( ).addAll( splitByColumn( node.getChildren( ), node.getGroup( ).getLayout( ) ) );
            }
            else
            {
                Question question = QuestionHome.findByPrimaryKey( display.getCompositeId( ) );
                if ( question == null || question.getEntry( ) == null )
                {
                    continue;
                }
                fillQuestionNode( request, node, question, locale );
            }

            listNodes.add( node );
        }

        return listNodes;
    }

    /**
     * Splits the children of a step or of a group by column of its layout, keeping the display order inside each column. A child placed in a column
     * the layout does not have goes to its last column.
     *
     * @param listNodes
     *            the children, in display order
     * @param nLayout
     *            the layout of the step or of the group
     * @return one list of nodes per column of the layout
     */
    private List<List<FormBuilderNode>> splitByColumn( List<FormBuilderNode> listNodes, int nLayout )
    {
        List<List<FormBuilderNode>> listColumns = new ArrayList<>( );
        for ( int i = 0; i < FormLayout.getColumnCount( nLayout ); i++ )
        {
            listColumns.add( new ArrayList<>( ) );
        }
        for ( FormBuilderNode node : listNodes )
        {
            listColumns.get( FormLayout.normalizeColumn( node.getDisplay( ).getDisplayColumn( ), nLayout ) - 1 ).add( node );
        }
        return listColumns;
    }

    /**
     * Fills a question node with its entry type, its flags and its rendered widget
     *
     * @param request
     *            the request
     * @param node
     *            the node to fill
     * @param question
     *            the question
     * @param locale
     *            the locale
     */
    private void fillQuestionNode( HttpServletRequest request, FormBuilderNode node, Question question, Locale locale )
    {
        Entry entry = question.getEntry( );
        if ( entry.getFields( ) == null )
        {
            entry.setFields( FieldHome.getFieldListByIdEntry( entry.getIdEntry( ) ) );
        }

        EntryType entryType = entry.getEntryType( );
        Field fieldDisabled = entry.getFieldByCode( IEntryTypeService.FIELD_DISABLED );

        node.setQuestion( question );
        node.setEntryType( entryType );
        node.setMandatory( entry.isMandatory( ) );
        node.setDisabled( fieldDisabled != null && Boolean.parseBoolean( fieldDisabled.getValue( ) ) );
        node.setValidated( CollectionUtils.isNotEmpty( node.getDisplay( ).getValidationControlList( ) ) );
        node.setContentHtml( renderWidget( request, question, entry, locale ) );
    }

    /**
     * Renders the back office widget of a question, without its scripts: the canvas only shows the widget, it never runs it
     *
     * @param request
     *            the request
     * @param question
     *            the question
     * @param entry
     *            the entry of the question
     * @param locale
     *            the locale
     * @return the HTML of the widget, empty if the entry type has no display service or fails to render
     */
    private String renderWidget( HttpServletRequest request, Question question, Entry entry, Locale locale )
    {
        IEntryDisplayService displayService = _entryServiceManager.getEntryDisplayService( entry.getEntryType( ) );
        if ( displayService == null )
        {
            return StringUtils.EMPTY;
        }

        Map<Integer, String> mapIllustration = new HashMap<>( );
        for ( Field field : entry.getFields( ) )
        {
            if ( ILLUSTRATION_IMAGE.equals( field.getCode( ) ) && StringUtils.isNumeric( field.getValue( ) ) )
            {
                mapIllustration.put( field.getIdField( ), ImageResourceManager.getImageUrl( PUBLIC_IMAGE_RESOURCE, Integer.parseInt( field.getValue( ) ) ) );
            }
        }

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_ENTRY_ITERATION_NUMBER, question.getIterationNumber( ) );
        model.put( FormsConstants.MARK_QUESTION_LIST_RESPONSES, new ArrayList<>( ) );
        model.put( MARK_QUESTION_ENTRY, entry );
        model.put( MARK_COMPLETENESS_FO, false );
        model.put( FormsConstants.MARK_REGEX_URL, FormsConstants.DEFAULT_REGEX_URL );
        model.put( MARK_CONFIRM_FIELD_VALUE, StringUtils.EMPTY );
        model.put( MARK_FIELDS_LIST_BY_ID_ENTRIES, mapIllustration );

        try
        {
            String strHtml = displayService.getEntryTemplateDisplay( request, entry, locale, model, DisplayType.EDITION_BACKOFFICE );
            return PATTERN_SCRIPT.matcher( StringUtils.defaultString( strHtml ) ).replaceAll( StringUtils.EMPTY );
        }
        catch( RuntimeException e )
        {
            AppLogService.getLogger( ).error( "Form builder : unable to render the question {}", question.getId( ), e );
            return StringUtils.EMPTY;
        }
    }
}
