package fr.paris.lutece.plugins.forms.business;

public class ConditionControl {
	
    private String  _strTargetStepTitle;
    
    private String _strTargetQuestionTitle;
    
    private Control _control;
    
	public ConditionControl() {
	}
	
	public ConditionControl(String strTargetStepTitle, String strTargetQuestionTitle, Control control) {
		super();
		this._strTargetStepTitle = strTargetStepTitle;
		this._strTargetQuestionTitle = strTargetQuestionTitle;
		this._control = control;
	}

	public String getTargetStepTitle() {
		return _strTargetStepTitle;
	}

	public void setTargetStepTitle(String strTargetStepTitle) {
		this._strTargetStepTitle = strTargetStepTitle;
	}

	public String getTargetQuestionTitle() {
		return _strTargetQuestionTitle;
	}

	public void setTargetQuestionTitle(String strTargetQuestionTitle) {
		this._strTargetQuestionTitle = strTargetQuestionTitle;
	}

	public Control getControl() {
		return _control;
	}

	public void setControl(Control control) {
		this._control = control;
	}

}
