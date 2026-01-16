-- liquibase formatted sql
-- changeset forms:update_db_forms-3.1.3-3.1.4.sql
-- preconditions onFail:MARK_RAN onError:WARN

ALTER TABLE forms_form ADD backup_storage_period INT DEFAULT -1 NOT NULL;