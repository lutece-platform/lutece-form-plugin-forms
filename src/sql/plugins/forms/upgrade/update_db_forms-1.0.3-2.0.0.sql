-- liquibase formatted sql
-- changeset forms:update_db_forms-1.0.3-2.0.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE forms_question q 
SET q.is_filterable_multiview_global = 0, q.is_filterable_multiview_form_selected = 0
WHERE q.id_entry IN (
SELECT e.id_entry FROM genatt_entry e WHERE e.id_type IN (
SELECT et.id_type FROM genatt_entry_type et WHERE et.class_name IN ('forms.entryTypeText', 'forms.entryTypeTextArea')));

ALTER TABLE forms_form MODIFY COLUMN availability_start_date timestamp;
ALTER TABLE forms_form MODIFY COLUMN availability_end_date timestamp;

-- changeset forms:update_db_forms-1.0.3-2.0.0_question_filters
-- preconditions onFail:MARK_RAN onError:WARN
-- LUT-33260 : moved from sql/plugins/genericattributes/upgrade/update_db_generic_attributes_forms_1.0.2-1.0.3.sql

ALTER TABLE forms_question ADD COLUMN is_filterable_multiview_global SMALLINT default 0 NOT NULL;
ALTER TABLE forms_question ADD COLUMN is_filterable_multiview_form_selected SMALLINT default 0 NOT NULL;