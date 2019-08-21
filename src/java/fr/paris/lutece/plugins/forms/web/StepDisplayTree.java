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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.service.FormService;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.web.entrytype.DisplayType;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;

/**
 * 
 * Management class for the display tree of a Step
 *
 */
public class StepDisplayTree
{
    // Templates
    private static final String TEMPLATE_STEP_EDITION_FRONTOFFICE = "/skin/plugins/forms/composite_template/view_step.html";
    private static final String TEMPLATE_STEP_EDITION_NO_BUTTON_FRONTOFFICE = "/skin/plugins/forms/composite_template/view_step_no_button.html";
    private static final String TEMPLATE_STEP_READONLY_FRONTOFFICE = "/skin/plugins/forms/composite_template/view_step_read_only.html";
    private static final String TEMPLATE_STEP_READONLY_BACKOFFICE = "/admin/plugins/forms/composite/view_step.html";
    private static final String TEMPLATE_STEP_SELECT_BACKOFFICE = "/admin/plugins/forms/composite/select_step.html";

    // Marks
    private static final String MARK_STEP_CONTENT = "stepContent";

    private static FormService _formService = SpringContextService.getBean( FormService.BEAN_NAME );

    private final List<ICompositeDisplay> _listChildren = new ArrayList<ICompositeDisplay>( );
    private final List<ICompositeDisplay> _listICompositeDisplay = new ArrayList<ICompositeDisplay>( );
    private Step _step;
    private Form _form;
    private final FormResponse _formResponse;
    private final Map<Integer, List<Response>> _mapStepResponses = new HashMap<Integer, List<Response>>( );
    private List<Control> _listDisplayControls = new ArrayList<Control>( );
    private final Map<String, Object> _model = new HashMap<>( );

    /**
     * Constructor
     * 
     * @param nIdStep
     *            the step identifier
     */
    public StepDisplayTree( int nIdStep )
    {
        _formResponse = null;

        initStepTree( nIdStep );
    }

    /**
     * Constructor
     * 
     * @param nIdStep
     *            the step identifier
     * @param formResponse
     *            the form response containing the responses
     */
    public StepDisplayTree( int nIdStep, FormResponse formResponse )
    {
        _formResponse = formResponse;

        initStepTree( nIdStep );
    }

    /**
     * Initialize the composite tree
     * 
     * @param nIdStep
     *            The step primary key
     */
    public void initStepTree( int nIdStep )
    {
        _step = StepHome.findByPrimaryKey( nIdStep );

        if ( _step != null )
        {
            _form = FormHome.findByPrimaryKey( _step.getIdForm( ) );

            List<FormDisplay> listStepFormDisplay = FormDisplayHome.getFormDisplayListByParent( nIdStep, 0 );
            _listDisplayControls = new ArrayList<Control>( );
            for ( FormDisplay formDisplayChild : listStepFormDisplay )
            {
                ICompositeDisplay composite = _formService.formDisplayToComposite( formDisplayChild, _formResponse, 0 );
                _listChildren.add( composite );
                _listDisplayControls.addAll( composite.getAllDisplayControls( ) );
            }
        }
    }

    /**
     * Iterates the specified form display
     * 
     * @param nIdFormDisplay
     *            the id of the form display to iterate
     */
    public void iterate( int nIdFormDisplay )
    {
        for ( ICompositeDisplay composite : _listChildren )
        {
            composite.iterate( nIdFormDisplay );
        }
    }

    /**
     * Remove the specified iteration of group
     * 
     * @param request
     *            the request
     * @param nIdGroupParent
     *            the id of the parent group
     * @param nIndexIterationToRemove
     *            the index of the iteration to remove in group
     */
    public void removeIteration( HttpServletRequest request, int nIdGroupParent, int nIndexIterationToRemove )
    {
        for ( ICompositeDisplay composite : _listChildren )
        {
            composite.removeIteration( request, nIdGroupParent, nIndexIterationToRemove, _formResponse );
        }
    }

    /**
     * Build and return the html template of the tree for Front-Office display
     * 
     * @param request
     *            the request
     * @param listFormQuestionResponse
     *            the list of form question responses
     * @param locale
     *            the locale
     * @param displayType
     *            The display type
     * @return the html template of the tree as a String
     */
    public String getCompositeHtml( HttpServletRequest request, List<FormQuestionResponse> listFormQuestionResponse, Locale locale, DisplayType displayType )
    {
        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );

        StringBuilder strBuilder = new StringBuilder( );
        
        boolean isStepVisible = false;

        for ( ICompositeDisplay child : _listChildren )
        {
            child.addModel( _model );
            strBuilder.append( child.getCompositeHtml( request, listFormQuestionResponse, locale, displayType ) );
            
            if ( child.isVisible( ) )
            {
            	isStepVisible = true;	
            }
        }
        
        if ( !isStepVisible )
        {
        	return "";
        }

        _model.put( FormsConstants.MARK_FORM, _form );
        _model.put( FormsConstants.MARK_STEP, _step );
        _model.put( FormsConstants.MARK_USER, user );
        _model.put( MARK_STEP_CONTENT, strBuilder.toString( ) );

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
            strTemplate = TEMPLATE_STEP_EDITION_FRONTOFFICE;
        }

        if ( displayType == DisplayType.READONLY_BACKOFFICE )
        {
            strTemplate = TEMPLATE_STEP_READONLY_BACKOFFICE;
        }

        if ( displayType == DisplayType.READONLY_FRONTOFFICE )
        {
            strTemplate = TEMPLATE_STEP_READONLY_FRONTOFFICE;
        }
        if ( displayType == DisplayType.EDITION_BACKOFFICE )
        {
            strTemplate = TEMPLATE_STEP_READONLY_BACKOFFICE;
        }
        if ( displayType == DisplayType.RESUBMIT_BACKOFFICE )
        {
            strTemplate = TEMPLATE_STEP_SELECT_BACKOFFICE;
        }
        if ( displayType == DisplayType.RESUBMIT_FRONTOFFICE )
        {
            strTemplate = TEMPLATE_STEP_EDITION_NO_BUTTON_FRONTOFFICE;
        }
        return strTemplate;
    }

    /**
     * Build and return all the composite display of the tree as a flat List
     * 
     * @return the list of composite display
     */
    public List<ICompositeDisplay> getCompositeList( )
    {
        for ( ICompositeDisplay child : _listChildren )
        {
            _listICompositeDisplay.addAll( child.getCompositeList( ) );
        }
        return _listICompositeDisplay;
    }

    /**
     * @return the _step
     */
    public Step getStep( )
    {
        return _step;
    }

    /**
     * @param step
     *            the step to set
     */
    public void setStep( Step step )
    {
        this._step = step;
    }

    /**
     * 
     * @return mapStepResponses The map containing question responses and potential errors
     */
    public Map<Integer, List<Response>> getResponses( )
    {
        return _mapStepResponses;
    }

    /**
     * 
     * @return the list of display controls for this display tree
     */
    public List<Control> getListDisplayControls( )
    {
        return _listDisplayControls;
    }

    /**
     * Gives all the questions
     * 
     * @return the questions
     */
    public List<Question> getQuestions( )
    {
        List<Question> listQuestion = new ArrayList<>( );

        for ( ICompositeDisplay composite : _listChildren )
        {
            composite.addQuestions( listQuestion );
        }

        return listQuestion;
    }

    /**
     * Adds the specified model in the model of this instance
     * 
     * @param model
     *            the model to add
     */
    public void addModel( Map<String, Object> model )
    {
        _model.putAll( model );
    }
}
