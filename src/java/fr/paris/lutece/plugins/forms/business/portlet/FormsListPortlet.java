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
package fr.paris.lutece.plugins.forms.business.portlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.portal.business.portlet.PortletHtmlContent;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

public class FormsListPortlet extends PortletHtmlContent
{
	public static final String RESOURCE_ID = "FORMS_LIST_PORTLET";

	// TEMPALTES
    private static final String TEMPLATE_PORTLET_FORMSLIST = "skin/plugins/forms/portletformslist_content.html";

    // MARKS
    private static final String MARK_FORMS_LIST = "forms_list";
    private static final String MARK_PORTLET_NAME = "portlet_name";

    // VARIABLES
	private List<Integer> _formsIdList = new ArrayList<>( );

	/**
     * Sets the identifier of the portlet type to the value specified in the FormsListPortletHome class
     */
    public FormsListPortlet( )
    {
        setPortletTypeId( FormsListPortletHome.getInstance( ).getPortletTypeId( ) );
    }

    /**
     * Returns the HTML code of the FormsListPortlet portlet
     *
     * @param request
     *            The HTTP servlet request
     * @return the HTML code of the FormsListPortlet portlet
     */
    @Override
    public String getHtmlContent( HttpServletRequest request )
    {
    	if ( request != null )
        {
    	    Map<String, Object> model = new HashMap<>( );

    	    model.put( MARK_FORMS_LIST, FormHome.getFormByPrimaryKeyList( _formsIdList ) );
    	    model.put( MARK_PORTLET_NAME, this.getName( ) );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_PORTLET_FORMSLIST, getLocale( request ), model );

            return template.getHtml( );
        }

        return StringUtils.EMPTY;
    }
    
    /**
     * Removes the current instance of the FormsListPortlet object
     */
    @Override
    public void remove( )
    {
        FormsListPortletHome.getInstance( ).remove( this );
    }

    /**
     * Updates the current instance of the FormsListPortlet object
     */
    public void update( )
    {
    	FormsListPortletHome.getInstance( ).update( this );
    }

	/**
	 * get the list of forms identifiers
	 * 
	 * @return the _formsIdList
	 */
	public List<Integer> getFormsIdList( )
	{
	    return _formsIdList;
	}

	/**
	 * set the list of forms identifiers
	 * 
	 * @param formsIdList the _formsIdList to set
	 */
	public void setFormsIdList( List<Integer> formsIdList ) 
	{
	    this._formsIdList = formsIdList;
	}
	
	/**
     * add a form id to the list of forms identifiers
     * 
     * @param formId
     *            The form identifier
     */
    public void addFormId( int formId )
    {
        boolean bFormExists = false;
        Iterator<Integer> itr = _formsIdList.iterator( );
        while ( itr.hasNext( ) )
    	{
    		if ( itr.next( ) == formId )
    		{
    			bFormExists = true;
    			break;
    		}
    	}

        if ( !bFormExists )
        {
            _formsIdList.add( formId );
        }
    }

    /**
     * Remove a form id to the list of forms identifiers
     * 
     * @param formId
     *            The form identifier
     */
    public void removeFormId( int formId )
    {
    	Iterator<Integer> itr = _formsIdList.iterator( );
    	while ( itr.hasNext( ) )
    	{
    		if ( itr.next( ) == formId )
    		{
    			itr.remove( );
    			break;
    		}
    	}
    }

}
