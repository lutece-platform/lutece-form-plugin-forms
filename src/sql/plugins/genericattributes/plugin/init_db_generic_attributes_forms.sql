-- liquibase formatted sql
-- changeset forms:init_db_generic_attributes_forms.sql
-- preconditions onFail:MARK_RAN onError:WARN
--
-- Dumping data for table genatt_entry_type
--
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin,display_order,inactive) VALUES
('Bouton radio',0,0,0,'forms.entryTypeRadioButton','dot-circle radio','forms',5,0);
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin,display_order,inactive) VALUES
('Case à cocher',0,0,0,'forms.entryTypeCheckBox','check-square checkbox','forms',6,0);
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin,display_order,inactive) VALUES
('Commentaire',0,1,0,'forms.entryTypeComment','comment textarea','forms',11,0);
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin,display_order,inactive) VALUES
('Date',0,0,0,'forms.entryTypeDate','calendar date','forms',4,0);
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin,display_order,inactive) VALUES
('Liste déroulante',0,0,0,'forms.entryTypeSelect','list-alt list','forms',7,0);
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin,display_order,inactive) VALUES
('Zone de texte court',0,0,0,'forms.entryTypeText','file-alt text','forms',2,0);
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin,display_order,inactive) VALUES
('Zone de texte long',0,0,0,'forms.entryTypeTextArea','sticky-note textarea','forms',3,0);
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin,display_order,inactive) VALUES
('Fichier',0,0,0,'forms.entryTypeFile','file file','forms',9,0);
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin,display_order,inactive) VALUES
('Géolocalisation',0,0,0,'forms.entryTypeGeolocation','map-marked-alt geoloc','forms',16,0);
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin,display_order,inactive) VALUES
('Image',0,0,0,'forms.entryTypeImage','image image','forms',10,0);
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin,display_order,inactive) VALUES
('Utilisateur MyLutece',0,0,1,'forms.entryTypeMyLuteceUser','user user','forms',13,1);
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin,display_order,inactive) VALUES
('Numérotation',0,0,0,'forms.entryTypeNumbering','hashtag number','forms',8,0);
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin,display_order,inactive) VALUES
('Attribut de l''utilisateur MyLutece',0,0,0,'forms.entryTypeMyLuteceUserattribute','user user','forms',14,0);
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin,display_order,inactive) VALUES
('Tableau',0,0,0,'forms.entryTypeArray','terminal table','forms',17,1);
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin,display_order,inactive) VALUES
('Regroupement',1,0,0,'forms.entryTypeGroup','info-circle','forms',1,0);
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin,display_order,inactive) VALUES
('Conditions d''utilisation', 0, 0, 0, 'forms.entryTypeTermsOfService', 'futbol checkbox', 'forms',12,0);
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin,display_order,inactive) VALUES
('Lecture automatique fichier (OCR)', 0, 0, 0, 'forms.entryTypeAutomaticFileReading', 'file file', 'forms',18,1);
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin,display_order,inactive) VALUES
('Camera', 0, 0, 0, 'forms.entryTypeCamera', 'camera camera', 'forms',15,0);
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin,display_order,inactive) VALUES
('Numéro de téléphone', 0, 0, 0, 'forms.entryTypeTelephoneNumber', 'phone-square phone', 'forms',19,0);
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin,display_order,inactive) VALUES
('Nombre', 0, 0, 0, 'forms.entryTypeNumber', 'hashtag number', 'forms',5,0);
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin,display_order,inactive) VALUES
('Liste triable', 0, 0, 0, 'forms.entryTypeSelectOrder', 'list-ol list', 'forms',20,0);
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin,display_order,inactive) VALUES
('Galerie image', 0, 0, 0, 'forms.entryTypeGalleryImage', 'image image', 'forms',21,1);
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin,display_order,inactive) VALUES
('Creneau horaire', 0, 0, 0, 'forms.entryTypeSlot', 'calendar time', 'forms',22,0);
INSERT INTO genatt_entry_type (title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin,display_order,inactive) VALUES
('Session',0,0,0,'forms.entryTypeSession','map-marked-alt id-badge-2','forms',23,0);
