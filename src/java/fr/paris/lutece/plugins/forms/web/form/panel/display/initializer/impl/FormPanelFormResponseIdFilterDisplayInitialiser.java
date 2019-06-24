package fr.paris.lutece.plugins.forms.web.form.panel.display.initializer.impl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.form.FormParameters;
import fr.paris.lutece.plugins.forms.business.form.panel.initializer.querypart.impl.FormPanelFormResponseIdFilterInitializerQueryPart;

public abstract class FormPanelFormResponseIdFilterDisplayInitialiser extends AbstractFormPanelDisplayInitializer {

	@Override
    public void buildFormParameters( HttpServletRequest request )
    {
		FormParameters formParameters = new FormParameters( );
		formParameters.getFormParametersMap().put( FormPanelFormResponseIdFilterInitializerQueryPart.PARAM_ID_LIST, getIdList( request ) );
        getFormPanelInitializer( ).setFormParameters( formParameters );
    }
	
	
	protected abstract List<Integer> getIdList( HttpServletRequest request );
}
