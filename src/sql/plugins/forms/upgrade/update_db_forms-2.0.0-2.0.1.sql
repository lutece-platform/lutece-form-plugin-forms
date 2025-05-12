--liquibase formatted sql
--changeset forms:update_db_forms-2.0.0-2.0.1.sql
--preconditions onFail:MARK_RAN onError:WARN
UPDATE `forms_action` SET `action_permission`='PUBLISH' WHERE `id_action`=4;
