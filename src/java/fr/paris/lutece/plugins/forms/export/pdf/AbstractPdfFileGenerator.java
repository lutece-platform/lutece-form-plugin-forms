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
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities.EscapeMode;
import fr.paris.lutece.plugins.forms.business.CompositeDisplayType;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseStep;
import fr.paris.lutece.plugins.forms.business.Group;
import fr.paris.lutece.plugins.forms.business.GroupHome;
import fr.paris.lutece.plugins.forms.business.MultiviewConfig;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.form.FormItemSortConfig;
import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.filter.FormFilter;
import fr.paris.lutece.plugins.forms.business.form.panel.FormPanel;
import fr.paris.lutece.plugins.forms.export.AbstractFileGenerator;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeCamera;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeFile;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeImage;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeTermsOfService;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeComment;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.html2pdf.service.PdfConverterService;
import fr.paris.lutece.plugins.html2pdf.service.PdfConverterServiceException;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFileHome;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.FileServiceException;
import fr.paris.lutece.portal.service.file.IFileStoreServiceProvider;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.util.html.HtmlTemplate;
import jakarta.enterprise.inject.spi.CDI;


public abstract class AbstractPdfFileGenerator extends AbstractFileGenerator {
	
	protected static final String CONSTANT_MIME_TYPE_PDF = "application/pdf";
	protected static final String EXTENSION_PDF = ".pdf";
	protected static final String LOG_ERROR_PDF_EXPORT_GENERATION = "Error during PDF export generation";
	protected static final String DEFAULT_TEMPLATE = "admin/plugins/forms/pdf/default_template_export_pdf.html";
	
	private static final String KEY_LABEL_YES = "portal.util.labelYes";
    private static final String KEY_LABEL_NO = "portal.util.labelNo";
    
    // markers
    private static final String MARK_PDF_CELL_LIST = "list_pdf_cell";
    private static final String MARK_FORM_TITLE = "form_title";
    
    protected final String _formTitle;
	
	protected AbstractPdfFileGenerator(String fileName, String formTitle, FormPanel formPanel, List<IFormColumn> listFormColumn,
			List<FormFilter> listFormFilter, FormItemSortConfig sortConfig, String fileDescription) {
		super(fileName, formPanel, listFormColumn, listFormFilter, sortConfig, fileDescription);
		_formTitle = formTitle;
	}
	
	protected HtmlTemplate generateHtmlSingleFormResponsesFromTemplate(Map<String, Object> model, FormResponse formResponse) throws Exception
	{
		model.put( MARK_FORM_TITLE, _formTitle);
		List<PdfCell> listPdfCell = new ArrayList<>();
		listPdfCell.addAll(generateFirstFormResponseInfos(formResponse));
		listPdfCell.addAll(getPdfCellValueFormsReponse( formResponse ));
		model.put( MARK_PDF_CELL_LIST, listPdfCell );
		return getTemplateExportPDF(model);
	}

	protected HtmlTemplate generateHtmlMultipleFormResponsesFromTemplate(Map<String, Object> model, List<FormResponse> listFormResponse) throws Exception
	{
		model.put( MARK_FORM_TITLE, _formTitle);
		List<PdfCell> listPdfCell = new ArrayList<>();
		for (FormResponse formResponse : listFormResponse)
		{
			listPdfCell.addAll(generateFirstFormResponseInfos(formResponse));
			listPdfCell.addAll(getPdfCellValueFormsReponse( formResponse ));
		}
		model.put( MARK_PDF_CELL_LIST, listPdfCell );
		return getTemplateExportPDF(model);
	}
	
	private HtmlTemplate getTemplateExportPDF(Map<String, Object> model)
	{
		File templateFile = FileHome.findByPrimaryKey(MultiviewConfig.getInstance().getIdFileTemplatePdf());
		if (templateFile != null && templateFile.getPhysicalFile() != null)
		{
			PhysicalFile physicalTemplateFile = PhysicalFileHome.findByPrimaryKey(templateFile.getPhysicalFile().getIdPhysicalFile());
			if (physicalTemplateFile != null)
			{
				String strTemplateContent = new String(physicalTemplateFile.getValue());
				return AppTemplateService.getTemplateFromStringFtl(strTemplateContent, Locale.getDefault(), model);
			}
		}
		return AppTemplateService.getTemplate(DEFAULT_TEMPLATE, Locale.getDefault(), model);
	}

	private List<PdfCell> generateFirstFormResponseInfos(FormResponse formResponse)
	{
		List<PdfCell> pdfCells = new ArrayList<>();
		
		// form response number
		PdfCell formResponseNumberCell = new PdfCell();
		formResponseNumberCell.setFormResponseNumber(formResponse.getId());
		pdfCells.add(formResponseNumberCell);
		
		// form response update date
		PdfCell formResponseDateCell = new PdfCell();
		formResponseDateCell.setFormResponseDate(formatDateFormResponse(formResponse));
		pdfCells.add(formResponseDateCell);

		return pdfCells;
	}

	private String formatDateFormResponse(FormResponse formResponse)
	{
		if (formResponse != null && formResponse.getUpdate() != null)
		{
			DateFormat dateFormat = new SimpleDateFormat( AppPropertiesService.getProperty( FormsConstants.PROPERTY_EXPORT_FORM_DATE_CREATION_FORMAT ), Locale.getDefault( ) );
			return dateFormat.format( formResponse.getUpdate( ) );
		}
		return null;
		
	}
	
	/**
     * Gets the pdf cell value forms reponse.
     *
     * @param formresponse
     *            the formresponse
     * @return the pdf cell value forms reponse
     */
    private List<PdfCell> getPdfCellValueFormsReponse( FormResponse formresponse )
    {
        List<PdfCell> listContent = new ArrayList<>( );

        // Filters the FormResponseStep with at least one question exportable in pdf
        List<FormResponseStep> filteredList = formresponse.getSteps( ).stream( ).filter(
                frs -> frs.getQuestions( ).stream( ).map( FormQuestionResponse::getQuestion ).map( Question::getEntry ).anyMatch( Entry::isExportablePdf ) )
                .collect( Collectors.toList( ) );
        for ( FormResponseStep formResponseStep : filteredList )
        {
            listContent.addAll( createCellsForStep( formResponseStep, formresponse ) );
        }

        return listContent;
    }
    
    /**
     * Creates the cells for step.
     *
     * @param formResponseStep
     *            the form response step
     * @return the list
     */
    private List<PdfCell> createCellsForStep( FormResponseStep formResponseStep, FormResponse formResponse  )
    {
        Step step = formResponseStep.getStep( );

        List<FormDisplay> listStepFormDisplay = FormDisplayHome.getFormDisplayListByParent( step.getId( ), 0 );

        List<PdfCell> listContent = new ArrayList<>( );
        
        // add step title
        PdfCell stepCell = new PdfCell( );
        stepCell.setStep(step.getTitle());
        listContent.add(stepCell);
        
        for ( FormDisplay formDisplay : listStepFormDisplay )
        {
            if ( CompositeDisplayType.GROUP.getLabel( ).equals( formDisplay.getCompositeType( ) ) )
            {
                List<PdfCell> listContentGroup = createCellsForGroup( formResponseStep, formDisplay, formResponse );
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
    
    /**
     * Creates the cells for group.
     *
     * @param formResponseStep
     *            the form response step
     * @param formDisplay
     *            the form display
     * @return the list
     */
    private List<PdfCell> createCellsForGroup( FormResponseStep formResponseStep, FormDisplay formDisplay, FormResponse formrResponse )
    {
        List<PdfCell> listContent = new ArrayList<>( );
        
        Group group = GroupHome.findByPrimaryKey( formDisplay.getCompositeId( ) );
        String groupName = group.getTitle( );

        List<FormDisplay> listGroupDisplay = FormDisplayHome.getFormDisplayListByParent( formResponseStep.getStep( ).getId( ), formDisplay.getId( ) );

        List<FormQuestionResponse> listFormQuestionResponse = FormQuestionResponseHome.getFormQuestionResponseListByFormResponse(formrResponse.getId());
   if(listFormQuestionResponse != null && !listFormQuestionResponse.isEmpty()) {
       for (int ite = 0; ite < listFormQuestionResponse.size(); ite++) {
           int iteration = ite + 1;
           String groupePlusIte = groupName + " (" + iteration + ")";
           PdfCell groupCell = new PdfCell();
           groupCell.setGroup(groupePlusIte);
           // if the group is not displayed yet and the group has responses, we add it to the list of cells the group is displayed only once
           boolean isGroupIterationDisplayed = false;
           for (int i = 0; i < listGroupDisplay.size(); i++) {
               PdfCell cell = createPdfCell(formResponseStep, listGroupDisplay.get(i), ite);
               if (cell != null) {
                   if (!isGroupIterationDisplayed) {
                       listContent.add(groupCell);
                       isGroupIterationDisplayed = true;
                   }
                   listContent.add(cell);
               }
           }
       }
   }
        return listContent;
    }

    /**
     * Creates the pdf cell no group.
     *
     * @param formResponseStep
     *            the form response step
     * @param formDisplay
     *            the form display
     * @return the pdf cell
     */
    private PdfCell createPdfCellNoGroup( FormResponseStep formResponseStep, FormDisplay formDisplay )
    {
        return createPdfCell( formResponseStep, formDisplay, 0 );
    }

    /**
     * Creates the pdf cell.
     *
     * @param formResponseStep
     *            the form response step
     * @param formDisplay
     *            the form display
     * @param iterationNumber
     *            the iteration number
     * @return the pdf cell
     */
    private PdfCell createPdfCell( FormResponseStep formResponseStep, FormDisplay formDisplay, int iterationNumber )
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
                return cell;
            }
        }
        return null;
    }
    
    /**
     * Gets the response value.
     *
     * @param formQuestionResponse
     *            the form question response
     * @param iteration
     *            the iteration
     * @return the response value
     */
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

            String strResponseValue = entryTypeService.getResponseValueForRecap( entry, null, response, I18nService.getDefaultLocale( ) );

            if ( ( entryTypeService instanceof EntryTypeImage || entryTypeService instanceof EntryTypeCamera ) && strResponseValue != null )
            {
                if ( response.getFile( ) != null )
                {
                    IFileStoreServiceProvider fileStoreService = CDI.current( ).select( FileService.class ).get( ).getFileStoreServiceProvider( );
                    fr.paris.lutece.portal.business.file.File fileImage = null;
					try {
						fileImage = fileStoreService.getFile( response.getFile( ).getFileKey( ) );
					} catch (FileServiceException e) {
						AppLogService.error(e);
					}
                    
                    String encoded = StringUtils.EMPTY;
                    if(fileImage != null && fileImage.getPhysicalFile() != null)
                    {
                    	encoded = Base64.getEncoder( ).encodeToString( fileImage.getPhysicalFile( ).getValue( ) );
                    }
                    listResponseValue.add( "<img src=\"data:image/jpeg;base64, " + encoded + " \" width=\"100px\" height=\"100px\" /> " );
                }
            }
            if ( entryTypeService instanceof EntryTypeFile && response.getFile( ) != null )
            {
                IFileStoreServiceProvider fileStoreService = CDI.current( ).select( FileService.class ).get( ).getFileStoreServiceProvider( );
                File fileImage = null;
				try {
					fileImage = fileStoreService.getFile( response.getFile( ).getFileKey( ) );
				} catch (FileServiceException e) {
					AppLogService.error(e);
				}
                listResponseValue.add( fileImage != null ? fileImage.getTitle( ) : StringUtils.EMPTY);
            }

            if ( strResponseValue != null )
            {
                listResponseValue.add( strResponseValue );
            }
        }

        return listResponseValue;
    }
    
    protected void generatePdfFile(Path directoryFile, HtmlTemplate htmltemplate, String fileName) throws PdfConverterServiceException, IOException
    {
    	try ( OutputStream outputStream = Files.newOutputStream( directoryFile.resolve( fileName + EXTENSION_PDF ) ) )
        {
        	Document doc = Jsoup.parse( htmltemplate.getHtml( ), UTF_8 );
            doc.outputSettings( ).syntax( Document.OutputSettings.Syntax.xml );
            doc.outputSettings( ).escapeMode( EscapeMode.base.xhtml );
            doc.outputSettings( ).charset( UTF_8 );

            CDI.current( ).select( PdfConverterService.class ).get( ).getPdfBuilder( ).reset( ).withHtmlContent( doc.html( ) ).notEditable( ).render( outputStream );
        }
        catch(PdfConverterServiceException | IOException e)
        {
            AppLogService.error( "Error generating pdf for file " + fileName, e );
            throw e;
        }
    }

}
