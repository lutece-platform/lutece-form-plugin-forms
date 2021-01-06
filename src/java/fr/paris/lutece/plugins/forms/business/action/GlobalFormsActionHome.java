/*
 * Copyright (c) 2002-2021, City of Paris
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
package fr.paris.lutece.plugins.forms.business.action;

import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;
import java.util.Locale;

/**
 * This class provides instances management methods (create, find, ...) for SpaceAction objects
 */
public final class GlobalFormsActionHome
{
    // Static variable pointed at the DAO instance
    private static IGlobalFormsActionDAO _dao = SpringContextService.getBean( "forms.globalFormsActionDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private GlobalFormsActionHome( )
    {
    }

    /**
     * Load the list of all form actions
     * 
     * @param locale
     *            the locale
     * @param plugin
     *            the plugin
     * @return The Collection of actions
     */
    public static List<GlobalFormsAction> selectAllFormActions( Plugin plugin, Locale locale )
    {
        List<GlobalFormsAction> listFormActions = _dao.selectAllFormActions( plugin );

        return (List<GlobalFormsAction>) I18nService.localizeCollection( listFormActions, locale );
    }

    /**
     * Load the list of all form actions
     * 
     * @param locale
     *            the locale
     * @param plugin
     *            the plugin
     * @return The Collection of actions
     */
    public static GlobalFormsAction selectGlobalFormActionByCode( String strCode, Plugin plugin, Locale locale )
    {
        return _dao.selectGlobalFormsActionByCode( strCode, plugin, locale );
    }
}
