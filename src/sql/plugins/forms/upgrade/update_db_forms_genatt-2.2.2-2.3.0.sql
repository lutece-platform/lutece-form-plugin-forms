-- liquibase formatted sql
-- lutece runAfter:genericattributes
-- LUT-33260 : formerly shipped under sql/plugins/genericattributes ; renamed with forms versions (first release shipping the script)

-- formerly src/sql/plugins/genericattributes/upgrade/update_db_genericattributes-2.0.0-2.2.1.sql
-- changeset forms:update_db_genericattributes-2.0.0-2.2.1.sql logicalFilePath:sql/plugins/genericattributes/upgrade/update_db_genericattributes-2.0.0-2.2.1.sql
-- preconditions onFail:MARK_RAN onError:WARN
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin) VALUES 
(118, 'Numéro de téléphone', 0, 0, 0, 'forms.entryTypeTelephoneNumber', 'phone-square', 'forms');
