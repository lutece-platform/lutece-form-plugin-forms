package fr.paris.lutece.plugins.forms.business.form.panel.initializer.querypart.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.lucene.search.DocValuesNumbersQuery;

import fr.paris.lutece.plugins.forms.business.form.FormParameters;
import fr.paris.lutece.plugins.forms.business.form.search.FormResponseSearchItem;

public class FormPanelFormResponseIdFilterInitializerQueryPart 
extends AbstractFormPanelInitializerQueryPart {
	
	public static final String PARAM_ID_LIST = "id_list";
	
	@SuppressWarnings("unchecked")
	@Override
	public void buildFormPanelInitializerQuery(FormParameters formParameters) {
		List<Integer> idList = (List<Integer>) formParameters.getFormParametersMap().get( PARAM_ID_LIST );
		setFormPanelInitializerSelectQuery( new DocValuesNumbersQuery( FormResponseSearchItem.FIELD_ID_FORM_RESPONSE, idList.stream( ).map( Integer::longValue ).collect( Collectors.toList( ) ) ) );
	}

}
