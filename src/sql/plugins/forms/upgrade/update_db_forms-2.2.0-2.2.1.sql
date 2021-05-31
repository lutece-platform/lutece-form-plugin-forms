ALTER TABLE forms_form ADD COLUMN label_final_button VARCHAR(255);
ALTER TABLE forms_question ADD COLUMN multiview_column_order INT default 0 NOT NULL;
INSERT INTO forms_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (6, 'forms.action.json.copy.name', 'forms.json.copy.description', 'jsp/admin/plugins/forms/ManageForms.jsp?action=duplicateForm', 'copy', 'MODIFY', 0);
INSERT INTO forms_action (id_action, name_key, description_key, action_url, icon_url, action_permission, form_state) VALUES (7, 'forms.action.json.download.name', 'forms.json.download.description', 'jsp/admin/plugins/forms/ManageForms.jsp?action=doExportJson', 'download', 'MODIFY', 0);
