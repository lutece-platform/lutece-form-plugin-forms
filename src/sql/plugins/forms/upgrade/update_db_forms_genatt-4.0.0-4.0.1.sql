-- liquibase formatted sql
-- lutece runAfter:genericattributes
-- LUT-33260 : formerly shipped under sql/plugins/genericattributes ; renamed with forms versions (first release shipping the script)

-- formerly src/sql/plugins/genericattributes/upgrade/update_db_genericattributes-4.0.0-4.0.1.sql
-- changeset forms:update_db_genericattributes-4.0.0-4.0.1.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE genatt_entry_type SET icon_name='circle-dot' WHERE id_type=1 AND plugin='forms';
UPDATE genatt_entry_type SET icon_name='square-check' WHERE id_type=2 AND plugin='forms';
UPDATE genatt_entry_type SET icon_name='select' WHERE id_type=5 AND plugin='forms';
UPDATE genatt_entry_type SET icon_name='forms' WHERE id_type=6 AND plugin='forms';
UPDATE genatt_entry_type SET icon_name='align-box-left-top' WHERE id_type=7 AND plugin='forms';
UPDATE genatt_entry_type SET icon_name='map-pin' WHERE id_type=9 AND plugin='forms';
UPDATE genatt_entry_type SET icon_name='photo' WHERE id_type=10 AND plugin='forms';
UPDATE genatt_entry_type SET icon_name='hash' WHERE id_type=12 AND plugin='forms';
UPDATE genatt_entry_type SET icon_name='gavel' WHERE id_type=16 AND plugin='forms';
UPDATE genatt_entry_type SET icon_name='album' WHERE id_type=18 AND plugin='forms';
UPDATE genatt_entry_type SET icon_name='phone' WHERE id_type=19 AND plugin='forms';
UPDATE genatt_entry_type SET icon_name='number' WHERE id_type=20 AND plugin='forms';
UPDATE genatt_entry_type SET icon_name='list-tree' WHERE id_type=21 AND plugin='forms';
UPDATE genatt_entry_type SET icon_name='calendar-clock' WHERE id_type=23 AND plugin='forms';
