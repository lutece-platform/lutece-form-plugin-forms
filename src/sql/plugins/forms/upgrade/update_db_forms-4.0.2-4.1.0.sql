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

-- changeset forms:update_db_forms-4.0.2-4.1.0.sql-decode-entities
-- preconditions onFail:MARK_RAN onError:WARN
-- comment LUT-34111 : decodes, once, the HTML entities the removed input filter of lutece-core 8 (sanitizeFilterMode, LUT-34109) wrote into the plain text values of the forms. The values typed with the rich text editor (unavailable and end messages, group description) are left as they are. Moved from update_db_forms-4.0.1-4.0.2.sql (not in the released 4.0.2): skipped where a develop build already ran it under its former id.
-- precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM DATABASECHANGELOG WHERE ID='update_db_forms-4.0.1-4.0.2.sql-decode-entities';
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
