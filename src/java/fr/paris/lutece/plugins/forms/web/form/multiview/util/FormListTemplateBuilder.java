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
package fr.paris.lutece.plugins.forms.web.form.multiview.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.paris.lutece.plugins.forms.business.form.FormResponseItem;
import fr.paris.lutece.plugins.forms.business.form.column.FormColumnCell;
import fr.paris.lutece.plugins.forms.service.IMultiviewMapProvider;
import fr.paris.lutece.plugins.forms.web.form.column.display.IFormColumnDisplay;
import fr.paris.lutece.plugins.forms.web.form.column.display.impl.FormColumnDisplayEntryCartography;
import fr.paris.lutece.plugins.forms.web.form.column.display.impl.FormColumnDisplayEntryGeolocation;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.literal.NamedLiteral;
import jakarta.enterprise.inject.spi.CDI;

/**
 * Builder class for the template of FormColumnDisplay and FormFilterDisplay objects
 */
public final class FormListTemplateBuilder
{
    // Templates
    private static final String TEMPLATE_MULTIVIEW_FORM_TABLE = "admin/plugins/forms/multiview/includes/include_manage_multiview_forms_table.html";
    private static final String TEMPLATE_MULTIVIEW_GEOJSON_POPUP = "admin/plugins/forms/multiview/includes/include_manage_multiview_geojson_popup.html";

    // Multiviewmap Provider bean name
    private static final String BEAN_NAME_MULTIVIEWMAP = "forms.multiviewMap";

    // Marks
    private static final String MARK_FROM_RESPONSE_ITEM_LIST = "from_response_item_list";
    private static final String MARK_FORM_RESPONSE_COLUMN_HEADER_TEMPLATE_LIST = "form_response_column_header_template_list";
    private static final String MARK_FORM_RESPONSE_LINE_TEMPLATE_LIST = "form_response_line_template_list";
    private static final String MARK_FORM_RESPONSE_LINE_TEMPLATE = "form_response_line_template";
    private static final String MARK_FORM_RESPONSE_DETAILS_REDIRECT_BASE_URL = "redirect_details_base_url";
    private static final String MARK_FROM_RESPONSE_GEOJSON_POINT_LIST = "form_response_geojson_point_list";
    private static final String MARK_MULTIVIEWMAP = "multiviewmap";
    private static final String GEOJSON_PROPERTIES = "properties";
    private static final String GEOJSON_COORDINATES = "coordinates";
    private static final String GEOJSON_GEOMETRY = "geometry";
    private static final String GEOJSON_GEOMETRY_TYPE_POINT = "Point";
    private static final String GEOJSON_TYPE = "type";
    private static final String GEOJSON_TYPE_FEATURE = "Feature";
    private static final String PROPERTY_POPUP_CONTENT = "popupContent";

    private static final String PROPERTY_MAPPROVIDER_BEANS_NAME = "forms.mapProvider.beanName.list";

    // To serialize to geojson
    private static ObjectMapper _mapper = new ObjectMapper( );

    /**
     * Constructor
     */
    private FormListTemplateBuilder( )
    {

    }

    /**
     * Build the template of the table of all form response
     * 
     * @param listFormColumnDisplay
     *            The list of all FormColumnDisplay objects to build the global template of the form columns from
     * @param listFormResponseItem
     *            The list of all FormResponseItem used to build the tab with all form responses
     * @param locale
     *            The locale to used for build template
     * @param strRedirectionDetailsBaseUrl
     *            The base url to use for the redirection on the details page
     * @param strSortUrl
     *            The url to use for sort a column
     * @return the global template of all FormColumnDisplay objects
     */
    public static String buildTableTemplate( List<IFormColumnDisplay> listFormColumnDisplay, List<FormResponseItem> listFormResponseItem, Locale locale,
            String strRedirectionDetailsBaseUrl, String strSortUrl, List<Integer> listIdFormResponsePaginated )
    {
        String strTableTemplate = StringUtils.EMPTY;
        List<FormResponseItem> listFormResponseItemPaginated = buildFormResponseItemListToDisplay( listFormResponseItem, listIdFormResponsePaginated );

        if ( !CollectionUtils.isEmpty( listFormColumnDisplay ) && !CollectionUtils.isEmpty( listFormResponseItem ) )
        {
            // Build the list of all from column header template
            List<String> listFormColumnHeaderTemplate = buildFormColumnHeaderTemplateList( listFormColumnDisplay, locale, strSortUrl );

            // Build the model
            Map<String, Object> model = new LinkedHashMap<>( );

            // Build the list of paginated FormColumnLineTemplate and optionally the geojson points
            // We optimize by building the full lineTemplate list only when a map is used
            // (it is used for the popups). If not, we build only the paginated list.
            Optional<FormColumnDisplayEntryGeolocation> maybeFirstGeolocColumn = findFirstGeolocColumnDisplay( listFormColumnDisplay );
            Optional<FormColumnDisplayEntryCartography> maybeFirstCartoColumn = findFirstCartographyColumnDisplay( listFormColumnDisplay );
            Optional<IMultiviewMapProvider> maybeMapProvider = getMapProvider( );
            List<FormColumnLineTemplate> listFormColumnLineTemplatePaginated;
            if ( ( maybeFirstGeolocColumn.isPresent( ) && maybeMapProvider.isPresent( ) ) || maybeFirstCartoColumn.isPresent( ) )
            {
                List<FormColumnLineTemplate> listFormColumnLineTemplate = buildFormColumnLineTemplateList( listFormColumnDisplay, listFormResponseItem,
                        locale );
                List<String> listGeoJsonPoints = new ArrayList<>( );
                if ( maybeFirstCartoColumn.isPresent( ) )
            	{
            		listGeoJsonPoints.addAll( buildGeoJsonList( maybeFirstCartoColumn.get( ), listFormResponseItem, listFormColumnLineTemplate,
                          strRedirectionDetailsBaseUrl ) );
            	}
                if ( maybeFirstGeolocColumn.isPresent( ) )
                {
                	listGeoJsonPoints.addAll ( buildGeoJsonPointsList( maybeFirstGeolocColumn.get( ), listFormResponseItem, listFormColumnLineTemplate,
                        strRedirectionDetailsBaseUrl ) );
                }
                model.put( MARK_FROM_RESPONSE_GEOJSON_POINT_LIST, listGeoJsonPoints );
                model.put( MARK_MULTIVIEWMAP, maybeMapProvider.get( ).getMapTemplate( ) );

                listFormColumnLineTemplatePaginated = buildFormColumnLineTemplateList( listFormColumnLineTemplate, listIdFormResponsePaginated );
            }
            else
            {
                listFormColumnLineTemplatePaginated = buildFormColumnLineTemplateList( listFormColumnDisplay, listFormResponseItemPaginated, locale );
            }

            model.put( MARK_FORM_RESPONSE_COLUMN_HEADER_TEMPLATE_LIST, listFormColumnHeaderTemplate );
            model.put( MARK_FROM_RESPONSE_ITEM_LIST, listFormResponseItemPaginated );
            model.put( MARK_FORM_RESPONSE_LINE_TEMPLATE_LIST, listFormColumnLineTemplatePaginated );
            model.put( MARK_FORM_RESPONSE_DETAILS_REDIRECT_BASE_URL, strRedirectionDetailsBaseUrl );

            strTableTemplate = AppTemplateService.getTemplate( TEMPLATE_MULTIVIEW_FORM_TABLE, locale, model ).getHtml( );
        }

        return strTableTemplate;
    }

    private static Optional<IMultiviewMapProvider> getMapProvider( )
    {
        Instance<IMultiviewMapProvider> mapProviderInstances = CDI.current( ).select( IMultiviewMapProvider.class );

    	if ( !mapProviderInstances.isUnsatisfied( ) )
        {
    		String strMapProviderBeanNameList = AppPropertiesService.getProperty( PROPERTY_MAPPROVIDER_BEANS_NAME );
        	List<String> mapProviderBeanNameList = StringUtils.isEmpty( strMapProviderBeanNameList ) ? new ArrayList<>( ) : Arrays.asList( strMapProviderBeanNameList.split( "," ) );
        	
        	if ( CollectionUtils.isNotEmpty( mapProviderBeanNameList ) )
        	{
        		String strOriginalBeanName = mapProviderBeanNameList.get( 0 );
        		int nPos = strOriginalBeanName.indexOf( '.' );
                String strPrefix = null;
                if ( nPos > 0 )
                {
                    strPrefix = strOriginalBeanName.substring( 0, nPos );
                }
                Plugin plugin = null;
                if ( strPrefix != null )
                {
                    plugin = PluginService.getPlugin( strPrefix );
                }
                if ( plugin == null || plugin.isInstalled( ) ) // A bean without a plugin is always enabled (core beans)
                {
                    return Optional.of( CDI.current().select( IMultiviewMapProvider.class , NamedLiteral.of( strOriginalBeanName ) ).get( ) );
                }
        	}
        }

    	return Optional.empty( );
    }

    private static List<String> buildGeoJsonPointsList( FormColumnDisplayEntryGeolocation geolocFormColumnDisplay, List<FormResponseItem> listFormResponseItem,
            List<FormColumnLineTemplate> listFormColumnLineTemplate, String strRedirectionDetailsBaseUrl )
    {
        return IntStream
                .range( 0, listFormResponseItem.size( ) ).mapToObj( i -> buildResponseItemJson( geolocFormColumnDisplay, listFormResponseItem.get( i ),
                        listFormColumnLineTemplate.get( i ), strRedirectionDetailsBaseUrl ) )
                .filter( Optional::isPresent ).map( Optional::get ).collect( Collectors.toList( ) );
    }

    private static Optional<String> buildResponseItemJson( FormColumnDisplayEntryGeolocation formColumnDisplayEntryGeolocation,
            FormResponseItem formResponseItem, FormColumnLineTemplate formColumnlineTemplate, String strRedirectionDetailsBaseUrl )
    {
        int nColumnCellPosition = columnDisplayPositionToCellIndex( formColumnDisplayEntryGeolocation.getPosition( ) );
        FormColumnCell geolocFormColumnCell = formResponseItem.getFormColumnCellValues( ).get( nColumnCellPosition );
        List<Object> listStoredCoordinates = formColumnDisplayEntryGeolocation.buildXYList( geolocFormColumnCell );
        List<Object> listValidatedCoordinates = listStoredCoordinates.stream( ).filter( Objects::nonNull ).map( ( Object str ) -> {
            try
            {
                return _mapper.readValue( (String) str, Number.class );
            }
            catch( IOException e )
            {
                throw new AppException( "Error reading coordinates for formResponseItem idFormResponse=" + formResponseItem.getIdFormResponse( ) + " : " + str,
                        e );
            }
        } ).collect( Collectors.toList( ) );
        if ( listValidatedCoordinates.size( ) != 2 )
        {
            return Optional.empty( );
        }
        Map<String, Object> root = new HashMap<>( );
        Map<String, Object> geometry = new HashMap<>( );
        root.put( GEOJSON_TYPE, GEOJSON_TYPE_FEATURE );
        root.put( GEOJSON_GEOMETRY, geometry );
        geometry.put( GEOJSON_COORDINATES, listValidatedCoordinates );
        geometry.put( GEOJSON_TYPE, GEOJSON_GEOMETRY_TYPE_POINT );
        Map<String, Object> properties = new HashMap<>( );
        properties.put( PROPERTY_POPUP_CONTENT, buildPopupContent( formColumnlineTemplate, strRedirectionDetailsBaseUrl ) );
        root.put( GEOJSON_PROPERTIES, properties );
        try
        {
            return Optional.of( _mapper.writeValueAsString( root ) );
        }
        catch( JsonProcessingException e )
        {
            throw new AppException( "Error creating json for formResponseItem idFormResponse=" + formResponseItem.getIdFormResponse( ), e );
        }
    }
    
    private static List<String> buildGeoJsonList( FormColumnDisplayEntryCartography geolocFormColumnDisplay, List<FormResponseItem> listFormResponseItem,
            List<FormColumnLineTemplate> listFormColumnLineTemplate, String strRedirectionDetailsBaseUrl )
    {
        return IntStream
                .range( 0, listFormResponseItem.size( ) ).mapToObj( i -> buildResponseItemGeoJson( geolocFormColumnDisplay, listFormResponseItem.get( i ),
                        listFormColumnLineTemplate.get( i ), strRedirectionDetailsBaseUrl ) )
                .filter( Optional::isPresent ).map( Optional::get ).collect( Collectors.toList( ) );
    }

    private static Optional<String> buildResponseItemGeoJson( FormColumnDisplayEntryCartography formColumnDisplayEntryCartography,
            FormResponseItem formResponseItem, FormColumnLineTemplate formColumnlineTemplate, String strRedirectionDetailsBaseUrl )
    {
        int nColumnCellPosition = columnDisplayPositionToCellIndex( formColumnDisplayEntryCartography.getPosition( ) );
        FormColumnCell geolocFormColumnCell = formResponseItem.getFormColumnCellValues( ).get( nColumnCellPosition );
        Object strGeoJSONCoordinate = formColumnDisplayEntryCartography.buildGeoJsonString( geolocFormColumnCell );

        if ( strGeoJSONCoordinate != null  )
        {
        	return Optional.of( String.valueOf( strGeoJSONCoordinate ) );
        }
        else
        {
        	return Optional.empty( );
        }
    }

    private static String buildPopupContent( FormColumnLineTemplate formColumnlineTemplate, String strRedirectionDetailsBaseUrl )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_FORM_RESPONSE_DETAILS_REDIRECT_BASE_URL, strRedirectionDetailsBaseUrl );
        model.put( MARK_FORM_RESPONSE_LINE_TEMPLATE, formColumnlineTemplate );
        return AppTemplateService.getTemplate( TEMPLATE_MULTIVIEW_GEOJSON_POPUP, null, model ).getHtml( );
    }

    /**
     * Build the list of all form column header template
     * 
     * @param listFormColumnDisplay
     *            The list of all form column display to retrieve the header template from
     * @param locale
     *            The locale to use for build the template
     * @param strSortUrl
     *            The url to use for sort a column (can be null)
     * @return the list of all form column header template
     */
    private static List<String> buildFormColumnHeaderTemplateList( List<IFormColumnDisplay> listFormColumnDisplay, Locale locale, String strSortUrl )
    {
        List<String> listFormColumnHeaderTemplate = new ArrayList<>( );

        if ( !CollectionUtils.isEmpty( listFormColumnDisplay ) )
        {
            for ( IFormColumnDisplay formColumnDisplay : listFormColumnDisplay )
            {
                String strFormColumnDisplayHeaderTemplate = formColumnDisplay.buildFormColumnHeaderTemplate( strSortUrl, locale );
                listFormColumnHeaderTemplate.add( strFormColumnDisplayHeaderTemplate );
            }
        }

        return listFormColumnHeaderTemplate;
    }

    /**
     * Build the list of all FormColumnLineTemplate
     * 
     * @param listFormColumnDisplay
     *            The list of form column display to use for build the map of line template
     * @param listFormResponseItem
     *            The list of form response item to use for build the map of line template
     * @param locale
     *            The locale to use for build the template
     * @return the list of all FormColumnLineTemplate
     */
    private static List<FormColumnLineTemplate> buildFormColumnLineTemplateList( List<IFormColumnDisplay> listFormColumnDisplay,
            List<FormResponseItem> listFormResponseItem, Locale locale )
    {
        List<FormColumnLineTemplate> listFormColumnLineTemplate = new ArrayList<>( );

        if ( !CollectionUtils.isEmpty( listFormColumnDisplay ) && !CollectionUtils.isEmpty( listFormResponseItem ) )
        {
            for ( FormResponseItem formResponseItem : listFormResponseItem )
            {
                int nIdFormResponse = formResponseItem.getIdFormResponse( );
                FormColumnLineTemplate formResponseColumnLineTemplate = new FormColumnLineTemplate( nIdFormResponse );

                List<FormColumnCell> listFormColumnCell = formResponseItem.getFormColumnCellValues( );
                populateLineTemplateFromCellValues( formResponseColumnLineTemplate, listFormColumnCell, listFormColumnDisplay, locale );

                listFormColumnLineTemplate.add( formResponseColumnLineTemplate );
            }
        }

        return listFormColumnLineTemplate;
    }

    /**
     * Populate the given FormColumnLineTemplate with the value from the list of FormColumnCell and the display from the given list of IFormColumnDisplay
     * 
     * @param formColumnLineTemplate
     *            The FormColumnLineTemplate to populate
     * @param listFormColumnCell
     *            The list of FormColumnCell to retrieve the values from
     * @param listFormColumnDisplay
     *            The list of IFormColumnDisplay to use for the column
     * @param locale
     *            The locale to use for build the templates
     */
    private static void populateLineTemplateFromCellValues( FormColumnLineTemplate formColumnLineTemplate, List<FormColumnCell> listFormColumnCell,
            List<IFormColumnDisplay> listFormColumnDisplay, Locale locale )
    {
        if ( !CollectionUtils.isEmpty( listFormColumnCell ) )
        {
            for ( int index = 0; index < listFormColumnCell.size( ); index++ )
            {
                int nColumnDisplayPosition = cellIndexToColumnDisplayPosition( index );
                IFormColumnDisplay formColumnDisplay = findFormColumnDisplayByPosition( nColumnDisplayPosition, listFormColumnDisplay );
                if ( formColumnDisplay != null )
                {
                    FormColumnCell formColumnCell = listFormColumnCell.get( index );
                    String strFormColunmCellTemplate = formColumnDisplay.buildFormColumnCellTemplate( formColumnCell, locale );
                    formColumnLineTemplate.addFormColumnCellTemplate( strFormColunmCellTemplate );
                }
            }
        }
    }

    /**
     * Convert from columnDisplayPosition to cell index.
     *
     * The positions start at index 1 and cells at index 0..
     * 
     * @param nCellIndex
     *            the cell index
     * @return the position of the corresponding ColumnDisplay
     * @see columnDisplayPositionToCellIndex
     */
    private static int cellIndexToColumnDisplayPosition( int nCellIndex )
    {
        return nCellIndex + 1;
    }

    /**
     * Convert from cell index to columnDisplayPosition.
     *
     * The positions start at index 1 and cells at index 0..
     *
     * @param nColumnDisplayPosition
     *            the column position
     * @return the cell index of the corresponding FormColumnCell
     * @see cellIndexToColumnDisplayPosition
     */
    private static int columnDisplayPositionToCellIndex( int nColumnDisplayPosition )
    {
        return nColumnDisplayPosition - 1;
    }

    /**
     * Find the FormColumnDisplay in the given list with the specified position or null if not found
     * 
     * @param nFormColumnPosition
     *            The position of the FormColumnDisplay to retrieve
     * @param listFormColumnDisplay
     *            The list of FormColumnDisplay from where to find the column with the specified position
     * @return the FormColumnDisplay with the specified position nor null if not found
     */
    private static IFormColumnDisplay findFormColumnDisplayByPosition( int nFormColumnPosition, List<IFormColumnDisplay> listFormColumnDisplay )
    {
        IFormColumnDisplay formColumnDisplayResult = null;

        if ( !CollectionUtils.isEmpty( listFormColumnDisplay ) )
        {
            for ( IFormColumnDisplay formColumnDisplay : listFormColumnDisplay )
            {
                if ( formColumnDisplay.getPosition( ) == nFormColumnPosition )
                {
                    formColumnDisplayResult = formColumnDisplay;
                    break;
                }
            }
        }

        return formColumnDisplayResult;
    }

    /**
     * Build the list of FormResponseItem to display for the active FormPanelDisplay based on the number of items of the current paginator
     * 
     * @return list of FormResponseItem to display for the active FormPanelDisplay
     */
    private static List<FormResponseItem> buildFormResponseItemListToDisplay( List<FormResponseItem> listFormResponseItem,
            List<Integer> listIdFormResponsePaginated )
    {
        List<FormResponseItem> listFormResponseItemToDisplay = new ArrayList<>( );

        if ( !CollectionUtils.isEmpty( listIdFormResponsePaginated ) && !CollectionUtils.isEmpty( listFormResponseItem ) )
        {
            for ( FormResponseItem formResponseItem : listFormResponseItem )
            {
                Integer nIdFormResponse = formResponseItem.getIdFormResponse( );
                if ( listIdFormResponsePaginated.contains( nIdFormResponse ) )
                {
                    listFormResponseItemToDisplay.add( formResponseItem );
                }
            }
        }

        return listFormResponseItemToDisplay;
    }

    /**
     * Build the list of all FormColumnLineTemplate
     *
     * @return list of FormColumnLineTemplate to display for the active FormPanelDisplay
     */
    private static List<FormColumnLineTemplate> buildFormColumnLineTemplateList( List<FormColumnLineTemplate> listFormColumnLineTemplate,
            List<Integer> listIdFormResponsePaginated )
    {
        List<FormColumnLineTemplate> listFormColumnLineTemplateToDisplay = new ArrayList<>( );

        if ( !CollectionUtils.isEmpty( listIdFormResponsePaginated ) && !CollectionUtils.isEmpty( listFormColumnLineTemplate ) )
        {
            for ( FormColumnLineTemplate formColumnLineTemplate : listFormColumnLineTemplate )
            {
                Integer nIdFormResponse = formColumnLineTemplate.getIdFormResponse( );
                if ( listIdFormResponsePaginated.contains( nIdFormResponse ) )
                {
                    listFormColumnLineTemplateToDisplay.add( formColumnLineTemplate );
                }
            }
        }

        return listFormColumnLineTemplateToDisplay;
    }

    /**
     * Get the first geoloc column display if it exists.
     *
     * Note: this could be replaced with a method to choose the geoloccolumn if we wanted. Note: this could also be replaced with something to return geojson
     * multipoints.
     *
     * @param listFormColumnDisplay
     *            The list of all form column display to retrieve the header template from
     * @param strSortUrl
     *            The url to use for sort a column (can be null)
     * @return the list of all form column header template
     */
    private static Optional<FormColumnDisplayEntryGeolocation> findFirstGeolocColumnDisplay( List<IFormColumnDisplay> listFormColumnDisplay )
    {
        return listFormColumnDisplay.stream( ).filter( FormColumnDisplayEntryGeolocation.class::isInstance )
                .map( FormColumnDisplayEntryGeolocation.class::cast ).findFirst( );
    }
    
    /**
     * Get the first carto column display if it exists.
     *
     * Note: this could be replaced with a method to choose the cartocolumn if we wanted. Note: this could also be replaced with something to return geojson
     * multipoints.
     *
     * @param listFormColumnDisplay
     *            The list of all form column display to retrieve the header template from
     * @param strSortUrl
     *            The url to use for sort a column (can be null)
     * @return the list of all form column header template
     */
    private static Optional<FormColumnDisplayEntryCartography> findFirstCartographyColumnDisplay( List<IFormColumnDisplay> listFormColumnDisplay )
    {
        return listFormColumnDisplay.stream( ).filter( FormColumnDisplayEntryCartography.class::isInstance )
                .map( FormColumnDisplayEntryCartography.class::cast ).findFirst( );
    }
}
