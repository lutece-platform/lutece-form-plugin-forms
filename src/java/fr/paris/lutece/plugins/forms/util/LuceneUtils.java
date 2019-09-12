package fr.paris.lutece.plugins.forms.util;

import fr.paris.lutece.plugins.forms.business.form.search.FormResponseSearchItem;

/**
 * Utils for lucene.
 */
public final class LuceneUtils
{

    private LuceneUtils( )
    {
    }

    /**
     * Creates the lucene index key.
     * 
     * @param strQuestionCode
     * @param nIterationNumber
     * @return key
     */
    public static String createLuceneEntryKey( String strQuestionCode, int nIterationNumber )
    {
        StringBuilder fieldNameBuilder = new StringBuilder( FormResponseSearchItem.FIELD_ENTRY_CODE_SUFFIX );
        fieldNameBuilder.append( strQuestionCode );
        fieldNameBuilder.append( FormResponseSearchItem.FIELD_RESPONSE_FIELD_ITER_ );

        if ( nIterationNumber == -1 )
            nIterationNumber = 0;
        fieldNameBuilder.append( nIterationNumber );
        return fieldNameBuilder.toString( );
    }
}
