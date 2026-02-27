-- liquibase formatted sql
-- changeset genericattributes:update_db_genericattributes-4.0.0-4.0.1.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE genatt_entry_type SET icon_name='circle-dot' WHERE id_type=1;
UPDATE genatt_entry_type SET icon_name='square-check' WHERE id_type=2;
UPDATE genatt_entry_type SET icon_name='select' WHERE id_type=5;
UPDATE genatt_entry_type SET icon_name='forms' WHERE id_type=6;
UPDATE genatt_entry_type SET icon_name='align-box-left-top' WHERE id_type=7;
UPDATE genatt_entry_type SET icon_name='map-pin' WHERE id_type=9;
UPDATE genatt_entry_type SET icon_name='photo' WHERE id_type=10;
UPDATE genatt_entry_type SET icon_name='hash' WHERE id_type=12;
UPDATE genatt_entry_type SET icon_name='gavel' WHERE id_type=16;
UPDATE genatt_entry_type SET icon_name='album' WHERE id_type=18;
UPDATE genatt_entry_type SET icon_name='phone' WHERE id_type=19;
UPDATE genatt_entry_type SET icon_name='number' WHERE id_type=20;
UPDATE genatt_entry_type SET icon_name='list-tree' WHERE id_type=21;
UPDATE genatt_entry_type SET icon_name='calendar-clock' WHERE id_type=23;