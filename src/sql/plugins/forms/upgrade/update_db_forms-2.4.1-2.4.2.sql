-- liquibase formatted sql
-- changeset forms:update_db_forms-2.4.1-2.4.2.sql
-- preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE forms_response ADD status SMALLINT default 0 NOT NULL;
ALTER TABLE forms_response ADD update_date_status timestamp default CURRENT_TIMESTAMP NOT NULL;

CREATE TABLE forms_category (
id_category int AUTO_INCREMENT,
code varchar(100) NOT NULL,
name varchar(100) NOT NULL,
PRIMARY KEY (id_category)
);

ALTER TABLE forms_form ADD id_category INT DEFAULT NULL;
ALTER TABLE forms_form ADD backup_enabled SMALLINT default 0 NOT NULL;

INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,icon_name,plugin,display_order,inactive) VALUES 
(121, 'Liste triable', 0, 0, 0, 'forms.entryTypeSelectOrder', 'list-ol', 'forms',20,0);
