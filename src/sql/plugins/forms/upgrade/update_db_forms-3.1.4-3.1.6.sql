-- liquibase formatted sql
-- changeset forms:update_db_forms-3.1.4-3.1.6.sql
-- preconditions onFail:MARK_RAN onError:WARN

DROP TABLE IF EXISTS forms_lucene_lock;

CREATE TABLE forms_lucene_lock (
index_name varchar(50),
instance_name varchar(50),
is_locked smallint,
date_begin timestamp,
expired_date timestamp,
uuid varchar(50),
PRIMARY KEY (index_name)
);

INSERT INTO forms_lucene_lock
(index_name, instance_name, is_locked, date_begin, expired_date, uuid)
VALUES
    ('forms.lucene.lock', NULL, 0, NULL, NULL, NULL);

-- changeset forms:update_db_forms-3.1.4-3.1.6-rev1.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema = database() AND table_name = 'forms_group' AND index_name = 'index_fgroup_id_step'

-- index_fg_id_step was declared on forms_question instead of forms_group, so forms_group.id_step had no index and forms_question.id_step had two.
-- A new name is used rather than the mistaken one : index names are global to the schema on PostgreSQL, and the old index still holds that name.
-- The redundant index is left where it is, dropping it needs one statement per dialect for no gain on a table of this size.
CREATE INDEX index_fgroup_id_step ON forms_group ( id_step );