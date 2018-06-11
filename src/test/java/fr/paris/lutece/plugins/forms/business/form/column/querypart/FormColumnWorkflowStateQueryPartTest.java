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

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.form.column.FormColumnCell;
import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.column.impl.FormColumnWorkflowState;
import fr.paris.lutece.plugins.forms.business.form.column.querypart.impl.FormColumnWorkflowStateQueryPart;
import fr.paris.lutece.plugins.forms.business.form.column.querypart.mock.DAOUtilMock;
import fr.paris.lutece.plugins.forms.util.FormMultiviewWorkflowStateNameConstants;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * Test of the implementation of the IFormColumnQueryPart for the Workflow State column
 */
public class FormColumnWorkflowStateQueryPartTest extends LuteceTestCase
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
    public void testgetFormColumnCellWorkflowState( )
    {
        String strWorkflowStateValueToRetrieve = "workflow state";
        DAOUtil daoUtil = new DAOUtilMock( StringUtils.EMPTY, FormMultiviewWorkflowStateNameConstants.COLUMN_WORKFLOW_STATE_NAME, strWorkflowStateValueToRetrieve );

        IFormColumn formColumn = new FormColumnWorkflowState( 1, "Workflow State" );
        FormColumnWorkflowStateQueryPart formColumnWorkflowStateQueryPart = new FormColumnWorkflowStateQueryPart( );
        formColumnWorkflowStateQueryPart.setFormColumn( formColumn );

        FormColumnCell formColumnCell = formColumnWorkflowStateQueryPart.getFormColumnCell( daoUtil );
        assertThat( formColumnCell, is( not( nullValue( ) ) ) );

        Map<String, Object> mapFormColumnCellValues = formColumnCell.getFormColumnCellValues( );
        assertThat( mapFormColumnCellValues, is( not( nullValue( ) ) ) );
        assertThat( mapFormColumnCellValues.size( ), is( 1 ) );

        Object objFormsResult = formColumnCell.getFormColumnCellValueByName( FormMultiviewWorkflowStateNameConstants.COLUMN_WORKFLOW_STATE_NAME );
        assertThat( objFormsResult, is( not( nullValue( ) ) ) );
        assertThat( String.valueOf( objFormsResult ), is( strWorkflowStateValueToRetrieve ) );
    }

    /**
     * Test for the {@link IFormColumnQueryPart#getFormColumnCell(fr.paris.lutece.util.sql.DAOUtil)} using a column that doesn't exist
     */
    public void testgetFormColumnCellWorkflowStateWithWrongColumnName( )
    {
        String strWorkflowStateValueToRetrieve = "workflow state";
        DAOUtil daoUtil = new DAOUtilMock( StringUtils.EMPTY, "colonne", strWorkflowStateValueToRetrieve );

        IFormColumn formColumn = new FormColumnWorkflowState( 1, "Workflow State" );
        FormColumnWorkflowStateQueryPart formColumnWorkflowStateQueryPart = new FormColumnWorkflowStateQueryPart( );
        formColumnWorkflowStateQueryPart.setFormColumn( formColumn );

        try
        {
            formColumnWorkflowStateQueryPart.getFormColumnCell( daoUtil );
            fail( "Test fail : AppException hasn't been thrown !" );
        }
        catch( AppException exception )
        {

        }
    }
}
