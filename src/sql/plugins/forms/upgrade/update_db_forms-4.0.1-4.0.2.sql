-- liquibase formatted sql
-- changeset forms:update_db_forms-4.0.1-4.0.2.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE forms_action SET action_url = 'jsp/admin/plugins/forms/export/DoExportFormJson.jsp' WHERE name_key = 'forms.action.json.download.name';

-- changeset forms:update_db_forms-4.0.1-4.0.2.sql-lucene-lock
-- preconditions onFail:MARK_RAN onError:WARN
-- comment Moved from update_db_forms-4.0.2-4.0.3.sql (this table belongs to the 4.0.2 release): skipped where the 4.0.2 betas created it, so the lock held by an indexing instance is kept
-- precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA=database() AND TABLE_NAME='forms_lucene_lock';
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
    'core.cache.status.formsCacheService.timeToIdleSeconds' );
-- a size tuned by the admin is kept
INSERT INTO core_datastore (entity_key, entity_value) SELECT 'core.cache.status.formsCacheService.maxElementsInMemory', '20000' FROM core_datastore
    WHERE entity_key = 'core.cache.status.formsCacheService.maxElementsInMemory' HAVING COUNT(*) = 0;

-- changeset forms:update_db_forms-4.0.1-4.0.2.sql-decode-entities
-- preconditions onFail:MARK_RAN onError:WARN
-- comment LUT-34111 : decodes, once, the HTML entities the removed input filter of lutece-core 8 (sanitizeFilterMode, LUT-34109) wrote into the plain text values of the forms. The values typed with the rich text editor (unavailable and end messages, group description) are left as they are.
UPDATE forms_form SET title = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(title, '&#39;', ''''), '&#x27;', ''''), '&#34;', '"'), '&quot;', '"'), '&#96;', '`'), '&#61;', '='), '&#64;', '@'), '&#43;', '+'), '&lt;', '<'), '&gt;', '>'), '&amp;', '&') WHERE title LIKE '%&%;%';
UPDATE forms_form SET description = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(description, '&#39;', ''''), '&#x27;', ''''), '&#34;', '"'), '&quot;', '"'), '&#96;', '`'), '&#61;', '='), '&#64;', '@'), '&#43;', '+'), '&lt;', '<'), '&gt;', '>'), '&amp;', '&') WHERE description LIKE '%&%;%';
UPDATE forms_form SET breadcrumb_name = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(breadcrumb_name, '&#39;', ''''), '&#x27;', ''''), '&#34;', '"'), '&quot;', '"'), '&#96;', '`'), '&#61;', '='), '&#64;', '@'), '&#43;', '+'), '&lt;', '<'), '&gt;', '>'), '&amp;', '&') WHERE breadcrumb_name LIKE '%&%;%';
UPDATE forms_form SET label_final_button = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(label_final_button, '&#39;', ''''), '&#x27;', ''''), '&#34;', '"'), '&quot;', '"'), '&#96;', '`'), '&#61;', '='), '&#64;', '@'), '&#43;', '+'), '&lt;', '<'), '&gt;', '>'), '&amp;', '&') WHERE label_final_button LIKE '%&%;%';
UPDATE forms_form SET return_url = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(return_url, '&#39;', ''''), '&#x27;', ''''), '&#34;', '"'), '&quot;', '"'), '&#96;', '`'), '&#61;', '='), '&#64;', '@'), '&#43;', '+'), '&lt;', '<'), '&gt;', '>'), '&amp;', '&') WHERE return_url LIKE '%&%;%';
UPDATE forms_step SET title = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(title, '&#39;', ''''), '&#x27;', ''''), '&#34;', '"'), '&quot;', '"'), '&#96;', '`'), '&#61;', '='), '&#64;', '@'), '&#43;', '+'), '&lt;', '<'), '&gt;', '>'), '&amp;', '&') WHERE title LIKE '%&%;%';
UPDATE forms_step SET description = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(description, '&#39;', ''''), '&#x27;', ''''), '&#34;', '"'), '&quot;', '"'), '&#96;', '`'), '&#61;', '='), '&#64;', '@'), '&#43;', '+'), '&lt;', '<'), '&gt;', '>'), '&amp;', '&') WHERE description LIKE '%&%;%';
UPDATE forms_question SET title = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(title, '&#39;', ''''), '&#x27;', ''''), '&#34;', '"'), '&quot;', '"'), '&#96;', '`'), '&#61;', '='), '&#64;', '@'), '&#43;', '+'), '&lt;', '<'), '&gt;', '>'), '&amp;', '&') WHERE title LIKE '%&%;%';
UPDATE forms_question SET description = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(description, '&#39;', ''''), '&#x27;', ''''), '&#34;', '"'), '&quot;', '"'), '&#96;', '`'), '&#61;', '='), '&#64;', '@'), '&#43;', '+'), '&lt;', '<'), '&gt;', '>'), '&amp;', '&') WHERE description LIKE '%&%;%';
UPDATE forms_question SET column_title = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(column_title, '&#39;', ''''), '&#x27;', ''''), '&#34;', '"'), '&quot;', '"'), '&#96;', '`'), '&#61;', '='), '&#64;', '@'), '&#43;', '+'), '&lt;', '<'), '&gt;', '>'), '&amp;', '&') WHERE column_title LIKE '%&%;%';
UPDATE forms_group SET title = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(title, '&#39;', ''''), '&#x27;', ''''), '&#34;', '"'), '&quot;', '"'), '&#96;', '`'), '&#61;', '='), '&#64;', '@'), '&#43;', '+'), '&lt;', '<'), '&gt;', '>'), '&amp;', '&') WHERE title LIKE '%&%;%';
UPDATE forms_group SET iteration_add_label = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(iteration_add_label, '&#39;', ''''), '&#x27;', ''''), '&#34;', '"'), '&quot;', '"'), '&#96;', '`'), '&#61;', '='), '&#64;', '@'), '&#43;', '+'), '&lt;', '<'), '&gt;', '>'), '&amp;', '&') WHERE iteration_add_label LIKE '%&%;%';
UPDATE forms_group SET iteration_remove_label = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(iteration_remove_label, '&#39;', ''''), '&#x27;', ''''), '&#34;', '"'), '&quot;', '"'), '&#96;', '`'), '&#61;', '='), '&#64;', '@'), '&#43;', '+'), '&lt;', '<'), '&gt;', '>'), '&amp;', '&') WHERE iteration_remove_label LIKE '%&%;%';
UPDATE forms_control SET error_message = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(error_message, '&#39;', ''''), '&#x27;', ''''), '&#34;', '"'), '&quot;', '"'), '&#96;', '`'), '&#61;', '='), '&#64;', '@'), '&#43;', '+'), '&lt;', '<'), '&gt;', '>'), '&amp;', '&') WHERE error_message LIKE '%&%;%';
UPDATE forms_message SET label_end_message_button = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(label_end_message_button, '&#39;', ''''), '&#x27;', ''''), '&#34;', '"'), '&quot;', '"'), '&#96;', '`'), '&#61;', '='), '&#64;', '@'), '&#43;', '+'), '&lt;', '<'), '&gt;', '>'), '&amp;', '&') WHERE label_end_message_button LIKE '%&%;%';
UPDATE forms_category SET name = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(name, '&#39;', ''''), '&#x27;', ''''), '&#34;', '"'), '&quot;', '"'), '&#96;', '`'), '&#61;', '='), '&#64;', '@'), '&#43;', '+'), '&lt;', '<'), '&gt;', '>'), '&amp;', '&') WHERE name LIKE '%&%;%';
