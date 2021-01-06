package fr.paris.lutece.plugins.forms.export.csv;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.filegenerator.service.IFileGenerator;
import fr.paris.lutece.plugins.forms.business.form.FormResponseItemSortConfig;
import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.filter.FormFilter;
import fr.paris.lutece.plugins.forms.business.form.panel.FormPanel;

public abstract class AbstractFileGenerator implements IFileGenerator
{
    protected static final String TMP_DIR = System.getProperty( "java.io.tmpdir" );
    
    protected final FormPanel _formPanel;
    protected final List<IFormColumn> _listFormColumn;
    protected final List<FormFilter> _listFormFilter;
    protected final FormResponseItemSortConfig _sortConfig;
    protected final String _fileName;
    protected final String _fileDescription;
    
    /**
     * Constructor
     * 
     * @param _listFormResponseItems
     */
    protected AbstractFileGenerator( String fileName, FormPanel formPanel, List<IFormColumn> listFormColumn, List<FormFilter> listFormFilter,
            FormResponseItemSortConfig sortConfig, String fileDescription )
    {
        _fileName = fileName;
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
}
