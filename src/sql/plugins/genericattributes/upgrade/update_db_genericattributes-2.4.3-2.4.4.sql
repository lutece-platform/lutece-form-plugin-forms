--liquibase formatted sql
--changeset forms:update_db_genericattributes-2.4.3-2.4.4.sql
--preconditions onFail:MARK_RAN onError:WARN
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin,display_order,inactive) VALUES
(123,'Cartographie',0,0,0,'forms.entryTypeCartography','map-marked-alt','forms',16,0);