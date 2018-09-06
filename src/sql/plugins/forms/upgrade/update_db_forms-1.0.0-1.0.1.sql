INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin) VALUES 
('116', 'Conditions d''utilisation', 0, 0, 0, 'forms.entryTypeTermsOfService', 'legal', 'forms');

ALTER TABLE forms_form ADD COLUMN display_summary SMALLINT default 0 NOT NULL AFTER breadcrumb_name;

ALTER TABLE forms_group DROP COLUMN iteration_number;
ALTER TABLE forms_group	ADD COLUMN iteration_min INT  DEFAULT '1' AFTER collapsible;
ALTER TABLE forms_group	ADD COLUMN iteration_max INT  DEFAULT '1' AFTER iteration_min;
ALTER TABLE forms_group	ADD COLUMN iteration_label varchar(255) default '' AFTER iteration_max;

ALTER TABLE forms_question	CHANGE COLUMN description description LONGTEXT NULL DEFAULT '' COLLATE 'utf8_unicode_ci' AFTER title;