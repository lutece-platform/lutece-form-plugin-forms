-- liquibase formatted sql
-- changeset forms:update_db_genericattributes-2.4.4-2.4.5.sql
-- preconditions onFail:MARK_RAN onError:WARN
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin,display_order,inactive) VALUES (124, 'Creneau horaire', 0, 0, 0, 'forms.entryTypeSlot', 'calendar', 'forms',22,0);
