ALTER TABLE forms_form ADD COLUMN captcha_step_initial SMALLINT default 0 NOT NULL;
ALTER TABLE forms_form ADD COLUMN captcha_step_final SMALLINT default 0 NOT NULL;
ALTER TABLE forms_form ADD COLUMN captcha_recap SMALLINT default 0 NOT NULL;
ALTER TABLE forms_form ADD COLUMN count_responses SMALLINT default 0 NOT NULL;

DROP TABLE IF EXISTS forms_export_config;
CREATE TABLE forms_export_config (
	id int AUTO_INCREMENT,
	id_form int,
	field varchar(255),
	display_order int,
	PRIMARY KEY (id)
);
