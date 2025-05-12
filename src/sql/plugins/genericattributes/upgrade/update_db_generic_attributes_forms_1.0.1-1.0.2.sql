--liquibase formatted sql
--changeset forms:update_db_generic_attributes_forms_1.0.1-1.0.2.sql
--preconditions onFail:MARK_RAN onError:WARN
--
-- Dumping data for table genatt_entry_type
--
REPLACE INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin) VALUES
(112,'Numérotation',0,0,0,'forms.entryTypeNumbering','hashtag','forms');
