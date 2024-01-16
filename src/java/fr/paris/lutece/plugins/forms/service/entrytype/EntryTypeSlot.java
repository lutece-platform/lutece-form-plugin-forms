package fr.paris.lutece.plugins.forms.service.entrytype;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeSlot;

import java.util.List;

public class EntryTypeSlot extends AbstractEntryTypeSlot implements IResponseComparator
{
    /**
     * Name of the bean of this service
     */
    public static final String BEAN_NAME = "forms.entryTypeSlot";

    private static final String TEMPLATE_CREATE               = "admin/plugins/forms/entries/create_entry_type_slot.html";
    private static final String TEMPLATE_MODIFY               = "admin/plugins/forms/entries/modify_entry_type_slot.html";
    private static final String TEMPLATE_READONLY_BACKOFFICE  = "admin/plugins/forms/entries/readonly_entry_type_slot.html";
    private static final String TEMPLATE_EDITION_BACKOFFICE   = "admin/plugins/forms/entries/fill_entry_type_slot.html";
    private static final String TEMPLATE_EDITION_FRONTOFFICE  = "skin/plugins/forms/entries/fill_entry_type_slot.html";
    private static final String TEMPLATE_READONLY_FRONTOFFICE = "skin/plugins/forms/entries/readonly_entry_type_slot.html";

    @Override
    public String getTemplateCreate( Entry entry, boolean bDisplayFront )
    {
        return TEMPLATE_CREATE;
    }

    @Override
    public String getTemplateHtmlForm( Entry entry, boolean bDisplayFront )
    {
        if ( bDisplayFront )
        {
            return TEMPLATE_EDITION_FRONTOFFICE;
        }

        return TEMPLATE_EDITION_BACKOFFICE;
    }

    @Override
    public String getTemplateModify( Entry entry, boolean bDisplayFront )
    {
        return TEMPLATE_MODIFY;
    }

    @Override
    public String getTemplateEntryReadOnly( boolean bDisplayFront )
    {
        if ( bDisplayFront )
        {
            return TEMPLATE_READONLY_FRONTOFFICE;
        }

        return TEMPLATE_READONLY_BACKOFFICE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isResponseChanged( List<Response> listResponseReference, List<Response> listResponseNew )
    {
        if ( listResponseReference.size( ) != listResponseNew.size( ) )
        {
            return true;
        }

        boolean bAllResponsesEquals = true;

        for ( int i = 0; i < listResponseReference.size( ); i++ )
        {
            Response responseReference = listResponseReference.get( i );
            Response responseNew = listResponseNew.get( i );

            if ( responseReference == null || responseReference.getResponseValue( ) == null || !responseReference.getResponseValue( ).equals( responseNew.getResponseValue( ) ) )
            {
                bAllResponsesEquals = false;
                break;
            }
        }

        return !bAllResponsesEquals;
    }
}
