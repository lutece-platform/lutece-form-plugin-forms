-- liquibase formatted sql
-- changeset forms:update_db_forms-4.0.2-4.1.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- comment Layout of the questions of a step in the front office: 1 one column, 2 two columns
-- precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA=database() AND TABLE_NAME='forms_step' AND COLUMN_NAME='layout';
ALTER TABLE forms_step ADD COLUMN layout SMALLINT default 1 NOT NULL;

-- changeset forms:update_db_forms-4.0.2-4.1.0.sql-group-layout
-- preconditions onFail:MARK_RAN onError:WARN
-- comment Layout of the questions of a group in the front office: 1 one column, 2 two columns
-- precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA=database() AND TABLE_NAME='forms_group' AND COLUMN_NAME='layout';
ALTER TABLE forms_group ADD COLUMN layout SMALLINT default 1 NOT NULL;

-- changeset forms:update_db_forms-4.0.2-4.1.0.sql-display-column
-- preconditions onFail:MARK_RAN onError:WARN
-- comment Column of the parent step or group where a question or a group is placed, starting at 1
-- precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA=database() AND TABLE_NAME='forms_display' AND COLUMN_NAME='display_column';
ALTER TABLE forms_display ADD COLUMN display_column SMALLINT default 1 NOT NULL;
