package fr.paris.lutece.plugins.forms.export;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import fr.paris.lutece.plugins.forms.export.csv.CSVExport;
import fr.paris.lutece.plugins.forms.export.pdf.PDFExport;
import fr.paris.lutece.plugins.forms.export.pdffull.PdfFullExport;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

@ApplicationScoped
public class ExportProducer 
{
	@Produces
	@ApplicationScoped
	@Named( "forms.csvExport" )
    public IFormatExport produceCSVExport( @ConfigProperty( name = "forms.export.csv.formatExportName" ) String strFormatExportName,
            @ConfigProperty( name = "forms.export.csv.formatExportDisplayName" ) String strFormatExportDisplayName,
            @ConfigProperty( name = "forms.export.csv.formatExportDescription" ) String strFormatExportDescription )
    {
        return new CSVExport( strFormatExportName, strFormatExportDisplayName, strFormatExportDescription );
    }

	@Produces
	@ApplicationScoped
	@Named( "forms.pdfExport" )
    public IFormatExport producePDFExport( @ConfigProperty( name = "forms.export.pdf.formatExportName" ) String strFormatExportName,
            @ConfigProperty( name = "forms.export.pdf.formatExportDisplayName" ) String strFormatExportDisplayName,
            @ConfigProperty( name = "forms.export.pdf.formatExportDescription" ) String strFormatExportDescription )
    {
        return new PDFExport( strFormatExportName, strFormatExportDisplayName, strFormatExportDescription );
    }

	@Produces
	@ApplicationScoped
	@Named( "forms.fullExport" )
    public IFormatExport producePdfFullExport( @ConfigProperty( name = "forms.export.pdfFull.formatExportName" ) String strFormatExportName,
            @ConfigProperty( name = "forms.export.pdfFull.formatExportDisplayName" ) String strFormatExportDisplayName,
            @ConfigProperty( name = "forms.export.pdfFull.formatExportDescription" ) String strFormatExportDescription )
    {
        return new PdfFullExport( strFormatExportName, strFormatExportDisplayName, strFormatExportDescription );
    }
}
