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

-- changeset forms:update_db_forms-3.1.4-3.1.6.sql-control-active
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA=database() AND TABLE_NAME='forms_control' AND COLUMN_NAME='is_active';
-- comment The conditional displays which use a disabled question are inactive
ALTER TABLE forms_control ADD COLUMN is_active SMALLINT default 1 NOT NULL;

UPDATE forms_control SET is_active = 0 WHERE control_type = 'conditional' AND id_control IN (
    SELECT fcq.id_control FROM forms_control_question fcq
    JOIN forms_question fq ON fq.id_question = fcq.id_question
    JOIN genatt_field gf ON gf.id_entry = fq.id_entry
    WHERE gf.code = 'disabled' AND gf.value = 'true' );
