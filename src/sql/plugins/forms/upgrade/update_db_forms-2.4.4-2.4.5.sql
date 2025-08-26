-- liquibase formatted sql
-- changeset forms:update_db_forms-2.4.4-2.4.5.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- configure forms cache
DELETE FROM core_datastore WHERE entity_key='core.cache.status.formsCacheService.enabled';
INSERT INTO core_datastore VALUES ( 'core.cache.status.formsCacheService.enabled', '1' );
DELETE FROM core_datastore WHERE entity_key='core.cache.status.formsCacheService.timeToIdleSeconds';
INSERT INTO core_datastore VALUES ( 'core.cache.status.formsCacheService.timeToIdleSeconds', '86400' );
DELETE FROM core_datastore WHERE entity_key='core.cache.status.formsCacheService.timeToLiveSeconds';
INSERT INTO core_datastore VALUES ( 'core.cache.status.formsCacheService.timeToLiveSeconds', '86400' );
DELETE FROM core_datastore WHERE entity_key='core.cache.status.formsCacheService.overflowToDisk';
INSERT INTO core_datastore VALUES ( 'core.cache.status.formsCacheService.overflowToDisk', '0' );
