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

import java.util.List;

import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.ControlType;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.business.Transition;
import fr.paris.lutece.plugins.forms.business.TransitionHome;
import fr.paris.lutece.plugins.forms.service.json.IStepTemplateProvider;
import fr.paris.lutece.plugins.forms.service.json.StepJsonData;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

/**
 * Service dedicated to management of formDisplay
 */
@ApplicationScoped
public class StepService
{
    public static final String BEAN_NAME = "forms.stepService";

    private static final int DISPLAY_ROOT_PARENT_ID = 0;

    @Inject
    private Instance<IStepTemplateProvider> _provider;

    @Inject
    private FormDisplayService _displayService;
    
    /**
     * Constructor
     */
    StepService( )
    {
    }

    /**
     * Remove a given Step and the all its formDisplays. The responses, group/question associated to the displays will be deleted. All the descendants of the
     * displays will also be removed
     * 
     * @param nIdStep
     *            The Step Id
     */
    public void removeStep( int nIdStep )
    {
        List<FormDisplay> listChildrenDisplay = FormDisplayHome.getFormDisplayListByParent( nIdStep, DISPLAY_ROOT_PARENT_ID );

        for ( FormDisplay childDisplay : listChildrenDisplay )
        {
            _displayService.deleteDisplayAndDescendants( childDisplay.getId( ) );
        }

        for ( Transition transition : TransitionHome.getTransitionsListFromStep( nIdStep ) )
        {
            ControlHome.removeByControlTarget( transition.getId( ), ControlType.TRANSITION );
        }

        TransitionHome.removeTransitionByStep( nIdStep );

        StepHome.remove( nIdStep );

    }

    public IStepTemplateProvider getStepTemplateProvider( )
    {
        return _provider.isUnsatisfied( ) ? null : _provider.get( );
    }

    public StepJsonData getStepTemplateData( int idTemplate )
    {
        if ( _provider.isUnsatisfied( ) )
        {
            return null;
        }
        return _provider.get( ).getStepTemplateData( idTemplate );
    }
}
