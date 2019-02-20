--
-- Dumping data for table `forms_action`
--
INSERT INTO forms_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (1,'forms.action.modify.name','forms.action.modify.description','jsp/admin/plugins/forms/ManageSteps.jsp?view=manageSteps','edit','MODIFY',0);
INSERT INTO forms_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (17,'forms.action.delete.name','forms.action.delete.description','jsp/admin/plugins/forms/ManageForms.jsp?view=confirmRemoveForm','trash','DELETE',0);

--
-- Datastore config values
--
INSERT INTO core_datastore ( entity_key, entity_value ) VALUES
    ( 'forms.display.form.columnTitle', 'true' );

INSERT INTO forms_global_action (id_action, code, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (1,'multiviewconfig','forms.action.multiviewConfig.labelKey','forms.action.multiviewConfig.descriptionKey','jsp/admin/plugins/forms/MultiviewForms.jsp?view=view_multiview_config','edit');
