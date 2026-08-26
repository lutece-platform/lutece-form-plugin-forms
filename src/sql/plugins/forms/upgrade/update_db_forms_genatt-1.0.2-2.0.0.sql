-- liquibase formatted sql
-- lutece runAfter:genericattributes
-- LUT-33260 : formerly shipped under sql/plugins/genericattributes ; renamed with forms versions (first release shipping the script)

-- formerly src/sql/plugins/genericattributes/upgrade/update_db_generic_attributes_forms_1.0.2-1.0.3.sql
-- formerly src/sql/plugins/genericattributes/upgrade/update_db_generic_attributes_forms_1.3.2-1.3.3.sql
-- formerly src/sql/plugins/genericattributes/upgrade/update_db_genericattributes-1.3.3-2.0.0.sql
-- changeset forms:update_db_generic_attributes_forms_1.0.2-1.0.3.sql
-- preconditions onFail:MARK_RAN onError:WARN
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin) VALUES 
('117', 'Lecture automatique fichier (OCR)', 0, 0, 0, 'forms.entryTypeAutomaticFileReading', 'file', 'forms');

-- changeset forms:update_db_generic_attributes_forms_1.3.2-1.3.3.sql
-- preconditions onFail:MARK_RAN onError:WARN

UPDATE genatt_field f SET f.CODE = 'default_date_value'
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name = 'forms.entryTypeDate'
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.CODE = f.title
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name = 'forms.entryTypeGeolocation'
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.title = null
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name = 'forms.entryTypeGeolocation'
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.VALUE = f.title
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name = 'forms.entryTypeNumbering'
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.CODE = 'prefix'
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name = 'forms.entryTypeNumbering'
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.title = null
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name = 'forms.entryTypeNumbering'
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.CODE = f.title
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name IN( 'forms.entryTypeFile', 'forms.entryTypeImage', 'forms.entryTypeAutomaticFileReading')
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.CODE = 'file_config'
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name IN( 'forms.entryTypeFile', 'forms.entryTypeImage', 'forms.entryTypeAutomaticFileReading')
AND f.code is null
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.title = null
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name IN( 'forms.entryTypeFile', 'forms.entryTypeImage', 'forms.entryTypeAutomaticFileReading')
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.CODE = 'answer_choice'
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name IN( 'forms.entryTypeSelect', 'forms.entryTypeRadioButton', 'forms.entryTypeCheckBox')
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.CODE = 'array_cell'
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name IN( 'forms.entryTypeArray')
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.CODE = 'text_config'
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name IN( 'forms.entryTypeText')
AND e.id_entry = f.id_entry);

UPDATE genatt_field f SET f.CODE = 'text_config'
WHERE f.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name IN( 'forms.entryTypeTextArea')
AND e.id_entry = f.id_entry);

-- changeset forms:update_db_genericattributes-1.3.3-2.0.0.sql
-- preconditions onFail:MARK_RAN onError:WARN

UPDATE genatt_response r
SET r.response_value = UNIX_TIMESTAMP(STR_TO_DATE(r.response_value, "%m/%d/%Y") )
WHERE SUBSTR(r.response_value, 4, 2) > 12
AND r.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name = 'forms.entryTypeDate'
AND e.id_entry = r.id_entry);

UPDATE genatt_response r
SET r.response_value = UNIX_TIMESTAMP(STR_TO_DATE(r.response_value, "%d/%m/%Y") )
WHERE r.response_value LIKE '%/%'
AND r.id_entry IN  (
SELECT e.id_entry FROM genatt_entry e
INNER JOIN genatt_entry_type t ON t.id_type = e.id_type
WHERE t.class_name = 'forms.entryTypeDate'
AND e.id_entry = r.id_entry);

INSERT INTO genatt_field (id_entry, code, VALUE)
	SELECT e.id_entry, 'richtext', 'true' from genatt_entry e 
	INNER JOIN genatt_entry_type t ON t.id_type = e.id_type 
	WHERE resource_type = 'FORMS_FORM' 
	AND t.class_name = 'forms.entryTypeTextArea'
	AND e.fields_in_line = 1;
	
UPDATE genatt_entry_type SET icon_name='dot-circle' WHERE id_type=101 AND plugin='forms';
UPDATE genatt_entry_type SET icon_name='check-square' WHERE id_type=102 AND plugin='forms';
UPDATE genatt_entry_type SET icon_name='comment' WHERE id_type=103 AND plugin='forms';
UPDATE genatt_entry_type SET icon_name='calendar' WHERE id_type=104 AND plugin='forms';
UPDATE genatt_entry_type SET icon_name='list-alt' WHERE id_type=105 AND plugin='forms';
UPDATE genatt_entry_type SET icon_name='file-alt' WHERE id_type=106 AND plugin='forms';
UPDATE genatt_entry_type SET icon_name='sticky-note' WHERE id_type=107 AND plugin='forms';
UPDATE genatt_entry_type SET icon_name='file' WHERE id_type=108 AND plugin='forms';
UPDATE genatt_entry_type SET icon_name='map-marked-alt' WHERE id_type=109 AND plugin='forms';
UPDATE genatt_entry_type SET icon_name='image' WHERE id_type=110 AND plugin='forms';
UPDATE genatt_entry_type SET icon_name='user' WHERE id_type=111 AND plugin='forms';
UPDATE genatt_entry_type SET icon_name='hashtag' WHERE id_type=112 AND plugin='forms';
UPDATE genatt_entry_type SET icon_name='user' WHERE id_type=113 AND plugin='forms';
UPDATE genatt_entry_type SET icon_name='table' WHERE id_type=114 AND plugin='forms';
UPDATE genatt_entry_type SET icon_name='indent' WHERE id_type=115 AND plugin='forms';
UPDATE genatt_entry_type SET icon_name='gavel' WHERE id_type=116 AND plugin='forms';
UPDATE genatt_entry_type SET icon_name='file' WHERE id_type=117 AND plugin='forms';
