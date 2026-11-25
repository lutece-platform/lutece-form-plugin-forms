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

-- changeset forms:update_db_forms-4.0.2-4.1.0.sql-lucene-lock-columns-mariaDB-mySQL dbms:mariadb,mysql
-- preconditions onFail:MARK_RAN onError:WARN
-- comment Instance names longer than 50 characters (moved from update_db_forms-4.0.1-4.0.2.sql: plugin-forms 4.0.2 is released, a site already in 4.0.2 never plays that file again)
ALTER TABLE forms_lucene_lock MODIFY instance_name VARCHAR(255);
ALTER TABLE forms_lucene_lock MODIFY index_name VARCHAR(255);
ALTER TABLE forms_lucene_lock MODIFY uuid VARCHAR(255);

-- changeset forms:update_db_forms-4.0.2-4.1.0.sql-lucene-lock-columns-postgreSQL dbms:postgresql
-- preconditions onFail:MARK_RAN onError:WARN
-- comment Same change in the PostgreSQL syntax (MODIFY does not exist there)
ALTER TABLE forms_lucene_lock ALTER COLUMN instance_name TYPE VARCHAR(255);
ALTER TABLE forms_lucene_lock ALTER COLUMN index_name TYPE VARCHAR(255);
ALTER TABLE forms_lucene_lock ALTER COLUMN uuid TYPE VARCHAR(255);

-- changeset forms:update_db_forms-4.0.2-4.1.0.sql-admin-right-group
-- preconditions onFail:MARK_RAN onError:WARN
-- comment Feature group of the forms rights (moved from update_db_forms-4.0.1-4.0.2.sql for the same reason); no right is granted
UPDATE core_admin_right SET id_feature_group = 'APPLICATIONS' WHERE id_right IN ( 'FORMS_MANAGEMENT', 'FORMS_MULTIVIEW', 'FORMS_CATEGORIES', 'FORMS_SEARCH_INDEXATION' );
