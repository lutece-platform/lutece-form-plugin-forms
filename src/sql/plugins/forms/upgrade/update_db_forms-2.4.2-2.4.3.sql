ALTER TABLE forms_response ADD role varchar(50) default NULL;
ALTER TABLE forms_form ADD access_to_responses_by_role SMALLINT default 0;

DROP TABLE IF EXISTS forms_control_group;
CREATE TABLE forms_control_group (
	id_control_group int AUTO_INCREMENT,
	logical_operator varchar(50) NOT NULL,
	PRIMARY KEY (id_control_group)
);
ALTER TABLE forms_control ADD id_control_group INT DEFAULT NULL;
