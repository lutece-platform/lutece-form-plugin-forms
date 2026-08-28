-- liquibase formatted sql
-- lutece runAfter:genericattributes
-- LUT-33260 : formerly shipped under sql/plugins/genericattributes ; renamed with forms versions (first release shipping the script)

-- formerly src/sql/plugins/genericattributes/upgrade/update_db_genericattributes-2.4.6-2.4.7.sql

-- changeset forms:update_db_genericattributes-2.4.6-2.4.7.sql logicalFilePath:sql/plugins/genericattributes/upgrade/update_db_genericattributes-2.4.6-2.4.7.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE genatt_entry_type
SET inactive = 1
WHERE class_name = 'forms.entryTypeGalleryImage';
