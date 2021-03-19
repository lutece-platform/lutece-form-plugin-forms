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
package fr.paris.lutece.plugins.forms.business.form.filter.configuration;

import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.filter.FormFilter;
import fr.paris.lutece.plugins.forms.business.form.filter.querypart.IFormFilterQueryPart;
import fr.paris.lutece.plugins.forms.business.form.filter.querypart.impl.FormFilterEntryLuceneQueryPart;
import fr.paris.lutece.plugins.forms.web.form.filter.display.IFormFilterDisplay;
import fr.paris.lutece.plugins.forms.web.form.filter.display.impl.FormFilterDisplayEntry;

/**
 * Specific configuration for the Filter of an Entry
 */
public class FormFilterEntryConfiguration extends AbstractFormFilterConfiguration
{
    private final IFormColumn _formColumn;

    public FormFilterEntryConfiguration( int nPosition, String strFormFilterLabel, String strFormFilterName, IFormColumn formColumn )
    {
        super( nPosition, strFormFilterLabel, strFormFilterName );
        _formColumn = formColumn;
    }

    /**
     * Return the FormColumn linked to the FormFilter
     * 
     * @return the FormColumn linked to the FormFilter
     */
    public IFormColumn getFormColumn( )
    {
        return _formColumn;
    }

    @Override
    public IFormFilterQueryPart getFormFilterQueryPart( )
    {
        return new FormFilterEntryLuceneQueryPart( );
    }

    @Override
    public IFormFilterDisplay getFormFilterDisplay( FormFilter formFilter )
    {
        FormFilterDisplayEntry formFilterDisplayEntry = new FormFilterDisplayEntry( );
        formFilterDisplayEntry.setFormFilter( formFilter );
        return formFilterDisplayEntry;
    }
}
