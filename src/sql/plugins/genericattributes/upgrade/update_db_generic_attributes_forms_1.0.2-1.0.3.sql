--liquibase formatted sql
--changeset forms:update_db_generic_attributes_forms_1.0.2-1.0.3.sql
--preconditions onFail:MARK_RAN onError:WARN
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin) VALUES 
('117', 'Lecture automatique fichier (OCR)', 0, 0, 0, 'forms.entryTypeAutomaticFileReading', 'file', 'forms');

ALTER TABLE forms_question ADD COLUMN is_filterable_multiview_global SMALLINT default 0 NOT NULL;
ALTER TABLE forms_question ADD COLUMN is_filterable_multiview_form_selected SMALLINT default 0 NOT NULL;