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

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.Group;
import fr.paris.lutece.plugins.forms.business.GroupHome;
import fr.paris.lutece.plugins.forms.service.FormService;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * Implementation of ICompositeDisplay for Groups
 *
 */
public class CompositeGroupDisplay implements ICompositeDisplay
{
    private static final String GROUP_TEMPLATE = "/skin/plugins/forms/composite_template/view_group.html";
    private static final String GROUP_MARKER = "group";
    private static final String GROUP_CONTENT_MARKER = "groupContent";
    private static final String PROPERTY_COMPOSITE_GROUP_ICON = "forms.composite.group.icon";
    private static final String DEFAULT_GROUP_ICON = "indent";

    private static FormService _formService = SpringContextService.getBean( FormService.BEAN_NAME );

    private List<ICompositeDisplay> _listChildren = new ArrayList<ICompositeDisplay>( );
    private Group _group;
    private FormDisplay _formDisplay;
    private String _strIconName;

    @Override
    public void initComposite( FormDisplay formDisplay )
    {
        if ( !StringUtils.isEmpty( formDisplay.getCompositeType( ) ) )
        {
            _group = GroupHome.findByPrimaryKey( formDisplay.getCompositeId( ) );
            _strIconName = AppPropertiesService.getProperty( PROPERTY_COMPOSITE_GROUP_ICON, DEFAULT_GROUP_ICON );
        }

        List<FormDisplay> formDisplayList = FormDisplayHome.getFormDisplayListByParent( formDisplay.getStepId( ), formDisplay.getId( ) );

        for ( FormDisplay formDisplayChild : formDisplayList )
        {
            ICompositeDisplay composite = _formService.formDisplayToComposite( formDisplayChild );
            _listChildren.add( composite );
            composite.initComposite( formDisplayChild );
        }
    }

    @Override
    public String getCompositeHtml( Locale locale )
    {
        Map<String, Object> model = new HashMap<String, Object>( );

        StringBuilder strBuilder = new StringBuilder( );

        for ( ICompositeDisplay child : _listChildren )
        {
            strBuilder.append( child.getCompositeHtml( locale ) );
        }

        model.put( GROUP_MARKER, _group );
        model.put( GROUP_CONTENT_MARKER, strBuilder.toString( ) );

        HtmlTemplate t = AppTemplateService.getTemplate( GROUP_TEMPLATE, locale, model );

        return t.getHtml( );
    }

    @Override
    public void setResponses( Map<Integer, List<Response>> mapStepResponses )
    {
        for ( ICompositeDisplay composite : _listChildren )
        {
            composite.setResponses( mapStepResponses );
        }
    }

    @Override
    public List<ICompositeDisplay> getCompositeList( )
    {
        List<ICompositeDisplay> listICompositeDisplay = new ArrayList<ICompositeDisplay>( );
        listICompositeDisplay.add( this );

        for ( ICompositeDisplay child : _listChildren )
        {
            listICompositeDisplay.addAll( child.getCompositeList( ) );
        }
        return listICompositeDisplay;
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
    public void setFormDisplay( FormDisplay formDisplay )
    {
        _formDisplay = formDisplay;

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
    public void setIcon( String strIconName )
    {
        _strIconName = strIconName;
    }
}
