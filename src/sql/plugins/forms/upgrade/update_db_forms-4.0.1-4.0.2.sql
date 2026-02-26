-- liquibase formatted sql
-- changeset forms:update_db_forms-4.0.1-4.0.2.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE forms_action SET action_url = 'jsp/admin/plugins/forms/export/DoExportFormJson.jsp' WHERE name_key = 'forms.action.json.download.name';