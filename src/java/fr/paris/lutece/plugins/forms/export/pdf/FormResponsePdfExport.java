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
package fr.paris.lutece.plugins.forms.export.pdf;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseStep;
import fr.paris.lutece.plugins.forms.business.Group;
import fr.paris.lutece.plugins.forms.business.GroupHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeTermsOfService;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeComment;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppLogService;

@SuppressWarnings("unused")
@Deprecated
public class FormResponsePdfExport
{
    private static final String KEY_LABEL_YES = "portal.util.labelYes";
    private static final String KEY_LABEL_NO = "portal.util.labelNo";

    public void buildPdfExport( FormResponse formResponse, OutputStream outputStream )
    {
        try ( PDDocument pdDocument = new PDDocument( ) )
        {
            Form form = FormHome.findByPrimaryKey( formResponse.getFormId( ) );

            // Filters the FormResponseStep with at least one question exportable in pdf
            List<FormResponseStep> filteredList = formResponse.getSteps( ).stream( ).filter(
                    frs -> frs.getQuestions( ).stream( ).map( FormQuestionResponse::getQuestion ).map( Question::getEntry ).anyMatch( Entry::isExportablePdf ) )
                    .collect( Collectors.toList( ) );
            for ( FormResponseStep formResponseStep : filteredList )
            {
                PDPage pdPage = new PDPage( );
                PDPageContentStream contentStream = new PDPageContentStream( pdDocument, pdPage );

                PdfUtil.addCenteredText( pdPage, contentStream, PDType1Font.HELVETICA_BOLD, 16, 30, form.getTitle( ) );
                createStepTable( pdPage, contentStream, formResponseStep );

                pdDocument.addPage( pdPage );
            }
            pdDocument.save( outputStream );
        }
        catch( IOException e )
        {
            AppLogService.error( "Error generating pdf for response " + formResponse.getId( ), e );
        }
    }

    private void createStepTable( PDPage page, PDPageContentStream contentStream, FormResponseStep formResponseStep )
    {
        try
        {
            PdfUtil.addCenteredText( page, contentStream, PDType1Font.HELVETICA, 14, 60, formResponseStep.getStep( ).getTitle( ) );
            List<PdfCell> listContent = createCellsForStep( formResponseStep );
            PdfUtil.drawTable( page, contentStream, 700, 10, listContent );
            contentStream.close( );
        }
        catch( IOException e )
        {
            AppLogService.error( "Error generating pdf for step " + formResponseStep.getId( ) + " of response " + formResponseStep.getFormResponseId( ), e );
        }
    }

    private List<PdfCell> createCellsForStep( FormResponseStep formResponseStep )
    {
        Step step = formResponseStep.getStep( );

        List<FormDisplay> listStepFormDisplay = FormDisplayHome.getFormDisplayListByParentOrderByQuestionExportDisplayOrder( step.getId( ), 0 );

        List<PdfCell> listContent = new ArrayList<>( );
        for ( FormDisplay formDisplay : listStepFormDisplay )
        {
            if ( CompositeDisplayType.GROUP.getLabel( ).equals( formDisplay.getCompositeType( ) ) )
            {
                List<PdfCell> listContentGroup = createCellsForGroup( formResponseStep, formDisplay );
                listContent.addAll( listContentGroup );
            }
            else
            {
                PdfCell cell = createPdfCellNoGroup( formResponseStep, formDisplay );
                if ( cell != null )
                {
                    listContent.add( cell );
                }
            }
        }
        return listContent;
    }

    private List<PdfCell> createCellsForGroup( FormResponseStep formResponseStep, FormDisplay formDisplay )
    {
        List<PdfCell> listContent = new ArrayList<>( );
        Group group = GroupHome.findByPrimaryKey( formDisplay.getCompositeId( ) );

        List<FormDisplay> listGroupDisplay = FormDisplayHome.getFormDisplayListByParentOrderByQuestionExportDisplayOrder( formResponseStep.getStep( ).getId( ), formDisplay.getId( ) );
        for ( int ite = 0; ite < group.getIterationMax( ); ite++ )
        {
            for ( FormDisplay formDisplayGroup : listGroupDisplay )
            {
                PdfCell cell = createPdfCell( formResponseStep, formDisplayGroup, group, ite );
                if ( cell != null )
                {
                    listContent.add( cell );
                }
            }
        }
        return listContent;
    }

    private PdfCell createPdfCellNoGroup( FormResponseStep formResponseStep, FormDisplay formDisplay )
    {
        return createPdfCell( formResponseStep, formDisplay, null, 0 );
    }

    private PdfCell createPdfCell( FormResponseStep formResponseStep, FormDisplay formDisplay, Group group, int iterationNumber )
    {
        FormQuestionResponse formQuestionResponse = formResponseStep.getQuestions( ).stream( )
                .filter( fqr -> fqr.getQuestion( ).getEntry( ).isExportablePdf( ) )
                .filter( fqr -> fqr.getQuestion( ).getId( ) == formDisplay.getCompositeId( ) )
                .filter( fqr -> fqr.getQuestion( ).getIterationNumber( ) == iterationNumber ).findFirst( ).orElse( null );

        if ( formQuestionResponse != null )
        {
            String key = formQuestionResponse.getQuestion( ).getTitle( );
            List<String> listResponseValue = getResponseValue( formQuestionResponse, iterationNumber );
            if ( CollectionUtils.isNotEmpty( listResponseValue ) )
            {
                PdfCell cell = new PdfCell( );
                cell.setTitle( key );
                cell.setValue( listResponseValue.stream( ).filter( StringUtils::isNotEmpty ).collect( Collectors.joining( ";" ) ) );
                if ( group != null )
                {
                    String groupName = group.getTitle( );
                    if ( group.getIterationMax( ) > 1 )
                    {
                        int iteration = iterationNumber + 1;
                        groupName += " (" + iteration + ")";
                    }
                    cell.setGroup( groupName );
                }
                return cell;
            }
        }
        return null;
    }

    private List<String> getResponseValue( FormQuestionResponse formQuestionResponse, int iteration )
    {
        Entry entry = formQuestionResponse.getQuestion( ).getEntry( );

        IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( entry );
        List<String> listResponseValue = new ArrayList<>( );
        if ( entryTypeService instanceof AbstractEntryTypeComment )
        {
            return listResponseValue;
        }
        if ( entryTypeService instanceof EntryTypeTermsOfService )
        {
            boolean aggrement = formQuestionResponse.getEntryResponse( ).stream( )
                    .filter( response -> response.getField( ).getCode( ).equals( EntryTypeTermsOfService.FIELD_AGREEMENT_CODE ) )
                    .map( Response::getResponseValue ).map( Boolean::valueOf ).findFirst( ).orElse( false );

            if ( aggrement )
            {
                listResponseValue.add( I18nService.getLocalizedString( KEY_LABEL_YES, I18nService.getDefaultLocale( ) ) );
            }
            else
            {
                listResponseValue.add( I18nService.getLocalizedString( KEY_LABEL_NO, I18nService.getDefaultLocale( ) ) );
            }
            return listResponseValue;

        }
        for ( Response response : formQuestionResponse.getEntryResponse( ) )
        {
            if ( response.getIterationNumber( ) != -1 && response.getIterationNumber( ) != iteration )
            {
                continue;
            }
            if ( response.getFile( ) != null )
            {
                response.setFile( FileHome.findByPrimaryKey( response.getFile( ).getIdFile( ) ) );
            }
            String strResponseValue = entryTypeService.getResponseValueForRecap( entry, null, response, I18nService.getDefaultLocale( ) );

            if ( strResponseValue != null )
            {
                listResponseValue.add( strResponseValue );
            }
        }

        return listResponseValue;
    }
}
