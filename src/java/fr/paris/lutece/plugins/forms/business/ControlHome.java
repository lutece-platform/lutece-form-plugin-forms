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
package fr.paris.lutece.plugins.forms.business;

import java.util.List;

import fr.paris.lutece.plugins.forms.service.cache.FormsCacheService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

/**
 * This class provides instances management methods (create, find, ...) for Control objects
 */
public final class ControlHome
{
    // Static variable pointed at the DAO instance
    private static IControlDAO _dao = SpringContextService.getBean( "forms.controlDAO" );
    private static FormsCacheService _cache = SpringContextService.getBean( "forms.cacheService" );
    private static Plugin _plugin = PluginService.getPlugin( "forms" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private ControlHome( )
    {
    }

    /**
     * Create an instance of the control class
     * 
     * @param control
     *            The instance of the Control which contains the informations to store
     * @return The instance of control which has been created with its primary key.
     */
    public static Control create( Control control )
    {
        _dao.insert( control, _plugin );
        for ( int idQuestion : control.getListIdQuestion( ) )
        {

            _dao.insert( control.getId( ), idQuestion, _plugin );
        }
        _cache.putInCache( _cache.getControlCacheKey( control.getId( ) ), control );
        return control;
    }

    public static void createMappingControl( int nIdcontrol, int nIdQuestion, String strValue )
    {

        _dao.insert( nIdcontrol, nIdQuestion, strValue, _plugin );
        _cache.resetCache( );
    }

    /**
     * Update of the control which is specified in parameter
     * 
     * @param control
     *            The instance of the Control which contains the data to store
     * @return The instance of the control which has been updated
     */
    public static Control update( Control control )
    {

        _dao.store( control, _plugin );
        _dao.deleteControlQuestion( control.getId( ), _plugin );
        for ( int nIdQuestion : control.getListIdQuestion( ) )
        {

            _dao.insert( control.getId( ), nIdQuestion, _plugin );
        }
        _cache.resetCache( );
        return control;
    }

    /**
     * Remove the control whose identifier is specified in parameter
     * 
     * @param nKey
     *            The control Id
     */
    public static void remove( int nKey )
    {
        _dao.deleteControlQuestion( nKey, _plugin );
        _dao.delete( nKey, _plugin );
        _cache.resetCache( );
    }

    /**
     * Remove the control mapping whose identifier control is specified in parameter
     * 
     * @param nKey
     *            The control Id
     */
    public static void removeMappingControl( int nKey )
    {
        _dao.deleteControlQuestionValue( nKey, _plugin );
        _cache.resetCache( );
    }

    /**
     * Remove the control whose control target identifier is specified in parameter
     * 
     * @param nIdControlTarget
     *            The control target Id
     * @param controlType
     *            The control type
     */
    public static void removeByControlTarget( int nIdControlTarget, ControlType controlType )
    {
        List<Control> listControl = _dao.selectControlByControlTargetAndType( nIdControlTarget, controlType, _plugin );
        for ( Control ctrl : listControl )
        {
            _dao.deleteControlQuestion( ctrl.getId( ), _plugin );
        }
        _dao.deleteByControlTarget( nIdControlTarget, controlType, _plugin );
        _cache.resetCache( );
    }

    /**
     * Returns an instance of a control whose identifier is specified in parameter
     * 
     * @param nKey
     *            The control primary key
     * @return an instance of Control
     */
    public static Control findByPrimaryKey( int nKey )
    {
        String controlCacheKey = _cache.getControlCacheKey( nKey );
        Control control = ( Control ) _cache.getFromCache( controlCacheKey );
        if ( control == null )
        {
            control = _dao.load( nKey, _plugin );
            if ( control != null )
            {
                control.setListIdQuestion( _dao.loadIdQuestions( nKey, _plugin ) );
            }
            _cache.putInCache( controlCacheKey, control );
        }
        return control;
    }

    /**
     * Load the data of all the control objects and returns them as a list
     * 
     * @return the list which contains the data of all the control objects
     */
    public static List<Control> getControlsList( )
    {
        return _dao.selectControlsList( _plugin );
    }

    /**
     * Load the id of all the control objects and returns them as a list
     * 
     * @return the list which contains the id of all the control objects
     */
    public static List<Integer> getIdControlsList( )
    {
        return _dao.selectIdControlsList( _plugin );
    }

    /**
     * Load the data of all the control objects and returns them as a referenceList
     * 
     * @return the referenceList which contains the data of all the control objects
     */
    public static ReferenceList getControlsReferenceList( )
    {
        return _dao.selectControlsReferenceList( _plugin );
    }

    /**
     * Loads an existing control based on the display it is linked to (for conditional display)
     * 
     * @param nIdControlTarget
     *            the Display
     * @param controlType
     *            the control type
     * @return the requested Control
     */
    public static List<Control> getControlByControlTargetAndType( int nIdControlTarget, ControlType controlType )
    {
        String cacheKey = _cache.getControlByControlTargetAndTypeCacheKey( nIdControlTarget, controlType );
        @SuppressWarnings( "unchecked" )
        List<Control> listControl = ( List<Control> ) _cache.getFromCache( cacheKey );
        if ( listControl == null )
        {
            listControl = _dao.selectControlByControlTargetAndType( nIdControlTarget, controlType, _plugin );
            for ( Control ctrl : listControl )
            {
                ctrl.setListIdQuestion( _dao.loadIdQuestions( ctrl.getId( ), _plugin ) );
            }
            _cache.putInCache( cacheKey, listControl );
        }
        return listControl;
    }

    /**
     * Return the number of controls linked to the display
     * 
     * @param nIdControlTarget
     * @param controlType
     * @return
     */
    public static int getControlCountByControlTargetAndType( int nIdControlTarget, ControlType controlType )
    {
    	List<Control> listControl = getControlByControlTargetAndType(nIdControlTarget, controlType);
    	if (listControl != null) {
    		return listControl.size();
    	}
    	return 0;
    }

    /**
     * Loads control list based on its attached question and control type
     * 
     * @param nIdQuestion
     *            the question id
     * @param strControlType
     *            the control type
     * @return the requested Control list
     */
    public static List<Control> getControlByQuestionAndType( int nIdQuestion, String strControlType )
    {
        String cacheKey = _cache.getControlByQuestionAndTypeCacheKey( nIdQuestion, strControlType );
        @SuppressWarnings( "unchecked" )
        List<Control> listControl = ( List<Control> ) _cache.getFromCache( cacheKey );
        if ( listControl == null )
        {
            listControl = _dao.selectControlByQuestionAndType( nIdQuestion, strControlType, _plugin );
            for ( Control ctrl : listControl )
            {
                ctrl.setListIdQuestion( _dao.loadIdQuestions( ctrl.getId( ), _plugin ) );
            }
            _cache.putInCache( cacheKey, listControl );
        }
        return listControl;
    }

    /**
     * Loads control list based on its attached question and control type
     * 
     * @param nIdQuestion
     *            the question id
     * @return the requested Control list
     */
    public static List<Control> getControlByQuestion( int nIdQuestion )
    {
        List<Control> listControl = _dao.selectControlByQuestion( nIdQuestion, _plugin );
        for ( Control ctrl : listControl )
        {
            ctrl.setListIdQuestion( _dao.loadIdQuestions( ctrl.getId( ), _plugin ) );
        }
        return listControl;
    }

    /**
     * Load the data of all the control objects and returns them as a referenceList
     * 
     * @param nIdControl
     *            the Control id
     * @return the referenceList which contains the data of all the control objects
     */
    public static ReferenceList getCtrlMappingListByIdControl( int nIdControl )
    {
        return _dao.selectMappingControlReferenceList( nIdControl, _plugin );
    }

    /**
     * Load the data of all the control objects and returns them as a list
     * 
     * @param nIdControl
     *            the Control id
     * @return the referenceList which contains the data of all the control objects
     */
    public static List<ControlMapping> getControlMappingListByIdControl( int nIdControl )
    {
        return _dao.selectMappingControlList( nIdControl, _plugin );
    }
}
