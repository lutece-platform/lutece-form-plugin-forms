-- liquibase formatted sql
-- changeset forms:update_db_genericattributes-2.4.6-4.0.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE genatt_entry_type SET icon_name='dot-circle radio' WHERE class_name='forms.entryTypeRadioButton';
UPDATE genatt_entry_type SET icon_name='check-square checkbox' WHERE class_name='forms.entryTypeCheckBox';
UPDATE genatt_entry_type SET icon_name='comment textarea' WHERE class_name='forms.entryTypeComment';
UPDATE genatt_entry_type SET icon_name='calendar date' WHERE class_name='forms.entryTypeDate';
UPDATE genatt_entry_type SET icon_name='list-alt list' WHERE class_name='forms.entryTypeSelect';
UPDATE genatt_entry_type SET icon_name='file-alt text' WHERE class_name='forms.entryTypeText';
UPDATE genatt_entry_type SET icon_name='sticky-note textarea' WHERE class_name='forms.entryTypeTextArea';
UPDATE genatt_entry_type SET icon_name='file file' WHERE class_name='forms.entryTypeFile';
UPDATE genatt_entry_type SET icon_name='map-marked-alt geoloc' WHERE class_name='forms.entryTypeGeolocation';
UPDATE genatt_entry_type SET icon_name='image image' WHERE class_name='forms.entryTypeImage';
UPDATE genatt_entry_type SET icon_name='user user' WHERE class_name='forms.entryTypeMyLuteceUser';
UPDATE genatt_entry_type SET icon_name='hashtag number' WHERE class_name='forms.entryTypeNumbering';
UPDATE genatt_entry_type SET icon_name='user user' WHERE class_name='forms.entryTypeMyLuteceUserattribute';
UPDATE genatt_entry_type SET icon_name='terminal table' WHERE class_name='forms.entryTypeArray';
UPDATE genatt_entry_type SET icon_name='info-circle' WHERE class_name='forms.entryTypeGroup';
UPDATE genatt_entry_type SET icon_name='futbol checkbox' WHERE class_name='forms.entryTypeTermsOfService';
UPDATE genatt_entry_type SET icon_name='file file' WHERE class_name='forms.entryTypeAutomaticFileReading';
UPDATE genatt_entry_type SET icon_name='camera camera' WHERE class_name='forms.entryTypeCamera';
UPDATE genatt_entry_type SET icon_name='phone-square phone' WHERE class_name='forms.entryTypeTelephoneNumber';
UPDATE genatt_entry_type SET icon_name='hashtag number' WHERE class_name='forms.entryTypeNumber';
UPDATE genatt_entry_type SET icon_name='list-ol list' WHERE class_name='forms.entryTypeSelectOrder';
UPDATE genatt_entry_type SET icon_name='image image' WHERE class_name='forms.entryTypeGalleryImage';
UPDATE genatt_entry_type SET icon_name='map-marked-alt carto' WHERE class_name='forms.entryTypeCartography';
UPDATE genatt_entry_type SET icon_name='calendar time' WHERE class_name='forms.entryTypeSlot';
UPDATE genatt_entry_type SET icon_name='map-marked-alt id-badge-2' WHERE class_name='forms.entryTypeSession';