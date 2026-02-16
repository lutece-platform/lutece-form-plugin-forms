-- liquibase formatted sql
-- changeset forms:update_db_forms-3.1.3-3.1.4.sql
-- preconditions onFail:MARK_RAN onError:WARN

ALTER TABLE forms_form ADD backup_storage_period INT DEFAULT -1 NOT NULL;

ALTER TABLE forms_step ADD is_title_displayed SMALLINT default 1 NOT NULL;
ALTER TABLE forms_group ADD is_title_displayed SMALLINT default 1 NOT NULL;

UPDATE forms_step SET is_title_displayed=0, title=REPLACE(title, ' - hidden', '') where title like '% - hidden';
UPDATE forms_group SET is_title_displayed=0, title=REPLACE(title, ' - hidden', '') where title like '% - hidden';