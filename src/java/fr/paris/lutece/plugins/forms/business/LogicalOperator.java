package fr.paris.lutece.plugins.forms.business;

public enum LogicalOperator {

	AND( "and" ),
    OR( "or" );
	
	private final String _strLabel;

	LogicalOperator(String strLabel) {
		this._strLabel = strLabel;
	}

	public String getLabel() {
		return _strLabel;
	}
	
}
