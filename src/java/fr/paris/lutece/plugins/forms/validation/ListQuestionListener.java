package fr.paris.lutece.plugins.forms.validation;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.portal.service.spring.SpringContextService;

public class ListQuestionListener implements IControlListener{

	
	
	@Override
	public void notifyControlRemoval(Control control, HttpServletRequest request) {
		
		String paramValidator=request.getParameter( FormsConstants.PARAMETRE_VALIDATOR_LISTQUESTION_NAME );
		
		if( paramValidator != null && paramValidator.equals(FormsConstants.VALUE_VALIDATOR_LISTEQUESTION_NAME) ){
			ControlHome.removeMappingControl(control.getId( ));
		}
		
	}

	@Override
	public void notifyControlCreated(Control control, HttpServletRequest request) {
		String paramValidator=request.getParameter( FormsConstants.PARAMETRE_VALIDATOR_LISTQUESTION_NAME );
		if( paramValidator != null && paramValidator.equals(FormsConstants.VALUE_VALIDATOR_LISTEQUESTION_NAME) ){
			AbstractListQuestionValidator listQuestValidator= SpringContextService.getBean(control.getValidatorName( ));
			if(listQuestValidator.getListAvailableFieldControl() != null ){
				for(String param: listQuestValidator.getListAvailableFieldControl()){
					String idQuestion= request.getParameter(param);
					if(idQuestion != null && !idQuestion.isEmpty() && !idQuestion.equals(FormsConstants.REFERENCE_ITEM_DEFAULT_CODE)){
						ControlHome.createMappingControl(control.getId( ), Integer.parseInt(idQuestion), param);
					}
					
				}
			}
		
			
		}
	}

	@Override
	public void notifyControlUpdated(Control control, HttpServletRequest request) {
		String paramValidator=request.getParameter( FormsConstants.PARAMETRE_VALIDATOR_LISTQUESTION_NAME );
		if( paramValidator != null && paramValidator.equals(FormsConstants.VALUE_VALIDATOR_LISTEQUESTION_NAME) ){
			notifyControlRemoval( control,  request);
			notifyControlCreated( control,  request);
		}
		
	}

}
