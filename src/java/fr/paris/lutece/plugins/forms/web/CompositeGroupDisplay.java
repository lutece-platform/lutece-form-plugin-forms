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
package fr.paris.lutece.plugins.forms.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseStep;
import fr.paris.lutece.plugins.forms.business.Group;
import fr.paris.lutece.plugins.forms.business.GroupHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.service.FormService;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.web.entrytype.DisplayType;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * 
 * Implementation of ICompositeDisplay for Groups
 *
 */
public class CompositeGroupDisplay implements ICompositeDisplay
{
    // Templates
    private static final String TEMPLATE_GROUP_EDITION_FRONTOFFICE = "/skin/plugins/forms/composite_template/view_group.html";
    private static final String TEMPLATE_GROUP_EDITION_BACKOFFICE = "/admin/plugins/forms/composite/view_group_edition.html";
    private static final String TEMPLATE_GROUP_READONLY_FRONTOFFICE = "/skin/plugins/forms/composite_template/view_group_read_only.html";
    private static final String TEMPLATE_GROUP_READONLY_BACKOFFICE = "/admin/plugins/forms/composite/view_group.html";
    private static final String TEMPLATE_GROUP_RESUBMIT_BACKOFFICE = "/admin/plugins/forms/composite/view_group_resubmit.html";
    private static final String TEMPLATE_GROUP_RESUBMIT_FRONTOFFICE = "/skin/plugins/forms/composite_template/view_group_resubmit.html";

    // Marks
    private static final String MARK_GROUP = "group";
    private static final String MARK_GROUP_CONTENT = "groupContent";
    private static final String MARK_IS_ITERABLE = "isIterable";
    private static final String MARK_NB_BASE_CHILDREN = "nbBaseChildren";
    private static final String MARK_VISIBLE = "visible";

    // Properties
    private static final String PROPERTY_COMPOSITE_GROUP_ICON = "forms.composite.group.icon";

    private static final String DEFAULT_GROUP_ICON = "indent";

    private static FormService _formService = SpringContextService.getBean( FormService.BEAN_NAME );
    private int _nIterationNumber;
    private int _nNbBaseChildren;

    private final List<ICompositeDisplay> _listChildren = new ArrayList< >( );
    private Group _group;
    private final FormDisplay _formDisplay;
    private String _strIconName;
    private final Map<String, Object> _model = new HashMap<>( );

    /**
     * Constructor
     * 
     * @param formDisplay
     *            the form display
     * @param formResponse
     *            the form response
     * @param nIterationNumber
     *            the iteration number
     */
    public CompositeGroupDisplay( FormDisplay formDisplay, FormResponse formResponse, int nIterationNumber )
    {
        _formDisplay = formDisplay;

        initComposite( formResponse );
    }

    /**
     * Initializes the composite
     * 
     * @param formResponse
     *            the form response
     * @param nIterationNumber
     *            the iteration number
     */
    private void initComposite( FormResponse formResponse )
    {
        if ( !StringUtils.isEmpty( _formDisplay.getCompositeType( ) ) )
        {
            _group = GroupHome.findByPrimaryKey( _formDisplay.getCompositeId( ) );
            _strIconName = AppPropertiesService.getProperty( PROPERTY_COMPOSITE_GROUP_ICON, DEFAULT_GROUP_ICON );
        }

        List<FormDisplay> listFormDisplayChildren = FormDisplayHome.getFormDisplayListByParent( _formDisplay.getStepId( ), _formDisplay.getId( ) );
        _nNbBaseChildren = listFormDisplayChildren.size( );
        FormResponseStep formResponseStep = findResponseStep( _formDisplay, formResponse );
        _nIterationNumber = findIterationNumber( listFormDisplayChildren, formResponseStep );

        for ( int i = 0; i <= _nIterationNumber; i++ )
        {
            addChildren( listFormDisplayChildren, formResponse, i );
        }
    }

    /**
     * Finds the form response step associated to the step of this instance
     * 
     * @param formDisplay
     *            the form display
     * @param formResponse
     *            the form response
     * @return the form response step
     */
    private FormResponseStep findResponseStep( FormDisplay formDisplay, FormResponse formResponse )
    {
        FormResponseStep formResponseStepResult = null;

        if ( formResponse != null )
        {
            for ( FormResponseStep formResponseStep : formResponse.getSteps( ) )
            {
                if ( formResponseStep.getStep( ).getId( ) == formDisplay.getStepId( ) )
                {
                    formResponseStepResult = formResponseStep;
                    break;
                }
            }
        }

        return formResponseStepResult;
    }

    /**
     * Finds the iteration number for this instance
     * 
     * @param listFormDisplayChildren
     *            the children list of form displays of this instance
     * @param formResponseStep
     *            the form response step
     * @return the iteration number
     */
    private int findIterationNumber( List<FormDisplay> listFormDisplayChildren, FormResponseStep formResponseStep )
    {
        int nIterationNumber = _group.getIterationMin( ) - 1;

        if ( formResponseStep != null )
        {
            for ( FormDisplay formDisplayChildren : listFormDisplayChildren )
            {
                for ( FormQuestionResponse formQuestionResponse : formResponseStep.getQuestions( ) )
                {
                    Question question = formQuestionResponse.getQuestion( );

                    if ( question.getId( ) == formDisplayChildren.getCompositeId( ) )
                    {
                        nIterationNumber = Math.max( nIterationNumber, question.getIterationNumber( ) );
                    }
                }
            }
        }

        return nIterationNumber;
    }

    /**
     * Adds children from the specified list of form displays
     * 
     * @param listFormDisplayChildren
     *            the list of form displays
     * @param formResponse
     *            the form response
     * @param nIterationNumber
     *            the iteration number
     */
    private void addChildren( List<FormDisplay> listFormDisplayChildren, FormResponse formResponse, int nIterationNumber )
    {
        for ( FormDisplay formDisplayChild : listFormDisplayChildren )
        {
            ICompositeDisplay composite = _formService.formDisplayToComposite( formDisplayChild, formResponse, nIterationNumber );
            _listChildren.add( composite );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCompositeHtml( HttpServletRequest request, List<FormQuestionResponse> listFormQuestionResponse, Locale locale, DisplayType displayType )
    {
        List<String> listChildrenHtml = new ArrayList< >( );

        for ( ICompositeDisplay child : _listChildren )
        {
            child.addModel( _model );
            listChildrenHtml.add( child.getCompositeHtml( request, listFormQuestionResponse, locale, displayType ) );
        }

        _model.put( MARK_VISIBLE, isVisible( ) );
        _model.put( MARK_GROUP, _group );
        _model.put( MARK_GROUP_CONTENT, listChildrenHtml );
        _model.put( FormsConstants.PARAMETER_ID_GROUP, _formDisplay.getId( ) );
        _model.put( MARK_IS_ITERABLE, isIterable( ) );
        _model.put( MARK_NB_BASE_CHILDREN, _nNbBaseChildren );

        if ( _formDisplay.getDisplayControl( ) != null )
        {
            _model.put( FormsConstants.MARK_ID_DISPLAY, _formDisplay.getDisplayControl( ).getIdControlTarget( ) );
        }

        String strTemplate = findTemplateFor( displayType );

        return AppTemplateService.getTemplate( strTemplate, locale, _model ).getHtml( );
    }

    /**
     * Finds the template to use for the specified display type
     * 
     * @param displayType
     *            the display type
     * @return the template
     */
    private String findTemplateFor( DisplayType displayType )
    {
        String strTemplate = StringUtils.EMPTY;

        if ( displayType == DisplayType.EDITION_FRONTOFFICE )
        {
            strTemplate = TEMPLATE_GROUP_EDITION_FRONTOFFICE;
        }

        if ( displayType == DisplayType.READONLY_BACKOFFICE )
        {
            strTemplate = TEMPLATE_GROUP_READONLY_BACKOFFICE;
        }
        if ( displayType == DisplayType.READONLY_FRONTOFFICE )
        {
            strTemplate = TEMPLATE_GROUP_READONLY_FRONTOFFICE;
        }
        if ( displayType == DisplayType.EDITION_BACKOFFICE )
        {
            strTemplate = TEMPLATE_GROUP_EDITION_BACKOFFICE;
        }
        if ( displayType == DisplayType.RESUBMIT_BACKOFFICE )
        {
            strTemplate = TEMPLATE_GROUP_RESUBMIT_BACKOFFICE;
        }
        if ( displayType == DisplayType.COMPLETE_BACKOFFICE )
        {
            strTemplate = TEMPLATE_GROUP_RESUBMIT_BACKOFFICE;
        }
        if ( displayType == DisplayType.RESUBMIT_FRONTOFFICE )
        {
            strTemplate = TEMPLATE_GROUP_RESUBMIT_FRONTOFFICE;
        }
        if ( displayType == DisplayType.COMPLETE_FRONTOFFICE )
        {
            strTemplate = TEMPLATE_GROUP_RESUBMIT_FRONTOFFICE;
        }
        return strTemplate;
    }

    /**
     * Tests if this instance is iterable or not
     * 
     * @return {@code true} if this instance is iterable, {@code false} otherwise
     */
    private boolean isIterable( )
    {
        boolean bIsIterable = true;

        if ( _group.getIterationMax( ) > 0 && _group.getIterationMax( ) - 1 <= _nIterationNumber )
        {
            bIsIterable = false;
        }
        else
            if ( !_listChildren.isEmpty( ) )
            {
                for ( ICompositeDisplay composite : _listChildren )
                {
                    if ( CompositeDisplayType.GROUP.getLabel( ).equals( composite.getType( ) ) )
                    {
                        bIsIterable = false;
                        break;
                    }
                }
            }

        return bIsIterable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void iterate( int nIdFormDisplay )
    {
        if ( _formDisplay.getId( ) == nIdFormDisplay && isIterable( ) )
        {
            _nIterationNumber++;

            List<FormDisplay> listFormDisplayChildren = FormDisplayHome.getFormDisplayListByParent( _formDisplay.getStepId( ), _formDisplay.getId( ) );

            addChildren( listFormDisplayChildren, null, _nIterationNumber );
        }
        else
        {
            for ( ICompositeDisplay composite : _listChildren )
            {
                composite.iterate( nIdFormDisplay );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeIteration( HttpServletRequest request, int nIdGroupParent, int nIndexIterationToRemove, FormResponse formResponse )
    {
        for ( ICompositeDisplay child : _listChildren )
        {
            child.removeIteration( request, nIdGroupParent, nIndexIterationToRemove, formResponse );
        }

        if ( nIdGroupParent == _group.getId( ) )
        {
            int nNbIterationToAdd = _nIterationNumber - nIndexIterationToRemove;

            _nIterationNumber = nIndexIterationToRemove - 1;

            int nIndexIterationStart = nIndexIterationToRemove * _nNbBaseChildren;

            _listChildren.subList( nIndexIterationStart, _listChildren.size( ) ).clear( );

            List<FormDisplay> listGroupChildren = FormDisplayHome.getFormDisplayListByParent( _group.getIdStep( ), _formDisplay.getId( ) );

            updateIterationResponse( formResponse, listGroupChildren, nIndexIterationToRemove );

            for ( int i = 0; i < nNbIterationToAdd; i++ )
            {
                _nIterationNumber++;
                addChildren( listGroupChildren, formResponse, _nIterationNumber );
            }
        }
    }

    /**
     * 
     * @param formResponse
     *            The Form responses
     * @param listFormDisplayChildren
     *            The children of current formDisplay
     * @param nIndexIterationToRemove
     *            The index of iteration to remove
     */
    private void updateIterationResponse( FormResponse formResponse, List<FormDisplay> listFormDisplayChildren, int nIndexIterationToRemove )
    {
        List<FormQuestionResponse> listFormQuestionResponse = new ArrayList< >( );

        for ( FormResponseStep formResponseStep : formResponse.getSteps( ) )
        {
            if ( formResponseStep.getStep( ).getId( ) == _group.getIdStep( ) )
            {
                listFormQuestionResponse = formResponseStep.getQuestions( );
            }
        }

        List<Integer> listQuestionIdChildren = new ArrayList< >( );

        for ( FormDisplay formDisplayChild : listFormDisplayChildren )
        {
            if ( FormsConstants.COMPOSITE_QUESTION_TYPE.equals( formDisplayChild.getCompositeType( ) ) )
            {
                listQuestionIdChildren.add( formDisplayChild.getCompositeId( ) );
            }
        }

        Iterator<FormQuestionResponse> responseIterator = listFormQuestionResponse.iterator( );

        while ( responseIterator.hasNext( ) )
        {
        	FormQuestionResponse formQuestionResponseTemp = responseIterator.next( );

            if ( listQuestionIdChildren.contains( formQuestionResponseTemp.getQuestion( ).getId( ) ) )
            {
                int nResponseIterationNumber = formQuestionResponseTemp.getQuestion( ).getIterationNumber( );

                if ( nResponseIterationNumber == nIndexIterationToRemove )
                {
                    responseIterator.remove( );
                }
                else
                    if ( nResponseIterationNumber > nIndexIterationToRemove )
                    {
                        formQuestionResponseTemp.getQuestion( ).setIterationNumber( nResponseIterationNumber - 1 );
                    }
            }
        }
    }

    @Override
    public List<ICompositeDisplay> getCompositeList( )
    {
        List<ICompositeDisplay> listCompositeDisplay = new ArrayList< >( );
        listCompositeDisplay.add( this );

        for ( FormDisplay child : FormDisplayHome.getFormDisplayListByParent( this.getFormDisplay( ).getStepId( ), this.getFormDisplay( ).getId( ) ) )
        {
            ICompositeDisplay compositeChild = _formService.formDisplayToComposite( child, null, 0 );
            listCompositeDisplay.addAll( compositeChild.getCompositeList( ) );
        }
        return listCompositeDisplay;
    }

    @Override
    public String getTitle( )
    {
        String strTitle = "";
        if ( _group != null && StringUtils.isNotEmpty( _group.getTitle( ) ) )
        {
            strTitle = _group.getTitle( );
        }
        return strTitle;
    }

    @Override
    public String getType( )
    {
        return _group != null ? CompositeDisplayType.GROUP.getLabel( ) : StringUtils.EMPTY;
    }

    @Override
    public FormDisplay getFormDisplay( )
    {
        return _formDisplay;
    }

    @Override
    public String getIcon( )
    {
        return _strIconName;
    }

    @Override
    public List<Control> getAllDisplayControls( )
    {
        List<Control> listDisplayControls = new ArrayList< >( );

        if ( _formDisplay.getDisplayControl( ) != null )
        {
            listDisplayControls.add( _formDisplay.getDisplayControl( ) );
        }

        for ( ICompositeDisplay child : _listChildren )
        {
            listDisplayControls.addAll( child.getAllDisplayControls( ) );
        }

        return listDisplayControls;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addQuestions( List<Question> listQuestion )
    {
        for ( ICompositeDisplay child : _listChildren )
        {
            child.addQuestions( listQuestion );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addModel( Map<String, Object> model )
    {
        _model.putAll( model );
    }

    @Override
    public boolean isVisible( )
    {
        for ( ICompositeDisplay child : _listChildren )
        {
            if ( child.isVisible( ) )
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public ICompositeDisplay filter( List<Integer> listQuestionIds )
    {
        List<ICompositeDisplay> listChildrenFiltered = new ArrayList<>( );

        for ( ICompositeDisplay child : _listChildren )
        {
            ICompositeDisplay newChild = child.filter( listQuestionIds );
            if ( newChild != null )
            {
                listChildrenFiltered.add( newChild );
            }
        }
        if ( listChildrenFiltered.isEmpty( ) )
        {
            return null;
        }
        _listChildren.clear( );
        _listChildren.addAll( listChildrenFiltered );
        return this;
    }
}
