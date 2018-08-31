INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin) VALUES 
('116', 'Conditions d''utilisation', 0, 0, 0, 'forms.entryTypeTermsOfService', 'legal', 'forms');

ALTER TABLE forms_form ADD COLUMN display_summary SMALLINT default 0 NOT NULL AFTER breadcrumb_name;
