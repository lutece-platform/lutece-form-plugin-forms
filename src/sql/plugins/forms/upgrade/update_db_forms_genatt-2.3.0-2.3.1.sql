-- liquibase formatted sql
-- lutece runAfter:genericattributes
-- LUT-33260 : formerly shipped under sql/plugins/genericattributes ; renamed with forms versions (first release shipping the script)

-- formerly src/sql/plugins/genericattributes/upgrade/update_db_genericattributes-2.2.0-2.3.2.sql
-- changeset forms:update_db_genericattributes-2.2.0-2.3.2.sql logicalFilePath:sql/plugins/genericattributes/upgrade/update_db_genericattributes-2.2.0-2.3.2.sql
-- preconditions onFail:MARK_RAN onError:WARN
INSERT INTO genatt_field (id_entry, code, VALUE, title)
	SELECT e.id_entry, 'anonymizable', 
	'false', '-1' from genatt_entry e 
	INNER JOIN genatt_entry_type t ON t.id_type = e.id_type 
	WHERE resource_type = 'FORMS_FORM' 
	AND t.class_name in ( 'forms.entryTypeCheckBox','forms.entryTypeRadioButton','forms.entryTypeSelect' );
