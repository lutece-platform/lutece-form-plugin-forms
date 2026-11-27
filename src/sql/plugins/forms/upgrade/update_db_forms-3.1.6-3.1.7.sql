-- liquibase formatted sql
-- changeset forms:update_db_forms-3.1.6-3.1.7.sql dbms:mysql,mariadb
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema = database() AND table_name = 'forms_group' AND index_name = 'index_fgroup_id_step'

-- index_fg_id_step was declared on forms_question instead of forms_group, so forms_group.id_step had no index and forms_question.id_step had two.
-- A new name is used rather than the mistaken one : index names are global to the schema on PostgreSQL, and the old index still holds that name.
-- The redundant index is left where it is, dropping it needs one statement per dialect for no gain on a table of this size.
CREATE INDEX index_fgroup_id_step ON forms_group ( id_step );

-- changeset forms:update_db_forms-3.1.6-3.1.7-rev1.sql dbms:postgresql,hsqldb
-- The query above only answers on MySQL and MariaDB : elsewhere it fails, and the index was created even where it already existed, which
-- stopped the upgrade. These databases skip an existing index themselves.
CREATE INDEX IF NOT EXISTS index_fgroup_id_step ON forms_group ( id_step );
