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
package fr.paris.lutece.plugins.forms.business.form.column.querypart;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.form.column.FormColumnCell;
import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.column.impl.FormColumnFormResponseDateCreation;
import fr.paris.lutece.plugins.forms.business.form.column.querypart.impl.FormColumnFormResponseDateCreationQueryPart;
import fr.paris.lutece.plugins.forms.business.form.column.querypart.mock.DAOUtilMock;
import fr.paris.lutece.plugins.forms.util.FormMultiviewFormResponseDateCreationNameConstants;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * Test of the implementation of the IFormColumnQueryPart for the Form Response Date Creation column
 */
public class FormColumnFormResponseDateCreationQueryPartTest extends LuteceTestCase
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp( ) throws Exception
    {
        super.setUp( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDown( ) throws Exception
    {
        super.tearDown( );
    }

    /**
     * Test for the {@link IFormColumnQueryPart#getFormColumnCell(fr.paris.lutece.util.sql.DAOUtil)}
     */
    public void testGetFormColumnCellFormResponseDateCreation( )
    {
        Date dateFormResponseDateCreationValueToRetrieve = new Date( );
        DAOUtil daoUtil = new DAOUtilMock( StringUtils.EMPTY, FormMultiviewFormResponseDateCreationNameConstants.COLUMN_FORM_RESPONSE_DATE_CREATION,
                dateFormResponseDateCreationValueToRetrieve );

        IFormColumn formColumn = new FormColumnFormResponseDateCreation( 1, "Form Response Date Creation" );
        FormColumnFormResponseDateCreationQueryPart formColumnFormResponseDateCreationQueryPart = new FormColumnFormResponseDateCreationQueryPart( );
        formColumnFormResponseDateCreationQueryPart.setFormColumn( formColumn );

        FormColumnCell formColumnCell = formColumnFormResponseDateCreationQueryPart.getFormColumnCell( daoUtil );
        assertThat( formColumnCell, is( not( nullValue( ) ) ) );

        Map<String, Object> mapFormColumnCellValues = formColumnCell.getFormColumnCellValues( );
        assertThat( mapFormColumnCellValues, is( not( nullValue( ) ) ) );
        assertThat( mapFormColumnCellValues.size( ), is( 1 ) );

        Object objFormResponseDateCreationResult = formColumnCell
                .getFormColumnCellValueByName( FormMultiviewFormResponseDateCreationNameConstants.COLUMN_FORM_RESPONSE_DATE_CREATION );
        assertThat( objFormResponseDateCreationResult, is( not( nullValue( ) ) ) );
        assertThat( (Date) objFormResponseDateCreationResult, is( dateFormResponseDateCreationValueToRetrieve ) );
    }

    /**
     * Test for the {@link IFormColumnQueryPart#getFormColumnCell(fr.paris.lutece.util.sql.DAOUtil)} using a column that doesn't exist
     */
    public void testGetFormColumnCellFormResponseDateCreationWithWrongColumnName( )
    {
        String strFormResponseDateCreationValueToRetrieve = "creation date";
        DAOUtil daoUtil = new DAOUtilMock( StringUtils.EMPTY, "colonne", strFormResponseDateCreationValueToRetrieve );

        IFormColumn formColumn = new FormColumnFormResponseDateCreation( 1, "Form Response Date Creation" );
        FormColumnFormResponseDateCreationQueryPart formColumnFormResponseDateCreationQueryPart = new FormColumnFormResponseDateCreationQueryPart( );
        formColumnFormResponseDateCreationQueryPart.setFormColumn( formColumn );

        try
        {
            formColumnFormResponseDateCreationQueryPart.getFormColumnCell( daoUtil );
            fail( "Test fail : AppException hasn't been thrown !" );
        }
        catch( AppException exception )
        {

        }
    }
}
