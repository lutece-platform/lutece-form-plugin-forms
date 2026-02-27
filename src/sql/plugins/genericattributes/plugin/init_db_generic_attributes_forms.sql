-- liquibase formatted sql
-- changeset forms:init_db_generic_attributes_forms.sql
-- preconditions onFail:MARK_RAN onError:WARN
--
-- Dumping data for table genatt_entry_type
--
INSERT INTO genatt_entry_type (id_type, title, is_group, is_comment, is_mylutece_user, class_name, icon_name, plugin, display_order, inactive) VALUES (1, 'Bouton radio', 0, 0, 0, 'forms.entryTypeRadioButton', 'circle-dot', 'forms', 5, 0);
INSERT INTO genatt_entry_type (id_type, title, is_group, is_comment, is_mylutece_user, class_name, icon_name, plugin, display_order, inactive) VALUES (2, 'Case à cocher', 0, 0, 0, 'forms.entryTypeCheckBox', 'square-check', 'forms', 6, 0);
INSERT INTO genatt_entry_type (id_type, title, is_group, is_comment, is_mylutece_user, class_name, icon_name, plugin, display_order, inactive) VALUES (3, 'Commentaire', 0, 1, 0, 'forms.entryTypeComment', 'comment', 'forms', 11, 0);
INSERT INTO genatt_entry_type (id_type, title, is_group, is_comment, is_mylutece_user, class_name, icon_name, plugin, display_order, inactive) VALUES (4, 'Date', 0, 0, 0, 'forms.entryTypeDate', 'calendar', 'forms', 4, 0);
INSERT INTO genatt_entry_type (id_type, title, is_group, is_comment, is_mylutece_user, class_name, icon_name, plugin, display_order, inactive) VALUES (5, 'Liste déroulante', 0, 0, 0, 'forms.entryTypeSelect', 'select', 'forms', 7, 0);
INSERT INTO genatt_entry_type (id_type, title, is_group, is_comment, is_mylutece_user, class_name, icon_name, plugin, display_order, inactive) VALUES (6, 'Zone de texte court', 0, 0, 0, 'forms.entryTypeText', 'forms', 'forms', 2, 0);
INSERT INTO genatt_entry_type (id_type, title, is_group, is_comment, is_mylutece_user, class_name, icon_name, plugin, display_order, inactive) VALUES (7, 'Zone de texte long', 0, 0, 0, 'forms.entryTypeTextArea', 'align-box-left-top', 'forms', 3, 0);
INSERT INTO genatt_entry_type (id_type, title, is_group, is_comment, is_mylutece_user, class_name, icon_name, plugin, display_order, inactive) VALUES (8, 'Fichier', 0, 0, 0, 'forms.entryTypeFile', 'file', 'forms', 9, 0);
INSERT INTO genatt_entry_type (id_type, title, is_group, is_comment, is_mylutece_user, class_name, icon_name, plugin, display_order, inactive) VALUES (9, 'Géolocalisation', 0, 0, 0, 'forms.entryTypeGeolocation', 'map-pin', 'forms', 16, 0);
INSERT INTO genatt_entry_type (id_type, title, is_group, is_comment, is_mylutece_user, class_name, icon_name, plugin, display_order, inactive) VALUES (10, 'Image', 0, 0, 0, 'forms.entryTypeImage', 'photo', 'forms', 10, 0);
INSERT INTO genatt_entry_type (id_type, title, is_group, is_comment, is_mylutece_user, class_name, icon_name, plugin, display_order, inactive) VALUES (11, 'Utilisateur MyLutece', 0, 0, 1, 'forms.entryTypeMyLuteceUser', 'user', 'forms', 13, 1);
INSERT INTO genatt_entry_type (id_type, title, is_group, is_comment, is_mylutece_user, class_name, icon_name, plugin, display_order, inactive) VALUES (12, 'Numérotation', 0, 0, 0, 'forms.entryTypeNumbering', 'hash', 'forms', 8, 0);
INSERT INTO genatt_entry_type (id_type, title, is_group, is_comment, is_mylutece_user, class_name, icon_name, plugin, display_order, inactive) VALUES (13, 'Attribut de l\'utilisateur MyLutece', 0, 0, 0, 'forms.entryTypeMyLuteceUserattribute', 'user', 'forms', 14, 0);
INSERT INTO genatt_entry_type (id_type, title, is_group, is_comment, is_mylutece_user, class_name, icon_name, plugin, display_order, inactive) VALUES (14, 'Tableau', 0, 0, 0, 'forms.entryTypeArray', 'terminal', 'forms', 17, 1);
INSERT INTO genatt_entry_type (id_type, title, is_group, is_comment, is_mylutece_user, class_name, icon_name, plugin, display_order, inactive) VALUES (15, 'Regroupement', 1, 0, 0, 'forms.entryTypeGroup', 'info-circle', 'forms', 1, 0);
INSERT INTO genatt_entry_type (id_type, title, is_group, is_comment, is_mylutece_user, class_name, icon_name, plugin, display_order, inactive) VALUES (16, 'Conditions d\'utilisation', 0, 0, 0, 'forms.entryTypeTermsOfService', 'gavel', 'forms', 12, 0);
INSERT INTO genatt_entry_type (id_type, title, is_group, is_comment, is_mylutece_user, class_name, icon_name, plugin, display_order, inactive) VALUES (17, 'Lecture automatique fichier (OCR)', 0, 0, 0, 'forms.entryTypeAutomaticFileReading', 'file', 'forms', 18, 1);
INSERT INTO genatt_entry_type (id_type, title, is_group, is_comment, is_mylutece_user, class_name, icon_name, plugin, display_order, inactive) VALUES (18, 'Camera', 0, 0, 0, 'forms.entryTypeCamera', 'camera', 'forms', 15, 0);
INSERT INTO genatt_entry_type (id_type, title, is_group, is_comment, is_mylutece_user, class_name, icon_name, plugin, display_order, inactive) VALUES (19, 'Numéro de téléphone', 0, 0, 0, 'forms.entryTypeTelephoneNumber', 'phone', 'forms', 19, 0);
INSERT INTO genatt_entry_type (id_type, title, is_group, is_comment, is_mylutece_user, class_name, icon_name, plugin, display_order, inactive) VALUES (20, 'Nombre', 0, 0, 0, 'forms.entryTypeNumber', 'number', 'forms', 5, 0);
INSERT INTO genatt_entry_type (id_type, title, is_group, is_comment, is_mylutece_user, class_name, icon_name, plugin, display_order, inactive) VALUES (21, 'Liste triable', 0, 0, 0, 'forms.entryTypeSelectOrder', 'list-tree', 'forms', 20, 0);
INSERT INTO genatt_entry_type (id_type, title, is_group, is_comment, is_mylutece_user, class_name, icon_name, plugin, display_order, inactive) VALUES (22, 'Galerie image', 0, 0, 0, 'forms.entryTypeGalleryImage', 'album', 'forms', 21, 1);
INSERT INTO genatt_entry_type (id_type, title, is_group, is_comment, is_mylutece_user, class_name, icon_name, plugin, display_order, inactive) VALUES (23, 'Creneau horaire', 0, 0, 0, 'forms.entryTypeSlot', 'calendar-clock', 'forms', 22, 0);
INSERT INTO genatt_entry_type (id_type, title, is_group, is_comment, is_mylutece_user, class_name, icon_name, plugin, display_order, inactive) VALUES (24, 'Session', 0, 0, 0, 'forms.entryTypeSession', 'activity', 'forms', 23, 0);
