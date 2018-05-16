--
-- Dumping data for table genatt_entry_type
--
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES 
(1,'Bouton radio',0,0,0,'forms.entryTypeRadioButton','forms');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES 
(2,'Case à cocher',0,0,0,'forms.entryTypeCheckBox','forms');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES 
(3,'Commentaire',0,1,0,'forms.entryTypeComment','forms');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES 
(4,'Date',0,0,0,'forms.entryTypeDate','forms');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES 
(5,'Liste déroulante',0,0,0,'forms.entryTypeSelect','forms');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES 
(6,'Zone de texte court',0,0,0,'forms.entryTypeText','forms');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES 
(7,'Zone de texte long',0,0,0,'forms.entryTypeTextArea','forms');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES 
(8,'Fichier',0,0,0,'forms.entryTypeFile','forms');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES 
(9,'Regroupement',1,0,0,'forms.entryTypeGroup','forms');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES
(10,'Géolocalisation',0,0,0,'forms.entryTypeGeolocation','forms');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES
(11,'Image',0,0,0,'forms.entryTypeImage','forms');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES
(12,'Utilisateur MyLutece',0,0,1,'forms.entryTypeMyLuteceUser','forms');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES
(13,'Numérotation',0,0,0,'forms.entryTypeNumbering','forms');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES 
(14,'Cases à cocher obligatoires',0,0,0,'forms.entryTypeMandatoryCheckBox','forms');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES 
(15,'Attribut de l''utilisateur MyLutece',0,0,0,'forms.entryTypeMyLuteceUserattribute','forms');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES 
(16,'Tableau',0,0,0,'forms.entryTypeArray','forms');

-- register entries for the exemple form
INSERT INTO genatt_entry (id_entry, id_resource, resource_type, id_type, id_parent, title, help_message, comment, mandatory, fields_in_line, pos, id_field_depend, confirm_field, confirm_field_title, field_unique) VALUES (1,1,'FORM_FORM_TYPE',1,2,'Quelle licence préférez vous ?','','',0,0,2,NULL,NULL,NULL,NULL);
INSERT INTO genatt_entry (id_entry, id_resource, resource_type, id_type, id_parent, title, help_message, comment, mandatory, fields_in_line, pos, id_field_depend, confirm_field, confirm_field_title, field_unique) VALUES (2,1,'FORM_FORM_TYPE',9,NULL,'Qu''attendez vous d''un CMS open-source ?',NULL,NULL,0,0,1,NULL,NULL,NULL,NULL);
INSERT INTO genatt_entry (id_entry, id_resource, resource_type, id_type, id_parent, title, help_message, comment, mandatory, fields_in_line, pos, id_field_depend, confirm_field, confirm_field_title, field_unique, pos_conditional) VALUES (3,1,'FORM_FORM_TYPE',6,NULL,'Si autre merci de préciser.','','',0,0,0,7,NULL,NULL,NULL,1);
INSERT INTO genatt_entry (id_entry, id_resource, resource_type, id_type, id_parent, title, help_message, comment, mandatory, fields_in_line, pos, id_field_depend, confirm_field, confirm_field_title, field_unique) VALUES (4,1,'FORM_FORM_TYPE',2,2,'Quel éditeur pour ajouter vos contenus ?','','',0,0,3,NULL,NULL,NULL,NULL);
INSERT INTO genatt_entry (id_entry, id_resource, resource_type, id_type, id_parent, title, help_message, comment, mandatory, fields_in_line, pos, id_field_depend, confirm_field, confirm_field_title, field_unique) VALUES (5,1,'FORM_FORM_TYPE',5,2,'Quelle accessibilité pour votre CMS ?','','',0,0,4,NULL,NULL,NULL,NULL);
INSERT INTO genatt_entry (id_entry, id_resource, resource_type, id_type, id_parent, title, help_message, comment, mandatory, fields_in_line, pos, id_field_depend, confirm_field, confirm_field_title, field_unique) VALUES (6,1,'FORM_FORM_TYPE',1,2,'Quelle communauté pour votre CMS ?','','',0,0,5,NULL,NULL,NULL,NULL);
INSERT INTO genatt_entry (id_entry, id_resource, resource_type, id_type, id_parent, title, help_message, comment, mandatory, fields_in_line, pos, id_field_depend, confirm_field, confirm_field_title, field_unique) VALUES (7,1,'FORM_FORM_TYPE',2,2,'Quelle base de données pour votre CMS ?','','',0,0,6,NULL,NULL,NULL,NULL);
INSERT INTO genatt_entry (id_entry, id_resource, resource_type, id_type, id_parent, title, help_message, comment, mandatory, fields_in_line, pos, id_field_depend, confirm_field, confirm_field_title, field_unique) VALUES (8,1,'FORM_FORM_TYPE',7,2,'Autre','','',0,0,7,NULL,NULL,NULL,NULL);

INSERT INTO genatt_field (id_field, id_entry, title, value, height, width, default_value, max_size_enter, pos, value_type_date, no_display_title) VALUES (1,1,'GPL (GNU General Public License).','gpl',0,0,0,0,1,NULL,NULL);
INSERT INTO genatt_field (id_field, id_entry, title, value, height, width, default_value, max_size_enter, pos, value_type_date, no_display_title) VALUES (2,1,'LGPL (Lesser General Public License).','lgpl',0,0,0,0,2,NULL,NULL);
INSERT INTO genatt_field (id_field, id_entry, title, value, height, width, default_value, max_size_enter, pos, value_type_date, no_display_title) VALUES (24,1,'Apache License','ApacheLicense',0,0,0,0,7,NULL,0);
INSERT INTO genatt_field (id_field, id_entry, title, value, height, width, default_value, max_size_enter, pos, value_type_date, no_display_title) VALUES (25,5,'RGAA A','RGAA_A',0,0,0,0,25,NULL,0);
INSERT INTO genatt_field (id_field, id_entry, title, value, height, width, default_value, max_size_enter, pos, value_type_date, no_display_title) VALUES (6,1,'BSD License.','bsd',0,0,1,0,6,NULL,NULL);
INSERT INTO genatt_field (id_field, id_entry, title, value, height, width, default_value, max_size_enter, pos, value_type_date, no_display_title) VALUES (7,1,'Autre','autre',0,0,0,0,24,NULL,0);
INSERT INTO genatt_field (id_field, id_entry, title, value, height, width, default_value, max_size_enter, pos, value_type_date, no_display_title) VALUES (8,3,NULL,'',0,43,0,-1,8,NULL,NULL);
INSERT INTO genatt_field (id_field, id_entry, title, value, height, width, default_value, max_size_enter, pos, value_type_date, no_display_title) VALUES (9,4,'Syntaxe wiki','wiki',0,0,0,0,9,NULL,NULL);
INSERT INTO genatt_field (id_field, id_entry, title, value, height, width, default_value, max_size_enter, pos, value_type_date, no_display_title) VALUES (10,4,'Editeur riche (wysiwyg)','wysiwyg',0,0,1,0,10,NULL,NULL);
INSERT INTO genatt_field (id_field, id_entry, title, value, height, width, default_value, max_size_enter, pos, value_type_date, no_display_title) VALUES (11,4,'Html/Css/Javascript','html',0,0,1,0,11,NULL,NULL);
INSERT INTO genatt_field (id_field, id_entry, title, value, height, width, default_value, max_size_enter, pos, value_type_date, no_display_title) VALUES (12,5,'Aucune contrainte','empty',0,0,0,0,12,NULL,NULL);
INSERT INTO genatt_field (id_field, id_entry, title, value, height, width, default_value, max_size_enter, pos, value_type_date, no_display_title) VALUES (27,5,'RGAA AAA','RGAA_AAA',0,0,0,0,27,NULL,0);
INSERT INTO genatt_field (id_field, id_entry, title, value, height, width, default_value, max_size_enter, pos, value_type_date, no_display_title) VALUES (26,5,'RGAA AA','RGAA_AA',0,0,1,0,26,NULL,0);
INSERT INTO genatt_field (id_field, id_entry, title, value, height, width, default_value, max_size_enter, pos, value_type_date, no_display_title) VALUES (16,6,'Offre avec double licence libre/payant','entreprise',0,0,0,0,16,NULL,NULL);
INSERT INTO genatt_field (id_field, id_entry, title, value, height, width, default_value, max_size_enter, pos, value_type_date, no_display_title) VALUES (17,6,'Une communauté de SSII et collectivités','lutece',0,0,1,0,17,NULL,NULL);
INSERT INTO genatt_field (id_field, id_entry, title, value, height, width, default_value, max_size_enter, pos, value_type_date, no_display_title) VALUES (18,6,'Je préfère ne pas savoir','none',0,0,0,0,18,NULL,NULL);
INSERT INTO genatt_field (id_field, id_entry, title, value, height, width, default_value, max_size_enter, pos, value_type_date, no_display_title) VALUES (19,7,'MySql','MySql',0,0,1,0,19,NULL,NULL);
INSERT INTO genatt_field (id_field, id_entry, title, value, height, width, default_value, max_size_enter, pos, value_type_date, no_display_title) VALUES (20,7,'Oracle','oracle',0,0,1,0,20,NULL,NULL);
INSERT INTO genatt_field (id_field, id_entry, title, value, height, width, default_value, max_size_enter, pos, value_type_date, no_display_title) VALUES (21,7,'Postgre','postgre',0,0,1,0,21,NULL,NULL);
INSERT INTO genatt_field (id_field, id_entry, title, value, height, width, default_value, max_size_enter, pos, value_type_date, no_display_title) VALUES (22,8,NULL,'',4,26,0,50,22,NULL,NULL);
INSERT INTO genatt_field (id_field, id_entry, title, value, height, width, default_value, max_size_enter, pos, value_type_date, no_display_title) VALUES (23,7,'SqlServer (Microsoft)','sqlserver',0,0,0,0,23,NULL,NULL);
