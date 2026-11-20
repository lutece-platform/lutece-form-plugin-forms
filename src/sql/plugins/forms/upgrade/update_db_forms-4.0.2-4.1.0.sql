-- liquibase formatted sql
-- changeset forms:update_db_forms-4.0.2-4.1.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA=database() AND TABLE_NAME='forms_control' AND COLUMN_NAME='is_active';
-- comment The conditional displays which use a disabled question are inactive
ALTER TABLE forms_control ADD COLUMN is_active SMALLINT default 1 NOT NULL;

UPDATE forms_control SET is_active = 0 WHERE control_type = 'conditional' AND id_control IN (
    SELECT fcq.id_control FROM forms_control_question fcq
    JOIN forms_question fq ON fq.id_question = fcq.id_question
    JOIN genatt_field gf ON gf.id_entry = fq.id_entry
    WHERE gf.code = 'disabled' AND gf.value = 'true' );
