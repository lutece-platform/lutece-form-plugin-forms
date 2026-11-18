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

-- Removing a form or a response left behind the workflow rows of its responses (LUT-34919): the following changesets remove that stock.
-- Rows written by a task for such a history go first, then the rows keyed by the response, then the history and the resource themselves.
-- Each table belongs to the workflow plugin or to one of its modules, so each changeset only runs where its table exists.

-- changeset forms:update_db_forms-3.1.4-3.1.6-rev2.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:2 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND table_name IN ( 'workflow_assignment_history', 'workflow_resource_history' )
DELETE FROM workflow_assignment_history WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.4-3.1.6-rev3.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:2 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND table_name IN ( 'workflow_resource_user_history', 'workflow_resource_history' )
DELETE FROM workflow_resource_user_history WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.4-3.1.6-rev4.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:2 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND table_name IN ( 'workflow_task_comment_value', 'workflow_resource_history' )
DELETE FROM workflow_task_comment_value WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.4-3.1.6-rev5.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:2 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND table_name IN ( 'task_alert', 'workflow_resource_history' )
DELETE FROM task_alert WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.4-3.1.6-rev6.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:2 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND table_name IN ( 'workflow_task_assign_user_information', 'workflow_resource_history' )
DELETE FROM workflow_task_assign_user_information WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.4-3.1.6-rev7.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:2 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND table_name IN ( 'workflow_task_change_state_information', 'workflow_resource_history' )
DELETE FROM workflow_task_change_state_information WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.4-3.1.6-rev8.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:2 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND table_name IN ( 'workflow_task_choose_state_information', 'workflow_resource_history' )
DELETE FROM workflow_task_choose_state_information WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.4-3.1.6-rev9.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:2 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND table_name IN ( 'workflow_task_complete_response', 'workflow_resource_history' )
DELETE FROM workflow_task_complete_response WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.4-3.1.6-rev10.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:2 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND table_name IN ( 'workflow_task_complete_response_history', 'workflow_resource_history' )
DELETE FROM workflow_task_complete_response_history WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.4-3.1.6-rev11.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:2 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND table_name IN ( 'workflow_task_complete_response_value', 'workflow_resource_history' )
DELETE FROM workflow_task_complete_response_value WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.4-3.1.6-rev12.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:2 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND table_name IN ( 'workflow_task_forms_editresponse_history', 'workflow_resource_history' )
DELETE FROM workflow_task_forms_editresponse_history WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.4-3.1.6-rev13.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:2 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND table_name IN ( 'workflow_task_notify_gru_history', 'workflow_resource_history' )
DELETE FROM workflow_task_notify_gru_history WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.4-3.1.6-rev14.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:2 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND table_name IN ( 'workflow_task_resubmit_response', 'workflow_resource_history' )
DELETE FROM workflow_task_resubmit_response WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.4-3.1.6-rev15.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:2 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND table_name IN ( 'workflow_task_resubmit_response_history', 'workflow_resource_history' )
DELETE FROM workflow_task_resubmit_response_history WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.4-3.1.6-rev16.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:2 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND table_name IN ( 'workflow_task_resubmit_response_value', 'workflow_resource_history' )
DELETE FROM workflow_task_resubmit_response_value WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.4-3.1.6-rev17.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:2 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND table_name IN ( 'workflow_task_unittree_information', 'workflow_resource_history' )
DELETE FROM workflow_task_unittree_information WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.4-3.1.6-rev18.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:2 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND table_name IN ( 'workflow_task_upload_files', 'workflow_resource_history' )
DELETE FROM workflow_task_upload_files WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.4-3.1.6-rev19.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:2 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND table_name IN ( 'workflow_task_upload_history', 'workflow_resource_history' )
DELETE FROM workflow_task_upload_history WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.4-3.1.6-rev20.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND table_name IN ( 'unittree_unit_assignment' )
DELETE FROM unittree_unit_assignment WHERE resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = unittree_unit_assignment.id_resource );

-- changeset forms:update_db_forms-3.1.4-3.1.6-rev21.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND table_name IN ( 'userassignment_resource_user' )
DELETE FROM userassignment_resource_user WHERE resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = userassignment_resource_user.id_resource );

-- changeset forms:update_db_forms-3.1.4-3.1.6-rev22.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND table_name IN ( 'workflow_resource_workgroup' )
DELETE FROM workflow_resource_workgroup WHERE resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = workflow_resource_workgroup.id_resource );

-- changeset forms:update_db_forms-3.1.4-3.1.6-rev23.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND table_name IN ( 'workflow_resource_history' )
DELETE FROM workflow_resource_history WHERE resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = workflow_resource_history.id_resource );

-- changeset forms:update_db_forms-3.1.4-3.1.6-rev24.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND table_name IN ( 'workflow_resource_workflow' )
DELETE FROM workflow_resource_workflow WHERE resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = workflow_resource_workflow.id_resource );