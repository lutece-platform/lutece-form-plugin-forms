--liquibase formatted sql
--changeset forms:update_db_genericattributes-2.0.0-2.3.0.sql
--preconditions onFail:MARK_RAN onError:WARN
UPDATE genatt_entry_type SET display_order=5,inactive=0 WHERE id_type=101;
UPDATE genatt_entry_type SET display_order=6,inactive=0 WHERE id_type=102;
UPDATE genatt_entry_type SET display_order=11,inactive=0 WHERE id_type=103;
UPDATE genatt_entry_type SET display_order=4,inactive=0 WHERE id_type=104;
UPDATE genatt_entry_type SET display_order=7,inactive=0 WHERE id_type=105;
UPDATE genatt_entry_type SET display_order=2,inactive=0 WHERE id_type=106;
UPDATE genatt_entry_type SET display_order=3,inactive=0 WHERE id_type=107;
UPDATE genatt_entry_type SET display_order=9,inactive=0 WHERE id_type=108;
UPDATE genatt_entry_type SET display_order=16,inactive=0 WHERE id_type=109;
UPDATE genatt_entry_type SET display_order=10,inactive=0 WHERE id_type=110;
UPDATE genatt_entry_type SET display_order=13,inactive=1 WHERE id_type=111;
UPDATE genatt_entry_type SET display_order=8,inactive=0 WHERE id_type=112;
UPDATE genatt_entry_type SET display_order=14,inactive=0 WHERE id_type=113;
UPDATE genatt_entry_type SET display_order=17,inactive=1 WHERE id_type=114;
UPDATE genatt_entry_type SET display_order=1,inactive=0 WHERE id_type=115;
UPDATE genatt_entry_type SET display_order=12,inactive=0 WHERE id_type=116;
UPDATE genatt_entry_type SET display_order=18,inactive=1 WHERE id_type=117;
UPDATE genatt_entry_type SET display_order=15,inactive=0 WHERE id_type=118;
