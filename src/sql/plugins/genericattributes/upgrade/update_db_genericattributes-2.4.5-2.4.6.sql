--liquibase formatted sql
--changeset forms:update_db_genericattributes-2.4.5-2.4.6.sql
--preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE genatt_entry_type MODIFY COLUMN id_type int AUTO_INCREMENT NOT NULL;

INSERT INTO genatt_field ( id_entry, title, code, VALUE, default_value )
	SELECT e.id_entry, null, 'sortable_list_type', '0', 0
	FROM genatt_entry e
	INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
	WHERE e.resource_type = 'FORMS_FORM'
	AND t.class_name = 'forms.entryTypeSelectOrder';

INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin,display_order,inactive) VALUES
    ('Session',0,0,0,'forms.entryTypeSession','user','forms',23,0);
