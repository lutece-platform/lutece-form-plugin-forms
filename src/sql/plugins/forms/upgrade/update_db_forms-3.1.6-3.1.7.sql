-- liquibase formatted sql
-- Removing a form or a response left behind the workflow rows of its responses (LUT-34919): the changesets after the index remove that stock.
-- Rows written by a task for such a history go first, then the rows keyed by the response, then the history and the resource themselves.
-- Each table belongs to the workflow plugin or to one of its modules, so each changeset only runs where its table exists.

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

-- changeset forms:update_db_forms-3.1.6-3.1.7-rev2.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-table-exists table:workflow_assignment_history
-- precondition-table-exists table:workflow_resource_history
DELETE FROM workflow_assignment_history WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.6-3.1.7-rev3.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-table-exists table:workflow_resource_user_history
-- precondition-table-exists table:workflow_resource_history
DELETE FROM workflow_resource_user_history WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.6-3.1.7-rev4.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-table-exists table:workflow_task_comment_value
-- precondition-table-exists table:workflow_resource_history
DELETE FROM workflow_task_comment_value WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.6-3.1.7-rev5.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-table-exists table:task_alert
-- precondition-table-exists table:workflow_resource_history
DELETE FROM task_alert WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.6-3.1.7-rev6.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-table-exists table:workflow_task_assign_user_information
-- precondition-table-exists table:workflow_resource_history
DELETE FROM workflow_task_assign_user_information WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.6-3.1.7-rev7.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-table-exists table:workflow_task_change_state_information
-- precondition-table-exists table:workflow_resource_history
DELETE FROM workflow_task_change_state_information WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.6-3.1.7-rev8.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-table-exists table:workflow_task_choose_state_information
-- precondition-table-exists table:workflow_resource_history
DELETE FROM workflow_task_choose_state_information WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.6-3.1.7-rev9.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-table-exists table:workflow_task_complete_response
-- precondition-table-exists table:workflow_resource_history
DELETE FROM workflow_task_complete_response WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.6-3.1.7-rev10.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-table-exists table:workflow_task_complete_response_history
-- precondition-table-exists table:workflow_resource_history
DELETE FROM workflow_task_complete_response_history WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.6-3.1.7-rev11.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-table-exists table:workflow_task_complete_response_value
-- precondition-table-exists table:workflow_resource_history
DELETE FROM workflow_task_complete_response_value WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.6-3.1.7-rev12.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-table-exists table:workflow_task_forms_editresponse_history
-- precondition-table-exists table:workflow_resource_history
DELETE FROM workflow_task_forms_editresponse_history WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.6-3.1.7-rev13.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-table-exists table:workflow_task_notify_gru_history
-- precondition-table-exists table:workflow_resource_history
DELETE FROM workflow_task_notify_gru_history WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.6-3.1.7-rev14.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-table-exists table:workflow_task_resubmit_response
-- precondition-table-exists table:workflow_resource_history
DELETE FROM workflow_task_resubmit_response WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.6-3.1.7-rev15.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-table-exists table:workflow_task_resubmit_response_history
-- precondition-table-exists table:workflow_resource_history
DELETE FROM workflow_task_resubmit_response_history WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.6-3.1.7-rev16.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-table-exists table:workflow_task_resubmit_response_value
-- precondition-table-exists table:workflow_resource_history
DELETE FROM workflow_task_resubmit_response_value WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.6-3.1.7-rev17.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-table-exists table:workflow_task_unittree_information
-- precondition-table-exists table:workflow_resource_history
DELETE FROM workflow_task_unittree_information WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.6-3.1.7-rev18.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-table-exists table:workflow_task_upload_files
-- precondition-table-exists table:workflow_resource_history
DELETE FROM workflow_task_upload_files WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.6-3.1.7-rev19.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-table-exists table:workflow_task_upload_history
-- precondition-table-exists table:workflow_resource_history
DELETE FROM workflow_task_upload_history WHERE id_history IN ( SELECT h.id_history FROM workflow_resource_history h WHERE h.resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = h.id_resource ) );

-- changeset forms:update_db_forms-3.1.6-3.1.7-rev20.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-table-exists table:unittree_unit_assignment
DELETE FROM unittree_unit_assignment WHERE resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = unittree_unit_assignment.id_resource );

-- changeset forms:update_db_forms-3.1.6-3.1.7-rev21.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-table-exists table:userassignment_resource_user
DELETE FROM userassignment_resource_user WHERE resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = userassignment_resource_user.id_resource );

-- changeset forms:update_db_forms-3.1.6-3.1.7-rev22.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-table-exists table:workflow_resource_workgroup
DELETE FROM workflow_resource_workgroup WHERE resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = workflow_resource_workgroup.id_resource );

-- changeset forms:update_db_forms-3.1.6-3.1.7-rev23.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-table-exists table:workflow_resource_history
DELETE FROM workflow_resource_history WHERE resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = workflow_resource_history.id_resource );

-- changeset forms:update_db_forms-3.1.6-3.1.7-rev24.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-table-exists table:workflow_resource_workflow
DELETE FROM workflow_resource_workflow WHERE resource_type = 'FORMS_FORM_RESPONSE' AND NOT EXISTS ( SELECT 1 FROM forms_response r WHERE r.id_response = workflow_resource_workflow.id_resource );
