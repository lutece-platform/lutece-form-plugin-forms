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