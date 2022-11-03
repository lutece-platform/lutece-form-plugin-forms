DROP TABLE IF EXISTS forms_control_group;
CREATE TABLE forms_control_group (
	id_control_group int AUTO_INCREMENT,
	logical_operator varchar(50) NOT NULL,
	PRIMARY KEY (id_control_group)
);

ALTER TABLE forms_control ADD id_control_group INT DEFAULT NULL;
