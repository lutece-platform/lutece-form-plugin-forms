-- liquibase formatted sql
-- changeset forms:update_db_genericattributes-2.4.6-4.0.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE genatt_entry_type SET icon_name='dot-circle' WHERE class_name='forms.entryTypeRadioButton';
UPDATE genatt_entry_type SET icon_name='check-square' WHERE class_name='forms.entryTypeCheckBox';
UPDATE genatt_entry_type SET icon_name='comment' WHERE class_name='forms.entryTypeComment';
UPDATE genatt_entry_type SET icon_name='calendar' WHERE class_name='forms.entryTypeDate';
UPDATE genatt_entry_type SET icon_name='list-alt' WHERE class_name='forms.entryTypeSelect';
UPDATE genatt_entry_type SET icon_name='file-alt' WHERE class_name='forms.entryTypeText';
UPDATE genatt_entry_type SET icon_name='sticky-note' WHERE class_name='forms.entryTypeTextArea';
UPDATE genatt_entry_type SET icon_name='file' WHERE class_name='forms.entryTypeFile';
UPDATE genatt_entry_type SET icon_name='map-marked-alt' WHERE class_name='forms.entryTypeGeolocation';
UPDATE genatt_entry_type SET icon_name='image' WHERE class_name='forms.entryTypeImage';
UPDATE genatt_entry_type SET icon_name='user' WHERE class_name='forms.entryTypeMyLuteceUser';
UPDATE genatt_entry_type SET icon_name='hashtag' WHERE class_name='forms.entryTypeNumbering';
UPDATE genatt_entry_type SET icon_name='user' WHERE class_name='forms.entryTypeMyLuteceUserattribute';
UPDATE genatt_entry_type SET icon_name='terminal' WHERE class_name='forms.entryTypeArray';
UPDATE genatt_entry_type SET icon_name='info-circle' WHERE class_name='forms.entryTypeGroup';
UPDATE genatt_entry_type SET icon_name='futbol' WHERE class_name='forms.entryTypeTermsOfService';
UPDATE genatt_entry_type SET icon_name='file' WHERE class_name='forms.entryTypeAutomaticFileReading';
UPDATE genatt_entry_type SET icon_name='camera' WHERE class_name='forms.entryTypeCamera';
UPDATE genatt_entry_type SET icon_name='phone-square' WHERE class_name='forms.entryTypeTelephoneNumber';
UPDATE genatt_entry_type SET icon_name='hashtag' WHERE class_name='forms.entryTypeNumber';
UPDATE genatt_entry_type SET icon_name='list-ol' WHERE class_name='forms.entryTypeSelectOrder';
UPDATE genatt_entry_type SET icon_name='image' WHERE class_name='forms.entryTypeGalleryImage';
UPDATE genatt_entry_type SET icon_name='map-marked-alt' WHERE class_name='forms.entryTypeCartography';
UPDATE genatt_entry_type SET icon_name='calendar' WHERE class_name='forms.entryTypeSlot';
UPDATE genatt_entry_type SET icon_name='map-marked-alt' WHERE class_name='forms.entryTypeSession';