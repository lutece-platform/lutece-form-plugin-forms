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
package fr.paris.lutece.plugins.forms.export;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.filegenerator.service.IFileGenerator;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.export.FormExportConfig;
import fr.paris.lutece.plugins.forms.business.export.FormExportConfigHome;
import fr.paris.lutece.plugins.forms.business.form.FormResponseItemSortConfig;
import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.filter.FormFilter;
import fr.paris.lutece.plugins.forms.business.form.panel.FormPanel;
import fr.paris.lutece.util.file.FileUtil;

public abstract class AbstractFileGenerator implements IFileGenerator
{
    private static final String PATTERN_TIMESTAMP = "_yyyy-MM-dd-HH-mm-ss";

    // 4: size for file extension (.pdf, .zip, .csv)
    private static final int MAX_NAME_LENGTH = 250 - PATTERN_TIMESTAMP.length( );
    protected static final String TMP_DIR = System.getProperty( "java.io.tmpdir" );

    protected final FormPanel _formPanel;
    protected final List<IFormColumn> _listFormColumn;
    protected final List<FormFilter> _listFormFilter;
    protected final FormResponseItemSortConfig _sortConfig;
    protected final String _fileName;
    protected final String _fileDescription;

    private List<FormExportConfig> _configList = null;
    private Form _form;

    /**
     * Constructor
     * 
     * @param _listFormResponseItems
     */
    protected AbstractFileGenerator( String fileName, FormPanel formPanel, List<IFormColumn> listFormColumn, List<FormFilter> listFormFilter,
            FormResponseItemSortConfig sortConfig, String fileDescription )
    {
        _fileName = StringUtils.substring( fileName, 0, MAX_NAME_LENGTH ) + LocalDateTime.now( ).format( DateTimeFormatter.ofPattern( PATTERN_TIMESTAMP ) );
        _formPanel = formPanel;
        _listFormColumn = new ArrayList<>( listFormColumn );
        _listFormFilter = new ArrayList<>( listFormFilter );
        _sortConfig = sortConfig;
        _fileDescription = fileDescription;
    }

    @Override
    public String getDescription( )
    {
        return _fileDescription;
    }

    protected String generateFileName( FormResponse response )
    {
        if ( CollectionUtils.isEmpty( _configList ) )
        {
            _configList = FormExportConfigHome.findByForm( response.getFormId( ) );
            _form = FormHome.findByPrimaryKey( response.getFormId( ) );
        }

        List<String> nameValues = ExportServiceManager.getInstance( ).generateNameComponents( _form, response, _configList );
        if ( CollectionUtils.isEmpty( nameValues ) )
        {
            nameValues.add( String.valueOf( response.getId( ) ) );
        }
        return FileUtil.normalizeFileName( nameValues.stream( ).collect( Collectors.joining( "_" ) ) );
    }
}
