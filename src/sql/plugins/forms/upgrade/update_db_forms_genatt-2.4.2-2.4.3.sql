-- liquibase formatted sql
-- lutece runAfter:genericattributes
-- LUT-33260 : formerly shipped under sql/plugins/genericattributes ; renamed with forms versions (first release shipping the script)

-- formerly src/sql/plugins/genericattributes/upgrade/update_db_genericattributes-2.4.2-2.4.3.sql
-- formerly src/sql/plugins/genericattributes/upgrade/update_db_genericattributes-2.4.3-2.4.4.sql
-- formerly src/sql/plugins/genericattributes/upgrade/update_db_genericattributes-2.4.4-2.4.5.sql

-- changeset forms:update_db_genericattributes-2.4.2-2.4.3.sql logicalFilePath:sql/plugins/genericattributes/upgrade/update_db_genericattributes-2.4.2-2.4.3.sql
-- preconditions onFail:MARK_RAN onError:WARN
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin,display_order,inactive) VALUES
(122, 'Galerie image', 0, 0, 0, 'forms.entryTypeGalleryImage', 'image', 'forms',21,0);

-- changeset forms:update_db_genericattributes-2.4.3-2.4.4.sql logicalFilePath:sql/plugins/genericattributes/upgrade/update_db_genericattributes-2.4.3-2.4.4.sql
-- preconditions onFail:MARK_RAN onError:WARN
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin,display_order,inactive) VALUES
(123,'Cartographie',0,0,0,'forms.entryTypeCartography','map-marked-alt','forms',16,0);

-- changeset forms:update_db_genericattributes-2.4.4-2.4.5.sql logicalFilePath:sql/plugins/genericattributes/upgrade/update_db_genericattributes-2.4.4-2.4.5.sql
-- preconditions onFail:MARK_RAN onError:WARN
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin,display_order,inactive) VALUES (124, 'Creneau horaire', 0, 0, 0, 'forms.entryTypeSlot', 'calendar', 'forms',22,0);
