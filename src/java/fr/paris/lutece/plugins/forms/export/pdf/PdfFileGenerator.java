package fr.paris.lutece.plugins.forms.export.pdf;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseHome;
import fr.paris.lutece.plugins.forms.business.form.FormResponseItem;
import fr.paris.lutece.plugins.forms.business.form.FormResponseItemSortConfig;
import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.filter.FormFilter;
import fr.paris.lutece.plugins.forms.business.form.panel.FormPanel;
import fr.paris.lutece.plugins.forms.export.csv.AbstractFileGenerator;
import fr.paris.lutece.plugins.forms.service.MultiviewFormService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.file.FileUtil;

public class PdfFileGenerator extends AbstractFileGenerator
{
    private static final boolean ZIP_EXPORT = Boolean.parseBoolean( AppPropertiesService.getProperty( "forms.export.pdf.zip", "false" ) );
    private static final String CONSTANT_MIME_TYPE_PDF = "application/pdf"; 
    private static final String EXTENSION_PDF = ".pdf";
    
    private boolean _hasMultipleFiles = false;
    
    protected PdfFileGenerator( String formName , FormPanel formPanel, List<IFormColumn> listFormColumn, List<FormFilter> listFormFilter,
            FormResponseItemSortConfig sortConfig, String fileDescription )
    {
        super( FileUtil.normalizeFileName( formName ), formPanel, listFormColumn, listFormFilter, sortConfig, fileDescription );
    }

    @Override
    public Path generateFile( ) throws IOException
    {
        Path directoryFile = Paths.get( TMP_DIR, _fileName );
        if ( !directoryFile.toFile( ).exists( ) )
        {
            directoryFile.toFile( ).mkdir( );
        }
        writeExportFile( directoryFile );
        if ( hasMultipleFiles( ) )
        {
            return directoryFile;
        }
        File[] files = directoryFile.toFile( ).listFiles( ( File f ) -> f.getName( ).endsWith( EXTENSION_PDF ) );
        return files[0].toPath( );
    }

    @Override
    public String getDescription( )
    {
        return _fileDescription;
    }

    @Override
    public String getFileName( )
    {
        return _fileName + ( hasMultipleFiles( ) || isZippable( ) ? FileUtil.EXTENSION_ZIP : EXTENSION_PDF );
    }

    @Override
    public String getMimeType( )
    {
        return hasMultipleFiles( ) || isZippable( ) ? FileUtil.CONSTANT_MIME_TYPE_ZIP : CONSTANT_MIME_TYPE_PDF;
    }

    @Override
    public boolean isZippable( )
    {
        return ZIP_EXPORT;
    }
    
    @Override
    public boolean hasMultipleFiles( )
    {
        return _hasMultipleFiles;
    }
    
    private void writeExportFile( Path directoryFile ) throws IOException
    {
        FormResponsePdfExport export = new FormResponsePdfExport( );
        
        List<FormResponseItem> listFormResponseItems = MultiviewFormService.getInstance( ).searchAllListFormResponseItem( _formPanel, _listFormColumn,
                _listFormFilter, _sortConfig );
        
        _hasMultipleFiles = listFormResponseItems.size( ) > 1;
        for ( FormResponseItem responseItem : listFormResponseItems )
        {
            FormResponse formResponse = FormResponseHome.findByPrimaryKey( responseItem.getIdFormResponse( ) );
            try ( OutputStream outputStream = Files.newOutputStream( directoryFile.resolve( formResponse.getId( ) + ".pdf" ) ) )
            {
                export.buildPdfExport( formResponse, outputStream );
            }
        }
    }
}
