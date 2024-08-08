INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin,display_order,inactive) VALUES (124, 'Creneau horaire', 0, 0, 0, 'forms.entryTypeSlot', 'calendar', 'forms',22,0);

INSERT INTO genatt_field ( id_entry, title, code, VALUE, default_value )
	SELECT e.id_entry, null, 'sortable_list_type', '0', 0
	FROM genatt_entry e
	INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
	WHERE e.resource_type = 'FORMS_FORM'
	AND t.class_name = 'forms.entryTypeSelectOrder';
