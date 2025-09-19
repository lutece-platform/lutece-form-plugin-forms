-- liquibase formatted sql
-- changeset forms:update_db_genericattributes-2.4.6-2.4.7.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE genatt_entry_type
SET inactive = 1
WHERE class_name = 'forms.entryTypeGalleryImage';
