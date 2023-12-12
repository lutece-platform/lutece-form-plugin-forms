package fr.paris.lutece.plugins.forms.export.pdf;

import fr.paris.lutece.plugins.forms.business.*;
import fr.paris.lutece.plugins.forms.business.form.FormItemSortConfig;
import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.filter.FormFilter;
import fr.paris.lutece.plugins.forms.business.form.panel.FormPanel;
import fr.paris.lutece.plugins.forms.export.AbstractFileGenerator;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeCamera;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeFile;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeImage;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeTermsOfService;
import fr.paris.lutece.plugins.forms.service.provider.GenericFormsProvider;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeComment;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.html2pdf.service.PdfConverterService;
import fr.paris.lutece.plugins.html2pdf.service.PdfConverterServiceException;
import fr.paris.lutece.plugins.workflowcore.service.provider.InfoMarker;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFileHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.html.HtmlTemplate;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public abstract class AbstractPdfFileGenerator extends AbstractFileGenerator {
	
	protected static final String CONSTANT_MIME_TYPE_PDF = "application/pdf";
	protected static final String EXTENSION_PDF = ".pdf";
	protected static final String LOG_ERROR_PDF_EXPORT_GENERATION = "Error during PDF export generation";
	private static final String KEY_LABEL_YES = "portal.util.labelYes";
    private static final String KEY_LABEL_NO = "portal.util.labelNo";

    private static final String LINK_MESSAGE_FO = "forms.export.pdf.label.link_FO";
    private static final String LINK_MESSAGE_BO = "forms.export.pdf.label.link_BO";
    private static final String PUBLISHED = "forms.export.pdf.published";
    private static final String NOT_PUBLISHED = "forms.export.pdf.unpublished";
    private static final String RESPONSE_CREATED = "forms.export.formResponse.form.date.creation";
    private static final String RESPONSE_UPDATED = "forms.export.formResponse.form.date.update";
    private static final String MESSAGE_EXPORT_FORM_TITLE = "forms.export.formResponse.form.title";
    private static final String MESSAGE_EXPORT_FORM_STATE = "forms.export.formResponse.form.state";

    private static final String RESPONSE_NUMBER = "response_number";
    
    protected final String _formTitle;
    protected HttpServletRequest _request;
    protected String _fileDescription;
    protected String _fileName;
    protected FormPanel _formPanel;
    protected List<IFormColumn> _listFormColumn;
    protected List<FormFilter> _listFormFilter;
    protected FormItemSortConfig _sortConfig;
    protected Form _form;

    protected AbstractPdfFileGenerator(String fileName, String formTitle, FormPanel formPanel, List<IFormColumn> listFormColumn,
			List<FormFilter> listFormFilter, FormItemSortConfig sortConfig, String fileDescription, HttpServletRequest request, Form form) {
		super(fileName, formPanel, listFormColumn, listFormFilter, sortConfig, fileDescription, request, form);
		_formTitle = formTitle;
        _request = request;
        _fileDescription = fileDescription;
        _fileName = fileName;
        _formPanel = formPanel;
        _listFormColumn = listFormColumn;
        _listFormFilter = listFormFilter;
        _sortConfig = sortConfig;
        _form = form;
	}
    protected String getTemplateExportPDF()
    {
        File templateFile = FileHome.findByPrimaryKey(MultiviewConfig.getInstance().getIdFileTemplatePdf());
        if (templateFile != null && templateFile.getPhysicalFile() != null)
        {
            PhysicalFile physicalTemplateFile = PhysicalFileHome.findByPrimaryKey(templateFile.getPhysicalFile().getIdPhysicalFile());
            if (physicalTemplateFile != null)
            {
                return new String(physicalTemplateFile.getValue());
            } else {
                AppLogService.error(LOG_ERROR_PDF_EXPORT_GENERATION + " : " + "Physical file not found for file " + templateFile.getTitle());
            }
        }
        return null;
    }

    public String generateTemplateForPdfExportResponses (Form form,  List<Question> listQuestions )
    {
        String space = "                ";
        String freemarkerOpenBracket = "${";
        String freemarkerCloseBracket = "}";
        Document document = new Document("");

        String InfoMessageResponseCreated = I18nService.getLocalizedString( RESPONSE_CREATED, I18nService.getDefaultLocale( ));
        String InfoMessageResponseUpdated = I18nService.getLocalizedString( RESPONSE_UPDATED, I18nService.getDefaultLocale( ));
        String InfoMessageResponseState = I18nService.getLocalizedString( MESSAGE_EXPORT_FORM_STATE, I18nService.getDefaultLocale( ));
        String InfoMessageFormTitle = I18nService.getLocalizedString( MESSAGE_EXPORT_FORM_TITLE, I18nService.getDefaultLocale( ));
        Element div = document.appendElement("div");
        div.addClass("response_container");

        div.appendElement("h1").text(InfoMessageFormTitle+ " : " + form.getTitle()   + space + "# " + "$$" + RESPONSE_NUMBER + "$$");
        div.appendElement("h3").text(InfoMessageResponseCreated+ " : " + freemarkerOpenBracket + "creation_date!" + freemarkerCloseBracket);
        div.appendElement("h3").text(InfoMessageResponseUpdated+ " : " + freemarkerOpenBracket + "update_date!" + freemarkerCloseBracket);
        div.appendElement("h3").text(InfoMessageResponseState + " : " + freemarkerOpenBracket + "status!" + freemarkerCloseBracket);
        Element table = div.appendElement("table");
        Element tbody = table.appendElement("tbody");

        int numRows = listQuestions.size();

        for (int i = 0; i < numRows; i++)
        {
            Element row = tbody.appendElement("tr");
            Element cellTitle = row.appendElement("td");
            cellTitle.text(listQuestions.get(i).getTitle() + " : ");
            Element cellResponse = row.appendElement("td");
            cellResponse.text(freemarkerOpenBracket+"position_"+listQuestions.get(i).getEntry().getIdEntry()+"!"+freemarkerCloseBracket);
        }
        return document.outerHtml();
    }

    public String appendDefaultStyleToPdf(String template)
    {
        Document doc = Jsoup.parse( template );
        Element head = doc.head();
        Element style = head.appendElement("style");
        style.appendText("body {font-family: sans-serif; border-collapse: collapse; width: 100%;}");
        style.appendText("td, th {padding: 8px;}");
        style.appendText("tr:nth-child(even){background-color: #f2f2f2;}");
        style.appendText("th {padding-top: 12px; padding-bottom: 12px; text-align: center; background-color: #04AA6D; color: white;}");
        style.appendText("h1 {text-align: center;}");
        style.appendText("h2 {text-align: center;}");
        style.appendText("#form_responses {margin-top: 50px; margin-bottom: 50px;}");
        style.appendText("a {color: #000000;}");
        style.appendText("a:visited {color: #800080;}");
        style.appendText("a:hover {color: #0000FF;}");
        style.appendText(".response_container {margin-left: 10px; margin-right: 10px;margin-top: 50px; margin-bottom: 50px; border: 1px solid #ddd; padding: 8px;}");

        return doc.outerHtml();
    }
    public String generateTemplateForPdfExportCoverPage (Form form, List<FormResponse> listResponse )
    {
        List<String> listBaseUrl = getBaseUrl();
        String  adminBaseUrl = listBaseUrl.get(1);
        Document document = new Document("");
        Element html = document.appendElement("html");
        Element head = html.appendElement("head");
        Element body = html.appendElement("body");
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        head.appendElement("title").text( form.getTitle()+ " - "  +  now );
        head.appendElement("meta").attr("charset", "UTF-8");
        Element h1 = body.appendElement("h1");
        h1.text(form.getTitle() + " - "  +  now );

        Element table = body.appendElement("table");
        Element tbody = table.appendElement("tbody");
        for (int i = 0; i < listResponse.size(); i++) {
            Element row = tbody.appendElement("tr");
            Element cellTitle = row.appendElement("td");
            cellTitle.text(I18nService.getLocalizedString( RESPONSE_NUMBER, I18nService.getDefaultLocale( )) + " " + (i+1));
            Element cellResponse = row.appendElement("td");
            String linkMessageBO = I18nService.getLocalizedString( LINK_MESSAGE_BO, Locale.getDefault( ) );
            Element linkBO = cellResponse.appendElement("a");
            linkBO.attr("href",adminBaseUrl+"jsp/admin/plugins/forms/ManageDirectoryFormResponseDetails.jsp?view=view_form_response_details&id_form_response=" + listResponse.get(i).getId( ));
            linkBO.text(linkMessageBO);
        }
        Element div = body.appendElement("div");
        div.id("form_responses");

        return document.outerHtml();
    }



    /**
     * Gets the response value.
     *
     * @param formQuestionResponse
     *            the form question response
     * @return List<String> response value
     */
    public List<String> getResponseValue( FormQuestionResponse formQuestionResponse )
    {

        IEntryTypeService entryTypeService ;
        List<String> listResponseValue = new ArrayList<>( );
        if(formQuestionResponse != null && formQuestionResponse.getQuestion( ) != null && formQuestionResponse.getQuestion( ).getEntry( ) != null && formQuestionResponse.getEntryResponse() != null)
        {
            Entry entry = formQuestionResponse.getQuestion( ).getEntry( );
            entryTypeService = EntryTypeServiceManager.getEntryTypeService( entry );
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
            if ((entryTypeService instanceof EntryTypeImage || entryTypeService instanceof EntryTypeCamera))
            {
                PhysicalFile physicalFile = PhysicalFileHome.findByPrimaryKey(Integer.parseInt(response.getFile().getFileKey()));
                if (response.getFile() != null)
                {
                    if (physicalFile != null)
                    {
                        String encoded = Base64.getEncoder().encodeToString(physicalFile.getValue());
                        listResponseValue.add("<div style=\"margin-top: 10px; margin-bottom: 10px;\">"
                                + "<center><img src=\"data:image/jpeg;base64, " + encoded + " \" width=\"500px\" height=\"auto\" /></center> "
                                + "</div>"
                        );
                    } else {
                        listResponseValue.add(StringUtils.EMPTY);
                    }
                }
            } else if (entryTypeService instanceof EntryTypeFile && response.getFile() != null)
            {
                fr.paris.lutece.portal.business.file.File file = FileHome.findByPrimaryKey(Integer.parseInt(response.getFile().getFileKey()));
                if (file != null) {
                    String space = "                ";
                    String textInFileLink = file.getTitle() + space + file.getSize() + "Bytes" + space + file.getMimeType() + space + file.getDateCreation().toLocalDateTime().toString();
                    String htmlDisplayFile = "<center><p>" + textInFileLink + "</p></center>";
                    listResponseValue.add(htmlDisplayFile);
                }
            }
            else
            {
                String strResponseValue = entryTypeService.getResponseValueForExport(entry, null, response, I18nService.getDefaultLocale());
                if (strResponseValue != null) {
                    listResponseValue.add(strResponseValue);
                }
            }
         }
        }
        if(listResponseValue.isEmpty())
        {
            listResponseValue.add(StringUtils.EMPTY);
        }
        return listResponseValue;
    }
    public  HashMap<Integer, FormQuestionResponse>  formResponseListToHashmap( List<FormQuestionResponse>  formQuestionResponseList)
    {
        HashMap<Integer, FormQuestionResponse> formResponseListByEntryId = new HashMap<>();
        for (int i = 0; i < formQuestionResponseList.size(); i++)
        {
            int idEntry = formQuestionResponseList.get(i).getQuestion().getIdEntry();
            if(formResponseListByEntryId.containsKey(idEntry))
            {
                // This is to had the response to the hashmap when there are iterations in the form (one than one time the same entry)
                List <Response> presentResponses = formResponseListByEntryId.get(formQuestionResponseList.get(i).getQuestion().getIdEntry()).getEntryResponse();
                List <Response> newResponses = formQuestionResponseList.get(i).getEntryResponse();
                presentResponses.addAll(newResponses);
                formQuestionResponseList.get(i).setEntryResponse(presentResponses);
                formResponseListByEntryId.put(idEntry, formQuestionResponseList.get(i));
            }
            else
            {
                formResponseListByEntryId.put(idEntry, formQuestionResponseList.get(i));
            }
        }
        return formResponseListByEntryId;
    }
    /**
     * Fill template with form question response.
     *
     * @param template
     *            the template
     * @param formQuestionResponseList
     *            the form question response list
     * @param listQuestions
     *            the list questions
     * @return the html template
     */
    protected HtmlTemplate fillTemplateWithFormQuestionResponse (String template, List<FormQuestionResponse> formQuestionResponseList, List<Question> listQuestions, FormResponse formResponse)
    {
        Map<String, Object> model = new HashMap<>();
        Collection<InfoMarker> collectionNotifyMarkers = GenericFormsProvider.getProviderMarkerDescriptions(_form);
        model = markersToModel(model, collectionNotifyMarkers, formQuestionResponseList, formResponse);
        return AppTemplateService.getTemplateFromStringFtl(template, Locale.getDefault(), model);
    }
    protected List<String> getBaseUrl()
    {

        String baseUrl = AppPathService.getProdUrl(_request);
        String adminBaseUrl = "";
        if(AppPropertiesService.getProperty( "lutece.admin.prod.url") != null)
        {
            adminBaseUrl = AppPropertiesService.getProperty( "lutece.admin.prod.url" );
        }
        else
        {
            AppLogService.info( "lutece.admin.prod.url property not found" );
            adminBaseUrl = baseUrl;
        }
        List<String> listBaseUrl = new ArrayList<>();
        listBaseUrl.add(baseUrl);
        listBaseUrl.add(adminBaseUrl);
        return listBaseUrl;
    }
    protected Map<String, Object> markersToModel(Map<String, Object> model, Collection<InfoMarker> collectionInfoMarkers, List<FormQuestionResponse> formQuestionResponseList, FormResponse formResponse)
    {
        HashMap<Integer, FormQuestionResponse> formResponseListByEntryId = formResponseListToHashmap(formQuestionResponseList);
        List<String> listBaseUrl = getBaseUrl();
        String  baseUrl = listBaseUrl.get(0);
        String  adminBaseUrl = listBaseUrl.get(1);
        for ( InfoMarker infoMarker : collectionInfoMarkers )
        {
            model.put( infoMarker.getMarker(), infoMarker.getValue() );
            if(infoMarker.getMarker().contains("position_"))
            {
                String position = infoMarker.getMarker().replace("position_", "");
                int positionInt = Integer.parseInt(position);
                List<String> responseValue = getResponseValue(formResponseListByEntryId.get(positionInt));
                String responseValueString = "";
                for (String response : responseValue)
                {
                    responseValueString += response + " ";
                }
                model.put( infoMarker.getMarker(), responseValueString );

            }
            if(infoMarker.getMarker().equals("url_admin_forms_response_detail"))
            {
                String linkMessage = I18nService.getLocalizedString( LINK_MESSAGE_BO, Locale.getDefault( ) );
                model.put( infoMarker.getMarker(), "<a href=\""+ adminBaseUrl+"/jsp/admin/plugins/forms/ManageDirectoryFormResponseDetails.jsp?view=view_form_response_details&id_form_response=" + formResponse.getId( ) + "\">" + linkMessage + "</a>" );
            }
            if(infoMarker.getMarker().equals("url_fo_forms_response_detail"))
            {
                String linkMessage = I18nService.getLocalizedString( LINK_MESSAGE_FO, Locale.getDefault( ) );
                model.put( infoMarker.getMarker(), "<a href=\""+ baseUrl+"/jsp/site/Portal.jsp?page=formsResponse&view=formResponseView&id_response=" + formResponse.getId( ) + "\">" + linkMessage + "</a>" );
            }
            if(infoMarker.getMarker().equals("creation_date"))
            {
                String creationDate = formResponse.getCreation( ).toLocalDateTime().toString();
                String[] parts = creationDate.split("T");
                String date = parts[0];
                String time = parts[1];
                model.put( infoMarker.getMarker(), date + " " + time );
            }
            if(infoMarker.getMarker().equals("update_date"))
            {
                String updateDate = formResponse.getUpdate( ).toLocalDateTime().toString();
                String[] parts = updateDate.split("T");
                String date = parts[0];
                String time = parts[1];
                model.put( infoMarker.getMarker(), date + " " + time );
            }
            if(infoMarker.getMarker().equals("status"))
            {
                if(formResponse.isPublished()) {
                    String published = I18nService.getLocalizedString( PUBLISHED, Locale.getDefault( ) );
                    model.put( infoMarker.getMarker(), published );
                } else {
                    String notPublished = I18nService.getLocalizedString( NOT_PUBLISHED, Locale.getDefault( ) );
                    model.put( infoMarker.getMarker(), notPublished );
                }
            }
            if(infoMarker.getMarker().equals("update_date_status"))
            {
                String updateDate = formResponse.getUpdateStatus( ).toLocalDateTime().toString();
                String[] parts = updateDate.split("T");
                String date = parts[0];
                String time = parts[1];
                model.put( infoMarker.getMarker(), date + " " + time );
            }
        }
        return model;
    }


    protected void generatePdfFile(Path directoryFile, String strFilledTemplate, String fileName, boolean isCustomTemplate ) throws PdfConverterServiceException, IOException
    {
        try ( OutputStream outputStream = Files.newOutputStream( directoryFile.resolve( fileName + EXTENSION_PDF ) ) )
        {
            if(!isCustomTemplate){
                strFilledTemplate = appendDefaultStyleToPdf(strFilledTemplate);
            }
            Document doc = Jsoup.parse( strFilledTemplate, UTF_8 );
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
    protected void generatedPdfForRangeOfFormResponses ( Path directoryFile, String strTemplateResponses, List<Question> listQuestions, List<FormResponse> listResponse, List<List<FormQuestionResponse>> formQuestionResponseList, int startRange, String fileName, boolean isCustomTemplate) throws IOException
    {
        try {
            String strFilledTemplate = "";
            for (int i = 0; i < formQuestionResponseList.size(); i++) {
                List<FormQuestionResponse> formQuestionResponseList1 = formQuestionResponseList.get(i);
                HtmlTemplate filledTemplate = fillTemplateWithFormQuestionResponse(strTemplateResponses, formQuestionResponseList1, listQuestions, listResponse.get(i));
                String strSingleTemplate = "";
                if (filledTemplate.getHtml().contains("$$response_number$$")) {
                    strSingleTemplate = filledTemplate.getHtml().replace("$$response_number$$", String.valueOf(startRange+i + 1));

                } else {
                    strSingleTemplate = filledTemplate.getHtml();
                }
                strFilledTemplate += strSingleTemplate;
            }
            generatePdfFile(directoryFile, strFilledTemplate, fileName, isCustomTemplate);
        } catch (IOException e) {
            AppLogService.error( LOG_ERROR_PDF_EXPORT_GENERATION, e );
            throw e;
        } catch (Exception e) {
            AppLogService.error( LOG_ERROR_PDF_EXPORT_GENERATION, e );
        }

    }

}
