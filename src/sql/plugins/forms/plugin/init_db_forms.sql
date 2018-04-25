--
-- Dumping data for table `forms_action`
--
INSERT INTO forms_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (1,'forms.action.modify.name','forms.action.modify.description','jsp/admin/plugins/forms/ManageForms.jsp?view=modifyForm','edit','MODIFY',0);
INSERT INTO forms_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (13,'forms.action.copy.name','forms.action.copy.description','jsp/admin/plugins/forms/ManageForms.jsp?action=duplicateForm','copy','COPY',0);
INSERT INTO forms_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (17,'forms.action.delete.name','forms.action.delete.description','jsp/admin/plugins/forms/ManageForms.jsp?view=confirmRemoveForm','trash','DELETE',0);
