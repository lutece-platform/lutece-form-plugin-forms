--liquibase formatted sql
--changeset forms:update_db_genericattributes-2.4.6-3.1.2.sql
--preconditions onFail:MARK_RAN onError:WARN
UPDATE genatt_entry_type icon_name='dot-circle radio' WHERE id_type=101;
UPDATE genatt_entry_type icon_name='check-square checkbox' WHERE id_type=102;
UPDATE genatt_entry_type icon_name='comment textarea' WHERE id_type=103;
UPDATE genatt_entry_type icon_name='calendar date' WHERE id_type=104;
UPDATE genatt_entry_type icon_name='list-alt list' WHERE id_type=105;
UPDATE genatt_entry_type icon_name='file-alt text' WHERE id_type=106;
UPDATE genatt_entry_type icon_name='sticky-note textarea' WHERE id_type=107;
UPDATE genatt_entry_type icon_name='file file' WHERE id_type=108;
UPDATE genatt_entry_type icon_name='map-marked-alt geoloc' WHERE id_type=109;
UPDATE genatt_entry_type icon_name='image image' WHERE id_type=110;
UPDATE genatt_entry_type icon_name='user user' WHERE id_type=111;
UPDATE genatt_entry_type icon_name='hashtag number' WHERE id_type=112;
UPDATE genatt_entry_type icon_name='user user' WHERE id_type=113;
UPDATE genatt_entry_type icon_name='terminal table' WHERE id_type=114;
UPDATE genatt_entry_type icon_name='info-circle' WHERE id_type=115;
UPDATE genatt_entry_type icon_name='futbol checkbox' WHERE id_type=116;
UPDATE genatt_entry_type icon_name='file file' WHERE id_type=117;
UPDATE genatt_entry_type icon_name='camera camera' WHERE id_type=118;
UPDATE genatt_entry_type icon_name='phone-square phone' WHERE id_type=119;
UPDATE genatt_entry_type icon_name='hashtag number' WHERE id_type=120;
UPDATE genatt_entry_type icon_name='list-ol list' WHERE id_type=121;
UPDATE genatt_entry_type icon_name='image image' WHERE id_type=122;
UPDATE genatt_entry_type icon_name='map-marked-alt carto' WHERE id_type=123;
UPDATE genatt_entry_type icon_name='calendar time' WHERE id_type=124;
UPDATE genatt_entry_type icon_name='map-marked-alt id-badge-2' WHERE id_type=125;