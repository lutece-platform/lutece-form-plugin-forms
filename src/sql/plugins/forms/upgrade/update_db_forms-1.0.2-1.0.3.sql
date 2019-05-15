

DROP TABLE IF EXISTS forms_control_question;
CREATE TABLE forms_control_question (
id_control int NOT NULL,
id_question int NOT NULL,
PRIMARY KEY (id_control, id_question)
);

DROP TABLE IF EXISTS forms_control_question_mapping;
CREATE TABLE forms_control_question_mapping (
id_control int NOT NULL,
id_question int NOT NULL,
value varchar(255),
PRIMARY KEY (id_control, id_question, value)
);


INSERT INTO forms_control_question SELECT id_control, id_question from forms_control;

ALTER TABLE forms_control DROP COLUMN id_question;

