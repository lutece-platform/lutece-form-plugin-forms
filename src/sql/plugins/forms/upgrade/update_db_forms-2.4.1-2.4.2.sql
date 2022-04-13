ALTER TABLE forms_response ADD status SMALLINT default 0 NOT NULL;
ALTER TABLE forms_response ADD update_date_status timestamp default CURRENT_TIMESTAMP NOT NULL;

CREATE TABLE forms_category (
id_category int AUTO_INCREMENT,
code varchar(100) NOT NULL,
name varchar(100) NOT NULL,
PRIMARY KEY (id_category)
);

ALTER TABLE forms_form ADD id_category INT DEFAULT NULL;