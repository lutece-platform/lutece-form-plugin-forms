-- liquibase formatted sql
-- changeset forms:update_db_forms-4.0.1-4.0.2.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE forms_action SET action_url = 'jsp/admin/plugins/forms/export/DoExportFormJson.jsp' WHERE name_key = 'forms.action.json.download.name';

-- changeset forms:update_db_forms-4.0.1-4.0.2.sql-lucene-lock
-- preconditions onFail:MARK_RAN onError:WARN
-- Moved from update_db_forms-4.0.2-4.0.3.sql: this table belongs to the 4.0.2 release
DROP TABLE IF EXISTS forms_lucene_lock;

CREATE TABLE forms_lucene_lock
(
    index_name    varchar(50),
    instance_name varchar(50),
    is_locked     smallint,
    date_begin    timestamp,
    expired_date  timestamp,
    uuid          varchar(50),
    PRIMARY KEY (index_name)
);

INSERT INTO forms_lucene_lock
    (index_name, instance_name, is_locked, date_begin, expired_date, uuid)
VALUES ('forms.lucene.lock', NULL, 0, NULL, NULL, NULL);

-- changeset forms:update_db_forms-4.0.1-4.0.2.sql-cache-settings
-- preconditions onFail:MARK_RAN onError:WARN
-- Bounded forms cache (about 40 entries per form); overflowToDisk (Ehcache 2) and the time to idle have no effect
DELETE FROM core_datastore WHERE entity_key IN ( 'core.cache.status.formsCacheService.overflowToDisk',
    'core.cache.status.formsCacheService.timeToIdleSeconds', 'core.cache.status.formsCacheService.maxElementsInMemory' );
INSERT INTO core_datastore VALUES ( 'core.cache.status.formsCacheService.maxElementsInMemory', '20000' );
