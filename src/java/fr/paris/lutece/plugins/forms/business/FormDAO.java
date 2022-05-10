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

import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Form objects
 */
public final class FormDAO implements IFormDAO
{
    // Constants
    private static final String SQL_QUERY_SELECTALL = "SELECT id_form, title, description, creation_date,update_date, availability_start_date, availability_end_date, workgroup, id_workflow, authentification_needed, one_response_by_user, breadcrumb_name, display_summary, return_url, max_number_response,captcha_step_initial,captcha_step_final,captcha_recap,count_responses,label_final_button,unavailable_message, id_logo, id_category,backup_enabled FROM forms_form";
    private static final String SQL_QUERY_SELECT = SQL_QUERY_SELECTALL + " WHERE id_form = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO forms_form ( title, description, update_date, availability_start_date, availability_end_date, workgroup, id_workflow, authentification_needed, one_response_by_user, breadcrumb_name, display_summary, return_url, max_number_response, captcha_step_initial, captcha_step_final, captcha_recap,count_responses, label_final_button,unavailable_message, id_logo, id_category, backup_enabled ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM forms_form WHERE id_form = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE forms_form SET id_form = ?, title = ?, description = ?, update_date = ?, availability_start_date = ?, availability_end_date = ?, workgroup = ?, id_workflow = ?, authentification_needed = ?, one_response_by_user = ?, breadcrumb_name = ?, display_summary = ?, return_url = ?, max_number_response = ?, captcha_step_initial = ?, captcha_step_final = ?, captcha_recap = ?, count_responses = ?, label_final_button = ?, unavailable_message = ?, id_logo = ? , id_category = ?, backup_enabled = ? WHERE id_form = ?";
    private static final String SQL_QUERY_COUNT_NUMBER_OF_RESPONSE = "SELECT count(id_form) FROM forms_response WHERE id_form = ? and from_save = 0";
    private static final String SQL_QUERY_COUNT_NUMBER_RESPONSE_USER = "SELECT count(id_form) FROM forms_response WHERE id_form=? and guid= ? AND from_save = 0 ";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Form form, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++, form.getTitle( ) );
            daoUtil.setString( nIndex++, form.getDescription( ) );
            Timestamp tsUpdateDate = new Timestamp( Calendar.getInstance( ).getTimeInMillis( ) );
            daoUtil.setTimestamp( nIndex++, tsUpdateDate );
            daoUtil.setTimestamp( nIndex++, form.getAvailabilityStartDate( ) );
            daoUtil.setTimestamp( nIndex++, form.getAvailabilityEndDate( ) );
            daoUtil.setString( nIndex++, form.getWorkgroup( ) );
            daoUtil.setInt( nIndex++, form.getIdWorkflow( ) );
            daoUtil.setBoolean( nIndex++, form.isAuthentificationNeeded( ) );
            daoUtil.setBoolean( nIndex++, form.isOneResponseByUser( ) );
            daoUtil.setString( nIndex++, form.getBreadcrumbName( ) );
            daoUtil.setBoolean( nIndex++, form.isDisplaySummary( ) );
            daoUtil.setString( nIndex++, form.getReturnUrl( ) );
            daoUtil.setInt( nIndex++, form.getMaxNumberResponse( ) );
            daoUtil.setBoolean( nIndex++, form.isCaptchaStepInitial( ) );
            daoUtil.setBoolean( nIndex++, form.isCaptchaStepFinal( ) );
            daoUtil.setBoolean( nIndex++, form.isCaptchaRecap( ) );
            daoUtil.setBoolean( nIndex++, form.isCountResponses( ) );
            daoUtil.setString( nIndex++, form.getLabelFinalButton( ) );
            daoUtil.setString( nIndex++, form.getUnavailableMessage( ) );
            if ( form.getLogo( ) != null )
            {
                daoUtil.setInt( nIndex++, form.getLogo( ).getIdFile( ) );
            }
            else
            {
                daoUtil.setInt( nIndex++, 0 );
            }
            daoUtil.setInt( nIndex++, form.getIdCategory( ) );
            daoUtil.setBoolean( nIndex++, form.isBackupEnabled( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                form.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Form load( int nKey, Plugin plugin )
    {
        Form form = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                form = dataToObject( daoUtil );
            }
        }
        return form;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nKey, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( Form form, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 1;

            daoUtil.setInt( nIndex++, form.getId( ) );
            daoUtil.setString( nIndex++, form.getTitle( ) );
            daoUtil.setString( nIndex++, form.getDescription( ) );
            Timestamp tsUpdateDate = new Timestamp( Calendar.getInstance( ).getTimeInMillis( ) );
            daoUtil.setTimestamp( nIndex++, tsUpdateDate );
            daoUtil.setTimestamp( nIndex++, form.getAvailabilityStartDate( ) );
            daoUtil.setTimestamp( nIndex++, form.getAvailabilityEndDate( ) );
            daoUtil.setString( nIndex++, form.getWorkgroup( ) );
            daoUtil.setInt( nIndex++, form.getIdWorkflow( ) );
            daoUtil.setBoolean( nIndex++, form.isAuthentificationNeeded( ) );
            daoUtil.setBoolean( nIndex++, form.isOneResponseByUser( ) );
            daoUtil.setString( nIndex++, form.getBreadcrumbName( ) );
            daoUtil.setBoolean( nIndex++, form.isDisplaySummary( ) );
            daoUtil.setString( nIndex++, form.getReturnUrl( ) );
            daoUtil.setInt( nIndex++, form.getMaxNumberResponse( ) );
            daoUtil.setBoolean( nIndex++, form.isCaptchaStepInitial( ) );
            daoUtil.setBoolean( nIndex++, form.isCaptchaStepFinal( ) );
            daoUtil.setBoolean( nIndex++, form.isCaptchaRecap( ) );
            daoUtil.setBoolean( nIndex++, form.isCountResponses( ) );
            daoUtil.setString( nIndex++, form.getLabelFinalButton( ) );
            daoUtil.setString( nIndex++, form.getUnavailableMessage( ) );
            if ( form.getLogo( ) != null )
            {
                daoUtil.setInt( nIndex++, form.getLogo( ).getIdFile( ) );
            }
            else
            {
                daoUtil.setInt( nIndex++, 0 );
            }
            daoUtil.setInt( nIndex++, form.getIdCategory( ) );
            daoUtil.setBoolean( nIndex++, form.isBackupEnabled( ) );
            
            daoUtil.setInt( nIndex, form.getId( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Form> selectFormsList( Plugin plugin )
    {
        List<Form> formList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                formList.add( dataToObject( daoUtil ) );
            }
        }
        return formList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectFormsReferenceList( Plugin plugin )
    {
        ReferenceList formList = new ReferenceList( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                formList.addItem( daoUtil.getInt( "id_form" ), daoUtil.getString( "title" ) );
            }
        }
        return formList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int countNumberOfResponseForms( int nIdForm )
    {
        int nCount = 0;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_COUNT_NUMBER_OF_RESPONSE ) )
        {
            daoUtil.setInt( 1, nIdForm );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                nCount = daoUtil.getInt( 1 );
            }
        }
        return nCount;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int countNumberOfResponseFormByUser( int nIdForm, String strGuid )
    {
        int nCount = 0;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_COUNT_NUMBER_RESPONSE_USER ) )
        {
            daoUtil.setInt( 1, nIdForm );
            daoUtil.setString( 2, strGuid );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                nCount = daoUtil.getInt( 1 );
            }
        }
        return nCount;
    }

    /**
     * 
     * @param daoUtil
     *            The daoutil
     * @return The populated Form object
     *
     */
    private Form dataToObject( DAOUtil daoUtil )
    {
        Form form = new Form( );

        form.setId( daoUtil.getInt( "id_form" ) );
        form.setTitle( daoUtil.getString( "title" ) );
        form.setDescription( daoUtil.getString( "description" ) );
        form.setCreationDate( daoUtil.getTimestamp( "creation_date" ) );
        form.setUpdateDate( daoUtil.getTimestamp( "update_date" ) );
        form.setAvailabilityStartDate( daoUtil.getTimestamp( "availability_start_date" ) );
        form.setAvailabilityEndDate( daoUtil.getTimestamp( "availability_end_date" ) );
        form.setWorkgroup( daoUtil.getString( "workgroup" ) );
        form.setIdWorkflow( daoUtil.getInt( "id_workflow" ) );
        form.setAuthentificationNeeded( daoUtil.getBoolean( "authentification_needed" ) );
        form.setOneResponseByUser( daoUtil.getBoolean( "one_response_by_user" ) );
        form.setBreadcrumbName( daoUtil.getString( "breadcrumb_name" ) );
        form.setDisplaySummary( daoUtil.getBoolean( "display_summary" ) );
        form.setReturnUrl( daoUtil.getString( "return_url" ) );
        form.setMaxNumberResponse( daoUtil.getInt( "max_number_response" ) );
        form.setCaptchaStepInitial( daoUtil.getBoolean( "captcha_step_initial" ) );
        form.setCaptchaStepFinal( daoUtil.getBoolean( "captcha_step_final" ) );
        form.setCaptchaRecap( daoUtil.getBoolean( "captcha_recap" ) );
        form.setCountResponses( daoUtil.getBoolean( "count_responses" ) );
        form.setLabelFinalButton( daoUtil.getString( "label_final_button" ) );
        form.setUnavailableMessage( daoUtil.getString( "unavailable_message" ) );

        int idLogo = daoUtil.getInt( "id_logo" );
        if ( idLogo > 0 )
        {
            File logo = new File( );
            logo.setIdFile( idLogo );
            form.setLogo( logo );
        }

        form.setIdCategory( daoUtil.getInt( "id_category" ) );
        form.setBackupEnabled( daoUtil.getBoolean( "backup_enabled" ) );
        return form;
    }

}
