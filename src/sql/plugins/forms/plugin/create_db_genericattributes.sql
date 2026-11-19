-- liquibase formatted sql
-- changeset forms:create_db_genericattributes.sql
-- preconditions onFail:MARK_RAN onError:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND LOWER(table_name) = 'genatt_entry_type'

--
-- Table structure for table genatt_entry_type
--
CREATE TABLE IF NOT EXISTS genatt_entry_type (
	id_type int AUTO_INCREMENT NOT NULL,
	title varchar(255),
	is_group smallint default NULL,
	is_comment int default NULL,
	is_mylutece_user smallint default NULL,
	class_name varchar(255),
	icon_name varchar(255),
	plugin varchar(255) NOT NULL,
	display_order int default 0,
	inactive int default 0,
	PRIMARY KEY (id_type)
);

CREATE INDEX index_genatt_entry_type_plugin ON genatt_entry_type (plugin);

--
-- Table structure for table genatt_entry
--
CREATE TABLE IF NOT EXISTS genatt_entry (
	id_entry int AUTO_INCREMENT NOT NULL,
	id_resource int default 0 NOT NULL,
	resource_type VARCHAR(255) NOT NULL,
	id_type int default 0 NOT NULL,
	id_parent int default NULL,
	title long varchar,
	code varchar(100) default NULL,	
	help_message long varchar,
	comment long varchar,
	mandatory smallint default NULL,
	fields_in_line smallint default NULL,
	pos int default NULL,
	id_field_depend int default NULL,
	field_unique smallint default NULL,
	css_class varchar(255) default NULL,
	pos_conditional int default 0,
	error_message long varchar default NULL,
    is_only_display_back smallint DEFAULT '0',
	is_indexed smallint DEFAULT '0',
    PRIMARY KEY (id_entry)
);

CREATE INDEX index_genatt_entry_resource ON genatt_entry (id_resource);
CREATE INDEX index_genatt_entry_parent ON genatt_entry (id_parent);
CREATE INDEX index_genatt_code ON genatt_entry ( code);

ALTER TABLE genatt_entry ADD CONSTRAINT fk_genatt_entry_type FOREIGN KEY (id_type)
	REFERENCES genatt_entry_type (id_type);

--
-- Table structure for table genatt_field
--
CREATE TABLE IF NOT EXISTS genatt_field (
	id_field int AUTO_INCREMENT,
	id_entry int default 0 NOT NULL,
	title varchar(255),
	code varchar(100) default NULL,
	value long varchar,
	default_value smallint default NULL,
	pos int default NULL,
	value_type_date date NULL,
	no_display_title smallint default NULL,
	comment long varchar default null,
	id_file_key varchar(50) default null,
	PRIMARY KEY (id_field)
);

CREATE INDEX index_genatt_field_entry ON genatt_field (id_entry);

ALTER TABLE genatt_field ADD CONSTRAINT fk_genatt_field_entry FOREIGN KEY (id_entry)
	REFERENCES genatt_entry (id_entry);
	
--
-- Table structure for table genatt_response
--
CREATE TABLE IF NOT EXISTS genatt_response (
	id_response int AUTO_INCREMENT,
	response_value long VARCHAR DEFAULT NULL,
	id_entry int default NULL,
	iteration_number int default -1,
	id_field int default NULL,
	file_key VARCHAR(255) default NULL,
	file_store VARCHAR(255) default NULL,
	status smallint default 1,
	sort_order int default 0,
	PRIMARY KEY (id_response)
);

ALTER TABLE genatt_response ADD CONSTRAINT fk_genatt_response_entry FOREIGN KEY (id_entry)
	REFERENCES genatt_entry (id_entry);
CREATE INDEX index_genatt_response_entry ON genatt_response (id_entry);
	
--
-- Table structure for table genatt_verify_by
--
CREATE TABLE IF NOT EXISTS genatt_verify_by (
	id_field int default 0 NOT NULL,
	id_expression int default 0 NOT NULL,
	PRIMARY KEY (id_field,id_expression)
);

CREATE INDEX index_genatt_verify_by_field ON genatt_verify_by (id_field);
	
ALTER TABLE genatt_verify_by ADD CONSTRAINT fk_genatt_verify_by_field FOREIGN KEY (id_field)
	REFERENCES genatt_field (id_field);
	
CREATE TABLE IF NOT EXISTS genatt_referenceitem_field (
	id_field int default 0 NOT NULL,
	id_item int default 0 NOT NULL,
	PRIMARY KEY( id_field )
);
