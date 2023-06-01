package fr.paris.lutece.plugins.forms.export.pdf;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
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
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.form.FormItemSortConfig;
import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.filter.FormFilter;
import fr.paris.lutece.plugins.forms.business.form.panel.FormPanel;
import fr.paris.lutece.plugins.forms.export.AbstractFileGenerator;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeCamera;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeCheckBox;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeFile;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeImage;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeRadioButton;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeSelect;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeTermsOfService;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeComment;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.html2pdf.service.PdfConverterService;
import fr.paris.lutece.plugins.html2pdf.service.PdfConverterServiceException;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.IFileStoreServiceProvider;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.html.HtmlTemplate;

public abstract class AbstractPdfFileGenerator extends AbstractFileGenerator {
	
	protected static final String CONSTANT_MIME_TYPE_PDF = "application/pdf";
	protected static final String EXTENSION_PDF = ".pdf";
	protected static final String LOG_ERROR_PDF_EXPORT_GENERATION = "Error during PDF export generation";
	protected static final String DEFAULT_TEMPLATE = "admin/plugins/forms/pdf/form_response_summary.html";
	
	private static final String KEY_LABEL_YES = "portal.util.labelYes";
    private static final String KEY_LABEL_NO = "portal.util.labelNo";
    
    private static final String MARK_STEP_FORMS = "list_export_step_display";
	
	protected AbstractPdfFileGenerator(String fileName, FormPanel formPanel, List<IFormColumn> listFormColumn,
			List<FormFilter> listFormFilter, FormItemSortConfig sortConfig, String fileDescription) {
		super(fileName, formPanel, listFormColumn, listFormFilter, sortConfig, fileDescription);
	}

	protected HtmlTemplate generateHtmlFromDefaultTemplate(Map<String, Object> model, FormResponse formResponse) throws Exception
	{
        model.put( MARK_STEP_FORMS, getPdfCellValueFormsReponse( formResponse ) );
        return AppTemplateService.getTemplate( DEFAULT_TEMPLATE, Locale.getDefault( ), model );
	}

	protected HtmlTemplate generateHtmlMultipleFormResponsesFromDefaultTemplate(Map<String, Object> model, List<FormResponse> listFormResponse) throws Exception
	{
		List<PdfCell> listPdfCell = new ArrayList<>();
		for (FormResponse formResponse : listFormResponse)
		{
			listPdfCell.addAll(getPdfCellValueFormsReponse( formResponse ));
		}
        model.put( MARK_STEP_FORMS, listPdfCell );
        return AppTemplateService.getTemplate( DEFAULT_TEMPLATE, Locale.getDefault( ), model );
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
            listContent.addAll( createCellsForStep( formResponseStep ) );
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
    private List<PdfCell> createCellsForStep( FormResponseStep formResponseStep )
    {
        Step step = formResponseStep.getStep( );

        List<FormDisplay> listStepFormDisplay = FormDisplayHome.getFormDisplayListByParent( step.getId( ), 0 );

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
    
    /**
     * Creates the cells for group.
     *
     * @param formResponseStep
     *            the form response step
     * @param formDisplay
     *            the form display
     * @return the list
     */
    private List<PdfCell> createCellsForGroup( FormResponseStep formResponseStep, FormDisplay formDisplay )
    {
        List<PdfCell> listContent = new ArrayList<>( );
        Group group = GroupHome.findByPrimaryKey( formDisplay.getCompositeId( ) );

        List<FormDisplay> listGroupDisplay = FormDisplayHome.getFormDisplayListByParent( formResponseStep.getStep( ).getId( ), formDisplay.getId( ) );
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
        return createPdfCell( formResponseStep, formDisplay, null, 0 );
    }

    /**
     * Creates the pdf cell.
     *
     * @param formResponseStep
     *            the form response step
     * @param formDisplay
     *            the form display
     * @param group
     *            the group
     * @param iterationNumber
     *            the iteration number
     * @return the pdf cell
     */
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

            if ( entryTypeService instanceof EntryTypeCheckBox && strResponseValue != null )
            {
                listResponseValue.add( "<input type=\"checkbox\" name=\"" + strResponseValue + " \" checked=\"checked\" />" );
            }

            if ( ( entryTypeService instanceof EntryTypeImage || entryTypeService instanceof EntryTypeCamera ) && strResponseValue != null )
            {

                if ( response.getFile( ) != null )
                {
                    IFileStoreServiceProvider fileStoreService = FileService.getInstance( ).getFileStoreServiceProvider( );
                    fr.paris.lutece.portal.business.file.File fileImage = fileStoreService.getFile( String.valueOf( response.getFile( ).getIdFile( ) ) );
                    
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
                IFileStoreServiceProvider fileStoreService = FileService.getInstance( ).getFileStoreServiceProvider( );
                File fileImage = fileStoreService.getFile( String.valueOf( response.getFile( ).getIdFile( ) ) );
                listResponseValue.add( fileImage != null ? fileImage.getTitle( ) : StringUtils.EMPTY);
            }

            if ( entryTypeService instanceof EntryTypeRadioButton && strResponseValue != null )
            {
                listResponseValue.add( "<input type=\"radio\" value=\"" + strResponseValue + " \" name=\"" + strResponseValue + " \" checked=\"checked\" />" );
            }

            if ( entryTypeService instanceof EntryTypeSelect && strResponseValue != null )
            {
                listResponseValue.add( "<select name=\"" + strResponseValue + " \" > <option>" + strResponseValue + "</option> </select>" );
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

            PdfConverterService.getInstance( ).getPdfBuilder( ).reset( ).withHtmlContent( doc.html( ) ).notEditable( ).render( outputStream );
        }
        catch(PdfConverterServiceException | IOException e)
        {
            AppLogService.error( "Error generating pdf for file " + fileName, e );
            throw e;
        }
    }

}
