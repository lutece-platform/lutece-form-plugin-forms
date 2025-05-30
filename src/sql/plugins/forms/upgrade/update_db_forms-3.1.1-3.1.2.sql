--liquibase formatted sql
--changeset forms:update_db_forms-3.1.1-3.1.2.sql
--preconditions onFail:MARK_RAN onError:WARN

UPDATE forms_action SET action_url = 'jsp/admin/plugins/forms/ManageForms.jsp?view=modifyForm', icon_url='cog' WHERE name_key = 'forms.action.params.name';
UPDATE forms_action SET action_url = 'jsp/admin/plugins/forms/ManageSteps.jsp?view=manageSteps', icon_url='step-forward' WHERE name_key = 'forms.action.modify.name';
