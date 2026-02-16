-- liquibase formatted sql
-- changeset forms:update_db_forms-4.0.0-4.0.1.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA=database() AND TABLE_NAME='forms_form' AND COLUMN_NAME='backup_storage_period';

ALTER TABLE forms_form ADD backup_storage_period INT DEFAULT -1 NOT NULL;

-- changeset forms:update_db_forms-4.0.0-4.0.1-rev1.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA=database() AND TABLE_NAME='forms_step' AND COLUMN_NAME='is_title_displayed';
ALTER TABLE forms_step ADD is_title_displayed SMALLINT default 1 NOT NULL;
UPDATE forms_step SET is_title_displayed=0, title=REPLACE(title, ' - hidden', '') where title like '% - hidden';

-- changeset forms:update_db_forms-4.0.0-4.0.1-rev2.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA=database() AND TABLE_NAME='forms_group' AND COLUMN_NAME='is_title_displayed';
ALTER TABLE forms_group ADD is_title_displayed SMALLINT default 1 NOT NULL;
UPDATE forms_group SET is_title_displayed=0, title=REPLACE(title, ' - hidden', '') where title like '% - hidden';