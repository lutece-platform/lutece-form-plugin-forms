--liquibase formatted sql
--changeset forms:update_db_genericattributes-2.0.0-2.2.1.sql
--preconditions onFail:MARK_RAN onError:WARN
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin) VALUES 
(118, 'Numéro de téléphone', 0, 0, 0, 'forms.entryTypeTelephoneNumber', 'phone-square', 'forms');